package com.zbkj.common.request.wxvedio.forregister;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;

/**
 * @Auther: 大粽子
 * @Date: 2022/9/26 11:20
 * @Description: 小程序交易组件完成接入状态 rtequest
 */
@Data
public class ShopRegisterFinishAccessInfoRequest {

    @ApiModelProperty(value ="6", example = "6:完成 spu 接口，7:完成订单接口 / 19:完成二级商户号订单，8:完成物流接口，9:完成售后接口 / 20:完成二级商户号售后，10:测试完成，11:发版完成\n" +
            "回包参数说明")
    @NotEmpty
    private Integer access_info_item;
}
