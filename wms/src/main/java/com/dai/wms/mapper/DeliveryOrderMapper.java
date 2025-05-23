package com.dai.wms.mapper;

import com.dai.wms.entity.DeliveryOrder;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author dai
 * @since 2025-05-22
 */
@Mapper
public interface DeliveryOrderMapper extends BaseMapper<DeliveryOrder> {

    @Select("SELECT do.delivery_id, do.purchase_order_id, do.employee_id, do.delivery_date, do.status, po.order_date AS OrderDate " +
            "FROM delivery_order do " +
            "JOIN purchase_order po ON do.purchase_order_id = po.purchase_order_id")
    List<DeliveryOrder> selectDeliveryOrderListWithOrderDate();

    @Select("SELECT do.delivery_id, do.purchase_order_id, do.employee_id, do.delivery_date, do.status, po.order_date AS OrderDate " +
            "FROM delivery_order do " +
            "JOIN purchase_order po ON do.purchase_order_id = po.purchase_order_id " +
            "WHERE do.delivery_id = #{deliveryOrderId}")
    DeliveryOrder selectDeliveryOrderByIdWithOrderDate(Integer deliveryOrderId);

    @Update("UPDATE delivery_order SET status = #{status} WHERE delivery_order_id = #{deliveryOrderId}")
    void updateStatusById(@Param("deliveryOrderId") Integer deliveryOrderId, @Param("status") String status);
}
