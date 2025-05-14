package com.dai.wms.service;

import com.dai.wms.entity.PurchasePlanItem;
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
public interface PurchasePlanItemService extends IService<PurchasePlanItem> {

    List<PurchasePlanItem> getPurchasePlanItemsByPurchasePlanId(Integer purchasePlanId);
    List<PurchasePlanItem> getPurchasePlanItemsWithProductInfo(Integer purchasePlanId); // 新增方法
}
