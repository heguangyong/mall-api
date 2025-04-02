package com.zbkj.service.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zbkj.common.model.alipay.AliPayInfo;
import com.zbkj.service.dao.AliPayInfoDao;
import com.zbkj.service.service.AliPayInfoService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.sql.Wrapper;

/**
 * @ClassName AliPayInfoServiceImpl
 * @Description AliPayInfoService 实现类
 * @Author HZW
 * @Date 2023/4/20 16:06
 * @Version 1.0
 */
@Service
public class AliPayInfoServiceImpl extends ServiceImpl<AliPayInfoDao, AliPayInfo> implements AliPayInfoService {

    @Resource
    private AliPayInfoDao dao;

    /**
     * 获取支付宝支付信息
     * @param outTradeNo 商户订单号
     */
    @Override
    public AliPayInfo getByOutTradeNo(String outTradeNo) {
        LambdaQueryWrapper<AliPayInfo> lqw = Wrappers.lambdaQuery();
        lqw.eq(AliPayInfo::getOutTradeNo, outTradeNo);
        lqw.orderByDesc(AliPayInfo::getId);
        lqw.last(" limit 1");
        return dao.selectOne(lqw);
    }
}
