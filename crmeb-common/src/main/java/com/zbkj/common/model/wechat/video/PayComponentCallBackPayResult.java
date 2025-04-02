package com.zbkj.common.model.wechat.video;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 自定义交易组件 支付结果回调
 * @Auther: 大粽子
 * @Date: 2022/9/30 10:43
 * @Description: 描述对应的业务场景
 */
@Data
public class PayComponentCallBackPayResult {

    @ApiModelProperty(value = "内部订单号")
    private Long order_id;

    @ApiModelProperty(value = "商家自定义订单号")
    private String out_order_id;

    @ApiModelProperty(value = "支付单号")
    private String transaction_id;

    @ApiModelProperty(value = "支付时间")
    private String pay_time;

    @ApiModelProperty(value = "amount")
    private Integer amount;

    @ApiModelProperty(value = "用户实付金额")
    private Integer payer_amount;

    @ApiModelProperty(value = "用户在一级商户 appid 下的唯一标识")
    private String sp_openid;
}
