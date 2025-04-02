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
import java.math.BigDecimal;

/**
 * <p>
 * 拼团商品表
 * </p>
 *
 * @author dazongzi
 * @since 2024-08-13
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("eb_group_buy_activity_sku")
@ApiModel(value="GroupBuyActivitySku对象", description="拼团商品表")
public class GroupBuyActivitySku implements Serializable {

    private static final long serialVersionUID=1L;

    @ApiModelProperty(value = "主键")
    @TableId(value = "id", type = IdType.AUTO)
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

}
