package com.zbkj.common.model.product;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.List;

/**
 * <p>
 * 商品规格表
 * </p>
 *
 * @author HZW
 * @since 2024-11-11
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("eb_product_attribute")
@ApiModel(value = "ProductAttribute对象", description = "商品规格表")
public class ProductAttribute implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "商品ID")
    private Integer productId;

    @ApiModelProperty(value = "规格名")
    private String attributeName;

    @ApiModelProperty(value = "是否展示规格图片")
    private Boolean isShowImage;

    @ApiModelProperty(value = "排序")
    private Integer sort;

    @ApiModelProperty(value = "是否删除,0-否，1-是")
    private Boolean isDel;

    @ApiModelProperty(value = "商品规格属性列表")
    @TableField(exist = false)
    private List<ProductAttributeOption> optionList;
}
