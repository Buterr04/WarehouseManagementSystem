package com.dai.wms.mapper;

import com.dai.wms.entity.SalesOrder;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author dai
 * @since 2025-04-22
 */
@Mapper
public interface SalesOrderMapper extends BaseMapper<SalesOrder> {
    @Select("SELECT so.sales_order_id AS salesOrderId, so.customer_id AS customerId, c.customer_name AS customerName, so.sale_date AS saleDate, so.status AS status " +
            "FROM sales_order so " +
            "JOIN customer c ON so.customer_id = c.customer_id")
    List<SalesOrder> selectSalesOrderListWithCustomerName();


    @Select("SELECT so.sales_order_id, so.customer_id, c.customer_name, so.sale_date, so.status " +
            "FROM sales_order so " +
            "JOIN customer c ON so.customer_id = c.customer_id " +
            "WHERE so.sales_order_id = #{salesOrderId}")
    SalesOrder selectSalesOrderByIdWithCustomer(Integer salesOrderId);

    @Update("UPDATE sales_order SET status = #{status} WHERE sales_order_id = #{salesOrderId}")
    void updateStatusById(@Param("salesOrderId") Integer salesOrderId, @Param("status") String status);



}
