package com.zbkj.common.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 商品品牌请求对象
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
@ApiModel(value="ProductBrandRequest对象", description="商品品牌请求对象")
public class ProductBrandRequest implements Serializable {

    private static final long serialVersionUID=1L;

    @ApiModelProperty(value = "品牌ID,添加时不填，编辑时必填")
    private Integer id;

    @ApiModelProperty(value = "名称")
    @NotEmpty(message = "品牌名不能为空")
    @Length(max = 100, message = "品牌名不能超过100个字符")
    private String name;

    @ApiModelProperty(value = "icon")
    private String icon;

    @ApiModelProperty(value = "排序")
    @NotNull(message = "排序不能为空")
    @Range(min = 0, max = 999, message = "排序的范围为0-999")
    private Integer sort;

    @ApiModelProperty(value = "关联分类id（只能关联3级分类），逗号分割")
    private String categoryIds;
}
