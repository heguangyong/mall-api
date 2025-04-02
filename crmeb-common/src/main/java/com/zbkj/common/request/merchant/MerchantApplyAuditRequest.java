package com.zbkj.common.request.merchant;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 商户申请审核请求对象
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
@ApiModel(value="MerchantApplyAuditRequest对象", description="商户申请审核请求对象")
public class MerchantApplyAuditRequest implements Serializable {

    private static final long serialVersionUID=1L;

    @ApiModelProperty(value = "申请ID")
    @NotNull(message = "申请单ID不能为空")
    private Integer id;

    @ApiModelProperty(value = "审核状态：2-审核通过，3-审核拒绝")
    @NotNull(message = "审核状态不能为空")
    @Range(min = 2, max = 3, message = "未知的审核状态")
    private Integer auditStatus;

    @ApiModelProperty(value = "拒绝原因")
    private String denialReason;

}
