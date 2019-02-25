package cn.lollipop.item.mapper;

import cn.lollipop.common.mapper.BaseMapper;
import cn.lollipop.item.pojo.Stock;
import org.apache.ibatis.annotations.Update;

public interface StockMapper extends BaseMapper<Stock, Long> {

    @Update("UPDATE tb_stock SET stock = stock - #{num} WHERE sku_id = #{skuId} AND stock >= #{num}")
    int decreaseStock(Long skuId, Integer num);
}
