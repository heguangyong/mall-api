package com.zbkj.common.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 系统会员等级请求对象
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
@ApiModel(value="SystemUserLevelRequest对象", description="系统会员等级请求对象")
public class SystemUserLevelRequest implements Serializable {

    private static final long serialVersionUID=1L;

    @ApiModelProperty(value = "等级id，新增时不传")
    private Integer id;

    @ApiModelProperty(value = "等级名称")
    @NotBlank(message = "等级名称不能为空")
    @Length(max = 50, message = "等级名称不能超过50个字符")
    private String name;

    @ApiModelProperty(value = "达到多少升级经验")
    @NotNull(message = "等级经验不能为空")
    private Integer experience;

    @ApiModelProperty(value = "会员等级")
    @NotNull(message = "会员等级不能为空")
    @Min(value = 0, message = "会员等级最小为0")
    private Integer grade;

    @ApiModelProperty(value = "会员图标")
    @NotBlank(message = "会员图标不能为空")
    private String icon;

    @ApiModelProperty(value = "用户等级背景图")
    @NotBlank(message = "背景图不能为空")
    private String backImage;

    @ApiModelProperty(value = "用户等级文字背景色")
    @NotBlank(message = "用户等级文字背景色不能为空")
    private String backColor;
}
