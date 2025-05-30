package com.dai.wms.mapper;

import com.dai.wms.entity.DeliveryOrderItem;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
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
public interface DeliveryOrderItemMapper extends BaseMapper<DeliveryOrderItem> {

    @ResultMap("BaseResultMap")
    @Select("SELECT doi.delivery_item_id, doi.delivery_id, doi.product_id, p.product_name, p.specifications, doi.quantity " +
            "FROM delivery_order_item doi " +
            "JOIN product p ON doi.product_id = p.product_id " +
            "WHERE doi.delivery_id = #{deliveryId}")
    List<DeliveryOrderItem> selectDeliveryOrderItemsWithProductInfo(@Param("deliveryId") Integer deliveryId);

    @Insert("insert into delivery_order_item (delivery_id, product_id, quantity) " +
            "values (#{deliveryId}, #{productId}, #{quantity})")
    int insert(DeliveryOrderItem deliveryOrderItem);
}
