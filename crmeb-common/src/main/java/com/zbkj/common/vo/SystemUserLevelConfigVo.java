package com.zbkj.common.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 系统用户等级配置Vo对象
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
@ApiModel(value="SystemUserLevelConfigVo对象", description="系统用户等级配置Vo对象")
public class SystemUserLevelConfigVo implements Serializable {

    private static final long serialVersionUID = -5647992170392368353L;

    @ApiModelProperty(value = "用户等级开关")
    @NotBlank(message = "用户等级开关不能为空")
    private String userLevelSwitch;

    @ApiModelProperty(value = "种草图文可获得成长值")
    @NotNull(message = "种草图文可获得成长值不能为空")
    @Min(value = 0, message = "种草图文可获得成长值最小值为0")
    private Integer userLevelCommunityNotesExp;

    @ApiModelProperty(value = "种草图文可获得成长值")
    @NotNull(message = "种草图文可获得成长值不能为空")
    @Min(value = 0, message = "种草图文可获得成长值最小值为0")
    private Integer userLevelCommunityNotesNum;
}
