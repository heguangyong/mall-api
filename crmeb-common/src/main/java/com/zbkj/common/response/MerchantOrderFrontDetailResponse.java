package com.zbkj.common.response;

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
 * 订单移动端商户详情响应对象
 *  +----------------------------------------------------------------------
 *  | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
 *  +----------------------------------------------------------------------
 *  | Copyright (c) 2016~2025 https://www.crmeb.com All rights reserved.
 *  +----------------------------------------------------------------------
 *  | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
 *  +----------------------------------------------------------------------
 *  | Author: CRMEB Team <admin@crmeb.com>
 *  +----------------------------------------------------------------------
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="MerchantOrderFrontDetailResponse对象", description="订单移动端商户详情响应对象")
public class MerchantOrderFrontDetailResponse implements Serializable {

    private static final long serialVersionUID = -4324222121352855551L;

    @ApiModelProperty(value = "商户ID")
    private Integer merId;

    @ApiModelProperty(value = "收货人姓名")
    private String realName;

    @ApiModelProperty(value = "收货人电话")
    private String userPhone;

    @ApiModelProperty(value = "收货详细地址")
    private String userAddress;

    @ApiModelProperty(value = "订单商品总数")
    private Integer totalNum;

    @ApiModelProperty(value = "商品总价")
    private BigDecimal proTotalPrice;

    @ApiModelProperty(value = "邮费")
    private BigDecimal totalPostage;

    @ApiModelProperty(value = "订单总价")
    private BigDecimal totalPrice;

    @ApiModelProperty(value = "实际支付金额")
    private BigDecimal payPrice;

    @ApiModelProperty(value = "支付邮费")
    private BigDecimal payPostage;

    @ApiModelProperty(value = "使用积分")
    private Integer useIntegral;

    @ApiModelProperty(value = "积分抵扣金额")
    private BigDecimal integralPrice;

    @ApiModelProperty(value = "优惠券id")
    private Integer couponId;

    @ApiModelProperty(value = "优惠券金额")
    private BigDecimal couponPrice;

    @ApiModelProperty(value = "赠送积分")
    private Integer gainIntegral;

    @ApiModelProperty(value = "用户备注")
    private String userRemark;

    @ApiModelProperty(value = "配送方式 1=快递 ，2=门店自提，3-虚拟发货")
    private Integer shippingType;

    @ApiModelProperty(value = "核销码")
    private String verifyCode;

    @ApiModelProperty(value = "发货类型：express-快递,fictitious-虚拟发货,merchant-商家配送，noNeed-无需发货")
    private String deliveryType;

    @ApiModelProperty(value = "商户名称")
    private String merName;

    @ApiModelProperty(value = "商户手机号")
    private String merPhone;

    @ApiModelProperty(value = "省")
    private String merProvince;

    @ApiModelProperty(value = "市")
    private String merCity;

    @ApiModelProperty(value = "区")
    private String merDistrict;

    @ApiModelProperty(value = "商户详细地址")
    private String merAddressDetail;

    @ApiModelProperty(value = "纬度")
    private String merLatitude;

    @ApiModelProperty(value = "经度")
    private String merLongitude;

    @ApiModelProperty(value = "订单详情")
    private List<OrderInfoFrontDataResponse> orderInfoList;

    @ApiModelProperty(value = "是否自营：0-非自营，1-自营")
    private Boolean isSelf;

    @ApiModelProperty(value = "svip优惠金额")
    private BigDecimal svipDiscountPrice;

    @ApiModelProperty(value = "是否拆分发货")
    private Boolean isSplitDelivery;
}
