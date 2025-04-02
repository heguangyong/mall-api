package com.zbkj.common.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;


/**
 * @ClassName CommunityReplySearchRequest
 * @Description 社区评论查询请求对象
 * @Author HZW
 * @Date 2023/3/7 11:32
 * @Version 1.0
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "CommunityReplySearchRequest对象", description = "社区评论查询请求对象")
public class CommunityReplySearchRequest extends UserCommonSearchRequest {

    private static final long serialVersionUID = -6587374170485943982L;

    @ApiModelProperty("文章标题")
    private String title;

    @ApiModelProperty("关键字搜索（评论内容）")
    private String keywords;

    @ApiModelProperty("审核状态,全部不传: 0-待审核，1-审核通过，2-审核失败")
    private Integer auditStatus;

    @ApiModelProperty(value = "创建时间区间")
    private String dateLimit;
}
