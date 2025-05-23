package com.dai.wms.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import java.time.LocalDate;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import java.util.List;

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
@ApiModel(value="DeliveryOrder对象", description="")
public class DeliveryOrder implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "delivery_id", type = IdType.AUTO)
    private Integer deliveryId;

    private Integer orderId;

    private LocalDate deliveryDate;

    private Integer employeeId;

    private Integer status;

    @TableField(exist = false)
    private List<DeliveryOrderItem> deliveryOrderItems; // 关联的交货单项列表


}
