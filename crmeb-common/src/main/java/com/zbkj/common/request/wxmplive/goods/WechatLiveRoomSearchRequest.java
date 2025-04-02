package com.zbkj.common.request.wxmplive.goods;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * @Auther: 大粽子
 * @Date: 2023/3/27 17:57
 * @Description: 小程序直播间搜索参数
 */
@Data
public class WechatLiveRoomSearchRequest {

    @ApiModelProperty(value = "搜索关键字:id,直播间id,直播间名称,主播昵称,主播微信号,主播副号微信号,主播手机号")
    private String keywords;

    @ApiModelProperty(value = "直播间状态。101：直播中，102：未开始，103已结束，104禁播，105：暂停，106：异常，107：已过期")
    private Integer liveStatus;

    @ApiModelProperty(value = "显示在商城 显示在商城0关闭1开启")
    private Integer storeShow;

    @ApiModelProperty(value = "审核:0=商户创建成功平台待审核，1=平台审核失败，2=平台审核成功并提交给微信审核失败，3=平台审核成功并提交给微信审核成功")
    private Integer reviewStatus;

    @ApiModelProperty(value = "商户名称 平台用")
    private String merName;

    @ApiModelProperty(value = "商户类型 平台用")
    private Integer merType;

    @ApiModelProperty(value = "推荐星级 平台用")
    private Integer star;

    @ApiModelProperty(value = "排序 平台用")
    private Integer sort;
}
