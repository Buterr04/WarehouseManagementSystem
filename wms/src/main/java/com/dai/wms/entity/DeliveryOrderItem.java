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
 * @since 2025-05-22
 */
@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="DeliveryOrderItem对象", description="")
public class DeliveryOrderItem implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "delivery_item_id", type = IdType.AUTO)
    private Integer deliveryItemId;

    private Integer deliveryId;

    private Integer productId;

    private Integer quantity;

    private Integer shippedQuantity;

    private String productName; // 非数据库字段，用于前端展示

    private String specifications; // 非数据库字段，用于前端展示


}
