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
 * 社区评论点赞表
 * </p>
 *
 * @author HZW
 * @since 2023-03-14
 */
@Getter
@Setter
@Accessors(chain = true)
@TableName("eb_community_reply_like")
@ApiModel(value = "CommunityReplyLike对象", description = "社区评论点赞表")
public class CommunityReplyLike implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty("笔记ID")
    @TableField("note_id")
    private Integer noteId;

    @ApiModelProperty("评论ID")
    @TableField("reply_id")
    private Integer replyId;

    @ApiModelProperty("用户ID")
    @TableField("uid")
    private Integer uid;

    @ApiModelProperty("创建时间")
    @TableField("create_time")
    private Date createTime;


}
