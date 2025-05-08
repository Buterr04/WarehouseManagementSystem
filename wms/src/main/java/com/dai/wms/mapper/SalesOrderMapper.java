package com.dai.wms.mapper;

import com.dai.wms.entity.SalesOrder;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

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
    @Select("SELECT so.sales_order_id, so.customer_id, c.customer_name, so.sale_date " +
            "FROM sales_order so " +
            "JOIN customer c ON so.customer_id = c.customer_id")
    List<SalesOrder> selectSalesOrderListWithCustomerName();
}
