package com.zbkj.service.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zbkj.common.model.coupon.CouponProduct;

import java.util.List;

/**
 * CouponProductService 接口
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
public interface CouponProductService extends IService<CouponProduct> {

    /**
     * 通过优惠券id查询列表
     * @param cid 优惠券ID
     * @return List
     */
    List<CouponProduct> findByCid(Integer cid);

    /**
     * 通过优惠券id删除
     * @param cid 优惠券ID
     * @return Boolean
     */
    Boolean deleteByCid(Integer cid);
}
