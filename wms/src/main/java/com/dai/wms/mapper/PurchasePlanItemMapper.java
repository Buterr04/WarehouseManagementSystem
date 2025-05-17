package com.dai.wms.mapper;

import com.dai.wms.entity.PurchasePlanItem;
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
public interface PurchasePlanItemMapper extends BaseMapper<PurchasePlanItem> {

    /**
     * 查询指定计划ID下的所有明细项（字段简版）
     */
    @Select("SELECT plan_item_id, plan_id, product_id, plan_quantity " +
            "FROM purchase_plan_item " +
            "WHERE plan_id = #{planId}")
    List<PurchasePlanItem> selectPurchasePlanItemsByPlanId(@Param("planId") Integer planId);

    /**
     * 查询指定计划ID下的所有明细 + 产品信息
     */
    @Select("SELECT ppi.plan_item_id, ppi.plan_id, ppi.product_id, ppi.plan_quantity, " +
            "p.product_name, p.specifications, p.stock_quantity " +
            "FROM purchase_plan_item ppi " +
            "JOIN product p ON ppi.product_id = p.product_id " +
            "WHERE ppi.plan_id = #{planId}")
    List<PurchasePlanItem> selectPurchasePlanItemsWithProductInfo(@Param("planId") Integer planId);

    /**
     * 查询某个计划下所有明细（完整字段）
     */
    @Select("SELECT * FROM purchase_plan_item WHERE plan_id = #{planId}")
    List<PurchasePlanItem> findByPlanId(@Param("planId") Long planId);

    /**
     * 批量插入明细项
     */
    @Insert({
            "<script>",
            "INSERT INTO purchase_plan_item (plan_id, product_id, quantity, remark) VALUES ",
            "<foreach collection='items' item='item' separator=','>",
            "(#{item.planId}, #{item.productId}, #{item.quantity}, #{item.remark})",
            "</foreach>",
            "</script>"
    })
    void insertBatch(@Param("items") List<PurchasePlanItem> items);

    /**
     * 删除某个计划下所有明细项
     */
    @Delete("DELETE FROM purchase_plan_item WHERE plan_id = #{planId}")
    void deleteByPlanId(@Param("planId") Long planId);

}
