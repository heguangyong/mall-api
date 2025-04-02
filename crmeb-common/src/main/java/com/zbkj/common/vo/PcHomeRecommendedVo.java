package com.zbkj.common.vo;

import com.zbkj.common.annotation.StringContains;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;

/**
 * PC首页首页推荐Vo对象
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
@ApiModel(value = "PcHomeRecommendedVo", description = "PC首页首页推荐Vo对象")
public class PcHomeRecommendedVo implements Serializable {

    private static final long serialVersionUID = -3097800453211866415L;

    @ApiModelProperty(value = "ID")
    private Integer id;

    @ApiModelProperty(value = "板块名称", required = true)
    @NotBlank(message = "请填写板块名称")
    @Length(min = 1, max = 6, message = "模块名称长度不能超过6个字符")
    private String name;

    @ApiModelProperty(value = "广告图", required = true)
    @NotBlank(message = "请选择广告图")
    private String imageUrl;

    @ApiModelProperty(value = "广告图链接")
    private String linkUrl;

    @ApiModelProperty(value = "显示状态", required = true)
    @NotNull(message = "请选择显示状态")
    private Boolean status;

    @ApiModelProperty(value = "排序", required = true)
    @NotNull(message = "排序不能为空")
    @Range(min = 0, max = 999, message = "排序范围为1~999")
    private Integer sort;

    @ApiModelProperty(value = "商品关联类型:product-商品，category-品类，brand-品牌,merchant-店铺", required = true)
    @NotBlank(message = "请选择商品关联类型")
    @StringContains(limitValues = {"product","category","brand","merchant"}, message = "未知的商品关联类型")
    private String productAssociationType;

    @ApiModelProperty(value = "关联的数据")
    @NotBlank(message = "关联的数据不能为空")
    private String data;
}
