package com.zbkj.common.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * 积分订单表头请求对象
 *
 * @author Han
 * @version 1.0.0
 * @Date 2024/8/27
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "IntegralOrderTabsHeaderRequest", description = "积分订单表头请求对象")
public class IntegralOrderTabsHeaderRequest extends UserCommonSearchRequest implements Serializable {

    private static final long serialVersionUID = -3553321070530955943L;

    @ApiModelProperty(value = "订单号")
    private String orderNo;

    @ApiModelProperty(value = "创建时间区间")
    private String dateLimit;

}
