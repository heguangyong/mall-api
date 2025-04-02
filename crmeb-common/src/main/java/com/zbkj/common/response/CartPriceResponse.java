package com.zbkj.common.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 购物车价格响应对象
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
@ApiModel(value = "CartPriceResponse对象", description = "购物车价格响应对象")
public class CartPriceResponse implements Serializable {

    private static final long serialVersionUID = -2647644862597830961L;

    @ApiModelProperty(value = "商品总价")
    private BigDecimal proTotalPrice;

    @ApiModelProperty(value = "店铺优惠金额")
    private BigDecimal merCouponPrice;

    @ApiModelProperty(value = "平台优惠金额")
    private BigDecimal platCouponPrice;

    @ApiModelProperty(value = "总计优惠金额")
    private BigDecimal totalCouponPrice;

    @ApiModelProperty(value = "svip优惠金额")
    private BigDecimal svipDiscountPrice;

    @ApiModelProperty(value = "总计金额")
    private BigDecimal totalPrice;
}
