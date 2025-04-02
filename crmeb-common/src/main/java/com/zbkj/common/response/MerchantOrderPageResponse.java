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
 * 商户端订单分页列表响应对象
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
@ApiModel(value = "MerchantOrderPageResponse对象", description = "商户端订单分页列表响应对象")
public class MerchantOrderPageResponse implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "订单号")
    private String orderNo;

    @ApiModelProperty(value = "实际支付金额")
    private BigDecimal payPrice;

    @ApiModelProperty(value = "支付方式:weixin,alipay,yue")
    private String payType;

    @ApiModelProperty(value = "订单状态（0：待支付，1：待发货,2：部分发货， 3：待核销，4：待收货,5：已收货,6：已完成，9：已取消）")
    private Integer status;

    @ApiModelProperty(value = "退款状态：0 未退款 1 申请中 2 部分退款 3 已退款")
    private Integer refundStatus;

    @ApiModelProperty(value = "取消状态：0-未取消，1-系统取消，2-用户取消")
    private Integer cancelStatus;

    @ApiModelProperty(value = "用户是否删除")
    private Boolean isUserDel;

    @ApiModelProperty(value = "订单类型:0-基础订单,1-秒杀订单,2-拼团订单")
    private Integer type;

    @ApiModelProperty(value = "订单二级类型:0-普通订单，1-积分订单，2-虚拟订单，4-视频号订单，5-云盘订单，6-卡密订单")
    private Integer secondType;

    @ApiModelProperty(value = "订单等级:0-主订单，1-商户订单，2-商户子订单")
    private Integer level;

    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    @ApiModelProperty(value = "用户昵称")
    private String nickName;

    @ApiModelProperty(value = "用户是否注销")
    private Boolean isLogoff;

    @ApiModelProperty(value = "用户是否退款标志")
    private Boolean userRefundSign = false;

    @ApiModelProperty(value = "订单商品总数")
    private Integer totalNum;

    @ApiModelProperty(value = "商户备注")
    private String merchantRemark;

    @ApiModelProperty(value = "订单详情列表")
    private List<OrderInfoFrontDataResponse> infoResponseList;

    @ApiModelProperty(value = "拼团订单状态：0=进行中，10=已成功，-1=已失败, 99非拼团订单")
    private Integer groupBuyRecordStatus;

    @ApiModelProperty(value = "订单商品已退款总数")
    private Integer refundNum = 0;
}
