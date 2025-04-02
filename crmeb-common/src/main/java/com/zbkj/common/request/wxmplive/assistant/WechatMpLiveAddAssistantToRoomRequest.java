package com.zbkj.common.request.wxmplive.assistant;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

/**
 * @Auther: 大粽子
 * @Date: 2023/3/23 17:06
 * @Description: 微信小程序直播 添加微信助手对象
 */
@Data
public class WechatMpLiveAddAssistantToRoomRequest {

    @ApiModelProperty(value = "微信直播间本地id", required = true)
    private Integer id;

    @ApiModelProperty(value = "微信直播间助手对象", required = true)
    @NotNull(message = "微信直播间助手 不能为空")
    @Size(min = 1, message = "小助手集合不能为空")
    private List<Integer> assid;
}
