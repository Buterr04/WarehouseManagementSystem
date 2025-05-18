package com.dai.wms.mapper;

import com.dai.wms.entity.StockOut;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

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
public interface StockOutMapper extends BaseMapper<StockOut> {

    @Select("SELECT so.stock_out_id, so.sales_order_id, so.employee_id, so.stock_out_date, so.status, sa.sale_date AS SaleDate " +
            "FROM stock_out so " +
            "JOIN sales_order sa ON so.sales_order_id = sa.sales_order_id")
    List<StockOut> selectStockOutListWithSaleDate();

    @Select("SELECT so.stock_out_id, so.sales_order_id, so.employee_id, so.stock_out_date, so.status, sa.sale_date AS SaleDate " +
            "FROM stock_out so " +
            "JOIN sales_order sa ON so.sales_order_id = sa.sales_order_id " +
            "WHERE so.stock_out_id = #{stockOutId}")
    StockOut selectStockOutByIdWithSaleDate(Integer stockOutId);

    @Update("UPDATE stock_out SET status = #{status} WHERE stock_out_id = #{stockOutId}")
    void updateStatusById(@Param("stockOutId") Integer stockOutId, @Param("status") String status);

}