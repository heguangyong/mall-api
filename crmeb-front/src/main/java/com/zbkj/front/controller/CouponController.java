package com.zbkj.front.controller;

import com.zbkj.common.page.CommonPage;
import com.zbkj.common.request.*;
import com.zbkj.common.response.CouponCenterPageResponse;
import com.zbkj.common.response.CouponFrontResponse;
import com.zbkj.common.response.CouponUserOrderResponse;
import com.zbkj.common.response.UserCouponResponse;
import com.zbkj.common.result.CommonResult;
import com.zbkj.service.service.CouponService;
import com.zbkj.service.service.CouponUserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;


/**
 * 优惠券表 前端控制器
 *  +----------------------------------------------------------------------
 *  | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
 *  +----------------------------------------------------------------------
 *  | Copyright (c) 2016~2025 https://www.crmeb.com All rights reserved.
 *  +----------------------------------------------------------------------
 *  | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
 *  +----------------------------------------------------------------------
 *  | Author: CRMEB Team <admin@crmeb.com>
 *  +----------------------------------------------------------------------
 */
@Slf4j
@RestController
@RequestMapping("api/front/coupon")
@Api(tags = "优惠券控制器")
public class CouponController {

    @Autowired
    private CouponService couponService;
    @Autowired
    private CouponUserService couponUserService;


    @ApiOperation(value = "优惠券分页列表")
    @RequestMapping(value = "/page/list", method = RequestMethod.GET)
    public CommonResult<CommonPage<CouponFrontResponse>> getList(@ModelAttribute @Validated CouponFrontSearchRequest request,
                                                                 @ModelAttribute PageParamRequest pageParamRequest) {
        return CommonResult.success(CommonPage.restPage(couponService.getH5List(request, pageParamRequest)));
    }

    @ApiOperation(value = "当前订单可用优惠券")
    @RequestMapping(value = "/order/list", method = RequestMethod.GET)
    public CommonResult<List<CouponUserOrderResponse>> getCouponsListByPreOrderNo(@ModelAttribute @Validated OrderUseCouponRequest request) {
        return CommonResult.success(couponUserService.getListByPreOrderNo(request));
    }

    @ApiOperation(value = "我的优惠券")
    @RequestMapping(value = "/user/list", method = RequestMethod.GET)
    public CommonResult<CommonPage<UserCouponResponse>> getList(@Validated MyCouponRequest request) {
        return CommonResult.success(CommonPage.restPage(couponUserService.getMyCouponList(request)));
    }

    @ApiOperation(value = "领券")
    @RequestMapping(value = "/receive/{id}", method = RequestMethod.POST)
    public CommonResult<String> receive(@PathVariable(value = "id") Integer id) {
        if (couponUserService.receiveCoupon(id)) {
            return CommonResult.success();
        }
        return CommonResult.failed();
    }

    @ApiOperation(value = "优惠券领券中心")
    @RequestMapping(value = "/voucher/collection/center", method = RequestMethod.GET)
    public CommonResult<CommonPage<CouponCenterPageResponse>> getCouponCenter(@Validated CouponCenterSearchRequest request) {
        return CommonResult.success(CommonPage.restPage(couponService.getCouponCenter(request)));
    }
}



