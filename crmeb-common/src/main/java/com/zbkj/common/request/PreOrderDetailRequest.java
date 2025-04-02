package com.zbkj.common.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * 预下单详情请求对象
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
@ApiModel(value = "PreOrderRequest对象", description = "预下单详情请求对象")
public class PreOrderDetailRequest {

    @ApiModelProperty(value = "购物车编号，购物车预下单时必填")
    private Integer shoppingCartId;

    @ApiModelProperty(value = "商品id（立即购买、活动购买必填）")
    private Integer productId;

    @ApiModelProperty(value = "商品规格属性id（立即购买、活动购买必填）")
    private Integer attrValueId;

    @ApiModelProperty(value = "商品数量（立即购买、活动购买必填）")
    private Integer productNum;

    @ApiModelProperty(value = "订单编号（再次购买必填）")
    private String orderNo;

    @ApiModelProperty(value = "拼团活动id（拼团下单时必填）")
    private Integer groupBuyActivityId;

    @ApiModelProperty(value = "拼团记录id，营销类型2=拼团 时必填 0=开团 实际recordId=拼团")
    private Integer groupBuyRecordId = 0;

}
