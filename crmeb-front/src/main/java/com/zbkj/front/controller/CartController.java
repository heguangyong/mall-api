package com.zbkj.front.controller;

import com.zbkj.common.request.*;
import com.zbkj.common.response.CartMerchantResponse;
import com.zbkj.common.response.CartPriceResponse;
import com.zbkj.common.result.CommonResult;
import com.zbkj.service.service.CartService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;


/**
 * 购物车控制器
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
@RequestMapping("api/front/cart")
@Api(tags = "购物车控制器") //配合swagger使用
public class CartController {

    @Autowired
    private CartService cartService;

    @ApiOperation(value = "购物车列表") //配合swagger使用
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "isValid", value = "类型，true-有效商品，false-无效商品", required = true)
    })
    public CommonResult<List<CartMerchantResponse>> getList(@RequestParam Boolean isValid) {
        return CommonResult.success(cartService.getList(isValid));
    }

    @ApiOperation(value = "添加购物车")
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public CommonResult<String> add(@RequestBody @Validated CartRequest request) {
        if (cartService.add(request)) {
            return CommonResult.success();
        }
        return CommonResult.failed();
    }

    @ApiOperation(value = "批量添加购物车")
    @RequestMapping(value = "/batch/add", method = RequestMethod.POST)
    public CommonResult<String> batchAdd(@RequestBody @Validated List<CartRequest> cartListRequest) {
        if (cartService.batchAdd(cartListRequest)) {
            return CommonResult.success();
        }
        return CommonResult.failed();
    }

    @ApiOperation(value = "删除购物车")
    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    public CommonResult<String> delete(@RequestBody @Validated CartDeleteRequest request) {
        if (cartService.deleteCartByIds(request.getIds())) {
            return CommonResult.success();
        }
        return CommonResult.failed();
    }

    @ApiOperation(value = "修改购物车商品数量")
    @RequestMapping(value = "/num", method = RequestMethod.POST)
    public CommonResult<String> update(@RequestBody @Validated CartUpdateNumRequest request) {
        if (cartService.updateCartNum(request.getId(), request.getNumber())) {
            return CommonResult.success();
        }
        return CommonResult.failed();
    }

    @ApiOperation(value = "获取购物车数量")
    @RequestMapping(value = "/count", method = RequestMethod.GET)
    public CommonResult<Map<String, Integer>> count(@Validated CartNumRequest request) {
        return CommonResult.success(cartService.getUserCount(request));
    }

    @ApiOperation(value = "购物车重选提交")
    @RequestMapping(value = "/resetcart", method = RequestMethod.POST)
    public CommonResult<Object> resetCart(@RequestBody @Validated CartResetRequest resetRequest) {
        if (cartService.resetCart(resetRequest)) {
            return CommonResult.success();
        }
        return CommonResult.failed();
    }

    @ApiOperation(value = "购物车移入收藏")
    @RequestMapping(value = "/to/collect", method = RequestMethod.POST)
    public CommonResult<String> toCollect(@RequestBody @Validated CartDeleteRequest request) {
        if (cartService.toCollect(request.getIds())) {
            return CommonResult.success();
        }
        return CommonResult.failed();
    }

    @ApiOperation(value = "购物车商品价格计算")
    @RequestMapping(value = "/calculate/price", method = RequestMethod.POST)
    public CommonResult<CartPriceResponse> calculatePrice(@RequestBody @Validated CartDeleteRequest request) {
        return CommonResult.success(cartService.calculatePrice(request.getIds()));
    }
}



