package com.zbkj.common.request;

import com.zbkj.common.annotation.StringContains;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * 订单发货对象
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
@ApiModel(value = "OrderSendRequest对象", description = "订单发货对象")
public class OrderSendRequest {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "订单编号", required = true)
    @NotBlank(message = "订单编号不能为空")
    private String orderNo;

    @ApiModelProperty(value = "发货类型：express-快递,merchant-商家配送，noNeed-无需发货", allowableValues = "range[express,merchant,noNeed]", required = true)
    @NotBlank(message = "请选择发货类型")
    @StringContains(limitValues = {"express","merchant","noNeed"}, message = "未知的发货类型")
    private String deliveryType;

    @ApiModelProperty(value = "快递公司编码")
    private String expressCode;

    @ApiModelProperty(value = "快递单号")
    private String expressNumber;

    @ApiModelProperty(value = "是否拆单发货", required = true)
    @NotNull(message = "是否拆单发货不能为空")
    private Boolean isSplit;

    @ApiModelProperty(value = "拆单发货详情列表,拆单发货时必传")
    @Valid
    private List<SplitOrderSendDetailRequest> detailList;

    @ApiModelProperty(value = "发货备注")
    @Length(max = 250, message = "备注最多250个字符")
    private String deliveryMark;

    @ApiModelProperty(value = "配送人员")
    private String deliveryCarrier;

    @ApiModelProperty(value = "配送人员手机号")
    private String carrierPhone;

    @ApiModelProperty(value = "发货记录类型，1快递发货、2电子面单")
    private String expressRecordType;

    @ApiModelProperty(value = "电子面单模板,电子面单必传")
    private String expressTempId;

    @ApiModelProperty(value = "寄件人姓名,电子面单必传")
    private String toName;

    @ApiModelProperty(value = "寄件人电话,电子面单必传")
    private String toTel;

    @ApiModelProperty(value = "寄件人地址,电子面单必传")
    private String toAddr;

    @ApiModelProperty(value = "送货人姓名,送货类型必传")
    private String deliveryName;

    @ApiModelProperty(value = "送货人电话,送货类型必传")
    private String deliveryTel;
}
