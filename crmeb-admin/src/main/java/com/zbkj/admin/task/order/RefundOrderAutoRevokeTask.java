package com.zbkj.admin.task.order;

import com.zbkj.common.utils.CrmebDateUtil;
import com.zbkj.service.service.RefundOrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
     * 售后订单自动撤销定时任务
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
@Component("RefundOrderAutoRevokeTask")
public class RefundOrderAutoRevokeTask {
    //日志
    private static final Logger logger = LoggerFactory.getLogger(RefundOrderAutoRevokeTask.class);

    @Autowired
    private RefundOrderService refundOrderService;

    /**
     * 每天3点同步一次数据
     */
    public void autoRevoke() {
        // cron : 0 0 3 * * ?
        logger.info("---RefundOrderAutoRevokeTask task------produce Data with fixed rate task: Execution Time - {}", CrmebDateUtil.nowDateTime());
        try {
            refundOrderService.autoRevoke();
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("RefundOrderAutoRevokeTask.task" + " | msg : " + e.getMessage());
        }
    }

}
