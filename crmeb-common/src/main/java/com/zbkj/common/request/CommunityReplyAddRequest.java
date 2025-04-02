package com.zbkj.common.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 评论添加请求对象
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
@ApiModel(value = "CommunityReplyAddRequest对象", description = "评论添加请求对象")
public class CommunityReplyAddRequest implements Serializable {

    private static final long serialVersionUID = 3362714265772774491L;

    @ApiModelProperty(value = "笔记ID")
    @NotNull(message = "笔记ID不能为空")
    private Integer noteId;

    @ApiModelProperty(value = "评论ID，一级评论传0")
    @NotNull(message = "评论ID不能为空")
    private Integer replyId;

    @ApiModelProperty(value = "评论/回复内容")
    @Length(max = 200, message = "内容不能超过200个字符")
    private String content;

    @ApiModelProperty(value = "用户ID，移动端不传值")
    private Integer userId;
}
