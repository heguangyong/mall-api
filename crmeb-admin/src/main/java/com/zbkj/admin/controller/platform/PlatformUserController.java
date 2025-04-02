package com.zbkj.admin.controller.platform;


import com.zbkj.common.model.sgin.UserSignRecord;
import com.zbkj.common.model.user.UserBalanceRecord;
import com.zbkj.common.model.user.UserBrokerageRecord;
import com.zbkj.common.model.user.UserExperienceRecord;
import com.zbkj.common.model.user.UserIntegralRecord;
import com.zbkj.common.page.CommonPage;
import com.zbkj.common.request.*;
import com.zbkj.common.response.UserAdminDetailResponse;
import com.zbkj.common.response.UserResponse;
import com.zbkj.common.result.CommonResult;
import com.zbkj.service.service.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 平台端用户控制器
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
@RequestMapping("api/admin/platform/user")
@Api(tags = "平台端用户控制器")
@Validated
public class PlatformUserController {
    @Autowired
    private UserService userService;
    @Autowired
    private UserBalanceRecordService userBalanceRecordService;
    @Autowired
    private UserBrokerageRecordService userBrokerageRecordService;
    @Autowired
    private UserIntegralRecordService userIntegralRecordService;
    @Autowired
    private UserExperienceRecordService userExperienceRecordService;
    @Autowired
    private UserSignRecordService userSignRecordService;

    @PreAuthorize("hasAuthority('platform:user:page:list')")
    @ApiOperation(value = "平台端用户分页列表")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public CommonResult<CommonPage<UserResponse>> getList(@ModelAttribute @Validated UserSearchRequest request) {
        CommonPage<UserResponse> userCommonPage = CommonPage.restPage(userService.getPlatformPage(request));
        return CommonResult.success(userCommonPage);
    }

    @PreAuthorize("hasAuthority('platform:user:update')")
    @ApiOperation(value = "修改用户信息")
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public CommonResult<String> update(@RequestBody @Validated UserUpdateRequest userRequest) {
        if (userService.updateUser(userRequest)) {
            return CommonResult.success();
        }
        return CommonResult.failed();
    }

    @PreAuthorize("hasAuthority('platform:user:detail')")
    @ApiOperation(value = "用户详情")
    @RequestMapping(value = "/detail/{id}", method = RequestMethod.GET)
    public CommonResult<UserAdminDetailResponse> detail(@PathVariable(value = "id") Integer id) {
        return CommonResult.success(userService.getAdminDetail(id));
    }

    @PreAuthorize("hasAuthority('platform:user:tag')")
    @ApiOperation(value = "用户分配标签")
    @RequestMapping(value = "/tag", method = RequestMethod.POST)
    public CommonResult<String> tag(@RequestBody @Validated UserAssignTagRequest request) {
        if (userService.tag(request)) {
            return CommonResult.success();
        }
        return CommonResult.failed();
    }

    @PreAuthorize("hasAuthority('platform:user:operate:integer')")
    @ApiOperation(value = "操作用户积分")
    @RequestMapping(value = "/operate/integer", method = RequestMethod.GET)
    public CommonResult<Object> founds(@Validated UserOperateIntegralRequest request) {
        if (userService.operateUserInteger(request)) {
            return CommonResult.success();
        }
        return CommonResult.failed();
    }

    @PreAuthorize("hasAuthority('platform:user:operate:balance')")
    @ApiOperation(value = "操作用户余额")
    @RequestMapping(value = "/operate/balance", method = RequestMethod.GET)
    public CommonResult<Object> founds(@Validated UserOperateBalanceRequest request) {
        if (userService.operateUserBalance(request)) {
            return CommonResult.success();
        }
        return CommonResult.failed();
    }

    @PreAuthorize("hasAuthority('platform:user:gift:paid:member')")
    @ApiOperation(value = "赠送用户付费会员")
    @RequestMapping(value = "/gift/paid/member", method = RequestMethod.POST)
    public CommonResult<String> giftPaidMember(@RequestBody @Validated GiftPaidMemberRequest request) {
        if (userService.giftPaidMember(request)) {
            return CommonResult.success();
        }
        return CommonResult.failed();
    }

    @PreAuthorize("hasAuthority('platform:user:balance:record')")
    @ApiOperation(value = "用户余额记录")
    @RequestMapping(value = "/balance/record", method = RequestMethod.GET)
    public CommonResult<CommonPage<UserBalanceRecord>> getUserBalanceRecord(@Validated UserRecordSearchRequest request) {
        return CommonResult.success(CommonPage.restPage(userBalanceRecordService.getAdminUserBalanceRecord(request)));
    }

    @PreAuthorize("hasAuthority('platform:user:integral:record')")
    @ApiOperation(value = "用户积分记录")
    @RequestMapping(value = "/integral/record", method = RequestMethod.GET)
    public CommonResult<CommonPage<UserIntegralRecord>> getUserIntegralRecord(@Validated UserRecordSearchRequest request) {
        return CommonResult.success(CommonPage.restPage(userIntegralRecordService.getAdminUserIntegralRecord(request)));
    }

    @PreAuthorize("hasAuthority('platform:user:brokerage:record')")
    @ApiOperation(value = "用户佣金记录")
    @RequestMapping(value = "/brokerage/record", method = RequestMethod.GET)
    public CommonResult<CommonPage<UserBrokerageRecord>> getUserBrokerageRecord(@Validated UserRecordSearchRequest request) {
        return CommonResult.success(CommonPage.restPage(userBrokerageRecordService.getAdminUserBrokerageRecord(request)));
    }

    @PreAuthorize("hasAuthority('platform:user:experience:record')")
    @ApiOperation(value = "用户经验记录")
    @RequestMapping(value = "/experience/record", method = RequestMethod.GET)
    public CommonResult<CommonPage<UserExperienceRecord>> getUserExperienceRecord(@Validated UserRecordSearchRequest request) {
        return CommonResult.success(CommonPage.restPage(userExperienceRecordService.getAdminUserBrokerageRecord(request)));
    }

    @PreAuthorize("hasAuthority('platform:user:sign:record')")
    @ApiOperation(value = "用户签到记录")
    @RequestMapping(value = "/sign/record", method = RequestMethod.GET)
    public CommonResult<CommonPage<UserSignRecord>> getUserSignRecord(@Validated UserRecordSearchRequest request) {
        return CommonResult.success(CommonPage.restPage(userSignRecordService.getAdminUserSignRecord(request)));
    }
}



