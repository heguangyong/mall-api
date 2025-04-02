package com.zbkj.common.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 我的推广响应对象
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
@ApiModel(value="UserMyPromotionResponse对象", description="我的推广响应对象")
public class UserMyPromotionResponse implements Serializable {

    private static final long serialVersionUID=1L;

    @ApiModelProperty(value = "冻结的佣金")
    private BigDecimal freezePrice;

    @ApiModelProperty(value = "累计已提取佣金")
    private BigDecimal settledCommissionPrice;

    @ApiModelProperty(value = "当前佣金")
    private BigDecimal brokeragePrice;
}
