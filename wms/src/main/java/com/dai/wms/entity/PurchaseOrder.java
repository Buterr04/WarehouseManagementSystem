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
@ApiModel(value="PurchaseOrder对象", description="")
public class PurchaseOrder implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "order_id", type = IdType.AUTO)
    private Integer orderId;

    private Integer supplierId;

    private LocalDate orderDate;

    private LocalDate deliveryDate;

    private String supplierName; // 非数据库字段，表示供应商名称

    private Integer status;

    @TableField(exist = false)
    private List<PurchaseOrderItem> purchaseOrderItems;



}
