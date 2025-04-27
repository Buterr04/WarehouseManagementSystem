package com.dai.wms.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dai.wms.common.QueryPageParam;
import com.dai.wms.common.Result;
import com.dai.wms.entity.StockOut;
import com.dai.wms.entity.StockOut;
import com.dai.wms.service.StockOutService;
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
@RequestMapping("/stock-out")
public class StockOutController {
    @Autowired
    private StockOutService stockOutService;

    @GetMapping("/list")
    public List<StockOut> list() {
        return stockOutService.list();
    }

    // 增
    @PostMapping("/save")
    public Result save(@RequestBody StockOut stockOut) {
        return stockOutService.save(stockOut)? Result.success() : Result.fail();
    }

    // 删
    @GetMapping("/delete")
    public Result delete(@RequestParam String id) {
        return stockOutService.removeById(id)? Result.success() : Result.fail();
    }

    // 改
    @PostMapping("/modify")
    public Result mod(@RequestBody StockOut stockOut) {
        return stockOutService.updateById(stockOut)? Result.success() : Result.fail();
    }

    // 新增或修改
    @PostMapping("saveOrModify")
    public boolean saveOrModify(@RequestBody StockOut stockOut) {
        return stockOutService.saveOrUpdate(stockOut);
    }

//    // 查 模糊查询
//    @PostMapping("/listP")
//    public Result listP(@RequestBody StockOut stockOut) {
//        LambdaQueryWrapper<StockOut> lambdaQueryWrapper = new LambdaQueryWrapper<>();
//        if(StringUtils.isNotBlank(stockOut.getName())) {
//            lambdaQueryWrapper.like(StockOut::getName, stockOut.getName());
//        }
//        return Result.success(stockOutService.list(lambdaQueryWrapper));
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
//        Page<StockOut> page = new Page<>();
//        page.setCurrent(query.getPageNum());
//        page.setSize(query.getPageSize());
//
//        LambdaQueryWrapper<StockOut> lambdaQueryWrapper = new LambdaQueryWrapper<>();
//        if(StringUtils.isNotBlank(name) && !name.equals("null")) {
//            lambdaQueryWrapper.like(StockOut::getName, name);
//        }
//        if(StringUtils.isNotBlank(sex)) {
//            lambdaQueryWrapper.eq(StockOut::getSex, sex);
//        }
//        if(StringUtils.isNotBlank(roleId)) {
//            lambdaQueryWrapper.eq(StockOut::getRoleId, roleId);
//        }
//        IPage res = stockOutService.page(page, lambdaQueryWrapper);
//
//        return Result.success(res.getTotal(), res.getRecords());
//    }
//
//    //按照id查询
//    @GetMapping("/findByNo")
//    public Result findByNo(@RequestParam String no) {
//        List<StockOut> list = stockOutService.lambdaQuery().eq(StockOut::getNo, no).list();
//        return list.size()>0 ? Result.success(list) : Result.fail();
//    }

}
