package com.zbkj.common.response.groupbuy;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 拼团开团记录详情成员响应对象
 *
 * @author Han
 * @version 1.0.0
 * @Date 2024/9/26
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "GroupBuyRecordDetailMemberResponse", description = "拼团开团记录详情成员响应对象")
public class GroupBuyRecordDetailMemberResponse implements Serializable {

    private static final long serialVersionUID = -6375894771668563418L;

    @ApiModelProperty(value = "成员记录id")
    private Integer id;

    @ApiModelProperty(value = "成员头像")
    private String memberAvatar;

    @ApiModelProperty(value = "成员ID")
    private Integer memberId;

    @ApiModelProperty(value = "成员昵称")
    private String memberNickname;

    @ApiModelProperty(value = "订单编号")
    private String orderNo;

    @ApiModelProperty(value = "订单详情sku")
    private String orderDetailSku;

    @ApiModelProperty(value = "购买数量")
    private Integer payNum;

    @ApiModelProperty(value = "实付金额")
    private BigDecimal payPrice;

    @ApiModelProperty(value = "退款数量")
    private Integer refundNum;

    @ApiModelProperty(value = "是否为 团长 0=否 1=是")
    private Integer isLeader;

    @ApiModelProperty(value = "创建时间=支付时间")
    private Date createTime;
}
