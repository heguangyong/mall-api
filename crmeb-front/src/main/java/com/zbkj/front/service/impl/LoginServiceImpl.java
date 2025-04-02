package com.zbkj.front.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.zbkj.common.constants.*;
import com.zbkj.common.exception.CrmebException;
import com.zbkj.common.model.coupon.Coupon;
import com.zbkj.common.model.merchant.Merchant;
import com.zbkj.common.model.merchant.MerchantEmployee;
import com.zbkj.common.model.user.User;
import com.zbkj.common.model.user.UserToken;
import com.zbkj.common.request.*;
import com.zbkj.common.response.FrontLoginConfigResponse;
import com.zbkj.common.response.LoginResponse;
import com.zbkj.common.response.employee.EmployeeMerchantResponse;
import com.zbkj.common.response.employee.FrontMerchantEmployeeResponse;
import com.zbkj.common.result.CommonResultCode;
import com.zbkj.common.result.WechatResultCode;
import com.zbkj.common.token.FrontTokenComponent;
import com.zbkj.common.utils.*;
import com.zbkj.common.vo.*;
import com.zbkj.front.service.LoginService;
import com.zbkj.service.service.*;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * 移动端登录服务类
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
public class LoginServiceImpl implements LoginService {

    private static final Logger logger = LoggerFactory.getLogger(LoginServiceImpl.class);

    @Autowired
    private UserService userService;

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private FrontTokenComponent tokenComponent;
    @Autowired
    private SmsService smsService;
    @Autowired
    private SystemConfigService systemConfigService;
    @Autowired
    private TransactionTemplate transactionTemplate;
    @Autowired
    private WechatService wechatService;
    @Autowired
    private UserTokenService userTokenService;
    @Autowired
    private CouponService couponService;
    @Autowired
    private MerchantEmployeeService merchantEmployeeService;
    @Autowired
    private MerchantService merchantService;

    /**
     * 发送短信验证码
     *
     * @param phone 手机号
     * @return Boolean
     */
    @Override
    public Boolean sendLoginCode(String phone) {
        return smsService.sendCommonCode(phone, SmsConstants.VERIFICATION_CODE_SCENARIO_LOGIN);
    }

    /**
     * 退出登录
     *
     * @param request HttpServletRequest
     */
    @Override
    public void loginOut(HttpServletRequest request) {
        tokenComponent.logout(request);
    }

