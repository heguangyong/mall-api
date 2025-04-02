package com.zbkj.admin.controller.merchant;

import cn.hutool.json.JSONException;
import com.zbkj.common.annotation.LogControllerAnnotation;
import com.zbkj.common.enums.MethodType;
import com.zbkj.common.page.CommonPage;
import com.zbkj.common.request.*;
import com.zbkj.common.response.*;
import com.zbkj.common.result.CommonResult;
import com.zbkj.service.service.ProductManagerService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.io.IOException;
import java.util.List;
import java.util.Map;


/**
 * 商户端商品控制器
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
@RequestMapping("api/admin/merchant/product")
@Api(tags = "商户端商品控制器") //配合swagger使用
public class MerchantProductController {

    @Autowired
    private ProductManagerService productService;

    @PreAuthorize("hasAuthority('merchant:product:page:list')")
    @ApiOperation(value = "商品分页列表") //配合swagger使用
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public CommonResult<CommonPage<AdminProductListResponse>> getList(@Validated MerProductSearchRequest request) {
        return CommonResult.success(CommonPage.restPage(productService.getAdminList(request)));
    }

    @LogControllerAnnotation(intoDB = true, methodType = MethodType.ADD, description = "新增商品")
    @PreAuthorize("hasAuthority('merchant:product:save')")
    @ApiOperation(value = "新增商品")
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    public CommonResult<String> save(@RequestBody @Validated ProductAddRequest request) {
        if (productService.save(request)) {
            return CommonResult.success();
        }
        return CommonResult.failed();
    }

    @LogControllerAnnotation(intoDB = true, methodType = MethodType.DELETE, description = "删除商品")
    @PreAuthorize("hasAuthority('merchant:product:delete')")
    @ApiOperation(value = "删除商品")
    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    public CommonResult<String> delete(@RequestBody @Validated ProductDeleteRequest request) {
        if (productService.deleteProduct(request)) {
            return CommonResult.success();
        }
        return CommonResult.failed();
    }

    @LogControllerAnnotation(intoDB = true, methodType = MethodType.UPDATE, description = "恢复回收站商品")
    @PreAuthorize("hasAuthority('merchant:product:restore')")
    @ApiOperation(value = "恢复回收站商品")
    @RequestMapping(value = "/restore/{id}", method = RequestMethod.POST)
    public CommonResult<String> restore(@PathVariable Integer id) {
        if (productService.restoreProduct(id)) {
            return CommonResult.success();
        }
        return CommonResult.failed();
    }

    @LogControllerAnnotation(intoDB = true, methodType = MethodType.UPDATE, description = "修改商品")
    @PreAuthorize("hasAuthority('merchant:product:update')")
    @ApiOperation(value = "商品修改")
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public CommonResult<String> update(@RequestBody @Validated ProductAddRequest ProductRequest) {
        if (productService.update(ProductRequest)) {
            return CommonResult.success();
        }
        return CommonResult.failed();
    }

    @PreAuthorize("hasAuthority('merchant:product:info')")
    @ApiOperation(value = "商品详情")
    @RequestMapping(value = "/info/{id}", method = RequestMethod.GET)
    public CommonResult<ProductInfoResponse> info(@PathVariable Integer id) {
        return CommonResult.success(productService.getInfo(id));
    }

    @PreAuthorize("hasAuthority('merchant:product:tabs:headers')")
    @ApiOperation(value = "商品表头数量")
    @RequestMapping(value = "/tabs/headers", method = RequestMethod.GET)
    public CommonResult<List<ProductTabsHeaderResponse>> getTabsHeader(@Validated MerProductTabsHeaderRequest request) {
        return CommonResult.success(productService.getTabsHeader(request));
    }

    @LogControllerAnnotation(intoDB = true, methodType = MethodType.UPDATE, description = "商品提审")
    @PreAuthorize("hasAuthority('merchant:product:submit:audit')")
    @ApiOperation(value = "商品提审")
    @RequestMapping(value = "/submit/audit", method = RequestMethod.POST)
    public CommonResult<String> submitAudit(@RequestBody @Validated ProductSubmitAuditRequest request) {
        if (productService.submitAudit(request)) {
            return CommonResult.success();
        }
        return CommonResult.failed();
    }

    @LogControllerAnnotation(intoDB = true, methodType = MethodType.UPDATE, description = "上架商品")
    @PreAuthorize("hasAuthority('merchant:product:up')")
    @ApiOperation(value = "商品上架")
    @RequestMapping(value = "/up/{id}", method = RequestMethod.POST)
    public CommonResult<String> up(@PathVariable Integer id) {
        if (productService.putOnShelf(id)) {
            return CommonResult.success();
        }
        return CommonResult.failed();
    }

    @LogControllerAnnotation(intoDB = true, methodType = MethodType.UPDATE, description = "下架商品")
    @PreAuthorize("hasAuthority('merchant:product:down')")
    @ApiOperation(value = "商品下架")
    @RequestMapping(value = "/down/{id}", method = RequestMethod.POST)
    public CommonResult<String> down(@PathVariable Integer id) {
        if (productService.offShelf(id)) {
            return CommonResult.success();
        }
        return CommonResult.failed();
    }

    @LogControllerAnnotation(intoDB = true, methodType = MethodType.UPDATE, description = "快捷添加库存")
    @PreAuthorize("hasAuthority('merchant:product:quick:stock:add')")
    @ApiOperation(value = "快捷添加库存")
    @RequestMapping(value = "/quick/stock/add", method = RequestMethod.POST)
    public CommonResult<String> quickAddStock(@RequestBody @Validated ProductAddStockRequest request) {
        if (productService.quickAddStock(request)) {
            return CommonResult.success();
        }
        return CommonResult.failed();
    }

    @LogControllerAnnotation(intoDB = true, methodType = MethodType.UPDATE, description = "商品免审编辑")
    @PreAuthorize("hasAuthority('merchant:product:review:free:edit')")
    @ApiOperation(value = "商品免审编辑")
    @RequestMapping(value = "/review/free/edit", method = RequestMethod.POST)
    public CommonResult<String> reviewFreeEdit(@RequestBody @Validated ProductReviewFreeEditRequest request) {
        if (productService.reviewFreeEdit(request)) {
            return CommonResult.success();
        }
        return CommonResult.failed();
    }

    @PreAuthorize("hasAuthority('merchant:product:import:product')")
    @ApiOperation(value = "导入99Api商品")
    @RequestMapping(value = "/importProduct", method = RequestMethod.POST)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "form", value = "导入平台1=淘宝，2=京东，3=苏宁，4=拼多多, 5=天猫", dataType = "int", required = true),
            @ApiImplicitParam(name = "url", value = "URL", dataType = "String", required = true),
    })
    public CommonResult<ProductResponseForCopyProduct> importProduct(
            @RequestParam @Valid int form,
            @RequestParam @Valid String url) throws IOException, JSONException {
        ProductResponseForCopyProduct productRequest = productService.importProductFrom99Api(url, form);
        return CommonResult.success(productRequest);
    }

    /**
     * 获取复制商品配置
     */
    @PreAuthorize("hasAuthority('admin:product:copy:config')")
    @ApiOperation(value = "获取复制商品配置")
    @RequestMapping(value = "/copy/config", method = RequestMethod.POST)
    public CommonResult<Map<String, Object>> copyConfig() {
        return CommonResult.success(productService.copyConfig());
    }


    @PreAuthorize("hasAuthority('merchant:product:copy:product')")
    @ApiOperation(value = "复制商品")
    @RequestMapping(value = "/copy/product", method = RequestMethod.POST)
    public CommonResult<ProductResponseForCopyProduct> copyProduct(@RequestBody @Valid CopyProductRequest request) {
        return CommonResult.success(productService.copyProduct(request.getUrl()));
    }

    @PreAuthorize("hasAuthority('merchant:product:activity:search:page')")
    @ApiOperation(value = "商品搜索分页列表（活动）")
    @RequestMapping(value = "/activity/search/page", method = RequestMethod.GET)
    public CommonResult<CommonPage<ProductActivityResponse>> getActivitySearchPage(@Validated ProductActivitySearchRequest request) {
        return CommonResult.success(CommonPage.restPage(productService.getActivitySearchPageByMerchant(request)));
    }

    @PreAuthorize("hasAuthority('merchant:product:set:freight:template')")
    @ApiOperation(value = "设置运费模板")
    @RequestMapping(value = "/set/freight/template", method = RequestMethod.POST)
    public CommonResult<Object> setFreightTemplate(@RequestBody @Valid ProductFreightTemplateRequest request) {
        if (productService.setFreightTemplate(request)) {
            return CommonResult.success();
        }
        return CommonResult.failed().setMessage("设置失败");
    }

    @PreAuthorize("hasAuthority('merchant:product:set:brokerage')")
    @ApiOperation(value = "设置佣金")
    @RequestMapping(value = "/set/brokerage", method = RequestMethod.POST)
    public CommonResult<Object> setBrokerage(@RequestBody @Valid ProductSetBrokerageRequest request) {
        if (productService.setBrokerage(request)) {
            return CommonResult.success();
        }
        return CommonResult.failed().setMessage("设置失败");
    }

    @PreAuthorize("hasAuthority('merchant:product:add:feedback:coupons')")
    @ApiOperation(value = "添加回馈券")
    @RequestMapping(value = "/add/feedback/coupons", method = RequestMethod.POST)
    public CommonResult<Object> addFeedbackCoupons(@RequestBody @Valid ProductAddFeedbackCouponsRequest request) {
        if (productService.addFeedbackCoupons(request)) {
            return CommonResult.success();
        }
        return CommonResult.failed().setMessage("设置失败");
    }

    @LogControllerAnnotation(intoDB = true, methodType = MethodType.UPDATE, description = "批量上架商品")
    @PreAuthorize("hasAuthority('merchant:product:batch:up')")
    @ApiOperation(value = "批量上架商品")
    @RequestMapping(value = "/batch/up", method = RequestMethod.POST)
    public CommonResult<String> batchUp(@RequestBody @Valid CommonBatchRequest request) {
        if (productService.batchUp(request.getIdList())) {
            return CommonResult.success();
        }
        return CommonResult.failed();
    }

    @LogControllerAnnotation(intoDB = true, methodType = MethodType.UPDATE, description = "批量下架商品")
    @PreAuthorize("hasAuthority('merchant:product:batch:down')")
    @ApiOperation(value = "批量商品下架")
    @RequestMapping(value = "/batch/down", method = RequestMethod.POST)
    public CommonResult<String> batchDown(@RequestBody @Valid CommonBatchRequest request) {
        if (productService.batchDown(request.getIdList())) {
            return CommonResult.success();
        }
        return CommonResult.failed();
    }

    @PreAuthorize("hasAuthority('merchant:product:batch:set:freight:template')")
    @ApiOperation(value = "批量设置运费模板")
    @RequestMapping(value = "/batch/set/freight/template", method = RequestMethod.POST)
    public CommonResult<Object> batchSetFreightTemplate(@RequestBody @Valid BatchSetProductFreightTemplateRequest request) {
        if (productService.batchSetFreightTemplate(request)) {
            return CommonResult.success();
        }
        return CommonResult.failed().setMessage("设置失败");
    }

    @PreAuthorize("hasAuthority('merchant:product:batch:set:brokerage')")
    @ApiOperation(value = "批量设置佣金")
    @RequestMapping(value = "/batch/set/brokerage", method = RequestMethod.POST)
    public CommonResult<Object> batchSetBrokerage(@RequestBody @Valid BatchSetProductBrokerageRequest request) {
        if (productService.batchSetBrokerage(request)) {
            return CommonResult.success();
        }
        return CommonResult.failed().setMessage("设置失败");
    }

    @PreAuthorize("hasAuthority('merchant:product:batch:add:feedback:coupons')")
    @ApiOperation(value = "批量添加回馈券")
    @RequestMapping(value = "/batch/add/feedback/coupons", method = RequestMethod.POST)
    public CommonResult<Object> batchAddFeedbackCoupons(@RequestBody @Valid BatchAddProductFeedbackCouponsRequest request) {
        if (productService.batchAddFeedbackCoupons(request)) {
            return CommonResult.success();
        }
        return CommonResult.failed().setMessage("设置失败");
    }

    @LogControllerAnnotation(intoDB = true, methodType = MethodType.DELETE, description = "批量加入回收站")
    @PreAuthorize("hasAuthority('merchant:product:batch:recycle')")
    @ApiOperation(value = "批量加入回收站")
    @RequestMapping(value = "/batch/recycle", method = RequestMethod.POST)
    public CommonResult<String> batchRecycle(@RequestBody @Validated CommonBatchRequest request) {
        if (productService.batchRecycle(request.getIdList())) {
            return CommonResult.success();
        }
        return CommonResult.failed();
    }

    @LogControllerAnnotation(intoDB = true, methodType = MethodType.DELETE, description = "批量删除商品")
    @PreAuthorize("hasAuthority('merchant:product:batch:delete')")
    @ApiOperation(value = "批量删除商品")
    @RequestMapping(value = "/batch/delete", method = RequestMethod.POST)
    public CommonResult<String> batchDelete(@RequestBody @Validated CommonBatchRequest request) {
        if (productService.batchDelete(request.getIdList())) {
            return CommonResult.success();
        }
        return CommonResult.failed();
    }

    @LogControllerAnnotation(intoDB = true, methodType = MethodType.UPDATE, description = "批量恢复回收站商品")
    @PreAuthorize("hasAuthority('merchant:product:batch:restore')")
    @ApiOperation(value = "批量恢复回收站商品")
    @RequestMapping(value = "/batch/restore", method = RequestMethod.POST)
    public CommonResult<String> batchRestore(@RequestBody @Validated CommonBatchRequest request) {
        if (productService.batchRestore(request.getIdList())) {
            return CommonResult.success();
        }
        return CommonResult.failed();
    }

    @LogControllerAnnotation(intoDB = true, methodType = MethodType.UPDATE, description = "批量提审商品")
    @PreAuthorize("hasAuthority('merchant:product:batch:submit:audit')")
    @ApiOperation(value = "批量提审商品")
    @RequestMapping(value = "/batch/submit/audit", method = RequestMethod.POST)
    public CommonResult<String> batchSubmitAudit(@RequestBody @Validated CommonBatchRequest request) {
        if (productService.batchSubmitAudit(request.getIdList())) {
            return CommonResult.success();
        }
        return CommonResult.failed();
    }

    @PreAuthorize("hasAuthority('merchant:product:marketing:search:page')")
    @ApiOperation(value = "商品搜索分页列表（营销）")
    @RequestMapping(value = "/marketing/search/page", method = RequestMethod.GET)
    public CommonResult<CommonPage<ProductMarketingResponse>> getMarketingSearchPage(@Validated MerProductMarketingSearchRequest request) {
        return CommonResult.success(CommonPage.restPage(productService.getMarketingSearchPageByMerchant(request)));
    }
}



