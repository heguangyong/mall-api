package com.zbkj.common.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * 社区配置VO对象
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
@ApiModel(value="CommunityConfigVo对象", description="社区配置VO对象")
public class CommunityConfigVo implements Serializable {

    private static final long serialVersionUID = 2543708543623796607L;

    @ApiModelProperty(value = "社区图文笔记审核开关", required = true)
    @NotBlank(message = "社区图文笔记审核开关不能为空")
    private String communityImageTextAuditSwitch;

    @ApiModelProperty(value = "社区短视频审核开关", required = true)
    @NotBlank(message = "社区短视频审核开关不能为空")
    private String communityShortVideoAuditSwitch;

    @ApiModelProperty(value = "社区评论审核开关", required = true)
    @NotBlank(message = "社区评论审核开关不能为空")
    private String communityReplyAuditSwitch;

    @ApiModelProperty(value = "社区评论开关", required = true)
    @NotBlank(message = "社区评论开关不能为空")
    private String communityReplySwitch;

}
