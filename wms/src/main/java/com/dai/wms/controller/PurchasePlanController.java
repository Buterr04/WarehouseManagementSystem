package com.dai.wms.controller;



import com.dai.wms.common.Result;
import com.dai.wms.entity.PurchasePlan;
import com.dai.wms.entity.PurchasePlanItem;
import com.dai.wms.service.PurchasePlanService;
import com.dai.wms.service.PurchasePlanItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


import java.util.List;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author dai
 * @since 2025-04-22
 */
@RestController
@RequestMapping("/purchase-plan")
public class PurchasePlanController {

    @Autowired
    private PurchasePlanService purchasePlanService;
    @Autowired
    private PurchasePlanItemService purchasePlanItemService; // 注入 PurchasePlanItemService

    @GetMapping("/list")
    public Result list() {
        List<PurchasePlan> purchasePlanList = purchasePlanService.list();
        return Result.success(purchasePlanList);
    }

    @PostMapping("/save")
    public Result save(@RequestBody PurchasePlan purchasePlan) {
        boolean purchasePlanSaved = purchasePlanService.savePurchasePlanWithItems(purchasePlan);

        if (purchasePlanSaved) {
            return Result.success();
        } else {
            return Result.fail();
        }
    }

    @PostMapping("/modify")
    public Result modify(@RequestBody PurchasePlan purchasePlan) {
        boolean purchasePlanUpdated = purchasePlanService.updatePurchasePlanWithItems(purchasePlan);

        if (purchasePlanUpdated) {
            return Result.success();
        } else {
            return Result.fail();
        }
    }

    @GetMapping("/{purchasePlanId}")
    public Result getPurchasePlan(@PathVariable Integer purchasePlanId) {
        PurchasePlan purchasePlan = purchasePlanService.findByIdWithItems(purchasePlanId);
        if (purchasePlan != null) {
            return Result.success(purchasePlan);
        } else {
            return Result.fail();
        }
    }

    @GetMapping("/delete")
    public Result delete(@RequestParam Integer purchasePlanId) {
        return purchasePlanService.removeById(purchasePlanId) ? Result.success() : Result.fail();
    }

    // 新增方法：根据计划单 ID 查询明细，并包含商品信息
    @GetMapping("/items/{purchasePlanId}")
    public Result getPurchasePlanItemsWithProductInfo(@PathVariable Integer purchasePlanId) {
        List<PurchasePlanItem> items = purchasePlanItemService.getPurchasePlanItemsWithProductInfo(purchasePlanId);
        return Result.success(items);
    }
}
