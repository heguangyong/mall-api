package com.zbkj.service.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zbkj.common.model.merchant.MerchantBalanceRecord;

import java.math.BigDecimal;

/**
 * MerchantBalanceRecordService 接口
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
public interface MerchantBalanceRecordService extends IService<MerchantBalanceRecord> {

    /**
     * 通过关联单号查询记录
     * @param linkNo 关联单号
     * @return MerchantBalanceRecord
     */
    MerchantBalanceRecord getByLinkNo(String linkNo);

    /**
     * 获取商户冻结金额
     * @param merId 商户ID
     * @return 商户冻结金额
     */
    BigDecimal getFreezeAmountByMerId(Integer merId);

    /**
     * 余额解冻
     */
    void balanceThaw();
}