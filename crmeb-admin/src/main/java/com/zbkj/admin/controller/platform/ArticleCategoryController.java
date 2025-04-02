package com.zbkj.admin.controller.platform;

import com.zbkj.common.annotation.LogControllerAnnotation;
import com.zbkj.common.enums.MethodType;
import com.zbkj.common.request.ArticleCategoryRequest;
import com.zbkj.common.response.ArticleCategoryResponse;
import com.zbkj.common.result.CommonResult;
import com.zbkj.service.service.ArticleCategoryService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;


/**
 * 文章管理表 前端控制器
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
@RequestMapping("api/admin/platform/article/category")
@Api(tags = "文章分类管理")
public class ArticleCategoryController {

    @Autowired
    private ArticleCategoryService articleCategoryService;

    @PreAuthorize("hasAuthority('platform:article:category:list')")
    @ApiOperation(value = "文章分类分页列表")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public CommonResult<List<ArticleCategoryResponse>> getList() {
        return CommonResult.success(articleCategoryService.getAdminList());
    }

    @LogControllerAnnotation(intoDB = true, methodType = MethodType.ADD, description = "新增文章分类")
    @PreAuthorize("hasAuthority('platform:article:category:add')")
    @ApiOperation(value = "新增文章分类")
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public CommonResult<String> add(@RequestBody @Validated ArticleCategoryRequest request) {
        if (articleCategoryService.create(request)) {
            return CommonResult.success();
        } else {
            return CommonResult.failed();
        }
    }

    @LogControllerAnnotation(intoDB = true, methodType = MethodType.DELETE, description = "删除文章分类")
    @PreAuthorize("hasAuthority('platform:article:category:delete')")
    @ApiOperation(value = "删除文章分类")
    @RequestMapping(value = "/delete/{id}", method = RequestMethod.POST)
    public CommonResult<String> delete(@PathVariable(value = "id") Integer id) {
        if (articleCategoryService.deleteById(id)) {
            return CommonResult.success();
        } else {
            return CommonResult.failed();
        }
    }

    @LogControllerAnnotation(intoDB = true, methodType = MethodType.UPDATE, description = "修改文章分类")
    @PreAuthorize("hasAuthority('platform:article:category:update')")
    @ApiOperation(value = "修改文章分类")
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public CommonResult<String> update(@RequestBody @Validated ArticleCategoryRequest request) {
        if (articleCategoryService.edit(request)) {
            return CommonResult.success();
        } else {
            return CommonResult.failed();
        }
    }

    @LogControllerAnnotation(intoDB = true, methodType = MethodType.UPDATE, description = "文章分类开关")
    @PreAuthorize("hasAuthority('platform:article:category:switch')")
    @ApiOperation(value="文章分类开关")
    @RequestMapping(value = "/switch/{id}", method = RequestMethod.POST)
    public CommonResult<String> categorySwitch(@PathVariable("id") Integer id) {
        if (articleCategoryService.categorySwitch(id)) {
            return CommonResult.success("切换文章分类开关成功");
        }
        return CommonResult.failed("切换文章分类开关失败");
    }
}



