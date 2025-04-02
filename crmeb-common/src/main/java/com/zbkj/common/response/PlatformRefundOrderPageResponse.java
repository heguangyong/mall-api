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
 * 平台端退款订单分页列表响应对象
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
@ApiModel(value = "PlatformRefundOrderPageResponse对象", description = "平台端退款订单分页列表响应对象")
public class PlatformRefundOrderPageResponse implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "退款订单号")
    private String refundOrderNo;

    @ApiModelProperty(value = "主订单号")
    private String orderNo;

    @ApiModelProperty(value = "用户id")
    private Integer uid;

    @ApiModelProperty(value = "售后状态：0:待审核 1:商家拒绝 2：退款中 3:已退款 4:用户退货 5:商家待收货 6:已撤销")
    private Integer refundStatus;

    @ApiModelProperty(value = "退款金额")
    private BigDecimal refundPrice;

//    @ApiModelProperty(value = "商户备注")
//    private String merRemark;

    @ApiModelProperty(value = "平台备注")
    private String platformRemark;

    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    @ApiModelProperty(value = "售后类型：1-仅退款，2-退货退款")
    private Integer afterSalesType;

    @ApiModelProperty(value = "用户昵称")
    private String userNickName;

//    @ApiModelProperty(value = "商户名称")
//    private String merName;

    @ApiModelProperty(value = "是否强制退款")
    private Boolean isCompulsoryRefund;

    @ApiModelProperty(value = "退货类型：0-不退货 1-快递退回，2-到店退货")
    private Integer returnGoodsType;
}
