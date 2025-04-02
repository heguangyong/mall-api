package com.zbkj.service.service;

/**
 * StatisticsTaskService 接口
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
public interface StatisticsTaskService {

    /**
     * 每天零点的自动统计
     */
    void autoStatistics();

    /**
     * 每日帐单定时任务
     * 每天1点执行
     */
    void dailyStatement();

    /**
     * 每月帐单定时任务
     * 每月第一天2点执行
     */
    void monthStatement();

    /**
     * 每日财务流水定时任务
     * 每天1点执行
     */
    void dailySummaryFinancialStatement();
}
