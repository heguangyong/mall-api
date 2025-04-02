package com.zbkj.common.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * 积分区间分页搜索对象
 *
 * @author Han
 * @version 1.0.0
 * @Date 2024/7/13
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "IntegralIntervalPageSearchRequest", description = "积分区间分页列表搜索对象")
public class IntegralIntervalPageSearchRequest extends PageParamRequest implements Serializable {

    private static final long serialVersionUID = -4572464885363357010L;

    @ApiModelProperty(value = "区间名称")
    private String name;

    @ApiModelProperty(value = "状态（0：未上架，1：上架）")
    private Boolean status;

    @ApiModelProperty(value = "创建时间区间")
    private String dateLimit;
}
