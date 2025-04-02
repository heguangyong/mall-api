package com.zbkj.common.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @ClassName CommunityNotePageDateResponse
 * @Description 社区笔记分页数据响应对象
 * @Author HZW
 * @Date 2023/3/7 18:02
 * @Version 1.0
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="CommunityNotePageDateResponse对象", description="社区笔记分页数据响应对象")
public class CommunityNotePageDateResponse implements Serializable {

    private static final long serialVersionUID = -8121525449704982702L;

    @ApiModelProperty("笔记ID")
    private Integer id;

    @ApiModelProperty("标题")
    private String title;

    @ApiModelProperty("作者昵称")
    private String authorName;

    @ApiModelProperty("作者ID")
    private Integer authorId;

    @ApiModelProperty("笔记类型：1-图文，2-视频")
    private Integer type;

    @ApiModelProperty("封面")
    private String cover;

    @ApiModelProperty("社区分类名称")
    private String categoryName;

    @ApiModelProperty("话题名称数组")
    private List<String> topicList;

    @ApiModelProperty("星级排序:1-5")
    private Integer star;

    @ApiModelProperty("审核状态:0-待审核，1-审核通过，2-审核失败，3-平台关闭")
    private Integer auditStatus;

    @ApiModelProperty("拒绝理由")
    private String refusal;

    @ApiModelProperty("点赞数")
    private Integer likeNum;

    @ApiModelProperty("评论数")
    private Integer replyNum;

    @ApiModelProperty("是否开启评论，1-开启，2-关闭，3-平台关闭")
    private Integer replyStatus;

    @ApiModelProperty("创建时间")
    private Date createTime;

}
