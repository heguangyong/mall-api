package com.zbkj.service.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zbkj.common.model.order.OrderInvoiceDetail;

import java.util.List;

/**
 * OrderInvoiceDetailService 接口
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
public interface OrderInvoiceDetailService extends IService<OrderInvoiceDetail> {

    /**
     * 获取发货单详情列表
     * @param invoiceIdList 发货单ID列表
     * @return 发货单详情列表
     */
    List<OrderInvoiceDetail> findInInvoiceIdList(List<Integer> invoiceIdList);
}