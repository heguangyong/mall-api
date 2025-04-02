package com.zbkj.front.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.github.pagehelper.PageInfo;
import com.zbkj.common.constants.CommunityConstants;
import com.zbkj.common.constants.Constants;
import com.zbkj.common.constants.UserLevelConstants;
import com.zbkj.common.exception.CrmebException;
import com.zbkj.common.model.community.*;
import com.zbkj.common.model.system.SystemUserLevel;
import com.zbkj.common.model.user.User;
import com.zbkj.common.page.CommonPage;
import com.zbkj.common.request.*;
import com.zbkj.common.response.*;
import com.zbkj.common.result.CommonResultCode;
import com.zbkj.common.result.CommunityResultCode;
import com.zbkj.common.result.MarketingResultCode;
import com.zbkj.common.result.UserResultCode;
import com.zbkj.common.utils.CrmebUtil;
import com.zbkj.front.service.CommunityFrontService;
import com.zbkj.service.service.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @ClassName CommunityServiceImpl
 * @Description 社区服务实现类
 * @Author HZW
 * @Date 2023/3/9 14:39
 * @Version 1.0
 */
@Service
public class CommunityFrontServiceImpl implements CommunityFrontService {

    @Autowired
    private SystemConfigService systemConfigService;
    @Autowired
    private CommunityCategoryService communityCategoryService;
    @Autowired
    private CommunityTopicService communityTopicService;
    @Autowired
    private CommunityNotesService communityNotesService;
    @Autowired
    private UserService userService;
    @Autowired
    private CommunityNotesRelationService communityNotesRelationService;
    @Autowired
    private SystemUserLevelService systemUserLevelService;
    @Autowired
    private CommunityAuthorConcernedService communityAuthorConcernedService;
    @Autowired
    private CommunityNotesProductService communityNotesProductService;
    @Autowired
    private CommunityReplyService communityReplyService;
    @Autowired
    private CommunityReplyLikeService communityReplyLikeService;
    @Autowired
    private AsyncService asyncService;

    /**
     * 获取所有社区分类
     */
    @Override
    public List<CommunityCategory> getAllCategory() {
        return communityCategoryService.findListByShow(Constants.COMMON_IS_FILED_ONE);
    }

    /**
     * 社区笔记发现列表
     * @param request 搜索参数
     * @return PageInfo
     */
    @Override
    public PageInfo<CommunityNoteFrontPageResponse> findDiscoverNoteList(CommunityNoteFrontDiscoverRequest request) {
        PageInfo<CommunityNotes> pageInfo = communityNotesService.findDiscoverNoteList(request);
        if (CollUtil.isEmpty(pageInfo.getList())) {
            return CommonPage.copyPageInfo(pageInfo, new ArrayList<>());
        }
        Integer userId = userService.getUserId();
        List<CommunityNoteFrontPageResponse> responseList = notesListToResponseList(pageInfo.getList(), userId);
        return CommonPage.copyPageInfo(pageInfo, responseList);
    }

