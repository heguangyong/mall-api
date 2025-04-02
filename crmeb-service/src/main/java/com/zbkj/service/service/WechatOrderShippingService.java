package com.zbkj.service.service;

import com.zbkj.common.model.order.Order;

/**
 * 微信订单发货管理服务类
 *
 * @author Han
 * @version 1.0.0
 * @Date 2023/11/27
 */
public interface WechatOrderShippingService {

    /**
     * 录入发货信息
     */
    void uploadShippingInfo(String orderNo);

    /**
     * 录入发货信息
     * @param type normal-正常订单，verify-核销订单，fictitious-虚拟订单
     */
    void uploadShippingInfo(String orderNo, String type);

    /**
     * 批量录入充值订单发货
     */
    void batchUploadRechargeOrderShipping();

    /**
     * 批量录入SVIP订单发货
     */
    void batchUploadSvipOrderShipping();
}
