package com.dai.wms.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dai.wms.common.QueryPageParam;
import com.dai.wms.common.Result;
import com.dai.wms.entity.Product;
import com.dai.wms.mapper.ProductMapper;
import com.dai.wms.service.ProductService;
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
@RequestMapping("/product")
public class ProductController {

    @Autowired
    private ProductService productService;

    @Autowired
    private ProductMapper productMapper;

    // 查
    @GetMapping("/list")
    public List<Product> list() {
        return productMapper.findAllWithSupplier();
    }

    // 增
    @PostMapping("/save")
    public Result save(@RequestBody Product product) {
        return productService.save(product)? Result.success() : Result.fail();
    }

    // 删
    @GetMapping("/delete")
    public Result delete(@RequestParam String id) {
        return productService.removeById(id)? Result.success() : Result.fail();
    }

    // 改
    @PostMapping("/modify")
    public Result mod(@RequestBody Product product) {
        return productService.updateById(product)? Result.success() : Result.fail();
    }

    // 查 模糊查询
    @PostMapping("/listP")
    public Result listP(@RequestBody Product product) {
        LambdaQueryWrapper<Product> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        if(StringUtils.isNotBlank(product.getProductName())) {
            lambdaQueryWrapper.like(Product::getProductName, product.getProductName());
        }
        if(StringUtils.isNotBlank(product.getSpecifications())) {
            lambdaQueryWrapper.like(Product::getSpecifications, product.getSpecifications());
        }
        // 可以根据需要添加其他字段的模糊查询条件
        return Result.success(productService.list(lambdaQueryWrapper));
    }

    // 分页查询
    @PostMapping("/listPage")
    public Result listPage(@RequestBody QueryPageParam query) {
        HashMap param = query.getParam();
        String productName = (String) param.get("productName");
        String specifications = (String) param.get("specifications");
        String unitPrice = (String) param.get("unitPrice"); // 可以根据需要添加

        Page<Product> page = new Page<>();
        page.setCurrent(query.getPageNum());
        page.setSize(query.getPageSize());

        LambdaQueryWrapper<Product> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        if(StringUtils.isNotBlank(productName) && !productName.equals("null")) {
            lambdaQueryWrapper.like(Product::getProductName, productName);
        }
        if(StringUtils.isNotBlank(specifications) && !specifications.equals("null")) {
            lambdaQueryWrapper.like(Product::getSpecifications, specifications);
        }
        if(StringUtils.isNotBlank(unitPrice) && !unitPrice.equals("null")) {
            lambdaQueryWrapper.eq(Product::getUnitPrice, unitPrice);
        }
        IPage<Product> res = productService.page(page, lambdaQueryWrapper);

        return Result.success(res.getTotal(), res.getRecords());
    }

    //按照id查询
    @GetMapping("/findByNo")
    public Result findByNo(@RequestParam String no) {
        LambdaQueryWrapper<Product> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(Product::getProductId, no);
        List<Product> list = productService.list(lambdaQueryWrapper);
        return list.size() > 0 ? Result.success(list) : Result.fail();
    }


}