    /**
     * 社区笔记关注列表
     * @param request 分页参数
     * @return PageInfo
     */
    @Override
    public PageInfo<CommunityNoteFrontFollowResponse> findFollowNoteList(PageParamRequest request) {
        Integer userId = userService.getUserIdException();
//        if (userId <= 0) {
//            return null;
//        }
        PageInfo<CommunityNotes> pageInfo = communityNotesService.findFollowNoteList(request, userId);
        if (CollUtil.isEmpty(pageInfo.getList())) {
            return CommonPage.copyPageInfo(pageInfo, new ArrayList<>());
        }
        List<CommunityNotes> notesList = pageInfo.getList();
        List<Integer> uidList = new ArrayList<>();
        List<Integer> topicIdList = new ArrayList<>();
        notesList.forEach(note -> {
            uidList.add(note.getUid());
            if (StrUtil.isNotBlank(note.getTopicIds())) {
                List<Integer> tidList = CrmebUtil.stringToArray(note.getTopicIds());
                tidList.forEach(t -> {
                    if (!topicIdList.contains(t)) {
                        topicIdList.add(t);
                    }
                });
            }
        });
        Map<Integer, User> userMap = userService.getUidMapList(uidList);
        Map<Integer, CommunityTopic> topicMap = communityTopicService.getMapInIdList(topicIdList);

        String replySwitch = systemConfigService.getValueByKey(CommunityConstants.COMMUNITY_REPLY_SWITCH);
        List<CommunityNoteFrontFollowResponse> responseList = notesList.stream().map(note -> {
            CommunityNoteFrontFollowResponse response = new CommunityNoteFrontFollowResponse();
            BeanUtils.copyProperties(note, response);
            response.setAuthorId(note.getUid());
            response.setAuthorName(userMap.get(note.getUid()).getNickname());
            response.setAuthorAvatar(userMap.get(note.getUid()).getAvatar());
            response.setUserIsLike(communityNotesRelationService.isLikeByUid(note.getId(), userId));
            if (userMap.get(note.getUid()).getLevel() > 0) {
                SystemUserLevel userLevel = systemUserLevelService.getByLevelId(userMap.get(note.getUid()).getLevel());
                response.setAuthorLevelIcon(userLevel.getIcon());
            }
            if (StrUtil.isNotBlank(note.getTopicIds())) {
                List<CommunityTopic> topicList = Arrays.stream(note.getTopicIds().split(",")).map(e -> topicMap.get(Integer.valueOf(e))).collect(Collectors.toList());
                if (CollUtil.isNotEmpty(topicList)) {
                    response.setTopicList(topicList);
                }
            }
            response.setProductList(communityNotesProductService.findListByNoteId(note.getId()));

            if (replySwitch.equals(Constants.COMMON_SWITCH_OPEN)) {
                response.setPlatReplySwitch(true);
                response.setReplyStatus(note.getReplyStatus());
            }
            return response;
        }).collect(Collectors.toList());
        return CommonPage.copyPageInfo(pageInfo, responseList);
    }

    /**
     * 社区笔记作者列表
     * @param authorId 作者ID
     * @param request 分页参数
     * @return PageInfo
     */
    @Override
    public PageInfo<CommunityNoteFrontPageResponse> findAuthorNoteList(Integer authorId, PageParamRequest request) {
        PageInfo<CommunityNotes> pageInfo = communityNotesService.findAuthorNoteList(authorId, request);
        if (CollUtil.isEmpty(pageInfo.getList())) {
            return CommonPage.copyPageInfo(pageInfo, new ArrayList<>());
        }
        Integer userId = userService.getUserId();
        List<CommunityNoteFrontPageResponse> responseList = notesListToResponseList(pageInfo.getList(), userId);
        return CommonPage.copyPageInfo(pageInfo, responseList);
    }

    /**
     * 社区话题统计数据
     * @param tid 话题ID
     */
    @Override
    public CommunityTopicFrontCountResponse getTopicCount(Integer tid) {
        CommunityTopic topic = communityTopicService.getById(tid);
        if (ObjectUtil.isNull(topic) || topic.getIsDel().equals(Constants.COMMON_IS_FILED_ONE)) {
            throw new CrmebException(CommunityResultCode.COMMUNITY_TOPIC_NOT_EXIST);
        }
        CommunityTopicFrontCountResponse response = new CommunityTopicFrontCountResponse();
        response.setId(topic.getId());
        response.setName(topic.getName());
        response.setNoteNum(communityNotesService.getFrontCountByTopic(topic.getId()));
        return response;
    }

    /**
     * 社区用户主页
     * @param uid 社区用户ID
     */
    @Override
    public CommunityUserHomePageResponse getUserHomePage(Integer uid) {
        User user = userService.getById(uid);
        if (ObjectUtil.isNull(user) || user.getIsLogoff()) {
            throw new CrmebException(UserResultCode.USER_NOT_EXIST);
        }
        CommunityUserHomePageResponse response = new CommunityUserHomePageResponse();
        response.setId(user.getId());
        response.setNickname(user.getNickname());
        response.setAvatar(user.getAvatar());
        response.setSignature(user.getSignature());
        if (user.getLevel() > 0) {
            SystemUserLevel userLevel = systemUserLevelService.getByLevelId(user.getLevel());
            response.setUserLevelIcon(userLevel.getIcon());
        }
        response.setConcernedNum(communityAuthorConcernedService.getCountByUserId(uid));
        response.setFansNum(communityAuthorConcernedService.getCountByAuthorId(uid));
        response.setLikeNum(communityNotesRelationService.getCountLikeByAuthorId(uid));
        Integer userId = userService.getUserId();
        if (userId > 0) {
            response.setIsConcerned(communityAuthorConcernedService.isConcernedByUid(uid, userId));
        }
        return response;
    }

