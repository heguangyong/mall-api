package com.zbkj.common.request.groupbuy;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author stivepeim
 * @date 2024/9/25 14:16
 * @description GroupBuyRecordForCache
 */
@Data
public class GroupBuyRecordForCache {

    @ApiModelProperty(value = "订单号")
    private String orderNo;

    @ApiModelProperty(value = "购买数量")
    private Integer payNum;

    @ApiModelProperty(value = "拼团活动id，营销类型2=拼团 时必填")
    private Integer groupBuyActivityId;

    @ApiModelProperty(value = "商品id")
    private Integer productId;

    @ApiModelProperty(value = "拼团记录id，营销类型2=拼团 时必填 0=开团 1=拼团")
    private Integer groupBuyRecordId = 0;

    @ApiModelProperty(value = "商户id")
    private Integer merId;

    @ApiModelProperty(value = "规格属性id")
    private Integer attrValueId;

    @ApiModelProperty(value = "虚拟成团状态0.未开启，1开启")
    private Integer fictiStatus;

    @ApiModelProperty(value = "用户id")
    private Integer id;

    @ApiModelProperty(value = "用户昵称")
    private String nickname;

    @ApiModelProperty(value = "用户头像")
    private String avatar;
}
