package com.zbkj.common.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * 系统表单添加请求对象
 *
 * @author Han
 * @version 1.0.0
 * @Date 2024/8/5
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "SystemFormAddRequest", description = "系统表单添加请求对象")
public class SystemFormAddRequest implements Serializable {

    private static final long serialVersionUID = -1124229577224074555L;

    @ApiModelProperty(value = "表单ID")
    private Integer id;

    @ApiModelProperty(value = "表单标题")
    @NotBlank(message = "标题不能为空")
    private String formName;

    @ApiModelProperty(value = "表单所有的key")
    private String allKeys;

    @ApiModelProperty(value = "表单内容")
    private String formValue;

}
