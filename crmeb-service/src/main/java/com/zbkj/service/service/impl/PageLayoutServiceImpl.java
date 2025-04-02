package com.zbkj.service.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import com.zbkj.common.constants.*;
import com.zbkj.common.exception.CrmebException;
import com.zbkj.common.model.system.GroupConfig;
import com.zbkj.common.model.system.SystemGroupData;
import com.zbkj.common.request.SystemFormItemCheckRequest;
import com.zbkj.common.response.PageLayoutBottomNavigationResponse;
import com.zbkj.common.response.PageLayoutIndexResponse;
import com.zbkj.common.result.CommonResultCode;
import com.zbkj.common.utils.CrmebUtil;
import com.zbkj.common.vo.MyRecord;
import com.zbkj.common.vo.SplashAdConfigVo;
import com.zbkj.common.vo.SplashAdDataVo;
import com.zbkj.service.service.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 页面布局接口实现类
 *  +----------------------------------------------------------------------
 *  | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
 *  +----------------------------------------------------------------------
 *  | Copyright (c) 2016~2025 https://www.crmeb.com All rights reserved.
 *  +----------------------------------------------------------------------
 *  | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
 *  +----------------------------------------------------------------------
 *  | Author: CRMEB Team <admin@crmeb.com>
 *  +----------------------------------------------------------------------
 */
@Service
public class PageLayoutServiceImpl implements PageLayoutService {

    @Autowired
    private SystemGroupDataService systemGroupDataService;
    @Autowired
    private TransactionTemplate transactionTemplate;
    @Autowired
    private SystemAttachmentService systemAttachmentService;
    @Autowired
    private SystemConfigService systemConfigService;
    @Autowired
    private ArticleService articleService;
    @Autowired
    private GroupConfigService groupConfigService;

    /**
     * 页面首页
     * @return 首页信息
     */
    @Override
    public PageLayoutIndexResponse index() {
        PageLayoutIndexResponse response = new PageLayoutIndexResponse();
        // 首页banner
        List<SystemGroupData> bannerList = systemGroupDataService.findListByGid(GroupDataConstants.GROUP_DATA_ID_INDEX_BANNER);
        response.setIndexBanner(convertData(bannerList));
        // 首页金刚区
        List<SystemGroupData> menuList = systemGroupDataService.findListByGid(GroupDataConstants.GROUP_DATA_ID_INDEX_MENU);
        response.setIndexMenu(convertData(menuList));
        // 首页新闻
        response.setIndexNews(articleService.getIndexHeadline());
        // 个人中心页服务
        List<SystemGroupData> userMenuList = systemGroupDataService.findListByGid(GroupDataConstants.GROUP_DATA_ID_USER_CENTER_MENU);
        response.setUserMenu(convertData(userMenuList));
        // 个人中心页banner
        List<SystemGroupData> userBannerList = systemGroupDataService.findListByGid(GroupDataConstants.GROUP_DATA_ID_USER_CENTER_BANNER);
        response.setUserBanner(convertData(userBannerList));
        // 用户默认头像
        response.setUserDefaultAvatar(systemConfigService.getValueByKey(SysConfigConstants.USER_DEFAULT_AVATAR_CONFIG_KEY));
        // 首页logo 1.3 版本DIY已经替代
//        response.setIndexLogo(systemConfigService.getValueByKey(SysConfigConstants.CONFIG_KEY_MOBILE_TOP_LOGO));
        return response;
    }

    /**
     * 页面首页banner保存
     * @param jsonObject 数据
     * @return Boolean
     */
    @Override
    public Boolean indexBannerSave(JSONObject jsonObject) {
        List<JSONObject> indexBanner = CrmebUtil.jsonArrayToJsonObjectList(jsonObject.getJSONArray("indexBanner"));
        List<SystemGroupData> dataList = convertGroupData(indexBanner, GroupDataConstants.GROUP_DATA_ID_INDEX_BANNER);
        return transactionTemplate.execute(e -> {
            // 先删除历史数据
            systemGroupDataService.deleteByGid(GroupDataConstants.GROUP_DATA_ID_INDEX_BANNER);
            // 保存新数据
            systemGroupDataService.saveBatch(dataList, 100);
            return Boolean.TRUE;
        });
    }

    /**
     * 页面首页menu保存
     * @param jsonObject 数据
     * @return Boolean
     */
    @Override
    public Boolean indexMenuSave(JSONObject jsonObject) {
        List<JSONObject> indexMenu = CrmebUtil.jsonArrayToJsonObjectList(jsonObject.getJSONArray("indexMenu"));
        List<SystemGroupData> dataList = convertGroupData(indexMenu, GroupDataConstants.GROUP_DATA_ID_INDEX_MENU);
        return transactionTemplate.execute(e -> {
            // 先删除历史数据
            systemGroupDataService.deleteByGid(GroupDataConstants.GROUP_DATA_ID_INDEX_MENU);
            // 保存新数据
            systemGroupDataService.saveBatch(dataList, 100);
            return Boolean.TRUE;
        });
    }

