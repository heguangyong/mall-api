package com.zbkj.common.request.merchant;

import com.zbkj.common.annotation.StringContains;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 商户结算申请请求对象
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
@ApiModel(value="MerchantClosingApplyRequest对象", description="商户结算申请请求对象")
public class MerchantClosingApplyRequest implements Serializable {

    private static final long serialVersionUID=1L;

    @ApiModelProperty(value = "结算类型:bank-银行卡,wechat-微信,alipay-支付宝", required = true)
    @NotEmpty(message = "转账类型不能为空")
    @StringContains(limitValues = {"bank","wechat","alipay"}, message = "未知的转账类型")
    private String closingType;

    @ApiModelProperty(value = "转账金额", required = true)
    @NotNull(message = "转账金额不能为空")
    @DecimalMin(value = "0.01", message = "转账金额不能小于0.01")
    private BigDecimal amount;

    @ApiModelProperty(value = "备注")
    private String mark;
}
