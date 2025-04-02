package com.zbkj.common.vo.wxvedioshop.register;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 *
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
public class RegisterCheckAccessInfoItemVo {
    @ApiModelProperty(value = "上传商品并审核成功，0:未成功，1:成功")
    private Integer spu_audit_success;

    @ApiModelProperty(value = "物流接口调用成功，0:未成功，1:成功")
    private Integer send_delivery_success;

    @ApiModelProperty(value = "发起一笔订单并支付成功，0:未成功，1:成功")
    private Integer ec_order_success;

    @ApiModelProperty(value = "售后接口调用成功，0:未成功，1:成功")
    private Integer ec_after_sale_success;

    @ApiModelProperty(value = "商品接口调试完成，0:未完成，1:已完成")
    private Integer spu_audit_finished;

    @ApiModelProperty(value = "物流接口调试完成，0:未完成，1:已完成")
    private Integer send_delivery_finished;

    @ApiModelProperty(value = "订单接口调试完成，0:未完成，1:已完成")
    private Integer ec_order_finished;

    @ApiModelProperty(value = "售后接口调试完成，0:未完成，1:已完成")
    private Integer ec_after_sale_finished;

    @ApiModelProperty(value = "测试完成，0:未完成，1:已完成")
    private Integer test_api_finished;

    @ApiModelProperty(value = "发版完成，0:未完成，1:已完成")
    private Integer deploy_wxa_finished;

    @ApiModelProperty(value = "完成自定义组件全部任务 0:未完成 1:已完成")
    private Integer open_product_task_finished;
}
