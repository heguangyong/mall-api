package com.zbkj.service.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zbkj.common.model.product.ProductAttr;
import com.zbkj.service.dao.ProductAttrDao;
import com.zbkj.service.service.ProductAttrService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * ProductAttrServiceImpl 接口实现
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
public class ProductAttrServiceImpl extends ServiceImpl<ProductAttrDao, ProductAttr>
        implements ProductAttrService {

    @Resource
    private ProductAttrDao dao;

    /**
     * 根据id删除商品
     * @param productId 待删除商品id
     * @param type 类型区分是是否添加营销
     */
    @Override
    public void removeByProductId(Integer productId,int type) {
        LambdaQueryWrapper<ProductAttr> lambdaQW = Wrappers.lambdaQuery();
        lambdaQW.eq(ProductAttr::getProductId, productId).eq(ProductAttr::getType,type);
        dao.delete(lambdaQW);
    }

    /**
     * 删除商品规格
     * @param productId 商品id
     * @param type 商品类型
     * @param marketingType 营销类型
     * @return Boolean
     */
    @Override
    public Boolean deleteByProductIdAndType(Integer productId, Integer type, Integer marketingType) {
        LambdaUpdateWrapper<ProductAttr> luw = Wrappers.lambdaUpdate();
        luw.set(ProductAttr::getIsDel, true);
        luw.eq(ProductAttr::getProductId, productId);
        luw.eq(ProductAttr::getType, type);
        luw.eq(ProductAttr::getMarketingType, marketingType);
        return update(luw);
    }

    /**
     * 批量删除商品规格
     * @param productIds 商品id
     * @param marketingType 营销类型
     * @return Boolean
     */
    @Override
    public Boolean batchDeleteByProductIdAndMarketingType(List<Integer> productIds, Integer marketingType) {
        LambdaUpdateWrapper<ProductAttr> luw = Wrappers.lambdaUpdate();
        luw.set(ProductAttr::getIsDel, true);
        luw.in(ProductAttr::getProductId, productIds);
        luw.eq(ProductAttr::getMarketingType, marketingType);
        return update(luw);
    }

    /**
     * 获取商品规格列表
     *
     * @param productId 商品id
     * @param type      商品类型
     * @return List
     */
    @Override
    public List<ProductAttr> getListByProductIdAndType(Integer productId, Integer type) {
        LambdaQueryWrapper<ProductAttr> lqw = Wrappers.lambdaQuery();
        lqw.eq(ProductAttr::getProductId, productId);
        lqw.eq(ProductAttr::getType, type);
        lqw.eq(ProductAttr::getIsDel, false);
        return dao.selectList(lqw);
    }

    /**
     * 获取商品规格列表
     * @param productId 商品id
     * @param type 商品类型
     * @param marketingType 营销类型
     * @return List
     */
    @Override
    public List<ProductAttr> getListByProductIdAndType(Integer productId, Integer type, Integer marketingType) {
        LambdaQueryWrapper<ProductAttr> lqw = Wrappers.lambdaQuery();
        lqw.eq(ProductAttr::getProductId, productId);
        lqw.eq(ProductAttr::getType, type);
        lqw.eq(ProductAttr::getMarketingType, marketingType);
        lqw.eq(ProductAttr::getIsDel, false);
        return dao.selectList(lqw);
    }
}