    /**
     * 社区用户笔记详情
     * @param noteId 文章ID
     */
    @Override
    public CommunityNoteFrontDetailResponse getUserNoteDetail(Integer noteId) {
        Integer userId = userService.getUserId();
        CommunityNotes note = communityNotesService.getByIdException(noteId);
        if (!note.getAuditStatus().equals(CommunityConstants.COMMUNITY_NOTE_AUDIT_SUCCESS) && !userId.equals(note.getUid())) {
            throw new CrmebException(CommunityResultCode.COMMUNITY_NOTE_NOT_EXIST);
        }

        User author = userService.getById(note.getUid());
        if (ObjectUtil.isNull(author) || author.getIsLogoff()) {
            throw new CrmebException(CommunityResultCode.COMMUNITY_NOTE_AUTHOR_NOTE_EXIST);
        }

        CommunityNoteFrontDetailResponse response = new CommunityNoteFrontDetailResponse();
        BeanUtils.copyProperties(note, response);

        response.setAuthorId(author.getId());
        response.setAuthorAvatar(author.getAvatar());
        response.setAuthorName(author.getNickname());
        if (author.getLevel() > 0) {
            SystemUserLevel userLevel = systemUserLevelService.getByLevelId(author.getLevel());
            response.setAuthorLevelIcon(userLevel.getIcon());
        }

        CommunityCategory category = communityCategoryService.getById(note.getCategoryId());
        response.setCategoryName(category.getName());
        if (StrUtil.isNotBlank(note.getTopicIds())) {
            List<Integer> topicIdList = CrmebUtil.stringToArray(note.getTopicIds());
            List<CommunityTopic> topicList = communityTopicService.findAllByIdList(topicIdList);
            response.setTopicList(topicList);
        }
        response.setProductList(communityNotesProductService.findListByNoteId(note.getId()));

        if (userId > 0) {
            response.setUserIsLike(communityNotesRelationService.isLikeByUid(note.getId(), userId));
            if (userId.equals(note.getUid())) {
                response.setIsConcerned(false);
            } else {
                response.setIsConcerned(communityAuthorConcernedService.isConcernedByUid(note.getUid(), userId));
            }
        }

        String replySwitch = systemConfigService.getValueByKey(CommunityConstants.COMMUNITY_REPLY_SWITCH);
        if (replySwitch.equals(Constants.COMMON_SWITCH_OPEN)) {
            response.setPlatReplySwitch(true);
        }
        return response;
    }

    /**
     * 社区笔记评论列表
     * @param noteId 笔记ID
     * @return PageInfo
     */
    @Override
    public PageInfo<CommunityCommentReplyResponse> findNoteReplyPageList(Integer noteId, PageParamRequest request) {
        PageInfo<CommunityCommentReplyResponse> pageInfo = communityReplyService.findNotePageList(noteId, request);
        List<CommunityCommentReplyResponse> list = pageInfo.getList();
        if (CollUtil.isEmpty(list)) {
            return pageInfo;
        }
        Integer userId = userService.getUserId();
        if (userId <= 0) {
            return pageInfo;
        }
        List<CommunityReplyLike> replyLikeList = communityReplyLikeService.findListByNoteIdAndUid(noteId, userId);
        if (CollUtil.isEmpty(replyLikeList)) {
            return pageInfo;
        }
        List<Integer> likeList = replyLikeList.stream().map(CommunityReplyLike::getReplyId).collect(Collectors.toList());
        for (CommunityCommentReplyResponse response : list) {
            if (likeList.contains(response.getId())) {
                response.setIsLike(true);
            }
            if (CollUtil.isEmpty(response.getReplyList())) {
                continue;
            }
            response.getReplyList().forEach(e -> {
                if (likeList.contains(e.getId())) {
                    e.setIsLike(true);
                }
            });
        }
        return pageInfo;
    }

