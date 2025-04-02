package com.zbkj.common.response.groupbuy;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

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
@ApiModel(value="GroupBuyActivity对象", description="拼团活动表")
public class GroupBuyActivityResponse implements Serializable {

    private static final long serialVersionUID=1L;

    @ApiModelProperty(value = "主键")
    @TableId(value = "product_group_id", type = IdType.AUTO)
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

//    @ApiModelProperty(value = "创建时间")
//    private Date createTime;
//
//    @ApiModelProperty(value = "更新时间")
//    private Date updateTime;

    // 根据当前时间 结合上面的开始和结束时间判断
    @ApiModelProperty(value = "活动进程  0=未开始 1=进行中 2=已结束")
    private Integer groupProcess;

    @ApiModelProperty(value = "活动状态:0关闭，1开启")
    private Integer activityStatus;

    @ApiModelProperty(value = "拼团商品列表")
    private List<GroupBuyActivityProductResponse> groupBuyActivityProductResponseList;

    @ApiModelProperty(value = "当前活动历史拼团成功的")
    private List<GroupBuyUserInfo> groupBuyUserActivityDoneList;

    @ApiModelProperty(value = "全部成团订单数")
    private Integer totalAllOrderDone;

    @ApiModelProperty(value = "拼团成功人员列表")
    private List<GroupBuyActivityRecordOnProcessItem> processItem;

    // 因为拼团商品是引用关系，所有这里仅仅给予拼团价格即可
    @ApiModelProperty(value = "在拼团详情时使用 - 拼团活动价")
    private BigDecimal activePrice;

    @ApiModelProperty(value = "当前用户还可以购买几件")
    private Integer buyLimitCount;

    @ApiModelProperty(value = "当前拼团商品销量 - 当前拼团的销量")
    private Integer sales;

    @ApiModelProperty(value = "当前商品已经拼了多少份")
    private Integer latestBuyCount;

    public Integer getGroupProcess() {
        LocalDateTime now = LocalDateTime.now();
        if (now.isBefore(startTime.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime())) {
            groupProcess = 0;
        } else if (now.isAfter(endTime.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime())) {
            groupProcess = 2;
        } else {
            groupProcess = 1;
        }
        return groupProcess;
    }

}
