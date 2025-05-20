package com.dai.wms.controller;



import com.dai.wms.common.Result;
import com.dai.wms.entity.SalesOrderItem;
import com.dai.wms.service.SalesOrderItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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
@RequestMapping("/sales-order-item")
public class SalesOrderItemController {
    @Autowired
    private SalesOrderItemService salesOrderItemService;

    @GetMapping("/list")
    public List<SalesOrderItem> list() {
        return salesOrderItemService.list();
    }

    // 增
    @PostMapping("/save")
    public Result save(@RequestBody SalesOrderItem salesOrderItem) {
        return salesOrderItemService.save(salesOrderItem)? Result.success() : Result.fail();
    }

    // 删
    @GetMapping("/delete")
    public Result delete(@RequestParam String id) {
        return salesOrderItemService.removeById(id)? Result.success() : Result.fail();
    }

    // 改
    @PostMapping("/modify")
    public Result mod(@RequestBody SalesOrderItem salesOrderItem) {
        return salesOrderItemService.updateById(salesOrderItem)? Result.success() : Result.fail();
    }

    // 新增或修改
    @PostMapping("saveOrModify")
    public boolean saveOrModify(@RequestBody SalesOrderItem salesOrderItem) {
        return salesOrderItemService.saveOrUpdate(salesOrderItem);
    }

//    // 查 模糊查询
//    @PostMapping("/listP")
//    public Result listP(@RequestBody SalesOrderItem salesOrderItem) {
//        LambdaQueryWrapper<SalesOrderItem> lambdaQueryWrapper = new LambdaQueryWrapper<>();
//        if(StringUtils.isNotBlank(salesOrderItem.getName())) {
//            lambdaQueryWrapper.like(SalesOrderItem::getName, salesOrderItem.getName());
//        }
//        return Result.success(salesOrderItemService.list(lambdaQueryWrapper));
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
//        Page<SalesOrderItem> page = new Page<>();
//        page.setCurrent(query.getPageNum());
//        page.setSize(query.getPageSize());
//
//        LambdaQueryWrapper<SalesOrderItem> lambdaQueryWrapper = new LambdaQueryWrapper<>();
//        if(StringUtils.isNotBlank(name) && !name.equals("null")) {
//            lambdaQueryWrapper.like(SalesOrderItem::getName, name);
//        }
//        if(StringUtils.isNotBlank(sex)) {
//            lambdaQueryWrapper.eq(SalesOrderItem::getSex, sex);
//        }
//        if(StringUtils.isNotBlank(roleId)) {
//            lambdaQueryWrapper.eq(SalesOrderItem::getRoleId, roleId);
//        }
//        IPage res = salesOrderItemService.page(page, lambdaQueryWrapper);
//
//        return Result.success(res.getTotal(), res.getRecords());
//    }
//
//    //按照id查询
//    @GetMapping("/findByNo")
//    public Result findByNo(@RequestParam String no) {
//        List<SalesOrderItem> list = salesOrderItemService.lambdaQuery().eq(SalesOrderItem::getNo, no).list();
//        return list.size()>0 ? Result.success(list) : Result.fail();
//    }

}
