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
 * 积分订单分页列表搜索请求对象
 *
 * @author Han
 * @version 1.0.0
 * @Date 2024/8/27
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "IntegralOrderSearchRequest", description = "积分订单分页列表搜索请求对象")
public class IntegralOrderSearchRequest extends UserCommonSearchRequest implements Serializable {

    private static final long serialVersionUID = -3576847597383369976L;

    @ApiModelProperty(value = "订单号")
    private String orderNo;

    @ApiModelProperty(value = "创建时间区间")
    private String dateLimit;

    @ApiModelProperty(value = "订单状态（all 全部； 未支付 unPaid； 未发货 notShipped；待收货 spike；已收货 receiving;已完成 complete；已退款:refunded；已删除:deleted；待核销：awaitVerification；已取消:cancel")
    @NotBlank(message = "订单状态不能为空")
    @StringContains(limitValues = {"all", "unPaid", "notShipped", "spike", "receiving", "complete", "refunded", "deleted", "awaitVerification", "cancel"}, message = "未知的订单状态")
    private String status;
}
