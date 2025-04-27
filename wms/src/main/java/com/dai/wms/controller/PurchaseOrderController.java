package com.dai.wms.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dai.wms.common.QueryPageParam;
import com.dai.wms.common.Result;
import com.dai.wms.entity.PurchaseOrder;
import com.dai.wms.service.PurchaseOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
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
@RequestMapping("/purchase-order")
public class PurchaseOrderController {
    @Autowired
    private PurchaseOrderService purchaseOrderService;

    @GetMapping("/list")
    public List<PurchaseOrder> list() {
        return purchaseOrderService.list();
    }

    // 增
    @PostMapping("/save")
    public Result save(@RequestBody PurchaseOrder purchaseOrder) {
        return purchaseOrderService.save(purchaseOrder)? Result.success() : Result.fail();
    }

    // 删
    @GetMapping("/delete")
    public Result delete(@RequestParam String id) {
        return purchaseOrderService.removeById(id)? Result.success() : Result.fail();
    }

    // 改
    @PostMapping("/modify")
    public Result mod(@RequestBody PurchaseOrder purchaseOrder) {
        return purchaseOrderService.updateById(purchaseOrder)? Result.success() : Result.fail();
    }

    // 新增或修改
    @PostMapping("saveOrModify")
    public boolean saveOrModify(@RequestBody PurchaseOrder purchaseOrder) {
        return purchaseOrderService.saveOrUpdate(purchaseOrder);
    }
//
//    // 查 模糊查询
//    @PostMapping("/listP")
//    public Result listP(@RequestBody PurchaseOrder purchaseOrder) {
//        LambdaQueryWrapper<PurchaseOrder> lambdaQueryWrapper = new LambdaQueryWrapper<>();
//        if(StringUtils.isNotBlank(purchaseOrder.getName())) {
//            lambdaQueryWrapper.like(PurchaseOrder::getName, purchaseOrder.getName());
//        }
//        return Result.success(purchaseOrderService.list(lambdaQueryWrapper));
//    }
//
//    //按照id查询
//    @GetMapping("/findByNo")
//    public Result findByNo(@RequestParam String no) {
//        List<PurchaseOrder> list = purchaseOrderService.lambdaQuery().eq(PurchaseOrder::getNo, no).list();
//        return list.size()>0 ? Result.success(list) : Result.fail();
//    }
}
