package com.zbkj.common.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.List;

/**
 * 商品ID集合搜索请求对象
 *
 * @author Han
 * @version 1.0.0
 * @Date 2024/7/13
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "ProductIdsSearchRequest", description = "商品ID集合搜索请求对象")
public class ProductIdsSearchRequest implements Serializable {

    private static final long serialVersionUID = 995150339964084658L;

    @ApiModelProperty(value = "商品ID集合")
    private List<Integer> ids;

}
