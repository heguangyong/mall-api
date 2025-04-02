package com.zbkj.common.response;

import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @ClassName CommunityCommentReplyResponse
 * @Description 社区评论回复响应对象
 * @Author HZW
 * @Date 2023/3/7 18:02
 * @Version 1.0
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="CommunityCommentReplyResponse对象", description="社区评论回复响应对象")
public class CommunityCommentReplyResponse implements Serializable {

    private static final long serialVersionUID = -8121525449704982702L;

    @ApiModelProperty("评论ID")
    private Integer id;

    @ApiModelProperty("评论类型：1-评论，2-回复")
    private Integer type;

    @ApiModelProperty("发言用户ID")
    private Integer uid;

    @ApiModelProperty("发言用户昵称")
    private String nickname;

    @ApiModelProperty("发言头像")
    private String avatar;

    @ApiModelProperty("内容")
    private String content;

    @ApiModelProperty("点赞数")
    private Integer countStart;

    @ApiModelProperty("评论数")
    private Integer countReply;

    @ApiModelProperty("创建时间")
    private Date createTime;

    @ApiModelProperty("是否点赞")
    private Boolean isLike = false;

    @ApiModelProperty("下级评论列表")
    private List<CommunityCommentReplyResponse> replyList;

    @ApiModelProperty("原评论用户ID")
    private Integer reviewUid;

    @ApiModelProperty("原评论用户昵称")
    private String reviewUserNickname;

    @ApiModelProperty("审核状态:0-待审核，1-审核通过，2-审核失败")
    private Integer auditStatus;

    @ApiModelProperty("笔记评论总数")
    private Integer noteReplyNum = 0;

}
