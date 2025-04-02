package com.zbkj.service.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zbkj.common.constants.CommunityConstants;
import com.zbkj.common.model.community.CommunityNotesRelation;
import com.zbkj.common.page.CommonPage;
import com.zbkj.common.request.PageParamRequest;
import com.zbkj.service.dao.community.CommunityNotesRelationDao;
import com.zbkj.service.service.CommunityNotesRelationService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
* CommunityNotesRelation 接口实现
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
public class CommunityNotesRelationServiceImpl extends ServiceImpl<CommunityNotesRelationDao, CommunityNotesRelation> implements CommunityNotesRelationService {

    @Resource
    private CommunityNotesRelationDao dao;

    /**
     * 用户是否点赞
     * @param noteId 笔记ID
     * @param userId 用户ID
     * @return 是否点赞
     */
    @Override
    public Boolean isLikeByUid(Integer noteId, Integer userId) {
        LambdaQueryWrapper<CommunityNotesRelation> lqw = Wrappers.lambdaQuery();
        lqw.select(CommunityNotesRelation::getId);
        lqw.eq(CommunityNotesRelation::getNoteId, noteId);
        lqw.eq(CommunityNotesRelation::getType, CommunityConstants.COMMUNITY_NOTE_RELATION_LIKE);
        lqw.eq(CommunityNotesRelation::getUid, userId);
        CommunityNotesRelation relation = dao.selectOne(lqw);
        return ObjectUtil.isNotNull(relation) ? Boolean.TRUE : Boolean.FALSE;
    }

    /**
     * 获取作者获赞数
     * @param authorId 作者ID
     */
    @Override
    public Integer getCountLikeByAuthorId(Integer authorId) {
        LambdaQueryWrapper<CommunityNotesRelation> lqw = Wrappers.lambdaQuery();
        lqw.select(CommunityNotesRelation::getId);
        lqw.eq(CommunityNotesRelation::getAuthorId, authorId);
        lqw.eq(CommunityNotesRelation::getType, CommunityConstants.COMMUNITY_NOTE_RELATION_LIKE);
        return dao.selectCount(lqw);
    }

    /**
     * 获取点赞详情
     * @param noteId 笔记ID
     * @param userId 用户ID
     */
    @Override
    public CommunityNotesRelation getOneByNoteIdAndUid(Integer noteId, Integer userId) {
        LambdaQueryWrapper<CommunityNotesRelation> lqw = Wrappers.lambdaQuery();
        lqw.eq(CommunityNotesRelation::getNoteId, noteId);
        lqw.eq(CommunityNotesRelation::getUid, userId);
        lqw.eq(CommunityNotesRelation::getType, CommunityConstants.COMMUNITY_NOTE_RELATION_LIKE);
        lqw.last(" limit 1");
        return dao.selectOne(lqw);
    }

    /**
     * 删除社区笔记关联关系
     */
    @Override
    public Boolean deleteByNoteId(Integer noteId) {
        LambdaUpdateWrapper<CommunityNotesRelation> wrapper = Wrappers.lambdaUpdate();
        wrapper.eq(CommunityNotesRelation::getNoteId, noteId);
        return remove(wrapper);
    }

    /**
     * 获取用户点赞分页列表
     * @param userId 用户ID
     * @param request 分页参数
     */
    @Override
    public PageInfo<CommunityNotesRelation> findLikePageByUid(Integer userId, PageParamRequest request) {
        Page<CommunityNotesRelation> page = PageHelper.startPage(request.getPage(), request.getLimit());
        LambdaQueryWrapper<CommunityNotesRelation> lqw = Wrappers.lambdaQuery();
        lqw.eq(CommunityNotesRelation::getUid, userId);
        lqw.eq(CommunityNotesRelation::getType, CommunityConstants.COMMUNITY_NOTE_RELATION_LIKE);
        lqw.orderByDesc(CommunityNotesRelation::getId);
        List<CommunityNotesRelation> list = dao.selectList(lqw);
        return CommonPage.copyPageInfo(page, list);
    }

    /**
     * 通过作者ID删除
     * @param authorId 作责ID
     */
    @Override
    public Boolean deleteByAuthorId(Integer authorId) {
        LambdaUpdateWrapper<CommunityNotesRelation> wrapper = Wrappers.lambdaUpdate();
        wrapper.eq(CommunityNotesRelation::getAuthorId, authorId);
        return remove(wrapper);
    }
}

