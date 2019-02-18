package cn.lollipop.item.service;

import cn.lollipop.common.ExceptionConstant;
import cn.lollipop.common.exception.LyException;
import cn.lollipop.item.mapper.SkuMapper;
import cn.lollipop.item.mapper.SpuDetailMapper;
import cn.lollipop.item.mapper.SpuMapper;
import cn.lollipop.item.mapper.StockMapper;
import cn.lollipop.item.pojo.Category;
import cn.lollipop.item.pojo.Spu;
import cn.lollipop.item.pojo.SpuDetail;
import cn.lollipop.item.pojo.Stock;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;
import vo.PageResult;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
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


    @Autowired
    public GoodsService(SpuMapper spuMapper, SpuDetailMapper spuDetailMapper, SkuMapper skuMapper, StockMapper stockMapper, CategoryService categoryService, BrandService brandService) {
        this.spuMapper = spuMapper;
        this.spuDetailMapper = spuDetailMapper;
        this.skuMapper = skuMapper;
        this.stockMapper = stockMapper;
        this.categoryService = categoryService;
        this.brandService = brandService;
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
        if (list == null) {
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

        List<Stock> stockList = new ArrayList<>();
        // 新增sku
        spu.getSkus().forEach(sku -> {
            sku.setCreateTime(spu.getCreateTime());
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

    public SpuDetail querySpuDetailById(Long id) {
        SpuDetail detail = spuDetailMapper.selectByPrimaryKey(id);
        if (detail == null) {
            throw new LyException(ExceptionConstant.GOODS_DETAIL_NOT_FOUND);
        }
        return detail;
    }
}
