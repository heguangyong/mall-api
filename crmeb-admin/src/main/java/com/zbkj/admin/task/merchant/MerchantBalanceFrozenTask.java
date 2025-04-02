package com.zbkj.admin.task.merchant;


import com.zbkj.common.utils.CrmebDateUtil;
import com.zbkj.service.service.MerchantBalanceRecordService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 商户余额解冻定时任务
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
@Component("MerchantBalanceFrozenTask")
public class MerchantBalanceFrozenTask {

    //日志
    private static final Logger logger = LoggerFactory.getLogger(MerchantBalanceFrozenTask.class);

    @Autowired
    private MerchantBalanceRecordService merchantBalanceRecordService;

    /**
     * 1小时同步一次数据
     */
    public void brokerageFrozen() {
        // cron : 0 0 */1 * * ?
        logger.info("---MerchantBalanceFrozenTask task------produce Data with fixed rate task: Execution Time - {}", CrmebDateUtil.nowDateTime());
        try {
            merchantBalanceRecordService.balanceThaw();
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("MerchantBalanceFrozenTask.task" + " | msg : " + e.getMessage());
        }
    }
}
