package com.zbkj.common.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 用户-我的账户响应对象
 *  +----------------------------------------------------------------------
 *  | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
 *  +----------------------------------------------------------------------
 *  | Copyright (c) 2016~2025 https://www.crmeb.com All rights reserved.
 *  +----------------------------------------------------------------------
 *  | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
 *  +----------------------------------------------------------------------
 *  | Author: CRMEB Team <admin@crmeb.com>
 *  +----------------------------------------------------------------------
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="UserMyAccountResponse对象", description="用户-我的账户响应对象")
public class UserMyAccountResponse implements Serializable {

    private static final long serialVersionUID=1L;

    @ApiModelProperty(value = "账户余额")
    private BigDecimal nowMoney;

    @ApiModelProperty(value = "累计充值金额")
    private BigDecimal recharge;

    @ApiModelProperty(value = "消费金额")
    private BigDecimal monetary;

    @ApiModelProperty(value = "充值开关")
    private Boolean rechargeSwitch;
}
