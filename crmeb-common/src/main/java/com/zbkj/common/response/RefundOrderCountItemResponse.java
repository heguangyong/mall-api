package com.zbkj.common.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * 退款订单状态数量
 * +----------------------------------------------------------------------
 * | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
 * +----------------------------------------------------------------------
 * | Copyright (c) 2016~2025 https://www.crmeb.com All rights reserved.
 * +----------------------------------------------------------------------
 * | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
 * +----------------------------------------------------------------------
 * | Author: CRMEB Team <admin@crmeb.com>
 * +----------------------------------------------------------------------
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "RefundOrderCountItemResponse对象", description = "退款订单状态数量")
public class RefundOrderCountItemResponse implements Serializable {

    private static final long serialVersionUID = -8605913636959651047L;

    @ApiModelProperty(value = "总数")
    private Integer all;

    @ApiModelProperty(value = "待审核")
    private Integer await;

    @ApiModelProperty(value = "已拒绝")
    private Integer reject;

    @ApiModelProperty(value = "退款中")
    private Integer refunding;

    @ApiModelProperty(value = "已退款")
    private Integer refunded;

    @ApiModelProperty(value = "待退货")
    private Integer awaitReturning;

    @ApiModelProperty(value = "待收货")
    private Integer awaitReceiving;

    @ApiModelProperty(value = "已撤销")
    private Integer revoke;

}
