package com.zbkj.common.vo.wxvedioshop.order;

import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotEmpty;
import java.util.Date;

/**
 * 生成订单Vo对象
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
public class ShopOrderAddVo {

    /** 创建时间 */
    @TableField(value = "create_time")
    @ApiModelProperty(value = "创建时间，yyyy-MM-dd HH:mm:ss，与微信服务器不得超过5秒偏差")
    @NotEmpty
    private String createTime;

    /** 商家自定义订单ID */
    @ApiModelProperty(value = "商家自定义订单ID(字符集包括大小写字母数字，长度小于128个字符）")
    @NotEmpty
    @Length(max = 128)
    @TableField(value = "out_order_id")
    private String outOrderId;

    /** 用户的openid */
    @ApiModelProperty(value = "用户的openid")
    @NotEmpty
    private String openid;

    /** 商家小程序该订单的页面path，用于微信侧订单中心跳转 */
    @ApiModelProperty(value = "订单详情页路径")
    @NotEmpty
    private String path;

    /** 下单时小程序的场景值，可通getLaunchOptionsSync或onLaunch/onShow拿到 */
//    private Integer scene;

    /** 订单详情 */
    @TableField(value = "order_detail")
    @ApiModelProperty(value = "订单详细数据")
    private ShopOrderDetailAddVo orderDetail;

    @ApiModelProperty(value = "配送信息 ")
    @TableField(value = "delivery_detail")
    @NotEmpty
    private ShopOrderDeliveryDetailAddVo deliveryDetail;

    /** 地址详情 */
    @TableField(value = "address_info")
    @ApiModelProperty(value = "地址信息")
    private ShopOrderAddressInfoAddVo addressInfo;

    @ApiModelProperty(value = "订单类型：0，普通单，1，二级商户单")
    @NotEmpty
    private Integer fund_type;

    @ApiModelProperty(value = "unix秒级时间戳，订单超时时间，取值：[15min, 1d]")
    @NotEmpty
    private Integer expire_time;

    @ApiModelProperty(value = "取值范围，[7，3 * 365]，单位：天")
    private Integer aftersale_duration;

    @ApiModelProperty(value = "会影响主播归因、分享员归因等，从下单前置检查获取")
    @NotEmpty
    private String trace_id;

    /** 用户id */
    private Integer outUserId;

    /** 商户id */
    private Integer merId;
}
