package com.dai.wms.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dai.wms.common.QueryPageParam;
import com.dai.wms.common.Result;
import com.dai.wms.entity.PurchaseOrderItem;
import com.dai.wms.service.PurchaseOrderItemService;
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
@RequestMapping("/purchase-order-item")
public class PurchaseOrderItemController {
    @Autowired
    private PurchaseOrderItemService purchaseOrderItemService;

    @GetMapping("/list")
    public List<PurchaseOrderItem> list() {
        return purchaseOrderItemService.list();
    }

    // 增
    @PostMapping("/save")
    public Result save(@RequestBody PurchaseOrderItem purchaseOrderItem) {
        return purchaseOrderItemService.save(purchaseOrderItem)? Result.success() : Result.fail();
    }

    // 删
    @GetMapping("/delete")
    public Result delete(@RequestParam String id) {
        return purchaseOrderItemService.removeById(id)? Result.success() : Result.fail();
    }

    // 改
    @PostMapping("/modify")
    public Result mod(@RequestBody PurchaseOrderItem purchaseOrderItem) {
        return purchaseOrderItemService.updateById(purchaseOrderItem)? Result.success() : Result.fail();
    }

    // 新增或修改
    @PostMapping("saveOrModify")
    public boolean saveOrModify(@RequestBody PurchaseOrderItem purchaseOrderItem) {
        return purchaseOrderItemService.saveOrUpdate(purchaseOrderItem);
    }

    // 查 模糊查询
//    @PostMapping("/listP")
//    public Result listP(@RequestBody PurchaseOrderItem purchaseOrderItem) {
//        LambdaQueryWrapper<PurchaseOrderItem> lambdaQueryWrapper = new LambdaQueryWrapper<>();
//        if(StringUtils.isNotBlank(purchaseOrderItem.getName())) {
//            lambdaQueryWrapper.like(PurchaseOrderItem::getName, purchaseOrderItem.getName());
//        }
//        return Result.success(purchaseOrderItemService.list(lambdaQueryWrapper));
//    }
//
//    // 分页查询
//    @PostMapping("/listPage")
//    public Result listPage(@RequestBody QueryPageParam query) {
//        HashMap param = query.getParam();
//        String name = (String) param.get("name");
//        String sex = (String) param.get("sex");
//        String roleId = (String) param.get("roleId");
//
//        Page<PurchaseOrderItem> page = new Page<>();
//        page.setCurrent(query.getPageNum());
//        page.setSize(query.getPageSize());
//
//        LambdaQueryWrapper<PurchaseOrderItem> lambdaQueryWrapper = new LambdaQueryWrapper<>();
//        if(StringUtils.isNotBlank(name) && !name.equals("null")) {
//            lambdaQueryWrapper.like(PurchaseOrderItem::getName, name);
//        }
//        if(StringUtils.isNotBlank(sex)) {
//            lambdaQueryWrapper.eq(PurchaseOrderItem::getSex, sex);
//        }
//        if(StringUtils.isNotBlank(roleId)) {
//            lambdaQueryWrapper.eq(PurchaseOrderItem::getRoleId, roleId);
//        }
//        IPage res = purchaseOrderItemService.page(page, lambdaQueryWrapper);
//
//        return Result.success(res.getTotal(), res.getRecords());
//    }
//
//    //按照id查询
//    @GetMapping("/findByNo")
//    public Result findByNo(@RequestParam String no) {
//        List<PurchaseOrderItem> list = purchaseOrderItemService.lambdaQuery().eq(PurchaseOrderItem::getNo, no).list();
//        return list.size()>0 ? Result.success(list) : Result.fail();
//    }

}
