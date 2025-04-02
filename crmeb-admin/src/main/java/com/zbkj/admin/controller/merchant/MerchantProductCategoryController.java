package com.zbkj.admin.controller.merchant;

import com.zbkj.common.annotation.LogControllerAnnotation;
import com.zbkj.common.enums.MethodType;
import com.zbkj.common.model.merchant.MerchantProductCategory;
import com.zbkj.common.request.merchant.MerchantProductCategoryRequest;
import com.zbkj.common.result.CommonResult;
import com.zbkj.common.vo.ProCategoryCacheVo;
import com.zbkj.service.service.MerchantProductCategoryService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;


/**
 * 商户端商户商品分类控制器
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
@RequestMapping("api/admin/merchant/store/product/category")
@Api(tags = "商户端商户商品分类控制器")
public class MerchantProductCategoryController {

    @Autowired
    private MerchantProductCategoryService categoryService;


    @PreAuthorize("hasAuthority('merchant:product:category:list')")
    @ApiOperation(value = "商户商品分类列表")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public CommonResult<List<MerchantProductCategory>> getList() {
        return CommonResult.success(categoryService.getAdminList());
    }

    @PreAuthorize("hasAuthority('merchant:product:category:add')")
    @LogControllerAnnotation(intoDB = true, methodType = MethodType.ADD, description = "新增商户商品分类")
    @ApiOperation(value = "新增商户商品分类")
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public CommonResult<String> add(@RequestBody @Validated MerchantProductCategoryRequest request) {
        if (categoryService.add(request)) {
            return CommonResult.success();
        }
        return CommonResult.failed();
    }

    @PreAuthorize("hasAuthority('merchant:product:category:delete')")
    @LogControllerAnnotation(intoDB = true, methodType = MethodType.DELETE, description = "删除商户商品分类")
    @ApiOperation(value = "删除商户商品分类")
    @RequestMapping(value = "/delete/{id}", method = RequestMethod.POST)
    public CommonResult<String> delete(@PathVariable(value = "id") Integer id) {
        if (categoryService.delete(id)) {
            return CommonResult.success();
        }
        return CommonResult.failed();
    }

    @PreAuthorize("hasAuthority('merchant:product:category:update')")
    @LogControllerAnnotation(intoDB = true, methodType = MethodType.UPDATE, description = "修改商户商品分类")
    @ApiOperation(value = "修改商户商品分类")
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public CommonResult<String> update(@RequestBody @Validated MerchantProductCategoryRequest request) {
        if (categoryService.edit(request)) {
            return CommonResult.success();
        }
        return CommonResult.failed();
    }

    @PreAuthorize("hasAuthority('merchant:product:category:show:status')")
    @LogControllerAnnotation(intoDB = true, methodType = MethodType.UPDATE, description = "修改商户商品分类显示状态")
    @ApiOperation(value = "修改商户商品分类显示状态")
    @RequestMapping(value = "/update/show/{id}", method = RequestMethod.POST)
    public CommonResult<String> updateShowStatus(@PathVariable(value = "id") Integer id) {
        if (categoryService.updateShowStatus(id)) {
            return CommonResult.success("修改成功");
        }
        return CommonResult.failed("修改失败");
    }

    @PreAuthorize("hasAuthority('merchant:product:category:cache:tree')")
    @ApiOperation(value = "商户商品分类缓存树")
    @RequestMapping(value = "/cache/tree", method = RequestMethod.GET)
    public CommonResult<List<ProCategoryCacheVo>> getCacheTree() {
        return CommonResult.success(categoryService.getCacheTree());
    }
}



