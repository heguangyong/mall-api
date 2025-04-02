package com.zbkj.admin.filter;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.zbkj.common.constants.Constants;
import com.zbkj.common.enums.RoleEnum;
import com.zbkj.common.utils.RedisUtil;
import com.zbkj.common.vo.LoginUserVo;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * token验证处理
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
@Component
public class TokenComponent {

    @Resource
    private RedisUtil redisUtil;

    private static final Long MILLIS_MINUTE_TEN = 20 * 60 * 1000L;

    private static final Long MILLIS_MINUTE = 60 * 1000L;

    // 令牌有效期（默认30分钟） todo 调试期改为24小时
//    private static final int expireTime = 30;
//    private static final int expireTime = 5 * 60;
    private static final int expireTime = 24 * 60;

    // Redis 存储的key
    private static final String TOKEN_REDIS = "TOKEN:ADMIN:";
    private static final String TOKEN_PLATFORM_REDIS = "TOKEN:ADMIN:PLATFORM:";
    private static final String TOKEN_MERCHANT_REDIS = "TOKEN:ADMIN:MERCHANT:";

    // token前缀
    private static final String TOKEN_PLATFORM_START_PREFIX = "platform";
    private static final String TOKEN_MERCHANT_START_PREFIX = "merchant";

    /**
     * 获取用户身份信息
     *
     * @return 用户信息
     */
    public LoginUserVo getLoginUser(HttpServletRequest request) {
        // 获取请求携带的令牌
        String token = getToken(request);
        if (StrUtil.isNotBlank(token)) {
            return redisUtil.get(token);
        }
        return null;
    }

    /**
     * 设置用户身份信息
     */
    public void setLoginUser(LoginUserVo loginUser) {
        if (ObjectUtil.isNotNull(loginUser) && StrUtil.isNotBlank(loginUser.getToken())) {
            refreshToken(loginUser);
        }
    }

    /**
     * 删除用户身份信息
     */
    public void delLoginUser(LoginUserVo loginUserVo) {
        if (ObjectUtil.isNotNull(loginUserVo)) {
            if (loginUserVo.getUser().getType().equals(RoleEnum.PLATFORM_ADMIN.getValue()) || loginUserVo.getUser().getType().equals(RoleEnum.SUPER_ADMIN.getValue())) {
                redisUtil.delete(getPlatformTokenKey(loginUserVo.getToken()));
            }
            if (loginUserVo.getUser().getType().equals(RoleEnum.MERCHANT_ADMIN.getValue()) || loginUserVo.getUser().getType().equals(RoleEnum.SUPER_MERCHANT.getValue())) {
                redisUtil.delete(getMerchantTokenKey(loginUserVo.getToken()));
            }
        }
    }

    /**
     * 创建令牌
     *
     * @param loginUser 用户信息
     * @return 令牌
     */
    public String createToken(LoginUserVo loginUser) {
        String token = UUID.randomUUID().toString().replace("-", "");
        loginUser.setToken(token);
        refreshToken(loginUser);
        if (loginUser.getUser().getType().equals(RoleEnum.PLATFORM_ADMIN.getValue()) || loginUser.getUser().getType().equals(RoleEnum.SUPER_ADMIN.getValue())) {
            token = TOKEN_PLATFORM_START_PREFIX + token;
        }
        if (loginUser.getUser().getType().equals(RoleEnum.MERCHANT_ADMIN.getValue()) || loginUser.getUser().getType().equals(RoleEnum.SUPER_MERCHANT.getValue())) {
            token = TOKEN_MERCHANT_START_PREFIX + token;
        }
        return token;
    }

    /**
     * 验证令牌有效期，相差不足20分钟，自动刷新缓存
     *
     * @param loginUser LoginUserVo
     */
    public void verifyToken(LoginUserVo loginUser) {
        long expireTime = loginUser.getExpireTime();
        long currentTime = System.currentTimeMillis();
        if (expireTime - currentTime <= MILLIS_MINUTE_TEN) {
            refreshToken(loginUser);
        }
    }

    /**
     * 刷新令牌有效期
     *
     * @param loginUser 登录信息
     */
    public void refreshToken(LoginUserVo loginUser) {
        loginUser.setLoginTime(System.currentTimeMillis());
        loginUser.setExpireTime(loginUser.getLoginTime() + expireTime * MILLIS_MINUTE);
        // 根据uuid将loginUser缓存
        String userKey = "";
        if (loginUser.getUser().getType().equals(RoleEnum.PLATFORM_ADMIN.getValue()) || loginUser.getUser().getType().equals(RoleEnum.SUPER_ADMIN.getValue())) {
            userKey = getPlatformTokenKey(loginUser.getToken());
        }
        if (loginUser.getUser().getType().equals(RoleEnum.MERCHANT_ADMIN.getValue()) || loginUser.getUser().getType().equals(RoleEnum.SUPER_MERCHANT.getValue())) {
            userKey = getMerchantTokenKey(loginUser.getToken());
        }
        redisUtil.set(userKey, loginUser, (long) expireTime, TimeUnit.MINUTES);
    }

    /**
     * 获取请求token
     *
     * @param request HttpServletRequest
     * @return token
     */
    private String getToken(HttpServletRequest request) {
        String token = request.getHeader(Constants.HEADER_AUTHORIZATION_KEY);
        if (StrUtil.isEmpty(token)) {
            return token;
        }
        if (token.startsWith(TOKEN_PLATFORM_START_PREFIX)) {
            token = getPlatformTokenKey(token.replace(TOKEN_PLATFORM_START_PREFIX, ""));
        }
        if (token.startsWith(TOKEN_MERCHANT_START_PREFIX)) {
            token = getMerchantTokenKey(token.replace(TOKEN_MERCHANT_START_PREFIX, ""));
        }
        return token;
    }

    private String getPlatformTokenKey(String uuid) {
        return TOKEN_PLATFORM_REDIS + uuid;
    }

    private String getMerchantTokenKey(String uuid) {
        return TOKEN_MERCHANT_REDIS + uuid;
    }
}
