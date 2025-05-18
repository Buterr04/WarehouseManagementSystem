package com.dai.wms.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
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
@ApiModel(value="PurchasePlanItem对象", description="")
public class PurchasePlanItem implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "plan_item_id", type = IdType.AUTO)
    private Integer planItemId;

    private Integer planId;

    private Integer productId;

    private Integer planQuantity ;

    private String productName; // 非数据库字段

    private String specifications; // 非数据库字段

    private Integer stockQuantity; // 非数据库字段

    private Integer supplierId; // 非数据库字段

}
