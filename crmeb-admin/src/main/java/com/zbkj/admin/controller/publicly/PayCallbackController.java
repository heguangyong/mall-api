package com.zbkj.admin.controller.publicly;

import com.alibaba.fastjson.JSON;
import com.zbkj.common.annotation.CustomResponseAnnotation;
import com.zbkj.common.result.CommonResult;
import com.zbkj.service.service.PayCallbackService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;


/**
 * 支付回调
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
@RequestMapping("api/publicly/payment/callback")
@Api(tags = "支付回调控制器")
@CustomResponseAnnotation
public class PayCallbackController {

    @Autowired
    private PayCallbackService callbackService;

    @ApiOperation(value = "微信支付回调")
    @RequestMapping(value = "/wechat", method = RequestMethod.POST)
    public String weChat(@RequestBody String  request) {
        System.out.println("微信支付回调 request ===> " + request);
        String response = callbackService.wechatPayCallback(request);
        System.out.println("微信支付回调 response ===> " + response);
        return response;
    }

    @ApiOperation(value = "支付宝支付回调 ")
    @RequestMapping(value = "/alipay", method = RequestMethod.POST)
    public String aliPay(HttpServletRequest request){
        //支付宝支付回调
        System.out.println("支付宝支付回调 request ===> " + JSON.toJSONString(request.getParameterMap()));
        return callbackService.aliPayCallback(request);
    }

    /**
     * 微信退款回调
     */
    @ApiOperation(value = "微信退款回调")
    @RequestMapping(value = "/wechat/refund", method = RequestMethod.POST)
    public String weChatRefund(@RequestBody String request) {
        System.out.println("微信退款回调 request ===> " + request);
        String response = callbackService.weChatRefund(request);
        System.out.println("微信退款回调 response ===> " + response);
        return response;
    }
}



