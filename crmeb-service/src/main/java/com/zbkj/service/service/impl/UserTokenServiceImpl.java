package com.zbkj.service.service.impl;

import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zbkj.common.model.user.UserToken;
import com.zbkj.service.dao.UserTokenDao;
import com.zbkj.service.service.UserTokenService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * UserTokenServiceImpl 接口实现
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
public class UserTokenServiceImpl extends ServiceImpl<UserTokenDao, UserToken> implements UserTokenService {

    @Resource
    private UserTokenDao dao;

    /**
     * 检测token是否存在
     * @param token String openId
     * @param type int 类型
     * @return UserToken
     */
    @Override
    public UserToken getByOpenidAndType(String token, Integer type) {
        LambdaQueryWrapper<UserToken> lqw = new LambdaQueryWrapper<>();
        lqw.eq(UserToken::getType, type).eq(UserToken::getToken, token);
        lqw.eq(UserToken::getIsDel, 0);
        lqw.last(" limit 1");
        return dao.selectOne(lqw);
    }

    /**
     * 绑定token关系
     * @param token String token
     * @param type int 类型
     */
    @Override
    public void bind(String token, Integer type, Integer userId) {
        UserToken userToken = new UserToken();
        userToken.setToken(token);
        userToken.setType(type);
        userToken.setUid(userId);
        save(userToken);
    }

    @Override
    public UserToken getTokenByUserId(Integer userId, Integer type) {
        LambdaQueryWrapper<UserToken> lqw = new LambdaQueryWrapper<>();
        lqw.eq(UserToken::getUid, userId).eq(UserToken::getType, type);
        lqw.eq(UserToken::getIsDel, 0);
        lqw.last(" limit 1");
        return dao.selectOne(lqw);
    }

    /**
     * 通过用户id删除
     * @param uid 用户ID
     */
    @Override
    public Boolean deleteByUid(Integer uid) {
        LambdaUpdateWrapper<UserToken> wrapper = Wrappers.lambdaUpdate();
        wrapper.set(UserToken::getIsDel, 1);
        wrapper.set(UserToken::getExpiresTime, DateUtil.date());
        wrapper.eq(UserToken::getUid, uid);
        wrapper.eq(UserToken::getIsDel, 0);
        return update(wrapper);
    }
}

