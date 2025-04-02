package com.zbkj.common.model.coupon;

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
 * 优惠券表
 * </p>
 *
 * @author HZW
 * @since 2022-07-19
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("eb_coupon")
@ApiModel(value = "Coupon对象", description = "优惠券表")
public class Coupon implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "优惠券表ID")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "商户ID")
    private Integer merId;

    @ApiModelProperty(value = "优惠券名称")
    private String name;

    @ApiModelProperty(value = "发行方，1-平台，2-商户")
    private Integer publisher;

    @ApiModelProperty(value = "类别 1-商家券, 2-商品券, 3-通用券，4-品类券，5-品牌券，6-跨店券")
    private Integer category;

    @ApiModelProperty(value = "领取类型 1-手动领取,2-商品赠送券,3-平台活动发放")
    private Integer receiveType;

    @ApiModelProperty(value = "优惠券类型 1-满减券,2-折扣券")
    private Integer couponType;

    @ApiModelProperty(value = "优惠金额")
    private Long money;

    @ApiModelProperty(value = "折扣")
    private Integer discount;

    @ApiModelProperty(value = "最低消费，0代表不限制")
    private Long minPrice;

    @ApiModelProperty(value = "是否限量, 默认0 不限量， 1限量")
    private Boolean isLimited;

    @ApiModelProperty(value = "发放总数")
    private Integer total;

    @ApiModelProperty(value = "剩余数量")
    private Integer lastTotal;

    @ApiModelProperty(value = "领取是否限时, 默认0-不限时，1-限时")
    private Boolean isTimeReceive;

    @ApiModelProperty(value = "可领取开始时间")
    private Date receiveStartTime;

    @ApiModelProperty(value = "可领取结束时间")
    private Date receiveEndTime;

    @ApiModelProperty(value = "是否固定使用时间, 默认0-否，1-使用固定时间")
    private Boolean isFixedTime;

    @ApiModelProperty(value = "可使用时间范围 开始时间")
    private Date useStartTime;

    @ApiModelProperty(value = "可使用时间范围 结束时间")
    private Date useEndTime;

    @ApiModelProperty(value = "天数")
    private Integer day;

    @ApiModelProperty(value = "排序")
    private Integer sort;

    @ApiModelProperty(value = "状态（0：关闭，1：开启）")
    private Boolean status;

    @ApiModelProperty(value = "是否删除 状态（0：否，1：是）")
    private Boolean isDel;

    @ApiModelProperty(value = "关联数据")
    private String linkedData;

    @ApiModelProperty(value = "是否可重复领取")
    private Boolean isRepeated;

    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    @ApiModelProperty(value = "更新时间")
    private Date updateTime;


}
