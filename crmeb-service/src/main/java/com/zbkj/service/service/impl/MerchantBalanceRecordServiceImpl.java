package com.zbkj.service.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zbkj.common.constants.Constants;
import com.zbkj.common.constants.IntegralRecordConstants;
import com.zbkj.common.model.merchant.Merchant;
import com.zbkj.common.model.merchant.MerchantBalanceRecord;
import com.zbkj.service.dao.MerchantBrokerageRecordDao;
import com.zbkj.service.service.MerchantBalanceRecordService;
import com.zbkj.service.service.MerchantService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.List;

/**
 * MerchantBalanceRecordServiceImpl 接口实现
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
@Slf4j
@Service
public class MerchantBalanceRecordServiceImpl extends ServiceImpl<MerchantBrokerageRecordDao, MerchantBalanceRecord> implements MerchantBalanceRecordService {

    @Resource
    private MerchantBrokerageRecordDao dao;

    @Autowired
    private MerchantService merchantService;
    @Autowired
    private TransactionTemplate transactionTemplate;

    /**
     * 通过关联单号查询记录
     * @param linkNo 关联单号
     * @return MerchantBalanceRecord
     */
    @Override
    public MerchantBalanceRecord getByLinkNo(String linkNo) {
        LambdaQueryWrapper<MerchantBalanceRecord> lqw = Wrappers.lambdaQuery();
        lqw.eq(MerchantBalanceRecord::getLinkNo, linkNo);
        lqw.last("limit 1");
        return dao.selectOne(lqw);
    }

    /**
     * 获取商户冻结金额
     * @param merId 商户ID
     * @return 商户冻结金额
     */
    @Override
    public BigDecimal getFreezeAmountByMerId(Integer merId) {
        LambdaQueryWrapper<MerchantBalanceRecord> lqw = Wrappers.lambdaQuery();
        lqw.select(MerchantBalanceRecord::getAmount);
        lqw.eq(MerchantBalanceRecord::getMerId, merId);
        lqw.eq(MerchantBalanceRecord::getType, 1);
        lqw.eq(MerchantBalanceRecord::getStatus, 2);
        lqw.eq(MerchantBalanceRecord::getMerId, merId);
        List<MerchantBalanceRecord> recordList = dao.selectList(lqw);
        if (CollUtil.isEmpty(recordList)) {
            return BigDecimal.ZERO;
        }
        return recordList.stream().map(MerchantBalanceRecord::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * 余额解冻
     */
    @Override
    public void balanceThaw() {
        List<MerchantBalanceRecord> thawList = findThawList();
        if (CollUtil.isEmpty(thawList)) {
            return;
        }
        for (MerchantBalanceRecord record : thawList) {
            Merchant merchant = merchantService.getById(record.getMerId());
            if (ObjectUtil.isNull(merchant)) {
                continue;
            }
            record.setStatus(IntegralRecordConstants.INTEGRAL_RECORD_STATUS_COMPLETE);
            BigDecimal balance = merchant.getBalance().add(record.getAmount());
            record.setBalance(balance);
            record.setUpdateTime(DateUtil.date());

            // 解冻
            Boolean execute = transactionTemplate.execute(e -> {
                updateById(record);
                merchantService.operationBalance(merchant.getId(), record.getAmount(), Constants.OPERATION_TYPE_ADD);
                return Boolean.TRUE;
            });
            if (!execute) {
                log.error(StrUtil.format("商户余额解冻处理—解冻出错，记录id = {}", record.getId()));
            }
        }
    }

    /**
     * 获取冻结记录
     */
    private List<MerchantBalanceRecord> findThawList() {
        LambdaQueryWrapper<MerchantBalanceRecord> lqw = Wrappers.lambdaQuery();
        lqw.le(MerchantBalanceRecord::getThawTime, System.currentTimeMillis());
        lqw.eq(MerchantBalanceRecord::getLinkType, IntegralRecordConstants.INTEGRAL_RECORD_LINK_TYPE_ORDER);
        lqw.eq(MerchantBalanceRecord::getType, IntegralRecordConstants.INTEGRAL_RECORD_TYPE_ADD);
        lqw.eq(MerchantBalanceRecord::getStatus, IntegralRecordConstants.INTEGRAL_RECORD_STATUS_FROZEN);
        return dao.selectList(lqw);
    }
}

