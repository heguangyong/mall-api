package com.zbkj.front.controller;

import com.zbkj.common.model.article.Article;
import com.zbkj.common.page.CommonPage;
import com.zbkj.common.request.PageParamRequest;
import com.zbkj.common.response.ArticleCategoryResponse;
import com.zbkj.common.response.ArticleInfoResponse;
import com.zbkj.common.response.ArticleResponse;
import com.zbkj.common.result.CommonResult;
import com.zbkj.service.service.ArticleCategoryService;
import com.zbkj.service.service.ArticleService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


/**
 * 文章控制器
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
@RequestMapping("api/front/article")
@Api(tags = "文章")
public class ArticleController {

    @Autowired
    private ArticleService articleService;
    @Autowired
    private ArticleCategoryService articleCategoryService;

    @ApiOperation(value = "文章分类列表")
    @RequestMapping(value = "/category/list", method = RequestMethod.GET)
    public CommonResult<List<ArticleCategoryResponse>> getCategoryList() {
        return CommonResult.success(articleCategoryService.getFrontList());
    }

    @ApiOperation(value = "文章分页列表")
    @RequestMapping(value = "/list/{cid}", method = RequestMethod.GET)
    public CommonResult<CommonPage<ArticleResponse>> getList(@PathVariable(name = "cid") Integer cid,
                                                             @Validated PageParamRequest pageParamRequest) {
        return CommonResult.success(CommonPage.restPage(articleService.getList(cid, pageParamRequest)));
    }

    @ApiOperation(value = "热门列表")
    @RequestMapping(value = "/hot/list", method = RequestMethod.GET)
    public CommonResult<List<ArticleResponse>> getHotList() {
        return CommonResult.success(articleService.getHotList());
    }

    @ApiOperation(value = "轮播列表")
    @RequestMapping(value = "/banner/list", method = RequestMethod.GET)
    public CommonResult<List<Article>> getList() {
        return CommonResult.success(articleService.getBannerList());
    }

    @ApiOperation(value = "详情")
    @RequestMapping(value = "/info/{id}", method = RequestMethod.GET)
    public CommonResult<ArticleInfoResponse> info(@PathVariable(value = "id") Integer id) {
        return CommonResult.success(articleService.getVoByFront(id));
    }
}



