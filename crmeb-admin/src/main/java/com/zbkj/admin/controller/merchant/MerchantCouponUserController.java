package com.zbkj.admin.controller.merchant;

import com.zbkj.common.page.CommonPage;
import com.zbkj.common.request.CouponUserSearchRequest;
import com.zbkj.common.request.PageParamRequest;
import com.zbkj.common.response.CouponUserResponse;
import com.zbkj.common.result.CommonResult;
import com.zbkj.service.service.CouponUserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;


/**
 * 优惠券发放记录表 前端控制器
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
@RequestMapping("api/admin/merchant/coupon/user")
@Api(tags = "商户端用户优惠券控制器")
public class MerchantCouponUserController {

    @Autowired
    private CouponUserService couponUserService;

    @PreAuthorize("hasAuthority('merchant:coupon:user:page:list')")
    @ApiOperation(value = "优惠券领取记录分页列表")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public CommonResult<CommonPage<CouponUserResponse>> getList(@Validated CouponUserSearchRequest request,
                                                                @Validated PageParamRequest pageParamRequest) {
        return CommonResult.success(CommonPage.restPage(couponUserService.getPageList(request, pageParamRequest)));
    }

}



