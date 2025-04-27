package com.dai.wms.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dai.wms.common.QueryPageParam;
import com.dai.wms.common.Result;
import com.dai.wms.entity.ReturnOrderItem;
import com.dai.wms.mapper.ProductMapper;
import com.dai.wms.service.ReturnOrderItemService;
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
@RequestMapping("/return-order-item")
public class ReturnOrderItemController {
    @Autowired
    private ReturnOrderItemService returnOrderItemService;

    @GetMapping("/list")
    public List<ReturnOrderItem> list() {
        return returnOrderItemService.list();
    }

    // 增
    @PostMapping("/save")
    public Result save(@RequestBody ReturnOrderItem returnOrderItem) {
        return returnOrderItemService.save(returnOrderItem)? Result.success() : Result.fail();
    }

    // 删
    @GetMapping("/delete")
    public Result delete(@RequestParam String id) {
        return returnOrderItemService.removeById(id)? Result.success() : Result.fail();
    }

    // 改
    @PostMapping("/modify")
    public Result mod(@RequestBody ReturnOrderItem returnOrderItem) {
        return returnOrderItemService.updateById(returnOrderItem)? Result.success() : Result.fail();
    }

    // 新增或修改
    @PostMapping("saveOrModify")
    public boolean saveOrModify(@RequestBody ReturnOrderItem returnOrderItem) {
        return returnOrderItemService.saveOrUpdate(returnOrderItem);
    }

//    // 查 模糊查询
//    @PostMapping("/listP")
//    public Result listP(@RequestBody ReturnOrderItem returnOrderItem) {
//        LambdaQueryWrapper<ReturnOrderItem> lambdaQueryWrapper = new LambdaQueryWrapper<>();
//        if(StringUtils.isNotBlank(returnOrderItem.getName())) {
//            lambdaQueryWrapper.like(ReturnOrderItem::getName, returnOrderItem.getName());
//        }
//        return Result.success(returnOrderItemService.list(lambdaQueryWrapper));
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
//        Page<ReturnOrderItem> page = new Page<>();
//        page.setCurrent(query.getPageNum());
//        page.setSize(query.getPageSize());
//
//        LambdaQueryWrapper<ReturnOrderItem> lambdaQueryWrapper = new LambdaQueryWrapper<>();
//        if(StringUtils.isNotBlank(name) && !name.equals("null")) {
//            lambdaQueryWrapper.like(ReturnOrderItem::getName, name);
//        }
//        if(StringUtils.isNotBlank(sex)) {
//            lambdaQueryWrapper.eq(ReturnOrderItem::getSex, sex);
//        }
//        if(StringUtils.isNotBlank(roleId)) {
//            lambdaQueryWrapper.eq(ReturnOrderItem::getRoleId, roleId);
//        }
//        IPage res = returnOrderItemService.page(page, lambdaQueryWrapper);
//
//        return Result.success(res.getTotal(), res.getRecords());
//    }
//
//    //按照id查询
//    @GetMapping("/findByNo")
//    public Result findByNo(@RequestParam String no) {
//        List<ReturnOrderItem> list = returnOrderItemService.lambdaQuery().eq(ReturnOrderItem::getNo, no).list();
//        return list.size()>0 ? Result.success(list) : Result.fail();
//    }

}
