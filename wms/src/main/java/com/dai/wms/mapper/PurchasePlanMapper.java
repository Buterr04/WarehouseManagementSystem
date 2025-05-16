package com.dai.wms.mapper;

import com.dai.wms.entity.PurchasePlan;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dai.wms.entity.SalesOrder;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
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
public interface PurchasePlanMapper extends BaseMapper<PurchasePlan> {

    @Select("SELECT plan_id, purchase_date FROM purchase_plan")
    List<PurchasePlan> selectPurchasePlanList();

    @Select("SELECT plan_id, purchase_date FROM purchase_plan WHERE plan_id = #{planId}")
    PurchasePlan selectPurchasePlanById(Integer planId);

}
