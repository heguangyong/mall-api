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
 * PC首页导航Vo对象
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
@ApiModel(value = "PcHomeNavigationVo", description = "PC首页导航Vo对象")
public class PcHomeNavigationVo implements Serializable {

    private static final long serialVersionUID = -3097800453211866415L;

    @ApiModelProperty(value = "ID")
    private Integer id;

    @ApiModelProperty(value = "二维码名称", required = true)
    @NotBlank(message = "请填写二维码名称")
    private String name;

    @ApiModelProperty(value = "二维码地址", required = true)
    @NotBlank(message = "二维码地址不能为空")
    private String linkUrl;

    @ApiModelProperty(value = "排序", required = true)
    @NotNull(message = "排序不能为空")
    private Integer sort;

    @ApiModelProperty(value = "状态:是否显示")
    private Boolean status;
}
