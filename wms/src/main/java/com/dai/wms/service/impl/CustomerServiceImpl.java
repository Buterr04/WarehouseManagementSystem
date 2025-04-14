package com.dai.wms.service.impl;

import com.dai.wms.entity.Customer;
import com.dai.wms.mapper.CustomerMapper;
import com.dai.wms.service.CustomerService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author dai
 * @since 2025-04-13
 */
@Service
public class CustomerServiceImpl extends ServiceImpl<CustomerMapper, Customer> implements CustomerService {

}
