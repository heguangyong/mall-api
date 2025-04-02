package com.zbkj.service.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zbkj.common.model.order.RefundOrderStatus;

import java.util.List;

/**
*  RefundOrderStatusService 接口
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
public interface RefundOrderStatusService extends IService<RefundOrderStatus> {

    /**
     * 添加退款单日志记录
     * @param refundOrderNo 退款单号
     * @param changeType 操作类型
     * @param message 备注信息
     */
    void add(String refundOrderNo, String changeType, String message);

    /**
     * 获取退款单记录列表
     * @param refundOrderNo 退款单号
     */
    List<RefundOrderStatus> findListByRefundOrderNo(String refundOrderNo);
}