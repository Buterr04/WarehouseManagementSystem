package com.dai.wms.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import java.time.LocalDate;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import java.util.List;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 
 * </p>
 *
 * @author dai
 * @since 2025-04-22
 */
@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="StockOut对象", description="")
public class StockOut implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "stock_out_id", type = IdType.AUTO)
    private Integer stockOutId;

    private Integer salesOrderId;

    private Integer employeeId;

    private LocalDate stockOutDate;

    private Integer status;

    @TableField(exist = false)
    private LocalDate SaleDate;

    @TableField(exist = false)
    private List<StockOutItem> stockOutItems;
}
