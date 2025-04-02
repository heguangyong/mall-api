package com.zbkj.common.vo.wxvedioshop.register;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * RegisterCheckAccessInfoItemVo 获取接入状态Response
 *  +----------------------------------------------------------------------
 *  | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
 *  +----------------------------------------------------------------------
 *  | Copyright (c) 2016~2025 https://www.crmeb.com All rights reserved.
 *  +----------------------------------------------------------------------
 *  | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
 *  +----------------------------------------------------------------------
 *  | Author: CRMEB Team <admin@crmeb.com>
 *  +----------------------------------------------------------------------
 */
@Data
public class RegisterCheckDataItemnVo {

    @ApiModelProperty(value = "API 文档中没有描述  看起来是状态")
    private Integer status;

    @ApiModelProperty(value = "API 文档中没有描述 看起来是拒绝原因")
    private String reject_reason;

    @ApiModelProperty(value = "接入相关信息")
    private RegisterCheckAccessInfoItemVo access_info;

    @ApiModelProperty(value = "场景接入相关")
    private List<RegisterSceneGroupList> scene_group_list;
}
