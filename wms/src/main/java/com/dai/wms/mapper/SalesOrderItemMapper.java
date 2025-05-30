package com.dai.wms.mapper;

import com.dai.wms.entity.SalesOrderItem;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.*;

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
public interface SalesOrderItemMapper extends BaseMapper<SalesOrderItem> {
    @ResultMap("BaseResultMap")
    @Select("SELECT soi.sales_item_id, soi.sales_order_id, soi.product_id, p.product_name, p.specifications, p.stock_quantity, soi.quantity, soi.sale_price " +
            "FROM sales_order_item soi " +
            "JOIN product p ON soi.product_id = p.product_id " +
            "WHERE soi.sales_order_id = #{salesOrderId}")
    List<SalesOrderItem> selectSalesOrderItemsWithProductName(@Param("salesOrderId") Integer salesOrderId);

    @ResultMap("BaseResultMap")
    @Select("SELECT soi.sales_item_id, soi.sales_order_id, soi.product_id, p.product_name, p.specifications, p.stock_quantity, soi.quantity, soi.sale_price " +
            "FROM sales_order_item soi " +
            "JOIN product p ON soi.product_id = p.product_id " +
            "WHERE soi.sales_order_id = #{salesOrderId}")
    List<SalesOrderItem> selectSalesOrderItemsWithProductInfo(@Param("salesOrderId") Integer salesOrderId);

    @Insert("insert into sales_order_item (sales_order_id, product_id, quantity, sale_price) " +
            "values (#{salesOrderId}, #{productId}, #{quantity}, #{salePrice})")
    int insert(SalesOrderItem salesOrderItem);
}