package com.zbkj.common.response;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * App版本信息响应对象
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
@ApiModel(value = "AppVersionResponse对象", description = "App版本信息响应对象")
public class AppVersionResponse implements Serializable {

    private static final long serialVersionUID = 3285713110515203543L;

    @ApiModelProperty(value = "app 版本号")
    private String appVersion;

    @ApiModelProperty(value = "android版本地址")
    private String androidAddress;

    @ApiModelProperty(value = "ios版本地址")
    private String iosAddress;

    @ApiModelProperty(value = "开放式升级")
    private String openUpgrade;
}
