package com.zbkj.common.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

/**
 * 商品规格添加对象
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
@ApiModel(value="ProductAttrAddRequest对象", description="商品规格添加对象")
public class ProductAttrAddRequest implements Serializable {

    private static final long serialVersionUID=1L;

    @ApiModelProperty(value = "attrID|新增时不填，修改时必填")
    private Integer id;

    @ApiModelProperty(value = "规格名", required = true)
    @NotBlank(message = "规格名不能为空")
    private String attributeName;

    @ApiModelProperty(value = "是否展示规格图片", required = true)
    @NotNull(message = "请选择是否展示规格图片")
    private Boolean isShowImage;

    @ApiModelProperty(value = "排序")
    private Integer sort;

    @ApiModelProperty(value = "规格属性数组")
    @NotEmpty(message = "规格属性数组不能为空")
    @Valid
    private List<ProductAttrOptionAddRequest> optionList;
}
