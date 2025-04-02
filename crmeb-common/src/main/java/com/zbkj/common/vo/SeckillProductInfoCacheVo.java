package com.zbkj.common.vo;

import com.zbkj.common.model.product.Product;
import com.zbkj.common.model.product.ProductAttr;
import com.zbkj.common.model.product.ProductAttrValue;
import com.zbkj.common.model.product.ProductGuarantee;
import com.zbkj.common.model.seckill.SeckillProduct;
import com.zbkj.common.response.ProductAttrValueResponse;
import com.zbkj.common.response.ProductMerchantResponse;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

/**
 * 秒杀商品信息缓存对象
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
@ApiModel(value = "SeckillProductInfoCacheVo", description = "秒杀商品信息缓存对象")
public class SeckillProductInfoCacheVo implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "产品属性")
    private List<ProductAttr> productAttrList;

    @ApiModelProperty(value = "商品属性详情")
    private List<ProductAttrValue> productAttrValueList;

    @ApiModelProperty(value = "商品信息")
    private SeckillProduct seckillProduct;

    @ApiModelProperty(value = "商户信息")
    private ProductMerchantResponse merchantInfo;

    @ApiModelProperty(value = "保障服务")
    private List<ProductGuarantee> guaranteeList;

    @ApiModelProperty(value = "主商品ID，普通商品值为0")
    private Integer masterProductId = 0;

    @ApiModelProperty(value = "活动单次限购")
    private Integer oneQuota;
}
