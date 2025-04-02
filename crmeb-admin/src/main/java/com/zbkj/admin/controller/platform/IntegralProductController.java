package com.zbkj.admin.controller.platform;

import com.zbkj.common.annotation.LogControllerAnnotation;
import com.zbkj.common.enums.MethodType;
import com.zbkj.common.page.CommonPage;
import com.zbkj.common.request.IntegralProductAddRequest;
import com.zbkj.common.request.IntegralProductPageSearchRequest;
import com.zbkj.common.request.IntegralProductTabsHeaderRequest;
import com.zbkj.common.response.IntegralProductDetailResponse;
import com.zbkj.common.response.IntegralProductPageResponse;
import com.zbkj.common.result.CommonResult;
import com.zbkj.service.service.ProductService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 积分商品控制器
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
@RequestMapping("api/admin/platform/integral/product")
@Api(tags = "积分商品控制器")
public class IntegralProductController {

    @Autowired
    private ProductService productService;

    @PreAuthorize("hasAuthority('platform:integral:product:page')")
    @ApiOperation(value = "积分商品分页列表") //配合swagger使用
    @RequestMapping(value = "/page", method = RequestMethod.GET)
    public CommonResult<CommonPage<IntegralProductPageResponse>> getList(@Validated IntegralProductPageSearchRequest request) {
        return CommonResult.success(CommonPage.restPage(productService.getIntegralProductPageByPlat(request)));
    }

    @LogControllerAnnotation(intoDB = true, methodType = MethodType.ADD, description = "新增积分商品")
    @PreAuthorize("hasAuthority('platform:integral:product:save')")
    @ApiOperation(value = "新增积分商品")
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    public CommonResult<String> save(@RequestBody @Validated IntegralProductAddRequest request) {
        if (productService.saveIntegralProduct(request)) {
            return CommonResult.success();
        }
        return CommonResult.failed();
    }

    @LogControllerAnnotation(intoDB = true, methodType = MethodType.UPDATE, description = "修改积分商品")
    @PreAuthorize("hasAuthority('platform:integral:product:update')")
    @ApiOperation(value = "修改积分商品")
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public CommonResult<String> update(@RequestBody @Validated IntegralProductAddRequest request) {
        if (productService.updateIntegralProduct(request)) {
            return CommonResult.success();
        }
        return CommonResult.failed();
    }

    @PreAuthorize("hasAuthority('platform:integral:product:detail')")
    @ApiOperation(value = "积分商品详情")
    @RequestMapping(value = "/detail/{id}", method = RequestMethod.GET)
    public CommonResult<IntegralProductDetailResponse> detail(@PathVariable Integer id) {
        return CommonResult.success(productService.getIntegralProductDetail(id));
    }

    @PreAuthorize("hasAuthority('platform:integral:product:tabs:headers')")
    @ApiOperation(value = "积分商品表头数量")
    @RequestMapping(value = "/tabs/headers", method = RequestMethod.GET)
    public CommonResult<Map<String, Object>> getTabsHeader(@Validated IntegralProductTabsHeaderRequest request) {
        return CommonResult.success(productService.getIntegralProductTabsHeader(request));
    }

    @PreAuthorize("hasAuthority('platform:integral:product:delete')")
    @ApiOperation(value = "删除积分商品")
    @RequestMapping(value = "/delete/{id}", method = RequestMethod.POST)
    public CommonResult<IntegralProductDetailResponse> delete(@PathVariable Integer id) {
        if (productService.deleteIntegralProduct(id)) {
            return CommonResult.success();
        }
        return CommonResult.failed();
    }

    @LogControllerAnnotation(intoDB = true, methodType = MethodType.UPDATE, description = "上架商品")
    @PreAuthorize("hasAuthority('platform:integral:product:update:show')")
    @ApiOperation(value = "积分商品上/下架")
    @RequestMapping(value = "/update/show/{id}", method = RequestMethod.POST)
    public CommonResult<String> updateShow(@PathVariable Integer id) {
        if (productService.updateShowIntegralProduct(id)) {
            return CommonResult.success();
        }
        return CommonResult.failed();
    }

}
