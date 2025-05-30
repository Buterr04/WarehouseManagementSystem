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

    @Select("SELECT do.delivery_id, do.stock_out_id, do.delivery_date, do.employee_id, do.status " +
            "FROM delivery_order do " +
            "JOIN user e ON do.employee_id = e.id")
    List<DeliveryOrder> selectDeliveryOrderListWithEmployeeName();

    @Select("SELECT do.delivery_id, do.stock_out_id, do.delivery_date, do.employee_id, do.status " +
            "FROM delivery_order do " +
            "JOIN user e ON do.employee_id = e.id " +
            "WHERE do.delivery_id = #{deliveryId}")
    DeliveryOrder selectDeliveryOrderByIdWithEmployee(Integer deliveryId);

    @Update("UPDATE delivery_order SET status = #{status} WHERE delivery_id = #{deliveryId}")
    void updateStatusById(@Param("deliveryId") Integer deliveryId, @Param("status") Integer status);

}
