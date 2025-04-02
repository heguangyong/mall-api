package com.zbkj.admin.service;

import com.zbkj.common.vo.CommunityConfigVo;

/**
 * @ClassName CommunityService
 * @Description 社区服务
 * @Author HZW
 * @Date 2023/3/9 14:38
 * @Version 1.0
 */
public interface CommunityService {

    /**
     * 获取社区配置
     */
    CommunityConfigVo getConfig();

    /***
     * 更新社区配置
     * @Author HZW
     * @Date 2023/3/9 14:40
     */
    void updateConfig(CommunityConfigVo configVo);
}
