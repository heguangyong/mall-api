package com.zbkj.common.vo;

import com.zbkj.common.annotation.StringContains;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 商户结算配置Vo对象
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
@ApiModel(value = "MerchantClosingConfigVo对象", description = "商户结算配置Vo对象")
public class MerchantClosingConfigVo implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "商户保证金额", required = true)
    @NotNull(message = "商户保证金额不能为空")
    @DecimalMin(value = "0.00", message = "商户保证金额不能小于0")
    private BigDecimal guaranteedAmount;

    @ApiModelProperty(value = "商户每笔最小转账额度", required = true)
    @NotNull(message = "商户每笔最小转账额度不能为空")
    @DecimalMin(value = "0.00", message = "商户每笔最小转账额度不能小于0")
    private BigDecimal transferMinAmount;

    @ApiModelProperty(value = "商户每笔最高转账额度", required = true)
    @NotNull(message = "商户每笔最高转账额度不能为空")
    @DecimalMin(value = "0.00", message = "商户每笔最高转账额度不能小于0")
    private BigDecimal transferMaxAmount;

    @ApiModelProperty(value = "商户分账节点:pay:订单支付后，receipt:订单收货后，complete:订单完成后", required = true)
    @NotBlank(message = "商户分账节点不能为空")
    @StringContains(limitValues = {"pay", "receipt", "complete"}, message = "未知的商户分账节点")
    private String merchantShareNode;

    @ApiModelProperty(value = "商户分账冻结时间（天）", required = true)
    @NotNull(message = "商户分账冻结时间不能为空")
    @Min(value = 0, message = "冻结时间不能小于0天")
    private Integer merchantShareFreezeTime;
}
