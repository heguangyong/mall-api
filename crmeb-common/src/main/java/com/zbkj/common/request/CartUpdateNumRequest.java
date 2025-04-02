package com.zbkj.common.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotNull;

/**
 * 修改购物车商品数量请求对象
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
@ApiModel(value="CartUpdateNumRequest对象", description="修改购物车商品数量请求对象")
public class CartUpdateNumRequest {

    @ApiModelProperty(value = "购物车id", required = true)
    @NotNull(message = "Cart id cannot be empty")
    private Integer id;

    @ApiModelProperty(value = "购物车数量", required = true)
    @NotNull(message = "Cart quantity cannot be empty")
    @Range(min = 0, max = 999, message = "Invalid number of shopping carts")
    private Integer number;
}
