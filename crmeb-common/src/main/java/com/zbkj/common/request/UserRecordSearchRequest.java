package com.zbkj.common.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 用户记录查询请求对象
 *
 * @author Han
 * @version 1.0.0
 * @Date 2025/2/12
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="UserRecordSearchRequest", description="用户记录查询请求对象")
public class UserRecordSearchRequest extends PageParamRequest implements Serializable {
    private static final long serialVersionUID = -7221553355509474154L;

    @ApiModelProperty(value = "用户ID")
    @NotNull(message = "请选择用户")
    private Integer userId;
}
