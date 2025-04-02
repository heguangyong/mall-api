package com.zbkj.common.request;

import com.zbkj.common.annotation.StringContains;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 *  购物车数量请求对象
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
@ApiModel(value="CartRequest对象", description="购物车数量请求对象")
public class CartNumRequest implements Serializable {

    private static final long serialVersionUID = -1186533756329913311L;

    @ApiModelProperty(value = "数量类型：total-商品数量，sum-购物数量", required = true)
    @NotEmpty(message = "Quantity type cannot be empty")
    @StringContains(limitValues = {"total", "sum"}, message = "Unknown quantity type")
    private String type;

    @ApiModelProperty(value = "商品类型：true-有效商品，false-无效商品", required = true)
    @NotNull(message = "Product type cannot be empty")
    private Boolean numType;

}
