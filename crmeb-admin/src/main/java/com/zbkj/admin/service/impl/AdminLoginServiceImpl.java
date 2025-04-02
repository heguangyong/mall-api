package com.zbkj.admin.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.anji.captcha.model.common.ResponseModel;
import com.zbkj.admin.filter.TokenComponent;
import com.zbkj.admin.service.AdminLoginService;
import com.zbkj.admin.service.ValidateCodeService;
import com.zbkj.common.constants.RedisConstants;
import com.zbkj.common.constants.SysConfigConstants;
import com.zbkj.common.enums.RoleEnum;
import com.zbkj.common.exception.CrmebException;
import com.zbkj.common.model.admin.SystemAdmin;
import com.zbkj.common.model.admin.SystemMenu;
import com.zbkj.common.model.admin.SystemPermissions;
import com.zbkj.common.model.merchant.Merchant;
import com.zbkj.common.request.AjAdminLoginRequest;
import com.zbkj.common.request.LoginAdminUpdatePasswordRequest;
import com.zbkj.common.request.LoginAdminUpdateRequest;
import com.zbkj.common.response.AdminLoginPicResponse;
import com.zbkj.common.response.LoginAdminResponse;
import com.zbkj.common.response.MenusResponse;
import com.zbkj.common.response.SystemLoginResponse;
import com.zbkj.common.result.CommonResultCode;
import com.zbkj.common.result.MerchantResultCode;
import com.zbkj.common.utils.CrmebUtil;
import com.zbkj.common.utils.RedisUtil;
import com.zbkj.common.utils.SecurityUtil;
import com.zbkj.common.vo.LoginUserVo;
import com.zbkj.common.vo.MenuTree;
import com.zbkj.service.service.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 管理端登录服务实现类
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
public class AdminLoginServiceImpl implements AdminLoginService {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Resource
    private TokenComponent tokenComponent;

    @Resource
    private AuthenticationManager authenticationManager;

    @Autowired
    private SystemAdminService systemAdminService;

    @Autowired
    private SystemConfigService systemConfigService;

    @Autowired
    private SystemMenuService systemMenuService;

    @Autowired
    private AsyncService asyncService;

    @Autowired
    private ValidateCodeService validateCodeService;
    @Autowired
    private MerchantService merchantService;
    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private SafetyService safetyService;


