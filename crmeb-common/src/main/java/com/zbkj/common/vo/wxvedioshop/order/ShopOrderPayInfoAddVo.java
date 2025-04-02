package com.zbkj.common.vo.wxvedioshop.order;

import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 生成订单Vo对象
 *  +----------------------------------------------------------------------
 *  | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
 *  +----------------------------------------------------------------------
 *  | Copyright (c) 2016~2025 https://www.crmeb.com All rights reserved.
 *  +----------------------------------------------------------------------
 *  | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
 *  +----------------------------------------------------------------------
 *  | Author: CRMEB Team <admin@crmeb.com>
 *  +----------------------------------------------------------------------
 */
@Data
public class ShopOrderPayInfoAddVo {

    /** 支付方式（目前只有"微信支付"） */
    @TableField(value = "pay_method")
    private String payMethod;

    @TableField(value = "pay_method_type")
    @ApiModelProperty(value = "0: 微信支付, 1: 货到付款, 2: 商家会员储蓄卡（默认0）")
    private Integer payMethodType = 0;

    /** 预支付ID */
    @TableField(value = "prepay_id")
    @ApiModelProperty(value = "预支付ID")
    private String prepayId;

    /** 预付款时间（拿到prepay_id的时间） */
    @TableField(value = "prepay_time")
    @ApiModelProperty(value = "预付款时间（拿到prepay_id的时间）")
    private String prepayTime;
}
