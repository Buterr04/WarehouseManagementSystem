package com.dai.wms.controller;


import com.dai.wms.entity.Product;
import com.dai.wms.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

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

    // 查
    @GetMapping("/list")
    public List<Product> list() {
        return productService.list();
    }

    // 增
    @GetMapping("/save")
    public boolean save(Product product) {
        return productService.save(product);
    }

    // 删
    @GetMapping("/delete")
    public boolean delete(Long id) {
        return productService.removeById(id);
    }

    // 改
    @GetMapping("/modify")
    public boolean modify(Product product) {
        return productService.updateById(product);
    }

    // 新增或修改
    @GetMapping("saveOrModify")
    public boolean saveOrModify(Product product) {
        return productService.saveOrUpdate(product);
    }

    // 根据id查询
    @GetMapping("/findById")
    public Product findById(String id) {
        return productService.getById(id);
    }


}
