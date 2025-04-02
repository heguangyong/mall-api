package com.zbkj.service.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zbkj.common.model.order.OrderStatus;
import com.zbkj.service.dao.OrderStatusDao;
import com.zbkj.service.service.OrderStatusService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
*  OrderStatusServiceImpl 接口实现
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
@Service
public class OrderStatusServiceImpl extends ServiceImpl<OrderStatusDao, OrderStatus> implements OrderStatusService {

    @Resource
    private OrderStatusDao dao;

    /**
     * 创建日志
     * @param orderNo 订单号
     * @param changeType 操作类型
     * @param changeMessage 操作备注
     * @return Boolean
     */
    @Override
    public Boolean createLog(String orderNo, String changeType, String changeMessage) {
        OrderStatus orderStatus = new OrderStatus();
        orderStatus.setOrderNo(orderNo);
        orderStatus.setChangeType(changeType);
        orderStatus.setChangeMessage(changeMessage);
        return save(orderStatus);
    }
}

