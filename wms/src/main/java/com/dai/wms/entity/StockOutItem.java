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
 * @since 2025-04-22
 */
@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="StockOutItem对象", description="")
public class StockOutItem implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "stock_out_item_id", type = IdType.AUTO)
    private Integer stockOutItemId;

    private Integer stockOutId;

    private String productId;

    private Integer quantity;


}
