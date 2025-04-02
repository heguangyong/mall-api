package com.zbkj.common.request.wxmplive.assistant;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Auther: 大粽子
 * @Date: 2023/3/20 17:32
 * @Description: 小程序直播间助手
 */
@Data
public class WechatMaLiveAssistantInfoRequest {

    @ApiModelProperty(value = "用户微信号")
    private String username;

    @ApiModelProperty(value = "用户微信昵称")
    private String nickname;
}
