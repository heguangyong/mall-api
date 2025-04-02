package com.zbkj.common.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 个人中心订单数量响应对象
 *  +----------------------------------------------------------------------
 *  | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
 *  +----------------------------------------------------------------------
 *  | Copyright (c) 2016~2025 https://www.crmeb.com All rights reserved.
 *  +----------------------------------------------------------------------
 *  | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
 *  +----------------------------------------------------------------------
 *  | Author: CRMEB Team <admin@crmeb.com>
 *  +----------------------------------------------------------------------
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="OrderCenterNumResponse对象", description="个人中心订单数量响应对象")
public class OrderCenterNumResponse implements Serializable {

    private static final long serialVersionUID = 1387727608277207652L;

    @ApiModelProperty(value = "未支付订单数量")
    private Integer awaitPayCount;

    @ApiModelProperty(value = "待发货订单数量")
    private Integer awaitShippedCount;

    @ApiModelProperty(value = "待核销订单数量")
    private Integer verificationCount;

    @ApiModelProperty(value = "待收货订单数量")
    private Integer receiptCount;

    @ApiModelProperty(value = "待评价数量")
    private Integer awaitReplyCount;

    @ApiModelProperty(value = "退款中数量")
    private Integer refundCount;

}
