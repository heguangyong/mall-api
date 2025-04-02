package com.zbkj.common.response;


import com.zbkj.common.annotation.StringContains;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.models.auth.In;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * 积分配置响应对象
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
@ApiModel(value = "IntegralConfigResponse对象", description = "积分配置响应对象")
public class IntegralConfigResponse implements Serializable {

    private static final long serialVersionUID = -2562156923679905929L;

    @ApiModelProperty(value = "积分抵扣开关")
    @NotNull(message = "积分抵扣开关不能为空")
    private Boolean integralDeductionSwitch;

    @ApiModelProperty(value = "积分抵扣开启金额(单位元),订单满多少钱可以使用积分抵扣")
    @NotNull(message = "积分抵扣开启金额不能为空")
    @Min(value = 0, message = "积分抵扣开启金额最小为0元")
    private Integer integralDeductionStartMoney;

    @ApiModelProperty(value = "积分抵扣金额(1积分抵多少金额，0.01)")
    @NotNull(message = "积分抵扣金额不能为空")
    @DecimalMin(value = "0.01", message = "积分抵扣金额最小为0.01")
    private BigDecimal integralDeductionMoney;

    @ApiModelProperty(value = "积分抵扣比例(订单中积分可抵扣商品金额比例（0~100）%)")
    @NotNull(message = "积分抵扣比例不能为空")
    @Range(min = 0, max = 100, message = "积分抵扣比例为0~100")
    private Integer integralDeductionRatio;

    @ApiModelProperty(value = "下单支付金额按比例赠送积分（实际支付多少元赠送1积分)")
    @NotNull(message = "下单支付金额按比例赠送积分不能为空")
    @Min(value = 0, message = "下单支付金额按比例赠送积分最小为0")
    private Integer orderGiveIntegral;

    @ApiModelProperty(value = "积分冻结时间")
    @NotNull(message = "积分冻结时间不能为空")
    @Min(value = 0, message = "积分冻结时间最小为0")
    private Integer freezeIntegralDay;

    @ApiModelProperty(value = "积分冻结节点:pay:订单支付后，receipt:订单收货后，complete:订单完成后", required = true)
    @NotBlank(message = "积分冻结节点不能为空")
    @StringContains(limitValues = {"pay", "receipt", "complete"}, message = "未知的积分冻结节点")
    private String integralFreezeNode;
}
