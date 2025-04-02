package com.zbkj.common.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 商品规格属性免审编辑对象
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
@ApiModel(value="ProductAttrValueReviewFreeEditRequest对象", description="商品规格属性免审编辑对象")
public class ProductAttrValueReviewFreeEditRequest implements Serializable {

    private static final long serialVersionUID=1L;

    @ApiModelProperty(value = "商品规格属性ID", required = true)
    @NotNull(message = "商品规格属性ID不能为空")
    private Integer id;

    @ApiModelProperty(value = "商品规格属性库存", required = true)
    @NotNull(message = "商品规格属性库存不能为空")
    @Min(value = 0, message = "库存不能小于0")
    private Integer stock;

    @ApiModelProperty(value = "规格属性金额", required = true)
    @NotNull(message = "规格属性金额不能为空")
    @DecimalMin(value = "0", message = "金额不能小于0")
    private BigDecimal price;


    @ApiModelProperty(value = "拓展文本，例云盘链接,云盘商品必填")
    private String expand;

    @ApiModelProperty(value = "卡密ID，卡密商品必填")
    private Integer cdkeyId;

}
