package com.dai.wms.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dai.wms.common.QueryPageParam;
import com.dai.wms.common.Result;
import com.dai.wms.entity.SalesOrder;
import com.dai.wms.service.ReturnOrderService;
import com.dai.wms.service.SalesOrderService;
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
@RequestMapping("/sales-order")
public class SalesOrderController {
    @Autowired
    private SalesOrderService salesOrderService;

    @GetMapping("/list")
    public List<SalesOrder> list() {
        return salesOrderService.list();
    }

    // 增
    @PostMapping("/save")
    public Result save(@RequestBody SalesOrder salesOrder) {
        return salesOrderService.save(salesOrder)? Result.success() : Result.fail();
    }

    // 删
    @GetMapping("/delete")
    public Result delete(@RequestParam String id) {
        return salesOrderService.removeById(id)? Result.success() : Result.fail();
    }

    // 改
    @PostMapping("/modify")
    public Result mod(@RequestBody SalesOrder salesOrder) {
        return salesOrderService.updateById(salesOrder)? Result.success() : Result.fail();
    }

    // 新增或修改
    @PostMapping("saveOrModify")
    public boolean saveOrModify(@RequestBody SalesOrder salesOrder) {
        return salesOrderService.saveOrUpdate(salesOrder);
    }

//    // 查 模糊查询
//    @PostMapping("/listP")
//    public Result listP(@RequestBody SalesOrder salesOrder) {
//        LambdaQueryWrapper<SalesOrder> lambdaQueryWrapper = new LambdaQueryWrapper<>();
//        if(StringUtils.isNotBlank(salesOrder.getName())) {
//            lambdaQueryWrapper.like(SalesOrder::getName, salesOrder.getName());
//        }
//        return Result.success(salesOrderService.list(lambdaQueryWrapper));
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
//        Page<SalesOrder> page = new Page<>();
//        page.setCurrent(query.getPageNum());
//        page.setSize(query.getPageSize());
//
//        LambdaQueryWrapper<SalesOrder> lambdaQueryWrapper = new LambdaQueryWrapper<>();
//        if(StringUtils.isNotBlank(name) && !name.equals("null")) {
//            lambdaQueryWrapper.like(SalesOrder::getName, name);
//        }
//        if(StringUtils.isNotBlank(sex)) {
//            lambdaQueryWrapper.eq(SalesOrder::getSex, sex);
//        }
//        if(StringUtils.isNotBlank(roleId)) {
//            lambdaQueryWrapper.eq(SalesOrder::getRoleId, roleId);
//        }
//        IPage res = salesOrderService.page(page, lambdaQueryWrapper);
//
//        return Result.success(res.getTotal(), res.getRecords());
//    }
//
//    //按照id查询
//    @GetMapping("/findByNo")
//    public Result findByNo(@RequestParam String no) {
//        List<SalesOrder> list = salesOrderService.lambdaQuery().eq(SalesOrder::getNo, no).list();
//        return list.size()>0 ? Result.success(list) : Result.fail();
//    }

}
