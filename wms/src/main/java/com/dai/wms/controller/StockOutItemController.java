package com.dai.wms.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dai.wms.common.QueryPageParam;
import com.dai.wms.common.Result;
import com.dai.wms.entity.StockOutItem;
import com.dai.wms.entity.StockOutItem;
import com.dai.wms.service.StockOutItemService;
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
@RequestMapping("/stock-out-item")
public class StockOutItemController {
    @Autowired
    private StockOutItemService stockOutItemService;

    @GetMapping("/list")
    public List<StockOutItem> list() {
        return stockOutItemService.list();
    }

    // 增
    @PostMapping("/save")
    public Result save(@RequestBody StockOutItem stockOutItem) {
        return stockOutItemService.save(stockOutItem)? Result.success() : Result.fail();
    }

    // 删
    @GetMapping("/delete")
    public Result delete(@RequestParam String id) {
        return stockOutItemService.removeById(id)? Result.success() : Result.fail();
    }

    // 改
    @PostMapping("/modify")
    public Result mod(@RequestBody StockOutItem stockOutItem) {
        return stockOutItemService.updateById(stockOutItem)? Result.success() : Result.fail();
    }

    // 新增或修改
    @PostMapping("saveOrModify")
    public boolean saveOrModify(@RequestBody StockOutItem stockOutItem) {
        return stockOutItemService.saveOrUpdate(stockOutItem);
    }

//    // 查 模糊查询
//    @PostMapping("/listP")
//    public Result listP(@RequestBody StockOutItem stockOutItem) {
//        LambdaQueryWrapper<StockOutItem> lambdaQueryWrapper = new LambdaQueryWrapper<>();
//        if(StringUtils.isNotBlank(stockOutItem.getName())) {
//            lambdaQueryWrapper.like(StockOutItem::getName, stockOutItem.getName());
//        }
//        return Result.success(stockOutItemService.list(lambdaQueryWrapper));
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
//        Page<StockOutItem> page = new Page<>();
//        page.setCurrent(query.getPageNum());
//        page.setSize(query.getPageSize());
//
//        LambdaQueryWrapper<StockOutItem> lambdaQueryWrapper = new LambdaQueryWrapper<>();
//        if(StringUtils.isNotBlank(name) && !name.equals("null")) {
//            lambdaQueryWrapper.like(StockOutItem::getName, name);
//        }
//        if(StringUtils.isNotBlank(sex)) {
//            lambdaQueryWrapper.eq(StockOutItem::getSex, sex);
//        }
//        if(StringUtils.isNotBlank(roleId)) {
//            lambdaQueryWrapper.eq(StockOutItem::getRoleId, roleId);
//        }
//        IPage res = stockOutItemService.page(page, lambdaQueryWrapper);
//
//        return Result.success(res.getTotal(), res.getRecords());
//    }
//
//    //按照id查询
//    @GetMapping("/findByNo")
//    public Result findByNo(@RequestParam String no) {
//        List<StockOutItem> list = stockOutItemService.lambdaQuery().eq(StockOutItem::getNo, no).list();
//        return list.size()>0 ? Result.success(list) : Result.fail();
//    }

}
