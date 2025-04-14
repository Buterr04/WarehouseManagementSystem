package com.dai.wms.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dai.wms.common.QueryPageParam;
import com.dai.wms.common.Result;
import com.dai.wms.entity.Customer;
import com.dai.wms.service.CustomerService;
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
 * @since 2025-04-13
 */
@RestController
@RequestMapping("/customer")
public class CustomerController {

    @Autowired
    private CustomerService customerService;

    // 查
    @GetMapping("/list")
    public List<Customer> list() {
        return customerService.list();
    }

    // 增
    @PostMapping("/save")
    public Result save(@RequestBody Customer customer) {
        return customerService.save(customer)? Result.success() : Result.fail();
    }

    // 删
    @GetMapping("/delete")
    public Result delete(Long id) {
        return customerService.removeById(id)? Result.success() : Result.fail();
    }

    // 改
    @PostMapping("/modify")
    public Result mod(@RequestBody Customer customer) {
        return customerService.updateById(customer)? Result.success() : Result.fail();
    }

    // 新增或修改
    @PostMapping("saveOrModify")
    public boolean saveOrModify(@RequestBody Customer customer) {
        return customerService.saveOrUpdate(customer);
    }

}
