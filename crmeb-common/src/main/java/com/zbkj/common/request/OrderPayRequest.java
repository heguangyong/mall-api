package com.zbkj.common.request;

import com.zbkj.common.annotation.StringContains;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;

/**
 * 支付订单参数
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
@ApiModel(value = "OrderPayRequest对象", description = "订单支付")
public class OrderPayRequest {

    @ApiModelProperty(value = "订单编号", required = true)
    @NotBlank(message = "订单编号不能为空")
    private String orderNo;

    @ApiModelProperty(value = "支付方式:weixin,alipay,yue", required = true)
    @NotBlank(message = "支付方式不能为空")
    @StringContains(limitValues = {"weixin","alipay","yue"}, message = "未知的支付方式")
    private String payType;

    @ApiModelProperty(value = "支付渠道：public-公众号,mini-小程序，h5-网页支付,yue-余额，wechatIos-微信Ios，wechatAndroid-微信Android,alipay-支付宝，alipayApp-支付宝App, video=视频号, native=微信native支付， alipayPc=支付宝PC支付", required = true)
    @NotBlank(message = "支付渠道不能为空")
    @StringContains(limitValues = {"public","mini","h5","yue","wechatIos","wechatAndroid","alipay","alipayApp","video","native","alipayPc"}, message = "未知的支付渠道")
    private String payChannel;

    @ApiModelProperty(value = "支付平台")
    private String from;

    @ApiModelProperty(value = "下单时小程序的场景值")
    private Integer scene;
}
