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
 * 订单操作记录表
 * </p>
 *
 * @author HZW
 * @since 2022-07-19
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("eb_order_status")
@ApiModel(value="OrderStatus对象", description="订单操作记录表")
public class OrderStatus implements Serializable {

    private static final long serialVersionUID=1L;

    @ApiModelProperty(value = "订单号")
    private String orderNo;

    @ApiModelProperty(value = "操作类型:create-创建订单,pay-支付,express-快递,fictitious-虚拟发货,receipt-收货,evaluation-评价订单,fulfilled-完成,edit-编辑,cancel-取消,remove-删除订单")
    private String changeType;

    @ApiModelProperty(value = "操作备注")
    private String changeMessage;

    @ApiModelProperty(value = "操作时间")
    private Date createTime;


}
