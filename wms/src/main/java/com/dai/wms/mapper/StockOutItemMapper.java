package com.dai.wms.mapper;

import com.dai.wms.entity.StockOutItem;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author dai
 * @since 2025-04-22
 */
@Mapper
public interface StockOutItemMapper extends BaseMapper<StockOutItem> {
    @Select("SELECT soi.stock_out_item_id, soi.stock_out_id, soi.product_id, p.product_name, p.specifications, soi.quantity " +
            "FROM stock_out_item soi " +
            "JOIN product p ON soi.product_id = p.product_id " +
            "WHERE soi.stock_out_id = #{stockOutId}")
    List<StockOutItem> selectStockOutItemsWithProductInfo(@Param("stockOutId") Integer stockOutId);
}
