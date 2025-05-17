package com.dai.wms.service;

import com.dai.wms.entity.PurchaseOrderItem;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author dai
 * @since 2025-04-22
 */
public interface PurchaseOrderItemService extends IService<PurchaseOrderItem> {

    List<PurchaseOrderItem> getPurchaseOrderItemsByOrderId(Integer purchaseOrderId);
    List<PurchaseOrderItem> getPurchaseOrderItemsWithProductInfo(Integer purchaseOrderId);  //   新增方法
}