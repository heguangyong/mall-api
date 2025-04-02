package com.zbkj.front.controller;


import com.zbkj.common.page.CommonPage;
import com.zbkj.common.request.*;
import com.zbkj.common.request.merchant.MerchantProductSearchRequest;
import com.zbkj.common.response.*;
import com.zbkj.common.result.CommonResult;
import com.zbkj.common.utils.CrmebUtil;
import com.zbkj.front.service.FrontProductService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 商品控制器
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
@RequestMapping("api/front/product")
@Api(tags = "商品控制器")
public class ProductController {

    @Autowired
    private FrontProductService productService;

    @ApiOperation(value = "商品分页列表前置信息")
    @RequestMapping(value = "/list/before", method = RequestMethod.GET)
    public CommonResult<ProductSearchBeforeResponse> getListBefore(@Validated ProductFrontSearchRequest request) {
        return CommonResult.success(productService.getListBefore(request.getKeyword()));
    }

    @ApiOperation(value = "商品分页列表")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public CommonResult<CommonPage<ProductFrontResponse>> getList(@ModelAttribute @Validated ProductFrontSearchRequest request,
                                                                  @ModelAttribute @Validated PageParamRequest pageParamRequest) {
        return CommonResult.success(CommonPage.restPage(productService.getList(request, pageParamRequest)));
    }

    @ApiOperation(value = "商品详情")
    @RequestMapping(value = "/detail", method = RequestMethod.GET)
    public CommonResult<ProductDetailResponse> getDetail(@Validated ProductFrontDetailRequest request) {
        return CommonResult.success(productService.getDetail_V1_7(request.getId(), request.getType(),
                request.getMarketingType(), request.getGroupActivityId(), request.getGroupBuyRecordId()));
    }

    @ApiOperation(value = "根据商品id集合查询对应商品")
    @RequestMapping(value = "/byids/{ids}", method = RequestMethod.GET)
    public CommonResult<List<ProductFrontResponse>> getProductByIds(@PathVariable String ids) {
        return CommonResult.success(productService.getProductByIds(CrmebUtil.stringToArray(ids)));
    }

    @ApiOperation(value = "商品评论列表")
    @RequestMapping(value = "/reply/list/{id}", method = RequestMethod.GET)
    @ApiImplicitParam(name = "type", value = "评价等级|0=全部,1=好评,2=中评,3=差评", allowableValues = "range[0,1,2,3]")
    public CommonResult<CommonPage<ProductReplyResponse>> getReplyList(@PathVariable Integer id,
                                                                       @RequestParam(value = "type") Integer type, @Validated PageParamRequest pageParamRequest) {
        return CommonResult.success(CommonPage.restPage(productService.getReplyList(id, type, pageParamRequest)));
    }

    @ApiOperation(value = "商品评论数量")
    @RequestMapping(value = "/reply/config/{id}", method = RequestMethod.GET)
    public CommonResult<ProductReplayCountResponse> getReplyCount(@PathVariable Integer id) {
        return CommonResult.success(productService.getReplyCount(id));
    }

    @ApiOperation(value = "商品详情评论")
    @RequestMapping(value = "/reply/detail/{id}", method = RequestMethod.GET)
    public CommonResult<ProductDetailReplyResponse> getProductReply(@PathVariable Integer id) {
        return CommonResult.success(productService.getProductReply(id));
    }

    @ApiOperation(value = "商户商品列表")
    @RequestMapping(value = "/merchant/pro/list", method = RequestMethod.GET)
    public CommonResult<CommonPage<ProductCommonResponse>> getMerchantProList(@ModelAttribute @Validated MerchantProductSearchRequest request,
                                                                              @ModelAttribute @Validated PageParamRequest pageParamRequest) {
        return CommonResult.success(CommonPage.restPage(productService.getMerchantProList(request, pageParamRequest)));
    }

    @ApiOperation(value = "我的优惠券商品列表")
    @RequestMapping(value = "/coupon/pro/list", method = RequestMethod.GET)
    public CommonResult<CommonPage<ProductFrontResponse>> getCouponProList(@ModelAttribute @Validated CouponProductSearchRequest request) {
        return CommonResult.success(CommonPage.restPage(productService.getCouponProList(request)));
    }

    @ApiOperation(value = "已购商品列表")
    @RequestMapping(value = "/purchased/list", method = RequestMethod.GET)
    public CommonPage<ProductSimpleResponse> getPurchasedList(@ModelAttribute @Validated PageParamRequest pageParamRequest) {
        return CommonPage.restPage(productService.findPurchasedList(pageParamRequest));
    }

    @ApiOperation(value = "足迹商品列表")
    @RequestMapping(value = "/browse/list", method = RequestMethod.GET)
    public CommonPage<ProductSimpleResponse> getBrowseList(@ModelAttribute @Validated PageParamRequest pageParamRequest) {
        return CommonPage.restPage(productService.findBrowseList(pageParamRequest));
    }

    @ApiOperation(value = "系统优惠券商品列表")
    @RequestMapping(value = "/system/coupon/pro/list", method = RequestMethod.GET)
    public CommonResult<CommonPage<ProductFrontResponse>> findCouponProductList(@Validated SystemCouponProductSearchRequest request) {
        return CommonResult.success(CommonPage.restPage(productService.findCouponProductList(request)));
    }

    @ApiOperation(value = "推荐商品分页列表")
    @RequestMapping(value = "/recommend/list", method = RequestMethod.GET)
    public CommonResult<CommonPage<RecommendProductResponse>> findRecommendPage(@ModelAttribute @Validated PageParamRequest pageParamRequest) {
        return CommonResult.success(CommonPage.restPage(productService.findRecommendPage(pageParamRequest)));
    }

    @ApiOperation(value = "会员商品分页列表")
    @RequestMapping(value = "/member/list", method = RequestMethod.GET)
    public CommonResult<CommonPage<RecommendProductResponse>> findMemberPage(@ModelAttribute @Validated PageParamRequest pageParamRequest) {
        return CommonResult.success(CommonPage.restPage(productService.findMemberPage(pageParamRequest)));
    }
}



