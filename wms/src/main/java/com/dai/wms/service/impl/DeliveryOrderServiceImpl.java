package com.dai.wms.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
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
    private DeliveryOrderMapper deliveryOrderMapper;
    @Autowired
    private DeliveryOrderItemMapper deliveryOrderItemMapper;

    @Override
    public List<DeliveryOrder> getDeliveryOrderListWithEmployeeName() {
        return deliveryOrderMapper.selectDeliveryOrderListWithEmployeeName();
    }

    @Override
    public DeliveryOrder findByIdWithEmployee(Integer deliveryId) {
        return deliveryOrderMapper.selectDeliveryOrderByIdWithEmployee(deliveryId);
    }

    @Transactional
    @Override
    public boolean saveDeliveryOrderWithItems(DeliveryOrder deliveryOrder) {
        boolean deliveryOrderSaved = this.save(deliveryOrder);

        if (deliveryOrderSaved) {
            Integer deliveryId = deliveryOrder.getDeliveryId();
            List<DeliveryOrderItem> deliveryOrderItems = deliveryOrder.getDeliveryOrderItems();
            if (deliveryOrderItems != null && !deliveryOrderItems.isEmpty()) {
                for (DeliveryOrderItem item : deliveryOrderItems) {
                    item.setDeliveryId(deliveryId);
                    deliveryOrderItemMapper.insert(item);
                }
            }
            return true;
        }
        return false;
    }

    @Transactional
    @Override
    public boolean updateDeliveryOrderWithItems(DeliveryOrder deliveryOrder) {
        boolean deliveryOrderUpdated = this.updateById(deliveryOrder);

        if (deliveryOrderUpdated) {
            Integer deliveryId = deliveryOrder.getDeliveryId();
            LambdaQueryWrapper<DeliveryOrderItem> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(DeliveryOrderItem::getDeliveryId, deliveryId);
            deliveryOrderItemMapper.delete(queryWrapper);

            List<DeliveryOrderItem> deliveryOrderItems = deliveryOrder.getDeliveryOrderItems();
            if (deliveryOrderItems != null && !deliveryOrderItems.isEmpty()) {
                for (DeliveryOrderItem item : deliveryOrderItems) {
                    item.setDeliveryId(deliveryId);
                    deliveryOrderItemMapper.insert(item);
                }
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean updateStatusById(Integer deliveryId, Integer status) {
        deliveryOrderMapper.updateStatusById(deliveryId, status);
        return true;
    }
}
