package com.zbkj.common.response.groupbuy;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author stivepeim
 * @date 2024/8/19 11:31
 * @description GroupBuyActivityProductResponse
 */
@Data
public class GroupBuyActivityProductResponse {

    @ApiModelProperty(value = "拼团基础商品id")
    private Integer productId;

    @ApiModelProperty(value = "商品名称")
    private String productName;

    @ApiModelProperty(value = "商品主图")
    private String image;

    @ApiModelProperty(value = "拼团商品")
    private List<GroupBuyActivitySkuResponse> groupBuyActivitySkuResponses;

}
