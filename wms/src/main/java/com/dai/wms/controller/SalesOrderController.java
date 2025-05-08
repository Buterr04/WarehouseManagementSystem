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
            // 在更新销售单时，你可能需要处理销售明细项的更新或删除逻辑
            // 这里只是一个基本示例，你需要根据你的具体需求实现
            List<SalesOrderItem> salesOrderItems = salesOrder.getSalesOrderItems();
            if (salesOrderItems != null && !salesOrderItems.isEmpty()) {
                for (SalesOrderItem item : salesOrderItems) {
                    item.setSalesOrderId(salesOrderId);
                    salesOrderItemService.saveOrUpdate(item); // 使用 saveOrUpdate
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

    // 根据销售单ID查询明细
    @GetMapping("/items/{salesOrderId}")
    public Result getSalesOrderItems(@PathVariable Integer salesOrderId) {
        LambdaQueryWrapper<SalesOrderItem> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SalesOrderItem::getSalesOrderId, salesOrderId);
        List<SalesOrderItem> items = salesOrderItemService.list(queryWrapper);
        return Result.success(items);
    }
}