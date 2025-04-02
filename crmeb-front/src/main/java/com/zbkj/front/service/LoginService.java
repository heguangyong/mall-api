package com.zbkj.front.service;

import com.zbkj.common.model.user.User;
import com.zbkj.common.request.*;
import com.zbkj.common.response.FrontLoginConfigResponse;
import com.zbkj.common.response.LoginResponse;

import javax.servlet.http.HttpServletRequest;

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
public interface LoginService {

    /**
     * 退出登录
     * @param request HttpServletRequest
     */
    void loginOut(HttpServletRequest request);

    /**
     * 发送短信验证码
     * @param phone 手机号
     * @return Boolean
     */
    Boolean sendLoginCode(String phone);

    /**
     * 手机号验证码登录
     * @param loginRequest 登录信息
     * @return LoginResponse
     */
    LoginResponse phoneCaptchaLogin(LoginMobileRequest loginRequest);

    /**
     * 手机号密码登录
     * @param loginRequest 登录信息
     * @return LoginResponse
     */
    LoginResponse phonePasswordLogin(LoginPasswordRequest loginRequest);

    /**
     * 微信公众号授权登录
     * @param request 登录参数
     * @return LoginResponse
     */
    LoginResponse wechatPublicLogin(WechatPublicLoginRequest request);

    /**
     * 微信登录小程序授权登录
     * @param request 用户参数
     * @return LoginResponse
     */
    LoginResponse wechatRoutineLogin(RegisterThirdUserRequest request);

    /**
     * 微信注册绑定手机号
     * @param request 请求参数
     * @return 登录信息
     */
    LoginResponse wechatRegisterBindingPhone(WxBindingPhoneRequest request);

    /**
     * 如果是商家管理员，那么自动赋予当前登录账号权限
     * @param user 当前登录的用户信息
     * @return 移动端商家管理权限
     */
    LoginResponse isEmployeeAuto(User user);

    /**
     * 获取登录配置
     */
    FrontLoginConfigResponse getLoginConfig();

    /**
     * 微信登录App授权登录
     */
    LoginResponse wechatAppLogin(RegisterAppWxRequest request);

    /**
     * ios登录
     */
    LoginResponse ioslogin(IosLoginRequest loginRequest);

    /**
     * 校验token是否有效
     * @return true 有效， false 无效
     */
    Boolean tokenIsExist();
}
