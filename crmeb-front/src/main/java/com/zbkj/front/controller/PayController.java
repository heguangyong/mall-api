package com.zbkj.front.controller;

import com.zbkj.common.request.OrderPayRequest;
import com.zbkj.common.request.WechatPaymentQueryRequest;
import com.zbkj.common.response.CashierInfoResponse;
import com.zbkj.common.response.OrderPayResultResponse;
import com.zbkj.common.response.PayConfigResponse;
import com.zbkj.common.result.CommonResult;
import com.zbkj.service.service.PayService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 支付控制器
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
@RequestMapping("api/front/pay")
@Api(tags = "支付管理")
public class PayController {

    @Autowired
    private PayService payService;

    @ApiOperation(value = "获取支付配置")
    @RequestMapping(value = "/get/config", method = RequestMethod.GET)
    public CommonResult<PayConfigResponse> getPayConfig() {
        return CommonResult.success(payService.getPayConfig());
    }

    @ApiOperation(value = "订单支付")
    @RequestMapping(value = "/payment", method = RequestMethod.POST)
    public CommonResult<OrderPayResultResponse> payment(@RequestBody @Validated OrderPayRequest orderPayRequest) {
        return CommonResult.success(payService.payment(orderPayRequest));
    }

    @ApiOperation(value = "查询订单微信支付结果")
    @RequestMapping(value = "/query/wechat/pay/result/{orderNo}", method = RequestMethod.GET)
    public CommonResult<Boolean> queryPayResult(@PathVariable(value = "orderNo") String orderNo) {
        return CommonResult.success(payService.queryWechatPayResult(orderNo));
    }

    @ApiOperation(value = "查询订单支付宝支付结果")
    @RequestMapping(value = "/query/ali/pay/result/{orderNo}", method = RequestMethod.GET)
    public CommonResult<Boolean> queryAliPayResult(@PathVariable(value = "orderNo") String orderNo) {
        return CommonResult.success(payService.queryAliPayResult(orderNo));
    }

    @ApiOperation(value = "获取收银台信息")
    @RequestMapping(value = "/get/cashier/{orderNo}", method = RequestMethod.GET)
    public CommonResult<CashierInfoResponse> getCashierIno(@PathVariable(value = "orderNo") String orderNo) {
        return CommonResult.success(payService.getCashierIno(orderNo));
    }

    @ApiOperation(value = "查询微信支付结果")
    @RequestMapping(value = "/query/wechat/payment/result", method = RequestMethod.GET)
    public CommonResult<Boolean> queryWechatPaymentResult(@Validated WechatPaymentQueryRequest request) {
        return CommonResult.success(payService.queryWechatPaymentResult(request));
    }
}
