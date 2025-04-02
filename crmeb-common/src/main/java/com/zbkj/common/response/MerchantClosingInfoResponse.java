package com.zbkj.common.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 商户结算详情响应对象
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
@ApiModel(value = "MerchantClosingInfoResponse对象", description = "商户结算详情响应对象")
public class MerchantClosingInfoResponse implements Serializable {

    private static final long serialVersionUID = -4585094537501770138L;

    @ApiModelProperty(value = "转账id")
    private Integer id;

    @ApiModelProperty(value = "结算单号")
    private String closingNo;

    @ApiModelProperty(value = "金额")
    private BigDecimal amount;

    @ApiModelProperty(value = "结算类型:bank-银行卡,wechat-微信,alipay-支付宝")
    private String closingType;

    @ApiModelProperty(value = "结算姓名")
    private String closingName;

    @ApiModelProperty(value = "结算银行")
    private String closingBank;

    @ApiModelProperty(value = "结算银行卡号")
    private String closingBankCard;

    @ApiModelProperty(value = "支付宝账号")
    private String alipayAccount;

    @ApiModelProperty(value = "微信号")
    private String wechatNo;

    @ApiModelProperty(value = "收款二维码")
    private String paymentCode;

    @ApiModelProperty(value = "真实姓名")
    private String realName;

    @ApiModelProperty(value = "到账状态：0-未到账，1-已到账")
    private Integer accountStatus;

    @ApiModelProperty(value = "结算凭证")
    private String closingProof;

    @ApiModelProperty(value = "结算时间")
    private Date closingTime;

    @ApiModelProperty(value = "审核状态：0-待审核，1-通过审核，2-审核失败")
    private Integer auditStatus;

    @ApiModelProperty(value = "拒绝原因")
    private String refusalReason;

    @ApiModelProperty(value = "审核时间")
    private Date auditTime;

    @ApiModelProperty(value = "商户备注")
    private String mark;

    @ApiModelProperty(value = "平台备注")
    private String platformMark;

    @ApiModelProperty(value = "添加时间")
    private Date createTime;

    @ApiModelProperty(value = "商户名称")
    private String merName;

    @ApiModelProperty(value = "商户余额")
    private BigDecimal balance;
}
