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

    // 查 - 查询所有客户
    @GetMapping("/list")
    public List<Customer> list() {
        return customerService.list();
    }

    // 增 - 添加客户
    @PostMapping("/save")
    public Result save(@RequestBody Customer customer) {
        return customerService.save(customer) ? Result.success() : Result.fail();
    }

    // 删 - 根据ID删除客户
    @GetMapping("/delete")
    public Result delete(@RequestParam String id) {
        return customerService.removeById(id) ? Result.success() : Result.fail();
    }

    // 改 - 根据ID修改客户信息
    @PostMapping("/modify")
    public Result mod(@RequestBody Customer customer) {
        return customerService.updateById(customer) ? Result.success() : Result.fail();
    }

    // 查 - 模糊查询客户
    @PostMapping("/listC")
    public Result listC(@RequestBody Customer customer) {
        LambdaQueryWrapper<Customer> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        if (StringUtils.isNotBlank(customer.getCustomerName())) {
            lambdaQueryWrapper.like(Customer::getCustomerName, customer.getCustomerName());
        }
        if (StringUtils.isNotBlank(customer.getContactPhone())) {
            lambdaQueryWrapper.like(Customer::getContactPhone, customer.getContactPhone());
        }
        if (StringUtils.isNotBlank(customer.getContactAddress())) {
            lambdaQueryWrapper.like(Customer::getContactAddress, customer.getContactAddress());
        }
        // 可以根据需要添加其他字段的模糊查询条件
        return Result.success(customerService.list(lambdaQueryWrapper));
    }

    // 分页查询客户
    @PostMapping("/listPage")
    public Result listPage(@RequestBody QueryPageParam query) {
        HashMap param = query.getParam();
        String customerName = (String) param.get("customerName");
        String contactPhone = (String) param.get("contactPhone");
        String contactAddress = (String) param.get("contactAddress"); // 可以根据需要添加

        Page<Customer> page = new Page<>();
        page.setCurrent(query.getPageNum());
        page.setSize(query.getPageSize());

        LambdaQueryWrapper<Customer> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        if (StringUtils.isNotBlank(customerName) && !customerName.equals("null")) {
            lambdaQueryWrapper.like(Customer::getCustomerName, customerName);
        }
        if (StringUtils.isNotBlank(contactPhone) && !contactPhone.equals("null")) {
            lambdaQueryWrapper.like(Customer::getContactPhone, contactPhone);
        }
        if (StringUtils.isNotBlank(contactAddress) && !contactAddress.equals("null")) {
            lambdaQueryWrapper.like(Customer::getContactAddress, contactAddress);
        }
        IPage<Customer> res = customerService.page(page, lambdaQueryWrapper);

        return Result.success(res.getTotal(), res.getRecords());
    }

    // 按照ID查询客户
    @GetMapping("/findByNo")
    public Result findByNo(@RequestParam String no) {
        LambdaQueryWrapper<Customer> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(Customer::getCustomerId, no);
        List<Customer> list = customerService.list(lambdaQueryWrapper);
        return list.size() > 0 ? Result.success(list) : Result.fail();
    }
}