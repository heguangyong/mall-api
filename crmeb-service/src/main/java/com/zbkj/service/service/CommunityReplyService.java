package com.zbkj.service.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.github.pagehelper.PageInfo;
import com.zbkj.common.model.community.CommunityReply;
import com.zbkj.common.request.CommonAuditRequest;
import com.zbkj.common.request.CommunityReplyAddRequest;
import com.zbkj.common.request.CommunityReplySearchRequest;
import com.zbkj.common.request.PageParamRequest;
import com.zbkj.common.response.CommunityCommentReplyResponse;
import com.zbkj.common.response.CommunityReplyPageDateResponse;

/**
* CommunityReply 接口
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
public interface CommunityReplyService extends IService<CommunityReply> {

    /**
     * 获取笔记评论数（评论+回复）
     */
    Integer getCountByNid(Integer nid);

    /**
     * 社区评论分页列表
     * @param request 搜索参数
     * @return PageInfo
     */
    PageInfo<CommunityReplyPageDateResponse> findPageList(CommunityReplySearchRequest request);

    /**
     * 社区评论审核
     */
    void audit(CommonAuditRequest request);

    /**
     * 社区评论删除
     */
    void delete(Integer id);

    /**
     * 社区评论-文章分页列表
     * @param noteId 文章ID
     * @param pageRequest 分页参数
     * @return PageInfo
     */
    PageInfo<CommunityCommentReplyResponse> findNotePageList(Integer noteId, PageParamRequest pageRequest);

    /**
     * 评论点赞/取消
     * @param replyId 评论ID
     * @param userId 用户ID
     */
    void likeReply(Integer replyId, Integer userId);

    /**
     * 社区笔记添加评论
     */
    CommunityCommentReplyResponse create(CommunityReplyAddRequest request);

    /**
     * 社区删除笔记评论
     */
    void deleteByNoteId(Integer noteId);

    /**
     * 社区笔记评论点赞与取消
     * @param replyId 评论ID
     * @param operationType 操作类型：add-点赞，sub-取消
     */
    void operationLike(Integer replyId, String operationType);

    /**
     * 操作社区笔记评论的评论数量
     * @param replyId 评论ID
     * @param num 评论数量
     * @param operationType 操作类型
     */
    void operationReplyNum(Integer replyId, Integer num, String operationType);
}