package com.zbkj.common.vo.wxvedioshop;

import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonProperty;
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
public class ShopOrderAddResultVo {

    /** 交易组件平台订单ID */
    @TableField(value = "order_id")
    @ApiModelProperty(value = "交易组件平台订单ID，类型取决于stringify_64bits_number参数")
    @JsonProperty( value= "order_id")
    private Long orderId;

    /** 交易组件平台订单ID */
    @TableField(value = "out_order_id")
    @ApiModelProperty(value = "交易组件第三方订单ID")
    @JsonProperty(value = "out_order_id")
    private String outOrderId;

    /** 拉起收银台的ticket */
    @JsonProperty(value = "ticket")
    private String ticket;

    /** ticket有效截止时间 */
    @TableField(value = "ticket_expire_time")
    @JsonProperty(value = "ticket_expire_time")
    private String ticketExpireTime;

    /** 订单最终价格（单位：分） */
    @TableField(value = "final_price")
    @JsonProperty(value = "final_price")
    private Long finalPrice;
}
