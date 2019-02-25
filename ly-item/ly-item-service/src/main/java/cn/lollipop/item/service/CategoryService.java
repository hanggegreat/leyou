package cn.lollipop.item.service;

import cn.lollipop.common.constants.ExceptionConstant;
import cn.lollipop.common.exception.LyException;
import cn.lollipop.item.mapper.CategoryMapper;
import cn.lollipop.item.pojo.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;

@Service
public class CategoryService {
    private final CategoryMapper categoryMapper;

    @Autowired
    public CategoryService(CategoryMapper categoryMapper) {
        this.categoryMapper = categoryMapper;
    }

    public List<Category> queryCategoryListByPid(long pid) {
        Category category = new Category();
        category.setParentId(pid);
        List<Category> list = categoryMapper.select(category);
        if (CollectionUtils.isEmpty(list)) {
            throw new LyException(ExceptionConstant.CATEGORY_NOT_FOUND);
        }
        return list;
    }

    public List<Category> queryCategoryByBid(Long bid) {
        List<Category> list = categoryMapper.selectByBid(bid);
        if (CollectionUtils.isEmpty(list)) {
            throw new LyException(ExceptionConstant.CATEGORY_NOT_FOUND);
        }
        return list;
    }

    public List<Category> queryByIdList(List<Long> cids) {
        List<Category> list = categoryMapper.selectByIdList(cids);
        if (CollectionUtils.isEmpty(list)) {
            throw new LyException(ExceptionConstant.CATEGORY_NOT_FOUND);
        }
        return list;
    }

    public Category queryCategoryById(Long id) {
        Category category = categoryMapper.selectByPrimaryKey(id);
        if (category == null) {
            throw new LyException(ExceptionConstant.CATEGORY_NOT_FOUND);
        }
        return category;
    }
}
