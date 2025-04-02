package com.zbkj.common.response;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * 移动端秒杀时间响应对象
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
@ApiModel(value = "SeckillFrontTimeResponse对象", description = "移动端秒杀时间响应对象")
public class SeckillFrontTimeResponse implements Serializable {

    private static final long serialVersionUID = 3285713110515203543L;

    @ApiModelProperty(value = "秒杀日期")
    private String date;

    @ApiModelProperty(value = "秒杀开始时间")
    private String startTime;

    @ApiModelProperty(value = "秒杀结束时间")
    private String endTime;

    @ApiModelProperty(value = "秒杀开始时间(时间戳)")
    private Long startTimeStamp;

    @ApiModelProperty(value = "秒杀结束时间(时间戳)")
    private Long endTimeStamp;

    @ApiModelProperty(value = "状态 0=已结束 1=抢购中 2=即将开始 3=明日预告")
    private Integer status;

}
