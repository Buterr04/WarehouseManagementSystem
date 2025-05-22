package com.dai.wms.service.impl;

import com.dai.wms.entity.PurchaseOrderItem;
import com.dai.wms.mapper.PurchaseOrderItemMapper;
import com.dai.wms.service.PurchaseOrderItemService;
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
 * @since 2025-04-22
 */
@Service
public class PurchaseOrderItemServiceImpl extends ServiceImpl<PurchaseOrderItemMapper, PurchaseOrderItem> implements PurchaseOrderItemService {

    @Autowired
    private PurchaseOrderItemMapper purchaseOrderItemMapper;

    @Override
    public List<PurchaseOrderItem> getPurchaseOrderItemsByOrderId(Integer purchaseOrderId) {
        return purchaseOrderItemMapper.selectPurchaseOrderItemsByOrderId(purchaseOrderId);
    }
    @Override
    public List<PurchaseOrderItem> getPurchaseOrderItemsWithProductInfo(Integer purchaseOrderId) {
        return purchaseOrderItemMapper.selectPurchaseOrderItemsWithProductInfo(purchaseOrderId);  //   新增方法的实现
    }

    @Override
    public List<PurchaseOrderItem> getAllPurchaseOrderItems() {
        return purchaseOrderItemMapper.selectAllPurchaseOrderItems();
    }
}