package com.zbkj.common.request.wxmplive.goods;

import lombok.Data;

/**
 * @Auther: 大粽子
 * @Date: 2023/4/19 16:28
 * @Description: 用于维护直播间商品对象
 */
@Data
public class WechatGoodsJSON {
    private String coverImg;
    private Integer goodsId;
    private String name;
    private Long price;
    private Long price2;
    private Integer priceType;
    private String url;
}
