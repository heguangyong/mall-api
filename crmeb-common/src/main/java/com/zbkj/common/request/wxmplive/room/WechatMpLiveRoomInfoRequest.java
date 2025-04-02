package com.zbkj.common.request.wxmplive.room;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @Auther: 大粽子
 * @Date: 2023/3/18 16:20
 * @Description: 微信小程序直播间参数
 */
@Data
public class WechatMpLiveRoomInfoRequest {

    private Integer id;

    @ApiModelProperty(value = "直播间名字，最短3个汉字，最长17个汉字，1个汉字相当于2个字符", required = true)
    @NotBlank(message = "直播间名称 不能为空")
//    @Range(min = 3, max = 17)
    private String roomName;

    @ApiModelProperty(value = "背景图，填入mediaID（mediaID获取后，三天内有效）；图片mediaID的获取，请参考以下文档： https://developers.weixin.qq.com/doc/offiaccount/Asset_Management/New_temporary_materials.html；直播间背景图，图片规则：建议像素1080*1920，大小不超过2M", required = true)
    @NotBlank(message = "直播间背景图 不能为空")
    private String coverImg;


    @ApiModelProperty(value = "直播间背景图 本地图片")
    @NotBlank(message = "直播间背景图 不能为空")
    private String coverImgLocal;


    @ApiModelProperty(value = "直播计划开始时间", required = true)
    @NotNull(message = "直播计划开始时间 不能为空")
    private String startTime;


    @ApiModelProperty(value = "直播计划结束时间（开播时间和结束时间间隔不得短于30分钟，不得超过24小时）", required = true)
    @NotNull(message = "直播计划开始时间 不能为空")
    private String endTime;


    @ApiModelProperty(value = "主播昵称，最短2个汉字，最长15个汉字，1个汉字相当于2个字符", required = true)
    @NotBlank(message = "主播昵称 不能为空")
//    @Range(min = 2, max = 17)
    private String anchorName;

    @ApiModelProperty(value = "主播微信号，如果未实名认证，需要先前往“小程序直播”小程序进行实名验证, 小程序二维码链接：https://res.wx.qq.com/op_res/9rSix1dhHfK4rR049JL0PHJ7TpOvkuZ3mE0z7Ou_Etvjf-w1J_jVX0rZqeStLfwh", required = true)
    @NotBlank(message = "主播微信账号 不能为空")
    private String anchorWechat;

    @ApiModelProperty(value = "主播副号微信号，如果未实名认证，需要先前往“小程序直播”小程序进行实名验证, 小程序二维码链接：https://res.wx.qq.com/op_res/9rSix1dhHfK4rR049JL0PHJ7TpOvkuZ3mE0z7Ou_Etvjf-w1J_jVX0rZqeStLfwh")
    private String subAnchorWechat;

    @ApiModelProperty(value = "创建者微信号，不传入则此直播间所有成员可见。传入则此房间仅创建者、管理员、超管、直播间主播可见")
    private String createrWechat;


    @ApiModelProperty(value = "分享图，填入mediaID（mediaID获取后，三天内有效）；图片mediaID的获取，请参考以下文档： https://developers.weixin.qq.com/doc/offiaccount/Asset_Management/New_temporary_materials.html；直播间分享图，图片规则：建议像素800*640，大小不超过1M；", required = true)
    @NotBlank(message = "分享图 不能为空")
    private String shareImg;


    @ApiModelProperty(value = "直播间分享图链接 本地图片")
    @NotBlank(message = "直播间分享图链接 不能为空")
    private String shareImgLocal;


    @ApiModelProperty(value = "购物直播频道封面图，填入mediaID（mediaID获取后，三天内有效）；图片mediaID的获取，请参考以下文档： https://developers.weixin.qq.com/doc/offiaccount/Asset_Management/New_temporary_materials.html; 购物直播频道封面图，图片规则：建议像素800*800，大小不超过100KB；", required = true)
    @NotBlank(message = "直播间频道 不能为空")
    private String feedsImg;


    @ApiModelProperty(value = "官方收录封面 本地图片")
    @NotBlank(message = "官方收录封面 不能为空")
    private String feedsImgLocal;


//    private String anchorImg;


    @ApiModelProperty(value = "是否开启官方收录 【1: 开启，0：关闭】，默认开启收录", required = true)
    @NotNull(message = "是否开启官方收录 不能为空")
    @Range(min = 0, max = 1)
    private Integer isFeedsPublic;


    @ApiModelProperty(value = "直播间类型 【1: 推流，0：手机直播", required = true)
    @NotNull(message = "直播间类型 不能为空")
    @Range(min = 0, max = 1)
    private Integer type;

    private Integer screenType;


    @ApiModelProperty(value = "是否关闭点赞 【0：开启，1：关闭】（若关闭，观众端将隐藏点赞按钮，直播开始后不允许开启）", required = true)
    @NotNull(message = "直播间是否关闭点赞 不能为空")
    @Range(min = 0, max = 1)
    private Integer closeLike;


    @ApiModelProperty(value = "是否关闭货架 【0：开启，1：关闭】（若关闭，观众端将隐藏商品货架，直播开始后不允许开启）", required = true)
    @NotNull(message = "直播间是否关闭货架 不能为空")
    @Range(min = 0, max = 1)
    private Integer closeGoods;


    @ApiModelProperty(value = "是否关闭评论 【0：开启，1：关闭】（若关闭，观众端将隐藏评论入口，直播开始后不允许开启）", required = true)
    @NotNull(message = "直播间是否关闭评论 不能为空")
    @Range(min = 0, max = 1)
    private Integer closeComment;


    @ApiModelProperty(value = "是否关闭回放 【0：开启，1：关闭】默认关闭回放（直播开始后允许开启）", required = true)
    @NotNull(message = "直播间是否关闭回放 不能为空")
    @Range(min = 0, max = 1)
    private Integer closeReplay;


    @ApiModelProperty(value = "是否关闭分享 【0：开启，1：关闭】默认开启分享（直播开始后不允许修改）", required = true)
    @NotNull(message = "直播间是否关闭分享 不能为空")
    @Range(min = 0, max = 1)
    private Integer closeShare;


    @ApiModelProperty(value = "是否关闭客服 【0：开启，1：关闭】 默认关闭客服（直播开始后允许开启）", required = true)
    @NotNull(message = "直播间是否关闭客服 不能为空")
    @Range(min = 0, max = 1)
    private Integer closeKf;

    @ApiModelProperty(value = "商户名称")
    private String merName;

    @ApiModelProperty(value = "商户类型")
    private Integer merType;

    @ApiModelProperty(value = "推荐星级")
    private Integer star;

    @ApiModelProperty(value = "排序")
    private Integer sort;
}
