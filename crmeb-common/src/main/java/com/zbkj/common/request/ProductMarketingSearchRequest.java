package com.zbkj.common.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * 营销活动查询商品请求对象
 *
 * @author Han
 * @version 1.0.0
 * @Date 2024/7/15
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "ProductMarketingSearchRequest", description = "营销活动查询商品请求对象")
public class ProductMarketingSearchRequest extends PageParamRequest implements Serializable {

    private static final long serialVersionUID = 6899941520186863137L;

    @ApiModelProperty(value = "商品名称")
    private String name;

    @ApiModelProperty(value = "平台商品分类ID 逗号分割")
    private String categoryId;

    @ApiModelProperty(value = "状态（0：未上架，1：上架）")
    private Boolean isShow;

}
