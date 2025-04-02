package com.zbkj.common.response;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @Auther: 大粽子
 * @Date: 2023/10/23 16:42
 * @Description: 商品标签商城返回Response
 */
@Data
public class ProductTagsFrontResponse {

    @ApiModelProperty(value = "商品标签 - 标题前")
    private List<ProductTagTaskItem> locationLeftTitle = new ArrayList<>();

    @ApiModelProperty(value = "商品标签 - 标题下")
    private List<ProductTagTaskItem> locationUnderTitle = new ArrayList<>();
}
