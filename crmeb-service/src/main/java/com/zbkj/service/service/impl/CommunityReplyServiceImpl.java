package com.zbkj.service.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.URLUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zbkj.common.constants.CommunityConstants;
import com.zbkj.common.constants.Constants;
import com.zbkj.common.constants.UserConstants;
import com.zbkj.common.exception.CrmebException;
import com.zbkj.common.model.community.CommunityReply;
import com.zbkj.common.model.community.CommunityReplyLike;
import com.zbkj.common.model.user.User;
import com.zbkj.common.page.CommonPage;
import com.zbkj.common.request.CommonAuditRequest;
import com.zbkj.common.request.CommunityReplyAddRequest;
import com.zbkj.common.request.CommunityReplySearchRequest;
import com.zbkj.common.request.PageParamRequest;
import com.zbkj.common.response.CommunityCommentReplyResponse;
import com.zbkj.common.response.CommunityReplyPageDateResponse;
import com.zbkj.common.result.CommonResultCode;
import com.zbkj.common.result.CommunityResultCode;
import com.zbkj.common.utils.CrmebDateUtil;
import com.zbkj.common.utils.ValidateFormUtil;
import com.zbkj.common.vo.DateLimitUtilVo;
import com.zbkj.service.dao.community.CommunityReplyDao;
import com.zbkj.service.service.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
* CommunityReply 接口实现
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
public class CommunityReplyServiceImpl extends ServiceImpl<CommunityReplyDao, CommunityReply> implements CommunityReplyService {

    @Resource
    private CommunityReplyDao dao;

    @Autowired
    private TransactionTemplate transactionTemplate;
    @Autowired
    private UserService userService;
    @Autowired
    private CommunityReplyLikeService communityReplyLikeService;
    @Autowired
    private SystemConfigService systemConfigService;
    @Autowired
    private AsyncService asyncService;

    /**
     * 获取笔记评论数（评论+回复）
     */
    @Override
    public Integer getCountByNid(Integer nid) {
        LambdaQueryWrapper<CommunityReply> lqw = Wrappers.lambdaQuery();
        lqw.eq(CommunityReply::getIsDel, Constants.COMMON_IS_FILED_ZERO);
        lqw.eq(CommunityReply::getAuditStatus, CommunityConstants.COMMUNITY_REPLY_AUDIT_SUCCESS);
        lqw.eq(CommunityReply::getNoteId, nid);
        return dao.selectCount(lqw);
    }

    /**
     * 社区评论分页列表
     * @param request 搜索参数
     * @return PageInfo
     */
    @Override
    public PageInfo<CommunityReplyPageDateResponse> findPageList(CommunityReplySearchRequest request) {
        Map<String, Object> map = new HashMap<>();
        if (StrUtil.isNotBlank(request.getTitle())) {
            map.put("title", URLUtil.decode(request.getTitle()));
        }
        if (StrUtil.isNotBlank(request.getKeywords())) {
            map.put("keywordsContent", URLUtil.decode(request.getKeywords()));
        }
        if (ObjectUtil.isNotNull(request.getAuditStatus())) {
            map.put("auditStatus", request.getAuditStatus());
        }
        if (StrUtil.isNotBlank(request.getDateLimit())) {
            DateLimitUtilVo dateLimitVo = CrmebDateUtil.getDateLimit(request.getDateLimit());
            map.put("startTime", dateLimitVo.getStartTime());
            map.put("endTime", dateLimitVo.getEndTime());
        }
        if (StrUtil.isNotBlank(request.getContent())) {
            ValidateFormUtil.validatorUserCommonSearch(request);
            String keywords = URLUtil.decode(request.getContent());
            switch (request.getSearchType()) {
                case UserConstants.USER_SEARCH_TYPE_ALL:
                    map.put("keywords", keywords);
                    break;
                case UserConstants.USER_SEARCH_TYPE_UID:
                    map.put("uid", Integer.valueOf(request.getContent()));
                    break;
                case UserConstants.USER_SEARCH_TYPE_NICKNAME:
                    map.put("nickname", keywords);
                    break;
                case UserConstants.USER_SEARCH_TYPE_PHONE:
                    map.put("phone", request.getContent());
                    break;
            }
        }
        Page<CommunityReplyPageDateResponse> page = PageHelper.startPage(request.getPage(), request.getLimit());
        List<CommunityReplyPageDateResponse> list = dao.findPageList(map);
        return CommonPage.copyPageInfo(page, list);
    }

