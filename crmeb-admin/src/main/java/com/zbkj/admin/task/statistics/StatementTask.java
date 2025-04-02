package com.zbkj.admin.task.statistics;

import com.zbkj.common.utils.CrmebDateUtil;
import com.zbkj.service.service.StatisticsTaskService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 帐单定时任务（每天一点）
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
@Component("StatementTask")
public class StatementTask {

    //日志
    private static final Logger logger = LoggerFactory.getLogger(StatementTask.class);

    @Autowired
    private StatisticsTaskService statisticsTaskService;

    /**
     * 每日帐单定时任务
     * 每天1点执行
     */
    public void dailyStatement() {
        // cron : 0 0 1 * * ?
        logger.info("---DailyStatementTask dailyStatement------produce Data with fixed rate task: Start Time - {}", CrmebDateUtil.nowDateTime());
        try {
            statisticsTaskService.dailyStatement();
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("DailyStatementTask.dailyStatement" + " | msg : " + e.getMessage());
        }
        logger.info("---DailyStatementTask dailyStatement------produce Data with fixed rate task: End Time - {}", CrmebDateUtil.nowDateTime());
    }

    /**
     * 每月帐单定时任务
     * 每月第一天2点执行
     */
    public void monthStatement() {
        // cron : 0 0 2 1 * ?
        logger.info("---DailyStatementTask monthStatement------produce Data with fixed rate task: Start Time - {}", CrmebDateUtil.nowDateTime());
        try {
            statisticsTaskService.monthStatement();
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("DailyStatementTask.monthStatement" + " | msg : " + e.getMessage());
        }
        logger.info("---DailyStatementTask monthStatement------produce Data with fixed rate task: End Time - {}", CrmebDateUtil.nowDateTime());
    }
}
