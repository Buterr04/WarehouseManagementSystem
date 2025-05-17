package com.dai.wms.controller;



import com.dai.wms.common.Result;
import com.dai.wms.entity.ReturnOrder;
import com.dai.wms.entity.ReturnOrderItem;
import com.dai.wms.service.ReturnOrderService;
import com.dai.wms.service.ReturnOrderItemService;
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
@RequestMapping("/return-order")
public class ReturnOrderController {
    @Autowired
    private ReturnOrderService returnOrderService;
    @Autowired
    private ReturnOrderItemService returnOrderItemService;

    @GetMapping("/list")
    public Result list() {
        List<ReturnOrder> returnOrderList = returnOrderService.list();
        return Result.success(returnOrderList);
    }

    @PostMapping("/save")
    public Result save(@RequestBody ReturnOrder returnOrder) {
        boolean returnOrderSaved = returnOrderService.saveReturnOrderWithItems(returnOrder);

        if (returnOrderSaved) {
            return Result.success();
        } else {
            return Result.fail();
        }
    }

    @PostMapping("/modify")
    public Result modify(@RequestBody ReturnOrder returnOrder) {
        boolean returnOrderUpdated = returnOrderService.updateReturnOrderWithItems(returnOrder);

        if (returnOrderUpdated) {
            return Result.success();
        } else {
            return Result.fail();
        }
    }

    @GetMapping("/{returnId}")
    public Result getReturnOrder(@PathVariable Integer returnId) {
        ReturnOrder returnOrder = returnOrderService.findByIdWithItems(returnId);
        if (returnOrder != null) {
            return Result.success(returnOrder);
        } else {
            return Result.fail();
        }
    }

    @GetMapping("/delete")
    public Result delete(@RequestParam Integer returnId) {
        return returnOrderService.removeById(returnId) ? Result.success() : Result.fail();
    }

    //   新增方法：根据退货单 ID 查询退货单详情，包含关联的销售单和客户信息
    @GetMapping("/details/{returnId}")
    public Result getReturnOrderWithDetails(@PathVariable Integer returnId) {
        ReturnOrder returnOrder = returnOrderService.findByIdWithItems(returnId); // 这里的实现需要调整，具体见下面的说明
        if (returnOrder != null) {
            return Result.success(returnOrder);
        } else {
            return Result.fail();
        }
    }

    //   新增方法：根据退货单 ID 查询明细，并包含商品信息
    @GetMapping("/items/{returnId}")
    public Result getReturnOrderItemsWithProductInfo(@PathVariable Integer returnId) {
        List<ReturnOrderItem> items = returnOrderItemService.getReturnOrderItemsWithProductInfo(returnId);
        return Result.success(items);
    }

}
