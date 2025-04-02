package com.zbkj.admin.service;

import com.zbkj.common.request.AjAdminLoginRequest;
import com.zbkj.common.request.LoginAdminUpdatePasswordRequest;
import com.zbkj.common.request.LoginAdminUpdateRequest;
import com.zbkj.common.response.AdminLoginPicResponse;
import com.zbkj.common.response.LoginAdminResponse;
import com.zbkj.common.response.MenusResponse;
import com.zbkj.common.response.SystemLoginResponse;

import java.util.List;

/**
 * 管理端登录服务
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
public interface AdminLoginService {

    /**
     * 平台端登录
     */
    SystemLoginResponse platformLogin(AjAdminLoginRequest request, String ip);

    /**
     * 商户端登录
     */
    SystemLoginResponse merchantLogin(AjAdminLoginRequest request, String ip);

    /**
     * 用户登出
     */
    Boolean logout();

    /**
     * 获取登录页图片
     *
     * @return AdminLoginPicResponse
     */
    AdminLoginPicResponse getLoginPic();

    /**
     * 获取商户端登录页图片
     *
     * @return AdminLoginPicResponse
     */
    AdminLoginPicResponse getMerchantLoginPic();

    /**
     * 获取管理员可访问目录
     *
     * @return List<MenusResponse>
     */
    List<MenusResponse> getMenus();

    /**
     * 根据Token获取对应用户信息
     */
    LoginAdminResponse getInfoByToken();

    /**
     * 修改登录用户信息
     *
     * @param request 请求参数
     * @return Boolean
     */
    Boolean loginAdminUpdate(LoginAdminUpdateRequest request);


    /**
     * 修改登录用户密码
     */
    Boolean loginAdminUpdatePwd(LoginAdminUpdatePasswordRequest request);

    /**
     * 账号登录检测
     * @param account 账号
     * @param type 用户类型
     * @return 账号错误登录次数
     */
    Integer accountDetection(String account, Integer type);

    /**
     * 通过商户ID获取商户超管
     *
     * @param merId 商户ID
     * @return SystemLoginResponse
     */
    SystemLoginResponse ultraViresLoginMerchantByPlat(Integer merId);
}
