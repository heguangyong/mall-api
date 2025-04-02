package com.zbkj.common.vo.wxvedioshop.register;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * 自定义交易组件接入状态 场景检查
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
public class RegisterSceneGroupList {
    @ApiModelProperty(value = "场景枚举，1:视频号、公众号场景")
    private Integer group_id;

    @ApiModelProperty(value = "审核理由")
    private String reason;

    @ApiModelProperty(value = "场景名称")
    private String name;

    @ApiModelProperty(value = "审核状态，0:审核中，1:审核完成，2:审核失败，3未审核")
    private Integer status;

    @ApiModelProperty(value = "场景相关审核结果")
    private List<RegisterSceneGroupExtList> scene_group_ext_list;

    @Data
    class RegisterSceneGroupExtList{
        @ApiModelProperty(value = "审核事项id，1:客服售后，2:电商平台")
        private Integer ext_id;

        @ApiModelProperty(value = "场景相关审核结果，0:审核中，1:审核成功，2:审核失败，3未审核")
        private Integer status;
    }

}
