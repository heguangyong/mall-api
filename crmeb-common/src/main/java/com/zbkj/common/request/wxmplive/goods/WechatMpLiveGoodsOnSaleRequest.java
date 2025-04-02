package com.zbkj.common.request.wxmplive.goods;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Range;

/**
 * @Auther: 大粽子
 * @Date: 2023/3/23 16:22
 * @Description: 微信小程序直播 商品上下架
 */
@Data
public class WechatMpLiveGoodsOnSaleRequest {
    /**
     * roomId 直播间id
     */
    @ApiModelProperty(value = "roomId 直播间id 不能为空", required = true)
    private Integer roomId;

    /**
     * goodsId 商品id
     */
    @ApiModelProperty(value = "goodsId 商品id 不能为空", required = true)
    private Integer goodsId;

    /**
     * onSale 上下架 【0：下架，1：上架】
     */
    @ApiModelProperty(value = "onSale 上下架 【0：下架，1：上架】")
    @Range(min = 0, max = 1)
    private Integer onSale;
}
