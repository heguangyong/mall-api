package com.zbkj.admin.controller.platform;

import com.zbkj.common.model.wechat.video.PayComponentDraftProduct;
import com.zbkj.common.model.wechat.video.PayComponentProduct;
import com.zbkj.common.page.CommonPage;
import com.zbkj.common.request.PageParamRequest;
import com.zbkj.common.request.wxvedio.product.ComponentProductSearchRequest;
import com.zbkj.common.request.wxvedio.product.PayComponentProductAddRequest;
import com.zbkj.common.response.wxvideo.PayComponentProductResponse;
import com.zbkj.common.result.CommonResult;
import com.zbkj.service.service.PayComponentDraftProductService;
import com.zbkj.service.service.PayComponentProductService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


/**
 *  组件商品表 前端控制器
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
@RequestMapping("api/admin/platform/pay/component/product")
@Api(tags = "07小程序 自定义交易组件—过审商品") //配合swagger使用
public class PayComponentProductController {

    @Autowired
    private PayComponentProductService payComponentProductService;

    /**
     * 删除商品
     */
    @PreAuthorize("hasAuthority('platform:pay:component:product:delete')")
    @ApiOperation(value = "删除商品")
    @RequestMapping(value = "/delete/{proId}", method = RequestMethod.POST)
    public CommonResult<Object> delete(@PathVariable Integer proId) {
        if (payComponentProductService.delete(proId)) {
            return CommonResult.success("删除商品成功");
        }
        return CommonResult.failed("删除商品失败");
    }

    /**
     * 更新商品 包括平台审核和微信审核
     */
//    @PreAuthorize("hasAuthority('platform:pay:component:product:update')")
//    @ApiOperation(value = "更新商品-替换更新审核商品")
//    @RequestMapping(value = "/update", method = RequestMethod.POST)
//    public CommonResult<Object> update(@RequestBody @Validated PayComponentProductAddRequest addRequest) {
//        if (payComponentProductService.update(addRequest)) {
//            return CommonResult.success("更新商品成功");
//        }
//        return CommonResult.failed("更新商品失败");
//    }

    /**
     * 上架商品
     */
//    @PreAuthorize("hasAuthority('platform:pay:component:product:listing')")
//    @ApiOperation(value = "上架商品")
//    @RequestMapping(value = "/puton/{proId}", method = RequestMethod.POST)
//    public CommonResult<Object> puton(@PathVariable Integer proId) {
//        if (payComponentProductService.puton(proId)) {
//            return CommonResult.success("上架商品成功");
//        }
//        return CommonResult.failed("上架商品失败");
//    }

    /**
     * 平台下架商品
     */
    @PreAuthorize("hasAuthority('platform:pay:component:product:delisting')")
    @ApiOperation(value = "下架商品")
    @RequestMapping(value = "/putdown/{proId}", method = RequestMethod.POST)
    public CommonResult<Object> putdown(@PathVariable Integer proId) {
        if (payComponentProductService.putdownByPlatForm(proId)) {
            return CommonResult.success("下架商品成功");
        }
        return CommonResult.failed("下架商品失败");
    }

    /**
     * 获取全平台下 的过审商品列表（分页）
     * @param request 搜索条件
     * @param pageParamRequest 分页参数
     */
    @PreAuthorize("hasAuthority('platform:pay:component:product:list')")
    @ApiOperation(value = "获取全平台下的过审商品列表（分页）") //配合swagger使用
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public CommonResult<CommonPage<PayComponentProduct>> getPlatformListByAfterWechatReview(@Validated ComponentProductSearchRequest request,
                                                                 @Validated PageParamRequest pageParamRequest) {
        return CommonResult.success(CommonPage.restPage(payComponentProductService.getPlatformProductListByAfterWechatReview(request, pageParamRequest)));
    }

    /**
     * 过审商品详情
     */
    @PreAuthorize("hasAuthority('platform:pay:component:product:info')")
    @ApiOperation(value = "过审商品详情") //配合swagger使用
    @RequestMapping(value = "/get/{id}", method = RequestMethod.GET)
    public CommonResult<PayComponentProductResponse> getInfo(@PathVariable Integer id) {
        return CommonResult.success(payComponentProductService.getInfo(id));
    }
}