    /**
     * 手机号验证码登录
     *
     * @param loginRequest 登录信息
     * @return LoginResponse
     */
    @Override
    public LoginResponse phoneCaptchaLogin(LoginMobileRequest loginRequest) {
        if (StrUtil.isBlank(loginRequest.getCaptcha())) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "手机号码验证码不能为空");
        }
        Integer spreadPid = Optional.ofNullable(loginRequest.getSpreadPid()).orElse(0);
        //检测验证码
        smsService.checkValidateCode(SmsConstants.VERIFICATION_CODE_SCENARIO_LOGIN, loginRequest.getPhone(), loginRequest.getCaptcha());
        //查询用户信息
        User user = userService.getByPhone(loginRequest.getPhone());
        if (ObjectUtil.isNull(user)) {// 此用户不存在，走新用户注册流程
            user = userService.registerPhone(loginRequest.getPhone(), spreadPid);
            return getLoginResponse_V1_3(user, true);
        }
        if (!user.getStatus()) {
            throw new CrmebException("当前帐户已禁用，请与管理员联系！");
        }
        return commonLogin(user, spreadPid);

    }

    /**
     * 手机号密码登录
     *
     * @param loginRequest 登录信息
     * @return LoginResponse
     */
    @Override
    public LoginResponse phonePasswordLogin(LoginPasswordRequest loginRequest) {
        if (StrUtil.isBlank(loginRequest.getPassword())) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "密码不能为空");
        }

        //查询用户信息
        User user = userService.getByPhone(loginRequest.getPhone());
        if (ObjectUtil.isNull(user)) {// 此用户不存在，走新用户注册流程
            throw new CrmebException("用户名或密码不正确");
        }
        if (!CrmebUtil.encryptPassword(loginRequest.getPassword(), loginRequest.getPhone()).equals(user.getPwd())) {
            throw new CrmebException("用户名或密码不正确");
        }
        if (!user.getStatus()) {
            throw new CrmebException("当前帐户已禁用，请与管理员联系！");
        }
        Integer spreadPid = Optional.ofNullable(loginRequest.getSpreadPid()).orElse(0);
        return commonLogin(user, spreadPid);
    }

    /**
     * 微信公众号授权登录
     *
     * @param request 登录参数
     * @return LoginResponse
     */
    @Override
    public LoginResponse wechatPublicLogin(WechatPublicLoginRequest request) {
        // 通过code获取获取公众号授权信息
        WeChatOauthToken oauthToken = wechatService.getOauth2AccessToken(request.getCode());
        //检测是否存在
        UserToken userToken = userTokenService.getByOpenidAndType(oauthToken.getOpenId(), UserConstants.USER_TOKEN_TYPE_WECHAT);
        Integer spreadPid = Optional.ofNullable(request.getSpreadPid()).orElse(0);
        LoginResponse loginResponse = new LoginResponse();
        if (ObjectUtil.isNotNull(userToken)) {// 已存在，正常登录
            User user = userService.getById(userToken.getUid());
            if (!user.getStatus()) {
                throw new CrmebException("当前账户已禁用，请联系管理员！");
            }
            return commonLogin(user, spreadPid);
        }
        // 没有用户，走创建用户流程
        // 从微信获取用户信息，存入Redis中，将key返回给前端，前端在下一步绑定手机号的时候下发
        WeChatAuthorizeLoginUserInfoVo userInfo = wechatService.getSnsUserInfo(oauthToken.getAccessToken(), oauthToken.getOpenId());
        RegisterThirdUserRequest registerThirdUserRequest = new RegisterThirdUserRequest();
        BeanUtils.copyProperties(userInfo, registerThirdUserRequest);
        registerThirdUserRequest.setSpreadPid(spreadPid);
        registerThirdUserRequest.setType(UserConstants.REGISTER_TYPE_WECHAT);
        registerThirdUserRequest.setOpenId(oauthToken.getOpenId());
        String key = SecureUtil.md5(oauthToken.getOpenId());
        redisUtil.set(key, JSONObject.toJSONString(registerThirdUserRequest), (long) (60 * 2), TimeUnit.MINUTES);

        loginResponse.setType(LoginConstants.LOGIN_STATUS_REGISTER);
        loginResponse.setKey(key);
        return loginResponse;
    }

    /**
     * 微信登录小程序授权登录
     *
     * @param request 用户参数
     * @return LoginResponse
     */
    @Override
    public LoginResponse wechatRoutineLogin(RegisterThirdUserRequest request) {
        WeChatMiniAuthorizeVo response = wechatService.miniAuthCode(request.getCode());
        //检测是否存在
        UserToken userToken = userTokenService.getByOpenidAndType(response.getOpenId(), UserConstants.USER_TOKEN_TYPE_ROUTINE);
        Integer spreadPid = Optional.ofNullable(request.getSpreadPid()).orElse(0);
        LoginResponse loginResponse = new LoginResponse();
        if (ObjectUtil.isNotNull(userToken)) {// 已存在，正常登录
            User user = userService.getById(userToken.getUid());
            if (!user.getStatus()) {
                throw new CrmebException("当前账户已禁用，请联系管理员！");
            }
            return commonLogin(user, spreadPid);
        }
        request.setSpreadPid(spreadPid);
        request.setType(UserConstants.REGISTER_TYPE_ROUTINE);
        request.setOpenId(response.getOpenId());
        String key = SecureUtil.md5(response.getOpenId());
        redisUtil.set(key, JSONObject.toJSONString(request), (long) (60 * 2), TimeUnit.MINUTES);
        loginResponse.setType(LoginConstants.LOGIN_STATUS_REGISTER);
        loginResponse.setKey(key);
        return loginResponse;
    }

    /**
     * 微信注册绑定手机号
     *
     * @param request 请求参数
     * @return 登录信息
     */
    @Override
    public LoginResponse wechatRegisterBindingPhone(WxBindingPhoneRequest request) {
        // 检验并获取手机号
        checkBindingPhone(request);

        // 进入创建用户绑定手机号流程
        String value = redisUtil.get(request.getKey());
        if (StrUtil.isBlank(value)) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "用户缓存已过期，请清除缓存重新登录");
        }
        RegisterThirdUserRequest registerThirdUserRequest = JSONObject.parseObject(value, RegisterThirdUserRequest.class);
        if (!request.getType().equals(registerThirdUserRequest.getType())) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "用户的类型与缓存中的类型不符");
        }

        boolean isNew = true;
        User user = userService.getByPhone(request.getPhone());
        // 查询是否用对应得token
        Integer userTokenType = getUserTokenType(request.getType());
        if (ObjectUtil.isNotNull(user)) {// 历史用户校验
            if (!user.getStatus()) {
                throw new CrmebException("当前账户已禁用，请联系管理员！");
            }
            if (request.getType().equals(UserConstants.REGISTER_TYPE_WECHAT) && user.getIsWechatPublic()) {
                throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "该手机号已绑定微信公众号");
            }
            if (request.getType().equals(UserConstants.REGISTER_TYPE_ROUTINE) && user.getIsWechatRoutine()) {
                throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "该手机号已绑定微信小程序");
            }
            if (request.getType().equals(UserConstants.REGISTER_TYPE_ANDROID_WX) && user.getIsWechatAndroid()) {
                throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "该手机号已绑定微信Android");
            }
            if (request.getType().equals(UserConstants.REGISTER_TYPE_IOS_WX) && user.getIsWechatIos()) {
                throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "该手机号已绑定微信IOS");
            }
            UserToken userToken = userTokenService.getTokenByUserId(user.getId(), userTokenType);
            if (ObjectUtil.isNotNull(userToken)) {
                throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "该手机号已被注册");
            }
            isNew = false;
        } else {
            user = new User();
            user.setRegisterType(registerThirdUserRequest.getType());
            user.setPhone(request.getPhone());
            user.setAccount(request.getPhone());
            user.setSpreadUid(0);
            user.setPwd(CommonUtil.createPwd(request.getPhone()));
            user.setNickname(CommonUtil.createNickName(request.getPhone()));
            user.setAvatar(systemConfigService.getValueByKey(SysConfigConstants.USER_DEFAULT_AVATAR_CONFIG_KEY));
            user.setSex(0);
            user.setAddress("");
            user.setLevel(1);
        }
        switch (request.getType()) {
            case UserConstants.REGISTER_TYPE_WECHAT:
                user.setIsWechatPublic(true);
                break;
            case UserConstants.REGISTER_TYPE_ROUTINE:
                user.setIsWechatRoutine(true);
                break;
            case UserConstants.REGISTER_TYPE_IOS_WX:
                user.setIsWechatIos(true);
                break;
            case UserConstants.REGISTER_TYPE_ANDROID_WX:
                user.setIsWechatAndroid(true);
                break;
        }
        user.setLastLoginTime(CrmebDateUtil.nowDateTime());
        User finalUser = user;
        boolean finalIsNew = isNew;
        Boolean execute = transactionTemplate.execute(e -> {
            Integer spreadPid = Optional.ofNullable(registerThirdUserRequest.getSpreadPid()).orElse(0);
            if (finalIsNew) {// 新用户
                // 分销绑定
                if (spreadPid > 0 && userService.checkBingSpread(finalUser, registerThirdUserRequest.getSpreadPid(), "new")) {
                    finalUser.setSpreadUid(registerThirdUserRequest.getSpreadPid());
                    finalUser.setSpreadTime(CrmebDateUtil.nowDateTime());
                    userService.updateSpreadCountByUid(registerThirdUserRequest.getSpreadPid(), Constants.OPERATION_TYPE_ADD);
                }
                userService.save(finalUser);
            } else {
                userService.updateById(finalUser);
                if (finalUser.getSpreadUid().equals(0) && spreadPid > 0) {
                    // 绑定推广关系
                    bindSpread(finalUser, spreadPid);
                }
            }
            userTokenService.bind(registerThirdUserRequest.getOpenId(), userTokenType, finalUser.getId());
            return Boolean.TRUE;
        });
        if (!execute) {
            logger.error(StrUtil.format("微信用户注册生成失败，openid = {}, key = {}", registerThirdUserRequest.getOpenId(), request.getKey()));
            throw new CrmebException(StrUtil.format("微信用户注册生成失败，openid = {}, key = {}", registerThirdUserRequest.getOpenId(), request.getKey()));
        }
        return getLoginResponse_V1_3(finalUser, isNew);
    }

    /**
     * 如果是商家管理员，那么自动赋予当前登录账号权限 谨慎调用
     *
     * @param user 当前登录的用户信息
     * @return 移动端商家管理权限
     */
    @Override
    public LoginResponse isEmployeeAuto(User user) {
        List<MerchantEmployee> shopMangerList = merchantEmployeeService.getShopMangerByUserId(user.getId());
        if (!shopMangerList.isEmpty()) {
            return commonLogin(user, 0);
        }
        return null;

    }

    /**
     * 获取用户Token类型
     *
     * @param type 用户注册类型
     */
    private Integer getUserTokenType(String type) {
        Integer userTokenType = 0;
        switch (type) {
            case UserConstants.REGISTER_TYPE_WECHAT:
                userTokenType = UserConstants.USER_TOKEN_TYPE_WECHAT;
                break;
            case UserConstants.REGISTER_TYPE_ROUTINE:
                userTokenType = UserConstants.USER_TOKEN_TYPE_ROUTINE;
                break;
            case UserConstants.REGISTER_TYPE_IOS_WX:
                userTokenType = UserConstants.USER_TOKEN_TYPE_IOS_WX;
                break;
            case UserConstants.REGISTER_TYPE_ANDROID_WX:
                userTokenType = UserConstants.USER_TOKEN_TYPE_ANDROID_WX;
                break;
        }
        return userTokenType;
    }

    /**
     * 绑定手机号数据校验
     */
    private void checkBindingPhone(WxBindingPhoneRequest request) {
        if (request.getType().equals(UserConstants.REGISTER_TYPE_WECHAT) || request.getType().equals(UserConstants.REGISTER_TYPE_IOS_WX) || request.getType().equals(UserConstants.REGISTER_TYPE_ANDROID_WX)) {
            if (StrUtil.isBlank(request.getPhone()) || StrUtil.isBlank(request.getCaptcha())) {
                throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "手机号、验证码不能为空");
            }
            smsService.checkValidateCode(SmsConstants.VERIFICATION_CODE_SCENARIO_LOGIN, request.getPhone(), request.getCaptcha());
        } else {
            // 小程序自填手机号校验
            if (StrUtil.isNotBlank(request.getCaptcha())) {
                if (StrUtil.isBlank(request.getPhone())) {
                    throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "手机号不能为空");
                }
                smsService.checkValidateCode(SmsConstants.VERIFICATION_CODE_SCENARIO_LOGIN, request.getPhone(), request.getCaptcha());
                return;
            }
            //  获取微信小程序手机号 参数校验
            if (StrUtil.isBlank(request.getCode())) {
                throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "小程序获取手机号code不能为空");
            }
            if (StrUtil.isBlank(request.getEncryptedData())) {
//                throw new CrmebException("小程序获取手机号加密数据不能为空");
                throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "请认证微信账号：获取手机号码失败");
            }
            if (StrUtil.isBlank(request.getIv())) {
                throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "小程序获取手机号加密算法的初始向量不能为空");
            }
            // 获取appid
            String programAppId = systemConfigService.getValueByKey(WeChatConstants.WECHAT_MINI_APPID);
            if (StringUtils.isBlank(programAppId)) {
                throw new CrmebException(WechatResultCode.ROUTINE_APPID_NOT_CONFIG);
            }

            WeChatMiniAuthorizeVo response = wechatService.miniAuthCode(request.getCode());
            System.out.println("小程序登陆成功 = " + JSON.toJSONString(response));
            String decrypt = WxUtil.decrypt(programAppId, request.getEncryptedData(), response.getSessionKey(), request.getIv());
            if (StrUtil.isBlank(decrypt)) {
                throw new CrmebException("微信小程序获取手机号解密失败");
            }
            JSONObject jsonObject = JSONObject.parseObject(decrypt);
            if (StrUtil.isBlank(jsonObject.getString("phoneNumber"))) {
                throw new CrmebException("微信小程序没有获取到有效的手机号");
            }
            request.setPhone(jsonObject.getString("phoneNumber"));
        }
    }

    /**
     * 绑定分销关系
     *
     * @param user      User 用户user类
     * @param spreadUid Integer 推广人id
     * @return Boolean
     */
    private Boolean bindSpread(User user, Integer spreadUid) {
        Boolean checkBingSpread = userService.checkBingSpread(user, spreadUid, "old");
        if (!checkBingSpread) return false;

        user.setSpreadUid(spreadUid);
        user.setSpreadTime(CrmebDateUtil.nowDateTime());

        Boolean execute = transactionTemplate.execute(e -> {
            userService.updateById(user);
            userService.updateSpreadCountByUid(spreadUid, Constants.OPERATION_TYPE_ADD);
            return Boolean.TRUE;
        });
        if (!execute) {
            logger.error(StrUtil.format("绑定推广人时出错，userUid = {}, spreadUid = {}", user.getId(), spreadUid));
        }
        return execute;
    }

    /**
     * 获取登录配置
     */
    @Override
    public FrontLoginConfigResponse getLoginConfig() {
        List<String> keyList = new ArrayList<>();
        keyList.add(SysConfigConstants.CONFIG_KEY_MOBILE_LOGIN_LOGO);
        keyList.add(SysConfigConstants.WECHAT_PUBLIC_LOGIN_TYPE);
        keyList.add(SysConfigConstants.WECHAT_ROUTINE_PHONE_VERIFICATION);
        keyList.add(SysConfigConstants.CONFIG_KEY_MOBILE_LOGIN_LOGO);
        MyRecord record = systemConfigService.getValuesByKeyList(keyList);
        FrontLoginConfigResponse response = new FrontLoginConfigResponse();
        response.setLogo(record.getStr(SysConfigConstants.CONFIG_KEY_MOBILE_LOGIN_LOGO));
        response.setWechatBrowserVisit(record.getStr(SysConfigConstants.WECHAT_PUBLIC_LOGIN_TYPE));
        response.setRoutinePhoneVerification(record.getStr(SysConfigConstants.WECHAT_ROUTINE_PHONE_VERIFICATION));
        response.setMobileLoginLogo(record.getStr(SysConfigConstants.CONFIG_KEY_MOBILE_LOGIN_LOGO));
        return response;
    }

    /**
     * 微信登录App授权登录
     */
    @Override
    public LoginResponse wechatAppLogin(RegisterAppWxRequest request) {
        //检测是否存在
        UserToken userToken = null;

        if (request.getType().equals(UserConstants.REGISTER_TYPE_IOS_WX)) {
            userToken = userTokenService.getByOpenidAndType(request.getOpenId(), UserConstants.USER_TOKEN_TYPE_IOS_WX);
        }
        if (request.getType().equals(UserConstants.REGISTER_TYPE_ANDROID_WX)) {
            userToken = userTokenService.getByOpenidAndType(request.getOpenId(), UserConstants.USER_TOKEN_TYPE_ANDROID_WX);
        }
        if (ObjectUtil.isNotNull(userToken)) {// 已存在，正常登录
            User user = userService.getById(userToken.getUid());
            if (ObjectUtil.isNull(user) && user.getIsLogoff()) {
                throw new CrmebException("当前账户异常，请联系管理员！");
            }
            if (!user.getStatus()) {
                throw new CrmebException("当前账户已禁用，请联系管理员！");
            }
            // 记录最后一次登录时间
            user.setLastLoginTime(CrmebDateUtil.nowDateTime());
            Boolean execute = transactionTemplate.execute(e -> {
                userService.updateById(user);
                return Boolean.TRUE;
            });
            if (!execute) {
                logger.error(StrUtil.format("APP微信登录记录最后一次登录时间失败，uid={}", user.getId()));
            }
            return getLoginResponse(user);
        }
        // 没有用户，走创建用户流程
        // 从微信获取用户信息，存入Redis中，将key返回给前端，前端在下一步绑定手机号的时候下发
        RegisterThirdUserRequest registerThirdUserRequest = new RegisterThirdUserRequest();
        registerThirdUserRequest.setSpreadPid(0);
        registerThirdUserRequest.setType(request.getType());
        registerThirdUserRequest.setOpenId(request.getOpenId());
        String key = SecureUtil.md5(request.getOpenId());
        redisUtil.set(key, JSONObject.toJSONString(registerThirdUserRequest), (long) (60 * 2), TimeUnit.MINUTES);
        LoginResponse loginResponse = new LoginResponse();
        loginResponse.setType(LoginConstants.LOGIN_STATUS_REGISTER);
        loginResponse.setKey(key);
        return loginResponse;
    }

    /**
     * ios登录
     */
    @Override
    public LoginResponse ioslogin(IosLoginRequest loginRequest) {
        // 检测是否存在
        UserToken userToken = userTokenService.getByOpenidAndType(loginRequest.getOpenId(), UserConstants.USER_TOKEN_TYPE_IOS);
        if (ObjectUtil.isNotNull(userToken)) {// 已存在，正常登录
            User user = userService.getById(userToken.getUid());
            if (ObjectUtil.isNull(user) && user.getIsLogoff()) {
                throw new CrmebException("当前账户异常，请联系管理员！");
            }
            if (!user.getStatus()) {
                throw new CrmebException("当前账户已禁用，请联系管理员！");
            }
            // 记录最后一次登录时间
            user.setLastLoginTime(CrmebDateUtil.nowDateTime());
            Boolean execute = transactionTemplate.execute(e -> {
                userService.updateById(user);
                return Boolean.TRUE;
            });
            if (!execute) {
                logger.error(StrUtil.format("App记录用户最后一次登陆时间失败，uid={}", user.getId()));
            }
            return getLoginResponse(user);
        }
        // 没有用户Ios直接创建新用户
        User user = new User();
        String randomString = RandomUtil.randomString(11);
        user.setPhone("");
        user.setAccount(randomString);
        user.setSpreadUid(0);
        user.setPwd("123");
        user.setRegisterType(UserConstants.REGISTER_TYPE_IOS);
        user.setNickname(CommonUtil.createNickName(randomString));
        user.setAvatar(systemConfigService.getValueByKey(SysConfigConstants.USER_DEFAULT_AVATAR_CONFIG_KEY));
        user.setSex(0);
        user.setAddress("");
        user.setIsBindingIos(true);
        user.setLastLoginTime(CrmebDateUtil.nowDateTime());
        user.setLevel(1);
        Boolean execute = transactionTemplate.execute(e -> {
            userService.save(user);
            userTokenService.bind(loginRequest.getOpenId(), UserConstants.USER_TOKEN_TYPE_IOS, user.getId());
            return Boolean.TRUE;
        });
        if (!execute) {
            throw new CrmebException("App用户注册生成失败，nickName = " + user.getNickname());
        }
        return getLoginResponse_V1_3(user, true);
    }

    /**
     * 校验token是否有效
     *
     * @return true 有效， false 无效
     */
    @Override
    public Boolean tokenIsExist() {
        Integer userId = userService.getUserId();
        return userId > 0;
    }

    private LoginResponse commonLogin(User user, Integer spreadPid) {
        if (!user.getStatus()) {
            throw new CrmebException("当前账户已禁用，请联系管理员！");
        }
        if (user.getSpreadUid().equals(0) && spreadPid > 0) {
            // 绑定推广关系
            bindSpread(user, spreadPid);
        }
        // 记录最后一次登录时间
        user.setLastLoginTime(CrmebDateUtil.nowDateTime());
        boolean b = userService.updateById(user);
        if (!b) {
            logger.error("用户登录时，记录最后一次登录时间出错,uid = " + user.getId());
        }
        return getLoginResponse(user);
    }

    private LoginResponse getLoginResponse(User user) {
        //生成token
        LoginResponse loginResponse = new LoginResponse();
        loginResponse.setToken(tokenComponent.createUserToken(user));
        loginResponse.setId(user.getId());
        loginResponse.setNickname(user.getNickname());
        loginResponse.setPhone(CrmebUtil.maskMobile(user.getPhone()));
        loginResponse.setType(LoginConstants.LOGIN_STATUS_LOGIN);
        loginResponse.setAvatar(user.getAvatar());
//        loginResponse.setMerchantEmployeeList(loginUserVo.getMerchantEmployeeList());
        return loginResponse;
    }


    private LoginResponse getLoginResponse_V1_3(User user, Boolean isNew) {
        //生成token
        LoginResponse loginResponse = new LoginResponse();
        loginResponse.setToken(tokenComponent.createUserToken(user));
        loginResponse.setId(user.getId());
        loginResponse.setNickname(user.getNickname());
        loginResponse.setPhone(CrmebUtil.maskMobile(user.getPhone()));
        loginResponse.setType(LoginConstants.LOGIN_STATUS_LOGIN);
        loginResponse.setAvatar(user.getAvatar());
//        loginResponse.setMerchantEmployeeList(loginUserVo.getMerchantEmployeeList());
        if (isNew) {
            loginResponse.setIsNew(true);
            List<Coupon> couponList = couponService.sendNewPeopleGift(user.getId());
            if (CollUtil.isNotEmpty(couponList)) {
                loginResponse.setNewPeopleCouponList(couponList);
            }
        }
        return loginResponse;
    }

    /**
     * 创建令牌
     *
     * @param loginUser 用户信息
     * @return 令牌
     */
    public LoginFrontUserVo createToken(LoginFrontUserVo loginUser) {
        String token = UUID.randomUUID().toString().replace("-", "");
        loginUser.setToken(token);
        refreshToken(loginUser);

        redisUtil.set(loginUser.getToken(), loginUser.getUser().getId(), Constants.TOKEN_EXPRESS_MINUTES, TimeUnit.MINUTES);
        return loginUser;
    }

    /**
     * 刷新令牌有效期
     *
     * @param loginFrontUser 登录信息
     */
    private void refreshToken(LoginFrontUserVo loginFrontUser) {
        loginFrontUser.setLoginTime(System.currentTimeMillis());
        loginFrontUser.setExpireTime(loginFrontUser.getLoginTime() + FrontTokenComponent.expireTime * FrontTokenComponent.MILLIS_MINUTE);
        // 根据uuid将loginUser缓存
        String userKey = "";
        // 根据当前用户检测是否店铺管理员
        List<MerchantEmployee> shopMangerByUserIds = merchantEmployeeService.getShopMangerByUserId(loginFrontUser.getUser().getId());

        String userToken = loginFrontUser.getToken();
        // 如果是普通用户
        if (CollUtil.isEmpty(shopMangerByUserIds)) {
            userKey = getNormalUserTokenKey(userToken);
            loginFrontUser.setToken(RedisConstants.WEB_TOKEN_USER_NORMAL_KEY + userToken);
            loginFrontUser.setMerchantEmployeeList(null);

            redisUtil.set(userKey, loginFrontUser, (long) FrontTokenComponent.expireTime, TimeUnit.MINUTES);
            redisUtil.addSet(StrUtil.format(RedisConstants.FRONT_USER_TOKEN_SET_KEY, loginFrontUser.getUser().getId()), userToken);
            return;
        }
        // 如果是店铺管理员
        userKey = getShopManagerTokenKey(userToken);
        loginFrontUser.setToken(RedisConstants.WEB_TOKEN_USER_SHOP_MANAGER_KEY + userToken);
        List<FrontMerchantEmployeeResponse> currentMerchantList = new ArrayList<>();
        shopMangerByUserIds.forEach(employee -> {
            Merchant merchant = merchantService.getByIdException(employee.getMerId());
            FrontMerchantEmployeeResponse frEmployee = new FrontMerchantEmployeeResponse();
            BeanUtils.copyProperties(employee, frEmployee);
            EmployeeMerchantResponse employeeMerchant = new EmployeeMerchantResponse();
            BeanUtils.copyProperties(merchant, employeeMerchant);
            frEmployee.setCurrentMerchant(employeeMerchant);
            currentMerchantList.add(frEmployee);
        });
        loginFrontUser.setMerchantEmployeeList(currentMerchantList);
        // 缓存用户信息
        redisUtil.set(userKey, loginFrontUser, (long) FrontTokenComponent.expireTime, TimeUnit.MINUTES);
        redisUtil.addSet(StrUtil.format(RedisConstants.FRONT_USER_TOKEN_SET_KEY, loginFrontUser.getUser().getId()), userToken);
    }

    private String getNormalUserTokenKey(String uuid) {
        return FrontTokenComponent.TOKEN_USER_NORMAL_REDIS_KEY + uuid;
    }

    private String getShopManagerTokenKey(String uuid) {
        return FrontTokenComponent.TOKEN_USER_SHOP_MANAGER_REDIS_KEY + uuid;
    }

}
