package cn.lollipop.item.service;

import cn.lollipop.common.constants.ExceptionConstant;
import cn.lollipop.common.dto.CartDTO;
import cn.lollipop.common.exception.LyException;
import cn.lollipop.item.mapper.SkuMapper;
import cn.lollipop.item.mapper.SpuDetailMapper;
import cn.lollipop.item.mapper.SpuMapper;
import cn.lollipop.item.mapper.StockMapper;
import cn.lollipop.item.pojo.*;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import tk.mybatis.mapper.entity.Example;
import cn.lollipop.common.vo.PageResult;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class GoodsService {

    private final SpuMapper spuMapper;
    private final SpuDetailMapper spuDetailMapper;
    private final SkuMapper skuMapper;
    private final StockMapper stockMapper;
    private final CategoryService categoryService;
    private final BrandService brandService;
    private final AmqpTemplate amqpTemplate;


    @Autowired
    public GoodsService(SpuMapper spuMapper, SpuDetailMapper spuDetailMapper, SkuMapper skuMapper, StockMapper stockMapper, CategoryService categoryService, BrandService brandService, AmqpTemplate amqpTemplate) {
        this.spuMapper = spuMapper;
        this.spuDetailMapper = spuDetailMapper;
        this.skuMapper = skuMapper;
        this.stockMapper = stockMapper;
        this.categoryService = categoryService;
        this.brandService = brandService;
        this.amqpTemplate = amqpTemplate;
    }

    public PageResult<Spu> querySpuByPage(String key, Boolean saleable, Integer page, Integer row) {
        // 分页
        PageHelper.startPage(page, row);

        // 过滤
        Example example = new Example(Spu.class);
        Example.Criteria criteria = example.createCriteria();

        if (StringUtils.isNotBlank(key)) {
            criteria.andLike("title", "%" + key + "%");
        }
        if (saleable != null) {
            criteria.andEqualTo("saleable", saleable);
        }

        // 默认排序
        example.setOrderByClause("last_update_time DESC");

        // 查询
        List<Spu> list = spuMapper.selectByExample(example);
        if (CollectionUtils.isEmpty(list)) {
            throw new LyException(ExceptionConstant.GOODS_NOT_FOUND);
        }

        PageInfo<Spu> pageInfo = PageInfo.of(list);
        loadCategoryNameAndBrandName(list);
        return new PageResult<>(pageInfo.getTotal(), list);
    }

    private void loadCategoryNameAndBrandName(List<Spu> list) {
        list.forEach(spu -> {
            //处理分类名称
            List<String> names = categoryService.queryByIdList(Arrays.asList(spu.getCid1(), spu.getCid2(), spu.getCid3()))
                    .stream().map(Category::getName).collect(Collectors.toList());
            spu.setCname(StringUtils.join(names, "/"));

            // 处理品牌名称
            spu.setBname(brandService.queryById(spu.getBrandId()).getName());
        });
    }

    public void saveGoods(Spu spu) {
        if (spu == null) {
            throw new LyException(ExceptionConstant.INVALID_PARAM);
        }

        // 新增spu
        spu.setCreateTime(new Date());
        spu.setLastUpdateTime(spu.getCreateTime());
        spu.setSaleable(true);
        spu.setValid(false);
        if (spuMapper.insert(spu) != 1) {
            throw new LyException(ExceptionConstant.GOODS_SAVE_ERROR);
        }

        // 新增detail
        spu.getSpuDetail().setSpuId(spu.getId());
        spuDetailMapper.insert(spu.getSpuDetail());

        // 新增sku、stock
        saveSkuAndStock(spu);

        // 发送mq消息
        amqpTemplate.convertAndSend("item.insert", spu.getId());
    }

    public SpuDetail querySpuDetailById(Long id) {
        SpuDetail detail = spuDetailMapper.selectByPrimaryKey(id);
        if (detail == null) {
            throw new LyException(ExceptionConstant.GOODS_DETAIL_NOT_FOUND);
        }
        return detail;
    }

    public List<Sku> querySkuListBySpuId(Long id) {
        Sku sku = new Sku();
        sku.setSpuId(id);
        List<Sku> list = skuMapper.select(sku);
        // 库存查询
        List<Long> ids = list.stream().map(Sku::getId).collect(Collectors.toList());
        loadStockInSku(ids, list);
        return list;
    }

    @Transactional
    public void editGoods(Spu spu) {
        if (spu == null || spu.getId() == null) {
            throw new LyException(ExceptionConstant.INVALID_PARAM);
        }

        // 删除sku、stock
        Sku sku = new Sku();
        sku.setSpuId(spu.getId());
        List<Sku> oldSkuList = skuMapper.select(sku);
        if (oldSkuList != null) {
            List<Long> ids = oldSkuList.stream().map(Sku::getId).collect(Collectors.toList());
            skuMapper.deleteByIdList(ids);
            stockMapper.deleteByIdList(ids);
        }

        // 修改spu
        spu.setValid(null);
        spu.setSaleable(null);
        spu.setLastUpdateTime(new Date());
        spu.setCreateTime(null);

        if (spuMapper.updateByPrimaryKeySelective(spu) != 1) {
            throw new LyException(ExceptionConstant.GOODS_UPDATE_ERROR);
        }

        // 修改detail
        spuDetailMapper.updateByPrimaryKeySelective(spu.getSpuDetail());

        // 新增sku、stock
        saveSkuAndStock(spu);

        // 发送mq消息
        amqpTemplate.convertAndSend("item.update", spu.getId());
    }

    private void saveSkuAndStock(Spu spu) {
        List<Stock> stockList = new ArrayList<>();
        // 新增sku
        spu.getSkus().forEach(sku -> {
            sku.setCreateTime(spu.getLastUpdateTime());
            sku.setLastUpdateTime(spu.getLastUpdateTime());
            sku.setSpuId(spu.getId());
            if (skuMapper.insert(sku) != 1) {
                throw new LyException(ExceptionConstant.GOODS_SAVE_ERROR);
            }
            // 新增库存
            Stock stock = new Stock();
            stock.setSkuId(sku.getId());
            stock.setStock(sku.getStock());
            stockList.add(stock);
        });

        // 批量新增库存
        if (stockMapper.insertList(stockList) != stockList.size()) {
            throw new LyException(ExceptionConstant.GOODS_SAVE_ERROR);
        }
    }

    public Spu querySpuById(Long id) {
        Spu spu = spuMapper.selectByPrimaryKey(id);
        if (spu == null) {
            throw new LyException(ExceptionConstant.GOODS_NOT_FOUND);
        }
        // 查询sku
        spu.setSkus(querySkuListBySpuId(id));
        // 查询detail
        spu.setSpuDetail(querySpuDetailById(id));
        return spu;
    }

    public List<Sku> querySkuListByIds(List<Long> ids) {
        List<Sku> list = skuMapper.selectByIdList(ids);
        if (list == null) {
            throw new LyException(ExceptionConstant.GOODS_SKU_NOT_FOUND);
        }

        loadStockInSku(ids, list);
        return list;
    }

    private void loadStockInSku(List<Long> ids, List<Sku> skuList) {
        List<Stock> stockList = stockMapper.selectByIdList(ids);
        if (stockList == null) {
            throw new LyException(ExceptionConstant.GOODS_STOCK_NOT_FOUND);
        }

        Map<Long, Integer> stockMap = stockList.stream().collect(Collectors.toMap(Stock::getSkuId, Stock::getStock));
        skuList.forEach(s -> s.setStock(stockMap.get(s.getId())));
    }

    @Transactional
    public void decreaseStock(List<CartDTO> cartDTOList) {
        for (CartDTO cartDTO : cartDTOList) {
            if (stockMapper.decreaseStock(cartDTO.getSkuId(), cartDTO.getNum()) != 1) {
                throw new LyException(ExceptionConstant.STOCK_NOT_ENOUGH);
            }
        }
    }
}
