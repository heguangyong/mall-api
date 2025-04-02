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
import java.util.Date;

/**
 * <p>
 * 帐单表
 * </p>
 *
 * @author HZW
 * @since 2022-07-20
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("eb_bill")
@ApiModel(value="Bill对象", description="平台帐单表")
public class Bill implements Serializable {

    private static final long serialVersionUID=1L;

    @ApiModelProperty(value = "帐单id")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "用户uid")
    private Integer uid;

    @ApiModelProperty(value = "商户id")
    private Integer merId;

    @ApiModelProperty(value = "关联id")
    private Integer linkId;

    @ApiModelProperty(value = "关联订单")
    private String orderNo;

    @ApiModelProperty(value = "0 = 支出 1 = 获得")
    private Integer pm;

    @ApiModelProperty(value = "金额")
    private BigDecimal amount;

    @ApiModelProperty(value = "类型：pay_order-订单支付,refund_order-订单退款，recharge_user-用户充值，yue_pay-余额支付,merchant_collect-商户分账，brokerage-分佣,system-系统")
    private String type;

    @ApiModelProperty(value = "备注")
    private String mark;

    @ApiModelProperty(value = "添加时间")
    private Date createTime;


}
