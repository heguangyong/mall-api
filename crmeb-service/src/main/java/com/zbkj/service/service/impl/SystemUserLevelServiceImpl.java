package com.zbkj.service.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zbkj.common.constants.UserLevelConstants;
import com.zbkj.common.exception.CrmebException;
import com.zbkj.common.model.system.SystemUserLevel;
import com.zbkj.common.request.SystemUserLevelRequest;
import com.zbkj.common.request.SystemUserLevelRuleRequest;
import com.zbkj.common.request.SystemUserLevelUpdateShowRequest;
import com.zbkj.common.result.CommonResultCode;
import com.zbkj.common.result.SystemConfigResultCode;
import com.zbkj.common.vo.SystemUserLevelConfigVo;
import com.zbkj.service.dao.SystemUserLevelDao;
import com.zbkj.service.service.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.Resource;
import java.util.List;
import java.util.Optional;

/**
 * SystemUserLevelServiceImpl 接口实现
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
public class SystemUserLevelServiceImpl extends ServiceImpl<SystemUserLevelDao, SystemUserLevel> implements SystemUserLevelService {

    @Resource
    private SystemUserLevelDao dao;

    @Autowired
    private SystemAttachmentService systemAttachmentService;
    @Autowired
    private UserLevelService userLevelService;
    @Autowired
    private UserService userService;
    @Autowired
    private TransactionTemplate transactionTemplate;
    @Autowired
    private SystemConfigService systemConfigService;


    /**
     * 分页显示设置用户等级表
     * @return List<SystemUserLevel>
     */
    @Override
    public List<SystemUserLevel> getList() {
        LambdaQueryWrapper<SystemUserLevel> levelLambdaQueryWrapper = new LambdaQueryWrapper<>();
        levelLambdaQueryWrapper.eq(SystemUserLevel::getIsDel, false);
        levelLambdaQueryWrapper.orderByAsc(SystemUserLevel::getGrade);
        return dao.selectList(levelLambdaQueryWrapper);
    }

    /**
     * 新增设置用户等级表
     * @param request SystemUserLevelRequest 新增参数
     * @return boolean
     * 等级名称不能重复
     * 等级级别不能重复
     */
    @Override
    public Boolean create(SystemUserLevelRequest request) {
        if (request.getGrade().equals(0)) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "用户等级不能设置0");
        }
        request.setId(null);
        checkLevel(request);
        SystemUserLevel systemUserLevel = new SystemUserLevel();
        BeanUtils.copyProperties(request, systemUserLevel);
        systemUserLevel.setDiscount(0);
        systemUserLevel.setIsShow(true);
        systemUserLevel.setIcon(systemAttachmentService.clearPrefix(request.getIcon()));
        systemUserLevel.setBackImage(systemAttachmentService.clearPrefix(request.getBackImage()));
        return save(systemUserLevel);
    }

    /**
     * 添加、修改校验
     * @param request 用户等级参数
     * 等级名称不能重复
     * 等级级别不能重复
     */
    private void checkLevel(SystemUserLevelRequest request) {
        SystemUserLevel temp;
        // 校验名称
        LambdaQueryWrapper<SystemUserLevel> lqw = Wrappers.lambdaQuery();
        lqw.eq(SystemUserLevel::getName, request.getName());
        if (ObjectUtil.isNotNull(request.getId())) {
            lqw.ne(SystemUserLevel::getId, request.getId());
        }
        lqw.eq(SystemUserLevel::getIsDel, false);
        temp = dao.selectOne(lqw);
        if (ObjectUtil.isNotNull(temp)) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "用户等级名称重复");
        }
        // 校验等级级别
        lqw.clear();
        lqw.eq(SystemUserLevel::getGrade, request.getGrade());
        if (ObjectUtil.isNotNull(request.getId())) {
            lqw.ne(SystemUserLevel::getId, request.getId());
        }
        lqw.eq(SystemUserLevel::getIsDel, false);
        temp = dao.selectOne(lqw);
        if (ObjectUtil.isNotNull(temp)) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "用户等级级别重复");
        }
        // 校验等级经验不能比上一级别的低,不能比下一级别高
        if (request.getGrade() > 1) {
            lqw.clear();
            lqw.lt(SystemUserLevel::getGrade, request.getGrade());
            if (ObjectUtil.isNotNull(request.getId())) {
                lqw.ne(SystemUserLevel::getId, request.getId());
            }
            lqw.eq(SystemUserLevel::getIsDel, false);
            lqw.orderByDesc(SystemUserLevel::getGrade);
            lqw.last(" limit 1");
            temp = dao.selectOne(lqw);
            if (ObjectUtil.isNotNull(temp) && temp.getExperience() >= request.getExperience()) {
                throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "当前等级的经验不能比上一级别的经验低");
            }
        }
        lqw.clear();
        lqw.gt(SystemUserLevel::getGrade, request.getGrade());
        if (ObjectUtil.isNotNull(request.getId())) {
            lqw.ne(SystemUserLevel::getId, request.getId());
        }
        lqw.eq(SystemUserLevel::getIsDel, false);
        lqw.orderByAsc(SystemUserLevel::getGrade);
        lqw.last(" limit 1");
        temp = dao.selectOne(lqw);
        if (ObjectUtil.isNotNull(temp) && temp.getExperience() <= request.getExperience()) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "当前等级的经验不能比下一级别的经验高");
        }
    }


    /**
     * 系统等级更新
     * @param request   等级数据
     * @return Boolean
     */
    @Override
    public Boolean edit(SystemUserLevelRequest request) {
        if (ObjectUtil.isNull(request.getId())) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "等级ID不能为空");
        }
        SystemUserLevel level = getById(request.getId());
        if (ObjectUtil.isNull(level) || level.getIsDel()) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "等级不存在");
        }
        if (level.getGrade().equals(0) && !request.getGrade().equals(0)) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "不能修改级别为0的级别");
        }
        if (level.getGrade().equals(0) && !request.getExperience().equals(0)) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "不能修改级别为0的等级经验");
        }
        checkLevel(request);
        SystemUserLevel systemUserLevel = new SystemUserLevel();
        BeanUtils.copyProperties(request, systemUserLevel);
        systemUserLevel.setIcon(systemAttachmentService.clearPrefix(request.getIcon()));
        systemUserLevel.setBackImage(systemAttachmentService.clearPrefix(request.getBackImage()));
        return transactionTemplate.execute(e -> {
            dao.updateById(systemUserLevel);
            // 修改等级级别或是经验值时，清除用户数据
            if (!level.getGrade().equals(systemUserLevel.getGrade()) || !level.getExperience().equals(systemUserLevel.getExperience())) {
                // 删除对应的用户等级数据
                userLevelService.deleteByLevelId(request.getId());
                // 清除对应的用户等级
                userService.removeLevelByLevelId(request.getId());
            }
            return Boolean.TRUE;
        });
    }

    @Override
    public SystemUserLevel getByLevelId(Integer levelId) {
        LambdaQueryWrapper<SystemUserLevel> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(SystemUserLevel::getIsShow, 1);
        lambdaQueryWrapper.eq(SystemUserLevel::getIsDel, 0);
        lambdaQueryWrapper.eq(SystemUserLevel::getId, levelId);
        return dao.selectOne(lambdaQueryWrapper);
    }

    /**
     * 获取系统等级列表（移动端）
     */
    @Override
    public List<SystemUserLevel> getH5LevelList() {
        LambdaQueryWrapper<SystemUserLevel> lqw = new LambdaQueryWrapper<>();
        lqw.select(SystemUserLevel::getId, SystemUserLevel::getName, SystemUserLevel::getIcon, SystemUserLevel::getExperience);
        lqw.eq(SystemUserLevel::getIsShow, true);
        lqw.eq(SystemUserLevel::getIsDel, false);
        lqw.orderByAsc(SystemUserLevel::getGrade);
        return dao.selectList(lqw);
    }

    /**
     * 删除系统等级
     * @param id 等级id
     * @return Boolean
     */
    @Override
    public Boolean delete(Integer id) {
        SystemUserLevel level = getById(id);
        if (ObjectUtil.isNull(level) || level.getIsDel()) {
            throw new CrmebException(SystemConfigResultCode.USER_LEVEL_NOT_EXIST);
        }
        if (level.getGrade().equals(0)) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "级别为0的用户等级不支持删除操作");
        }
        level.setIsDel(true);
        return transactionTemplate.execute(e -> {
            dao.updateById(level);
            // 删除对应的用户等级数据
            userLevelService.deleteByLevelId(id);
            // 清除对应的用户等级
            userService.removeLevelByLevelId(id);
            return Boolean.TRUE;
        });
    }

    /**
     * 使用/禁用
     * 用户等级在V1.2版本中去除等级的使用/禁用，在之后的逻辑中去除此逻辑
     * @param request request
     */
    @Deprecated
    @Override
    public Boolean updateShow(SystemUserLevelUpdateShowRequest request) {
        SystemUserLevel level = getById(request.getId());
        if (ObjectUtil.isNull(level) || level.getIsDel()) {
            throw new CrmebException(SystemConfigResultCode.USER_LEVEL_NOT_EXIST);
        }
        if (level.getGrade().equals(0)) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "级别为0的用户等级无法禁用");
        }
        if (level.getIsShow().equals(request.getIsShow())) {
            return Boolean.TRUE;
        }
        level.setIsShow(request.getIsShow());
        if (request.getIsShow()) {// 启用直接保存
            return dao.updateById(level) > 0 ? Boolean.TRUE : Boolean.FALSE;
        }
        return transactionTemplate.execute(e -> {
            dao.updateById(level);
            // 删除对应的用户等级数据
            userLevelService.deleteByLevelId(request.getId());
            // 清除对应的用户等级
            userService.removeLevelByLevelId(request.getId());
            return Boolean.TRUE;
        });
    }

    /**
     * 获取用户等级规则
     * @return 用户等级规则
     */
    @Override
    public String getRule() {
        return systemConfigService.getValueByKey(UserLevelConstants.SYSTEM_USER_LEVEL_RULE);
    }

    /**
     * 获取用户等级配置
     * @return 用户等级配置
     */
    @Override
    public SystemUserLevelConfigVo getConfig() {
        String userLevelSwitch = systemConfigService.getValueByKey(UserLevelConstants.SYSTEM_USER_LEVEL_SWITCH);
        String exp = systemConfigService.getValueByKey(UserLevelConstants.SYSTEM_USER_LEVEL_COMMUNITY_NOTES_EXP);
        String num = systemConfigService.getValueByKey(UserLevelConstants.SYSTEM_USER_LEVEL_COMMUNITY_NOTES_NUM);
        SystemUserLevelConfigVo configVo = new SystemUserLevelConfigVo();
        configVo.setUserLevelSwitch(userLevelSwitch);
        configVo.setUserLevelCommunityNotesExp(Integer.valueOf(exp));
        configVo.setUserLevelCommunityNotesNum(Integer.valueOf(num));
        return configVo;
    }

    /**
     * 编辑用户规则
     * @param request 用户规则参数
     */
    @Override
    public Boolean updateRule(SystemUserLevelRuleRequest request) {
        if (null == request.getRule()) {
            request.setRule("");
        }
        Boolean update = systemConfigService.updateOrSaveValueByName(UserLevelConstants.SYSTEM_USER_LEVEL_RULE, Optional.ofNullable(request.getRule()).orElse(""));
        if (!update) {
            throw new CrmebException(CommonResultCode.ERROR.setMessage("编辑用户等级规则失败"));
        }
        return true;
    }

    /**
     * 编辑用户等级配置
     */
    @Override
    public Boolean updateConfig(SystemUserLevelConfigVo request) {
        Boolean execute = transactionTemplate.execute(e -> {
            systemConfigService.updateOrSaveValueByName(UserLevelConstants.SYSTEM_USER_LEVEL_SWITCH, request.getUserLevelSwitch());
            systemConfigService.updateOrSaveValueByName(UserLevelConstants.SYSTEM_USER_LEVEL_COMMUNITY_NOTES_EXP, request.getUserLevelCommunityNotesExp().toString());
            systemConfigService.updateOrSaveValueByName(UserLevelConstants.SYSTEM_USER_LEVEL_COMMUNITY_NOTES_NUM, request.getUserLevelCommunityNotesNum().toString());
            return Boolean.TRUE;
        });
        if (!execute) {
            throw new CrmebException(CommonResultCode.ERROR.setMessage("编辑用户等级配置失败"));
        }
        return true;
    }

    /**
     * 获取下一个等级
     * @param level 当前等级
     */
    @Override
    public SystemUserLevel getNextLevel(Integer level) {
        LambdaQueryWrapper<SystemUserLevel> lqw = new LambdaQueryWrapper<>();
        lqw.gt(SystemUserLevel::getGrade, level);
        lqw.eq(SystemUserLevel::getIsShow, true);
        lqw.eq(SystemUserLevel::getIsDel, false);
        lqw.orderByAsc(SystemUserLevel::getGrade);
        lqw.last(" limit 1");
        return dao.selectOne(lqw);
    }

    /**
     * 获取经验所属的等级
     * @param exp 经验
     */
    @Override
    public SystemUserLevel getByExp(Integer exp) {
        LambdaQueryWrapper<SystemUserLevel> lqw = new LambdaQueryWrapper<>();
        lqw.gt(SystemUserLevel::getExperience, exp);
        lqw.eq(SystemUserLevel::getIsShow, true);
        lqw.eq(SystemUserLevel::getIsDel, false);
        lqw.orderByAsc(SystemUserLevel::getGrade);
        lqw.last(" limit 1");
        SystemUserLevel systemUserLevel = dao.selectOne(lqw);
        if (ObjectUtil.isNotNull(systemUserLevel)) {
            return systemUserLevel;
        }
        lqw.clear();
        lqw.eq(SystemUserLevel::getIsShow, true);
        lqw.eq(SystemUserLevel::getIsDel, false);
        lqw.orderByDesc(SystemUserLevel::getGrade);
        lqw.last(" limit 1");
        systemUserLevel = dao.selectOne(lqw);
        return systemUserLevel;
    }

    /**
     * 获取上一个系统用户等级
     * @param grade 用户等级级别
     */
    @Override
    public SystemUserLevel getPreviousGrade(Integer grade) {
        LambdaQueryWrapper<SystemUserLevel> lqw = new LambdaQueryWrapper<>();
        lqw.lt(SystemUserLevel::getGrade, grade);
        lqw.eq(SystemUserLevel::getIsShow, true);
        lqw.eq(SystemUserLevel::getIsDel, false);
        lqw.orderByDesc(SystemUserLevel::getGrade);
        lqw.last(" limit 1");
        return dao.selectOne(lqw);
    }

}

