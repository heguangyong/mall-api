package com.zbkj.service.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zbkj.common.model.merchant.MerchantInfo;
import com.zbkj.service.dao.MerchantInfoDao;
import com.zbkj.service.service.MerchantInfoService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
*  MerchantInfoServiceImpl 接口实现
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
public class MerchantInfoServiceImpl extends ServiceImpl<MerchantInfoDao, MerchantInfo> implements MerchantInfoService {

    @Resource
    private MerchantInfoDao dao;

    /**
     * 通过商户id获取
     * @param merId 商户id
     * @return MerchantInfo
     */
    @Override
    public MerchantInfo getByMerId(Integer merId) {
        LambdaQueryWrapper<MerchantInfo> lqw = Wrappers.lambdaQuery();
        lqw.eq(MerchantInfo::getMerId, merId);
        lqw.last(" limit 1");
        return dao.selectOne(lqw);
    }
}

