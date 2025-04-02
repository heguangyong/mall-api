package com.zbkj.common.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * 移动端登录配置响应对象
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
@ApiModel(value = "FrontLoginConfigResponse", description = "用户登录返回数据")
public class FrontLoginConfigResponse implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "登录logo")
    private String logo;

    @ApiModelProperty(value = "公众号-浏览访问方式")
    private String wechatBrowserVisit;

    @ApiModelProperty(value = "小程序—手机号授权验证类型")
    private String routinePhoneVerification;

    @ApiModelProperty(value = "移动端登录Logo")
    private String mobileLoginLogo;

}
