package com.zbkj.service.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zbkj.common.model.alipay.AliPayCallback;
import com.zbkj.service.dao.AliPayCallbackDao;
import com.zbkj.service.service.AliPayCallbackService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @ClassName AliPayCallbackServiceImpl
 * @Description 支付宝支付回调服务实现类
 * @Author HZW
 * @Date 2023/4/20 16:59
 * @Version 1.0
 */
@Service
public class AliPayCallbackServiceImpl extends ServiceImpl<AliPayCallbackDao, AliPayCallback> implements AliPayCallbackService {

    @Resource
    private AliPayCallbackDao dao;
}
