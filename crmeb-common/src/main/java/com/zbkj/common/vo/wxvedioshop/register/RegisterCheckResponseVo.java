package com.zbkj.common.vo.wxvedioshop.register;

import com.zbkj.common.vo.wxvedioshop.BaseResultResponseVo;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 获取接入状态 response
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
public class RegisterCheckResponseVo  extends BaseResultResponseVo {

    @ApiModelProperty(value = "获取申请状态 对象")
    private RegisterCheckDataItemnVo data;
}
