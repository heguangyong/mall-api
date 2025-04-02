package com.zbkj.common.model.community;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 社区评论表
 * </p>
 *
 * @author HZW
 * @since 2023-03-07
 */
@Getter
@Setter
@Accessors(chain = true)
@TableName("eb_community_reply")
@ApiModel(value = "CommunityReply对象", description = "社区评论表")
public class CommunityReply implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty("评论类型：1-评论，2-回复")
    @TableField("`type`")
    private Integer type;

    @ApiModelProperty("发言用户ID")
    @TableField("uid")
    private Integer uid;

    @ApiModelProperty("内容")
    @TableField("content")
    private String content;

    @ApiModelProperty("一级评论ID")
    @TableField("parent_id")
    private Integer parentId;

    @ApiModelProperty("一级用户ID")
    @TableField("parent_uid")
    private Integer parentUid;

    @ApiModelProperty("原评论ID")
    @TableField("review_id")
    private Integer reviewId;

    @ApiModelProperty("原评论用户ID")
    @TableField("review_uid")
    private Integer reviewUid;

    @ApiModelProperty("点赞数")
    @TableField("count_start")
    private Integer countStart;

    @ApiModelProperty("评论数")
    @TableField("count_reply")
    private Integer countReply;

    @ApiModelProperty("审核状态:0-待审核，1-审核通过，2-审核失败")
    @TableField("audit_status")
    private Integer auditStatus;

    @ApiModelProperty("拒绝原因")
    @TableField("refusal")
    private String refusal;

    @ApiModelProperty("文章id")
    @TableField("note_id")
    private Integer noteId;

    @ApiModelProperty("是否删除，0-未删除，1-删除")
    @TableField("is_del")
    private Integer isDel;

    @ApiModelProperty("创建时间")
    @TableField("create_time")
    private Date createTime;

    @ApiModelProperty("更新时间")
    @TableField("update_time")
    private Date updateTime;


}
