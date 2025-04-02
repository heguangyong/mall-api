package com.zbkj.admin.controller.merchant;

import com.zbkj.admin.service.FinanceService;
import com.zbkj.common.page.CommonPage;
import com.zbkj.common.request.FundsFlowRequest;
import com.zbkj.common.request.merchant.MerchantClosingApplyRequest;
import com.zbkj.common.request.merchant.MerchantClosingSearchRequest;
import com.zbkj.common.request.PageParamRequest;
import com.zbkj.common.response.*;
import com.zbkj.common.result.CommonResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


/**
 * 商户端财务控制器
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
@RequestMapping("api/admin/merchant/finance")
@Api(tags = "商户端财务控制器")
public class MerchantFinanceController {

    @Autowired
    private FinanceService financeService;

    @PreAuthorize("hasAuthority('merchant:finance:funds:flow')")
    @ApiOperation(value = "资金流水分页列表")
    @RequestMapping(value = "/funds/flow", method = RequestMethod.GET)
    public CommonResult<CommonPage<FundsFlowResponse>> getFundsFlow(@Validated FundsFlowRequest request,
                                                                      @Validated PageParamRequest pageParamRequest) {
        return CommonResult.success(CommonPage.restPage(financeService.getMerchantFundsFlow(request, pageParamRequest)));
    }

    @PreAuthorize("hasAuthority('merchant:finance:closing:base:info')")
    @ApiOperation(value = "获取结算申请基础信息")
    @RequestMapping(value = "/closing/base/info", method = RequestMethod.GET)
    public CommonResult<MerchantClosingBaseInfoResponse> getClosingBaseInfo() {
        return CommonResult.success(financeService.getClosingBaseInfo());
    }

    @PreAuthorize("hasAuthority('merchant:finance:closing:apply')")
    @ApiOperation(value = "结算申请")
    @RequestMapping(value = "/closing/apply", method = RequestMethod.POST)
    public CommonResult<String> closingApply(@RequestBody @Validated MerchantClosingApplyRequest request) {
        if (financeService.merchantClosingApply(request)) {
            return CommonResult.success();
        }
        return CommonResult.failed();
    }

    @PreAuthorize("hasAuthority('merchant:finance:closing:page:list')")
    @ApiOperation(value = "结算记录分页列表")
    @RequestMapping(value = "/closing/record/list", method = RequestMethod.GET)
    public CommonResult<CommonPage<MerchantClosingPageResponse>> getMerchantClosingPageList(@Validated MerchantClosingSearchRequest request,
                                                                                            @Validated PageParamRequest pageParamRequest) {
        return CommonResult.success(CommonPage.restPage(financeService.getMerchantClosingRecordPageList(request, pageParamRequest)));
    }

    @PreAuthorize("hasAuthority('merchant:finance:closing:detail')")
    @ApiOperation(value = "结算记录详情")
    @RequestMapping(value = "/closing/record/detail/{closingNo}", method = RequestMethod.GET)
    public CommonResult<MerchantClosingDetailResponse> getMerchantClosingDetail(@PathVariable(value = "closingNo") String closingNo) {
        return CommonResult.success(financeService.getMerchantClosingDetailByMerchant(closingNo));
    }

    @PreAuthorize("hasAuthority('merchant:finance:daily:statement:page:list')")
    @ApiOperation(value = "日帐单管理分页列表")
    @RequestMapping(value = "/daily/statement/list", method = RequestMethod.GET)
    public CommonResult<CommonPage<MerchantStatementResponse>> getDailyStatementList(@RequestParam(value = "dateLimit", required = false, defaultValue = "") String dateLimit,
                                                                                     @ModelAttribute PageParamRequest pageParamRequest) {
        return CommonResult.success(CommonPage.restPage(financeService.getMerchantDailyStatementList(dateLimit, pageParamRequest)));
    }

    @PreAuthorize("hasAuthority('merchant:finance:month:statement:page:list')")
    @ApiOperation(value = "月帐单管理分页列表")
    @RequestMapping(value = "/month/statement/list", method = RequestMethod.GET)
    public CommonResult<CommonPage<MerchantStatementResponse>> getMonthStatementList(@RequestParam(value = "dateLimit", required = false, defaultValue = "") String dateLimit,
                                                                                     @ModelAttribute PageParamRequest pageParamRequest) {
        return CommonResult.success(CommonPage.restPage(financeService.getMerchantMonthStatementList(dateLimit, pageParamRequest)));
    }
}



