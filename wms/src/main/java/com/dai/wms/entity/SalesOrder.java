package com.dai.wms.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;
import lombok.Data;
import lombok.EqualsAndHashCode;
import io.swagger.annotations.ApiModel;

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
@ApiModel(value="SalesOrder对象", description="")
@TableName("sales_order") // 确保表名正确
public class SalesOrder implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "sales_order_id")
    private Integer salesOrderId;

    private Integer customerId;

    private LocalDate saleDate;

    private String customerName; // 客户名称，非数据库字段，用于前端展示

    private List<SalesOrderItem> salesOrderItems;



}