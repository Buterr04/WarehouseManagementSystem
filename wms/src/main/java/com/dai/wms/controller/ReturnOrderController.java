package com.dai.wms.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dai.wms.common.QueryPageParam;
import com.dai.wms.common.Result;
import com.dai.wms.entity.ReturnOrder;
import com.dai.wms.mapper.ProductMapper;
import com.dai.wms.service.ReturnOrderService;
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
@RequestMapping("/return-order")
public class ReturnOrderController {
    @Autowired
    private ReturnOrderService returnOrderService;

    @GetMapping("/list")
    public List<ReturnOrder> list() {
        return returnOrderService.list();
    }

    // 增
    @PostMapping("/save")
    public Result save(@RequestBody ReturnOrder returnOrder) {
        return returnOrderService.save(returnOrder)? Result.success() : Result.fail();
    }

    // 删
    @GetMapping("/delete")
    public Result delete(@RequestParam String id) {
        return returnOrderService.removeById(id)? Result.success() : Result.fail();
    }

    // 改
    @PostMapping("/modify")
    public Result mod(@RequestBody ReturnOrder returnOrder) {
        return returnOrderService.updateById(returnOrder)? Result.success() : Result.fail();
    }

    // 新增或修改
    @PostMapping("saveOrModify")
    public boolean saveOrModify(@RequestBody ReturnOrder returnOrder) {
        return returnOrderService.saveOrUpdate(returnOrder);
    }

//    // 查 模糊查询
//    @PostMapping("/listP")
//    public Result listP(@RequestBody ReturnOrder returnOrder) {
//        LambdaQueryWrapper<ReturnOrder> lambdaQueryWrapper = new LambdaQueryWrapper<>();
//        if(StringUtils.isNotBlank(returnOrder.getName())) {
//            lambdaQueryWrapper.like(ReturnOrder::getName, returnOrder.getName());
//        }
//        return Result.success(returnOrderService.list(lambdaQueryWrapper));
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
//        Page<ReturnOrder> page = new Page<>();
//        page.setCurrent(query.getPageNum());
//        page.setSize(query.getPageSize());
//
//        LambdaQueryWrapper<ReturnOrder> lambdaQueryWrapper = new LambdaQueryWrapper<>();
//        if(StringUtils.isNotBlank(name) && !name.equals("null")) {
//            lambdaQueryWrapper.like(ReturnOrder::getName, name);
//        }
//        if(StringUtils.isNotBlank(sex)) {
//            lambdaQueryWrapper.eq(ReturnOrder::getSex, sex);
//        }
//        if(StringUtils.isNotBlank(roleId)) {
//            lambdaQueryWrapper.eq(ReturnOrder::getRoleId, roleId);
//        }
//        IPage res = returnOrderService.page(page, lambdaQueryWrapper);
//
//        return Result.success(res.getTotal(), res.getRecords());
//    }
//
//    //按照id查询
//    @GetMapping("/findByNo")
//    public Result findByNo(@RequestParam String no) {
//        List<ReturnOrder> list = returnOrderService.lambdaQuery().eq(ReturnOrder::getNo, no).list();
//        return list.size()>0 ? Result.success(list) : Result.fail();
//    }

}
