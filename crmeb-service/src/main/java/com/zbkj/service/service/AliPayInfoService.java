package com.zbkj.service.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zbkj.common.model.alipay.AliPayInfo;

/**
 * @ClassName AliPayInfoService
 * @Description 支付宝信息服务类
 * @Author HZW
 * @Date 2023/4/20 16:05
 * @Version 1.0
 */
public interface AliPayInfoService extends IService<AliPayInfo> {

    /**
     * 获取支付宝支付信息
     * @param outTradeNo 商户订单号
     */
    AliPayInfo getByOutTradeNo(String outTradeNo);
}
