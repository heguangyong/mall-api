package com.zbkj.front.controller;

import com.zbkj.common.page.CommonPage;
import com.zbkj.common.request.IntegralIntervalProductRequest;
import com.zbkj.common.request.PageParamRequest;
import com.zbkj.common.response.IntegralIntervalResponse;
import com.zbkj.common.response.IntegralProductFrontResponse;
import com.zbkj.common.result.CommonResult;
import com.zbkj.service.service.IntegralShoppingService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * 类的详细说明
 *
 * @author Han
 * @version 1.0.0
 * @Date 2024/8/20
 */
@RestController
@RequestMapping("api/front/integral/shopping")
@Api(tags = "积分商城控制器")
public class IntegralShoppingController {

    @Autowired
    private IntegralShoppingService integralShoppingService;

    @ApiOperation(value = "获取用户积分数据")
    @RequestMapping(value = "/get/user/integral", method = RequestMethod.GET)
    public CommonResult<Map<String, Object>> getUserIntegralInfo() {
        return CommonResult.success(integralShoppingService.getUserIntegralInfo());
    }

    @ApiOperation(value = "积分商品热门推荐分页列表")
    @RequestMapping(value = "/product/hot/page", method = RequestMethod.GET)
    public CommonResult<CommonPage<IntegralProductFrontResponse>> findProductHotPage(@Validated PageParamRequest request) {
        return CommonResult.success(CommonPage.restPage(integralShoppingService.findProductHotPage(request)));
    }

    @ApiOperation(value = "获取积分区间列表")
    @RequestMapping(value = "/get/integral/interval", method = RequestMethod.GET)
    public CommonResult<List<IntegralIntervalResponse>> getIntegralIntervalList() {
        return CommonResult.success(integralShoppingService.getIntegralIntervalList());
    }

    @ApiOperation(value = "积分商品分页列表(积分区间)")
    @RequestMapping(value = "/interval/product/page", method = RequestMethod.GET)
    public CommonResult<CommonPage<IntegralProductFrontResponse>> findIntegralIntervalProductPage(@Validated IntegralIntervalProductRequest request) {
        return CommonResult.success(CommonPage.restPage(integralShoppingService.findIntegralIntervalProductPage(request)));
    }
}
