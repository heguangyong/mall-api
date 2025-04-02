package com.zbkj.service.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zbkj.common.model.system.SystemUserLevel;
import com.zbkj.common.request.SystemUserLevelRequest;
import com.zbkj.common.request.SystemUserLevelRuleRequest;
import com.zbkj.common.request.SystemUserLevelUpdateShowRequest;
import com.zbkj.common.vo.SystemUserLevelConfigVo;

import java.util.List;

/**
 * SystemUserLevelService 接口
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
public interface SystemUserLevelService extends IService<SystemUserLevel> {

    /**
     * 获取等级列表
     */
    List<SystemUserLevel> getList();

    /**
     * 系统等级新增
     * @param request request
     * @return Boolean
     */
    Boolean create(SystemUserLevelRequest request);

    /**
     * 系统等级更新
     * @param request   等级数据
     * @return Boolean
     */
    Boolean edit(SystemUserLevelRequest request);

    SystemUserLevel getByLevelId(Integer levelId);

    /**
     * 获取系统等级列表（移动端）
     */
    List<SystemUserLevel> getH5LevelList();

    /**
     * 删除系统等级
     * @param id 等级id
     * @return Boolean
     */
    Boolean delete(Integer id);

    /**
     * 使用/禁用
     * @param request request
     */
    @Deprecated
    Boolean updateShow(SystemUserLevelUpdateShowRequest request);

    /**
     * 获取用户等级规则
     * @return 用户等级规则
     */
    String getRule();

    /**
     * 获取用户等级配置
     * @return 用户等级配置
     */
    SystemUserLevelConfigVo getConfig();

    /**
     * 编辑用户规则
     * @param request 用户规则参数
     */
    Boolean updateRule(SystemUserLevelRuleRequest request);

    /**
     * 编辑用户等级配置
     */
    Boolean updateConfig(SystemUserLevelConfigVo request);

    /**
     * 获取下一个等级
     * @param level 当前等级
     */
    SystemUserLevel getNextLevel(Integer level);

    /**
     * 获取经验所属的等级
     * @param exp 经验
     */
    SystemUserLevel getByExp(Integer exp);

    /**
     * 获取上一个系统用户等级
     * @param grade 用户等级级别
     */
    SystemUserLevel getPreviousGrade(Integer grade);
}
