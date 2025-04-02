package com.zbkj.common.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * 商户商品搜索对象
 *
 * @author Han
 * @version 1.0.0
 * @Date 2024/7/13
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "ProductSearchRequest对象", description = "商户商品搜索对象")
public class MerProductSearchRequest extends ProductSearchRequest {

    private static final long serialVersionUID = -4572464885363357010L;

    @ApiModelProperty(value = "商户商品分类ID")
    private String cateId;
}
