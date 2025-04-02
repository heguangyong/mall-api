package com.zbkj.admin.controller.platform;

import com.zbkj.common.annotation.LogControllerAnnotation;
import com.zbkj.common.enums.MethodType;
import com.zbkj.common.page.CommonPage;
import com.zbkj.common.request.*;
import com.zbkj.common.response.*;
import com.zbkj.common.result.CommonResult;
import com.zbkj.service.service.ProductManagerService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;


/**
 * 平台端商品控制器
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
@RequestMapping("api/admin/platform/product")
@Api(tags = "平台端商品控制器") //配合swagger使用
public class ProductController {

    @Autowired
    private ProductManagerService productService;

    @PreAuthorize("hasAuthority('platform:product:page:list')")
    @ApiOperation(value = "商品分页列表") //配合swagger使用
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public CommonResult<CommonPage<PlatformProductListResponse>> getList(@Validated PlatProductSearchRequest request) {
        return CommonResult.success(CommonPage.restPage(productService.getPlatformPageList(request)));
    }

    @PreAuthorize("hasAuthority('platform:product:list:ids')")
    @ApiOperation(value = "根据商品id集合查询商品列表") //配合swagger使用
    @RequestMapping(value = "/listbyids", method = RequestMethod.POST)
    public CommonResult<List<PlatformProductListResponse>> getListByIds(@RequestBody ProductIdsSearchRequest request) {
        return CommonResult.success(productService.getPlatformListForIdsByLimit(request.getIds()));
    }

    @PreAuthorize("hasAuthority('platform:product:tabs:headers')")
    @ApiOperation(value = "商品表头数量")
    @RequestMapping(value = "/tabs/headers", method = RequestMethod.GET)
    public CommonResult<List<ProductTabsHeaderResponse>> getTabsHeader(@Validated PlatProductTabsHeaderRequest request) {
        return CommonResult.success(productService.getPlatformTabsHeader(request));
    }

    @LogControllerAnnotation(intoDB = true, methodType = MethodType.UPDATE, description = "商品审核")
    @PreAuthorize("hasAuthority('platform:product:audit')")
    @ApiOperation(value = "商品审核")
    @RequestMapping(value = "/audit", method = RequestMethod.POST)
    public CommonResult<String> audit(@RequestBody @Validated ProductAuditRequest request) {
        if (productService.audit(request)) {
            return CommonResult.success();
        }
        return CommonResult.failed();
    }

    @LogControllerAnnotation(intoDB = true, methodType = MethodType.UPDATE, description = "强制下架商品")
    @PreAuthorize("hasAuthority('platform:product:force:down')")
    @ApiOperation(value = "强制下架商品")
    @RequestMapping(value = "/force/down", method = RequestMethod.POST)
    public CommonResult<String> forceDown(@RequestBody @Validated ProductForceDownRequest request) {
        if (productService.forceDown(request)) {
            return CommonResult.success();
        }
        return CommonResult.failed();
    }

    @LogControllerAnnotation(intoDB = true, methodType = MethodType.UPDATE, description = "平台端商品编辑")
    @PreAuthorize("hasAuthority('platform:product:update')")
    @ApiOperation(value = "商品编辑")
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public CommonResult<String> platUpdate(@RequestBody @Validated ProductPlatUpdateRequest request) {
        if (productService.platUpdate(request)) {
            return CommonResult.success();
        }
        return CommonResult.failed();
    }

    @PreAuthorize("hasAuthority('platform:product:info')")
    @ApiOperation(value = "商品详情")
    @RequestMapping(value = "/info/{id}", method = RequestMethod.GET)
    public CommonResult<ProductInfoResponse> info(@PathVariable Integer id) {
        return CommonResult.success(productService.getInfo(id));
    }

    @PreAuthorize("hasAuthority('platform:product:activity:search:page')")
    @ApiOperation(value = "商品搜索分页列表（活动）")
    @RequestMapping(value = "/activity/search/page", method = RequestMethod.GET)
    public CommonResult<CommonPage<ProductActivityResponse>> getActivitySearchPage(@Validated ProductActivitySearchRequest request) {
        return CommonResult.success(CommonPage.restPage(productService.getActivitySearchPage(request)));
    }

    @LogControllerAnnotation(intoDB = true, methodType = MethodType.UPDATE, description = "平台端批量设置虚拟销量")
    @PreAuthorize("hasAuthority('platform:product:batch:set:virtual:sales')")
    @ApiOperation(value = "批量设置虚拟销量")
    @RequestMapping(value = "/batch/set/virtual/sales", method = RequestMethod.POST)
    public CommonResult<String> platBatchSetVirtualSales(@RequestBody @Validated BatchSetVirtualSalesRequest request) {
        if (productService.platBatchSetVirtualSales(request)) {
            return CommonResult.success();
        }
        return CommonResult.failed();
    }

    @LogControllerAnnotation(intoDB = true, methodType = MethodType.UPDATE, description = "平台端批量商品审核")
    @PreAuthorize("hasAuthority('platform:product:batch:audit')")
    @ApiOperation(value = "平台端批量商品审核")
    @RequestMapping(value = "/batch/audit", method = RequestMethod.POST)
    public CommonResult<String> batchAudit(@RequestBody @Validated BatchProductAuditRequest request) {
        if (productService.batchAudit(request)) {
            return CommonResult.success();
        }
        return CommonResult.failed();
    }

    @PreAuthorize("hasAuthority('platform:product:marketing:search:page')")
    @ApiOperation(value = "商品搜索分页列表（营销）")
    @RequestMapping(value = "/marketing/search/page", method = RequestMethod.GET)
    public CommonResult<CommonPage<ProductMarketingResponse>> getMarketingSearchPage(@Validated PlatProductMarketingSearchRequest request) {
        return CommonResult.success(CommonPage.restPage(productService.getMarketingSearchPage(request)));
    }
}



