package com.zbkj.common.response.groupbuy;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 拼团前端分享时使用
 * @author stivepeim
 * @date 2024/9/20 10:32
 * @description GroupBuyActivityRecordForFromtShareUse
 */
@Data
public class GroupBuyActivityRecordForFrontShareUse {
    @ApiModelProperty(value = "拼团活动id")
    private Integer groupActivityId;

    @ApiModelProperty(value = "拼团基础商品id")
    private Integer productId;

    @ApiModelProperty(value = "拼团记录id")
    private Integer groupRecordId;

//    @ApiModelProperty(value = "商户id")
//    private Integer merId;

    @ApiModelProperty(value = "状态：0=进行中，10=已成功，-1=已失败")
    private Integer recordStatus;

    @ApiModelProperty(value = "虚拟成团状态0.未开启，1开启")
    private Integer fictiStatus;

    @ApiModelProperty(value = "成团总人数")
    private Integer buyingCountNum;

    @ApiModelProperty(value = "真实人数")
    private Integer buyingNum;

    @ApiModelProperty(value = "已参团人数")
    private Integer yetBuyingNum;

    @ApiModelProperty(value = "凑团 0不可见，1可见")
    private Integer showGroup;

    @ApiModelProperty(value = "拼团活动价")
    private BigDecimal activePrice;

    @ApiModelProperty(value = "团长 用户ID ")
    private Integer groupLeaderUid;

    @ApiModelProperty(value = "团长 昵称")
    private String groupLeaderNickname;

    @ApiModelProperty(value = "团长 昵称")
    private String groupLeaderAvatar;

//    @ApiModelProperty(value = "删除标记")
//    private Integer isDel;

    @ApiModelProperty(value = "结束时间")
    private Date endTime;

    @ApiModelProperty(value = "可购买标识 0=可购买(没买过) 1=不可购买(买过)")
    private Integer canBuyFlag = 0;

    @ApiModelProperty(value = "当前商品已经拼了多少份")
    private Integer latestBuyCount;

}
