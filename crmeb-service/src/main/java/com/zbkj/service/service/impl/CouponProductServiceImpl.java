package com.zbkj.service.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zbkj.common.model.coupon.CouponProduct;
import com.zbkj.service.dao.CouponProductDao;
import com.zbkj.service.service.CouponProductService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * CouponProductServiceImpl 接口实现
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
public class CouponProductServiceImpl extends ServiceImpl<CouponProductDao, CouponProduct> implements CouponProductService {

    @Resource
    private CouponProductDao dao;

    /**
     * 通过优惠券id查询列表
     * @param cid 优惠券ID
     * @return List
     */
    @Override
    public List<CouponProduct> findByCid(Integer cid) {
        LambdaQueryWrapper<CouponProduct> lqw = Wrappers.lambdaQuery();
        lqw.eq(CouponProduct::getCid, cid);
        return dao.selectList(lqw);
    }

    /**
     * 通过优惠券id删除
     * @param cid 优惠券ID
     * @return Boolean
     */
    @Override
    public Boolean deleteByCid(Integer cid) {
        LambdaUpdateWrapper<CouponProduct> wrapper = Wrappers.lambdaUpdate();
        wrapper.eq(CouponProduct::getCid, cid);
        return dao.delete(wrapper) > 0;
    }
}

