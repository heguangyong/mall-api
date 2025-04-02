package com.zbkj.common.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

/**
 *  添加购物车参数
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
@ApiModel(value="CartRequest对象", description="购物车")
public class CartRequest {
    private static final long serialVersionUID=1L;

    @ApiModelProperty(value = "商品ID", required = true)
    @NotNull(message = "请选择商品")
    private Integer productId;

    @ApiModelProperty(value = "商品属性 -- attr 对象的id", required = true)
    @NotNull(message = "请选择商品规格")
    private Integer productAttrUnique;

    @ApiModelProperty(value = "商品数量", required = true)
    @NotNull(message = "请选择商品数量")
    @Range(min = 1, max = 99, message = "单次可添加商品数量范围为 1~99")
    private Integer cartNum;
}
