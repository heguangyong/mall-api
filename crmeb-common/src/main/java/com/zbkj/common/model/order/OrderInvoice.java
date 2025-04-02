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
import java.util.Date;

/**
 * <p>
 * 订单发货单表
 * </p>
 *
 * @author HZW
 * @since 2022-10-31
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("eb_order_invoice")
@ApiModel(value = "OrderInvoice对象", description = "订单发货单表")
public class OrderInvoice implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "ID")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "订单号")
    private String orderNo;

    @ApiModelProperty(value = "商户ID")
    private Integer merId;

    @ApiModelProperty(value = "用户id")
    private Integer uid;

    @ApiModelProperty(value = "运单号")
    private String trackingNumber;

    @ApiModelProperty(value = "快递名称")
    private String expressName;

    @ApiModelProperty(value = "快递公司简称")
    private String expressCode;

    @ApiModelProperty(value = "发货商品总数")
    private Integer totalNum;

    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    @ApiModelProperty(value = "更新时间")
    private Date updateTime;

    @ApiModelProperty(value = "发货备注")
    private String deliveryMark;

    @ApiModelProperty(value = "配送人员")
    private String deliveryCarrier;

    @ApiModelProperty(value = "配送人员手机号")
    private String carrierPhone;

    @ApiModelProperty(value = "发货类型：express-快递,merchant-商家配送，noNeed-无需发货")
    private String deliveryType;

    @ApiModelProperty(value = "发货记录类型，1快递发货、2电子面单")
    private String expressRecordType;
}
