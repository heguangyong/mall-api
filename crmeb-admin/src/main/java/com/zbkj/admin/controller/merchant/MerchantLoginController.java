package com.zbkj.admin.controller.merchant;

import com.zbkj.admin.service.AdminLoginService;
import com.zbkj.common.enums.RoleEnum;
import com.zbkj.common.request.AdminAccountDetectionRequest;
import com.zbkj.common.request.AjAdminLoginRequest;
import com.zbkj.common.request.LoginAdminUpdatePasswordRequest;
import com.zbkj.common.request.LoginAdminUpdateRequest;
import com.zbkj.common.response.AdminLoginPicResponse;
import com.zbkj.common.response.LoginAdminResponse;
import com.zbkj.common.response.MenusResponse;
import com.zbkj.common.response.SystemLoginResponse;
import com.zbkj.common.result.CommonResult;
import com.zbkj.common.utils.CrmebUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 商户端登录控制器
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
@Slf4j
@RestController
@RequestMapping("api/admin/merchant")
@Api(tags = "商户端登录控制器")
public class MerchantLoginController {

    @Autowired
    private AdminLoginService loginService;

    @ApiOperation(value="账号登录检测")
    @RequestMapping(value = "/login/account/detection", method = RequestMethod.POST)
    public CommonResult<Integer> accountDetection(@RequestBody @Validated AdminAccountDetectionRequest request) {
        return CommonResult.success(loginService.accountDetection(request.getAccount(), RoleEnum.MERCHANT_ADMIN.getValue()));
    }

    @ApiOperation(value="登录")
    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public CommonResult<SystemLoginResponse> SystemAdminLogin(@RequestBody @Validated AjAdminLoginRequest systemAdminLoginRequest, HttpServletRequest request) {
        String ip = CrmebUtil.getClientIp(request);
        SystemLoginResponse systemAdminResponse = loginService.merchantLogin(systemAdminLoginRequest, ip);
        return CommonResult.success(systemAdminResponse);
    }

    @PreAuthorize("hasAuthority('merchant:logout')")
    @ApiOperation(value="登出")
    @RequestMapping(value = "/logout", method = RequestMethod.GET)
    public CommonResult<String> SystemAdminLogout() {
        loginService.logout();
        return CommonResult.success("logout success");
    }

    @PreAuthorize("hasAuthority('merchant:login:user:info')")
    @ApiOperation(value="获取登录用户详情")
    @RequestMapping(value = "/getAdminInfoByToken", method = RequestMethod.GET)
    public CommonResult<LoginAdminResponse> getAdminInfo() {
        return CommonResult.success(loginService.getInfoByToken());
    }

    @ApiOperation(value = "获取登录页图片")
    @RequestMapping(value = "/getLoginPic", method = RequestMethod.GET)
    public CommonResult<AdminLoginPicResponse> getLoginPic() {
        return CommonResult.success(loginService.getMerchantLoginPic());
    }

    @PreAuthorize("hasAuthority('merchant:login:menus')")
    @ApiOperation(value = "获取管理员可访问目录")
    @RequestMapping(value = "/getMenus", method = RequestMethod.GET)
    public CommonResult<List<MenusResponse>> getMenus() {
        return CommonResult.success(loginService.getMenus());
    }

    @PreAuthorize("hasAuthority('merchant:login:admin:update')")
    @ApiOperation(value="修改登录用户信息")
    @RequestMapping(value = "/login/admin/update", method = RequestMethod.POST)
    public CommonResult<SystemLoginResponse> loginAdminUpdate(@RequestBody @Validated LoginAdminUpdateRequest request) {
        if (loginService.loginAdminUpdate(request)) {
            return CommonResult.success();
        }
        return CommonResult.failed();
    }

    @PreAuthorize("hasAuthority('merchant:login:admin:update:password')")
    @ApiOperation(value="修改登录用户密码")
    @RequestMapping(value = "/login/admin/update/password", method = RequestMethod.POST)
    public CommonResult<SystemLoginResponse> loginAdminUpdatePwd(@RequestBody @Validated LoginAdminUpdatePasswordRequest request) {
        if (loginService.loginAdminUpdatePwd(request)) {
            return CommonResult.success();
        }
        return CommonResult.failed();
    }
}
