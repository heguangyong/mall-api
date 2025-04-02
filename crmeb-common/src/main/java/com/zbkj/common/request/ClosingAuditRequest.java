package com.zbkj.common.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 结算审核请求对象
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
@ApiModel(value = "ClosingAuditRequest对象", description = "结算审核请求对象")
public class ClosingAuditRequest implements Serializable {

    private static final long serialVersionUID = 3362714265772774491L;

    @ApiModelProperty(value = "结算单号")
    @NotEmpty(message = "结算单号不能为空")
    private String closingNo;

    @ApiModelProperty(value = "审核状态：1-通过审核，2-审核失败")
    @NotNull(message = "审核状态不能为空")
    @Range(min = 1, max = 2, message = "未知的审核状态")
    private Integer auditStatus;

    @ApiModelProperty(value = "拒绝原因")
    @Length(max = 50, message = "拒绝原因不能超过50个字符")
    private String refusalReason;

}