    /**
     * 页面用户中心banner保存
     * @param jsonObject 数据
     * @return Boolean
     */
    @Override
    public Boolean userBannerSave(JSONObject jsonObject) {
        List<JSONObject> userBanner = CrmebUtil.jsonArrayToJsonObjectList(jsonObject.getJSONArray("userBanner"));
        List<SystemGroupData> dataList = convertGroupData(userBanner, GroupDataConstants.GROUP_DATA_ID_USER_CENTER_BANNER);
        return transactionTemplate.execute(e -> {
            // 先删除历史数据
            systemGroupDataService.deleteByGid(GroupDataConstants.GROUP_DATA_ID_USER_CENTER_BANNER);
            // 保存新数据
            systemGroupDataService.saveBatch(dataList, 100);
            return Boolean.TRUE;
        });
    }

    /**
     * 页面用户中心导航保存
     * @param jsonObject 数据
     * @return Boolean
     */
    @Override
    public Boolean userMenuSave(JSONObject jsonObject) {
        List<JSONObject> userMenu = CrmebUtil.jsonArrayToJsonObjectList(jsonObject.getJSONArray("userMenu"));
        List<SystemGroupData> dataList = convertGroupData(userMenu, GroupDataConstants.GROUP_DATA_ID_USER_CENTER_MENU);
        return transactionTemplate.execute(e -> {
            // 先删除历史数据
            systemGroupDataService.deleteByGid(GroupDataConstants.GROUP_DATA_ID_USER_CENTER_MENU);
            // 保存新数据
            systemGroupDataService.saveBatch(dataList, 100);
            return Boolean.TRUE;
        });
    }

    /**
     * 获取页面底部导航信息
     */
    @Override
    public PageLayoutBottomNavigationResponse getBottomNavigation() {
        PageLayoutBottomNavigationResponse response = new PageLayoutBottomNavigationResponse();
        // 个人中心页服务
        List<SystemGroupData> dataList = systemGroupDataService.findListByGid(GroupDataConstants.GROUP_DATA_ID_BOTTOM_NAVIGATION);
        response.setBottomNavigationList(convertData(dataList));

        // 是否自定义
        String isCustom = systemConfigService.getValueByKey(SysConfigConstants.CONFIG_BOTTOM_NAVIGATION_IS_CUSTOM);
        response.setIsCustom(isCustom);
        return response;
    }

