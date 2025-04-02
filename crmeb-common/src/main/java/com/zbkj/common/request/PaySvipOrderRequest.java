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
 * 购买svip订单请求对象
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
@ApiModel(value = "PaySvipOrderRequest", description = "购买svip订单请求对象")
public class PaySvipOrderRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "付费会员卡ID", required = true)
    private Integer cardId;

    @ApiModelProperty(value = "支付方式:weixin,alipay,yue", required = true)
    @NotBlank(message = "支付方式不能为空")
    @StringContains(limitValues = {"weixin","alipay","yue"}, message = "未知的支付方式")
    private String payType;

    @ApiModelProperty(value = "支付渠道：public-公众号,mini-小程序，h5-网页支付,yue-余额，wechatIos-微信Ios，wechatAndroid-微信Android,alipay-支付宝，alipayApp-支付宝App, native=微信native支付， alipayPc=支付宝PC支付", required = true)
    @NotBlank(message = "支付渠道不能为空")
    @StringContains(limitValues = {"public","mini","h5","yue","wechatIos","wechatAndroid","alipay","alipayApp","native","alipayPc"}, message = "未知的支付渠道")
    private String payChannel;
}
