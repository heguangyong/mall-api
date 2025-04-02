package com.zbkj.common.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * 签到配置请求对象
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
@ApiModel(value = "SignConfigRequest对象", description = "签到配置请求对象")
public class SignConfigRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "id,新增、编辑基础配置时不传,编辑连续配置时必传")
    private Integer id;

    @ApiModelProperty(value = "连续签到天数，0-每天签到奖励", required = true)
    private Integer day;

    @ApiModelProperty(value = "是否奖励积分", required = true)
    private Boolean isIntegral;

    @ApiModelProperty(value = "签到积分", required = true)
    private Integer integral;

    @ApiModelProperty(value = "是否奖励经验", required = true)
    private Boolean isExperience;

    @ApiModelProperty(value = "签到经验", required = true)
    private Integer experience;

    @ApiModelProperty(value = "签到规则说明，基础签到配置专用")
    private String signRuleDescription;
}
