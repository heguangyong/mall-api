package com.zbkj.common.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.Max;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;

/**
 * 优惠券编辑请求对象
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
@ApiModel(value = "CouponUpdateRequest对象", description = "优惠券编辑请求对象")
public class CouponUpdateRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "优惠券表ID")
    @NotNull(message = "请选择优惠券")
    private Integer id;

    @ApiModelProperty(value = "优惠券名称", required = true)
    @NotBlank(message = "请填写优惠券名称")
    @Length(max = 20, message = "优惠券名称长度不能超过20个字符")
    private String name;

    @ApiModelProperty(value = "是否限量, 默认0 否， 1是", required = true)
    @NotNull(message = "请设置是否限量")
    private Boolean isLimited;

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

    @ApiModelProperty(value = "关联数据，英文逗号分隔，商品券，品类券，品牌券，跨店券必填")
    private String linkedData;

    @ApiModelProperty(value = "是否可重复领取")
    private Boolean isRepeated;

    @ApiModelProperty(value = "添加发放数量")
    @Range(min = 0, max = 999999, message = "添加发放数量范围为0~999999")
    private Integer num;
}
