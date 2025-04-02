package com.zbkj.service.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zbkj.common.model.order.OrderProfitSharing;

import java.util.List;

/**
 * StoreOrderProfitSharingService 接口
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
public interface OrderProfitSharingService extends IService<OrderProfitSharing> {

    /**
     * 获取分账详情
     *
     * @param orderNo 商户订单号
     * @return StoreOrderProfitSharing
     */
    OrderProfitSharing getByOrderNo(String orderNo);

    /**
     * 获取某一天的所有数据
     *
     * @param merId 商户id，0为所有商户
     * @param date  日期：年-月-日
     * @return List
     */
    List<OrderProfitSharing> findByDate(Integer merId, String date);

    /**
     * 获取某一月的所有数据
     *
     * @param merId 商户id，0为所有商户
     * @param month 日期：年-月
     * @return List
     */
    List<OrderProfitSharing> findByMonth(Integer merId, String month);
}