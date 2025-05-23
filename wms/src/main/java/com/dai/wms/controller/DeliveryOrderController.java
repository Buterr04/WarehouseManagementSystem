package com.dai.wms.controller;


import com.dai.wms.common.Result;
import com.dai.wms.entity.DeliveryOrder;
import com.dai.wms.entity.DeliveryOrderItem;
import com.dai.wms.service.DeliveryOrderItemService;
import com.dai.wms.service.DeliveryOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

import static org.apache.logging.log4j.message.MapMessage.MapFormat.JSON;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author dai
 * @since 2025-05-22
 */
@RestController
@RequestMapping("/delivery-order")
public class DeliveryOrderController {

    @Autowired
    private DeliveryOrderService deliveryOrderService;

    @Autowired
    private DeliveryOrderItemService deliveryOrderItemService;  // 注入 DeliveryOrderItemService

    // 获取发货单列表
    @GetMapping("/list")
    public Result list() {
        List<DeliveryOrder> deliveryOrderList = deliveryOrderService.list();
        return Result.success(deliveryOrderList);
    }

    // 新增发货单及其明细
    @PostMapping("/save")
    public Result save(@RequestBody DeliveryOrder deliveryOrder) {
        boolean deliveryOrderSaved = deliveryOrderService.saveDeliveryOrderWithItems(deliveryOrder);

        if (deliveryOrderSaved) {
            return Result.success();
        } else {
            return Result.fail();
        }
    }

    // 修改发货单及其明细
    @PostMapping("/modify")
    public Result modify(@RequestBody DeliveryOrder deliveryOrder) {
        boolean deliveryOrderUpdated = deliveryOrderService.updateDeliveryOrderWithItems(deliveryOrder);

        if (deliveryOrderUpdated) {
            return Result.success();
        } else {
            return Result.fail();
        }
    }

    // 根据发货单 ID 获取发货单信息
    @GetMapping("/{deliveryOrderId}")
    public Result getDeliveryOrder(@PathVariable Integer deliveryOrderId) {
        DeliveryOrder deliveryOrder = deliveryOrderService.findByIdWithItems(deliveryOrderId);
        if (deliveryOrder != null) {
            return Result.success(deliveryOrder);
        } else {
            return Result.fail();
        }
    }

    // 删除发货单
    @GetMapping("/delete")
    public Result delete(@RequestParam Integer deliveryOrderId) {
        return deliveryOrderService.removeById(deliveryOrderId) ? Result.success() : Result.fail();
    }

    // 获取带详细信息的发货单
    @GetMapping("/details/{deliveryOrderId}")
    public Result getDeliveryOrderWithDetails(@PathVariable Integer deliveryOrderId) {
        DeliveryOrder deliveryOrder = deliveryOrderService.getDeliveryOrderWithDetails(deliveryOrderId);
        if (deliveryOrder != null) {
            return Result.success(deliveryOrder);
        } else {
            return Result.fail();
        }
    }

    // 根据发货单 ID 获取明细项及商品信息
    @GetMapping("/items/{deliveryOrderId}")
    public Result getDeliveryOrderItemsWithProductInfo(@PathVariable Integer deliveryOrderId) {
        List<DeliveryOrderItem> items = deliveryOrderItemService.getDeliveryOrderItemsWithProductInfo(deliveryOrderId);
        return Result.success(items);
    }
}
