package com.zbkj.common.request;

import com.zbkj.common.annotation.StringContains;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * App微信注册/登录请求对象
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
@ApiModel(value="RegisterAppWxRequest对象", description="App微信注册/登录请求对象")
public class RegisterAppWxRequest implements Serializable {

    private static final long serialVersionUID=1L;

    @ApiModelProperty(value = "用户openId", required = true)
    @NotBlank(message = "openId不能为空")
    private String openId;

    @ApiModelProperty(value = "用户unionId")
    private String unionId;

    @ApiModelProperty(value = "微信App类型：iosWx-苹果微信，androidWx-安卓微信", required = true)
    @StringContains(limitValues = {"iosWx", "androidWx"}, message = "未知的微信App类型")
    private String type;

}
