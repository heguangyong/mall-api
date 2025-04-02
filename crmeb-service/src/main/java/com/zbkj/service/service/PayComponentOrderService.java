package com.zbkj.service.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zbkj.common.model.wechat.video.PayComponentOrder;
import com.zbkj.common.vo.wxvedioshop.ShopOrderAddResultVo;
import com.zbkj.common.vo.wxvedioshop.order.ShopOrderAddVo;

/**
 *
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
public interface PayComponentOrderService extends IService<PayComponentOrder> {

    /**
     * 创建组件订单
     * @param shopOrderAddVo 创建订单参数
     * @return ticket
     */
    ShopOrderAddResultVo create(ShopOrderAddVo shopOrderAddVo);

    /**
     * 通过订单号获取订单
     * @param orderNo 订单编号
     * @return PayComponentOrder
     */
    PayComponentOrder getByOrderNo(String orderNo);

    /**
     * 创建售后
     * @param orderNo 订单编号
     */
    void createAfterSale(String orderNo);
}
