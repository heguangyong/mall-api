package com.zbkj.common.response.productTag;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @Auther: 大粽子
 * @Date: 2023/11/9 17:56
 * @Description: 商品标签查询所用对象
 */
@Data
public class ProductTagsForSearchResponse {

    @ApiModelProperty(value = "商品id集合")
    private List<Integer> productIds = new ArrayList<>();

    @ApiModelProperty(value = "品牌id")
    private List<Integer> brandId;

    @ApiModelProperty(value = "分类id")
    private List<Integer> categoryId;

    @ApiModelProperty(value = "商户id")
    private List<Integer> merId;
}
