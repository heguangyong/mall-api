package com.zbkj.common.model.groupbuy;

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
 * 拼团活动记录表
 * </p>
 *
 * @author dazongzi
 * @since 2024-08-13
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("eb_group_buy_record")
@ApiModel(value="GroupBuyRecord对象", description="拼团活动记录表")
public class GroupBuyRecord implements Serializable {

    private static final long serialVersionUID=1L;

    @TableId(value = "group_buying_id", type = IdType.AUTO)
    private Integer groupBuyingId;

    @ApiModelProperty(value = "拼团活动ID")
    private Integer groupActivityId;

    @ApiModelProperty(value = "活动商品ID")
    private Integer productGroupId;

    @ApiModelProperty(value = "商户id")
    private Integer merId;

    @ApiModelProperty(value = "skuid")
    private Integer skuid;

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

    @ApiModelProperty(value = "团长 用户ID ")
    private Integer groupLeaderUid;

    @ApiModelProperty(value = "团长 昵称")
    private String groupLeaderNickname;

    @ApiModelProperty(value = "删除标记")
    private Integer isDel;

    @ApiModelProperty(value = "结束时间")
    private Date endTime;

    private Date createTime;

    @ApiModelProperty(value = "是否隐藏团信息 0 否 1 是")
    private Boolean isHidde;


}
