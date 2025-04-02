package com.zbkj.common.vo;

import com.zbkj.common.annotation.StringContains;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * 分销配置Vo对象
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
@ApiModel(value = "RetailStoreConfigVo对象", description = "分销配置Vo对象")
public class RetailStoreConfigVo {

    @ApiModelProperty(value = "是否启用分销:1-启用，0-禁止")
    @NotNull(message = "是否启用分销 不能为空")
    @Range(min = 0, max = 1, message = "超出分销开关选择范围")
    private Integer retailStoreSwitch;

    @ApiModelProperty(value = "分销额度：-1-关闭，0--用户购买金额大于等于设置金额时，用户自动成为分销员")
    @NotNull(message = "分销额度 不能为空")
    @Min(value = -1, message = "分销额度,不能小于-1")
    private Integer retailStoreLine;

    @ApiModelProperty(value = "分销关系绑定:0-所有用户，1-新用户")
    @NotNull(message = "分销关系绑定 不能为空")
    @Range(min = 0, max = 1, message = "未知的分校关系绑定类型")
    private Integer retailStoreBindingType;

    @ApiModelProperty(value = "是否展示分销气泡：0-展示，1-展示")
    @NotNull(message = "是否展示分销气泡 不能为空")
    @Range(min = 0, max = 1, message = "是否展示分销气泡只能选择0-1")
    private Integer retailStoreBubbleSwitch;

    @ApiModelProperty(value = "分销一级返佣比例")
    @NotNull(message = "一级返佣比例 不能为空")
    @Range(min = 0, max = 100, message = "一级返佣比例请在0-100中选择")
    private Integer retailStoreBrokerageFirstRatio;

    @ApiModelProperty(value = "分销二级返佣比例")
    @NotNull(message = "二级返佣比例 不能为空")
    @Range(min = 0, max = 100, message = "二级返佣比例在0-100中选择")
    private Integer retailStoreBrokerageSecondRatio;

    @ApiModelProperty(value = "分销佣金冻结时间")
    @NotNull(message = "分销佣金冻结时间 不能为空")
    @Min(value = 0, message = "分销佣金冻结时间最少为0天")
    private Integer retailStoreBrokerageFreezingTime;

    @ApiModelProperty(value = "分销提现最低金额(元)")
    @NotNull(message = "分销提现最低金额 不能为空")
    @DecimalMin(value = "0", message = "分销提现最低金额最小为0")
    private BigDecimal retailStoreExtractMinPrice;

    @ApiModelProperty(value = "分销提现银行")
    @NotNull(message = "分销提现银行 不能为空")
    private String retailStoreExtractBank;

    @ApiModelProperty(value = "分销佣金分账节点:pay:订单支付后，receipt:订单收货后，complete:订单完成后", required = true)
    @NotBlank(message = "分销佣金分账节点不能为空")
    @StringContains(limitValues = {"pay", "receipt", "complete"}, message = "未知的分销佣金分账节点")
    private String retailStoreBrokerageShareNode;

}
