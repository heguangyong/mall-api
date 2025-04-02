package com.zbkj.common.response;

import com.zbkj.common.model.coupon.CouponUser;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * 计算订单价商户部分响应对象
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
@ApiModel(value = "ComputedMerchantOrderResponse对象", description = "计算订单价商户部分响应对象")
public class ComputedMerchantOrderResponse implements Serializable {

    private static final long serialVersionUID = 7282892323898493847L;

    @ApiModelProperty(value = "商户id")
    private Integer merId;

    @ApiModelProperty(value = "优惠券编号（不选时为0）")
    private Integer userCouponId;

    @ApiModelProperty(value = "优惠券优惠金额")
    private BigDecimal couponFee;

    @ApiModelProperty(value = "运费金额")
    private BigDecimal freightFee;

    @ApiModelProperty(value = "订单商户优惠券列表")
    private List<CouponUser> merCouponUserList;

    @ApiModelProperty(value = "svip优惠金额")
    private BigDecimal svipDiscountPrice = BigDecimal.ZERO;
}
