package com.zbkj.common.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;

/**
 * 添加优惠卷请求对象
 * +----------------------------------------------------------------------
 * | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
 * +----------------------------------------------------------------------
 * | Copyright (c) 2016~2025 https://www.crmeb.com All rights reserved.
 * +----------------------------------------------------------------------
 * | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
 * +----------------------------------------------------------------------
 * | Author: CRMEB Team <admin@crmeb.com>
 * +----------------------------------------------------------------------
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "CouponAddRequest对象", description = "添加优惠卷请求对象")
public class CouponAddRequest implements Serializable {

    private static final long serialVersionUID = -7053809553211774431L;

    @ApiModelProperty(value = "优惠券名称", required = true)
    @NotBlank(message = "请填写优惠券名称")
    @Length(max = 20, message = "优惠券名称长度不能超过20个字符")
    private String name;

    @ApiModelProperty(value = "类别 1-商家券, 2-商品券, 3-通用券，4-品类券，5-品牌券，6-跨店券", required = true)
    @Range(min = 1, max = 6, message = "未知的优惠券类别")
    private Integer category;

    @ApiModelProperty(value = "领取类型 1-手动领取,2-商品赠送券,3-平台活动发放", required = true)
    @Range(min = 1, max = 3, message = "未知的优惠券领取方式")
    private Integer receiveType;

    @ApiModelProperty(value = "优惠金额", required = true)
    @NotNull(message = "请填写优惠金额")
    @Range(min = 1, max = 999999, message = "优惠金额范围为1~999999")
    private Long money;

    @ApiModelProperty(value = "最低消费，0代表不限制", required = true)
    @NotNull(message = "最低消费不能为空")
    @Min(value = 0, message = "最小值不能小于0")
    @Max(value = 999999, message = "最大值不能大于999999")
    private Long minPrice;

    @ApiModelProperty(value = "是否限量, 默认0 否， 1是", required = true)
    @NotNull(message = "请设置是否限量")
    private Boolean isLimited;

    @ApiModelProperty(value = "发放总数")
    @Range(min = 1, max = 999999, message = "发布数量范围为1~999999")
    private Integer total;

    @ApiModelProperty(value = "领取是否限时, 默认0-不限时，1-限时", required = true)
    @NotNull(message = "请选择领取是否限时")
    private Boolean isTimeReceive;

    @ApiModelProperty(value = "可领取开始时间")
    private Date receiveStartTime;

    @ApiModelProperty(value = "可领取结束时间")
    private Date receiveEndTime;

    @ApiModelProperty(value = "请设置是否固定使用时间, 默认0 否， 1是", required = true)
    @NotNull(message = "请设置是否固定使用时间")
    private Boolean isFixedTime;

    @ApiModelProperty(value = "可使用时间范围 开始时间")
    private Date useStartTime;

    @ApiModelProperty(value = "可使用时间范围 结束时间")
    private Date useEndTime;

    @ApiModelProperty(value = "天数")
    @Max(value = 999, message = "天数不能超过999天")
    private Integer day;

    @ApiModelProperty(value = "状态（0：关闭，1：开启）", required = true)
    @NotNull(message = "优惠券状态不能为空")
    private Boolean status;

    @ApiModelProperty(value = "关联数据，英文逗号分隔，商品券，品类券，品牌券，跨店券必填")
    private String linkedData;

    @ApiModelProperty(value = "是否可重复领取")
    private Boolean isRepeated;
}
