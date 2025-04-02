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
 * 社区笔记表
 * </p>
 *
 * @author HZW
 * @since 2023-03-07
 */
@Getter
@Setter
@Accessors(chain = true)
@TableName("eb_community_notes")
@ApiModel(value = "CommunityNotes对象", description = "社区笔记表")
public class CommunityNotes implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty("标题")
    @TableField("title")
    private String title;

    @ApiModelProperty("笔记类型：1-图文，2-视频")
    @TableField("`type`")
    private Integer type;

    @ApiModelProperty("封面")
    @TableField("cover")
    private String cover;

    @ApiModelProperty("图片")
    @TableField("image")
    private String image;

    @ApiModelProperty("视频链接")
    @TableField("video")
    private String video;

    @ApiModelProperty("笔记正文")
    @TableField("content")
    private String content;

    @ApiModelProperty("社区分类ID")
    @TableField("category_id")
    private Integer categoryId;

    @ApiModelProperty("话题ID,英文逗号拼接")
    @TableField("topic_ids")
    private String topicIds;

    @ApiModelProperty("用户ID")
    @TableField("uid")
    private Integer uid;

    @ApiModelProperty("星级排序:1-5")
    @TableField("star")
    private Integer star;

    @ApiModelProperty("审核状态:0-待审核，1-审核通过，2-审核失败，3-平台关闭")
    @TableField("audit_status")
    private Integer auditStatus;

    @ApiModelProperty("拒绝理由")
    @TableField("refusal")
    private String refusal;

    @ApiModelProperty("点赞数")
    @TableField("like_num")
    private Integer likeNum;

    @ApiModelProperty("评论数")
    @TableField("reply_num")
    private Integer replyNum;

    @ApiModelProperty("分享数")
    @TableField("share_num")
    private Integer shareNum;

    @ApiModelProperty("浏览量")
    @TableField("views")
    private Integer views;

    @ApiModelProperty("排序")
    @TableField("sort")
    private Integer sort;

    @ApiModelProperty("是否开启评论，1-开启，2-关闭，3-平台关闭")
    @TableField("reply_status")
    private Integer replyStatus;

    @ApiModelProperty("是否删除，0-未删除，1-删除")
    @TableField("is_del")
    private Integer isDel;

    @ApiModelProperty("操作时间")
    @TableField("operate_time")
    private Date operateTime;

    @ApiModelProperty("创建时间")
    @TableField("create_time")
    private Date createTime;

    @ApiModelProperty("更新时间")
    @TableField("update_time")
    private Date updateTime;


}
