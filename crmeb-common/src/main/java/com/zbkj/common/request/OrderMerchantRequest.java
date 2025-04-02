package com.zbkj.common.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotNull;

/**
 * 订单商户请求对象
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
@ApiModel(value = "OrderMerchantRequest", description = "订单商户请求对象")
public class OrderMerchantRequest {

    @ApiModelProperty(value = "商户id")
    @NotNull(message = "订单商户id不能为空")
    private Integer merId;

    @ApiModelProperty(value = "优惠券编号（不选时为0）")
    @NotNull(message = "订单商户优惠券编号不能为空")
    private Integer userCouponId;

    @ApiModelProperty(value = "快递类型: 1-快递配送，2-到店自提，3-虚拟发货", required = true)
    @NotNull(message = "快递类型不能为空")
    @Range(min = 1, max = 3, message = "未知的快递类型")
    private Integer shippingType;

    @ApiModelProperty(value = "备注")
    private String remark;
}
