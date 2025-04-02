package com.zbkj.common.request.wxmplive.room;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * @Auther: 大粽子
 * @Date: 2023/3/27 18:10
 * @Description: 微信小程序直播间搜索对象
 */
@Data
public class WechatLiveRoomRequest {

    @ApiModelProperty(value = "直播间id")
    private Integer roomId;

//    @ApiModelProperty(value = "主播昵称")
//    private String anchorName;
//
//    @ApiModelProperty(value = "主播微信号")
//    private String anchorWechat;
//
//    @ApiModelProperty(value = "主播副号微信号")
//    private String subAnchorWechat;
//
//    @ApiModelProperty(value = "主播手机号")
//    private String anchorPhone;

    @ApiModelProperty(value = "直播开始时间")
    private Date startTime;

    @ApiModelProperty(value = "直播预计结束时间")
    private Date endTime;

    @ApiModelProperty(value = "直播间状态。101：直播中，102：未开始，103已结束，104禁播，105：暂停，106：异常，107：已过期")
    private Integer liveStatus;

    @ApiModelProperty(value = "显示在商城 显示在商城0关闭1开启")
    private Integer storeShow;

    @ApiModelProperty(value = "直播类型，1 推流 0 手机直播")
    private Integer type;

    @ApiModelProperty(value = "是否关闭点赞 【0：开启，1：关闭】（若关闭，观众端将隐藏点赞按钮，直播开始后不允许开启）")
    private Integer closeLike;

    @ApiModelProperty(value = "是否关闭货架 【0：开启，1：关闭】（若关闭，观众端将隐藏商品货架，直播开始后不允许开启）")
    private Integer closeGoods;

    @ApiModelProperty(value = "是否关闭评论 【0：开启，1：关闭】（若关闭，观众端将隐藏评论入口，直播开始后不允许开启）")
    private Integer closeComment;

    @ApiModelProperty(value = "是否关闭客服 【0：开启，1：关闭】 默认关闭客服（直播开始后允许开启）")
    private Integer closeKf;

    @ApiModelProperty(value = "是否关闭回放 【0：开启，1：关闭】默认关闭回放（直播开始后允许开启）")
    private Integer closeReplay;

    @ApiModelProperty(value = "是否关闭分享 【0：开启，1：关闭】默认开启分享（直播开始后不允许修改）")
    private Integer closeShare;

    @ApiModelProperty(value = "是否开启官方收录，1 开启，0 关闭")
    private Integer isFeedsPublic;

    @ApiModelProperty(value = "直播间回放")
    private String liveReplay;

    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    @ApiModelProperty(value = "直播小助手")
    private String assistant;

    @ApiModelProperty(value = "审核:0=商户创建成功平台待审核，1=平台审核失败，2=平台审核成功并提交给微信审核失败，3=平台审核成功并提交给微信审核成功")
    private Integer reviewStatus;
}
