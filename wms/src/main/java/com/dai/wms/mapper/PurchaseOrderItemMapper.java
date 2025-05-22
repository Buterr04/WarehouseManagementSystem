package com.dai.wms.mapper;

import com.dai.wms.entity.PurchaseOrderItem;
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
public interface PurchaseOrderItemMapper extends BaseMapper<PurchaseOrderItem> {

    @Select("SELECT order_item_id, order_id, product_id, quantity, purchase_price FROM purchase_order_item WHERE order_id = #{orderId}")
    List<PurchaseOrderItem> selectPurchaseOrderItemsByOrderId(@Param("orderId") Integer orderId);

    @Select("SELECT poi.order_item_id, poi.order_id, poi.product_id, p.product_name, p.specifications, poi.quantity, poi.purchase_price " +
            "FROM purchase_order_item poi " +
            "JOIN product p ON poi.product_id = p.product_id " +
            "WHERE poi.order_id = #{orderId}")
    List<PurchaseOrderItem> selectPurchaseOrderItemsWithProductInfo(@Param("orderId") Integer orderId);

    @Select("SELECT poi.order_item_id, poi.order_id, poi.product_id, poi.quantity, poi.purchase_price, p.product_name, p.specifications " +
            "FROM purchase_order_item poi " +
            "JOIN product p ON poi.product_id = p.product_id")
    List<PurchaseOrderItem> selectAllPurchaseOrderItems();
}