    /**
     * PC登录
     *
     * @param request   请求信息
     * @param adminType 管理员类型
     * @param ip        ip
     */
    private SystemLoginResponse login(AjAdminLoginRequest request, Integer adminType, String ip) {
        Integer errorNum = accountDetection(request.getAccount(), adminType);
        if (errorNum > 3) {
            if (ObjectUtil.isNull(request.getCaptchaVO())) {
                throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "验证码信息不存在");
            }
            // 校验验证码
            ResponseModel responseModel = safetyService.verifySafetyCode(request.getCaptchaVO());
            if (!responseModel.getRepCode().equals("0000")) {
                logger.error("验证码登录失败，repCode = {}, repMsg = {}", responseModel.getRepCode(), responseModel.getRepMsg());
                accountErrorNumAdd(request.getAccount());
                throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "验证码校验失败");
            }
        }
        // 用户验证
        Authentication authentication = null;
        // 该方法会去调用UserDetailsServiceImpl.loadUserByUsername
        try {
            String principal = request.getAccount() + adminType;
            authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(principal, request.getPwd()));
        } catch (AuthenticationException e) {
            accountErrorNumAdd(request.getAccount());
            if (e instanceof BadCredentialsException) {
                throw new CrmebException("用户不存在或密码错误");
            }
            throw new CrmebException(e.getMessage());
        } catch (CrmebException e) {
            accountErrorNumAdd(request.getAccount());
            throw new CrmebException("账号或者密码不正确");
        }
        LoginUserVo loginUser = (LoginUserVo) authentication.getPrincipal();
        SystemAdmin systemAdmin = loginUser.getUser();
        String token = tokenComponent.createToken(loginUser);

        SystemLoginResponse response = adminToLoginResponse(token, systemAdmin, adminType, ip);
        accountErrorNumClear(request.getAccount());
        return response;
    }

    private SystemLoginResponse adminToLoginResponse(String token, SystemAdmin systemAdmin, Integer adminType, String ip) {
        SystemLoginResponse systemAdminResponse = new SystemLoginResponse();
        systemAdminResponse.setToken(token);
        BeanUtils.copyProperties(systemAdmin, systemAdminResponse);

        if (adminType.equals(RoleEnum.ULTRA_VIRES_ADMIN.getValue())) {
            systemAdminResponse.setLeftTopLogo(systemConfigService.getValueByKey(SysConfigConstants.CONFIG_KEY_MERCHANT_LOGIN_LOGO_LEFT_TOP));
            systemAdminResponse.setLeftSquareLogo(systemConfigService.getValueByKey(SysConfigConstants.CONFIG_KEY_MERCHANT_SITE_LOGO_SQUARE));
            return systemAdminResponse;
        }
        //更新最后登录信息
        systemAdmin.setUpdateTime(DateUtil.date());
        systemAdmin.setLoginCount(systemAdmin.getLoginCount() + 1);
        systemAdmin.setLastIp(ip);
        systemAdminService.updateById(systemAdmin);
        // 返回后台LOGO图标
        if (adminType.equals(RoleEnum.PLATFORM_ADMIN.getValue())) {
            systemAdminResponse.setLeftTopLogo(systemConfigService.getValueByKey(SysConfigConstants.CONFIG_KEY_ADMIN_LOGIN_LOGO_LEFT_TOP));
            systemAdminResponse.setLeftSquareLogo(systemConfigService.getValueByKey(SysConfigConstants.CONFIG_KEY_ADMIN_SITE_LOGO_SQUARE));
        } else {
            systemAdminResponse.setLeftTopLogo(systemConfigService.getValueByKey(SysConfigConstants.CONFIG_KEY_MERCHANT_LOGIN_LOGO_LEFT_TOP));
            systemAdminResponse.setLeftSquareLogo(systemConfigService.getValueByKey(SysConfigConstants.CONFIG_KEY_MERCHANT_SITE_LOGO_SQUARE));
        }
        return systemAdminResponse;
    }

    /**
     * 平台端登录
     */
    @Override
    public SystemLoginResponse platformLogin(AjAdminLoginRequest request, String ip) {
        SystemLoginResponse loginResponse = login(request, RoleEnum.PLATFORM_ADMIN.getValue(), ip);
        asyncService.installStatistics();
        return loginResponse;
    }

    /**
     * 商户端登录
     */
    @Override
    public SystemLoginResponse merchantLogin(AjAdminLoginRequest request, String ip) {
        return login(request, RoleEnum.MERCHANT_ADMIN.getValue(), ip);
    }

    /**
     * 用户登出
     */
    @Override
    public Boolean logout() {
        LoginUserVo loginUserVo = SecurityUtil.getLoginUserVo();
        if (ObjectUtil.isNotNull(loginUserVo)) {
            // 删除用户缓存记录
            tokenComponent.delLoginUser(loginUserVo);
        }
        return true;
    }

    /**
     * 获取登录页图片
     *
     * @return AdminLoginPicResponse
     */
    @Override
    public AdminLoginPicResponse getLoginPic() {
        AdminLoginPicResponse loginPicResponse = new AdminLoginPicResponse();
        loginPicResponse.setBackgroundImage(systemConfigService.getValueByKey(SysConfigConstants.CONFIG_KEY_ADMIN_LOGIN_BACKGROUND_IMAGE));
        loginPicResponse.setLoginLogo(systemConfigService.getValueByKey(SysConfigConstants.CONFIG_KEY_ADMIN_LOGIN_LOGO_LOGIN));
        loginPicResponse.setLeftLogo(systemConfigService.getValueByKey(SysConfigConstants.CONFIG_KEY_ADMIN_LOGIN_LEFT_LOGO));
        loginPicResponse.setSiteName(systemConfigService.getValueByKey(SysConfigConstants.CONFIG_KEY_SITE_NAME));
        return loginPicResponse;
    }

    /**
     * 获取商户端登录页图片
     *
     * @return AdminLoginPicResponse
     */
    @Override
    public AdminLoginPicResponse getMerchantLoginPic() {
        AdminLoginPicResponse loginPicResponse = new AdminLoginPicResponse();
        loginPicResponse.setBackgroundImage(systemConfigService.getValueByKey(SysConfigConstants.CONFIG_KEY_MERCHANT_LOGIN_BACKGROUND_IMAGE));
        loginPicResponse.setLoginLogo(systemConfigService.getValueByKey(SysConfigConstants.CONFIG_KEY_MERCHANT_LOGIN_LOGO_LOGIN));
        loginPicResponse.setLeftLogo(systemConfigService.getValueByKey(SysConfigConstants.CONFIG_KEY_MERCHANT_LOGIN_LEFT_LOGO));
        loginPicResponse.setSiteName(systemConfigService.getValueByKey(SysConfigConstants.CONFIG_KEY_SITE_NAME));
        return loginPicResponse;
    }

    /**
     * 获取管理员可访问目录
     *
     * @return List<MenusResponse>
     */
    @Override
    public List<MenusResponse> getMenus() {
        LoginUserVo loginUserVo = SecurityUtil.getLoginUserVo();
        List<String> roleList = Stream.of(loginUserVo.getUser().getRoles().split(",")).collect(Collectors.toList());
        List<SystemMenu> menuList;
        if (roleList.contains("1")) {// 超管
            menuList = systemMenuService.findAllCatalogue(RoleEnum.PLATFORM_ADMIN.getValue());
        } else if (roleList.contains("2")) {// 商户主
            menuList = systemMenuService.findAllCatalogue(RoleEnum.MERCHANT_ADMIN.getValue());
        } else {
            menuList = systemMenuService.getMenusByUserId(loginUserVo.getUser().getId());
        }
        // 组装前端对象
        List<MenusResponse> responseList = menuList.stream().map(e -> {
            MenusResponse response = new MenusResponse();
            BeanUtils.copyProperties(e, response);
            // 针对管理端UI修改Response数据
            response.setPath(e.getComponent());
            response.setTitle(e.getName());
            return response;
        }).collect(Collectors.toList());

        MenuTree menuTree = new MenuTree(responseList);
        return menuTree.buildTree();
    }

    /**
     * 根据Token获取对应用户信息
     */
    @Override
    public LoginAdminResponse getInfoByToken() {
        LoginUserVo loginUserVo = SecurityUtil.getLoginUserVo();
        SystemAdmin systemAdmin = loginUserVo.getUser();
        LoginAdminResponse loginAdminResponse = new LoginAdminResponse();
        BeanUtils.copyProperties(systemAdmin, loginAdminResponse);
        List<String> roleList = Stream.of(systemAdmin.getRoles().split(",")).collect(Collectors.toList());
        List<String> permList = CollUtil.newArrayList();
        if ((roleList.contains("1") &&
                (systemAdmin.getType().equals(RoleEnum.PLATFORM_ADMIN.getValue())) || systemAdmin.getType().equals(RoleEnum.SUPER_ADMIN.getValue()))) {
            permList.add("*:*:*");
        } else if ((roleList.contains("2") &&
                (systemAdmin.getType().equals(RoleEnum.MERCHANT_ADMIN.getValue())) || systemAdmin.getType().equals(RoleEnum.SUPER_MERCHANT.getValue()))) {
            permList.add("*:*:*");
        } else {
            permList = loginUserVo.getPermissions().stream().map(SystemPermissions::getPath).collect(Collectors.toList());
        }
        loginAdminResponse.setPermissionsList(permList);

        if (systemAdmin.getMerId() > 0) {
            Merchant merchant = merchantService.getById(systemAdmin.getMerId());
            loginAdminResponse.setMerStarLevel(merchant.getStarLevel());
            loginAdminResponse.setMerReceiptPrintingSwitch(merchant.getReceiptPrintingSwitch());
            loginAdminResponse.setElectrPrintingSwitch(merchant.getElectrPrintingSwitch());
        }
        return loginAdminResponse;
    }

    /**
     * 修改登录用户信息
     *
     * @param request 请求参数
     * @return Boolean
     */
    @Override
    public Boolean loginAdminUpdate(LoginAdminUpdateRequest request) {
        SystemAdmin admin = SecurityUtil.getLoginUserVo().getUser();
        SystemAdmin systemAdmin = new SystemAdmin();
        systemAdmin.setId(admin.getId());
        systemAdmin.setRealName(request.getRealName());
        return systemAdminService.updateById(systemAdmin);
    }

    /**
     * 修改登录用户密码
     */
    @Override
    public Boolean loginAdminUpdatePwd(LoginAdminUpdatePasswordRequest request) {
        SystemAdmin admin = SecurityUtil.getLoginUserVo().getUser();
        SystemAdmin systemAdmin = systemAdminService.getById(admin.getId());
        String encryptPassword = CrmebUtil.encryptPassword(request.getOldPassword(), systemAdmin.getAccount());
        if (!systemAdmin.getPwd().equals(encryptPassword)) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "原密码不正确");
        }
        SystemAdmin newAdmin = new SystemAdmin();
        newAdmin.setId(admin.getId());
        String pwd = CrmebUtil.encryptPassword(request.getPassword(), admin.getAccount());
        newAdmin.setPwd(pwd);
        return systemAdminService.updateById(newAdmin);
    }

    /**
     * 账号登录检测
     * @param account 账号
     * @param type 用户类型:3-平台，4-商户
     * @return 账号错误登录次数
     */
    @Override
    public Integer accountDetection(String account, Integer type) {
        SystemAdmin admin = systemAdminService.selectUserByUserNameAndType(account, type);
        if (ObjectUtil.isNull(admin)) {
            return 0;
        }
        String key = StrUtil.format(RedisConstants.ADMIN_ACCOUNT_LOGIN_ERROR_NUM_KEY, account);
        if (!redisUtil.exists(key)) {
            return 0;
        }
        Integer num = redisUtil.get(key);
        return num;
    }

    /**
     * 通过商户ID获取商户超管
     *
     * @param merId 商户ID
     */
    @Override
    public SystemLoginResponse ultraViresLoginMerchantByPlat(Integer merId) {
        if (merId < 1) {
            throw new CrmebException(MerchantResultCode.MERCHANT_NOT_EXIST);
        }
        merchantService.getByIdException(merId);
        // 用户验证
        Authentication authentication = null;
        // 该方法会去调用UserDetailsServiceImpl.loadUserByUsername
        try {
            String principal = merId.toString() + RoleEnum.ULTRA_VIRES_ADMIN.getValue();
            authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(principal, "0"));
        } catch (AuthenticationException e) {
            if (e instanceof BadCredentialsException) {
                throw new CrmebException("用户不存在或密码错误");
            }
            throw new CrmebException(e.getMessage());
        } catch (CrmebException e) {
            throw new CrmebException("越权登录失败:" + e.getMessage());
        }
        LoginUserVo loginUser = (LoginUserVo) authentication.getPrincipal();
        SystemAdmin systemAdmin = loginUser.getUser();
        String token = tokenComponent.createToken(loginUser);
        SystemLoginResponse response = adminToLoginResponse(token, systemAdmin, RoleEnum.ULTRA_VIRES_ADMIN.getValue(), "");
        String merSiteUrl = systemConfigService.getValueByKey(SysConfigConstants.CONFIG_KEY_MERCHANT_SITE_URL);
        if (StrUtil.isBlank(merSiteUrl)) {
            merSiteUrl = "";
        }
        response.setMerSiteUrl(merSiteUrl);
        return response;
    }

    private void accountErrorNumAdd(String account) {
        redisUtil.incr(StrUtil.format(RedisConstants.ADMIN_ACCOUNT_LOGIN_ERROR_NUM_KEY, account), 1);
    }

    private void accountErrorNumClear(String account) {
        String key = StrUtil.format(RedisConstants.ADMIN_ACCOUNT_LOGIN_ERROR_NUM_KEY, account);
        if (redisUtil.exists(key)) {
            redisUtil.delete(StrUtil.format(RedisConstants.ADMIN_ACCOUNT_LOGIN_ERROR_NUM_KEY, account));
        }
    }
}
