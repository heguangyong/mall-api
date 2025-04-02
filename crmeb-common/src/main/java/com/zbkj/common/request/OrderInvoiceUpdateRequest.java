package com.zbkj.common.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 订单发货单修改请求对象
 *
 * @author Han
 * @version 1.0.0
 * @Date 2024/7/4
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "OrderInvoiceUpdateRequest", description = "订单发货单修改请求对象")
public class OrderInvoiceUpdateRequest implements Serializable {

    private static final long serialVersionUID = -6729052082342498690L;

    @ApiModelProperty(value = "发货单ID")
    @NotNull(message = "请选择发货单")
    private Integer id;

    @ApiModelProperty(value = "快递公司编码")
    private String expressCode;

    @ApiModelProperty(value = "快递单号")
    private String expressNumber;

    @ApiModelProperty(value = "发货记录类型，1快递发货、2电子面单")
    private String expressRecordType;

    @ApiModelProperty(value = "发货备注")
    @Length(max = 250, message = "备注最多250个字符")
    private String deliveryMark;

    @ApiModelProperty(value = "配送人员")
    private String deliveryCarrier;

    @ApiModelProperty(value = "配送人员手机号")
    private String carrierPhone;
}
