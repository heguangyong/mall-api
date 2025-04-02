package com.zbkj.common.model.coupon;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
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
 * 用户优惠券表
 * </p>
 *
 * @author HZW
 * @since 2022-07-19
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("eb_coupon_user")
@ApiModel(value="CouponUser对象", description="用户优惠券表")
public class CouponUser implements Serializable {

    private static final long serialVersionUID=1L;

    @ApiModelProperty(value = "id")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "优惠券id")
    private Integer couponId;

    @ApiModelProperty(value = "店铺id，平台为0")
    private Integer merId;

    @ApiModelProperty(value = "用户id")
    private Integer uid;

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

    @ApiModelProperty(value = "开始使用时间")
    private Date startTime;

    @ApiModelProperty(value = "过期时间")
    private Date endTime;

    @ApiModelProperty(value = "使用时间")
    private Date useTime;

    @ApiModelProperty(value = "状态（0：未使用，1：已使用, 2:已失效）")
    private Integer status;

    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    @ApiModelProperty(value = "更新时间")
    private Date updateTime;

    @TableField(exist = false)
    @ApiModelProperty(value = "是否可选")
    private Boolean isChoose = false;

    @TableField(exist = false)
    @ApiModelProperty(value = "是否选中")
    private Boolean isChecked = false;
}