    /**
     * 社区话题笔记列表
     * @param request 搜索参数
     * @return PageInfo
     */
    @Override
    public PageInfo<CommunityNoteFrontPageResponse> findTopicNoteList(CommunityNoteTopicSearchRequest request) {
        PageInfo<CommunityNotes> pageInfo = communityNotesService.findTopicNoteList(request);
        if (CollUtil.isEmpty(pageInfo.getList())) {
            return CommonPage.copyPageInfo(pageInfo, new ArrayList<>());
        }
        Integer userId = userService.getUserId();
        List<CommunityNoteFrontPageResponse> responseList = notesListToResponseList(pageInfo.getList(), userId);
        return CommonPage.copyPageInfo(pageInfo, responseList);
    }

    /**
     * 社区笔记发现推荐列表
     * @param noteId 笔记ID
     * @param request 分页参数
     */
    @Override
    public PageInfo<CommunityNoteFrontDetailResponse> findDiscoverNoteRecommendList(Integer noteId, PageParamRequest request) {
        PageInfo<CommunityNotes> pageInfo = communityNotesService.findDiscoverNoteRecommendList(noteId, request);
        List<CommunityNotes> notesList = pageInfo.getList();
        if (CollUtil.isEmpty(notesList)) {
            return CommonPage.copyPageInfo(pageInfo, new ArrayList<>());
        }
        Integer userId = userService.getUserId();
        List<Integer> uidList = notesList.stream().map(CommunityNotes::getUid).collect(Collectors.toList());
        Map<Integer, User> userMap = userService.getUidMapList(uidList);
        String replySwitch = systemConfigService.getValueByKey(CommunityConstants.COMMUNITY_REPLY_SWITCH);
        boolean platReplySwitch = replySwitch.equals(Constants.COMMON_SWITCH_OPEN);
        List<CommunityNoteFrontDetailResponse> responseList = notesList.stream().map(note -> {
            CommunityNoteFrontDetailResponse response = new CommunityNoteFrontDetailResponse();
            BeanUtils.copyProperties(note, response);
            User author = userMap.get(note.getUid());
            if (ObjectUtil.isNull(author) || author.getIsLogoff()) {
                response.setAuthorId(0);
                response.setAuthorAvatar("");
                response.setAuthorName("");
            } else {
                response.setAuthorId(author.getId());
                response.setAuthorAvatar(author.getAvatar());
                response.setAuthorName(author.getNickname());
                if (author.getLevel() > 0) {
                    SystemUserLevel userLevel = systemUserLevelService.getByLevelId(author.getLevel());
                    response.setAuthorLevelIcon(userLevel.getIcon());
                }
            }

            if (StrUtil.isNotBlank(note.getTopicIds())) {
                List<Integer> topicIdList = CrmebUtil.stringToArray(note.getTopicIds());
                List<CommunityTopic> topicList = communityTopicService.findAllByIdList(topicIdList);
                if (CollUtil.isNotEmpty(topicList)) {
                    response.setTopicList(topicList);
                }
            }
            response.setProductList(communityNotesProductService.findListByNoteId(note.getId()));
            if (ObjectUtil.isNotNull(userId) && userId > 0) {
                response.setUserIsLike(communityNotesRelationService.isLikeByUid(note.getId(), userId));
                response.setIsConcerned(communityAuthorConcernedService.isConcernedByUid(note.getUid(), userId));
            }
            response.setPlatReplySwitch(platReplySwitch);
            return response;
        }).collect(Collectors.toList());
        return CommonPage.copyPageInfo(pageInfo, responseList);
    }

    /**
     * 创建社区笔记
     */
    @Override
    public void createNote(CommunityNoteSaveRequest request) {
       communityNotesService.create(request);
    }

    /**
     * 编辑社区笔记
     */
    @Override
    public void updateNote(CommunityNoteSaveRequest request) {
        communityNotesService.updateNote(request);
    }

    /**
     * 社区推荐话题列表
     */
    @Override
    public List<CommunityTopic> findRecommendTopicList() {
       return communityTopicService.findRecommendTopicList();
    }

    /**
     * 社区话题搜索列表
     */
    @Override
    public PageInfo<CommunityTopic> findSearchTopicList(CommonSearchRequest request) {
        return communityTopicService.findSearchTopicList(request);
    }

