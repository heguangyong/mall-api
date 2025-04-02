package com.zbkj.common.response;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * 签到页信息响应对象
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
@ApiModel(value = "SignPageInfoResponse对象", description = "签到页信息响应对象")
public class SignPageInfoResponse implements Serializable {

    private static final long serialVersionUID = 3285713110515203543L;

    @ApiModelProperty(value = "签到日期列表")
    private List<String> signDateList;

    @ApiModelProperty(value = "连续签到天数")
    private Integer signDayNum;

    @ApiModelProperty(value = "今日获得积分")
    private Integer integral;

    @ApiModelProperty(value = "今日获得经验")
    private Integer experience;

    @ApiModelProperty(value = "签到规则")
    private String signRule;

    @ApiModelProperty(value = "是否展示提示")
    private Boolean isTip;
}
