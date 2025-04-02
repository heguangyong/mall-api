package com.zbkj.common.model.order;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 退款订单操作记录表
 * </p>
 *
 * @author HZW
 * @since 2022-07-19
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("eb_refund_order_status")
@ApiModel(value="RefundOrderStatus对象", description="退款订单操作记录表")
public class RefundOrderStatus implements Serializable {

    private static final long serialVersionUID=1L;

    @ApiModelProperty(value = "订单号")
    private String orderNo;

    @ApiModelProperty(value = "操作类型：apply-申请退款，audit-商家审核，returning-商品退回，receiving-商家确认收货，refund-退款，compulsory-平台强制退款,revoke-撤销")
    private String changeType;

    @ApiModelProperty(value = "操作备注")
    private String changeMessage;

    @ApiModelProperty(value = "操作时间")
    private Date createTime;


}
