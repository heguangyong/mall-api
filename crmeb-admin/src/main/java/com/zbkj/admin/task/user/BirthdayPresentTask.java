package com.zbkj.admin.task.user;

import cn.hutool.core.date.DateUtil;
import com.zbkj.admin.service.MarketingActivityService;
import com.zbkj.admin.task.coupon.CouponOverdueTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @ClassName BirthdayPresentTask
 * @Description 生日有礼定时任务
 * @Author HZW
 * @Date 2023/6/5 14:39
 * @Version 1.0
 */
@Component("BirthdayPresentTask")
public class BirthdayPresentTask {

    //日志
    private static final Logger logger = LoggerFactory.getLogger(CouponOverdueTask.class);

    @Autowired
    private MarketingActivityService marketingActivityService;

    /**
     * 每天凌晨1点执行
     */
    public void sendBirthdayPresent() {
        // cron : 0 0 1 * * ?
        logger.info("---BirthdayPresentTask sendBirthdayPresent------produce Data with fixed rate task: Execution Time - {}", DateUtil.date());
        try {
            marketingActivityService.sendBirthdayPresent();
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("BirthdayPresentTask.sendBirthdayPresent" + " | msg : " + e.getMessage());
        }
    }

}
