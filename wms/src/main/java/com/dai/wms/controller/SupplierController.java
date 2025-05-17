package com.dai.wms.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dai.wms.common.QueryPageParam;
import com.dai.wms.common.Result;
import com.dai.wms.entity.Supplier;
import com.dai.wms.service.SupplierService;
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
 * @since 2025-05-17
 */
@RestController
@RequestMapping("/supplier")
public class SupplierController {

    @Autowired
    private SupplierService supplierService;

    // 查
    @GetMapping("/list")
    public List<Supplier> list() {
        return supplierService.list();
    }

    // 增
    @PostMapping("/save")
    public Result save(@RequestBody Supplier supplier) {
        return supplierService.save(supplier)? Result.success() : Result.fail();
    }

    // 删
    @GetMapping("/delete")
    public Result delete(@RequestParam String id) {
        return supplierService.removeById(id)? Result.success() : Result.fail();
    }

    // 改
    @PostMapping("/modify")
    public Result mod(@RequestBody Supplier supplier) {
        return supplierService.updateById(supplier)? Result.success() : Result.fail();
    }

    // 查 模糊查询
    @PostMapping("/listP")
    public Result listP(@RequestBody Supplier supplier) {
        LambdaQueryWrapper<Supplier> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        if(StringUtils.isNotBlank(supplier.getSupplierName())) {
            lambdaQueryWrapper.like(Supplier::getSupplierName, supplier.getSupplierName());
        }
        // 可以根据需要添加其他字段的模糊查询条件
        return Result.success(supplierService.list(lambdaQueryWrapper));
    }

    // 分页查询
    @PostMapping("/listPage")
    public Result listPage(@RequestBody QueryPageParam query) {
        HashMap param = query.getParam();
        String supplierName = (String) param.get("supplierName");
        String specifications = (String) param.get("specifications");
        String unitPrice = (String) param.get("unitPrice"); // 可以根据需要添加

        Page<Supplier> page = new Page<>();
        page.setCurrent(query.getPageNum());
        page.setSize(query.getPageSize());

        LambdaQueryWrapper<Supplier> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        if(StringUtils.isNotBlank(supplierName) && !supplierName.equals("null")) {
            lambdaQueryWrapper.like(Supplier::getSupplierName, supplierName);
        }
        IPage<Supplier> res = supplierService.page(page, lambdaQueryWrapper);

        return Result.success(res.getTotal(), res.getRecords());
    }

    //按照id查询
    @GetMapping("/findByNo")
    public Result findByNo(@RequestParam String no) {
        LambdaQueryWrapper<Supplier> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(Supplier::getSupplierId, no);
        List<Supplier> list = supplierService.list(lambdaQueryWrapper);
        return list.size() > 0 ? Result.success(list) : Result.fail();
    }
}
