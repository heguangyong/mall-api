package com.zbkj.common.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 退款单退回商品请求对象
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
@ApiModel(value = "OrderRefundReturningGoodsRequest对象", description = "退款单退回商品请求对象")
public class OrderRefundReturningGoodsRequest {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "退款订单号", required = true)
    @NotBlank(message = "退款订单号不能为空")
    private String refundOrderNo;

    @ApiModelProperty(value = "用户联系电话", required = true)
    @NotNull(message = "请填写用户联系电话")
    private String telephone;

    @ApiModelProperty(value = "物流公司")
    private String expressName;

    @ApiModelProperty(value = "物流单号")
    private String trackingNumber;
}
