package com.zbkj.common.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

/**
 * 订单详情Vo对象
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
@ApiModel(value = "PreOrderInfoDetailVo对象", description = "预下单订单详情Vo对象")
public class PreOrderInfoDetailVo {

    @ApiModelProperty(value = "商品id")
    private Integer productId;

    @ApiModelProperty(value = "商品名称")
    private String productName;

    @ApiModelProperty(value = "规格属性id")
    private Integer attrValueId;

    @ApiModelProperty(value = "商品图片")
    private String image;

    @ApiModelProperty(value = "sku")
    private String sku;

    @ApiModelProperty(value = "单价")
    private BigDecimal price;

    @ApiModelProperty(value = "实际支付金额")
    private BigDecimal payPrice;

    @ApiModelProperty(value = "购买数量")
    private Integer payNum;

    @ApiModelProperty(value = "重量")
    private BigDecimal weight;

    @ApiModelProperty(value = "体积")
    private BigDecimal volume;

    @ApiModelProperty(value = "运费模板ID")
    private Integer tempId;

    @ApiModelProperty(value = "分佣类型:0-不参与分佣，1-单独分佣，2-默认分佣")
    private Integer subBrokerageType;

    @ApiModelProperty(value = "一级返佣")
    private Integer brokerage;

    @ApiModelProperty(value = "二级返佣")
    private Integer brokerageTwo;

    @ApiModelProperty(value = "基础类型：0=普通商品,1-积分商品,2-虚拟商品,4=视频号,5-云盘商品,6-卡密商品")
    private Integer productType;

    @ApiModelProperty(value = "营销类型：0=基础商品,1=秒杀,2=拼团")
    private Integer productMarketingType;

    @ApiModelProperty(value = "拼团活动id，营销类型2=拼团 时必填")
    private Integer groupBuyActivityId;

    @ApiModelProperty(value = "拼团记录id，营销类型2=拼团 时必填 0=开团 1=拼团")
    private Integer groupBuyRecordId = 0;

    @ApiModelProperty(value = "运费金额")
    private BigDecimal freightFee = BigDecimal.ZERO;

    @ApiModelProperty(value = "优惠券优惠金额")
    private BigDecimal couponPrice = BigDecimal.ZERO;

    @ApiModelProperty(value = "使用积分")
    private Integer useIntegral = 0;

    @ApiModelProperty(value = "积分抵扣金额")
    private BigDecimal integralPrice = BigDecimal.ZERO;

    @ApiModelProperty(value = "平台优惠券优惠金额")
    private BigDecimal platCouponPrice = BigDecimal.ZERO;

    @ApiModelProperty(value = "商户优惠券优惠金额")
    private BigDecimal merCouponPrice = BigDecimal.ZERO;

    @ApiModelProperty(value = "是否付费会员商品")
    private Boolean isPaidMember = false;

    @ApiModelProperty(value = "会员价格")
    private BigDecimal vipPrice = BigDecimal.ZERO;

    @ApiModelProperty(value = "配送方式：1-商家配送，2-到店核销")
    private String deliveryMethod;

    @ApiModelProperty(value = "商品是否支持退款")
    private Boolean proRefundSwitch;

    @ApiModelProperty(value = "兑换积分")
    private Integer redeemIntegral = 0;
}
