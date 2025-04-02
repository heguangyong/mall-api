package com.zbkj.common.request.wxmplive.goods;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * @Auther: 大粽子
 * @Date: 2023/3/23 16:35
 * @Description: 微信小程序直播 商品排序
 */
@Data
public class WechatMpLiveGoodsSortRequest {

    @ApiModelProperty(value = "roomId 直播间id 不能为空", required = true)
    private Integer roomId;

    @ApiModelProperty(value = "待排序商品id集合", required = true)
    private List<Map<String, String>> goods;
}
