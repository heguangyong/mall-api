package com.zbkj.admin.controller.merchant;

import com.zbkj.common.annotation.LogControllerAnnotation;
import com.zbkj.common.enums.MethodType;
import com.zbkj.common.model.coupon.Coupon;
import com.zbkj.common.page.CommonPage;
import com.zbkj.common.request.CouponProductJoinRequest;
import com.zbkj.common.request.CouponRequest;
import com.zbkj.common.request.CouponSearchRequest;
import com.zbkj.common.request.PageParamRequest;
import com.zbkj.common.response.CouponInfoResponse;
import com.zbkj.common.response.ProductCouponUseResponse;
import com.zbkj.common.result.CommonResult;
import com.zbkj.service.service.CouponService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;


/**
 * 优惠券表 前端控制器
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
@RequestMapping("api/admin/merchant/coupon")
@Api(tags = "商户端优惠券管理器")
public class MerchantCouponController {

    @Autowired
    private CouponService couponService;

    @PreAuthorize("hasAuthority('merchant:coupon:page:list')")
    @ApiOperation(value = "优惠券分页列表")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public CommonResult<CommonPage<Coupon>> getList(@Validated CouponSearchRequest request) {
        return CommonResult.success(CommonPage.restPage(couponService.getMerchantPageList(request)));
    }

    @PreAuthorize("hasAuthority('merchant:coupon:save')")
    @LogControllerAnnotation(intoDB = true, methodType = MethodType.ADD, description = "新增优惠券")
    @ApiOperation(value = "新增优惠券")
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    public CommonResult<String> save(@RequestBody @Validated CouponRequest request) {
        if (couponService.create(request)) {
            return CommonResult.success();
        }
        return CommonResult.failed();
    }

    @PreAuthorize("hasAuthority('merchant:coupon:update:status')")
    @LogControllerAnnotation(intoDB = true, methodType = MethodType.UPDATE, description = "修改优惠券状态")
    @ApiOperation(value = "修改优惠券状态")
    @RequestMapping(value = "/update/status/{id}", method = RequestMethod.POST)
    public CommonResult<String> updateStatus(@PathVariable Integer id) {
        if (couponService.updateStatus(id)) {
            return CommonResult.success();
        }
        return CommonResult.failed();
    }

    @PreAuthorize("hasAuthority('merchant:coupon:info')")
    @ApiOperation(value = "优惠券详情")
    @RequestMapping(value = "/info/{id}", method = RequestMethod.GET)
    public CommonResult<CouponInfoResponse> info(@PathVariable Integer id) {
        return CommonResult.success(couponService.info(id));
    }

    @PreAuthorize("hasAuthority('merchant:coupon:product:usable:list')")
    @ApiOperation(value = "商品可用优惠券列表")
    @RequestMapping(value = "/product/usable/list", method = RequestMethod.GET)
    public CommonResult<List<ProductCouponUseResponse>> getProductUsableList() {
        return CommonResult.success(couponService.getProductUsableList());
    }

    @PreAuthorize("hasAuthority('merchant:coupon:delete')")
    @LogControllerAnnotation(intoDB = true, methodType = MethodType.DELETE, description = "删除优惠券")
    @ApiOperation(value = "删除优惠券")
    @RequestMapping(value = "/delete/{id}", method = RequestMethod.POST)
    public CommonResult<String> delete(@PathVariable Integer id) {
        if (couponService.delete(id)) {
            return CommonResult.success("删除成功");
        }
        return CommonResult.failed("删除失败");
    }

    @PreAuthorize("hasAuthority('merchant:coupon:product:join:edit')")
    @LogControllerAnnotation(intoDB = true, methodType = MethodType.UPDATE, description = "商品券关联商品编辑")
    @ApiOperation(value = "商品券关联商品编辑")
    @RequestMapping(value = "/product/join/edit", method = RequestMethod.POST)
    public CommonResult<String> couponProductJoinEdit(@RequestBody @Validated CouponProductJoinRequest request) {
        if (couponService.couponProductJoinEdit(request)) {
            return CommonResult.success("编辑成功");
        }
        return CommonResult.failed("编辑失败");
    }
}



