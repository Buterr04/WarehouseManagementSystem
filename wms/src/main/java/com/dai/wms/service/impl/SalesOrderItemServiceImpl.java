package com.dai.wms.service.impl;

import com.dai.wms.entity.SalesOrderItem;
import com.dai.wms.mapper.SalesOrderItemMapper;
import com.dai.wms.service.SalesOrderItemService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author dai
 * @since 2025-04-22
 */
@Service
public class SalesOrderItemServiceImpl extends ServiceImpl<SalesOrderItemMapper, SalesOrderItem> implements SalesOrderItemService {

    @Autowired
    private SalesOrderItemMapper salesOrderItemMapper;

    @Override
    public List<SalesOrderItem> getSalesOrderItemsWithProductName(Integer salesOrderId) {
        return salesOrderItemMapper.selectSalesOrderItemsWithProductName(salesOrderId);
    }

    @Override
    public List<SalesOrderItem> getSalesOrderItemsWithProductInfo(Integer salesOrderId) {
        return salesOrderItemMapper.selectSalesOrderItemsWithProductInfo(salesOrderId);
    }
}
