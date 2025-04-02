package com.zbkj.service.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zbkj.common.exception.CrmebException;
import com.zbkj.common.model.community.CommunityAuthorConcerned;
import com.zbkj.common.page.CommonPage;
import com.zbkj.common.request.PageParamRequest;
import com.zbkj.common.result.CommonResultCode;
import com.zbkj.service.dao.community.CommunityAuthorConcernedDao;
import com.zbkj.service.service.CommunityAuthorConcernedService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
* CommunityAuthorConcerned 接口实现
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
public class CommunityAuthorConcernedServiceImpl extends ServiceImpl<CommunityAuthorConcernedDao, CommunityAuthorConcerned> implements CommunityAuthorConcernedService {

    @Resource
    private CommunityAuthorConcernedDao dao;

    /**
     * 获取关注的作者ID列表
     * @param userId 用户ID
     */
    @Override
    public List<Integer> findAuthorIdList(Integer userId) {
        LambdaQueryWrapper<CommunityAuthorConcerned> lqw = Wrappers.lambdaQuery();
        lqw.select(CommunityAuthorConcerned::getAuthorId);
        lqw.eq(CommunityAuthorConcerned::getUid, userId);
        List<CommunityAuthorConcerned> list = dao.selectList(lqw);
        if (CollUtil.isEmpty(list)) {
            return new ArrayList<>();
        }
        return list.stream().map(CommunityAuthorConcerned::getAuthorId).collect(Collectors.toList());
    }

    /**
     * 是否关注作者
     * @param authorId 作者ID
     * @param userId 用户ID
     */
    @Override
    public Boolean isConcernedByUid(Integer authorId, Integer userId) {
        LambdaQueryWrapper<CommunityAuthorConcerned> lqw = Wrappers.lambdaQuery();
        lqw.select(CommunityAuthorConcerned::getId);
        lqw.eq(CommunityAuthorConcerned::getAuthorId, authorId);
        lqw.eq(CommunityAuthorConcerned::getUid, userId);
        CommunityAuthorConcerned concerned = dao.selectOne(lqw);
        return ObjectUtil.isNotNull(concerned) ? Boolean.TRUE : Boolean.FALSE;
    }

    /**
     * 获取作者被关注数（粉丝数）
     * @param authorId 作者ID
     */
    @Override
    public Integer getCountByAuthorId(Integer authorId) {
        LambdaQueryWrapper<CommunityAuthorConcerned> lqw = Wrappers.lambdaQuery();
        lqw.select(CommunityAuthorConcerned::getId);
        lqw.eq(CommunityAuthorConcerned::getAuthorId, authorId);
        return dao.selectCount(lqw);
    }

    /**
     * 获取用户关注数
     * @param userId 用户ID
     */
    @Override
    public Integer getCountByUserId(Integer userId) {
        LambdaQueryWrapper<CommunityAuthorConcerned> lqw = Wrappers.lambdaQuery();
        lqw.select(CommunityAuthorConcerned::getId);
        lqw.eq(CommunityAuthorConcerned::getUid, userId);
        return dao.selectCount(lqw);
    }

    /**
     * 获取关注关系列表
     * @param authorId 作者ID
     * @param userId 用户ID
     * @param request 分页参数
     * @return PageInfo
     */
    @Override
    public PageInfo<CommunityAuthorConcerned> findPage(Integer authorId, Integer userId, PageParamRequest request) {
        Page<CommunityAuthorConcerned> page = PageHelper.startPage(request.getPage(), request.getLimit());
        LambdaQueryWrapper<CommunityAuthorConcerned> lqw = Wrappers.lambdaQuery();
        if (authorId > 0) {
            lqw.eq(CommunityAuthorConcerned::getAuthorId, authorId);
        }
        if (userId > 0) {
            lqw.eq(CommunityAuthorConcerned::getUid, userId);
        }
        lqw.orderByDesc(CommunityAuthorConcerned::getId);
        List<CommunityAuthorConcerned> list = dao.selectList(lqw);
        return CommonPage.copyPageInfo(page, list);
    }

    /**
     * 社区关注/取关作者
     * @param authorId 作者ID
     * @param userId 用户ID
     */
    @Override
    public void concernedAuthor(Integer authorId, Integer userId) {
        if (authorId.equals(userId)) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "不支持关注自己");
        }
        CommunityAuthorConcerned concerned = getDetail(authorId, userId);
        if (ObjectUtil.isNotNull(concerned)) {
            boolean remove = removeById(concerned);
            if (!remove) {
                throw new CrmebException(CommonResultCode.ERROR.setMessage("社区取关作者失败"));
            }
            return;
        }
        concerned = new CommunityAuthorConcerned();
        concerned.setAuthorId(authorId);
        concerned.setUid(userId);
        boolean save = save(concerned);
        if (!save) {
            throw new CrmebException(CommonResultCode.ERROR.setMessage("关注作者失败"));
        }
    }

    private CommunityAuthorConcerned getDetail(Integer authorId, Integer userId) {
        LambdaQueryWrapper<CommunityAuthorConcerned> lqw = Wrappers.lambdaQuery();
        lqw.eq(CommunityAuthorConcerned::getAuthorId, authorId);
        lqw.eq(CommunityAuthorConcerned::getUid, userId);
        lqw.last(" limit 1");
        return dao.selectOne(lqw);
    }
}

