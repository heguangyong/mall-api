package com.zbkj.common.model.order;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * <p>
 * 退款单表
 * </p>
 *
 * @author HZW
 * @since 2022-09-19
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("eb_refund_order")
@ApiModel(value="RefundOrder对象", description="退款单表")
public class RefundOrder implements Serializable {

    private static final long serialVersionUID=1L;

    @ApiModelProperty(value = "订单ID")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "退款订单号")
    private String refundOrderNo;

    @ApiModelProperty(value = "主订单号")
    private String orderNo;

    @ApiModelProperty(value = "商户ID")
    private Integer merId;

    @ApiModelProperty(value = "用户id")
    private Integer uid;

    @ApiModelProperty(value = "收货人姓名")
    private String realName;

    @ApiModelProperty(value = "收货人电话")
    private String userPhone;

    @ApiModelProperty(value = "收货人详细地址")
    private String userAddress;

    @ApiModelProperty(value = "退款单商品总数")
    private Integer totalNum;

    @ApiModelProperty(value = "退款原因")
    private String refundReasonWap;

    @ApiModelProperty(value = "退款图片")
    private String refundReasonWapImg;

    @ApiModelProperty(value = "退款用户说明")
    private String refundReasonWapExplain;

    @ApiModelProperty(value = "是否整单退款")
    private Boolean isAll;

    @ApiModelProperty(value = "售后状态：0:待审核 1:商家拒绝 2：退款中 3:已退款 4:用户退货 5:商家待收货 6:已撤销")
    private Integer refundStatus;

    @ApiModelProperty(value = "退运费金额")
    private BigDecimal refundFreightFee;

    @ApiModelProperty(value = "拒绝退款说明")
    private String refundReason;

    @ApiModelProperty(value = "退款金额")
    private BigDecimal refundPrice;

    @ApiModelProperty(value = "商家退款金额")
    private BigDecimal merchantRefundPrice;

    @ApiModelProperty(value = "平台退款金额")
    private BigDecimal platformRefundPrice;

    @ApiModelProperty(value = "退一级返佣金额")
    private BigDecimal refundFirstBrokerageFee;

    @ApiModelProperty(value = "退二级返佣金额")
    private BigDecimal refundSecondBrokerageFee;

    @ApiModelProperty(value = "是否平台代扣佣金")
    private Boolean isReplace;

    @ApiModelProperty(value = "退还使用积分")
    private Integer refundUseIntegral;

    @ApiModelProperty(value = "扣除赠送积分")
    private Integer refundGainIntegral;

    @ApiModelProperty(value = "退款积分抵扣金额")
    private BigDecimal refundIntegralPrice;

    @ApiModelProperty(value = "退款时间")
    private Date refundTime;

    @ApiModelProperty(value = "退款渠道类型:weixin,alipay,yue")
    private String refundPayType;

    @ApiModelProperty(value = "商户备注")
    private String merRemark;

    @ApiModelProperty(value = "平台备注")
    private String platformRemark;

    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    @ApiModelProperty(value = "更新时间")
    private Date updateTime;

    @ApiModelProperty(value = "退还平台优惠券补贴金额")
    private BigDecimal refundPlatCouponPrice;

    @ApiModelProperty(value = "售后类型：1-仅退款，2-退货退款")
    private Integer afterSalesType;

    @ApiModelProperty(value = "退货类型：0-不退货 1-快递退回，2-到店退货")
    private Integer returnGoodsType;

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

    @ApiModelProperty(value = "是否强制退款")
    private Boolean isCompulsoryRefund;

    @ApiModelProperty(value = "强制操作账户ID")
    private Integer compulsoryAdminId;

    @ApiModelProperty(value = "是否用户撤销")
    private Boolean isUserRevoke;

    @ApiModelProperty(value = "退款发起方：user-用户，merchant-商户")
    private String promoterType;

    @ApiModelProperty(value = "第三方退款订单号")
    private String outRefundNo;
}
