package com.zbkj.common.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 用户优惠券响应对象
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
@ApiModel(value = "UserCouponResponse对象", description = "用户优惠券响应对象")
public class UserCouponResponse implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "id")
    private Integer id;

    @ApiModelProperty(value = "领取人id")
    private Integer uid;

    @ApiModelProperty(value = "优惠券名称")
    private String name;

    @ApiModelProperty(value = "使用类型 1-商家券, 2-商品券, 3-平台券")
    private Integer category;

    @ApiModelProperty(value = "优惠券的面值")
    private Long money;

    @ApiModelProperty(value = "最低消费多少金额可用优惠券")
    private Long minPrice;

    @ApiModelProperty(value = "状态（0：未使用，1：已使用, 2:已失效）")
    private Integer status;

    @ApiModelProperty(value = "开始使用时间")
    private Date startTime;

    @ApiModelProperty(value = "过期时间")
    private Date endTime;

    @ApiModelProperty(value = "使用时间")
    private Date useTime;

    @ApiModelProperty(value = "优惠券有效状态：usable-可用，unusable-已用，overdue-过期，notStart-未开始")
    private String validStr;
}
