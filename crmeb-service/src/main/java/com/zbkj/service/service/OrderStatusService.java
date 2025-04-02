package com.zbkj.service.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zbkj.common.model.order.OrderStatus;

/**
*  OrderStatusService 接口
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
public interface OrderStatusService extends IService<OrderStatus> {

    /**
     * 创建日志
     * @param orderNo 订单号
     * @param changeType 操作类型
     * @param changeMessage 操作备注
     * @return Boolean
     */
    Boolean createLog(String orderNo, String changeType, String changeMessage);
}