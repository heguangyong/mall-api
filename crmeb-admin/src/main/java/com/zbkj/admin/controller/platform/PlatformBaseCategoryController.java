package com.zbkj.admin.controller.platform;

import com.zbkj.common.annotation.LogControllerAnnotation;
import com.zbkj.common.enums.MethodType;
import com.zbkj.common.model.category.Category;
import com.zbkj.common.page.CommonPage;
import com.zbkj.common.request.CategoryRequest;
import com.zbkj.common.request.CategorySearchRequest;
import com.zbkj.common.request.PageParamRequest;
import com.zbkj.common.result.CommonResult;
import com.zbkj.common.utils.CrmebUtil;
import com.zbkj.common.vo.CategoryTreeVo;
import com.zbkj.service.service.CategoryService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;


/**
 * 分类表 前端控制器
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
@RequestMapping("api/admin/platform/category")
@Api(tags = "平台端基础分类服务")
public class PlatformBaseCategoryController {

    @Autowired
    private CategoryService categoryService;

    @PreAuthorize("hasAuthority('platform:category:list')")
    @ApiOperation(value = "基础分类分页列表")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public CommonResult<CommonPage<Category>> getList(@ModelAttribute CategorySearchRequest request, @ModelAttribute PageParamRequest pageParamRequest) {
        CommonPage<Category> categoryCommonPage = CommonPage.restPage(categoryService.getList(request, pageParamRequest));
        return CommonResult.success(categoryCommonPage);
    }

    @LogControllerAnnotation(intoDB = true, methodType = MethodType.ADD, description = "新增基础分类")
    @PreAuthorize("hasAuthority('platform:category:save')")
    @ApiOperation(value = "新增基础分类")
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    public CommonResult<String> save(@RequestBody @Validated CategoryRequest categoryRequest) {
        if (categoryService.create(categoryRequest)) {
            return CommonResult.success();
        }
        return CommonResult.failed();
    }

    @LogControllerAnnotation(intoDB = true, methodType = MethodType.DELETE, description = "删除基础分类")
    @PreAuthorize("hasAuthority('platform:category:delete')")
    @ApiOperation(value = "删除基础分类")
    @RequestMapping(value = "/delete/{id}", method = RequestMethod.POST)
    public CommonResult<String> delete(@PathVariable(value = "id") Integer id) {
        if (categoryService.delete(id) > 0) {
            return CommonResult.success();
        }
        return CommonResult.failed();
    }

    @LogControllerAnnotation(intoDB = true, methodType = MethodType.UPDATE, description = "修改基础分类")
    @PreAuthorize("hasAuthority('platform:category:update')")
    @ApiOperation(value = "修改基础分类")
    @RequestMapping(value = "/update/{id}", method = RequestMethod.POST)
    public CommonResult<String> update(@PathVariable(value = "id") Integer id, @RequestBody @Validated CategoryRequest categoryRequest) {
        if (categoryService.update(categoryRequest, id)) {
            return CommonResult.success();
        }
        return CommonResult.failed();
    }

    @PreAuthorize("hasAuthority('platform:category:info')")
    @ApiOperation(value = "基础分类详情")
    @RequestMapping(value = "/info/{id}", method = RequestMethod.GET)
    public CommonResult<Category> info(@PathVariable(value = "id") Integer id) {
        return CommonResult.success(categoryService.getByIdException(id));
    }


    @PreAuthorize("hasAuthority('platform:category:list:tree')")
    @ApiOperation(value = "获取基础分类tree结构的列表")
    @RequestMapping(value = "/list/tree", method = RequestMethod.GET)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "type", value = "类型ID | 类型，2  附件分类， 4 设置分类， 6 配置分类", example = "1"),
            @ApiImplicitParam(name = "status", value = "-1=全部，0=未生效，1=已生效", example = "1"),
            @ApiImplicitParam(name = "name", value = "模糊搜索", example = "电视")
    })
    public CommonResult<List<CategoryTreeVo>> getListTree(@RequestParam(name = "type") Integer type,
                                                          @RequestParam(name = "status") Integer status,
                                                          @RequestParam(name = "name", required = false) String name) {
        List<CategoryTreeVo> listTree = categoryService.getListTree(type, status, name);
        return CommonResult.success(listTree);
    }

    @PreAuthorize("hasAuthority('platform:category:list:ids')")
    @ApiOperation(value = "根据id集合获取基础分类列表")
    @RequestMapping(value = "/list/ids", method = RequestMethod.GET)
    @ApiImplicitParam(name = "ids", value = "分类id集合")
    public CommonResult<List<Category>> getByIds(@Validated @RequestParam(name = "ids") String ids) {
        return CommonResult.success(categoryService.getByIds(CrmebUtil.stringToArray(ids)));
    }

    @PreAuthorize("hasAuthority('platform:category:update:status')")
    @ApiOperation(value = "更改基础分类状态")
    @RequestMapping(value = "/updateStatus/{id}", method = RequestMethod.POST)
    public CommonResult<Object> getByIds(@PathVariable(name = "id") Integer id) {
        if (categoryService.updateStatus(id)) {
            return CommonResult.success("修改成功");
        }
        return CommonResult.failed("修改失败");
    }
}



