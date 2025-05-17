package com.dai.wms.mapper;

import com.dai.wms.entity.StockInItem;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

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
public interface StockInItemMapper extends BaseMapper<StockInItem> {

    @Select("SELECT stock_in_item_id, stock_in_id, product_id, quantity, accepted_quantity FROM stock_in_item WHERE stock_in_id = #{stockInId}")
    List<StockInItem> selectStockInItemsByStockInId(@Param("stockInId") Integer stockInId);

    //   新增方法：根据入库单 ID 查询明细，并包含商品信息
    @Select("SELECT sii.stock_in_item_id, sii.stock_in_id, sii.product_id, p.product_name, p.specifications, sii.quantity, sii.accepted_quantity " +
            "FROM stock_in_item sii " +
            "JOIN product p ON sii.product_id = p.product_id " +
            "WHERE sii.stock_in_id = #{stockInId}")
    List<StockInItem> selectStockInItemsWithProductInfo(@Param("stockInId") Integer stockInId);
}