    /**
     * 我的主页
     */
    @Override
    public CommunityUserHomePageResponse getMyHomePage() {
        User user = userService.getInfo();
        CommunityUserHomePageResponse response = new CommunityUserHomePageResponse();
        response.setId(user.getId());
        response.setNickname(user.getNickname());
        response.setAvatar(user.getAvatar());
        response.setSignature(user.getSignature());
        if (user.getLevel() > 0) {
            String levelSwitch = systemConfigService.getValueByKey(UserLevelConstants.SYSTEM_USER_LEVEL_SWITCH);
            if (Constants.COMMON_SWITCH_OPEN.equals(levelSwitch)) {
                SystemUserLevel userLevel = systemUserLevelService.getByLevelId(user.getLevel());
                response.setUserLevelIcon(userLevel.getIcon());
            }
        }
        response.setConcernedNum(communityAuthorConcernedService.getCountByUserId(user.getId()));
        response.setFansNum(communityAuthorConcernedService.getCountByAuthorId(user.getId()));
        response.setLikeNum(communityNotesRelationService.getCountLikeByAuthorId(user.getId()));
        return response;
    }

    /**
     * 社区之我的笔记列表
     */
    @Override
    public PageInfo<CommunityNoteFrontPageResponse> findMyNoteList(PageParamRequest request) {
        Integer userId = userService.getUserIdException();
        PageInfo<CommunityNotes> pageInfo = communityNotesService.findMyNoteList(userId, request);
        if (CollUtil.isEmpty(pageInfo.getList())) {
            return CommonPage.copyPageInfo(pageInfo, new ArrayList<>());
        }
        List<CommunityNoteFrontPageResponse> responseList = notesListToResponseList(pageInfo.getList(), userId);
        return CommonPage.copyPageInfo(pageInfo, responseList);
    }

    /**
     * 社区笔记评论开关
     * @param noteId 笔记ID
     */
    @Override
    public void updateNoteReplySwitch(Integer noteId) {
        Integer userId = userService.getUserIdException();
        CommunityNotes notes = communityNotesService.getByIdException(noteId);
        if (!notes.getUid().equals(userId)) {
            throw new CrmebException(CommonResultCode.FORBIDDEN.setMessage( "不能操作非自己的数据"));
        }
        if (notes.getReplyStatus().equals(CommunityConstants.COMMUNITY_NOTE_IS_REPLY_FORCE_OFF)) {
            throw new CrmebException(CommonResultCode.ERROR.setMessage("笔记评论已被平台关闭，无法变更"));
        }
        notes.setReplyStatus(notes.getReplyStatus().equals(CommunityConstants.COMMUNITY_NOTE_IS_REPLY_OPEN)
                ? CommunityConstants.COMMUNITY_NOTE_IS_REPLY_CLOSE : CommunityConstants.COMMUNITY_NOTE_IS_REPLY_OPEN);
        boolean update = communityNotesService.updateById(notes);
        if (!update) {
            throw new CrmebException(CommonResultCode.ERROR.setMessage("变更笔记评论开关状态失败"));
        }
    }

    /**
     * 我的关注列表
     */
    @Override
    public PageInfo<CommunityUserResponse> findMyConcernedList(PageParamRequest request) {
        Integer userId = userService.getUserIdException();
        PageInfo<CommunityAuthorConcerned> pageInfo = communityAuthorConcernedService.findPage(0, userId, request);
        List<CommunityAuthorConcerned> concernedList = pageInfo.getList();
        if (CollUtil.isEmpty(concernedList)) {
            return CommonPage.copyPageInfo(pageInfo, new ArrayList<>());
        }
        List<Integer> authorIdList = concernedList.stream().map(CommunityAuthorConcerned::getAuthorId).collect(Collectors.toList());
        Map<Integer, User> userMap = userService.getUidMapList(authorIdList);
        List<CommunityUserResponse> responseList = concernedList.stream().map(c -> {
            User author = userMap.get(c.getAuthorId());
            CommunityUserResponse response = new CommunityUserResponse();
            response.setId(author.getId());
            response.setNickname(author.getNickname());
            response.setAvatar(author.getAvatar());
            response.setSignature(author.getSignature());
            if (author.getLevel() > 0) {
                SystemUserLevel userLevel = systemUserLevelService.getByLevelId(author.getLevel());
                response.setUserLevelIcon(userLevel.getIcon());
            }
            response.setFansNum(communityAuthorConcernedService.getCountByAuthorId(author.getId()));
            response.setIsConcerned(true);
            response.setIsLogoff(author.getIsLogoff());
            return response;
        }).collect(Collectors.toList());
        return CommonPage.copyPageInfo(pageInfo, responseList);
    }

