package com.dai.wms.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
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
    private DeliveryOrderItemService deliveryOrderItemService;

    @GetMapping("/list")
    public Result list() {
        List<DeliveryOrder> deliveryOrderListWithEmployeeName = deliveryOrderService.getDeliveryOrderListWithEmployeeName();
        return Result.success(deliveryOrderListWithEmployeeName);
    }

    // 增
    @PostMapping("/save")
    public Result save(@RequestBody DeliveryOrder deliveryOrder) {
        boolean deliveryOrderSaved = deliveryOrderService.save(deliveryOrder);

        if (deliveryOrderSaved) {
            Integer deliveryId = deliveryOrder.getDeliveryId();
            List<DeliveryOrderItem> deliveryOrderItems = deliveryOrder.getDeliveryOrderItems();
            if (deliveryOrderItems != null && !deliveryOrderItems.isEmpty()) {
                for (DeliveryOrderItem item : deliveryOrderItems) {
                    item.setDeliveryId(deliveryId);
                    deliveryOrderItemService.save(item);
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
        return deliveryOrderService.removeById(id) ? Result.success() : Result.fail();
    }

    // 改
    @PostMapping("/modify")
    public Result mod(@RequestBody DeliveryOrder deliveryOrder) {
        boolean deliveryOrderUpdated = deliveryOrderService.updateById(deliveryOrder);

        if (deliveryOrderUpdated) {
            Integer deliveryId = deliveryOrder.getDeliveryId();
            List<DeliveryOrderItem> deliveryOrderItems = deliveryOrder.getDeliveryOrderItems();
            if (deliveryOrderItems != null && !deliveryOrderItems.isEmpty()) {
                // 先删除与该交货单关联的所有现有明细项
                LambdaQueryWrapper<DeliveryOrderItem> queryWrapper = new LambdaQueryWrapper<>();
                queryWrapper.eq(DeliveryOrderItem::getDeliveryId, deliveryId);
                deliveryOrderItemService.remove(queryWrapper);

                // 然后保存前端传递过来的新明细项
                for (DeliveryOrderItem item : deliveryOrderItems) {
                    item.setDeliveryId(deliveryId);
                    deliveryOrderItemService.save(item);
                }
            }
            return Result.success();
        } else {
            return Result.fail();
        }
    }

    // 新增或修改
    @PostMapping("saveOrModify")
    public boolean saveOrModify(@RequestBody DeliveryOrder deliveryOrder) {
        return deliveryOrderService.saveOrUpdate(deliveryOrder);
    }

    // 根据ID查询
    @GetMapping("/{deliveryId}")
    public Result getDeliveryOrder(@PathVariable Integer deliveryId) {
        DeliveryOrder deliveryOrder = deliveryOrderService.findByIdWithEmployee(deliveryId);
        if (deliveryOrder != null) {
            return Result.success(deliveryOrder);
        } else {
            return Result.fail();
        }
    }

    // 根据交货单ID查询明细
    @GetMapping("/items/{deliveryId}")
    public Result getDeliveryOrderItemsWithProductInfo(@PathVariable Integer deliveryId) {
        List<DeliveryOrderItem> itemsWithProductInfo = deliveryOrderItemService.getDeliveryOrderItemsWithProductInfo(deliveryId);
        return Result.success(itemsWithProductInfo);
    }

    @PostMapping("/updateStatus")
    public Result updateStatus(@RequestBody Map<String, Object> data) {
        Integer deliveryId = (Integer) data.get("deliveryId");
        Integer status = (Integer) data.get("status"); // Assuming status is an Integer for DeliveryOrder

        boolean success = deliveryOrderService.updateStatusById(deliveryId, status);
        return success ? Result.success() : Result.fail();
    }
}
