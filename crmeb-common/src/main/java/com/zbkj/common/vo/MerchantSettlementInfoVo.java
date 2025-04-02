package com.zbkj.common.vo;

import com.zbkj.common.annotation.StringContains;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotEmpty;
import java.io.Serializable;

/**
 * 商户结算信息Vo对象
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
@ApiModel(value = "MerchantSettlementInfoVo对象", description = "商户结算信息Vo对象")
public class MerchantSettlementInfoVo implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "结算类型:bank-银行卡,wechat-微信,alipay-支付宝", required = true)
    @NotEmpty(message = "结算类型不能为空")
    @StringContains(limitValues = {"bank", "wechat", "alipay"}, message = "未知的转账类型")
    private String settlementType;

    @ApiModelProperty(value = "持卡人姓名")
    private String bankUserName;

    @ApiModelProperty(value = "银行名称")
    private String bankName;

    @ApiModelProperty(value = "银行卡号")
    private String bankCard;

    @ApiModelProperty(value = "开户地址")
    private String bankAddress;

    @ApiModelProperty(value = "微信号")
    private String wechatCode;

    @ApiModelProperty(value = "微信收款二维码")
    private String wechatQrcodeUrl;

    @ApiModelProperty(value = "真实姓名")
    private String realName;

    @ApiModelProperty(value = "支付宝账号")
    private String alipayCode;

    @ApiModelProperty(value = "支付宝收款二维码")
    private String alipayQrcodeUrl;
}
