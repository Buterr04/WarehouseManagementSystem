package com.dai.wms.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dai.wms.common.QueryPageParam;
import com.dai.wms.common.Result;
import com.dai.wms.entity.PurchasePlan;
import com.dai.wms.service.PurchasePlanService;
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
@RequestMapping("/purchase-plan")
public class PurchasePlanController {
    @Autowired
    private PurchasePlanService purchasePlanService;

    @GetMapping("/list")
    public List<PurchasePlan> list() {
        return purchasePlanService.list();
    }

    // 增
    @PostMapping("/save")
    public Result save(@RequestBody PurchasePlan purchasePlan) {
        return purchasePlanService.save(purchasePlan)? Result.success() : Result.fail();
    }

    // 删
    @GetMapping("/delete")
    public Result delete(@RequestParam String id) {
        return purchasePlanService.removeById(id)? Result.success() : Result.fail();
    }

    // 改
    @PostMapping("/modify")
    public Result mod(@RequestBody PurchasePlan purchasePlan) {
        return purchasePlanService.updateById(purchasePlan)? Result.success() : Result.fail();
    }

    // 新增或修改
    @PostMapping("saveOrModify")
    public boolean saveOrModify(@RequestBody PurchasePlan purchasePlan) {
        return purchasePlanService.saveOrUpdate(purchasePlan);
    }

//    // 查 模糊查询
//    @PostMapping("/listP")
//    public Result listP(@RequestBody PurchasePlan purchasePlan) {
//        LambdaQueryWrapper<PurchasePlan> lambdaQueryWrapper = new LambdaQueryWrapper<>();
//        if(StringUtils.isNotBlank(purchasePlan.getName())) {
//            lambdaQueryWrapper.like(PurchasePlan::getName, purchasePlan.getName());
//        }
//        return Result.success(purchasePlanService.list(lambdaQueryWrapper));
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
//        Page<PurchasePlan> page = new Page<>();
//        page.setCurrent(query.getPageNum());
//        page.setSize(query.getPageSize());
//
//        LambdaQueryWrapper<PurchasePlan> lambdaQueryWrapper = new LambdaQueryWrapper<>();
//        if(StringUtils.isNotBlank(name) && !name.equals("null")) {
//            lambdaQueryWrapper.like(PurchasePlan::getName, name);
//        }
//        if(StringUtils.isNotBlank(sex)) {
//            lambdaQueryWrapper.eq(PurchasePlan::getSex, sex);
//        }
//        if(StringUtils.isNotBlank(roleId)) {
//            lambdaQueryWrapper.eq(PurchasePlan::getRoleId, roleId);
//        }
//        IPage res = purchasePlanService.page(page, lambdaQueryWrapper);
//
//        return Result.success(res.getTotal(), res.getRecords());
//    }
//
//    //按照id查询
//    @GetMapping("/findByNo")
//    public Result findByNo(@RequestParam String no) {
//        List<PurchasePlan> list = purchasePlanService.lambdaQuery().eq(PurchasePlan::getNo, no).list();
//        return list.size()>0 ? Result.success(list) : Result.fail();
//    }

}
