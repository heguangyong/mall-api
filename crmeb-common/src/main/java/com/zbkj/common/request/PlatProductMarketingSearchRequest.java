package com.zbkj.common.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * 营销活动查询商品请求对象(平台)
 *
 * @author Han
 * @version 1.0.0
 * @Date 2024/7/15
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "PlatProductMarketingSearchRequest", description = "营销活动查询商品请求对象(平台)")
public class PlatProductMarketingSearchRequest extends ProductMarketingSearchRequest {

    private static final long serialVersionUID = 6899941520186863137L;

    @ApiModelProperty(value = "商户ID,英文逗号分隔")
    private String merIds;

}
