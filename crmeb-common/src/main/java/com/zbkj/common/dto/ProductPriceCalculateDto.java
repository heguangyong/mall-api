package com.zbkj.common.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @ClassName ProductPriceCalculateDto
 * @Description 商品价格计算Dto对象
 * @Author HZW
 * @Date 2023/6/2 18:02
 * @Version 1.0
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="ProductPriceCalculateDto对象", description="商品价格计算Dto对象")
public class ProductPriceCalculateDto implements Serializable {

    private static final long serialVersionUID = -8121525449704982702L;

    @ApiModelProperty("商品ID")
    private Integer productId;

    @ApiModelProperty("商品规格ID")
    private Integer attrValueId;

    @ApiModelProperty("商户ID")
    private Integer merchantId;

    @ApiModelProperty("购买数量")
    private Integer num;

    @ApiModelProperty("商品金额")
    private BigDecimal price;

    @ApiModelProperty("商户优惠金额")
    private BigDecimal merCouponPrice = BigDecimal.ZERO;

    @ApiModelProperty("平台优惠金额")
    private BigDecimal platCouponPrice = BigDecimal.ZERO;

    @ApiModelProperty(value = "是否付费会员商品")
    private Boolean isPaidMember;

    @ApiModelProperty("vip商品金额")
    private BigDecimal vipPrice;

}
