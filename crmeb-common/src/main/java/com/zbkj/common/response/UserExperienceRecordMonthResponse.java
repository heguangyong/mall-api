package com.zbkj.common.response;

import com.zbkj.common.model.user.UserExperienceRecord;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.List;

/**
 * 用户经验记录月份响应对象
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
@ApiModel(value = "UserExperienceRecordMonthResponse对象", description = "用户经验记录月份响应对象")
public class UserExperienceRecordMonthResponse implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "月份")
    private String month;

    @ApiModelProperty(value = "数据")
    private List<UserExperienceRecord> list;
}
