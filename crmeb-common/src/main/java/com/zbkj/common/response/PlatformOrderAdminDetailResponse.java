package com.zbkj.common.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 平台订单详情响应对象
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
@ApiModel(value="PlatformOrderAdminDetailResponse对象", description="平台订单详情响应对象")
public class PlatformOrderAdminDetailResponse implements Serializable {

    private static final long serialVersionUID=1L;

    @ApiModelProperty(value = "订单号")
    private String orderNo;

    @ApiModelProperty(value = "商户ID,商户订单等级及以下有值")
    private Integer merId;

    @ApiModelProperty(value = "用户id")
    private Integer uid;

    @ApiModelProperty(value = "订单商品总数")
    private Integer totalNum;

    @ApiModelProperty(value = "商品总价")
    private BigDecimal proTotalPrice;

    @ApiModelProperty(value = "邮费")
    private BigDecimal totalPostage;

    @ApiModelProperty(value = "订单总价")
    private BigDecimal totalPrice;

    @ApiModelProperty(value = "优惠券金额")
    private BigDecimal couponPrice;

    @ApiModelProperty(value = "使用积分")
    private Integer useIntegral;

    @ApiModelProperty(value = "积分抵扣金额")
    private BigDecimal integralPrice;

    @ApiModelProperty(value = "实际支付金额")
    private BigDecimal payPrice;

    @ApiModelProperty(value = "支付邮费")
    private BigDecimal payPostage;

    @ApiModelProperty(value = "支付状态")
    private Boolean paid;

    @ApiModelProperty(value = "支付时间")
    private Date payTime;

    @ApiModelProperty(value = "支付方式:weixin,alipay,yue")
    private String payType;

    @ApiModelProperty(value = "支付渠道：public-公众号,mini-小程序，h5-网页支付,yue-余额，wechatIos-微信Ios，wechatAndroid-微信Android,alipay-支付宝，alipayApp-支付宝App")
    private String payChannel;

    @ApiModelProperty(value = "订单状态（0：待支付，1：待发货,2：部分发货， 3：待核销，4：待收货,5：已收货,6：已完成，9：已取消）")
    private Integer status;

    @ApiModelProperty(value = "退款状态：0 未退款 1 申请中 2 退款中 3 已退款")
    private Integer refundStatus;

    @ApiModelProperty(value = "取消状态：0-未取消，1-系统取消，2-用户取消")
    private Integer cancelStatus;

    @ApiModelProperty(value = "用户是否删除")
    private Boolean isUserDel;

    @ApiModelProperty(value = "商户是否删除")
    private Boolean isMerchantDel;

    @ApiModelProperty(value = "赠送积分")
    private Integer gainIntegral;

    @ApiModelProperty(value = "商户系统内部的订单号,32个字符内、可包含字母, 其他说明见商户订单号")
    private String outTradeNo;

    @ApiModelProperty(value = "订单是否拆分")
    private Boolean isSplit;

    @ApiModelProperty(value = "拆分前的订单号")
    private String splitOrderNo;

    @ApiModelProperty(value = "订单类型:0-基础订单,1-秒杀订单,2-拼团订单")
    private Integer type;

    @ApiModelProperty(value = "订单二级类型:0-普通订单，1-积分订单，2-虚拟订单，4-视频号订单，5-云盘订单，6-卡密订单")
    private Integer secondType;

    @ApiModelProperty(value = "订单等级:0-主订单，1-商户订单，2-商户子订单")
    private Integer level;

    @ApiModelProperty(value = "创建时间")
    private Date createTime;


    @ApiModelProperty(value = "收货人姓名")
    private String realName;

    @ApiModelProperty(value = "收货人电话")
    private String userPhone;

    @ApiModelProperty(value = "收货详细地址")
    private String userAddress;

    @ApiModelProperty(value = "用户备注")
    private String userRemark;

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

    @ApiModelProperty(value = "核销员id")
    private Integer clerkId;

    @ApiModelProperty(value = "商户备注")
    private String merchantRemark;

    // 以下为自定义添加部分

    @ApiModelProperty(value = "用户昵称")
    private String nickname;

    @ApiModelProperty(value = "用户手机号")
    private String phone;

    @ApiModelProperty(value = "用户是否注销")
    private Boolean isLogoff;

    @ApiModelProperty(value = "核销员名称")
    private String clerkName;

    @ApiModelProperty(value = "订单商品详情")
    List<OrderInfoFrontDataResponse> orderDetailList;

    @ApiModelProperty(value = "商户名称")
    private String merName;

    @ApiModelProperty(value = "商户是否自营：0-非自营，1-自营")
    private Boolean merIsSelf;

    @ApiModelProperty(value = "商户优惠券金额")
    private BigDecimal merCouponPrice;

    @ApiModelProperty(value = "平台优惠券金额")
    private BigDecimal platCouponPrice;

    @ApiModelProperty(value = "svip优惠金额")
    private BigDecimal svipDiscountPrice;

    @ApiModelProperty(value = "系统表单ID")
    private Integer systemFormId;

    @ApiModelProperty(value = "系统表单数据")
    private String orderExtend;
}
