package com.zbkj.common.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 身份更新状态请求对象
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
@ApiModel(value = "SystemRoleStatusRequest对象", description = "身份更新状态请求对象")
public class SystemRoleStatusRequest implements Serializable {

    private static final long serialVersionUID = -7616469901068422271L;

    @ApiModelProperty(value = "角色id", required = true)
    @NotNull(message = "角色ID不能为空")
    private Integer id;

    @ApiModelProperty(value = "状态：0-关闭，1-正常", required = true)
    @NotNull(message = "状态不能为空")
    private Boolean status;
}
