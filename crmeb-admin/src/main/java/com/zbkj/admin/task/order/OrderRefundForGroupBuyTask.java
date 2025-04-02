package com.zbkj.admin.task.order;

import com.zbkj.common.utils.CrmebDateUtil;
import com.zbkj.service.service.groupbuy.GroupBuyRecordService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 拼团订单 拼团失败 后 退款操作
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
@Component("OrderRefundForGroupBuyTask")
public class OrderRefundForGroupBuyTask {
    //日志
    private static final Logger logger = LoggerFactory.getLogger(OrderRefundForGroupBuyTask.class);

    @Autowired
    private GroupBuyRecordService groupBuyRecordService;

    /**
     * 1分钟同步一次数据
     */
    public void orderRefund() {
        // cron : 0 */1 * * * ?
        logger.info("---OrderRefundTask task------produce Data with fixed rate task: Execution Time - {}", CrmebDateUtil.nowDateTime());
        try {
            groupBuyRecordService.groupBuyOrderRefund();
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("OrderRefundTask.task" + " | msg : " + e.getMessage());
        }
    }


    public void orderRefundEt() {
        // cron : 0 */1 * * * ?
        logger.info("---OrderRefundTask task------produce Data with fixed rate task: Execution Time - {}", CrmebDateUtil.nowDateTime());
        try {
            groupBuyRecordService.groupBuyOrderRefundEt();
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("OrderRefundTask.task" + " | msg : " + e.getMessage());
        }
    }
}
