package com.zbkj.front.controller;

import com.zbkj.common.request.PaySvipOrderRequest;
import com.zbkj.common.response.OrderPayResultResponse;
import com.zbkj.common.response.SvipBenefitsExplainResponse;
import com.zbkj.common.response.SvipInfoResponse;
import com.zbkj.common.response.SvipOrderRecordResponse;
import com.zbkj.common.result.CommonResult;
import com.zbkj.front.service.MemberService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 会员控制器
 *
 * @author Han
 * @version 1.0.0
 * @Date 2024/5/14
 */
@Slf4j
@RestController
@RequestMapping("api/front/member")
@Api(tags = "会员控制器")
public class MemberController {

    @Autowired
    private MemberService memberService;

    @ApiOperation(value = "获取svip会员中心信息")
    @RequestMapping(value = "/get/svip/info", method = RequestMethod.GET)
    public CommonResult<SvipInfoResponse> getSvipInfo() {
        return CommonResult.success(memberService.getSvipInfo());
    }

    @ApiOperation(value = "获取svip会员权益列表")
    @RequestMapping(value = "/get/svip/benefits/list", method = RequestMethod.GET)
    public CommonResult<List<SvipBenefitsExplainResponse>> getSvipBenefitsList() {
        return CommonResult.success(memberService.getSvipBenefitsList());
    }

    @ApiOperation(value = "生成购买svip会员订单")
    @RequestMapping(value = "/svip/order/create", method = RequestMethod.POST)
    public CommonResult<OrderPayResultResponse> paySvipOrderCreate(@RequestBody @Validated PaySvipOrderRequest request) {
        return CommonResult.success(memberService.paySvipOrderCreate(request));
    }

    @ApiOperation(value = "svip会员订单购买记录")
    @RequestMapping(value = "/svip/order/record", method = RequestMethod.POST)
    public CommonResult<List<SvipOrderRecordResponse>> getSvipOrderRecord() {
        return CommonResult.success(memberService.getSvipOrderRecord());
    }

}
