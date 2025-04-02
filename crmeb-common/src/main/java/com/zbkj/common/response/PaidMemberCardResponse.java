package com.zbkj.common.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 付费会员卡响应对象
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
@ApiModel(value="PaidMemberCardResponse", description="付费会员卡响应对象")
public class PaidMemberCardResponse implements Serializable {

    private static final long serialVersionUID = 3558884699193209193L;

    @ApiModelProperty(value = "id")
    private Integer id;

    @ApiModelProperty(value = "会员卡名称")
    private String name;

    @ApiModelProperty(value = "卡片标签")
    private String label;

    @ApiModelProperty(value = "0-试用，1-期限，2-永久")
    private Integer type;

    @ApiModelProperty(value = "期限天数")
    private Integer deadlineDay;

    @ApiModelProperty(value = "会员卡原价")
    private BigDecimal originalPrice;

    @ApiModelProperty(value = "会员卡售价")
    private BigDecimal price;

    @ApiModelProperty(value = "是否仅首充赠送")
    private Boolean isFirstChargeGive;

    @ApiModelProperty(value = "赠送余额")
    private BigDecimal giftBalance;

    @ApiModelProperty(value = "排序")
    private Integer sort;

    @ApiModelProperty(value = "状态：1-开启，0-关闭")
    private Boolean status;
}
