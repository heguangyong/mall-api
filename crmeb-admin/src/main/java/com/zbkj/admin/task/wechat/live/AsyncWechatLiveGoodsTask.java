package com.zbkj.admin.task.wechat.live;

import com.zbkj.common.utils.CrmebDateUtil;
import com.zbkj.service.service.WechatLiveGoodsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @Auther: 大粽子
 * @Date: 2023/4/12 16:59
 * @Description: 微信直播商品状态同步
 */
@Component("AsyncWechatLiveGoodsTask")
public class AsyncWechatLiveGoodsTask {
    //日志
    private static final Logger logger = LoggerFactory.getLogger(AsyncWechatLiveGoodsTask.class);

    @Autowired
    private WechatLiveGoodsService weChatLiveGoodsService;


    /**
     * 初始化5分钟执行一次
     */
    public void AsyncGoodsTask() {
        // 0 0/5 * * * ?
        logger.info("---AsyncWechatLiveGoodsTask task------produce Data with fixed rate task: Execution Time - {}", CrmebDateUtil.nowDateTime());
        try {
            weChatLiveGoodsService.getGoodsWareHouse();
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("AsyncWechatLiveGoodsTask.task" + " | msg : " + e.getMessage());
        }
    }
}
