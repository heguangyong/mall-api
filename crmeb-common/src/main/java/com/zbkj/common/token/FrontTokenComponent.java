package com.zbkj.common.token;

import cn.hutool.core.util.StrUtil;
import com.zbkj.common.constants.Constants;
import com.zbkj.common.constants.RedisConstants;
import com.zbkj.common.model.user.User;
import com.zbkj.common.utils.RedisUtil;
import com.zbkj.common.utils.RequestUtil;
import com.zbkj.common.vo.LoginFrontUserVo;
import com.zbkj.common.vo.LoginUserVo;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
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
public class FrontTokenComponent {

    @Resource
    private RedisUtil redisUtil;

    public static final Long MILLIS_MINUTE_TEN = 20 * 60 * 1000L;

    public static final Long MILLIS_MINUTE = 60 * 1000L;

    // 令牌有效期（默认30分钟） todo 调试期改为5小时
//    private static final int expireTime = 30;
    public static final int expireTime = 5 * 60;

    public static final String TOKEN_USER_NORMAL_REDIS_KEY = "TOKEN:USER:NORMAL:";
    public static final String TOKEN_USER_SHOP_MANAGER_REDIS_KEY = "TOKEN:USER:SHOPMANAGER:";


    /**
     * 创建移动端用户令牌
     *
     * @param user 用户信息
     * @return 令牌
     */
    public String createUserToken(User user) {
        String token = UUID.randomUUID().toString().replace("-", "");
        String userTokenKey = getRedisUserTokenKey(token);
        redisUtil.set(userTokenKey, user.getId(), Constants.TOKEN_EXPRESS_MINUTES, TimeUnit.MINUTES);
        // 用户token加入redis的set集合
        redisUtil.addSet(StrUtil.format(RedisConstants.FRONT_USER_TOKEN_SET_KEY, user.getId()), userTokenKey);
        return getFrontUserTokenKey(token);
    }

    /**
     * 移动端前端使用的token
     */
    private String getFrontUserTokenKey(String uuid) {
        return RedisConstants.WEB_TOKEN_USER_NORMAL_KEY + uuid;
    }

    private String getFrontMerchantUserTokenKey(String uuid) {
        return RedisConstants.WEB_TOKEN_USER_SHOP_MANAGER_KEY + uuid;
    }

    /**
     * 移动端redis缓存的token
     */
    private String getRedisUserTokenKey(String uuid) {
        return TOKEN_USER_NORMAL_REDIS_KEY + uuid;
    }

    private String getRedisMerchantUserTokenKey(String uuid) {
        return TOKEN_USER_SHOP_MANAGER_REDIS_KEY + uuid;
    }

    public LoginUserVo getLoginUser(HttpServletRequest request) {
        // 获取请求携带的令牌
        String token = getToken(request);
        if (StrUtil.isNotBlank(token)) {
            return redisUtil.get(token);
        }
        return null;
    }


    /**
     * 删除用户身份信息
     */
    public void delLoginUser(String token) {
        if (StrUtil.isNotBlank(token)) {
            redisUtil.delete(token);
        }
    }



    /**
     * 获取请求token
     *
     * @param request HttpServletRequest
     * @return token
     */
    public String getToken(HttpServletRequest request) {
        String token = request.getHeader(Constants.HEADER_AUTHORIZATION_KEY);
        if (StrUtil.isNotBlank(token) && token.startsWith(RedisConstants.WEB_TOKEN_USER_NORMAL_KEY)) {
            token = token.replace(RedisConstants.WEB_TOKEN_USER_NORMAL_KEY, "");
            token = RedisConstants.REDIS_TOKEN_USER_NORMAL_KEY + token;
        }
        if (StrUtil.isNotBlank(token) && token.startsWith(RedisConstants.WEB_TOKEN_USER_SHOP_MANAGER_KEY)) {
            token = token.replace(RedisConstants.WEB_TOKEN_USER_SHOP_MANAGER_KEY, "");
            token = RedisConstants.REDIS_TOKEN_USER_SHOP_MANAGER_KEY + token;
        }
        return token;
    }

    /**
     * 获取是否店铺管理端身份
     * @param request
     * @return
     */
    public String getTokenForMerchantEmployee(HttpServletRequest request) {
        String token = request.getHeader(Constants.HEADER_AUTHORIZATION_KEY);
        if (StrUtil.isNotBlank(token) && token.startsWith(RedisConstants.WEB_TOKEN_USER_NORMAL_KEY)) {
            token = token.replace(RedisConstants.WEB_TOKEN_USER_NORMAL_KEY, "");
            token = TOKEN_USER_NORMAL_REDIS_KEY + token;
        }
        if (StrUtil.isNotBlank(token) && token.startsWith(RedisConstants.WEB_TOKEN_USER_SHOP_MANAGER_KEY)) {
            token = token.replace(RedisConstants.WEB_TOKEN_USER_SHOP_MANAGER_KEY, "");
            token = TOKEN_USER_SHOP_MANAGER_REDIS_KEY + token;
        }
        return token;
    }


