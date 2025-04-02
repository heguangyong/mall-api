package com.zbkj.common.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * 用户结算配置信息
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
@ApiModel(value = "UserClosingConfigResponse对象", description = "用户结算配置信息")
public class UserClosingConfigResponse implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "提现最低金额")
    private String minPrice;

    @ApiModelProperty(value = "可提现佣金")
    private BigDecimal brokerage;

    @ApiModelProperty(value = "冻结佣金")
    private BigDecimal freezeBrokerage;

    @ApiModelProperty(value = "冻结天数")
    private String freezeDay;

    @ApiModelProperty(value = "提现银行")
    private List<String> bankList;
}
