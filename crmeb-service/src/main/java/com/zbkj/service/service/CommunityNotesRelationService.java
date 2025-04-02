package com.zbkj.service.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.github.pagehelper.PageInfo;
import com.zbkj.common.model.community.CommunityNotesRelation;
import com.zbkj.common.request.PageParamRequest;

/**
* CommunityNotesRelation 接口
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
public interface CommunityNotesRelationService extends IService<CommunityNotesRelation> {

    /**
     * 用户是否点赞
     * @param noteId 笔记ID
     * @param userId 用户ID
     * @return 是否点赞
     */
    Boolean isLikeByUid(Integer noteId, Integer userId);

    /**
     * 获取作者获赞数
     * @param authorId 作者ID
     */
    Integer getCountLikeByAuthorId(Integer authorId);

    /**
     * 获取点赞详情
     * @param noteId 笔记ID
     * @param userId 用户ID
     */
    CommunityNotesRelation getOneByNoteIdAndUid(Integer noteId, Integer userId);

    /**
     * 删除社区笔记关联关系
     */
    Boolean deleteByNoteId(Integer noteId);

    /**
     * 获取用户点赞分页列表
     * @param userId 用户ID
     * @param request 分页参数
     */
    PageInfo<CommunityNotesRelation> findLikePageByUid(Integer userId, PageParamRequest request);

    /**
     * 通过作者ID删除
     * @param authorId 作责ID
     */
    Boolean deleteByAuthorId(Integer authorId);
}