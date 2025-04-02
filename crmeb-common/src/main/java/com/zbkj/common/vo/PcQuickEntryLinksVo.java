package com.zbkj.common.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * PC快捷入口链接Vo对象
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
@ApiModel(value = "PcQuickEntryLinksVo", description = "PC快捷入口链接Vo对象")
public class PcQuickEntryLinksVo implements Serializable {

    private static final long serialVersionUID = -3097800453211866415L;

    @ApiModelProperty(value = "ID")
    private Integer id;

    @ApiModelProperty(value = "链接名称", required = true)
    @NotBlank(message = "请填写链接名称")
    private String name;

    @ApiModelProperty(value = "链接地址", required = true)
    @NotBlank(message = "链接地址不能为空")
    private String linkUrl;

    @ApiModelProperty(value = "排序", required = true)
    @NotNull(message = "排序不能为空")
    private Integer sort;
}
