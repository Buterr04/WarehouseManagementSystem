package com.dai.wms.mapper;

import com.dai.wms.entity.PurchasePlanItem;
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
public interface PurchasePlanItemMapper extends BaseMapper<PurchasePlanItem> {

    @Select("SELECT plan_item_id, plan_id, product_id, plan_quantity " +
            "FROM purchase_plan_item " +
            "WHERE plan_id = #{planId}")
    List<PurchasePlanItem> selectPurchasePlanItemsByPlanId(@Param("planId") Integer planId);

    @Select("SELECT ppi.plan_item_id, ppi.plan_id, ppi.product_id, ppi.plan_quantity, " +
            "p.product_name, p.specifications, p.stock_quantity " + // 假设 product 表有 product_name 和 specifications 字段
            "FROM purchase_plan_item ppi " +
            "JOIN product p ON ppi.product_id = p.product_id " +
            "WHERE ppi.plan_id = #{planId}")
    List<PurchasePlanItem> selectPurchasePlanItemsWithProductInfo(@Param("planId") Integer planId);
}
