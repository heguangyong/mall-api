package com.zbkj.common.response.groupbuy;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

/**
 * @author stivepeim
 * @date 2024/9/22 19:02
 * @description GroupBuyActivityRecordAdminListResponse
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "GroupBuyActivityRecordAdminListResponse", description = "拼团开团记录搜索响应对象")
public class GroupBuyActivityRecordAdminListResponse implements Serializable {

    private static final long serialVersionUID = 5834590952556732601L;

    @ApiModelProperty(value = "拼团记录ID")
    private Integer groupBuyingId;

    @ApiModelProperty(value = "拼团活动名称")
    private String groupName;

    @ApiModelProperty(value = "当前拼团商家信息")
    private String merName;

    @ApiModelProperty(value = "团长 用户ID ")
    private Integer groupLeaderUid;

    @ApiModelProperty(value = "团长 昵称")
    private String groupLeaderNickname;

    @ApiModelProperty(value = "拼团商品图")
    private String productImage;

    @ApiModelProperty(value = "拼团商品名称")
    private String productName;

    @ApiModelProperty(value = "拼团开始时间")
    private Date createTime;

    @ApiModelProperty(value = "拼团结束时间")
    private Date endTime;

    @ApiModelProperty(value = "成团总人数")
    private Integer buyingCountNum;

    @ApiModelProperty(value = "已参团人数")
    private Integer yetBuyingNum;

}
