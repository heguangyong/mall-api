package com.zbkj.common.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;


/**
 * @ClassName CommunityNoteSearchRequest
 * @Description 社区笔记查询请求对象
 * @Author HZW
 * @Date 2023/3/7 11:32
 * @Version 1.0
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "CommunityNoteSearchRequest对象", description = "社区笔记查询请求对象")
public class CommunityNoteSearchRequest extends UserCommonSearchRequest {

    private static final long serialVersionUID = 1567311735344286591L;

    @ApiModelProperty("标题")
    private String title;

    @ApiModelProperty("笔记类型：1-图文，2-视频")
    private Integer type;

    @ApiModelProperty("社区分类ID")
    private Integer categoryId;

    @ApiModelProperty("话题ID")
    private Integer topicId;

    @ApiModelProperty("审核状态,全部不传: 0-待审核，1-审核通过，2-审核失败，3-平台关闭")
    private Integer auditStatus;

}
