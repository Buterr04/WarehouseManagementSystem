package com.dai.wms.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.dai.wms.entity.DeliveryOrder;
import com.dai.wms.entity.DeliveryOrderItem;
import com.dai.wms.mapper.DeliveryOrderItemMapper;
import com.dai.wms.mapper.DeliveryOrderMapper;
import com.dai.wms.service.DeliveryOrderItemService;
import com.dai.wms.service.DeliveryOrderService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author dai
 * @since 2025-05-22
 */
@Service
public class DeliveryOrderServiceImpl extends ServiceImpl<DeliveryOrderMapper, DeliveryOrder> implements DeliveryOrderService {

    @Autowired
    private DeliveryOrderItemService deliveryOrderItemService;

    @Override
    public boolean saveDeliveryOrderWithItems(DeliveryOrder deliveryOrder) {
        boolean saved = this.save(deliveryOrder);
        if (saved && deliveryOrder.getDeliveryOrderItems() != null) {
            for (DeliveryOrderItem item : deliveryOrder.getDeliveryOrderItems()) {
                item.setDeliveryId(deliveryOrder.getDeliveryId());
            }
            return deliveryOrderItemService.saveBatch(deliveryOrder.getDeliveryOrderItems());
        }
        return saved;
    }

    @Override
    public boolean updateDeliveryOrderWithItems(DeliveryOrder deliveryOrder) {
        boolean updated = this.updateById(deliveryOrder);
        if (updated && deliveryOrder.getDeliveryOrderItems() != null) {
            deliveryOrderItemService.remove(new QueryWrapper<DeliveryOrderItem>().eq("delivery_order_id", deliveryOrder.getDeliveryId()));
            for (DeliveryOrderItem item : deliveryOrder.getDeliveryOrderItems()) {
                item.setDeliveryId(deliveryOrder.getDeliveryId());
            }
            return deliveryOrderItemService.saveBatch(deliveryOrder.getDeliveryOrderItems() );
        }
        return updated;
    }

    @Override
    public DeliveryOrder findByIdWithItems(Integer deliveryOrderId) {
        DeliveryOrder order = this.getById(deliveryOrderId);
        if (order != null) {
            List<DeliveryOrderItem> items = deliveryOrderItemService.list(
                    new QueryWrapper<DeliveryOrderItem>().eq("delivery_order_id", deliveryOrderId)
            );
            order.setDeliveryOrderItems(items);
        }
        return order;
    }

    @Override
    public DeliveryOrder getDeliveryOrderWithDetails(Integer deliveryOrderId) {
        return this.baseMapper.selectDeliveryOrderByIdWithOrderDate(deliveryOrderId);
    }
}
