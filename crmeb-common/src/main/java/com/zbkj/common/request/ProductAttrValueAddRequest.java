package com.zbkj.common.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 商品属性值表
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
@ApiModel(value = "ProductAttrValueAddRequest对象", description = "商品规格属性添加对象")
public class ProductAttrValueAddRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "ID")
    private Integer id;

    @ApiModelProperty(value = "商品规格属性库存", required = true)
    @NotNull(message = "商品规格属性库存不能为空")
    @Min(value = 0, message = "库存不能小于0")
    private Integer stock;

    @ApiModelProperty(value = "规格属性金额", required = true)
    @NotNull(message = "规格属性金额不能为空")
    @DecimalMin(value = "0", message = "金额不能小于0")
    @DecimalMax(value = "999999.99", message = "商品金额最大为999999.99")
    private BigDecimal price;

    @ApiModelProperty(value = "会员价格")
    private BigDecimal vipPrice;

    @ApiModelProperty(value = "图片", required = true)
    @NotEmpty(message = "商品规格属性图片不能为空")
    private String image;

    @ApiModelProperty(value = "成本价", required = true)
    @NotNull(message = "规格属性成本价不能为空")
    @DecimalMin(value = "0", message = "成本价不能小于0")
    @DecimalMax(value = "999999.99", message = "商品成本价最大为999999.99")
    private BigDecimal cost;

    @ApiModelProperty(value = "原价", required = true)
    @NotNull(message = "规格属性原价不能为空")
    @DecimalMin(value = "0", message = "原价不能小于0")
    @DecimalMax(value = "999999.99", message = "商品原价最大为999999.99")
    private BigDecimal otPrice;

    @ApiModelProperty(value = "重量", required = true)
    @NotNull(message = "规格属性重量不能为空")
    @DecimalMin(value = "0", message = "重量不能小于0")
    private BigDecimal weight;

    @ApiModelProperty(value = "体积", required = true)
    @NotNull(message = "规格属性体积不能为空")
    @DecimalMin(value = "0", message = "体积不能小于0")
    private BigDecimal volume;

    @ApiModelProperty(value = "一级返佣", required = true)
    @NotNull(message = "规格属性一级返佣不能为空")
    @Range(min = 0, max = 100, message = "返佣比例范围为0~100")
    private Integer brokerage;

    @ApiModelProperty(value = "二级返佣", required = true)
    @NotNull(message = "规格属性二级返佣不能为空")
    @Range(min = 0, max = 100, message = "返佣比例范围为0~100")
    private Integer brokerageTwo;

    @ApiModelProperty(value = "attr_values 创建更新时的属性对应", required = true, example = "{\"尺码\":\"2XL\",\"颜色\":\"DX027白色\"}")
    @NotBlank(message = "attr_values不能为空")
    private String attrValue;

    @ApiModelProperty(value = "商品条码")
    private String barCode;

    @ApiModelProperty(value = "拓展文本，例云盘链接")
    private String expand;

    @ApiModelProperty(value = "卡密ID,卡密商品必填")
    private Integer cdkeyId;

    @ApiModelProperty(value = "商品条码")
    private String itemNumber;

    @ApiModelProperty(value = "是否显示")
    private Boolean isShow;

    @ApiModelProperty(value = "是否默认")
    private Boolean isDefault;
}
