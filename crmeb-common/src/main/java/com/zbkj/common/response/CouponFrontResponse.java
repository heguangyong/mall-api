package com.zbkj.common.response;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 移动端优惠券响应对象
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
@ApiModel(value="CouponFrontResponse对象", description="移动端优惠券响应对象")
public class CouponFrontResponse implements Serializable {

    private static final long serialVersionUID=1L;

    @ApiModelProperty(value = "优惠券表ID")
    private Integer id;

    @ApiModelProperty(value = "商户ID")
    private Integer merId;

    @ApiModelProperty(value = "优惠券名称")
    private String name;

    @ApiModelProperty(value = "发行方，1-平台，2-商户")
    private Integer publisher;

    @ApiModelProperty(value = "类别 1-商家券, 2-商品券, 3-通用券，4-品类券，5-品牌券，6-跨店券")
    private Integer category;

    @ApiModelProperty(value = "优惠券类型 1-满减券,2-折扣券")
    private Integer couponType;

    @ApiModelProperty(value = "兑换的优惠券面值")
    private Long money;

    @ApiModelProperty(value = "折扣")
    private Integer discount;

    @ApiModelProperty(value = "最低消费，0代表不限制")
    private Long minPrice;

    @ApiModelProperty(value = "是否限量, 默认0 不限量， 1限量")
    private Boolean isLimited;

    @ApiModelProperty(value = "剩余数量")
    private Integer lastTotal;

    @ApiModelProperty(value = "可领取开始时间")
    private Date receiveStartTime;

    @ApiModelProperty(value = "可领取结束时间")
    private Date receiveEndTime;

    @ApiModelProperty(value = "是否固定使用时间, 默认0 否， 1是")
    private Boolean isFixedTime;

    @ApiModelProperty(value = "天数")
    private Integer day;

    @ApiModelProperty(value = "是否已领取未使用")
    private Boolean isUse = false;

    @ApiModelProperty(value = "可使用时间范围 开始时间字符串")
    private String useStartTimeStr;

    @ApiModelProperty(value = "可使用时间范围 结束时间字符串")
    private String useEndTimeStr;

}
