package com.zbkj.admin.controller.merchant;

import com.zbkj.common.model.page.PageCategory;
import com.zbkj.common.page.CommonPage;
import com.zbkj.common.request.PageParamRequest;
import com.zbkj.common.request.page.PageCategoryRequest;
import com.zbkj.common.request.page.PageCategorySearchRequest;
import com.zbkj.common.result.CommonResult;
import com.zbkj.service.service.PageCategoryService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 类的详细说明
 *
 * @author Han
 * @version 1.0.0
 * @Date 2024/10/28
 */
@Slf4j
@RestController
@RequestMapping("api/admin/merchant/page/category")
@Api(tags = "商户装修页面链接分类控制器") //配合swagger使用
public class MerchantPageCategoryController {

    @Autowired
    private PageCategoryService pageCategoryService;

    @PreAuthorize("hasAuthority('platform:pagecategory:list')")
    @ApiOperation(value = "页面链接分类分页列表") //配合swagger使用
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public CommonResult<CommonPage<PageCategory>> getList(@Validated PageCategorySearchRequest request, @Validated PageParamRequest pageParamRequest){

        CommonPage<PageCategory> pageCategoryCommonPage = CommonPage.restPage(pageCategoryService.getList(request, pageParamRequest));
        return CommonResult.success(pageCategoryCommonPage);
    }

    @PreAuthorize("hasAuthority('platform:pagecategory:save')")
    @ApiOperation(value = "新增页面链接分类")
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    public CommonResult<String> save(@RequestBody @Validated PageCategoryRequest pageCategoryRequest){
        PageCategory pageCategory = new PageCategory();
        BeanUtils.copyProperties(pageCategoryRequest, pageCategory);

        if(pageCategoryService.save(pageCategory)){
            return CommonResult.success();
        }else{
            return CommonResult.failed();
        }
    }

    @PreAuthorize("hasAuthority('platform:pagecategory:delete')")
    @ApiOperation(value = "删除页面链接分类")
    @RequestMapping(value = "/delete", method = RequestMethod.GET)
    public CommonResult<String> delete(@RequestParam(value = "id") Integer id){
        if(pageCategoryService.removeById(id)){
            return CommonResult.success();
        }else{
            return CommonResult.failed();
        }
    }

    @PreAuthorize("hasAuthority('platform:pagecategory:update')")
    @ApiOperation(value = "修改页面链接分类")
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public CommonResult<String> update(@RequestBody @Validated PageCategoryRequest pageCategoryRequest){
        PageCategory pageCategory = new PageCategory();
        BeanUtils.copyProperties(pageCategoryRequest, pageCategory);

        if(pageCategoryService.updateById(pageCategory)){
            return CommonResult.success();
        }else{
            return CommonResult.failed();
        }
    }

    @PreAuthorize("hasAuthority('platform:pagecategory:info')")
    @ApiOperation(value = "查询页面链接分类信息")
    @RequestMapping(value = "/info", method = RequestMethod.GET)
    public CommonResult<PageCategory> info(@RequestParam(value = "id") Integer id){
        PageCategory pageCategory = pageCategoryService.getById(id);
        return CommonResult.success(pageCategory);
    }

}
