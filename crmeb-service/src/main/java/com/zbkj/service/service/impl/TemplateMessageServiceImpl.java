package com.zbkj.service.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zbkj.common.constants.WeChatConstants;
import com.zbkj.common.exception.CrmebException;
import com.zbkj.common.model.system.SystemNotification;
import com.zbkj.common.model.template.TemplateMessage;
import com.zbkj.common.result.CommonResultCode;
import com.zbkj.common.vo.ProgramTemplateMessageVo;
import com.zbkj.common.vo.SendProgramTemplateMessageItemVo;
import com.zbkj.common.vo.SendTemplateMessageItemVo;
import com.zbkj.common.vo.TemplateMessageVo;
import com.zbkj.service.dao.TemplateMessageDao;
import com.zbkj.service.service.SystemNotificationService;
import com.zbkj.service.service.TemplateMessageService;
import com.zbkj.service.service.WechatService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * TemplateMessageServiceImpl 接口实现
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
@Service
public class TemplateMessageServiceImpl extends ServiceImpl<TemplateMessageDao, TemplateMessage> implements TemplateMessageService {

    private static final Logger logger = LoggerFactory.getLogger(TemplateMessageServiceImpl.class);

    @Resource
    private TemplateMessageDao dao;

    @Autowired
    private WechatService wechatService;

    @Autowired
    private SystemNotificationService systemNotificationService;

    /**
     * 发送模板消息
     *
     * @param templateId 模板消息编号
     * @param temMap     内容Map
     * @param openId     微信用户openid
     */
    @Override
    public void pushTemplateMessage(Integer templateId, HashMap<String, String> temMap, String openId) {
        TemplateMessageVo templateMessageVo = new TemplateMessageVo();

        TemplateMessage templateMessage = getById(templateId);
        if (ObjectUtil.isNull(templateMessage) || StrUtil.isBlank(templateMessage.getContent())) {
            return;
        }
        templateMessageVo.setTemplate_id(templateMessage.getTempId());

        HashMap<String, SendTemplateMessageItemVo> hashMap = new HashMap<>();
        for (Map.Entry<String, String> entry : temMap.entrySet()) {
            hashMap.put(entry.getKey(), new SendTemplateMessageItemVo(entry.getValue()));
        }

        templateMessageVo.setData(hashMap);
        templateMessageVo.setTouser(openId);
        wechatService.sendPublicTemplateMessage(templateMessageVo);
    }

    /**
     * 发送小程序订阅消息
     *
     * @param templateId 模板消息编号
     * @param temMap     内容Map
     * @param openId     微信用户openId
     */
    @Override
    public void pushMiniTemplateMessage(Integer templateId, HashMap<String, String> temMap, String openId) {
        TemplateMessage templateMessage = getById(templateId);
        if (ObjectUtil.isNull(templateMessage) || StrUtil.isBlank(templateMessage.getContent())) {
            return;
        }

        ProgramTemplateMessageVo programTemplateMessageVo = new ProgramTemplateMessageVo();
        programTemplateMessageVo.setTemplate_id(templateMessage.getTempId());

        //组装关键字数据
        HashMap<String, SendProgramTemplateMessageItemVo> hashMap = new HashMap<>();
        temMap.forEach((key, value) -> hashMap.put(key, new SendProgramTemplateMessageItemVo(value)));

        programTemplateMessageVo.setData(hashMap);
        programTemplateMessageVo.setTouser(openId);
        wechatService.sendMiniSubscribeMessage(programTemplateMessageVo);
    }

    /**
     * 公众号模板消息同步
     *
     * @return Boolean
     */
    @Override
    public Boolean whcbqhnSync() {
        List<SystemNotification> notificationList = systemNotificationService.getListByWechat("public");
        List<Integer> wechatIdList = notificationList.stream().map(SystemNotification::getWechatId).collect(Collectors.toList());
        List<TemplateMessage> templateMessageList = getListByIdList(wechatIdList);
        if (CollUtil.isEmpty(templateMessageList)) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "请先添加公众号模板消息");
        }

        // TODO
        return true;
//        // 获取公众平台所有的微信模板，删除之
//        List<PublicMyTemplateVo> templateVoList = wechatNewService.getPublicMyTemplateList();
//        templateVoList.forEach(e -> wechatNewService.delPublicMyTemplate(e.getTemplate_id()));
//        // 将现在的模板保存到公众平台
//        templateMessageList.forEach(e -> {
//            String templateId = wechatNewService.apiAddPublicTemplate(e.getTempKey());
//            e.setTempId(templateId);
//        });
//        return updateBatchById(templateMessageList);
    }

    /**
     * 小程序订阅消息同步
     *
     * @return Boolean
     */
    @Override
    public Boolean routineSync() {
        List<SystemNotification> notificationList = systemNotificationService.getListByWechat(WeChatConstants.WECHAT_MINI_APPID);
        List<Integer> routineIdList = notificationList.stream().map(SystemNotification::getRoutineId).collect(Collectors.toList());
        List<TemplateMessage> templateMessageList = getListByIdList(routineIdList);
        if (CollUtil.isEmpty(templateMessageList)) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "请先配置小程序订阅消息");
        }
        // TODO
        return true;
//        // 获取当前帐号下的个人模板列表
//        List<RoutineMyTemplateVo> templateVoList = wechatNewService.getRoutineMyTemplateList();
//        // 删除原有模板
//        templateVoList.forEach(e -> wechatNewService.delRoutineMyTemplate(e.getPriTmplId()));
//        // 将现在的模板保存到小程序平台
//        templateMessageList.forEach(e -> {
//            // 获取小程序平台上的标准模板
//            List<RoutineTemplateKeyVo> templateKeyVoList = wechatNewService.getRoutineTemplateByWechat(e.getTempKey());
//            List<Integer> kidList = getRoutineKidList(e.getContent(), templateKeyVoList);
//            String priTmplId = wechatNewService.apiAddRoutineTemplate(e.getTempKey(), kidList);
//            e.setTempId(priTmplId);
//        });
//        return updateBatchById(templateMessageList);
    }

    /**
     * 通过模板编号获取列表
     *
     * @param idList 模板编号列表
     * @return List
     */
    private List<TemplateMessage> getListByIdList(List<Integer> idList) {
        LambdaQueryWrapper<TemplateMessage> lqw = Wrappers.lambdaQuery();
        lqw.in(TemplateMessage::getId, idList);
        return dao.selectList(lqw);
    }

    /**
     * 查询单条数据
     *
     * @param id Integer id
     */
    @Override
    public TemplateMessage infoException(Integer id) {
        TemplateMessage message = getById(id);
        if (ObjectUtil.isNull(message)) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "模板不存在");
        }
        return message;
    }

    /**
     * 获取模板列表
     *
     * @param tidList id数组
     * @return List
     */
    @Override
    public List<TemplateMessage> getByIdList(List<Integer> tidList) {
        LambdaQueryWrapper<TemplateMessage> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.in(TemplateMessage::getId, tidList);
        return dao.selectList(lambdaQueryWrapper);
    }
}

