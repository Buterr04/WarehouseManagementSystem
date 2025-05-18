package com.dai.wms.mapper;

import com.dai.wms.entity.PurchasePlan;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dai.wms.entity.SalesOrder;
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
public interface PurchasePlanMapper extends BaseMapper<PurchasePlan> {

    // 查询所有计划（完整信息）
    @Select("SELECT * FROM purchase_plan ORDER BY id DESC")
    List<PurchasePlan> selectAll();

    // 查询计划列表（仅ID和时间）—— 用于选择下拉、简略展示等
    @Select("SELECT plan_id, purchase_date, stock_out_id, status FROM purchase_plan")
    List<PurchasePlan> selectPurchasePlanList();

    // 查询某个计划（仅ID和时间）
    @Select("SELECT plan_id, purchase_date FROM purchase_plan WHERE plan_id = #{planId}")
    PurchasePlan selectPurchasePlanById(@Param("planId") Integer planId);

    // 批量删除
    @Delete({
            "<script>",
            "DELETE FROM purchase_plan WHERE id IN ",
            "<foreach item='id' collection='ids' open='(' separator=',' close=')'>#{id}</foreach>",
            "</script>"
    })
    void deleteBatch(@Param("ids") List<Long> ids);

    @Update("UPDATE purchase_plan SET status = #{status} WHERE plan_id = #{planId}")
    void updateStatusById(@Param("planId") Integer planId, @Param("status") String status);

}
