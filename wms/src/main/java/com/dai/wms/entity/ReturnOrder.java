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
 * @since 2025-04-22
 */
@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="ReturnOrder对象", description="")
public class ReturnOrder implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "return_id", type = IdType.AUTO)
    private Integer returnId;

    private LocalDate returnDate;

    private Integer supplierId;


}
