package com.zbkj.common.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;


/**
 * @ClassName CommunityCategorySearchRequest
 * @Description 社区分类查询请求对象
 * @Author HZW
 * @Date 2023/3/7 11:32
 * @Version 1.0
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "CommunityCategorySearchRequest对象", description = "社区分类查询请求对象")
public class CommunityCategorySearchRequest extends PageParamRequest {

    @ApiModelProperty("分类名称")
    private String name;

    @ApiModelProperty("是否显示：1-显示，0-不显示")
    private Integer isShow;
}
