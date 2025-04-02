package com.zbkj.service.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zbkj.common.model.order.OrderInvoice;
import com.zbkj.common.model.order.OrderInvoiceDetail;
import com.zbkj.common.response.OrderInvoiceResponse;
import com.zbkj.service.dao.OrderInvoiceDao;
import com.zbkj.service.service.OrderInvoiceDetailService;
import com.zbkj.service.service.OrderInvoiceService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * OrderInvoiceServiceImpl 接口实现
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
public class OrderInvoiceServiceImpl extends ServiceImpl<OrderInvoiceDao, OrderInvoice> implements OrderInvoiceService {

    @Resource
    private OrderInvoiceDao dao;

    @Autowired
    private OrderInvoiceDetailService orderInvoiceDetailService;

    /**
     * 获取订单发货单列表
     * @param orderNo 订单号
     * @return 发货单列表
     */
    @Override
    public List<OrderInvoiceResponse> findByOrderNo(String orderNo) {
        LambdaQueryWrapper<OrderInvoice> lqw = Wrappers.lambdaQuery();
        lqw.eq(OrderInvoice::getOrderNo, orderNo);
        List<OrderInvoice> invoiceList = dao.selectList(lqw);
        if (CollUtil.isEmpty(invoiceList)) {
            return new ArrayList<>();
        }
        List<Integer> invoiceIdList = invoiceList.stream().map(OrderInvoice::getId).collect(Collectors.toList());
        List<OrderInvoiceDetail> invoiceDetailList = orderInvoiceDetailService.findInInvoiceIdList(invoiceIdList);
        return invoiceList.stream().map(invoice -> {
            OrderInvoiceResponse response = new OrderInvoiceResponse();
            BeanUtils.copyProperties(invoice, response);
            List<OrderInvoiceDetail> detailList = invoiceDetailList.stream().filter(e -> e.getInvoiceId().equals(invoice.getId())).collect(Collectors.toList());
            response.setDetailList(detailList);
            return response;
        }).collect(Collectors.toList());

    }
}

