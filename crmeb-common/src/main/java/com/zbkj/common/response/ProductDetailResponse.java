package com.zbkj.common.response;

import com.zbkj.common.model.coupon.Coupon;
import com.zbkj.common.model.product.Product;
import com.zbkj.common.model.product.ProductAttribute;
import com.zbkj.common.model.product.ProductGuarantee;
import com.zbkj.common.response.groupbuy.GroupBuyActivityResponse;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

/**
 * 移动端商品详情响应对象
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
@ApiModel(value = "ProductDetailResponse对象", description = "移动端商品详情响应对象")
public class ProductDetailResponse implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "产品属性")
    private List<ProductAttribute> productAttr;

    @ApiModelProperty(value = "商品属性详情")
    private HashMap<String, ProductAttrValueResponse> productValue;

    @ApiModelProperty(value = "商品信息")
    private Product productInfo;

    @ApiModelProperty(value = "商户信息")
    private ProductMerchantResponse merchantInfo;

    @ApiModelProperty(value = "收藏标识")
    private Boolean userCollect;

    @ApiModelProperty(value = "保障服务")
    private List<ProductGuarantee> guaranteeList;

    @ApiModelProperty(value = "主商品ID，普通商品值为0")
    private Integer masterProductId = 0;

    @ApiModelProperty(value = "活动单次限购")
    private Integer oneQuota;

    @ApiModelProperty(value = "秒杀开始时间(时间戳)")
    private Long startTimeStamp = 0L;

    @ApiModelProperty(value = "秒杀结束时间(时间戳)")
    private Long endTimeStamp = 0L;

    @ApiModelProperty(value = "优惠券信息(商户)")
    private List<Coupon> couponList;

    @ApiModelProperty(value = "拼团信息")
    private GroupBuyActivityResponse groupBuyActivityResponse;
}
