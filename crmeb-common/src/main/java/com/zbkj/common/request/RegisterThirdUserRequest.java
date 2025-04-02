package com.zbkj.common.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.zbkj.common.annotation.StringContains;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * 用户地址表
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
@ApiModel(value="RegisterThirdUserRequest对象", description="三方用户注册对象")
public class RegisterThirdUserRequest implements Serializable {

    private static final long serialVersionUID=1L;

    @ApiModelProperty(value = "推广人id")
    private Integer spreadPid;

    @ApiModelProperty(value = "用户类型:wechat-公众号，routine-小程序，h5-H5,iosWx-苹果微信，androidWx-安卓微信")
    @StringContains(limitValues = {"wechat","routine","h5","iosWx","androidWx"}, message = "未知的用户类型")
    private String type;

    @ApiModelProperty(value = "用户openId")
    private String openId;

    @ApiModelProperty(value = "前端临时授权code")
    private String code;
}
