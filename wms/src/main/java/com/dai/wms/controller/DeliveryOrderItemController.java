package com.dai.wms.controller;


import com.dai.wms.common.Result;
import com.dai.wms.entity.DeliveryOrderItem;
import com.dai.wms.service.DeliveryOrderItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author dai
 * @since 2025-05-22
 */
@RestController
@RequestMapping("/delivery-order-item")
public class DeliveryOrderItemController {

    @Autowired
    private DeliveryOrderItemService deliveryOrderItemService;

    @GetMapping("/list")
    public List<DeliveryOrderItem> list() {
        return deliveryOrderItemService.list();
    }

    // 增
    @PostMapping("/save")
    public Result save(@RequestBody DeliveryOrderItem deliveryOrderItem) {
        return deliveryOrderItemService.save(deliveryOrderItem)? Result.success() : Result.fail();
    }

    // 删
    @GetMapping("/delete")
    public Result delete(@RequestParam String id) {
        return deliveryOrderItemService.removeById(id)? Result.success() : Result.fail();
    }

    // 改
    @PostMapping("/modify")
    public Result mod(@RequestBody DeliveryOrderItem deliveryOrderItem) {
        return deliveryOrderItemService.updateById(deliveryOrderItem)? Result.success() : Result.fail();
    }

    // 新增或修改
    @PostMapping("saveOrModify")
    public boolean saveOrModify(@RequestBody DeliveryOrderItem deliveryOrderItem) {
        return deliveryOrderItemService.saveOrUpdate(deliveryOrderItem);
    }
}
