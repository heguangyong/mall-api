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
 * 社区笔记关系表
 * </p>
 *
 * @author HZW
 * @since 2023-03-07
 */
@Getter
@Setter
@Accessors(chain = true)
@TableName("eb_community_notes_relation")
@ApiModel(value = "CommunityNotesRelation对象", description = "社区笔记关系表")
public class CommunityNotesRelation implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty("笔记ID")
    @TableField("note_id")
    private Integer noteId;

    @ApiModelProperty("笔记作者ID")
    @TableField("author_id")
    private Integer authorId;

    @ApiModelProperty("用户ID")
    @TableField("uid")
    private Integer uid;

    @ApiModelProperty("关联类型(收藏(collect）、点赞(like))")
    @TableField("`type`")
    private String type;

    @ApiModelProperty("创建时间")
    @TableField("create_time")
    private Date createTime;


}
