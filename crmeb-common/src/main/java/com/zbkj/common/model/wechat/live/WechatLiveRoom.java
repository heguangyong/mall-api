package com.zbkj.common.model.wechat.live;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 *
 * </p>
 *
 * @author dazongzi
 * @since 2023-03-27
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("eb_wechat_live_room")
@ApiModel(value="WechatLiveRoom对象", description="")
public class WechatLiveRoom implements Serializable {

    private static final long serialVersionUID=1L;

    @ApiModelProperty(value = "主键")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "直播间id")
    private Integer roomId;

    @ApiModelProperty(value = "直播间背景图 MediaID")
    private String coverImg;

    @ApiModelProperty(value = "直播间背景图 微信返回")
    private String coverImgWx;

    @ApiModelProperty(value = "直播间背景图 本地图片")
    private String coverImgLocal;

    @ApiModelProperty(value = "直播间名称")
    private String roomName;

    @ApiModelProperty(value = "官方收录封面 MediaID")
    private String feedsImg;

    @ApiModelProperty(value = "官方收录封面 微信返回")
    private String feedsImgWx;

    @ApiModelProperty(value = "官方收录封面 本地图片")
    private String feedsImgLocal;

    @ApiModelProperty(value = "直播间分享图 MediaID")
    private String shareImg;

    @ApiModelProperty(value = "直播间分享图 微信返回")
    private String shareImgWx;

    @ApiModelProperty(value = "直播间分享图链接 本地图片")
    private String shareImgLocal;

    @ApiModelProperty(value = "主播昵称")
    private String anchorName;

    @ApiModelProperty(value = "主播微信号")
    private String anchorWechat;

    @ApiModelProperty(value = "主播副号微信号")
    private String subAnchorWechat;

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

    @ApiModelProperty(value = "创建者openid")
    private String createrOpenid;

    @ApiModelProperty(value = "创建者微信号，不传入则此直播间所有成员可见。传入则此房间仅创建者、管理员、超管、直播间主播可见")
    private String createrWechat;

    @ApiModelProperty(value = "直播间回放")
    private String liveReplay;

    @ApiModelProperty(value = "直播间商品id集合")
    private String goods;

    @ApiModelProperty(value = "同步微信直播间的商品")
    private String goodsJson;

    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    @ApiModelProperty(value = "小程序直播 小程序码")
    private String qrcodeUrl;

    @ApiModelProperty(value = "直播小助手")
    private String assistant;

    @ApiModelProperty(value = "审核:0=商户创建成功平台待审核，1=平台审核失败，2=平台审核成功并提交给微信审核失败，3=平台审核成功并提交给微信审核成功")
    private Integer reviewStatus;

    @ApiModelProperty(value = "审核错误原因")
    private String reviewReason;

    @ApiModelProperty(value = "商户名称")
    private String merName;

    @ApiModelProperty(value = "商户类型")
    private Integer merType;

    @ApiModelProperty(value = "商户id")
    private Integer merId;

    @ApiModelProperty(value = "推荐星级")
    private Integer star;

    @ApiModelProperty(value = "排序")
    private Integer sort;

}
