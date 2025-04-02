package com.zbkj.common.request;

import com.zbkj.common.annotation.StringContains;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 用户充值请求对象
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
@ApiModel(value = "UserRechargeRequest对象", description = "用户充值请求对象")
public class UserRechargeRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "充值金额,与充值套餐id二选一")
//    @DecimalMin(value = "0", message = "充值金额不能小于0")
    private BigDecimal price;

    @ApiModelProperty(value = "充值套餐id，与充值金额二选一")
    private Integer groupDataId;

    @ApiModelProperty(value = "支付方式:weixin,alipay", required = true)
    @NotBlank(message = "支付方式不能为空")
    @StringContains(limitValues = {"weixin", "alipay"}, message = "未知的支付方式")
    private String payType;

    @ApiModelProperty(value = "支付渠道：public-公众号,mini-小程序，h5-网页支付,wechatIos-微信Ios，wechatAndroid-微信Android,alipay-支付宝，alipayApp-支付宝App", required = true)
    @NotBlank(message = "支付渠道不能为空")
    @StringContains(limitValues = {"public", "mini", "h5", "wechatIos", "wechatAndroid", "alipay", "alipayApp"}, message = "未知的支付渠道")
    private String payChannel;
}
