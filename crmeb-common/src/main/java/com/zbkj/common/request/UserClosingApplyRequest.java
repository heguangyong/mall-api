package com.zbkj.common.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.zbkj.common.annotation.StringContains;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 用户结算申请请求对象
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
@ApiModel(value="UserClosingApplyRequest对象", description="用户结算申请请求对象")
public class UserClosingApplyRequest implements Serializable {

    private static final long serialVersionUID=1L;

    @ApiModelProperty(value = "结算方式| alipay=支付宝,bank=银行卡,wechat=微信", allowableValues = "range[alipay,weixin,bank]")
    @NotBlank(message = "请选择结算方式， 支付宝|微信|银行卡")
    @StringContains(limitValues = {"alipay","wechat","bank"}, message = "未知的结算方式")
    private String type;

    @ApiModelProperty(value = "支付宝账号")
    private String alipayAccount;

    @ApiModelProperty(value = "微信号")
    private String wechatNo;

    @ApiModelProperty(value = "微信收款码")
    private String paymentCode;

    @ApiModelProperty(value = "真实姓名")
    private String realName;

    @ApiModelProperty(value = "银行卡持卡人")
    private String cardholder;

    @ApiModelProperty(value = "银行卡卡号")
    private String bankCardNo;

    @ApiModelProperty(value = "提现银行名称")
    private String bankName;

    @ApiModelProperty(value = "结算金额")
    @NotNull(message = "请输入结算金额")
    @DecimalMin(value = "0.01", message = "提现金额不能小于0.01")
    private BigDecimal closingPrice;

}