    /**
     * 我的粉丝列表
     */
    @Override
    public PageInfo<CommunityUserResponse> findMyFansList(PageParamRequest request) {
        Integer userId = userService.getUserIdException();
        PageInfo<CommunityAuthorConcerned> pageInfo = communityAuthorConcernedService.findPage(userId, 0, request);
        List<CommunityAuthorConcerned> concernedList = pageInfo.getList();
        if (CollUtil.isEmpty(concernedList)) {
            return CommonPage.copyPageInfo(pageInfo, new ArrayList<>());
        }
        List<Integer> fansIdList = concernedList.stream().map(CommunityAuthorConcerned::getUid).collect(Collectors.toList());
        Map<Integer, User> userMap = userService.getUidMapList(fansIdList);
        List<CommunityUserResponse> responseList = concernedList.stream().map(c -> {
            User fans = userMap.get(c.getUid());
            CommunityUserResponse response = new CommunityUserResponse();
            response.setId(fans.getId());
            response.setNickname(fans.getNickname());
            response.setAvatar(fans.getAvatar());
            response.setSignature(fans.getSignature());
            if (fans.getLevel() > 0) {
                SystemUserLevel userLevel = systemUserLevelService.getByLevelId(fans.getLevel());
                response.setUserLevelIcon(userLevel.getIcon());
            }
            response.setFansNum(communityAuthorConcernedService.getCountByAuthorId(fans.getId()));
            response.setIsConcerned(communityAuthorConcernedService.isConcernedByUid(userId, fans.getId()));
            response.setIsFansConcerned(communityAuthorConcernedService.isConcernedByUid(fans.getId(), userId));
            response.setIsLogoff(fans.getIsLogoff());
            return response;
        }).collect(Collectors.toList());
        return CommonPage.copyPageInfo(pageInfo, responseList);
    }

    /**
     * 社区笔记点赞/取消
     * @param noteId 笔记ID
     */
    @Override
    public void likeNote(Integer noteId) {
        Integer userId = userService.getUserIdException();
        CommunityNotes notes = communityNotesService.getByIdException(noteId);
        if (!notes.getAuditStatus().equals(CommunityConstants.COMMUNITY_NOTE_AUDIT_SUCCESS)) {
            throw new CrmebException(CommunityResultCode.COMMUNITY_NOTE_AUDIT_STATUS_EXCEPTION);
        }
//        if (!notes.getReplyStatus().equals(CommunityConstants.COMMUNITY_NOTE_IS_REPLY_OPEN)) {
//            throw new CrmebException(CommunityResultCode.COMMUNITY_NOTE_REPLY_IS_NOT_OPEN);
//        }
        CommunityNotesRelation relation = communityNotesRelationService.getOneByNoteIdAndUid(noteId, userId);
        if (ObjectUtil.isNotNull(relation)) {
            boolean remove = communityNotesRelationService.removeById(relation);
            if (!remove) {
                throw new CrmebException(CommonResultCode.ERROR.setMessage("笔记取消点赞失败"));
            }
            asyncService.communityNoteLikeOrClean(noteId, Constants.OPERATION_TYPE_SUBTRACT);
            return;
        }
        relation = new CommunityNotesRelation();
        relation.setNoteId(notes.getId());
        relation.setAuthorId(notes.getUid());
        relation.setType(CommunityConstants.COMMUNITY_NOTE_RELATION_LIKE);
        relation.setUid(userId);
        boolean save = communityNotesRelationService.save(relation);
        if (!save) {
            throw new CrmebException(CommonResultCode.ERROR.setMessage("笔记点赞失败"));
        }
        asyncService.communityNoteLikeOrClean(noteId, Constants.OPERATION_TYPE_ADD);
    }

    /**
     * 社区笔记评论点赞/取消
     * @param replyId 评论ID
     */
    @Override
    public void likeReply(Integer replyId) {
        Integer userId = userService.getUserIdException();
        communityReplyService.likeReply(replyId, userId);
    }

