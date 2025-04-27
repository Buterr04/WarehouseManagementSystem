package com.dai.wms.service.impl;

import com.dai.wms.entity.Product;
import com.dai.wms.mapper.ProductMapper;
import com.dai.wms.service.ProductService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author dai
 * @since 2025-04-14
 */
@Service
public class ProductServiceImpl extends ServiceImpl<ProductMapper, Product> implements ProductService {

}
