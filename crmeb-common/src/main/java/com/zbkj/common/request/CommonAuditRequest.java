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
 * 通用审核对象
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
@ApiModel(value = "CommonAuditRequest对象", description = "通用审核对象")
public class CommonAuditRequest implements Serializable {

    private static final long serialVersionUID = 3362714265772774491L;

    @ApiModelProperty(value = "数据ID")
    @NotNull(message = "数据ID不能为空")
    private Integer id;

    @ApiModelProperty(value = "审核状态：1-审核通过，2-审核失败")
    @NotNull(message = "审核状态不能为空")
    @Range(min = 1, max = 2, message = "未知的审核状态")
    private Integer auditStatus;

    @ApiModelProperty(value = "拒绝原因")
    @Length(max = 50, message = "拒绝原因不能超过50个字符")
    private String refusalReason;

}