    /**
     * 社区评论审核
     */
    @Override
    public void audit(CommonAuditRequest request) {
        if (request.getAuditStatus().equals(CommunityConstants.COMMUNITY_REPLY_AUDIT_ERROR) && StrUtil.isBlank(request.getRefusalReason())) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED.setMessage("请填写审核拒绝原因"));
        }
        CommunityReply reply = getByIdException(request.getId());
        if (!reply.getAuditStatus().equals(CommunityConstants.COMMUNITY_REPLY_AUDIT_AWAIT)) {
            throw new CrmebException(CommunityResultCode.COMMUNITY_REPLY_AUDIT_STATUS_EXCEPTION);
        }
        reply.setAuditStatus(request.getAuditStatus());
        if (request.getAuditStatus().equals(CommunityConstants.COMMUNITY_REPLY_AUDIT_ERROR) && StrUtil.isNotBlank(request.getRefusalReason())) {
            reply.setRefusal(request.getRefusalReason());
        }
        boolean update = updateById(reply);
        if (!update) {
            throw new CrmebException(CommonResultCode.ERROR.setMessage("社区评论审核失败"));
        }
        if (reply.getAuditStatus().equals(CommunityConstants.COMMUNITY_REPLY_AUDIT_SUCCESS)) {
            asyncService.noteAddReplyAfter(reply.getNoteId(), reply.getParentId(), reply.getId());
        }
    }

    /**
     * 社区评论删除
     */
    @Override
    public void delete(Integer id) {
        CommunityReply reply = getByIdException(id);
        reply.setIsDel(Constants.COMMON_IS_FILED_ONE);
        Boolean execute = transactionTemplate.execute(e -> {
            updateById(reply);
            if (reply.getType().equals(CommunityConstants.COMMUNITY_REPLY_TYPE_COMMENT)) {
                deleteReplyByParentId(reply.getId());
            }
            return Boolean.TRUE;
        });
        if (!execute) {
            throw new CrmebException(CommonResultCode.ERROR.setMessage("社区评论删除失败"));
        }
        asyncService.communityReplyDeleteAfter(reply.getNoteId(), reply.getParentId(), reply.getId(), reply.getCountReply());
    }

    /**
     * 社区评论-文章分页列表
     * @param noteId 文章ID
     * @param pageRequest 分页参数
     * @return PageInfo
     */
    @Override
    public PageInfo<CommunityCommentReplyResponse> findNotePageList(Integer noteId, PageParamRequest pageRequest) {
        Page<CommunityReply> page = PageHelper.startPage(pageRequest.getPage(), pageRequest.getLimit());
        LambdaQueryWrapper<CommunityReply> lqw = Wrappers.lambdaQuery();
        lqw.eq(CommunityReply::getNoteId, noteId);
        lqw.eq(CommunityReply::getType, CommunityConstants.COMMUNITY_REPLY_TYPE_COMMENT);
        lqw.eq(CommunityReply::getAuditStatus, CommunityConstants.COMMUNITY_REPLY_AUDIT_SUCCESS);
        lqw.eq(CommunityReply::getIsDel, Constants.COMMON_IS_FILED_ZERO);
        lqw.orderByDesc(CommunityReply::getId);
        List<CommunityReply> list = dao.selectList(lqw);
        if (CollUtil.isEmpty(list)) {
            return CommonPage.copyPageInfo(page, new ArrayList<>());
        }
        List<CommunityCommentReplyResponse> responseList = list.stream().map(r -> {
            CommunityCommentReplyResponse replyResponse = new CommunityCommentReplyResponse();
            BeanUtils.copyProperties(r, replyResponse);
            replyResponse.setAuditStatus(null);
            List<CommunityReply> replyList = findReplyByParentId(r.getId());
            if (CollUtil.isNotEmpty(replyList)) {
                List<CommunityCommentReplyResponse> childReplyList = replyList.stream().map(reply -> {
                    CommunityCommentReplyResponse childReply = new CommunityCommentReplyResponse();
                    BeanUtils.copyProperties(reply, childReply);
                    return childReply;
                }).collect(Collectors.toList());
                replyResponse.setReplyList(childReplyList);
            }
            return replyResponse;
        }).collect(Collectors.toList());
        responseSetUserName(responseList);
        return CommonPage.copyPageInfo(page, responseList);
    }

    /**
     * 评论点赞/取消
     * @param replyId 评论ID
     * @param userId 用户ID
     */
    @Override
    public void likeReply(Integer replyId, Integer userId) {
        CommunityReply reply = getByIdException(replyId);
        if (!reply.getAuditStatus().equals(CommunityConstants.COMMUNITY_REPLY_AUDIT_SUCCESS)) {
            throw new CrmebException(CommunityResultCode.COMMUNITY_REPLY_AUDIT_STATUS_EXCEPTION);
        }
        CommunityReplyLike replyLike = communityReplyLikeService.getDetail(replyId, userId);
        if (ObjectUtil.isNotNull(replyLike)) {
            boolean remove = communityReplyLikeService.removeById(replyLike);
            if (!remove) {
                throw new CrmebException(CommonResultCode.ERROR.setMessage("评论取消点赞失败"));
            }
            asyncService.communityReplyLikeOrClean(replyId, Constants.OPERATION_TYPE_SUBTRACT);
            return;
        }
        replyLike = new CommunityReplyLike();
        replyLike.setNoteId(reply.getNoteId());
        replyLike.setReplyId(reply.getId());
        replyLike.setUid(userId);
        boolean save = communityReplyLikeService.save(replyLike);
        if (!save) {
            throw new CrmebException(CommonResultCode.ERROR.setMessage("评论点赞失败"));
        }
        asyncService.communityReplyLikeOrClean(replyId, Constants.OPERATION_TYPE_ADD);
    }

    /**
     * 社区笔记添加评论
     */
    @Override
    public CommunityCommentReplyResponse create(CommunityReplyAddRequest request) {
        CommunityReply reply = new CommunityReply();
        reply.setType(CommunityConstants.COMMUNITY_REPLY_TYPE_COMMENT);
        reply.setUid(request.getUserId());
        reply.setContent(request.getContent());
        reply.setAuditStatus(CommunityConstants.COMMUNITY_REPLY_AUDIT_SUCCESS);
        reply.setParentId(0);
        reply.setNoteId(request.getNoteId());
        if (request.getReplyId() > 0) {
            reply.setType(CommunityConstants.COMMUNITY_REPLY_TYPE_REPLY);
            CommunityReply tempReply = getByIdException(request.getReplyId());
            if (tempReply.getType().equals(CommunityConstants.COMMUNITY_REPLY_TYPE_COMMENT)) {
                reply.setParentId(tempReply.getId());
                reply.setParentUid(tempReply.getUid());
            } else {
                reply.setParentId(tempReply.getParentId());
                reply.setParentUid(tempReply.getParentUid());
                reply.setReviewId(tempReply.getId());
                reply.setReviewUid(tempReply.getUid());
            }
        }
        String replyAuditSwitch = systemConfigService.getValueByKeyException(CommunityConstants.COMMUNITY_REPLY_AUDIT_SWITCH);
        if (replyAuditSwitch.equals(Constants.COMMON_SWITCH_OPEN)) {
            reply.setAuditStatus(CommunityConstants.COMMUNITY_REPLY_AUDIT_AWAIT);
        }
        boolean save = save(reply);
        if (!save) {
            throw new CrmebException(CommonResultCode.ERROR.setMessage("笔记评论失败"));
        }
        if (reply.getAuditStatus().equals(CommunityConstants.COMMUNITY_REPLY_AUDIT_SUCCESS)) {
            asyncService.noteAddReplyAfter(reply.getNoteId(), reply.getParentId(), reply.getId());
        }
        CommunityCommentReplyResponse response = new CommunityCommentReplyResponse();
        BeanUtils.copyProperties(reply, response);
        User user = userService.getInfo();
        response.setNickname(user.getNickname());
        response.setAvatar(user.getAvatar());
        response.setCreateTime(DateUtil.date());
        if (ObjectUtil.isNotNull(reply.getReviewUid()) && reply.getReviewUid() > 0) {
            User reviewUser = userService.getById(reply.getReviewUid());
            response.setReviewUserNickname(reviewUser.getNickname());
            response.setCountStart(0);
            response.setCreateTime(DateUtil.date());
        }
        return response;
    }

    /**
     * 社区删除笔记评论
     */
    @Override
    public void deleteByNoteId(Integer noteId) {
        LambdaUpdateWrapper<CommunityReply> wrapper = Wrappers.lambdaUpdate();
        wrapper.set(CommunityReply::getIsDel, Constants.COMMON_IS_FILED_ONE);
        wrapper.eq(CommunityReply::getIsDel, Constants.COMMON_IS_FILED_ZERO);
        wrapper.eq(CommunityReply::getNoteId, noteId);
        update(wrapper);
    }

    /**
     * 社区笔记评论点赞与取消
     * @param replyId 评论ID
     * @param operationType 操作类型：add-点赞，sub-取消
     */
    @Override
    public void operationLike(Integer replyId, String operationType) {
        LambdaUpdateWrapper<CommunityReply> wrapper = Wrappers.lambdaUpdate();
        if (operationType.equals(Constants.OPERATION_TYPE_ADD)) {
            wrapper.setSql("count_start = count_start + 1");
        } else {
            wrapper.setSql("count_start = count_start - 1");
        }
        wrapper.eq(CommunityReply::getId, replyId);
        update(wrapper);
    }

    /**
     * 操作社区笔记评论的评论数量
     * @param replyId 评论ID
     * @param num 评论数量
     * @param operationType 操作类型
     */
    @Override
    public void operationReplyNum(Integer replyId, Integer num, String operationType) {
        LambdaUpdateWrapper<CommunityReply> wrapper = Wrappers.lambdaUpdate();
        if (operationType.equals(Constants.OPERATION_TYPE_ADD)) {
            wrapper.setSql(StrUtil.format("count_reply = count_reply + {}", num));
        } else {
            wrapper.setSql(StrUtil.format("count_reply = count_reply - {}", num));
        }
        wrapper.eq(CommunityReply::getId, replyId);
        update(wrapper);
    }

    private void responseSetUserName(List<CommunityCommentReplyResponse> responseList) {
        List<Integer> uidList = new ArrayList<>();
        responseList.forEach(r -> {
            if (!uidList.contains(r.getUid())) {
                uidList.add(r.getUid());
            }
            if (CollUtil.isNotEmpty(r.getReplyList())) {
                r.getReplyList().forEach(rc -> {
                    if (!uidList.contains(rc.getUid())) {
                        uidList.add(rc.getUid());
                    }
                    if (rc.getReviewUid() > 0 && !uidList.contains(rc.getReviewUid())) {
                        uidList.add(rc.getReviewUid());
                    }
                });
            }
        });
        Map<Integer, User> userMap = userService.getUidMapList(uidList);
        responseList.forEach(r -> {
            r.setNickname(userMap.get(r.getUid()).getNickname());
            r.setAvatar(userMap.get(r.getUid()).getAvatar());
            if (CollUtil.isNotEmpty(r.getReplyList())) {
                r.getReplyList().forEach(rc -> {
                    rc.setNickname(userMap.get(rc.getUid()).getNickname());
                    rc.setAvatar(userMap.get(rc.getUid()).getAvatar());
                    if (rc.getReviewUid() > 0) {
                        rc.setReviewUserNickname(userMap.get(rc.getReviewUid()).getNickname());
                    }
                });
            }
        });
    }

    /**
     * 获取评论下的所有回复
     * @param replyId 评论ID
     */
    private List<CommunityReply> findReplyByParentId(Integer replyId) {
        LambdaQueryWrapper<CommunityReply> lqw = Wrappers.lambdaQuery();
        lqw.eq(CommunityReply::getParentId, replyId);
        lqw.eq(CommunityReply::getType, CommunityConstants.COMMUNITY_REPLY_TYPE_REPLY);
        lqw.eq(CommunityReply::getAuditStatus, CommunityConstants.COMMUNITY_REPLY_AUDIT_SUCCESS);
        lqw.eq(CommunityReply::getIsDel, Constants.COMMON_IS_FILED_ZERO);
        lqw.orderByDesc(CommunityReply::getId);
        return dao.selectList(lqw);
    }

    /**
     * 删除评论下的所有回复
     * @param replyId 评论ID
     */
    private void deleteReplyByParentId(Integer replyId) {
        LambdaUpdateWrapper<CommunityReply> wrapper = Wrappers.lambdaUpdate();
        wrapper.set(CommunityReply::getIsDel, Constants.COMMON_IS_FILED_ONE);
        wrapper.eq(CommunityReply::getIsDel, Constants.COMMON_IS_FILED_ZERO);
        wrapper.eq(CommunityReply::getParentId, replyId);
        update(wrapper);
    }

    private CommunityReply getByIdException(Integer id) {
        CommunityReply reply = getById(id);
        if (ObjectUtil.isNull(reply) && reply.getIsDel().equals(Constants.COMMON_IS_FILED_ONE)) {
            throw new CrmebException(CommunityResultCode.COMMUNITY_REPLY_NOT_EXIST);
        }
        return reply;
    }
}

