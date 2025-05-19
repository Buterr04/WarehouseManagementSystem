package com.dai.wms.mapper;

import com.dai.wms.entity.PurchaseOrder;
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
public interface PurchaseOrderMapper extends BaseMapper<PurchaseOrder> {

    @Select("SELECT po.order_id, po.supplier_id, s.supplier_name, po.order_date, po.delivery_date, po.status " +
            "FROM purchase_order po " +
            "JOIN supplier s ON po.supplier_id = s.supplier_id")
    List<PurchaseOrder> selectPurchaseOrderList();


    @Select("SELECT order_id, supplier_id, order_date, delivery_date, status FROM purchase_order WHERE order_id = #{orderId}")
    PurchaseOrder selectPurchaseOrderById(Integer orderId);

    @Select("SELECT po.order_id, pp.purchase_date AS plan_date, po.supplier_id, s.supplier_name, po.order_date, po.delivery_date, po.status " +
            "FROM purchase_order po " +
            "JOIN supplier s ON po.supplier_id = s.supplier_id " +
            "WHERE po.order_id = #{orderId}")
    PurchaseOrder selectPurchaseOrderWithDetails(Integer orderId);

    @Update("UPDATE purchase_order SET status = #{status} WHERE order_id = #{orderId}")
    void updateStatusById(@Param("orderId") Integer orderId, @Param("status") String status);

}
