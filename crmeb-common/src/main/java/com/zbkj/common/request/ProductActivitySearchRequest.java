package com.zbkj.common.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * 商品活动查询请求对象
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
@ApiModel(value = "ProductActivitySearchRequest对象", description = "商品活动查询请求对象")
public class ProductActivitySearchRequest extends PageParamRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "商品名称")
    private String name;

    @ApiModelProperty(value = "平台商品分类ID 逗号分割")
    private String categoryId;

    @ApiModelProperty(value = "状态（0：未上架，1：上架）")
    private Boolean isShow;

    @ApiModelProperty(value = "商家星级")
    private Integer merStars;

    @ApiModelProperty(value = "商户ID,英文逗号分隔")
    private String merIds;

    @ApiModelProperty(value = "商户分类id(商户端使用) 逗号分割")
    private String cateIds;

    @ApiModelProperty(value = "商品id(商户端使用)")
    private Integer productId;

    @ApiModelProperty(value = "品牌id 逗号分割")
    private String brandId;

    @ApiModelProperty(value = "价格排序", allowableValues = "range[asc,desc]")
    private String priceOrder;

    @ApiModelProperty(value = "销量排序", allowableValues = "range[asc,desc]")
    private String salesOrder;
}
