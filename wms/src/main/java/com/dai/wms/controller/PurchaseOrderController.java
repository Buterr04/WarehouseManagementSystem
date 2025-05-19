package com.dai.wms.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dai.wms.common.QueryPageParam;
import com.dai.wms.common.Result;
import com.dai.wms.entity.PurchaseOrder;
import com.dai.wms.entity.PurchaseOrderItem;
import com.dai.wms.service.PurchaseOrderService;
import com.dai.wms.service.PurchaseOrderItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author dai
 * @since 2025-04-22
 */
@RestController
@RequestMapping("/purchase-order")
public class PurchaseOrderController {
    @Autowired
    private PurchaseOrderService purchaseOrderService;
    @Autowired
    private PurchaseOrderItemService purchaseOrderItemService;  //   注入 PurchaseOrderItemService


    @GetMapping("/list")
    public Result list() {
        List<PurchaseOrder> purchaseOrderList = purchaseOrderService.list();
        return Result.success(purchaseOrderList);
    }

    @PostMapping("/save")
    public Result save(@RequestBody PurchaseOrder purchaseOrder) {
        boolean purchaseOrderSaved = purchaseOrderService.savePurchaseOrderWithItems(purchaseOrder);

        if (purchaseOrderSaved) {
            return Result.success();
        } else {
            return Result.fail();
        }
    }

    @PostMapping("/modify")
    public Result modify(@RequestBody PurchaseOrder purchaseOrder) {
        boolean purchaseOrderUpdated = purchaseOrderService.updatePurchaseOrderWithItems(purchaseOrder);

        if (purchaseOrderUpdated) {
            return Result.success();
        } else {
            return Result.fail();
        }
    }

    @GetMapping("/{purchaseOrderId}")
    public Result getPurchaseOrder(@PathVariable Integer purchaseOrderId) {
        PurchaseOrder purchaseOrder = purchaseOrderService.findByIdWithItems(purchaseOrderId);
        if (purchaseOrder != null) {
            return Result.success(purchaseOrder);
        } else {
            return Result.fail();
        }
    }

    @GetMapping("/delete")
    public Result delete(@RequestParam Integer purchaseOrderId) {
        return purchaseOrderService.removeById(purchaseOrderId) ? Result.success() : Result.fail();
    }

    @GetMapping("/details/{purchaseOrderId}")
    public Result getPurchaseOrderWithDetails(@PathVariable Integer purchaseOrderId) {
        PurchaseOrder purchaseOrder = purchaseOrderService.getPurchaseOrderWithDetails(purchaseOrderId);
        if (purchaseOrder != null) {
            return Result.success(purchaseOrder);
        } else {
            return Result.fail();
        }
    }

    //   新增方法：根据订单 ID 查询明细，并包含商品信息
    @GetMapping("/items/{purchaseOrderId}")
    public Result getPurchaseOrderItemsWithProductInfo(@PathVariable Integer purchaseOrderId) {
        List<PurchaseOrderItem> items = purchaseOrderItemService.getPurchaseOrderItemsWithProductInfo(purchaseOrderId);
        return Result.success(items);
    }

    @PostMapping("/updateStatus")
    public Result updateStatus(@RequestBody Map<String, Object> data) {
        Integer orderId = (Integer) data.get("orderId");
        String status = (String) data.get("status");

        boolean success = purchaseOrderService.updateStatusById(orderId, status);
        return success ? Result.success() : Result.fail();
    }

}
