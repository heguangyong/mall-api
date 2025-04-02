package com.zbkj.common.response.groupbuy;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author stivepeim
 * @date 2024/9/11 15:11
 * @description GroupBuyActivityProductListForSale
 */
@Data
public class GroupBuyActivityProductListForSale {

    @ApiModelProperty(value = "id")
    private Integer id;

    @ApiModelProperty(value = "拼团活动id")
    private Integer groupActivityId;

    @ApiModelProperty(value = "拼团基础商品id")
    private Integer productId;

    @ApiModelProperty(value = "商品 sku id")
    private Integer skuId;

    @ApiModelProperty(value = "拼团活动价")
    private BigDecimal activePrice;

    @ApiModelProperty(value = "拼团限购数量 - 显示用")
    private Integer quotaShow;

    @ApiModelProperty(value = "拼团剩余数量 - 随减")
    private Integer quota;

    @ApiModelProperty(value = "控制状态 0=初始化 1=已拒绝 2=已撤销 3=待审核 4=已通过")
    private Integer groupStatus;

    @ApiModelProperty(value = "活动状态:0关闭，1开启")
    private Integer activityStatus;

    // 拼团活动属性

    @ApiModelProperty(value = "拼团活动名称")
    private String groupName;

    @ApiModelProperty(value = "成团总人数")
    private Integer buyCount;

//    @ApiModelProperty(value = "购买上限")
//    private Integer allQuota;
//
//    @ApiModelProperty(value = "单次购买数量")
//    private Integer oncQuota;

    @ApiModelProperty(value = "参与商品数量")
    private Integer productCount;

    @ApiModelProperty(value = "凑团 0不可见，1可见")
    private Integer showGroup;

    @ApiModelProperty(value = "虚拟成团 假团 0关闭 1开启")
    private Integer fictiStatus;

    @ApiModelProperty(value = "统计 - 参团订单数")
    private Integer totalOrderBegin;

    @ApiModelProperty(value = "统计 - 成团订单数")
    private Integer totalOrderDone;

    @ApiModelProperty(value = "商品图片")
    private String imageUrl;

    @ApiModelProperty(value = "商品名称")
    private String productName;

    @ApiModelProperty(value = "商品售价")
    private BigDecimal price;

    @ApiModelProperty(value = "当前商品已经拼了多少份")
    private Integer latestBuyCount;

}
