package com.zbkj.common.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * 积分区间响应对象
 *
 * @author Han
 * @version 1.0.0
 * @Date 2024/8/20
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "IntegralIntervalResponse", description = "积分区间响应对象")
public class IntegralIntervalResponse implements Serializable {

    private static final long serialVersionUID = 1533043427058173836L;

    @ApiModelProperty(value = "id")
    private Integer id;

    @ApiModelProperty(value = "名称")
    private String name;

    @ApiModelProperty(value = "值")
    private String value;

}
