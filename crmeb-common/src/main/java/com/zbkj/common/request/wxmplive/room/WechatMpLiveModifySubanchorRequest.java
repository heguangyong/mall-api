package com.zbkj.common.request.wxmplive.room;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Auther: 大粽子
 * @Date: 2023/3/21 17:50
 * @Description: 小程序直播 修改主播副号
 */
@Data
public class WechatMpLiveModifySubanchorRequest {

    @ApiModelProperty(value = "直播间id", required = true)
    private Integer roomId;

    @ApiModelProperty(value = "副号", required = true)
    private String userName;
}
