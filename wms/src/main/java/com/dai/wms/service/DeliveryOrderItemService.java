package com.dai.wms.service;

import com.dai.wms.entity.DeliveryOrderItem;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author dai
 * @since 2025-05-22
 */
public interface DeliveryOrderItemService extends IService<DeliveryOrderItem> {

    List<DeliveryOrderItem> getDeliveryOrderItemsWithProductInfo(Integer deliveryOrderId);

}
