package com.zbkj.common.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotNull;

/**
 * 购物车重选Request对象类
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
public class CartResetRequest {

    @ApiModelProperty(value = "购物车id", required = true)
    @NotNull(message = "cart id can not be empty")
    private Long id;

    @ApiModelProperty(value = "购物车数量", required = true)
    @NotNull(message = "cart num can not be empty")
    @Range(min = 0, max = 999, message = "Invalid number in cart")
    private Integer num;

    @ApiModelProperty(value = "商品id", required = true)
    @NotNull(message = "product id can not be empty")
    private Integer productId;

    @ApiModelProperty(value = "AttrValue Id", required = true)
    @NotNull(message = "AttrValue id can not be empty")
    private Integer unique;
}
