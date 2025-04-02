package com.zbkj.common.request.wxmplive.room;

import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;
import lombok.Data;

/**
 * @Auther: 大粽子
 * @Date: 2023/4/20 12:19
 * @Description: 直播间分享
 */
@Data
public class WechatLiveRoomSharCode {

    @ApiModelProperty(value = "分享二维码cdn url")
    private String cdnUrl;

    @ApiModelProperty(value = "分享路径")
    private String pagePath;

    @ApiModelProperty(value = "分享海报 url")
    private String posterUrl;
}
