package com.zbkj.common.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Range;

/**
 * 优惠券领取记录搜索请求对象
 *
 * @author Han
 * @version 1.0.0
 * @Date 2024/9/2
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "CouponReceiveRecordSearchRequest", description = "优惠券领取记录搜索请求对象")
public class CouponReceiveRecordSearchRequest extends UserCommonSearchRequest {

    private static final long serialVersionUID = 304057503832845241L;

    @ApiModelProperty(value = "优惠券名称")
    private String name;

    @ApiModelProperty(value = "状态（0：未使用，1：已使用, 2:已过期/已失效）")
    @Range(min = 0, max = 2, message = "未知的状态")
    private Integer status;
}
