package com.dai.wms.service.impl;

import com.dai.wms.entity.PurchasePlanItem;
import com.dai.wms.mapper.PurchasePlanItemMapper;
import com.dai.wms.service.PurchasePlanItemService;
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
public class PurchasePlanItemServiceImpl extends ServiceImpl<PurchasePlanItemMapper, PurchasePlanItem> implements PurchasePlanItemService {

    @Autowired
    private PurchasePlanItemMapper purchasePlanItemMapper;

    @Override
    public List<PurchasePlanItem> getPurchasePlanItemsByPurchasePlanId(Integer purchasePlanId) {
        return purchasePlanItemMapper.selectPurchasePlanItemsByPlanId(purchasePlanId);
    }

    @Override
    public List<PurchasePlanItem> getPurchasePlanItemsWithProductInfo(Integer purchasePlanId) {
        return purchasePlanItemMapper.selectPurchasePlanItemsWithProductInfo(purchasePlanId); // 新增方法的实现
    }
}
