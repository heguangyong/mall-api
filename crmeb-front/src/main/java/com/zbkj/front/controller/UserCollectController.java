package com.zbkj.front.controller;

import com.zbkj.common.page.CommonPage;
import com.zbkj.common.request.CancelCollectRequest;
import com.zbkj.common.request.PageParamRequest;
import com.zbkj.common.request.UserCollectRequest;
import com.zbkj.common.response.MerchantCollectResponse;
import com.zbkj.common.response.MerchantDetailResponse;
import com.zbkj.common.response.UserProductRelationResponse;
import com.zbkj.common.result.CommonResult;
import com.zbkj.service.service.ProductRelationService;
import com.zbkj.service.service.UserMerchantCollectService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


/**
 * 商品点赞和收藏表 前端控制器
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
@RequestMapping("api/front/collect")
@Api(tags = "用户 -- 收藏")
public class UserCollectController {

    @Autowired
    private ProductRelationService productRelationService;
    @Autowired
    private UserMerchantCollectService userMerchantCollectService;

    @ApiOperation(value = "我的商品收藏列表")
    @RequestMapping(value = "/product/list", method = RequestMethod.GET)
    public CommonResult<CommonPage<UserProductRelationResponse>> getList(@Validated PageParamRequest pageParamRequest) {
        return CommonResult.success(CommonPage.restPage(productRelationService.getUserList(pageParamRequest)));
    }

    @ApiOperation(value = "添加收藏产品")
    @RequestMapping(value = "/add/product", method = RequestMethod.POST)
    public CommonResult<String> save(@RequestBody @Validated UserCollectRequest request) {
        if (productRelationService.add(request)) {
            return CommonResult.success();
        }
        return CommonResult.failed();
    }

    @ApiOperation(value = "取消收藏产品")
    @RequestMapping(value = "/cancel/product", method = RequestMethod.POST)
    public CommonResult<String> delete(@RequestBody CancelCollectRequest request) {
        if (productRelationService.delete(request)) {
            return CommonResult.success();
        }
        return CommonResult.failed();
    }

    @ApiOperation(value = "收藏店铺")
    @RequestMapping(value = "/add/merchant/{id}", method = RequestMethod.POST)
    public CommonResult<MerchantDetailResponse> userCollect(@PathVariable Integer id) {
        if (userMerchantCollectService.userCollect(id)) {
            return CommonResult.success();
        }
        return CommonResult.failed();
    }

    @ApiOperation(value = "取消收藏店铺")
    @RequestMapping(value = "/cancel/merchant/{id}", method = RequestMethod.POST)
    public CommonResult<MerchantDetailResponse> userCancelCollect(@PathVariable Integer id) {
        if (userMerchantCollectService.userCancelCollect(id)) {
            return CommonResult.success();
        }
        return CommonResult.failed();
    }

    @ApiOperation(value = "商户收藏列表")
    @RequestMapping(value = "/merchant/list", method = RequestMethod.GET)
    public CommonResult<CommonPage<MerchantCollectResponse>> getMerchantList(@Validated PageParamRequest pageParamRequest) {
        return CommonResult.success(CommonPage.restPage(userMerchantCollectService.findList(pageParamRequest)));
    }

    @ApiOperation(value = "批量取消收藏店铺")
    @RequestMapping(value = "/cancel/merchant/batch", method = RequestMethod.POST)
    public CommonResult<MerchantDetailResponse> userBatchCancelCollect(@RequestBody CancelCollectRequest request) {
        if (userMerchantCollectService.userBatchCancelCollect(request)) {
            return CommonResult.success();
        }
        return CommonResult.failed();
    }
}



