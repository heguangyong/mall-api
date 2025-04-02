package com.zbkj.admin.controller.platform;

import com.zbkj.common.annotation.LogControllerAnnotation;
import com.zbkj.common.enums.MethodType;
import com.zbkj.common.model.product.ProductCategory;
import com.zbkj.common.request.ProductCategoryRequest;
import com.zbkj.common.result.CommonResult;
import com.zbkj.common.vo.ProCategoryCacheVo;
import com.zbkj.service.service.ProductCategoryService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;


/**
 * 平台端商品分类控制器
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
@RequestMapping("api/admin/platform/product/category")
@Api(tags = "平台端商品分类控制器")
public class ProductCategoryController {

    @Autowired
    private ProductCategoryService productCategoryService;

    @PreAuthorize("hasAuthority('platform:product:category:list')")
    @ApiOperation(value = "商品分类列表")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public CommonResult<List<ProductCategory>> getList() {
        return CommonResult.success(productCategoryService.getAdminList());
    }

    @LogControllerAnnotation(intoDB = true, methodType = MethodType.ADD, description = "新增商品分类")
    @PreAuthorize("hasAuthority('platform:product:category:add')")
    @ApiOperation(value = "新增商品分类")
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public CommonResult<String> add(@RequestBody @Validated ProductCategoryRequest request) {
        if (productCategoryService.add(request)) {
            return CommonResult.success();
        }
        return CommonResult.failed();
    }

    @LogControllerAnnotation(intoDB = true, methodType = MethodType.DELETE, description = "删除商品分类")
    @PreAuthorize("hasAuthority('platform:product:category:delete')")
    @ApiOperation(value = "删除商品分类")
    @RequestMapping(value = "/delete/{id}", method = RequestMethod.POST)
    public CommonResult<String> delete(@PathVariable(value = "id") Integer id) {
        if (productCategoryService.delete(id)) {
            return CommonResult.success();
        }
        return CommonResult.failed();
    }

    @LogControllerAnnotation(intoDB = true, methodType = MethodType.UPDATE, description = "修改商品分类")
    @PreAuthorize("hasAuthority('platform:product:category:update')")
    @ApiOperation(value = "修改商品分类")
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public CommonResult<String> update(@RequestBody @Validated ProductCategoryRequest request) {
        if (productCategoryService.edit(request)) {
            return CommonResult.success();
        }
        return CommonResult.failed();
    }

    @LogControllerAnnotation(intoDB = true, methodType = MethodType.UPDATE, description = "修改商品分类显示状态")
    @PreAuthorize("hasAuthority('platform:product:category:show:status')")
    @ApiOperation(value = "修改商品分类显示状态")
    @RequestMapping(value = "/update/show/{id}", method = RequestMethod.POST)
    public CommonResult<Object> updateShowStatus(@PathVariable(value = "id") Integer id) {
        if (productCategoryService.updateShowStatus(id)) {
            return CommonResult.success("修改成功");
        }
        return CommonResult.failed("修改失败");
    }

    @PreAuthorize("hasAuthority('platform:product:category:cache:tree')")
    @ApiOperation(value = "商品分类缓存树")
    @RequestMapping(value = "/cache/tree", method = RequestMethod.GET)
    public CommonResult<List<ProCategoryCacheVo>> getPlatformCacheTree() {
        return CommonResult.success(productCategoryService.getCacheTree());
    }
}



