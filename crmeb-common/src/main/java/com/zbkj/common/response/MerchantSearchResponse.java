package com.zbkj.common.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.List;

/**
 * 商户搜索响应对象
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
@ApiModel(value = "MerchantSearchResponse对象", description = "商户搜索响应对象")
public class MerchantSearchResponse implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "商户ID")
    private Integer id;

    @ApiModelProperty(value = "商户名称")
    private String name;

    @ApiModelProperty(value = "商户分类ID")
    private Integer categoryId;

    @ApiModelProperty(value = "商户类型ID")
    private Integer typeId;

    @ApiModelProperty(value = "是否自营：0-非自营，1-自营")
    private Boolean isSelf;

    @ApiModelProperty(value = "商户背景图")
    private String backImage;

    @ApiModelProperty(value = "商户头像")
    private String avatar;

    @ApiModelProperty(value = "商户街背景图")
    private String streetBackImage;

    @ApiModelProperty(value = "商户logo（横）")
    private String rectangleLogo;

    @ApiModelProperty(value = "商户星级1-5")
    private Integer starLevel;

    @ApiModelProperty(value = "关注人数")
    private Integer followerNum;

    @ApiModelProperty(value = "商品列表")
    private List<ProMerchantProductResponse> proList;

    @ApiModelProperty(value = "是否关注：0-未关注，1-已关注")
    private Boolean isCollect = false;

    @ApiModelProperty(value = "商户PCLogo")
    private String pcLogo;

    @ApiModelProperty(value = "商户PC品牌好店封面图片")
    private String pcGoodStoreCoverImage;

}
