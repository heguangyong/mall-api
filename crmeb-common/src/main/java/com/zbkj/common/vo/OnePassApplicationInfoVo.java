package com.zbkj.common.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;

/**
 * 一号通应用信息VO对象
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
@ApiModel(value = "OnePassApplicationInfoVo", description = "一号通应用信息VO对象")
public class OnePassApplicationInfoVo {

    private static final long serialVersionUID = 1L;

    public OnePassApplicationInfoVo() {

    }

    public OnePassApplicationInfoVo(String accessKey, String secretKey) {
        this.accessKey = accessKey;
        this.secretKey = secretKey;
    }

    @ApiModelProperty(value = "access_key一号通后台应用管理获得APPID", required = true)
    @NotBlank(message = "AccessKey 不能为空")
    private String accessKey;

    @ApiModelProperty(value = "secret_key一号通后台应用管理获得AppSecret", required = true)
    @NotBlank(message = "secretKey 不能为空")
    private String secretKey;
}
