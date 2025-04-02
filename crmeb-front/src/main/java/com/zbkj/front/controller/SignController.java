package com.zbkj.front.controller;

import com.zbkj.common.model.sgin.UserSignRecord;
import com.zbkj.common.page.CommonPage;
import com.zbkj.common.request.PageParamRequest;
import com.zbkj.common.response.SignPageInfoResponse;
import com.zbkj.common.result.CommonResult;
import com.zbkj.service.service.SignService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


/**
 * 签到控制器
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
@RequestMapping("api/front/sign")
@Api(tags = "签到控制器")
public class SignController {

    @Autowired
    private SignService signService;

    @ApiOperation(value = "签到记录列表")
    @RequestMapping(value = "/record/list", method = RequestMethod.GET)
    public CommonResult<CommonPage<UserSignRecord>> getList(@Validated PageParamRequest pageParamRequest) {
        return CommonResult.success(CommonPage.restPage(signService.findFrontSignRecordList(pageParamRequest)));
    }

    @ApiOperation(value = "签到页信息")
    @RequestMapping(value = "/page/info", method = RequestMethod.GET)
    public CommonResult<SignPageInfoResponse> getPageInfo(@RequestParam(value = "month", defaultValue = "", required = false) String month) {
        return CommonResult.success(signService.getPageInfo(month));
    }

}



