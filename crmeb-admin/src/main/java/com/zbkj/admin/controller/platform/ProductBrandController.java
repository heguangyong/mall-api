package com.zbkj.admin.controller.platform;

import com.zbkj.common.annotation.LogControllerAnnotation;
import com.zbkj.common.enums.MethodType;
import com.zbkj.common.page.CommonPage;
import com.zbkj.common.request.PageParamRequest;
import com.zbkj.common.request.ProductBrandRequest;
import com.zbkj.common.response.ProductBrandListResponse;
import com.zbkj.common.response.ProductBrandResponse;
import com.zbkj.common.result.CommonResult;
import com.zbkj.service.service.ProductBrandService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;


/**
 * 平台端商品品牌控制器
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
@RequestMapping("api/admin/platform/product/brand")
@Api(tags = "平台端商品品牌控制器")
public class ProductBrandController {

    @Autowired
    private ProductBrandService productBrandService;

    @PreAuthorize("hasAuthority('platform:product:brand:list')")
    @ApiOperation(value = "品牌分页列表")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public CommonResult<CommonPage<ProductBrandListResponse>> getList(@Validated PageParamRequest pageParamRequest) {
        return CommonResult.success(CommonPage.restPage(productBrandService.getAdminPage(pageParamRequest)));
    }

    @LogControllerAnnotation(intoDB = true, methodType = MethodType.ADD, description = "新增品牌")
    @PreAuthorize("hasAuthority('platform:product:brand:add')")
    @ApiOperation(value = "新增品牌")
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public CommonResult<String> add(@RequestBody @Validated ProductBrandRequest request) {
        if (productBrandService.add(request)) {
            return CommonResult.success();
        }
        return CommonResult.failed();
    }

    @LogControllerAnnotation(intoDB = true, methodType = MethodType.DELETE, description = "删除品牌")
    @PreAuthorize("hasAuthority('platform:product:brand:delete')")
    @ApiOperation(value = "删除品牌")
    @RequestMapping(value = "/delete/{id}", method = RequestMethod.POST)
    public CommonResult<String> delete(@PathVariable(value = "id") Integer id) {
        if (productBrandService.delete(id)) {
            return CommonResult.success();
        }
        return CommonResult.failed();
    }

    @LogControllerAnnotation(intoDB = true, methodType = MethodType.UPDATE, description = "修改品牌")
    @PreAuthorize("hasAuthority('platform:product:brand:update')")
    @ApiOperation(value = "修改品牌")
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public CommonResult<String> update(@RequestBody @Validated ProductBrandRequest request) {
        if (productBrandService.edit(request)) {
            return CommonResult.success();
        }
        return CommonResult.failed();
    }

    @LogControllerAnnotation(intoDB = true, methodType = MethodType.UPDATE, description = "修改品牌显示状态")
    @PreAuthorize("hasAuthority('platform:product:brand:show:status')")
    @ApiOperation(value = "修改品牌显示状态")
    @RequestMapping(value = "/update/show/{id}", method = RequestMethod.POST)
    public CommonResult<Object> updateShowStatus(@PathVariable(value = "id") Integer id) {
        if (productBrandService.updateShowStatus(id)) {
            return CommonResult.success("修改成功");
        }
        return CommonResult.failed("修改失败");
    }

    @PreAuthorize("hasAuthority('platform:product:brand:cache:list')")
    @ApiOperation(value = "品牌缓存列表(全部)")
    @RequestMapping(value = "/cache/list", method = RequestMethod.GET)
    public CommonResult<List<ProductBrandResponse>> getCacheAllList() {
        return CommonResult.success(productBrandService.getCacheAllList());
    }
}



