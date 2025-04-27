package com.dai.wms.entity;

import java.math.BigDecimal;
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
 * @since 2025-04-22
 */
@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="SalesOrderItem对象", description="")
public class SalesOrderItem implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "sales_item_id", type = IdType.AUTO)
    private Integer salesItemId;

    private Integer salesOrderId;

    private String productId;

    private Integer quantity;

    private BigDecimal salePrice;


}
