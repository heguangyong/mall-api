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
 * @ClassName CommunityReplyPageDateResponse
 * @Description 社区评论分页数据响应对象
 * @Author HZW
 * @Date 2023/3/7 18:02
 * @Version 1.0
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="CommunityReplyPageDateResponse对象", description="社区评论分页数据响应对象")
public class CommunityReplyPageDateResponse implements Serializable {

    private static final long serialVersionUID = -8121525449704982702L;

    @ApiModelProperty("评论ID")
    private Integer id;

    @ApiModelProperty("评论类型：1-评论，2-回复")
    private Integer type;

    @ApiModelProperty("发言用户ID")
    private Integer uid;

    @ApiModelProperty("内容")
    private String content;

    @ApiModelProperty("点赞数")
    private Integer countStart;

    @ApiModelProperty("评论数")
    private Integer countReply;

    @ApiModelProperty("审核状态:0-待审核，1-审核通过，2-审核失败")
    private Integer auditStatus;

    @ApiModelProperty("拒绝原因")
    private String refusal;

    @ApiModelProperty("创建时间")
    private Date createTime;

    @ApiModelProperty("文章标题")
    private String noteTitle;

    @ApiModelProperty("发言用户昵称")
    private String userNickname;
}
