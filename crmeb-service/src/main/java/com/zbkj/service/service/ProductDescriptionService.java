package com.zbkj.service.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zbkj.common.model.product.ProductDescription;

/**
 * StoreProductDescriptionService 接口
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
public interface ProductDescriptionService extends IService<ProductDescription> {

    /**
     * 根据商品id和type删除对应描述
     * @param productId 商品id
     * @param type 商品类型
     * @param marketingType 营销类型
     */
    Boolean deleteByProductId(Integer productId, Integer type, Integer marketingType);

    /**
     * 获取详情
     * @param productId 商品id
     * @param type 商品类型
     */
    ProductDescription getByProductIdAndType(Integer productId, Integer type);

    /**
     * 获取详情
     * @param productId 商品id
     * @param type 商品类型
     * @param marketingType 营销类型
     */
    ProductDescription getByProductIdAndType(Integer productId, Integer type, Integer marketingType);
}
