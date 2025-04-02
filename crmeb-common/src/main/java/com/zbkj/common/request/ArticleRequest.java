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
 * 文章请求对象
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
@ApiModel(value = "ArticleRequest对象", description = "文章请求对象")
public class ArticleRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "文章ID,新增时不传，修改时必传")
    private Integer id;

    @ApiModelProperty(value = "文章分类id", required = true)
    @NotNull(message = "请选择文章分类")
    private Integer cid;

    @ApiModelProperty(value = "文章标题", required = true)
    @NotBlank(message = "请填写文章标题")
    @Length(max = 200, message = "文章标题最多200个字符")
    private String title;

    @ApiModelProperty(value = "文章作者", required = true)
    @NotBlank(message = "请填写文章作者")
    @Length(max = 50, message = "文章作者最多50个字符")
    private String author;

    @ApiModelProperty(value = "文章封面", required = true)
    @NotBlank(message = "请上传文章封面")
    @Length(max = 255, message = "文章封面路径最多255个字符")
    private String cover;

    @ApiModelProperty(value = "文章简介", required = true)
    @Length(max = 200, message = "文章简介最多200个字符")
    @NotBlank(message = "请填写文章简介")
    private String synopsis;

    @ApiModelProperty(value = "文章内容", required = true)
    @NotBlank(message = "请填写文章内容")
    private String content;

    @ApiModelProperty(value = "是否热门(小程序)", example = "false")
    @NotNull(message = "是否热门(小程序)不能为空")
    private Boolean isHot;

    @ApiModelProperty(value = "是否轮播图(小程序)", example = "true")
    @NotNull(message = "是否轮播图(小程序)不能为空")
    private Boolean isBanner;

    @ApiModelProperty(value = "排序")
    @Range(min = 0, max = 9999, message = "排序范围为0~9999")
    private Integer sort;
}
