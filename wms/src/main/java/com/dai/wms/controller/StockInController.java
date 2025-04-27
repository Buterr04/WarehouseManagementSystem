package com.dai.wms.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dai.wms.common.QueryPageParam;
import com.dai.wms.common.Result;
import com.dai.wms.entity.StockIn;
import com.dai.wms.entity.StockIn;
import com.dai.wms.service.StockInService;
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
 * @since 2025-04-14
 */
@RestController
@RequestMapping("/stock-in")
public class StockInController {

    @Autowired
    private StockInService stockInService;

    @GetMapping("/list")
    public List<StockIn> list() {
        return stockInService.list();
    }

    // 增
    @PostMapping("/save")
    public Result save(@RequestBody StockIn stockIn) {
        return stockInService.save(stockIn)? Result.success() : Result.fail();
    }

    // 删
    @GetMapping("/delete")
    public Result delete(@RequestParam String id) {
        return stockInService.removeById(id)? Result.success() : Result.fail();
    }

    // 改
    @PostMapping("/modify")
    public Result mod(@RequestBody StockIn stockIn) {
        return stockInService.updateById(stockIn)? Result.success() : Result.fail();
    }

    // 新增或修改
    @PostMapping("saveOrModify")
    public boolean saveOrModify(@RequestBody StockIn stockIn) {
        return stockInService.saveOrUpdate(stockIn);
    }

//    // 查 模糊查询
//    @PostMapping("/listP")
//    public Result listP(@RequestBody StockIn stockIn) {
//        LambdaQueryWrapper<StockIn> lambdaQueryWrapper = new LambdaQueryWrapper<>();
//        if(StringUtils.isNotBlank(stockIn.getName())) {
//            lambdaQueryWrapper.like(StockIn::getName, stockIn.getName());
//        }
//        return Result.success(stockInService.list(lambdaQueryWrapper));
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
//        Page<StockIn> page = new Page<>();
//        page.setCurrent(query.getPageNum());
//        page.setSize(query.getPageSize());
//
//        LambdaQueryWrapper<StockIn> lambdaQueryWrapper = new LambdaQueryWrapper<>();
//        if(StringUtils.isNotBlank(name) && !name.equals("null")) {
//            lambdaQueryWrapper.like(StockIn::getName, name);
//        }
//        if(StringUtils.isNotBlank(sex)) {
//            lambdaQueryWrapper.eq(StockIn::getSex, sex);
//        }
//        if(StringUtils.isNotBlank(roleId)) {
//            lambdaQueryWrapper.eq(StockIn::getRoleId, roleId);
//        }
//        IPage res = stockInService.page(page, lambdaQueryWrapper);
//
//        return Result.success(res.getTotal(), res.getRecords());
//    }
//
//    //按照id查询
//    @GetMapping("/findByNo")
//    public Result findByNo(@RequestParam String no) {
//        List<StockIn> list = stockInService.lambdaQuery().eq(StockIn::getNo, no).list();
//        return list.size()>0 ? Result.success(list) : Result.fail();
//    }

}
