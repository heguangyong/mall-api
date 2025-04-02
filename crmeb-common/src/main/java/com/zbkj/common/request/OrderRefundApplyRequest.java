package com.zbkj.common.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

/**
 * 订单申请退款请求对象
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
@ApiModel(value = "OrderRefundApplyRequest对象", description = "订单申请退款请求对象")
public class OrderRefundApplyRequest {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "订单号", required = true)
    @NotEmpty(message = "订单号不能为空")
    private String orderNo;

    @ApiModelProperty(value = "订单详情id", required = true)
    @NotNull(message = "订单详情id不能为空")
    private Integer orderDetailId;

    @ApiModelProperty(value = "售后类型：1-仅退款，2-退货退款", required = true)
    @NotNull(message = "请选择售后类型")
    @Range(min = 1, max = 2, message = "未知的售后类型")
    private Integer afterSalesType;

    @ApiModelProperty(value = "退货类型：0-不退货 1-快递退回，2-到店退货", required = true)
    @NotNull(message = "请选择退货类型")
    @Range(min = 0, max = 2, message = "未知的退货类型")
    private Integer returnGoodsType;

    @ApiModelProperty(value = "退款数量", required = true)
    @NotNull(message = "退款数量不能为空")
    private Integer num;

    @ApiModelProperty(value = "退款原因", required = true)
    @NotEmpty(message = "退款原因必须填写")
    @Length(max = 255, message = "退款原因不能超过255个字符")
    private String text;

    @ApiModelProperty(value = "退款凭证图片(多个图片请用,(英文逗号)隔开)")
    private String reasonImage;

    @ApiModelProperty(value = "备注说明")
    private String explain;
}
