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
 * PC首页bannerVo对象
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
@ApiModel(value = "PcHomeBannerVo", description = "PC首页bannerVo对象")
public class PcHomeBannerVo implements Serializable {

    private static final long serialVersionUID = -3097800453211866415L;

    @ApiModelProperty(value = "bannerID")
    private Integer id;

    @ApiModelProperty(value = "图片地址", required = true)
    @NotBlank(message = "请选择图片")
    private String imageUrl;

    @ApiModelProperty(value = "banner标题", required = true)
    @NotBlank(message = "请填写标题")
    private String name;

    @ApiModelProperty(value = "banner链接")
    private String linkUrl;

    @ApiModelProperty(value = "显示状态", required = true)
    @NotNull(message = "请选择显示状态")
    private Boolean status;

    @ApiModelProperty(value = "排序", required = true)
    @NotNull(message = "排序不能为空")
    private Integer sort;
}
