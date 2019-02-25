package cn.lollipop.item.service;

import cn.lollipop.common.constants.ExceptionConstant;
import cn.lollipop.common.exception.LyException;
import cn.lollipop.item.mapper.BrandMapper;
import cn.lollipop.item.pojo.Brand;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import tk.mybatis.mapper.entity.Example;
import cn.lollipop.common.vo.PageResult;

import java.util.List;

@Service
public class BrandService {

    private final BrandMapper brandMapper;

    @Autowired
    public BrandService(BrandMapper brandMapper) {
        this.brandMapper = brandMapper;
    }

    public PageResult<Brand> queryBrandByPage(String key, Integer page, Integer rows, String sortBy, Boolean desc) {
        // 分页
        PageHelper.startPage(page, rows);

        // 过滤
        Example example = new Example(Brand.class);
        if (StringUtils.isNotBlank(key)) {
            example.createCriteria().orLike("name", "%" + key + "%")
                    .orEqualTo("letter", key.toUpperCase());
        }

        // 排序
        if (StringUtils.isNotBlank(sortBy)) {
            example.setOrderByClause(sortBy + (desc ? " DESC" : " ASC"));
        }

        // 查询
        List<Brand> brands = brandMapper.selectByExample(example);
        if (CollectionUtils.isEmpty(brands)) {
            throw new LyException(ExceptionConstant.BRAND_NOT_FOUNT);
        }
        PageInfo<Brand> pageInfo = new PageInfo<>(brands);
        return new PageResult<>(pageInfo.getTotal(), brands);
    }

    @Transactional
    public void saveBrand(Brand brand, List<Long> cids) {
        int count = brandMapper.insert(brand);

        if (count != 1) {
            throw new LyException(ExceptionConstant.BRAND_SAVE_ERROR);
        }

        cids.forEach(cid -> {
            if (brandMapper.insertCategoryBrand(cid, brand.getId()) != 1) {
                throw new LyException(ExceptionConstant.BRAND_SAVE_ERROR);
            }
        });
    }

    @Transactional
    public void editBrand(Brand brand, List<Long> cids) {
        Brand oldBrand = brandMapper.selectByPrimaryKey(brand.getId());
        if (oldBrand == null) {
            throw new LyException(ExceptionConstant.BRAND_NOT_FOUNT);
        }
        brandMapper.deleteCategoryBrandByBid(brand.getId());

        if (brandMapper.update(brand) != 1) {
            throw new LyException(ExceptionConstant.BRAND_SAVE_ERROR);
        }

        cids.forEach(cid -> {
            if (brandMapper.insertCategoryBrand(cid, brand.getId()) != 1) {
                throw new LyException(ExceptionConstant.BRAND_SAVE_ERROR);
            }
        });
    }

    public void deleteBrand(Long bid) {
        if (bid == null) {
            throw new LyException(ExceptionConstant.INVALID_PARAM);
        }

        Brand brand = new Brand();
        brand.setId(bid);
        brandMapper.deleteCategoryBrandByBid(bid);
        brandMapper.delete(brand);
    }

    public Brand queryById(Long bid) {
        if (bid == null) {
            throw new LyException(ExceptionConstant.INVALID_PARAM);
        }

        Brand brand = brandMapper.selectByPrimaryKey(bid);
        if (brand == null) {
            throw new LyException(ExceptionConstant.BRAND_NOT_FOUNT);
        }

        return brand;
    }

    public List<Brand> queryByCid(Long cid) {
        if (cid == null) {
            throw new LyException(ExceptionConstant.INVALID_FILE_TYPE);
        }

        List<Brand> list = brandMapper.selectByCategoryId(cid);
        if (CollectionUtils.isEmpty(list)) {
            throw new LyException(ExceptionConstant.BRAND_NOT_FOUNT);
        }
        return list;
    }

    public List<Brand> queryBrandByIds(List<Long> ids) {
        List<Brand> list = brandMapper.selectByIdList(ids);
        if (CollectionUtils.isEmpty(list)) {
            throw new LyException(ExceptionConstant.BRAND_NOT_FOUNT);
        }
        return list;
    }
}
