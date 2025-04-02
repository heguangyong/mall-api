package com.zbkj.admin.controller.merchant;

import com.zbkj.common.model.wechat.video.PayComponentProduct;
import com.zbkj.common.page.CommonPage;
import com.zbkj.common.request.PageParamRequest;
import com.zbkj.common.request.wxvedio.product.ComponentProductSearchRequest;
import com.zbkj.common.response.wxvideo.PayComponentProductResponse;
import com.zbkj.common.result.CommonResult;
import com.zbkj.service.service.PayComponentProductService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;


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
@RequestMapping("api/admin/merchant/pay/component/product")
@Api(tags = "07小程序 自定义交易组件—过审商品") //配合swagger使用
public class PayComponentProductMerchantController {

    @Autowired
    private PayComponentProductService payComponentProductService;

    /**
     * 删除商品
     */
    @PreAuthorize("hasAuthority('merchant:pay:component:product:delete')")
    @ApiOperation(value = "删除商品")
    @RequestMapping(value = "/delete/{proId}", method = RequestMethod.GET)
    public CommonResult<Object> delete(@PathVariable Integer proId) {
        if (payComponentProductService.delete(proId)) {
            return CommonResult.success("删除商品成功");
        }
        return CommonResult.failed("删除商品失败");
    }

    /**
     * 商家上架商品
     */
    @PreAuthorize("hasAuthority('merchant:pay:component:product:puton')")
    @ApiOperation(value = "上架商品")
    @RequestMapping(value = "/puton/{proId}", method = RequestMethod.GET)
    public CommonResult<Object> puton(@PathVariable Integer proId) {
        if (payComponentProductService.putonByMerchant(proId)) {
            return CommonResult.success("上架商品成功");
        }
        return CommonResult.failed("上架商品失败");
    }

    /**
     * 商家下架商品
     */
    @PreAuthorize("hasAuthority('merchant:pay:component:product:putdown')")
    @ApiOperation(value = "下架商品")
    @RequestMapping(value = "/putdown/{proId}", method = RequestMethod.GET)
    public CommonResult<Object> putdown(@PathVariable Integer proId) {
        if (payComponentProductService.putdownByMerchant(proId)) {
            return CommonResult.success("下架商品成功");
        }
        return CommonResult.failed("下架商品失败");
    }

    /**
     * 当前商户的过审商品列表（分页）
     * @param request 搜索条件
     * @param pageParamRequest 分页参数
     */
    @PreAuthorize("hasAuthority('merchant:pay:component:product:list')")
    @ApiOperation(value = "过审商品列表（分页）") //配合swagger使用
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public CommonResult<CommonPage<PayComponentProduct>> getList(@Validated ComponentProductSearchRequest request,
                                                                 @Validated PageParamRequest pageParamRequest) {
        return CommonResult.success(CommonPage.restPage(payComponentProductService.getMerchantProductListByAfterWechatReview(request, pageParamRequest)));
    }


    /**
     * 过审商品详情
     */
    @PreAuthorize("hasAuthority('merchant:pay:component:product:info')")
    @ApiOperation(value = "过审商品详情") //配合swagger使用
    @RequestMapping(value = "/get/{id}", method = RequestMethod.GET)
    public CommonResult<PayComponentProductResponse> getInfo(@PathVariable Integer id) {
        return CommonResult.success(payComponentProductService.getInfo(id));
    }
}



