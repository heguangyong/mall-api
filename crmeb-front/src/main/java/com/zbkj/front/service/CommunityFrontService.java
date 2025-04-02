package com.zbkj.front.service;

import com.github.pagehelper.PageInfo;
import com.zbkj.common.model.community.CommunityCategory;
import com.zbkj.common.model.community.CommunityTopic;
import com.zbkj.common.request.*;
import com.zbkj.common.response.*;

import java.util.List;

/**
 * @ClassName CommunityService
 * @Description 社区服务
 * @Author HZW
 * @Date 2023/3/9 14:38
 * @Version 1.0
 */
public interface CommunityFrontService {

    /**
     * 获取所有社区分类
     */
    List<CommunityCategory> getAllCategory();

    /**
     * 社区笔记发现列表
     * @param request 搜索参数
     * @return PageInfo
     */
    PageInfo<CommunityNoteFrontPageResponse> findDiscoverNoteList(CommunityNoteFrontDiscoverRequest request);

    /**
     * 社区笔记关注列表
     * @param request 分页参数
     * @return PageInfo
     */
    PageInfo<CommunityNoteFrontFollowResponse> findFollowNoteList(PageParamRequest request);

    /**
     * 社区笔记作者列表
     * @param authorId 作者ID
     * @param request 分页参数
     * @return PageInfo
     */
    PageInfo<CommunityNoteFrontPageResponse> findAuthorNoteList(Integer authorId, PageParamRequest request);

    /**
     * 社区话题统计数据
     * @param tid 话题ID
     */
    CommunityTopicFrontCountResponse getTopicCount(Integer tid);

    /**
     * 社区用户主页
     * @param uid 社区用户ID
     */
    CommunityUserHomePageResponse getUserHomePage(Integer uid);

    /**
     * 社区用户笔记详情
     * @param noteId 文章ID
     */
    CommunityNoteFrontDetailResponse getUserNoteDetail(Integer noteId);

    /**
     * 社区笔记评论列表
     * @param noteId 笔记ID
     * @return PageInfo
     */
    PageInfo<CommunityCommentReplyResponse> findNoteReplyPageList(Integer noteId, PageParamRequest request);

    /**
     * 社区话题笔记列表
     * @param request 搜索参数
     * @return PageInfo
     */
    PageInfo<CommunityNoteFrontPageResponse> findTopicNoteList(CommunityNoteTopicSearchRequest request);

    /**
     * 社区笔记发现推荐列表
     * @param noteId 笔记ID
     * @param request 分页参数
     */
    PageInfo<CommunityNoteFrontDetailResponse> findDiscoverNoteRecommendList(Integer noteId, PageParamRequest request);

    /**
     * 创建社区笔记
     */
    void createNote(CommunityNoteSaveRequest request);

    /**
     * 编辑社区笔记
     */
    void updateNote(CommunityNoteSaveRequest request);

    /**
     * 社区推荐话题列表
     */
    List<CommunityTopic> findRecommendTopicList();

    /**
     * 社区话题搜索列表
     */
    PageInfo<CommunityTopic> findSearchTopicList(CommonSearchRequest request);

    /**
     * 我的主页
     */
    CommunityUserHomePageResponse getMyHomePage();

    /**
     * 社区之我的笔记列表
     */
    PageInfo<CommunityNoteFrontPageResponse> findMyNoteList(PageParamRequest request);

    /**
     * 社区笔记评论开关
     * @param noteId 笔记ID
     */
    void updateNoteReplySwitch(Integer noteId);

    /**
     * 我的关注列表
     */
    PageInfo<CommunityUserResponse> findMyConcernedList(PageParamRequest request);

    /**
     * 我的粉丝列表
     */
    PageInfo<CommunityUserResponse> findMyFansList(PageParamRequest request);

    /**
     * 社区笔记点赞/取消
     * @param noteId 笔记ID
     */
    void likeNote(Integer noteId);

    /**
     * 社区笔记评论点赞/取消
     * @param replyId 评论ID
     */
    void likeReply(Integer replyId);

    /**
     * 社区关注/取关作者
     * @param authorId 作者ID
     */
    void concernedAuthor(Integer authorId);

    /**
     * 社区推荐作者列表
     */
    PageInfo<CommunityRecommendAuthorResponse> getRecommendAuthorList(PageParamRequest request);

    /**
     * 社区笔记添加评论
     */
    CommunityCommentReplyResponse replyAdd(CommunityReplyAddRequest request);

    /**
     * 社区笔记删除
     * @param noteId 笔记ID
     */
    void deleteNote(Integer noteId);

    /**
     * 社区笔记评论删除
     */
    Integer deleteReply(Integer replyId);

    /**
     * 社区之我的点赞列表
     */
    PageInfo<CommunityNoteFrontPageResponse> findMyLikeNoteList(PageParamRequest request);

    /**
     * 编辑个性签名
     */
    void editMySignature(EditMySignatureRequest request);

    /**
     * 获取社区笔记评论平台开关设置
     */
    String getPlatformSwitchConfig();
}
