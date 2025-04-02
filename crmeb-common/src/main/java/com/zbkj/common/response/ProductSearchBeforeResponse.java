package com.zbkj.common.response;

import com.zbkj.common.model.product.ProductBrand;
import com.zbkj.common.model.product.ProductCategory;
import com.zbkj.common.model.product.ProductTag;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.List;

/**
 * 商品搜索前置信息响应对象
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
@ApiModel(value = "ProductSearchBeforeResponse", description = "商品搜索前置信息响应对象")
public class ProductSearchBeforeResponse implements Serializable {

    private static final long serialVersionUID = -4585094537501770138L;

    @ApiModelProperty(value = "品牌列表")
    private List<ProductBrand> brandList;

    @ApiModelProperty(value = "商品分类列表")
    private List<ProductCategory> categoryList;

    @ApiModelProperty(value = "标签列表")
    private List<ProductTag> productTagList;
}
