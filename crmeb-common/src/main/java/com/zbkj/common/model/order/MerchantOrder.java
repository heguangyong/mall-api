package com.zbkj.common.model.order;

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
 * 商户订单表
 * </p>
 *
 * @author HZW
 * @since 2022-09-19
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("eb_merchant_order")
@ApiModel(value = "MerchantOrder对象", description = "商户订单表")
public class MerchantOrder implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "订单ID")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "订单号")
    private String orderNo;

    @ApiModelProperty(value = "商户ID")
    private Integer merId;

    @ApiModelProperty(value = "用户id")
    private Integer uid;

    @ApiModelProperty(value = "收货人姓名")
    private String realName;

    @ApiModelProperty(value = "收货人电话")
    private String userPhone;

    @ApiModelProperty(value = "收货详细地址")
    private String userAddress;

    @ApiModelProperty(value = "订单商品总数")
    private Integer totalNum;

    @ApiModelProperty(value = "商品总价")
    private BigDecimal proTotalPrice;

    @ApiModelProperty(value = "邮费")
    private BigDecimal totalPostage;

    @ApiModelProperty(value = "订单总价")
    private BigDecimal totalPrice;

    @ApiModelProperty(value = "实际支付金额")
    private BigDecimal payPrice;

    @ApiModelProperty(value = "支付邮费")
    private BigDecimal payPostage;

    @ApiModelProperty(value = "使用积分")
    private Integer useIntegral;

    @ApiModelProperty(value = "积分抵扣金额")
    private BigDecimal integralPrice;

    @ApiModelProperty(value = "优惠券id")
    private Integer couponId;

    @ApiModelProperty(value = "优惠券金额")
    private BigDecimal couponPrice;

    @ApiModelProperty(value = "支付方式:weixin,alipay,yue")
    private String payType;

    @ApiModelProperty(value = "支付渠道：public-公众号,mini-小程序，h5-网页支付,yue-余额，wechatIos-微信Ios，wechatAndroid-微信Android,alipay-支付宝，alipayApp-支付宝App")
    private String payChannel;

    @ApiModelProperty(value = "赠送积分")
    private Integer gainIntegral;

    @ApiModelProperty(value = "用户备注")
    private String userRemark;

    @ApiModelProperty(value = "商户备注")
    private String merchantRemark;

    @ApiModelProperty(value = "配送方式 1=快递 ，2=门店自提，3-虚拟发货")
    private Integer shippingType;

    @ApiModelProperty(value = "一级返佣金额")
    private BigDecimal firstBrokerage;

    @ApiModelProperty(value = "二级返佣金额")
    private BigDecimal secondBrokerage;

    @ApiModelProperty(value = "平台手续费")
    private BigDecimal commissionCharge;

    @ApiModelProperty(value = "核销码")
    private String verifyCode;

    @ApiModelProperty(value = "发货类型：express-快递,fictitious-虚拟发货,merchant-商家配送，noNeed-无需发货")
    private String deliveryType;

    @ApiModelProperty(value = "是否拆分发货")
    private Boolean isSplitDelivery;

    @ApiModelProperty(value = "秒杀商品ID 0-非砍价商品")
    private Integer seckillId;

    @ApiModelProperty(value = "用户砍价活动id 0-没有")
    private Integer bargainUserId;

    @ApiModelProperty(value = "砍价id 0-没有砍价")
    private Integer bargainId;

    @ApiModelProperty(value = "拼团id 0-没有拼团")
    private Integer pinkId;

    @ApiModelProperty(value = "拼团商品id 0-一般商品")
    private Integer combinationId;

    @ApiModelProperty(value = "核销员id")
    private Integer clerkId;

    @ApiModelProperty(value = "订单类型:0-基础订单,1-秒杀订单,2-拼团订单")
    private Integer type;

    @ApiModelProperty(value = "订单二级类型:0-普通订单，1-积分订单，2-虚拟订单，4-视频号订单，5-云盘订单，6-卡密订单")
    private Integer secondType;

    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    @ApiModelProperty(value = "更新时间")
    private Date updateTime;

    @ApiModelProperty(value = "商户优惠券金额")
    private BigDecimal merCouponPrice;

    @ApiModelProperty(value = "平台优惠券金额")
    private BigDecimal platCouponPrice;

    @ApiModelProperty(value = "svip优惠金额")
    private BigDecimal svipDiscountPrice;

    @ApiModelProperty(value = "是否svip订单")
    private Boolean isSvip;
}
