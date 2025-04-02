package com.zbkj.front.controller;


import com.zbkj.common.request.PasswordRequest;
import com.zbkj.common.request.UserBindingPhoneUpdateRequest;
import com.zbkj.common.request.UserEditInfoRequest;
import com.zbkj.common.response.UserInfoResponse;
import com.zbkj.common.response.UserLogoffBeforeResponse;
import com.zbkj.common.result.CommonResult;
import com.zbkj.service.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * 用户 -- 用户中心
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
@RequestMapping("api/front/user")
@Api(tags = "用户控制器")
public class UserController {

    @Autowired
    private UserService userService;

    @ApiOperation(value = "手机号修改密码获取验证码")
    @RequestMapping(value = "/update/password/phone/code", method = RequestMethod.POST)
    public CommonResult<String> getUpdatePasswordPhoneCode() {
        if (userService.getUpdatePasswordPhoneCode()) {
            return CommonResult.success();
        }
        return CommonResult.failed();
    }

    @ApiOperation(value = "手机号修改密码")
    @RequestMapping(value = "/register/reset", method = RequestMethod.POST)
    public CommonResult<Boolean> password(@RequestBody @Validated PasswordRequest request) {
        return CommonResult.success(userService.password(request));
    }

    @ApiOperation(value = "修改个人信息")
    @RequestMapping(value = "/user/edit", method = RequestMethod.POST)
    public CommonResult<String> personInfo(@RequestBody @Validated UserEditInfoRequest request) {
        if (userService.editUser(request)) {
            return CommonResult.success();
        }
        return CommonResult.failed();
    }

    @ApiOperation(value = "用户信息")
    @RequestMapping(value = "/info", method = RequestMethod.GET)
    public CommonResult<UserInfoResponse> getUserCenter() {
        return CommonResult.success(userService.getUserInfo());
    }

    @ApiOperation(value = "换绑手机号获取用户手机号验证码")
    @RequestMapping(value = "/phone/code", method = RequestMethod.POST)
    public CommonResult<String> getCurrentPhoneCode() {
        if (userService.getCurrentPhoneCode()) {
            return CommonResult.success();
        }
        return CommonResult.failed();
    }

    @ApiOperation(value = "换绑手机号获取验证码")
    @RequestMapping(value = "/update/binding/phone/code", method = RequestMethod.POST)
    public CommonResult<String> updatePhoneCode(@RequestBody @Validated UserBindingPhoneUpdateRequest request) {
        if (userService.updatePhoneCode(request)) {
            return CommonResult.success();
        }
        return CommonResult.failed();
    }

    @ApiOperation(value = "换绑手机号")
    @RequestMapping(value = "/update/binding", method = RequestMethod.POST)
    public CommonResult<String> updatePhone(@RequestBody @Validated UserBindingPhoneUpdateRequest request) {
        if (userService.updatePhone(request)) {
            return CommonResult.success();
        }
        return CommonResult.failed();
    }

    @ApiOperation(value = "用户注销数据前置")
    @RequestMapping(value = "/logoff/before", method = RequestMethod.GET)
    public CommonResult<UserLogoffBeforeResponse> logoffBefore() {
        return CommonResult.success(userService.logoffBefore());
    }

    @ApiOperation(value = "用户注销")
    @RequestMapping(value = "/logoff", method = RequestMethod.POST)
    public CommonResult<String> logoff() {
        if (userService.logoff()) {
            return CommonResult.success();
        }
        return CommonResult.failed();
    }
}



