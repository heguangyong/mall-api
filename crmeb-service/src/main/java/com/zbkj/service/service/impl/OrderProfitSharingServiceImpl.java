package com.zbkj.service.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zbkj.common.model.order.OrderProfitSharing;
import com.zbkj.service.dao.OrderProfitSharingDao;
import com.zbkj.service.service.OrderProfitSharingService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * OrderProfitSharingServiceImpl 接口实现
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
public class OrderProfitSharingServiceImpl extends ServiceImpl<OrderProfitSharingDao, OrderProfitSharing> implements OrderProfitSharingService {

    @Resource
    private OrderProfitSharingDao dao;

    /**
     * 获取分账详情
     *
     * @param orderNo 商户订单号
     * @return OrderProfitSharing
     */
    @Override
    public OrderProfitSharing getByOrderNo(String orderNo) {
        LambdaQueryWrapper<OrderProfitSharing> lqw = Wrappers.lambdaQuery();
        lqw.eq(OrderProfitSharing::getOrderNo, orderNo);
        lqw.last(" limit 1");
        return dao.selectOne(lqw);
    }

    /**
     * 获取某一天的所有数据
     *
     * @param merId 商户id，0为所有商户
     * @param date  日期：年-月-日
     * @return List
     */
    @Override
    public List<OrderProfitSharing> findByDate(Integer merId, String date) {
        LambdaQueryWrapper<OrderProfitSharing> lqw = Wrappers.lambdaQuery();
        if (merId > 0) {
            lqw.eq(OrderProfitSharing::getMerId, merId);
        }
        lqw.apply("date_format(create_time, '%Y-%m-%d') = {0}", date);
        return dao.selectList(lqw);
    }

    /**
     * 获取某一月的所有数据
     *
     * @param merId 商户id，0为所有商户
     * @param month 日期：年-月
     * @return List
     */
    @Override
    public List<OrderProfitSharing> findByMonth(Integer merId, String month) {
        LambdaQueryWrapper<OrderProfitSharing> lqw = Wrappers.lambdaQuery();
        if (merId > 0) {
            lqw.eq(OrderProfitSharing::getMerId, merId);
        }
        lqw.apply("date_format(create_time, '%Y-%m') = {0}", month);
        return dao.selectList(lqw);
    }
}

