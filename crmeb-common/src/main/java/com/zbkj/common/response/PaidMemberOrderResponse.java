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
 * 付费会员订单响应对象
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
@ApiModel(value="PaidMemberOrderResponse", description="付费会员订单响应对象")
public class PaidMemberOrderResponse implements Serializable {

    private static final long serialVersionUID = 3558884699193209193L;

    @ApiModelProperty(value = "id")
    private Integer id;

    @ApiModelProperty(value = "用户UID")
    private Integer uid;

    @ApiModelProperty(value = "订单号")
    private String orderNo;

    @ApiModelProperty(value = "付费会员卡名称")
    private String cardName;

    @ApiModelProperty(value = "会员卡售价")
    private BigDecimal price;

    @ApiModelProperty(value = "支付方式:weixin,alipay,give")
    private String payType;

    @ApiModelProperty(value = "是否支付")
    private Boolean paid;

    @ApiModelProperty(value = "支付时间")
    private Date payTime;

    @ApiModelProperty(value = "会员卡到期时间")
    private Date cardExpirationTime;

    @ApiModelProperty(value = "0-试用，1-期限，2-永久")
    private Integer type;

    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    @ApiModelProperty(value = "用户昵称")
    private String userNickname;

    @ApiModelProperty(value = "用户手机号")
    private String userPhone;
}
