package com.zbkj.common.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * 付费会员配置VO对象
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
@ApiModel(value = "PaidMemberConfigResponse", description = "付费会员配置VO对象")
public class PaidMemberConfigVo implements Serializable {

    private static final long serialVersionUID = -4585094537501770138L;

    @ApiModelProperty(value = "付费会员-购卡入口 1-开启，0-关闭", required = true)
    @NotBlank(message = "购卡入口不能为空")
    private String paidMemberPaidEntrance;

    @ApiModelProperty(value = "付费会员-会员价格展示 all-全部，paid-仅付费会员", required = true)
    @NotBlank(message = "会员价格展示不能为空")
    private String paidMemberPriceDisplay;

    @ApiModelProperty(value = "付费会员-会员专享商品列表开关 1-开启，0-关闭", required = true)
    @NotBlank(message = "会员专享商品列表开关不能为空")
    private String paidMemberProductSwitch;
}
