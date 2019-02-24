package cn.lollipop.item.mapper;

import cn.lollipop.common.mapper.BaseMapper;
import cn.lollipop.item.pojo.Category;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface CategoryMapper extends BaseMapper<Category, Long> {

    @Select("SELECT id, name, parent_id, is_parent, sort " +
            "FROM tb_category c LEFT JOIN tb_category_brand cb ON c.id = cb.category_id " +
            "WHERE cb.brand_id = #{bid}")
    List<Category> selectByBid(@Param("bid") Long bid);
}
