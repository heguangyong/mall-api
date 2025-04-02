package com.zbkj.common.request.producttag;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Auther: 大粽子
 * @Date: 2023/10/11 11:45
 * @Description: 描述对应的业务场景
 */
@Data
public class ProductTagSearchRequest {

    @ApiModelProperty(value = "搜索关键字")
    private String keywords;
}
