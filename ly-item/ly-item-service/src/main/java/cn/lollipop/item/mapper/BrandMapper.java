package cn.lollipop.item.mapper;

import cn.lollipop.common.mapper.BaseMapper;
import cn.lollipop.item.pojo.Brand;
import org.apache.ibatis.annotations.*;

import java.util.List;

public interface BrandMapper extends BaseMapper<Brand, Long> {

    @Insert("INSERT INTO tb_category_brand(category_id, brand_id) VALUES(#{cid}, #{bid})")
    int insertCategoryBrand(@Param("cid") Long cid, @Param("bid") Long bid);

    @Delete("DELETE FROM tb_category_brand WHERE brand_id = #{bid}")
    void deleteCategoryBrandByBid(@Param("bid") Long bid);

    @Update("UPDATE tb_brand SET name = #{name}, image = #{image}, letter = #{letter} WHERE id = #{id}")
    int update(Brand brand);

    @Select("SELECT id, name, image, letter FROM tb_brand b LEFT JOIN tb_category_brand cb ON b.id = cb.brand_id WHERE cb.category_id = #{cid}")
    List<Brand> selectByCategoryId(Long cid);
}
