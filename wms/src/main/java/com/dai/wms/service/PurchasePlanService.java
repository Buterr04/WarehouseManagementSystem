package com.dai.wms.service;

import com.dai.wms.entity.PurchasePlan;
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
public interface PurchasePlanService extends IService<PurchasePlan> {

    boolean savePurchasePlanWithItems(PurchasePlan purchasePlan);
    boolean updatePurchasePlanWithItems(PurchasePlan purchasePlan);
    List<PurchasePlan> list();
    PurchasePlan findByIdWithItems(Integer purchasePlanId);
}
