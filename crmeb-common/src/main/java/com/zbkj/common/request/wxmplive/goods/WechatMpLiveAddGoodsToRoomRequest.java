package com.zbkj.common.request.wxmplive.goods;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @Auther: 大粽子
 * @Date: 2023/3/21 17:39
 * @Description: 微信小程序 直播间导入商品
 */
@Data
public class WechatMpLiveAddGoodsToRoomRequest {

    @ApiModelProperty(value = "本地直播间id", required = true)
    private Integer id;

    @ApiModelProperty(value = "数组列表，可传入多个，里面填写 商品 ID", required = true)
    private List<Integer> ids;
}
