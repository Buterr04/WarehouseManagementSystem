package com.dai.wms.mapper;

import com.dai.wms.entity.StockIn;
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
 * @since 2025-04-14
 */
@Mapper
public interface StockInMapper extends BaseMapper<StockIn> {

    @Select("SELECT stock_in_id, order_id, employee_id, stock_in_date, status FROM stock_in")
    List<StockIn> selectStockInList();

    @Select("SELECT stock_in_id, order_id, employee_id, stock_in_date, status FROM stock_in WHERE stock_in_id = #{stockInId}")
    StockIn selectStockInById(Integer stockInId);

    //   新增方法：根据 ID 查询入库单详情，包含关联的采购订单信息
    @Select("SELECT si.stock_in_id, si.order_id, po.order_date AS purchase_order_date, si.employee_id, si.stock_in_date, si.status " +
            "FROM stock_in si " +
            "JOIN purchase_order po ON si.order_id = po.order_id " +
            "WHERE si.stock_in_id = #{stockInId}")
    StockIn selectStockInWithDetails(Integer stockInId);

    @Update("UPDATE stock_in SET status = #{status} WHERE stock_in_id = #{stockInId}")
    void updateStatusById(@Param("stockInId") Integer stockInId, @Param("status") String status);

}