    /**
     * 页面底部导航信息保存
     * @return 保存结果
     */
    @Override
    public Boolean bottomNavigationSave(JSONObject jsonObject) {
        String isCustom = jsonObject.getString("isCustom");
        if (StrUtil.isBlank(isCustom)) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "请选择是否自定义");
        }
        List<JSONObject> bottomNavigationList = CrmebUtil.jsonArrayToJsonObjectList(jsonObject.getJSONArray("bottomNavigationList"));
        if (CollUtil.isEmpty(bottomNavigationList)) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "请传入底部导航数据");
        }
        List<SystemGroupData> dataList = convertGroupData(bottomNavigationList, GroupDataConstants.GROUP_DATA_ID_BOTTOM_NAVIGATION);
        return transactionTemplate.execute(e -> {
            // 先删除历史数据
            systemGroupDataService.deleteByGid(GroupDataConstants.GROUP_DATA_ID_BOTTOM_NAVIGATION);
            // 保存新数据
            systemGroupDataService.saveBatch(dataList, 100);

            systemConfigService.updateOrSaveValueByName(SysConfigConstants.CONFIG_BOTTOM_NAVIGATION_IS_CUSTOM, isCustom);
            return Boolean.TRUE;
        });
    }

    /**
     * 获取开屏广告配置
     */
    @Override
    public SplashAdConfigVo getSplashAdConfig() {
        List<String> keyList = new ArrayList<>();
        keyList.add(SysConfigConstants.SPLASH_AD_SWITCH);
        keyList.add(SysConfigConstants.SPLASH_AD_SHOW_TIME);
        keyList.add(SysConfigConstants.SPLASH_AD_SHOW_INTERVAL);
        MyRecord myRecord = systemConfigService.getValuesByKeyList(keyList);
        SplashAdConfigVo configVo = new SplashAdConfigVo();
        configVo.setSplashAdSwitch(myRecord.getInt(SysConfigConstants.SPLASH_AD_SWITCH));
        configVo.setSplashAdShowTime(myRecord.getInt(SysConfigConstants.SPLASH_AD_SHOW_TIME));
        configVo.setSplashAdShowInterval(myRecord.getInt(SysConfigConstants.SPLASH_AD_SHOW_INTERVAL));

        List<GroupConfig> configList = groupConfigService.findByTag(GroupConfigConstants.TAG_SPLASH_AD_DATA, Constants.SORT_ASC, null);
        if (CollUtil.isEmpty(configList)) {
            configVo.setAdList(new ArrayList<>());
        } else {
            Iterator<GroupConfig> iterator = configList.iterator();
            List<SplashAdDataVo> voList = new ArrayList<>();
            while (iterator.hasNext()) {
                GroupConfig config = iterator.next();
                SplashAdDataVo vo = new SplashAdDataVo();
                BeanUtils.copyProperties(config, vo);
                voList.add(vo);
            }
            configVo.setAdList(voList);
        }
        return configVo;
    }

    /**
     * 编辑开屏广告配置
     */
    @Override
    public Boolean splashAdConfigSave(SplashAdConfigVo configVo) {
        String adSwitch = configVo.getSplashAdSwitch().equals(1) ? "1" : "0";
        List<GroupConfig> configList = new ArrayList<>();
        if (CollUtil.isNotEmpty(configVo.getAdList())) {
            configList = configVo.getAdList().stream().map(data -> {
                GroupConfig groupConfig = new GroupConfig();
                groupConfig.setTag(GroupConfigConstants.TAG_SPLASH_AD_DATA);
                groupConfig.setName(data.getName());
                groupConfig.setLinkUrl(data.getLinkUrl());
                groupConfig.setImageUrl(data.getImageUrl());
                groupConfig.setSort(data.getSort());
                groupConfig.setStatus(true);
                return groupConfig;
            }).collect(Collectors.toList());
        }
        List<GroupConfig> finalConfigList = configList;
        return transactionTemplate.execute(e -> {
            systemConfigService.updateOrSaveValueByName(SysConfigConstants.SPLASH_AD_SWITCH, adSwitch);
            systemConfigService.updateOrSaveValueByName(SysConfigConstants.SPLASH_AD_SHOW_TIME, configVo.getSplashAdShowTime().toString());
            systemConfigService.updateOrSaveValueByName(SysConfigConstants.SPLASH_AD_SHOW_INTERVAL, configVo.getSplashAdShowInterval().toString());

            if (CollUtil.isEmpty(finalConfigList)) {
                groupConfigService.deleteByTagAndMerId(GroupConfigConstants.TAG_SPLASH_AD_DATA, 0);
            } else {
                groupConfigService.saveList(finalConfigList);
            }
            return Boolean.TRUE;
        });
    }

    /**
     * 转换组合数据
     * @param jsonObjectList 数组
     * @param gid gid
     * @return List
     */
    private List<SystemGroupData> convertGroupData(List<JSONObject> jsonObjectList, Integer gid) {
        return jsonObjectList.stream().map(e -> {
            SystemGroupData groupData = new SystemGroupData();
            if (e.containsKey("id") && ObjectUtil.isNotNull(e.getInteger("id"))) {
                groupData.setId(e.getInteger("id"));
            }
            groupData.setGid(gid);
            groupData.setSort(e.getInteger("sort"));
            groupData.setStatus(e.getBoolean("status"));
            // 组装json
            Map<String, Object> jsonMap = CollUtil.newHashMap();
            jsonMap.put("id", e.getInteger("tempid"));
            jsonMap.put("sort", groupData.getSort());
            jsonMap.put("status", groupData.getStatus());
            List<Map<String, Object>> mapList = CollUtil.newArrayList();
            e.remove("id");
            e.remove("gid");
            e.remove("sort");
            e.remove("status");
            e.remove("tempid");
            e.forEach((key, value) -> {
                Map<String, Object> map = CollUtil.newHashMap();
                map.put("name", key);
                map.put("title", key);
                map.put("value", value);
                if (String.valueOf(value).contains(UploadConstants.UPLOAD_FILE_KEYWORD)) {
                    String values = systemAttachmentService.clearPrefix(String.valueOf(value));
                    map.put("value", values);
                }
                mapList.add(map);
            });
            jsonMap.put("fields", mapList);
            groupData.setValue(JSONObject.toJSONString(jsonMap));
            return groupData;
        }).collect(Collectors.toList());
    }

    /**
     * 转换数据
     * @param dataList 数据列表
     * @return List<Map>
     */
    private List<HashMap<String, Object>> convertData(List<SystemGroupData> dataList) {
        return dataList.stream().map(data -> {
            HashMap<String, Object> map = CollUtil.newHashMap();
            map.put("id", data.getId());
            map.put("gid", data.getGid());
            map.put("sort", data.getSort());
            map.put("status", data.getStatus());
            JSONObject jsonObject = JSONObject.parseObject(data.getValue());
            List<SystemFormItemCheckRequest> systemFormItemCheckRequestList = CrmebUtil.jsonToListClass(jsonObject.getString("fields"), SystemFormItemCheckRequest.class);
            systemFormItemCheckRequestList.forEach(e -> {
                map.put(e.getName(), e.getValue());
            });
            map.put("tempid", jsonObject.getInteger("id"));
            return map;
        }).collect(Collectors.toList());
    }
}
