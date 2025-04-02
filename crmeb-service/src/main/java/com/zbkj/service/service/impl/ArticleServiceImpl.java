package com.zbkj.service.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.URLUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zbkj.common.constants.SysConfigConstants;
import com.zbkj.common.exception.CrmebException;
import com.zbkj.common.model.article.Article;
import com.zbkj.common.page.CommonPage;
import com.zbkj.common.request.ArticleRequest;
import com.zbkj.common.request.ArticleSearchRequest;
import com.zbkj.common.request.PageParamRequest;
import com.zbkj.common.response.ArticleInfoResponse;
import com.zbkj.common.response.ArticleResponse;
import com.zbkj.common.result.CommonResultCode;
import com.zbkj.common.result.MarketingResultCode;
import com.zbkj.service.dao.ArticleDao;
import com.zbkj.service.service.ArticleService;
import com.zbkj.service.service.SystemAttachmentService;
import com.zbkj.service.service.SystemConfigService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * ArticleServiceImpl 接口实现
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
@Service
public class ArticleServiceImpl extends ServiceImpl<ArticleDao, Article> implements ArticleService {

    private final Logger logger = LoggerFactory.getLogger(ArticleServiceImpl.class);

    @Resource
    private ArticleDao dao;

    @Autowired
    private SystemConfigService systemConfigService;

    @Autowired
    private SystemAttachmentService systemAttachmentService;

    /**
     * 列表
     *
     * @param cid              文章分类id
     * @param pageParamRequest 分页类参数
     * @return PageInfo<Article>
     */
    @Override
    public PageInfo<ArticleResponse> getList(Integer cid, PageParamRequest pageParamRequest) {
        Page<Article> articlePage = PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());

