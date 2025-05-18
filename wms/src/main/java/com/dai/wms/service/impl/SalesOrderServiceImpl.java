package com.dai.wms.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dai.wms.entity.SalesOrder;
import com.dai.wms.entity.SalesOrderItem;
import com.dai.wms.mapper.SalesOrderMapper;
import com.dai.wms.mapper.SalesOrderItemMapper;
import com.dai.wms.service.SalesOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class SalesOrderServiceImpl extends ServiceImpl<SalesOrderMapper, SalesOrder> implements SalesOrderService {

    @Autowired
    private SalesOrderMapper salesOrderMapper;
    @Autowired
    private SalesOrderItemMapper salesOrderItemMapper;

    @Override
    public List<SalesOrder> list() {
        return baseMapper.selectList(null);
    }

    @Override
    public List<SalesOrder> getSalesOrderListWithCustomerName() {
        return salesOrderMapper.selectSalesOrderListWithCustomerName();
    }

    @Override
    public SalesOrder findByIdWithCustomer(Integer salesOrderId) {
        return salesOrderMapper.selectSalesOrderByIdWithCustomer(salesOrderId);
    }


    @Transactional
    @Override
    public boolean saveSalesOrderWithItems(SalesOrder salesOrder) {
        boolean salesOrderSaved = this.save(salesOrder);

        if (salesOrderSaved) {
            Integer salesOrderId = salesOrder.getSalesOrderId();
            List<SalesOrderItem> salesOrderItems = salesOrder.getSalesOrderItems();
            if (salesOrderItems != null && !salesOrderItems.isEmpty()) {
                for (SalesOrderItem item : salesOrderItems) {
                    item.setSalesOrderId(salesOrderId);
                    // 直接使用前端发送的 salePrice
                    salesOrderItemMapper.insert(item);
                }
            }
            return true;
        }
        return false;
    }

    @Transactional
    @Override
    public boolean updateSalesOrderWithItems(SalesOrder salesOrder) {
        boolean salesOrderUpdated = this.updateById(salesOrder);

        if (salesOrderUpdated) {
            Integer salesOrderId = salesOrder.getSalesOrderId();
            LambdaQueryWrapper<SalesOrderItem> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(SalesOrderItem::getSalesOrderId, salesOrderId);
            salesOrderItemMapper.delete(queryWrapper);

            List<SalesOrderItem> salesOrderItems = salesOrder.getSalesOrderItems();
            if (salesOrderItems != null && !salesOrderItems.isEmpty()) {
                for (SalesOrderItem item : salesOrderItems) {
                    item.setSalesOrderId(salesOrderId);
                    salesOrderItemMapper.insert(item);
                }
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean updateStatusById(Integer salesOrderId, String status) {
        salesOrderMapper.updateStatusById(salesOrderId, status);
        return true;
    }


}