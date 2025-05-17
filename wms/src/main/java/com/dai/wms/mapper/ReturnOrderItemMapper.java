package com.dai.wms.mapper;

import com.dai.wms.entity.ReturnOrderItem;
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
 * @since 2025-04-22
 */
@Mapper
public interface ReturnOrderItemMapper extends BaseMapper<ReturnOrderItem> {

    @Select("SELECT return_item_id, return_id, product_id, quantity, return_price " +
            "FROM return_order_item " +
            "WHERE return_id = #{returnId}")
    List<ReturnOrderItem> selectReturnOrderItemsByReturnId(@Param("returnId") Integer returnId);

    @Select("SELECT roi.return_item_id, roi.return_id, roi.product_id, roi.quantity, roi.return_price, " +
            "roi.product_name, roi.specification " +
            "FROM return_order_item roi " +
            "WHERE roi.return_id = #{returnId}")
    List<ReturnOrderItem> selectReturnOrderItemsWithProductInfo(@Param("returnId") Integer returnId);
}
