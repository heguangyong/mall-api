package com.zbkj.service.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zbkj.common.model.user.UserLevel;
import com.zbkj.service.dao.UserLevelDao;
import com.zbkj.service.service.UserLevelService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * UserLevelServiceImpl 接口实现
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
public class UserLevelServiceImpl extends ServiceImpl<UserLevelDao, UserLevel> implements UserLevelService {

    @Resource
    private UserLevelDao dao;


    /**
     * 删除（通过系统等级id）
     *
     * @param levelId 系统等级id
     * @return Boolean
     */
    @Override
    public Boolean deleteByLevelId(Integer levelId) {
        LambdaUpdateWrapper<UserLevel> luw = Wrappers.lambdaUpdate();
        luw.set(UserLevel::getIsDel, true);
        luw.eq(UserLevel::getLevelId, levelId);
        luw.eq(UserLevel::getIsDel, false);
        return update(luw);
    }

    /**
     * 删除用户等级
     * @param userId 用户ID
     */
    @Override
    public Boolean deleteByUserId(Integer userId) {
        LambdaUpdateWrapper<UserLevel> luw = Wrappers.lambdaUpdate();
        luw.set(UserLevel::getIsDel, true);
        luw.eq(UserLevel::getUid, userId);
        luw.eq(UserLevel::getIsDel, false);
        return update(luw);
    }
}

