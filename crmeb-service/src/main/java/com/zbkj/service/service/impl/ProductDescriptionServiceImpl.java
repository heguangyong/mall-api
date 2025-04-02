package com.zbkj.service.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zbkj.common.model.product.ProductDescription;
import com.zbkj.service.dao.ProductDescriptionDao;
import com.zbkj.service.service.ProductDescriptionService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * ProductDescriptionServiceImpl 接口实现
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
@Service
public class ProductDescriptionServiceImpl extends ServiceImpl<ProductDescriptionDao, ProductDescription> implements ProductDescriptionService {

    @Resource
    private ProductDescriptionDao dao;

    /**
     * 根据商品id和type删除对应描述
     * @param productId 商品id
     * @param type 商品类型
     * @param marketingType 营销类型
     */
    @Override
    public Boolean deleteByProductId(Integer productId, Integer type, Integer marketingType) {
        LambdaQueryWrapper<ProductDescription> lmq = Wrappers.lambdaQuery();
        lmq.eq(ProductDescription::getProductId, productId);
        lmq.eq(ProductDescription::getType, type);
        lmq.eq(ProductDescription::getMarketingType, marketingType);
        return dao.delete(lmq) > 0;
    }

    /**
     * 获取详情
     *
     * @param productId 商品id
     * @param type      商品类型
     * @return ProductDescription
     */
    @Override
    public ProductDescription getByProductIdAndType(Integer productId, Integer type) {
        LambdaQueryWrapper<ProductDescription> lqw = Wrappers.lambdaQuery();
        lqw.eq(ProductDescription::getProductId, productId);
        lqw.eq(ProductDescription::getType, type);
        return dao.selectOne(lqw);
    }

    /**
     * 获取详情
     *
     * @param productId     商品id
     * @param type          商品类型
     * @param marketingType 营销类型
     */
    @Override
    public ProductDescription getByProductIdAndType(Integer productId, Integer type, Integer marketingType) {
        LambdaQueryWrapper<ProductDescription> lqw = Wrappers.lambdaQuery();
        lqw.eq(ProductDescription::getProductId, productId);
        lqw.eq(ProductDescription::getType, type);
        lqw.eq(ProductDescription::getMarketingType, marketingType);
        return dao.selectOne(lqw);
    }
}

