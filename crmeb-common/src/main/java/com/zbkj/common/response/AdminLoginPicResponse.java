package com.zbkj.common.response;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * 管理端登录页图片Response对象
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
@ApiModel(value = "AdminLoginPicResponse对象", description = "管理端登录页图片Response对象")
public class AdminLoginPicResponse implements Serializable {

    private static final long serialVersionUID = 3285713110515203543L;

    @ApiModelProperty(value = "登录页背景图")
    private String backgroundImage;

    @ApiModelProperty(value = "登录页LOGO")
    private String loginLogo;

    @ApiModelProperty(value = "登录页左侧logo")
    private String leftLogo;

    @ApiModelProperty(value = "网站名称")
    private String siteName;
}
