package com.zbkj.common.request.merchant;

import com.zbkj.common.annotation.StringContains;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * 商户结算搜索请求对象
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
@ApiModel(value="MerchantClosingSearchRequest对象", description="商户结算搜索请求对象")
public class MerchantClosingSearchRequest implements Serializable {

    private static final long serialVersionUID = 3362714265772774491L;

    @ApiModelProperty(value = "商户id,只有平台端有此搜索选项")
    private Integer merId;

    @ApiModelProperty(value = "结算单号")
    private String closingNo;

    @ApiModelProperty(value = "结算类型:bank-银行卡,wechat-微信,alipay-支付宝")
    @StringContains(limitValues = {"bank","wechat","alipay"}, message = "未知的结算类型")
    private String closingType;

    @ApiModelProperty(value = "审核状态：0-待审核，1-通过审核，2-审核失败")
    private Integer auditStatus;

    @ApiModelProperty(value = "转账状态：0-未到账，1-已到账")
    private Integer accountStatus;

    @ApiModelProperty(value = "申请时间")
    private String dateLimit;

}
