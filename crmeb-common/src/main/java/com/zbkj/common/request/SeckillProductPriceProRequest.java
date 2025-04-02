package com.zbkj.common.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

/**
 * <p>
 * 秒杀商品设置活动价商品请求对象
 * </p>
 *
 * @author HZW
 * @since 2022-12-15
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "SeckillProductPriceProRequest对象", description = "秒杀商品设置活动价商品请求对象")
public class SeckillProductPriceProRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "商品ID", required = true)
    @NotNull(message = "商品ID不能为空")
    private Integer id;

    @ApiModelProperty(value = "商品规格sku", required = true)
    private List<SeckillProductPriceAttrValueRequest> attrValue;
}
