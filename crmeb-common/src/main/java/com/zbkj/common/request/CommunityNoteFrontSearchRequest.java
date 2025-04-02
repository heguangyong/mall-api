package com.zbkj.common.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotNull;


/**
 * @ClassName CommunityNoteFrontSearchRequest
 * @Description 社区笔记移动端查询请求对象
 * @Author HZW
 * @Date 2023/3/7 11:32
 * @Version 1.0
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "CommunityNoteFrontSearchRequest对象", description = "社区笔记移动端查询请求对象")
public class CommunityNoteFrontSearchRequest extends PageParamRequest {

    @ApiModelProperty("标题")
    private String title;

    @ApiModelProperty("社区分类ID")
    private Integer categoryId;

    @ApiModelProperty("作者ID")
    private Integer uid;

    @ApiModelProperty("审核状态:0-待审核，1-审核通过，2-审核失败，3-平台关闭")
    private Integer auditStatus;

    @ApiModelProperty("社区话题ID")
    private Integer topicId;

    @ApiModelProperty("排序规则（默认id倒序）:star-星级排序，hot-最热排序")
    private String collation = "id";

}
