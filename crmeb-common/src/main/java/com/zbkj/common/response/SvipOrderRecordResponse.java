package com.zbkj.common.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * Svip订单记录响应对象
 *
 * @author Han
 * @version 1.0.0
 * @Date 2024/5/21
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "SvipOrderRecordResponse", description = "Svip订单记录响应对象")
public class SvipOrderRecordResponse implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "订单号")
    private String orderNo;

    @ApiModelProperty(value = "付费会员卡名称")
    private String cardName;

    @ApiModelProperty(value = "0-试用，1-期限，2-永久")
    private Integer type;

    @ApiModelProperty(value = "期限天数")
    private Integer deadlineDay;

    @ApiModelProperty(value = "会员卡售价")
    private BigDecimal price;

    @ApiModelProperty(value = "赠送余额")
    private BigDecimal giftBalance;

    @ApiModelProperty(value = "支付方式:weixin,alipay,give")
    private String payType;

    @ApiModelProperty(value = "是否支付")
    private Boolean paid;

    @ApiModelProperty(value = "支付时间")
    private Date payTime;

}
