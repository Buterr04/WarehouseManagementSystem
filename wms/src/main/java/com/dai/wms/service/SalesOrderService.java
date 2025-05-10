package com.dai.wms.service;

import com.dai.wms.entity.SalesOrder;
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
public interface SalesOrderService extends IService<SalesOrder> {
    boolean saveSalesOrderWithItems(SalesOrder salesOrder);
    boolean updateSalesOrderWithItems(SalesOrder salesOrder);
    List<SalesOrder> list();
    List<SalesOrder> getSalesOrderListWithCustomerName();
    SalesOrder findById(Integer salesOrderId);

}
