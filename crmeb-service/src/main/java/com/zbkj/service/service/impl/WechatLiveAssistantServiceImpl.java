package com.zbkj.service.service.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.URLUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.PageHelper;
import com.google.common.base.Joiner;
import com.zbkj.common.exception.CrmebException;
import com.zbkj.common.model.admin.SystemAdmin;
import com.zbkj.common.model.wechat.live.WechatLiveAssistant;
import com.zbkj.common.request.PageParamRequest;
import com.zbkj.common.request.wxmplive.assistant.WechatLiveAssistantAddRequest;
import com.zbkj.common.utils.SecurityUtil;
import com.zbkj.common.vo.LoginUserVo;
import com.zbkj.service.dao.wechat.live.WechatLiveAssistantDao;
import com.zbkj.service.service.WechatLiveAssistantService;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Auther: 大粽子
 * @Date: 2023/3/27 18:43
 * @Description: 微信小程序直播 小助手
 */
@Service
public class WechatLiveAssistantServiceImpl extends ServiceImpl<WechatLiveAssistantDao, WechatLiveAssistant> implements WechatLiveAssistantService {

    private static final Logger logger = LoggerFactory.getLogger(WechatLiveAssistantServiceImpl.class);

    @Resource
    private WechatLiveAssistantDao wechatLiveAssistantDao;

    /**
     * 直播间小助手 列表
     *
     * @param keywords         小助手微信号或者昵称
     * @param pageParamRequest 分页参数
     * @return 查询结果
     */
    @Override
    public List<WechatLiveAssistant> getList(String keywords, PageParamRequest pageParamRequest) {
        PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());
        LoginUserVo currentUser = SecurityUtil.getLoginUserVo();
        LambdaQueryWrapper<WechatLiveAssistant> lambdaQueryWrapper = Wrappers.lambdaQuery();
        if (ObjectUtil.isNotEmpty(keywords)) {
            lambdaQueryWrapper.like(WechatLiveAssistant::getWechat, URLUtil.decode(keywords)).or()
                    .like(WechatLiveAssistant::getWechatNickname, URLUtil.decode(keywords));
        }
        lambdaQueryWrapper.eq(WechatLiveAssistant::getMerId, currentUser.getUser().getMerId());
        lambdaQueryWrapper.orderByDesc(WechatLiveAssistant::getCreateTime);
        return wechatLiveAssistantDao.selectList(lambdaQueryWrapper);
    }

    @Override
    public Boolean saveUnique(WechatLiveAssistant wechatLiveAssistant) {
        LambdaQueryWrapper<WechatLiveAssistant> lambdaQueryWrapper = Wrappers.lambdaQuery();
        lambdaQueryWrapper.eq(WechatLiveAssistant::getWechat, wechatLiveAssistant.getWechat());
        List<WechatLiveAssistant> wechatLiveAssistants = wechatLiveAssistantDao.selectList(lambdaQueryWrapper);
        if (wechatLiveAssistants.size() > 0) {
            throw new CrmebException("微信直播小助手已经存在:" + wechatLiveAssistant.getWechat());
        }
        wechatLiveAssistant.setMerId(SecurityUtil.getLoginUserVo().getUser().getMerId());
        return wechatLiveAssistantDao.insert(wechatLiveAssistant) > 0;
    }

    /**
     * 暂时先不考虑多端操作还需要同步的情况
     * 保存直播间小助手 根据微信号 检查唯一
     *
     * @param wechatLiveAssistant 待保存小助手
     * @return 保存结果
     */
    @Override
    public List<WechatLiveAssistant> syncAssListUnique(List<WechatLiveAssistant> wechatLiveAssistant) {

        List<WechatLiveAssistant> resultList = new ArrayList<>();
        // 查询出更新的
        List<String> currentAssWechatList = wechatLiveAssistant.stream().map(WechatLiveAssistant::getWechat).collect(Collectors.toList());
        LambdaQueryWrapper<WechatLiveAssistant> lambdaQueryWrapper = Wrappers.lambdaQuery();
        lambdaQueryWrapper.in(WechatLiveAssistant::getWechat, Joiner.on(",").join(currentAssWechatList));
        List<WechatLiveAssistant> currentExitAssList = wechatLiveAssistantDao.selectList(lambdaQueryWrapper);

        for (WechatLiveAssistant assistant : currentExitAssList) {
            List<WechatLiveAssistant> existAss = wechatLiveAssistant.stream().filter(ass -> ass.getWechat().equals(assistant.getWechat())).collect(Collectors.toList());
            if (existAss.size() == 0) {
                logger.error("微信小程序 - 直播间 - 同步小助手 更新时存在漏网之鱼:{}:本地没有找到", JSON.toJSONString(assistant));
                break;
            }
            WechatLiveAssistant currentAssForUpdate = existAss.get(0);
            BeanUtils.copyProperties(currentAssForUpdate, assistant);
            assistant.setId(currentAssForUpdate.getId());
        }
        boolean assBatchUpdateResult = updateBatchById(currentExitAssList);
        resultList.addAll(currentExitAssList);

        // 查询出新增的
        List<WechatLiveAssistant> wechatLiveAssistantListForInsert = new ArrayList<>();
        for (WechatLiveAssistant assistant : wechatLiveAssistant) {
            List<WechatLiveAssistant> notExistAss = currentExitAssList.stream().filter(ass -> !ass.getWechat().equals(assistant.getWechat())).collect(Collectors.toList());
            if (notExistAss.size() > 0) {
                WechatLiveAssistant currentASS = notExistAss.get(0);
                wechatLiveAssistantListForInsert.add(currentASS);
            }
        }
        resultList.addAll(wechatLiveAssistantListForInsert);
        return resultList;
    }

    @Override
    public Boolean deleteById(Integer id) {
        getAssistantInfo(id);
        return removeById(id);
    }

    @Override
    public Boolean edit(WechatLiveAssistantAddRequest wechatLiveAssistant) {
        getAssistantInfo(wechatLiveAssistant.getId());
        WechatLiveAssistant assistant = new WechatLiveAssistant();
        BeanUtils.copyProperties(wechatLiveAssistant, assistant);
        return updateById(assistant);
    }

    @Override
    public WechatLiveAssistant getAssistantInfo(Integer id) {
        WechatLiveAssistant liveAssistant = getById(id);
        SystemAdmin admin = SecurityUtil.getLoginUserVo().getUser();
        if (ObjectUtil.isNull(liveAssistant) || !admin.getMerId().equals(liveAssistant.getMerId())) {
            throw new CrmebException("直播助手不存在");
        }
        return liveAssistant;
    }
}
