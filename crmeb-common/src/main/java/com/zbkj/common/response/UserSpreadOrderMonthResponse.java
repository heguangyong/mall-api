package com.zbkj.common.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 用户推广订单月响应对象
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
@ApiModel(value = "UserSpreadOrderResponse对象", description = "用户推广订单月响应对象")
public class UserSpreadOrderMonthResponse implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "推广条数")
    private Integer count;

    @ApiModelProperty(value = "推广年月")
    private String time;

    @ApiModelProperty(value = "推广订单信息")
    private List<UserSpreadOrderResponse> child = new ArrayList<>();
}
