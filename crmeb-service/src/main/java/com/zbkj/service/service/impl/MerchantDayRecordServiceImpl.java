package com.zbkj.service.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zbkj.common.constants.DateConstants;
import com.zbkj.common.constants.RedisConstants;
import com.zbkj.common.model.record.MerchantDayRecord;
import com.zbkj.common.utils.RedisUtil;
import com.zbkj.service.dao.MerchantDayRecordDao;
import com.zbkj.service.service.MerchantDayRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * MerchantDayRecordServiceImpl 接口实现
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
@Service
public class MerchantDayRecordServiceImpl extends ServiceImpl<MerchantDayRecordDao, MerchantDayRecord> implements MerchantDayRecordService {

    @Resource
    private MerchantDayRecordDao dao;

    @Autowired
    private RedisUtil redisUtil;

    /**
     * 获取某一天商户的访客量
     *
     * @param merId 商户ID
     * @param date  日期：yyyy-MM-dd
     * @return 访客量
     */
    @Override
    public Integer getVisitorsByDate(Integer merId, String date) {
        if (DateUtil.date().toString(DateConstants.DATE_FORMAT_DATE).equals(date)) {
            // 当日访客量
            return getVisitorsByToday(merId, date);
        }
        LambdaQueryWrapper<MerchantDayRecord> lqw = Wrappers.lambdaQuery();
        lqw.eq(MerchantDayRecord::getMerId, merId);
        lqw.eq(MerchantDayRecord::getDate, date);
        lqw.last(" limit 1");
        MerchantDayRecord merchantDayRecord = dao.selectOne(lqw);
        return ObjectUtil.isNotNull(merchantDayRecord) ? merchantDayRecord.getVisitors() : 0;
    }

    /**
     * 获取当日访客量
     * @param merId 商户ID
     */
    private Integer getVisitorsByToday(Integer merId, String today) {
        int merchantVisitors = 0;
        Object visitorsObject = redisUtil.get(StrUtil.format(RedisConstants.MERCHANT_VISITORS_KEY, today, merId));
        if (ObjectUtil.isNotNull(visitorsObject)) {
            merchantVisitors = (Integer) visitorsObject;
        }
        return merchantVisitors;
    }
}

