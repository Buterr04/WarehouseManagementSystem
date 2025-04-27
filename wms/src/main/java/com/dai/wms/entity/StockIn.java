package com.dai.wms.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import java.time.LocalDate;
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
@ApiModel(value="StockIn对象", description="")
public class StockIn implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "stock_in_id", type = IdType.AUTO)
    private Integer stockInId;

    private Integer orderId;

    private Integer employeeId;

    private LocalDate stockInDate;


}
