package com.zbkj.common.request.wxmplive.room;

import com.zbkj.common.request.PageParamRequest;
import com.zbkj.common.request.wxmplive.goods.WechatLiveRoomSearchRequest;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Auther: 大粽子
 * @Date: 2023/4/3 16:28
 * @Description: 描述对应的业务场景
 */
@Data
public class WechatMpLiveRoomSearchRequest {

    @ApiModelProperty(value = "搜索关键字:id,直播间id,直播间名称,主播昵称,主播微信号,主播副号微信号,主播手机号")
    WechatLiveRoomSearchRequest searchRequest;

    @ApiModelProperty(value = "分页")
    PageParamRequest pageParamRequest;
}
