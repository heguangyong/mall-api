package com.zbkj.service.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zbkj.common.model.product.ProductCoupon;
import com.zbkj.service.dao.ProductCouponDao;
import com.zbkj.service.service.ProductCouponService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * StoreProductCouponServiceImpl 接口实现
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
public class ProductCouponServiceImpl extends ServiceImpl<ProductCouponDao, ProductCoupon>
        implements ProductCouponService {

    @Resource
    private ProductCouponDao dao;

    /**
     * @param productId 产品id
     */
    @Override
    public Boolean deleteByProductId(Integer productId) {
        LambdaQueryWrapper<ProductCoupon> lqw = new LambdaQueryWrapper<>();
        lqw.eq(ProductCoupon::getProductId, productId);
        return dao.delete(lqw) > 0;
    }

    /**
     * 根据商品id获取已关联优惠券信息
     *
     * @param productId 商品id
     * @return 已关联优惠券
     */
    @Override
    public List<ProductCoupon> getListByProductId(Integer productId) {
        LambdaQueryWrapper<ProductCoupon> lqw = new LambdaQueryWrapper<>();
        lqw.eq(ProductCoupon::getProductId, productId);
        return dao.selectList(lqw);
    }
}

