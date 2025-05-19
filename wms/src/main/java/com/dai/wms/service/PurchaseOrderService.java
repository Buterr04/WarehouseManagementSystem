package com.dai.wms.service;

import com.dai.wms.entity.PurchaseOrder;
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
public interface PurchaseOrderService extends IService<PurchaseOrder> {

    boolean savePurchaseOrderWithItems(PurchaseOrder purchaseOrder);
    boolean updatePurchaseOrderWithItems(PurchaseOrder purchaseOrder);
    List<PurchaseOrder> list();
    PurchaseOrder findByIdWithItems(Integer purchaseOrderId);
    PurchaseOrder getPurchaseOrderWithDetails(Integer purchaseOrderId);  //   新增方法
    boolean updateStatusById(Integer orderId, String status);

}
