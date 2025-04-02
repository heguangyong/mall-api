package com.zbkj.common.vo.wxvedioshop.order;

import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;

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
public class ShopOrderDeliveryDetailAddVo {

    /** 1: 正常快递, 2: 无需快递, 3: 线下配送, 4: 用户自提 （ 默认1） */
    @TableField(value = "delivery_type")
    @ApiModelProperty(value = "1: 正常快递, 2: 无需快递, 3: 线下配送, 4: 用户自提，视频号场景目前只支持 1，正常快递")
    @NotEmpty
    private Integer deliveryType = 1;
}
