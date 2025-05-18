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
        boolean saved = this.save(purchasePlan);  // MyBatis-Plus 自动生成 INSERT 语句

        if (saved) {
            Integer planId = purchasePlan.getPlanId();  // 自增主键自动回填
            List<PurchasePlanItem> items = purchasePlan.getPurchasePlanItems();

            if (items != null && !items.isEmpty()) {
                for (PurchasePlanItem item : items) {
                    item.setPlanId(planId);  // 设置外键
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

    @Override
    public boolean updateStatusById(Integer planId, String status) {
        purchasePlanMapper.updateStatusById(planId, status);
        return true;
    }

}
