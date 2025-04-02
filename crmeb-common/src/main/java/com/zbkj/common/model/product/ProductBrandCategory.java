package com.zbkj.common.model.product;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * <p>
 * 商品品牌分类关联表
 * </p>
 *
 * @author HZW
 * @since 2022-07-20
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("eb_product_brand_category")
@ApiModel(value="ProductBrandCategory对象", description="商品品牌分类关联表")
public class ProductBrandCategory implements Serializable {

    private static final long serialVersionUID=1L;

    @ApiModelProperty(value = "品牌id")
    private Integer bid;

    @ApiModelProperty(value = "分类id")
    private Integer cid;


}
