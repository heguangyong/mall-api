package com.zbkj.common.response;

import com.zbkj.common.model.system.GroupConfig;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.List;

/**
 * PC商城商户首页信息响应对象
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
@ApiModel(value = "MerchantPcIndexResponse", description = "PC商城商户首页信息响应对象")
public class MerchantPcIndexResponse implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "商户ID")
    private Integer id;

    @ApiModelProperty(value = "商户名称")
    private String name;

    @ApiModelProperty(value = "是否自营：0-非自营，1-自营")
    private Boolean isSelf;

    @ApiModelProperty(value = "商户星级1-5")
    private Integer starLevel;

    @ApiModelProperty(value = "是否关注：0-未关注，1-已关注")
    private Boolean isCollect;

    @ApiModelProperty(value = "客服类型：H5-H5链接、phone-电话")
    private String serviceType;

    @ApiModelProperty(value = "客服H5链接")
    private String serviceLink;

    @ApiModelProperty(value = "客服电话")
    private String servicePhone;

    @ApiModelProperty(value = "资质图片")
    private String qualificationPicture;

    @ApiModelProperty(value = "商户PCLogo")
    private String pcLogo;

    @ApiModelProperty(value = "PC-banner列表")
    private List<GroupConfig> bannerList;

    @ApiModelProperty(value = "商户头像")
    private String avatar;
}
