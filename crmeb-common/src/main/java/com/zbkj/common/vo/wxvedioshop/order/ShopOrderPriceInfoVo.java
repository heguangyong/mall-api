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
public class ShopOrderPriceInfoVo {

    /** 该订单最终的金额（单位：分） */
    @TableField(value = "order_price")
    @ApiModelProperty(value = "订单总价")
    private Long orderPrice;

    /** 运费（单位：分） */
    @ApiModelProperty(value = "运费")
    private Long freight;

    /** 优惠金额（单位：分） */
    @TableField(value = "discounted_price")
    @ApiModelProperty(value = "折扣费用")
    private Long discountedPrice;

    /** 附加金额（单位：分） */
    @TableField(value = "additional_price")
    @ApiModelProperty(value = "其他费用")
    private Long additionalPrice;

    /** 附加金额备注 */
    @TableField(value = "additional_remarks")
    @ApiModelProperty(value = "其他费用说明")
    private String additional_remarks;
}
