package com.zbkj.service.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zbkj.common.model.order.OrderInvoiceDetail;
import com.zbkj.service.dao.OrderInvoiceDetailDao;
import com.zbkj.service.service.OrderInvoiceDetailService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * OrderInvoiceDetailServiceImpl 接口实现
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
public class OrderInvoiceDetailServiceImpl extends ServiceImpl<OrderInvoiceDetailDao, OrderInvoiceDetail> implements OrderInvoiceDetailService {

    @Resource
    private OrderInvoiceDetailDao dao;

    /**
     * 获取发货单详情列表
     * @param invoiceIdList 发货单ID列表
     * @return 发货单详情列表
     */
    @Override
    public List<OrderInvoiceDetail> findInInvoiceIdList(List<Integer> invoiceIdList) {
        LambdaQueryWrapper<OrderInvoiceDetail> lqw = Wrappers.lambdaQuery();
        lqw.in(OrderInvoiceDetail::getInvoiceId, invoiceIdList);
        return dao.selectList(lqw);
    }
}

