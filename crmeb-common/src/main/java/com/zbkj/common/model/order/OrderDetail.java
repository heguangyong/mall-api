package com.zbkj.common.model.order;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
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
 * 订单详情表
 * </p>
 *
 * @author HZW
 * @since 2022-09-19
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("eb_order_detail")
@ApiModel(value = "OrderDetail对象", description = "订单详情表")
public class OrderDetail implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "订单号")
    private String orderNo;

    @ApiModelProperty(value = "商户ID")
    private Integer merId;

    @ApiModelProperty(value = "用户id")
    private Integer uid;

    @ApiModelProperty(value = "商品ID")
    private Integer productId;

    @ApiModelProperty(value = "商品名称")
    private String productName;

    @ApiModelProperty(value = "商品图片")
    private String image;

    @ApiModelProperty(value = "商品规格值 ID")
    private Integer attrValueId;

    @ApiModelProperty(value = "商品sku")
    private String sku;

    @ApiModelProperty(value = "商品单价")
    private BigDecimal price;

    @ApiModelProperty(value = "会员价格")
    private BigDecimal vipPrice;

    @ApiModelProperty(value = "实际支付金额")
    private BigDecimal payPrice;

    @ApiModelProperty(value = "购买数量")
    private Integer payNum;

    @ApiModelProperty(value = "重量")
    private BigDecimal weight;

    @ApiModelProperty(value = "体积")
    private BigDecimal volume;

    @ApiModelProperty(value = "是否评价，0-未评价，1-已评价")
    private Boolean isReply;

    @ApiModelProperty(value = "是否收货，0-未收货，1-已收货")
    private Boolean isReceipt;

    @ApiModelProperty(value = "分佣类型:0-不参与分佣，1-单独分佣，2-默认分佣")
    private Integer subBrokerageType;

    @ApiModelProperty(value = "一级返佣比例")
    private Integer brokerage;

    @ApiModelProperty(value = "二级返佣比例")
    private Integer brokerageTwo;

    @ApiModelProperty(value = "运费金额")
    private BigDecimal freightFee;

    @ApiModelProperty(value = "优惠券金额")
    private BigDecimal couponPrice;

    @ApiModelProperty(value = "使用积分")
    private Integer useIntegral;

    @ApiModelProperty(value = "积分抵扣金额")
    private BigDecimal integralPrice;

    @ApiModelProperty(value = "赠送积分")
    private Integer gainIntegral;

    @ApiModelProperty(value = "基础类型：0=普通商品,1-积分商品,2-虚拟商品,4=视频号,5-云盘商品,6-卡密商品")
    private Integer productType;

    @ApiModelProperty(value = "营销类型：0=基础商品,1=秒杀,2=拼团")
    private Integer productMarketingType;

    @ApiModelProperty(value = "一级返佣金额")
    private BigDecimal firstBrokerageFee;

    @ApiModelProperty(value = "二级返佣金额")
    private BigDecimal secondBrokerageFee;

    @ApiModelProperty(value = "发货数量")
    private Integer  deliveryNum;

    @ApiModelProperty(value = "申请退款数量")
    private Integer applyRefundNum;

    @ApiModelProperty(value = "退款数量")
    private Integer refundNum;

    @ApiModelProperty(value = "退款金额")
    private BigDecimal refundPrice;

    @ApiModelProperty(value = "退使用积分")
    private Integer refundUseIntegral;

    @ApiModelProperty(value = "退款积分抵扣金额")
    private BigDecimal refundIntegralPrice;

    @ApiModelProperty(value = "退赠送积分")
    private Integer refundGainIntegral;

    @ApiModelProperty(value = "退一级返佣金额")
    private BigDecimal refundFirstBrokerageFee;

    @ApiModelProperty(value = "退二级返佣金额")
    private BigDecimal refundSecondBrokerageFee;

    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    @ApiModelProperty(value = "更新时间")
    private Date updateTime;

    @ApiModelProperty(value = "商户名称")
    @TableField(exist = false)
    private String merName;

    @ApiModelProperty(value = "商户优惠券金额")
    private BigDecimal merCouponPrice;

    @ApiModelProperty(value = "平台优惠券金额")
    private BigDecimal platCouponPrice;

    @ApiModelProperty(value = "退还平台优惠券金额")
    private BigDecimal refundPlatCouponPrice;

    @ApiModelProperty(value = "退运费金额")
    private BigDecimal refundFreightFee;

    @ApiModelProperty(value = "拓展文本，例云盘链接")
    private String expand;

    @ApiModelProperty(value = "卡密ID字符串")
    private String cardSecretIds;

    @ApiModelProperty(value = "是否svip订单")
    private Boolean isSvip;

    @ApiModelProperty(value = "是否付费会员商品")
    private Boolean isPaidMemberProduct;

    @ApiModelProperty(value = "商品是否支持退款")
    private Boolean proRefundSwitch;

    @ApiModelProperty(value = "兑换积分")
    private Integer redeemIntegral;

    @ApiModelProperty(value = "拼团活动id")
    private Integer groupBuyActivityId;

}
