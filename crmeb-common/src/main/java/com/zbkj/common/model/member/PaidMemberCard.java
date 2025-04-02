package com.zbkj.common.model.member;

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
import java.util.Date;

/**
 * <p>
 * 付费会员卡表
 * </p>
 *
 * @author hzw
 * @since 2024-05-10
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("eb_paid_member_card")
@ApiModel(value="PaidMemberCard对象", description="付费会员卡表")
public class PaidMemberCard implements Serializable {

    private static final long serialVersionUID=1L;

    @ApiModelProperty(value = "id")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "会员卡名称")
    private String name;

    @ApiModelProperty(value = "卡片标签")
    private String label;

    @ApiModelProperty(value = "0-试用，1-期限，2-永久")
    private Integer type;

    @ApiModelProperty(value = "期限天数")
    private Integer deadlineDay;

    @ApiModelProperty(value = "会员卡原价")
    private BigDecimal originalPrice;

    @ApiModelProperty(value = "会员卡售价")
    private BigDecimal price;

    @ApiModelProperty(value = "是否仅首充赠送")
    private Boolean isFirstChargeGive;

    @ApiModelProperty(value = "赠送余额")
    private BigDecimal giftBalance;

    @ApiModelProperty(value = "排序")
    private Integer sort;

    @ApiModelProperty(value = "状态：1-开启，0-关闭")
    private Boolean status;

    @ApiModelProperty(value = "是否删除")
    private Boolean isDelete;

    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    @ApiModelProperty(value = "更新时间")
    private Date updateTime;


}
