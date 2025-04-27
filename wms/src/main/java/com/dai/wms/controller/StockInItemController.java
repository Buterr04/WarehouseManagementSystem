package com.dai.wms.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dai.wms.common.QueryPageParam;
import com.dai.wms.common.Result;
import com.dai.wms.entity.StockInItem;
import com.dai.wms.entity.StockInItem;
import com.dai.wms.service.StockInItemService;
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
@RequestMapping("/stock-in-item")
public class StockInItemController {

    @Autowired
    private StockInItemService stockInItemService;

    @GetMapping("/list")
    public List<StockInItem> list() {
        return stockInItemService.list();
    }

    // 增
    @PostMapping("/save")
    public Result save(@RequestBody StockInItem stockInItem) {
        return stockInItemService.save(stockInItem)? Result.success() : Result.fail();
    }

    // 删
    @GetMapping("/delete")
    public Result delete(@RequestParam String id) {
        return stockInItemService.removeById(id)? Result.success() : Result.fail();
    }

    // 改
    @PostMapping("/modify")
    public Result mod(@RequestBody StockInItem stockInItem) {
        return stockInItemService.updateById(stockInItem)? Result.success() : Result.fail();
    }

    // 新增或修改
    @PostMapping("saveOrModify")
    public boolean saveOrModify(@RequestBody StockInItem stockInItem) {
        return stockInItemService.saveOrUpdate(stockInItem);
    }

//    // 查 模糊查询
//    @PostMapping("/listP")
//    public Result listP(@RequestBody StockInItem stockInItem) {
//        LambdaQueryWrapper<StockInItem> lambdaQueryWrapper = new LambdaQueryWrapper<>();
//        if(StringUtils.isNotBlank(stockInItem.getName())) {
//            lambdaQueryWrapper.like(StockInItem::getName, stockInItem.getName());
//        }
//        return Result.success(stockInItemService.list(lambdaQueryWrapper));
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
//        Page<StockInItem> page = new Page<>();
//        page.setCurrent(query.getPageNum());
//        page.setSize(query.getPageSize());
//
//        LambdaQueryWrapper<StockInItem> lambdaQueryWrapper = new LambdaQueryWrapper<>();
//        if(StringUtils.isNotBlank(name) && !name.equals("null")) {
//            lambdaQueryWrapper.like(StockInItem::getName, name);
//        }
//        if(StringUtils.isNotBlank(sex)) {
//            lambdaQueryWrapper.eq(StockInItem::getSex, sex);
//        }
//        if(StringUtils.isNotBlank(roleId)) {
//            lambdaQueryWrapper.eq(StockInItem::getRoleId, roleId);
//        }
//        IPage res = stockInItemService.page(page, lambdaQueryWrapper);
//
//        return Result.success(res.getTotal(), res.getRecords());
//    }
//
//    //按照id查询
//    @GetMapping("/findByNo")
//    public Result findByNo(@RequestParam String no) {
//        List<StockInItem> list = stockInItemService.lambdaQuery().eq(StockInItem::getNo, no).list();
//        return list.size()>0 ? Result.success(list) : Result.fail();
//    }
}
