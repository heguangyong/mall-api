package com.zbkj.common.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 付费会员订单详情响应对象
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
@ApiModel(value="PaidMemberOrderInfoResponse", description="付费会员订单详情响应对象")
public class PaidMemberOrderInfoResponse implements Serializable {

    private static final long serialVersionUID = 3558884699193209193L;

    @ApiModelProperty(value = "id")
    private Integer id;

    @ApiModelProperty(value = "用户UID")
    private Integer uid;

    @ApiModelProperty(value = "订单号")
    private String orderNo;

    @ApiModelProperty(value = "付费会员卡ID")
    private Integer cardId;

    @ApiModelProperty(value = "付费会员卡名称")
    private String  cardName;

    @ApiModelProperty(value = "0-试用，1-期限，2-永久")
    private Integer type;

    @ApiModelProperty(value = "期限天数")
    private Integer deadlineDay;

    @ApiModelProperty(value = "会员卡售价")
    private BigDecimal price;

    @ApiModelProperty(value = "赠送余额")
    private BigDecimal giftBalance;

    @ApiModelProperty(value = "支付方式:weixin,alipay,give")
    private String payType;

    @ApiModelProperty(value = "支付渠道：public-公众号,mini-小程序，h5-网页支付,wechatIos-微信Ios，wechatAndroid-微信Android,alipay-支付宝，alipayApp-支付宝App,give-平台赠送")
    private String payChannel;

    @ApiModelProperty(value = "是否支付")
    private Boolean paid;

    @ApiModelProperty(value = "支付时间")
    private Date payTime;

    @ApiModelProperty(value = "会员卡到期时间")
    private Date cardExpirationTime;

    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    @ApiModelProperty(value = "用户昵称")
    private String userNickname;

    @ApiModelProperty(value = "用户手机号")
    private String userPhone;
}
