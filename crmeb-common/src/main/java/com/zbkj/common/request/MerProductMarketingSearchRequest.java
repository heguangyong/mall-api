package com.zbkj.common.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * 营销活动查询商品请求对象(商品)
 *
 * @author Han
 * @version 1.0.0
 * @Date 2024/7/15
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "MerProductMarketingSearchRequest", description = "营销活动查询商品请求对象(商品)")
public class MerProductMarketingSearchRequest extends ProductMarketingSearchRequest {

    private static final long serialVersionUID = 6899941520186863137L;

    @ApiModelProperty(value = "活动ID，商户端使用必传")
    private Integer activityId;

    @ApiModelProperty(value = "商户分类id(商户端使用) 逗号分割")
    private String cateId;

    @ApiModelProperty(value = "营销类型：0=基础商品,1=秒杀,2=拼团")
    private Integer marketingType = 0;

    @ApiModelProperty(value = "商品id(商户端使用)")
    private Integer productId;
}
