package com.zbkj.common.response;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Auther: 大粽子
 * @Date: 2023/10/27 17:31
 * @Description: 商品标签 具体属性类 这样做方便拓展和排序
 */
@Data
public class ProductTagTaskItem {

    @ApiModelProperty(value = "商品id")
    private int id;

    @ApiModelProperty(value = "商品标签名称")
    private String tagName;

    @ApiModelProperty(value = "排序")
    private int sort;

    @ApiModelProperty(value = "标识 0否 大于0的业务 随着引用的业务")
    private int flag;

    @ApiModelProperty(value = "商城出现位置 =》 0标题下，1标题前")
    private int position;
}
