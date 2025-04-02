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
 * 批量设置商品佣金请求对象
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
@ApiModel(value = "BatchSetProductBrokerageRequest", description = "批量设置运费模板请求对象")
public class BatchSetProductBrokerageRequest extends CommonBatchRequest implements Serializable {

    private static final long serialVersionUID = -4585094537501770138L;

    @ApiModelProperty(value = "一级返佣", required = true)
    @NotNull(message = "规格属性一级返佣不能为空")
    @Range(min = 0, max = 100, message = "返佣比例范围为0~100")
    private Integer brokerage;

    @ApiModelProperty(value = "二级返佣", required = true)
    @NotNull(message = "规格属性二级返佣不能为空")
    @Range(min = 0, max = 100, message = "返佣比例范围为0~100")
    private Integer brokerageTwo;

}
