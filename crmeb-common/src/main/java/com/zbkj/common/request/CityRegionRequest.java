package com.zbkj.common.request;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 城市区域请求对象
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
@ApiModel(value = "CityRegionRequest对象", description = "城市区域请求对象")
public class CityRegionRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "区域id")
    @NotNull(message = "区域id不能为空")
    private Integer regionId;

    @ApiModelProperty(value = "父区域id")
    @NotNull(message = "父区域id不能为空")
    private Integer parentId;

    @ApiModelProperty(value = "区域名称")
    @NotEmpty(message = "区域名称不能为空")
    private String regionName;

    @ApiModelProperty(value = "区域类型，0-国家、1-省、2-市、3-区、4-街道")
    @NotNull(message = "区域类型不能为空")
    @Range(min = 0, max = 4, message = "未知的区域类型")
    private Integer regionType;

}
