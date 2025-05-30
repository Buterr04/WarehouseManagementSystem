package com.dai.wms.service.impl;

import com.dai.wms.entity.DeliveryOrderItem;
import com.dai.wms.mapper.DeliveryOrderItemMapper;
import com.dai.wms.service.DeliveryOrderItemService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
public class DeliveryOrderItemServiceImpl extends ServiceImpl<DeliveryOrderItemMapper, DeliveryOrderItem> implements DeliveryOrderItemService {

    @Autowired
    private DeliveryOrderItemMapper deliveryOrderItemMapper;

    @Override
    public List<DeliveryOrderItem> getDeliveryOrderItemsWithProductInfo(Integer deliveryId) {
        return deliveryOrderItemMapper.selectDeliveryOrderItemsWithProductInfo(deliveryId);
    }
}