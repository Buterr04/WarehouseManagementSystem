package com.dai.wms.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.dai.wms.common.Result;
import com.dai.wms.entity.SalesOrder;
import com.dai.wms.entity.SalesOrderItem;
import com.dai.wms.service.SalesOrderItemService;
import com.dai.wms.service.SalesOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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
@RequestMapping("/sales-order")
public class SalesOrderController {
    @Autowired
    private SalesOrderService salesOrderService;
    @Autowired
    private SalesOrderItemService salesOrderItemService;

    @GetMapping("/list")
    public Result list() {
        List<SalesOrder> salesOrderListWithCustomerName = salesOrderService.getSalesOrderListWithCustomerName();
        return Result.success(salesOrderListWithCustomerName);
    }

    // 增
    @PostMapping("/save")
    public Result save(@RequestBody SalesOrder salesOrder) {
        boolean salesOrderSaved = salesOrderService.save(salesOrder);

        if (salesOrderSaved) {
            Integer salesOrderId = salesOrder.getSalesOrderId();
            List<SalesOrderItem> salesOrderItems = salesOrder.getSalesOrderItems();
            if (salesOrderItems != null && !salesOrderItems.isEmpty()) {
                for (SalesOrderItem item : salesOrderItems) {
                    item.setSalesOrderId(salesOrderId);
                    // 前端传递了 salePrice，如果后端需要根据 productId 查询，则在此处理
                    salesOrderItemService.save(item);
                }
            }
            return Result.success();
        } else {
            return Result.fail();
        }
    }

    // 删
    @GetMapping("/delete")
    public Result delete(@RequestParam String id) {
        return salesOrderService.removeById(id) ? Result.success() : Result.fail();
    }

    // 改
    @PostMapping("/modify")
    public Result mod(@RequestBody SalesOrder salesOrder) {
        boolean salesOrderUpdated = salesOrderService.updateById(salesOrder);

        if (salesOrderUpdated) {
            Integer salesOrderId = salesOrder.getSalesOrderId();
            List<SalesOrderItem> salesOrderItems = salesOrder.getSalesOrderItems();
            if (salesOrderItems != null && !salesOrderItems.isEmpty()) {
                // 先删除与该销售单关联的所有现有明细项
                LambdaQueryWrapper<SalesOrderItem> queryWrapper = new LambdaQueryWrapper<>();
                queryWrapper.eq(SalesOrderItem::getSalesOrderId, salesOrderId);
                salesOrderItemService.remove(queryWrapper);

                // 然后保存前端传递过来的新明细项
                for (SalesOrderItem item : salesOrderItems) {
                    item.setSalesOrderId(salesOrderId);
                    salesOrderItemService.save(item);
                }
            }
            return Result.success();
        } else {
            return Result.fail();
        }
    }

    // 新增或修改
    @PostMapping("saveOrModify")
    public boolean saveOrModify(@RequestBody SalesOrder salesOrder) {
        return salesOrderService.saveOrUpdate(salesOrder);
    }

    // 根据ID查询
    @GetMapping("/{salesOrderId}")
    public Result getSalesOrder(@PathVariable Integer salesOrderId) {
        SalesOrder salesOrder = salesOrderService.findByIdWithCustomer(salesOrderId);
        if (salesOrder != null) {
            return Result.success(salesOrder);
        } else {
            return Result.fail();
        }
    }

    // 根据销售单ID查询明细
    @GetMapping("/items/{salesOrderId}")
    public Result getSalesOrderItemsWithProductInfo(@PathVariable Integer salesOrderId) {
        List<SalesOrderItem> itemsWithProductInfo = salesOrderItemService.getSalesOrderItemsWithProductInfo(salesOrderId);
        return Result.success(itemsWithProductInfo);
    }

    @PostMapping("/updateStatus")
    public Result updateStatus(@RequestBody Map<String, Object> data) {
        Integer salesOrderId = (Integer) data.get("salesOrderId");
        String status = (String) data.get("status");

        boolean success = salesOrderService.updateStatusById(salesOrderId, status);
        return success ? Result.success() : Result.fail();
    }


}