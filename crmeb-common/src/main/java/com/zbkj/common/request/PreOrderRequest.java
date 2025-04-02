package com.zbkj.common.request;

import com.zbkj.common.annotation.StringContains;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.util.List;

/**
 * 预下单请求对象
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
@ApiModel(value = "PreOrderRequest对象", description = "预下单请求对象")
public class PreOrderRequest {

    @ApiModelProperty(value = "预下单类型（“shoppingCart”:购物车下单,“buyNow”:立即购买,“video”: 视频号商品下单,“seckill”:秒杀下单,“group”:拼团下单）", required = true)
    @NotBlank(message = "预下单类型不能为空")
    @StringContains(limitValues = {"shoppingCart", "buyNow", "video", "seckill","group"}, message = "未知的预下单类型")
    private String preOrderType;

    @ApiModelProperty(value = "下单详情列表", required = true)
    @NotEmpty(message = "下单详情列表不能为空")
    private List<PreOrderDetailRequest> orderDetails;

}
