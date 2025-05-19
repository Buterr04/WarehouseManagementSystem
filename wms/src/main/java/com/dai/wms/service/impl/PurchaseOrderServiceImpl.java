package com.dai.wms.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.dai.wms.entity.PurchaseOrder;
import com.dai.wms.entity.PurchaseOrderItem;
import com.dai.wms.mapper.PurchaseOrderItemMapper;
import com.dai.wms.mapper.PurchaseOrderMapper;
import com.dai.wms.service.PurchaseOrderService;
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
 * @since 2025-04-22
 */
@Service
public class PurchaseOrderServiceImpl extends ServiceImpl<PurchaseOrderMapper, PurchaseOrder> implements PurchaseOrderService {

    @Autowired
    private PurchaseOrderMapper purchaseOrderMapper;
    @Autowired
    private PurchaseOrderItemMapper purchaseOrderItemMapper;

    @Override
    public List<PurchaseOrder> list() {
        return purchaseOrderMapper.selectPurchaseOrderList();
    }

    @Override
    public PurchaseOrder findByIdWithItems(Integer purchaseOrderId) {
        PurchaseOrder purchaseOrder = purchaseOrderMapper.selectPurchaseOrderById(purchaseOrderId);
        if (purchaseOrder != null) {
            List<PurchaseOrderItem> purchaseOrderItems = purchaseOrderItemMapper.selectPurchaseOrderItemsByOrderId(purchaseOrderId);
            purchaseOrder.setPurchaseOrderItems(purchaseOrderItems);
        }
        return purchaseOrder;
    }

    @Transactional
    @Override
    public boolean savePurchaseOrderWithItems(PurchaseOrder purchaseOrder) {
        boolean purchaseOrderSaved = this.save(purchaseOrder);

        if (purchaseOrderSaved) {
            Integer purchaseOrderId = purchaseOrder.getOrderId();
            List<PurchaseOrderItem> purchaseOrderItems = purchaseOrder.getPurchaseOrderItems();
            if (purchaseOrderItems != null && !purchaseOrderItems.isEmpty()) {
                for (PurchaseOrderItem item : purchaseOrderItems) {
                    item.setOrderId(purchaseOrderId);
                    purchaseOrderItemMapper.insert(item);
                }
            }
            return true;
        }
        return false;
    }

    @Transactional
    @Override
    public boolean updatePurchaseOrderWithItems(PurchaseOrder purchaseOrder) {
        boolean purchaseOrderUpdated = this.updateById(purchaseOrder);

        if (purchaseOrderUpdated) {
            Integer purchaseOrderId = purchaseOrder.getOrderId();
            LambdaQueryWrapper<PurchaseOrderItem> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(PurchaseOrderItem::getOrderId, purchaseOrderId);
            purchaseOrderItemMapper.delete(queryWrapper);

            List<PurchaseOrderItem> purchaseOrderItems = purchaseOrder.getPurchaseOrderItems();
            if (purchaseOrderItems != null && !purchaseOrderItems.isEmpty()) {
                for (PurchaseOrderItem item : purchaseOrderItems) {
                    item.setOrderId(purchaseOrderId);
                    purchaseOrderItemMapper.insert(item);
                }
            }
            return true;
        }
        return false;
    }

    @Override
    public PurchaseOrder getPurchaseOrderWithDetails(Integer purchaseOrderId) {
        return purchaseOrderMapper.selectPurchaseOrderWithDetails(purchaseOrderId); //   新增方法的实现
    }

    @Override
    public boolean updateStatusById(Integer orderId, String status) {
        purchaseOrderMapper.updateStatusById(orderId, status);
        return true;
    }

}