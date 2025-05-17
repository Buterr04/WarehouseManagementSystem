package com.dai.wms.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 
 * </p>
 *
 * @author dai
 * @since 2025-04-14
 */
@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="StockInItem对象", description="")
public class StockInItem implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "stock_in_item_id", type = IdType.AUTO)
    private Integer stockInItemId;

    private Integer stockInId;

    private Integer productId;

    private Integer quantity;

    private Integer acceptedQuantity;

    private String productName; // 产品名称，非数据库字段，用于前端展示

    private String specifications; // 规格，非数据库字段，用于前端展示


}
