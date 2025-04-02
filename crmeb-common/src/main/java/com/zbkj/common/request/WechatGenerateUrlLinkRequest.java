package com.zbkj.common.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 微信生成url链接请求对象
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
@ApiModel(value = "WechatGenerateUrlLinkRequest", description = "微信生成url链接请求对象")
public class WechatGenerateUrlLinkRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "链接名称")
    @NotBlank(message = "请填写链接名称")
    @Length(max = 50, message = "链接名称不能超过50个字符")
    private String name;

    @ApiModelProperty(value = "原始小程序页面带参路径")
    @NotBlank(message = "请填写小程序页面路径")
    private String originalPath;

    @ApiModelProperty(value = "失效间隔天数1-30")
    @NotNull(message = "请填写失效间隔天数")
    @Range(min = 1, max = 30, message = "失效间隔天数范围为1-30")
    private Integer expireInterval;
}
