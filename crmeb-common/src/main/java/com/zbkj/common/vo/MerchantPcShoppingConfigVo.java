package com.zbkj.common.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.util.List;

/**
 * 商户PC商城设置VO对象
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
@ApiModel(value = "MerchantPcShoppingConfigVo", description = "商户PC商城设置VO对象")
public class MerchantPcShoppingConfigVo implements Serializable {

    private static final long serialVersionUID = -3097800453211866415L;

    @ApiModelProperty(value = "商户PCLogo")
    @NotBlank(message = "请选择商户Logo图片")
    private String pcLogo;

    @ApiModelProperty(value = "商户PC品牌好店封面图片")
    @NotBlank(message = "请选择品牌好店商户封面图片")
    private String pcGoodStoreCoverImage;

    @ApiModelProperty(value = "商户PC的Banner")
    @Valid
    private List<MerchantPcBannerVo> bannerList;

    @ApiModelProperty(value = "商户店铺推荐商品字符串")
    private String recommendProductStr;

    @ApiModelProperty(value = "商户店铺推荐商品列表,编辑时不传")
    private List<ChooseProductVo> recommendProductList;
}