    /**
     * 社区关注/取关作者
     * @param authorId 作者ID
     */
    @Override
    public void concernedAuthor(Integer authorId) {
        Integer userId = userService.getUserIdException();

        User user = userService.getById(authorId);
        if (ObjectUtil.isNull(user) || user.getIsLogoff()) {
            throw new CrmebException(UserResultCode.USER_NOT_EXIST);
        }

        communityAuthorConcernedService.concernedAuthor(authorId, userId);
    }

    /**
     * 社区推荐作者列表
     */
    @Override
    public PageInfo<CommunityRecommendAuthorResponse> getRecommendAuthorList(PageParamRequest request) {
        Integer userId = userService.getUserIdException();
        PageInfo<Integer> pageInfo = communityNotesService.findAuthorPageTimeDesc(userId, request);
        List<Integer> authorIdList = pageInfo.getList();
        if (CollUtil.isEmpty(authorIdList)) {
            return CommonPage.copyPageInfo(pageInfo, new ArrayList<>());
        }
        Map<Integer, User> userMap = userService.getUidMapList(authorIdList);
        List<CommunityRecommendAuthorResponse> responseList = authorIdList.stream().map(authorId -> {
            CommunityRecommendAuthorResponse response = new CommunityRecommendAuthorResponse();
            response.setAuthorId(userMap.get(authorId).getId());
            response.setAuthorAvatar(userMap.get(authorId).getAvatar());
            response.setAuthorName(userMap.get(authorId).getNickname());
            response.setAuthorSignature(userMap.get(authorId).getSignature());
            response.setAuthorId(userMap.get(authorId).getId());
            response.setAuthorId(userMap.get(authorId).getId());
            if (userMap.get(authorId).getLevel() > 0) {
                SystemUserLevel userLevel = systemUserLevelService.getByLevelId(userMap.get(authorId).getLevel());
                response.setAuthorLevelIcon(userLevel.getIcon());
            }
            response.setFansNum(communityAuthorConcernedService.getCountByAuthorId(authorId));
            response.setIsConcerned(false);

            List<CommunityNotes> notesList = communityNotesService.findNewNoteByAuthorId(authorId, 3);
            List<CommunityNoteSimpleResponse> simpleResponseList = notesList.stream().map(n -> {
                CommunityNoteSimpleResponse simpleResponse = new CommunityNoteSimpleResponse();
                simpleResponse.setId(n.getId());
                simpleResponse.setCover(n.getCover());
                simpleResponse.setType(n.getType());
                return simpleResponse;
            }).collect(Collectors.toList());
            response.setNoteList(simpleResponseList);
            return response;
        }).collect(Collectors.toList());
        return CommonPage.copyPageInfo(pageInfo, responseList);
    }

    /**
     * 社区笔记添加评论
     */
    @Override
    public CommunityCommentReplyResponse replyAdd(CommunityReplyAddRequest request) {
        Integer userId = userService.getUserIdException();
        String platReplySwitch = systemConfigService.getValueByKeyException(CommunityConstants.COMMUNITY_REPLY_SWITCH);
        if (platReplySwitch.contains(Constants.COMMON_SWITCH_CLOSE)) {
            throw new CrmebException(CommunityResultCode.COMMUNITY_REPLY_PLAT_CLOSE);
        }
        CommunityNotes communityNotes = communityNotesService.getByIdException(request.getNoteId());
        if (!communityNotes.getReplyStatus().equals(CommunityConstants.COMMUNITY_NOTE_IS_REPLY_OPEN)) {
            throw new CrmebException(CommunityResultCode.COMMUNITY_NOTE_REPLY_IS_NOT_OPEN);
        }
        request.setUserId(userId);
        CommunityCommentReplyResponse response = communityReplyService.create(request);
        response.setNoteReplyNum(communityNotes.getReplyNum());
        if (response.getAuditStatus().equals(CommunityConstants.COMMUNITY_REPLY_AUDIT_SUCCESS)) {
            response.setNoteReplyNum(response.getNoteReplyNum() + 1);
        }
        return response;
    }

    /**
     * 社区笔记删除
     * @param noteId 笔记ID
     */
    @Override
    public void deleteNote(Integer noteId) {
        Integer userId = userService.getUserIdException();
        CommunityNotes communityNotes = communityNotesService.getByIdException(noteId);
        if (!userId.equals(communityNotes.getUid())) {
            throw new CrmebException(CommonResultCode.FORBIDDEN.setMessage("不能操作非自己的资源"));
        }
        communityNotesService.delete(noteId);
    }

