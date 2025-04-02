package com.zbkj.admin.controller.platform;

import com.zbkj.common.annotation.LogControllerAnnotation;
import com.zbkj.common.enums.MethodType;
import com.zbkj.common.page.CommonPage;
import com.zbkj.common.request.ArticleRequest;
import com.zbkj.common.request.ArticleSearchRequest;
import com.zbkj.common.request.PageParamRequest;
import com.zbkj.common.response.ArticleInfoResponse;
import com.zbkj.common.response.ArticleResponse;
import com.zbkj.common.result.CommonResult;
import com.zbkj.service.service.ArticleService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


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
@RequestMapping("api/admin/platform/article")
@Api(tags = "文章管理")
public class ArticleController {

    @Autowired
    private ArticleService articleService;

    @PreAuthorize("hasAuthority('platform:article:list')")
    @ApiOperation(value = "文章分页列表")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "cid", value = "分类id", dataType = "int"),
            @ApiImplicitParam(name = "title", value = "标题", dataType = "string"),
            @ApiImplicitParam(name = "author", value = "作者", dataType = "string")
    })
    public CommonResult<CommonPage<ArticleResponse>> getList(@Validated ArticleSearchRequest request,
                                                             @Validated PageParamRequest pageParamRequest) {
        return CommonResult.success(CommonPage.restPage(articleService.getAdminList(request, pageParamRequest)));
    }

    @LogControllerAnnotation(intoDB = true, methodType = MethodType.ADD, description = "文章新增")
    @PreAuthorize("hasAuthority('platform:article:save')")
    @ApiOperation(value = "文章新增")
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    public CommonResult<String> save(@RequestBody @Validated ArticleRequest articleRequest) {
        if (articleService.create(articleRequest)) {
            return CommonResult.success();
        }
        return CommonResult.failed();
    }

    @LogControllerAnnotation(intoDB = true, methodType = MethodType.DELETE, description = "删除文章")
    @PreAuthorize("hasAuthority('platform:article:delete')")
    @ApiOperation(value = "删除文章")
    @RequestMapping(value = "/delete/{id}", method = RequestMethod.POST)
    public CommonResult<String> delete(@PathVariable(value = "id") Integer id) {
        if (articleService.deleteById(id)) {
            return CommonResult.success();
        }
        return CommonResult.failed();
    }

    @LogControllerAnnotation(intoDB = true, methodType = MethodType.UPDATE, description = "文章编辑")
    @PreAuthorize("hasAuthority('platform:article:update')")
    @ApiOperation(value = "文章编辑")
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public CommonResult<String> update(@RequestBody @Validated ArticleRequest articleRequest) {
        if (articleService.updateArticle(articleRequest)) {
            return CommonResult.success();
        }
        return CommonResult.failed();
    }

    @PreAuthorize("hasAuthority('platform:article:info')")
    @ApiOperation(value = "文章详情")
    @RequestMapping(value = "/info/{id}", method = RequestMethod.GET)
    public CommonResult<ArticleInfoResponse> info(@PathVariable(value = "id") Integer id) {
        return CommonResult.success(articleService.getDetail(id));
    }

    @LogControllerAnnotation(intoDB = true, methodType = MethodType.UPDATE, description = "文章开关")
    @PreAuthorize("hasAuthority('platform:article:switch')")
    @ApiOperation(value = "文章开关")
    @RequestMapping(value = "/switch/{id}", method = RequestMethod.POST)
    public CommonResult<String> articleSwitch(@PathVariable("id") Integer id) {
        if (articleService.articleSwitch(id)) {
            return CommonResult.success("切换文章开关成功");
        }
        return CommonResult.failed("切换文章开关失败");
    }
}



