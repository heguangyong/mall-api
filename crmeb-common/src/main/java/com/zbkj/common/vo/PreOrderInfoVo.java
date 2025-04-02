package com.zbkj.common.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * 预下单Vo对象
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
@ApiModel(value = "PreOrderInfoVo对象", description = "预下单Vo对象")
public class PreOrderInfoVo implements Serializable {

    private static final long serialVersionUID = 567452850314559079L;

    @ApiModelProperty(value = "商品总计金额")
    private BigDecimal proTotalFee;

    @ApiModelProperty(value = "订单商品数量")
    private Integer orderProNum;

    @ApiModelProperty(value = "运费金额")
    private BigDecimal freightFee = BigDecimal.ZERO;

    @ApiModelProperty(value = "优惠券优惠金额")
    private BigDecimal couponFee = BigDecimal.ZERO;

    @ApiModelProperty(value = "商户优惠金额")
    private BigDecimal merCouponFee = BigDecimal.ZERO;

    @ApiModelProperty(value = "平台优惠金额")
    private BigDecimal platCouponFee = BigDecimal.ZERO;

    @ApiModelProperty(value = "svip优惠金额")
    private BigDecimal svipDiscountPrice = BigDecimal.ZERO;

    @ApiModelProperty(value = "实际支付金额")
    private BigDecimal payFee;

    @ApiModelProperty(value = "平台优惠券编号（选择优惠券时有值,不选时为0")
    private Integer platUserCouponId = 0;

    @ApiModelProperty(value = "地址id")
    private Integer addressId;

    @ApiModelProperty(value = "用户剩余积分")
    private Integer userIntegral;

    @ApiModelProperty(value = "用户可用余额")
    private BigDecimal userBalance;

    @ApiModelProperty(value = "商户订单数组")
    private List<PreMerchantOrderVo> merchantOrderVoList;

    @ApiModelProperty(value = "购物车编号列表")
    private List<Integer> cartIdList;

    @ApiModelProperty(value = "积分抵扣开关")
    private Boolean integralDeductionSwitch;

    @ApiModelProperty(value = "用户是否使用积分抵扣")
    private Boolean isUseIntegral;

    @ApiModelProperty(value = "订单类型:0-基础订单,1-秒杀订单,2-拼团订单")
    private Integer type;

    @ApiModelProperty(value = "订单二级类型:0-普通订单，1-积分订单，2-虚拟订单，4-视频号订单，5-云盘订单，6-卡密订单")
    private Integer secondType;

    @ApiModelProperty(value = "系统表单ID")
    private Integer systemFormId;

    @ApiModelProperty(value = "系统表单内容")
    private String systemFormValue;

    @ApiModelProperty(value = "兑换积分")
    private Integer redeemIntegral;
}
