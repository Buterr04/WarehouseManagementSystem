package com.dai.wms.mapper;

import com.dai.wms.entity.ReturnOrder;
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
public interface ReturnOrderMapper extends BaseMapper<ReturnOrder> {

    @Select("SELECT return_id, return_date, supplier_id FROM return_order")
    List<ReturnOrder> selectReturnOrderList();

    @Select("SELECT return_id, return_date, supplier_id FROM return_order WHERE return_id = #{returnId}")
    ReturnOrder selectReturnOrderById(Integer returnId);
}
