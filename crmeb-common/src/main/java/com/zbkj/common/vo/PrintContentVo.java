package com.zbkj.common.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.List;

/**
 * 打印内容Vo对象
 *
 * @author Han
 * @version 1.0.0
 * @Date 2025/2/18
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="PrintContentVo", description = "打印内容Vo对象")
public class PrintContentVo implements Serializable {

    private static final long serialVersionUID = -7545342385806043120L;

    @ApiModelProperty(value = "小票头部:1-商家名称,0-不展示")
    private Integer smallTickerHeader;

    @ApiModelProperty(value = "配送信息:1-配送信息,0-不展示")
    private Integer deliveryInfo;

    @ApiModelProperty(value = "买家备注:1-买家备注,0-不展示")
    private Integer buyerRemark;

    @ApiModelProperty(value = "商品信息:1-商品基础信息,0-不展示")
    private Integer productInfo;

    @ApiModelProperty(value = "运费信息:1-运费,0-不展示")
    private Integer freightInfo;

    @ApiModelProperty(value = "优惠信息:1-优惠总计,0-不展示")
    private Integer discountInfo;

    @ApiModelProperty(value = "支付信息:1-支付方式，2-实收金额")
    private List<Integer> payInfo;

    @ApiModelProperty(value = "其他订单信息:1-订单编号，2-下单时间，3-支付时间，4-打印时间")
    private List<Integer> orderInfo;

    @ApiModelProperty(value = "推广二维码")
    private String codeUrl;

    @ApiModelProperty(value = "底部公告")
    private String bottomNotice;

    @ApiModelProperty(value = "推广二维码开关:1-展示,0-不展示")
    private Integer codeUrlSwitch;

    @ApiModelProperty(value = "底部公告开关:1-展示,0-不展示")
    private Integer bottomNoticeSwitch;
}
