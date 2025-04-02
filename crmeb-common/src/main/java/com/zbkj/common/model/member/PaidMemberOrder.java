package com.zbkj.common.model.member;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * <p>
 * 付费会员订单表
 * </p>
 *
 * @author hzw
 * @since 2024-05-10
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("eb_paid_member_order")
@ApiModel(value="PaidMemberOrder对象", description="付费会员订单表")
public class PaidMemberOrder implements Serializable {

    private static final long serialVersionUID=1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "用户UID")
    private Integer uid;

    @ApiModelProperty(value = "订单号")
    private String orderNo;

    @ApiModelProperty(value = "付费会员卡ID")
    private Integer cardId;

    @ApiModelProperty(value = "付费会员卡名称")
    private String cardName;

    @ApiModelProperty(value = "0-试用，1-期限，2-永久")
    private Integer type;

    @ApiModelProperty(value = "期限天数")
    private Integer deadlineDay;

    @ApiModelProperty(value = "会员卡原价")
    private BigDecimal originalPrice;

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

    @ApiModelProperty(value = "支付服务方订单号")
    private String outTradeNo;

    @ApiModelProperty(value = "会员卡到期时间")
    private Date cardExpirationTime;

    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    @ApiModelProperty(value = "更新时间")
    private Date updateTime;

    @ApiModelProperty(value = "是否微信小程序发货")
    private Boolean isWechatShipping;


}
