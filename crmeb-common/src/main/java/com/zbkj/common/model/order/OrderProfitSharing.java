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
 * 订单分账表
 * </p>
 *
 * @author HZW
 * @since 2022-07-20
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("eb_order_profit_sharing")
@ApiModel(value="OrderProfitSharing对象", description="订单分账表")
public class OrderProfitSharing implements Serializable {

    private static final long serialVersionUID=1L;

    @ApiModelProperty(value = "分账id")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "订单号（商户）")
    private String orderNo;

    @ApiModelProperty(value = "商户ID")
    private Integer merId;

    @ApiModelProperty(value = "订单付款金额")
    private BigDecimal orderPrice;

    @ApiModelProperty(value = "消耗积分数量")
    private Integer integralNum;

    @ApiModelProperty(value = "订单积分抵扣金额")
    private BigDecimal integralPrice;

    @ApiModelProperty(value = "平台分账金额")
    private BigDecimal profitSharingPlatPrice;

    @ApiModelProperty(value = "商户分账金额")
    private BigDecimal profitSharingMerPrice;

    @ApiModelProperty(value = "一级返佣金额")
    private BigDecimal firstBrokerageFee;

    @ApiModelProperty(value = "二级返佣金额")
    private BigDecimal secondBrokerageFee;

    @ApiModelProperty(value = "退款金额")
    private BigDecimal profitSharingRefund;

    @ApiModelProperty(value = "退还使用积分")
    private Integer refundUseIntegral;

    @ApiModelProperty(value = "退款积分抵扣金额")
    private BigDecimal refundIntegralPrice;

    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    @ApiModelProperty(value = "更新时间")
    private Date updateTime;

    @ApiModelProperty(value = "平台优惠券金额")
    private BigDecimal platCouponPrice;

    @ApiModelProperty(value = "退还平台优惠券补贴金额")
    private BigDecimal refundPlatCouponPrice;

    @ApiModelProperty(value = "退还平台分账金额")
    private BigDecimal refundProfitSharingPlatPrice;

    @ApiModelProperty(value = "退还商户分账金额")
    private BigDecimal refundProfitSharingMerPrice;

    @ApiModelProperty(value = "退还一级返佣金额")
    private BigDecimal refundFirstBrokerageFee;

    @ApiModelProperty(value = "退还二级返佣金额")
    private BigDecimal refundSecondBrokerageFee;

    @ApiModelProperty(value = "运费金额")
    private BigDecimal freightFee;

    @ApiModelProperty(value = "退还运费金额")
    private BigDecimal refundFreightFee;
}
