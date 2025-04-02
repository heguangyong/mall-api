package com.zbkj.service.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.github.pagehelper.PageInfo;
import com.zbkj.common.model.article.Article;
import com.zbkj.common.request.ArticleRequest;
import com.zbkj.common.request.ArticleSearchRequest;
import com.zbkj.common.request.PageParamRequest;
import com.zbkj.common.response.ArticleInfoResponse;
import com.zbkj.common.response.ArticleResponse;

import java.util.List;

/**
 * ArticleService 接口
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
public interface ArticleService extends IService<Article> {

    /**
     * 文章列表
     *
     * @param cid              文章分类id
     * @param pageParamRequest 分页类参数
     * @return PageInfo<Article>
     */
    PageInfo<ArticleResponse> getList(Integer cid, PageParamRequest pageParamRequest);

    /**
     * 获取文章列表
     *
     * @param request          请求参数
     * @param pageParamRequest 分页参数
     * @return PageInfo
     */
    PageInfo<ArticleResponse> getAdminList(ArticleSearchRequest request, PageParamRequest pageParamRequest);

    /**
     * 文章详情
     *
     * @param id 文章id
     * @return ArticleVo
     */
    ArticleInfoResponse getVoByFront(Integer id);

    /**
     * 获取移动端banner列表
     *
     * @return List<Article>
     */
    List<Article> getBannerList();

    /**
     * 获取移动端热门列表
     *
     * @return List<ArticleResponse>
     */
    List<ArticleResponse> getHotList();

    /**
     * 文章新增
     *
     * @param articleRequest 文章新增参数
     * @return Boolean
     */
    Boolean create(ArticleRequest articleRequest);

    /**
     * 文章删除
     *
     * @param id 文章id
     * @return Boolean
     */
    Boolean deleteById(Integer id);

    /**
     * 文章修改
     *
     * @param articleRequest 文章修改参数
     */
    Boolean updateArticle(ArticleRequest articleRequest);

    /**
     * 获取文章详情
     *
     * @param id 文章id
     * @return Article
     */
    ArticleInfoResponse getDetail(Integer id);

    /**
     * 文章是否使用分类
     *
     * @param categoryId 分类ID
     * @return Boolean
     */
    Boolean isUseCategory(Integer categoryId);

    /**
     * 获取首页新闻头条
     */
    List<Article> getIndexHeadline();

    /**
     * 文章开关
     * @param id 文章id
     * @return Boolean
     */
    Boolean articleSwitch(Integer id);
}
