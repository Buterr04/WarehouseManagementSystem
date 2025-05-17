package com.dai.wms.mapper;

import com.dai.wms.entity.PurchaseOrder;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Delete;
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
public interface PurchaseOrderMapper extends BaseMapper<PurchaseOrder> {

    @Select("SELECT order_id, plan_id, supplier_id, order_date, delivery_date FROM purchase_order")
    List<PurchaseOrder> selectPurchaseOrderList();

    @Select("SELECT order_id, plan_id, supplier_id, order_date, delivery_date FROM purchase_order WHERE order_id = #{orderId}")
    PurchaseOrder selectPurchaseOrderById(Integer orderId);

    @Select("SELECT po.order_id, po.plan_id, pp.purchase_date AS plan_date, po.supplier_id, s.supplier_name, po.order_date, po.delivery_date " +
            "FROM purchase_order po " +
            "JOIN supplier s ON po.supplier_id = s.supplier_id " +
            "WHERE po.order_id = #{orderId}")
    PurchaseOrder selectPurchaseOrderWithDetails(Integer orderId);
}
