package com.zbkj.service.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.zbkj.common.model.wechat.live.WechatLiveAssistant;
import com.zbkj.common.request.PageParamRequest;
import com.zbkj.common.request.wxmplive.assistant.WechatLiveAssistantAddRequest;

import java.util.List;

/**
* @author dazongzi
* @description WechatLiveAssistantService 接口
* @date 2023-03-27
*/
public interface WechatLiveAssistantService extends IService<WechatLiveAssistant> {

    /**
     * 直播间小助手 列表
     * @param keywords 小助手微信号或者昵称
     * @param pageParamRequest 分页参数
     * @return 查询结果
     */
    List<WechatLiveAssistant> getList(String keywords, PageParamRequest pageParamRequest);

    Boolean saveUnique(WechatLiveAssistant wechatLiveAssistant);
    /**
     * 保存直播间小助手 根据微信号 检查唯一
     * @param wechatLiveAssistant 待保存小助手
     * @return 保存结果
     */
    List<WechatLiveAssistant> syncAssListUnique(List<WechatLiveAssistant> wechatLiveAssistant);

    Boolean deleteById(Integer id);

    Boolean edit(WechatLiveAssistantAddRequest wechatLiveAssistant);

    WechatLiveAssistant getAssistantInfo(Integer id);
}
