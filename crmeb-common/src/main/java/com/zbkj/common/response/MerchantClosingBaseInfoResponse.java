package com.zbkj.common.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 商户结算申请基础信息响应对象
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
@ApiModel(value = "MerchantClosingBaseInfoResponse对象", description = "商户结算申请基础信息响应对象")
public class MerchantClosingBaseInfoResponse implements Serializable {

    private static final long serialVersionUID = -4585094537501770138L;

    @ApiModelProperty(value = "商户保证金额")
    private BigDecimal guaranteedAmount;

    @ApiModelProperty(value = "商户每笔最小转账额度")
    private BigDecimal transferMinAmount;

    @ApiModelProperty(value = "商户每笔最高转账额度")
    private BigDecimal transferMaxAmount;

    @ApiModelProperty(value = "商户余额")
    private BigDecimal balance;

    @ApiModelProperty(value = "商户可用转账余额")
    private BigDecimal transferBalance;

    @ApiModelProperty(value = "结算类型:bank-银行卡,wechat-微信,alipay-支付宝")
    private String settlementType;

    @ApiModelProperty(value = "持卡人姓名")
    private String bankUserName;

    @ApiModelProperty(value = "银行名称")
    private String bankName;

    @ApiModelProperty(value = "银行卡号")
    private String bankCard;

    @ApiModelProperty(value = "微信号")
    private String wechatCode;

    @ApiModelProperty(value = "微信收款二维码")
    private String wechatQrcodeUrl;

    @ApiModelProperty(value = "真实姓名")
    private String realName;

    @ApiModelProperty(value = "支付宝账号")
    private String alipayCode;

    @ApiModelProperty(value = "支付宝收款二维码")
    private String alipayQrcodeUrl;

    @ApiModelProperty(value = "商户冻结额度")
    private BigDecimal freezeAmount;
}
