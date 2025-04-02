package com.zbkj.common.response;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 订单号响应对象
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
@ApiModel(value="OrderNoResponse对象", description="订单号响应对象")
public class OrderNoResponse implements Serializable {

    private static final long serialVersionUID = 5527685476730836401L;

    @ApiModelProperty(value = "订单号（预下单时为预下单单号）")
    private String orderNo;

    @ApiModelProperty(value = "订单类型:0-普通订单，1-视频号订单,2-秒杀订单,5-云盘订单，6-卡密订单")
    private Integer orderType;

    @ApiModelProperty(value = "支付金额")
    private BigDecimal payPrice;

    @ApiModelProperty(value = "拼团 - 下单是否满员 0=未满员 1=满员")
    private Integer groupBuyIsFull = 0;
}
