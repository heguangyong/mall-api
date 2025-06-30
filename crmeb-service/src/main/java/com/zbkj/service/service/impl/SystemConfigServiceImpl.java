package com.zbkj.service.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.PageHelper;
import com.zbkj.common.config.CrmebConfig;
import com.zbkj.common.constants.SysConfigConstants;
import com.zbkj.common.exception.CrmebException;
import com.zbkj.common.model.system.SystemConfig;
import com.zbkj.common.model.system.SystemFormTemp;
import com.zbkj.common.request.SaveConfigRequest;
import com.zbkj.common.request.SystemConfigAdminRequest;
import com.zbkj.common.request.SystemFormCheckRequest;
import com.zbkj.common.request.SystemFormItemCheckRequest;
import com.zbkj.common.result.CommonResultCode;
import com.zbkj.common.utils.RedisUtil;
import com.zbkj.common.vo.MyRecord;
import com.zbkj.service.dao.SystemConfigDao;
import com.zbkj.service.service.SystemAttachmentService;
import com.zbkj.service.service.SystemConfigService;
import com.zbkj.service.service.SystemFormTempService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

/**
 * SystemConfigServiceImpl 接口实现
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
public class SystemConfigServiceImpl extends ServiceImpl<SystemConfigDao, SystemConfig> implements SystemConfigService {

    @Resource
    private SystemConfigDao dao;

    @Autowired
    private SystemFormTempService systemFormTempService;

    @Autowired
    private SystemAttachmentService systemAttachmentService;

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private CrmebConfig crmebConfig;

    @Autowired
    private TransactionTemplate transactionTemplate;

    /**
     * 在系统启动初始化时
     * 根据配置文件加载config_list缓存
     */
    @PostConstruct
    public void loadingConfigCache() {
        if (!crmebConfig.getAsyncConfig()) {
            return;
        }
        if (redisUtil.exists(SysConfigConstants.CONFIG_LIST)) {
            Long hashSize = redisUtil.getHashSize(SysConfigConstants.CONFIG_LIST);
            if (hashSize > 0) {
                return;
            }
        }
        LambdaQueryWrapper<SystemConfig> lqw = Wrappers.lambdaQuery();
        lqw.select(SystemConfig::getName, SystemConfig::getValue);
        lqw.eq(SystemConfig::getStatus, false);
        List<SystemConfig> systemConfigList = dao.selectList(lqw);
        systemConfigList.forEach(config -> redisUtil.hset(SysConfigConstants.CONFIG_LIST, config.getName(), config.getValue()));
    }

    /**
     * 通过key数组获取Record对象
     * @param keyList key列表
     * @return MyRecord
     */
    @Override
    public MyRecord getValuesByKeyList(List<String> keyList) {
        if (CollUtil.isEmpty(keyList)) {
            return null;
        }
        MyRecord record = new MyRecord();
        keyList.forEach(k -> {
            SystemConfig systemConfig = getByName(k);
            if (ObjectUtil.isNotNull(systemConfig)) {
                record.set(k, systemConfig.getValue());
            } else {
                record.set(k, "");
            }
        });
        return record;
    }


    /**
     * 根据menu name 获取 value
     *
     * @param name menu name
     * @return String
     */
    @Override
    public String getValueByKey(String name) {
        return get(name);
    }

    /**
     * 根据 name 获取 value 找不到抛异常
     *
     * @param name menu name
     * @return String
     */
    @Override
    public String getValueByKeyException(String name) {
        String value = get(name);
        if (StrUtil.isBlank(value)) {
            throw new CrmebException("没有找到或配置：" + name + "数据");
        }
        return value;
    }

    /**
     * 整体保存表单数据
     *
     * @param systemFormCheckRequest SystemFormCheckRequest 数据保存
     * @return boolean
     */
    @Override
    public Boolean saveForm(SystemFormCheckRequest systemFormCheckRequest) {
        //检测form表单，并且返回需要添加的数据
        systemFormTempService.checkForm(systemFormCheckRequest);

        List<SystemConfig> systemConfigList = new ArrayList<>();
        SystemFormTemp systemFormTemp = systemFormTempService.getById(systemFormCheckRequest.getId());
        //批量添加
        for (SystemFormItemCheckRequest systemFormItemCheckRequest : systemFormCheckRequest.getFields()) {
            SystemConfig systemConfig = new SystemConfig();
            systemConfig.setName(systemFormItemCheckRequest.getName());
            String value = systemAttachmentService.clearPrefix(systemFormItemCheckRequest.getValue());
            if (StrUtil.isBlank(value)) {
                //去掉图片域名之后没有数据则说明当前数据就是图片域名
                value = systemFormItemCheckRequest.getValue();
            }
            systemConfig.setValue(value);
            systemConfig.setFormId(systemFormCheckRequest.getId());
            systemConfig.setTitle(systemFormItemCheckRequest.getTitle());
            if (systemFormCheckRequest.getId() > 0) {
                systemConfig.setFormName(systemFormTemp.getName());
            }
            systemConfigList.add(systemConfig);
        }

        LambdaQueryWrapper<SystemConfig> oldLqw = Wrappers.lambdaQuery();
        oldLqw.eq(SystemConfig::getFormId, systemFormCheckRequest.getId());
        //删除已经隐藏的数据
        List<SystemConfig> systemConfigOldList = dao.selectList(oldLqw);

        Boolean execute = transactionTemplate.execute(e -> {
            //删除之前的数据
            if (CollUtil.isNotEmpty(systemConfigOldList)) {
                dao.delete(oldLqw);
            }
            saveBatch(systemConfigList);
            return Boolean.TRUE;
        });
        if (execute) {
            if (crmebConfig.getAsyncConfig()) {
                if (CollUtil.isNotEmpty(systemConfigOldList)) {
                    asyncDelete(systemConfigOldList);
                }
                async(systemConfigList);
            }
        }
        return execute;
    }

    private void asyncDelete(List<SystemConfig> systemConfigList) {
        systemConfigList.forEach(config -> {
            if (redisUtil.hHasKey(SysConfigConstants.CONFIG_LIST, config.getName())) {
                redisUtil.hmDelete(SysConfigConstants.CONFIG_LIST, config.getName());
            }
        });
    }

    /**
     * 保存或更新配置数据
     *
     * @param name  菜单名称
     * @param value 菜单值
     * @return boolean
     */
    @Override
    public Boolean updateOrSaveValueByName(String name, String value) {
        value = systemAttachmentService.clearPrefix(value);
        LambdaQueryWrapper<SystemConfig> lambdaQueryWrapper = Wrappers.lambdaQuery();
        lambdaQueryWrapper.eq(SystemConfig::getName, name);
        List<SystemConfig> systemConfigs = dao.selectList(lambdaQueryWrapper);
        if (systemConfigs.size() >= 2) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "配置名称存在多个请检查配置 eb_system_config 重复数据：" + name + "条数：" + systemConfigs.size());
        } else if (systemConfigs.size() == 1) {
            SystemConfig systemConfig = systemConfigs.get(0);
            systemConfig.setValue(value);
            boolean update = updateById(systemConfig);
            if (update && crmebConfig.getAsyncConfig()) {
                async(systemConfig);
            }
            return update;
        } else {
            SystemConfig systemConfig = new SystemConfig().setName(name).setValue(value);
            boolean save = save(systemConfig);
            if (save && crmebConfig.getAsyncConfig()) {
                async(systemConfig);
            }
            return save;
        }
    }

    /**
     * 根据formId查询数据
     *
     * @param formId Integer id
     * @return HashMap<String, String>
     */
    @Override
    public HashMap<String, String> info(Integer formId) {
        LambdaQueryWrapper<SystemConfig> lqw = Wrappers.lambdaQuery();
        lqw.eq(SystemConfig::getFormId, formId);
        List<SystemConfig> systemConfigList = dao.selectList(lqw);
        if (ObjectUtil.isNull(systemConfigList)) {
            return CollUtil.newHashMap();
        }
        HashMap<String, String> map = new HashMap<>();
        for (SystemConfig systemConfig : systemConfigList) {
            map.put(systemConfig.getName(), systemConfig.getValue());
        }
        map.put("id", formId.toString());
        return map;
    }

    /**
     * 根据name查询数据
     *
     * @param name name
     * @return boolean
     */
    @Override
    public Boolean checkName(String name) {
        String value = get(name);
        return StrUtil.isBlank(value);
    }

    /**
     * 更新配置信息
     *
     * @param requestList 请求数组
     * @return Boolean
     */
    @Override
    public Boolean updateByList(List<SystemConfigAdminRequest> requestList) {
        List<SystemConfig> configList = requestList.stream().map(e -> {
            SystemConfig systemConfig = new SystemConfig();
            BeanUtils.copyProperties(e, systemConfig);
            return systemConfig;
        }).collect(Collectors.toList());
        boolean batch = updateBatchById(configList);
        if (batch && crmebConfig.getAsyncConfig()) {
            async(configList);
        }
        return batch;
    }

    /**
     * 获取颜色配置
     *
     * @return SystemConfig
     */
    @Override
    public SystemConfig getColorConfig() {
        String colorConfig = getValueByKeyException(SysConfigConstants.CONFIG_CHANGE_COLOR_CONFIG);
        SystemConfig systemConfig = new SystemConfig();
        systemConfig.setName(SysConfigConstants.CONFIG_CHANGE_COLOR_CONFIG);
        systemConfig.setValue(colorConfig);
        return systemConfig;
    }

    private void asyncBlank(String key) {
        redisUtil.hset(SysConfigConstants.CONFIG_LIST, key, "");
    }

    private void async(SystemConfig systemConfig) {
        redisUtil.hset(SysConfigConstants.CONFIG_LIST, systemConfig.getName(), systemConfig.getValue());
    }

    /**
     * 把数据同步到redis
     *
     * @param systemConfigList List<SystemConfig> 需要同步的数据
     */
    private void async(List<SystemConfig> systemConfigList) {
        for (SystemConfig systemConfig : systemConfigList) {
            redisUtil.hset(SysConfigConstants.CONFIG_LIST, systemConfig.getName(), systemConfig.getValue());
        }
    }

    /**
     * 把数据同步到redis
     *
     * @param name String
     * @return String
     */
    private String get(String name) {
        if (!crmebConfig.getAsyncConfig()) {
            SystemConfig systemConfig = getByName(name);
            if (ObjectUtil.isNull(systemConfig) || StrUtil.isBlank(systemConfig.getValue())) {
                return "";
            }
            return systemConfig.getValue();
        }
        Long size = redisUtil.getHashSize(SysConfigConstants.CONFIG_LIST);
        if (size <= 0) {
            SystemConfig systemConfig = getByName(name);
            if (ObjectUtil.isNull(systemConfig) || StrUtil.isBlank(systemConfig.getValue())) {
                asyncBlank(name);
                return "";
            }
            async(systemConfig);
            return systemConfig.getValue();
        }
        Object data = redisUtil.hget(SysConfigConstants.CONFIG_LIST, name);
        if (ObjectUtil.isNull(data)) {
            asyncBlank(name);
            return "";
        }
        return data.toString();
    }

    private SystemConfig getByName(String name) {
        PageHelper.clearPage();
        LambdaQueryWrapper<SystemConfig> lqw = Wrappers.lambdaQuery();
        lqw.select(SystemConfig::getId, SystemConfig::getName, SystemConfig::getValue);
        lqw.eq(SystemConfig::getStatus, false);
        lqw.eq(SystemConfig::getName, name);
        lqw.last(" limit 1");
        return getOne(lqw);
    }

    /**
     * 获取各种文字协议
     *
     * @return String
     */
    @Override
    public String getAgreementByKey(String agreementName) {
        if (ObjectUtil.isEmpty(agreementName)) {
            return "Key Not Empty";
        }
        LambdaQueryWrapper<SystemConfig> lqw = Wrappers.lambdaQuery();
        lqw.eq(SystemConfig::getName, agreementName);
        lqw.eq(SystemConfig::getStatus, 0);
        SystemConfig systemConfig = dao.selectOne(lqw);
        if (ObjectUtil.isNull(systemConfig)) {
            return "";
        }
        return systemConfig.getValue();
    }

    /**
     * 获取移动端域名
     * @return 移动端域名
     */
    @Override
    public String getFrontDomain() {
        return getValueByKey(SysConfigConstants.CONFIG_KEY_SITE_URL);
    }

    /**
     * 获取素材域名
     *
     * @return 素材域名
     */
    @Override
    public String getMediaDomain() {
        return systemAttachmentService.getCdnUrl();
    }

    /**
     * 获取主题色
     */
    @Override
    public SystemConfig getChangeColor() {
        return getConfigByNameException(SysConfigConstants.CONFIG_CHANGE_COLOR_CONFIG);
    }

    /**
     * 保存主题色
     */
    @Override
    public Boolean saveChangeColor(SaveConfigRequest request) {
        return updateOrSaveValueByName(SysConfigConstants.CONFIG_CHANGE_COLOR_CONFIG, request.getValue());
    }

    private SystemConfig getConfigByNameException(String name) {
        String value = getValueByKeyException(name);
        SystemConfig systemConfig = new SystemConfig();
        systemConfig.setName(name);
        systemConfig.setValue(value);
        return systemConfig;
    }

    /**
     * 获取主题色
     */
    @Override
    public SystemConfig getSeckillStyle() {
        return getConfigByNameException(SysConfigConstants.SECKILL_STYLE_CONFIG);
    }

    /**
     * 保存主题色
     */
    @Override
    public Boolean saveSeckillStyle(SaveConfigRequest request) {
        return updateOrSaveValueByName(SysConfigConstants.SECKILL_STYLE_CONFIG, request.getValue());
    }
}

