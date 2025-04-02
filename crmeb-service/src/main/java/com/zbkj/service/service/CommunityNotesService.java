package com.zbkj.service.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.github.pagehelper.PageInfo;
import com.zbkj.common.model.community.CommunityNotes;
import com.zbkj.common.request.*;
import com.zbkj.common.response.CommunityNoteDetailResponse;
import com.zbkj.common.response.CommunityNotePageDateResponse;

import java.util.List;
import java.util.Map;

/**
* CommunityNotes 接口
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
public interface CommunityNotesService extends IService<CommunityNotes> {

    /**
     * 是否使用社区分类
     * @param cateId 社区分类ID
     */
    Boolean isUseCategory(Integer cateId);

    /**
     * 通过话题ID获取所有笔记
     * @param topicId 话题ID
     */
    List<CommunityNotes> findAllByTopic(Integer topicId);

    /**
     * 更新笔记关联的话题
     * @param id 笔记ID
     * @param topicIds 话题Ids
     */
    void updateTopicIds(Integer id, String topicIds);

    /**
     * 社区笔记分页列表
     * @param request 查询参数
     * @return PageInfo
     */
    PageInfo<CommunityNotePageDateResponse> findPageList(CommunityNoteSearchRequest request);

    /**
     * 社区笔记详情
     * @param id 笔记ID
     */
    CommunityNoteDetailResponse detail(Integer id);

    /**
     * 社区笔记审核
     */
    void audit(CommonAuditRequest request);

    /**
     * 社区笔记强制下架
     */
    void forcedDown(CommonForcedDownRequest request);

    /**
     * 社区笔记删除
     * @param id 笔记ID
     */
    void delete(Integer id);

    /**
     * 社区笔记分类批量修改
     */
    void categoryBatchUpdate(CommunityNoteCategoryBatchUpdateRequest request);

    /**
     * 社区笔记推荐星级编辑
     */
    void updateStar(CommonStarUpdateRequest request);

    /**
     * 社区笔记评论强制关闭开关
     * @param id 笔记ID
     */
    void replyForceOffSwitch(Integer id);

    /**
     * 移动端社区发现笔记分页列表
     * @param request 搜索参数
     * @return PageInfo
     */
    PageInfo<CommunityNotes> findDiscoverNoteList(CommunityNoteFrontDiscoverRequest request);

    /**
     * 移动端社区笔记关注分页列表
     * @param request 分页参数
     * @return PageInfo
     */
    PageInfo<CommunityNotes> findFollowNoteList(PageParamRequest request, Integer userId);

    /**
     * 获取话题关联的笔记数量
     * @param topicId 话题ID
     */
    Integer getCountByTopic(Integer topicId);

    /**
     * 获取话题关联的笔记数量
     * @param topicId 话题ID
     */
    Integer getFrontCountByTopic(Integer topicId);

    /**
     * 社区笔记作者列表
     * @param authorId 作者ID
     * @param request 分页参数
     * @return PageInfo
     */
    PageInfo<CommunityNotes> findAuthorNoteList(Integer authorId, PageParamRequest request);

    /**
     * 获取笔记详情
     * @param id 笔记ID
     */
    CommunityNotes getByIdException(Integer id);

    /**
     * 社区话题笔记列表
     * @param request 搜索参数
     * @return PageInfo
     */
    PageInfo<CommunityNotes> findTopicNoteList(CommunityNoteTopicSearchRequest request);

    /**
     * 创建社区笔记
     */
    void create(CommunityNoteSaveRequest request);

    /**
     * 编辑社区笔记
     */
    void updateNote(CommunityNoteSaveRequest request);

    /**
     * 社区之我的笔记列表
     */
    PageInfo<CommunityNotes> findMyNoteList(Integer userId, PageParamRequest request);

    /**
     * 按时间倒序获取文章作者列表
     */
    PageInfo<Integer> findAuthorPageTimeDesc(Integer userId, PageParamRequest request);

    /**
     * 获取新笔记通过作者
     * @param authorId 作者ID
     * @param num 笔记数量
     */
    List<CommunityNotes> findNewNoteByAuthorId(Integer authorId, Integer num);

    /**
     * 社区笔记点赞数量
     * @param noteId 笔记ID
     * @param operationType 操作类型：add-点赞，sub-取消
     */
    void operationLike(Integer noteId, String operationType);

    /**
     * 操作社区笔记评论数量
     * @param noteId 笔记ID
     * @param num 评论数量
     * @param operationType 操作类型
     */
    void operationReplyNum(Integer noteId, Integer num, String operationType);

    /**
     * 社区笔记发现推荐列表
     * @param noteId 笔记ID
     * @param request 分页参数
     */
    PageInfo<CommunityNotes> findDiscoverNoteRecommendList(Integer noteId, PageParamRequest request);

    Map<Integer, CommunityNotes> getMapByIdList(List<Integer> noteIdList);

    /**
     * 删除用户所有笔记
     * @param userId 用户ID
     */
    Boolean deleteByUid(Integer userId);
}
