package com.zbkj.admin.controller.platform;

import com.github.pagehelper.PageInfo;
import com.zbkj.common.annotation.LogControllerAnnotation;
import com.zbkj.common.enums.MethodType;
import com.zbkj.common.model.merchant.MerchantCategory;
import com.zbkj.common.page.CommonPage;
import com.zbkj.common.request.merchant.MerchantCategoryRequest;
import com.zbkj.common.request.PageParamRequest;
import com.zbkj.common.result.CommonResult;
import com.zbkj.service.service.MerchantCategoryService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 商户分类控制器
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
@RequestMapping("api/admin/platform/merchant/category")
@Api(tags = "平台端商户分类控制器")
public class MerchantCategoryController {

    @Autowired
    private MerchantCategoryService categoryService;

    @PreAuthorize("hasAuthority('platform:merchant:category:list')")
    @ApiOperation(value="商户分类分页列表")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public CommonResult<CommonPage<MerchantCategory>> getList(@Validated PageParamRequest pageParamRequest) {
        PageInfo<MerchantCategory> pageInfo = categoryService.getAdminPage(pageParamRequest);
        return CommonResult.success(CommonPage.restPage(pageInfo));
    }

    @LogControllerAnnotation(intoDB = true, methodType = MethodType.ADD, description = "添加商户分类")
    @PreAuthorize("hasAuthority('platform:merchant:category:add')")
    @ApiOperation(value="添加商户分类")
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public CommonResult<Object> add(@RequestBody @Validated MerchantCategoryRequest request) {
        if (categoryService.add(request)) {
            return CommonResult.success("添加商户分类成功");
        }
        return CommonResult.failed("添加商户分类失败");
    }

    @LogControllerAnnotation(intoDB = true, methodType = MethodType.UPDATE, description = "编辑商户分类")
    @PreAuthorize("hasAuthority('platform:merchant:category:update')")
    @ApiOperation(value="编辑商户分类")
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public CommonResult<Object> update(@RequestBody @Validated MerchantCategoryRequest request) {
        if (categoryService.edit(request)) {
            return CommonResult.success("编辑商户分类成功");
        }
        return CommonResult.failed("编辑商户分类失败");
    }

    @LogControllerAnnotation(intoDB = true, methodType = MethodType.DELETE, description = "删除商户分类")
    @PreAuthorize("hasAuthority('platform:merchant:category:delete')")
    @ApiOperation(value="删除商户分类")
    @RequestMapping(value = "/delete/{id}", method = RequestMethod.POST)
    public CommonResult<Object> delete(@PathVariable("id") Integer id) {
        if (categoryService.delete(id)) {
            return CommonResult.success("删除商户分类成功");
        }
        return CommonResult.failed("删除商户分类失败");
    }

    @PreAuthorize("hasAuthority('platform:merchant:category:all')")
    @ApiOperation(value="获取全部商户分类列表")
    @RequestMapping(value = "/all/list", method = RequestMethod.GET)
    public CommonResult<List<MerchantCategory>> allList() {
        return CommonResult.success(categoryService.allList());
    }
}
