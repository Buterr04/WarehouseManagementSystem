package com.dai.wms.service;

import com.dai.wms.entity.SalesOrderItem;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author dai
 * @since 2025-04-22
 */
public interface SalesOrderItemService extends IService<SalesOrderItem> {

    List<SalesOrderItem> getSalesOrderItemsWithProductName(Integer salesOrderId); // 保留之前的
    List<SalesOrderItem> getSalesOrderItemsWithProductInfo(Integer salesOrderId); // 新增方法
}
