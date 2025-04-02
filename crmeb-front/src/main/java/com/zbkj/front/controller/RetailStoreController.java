package com.zbkj.front.controller;

import com.zbkj.common.model.user.User;
import com.zbkj.common.page.CommonPage;
import com.zbkj.common.request.BrokerageToYueRequest;
import com.zbkj.common.request.PageParamRequest;
import com.zbkj.common.request.UserClosingApplyRequest;
import com.zbkj.common.request.UserSpreadPeopleRequest;
import com.zbkj.common.response.*;
import com.zbkj.common.result.CommonResult;
import com.zbkj.service.service.RetailStoreService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

/**
 * 分销推广控制器
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
@RequestMapping("api/front/retail/store")
@Api(tags = "分销推广控制器")
public class RetailStoreController {

    @Autowired
    private RetailStoreService retailStoreService;

    @ApiOperation(value = "获取推广订单总数量")
    @RequestMapping(value = "/spread/order/count", method = RequestMethod.GET)
    public CommonResult<Integer> getSpreadOrderCount() {
        return CommonResult.success(retailStoreService.getSpreadOrderCount());
    }

    @ApiOperation(value = "推广订单列表")
    @RequestMapping(value = "/spread/order/list", method = RequestMethod.GET)
    public CommonResult<List<UserSpreadOrderMonthResponse>> getSpreadOrder(@Validated PageParamRequest pageParamRequest) {
        return CommonResult.success(retailStoreService.getSpreadOrder(pageParamRequest));
    }

    @ApiOperation(value = "推广人团队数量")
    @RequestMapping(value = "/spread/people/team/num", method = RequestMethod.GET)
    public CommonResult<UserSpreadPeopleTeamNumResponse> getSpreadPeopleTeamNum() {
        return CommonResult.success(retailStoreService.getSpreadPeopleTeamNum());
    }

    @ApiOperation(value = "推广人列表")
    @RequestMapping(value = "/spread/people/list", method = RequestMethod.GET)
    public CommonResult<List<UserSpreadPeopleItemResponse>> getSpreadPeopleList(@Validated UserSpreadPeopleRequest request, @Validated PageParamRequest pageParamRequest) {
        List<UserSpreadPeopleItemResponse> spreadPeopleList = retailStoreService.getSpreadPeopleList(request, pageParamRequest);
        return CommonResult.success(spreadPeopleList);
    }

    @ApiOperation(value = "推广人排行")
    @RequestMapping(value = "/spread/people/rank", method = RequestMethod.GET)
    @ApiImplicitParam(name="type", value="时间范围(week-周，month-月)", required = true)
    public CommonResult<List<User>> getTopSpreadPeopleListByDate(@RequestParam(value = "type") String type) {
        return CommonResult.success(retailStoreService.getSpreadPeopleTopByDate(type));
    }

    @ApiOperation(value = "佣金排行")
    @RequestMapping(value = "/brokerage/rank", method = RequestMethod.GET)
    @ApiImplicitParam(name="type", value="时间范围(week-周，month-月)", required = true)
    public CommonResult<List<User>> getBrokerageTopByDate(@RequestParam(value = "type") String type) {
        return CommonResult.success(retailStoreService.getBrokerageTopByDate(type));
    }

    @ApiOperation(value = "佣金记录")
    @RequestMapping(value = "/brokerage/record", method = RequestMethod.GET)
    public CommonResult<CommonPage<BrokerageRecordDetailResponse>> getBrokerageRecord(@Validated PageParamRequest pageParamRequest) {
        return CommonResult.success(CommonPage.restPage(retailStoreService.getBrokerageRecord(pageParamRequest)));
    }

    @ApiOperation(value = "推广海报轮播图")
    @RequestMapping(value = "/user/poster/banner", method = RequestMethod.GET)
    public CommonResult<List<RetailStorePosterBannerResponse>> getPosterBanner() {
        return CommonResult.success(retailStoreService.getPosterBanner());
    }

    @ApiOperation(value = "绑定推广关系（登录状态）")
    @RequestMapping(value = "/binding/user/{spreadPid}", method = RequestMethod.POST)
    public CommonResult<Boolean> bindingUser(@PathVariable(value = "spreadPid") Integer spreadPid) {
        retailStoreService.bindingUser(spreadPid);
        return CommonResult.success();
    }

    @ApiOperation(value = "获取用户结算配置")
    @RequestMapping(value = "/user/closing/config", method = RequestMethod.GET)
    public CommonResult<UserClosingConfigResponse> getClosingConfig() {
        return CommonResult.success(retailStoreService.getUserClosingConfig());
    }

    @ApiOperation(value = "用户结算申请")
    @RequestMapping(value = "/user/closing/apply", method = RequestMethod.POST)
    public CommonResult<String> closingApply(@RequestBody @Validated UserClosingApplyRequest request) {
        if (retailStoreService.userClosingApply(request)) {
            return CommonResult.success();
        }
        return CommonResult.failed();
    }

    @ApiOperation(value = "用户结算记录")
    @RequestMapping(value = "/user/closing/record", method = RequestMethod.GET)
    public CommonResult<List<UserClosingRecordResponse>> getUserClosingRecord(@Validated PageParamRequest pageParamRequest) {
        return CommonResult.success(retailStoreService.getUserClosingRecord(pageParamRequest));
    }

    @ApiOperation(value = "佣金转入余额")
    @RequestMapping(value = "/brokerage/to/yue", method = RequestMethod.POST)
    public CommonResult<String> brokerageToYue(@RequestBody @Validated BrokerageToYueRequest request) {
        if (retailStoreService.brokerageToYue(request)) {
            return CommonResult.success();
        }
        return CommonResult.failed();
    }
}
