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
 * 退款订单详情表
 * </p>
 *
 * @author HZW
 * @since 2022-09-19
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("eb_refund_order_info")
@ApiModel(value="RefundOrderInfo对象", description="退款订单详情表")
public class RefundOrderInfo implements Serializable {

    private static final long serialVersionUID=1L;

    @ApiModelProperty(value = "主键")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "退款订单号")
    private String refundOrderNo;

    @ApiModelProperty(value = "商户ID")
    private Integer merId;

    @ApiModelProperty(value = "订单商品表ID")
    private Integer orderDetailId;

    @ApiModelProperty(value = "商品ID")
    private Integer productId;

    @ApiModelProperty(value = "商品名称")
    private String productName;

    @ApiModelProperty(value = "商品图片")
    private String image;

    @ApiModelProperty(value = "商品规格值 ID")
    private Integer attrValueId;

    @ApiModelProperty(value = "商品sku")
    private String sku;

    @ApiModelProperty(value = "商品单价")
    private BigDecimal price;

    @ApiModelProperty(value = "支付金额")
    private BigDecimal payPrice;

    @ApiModelProperty(value = "购买数量")
    private Integer payNum;

    @ApiModelProperty(value = "商品类型:0-普通，1-秒杀，2-砍价，3-拼团，4-视频号")
    private Integer productType;

    @ApiModelProperty(value = "申请退款数量")
    private Integer applyRefundNum;

    @ApiModelProperty(value = "退款金额")
    private BigDecimal refundPrice;

    @ApiModelProperty(value = "商家退款金额")
    private BigDecimal MerchantRefundPrice;

    @ApiModelProperty(value = "平台退款金额")
    private BigDecimal platformRefundPrice;

    @ApiModelProperty(value = "退还使用积分")
    private Integer refundUseIntegral;

    @ApiModelProperty(value = "退款积分抵扣金额")
    private BigDecimal refundIntegralPrice;

    @ApiModelProperty(value = "扣除赠送积分")
    private Integer refundGainIntegral;

    @ApiModelProperty(value = "退一级返佣金额")
    private BigDecimal refundFirstBrokerageFee;

    @ApiModelProperty(value = "退二级返佣金额")
    private BigDecimal refundSecondBrokerageFee;

    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    @ApiModelProperty(value = "更新时间")
    private Date updateTime;

    @ApiModelProperty(value = "退还平台优惠券补贴金额")
    private BigDecimal refundPlatCouponPrice;

    @ApiModelProperty(value = "退运费金额")
    private BigDecimal refundFreightFee;
}
