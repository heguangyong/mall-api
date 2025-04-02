package com.zbkj.common.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * 积分区间商品请求对象
 *
 * @author Han
 * @version 1.0.0
 * @Date 2024/8/20
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "IntegralIntervalProductRequest", description = "积分区间商品请求对象")
public class IntegralIntervalProductRequest extends PageParamRequest implements Serializable {

    private static final long serialVersionUID = 4317630952478910406L;

    @ApiModelProperty(value = "积分区间id，全部为0", required = true)
    private Integer intervalId = 0;
}
