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

    private String productId;

    private Integer quantity;


}
