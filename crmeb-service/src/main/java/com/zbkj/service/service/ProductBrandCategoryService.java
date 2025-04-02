package com.zbkj.service.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zbkj.common.model.product.ProductBrandCategory;

import java.util.List;

/**
*  ProductBrandCategoryService 接口
*  +----------------------------------------------------------------------
*  | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
*  +----------------------------------------------------------------------
*  | Copyright (c) 2016~2025 https://www.crmeb.com All rights reserved.
*  +----------------------------------------------------------------------
*  | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
*  +----------------------------------------------------------------------
*  | Author: CRMEB Team <admin@crmeb.com>
*  +----------------------------------------------------------------------
*/
public interface ProductBrandCategoryService extends IService<ProductBrandCategory> {

    /**
     * 通过品牌id删除
     * @param bid 品牌id
     * @return Boolean
     */
    Boolean deleteByBid(Integer bid);

    /**
     * 获取关联的分类id
     * @param brandId 品牌id
     * @return List
     */
    List<ProductBrandCategory> getListByBrandId(Integer brandId);

    /**
     * 是否存在分类
     * @param categoryId 分类id
     * @return Boolean
     */
    Boolean isExistCategory(Integer categoryId);
}
