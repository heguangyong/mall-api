package com.zbkj.front.controller;

import com.zbkj.common.request.UserRechargeRequest;
import com.zbkj.common.response.OrderPayResultResponse;
import com.zbkj.common.response.RechargePackageResponse;
import com.zbkj.common.result.CommonResult;
import com.zbkj.service.service.RechargeOrderService;
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
 * 充值控制器
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
@RequestMapping("api/front/recharge")
@Api(tags = "充值控制器")
public class RechargeController {

    @Autowired
    private RechargeOrderService rechargeOrderService;

    @ApiOperation(value = "获取用户充值套餐")
    @RequestMapping(value = "/get/user/package", method = RequestMethod.GET)
    public CommonResult<RechargePackageResponse> getRechargePackage() {
        return CommonResult.success(rechargeOrderService.getRechargePackage());
    }

    @ApiOperation(value = "生成用户充值订单")
    @RequestMapping(value = "/user/create", method = RequestMethod.POST)
    public CommonResult<OrderPayResultResponse> userRechargeOrderCreate(@RequestBody @Validated UserRechargeRequest request) {
        return CommonResult.success(rechargeOrderService.userRechargeOrderCreate(request));
    }

}



