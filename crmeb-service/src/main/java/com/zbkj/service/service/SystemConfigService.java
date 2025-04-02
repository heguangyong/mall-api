package com.zbkj.service.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zbkj.common.model.system.SystemConfig;
import com.zbkj.common.request.SaveConfigRequest;
import com.zbkj.common.request.SystemConfigAdminRequest;
import com.zbkj.common.request.SystemFormCheckRequest;
import com.zbkj.common.vo.ExpressSheetVo;
import com.zbkj.common.vo.MyRecord;

import java.util.HashMap;
import java.util.List;

/**
 * SystemConfigService 接口
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
public interface SystemConfigService extends IService<SystemConfig> {

    /**
     * 通过key数组获取Record对象
     * @param keyList key列表
     * @return MyRecord
     */
    MyRecord getValuesByKeyList(List<String> keyList);

    /**
     * 根据menu name 获取 value
     * @param key menu name
     * @return String
     */
    String getValueByKey(String key);

    /**
     * 保存或更新配置数据
     * @param name 菜单名称
     * @param value 菜单值
     * @return Boolean
     */
    Boolean updateOrSaveValueByName(String name, String value);

    /**
     * 根据 name 获取 value 找不到抛异常
     * @param key menu name
     * @return String
     */
    String getValueByKeyException(String key);

    /**
     * 整体保存表单数据
     * @param systemFormCheckRequest SystemFormCheckRequest 数据保存
     * @return Boolean
     */
    Boolean saveForm(SystemFormCheckRequest systemFormCheckRequest);

    /**
     * 根据formId查询数据
     * @param formId Integer id
     * @return HashMap<String, String>
     */
    HashMap<String, String> info(Integer formId);

    /**
     * 根据name查询数据
     * @param name name
     * @return Boolean
     */
    Boolean checkName(String name);

    /**
     * 更新配置信息
     * @param requestList 请求数组
     * @return Boolean
     */
    Boolean updateByList(List<SystemConfigAdminRequest> requestList);

    /**
     * 获取颜色配置
     * @return SystemConfig
     */
    SystemConfig getColorConfig();

    /**
     * 获取各种文字协议
     * @return String
     */
    String getAgreementByKey(String agreementName);

    /**
     * 获取移动端域名
     * @return 移动端域名
     */
    String getFrontDomain();

    /**
     * 获取素材域名
     *
     * @return 素材域名
     */
    String getMediaDomain();

    /**
     * 获取主题色
     */
    SystemConfig getChangeColor();

    /**
     * 保存主题色
     */
    Boolean saveChangeColor(SaveConfigRequest request);

    /**
     * 获取秒杀样式设置
     */
    SystemConfig getSeckillStyle();

    /**
     * 保存秒杀样式设置
     */
    Boolean saveSeckillStyle(SaveConfigRequest request);
}
