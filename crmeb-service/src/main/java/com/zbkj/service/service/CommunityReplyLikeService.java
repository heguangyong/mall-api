package com.zbkj.service.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zbkj.common.model.community.CommunityReplyLike;

import java.util.List;

/**
* CommunityReplyLike 接口
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
public interface CommunityReplyLikeService extends IService<CommunityReplyLike> {

    /**
     * 获取点赞列表，通过笔记ID和用户ID
     * @param noteId 笔记ID
     * @param userId 用户ID
     */
    List<CommunityReplyLike> findListByNoteIdAndUid(Integer noteId, Integer userId);

    /**
     * 获取点赞详情
     * @param replyId 评论ID
     * @param userId 用户ID
     */
    CommunityReplyLike getDetail(Integer replyId, Integer userId);
}