package com.zbkj.common.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * 订单状态数量响应对象
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
@ApiModel(value = "OrderCountItemResponse对象", description = "订单状态数量响应对象")
public class OrderCountItemResponse implements Serializable {

    private static final long serialVersionUID = -8605913636959651047L;

    @ApiModelProperty(value = "总数")
    private Integer all = 0;

    @ApiModelProperty(value = "未支付")
    private Integer unPaid = 0;

    @ApiModelProperty(value = "未发货")
    private Integer notShipped = 0;

    @ApiModelProperty(value = "待收货")
    private Integer spike = 0;

    @ApiModelProperty(value = "已收货")
    private Integer receiving = 0;

    @ApiModelProperty(value = "交易完成")
    private Integer complete = 0;

    @ApiModelProperty(value = "已退款")
    private Integer refunded = 0;

    @ApiModelProperty(value = "已删除")
    private Integer deleted = 0;

    @ApiModelProperty(value = "待核销")
    private Integer verification = 0;

    @ApiModelProperty(value = "已取消")
    private Integer cancel = 0;
}
