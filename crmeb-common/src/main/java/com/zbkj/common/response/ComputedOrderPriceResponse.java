package com.zbkj.common.response;

import com.zbkj.common.model.coupon.CouponUser;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * 计算订单价格响应对象
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
@ApiModel(value = "ComputedOrderPriceResponse对象", description = "计算订单价格响应对象")
public class ComputedOrderPriceResponse implements Serializable {

    private static final long serialVersionUID = 7282892323898493847L;

    @ApiModelProperty(value = "优惠券优惠金额")
    private BigDecimal couponFee;

    @ApiModelProperty(value = "积分抵扣金额")
    private BigDecimal deductionPrice;

    @ApiModelProperty(value = "运费金额")
    private BigDecimal freightFee;

    @ApiModelProperty(value = "实际支付金额")
    private BigDecimal payFee;

    @ApiModelProperty(value = "商品总金额")
    private BigDecimal proTotalFee;

    @ApiModelProperty(value = "剩余积分")
    private Integer surplusIntegral;

    @ApiModelProperty(value = "是否使用积分")
    private Boolean isUseIntegral;

    @ApiModelProperty(value = "使用的积分")
    private Integer usedIntegral;

    @ApiModelProperty(value = "积分抵扣开关")
    private Boolean integralDeductionSwitch;

    @ApiModelProperty(value = "商户部分响应对象")
    private List<ComputedMerchantOrderResponse> merOrderResponseList;

    @ApiModelProperty(value = "平台优惠券优惠金额")
    private BigDecimal platCouponFee;

    @ApiModelProperty(value = "商户优惠券优惠金额")
    private BigDecimal merCouponFee;

    @ApiModelProperty(value = "订单平台优惠券列表")
    private List<CouponUser> platCouponUserList;

    @ApiModelProperty(value = "svip优惠金额")
    private BigDecimal svipDiscountPrice = BigDecimal.ZERO;
}
