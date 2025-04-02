package com.zbkj.common.request;

import com.zbkj.common.annotation.StringContains;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;


/**
 * @ClassName CommunityNoteTopicSearchRequest
 * @Description 社区笔记移动端话题搜索请求对象
 * @Author HZW
 * @Date 2023/3/7 11:32
 * @Version 1.0
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "CommunityNoteTopicSearchRequest对象", description = "社区笔记移动端话题搜索请求对象")
public class CommunityNoteTopicSearchRequest extends PageParamRequest {

    @ApiModelProperty("社区话题ID")
    @NotNull(message = "请选择社区话题")
    private Integer topicId;

    @ApiModelProperty("类型:hot-最热，new-最新")
    @NotBlank(message = "请选择查看类型")
    @StringContains(limitValues = {"hot", "new"}, message = "位置查看类型")
    private String type;

}
