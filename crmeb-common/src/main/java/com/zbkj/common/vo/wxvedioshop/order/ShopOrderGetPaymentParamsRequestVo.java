package com.zbkj.common.vo.wxvedioshop.order;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 自定义交易组件 获取支付参数 Request
 * @Auther: 大粽子
 * @Date: 2022/9/28 17:21
 * @Description: 描述对应的业务场景
 */
@Data
public class ShopOrderGetPaymentParamsRequestVo {

    @ApiModelProperty(value = "微信侧订单id")
    @JsonProperty(value = "order_id")
    private String order_id;

    @ApiModelProperty(value = "商家自定义订单ID")
    @JsonProperty(value = "out_order_id")
    private String out_order_id;

    @ApiModelProperty(value = "用户的openid")
    private String openid;

    public ShopOrderGetPaymentParamsRequestVo(String order_id, String out_order_id, String openid) {
        this.order_id = order_id;
        this.out_order_id = out_order_id;
        this.openid = openid;
    }
}
