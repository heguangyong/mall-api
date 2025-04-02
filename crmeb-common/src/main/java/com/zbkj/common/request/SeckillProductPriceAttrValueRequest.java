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
 * <p>
 * 秒杀商品设置活动价规格SKU请求对象
 * </p>
 *
 * @author HZW
 * @since 2022-12-15
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "SeckillProductPriceAttrValueRequest对象", description = "秒杀商品设置活动价规格SKU请求对象")
public class SeckillProductPriceAttrValueRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "秒杀商品attrValueId", required = true)
    @NotNull(message = "秒杀商品规格ID不能为空")
    private Integer id;

    @ApiModelProperty(value = "活动价", required = true)
    @NotNull(message = "活动价不能为空")
    @DecimalMin(value = "0.00", message = "活动价不能小于0")
    private BigDecimal activityPrice;

}
