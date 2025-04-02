package com.zbkj.front.controller;


import com.zbkj.common.model.system.SystemUserLevel;
import com.zbkj.common.model.user.UserExperienceRecord;
import com.zbkj.common.model.user.UserIntegralRecord;
import com.zbkj.common.page.CommonPage;
import com.zbkj.common.request.PageParamRequest;
import com.zbkj.common.response.*;
import com.zbkj.common.result.CommonResult;
import com.zbkj.front.service.UserCenterService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
@RequestMapping("api/front/user/center")
@Api(tags = "用户中心控制器")
public class UserCenterController {

    @Autowired
    private UserCenterService userCenterService;

    @ApiOperation(value = "获取个人中心详情")
    @RequestMapping(value = "/info", method = RequestMethod.GET)
    public CommonResult<UserCenterResponse> getUserCenterInfo() {
        return CommonResult.success(userCenterService.getUserCenterInfo());
    }

    @ApiOperation(value = "我的推广")
    @RequestMapping(value = "/my/promotion", method = RequestMethod.GET)
    public CommonResult<UserMyPromotionResponse> getMyPromotion() {
        return CommonResult.success(userCenterService.getMyPromotion());
    }

    @ApiOperation(value = "会员等级列表")
    @RequestMapping(value = "/user/level/grade", method = RequestMethod.GET)
    public CommonResult<List<SystemUserLevel>> getUserLevelList() {
        return CommonResult.success(userCenterService.getUserLevelList());
    }

    @ApiOperation(value = "我的积分")
    @RequestMapping(value = "/my/integral", method = RequestMethod.GET)
    public CommonResult<UserMyIntegralResponse> getMyIntegral() {
        return CommonResult.success(userCenterService.getMyIntegral());
    }

    @ApiOperation(value = "积分记录")
    @RequestMapping(value = "/integral/list", method = RequestMethod.GET)
    public CommonResult<CommonPage<UserIntegralRecord>> getIntegralList(@Validated PageParamRequest pageParamRequest) {
        return CommonResult.success(CommonPage.restPage(userCenterService.getUserIntegralRecordList(pageParamRequest)));
    }

    /**
     * V1.2 废弃
     */
    @Deprecated
    @ApiOperation(value = "经验记录")
    @RequestMapping(value = "/experience/expList", method = RequestMethod.GET)
    public CommonResult<CommonPage<UserExperienceRecord>> getExperienceList(@Validated PageParamRequest pageParamRequest) {
        return CommonResult.success(CommonPage.restPage(userCenterService.getUserExperienceList(pageParamRequest)));
    }

    @ApiOperation(value = "我的账户")
    @RequestMapping(value = "/my/account", method = RequestMethod.GET)
    public CommonResult<UserMyAccountResponse> getMyAccount() {
        return CommonResult.success(userCenterService.getMyAccount());
    }

    @ApiOperation(value = "余额记录")
    @RequestMapping(value = "/balance/record", method = RequestMethod.GET)
    @ApiImplicitParam(name = "type", value = "记录类型：all-全部，expenditure-支出，income-收入，recharge-充值", required = true)
    public CommonResult<CommonPage<UserBalanceRecordMonthResponse>> userBalanceRecord(@RequestParam(name = "type") String type,
                                                                                      @ModelAttribute PageParamRequest pageRequest) {
        return CommonResult.success(CommonPage.restPage(userCenterService.getUserBalanceRecord(type, pageRequest)));
    }

    @ApiOperation(value = "订单头部数量")
    @RequestMapping(value = "/order/num", method = RequestMethod.GET)
    public CommonResult<OrderCenterNumResponse> getUserCenterOrderNum() {
        return CommonResult.success(userCenterService.getUserCenterOrderNum());
    }

    @ApiOperation(value = "足迹记录")
    @RequestMapping(value = "/browse/record", method = RequestMethod.GET)
    public CommonResult<List<UserBrowseRecordDateResponse>> userBrowseRecord() {
        return CommonResult.success(userCenterService.getUserBrowseRecord());
    }

    @ApiOperation(value = "我的经验页")
    @RequestMapping(value = "/my/exp", method = RequestMethod.GET)
    public CommonResult<UserMyExpResponse> getMyExp() {
        return CommonResult.success(userCenterService.getMyExp());
    }

    @ApiOperation(value = "我的经验记录列表")
    @RequestMapping(value = "/my/exp/record/list", method = RequestMethod.GET)
    public CommonPage<UserExperienceRecordMonthResponse> findMyExpRecordList(PageParamRequest pageRequest) {
        return CommonPage.restPage(userCenterService.findMyExpRecordList(pageRequest));
    }

    @ApiOperation(value = "签到经验记录")
    @RequestMapping(value = "/sign/experience/expList", method = RequestMethod.GET)
    public CommonResult<CommonPage<UserExperienceRecord>> getSignExperienceList(@Validated PageParamRequest pageParamRequest) {
        return CommonResult.success(CommonPage.restPage(userCenterService.getUserSignExperienceList(pageParamRequest)));
    }
}



