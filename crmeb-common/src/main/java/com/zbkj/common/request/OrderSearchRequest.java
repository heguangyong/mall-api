package com.zbkj.common.request;

import com.zbkj.common.annotation.StringContains;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * 订单列表请求对象
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
@ApiModel(value = "OrderSearchRequest对象", description = "订单列表请求对象")
public class OrderSearchRequest extends UserCommonSearchRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "订单号")
    private String orderNo;

    @ApiModelProperty(value = "创建时间区间")
    private String dateLimit;

    @ApiModelProperty(value = "订单状态（all 全部； 未支付 unPaid； 未发货 notShipped；待收货 spike；已收货 receiving;已完成 complete；已退款:refunded；已删除:deleted；待核销：awaitVerification；已取消:cancel")
    @NotBlank(message = "订单状态不能为空")
    @StringContains(limitValues = {"all", "unPaid", "notShipped", "spike", "receiving", "complete", "refunded", "deleted", "awaitVerification", "cancel"}, message = "未知的订单状态")
    private String status;

    @ApiModelProperty(value = "商户id, 平台端查询值有效")
    private Integer merId;

    @ApiModelProperty(value = "订单类型:0-基础订单,1-秒杀订单,2-拼团订单")
    private Integer type;
}
