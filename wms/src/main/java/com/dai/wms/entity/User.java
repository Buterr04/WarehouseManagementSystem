package com.dai.wms.entity;

import java.io.Serial;
import java.io.Serializable;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
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
 * @since 2025-04-13
 */
@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="User对象", description="")
public class User implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    @ApiModelProperty(value = "user id")
    private String id;

    @ApiModelProperty(value = "user number")
    private String no;

    @ApiModelProperty(value = "name")
    private String name;

    @ApiModelProperty(value = "password")
    private String password;

    @ApiModelProperty(value = "age")
    private Integer age;

    @ApiModelProperty(value = "sex, 0-female, 1-male")
    private Integer sex;

    @ApiModelProperty(value = "phone number")
    private String phone;

    @ApiModelProperty(value = "role, 0-administrator, 1-seller, 2-stockkeeper, 3-purchaser")
    private Integer roleId;


}
