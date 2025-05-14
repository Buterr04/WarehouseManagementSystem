package com.dai.wms.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.dai.wms.entity.PurchasePlan;
import com.dai.wms.entity.PurchasePlanItem;
import com.dai.wms.mapper.PurchasePlanItemMapper;
import com.dai.wms.mapper.PurchasePlanMapper;
import com.dai.wms.service.PurchasePlanService;
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
public class PurchasePlanServiceImpl extends ServiceImpl<PurchasePlanMapper, PurchasePlan> implements PurchasePlanService {

    @Autowired
    private PurchasePlanMapper purchasePlanMapper;
    @Autowired
    private PurchasePlanItemMapper purchasePlanItemMapper;

    @Override
    public List<PurchasePlan> list() {
        return purchasePlanMapper.selectPurchasePlanList();
    }

    @Override
    public PurchasePlan findByIdWithItems(Integer purchasePlanId) {
        PurchasePlan purchasePlan = purchasePlanMapper.selectPurchasePlanById(purchasePlanId);
        if (purchasePlan != null) {
            List<PurchasePlanItem> purchasePlanItems = purchasePlanItemMapper.selectPurchasePlanItemsByPlanId(purchasePlanId); // 修改这里
            purchasePlan.setPurchasePlanItems(purchasePlanItems);
        }
        return purchasePlan;
    }

    @Transactional
    @Override
    public boolean savePurchasePlanWithItems(PurchasePlan purchasePlan) {
        boolean purchasePlanSaved = this.save(purchasePlan);

        if (purchasePlanSaved) {
            Integer purchasePlanId = purchasePlan.getPlanId();
            List<PurchasePlanItem> purchasePlanItems = purchasePlan.getPurchasePlanItems();
            if (purchasePlanItems != null && !purchasePlanItems.isEmpty()) {
                for (PurchasePlanItem item : purchasePlanItems) {
                    item.setPlanId(purchasePlanId);
                    purchasePlanItemMapper.insert(item);
                }
            }
            return true;
        }
        return false;
    }

    @Transactional
    @Override
    public boolean updatePurchasePlanWithItems(PurchasePlan purchasePlan) {
        boolean purchasePlanUpdated = this.updateById(purchasePlan);

        if (purchasePlanUpdated) {
            Integer purchasePlanId = purchasePlan.getPlanId();
            LambdaQueryWrapper<PurchasePlanItem> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(PurchasePlanItem::getPlanId, purchasePlanId);
            purchasePlanItemMapper.delete(queryWrapper);

            List<PurchasePlanItem> purchasePlanItems = purchasePlan.getPurchasePlanItems();
            if (purchasePlanItems != null && !purchasePlanItems.isEmpty()) {
                for (PurchasePlanItem item : purchasePlanItems) {
                    item.setPlanId(purchasePlanId);
                    purchasePlanItemMapper.insert(item);
                }
            }
            return true;
        }
        return false;
    }
}
