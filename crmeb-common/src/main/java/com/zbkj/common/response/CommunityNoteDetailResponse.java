package com.zbkj.common.response;

import com.baomidou.mybatisplus.annotation.TableField;
import com.zbkj.common.model.community.CommunityNotesProduct;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @ClassName CommunityNoteDetailResponse
 * @Description 社区笔记详情响应对象
 * @Author HZW
 * @Date 2023/3/7 18:02
 * @Version 1.0
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="CommunityNoteDetailResponse对象", description="社区笔记详情响应对象")
public class CommunityNoteDetailResponse implements Serializable {

    private static final long serialVersionUID = -8121525449704982702L;

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

    @ApiModelProperty("审核状态:0-待审核，1-审核通过，2-审核失败，3-平台关闭")
    private Integer auditStatus;

    @ApiModelProperty("拒绝理由")
    private String refusal;

    @ApiModelProperty("操作时间")
    private Date operateTime;

    @ApiModelProperty("创建时间")
    private Date createTime;

    @ApiModelProperty("作者昵称")
    private String authorName;

    @ApiModelProperty("作者ID")
    private Integer authorId;

    @ApiModelProperty("关联商品列表")
    private List<CommunityNotesProduct> productList;
}
