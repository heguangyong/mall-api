package com.zbkj.admin.controller.merchant;

import com.zbkj.common.model.wechat.video.PayComponentDraftProduct;
import com.zbkj.common.page.CommonPage;
import com.zbkj.common.request.PageParamRequest;
import com.zbkj.common.request.wxvedio.product.ComponentProductSearchRequest;
import com.zbkj.common.request.wxvedio.product.PayComponentDraftProductMerchantOperationReviewStatus;
import com.zbkj.common.request.wxvedio.product.PayComponentProductAddRequest;
import com.zbkj.common.result.CommonResult;
import com.zbkj.service.service.PayComponentDraftProductService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * @Auther: 大粽子
 * @Date: 2022/10/10 19:36
 * @Description: 微信 自定义交易组件 草稿商品 控制器
 */
@Slf4j
@RestController
@RequestMapping("api/admin/merchant/pay/component/draftproduct")
@Api(tags = "08小程序 自定义交易组件—草稿商品") //配合swagger使用
public class PayComponentDraftMerchantProductController {

    @Autowired
    private PayComponentDraftProductService payComponentDraftProductService;

    /**
     * 添加商品 添加商品到草稿 不自动提审 有专门提审的api
     */
    @PreAuthorize("hasAuthority('merchant:pay:component:product:draft:add')")
    @ApiOperation(value = "添加草稿商品")
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public CommonResult<Object> add(@RequestBody @Validated PayComponentProductAddRequest addRequest) {
        if (payComponentDraftProductService.add(addRequest)) {
            return CommonResult.success("添加商品成功");
        }
        return CommonResult.failed("添加商品失败");
    }


    /**
     * 编辑草稿商品 不自动提审 有专门提审的api
     */
    @PreAuthorize("hasAuthority('merchant:pay:component:product:draft:update')")
    @ApiOperation(value = "编辑草稿商品")
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public CommonResult<Object> update(@RequestBody @Validated PayComponentProductAddRequest addRequest) {
        if (payComponentDraftProductService.edit(addRequest)) {
            return CommonResult.success("编辑草稿商品成功");
        }
        return CommonResult.failed("编辑草稿商品失败");
    }

    /**
     * 删除草稿商品 不自动提审 有专门提审的api
     */
    @PreAuthorize("hasAuthority('merchant:pay:component:product:draft:delete')")
    @ApiOperation(value = "删除草稿商品")
    @RequestMapping(value = "/delete/{id}", method = RequestMethod.GET)
    public CommonResult<Object> delete(@PathVariable Integer id) {
        if (payComponentDraftProductService.removeById(id)) {
            return CommonResult.success("删除草稿商品成功");
        }
        return CommonResult.failed("删除草稿商品失败");
    }

    /**
     * 草稿商品 商家审核操作
     */
    @PreAuthorize("hasAuthority('merchant:pay:component:product:draft:review')")
    @ApiOperation(value = "商家提审草稿商品")
    @RequestMapping(value = "/review", method = RequestMethod.POST)
    public CommonResult<Object> review(@RequestBody @Validated PayComponentDraftProductMerchantOperationReviewStatus reviewStatus) {
        if (payComponentDraftProductService.OperationPlatformReviewStatusByMerchant(reviewStatus)) {
            return CommonResult.success("商家操作草稿商品成功");
        }
        return CommonResult.failed("商家操作草稿商品失败");
    }

    /**
     * 当前商户获取自己的草稿商品列表（分页）
     * @param request 搜索条件
     * @param pageParamRequest 分页参数
     */
    @PreAuthorize("hasAuthority('merchant:pay:component:product:draft:list')")
    @ApiOperation(value = "当前商户获取自己的草稿商品列表（分页））") //配合swagger使用
    @RequestMapping(value = "/draft/list", method = RequestMethod.GET)
    public CommonResult<CommonPage<PayComponentDraftProduct>> getDraftList(@Validated ComponentProductSearchRequest request,
                                                                           @Validated PageParamRequest pageParamRequest) {
        return CommonResult.success(CommonPage.restPage(payComponentDraftProductService.getCurrentMerchantAdminListBeforeWeChatReview(request, pageParamRequest)));
    }

    /**
     * 草稿商品详情
     */
    @PreAuthorize("hasAuthority('merchant:pay:component:product:draft:info')")
    @ApiOperation(value = "草稿商品详情") //配合swagger使用
    @RequestMapping(value = "/draft/get/{id}", method = RequestMethod.GET)
    public CommonResult<PayComponentDraftProduct> getDraftInfo(@PathVariable Integer id) {
        return CommonResult.success(payComponentDraftProductService.getInfo(id));
    }
}
