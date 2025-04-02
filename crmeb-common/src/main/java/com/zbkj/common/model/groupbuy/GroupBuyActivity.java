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
 * 拼团活动表
 * </p>
 *
 * @author dazongzi
 * @since 2024-08-13
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("eb_group_buy_activity")
@ApiModel(value="GroupBuyActivity对象", description="拼团活动表")
public class GroupBuyActivity implements Serializable {

    private static final long serialVersionUID=1L;

    @ApiModelProperty(value = "主键")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "拼团活动名称")
    private String groupName;

    @ApiModelProperty(value = "开始时间")
    private Date startTime;

    @ApiModelProperty(value = "结束时间")
    private Date endTime;

    @ApiModelProperty(value = "成团有效期 小时数")
    private Integer validHour;

    @ApiModelProperty(value = "成团总人数 - 真团")
    private Integer buyCount;

    @ApiModelProperty(value = "购买上限")
    private Integer allQuota;

    @ApiModelProperty(value = "单次购买数量")
    private Integer oncQuota;

    @ApiModelProperty(value = "控制状态 0=初始化 1=已拒绝 2=已撤销 3=待审核 4=已通过")
    private Integer groupStatus;

    @ApiModelProperty(value = "商户ID")
    private Integer merId;

    @ApiModelProperty(value = "商户名称")
    private String merName;

    @ApiModelProperty(value = "参与商品数量")
    private Integer productCount;

    @ApiModelProperty(value = "凑团 0不可见，1可见")
    private Integer showGroup;

    @ApiModelProperty(value = "虚拟成团 假团 0关闭 1开启")
    private Integer fictiStatus;

    @ApiModelProperty(value = "删除标记 0=未删除 1=已删除")
    private Integer isDel;

    @ApiModelProperty(value = "统计 - 开团数")
    private Integer totalActivityBegin;

    @ApiModelProperty(value = "统计 - 成团数")
    private Integer totalActivityDone;

    @ApiModelProperty(value = "统计 - 参团订单数")
    private Integer totalOrderBegin;

    @ApiModelProperty(value = "统计 - 成团订单数")
    private Integer totalOrderDone;

    @ApiModelProperty(value = "审核拒绝理由")
    private String refusal;

    @ApiModelProperty(value = "活动状态:0关闭，1开启")
    private Integer activityStatus;

    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    @ApiModelProperty(value = "更新时间")
    private Date updateTime;


}
