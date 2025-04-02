package com.zbkj.common.request;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 城市区域编辑请求对象
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
@ApiModel(value = "CityRegionEditRequest对象", description = "城市区域编辑请求对象")
public class CityRegionEditRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "历史区域id")
    @NotNull(message = "历史区域id不能为空")
    private Integer oldRegionId;

    @ApiModelProperty(value = "区域id")
    @NotNull(message = "区域id不能为空")
    private Integer regionId;

    @ApiModelProperty(value = "区域名称")
    @NotEmpty(message = "区域名称不能为空")
    private String regionName;

}
