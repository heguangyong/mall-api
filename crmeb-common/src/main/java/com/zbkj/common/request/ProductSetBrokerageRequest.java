package com.zbkj.common.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 商品设置佣金请求对象
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
@ApiModel(value = "ProductSetBrokerageRequest", description = "商品设置佣金请求对象")
public class ProductSetBrokerageRequest {

    @ApiModelProperty(value = "商品ID", required = true)
    @NotNull(message = "商品ID 不能为空")
    private Integer id;

    @ApiModelProperty(value = "一级返佣", required = true)
    @NotNull(message = "规格属性一级返佣不能为空")
    @Range(min = 0, max = 100, message = "返佣比例范围为0~100")
    private Integer brokerage;

    @ApiModelProperty(value = "二级返佣", required = true)
    @NotNull(message = "规格属性二级返佣不能为空")
    @Range(min = 0, max = 100, message = "返佣比例范围为0~100")
    private Integer brokerageTwo;

}
