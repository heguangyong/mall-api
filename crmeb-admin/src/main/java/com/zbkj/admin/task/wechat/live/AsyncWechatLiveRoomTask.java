package com.zbkj.admin.task.wechat.live;

import com.zbkj.common.utils.CrmebDateUtil;
import com.zbkj.service.service.WechatLiveRoomService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @Auther: 大粽子
 * @Date: 2023/4/13 14:10
 * @Description: 微信小程序直播同步直播间状态
 */
@Component("AsyncWechatLiveRoomTask")
public class AsyncWechatLiveRoomTask {
    //日志
    private static final Logger logger = LoggerFactory.getLogger(AsyncWechatLiveRoomTask.class);

    @Autowired
    private WechatLiveRoomService weChatLiveRoomService;


    /**
     * 初始化5分钟执行一次
     */
    public void AsyncLiveRoomTask() {
        // 0 0/5 * * * ?
        logger.info("---AsyncWechatLiveRoomTask task------produce Data with fixed rate task: Execution Time - {}", CrmebDateUtil.nowDateTime());
        try {
            weChatLiveRoomService.syncLiveRoom();
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("AsyncWechatLiveRoomTask.task" + " | msg : " + e.getMessage());
        }
    }
}
