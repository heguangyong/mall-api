package com.zbkj.common.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

/**
 * 积分商品添加对象
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
@ApiModel(value = "IntegralProductAddRequest", description = "积分商品添加对象")
public class IntegralProductAddRequest implements Serializable {

    private static final long serialVersionUID = -452373239606480650L;

    @ApiModelProperty(value = "商品id|添加时不填，修改时必填")
    private Integer id;

    @ApiModelProperty(value = "商品名称", required = true)
    @NotBlank(message = "商品名称不能为空")
    @Length(max = 50, message = "商品名称长度不能超过50个字符")
    private String name;

    @ApiModelProperty(value = "单位名", required = true)
    @NotBlank(message = "单位名称不能为空")
    @Length(max = 32, message = "单位名长度不能超过32个字符")
    private String unitName;

    @ApiModelProperty(value = "商品图片", required = true)
    @NotBlank(message = "商品图片不能为空")
    @Length(max = 255, message = "商品图片名称长度不能超过255个字符")
    private String image;

    @ApiModelProperty(value = "轮播图", required = true)
    @NotBlank(message = "轮播图不能为空")
    @Length(max = 2000, message = "轮播图名称长度不能超过2000个字符")
    private String sliderImage;

    @ApiModelProperty(value = "关键字")
//    @Length(max = 255, message = "关键字长度不能超过255个字符")
//    @NotBlank(message = "关键字不能为空")
    private String keyword;

    @ApiModelProperty(value = "商品简介", required = true)
    @NotBlank(message = "商品简介不能为空")
    @Length(max = 100, message = "商品简介长度不能超过100个字符")
    private String intro;

    @ApiModelProperty(value = "状态（0：未上架，1：上架）", required = true)
    @NotNull(message = "请选择上架状态")
    private Boolean isShow;

    @ApiModelProperty(value = "兑换数量")
    private Integer exchangeNum;

    @ApiModelProperty(value = "是否热门推荐")
    private Integer isHot;

    @ApiModelProperty(value = "排序")
    private Integer sort;

    @ApiModelProperty(value = "规格 0单 1多", required = true)
    @NotNull(message = "商品规格类型不能为空")
    private Boolean specType;

    @Valid
    @ApiModelProperty(value = "商品属性", required = true)
    @NotEmpty(message = "商品属性不能为空")
    private List<ProductAttrAddRequest> attrList;

    @Valid
    @ApiModelProperty(value = "商品属性详情", required = true)
    @NotEmpty(message = "商品属性详情不能为空")
    private List<IntegralProductAttrValueAddRequest> attrValueList;

    @ApiModelProperty(value = "商品描述")
    private String content;

}
