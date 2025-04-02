package com.zbkj.common.vo.wxvedioshop.cat_brand;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Auther: 大粽子
 * @Date: 2022/9/26 18:33
 * @Description: 描述对应的业务场景
 */
@Data
public class ShopCatListItemResponse {
    @ApiModelProperty(value = "类目ID")
    private Integer third_cat_id;

    @ApiModelProperty(value = "类目名称")
    private String third_cat_name;

    @ApiModelProperty(value = "二类目ID")
    private Integer second_cat_id;

    @ApiModelProperty(value = "二类目名称")
    private String second_cat_name;

    @ApiModelProperty(value = "三类目ID")
    private Integer first_cat_id;

    @ApiModelProperty(value = "三类目名称")
    private String first_cat_name;
}
