package com.zbkj.common.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.List;

/**
 * 商品详情商户响应对象
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
@ApiModel(value = "ProductMerchantResponse对象", description = "商品详情商户响应对象")
public class ProductMerchantResponse implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "商户名称")
    private String name;

    @ApiModelProperty(value = "商户头像")
    private String avatar;

    @ApiModelProperty(value = "是否自营：0-非自营，1-自营")
    private Boolean isSelf;

    @ApiModelProperty(value = "商户星级1-5")
    private Integer starLevel;

    @ApiModelProperty(value = "商户类型ID")
    private Integer typeId;

    @ApiModelProperty(value = "商户商品列表")
    private List<ProMerchantProductResponse> proList;

    @ApiModelProperty(value = "关注商户用户数")
    private Integer collectNum;

    @ApiModelProperty(value = "用户是否收藏店铺")
    private Boolean isCollect = false;
}
