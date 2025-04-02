package com.zbkj.common.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 社区笔记保存对象
 * +----------------------------------------------------------------------
 * | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
 * +----------------------------------------------------------------------
 * | Copyright (c) 2016~2025 https://www.crmeb.com All rights reserved.
 * +----------------------------------------------------------------------
 * | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
 * +----------------------------------------------------------------------
 * | Author: CRMEB Team <admin@crmeb.com>
 * +----------------------------------------------------------------------
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "CommunityNoteSaveRequest对象", description = "社区笔记保存对象")
public class CommunityNoteSaveRequest implements Serializable {

    private static final long serialVersionUID = 3362714265772774491L;

    @ApiModelProperty("笔记ID, 编辑时必填")
    private Integer id;

    @ApiModelProperty("标题")
    @Length(max = 20, message = "标题不能超过20个字符")
    private String title;

    @ApiModelProperty("笔记类型：1-图文，2-视频")
    @NotNull(message = "请选择笔记类型")
    @Range(min = 1, max = 2, message = "未知的笔记类型")
    private Integer type;

    @ApiModelProperty("封面")
    @NotBlank(message = "请先上传封面")
    private String cover;

    @ApiModelProperty("图片")
    private String image;

    @ApiModelProperty("视频链接")
    private String video;

    @ApiModelProperty("笔记正文")
    private String content;

    @ApiModelProperty("社区分类ID")
    @NotNull(message = "请选择分类")
    private Integer categoryId;

    @ApiModelProperty("话题ID,英文逗号拼接")
    private String topicIds;

    @ApiModelProperty("关联商品ID,英文逗号拼接")
    private String proIds;

    @ApiModelProperty("是否开启评论，1-开启，2-关闭，3-平台关闭")
    @NotNull(message = "请选择是否开启评论")
    @Range(min = 1, max = 3, message = "未知的评论开关状态")
    private Integer replyStatus;
}
