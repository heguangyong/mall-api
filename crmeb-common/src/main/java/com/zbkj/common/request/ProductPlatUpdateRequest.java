package com.zbkj.common.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 平台端修改商品请求对象
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
@ApiModel(value="ProductPlatUpdateRequest对象", description="平台端修改商品请求对象")
public class ProductPlatUpdateRequest implements Serializable {

    private static final long serialVersionUID = -452373239606480650L;

    @ApiModelProperty(value = "商品id", required = true)
    @NotNull(message = "商品id不能为空")
    private Integer id;

    @ApiModelProperty(value = "初始销量", required = true)
    @NotNull(message = "初始销量不能为空")
    @Range(min = 0, max = 99999, message = "初始销量范围为0~99999")
    private Integer ficti;

    @ApiModelProperty(value = "排序", required = true)
    @NotNull(message = "排序不能为空")
    @Range(min = 0, max = 9999, message = "排序范围为0~9999")
    private Integer rank;
}
