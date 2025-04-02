package com.zbkj.common.model.bill;

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

/**
 * <p>
 * 财务流水汇总表
 * </p>
 *
 * @author HZW
 * @since 2023-06-09
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("eb_summary_financial_statements")
@ApiModel(value="SummaryFinancialStatements对象", description="财务流水汇总表")
public class SummaryFinancialStatements implements Serializable {

    private static final long serialVersionUID=1L;

    @ApiModelProperty(value = "帐单id")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "会员充值金额")
    private BigDecimal rechargeAmount;

    @ApiModelProperty(value = "充值笔数")
    private Integer rechargeNum;

    @ApiModelProperty(value = "微信支付金额")
    private BigDecimal wechatPayAmount;

    @ApiModelProperty(value = "微信支付笔数")
    private Integer wechatPayNum;

    @ApiModelProperty(value = "支付宝支付金额")
    private BigDecimal aliPayAmount;

    @ApiModelProperty(value = "支付宝支付笔数")
    private Integer aliPayNum;

    @ApiModelProperty(value = "商户分账结算金额")
    private BigDecimal merchantSplitSettlement;

    @ApiModelProperty(value = "商户分账结算笔数")
    private Integer merchantSplitSettlementNum;

    @ApiModelProperty(value = "佣金结算金额")
    private BigDecimal brokerageSettlement;

    @ApiModelProperty(value = "佣金结算笔数")
    private Integer brokerageSettlementNum;

    @ApiModelProperty(value = "订单退款金额")
    private BigDecimal orderRefundAmount;

    @ApiModelProperty(value = "订单退款笔数")
    private Integer orderRefundNum;

    @ApiModelProperty(value = "收入金额")
    private BigDecimal incomeAmount;

    @ApiModelProperty(value = "支出金额")
    private BigDecimal payoutAmount;

    @ApiModelProperty(value = "日收支")
    private BigDecimal incomeExpenditure;

    @ApiModelProperty(value = "日期：年-月-日")
    private String dataDate;


}