        LambdaQueryWrapper<Article> lqw = Wrappers.lambdaQuery();
        if (ObjectUtil.isNotNull(cid) && cid > 0) {
            lqw.eq(Article::getCid, cid);
        }
        lqw.eq(Article::getStatus, true);
        lqw.eq(Article::getIsDel, false);
        lqw.orderByDesc(Article::getSort, Article::getId).orderByDesc(Article::getVisit).orderByDesc(Article::getCreateTime);
        List<Article> articleList = dao.selectList(lqw);
        if (CollUtil.isEmpty(articleList)) {
            return CommonPage.copyPageInfo(articlePage, CollUtil.newArrayList());
        }
        List<ArticleResponse> responseList = articleList.stream().map(e -> {
            ArticleResponse articleResponse = new ArticleResponse();
            BeanUtils.copyProperties(e, articleResponse);
            return articleResponse;
        }).collect(Collectors.toList());
        return CommonPage.copyPageInfo(articlePage, responseList);
    }

    /**
     * 获取文章列表
     *
     * @param request          请求参数
     * @param pageParamRequest 分页参数
     * @return PageInfo
     */
    @Override
    public PageInfo<ArticleResponse> getAdminList(ArticleSearchRequest request, PageParamRequest pageParamRequest) {
        Page<Article> articlePage = PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());

        LambdaQueryWrapper<Article> lqw = Wrappers.lambdaQuery();
        if (ObjectUtil.isNotNull(request.getCid())) {
            lqw.eq(Article::getCid, request.getCid());
        }
        if (StrUtil.isNotBlank(request.getTitle())) {
            String title = URLUtil.decode(request.getTitle());
            lqw.like(Article::getTitle, title);
        }
        if (StrUtil.isNotBlank(request.getAuthor())) {
            lqw.eq(Article::getAuthor, URLUtil.decode(request.getAuthor()));
        }
        lqw.eq(Article::getIsDel, false);
        lqw.orderByDesc(Article::getSort).orderByDesc(Article::getId);
        List<Article> articleList = dao.selectList(lqw);

        List<ArticleResponse> responseList = new ArrayList<>();
        if (CollUtil.isEmpty(articleList)) {
            return CommonPage.copyPageInfo(articlePage, responseList);
        }
        for (Article article : articleList) {
            ArticleResponse response = new ArticleResponse();
            BeanUtils.copyProperties(article, response);
            responseList.add(response);
        }
        return CommonPage.copyPageInfo(articlePage, responseList);
    }

    /**
     * 查询文章详情
     *
     * @param id Integer
     * @return ArticleVo
     */
    @Override
    public ArticleInfoResponse getVoByFront(Integer id) {
        Article article = getById(id);
        if (ObjectUtil.isNull(article) || article.getIsDel()) {
            throw new CrmebException(MarketingResultCode.ARTICLE_NOT_EXIST);
        }
        if (!article.getStatus()) {
            throw new CrmebException(MarketingResultCode.ARTICLE_NOT_EXIST);
        }
        ArticleInfoResponse articleResponse = new ArticleInfoResponse();
        BeanUtils.copyProperties(article, articleResponse);
        if (addArticleVisit(id)) {
            logger.error("增加文章阅读次数失败，文章id = {}", id);
        }
        return articleResponse;
    }

    /**
     * 增加文章浏览次数
     *
     * @param id 文章id
     */
    private Boolean addArticleVisit(Integer id) {
        UpdateWrapper<Article> wrapper = Wrappers.update();
        wrapper.setSql("visit = visit + 1");
        wrapper.eq("id", id);
        return update(wrapper);
    }

    /**
     * 获取移动端banner列表
     *
     * @return List<Article>
     */
    @Override
    public List<Article> getBannerList() {
        int articleBannerLimit = 5;
        // 根据配置控制banner的数量
        String articleBannerLimitString = systemConfigService.getValueByKey(SysConfigConstants.ARTICLE_BANNER_LIMIT);
        if (StrUtil.isNotBlank(articleBannerLimitString)) {
            articleBannerLimit = Integer.parseInt(articleBannerLimitString);
        }
        LambdaQueryWrapper<Article> lqw = Wrappers.lambdaQuery();
        lqw.select(Article::getId, Article::getCover, Article::getTitle);
        lqw.eq(Article::getIsBanner, true);
        lqw.eq(Article::getStatus, true);
        lqw.eq(Article::getIsDel, false);
        lqw.orderByDesc(Article::getSort, Article::getId);
        lqw.last(" limit " + articleBannerLimit);
        return dao.selectList(lqw);
    }

    /**
     * 获取移动端热门列表
     *
     * @return List<ArticleResponse>
     */
    @Override
    public List<ArticleResponse> getHotList() {
        LambdaQueryWrapper<Article> lqw = Wrappers.lambdaQuery();
        lqw.select(Article::getId, Article::getCover, Article::getTitle, Article::getCreateTime);
        lqw.eq(Article::getIsHot, true);
        lqw.eq(Article::getStatus, true);
        lqw.eq(Article::getIsDel, false);
        lqw.orderByDesc(Article::getSort, Article::getId);
        lqw.last(" limit 10");
        List<Article> articleList = dao.selectList(lqw);
        if (CollUtil.isEmpty(articleList)) {
            return CollUtil.newArrayList();
        }
        return articleList.stream().map(e -> {
            ArticleResponse articleResponse = new ArticleResponse();
            BeanUtils.copyProperties(e, articleResponse);
            return articleResponse;
        }).collect(Collectors.toList());
    }

    /**
     * 文章新增
     *
     * @param articleRequest 文章新增参数
     * @return Boolean
     */
    @Override
    public Boolean create(ArticleRequest articleRequest) {
        Article article = new Article();
        BeanUtils.copyProperties(articleRequest, article);
        article.setId(null);
        String cdnUrl = systemAttachmentService.getCdnUrl();
        article.setCover(systemAttachmentService.clearPrefix(article.getCover(), cdnUrl));
        article.setContent(systemAttachmentService.clearPrefix(article.getContent(), cdnUrl));
        article.setVisit(0L);
        return save(article);
    }

    /**
     * 文章删除
     *
     * @param id 文章id
     * @return Boolean
     */
    @Override
    public Boolean deleteById(Integer id) {
        Article article = getByIdException(id);
        article.setIsDel(true);
        return updateById(article);
    }

    private Article getByIdException(Integer id) {
        Article article = getById(id);
        if (ObjectUtil.isNull(article) || article.getIsDel()) {
            throw new CrmebException(MarketingResultCode.ARTICLE_NOT_EXIST);
        }
        return article;
    }


    /**
     * 文章修改
     *
     * @param articleRequest 文章修改参数
     */
    @Override
    public Boolean updateArticle(ArticleRequest articleRequest) {
        if (ObjectUtil.isNull(articleRequest.getId())) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "文章ID不能为空");
        }
        getByIdException(articleRequest.getId());
        Article article = new Article();
        BeanUtils.copyProperties(articleRequest, article);
        String cdnUrl = systemAttachmentService.getCdnUrl();
        article.setCover(systemAttachmentService.clearPrefix(article.getCover(), cdnUrl));
        article.setContent(systemAttachmentService.clearPrefix(article.getContent(), cdnUrl));
        return updateById(article);
    }

    /**
     * 获取文章详情
     *
     * @param id 文章id
     * @return Article
     */
    @Override
    public ArticleInfoResponse getDetail(Integer id) {
        Article article = getByIdException(id);
        ArticleInfoResponse response = new ArticleInfoResponse();
        BeanUtils.copyProperties(article, response);
        return response;
    }

    /**
     * 文章是否使用分类
     *
     * @param categoryId 分类ID
     * @return Boolean
     */
    @Override
    public Boolean isUseCategory(Integer categoryId) {
        LambdaQueryWrapper<Article> lqw = Wrappers.lambdaQuery();
        lqw.eq(Article::getCid, categoryId);
        lqw.eq(Article::getIsDel, false);
        lqw.eq(Article::getIsDel, false);
        lqw.last(" limit 1");
        Article article = dao.selectOne(lqw);
        return ObjectUtil.isNotNull(article);
    }

    /**
     * 获取首页新闻头条
     */
    @Override
    public List<Article> getIndexHeadline() {
        LambdaQueryWrapper<Article> lqw = Wrappers.lambdaQuery();
        lqw.select(Article::getId, Article::getTitle);
        lqw.eq(Article::getIsHot, true);
        lqw.eq(Article::getStatus, true);
        lqw.eq(Article::getIsDel, false);
        lqw.orderByDesc(Article::getSort);
        lqw.last(" limit 10");
        return dao.selectList(lqw);
    }

    /**
     * 文章开关
     * @param id 文章id
     * @return Boolean
     */
    @Override
    public Boolean articleSwitch(Integer id) {
        Article article = getByIdException(id);
        article.setStatus(!article.getStatus());
        return updateById(article);
    }
}

