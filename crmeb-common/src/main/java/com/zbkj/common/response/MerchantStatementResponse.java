package com.zbkj.common.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

/**
 * 商户帐单响应对象
 *
 * @Auther: HZW
 * @Date: 2023/6/8 16:17
 * @Description: 平台日帐单响应对象
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "MerchantStatementResponse对象", description = "商户帐单响应对象")
public class MerchantStatementResponse {

    @ApiModelProperty(value = "帐单id")
    private Integer id;

    @ApiModelProperty(value = "实际收入")
    private BigDecimal realIncome;

    @ApiModelProperty(value = "订单应收")
    private BigDecimal orderReceivable;

    @ApiModelProperty(value = "订单实际支付金额")
    private BigDecimal orderRealIncome;

    @ApiModelProperty(value = "平台优惠券补贴")
    private BigDecimal platCouponPrice;

    @ApiModelProperty(value = "平台积分补贴")
    private BigDecimal integralPrice;

    @ApiModelProperty(value = "订单应退")
    private BigDecimal orderRefundable;

    @ApiModelProperty(value = "订单实际退款金额")
    private BigDecimal orderRealRefund;

    @ApiModelProperty(value = "退还平台优惠券补贴")
    private BigDecimal refundPlatCouponPrice;

    @ApiModelProperty(value = "退还平台积分补贴")
    private BigDecimal refundIntegralPrice;

    @ApiModelProperty(value = "实际支出")
    private BigDecimal actualExpenditure;

    @ApiModelProperty(value = "平台手续费")
    private BigDecimal handlingFee;

    @ApiModelProperty(value = "支付手续费")
    private BigDecimal payHandlingFee;

    @ApiModelProperty(value = "退还手续费")
    private BigDecimal refundHandlingFee;

    @ApiModelProperty(value = "佣金")
    private BigDecimal brokerage;

    @ApiModelProperty(value = "支付佣金")
    private BigDecimal brokeragePrice;

    @ApiModelProperty(value = "退还佣金")
    private BigDecimal refundBrokeragePrice;

    @ApiModelProperty(value = "支付笔数")
    private Integer payNum;

    @ApiModelProperty(value = "退款笔数")
    private Integer refundNum;

    @ApiModelProperty(value = "当日结余")
    private BigDecimal currentDayBalance;

    @ApiModelProperty(value = "日期：年-月-日/年-月")
    private String dataDate;
}
