package com.zbkj.common.response;

import com.baomidou.mybatisplus.annotation.TableField;
import com.zbkj.common.model.community.CommunityNotesProduct;
import com.zbkj.common.model.community.CommunityTopic;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @ClassName CommunityNoteFrontDetailResponse
 * @Description 社区笔记移动端详情响应对象
 * @Author HZW
 * @Date 2023/3/7 18:02
 * @Version 1.0
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="CommunityNoteFrontDetailResponse", description="社区笔记移动端详情响应对象")
public class CommunityNoteFrontDetailResponse implements Serializable {

    private static final long serialVersionUID = -8121525449704982702L;

    @ApiModelProperty("作者ID")
    private Integer authorId;

    @ApiModelProperty("作者头像")
    private String authorAvatar;

    @ApiModelProperty("作者昵称")
    private String authorName;

    @ApiModelProperty("作者等级图标")
    private String authorLevelIcon;

    @ApiModelProperty("笔记ID")
    private Integer id;

    @ApiModelProperty("标题")
    private String title;

    @ApiModelProperty("笔记类型：1-图文，2-视频")
    private Integer type;

    @ApiModelProperty("封面")
    private String cover;

    @ApiModelProperty("图片")
    private String image;

    @ApiModelProperty("视频链接")
    private String video;

    @ApiModelProperty("笔记正文")
    private String content;

    @ApiModelProperty("创建时间")
    private Date createTime;

    @ApiModelProperty("点赞数")
    private Integer likeNum;

    @ApiModelProperty("评论数")
    private Integer replyNum;

    @ApiModelProperty("用户是否点赞")
    private Boolean userIsLike = false;

    @ApiModelProperty("关联话题列表")
    private List<CommunityTopic> topicList;

    @ApiModelProperty("关联商品列表")
    private List<CommunityNotesProduct> productList;

    @ApiModelProperty("审核状态:0-待审核，1-审核通过，2-审核失败，3-平台关闭")
    private Integer auditStatus;

    @ApiModelProperty("拒绝理由")
    private String refusal;

    @ApiModelProperty("是否开启评论，1-开启，2-关闭，3-平台关闭")
    private Integer replyStatus;

    @ApiModelProperty(value = "平台评论开关")
    private Boolean platReplySwitch = false;

    @ApiModelProperty("是否关注")
    private Boolean isConcerned = false;

    @ApiModelProperty("社区分类ID")
    private Integer categoryId;

    @ApiModelProperty("社区分类名称")
    private String categoryName;
}
