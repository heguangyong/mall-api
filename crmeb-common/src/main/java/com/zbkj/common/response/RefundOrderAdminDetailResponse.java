package com.zbkj.common.response;

import com.zbkj.common.model.order.RefundOrderStatus;
import com.zbkj.common.vo.RefundOrderDetailOrderInfoVo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 管理端退款订单详情响应对象
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
@ApiModel(value = "RefundOrderAdminDetailResponse对象", description = "管理端退款订单详情响应对象")
public class RefundOrderAdminDetailResponse implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "退款订单号")
    private String refundOrderNo;

    @ApiModelProperty(value = "售后类型：1-仅退款，2-退货退款")
    private Integer afterSalesType;

    @ApiModelProperty(value = "售后状态：0:待审核 1:商家拒绝 2：退款中 3:已退款 4:用户退货 5:商家待收货 6:已撤销")
    private Integer refundStatus;

    @ApiModelProperty(value = "退款金额")
    private BigDecimal refundPrice;

    @ApiModelProperty(value = "支付金额")
    private BigDecimal payPrice;

    /**
     * =======================================================================
     * 退款信息
     * =======================================================================
     */

    @ApiModelProperty(value = "商品名称")
    private String productName;

    @ApiModelProperty(value = "商品图片")
    private String image;

    @ApiModelProperty(value = "商品sku")
    private String sku;

    @ApiModelProperty(value = "商品单价")
    private BigDecimal price;

    @ApiModelProperty(value = "退款时间")
    private Date refundTime;

    @ApiModelProperty(value = "购买数量")
    private Integer payNum;

    @ApiModelProperty(value = "退还使用积分")
    private Integer refundUseIntegral;

    @ApiModelProperty(value = "扣除赠送积分")
    private Integer refundGainIntegral;

    @ApiModelProperty(value = "退一级返佣金额")
    private BigDecimal refundFirstBrokerageFee;

    @ApiModelProperty(value = "退二级返佣金额")
    private BigDecimal refundSecondBrokerageFee;

    @ApiModelProperty(value = "退运费金额")
    private BigDecimal refundFreightFee;

    @ApiModelProperty(value = "申请退款数量")
    private Integer applyRefundNum;

    @ApiModelProperty(value = "是否用户撤销")
    private Boolean isUserRevoke;

    @ApiModelProperty(value = "退款发起方：user-用户，merchant-商户")
    private String promoterType;

    /**
     * =======================================================================
     * 退款流程
     * =======================================================================
     */

    @ApiModelProperty(value = "退货类型：0-不退货 1-快递退回，2-到店退货")
    private Integer returnGoodsType;

    @ApiModelProperty(value = "退款原因")
    private String refundReasonWap;

    @ApiModelProperty(value = "退款图片")
    private String refundReasonWapImg;

    @ApiModelProperty(value = "退款用户说明")
    private String refundReasonWapExplain;

    @ApiModelProperty(value = "收货人姓名")
    private String receiver;

    @ApiModelProperty(value = "收货人电话")
    private String receiverPhone;

    @ApiModelProperty(value = "收货人详细地址")
    private String receiverAddressDetail;

    @ApiModelProperty(value = "物流公司名称")
    private String expressName;

    @ApiModelProperty(value = "运单号")
    private String trackingNumber;

    @ApiModelProperty(value = "用户联系电话")
    private String telephone;

    @ApiModelProperty(value = "拒绝退款说明")
    private String refundReason;

    @ApiModelProperty(value = "退款流程")
    private List<RefundOrderStatus> statusList;

    /**
     * =======================================================================
     * 订单信息
     * =======================================================================
     */
    @ApiModelProperty(value = "退款单之订单信息")
    private RefundOrderDetailOrderInfoVo orderInfoVo;

//    @ApiModelProperty(value = "商户ID")
//    private Integer merId;
//
//    @ApiModelProperty(value = "商户名称,平台端查询时有值")
//    private String merName;

    @ApiModelProperty(value = "商户备注")
    private String merRemark;

    @ApiModelProperty(value = "平台备注")
    private String platformRemark;
}
