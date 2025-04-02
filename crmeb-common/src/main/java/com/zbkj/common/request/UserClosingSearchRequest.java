package com.zbkj.common.request;

import com.zbkj.common.annotation.StringContains;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Range;

import java.io.Serializable;

/**
 * 用户结算搜索请求对象
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
@ApiModel(value = "UserClosingSearchRequest对象", description = "用户结算搜索请求对象")
public class UserClosingSearchRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "搜索关键字（微信号、支付宝账号、银行卡号、持卡人姓名）")
    private String keywords;

    @ApiModelProperty(value = "结算类型：bank = 银行卡 alipay = 支付宝 wechat=微信")
    @StringContains(limitValues = {"bank","alipay","wechat"}, message = "请选择正确的提现方式")
    private String closingType;

    @ApiModelProperty(value = "审核状态：0-待审核，1-通过审核，2-审核失败")
    @Range(min = 0, max = 2, message = "请选择正确的结算状态")
    private Integer auditStatus;

    @ApiModelProperty(value = "到账状态：0-未到账，1-已到账")
    private Integer accountStatus;

    @ApiModelProperty(value = "时间格式：today,yesterday,lately7,lately30,month,year,/yyyy-MM-dd hh:mm:ss,yyyy-MM-dd hh:mm:ss/")
    private String dateLimit;

}
