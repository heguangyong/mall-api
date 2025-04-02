package com.zbkj.common.response;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @Auther: 大粽子
 * @Date: 2023/10/21 11:03
 * @Description: 商品标签任务Response
 */
@Data
public class ProductTagTaskResponse {

    @ApiModelProperty(value = "Item")
    private List<ProductTagTaskItem> item = new ArrayList<>();

//    @ApiModelProperty(value = "爆品")
//    private ProductTagTaskItem totalSales;
//
//    @ApiModelProperty(value = "自营")
//    private ProductTagTaskItem isSelf;
//
//    @ApiModelProperty(value = "热卖")
//    private ProductTagTaskItem replayCount;
//
//    @ApiModelProperty(value = "优选")
//    private ProductTagTaskItem fiveStarCount;
//
//    @ApiModelProperty(value = "包邮")
//    private ProductTagTaskItem freeDelivery;

}
