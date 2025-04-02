package com.zbkj.common.request;

import com.zbkj.common.annotation.StringContains;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * 微信支付结果查询请求对象
 *
 * @author Han
 * @version 1.0.0
 * @Date 2024/5/25
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "WechatPaymentQueryRequest", description = "微信支付结果查询请求对象")
public class WechatPaymentQueryRequest implements Serializable {

    private static final long serialVersionUID = 5243709857679894593L;

    @ApiModelProperty(value = "订单号")
    @NotBlank(message = "订单号不能为空")
    private String orderNo;

    @ApiModelProperty(value = "订单类型:order-商品订单,recharge-充值订单,svip-付费会员订单")
    @NotBlank(message = "订单类型不能为空")
    @StringContains(limitValues = {"order", "recharge", "svip"}, message = "未知的订单类型")
    private String orderType;
}
