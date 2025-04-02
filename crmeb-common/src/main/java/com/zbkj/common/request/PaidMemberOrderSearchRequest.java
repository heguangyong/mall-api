package com.zbkj.common.request;

import com.zbkj.common.annotation.StringContains;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * 付费会员订单列表搜索请求对象
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
@ApiModel(value = "PaidMemberOrderSearchRequest", description = "付费会员订单列表搜索请求对象")
public class PaidMemberOrderSearchRequest extends UserCommonSearchRequest {

    private static final long serialVersionUID = 5061348062942389227L;

    @ApiModelProperty(value = "订单号")
    private String orderNo;

    @ApiModelProperty(value = "会员卡名")
    private String cardName;

    @ApiModelProperty(value = "创建时间区间")
    private String dateLimit;

    @ApiModelProperty(value = "支付方式:weixin,alipay,give,yue")
    @StringContains(limitValues = {"weixin","alipay","give","yue"}, message = "未知的支付方式")
    private String payType;

    @ApiModelProperty(value = "支付状态：0-未支付，1-已支付")
    private Integer payStatus;
}
