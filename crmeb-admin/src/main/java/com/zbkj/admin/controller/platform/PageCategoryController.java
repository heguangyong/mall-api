package com.zbkj.admin.controller.platform;


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
 * 页面链接分类 前端控制器
 */
@Slf4j
@RestController
@RequestMapping("api/admin/platform/pagecategory")
@Api(tags = "DIY 页面链接分类 控制器") //配合swagger使用

public class PageCategoryController {

    @Autowired
    private PageCategoryService pageCategoryService;

    /**
     * 分页显示页面链接分类
     * @param request 搜索条件
     * @param pageParamRequest 分页参数
     * @author dazongzi
     * @since 2023-05-16
     */
    @PreAuthorize("hasAuthority('platform:pagecategory:list')")
    @ApiOperation(value = "分页列表") //配合swagger使用
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public CommonResult<CommonPage<PageCategory>>  getList(@Validated PageCategorySearchRequest request, @Validated PageParamRequest pageParamRequest){

        CommonPage<PageCategory> pageCategoryCommonPage = CommonPage.restPage(pageCategoryService.getList(request, pageParamRequest));
        return CommonResult.success(pageCategoryCommonPage);
    }

    /**
     * 新增页面链接分类
     * @param pageCategoryRequest 新增参数
     * @author dazongzi
     * @since 2023-05-16
     */
    @PreAuthorize("hasAuthority('platform:pagecategory:save')")
    @ApiOperation(value = "新增")
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

    /**
     * 删除页面链接分类
     * @param id Integer
     * @author dazongzi
     * @since 2023-05-16
     */
    @PreAuthorize("hasAuthority('platform:pagecategory:delete')")
    @ApiOperation(value = "删除")
    @RequestMapping(value = "/delete", method = RequestMethod.GET)
    public CommonResult<String> delete(@RequestParam(value = "id") Integer id){
        if(pageCategoryService.removeById(id)){
            return CommonResult.success();
        }else{
            return CommonResult.failed();
        }
    }

    /**
     * 修改页面链接分类
     * @param pageCategoryRequest 修改参数
     * @author dazongzi
     * @since 2023-05-16
     */
    @PreAuthorize("hasAuthority('platform:pagecategory:update')")
    @ApiOperation(value = "修改")
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

    /**
     * 查询页面链接分类信息
     * @param id Integer
     * @author dazongzi
     * @since 2023-05-16
     */
    @PreAuthorize("hasAuthority('platform:pagecategory:info')")
    @ApiOperation(value = "详情")
    @RequestMapping(value = "/info", method = RequestMethod.GET)
    public CommonResult<PageCategory> info(@RequestParam(value = "id") Integer id){
        PageCategory pageCategory = pageCategoryService.getById(id);
        return CommonResult.success(pageCategory);
   }
}