    /**
     * 推出登录
     *
     * @param request HttpServletRequest
     */
    public void logout(HttpServletRequest request) {
        String token = getToken(request);
        delLoginUser(token);
    }

    public void cleanUserToken(Integer userId) {
        Set<Object> setAll = redisUtil.getSetAll(StrUtil.format(RedisConstants.FRONT_USER_TOKEN_SET_KEY, userId));
        List<String> keyList = new ArrayList<>();
        for (Object member : setAll) {
            keyList.add(member.toString());
        }
        // 清除用户所有token
        keyList.forEach(key -> {
            redisUtil.delete(key);
        });
        // 清除用户token集合
        redisUtil.delete(StrUtil.format(RedisConstants.FRONT_USER_TOKEN_SET_KEY, userId));
    }

    public void cleanMerchantUserToken(Integer userId) {
        // 清除用户侧管理端所有用户相关token
        Boolean exists = redisUtil.exists(StrUtil.format(RedisConstants.FRONT_MERCHANT_USER_TOKEN_SET_KEY, userId));
        if (exists) {
            Set<Object> merchantSetAll = redisUtil.getSetAll(StrUtil.format(RedisConstants.FRONT_MERCHANT_USER_TOKEN_SET_KEY, userId));
            List<String> merKeyList = new ArrayList<>();
            for (Object member : merchantSetAll) {
                merKeyList.add(member.toString());
            }
            // 清除用户所有token
            merKeyList.forEach(key -> {
                redisUtil.delete(key);
            });
            // 清除用户token集合
            redisUtil.delete(StrUtil.format(RedisConstants.FRONT_MERCHANT_USER_TOKEN_SET_KEY, userId));
        }
    }

    /**
     * 获取当前登录用户id
     */
    public Integer getUserId() {
        HttpServletRequest request = RequestUtil.getRequest();
        String token = getToken(request);
        if (StrUtil.isBlank(token)) {
            return null;
        }
//        return redisUtil.get(getTokenKey(token));
        return redisUtil.get(token);
    }

    /**
     * 获取当前登录的移动端商户管理
     * @return
     */
    public LoginFrontUserVo getUserForMerchantEmployee() {
        HttpServletRequest request = RequestUtil.getRequest();
        String token = getTokenForMerchantEmployee(request);
        if (StrUtil.isEmpty(token)) {
            return null;
        }
        return redisUtil.get(token);
    }


    /**
     * 激活当前要操作的商户
     * @param merchantId 商户id
     */
    public void activeMerchantEmployee(Integer merchantId){
        HttpServletRequest request = RequestUtil.getRequest();
        String token = getTokenForMerchantEmployee(request);
        if (StrUtil.isEmpty(token)) {
            return ;
        }
        LoginFrontUserVo userForMerchantEmployee = redisUtil.get(token);;
        userForMerchantEmployee.setActiveMerchant(merchantId);
        redisUtil.set(token, userForMerchantEmployee, (long) FrontTokenComponent.expireTime, TimeUnit.MINUTES);
    }

    /**
     * 校验token
     */
    public Boolean checkToken(String token) {
        try {
            boolean exists = redisUtil.exists(token);
//            if (exists) {
//                refreshToken(token);
//            }
            return exists;
        } catch (Exception e) {
            return false;
        }
    }

    public Boolean checkForEmployee(String token) {

        try {
            boolean exists = redisUtil.exists(token);
            if (exists) {
                LoginFrontUserVo loginFrontUserVo = redisUtil.get(token);
                redisUtil.set(token, loginFrontUserVo, Constants.TOKEN_EXPRESS_MINUTES, TimeUnit.MINUTES);
            }
            return exists;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 刷新token有效时间
     */
    private void refreshToken(String token) {
        Integer uid = redisUtil.get(token);
        redisUtil.set(token, uid, Constants.TOKEN_EXPRESS_MINUTES, TimeUnit.MINUTES);
    }

    public String createFrontMerchantToken(LoginFrontUserVo loginFrontUserVo) {
        String token = UUID.randomUUID().toString().replace("-", "");
        String userTokenKey = getRedisMerchantUserTokenKey(token);
        redisUtil.set(userTokenKey, loginFrontUserVo, Constants.TOKEN_EXPRESS_MINUTES, TimeUnit.MINUTES);
        // 用户token加入redis的set集合
        redisUtil.addSet(StrUtil.format(RedisConstants.FRONT_MERCHANT_USER_TOKEN_SET_KEY, loginFrontUserVo.getUserId()), userTokenKey);
        return getFrontMerchantUserTokenKey(token);
    }
}
