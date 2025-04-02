package com.zbkj.common.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 商户端主页统计数据对象
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
@ApiModel(value = "HomeRateResponse对象", description = "商户端主页统计数据对象")
public class HomeRateResponse implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "今日销售额")
    private BigDecimal sales;

    @ApiModelProperty(value = "昨日销售额")
    private BigDecimal yesterdaySales;

    @ApiModelProperty(value = "今日订单量")
    private Integer orderNum;

    @ApiModelProperty(value = "昨日订单量")
    private Integer yesterdayOrderNum;

    @ApiModelProperty(value = "今日访客数")
    private Integer visitorsNum;

    @ApiModelProperty(value = "昨日访客数")
    private Integer yesterdayVisitorsNum;

    @ApiModelProperty(value = "关注量")
    private Integer followNum;
}