    /**
     * 社区笔记评论删除
     */
    @Override
    public Integer deleteReply(Integer replyId) {
        Integer userId = userService.getUserIdException();
        CommunityReply reply = communityReplyService.getById(replyId);
        if (ObjectUtil.isNull(reply) && reply.getIsDel().equals(Constants.COMMON_IS_FILED_ONE)) {
            throw new CrmebException(CommunityResultCode.COMMUNITY_REPLY_NOT_EXIST);
        }
        if (!userId.equals(reply.getUid())) {
            throw new CrmebException(CommonResultCode.FORBIDDEN.setMessage("不能操作非自己的资源"));
        }
        communityReplyService.delete(replyId);
        return communityReplyService.getCountByNid(reply.getNoteId());
    }

    /**
     * 社区之我的点赞列表
     */
    @Override
    public PageInfo<CommunityNoteFrontPageResponse> findMyLikeNoteList(PageParamRequest request) {
        Integer userId = userService.getUserIdException();
        PageInfo<CommunityNotesRelation> pageInfo = communityNotesRelationService.findLikePageByUid(userId, request);
        List<CommunityNotesRelation> relationList = pageInfo.getList();
        if (CollUtil.isEmpty(relationList)) {
            return CommonPage.copyPageInfo(pageInfo, new ArrayList<>());
        }
        List<Integer> noteIdList = relationList.stream().map(CommunityNotesRelation::getNoteId).collect(Collectors.toList());
        Map<Integer, CommunityNotes> notesMap = communityNotesService.getMapByIdList(noteIdList);
        List<CommunityNotes> notesList = new ArrayList<>();
        relationList.forEach(r -> {
            notesList.add(notesMap.get(r.getNoteId()));
        });
        List<CommunityNoteFrontPageResponse> responseList = notesListToResponseList(notesList, userId);
        return CommonPage.copyPageInfo(pageInfo, responseList);
    }

    /**
     * 编辑个性签名
     */
    @Override
    public void editMySignature(EditMySignatureRequest request) {
        Integer userId = userService.getUserIdException();
        User user = new User();
        user.setId(userId);
        user.setSignature(StrUtil.isBlank(request.getSignature()) ? "" : request.getSignature());
        boolean update = userService.updateById(user);
        if (!update) {
            throw new CrmebException(CommonResultCode.ERROR.setMessage("编辑个性签名失败"));
        }
    }

    /**
     * 获取社区笔记评论平台开关设置
     */
    @Override
    public String getPlatformSwitchConfig() {
        return systemConfigService.getValueByKeyException(CommunityConstants.COMMUNITY_REPLY_SWITCH);
    }

    private List<CommunityNoteFrontPageResponse> notesListToResponseList(List<CommunityNotes> notesList, Integer userId) {
        List<Integer> uidList = notesList.stream().map(CommunityNotes::getUid).distinct().collect(Collectors.toList());
        Map<Integer, User> userMap = userService.getUidMapList(uidList);
        return notesList.stream().map(note -> {
            CommunityNoteFrontPageResponse response = new CommunityNoteFrontPageResponse();
            response.setId(note.getId());
            response.setTitle(note.getTitle());
            response.setType(note.getType());
            response.setCover(note.getCover());
            response.setAuthorId(note.getUid());
            response.setAuthorName(userMap.get(note.getUid()).getNickname());
            response.setAuthorAvatar(userMap.get(note.getUid()).getAvatar());
            response.setLikeNum(note.getLikeNum());
            if (ObjectUtil.isNotNull(userId) && userId > 0) {
                response.setUserIsLike(communityNotesRelationService.isLikeByUid(note.getId(), userId));
            }
            if (userMap.get(note.getUid()).getLevel() > 0) {
                SystemUserLevel userLevel = systemUserLevelService.getByLevelId(userMap.get(note.getUid()).getLevel());
                response.setUserLevelIcon(userLevel.getIcon());
            }
            if (ObjectUtil.isNotNull(note.getAuditStatus())) {
                response.setAuditStatus(note.getAuditStatus());
            }
            return response;
        }).collect(Collectors.toList());
    }
}
