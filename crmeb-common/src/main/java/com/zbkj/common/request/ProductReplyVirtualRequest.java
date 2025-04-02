package com.zbkj.common.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

/**
 * 虚拟评论对象
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
@ApiModel(value = "ProductReplyVirtualRequest对象", description = "虚拟评论对象")
public class ProductReplyVirtualRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "商品ID", required = true)
    @NotNull(message = "商品ID不能为空")
    private Integer productId;

    @ApiModelProperty(value = "商品规格属性值ID", required = true)
    @NotNull(message = "商品规格属性值ID不能为空")
    private Integer attrValueId;

    @ApiModelProperty(value = "评价星级", example = "5", required = true)
    @NotNull(message = "评价星级不能为空")
    @Range(min = 1, max = 5, message = "评价星级为1-5")
    private Integer star;

    @ApiModelProperty(value = "评论内容", required = true)
    @NotBlank(message = "请填写评论内容")
    @Length(max = 512, message = "评论内容长度不能超过512个字符")
    private String comment;

    @ApiModelProperty(value = "评论图片", required = true)
    private List<String> pics;

    @ApiModelProperty(value = "评论人头像 [虚拟评论参数]", required = true)
    @NotEmpty(message = "评论人头像不能为空")
    private String avatar;

    @ApiModelProperty(value = "评论人昵称 [虚拟评论参数]", required = true)
    @NotEmpty(message = "评论人昵称不能为空")
    private String nickname;
}
