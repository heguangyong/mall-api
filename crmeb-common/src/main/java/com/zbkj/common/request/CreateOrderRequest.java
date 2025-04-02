package com.zbkj.common.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

/**
 * 创建订单请求对象
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
@ApiModel(value="CreateOrderRequest对象", description="创建订单请求对象")
public class CreateOrderRequest implements Serializable {

    private static final long serialVersionUID = -6133994384185333872L;

    @ApiModelProperty(value = "预下单订单号", required = true)
    @NotBlank(message = "预下单订单号不能为空")
    private String preOrderNo;

    @ApiModelProperty(value = "收货地址id")
    @NotNull(message = "请选择收货地址")
    private Integer addressId;

    @ApiModelProperty(value = "订单商户对象", required = true)
    @NotEmpty(message = "订单商户对象不能为空")
    @Valid
    private List<OrderMerchantRequest> orderMerchantRequestList;

    @ApiModelProperty(value = "是否使用积分", required = true)
    @NotNull(message = "是否使用积分不能为空")
    private Boolean isUseIntegral;

    @ApiModelProperty(value = "用户平台优惠券编号（不选时为0）")
    @NotNull(message = "用户平台优惠券编号不能为空")
    private Integer platUserCouponId;

    @ApiModelProperty(value = "系统表单ID")
    private Integer systemFormId;

    @ApiModelProperty(value = "系统表单内容")
    private String orderExtend;
}
