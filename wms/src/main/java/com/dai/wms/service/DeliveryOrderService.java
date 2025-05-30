package com.dai.wms.service;

import com.dai.wms.entity.DeliveryOrder;
import com.baomidou.mybatisplus.extension.service.IService;
import com.dai.wms.entity.DeliveryOrderItem;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author dai
 * @since 2025-05-22
 */
public interface DeliveryOrderService extends IService<DeliveryOrder> {

    boolean saveDeliveryOrderWithItems(DeliveryOrder deliveryOrder);
    boolean updateDeliveryOrderWithItems(DeliveryOrder deliveryOrder);
    boolean updateStatusById(Integer deliveryId, Integer status);
    List<DeliveryOrder> getDeliveryOrderListWithEmployeeName();
    DeliveryOrder findByIdWithEmployee(Integer deliveryId);

}
