package com.zbkj.common.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;


/**
 * @ClassName CommunityNoteFrontDiscoverRequest
 * @Description 社区发现笔记请求对象
 * @Author HZW
 * @Date 2023/3/7 11:32
 * @Version 1.0
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "CommunityNoteFrontDiscoverRequest对象", description = "社区发现笔记请求对象")
public class CommunityNoteFrontDiscoverRequest extends PageParamRequest {

    @ApiModelProperty("标题")
    private String title;

    @ApiModelProperty("社区分类ID")
    private Integer categoryId;
}
