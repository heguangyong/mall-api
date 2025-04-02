package com.zbkj.common.response;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.zbkj.common.model.order.OrderInvoiceDetail;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * <p>
 * 发货单移动端响应对象
 * </p>
 *
 * @author HZW
 * @since 2022-10-31
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "OrderInvoiceFrontResponse对象", description = "发货单移动端响应对象")
public class OrderInvoiceFrontResponse implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "包裹数量")
    private Integer num = 0;

    @ApiModelProperty(value = "已发数量")
    private Integer deliveryNum = 0;

    @ApiModelProperty(value = "发货单列表")
    List<OrderInvoiceResponse> invoiceList;
}
