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
 * 拼团参与成员表
 * </p>
 *
 * @author dazongzi
 * @since 2024-08-13
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("eb_group_buy_user")
@ApiModel(value="GroupBuyUser对象", description="拼团参与成员表")
public class GroupBuyUser implements Serializable {

    private static final long serialVersionUID=1L;

    @ApiModelProperty(value = "id主键")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "拼团记录ID")
    private Integer groupRecordId;

    @ApiModelProperty(value = "活动商品ID")
    private Integer productGroupId;

    @ApiModelProperty(value = "拼团活动ID")
    private Integer groupActivityId;

    @ApiModelProperty(value = "商户ID")
    private Integer merId;

    @ApiModelProperty(value = "购买数量")
    private Integer payNum;

    @ApiModelProperty(value = "状态：0=进行中，10=已成功，-1=已失败")
    private Integer recordStatus;

    @ApiModelProperty(value = "订单状态")
    private Integer orderStatus;

    @ApiModelProperty(value = "退款状态：0 未退款 1 申请中 2 部分退款 3 已退款")
    private Integer refundStatus;

    @ApiModelProperty(value = "是否为 团长 0=否 1=是")
    private Integer isLeader;

    @ApiModelProperty(value = "订单ID 商户订单")
    private String orderId;

    @ApiModelProperty(value = "用户ID ")
    private Integer groupUid;

    @ApiModelProperty(value = "昵称")
    private String groupNickname;

    @ApiModelProperty(value = "头像")
    private String groupAvatar;

    private Integer isDel;

    private Date createTime;


}
