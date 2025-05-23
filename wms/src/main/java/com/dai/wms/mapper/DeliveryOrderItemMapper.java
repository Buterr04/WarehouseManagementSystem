package com.dai.wms.mapper;

import com.dai.wms.entity.DeliveryOrderItem;
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
 * @since 2025-05-22
 */
@Mapper
public interface DeliveryOrderItemMapper extends BaseMapper<DeliveryOrderItem> {

    @Select("SELECT doi.delivery_item_id, doi.delivery_id, doi.product_id, p.product_name, p.specifications, p.stock_quantity, doi.quantity " +
            "FROM delivery_order_item doi " +
            "JOIN product p ON doi.product_id = p.product_id " +
            "WHERE doi.delivery_id = #{deliveryOrderId}")
    List<DeliveryOrderItem> selectDeliveryOrderItemsWithProductInfo(@Param("deliveryOrderId") Integer deliveryOrderId);
}
