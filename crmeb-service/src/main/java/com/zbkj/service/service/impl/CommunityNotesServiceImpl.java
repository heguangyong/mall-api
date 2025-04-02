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
import com.zbkj.common.dto.CommunityNotePageDateDto;
import com.zbkj.common.exception.CrmebException;
import com.zbkj.common.model.community.CommunityNotes;
import com.zbkj.common.model.community.CommunityNotesProduct;
import com.zbkj.common.model.user.User;
import com.zbkj.common.page.CommonPage;
import com.zbkj.common.request.*;
import com.zbkj.common.response.CommunityNoteDetailResponse;
import com.zbkj.common.response.CommunityNotePageDateResponse;
import com.zbkj.common.result.CommonResultCode;
import com.zbkj.common.result.CommunityResultCode;
import com.zbkj.common.utils.CrmebUtil;
import com.zbkj.common.utils.ValidateFormUtil;
import com.zbkj.service.dao.community.CommunityNotesDao;
import com.zbkj.service.service.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
* CommunityNotes 接口实现
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
@Slf4j
@Service
public class CommunityNotesServiceImpl extends ServiceImpl<CommunityNotesDao, CommunityNotes> implements CommunityNotesService {

    @Resource
    private CommunityNotesDao dao;

    @Autowired
    private CommunityCategoryService categoryService;
    @Autowired
    private CommunityTopicService topicService;
    @Autowired
    private CommunityReplyService replyService;
    @Autowired
    private UserService userService;
    @Autowired
    private CommunityNotesProductService notesProductService;
    @Autowired
    private TransactionTemplate transactionTemplate;
    @Autowired
    private CommunityAuthorConcernedService communityAuthorConcernedService;
    @Autowired
    private CommunityNotesProductService communityNotesProductService;
    @Autowired
    private OrderDetailService orderDetailService;
    @Autowired
    private SystemAttachmentService systemAttachmentService;
    @Autowired
    private SystemConfigService systemConfigService;
    @Autowired
    private AsyncService asyncService;
    @Autowired
    private CommunityNotesRelationService communityNotesRelationService;


    /**
     * 是否使用社区分类
     * @param cateId 社区分类ID
     */
    @Override
    public Boolean isUseCategory(Integer cateId) {
        LambdaQueryWrapper<CommunityNotes> lqw = Wrappers.lambdaQuery();
        lqw.select(CommunityNotes::getId);
        lqw.eq(CommunityNotes::getCategoryId, cateId);
        lqw.eq(CommunityNotes::getIsDel, Constants.COMMON_IS_FILED_ZERO);
        lqw.last("limit 1");
        CommunityNotes communityNotes = dao.selectOne(lqw);
        return ObjectUtil.isNotNull(communityNotes);
    }

    /**
     * 通过话题ID获取所有笔记
     * @param topicId 话题ID
     */
    @Override
    public List<CommunityNotes> findAllByTopic(Integer topicId) {
        LambdaQueryWrapper<CommunityNotes> lqw = Wrappers.lambdaQuery();
        lqw.eq(CommunityNotes::getIsDel, Constants.COMMON_IS_FILED_ZERO);
        lqw.apply("FIND_IN_SET({0}, topic_ids)", topicId);
        return dao.selectList(lqw);
    }

    /**
     * 更新笔记关联的话题
     * @param id 笔记ID
     * @param topicIds 话题Ids
     */
    @Override
    public void updateTopicIds(Integer id, String topicIds) {
        LambdaUpdateWrapper<CommunityNotes> wrapper = Wrappers.lambdaUpdate();
        wrapper.set(CommunityNotes::getTopicIds, topicIds);
        wrapper.eq(CommunityNotes::getId, id);
        boolean update = update(wrapper);
        if (!update) {
            throw new CrmebException("更新笔记关联话题失败");
        }
    }

    /**
     * 社区笔记分页列表
     * @param request 查询参数
     * @return PageInfo
     */
    @Override
    public PageInfo<CommunityNotePageDateResponse> findPageList(CommunityNoteSearchRequest request) {
        Map<String, Object> map = CollUtil.newHashMap();
        if (StrUtil.isNotBlank(request.getTitle())) {
            map.put("title", URLUtil.decode(request.getTitle()));
        }
        if (ObjectUtil.isNotNull(request.getType())) {
            map.put("type", request.getType());
        }
        if (ObjectUtil.isNotNull(request.getCategoryId())) {
            map.put("categoryId", request.getCategoryId());
        }
        if (ObjectUtil.isNotNull(request.getTopicId())) {
            map.put("topicId", request.getTopicId());
        }
        if (ObjectUtil.isNotNull(request.getAuditStatus())) {
            map.put("auditStatus", request.getAuditStatus());
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
        Page<CommunityNotePageDateDto> page = PageHelper.startPage(request.getPage(), request.getLimit());
        List<CommunityNotePageDateDto> dtoList = dao.findPageList(map);
        if (CollUtil.isEmpty(dtoList)) {
            return CommonPage.copyPageInfo(page, new ArrayList<>());
        }
        List<Integer> cateIdList = CollUtil.newArrayList();
        List<Integer> topicIdList = CollUtil.newArrayList();
        dtoList.forEach(e -> {
            cateIdList.add(e.getCategoryId());
            if (StrUtil.isNotBlank(e.getTopicIds())) {
                topicIdList.addAll(CrmebUtil.stringToArray(e.getTopicIds()));
            }
        });
        Map<Integer, String> categoryMap = categoryService.getMapInIdList(cateIdList);
        Map<Integer, String> topicMap = topicService.getNameMapInIdList(topicIdList);

        List<CommunityNotePageDateResponse> responseList = dtoList.stream().map(dto -> {
            CommunityNotePageDateResponse response = new CommunityNotePageDateResponse();
            BeanUtils.copyProperties(dto, response);
            response.setCategoryName(categoryMap.get(dto.getCategoryId()));
            if (StrUtil.isNotBlank(dto.getTopicIds())) {
                List<String> topicList = Arrays.stream(dto.getTopicIds().split(",")).map(e -> topicMap.get(Integer.valueOf(e))).collect(Collectors.toList());
                response.setTopicList(topicList);
            }
            return response;
        }).collect(Collectors.toList());
        return CommonPage.copyPageInfo(page, responseList);
    }

    /**
     * 社区笔记详情
     * @param id 笔记ID
     */
    @Override
    public CommunityNoteDetailResponse detail(Integer id) {
        CommunityNotes note = getByIdException(id);
        CommunityNoteDetailResponse response = new CommunityNoteDetailResponse();
        BeanUtils.copyProperties(note, response);
        User user = userService.getById(note.getUid());
        response.setAuthorId(note.getUid());
        response.setAuthorName(user.getNickname());
        response.setProductList(notesProductService.findListByNoteId(id));
        return response;
    }

    /**
     * 社区笔记审核
     */
    @Override
    public void audit(CommonAuditRequest request) {
        if (request.getAuditStatus().equals(CommunityConstants.COMMUNITY_NOTE_AUDIT_ERROR) && StrUtil.isBlank(request.getRefusalReason())) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED.setMessage("请填写审核拒绝原因"));
        }
        CommunityNotes notes = getByIdException(request.getId());
        if (!notes.getAuditStatus().equals(CommunityConstants.COMMUNITY_NOTE_AUDIT_AWAIT)) {
            throw new CrmebException(CommunityResultCode.COMMUNITY_NOTE_AUDIT_STATUS_EXCEPTION);
        }
        notes.setAuditStatus(request.getAuditStatus());
        if (request.getAuditStatus().equals(CommunityConstants.COMMUNITY_NOTE_AUDIT_ERROR) && StrUtil.isNotBlank(request.getRefusalReason())) {
            notes.setRefusal(request.getRefusalReason());
        }
        notes.setOperateTime(DateUtil.date());
        boolean update = updateById(notes);
        if (!update) {
            throw new CrmebException("社区笔记审核失败");
        }
        if (notes.getAuditStatus().equals(CommunityConstants.COMMUNITY_NOTE_AUDIT_SUCCESS)) {
            asyncService.noteUpExp(notes.getUid(), notes.getId());
        }
    }

    /**
     * 社区笔记强制下架
     */
    @Override
    public void forcedDown(CommonForcedDownRequest request) {
        CommunityNotes notes = getByIdException(request.getId());
        if (!notes.getAuditStatus().equals(CommunityConstants.COMMUNITY_NOTE_AUDIT_SUCCESS)) {
            throw new CrmebException(CommunityResultCode.COMMUNITY_NOTE_AUDIT_STATUS_EXCEPTION);
        }
        notes.setAuditStatus(CommunityConstants.COMMUNITY_NOTE_AUDIT_CLOSE);
        notes.setOperateTime(DateUtil.date());
        notes.setRefusal(request.getReason());
        boolean update = updateById(notes);
        if (!update) {
            throw new CrmebException("社区笔记强制下架失败");
        }
    }

    /**
     * 社区笔记删除
     * @param id 笔记ID
     */
    @Override
    public void delete(Integer id) {
        CommunityNotes notes = getByIdException(id);
        notes.setIsDel(Constants.COMMON_IS_FILED_ONE);

        Boolean execute = transactionTemplate.execute(e -> {
            updateById(notes);
            notesProductService.deleteByNoteId(id);
            replyService.deleteByNoteId(id);
            communityNotesRelationService.deleteByNoteId(id);
            return Boolean.TRUE;
        });
        if (!execute) {
            throw new CrmebException("社区笔记删除失败");
        }
    }

    /**
     * 社区笔记分类批量修改
     */
    @Override
    public void categoryBatchUpdate(CommunityNoteCategoryBatchUpdateRequest request) {
        LambdaUpdateWrapper<CommunityNotes> wrapper = Wrappers.lambdaUpdate();
        wrapper.set(CommunityNotes::getCategoryId, request.getCategoryId());
        wrapper.in(CommunityNotes::getId, request.getNoteIdList());
        update(wrapper);
    }

    /**
     * 社区笔记推荐星级编辑
     */
    @Override
    public void updateStar(CommonStarUpdateRequest request) {
        CommunityNotes notes = getByIdException(request.getId());
        if (!notes.getAuditStatus().equals(CommunityConstants.COMMUNITY_NOTE_AUDIT_SUCCESS)) {
            throw new CrmebException(CommunityResultCode.COMMUNITY_NOTE_AUDIT_STATUS_EXCEPTION);
        }
        if (notes.getStar().equals(request.getStar())) {
            return;
        }
        notes.setStar(request.getStar());
        boolean update = updateById(notes);
        if (!update) {
            throw new CrmebException("社区笔记推荐星级编辑失败");
        }
    }

    /**
     * 社区笔记评论强制关闭开关
     * @param id 笔记ID
     */
    @Override
    public void replyForceOffSwitch(Integer id) {
        CommunityNotes notes = getByIdException(id);
        notes.setReplyStatus(notes.getReplyStatus().equals(CommunityConstants.COMMUNITY_NOTE_IS_REPLY_FORCE_OFF) ? CommunityConstants.COMMUNITY_NOTE_IS_REPLY_CLOSE : CommunityConstants.COMMUNITY_NOTE_IS_REPLY_FORCE_OFF);
        boolean update = updateById(notes);
        if (!update) {
            throw new CrmebException("修改社区笔记评论开关状态失败");
        }
    }

    private PageInfo<CommunityNotes> findFrontSearchList(CommunityNoteFrontSearchRequest request) {
        Page<CommunityNotes> page = PageHelper.startPage(request.getPage(), request.getLimit());
        LambdaQueryWrapper<CommunityNotes> lqw = Wrappers.lambdaQuery();
        lqw.select(CommunityNotes::getId, CommunityNotes::getTitle, CommunityNotes::getCover, CommunityNotes::getUid,
                CommunityNotes::getType, CommunityNotes::getLikeNum, CommunityNotes::getAuditStatus);
        if (ObjectUtil.isNotNull(request.getCategoryId())) {
            lqw.eq(CommunityNotes::getCategoryId, request.getCategoryId());
        }
        if (StrUtil.isNotBlank(request.getTitle())) {
            lqw.like(CommunityNotes::getTitle, request.getTitle());
        }
        if (ObjectUtil.isNotNull(request.getUid())) {
            lqw.eq(CommunityNotes::getUid, request.getUid());
        }
        if (ObjectUtil.isNotNull(request.getAuditStatus())) {
            lqw.eq(CommunityNotes::getAuditStatus, request.getAuditStatus());
        }
        lqw.eq(CommunityNotes::getIsDel, Constants.COMMON_IS_FILED_ZERO);
        if (ObjectUtil.isNotNull(request.getTopicId())) {
            lqw.apply("FIND_IN_SET({0}, topic_ids)", request.getTopicId());
        }
        switch (request.getCollation()) {
            case "star":
                lqw.orderByDesc(CommunityNotes::getStar, CommunityNotes::getId);
                break;
            case "hot":
                lqw.orderByDesc(CommunityNotes::getLikeNum, CommunityNotes::getId);
                break;
            default:
                lqw.orderByDesc(CommunityNotes::getId);
        }

        List<CommunityNotes> list = dao.selectList(lqw);
        return CommonPage.copyPageInfo(page, list);
    }

    /**
     * 移动端社区发现笔记分页列表
     * @param request 搜索参数
     * @return PageInfo
     */
    @Override
    public PageInfo<CommunityNotes> findDiscoverNoteList(CommunityNoteFrontDiscoverRequest request) {
        CommunityNoteFrontSearchRequest searchRequest = new CommunityNoteFrontSearchRequest();
        if (ObjectUtil.isNotNull(request.getCategoryId())) {
            searchRequest.setCategoryId(request.getCategoryId());
        }
        if (StrUtil.isNotBlank(request.getTitle())) {
            searchRequest.setTitle(URLUtil.decode(request.getTitle()));
        }
        searchRequest.setAuditStatus(CommunityConstants.COMMUNITY_NOTE_AUDIT_SUCCESS);
        searchRequest.setCollation("star");
        searchRequest.setPage(request.getPage());
        searchRequest.setLimit(request.getLimit());
        return findFrontSearchList(searchRequest);
    }

    /**
     * 移动端社区笔记关注分页列表
     * @param request 分页参数
     * @return PageInfo
     */
    @Override
    public PageInfo<CommunityNotes> findFollowNoteList(PageParamRequest request, Integer userId) {
        List<Integer> authorIdList = communityAuthorConcernedService.findAuthorIdList(userId);
        Page<CommunityNotes> page = PageHelper.startPage(request.getPage(), request.getLimit());
        if (CollUtil.isEmpty(authorIdList)) {
            return CommonPage.copyPageInfo(page, new ArrayList<>());
        }
        LambdaQueryWrapper<CommunityNotes> lqw = Wrappers.lambdaQuery();
        lqw.select(CommunityNotes::getId, CommunityNotes::getTitle, CommunityNotes::getCover, CommunityNotes::getContent,
                CommunityNotes::getVideo, CommunityNotes::getTopicIds, CommunityNotes::getUid, CommunityNotes::getType,
                CommunityNotes::getCreateTime, CommunityNotes::getLikeNum, CommunityNotes::getImage, CommunityNotes::getReplyNum);
        lqw.in(CommunityNotes::getUid, authorIdList);
        lqw.eq(CommunityNotes::getAuditStatus, CommunityConstants.COMMUNITY_NOTE_AUDIT_SUCCESS);
        lqw.eq(CommunityNotes::getIsDel, Constants.COMMON_IS_FILED_ZERO);
        lqw.orderByDesc(CommunityNotes::getId);
        List<CommunityNotes> list = dao.selectList(lqw);
        return CommonPage.copyPageInfo(page, list);
    }

    /**
     * 获取话题关联的笔记数量
     * @param topicId 话题ID
     */
    @Override
    public Integer getCountByTopic(Integer topicId) {
        LambdaQueryWrapper<CommunityNotes> lqw = Wrappers.lambdaQuery();
        lqw.eq(CommunityNotes::getIsDel, Constants.COMMON_IS_FILED_ZERO);
        lqw.apply("FIND_IN_SET({0}, topic_ids)", topicId);
        return dao.selectCount(lqw);
    }

    /**
     * 获取话题关联的笔记数量
     * @param topicId 话题ID
     */
    @Override
    public Integer getFrontCountByTopic(Integer topicId) {
        LambdaQueryWrapper<CommunityNotes> lqw = Wrappers.lambdaQuery();
        lqw.eq(CommunityNotes::getIsDel, Constants.COMMON_IS_FILED_ZERO);
        lqw.eq(CommunityNotes::getAuditStatus, CommunityConstants.COMMUNITY_NOTE_AUDIT_SUCCESS);
        lqw.apply("FIND_IN_SET({0}, topic_ids)", topicId);
        return dao.selectCount(lqw);
    }

    /**
     * 社区笔记作者列表
     * @param authorId 作者ID
     * @param request 分页参数
     * @return PageInfo
     */
    @Override
    public PageInfo<CommunityNotes> findAuthorNoteList(Integer authorId, PageParamRequest request) {
        CommunityNoteFrontSearchRequest searchRequest = new CommunityNoteFrontSearchRequest();
        searchRequest.setUid(authorId);
        searchRequest.setAuditStatus(CommunityConstants.COMMUNITY_NOTE_AUDIT_SUCCESS);
        searchRequest.setPage(request.getPage());
        searchRequest.setLimit(request.getLimit());
        return findFrontSearchList(searchRequest);
    }

    @Override
    public CommunityNotes getByIdException(Integer id) {
        CommunityNotes notes = getById(id);
        if (ObjectUtil.isNull(notes) || notes.getIsDel().equals(Constants.COMMON_IS_FILED_ONE)) {
            throw new CrmebException(CommunityResultCode.COMMUNITY_NOTE_NOT_EXIST);
        }
        return notes;
    }

    /**
     * 社区话题笔记列表
     * @param request 搜索参数
     * @return PageInfo
     */
    @Override
    public PageInfo<CommunityNotes> findTopicNoteList(CommunityNoteTopicSearchRequest request) {
        CommunityNoteFrontSearchRequest searchRequest = new CommunityNoteFrontSearchRequest();
        searchRequest.setTopicId(request.getTopicId());
        searchRequest.setCollation(request.getType());
        searchRequest.setAuditStatus(CommunityConstants.COMMUNITY_NOTE_AUDIT_SUCCESS);
        searchRequest.setPage(request.getPage());
        searchRequest.setLimit(request.getLimit());
        return findFrontSearchList(searchRequest);
    }

    /**
     * 创建社区笔记
     */
    @Override
    public void create(CommunityNoteSaveRequest request) {
        validatorNoteParam(request);
        Integer userId = userService.getUserIdException();
        CommunityNotes note = new CommunityNotes();
        BeanUtils.copyProperties(request, note, "id");
        note.setUid(userId);
        note.setCover(systemAttachmentService.clearPrefix(request.getCover()));
        if (CommunityConstants.COMMUNITY_NOTE_TYPE_IMAGE_CONTENT.equals(request.getType())) {
            note.setImage(systemAttachmentService.clearPrefix(request.getImage()));
        }
        if (CommunityConstants.COMMUNITY_NOTE_TYPE_SHORT_VIDEO.equals(request.getType())) {
            note.setVideo(systemAttachmentService.clearPrefix(request.getVideo()));
        }

        List<CommunityNotesProduct> notesProductList = new ArrayList<>();
        if (StrUtil.isNotBlank(request.getProIds())) {
            List<Integer> proIdList = CrmebUtil.stringToArray(request.getProIds());
            proIdList.forEach(pid -> {
                CommunityNotesProduct notesProduct = new CommunityNotesProduct();
                notesProduct.setProductId(pid);
                notesProduct.setIsPay(orderDetailService.isPurchased(pid, userId) ? 1 : 0);
                notesProductList.add(notesProduct);
            });
        }
        note.setAuditStatus(CommunityConstants.COMMUNITY_NOTE_AUDIT_SUCCESS);
        if (CommunityConstants.COMMUNITY_NOTE_TYPE_IMAGE_CONTENT.equals(request.getType())) {
            String textAuditSwitch = systemConfigService.getValueByKeyException(CommunityConstants.COMMUNITY_IMAGE_TEXT_AUDIT_SWITCH);
            if (Constants.COMMON_SWITCH_OPEN.equals(textAuditSwitch)) {
                note.setAuditStatus(CommunityConstants.COMMUNITY_NOTE_AUDIT_AWAIT);
            }
        }
        if (CommunityConstants.COMMUNITY_NOTE_TYPE_SHORT_VIDEO.equals(request.getType())) {
            String videoAuditSwitch = systemConfigService.getValueByKeyException(CommunityConstants.COMMUNITY_SHORT_VIDEO_AUDIT_SWITCH);
            if (Constants.COMMON_SWITCH_OPEN.equals(videoAuditSwitch)) {
                note.setAuditStatus(CommunityConstants.COMMUNITY_NOTE_AUDIT_AWAIT);
            }
        }

        Boolean execute = transactionTemplate.execute(e -> {
            int insert = dao.insert(note);
            if (insert < 1) {
                e.setRollbackOnly();
                log.error("添加社区笔记失败，request = {}", request);
                return Boolean.FALSE;
            }
            if (CollUtil.isNotEmpty(notesProductList)) {
                notesProductList.forEach(p -> p.setNoteId(note.getId()));
                communityNotesProductService.saveBatch(notesProductList);
            }
            return Boolean.TRUE;
        });
        if (!execute) {
            throw new CrmebException("创建社区笔记失败");
        }
        if (note.getAuditStatus().equals(CommunityConstants.COMMUNITY_NOTE_AUDIT_SUCCESS)) {
            // 异步掉用添加用户经验
            asyncService.noteUpExp(userId, note.getId());
        }
    }

    /**
     * 编辑社区笔记
     */
    @Override
    public void updateNote(CommunityNoteSaveRequest request) {
        if (ObjectUtil.isNull(request.getId())) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED.setMessage("请先择笔记"));
        }
        validatorNoteParam(request);
        Integer userId = userService.getUserIdException();
        CommunityNotes note = getByIdException(request.getId());
        if (!note.getUid().equals(userId)) {
            throw new CrmebException("不能操作别人的数据");
        }
        note.setTitle(StrUtil.isNotBlank(request.getTitle()) ? request.getTitle() : "");
        note.setCover(systemAttachmentService.clearPrefix(request.getCover()));
        if (CommunityConstants.COMMUNITY_NOTE_TYPE_IMAGE_CONTENT.equals(request.getType())) {
            note.setImage(systemAttachmentService.clearPrefix(request.getImage()));
        }
        if (CommunityConstants.COMMUNITY_NOTE_TYPE_SHORT_VIDEO.equals(request.getType())) {
            note.setVideo(systemAttachmentService.clearPrefix(request.getVideo()));
        }
        note.setContent(StrUtil.isNotBlank(request.getContent()) ? request.getContent() : "");
        note.setCategoryId(request.getCategoryId());
        note.setType(request.getType());
        note.setTopicIds(StrUtil.isNotBlank(request.getTopicIds()) ? request.getTopicIds() : "");
        if (!note.getReplyStatus().equals(CommunityConstants.COMMUNITY_NOTE_IS_REPLY_FORCE_OFF)) {
            note.setReplyStatus(request.getReplyStatus());
        }
        if (note.getAuditStatus().equals(CommunityConstants.COMMUNITY_NOTE_AUDIT_CLOSE)) {
            note.setAuditStatus(CommunityConstants.COMMUNITY_NOTE_AUDIT_AWAIT);
        } else if (CommunityConstants.COMMUNITY_NOTE_TYPE_IMAGE_CONTENT.equals(request.getType())) {
            String textAuditSwitch = systemConfigService.getValueByKeyException(CommunityConstants.COMMUNITY_IMAGE_TEXT_AUDIT_SWITCH);
            if (Constants.COMMON_SWITCH_OPEN.equals(textAuditSwitch)) {
                note.setAuditStatus(CommunityConstants.COMMUNITY_NOTE_AUDIT_AWAIT);
            }
        } else if (CommunityConstants.COMMUNITY_NOTE_TYPE_SHORT_VIDEO.equals(request.getType())) {
            String videoAuditSwitch = systemConfigService.getValueByKeyException(CommunityConstants.COMMUNITY_SHORT_VIDEO_AUDIT_SWITCH);
            if (Constants.COMMON_SWITCH_OPEN.equals(videoAuditSwitch)) {
                note.setAuditStatus(CommunityConstants.COMMUNITY_NOTE_AUDIT_AWAIT);
            }
        } else {
            note.setAuditStatus(CommunityConstants.COMMUNITY_NOTE_AUDIT_SUCCESS);
        }

        List<CommunityNotesProduct> notesProductList = new ArrayList<>();
        if (StrUtil.isNotBlank(request.getProIds())) {
            List<Integer> proIdList = CrmebUtil.stringToArray(request.getProIds());
            proIdList.forEach(pid -> {
                CommunityNotesProduct notesProduct = new CommunityNotesProduct();
                notesProduct.setProductId(pid);
                notesProduct.setNoteId(note.getId());
                notesProduct.setIsPay(orderDetailService.isPurchased(pid, userId) ? 1 : 0);
                notesProductList.add(notesProduct);
            });
        }

        Boolean execute = transactionTemplate.execute(e -> {
            boolean update = updateById(note);
            if (!update) {
                e.setRollbackOnly();
                log.error("编辑社区笔记失败，request = {}", request);
                return Boolean.FALSE;
            }
            communityNotesProductService.deleteByNoteId(note.getId());
            if (CollUtil.isNotEmpty(notesProductList)) {
                notesProductList.forEach(p -> p.setNoteId(note.getId()));
                communityNotesProductService.saveBatch(notesProductList);
            }
            return Boolean.TRUE;
        });
        if (!execute) {
            throw new CrmebException("编辑社区笔记失败");
        }
    }

    /**
     * 社区之我的笔记列表
     */
    @Override
    public PageInfo<CommunityNotes> findMyNoteList(Integer userId, PageParamRequest request) {
        CommunityNoteFrontSearchRequest searchRequest = new CommunityNoteFrontSearchRequest();
        searchRequest.setUid(userId);
        searchRequest.setPage(request.getPage());
        searchRequest.setLimit(request.getLimit());
        return findFrontSearchList(searchRequest);
    }

    /**
     * 按时间倒序获取文章作者列表
     */
    @Override
    public PageInfo<Integer> findAuthorPageTimeDesc(Integer userId, PageParamRequest request) {
        List<Integer> authorConcernedList = communityAuthorConcernedService.findAuthorIdList(userId);
        authorConcernedList.add(userId);
        Page<CommunityNotes> page = PageHelper.startPage(request.getPage(), request.getLimit());
        LambdaQueryWrapper<CommunityNotes> lqw = Wrappers.lambdaQuery();
        lqw.select(CommunityNotes::getUid);
        lqw.notIn(CommunityNotes::getUid, authorConcernedList);
        lqw.eq(CommunityNotes::getAuditStatus, CommunityConstants.COMMUNITY_NOTE_AUDIT_SUCCESS);
        lqw.eq(CommunityNotes::getIsDel, Constants.COMMON_IS_FILED_ZERO);
        lqw.groupBy(CommunityNotes::getUid);
        lqw.orderByDesc(CommunityNotes::getCreateTime);
        List<CommunityNotes> list = dao.selectList(lqw);
        if (CollUtil.isEmpty(list)) {
            return CommonPage.copyPageInfo(page, new ArrayList<>());
        }
        List<Integer> authorIdList = list.stream().map(CommunityNotes::getUid).collect(Collectors.toList());
        return CommonPage.copyPageInfo(page, authorIdList);
    }

    /**
     * 获取新笔记通过作者
     * @param authorId 作者ID
     * @param num 笔记数量
     */
    @Override
    public List<CommunityNotes> findNewNoteByAuthorId(Integer authorId, Integer num) {
        LambdaQueryWrapper<CommunityNotes> lqw = Wrappers.lambdaQuery();
        lqw.select(CommunityNotes::getId, CommunityNotes::getCover, CommunityNotes::getType);
        lqw.eq(CommunityNotes::getUid, authorId);
        lqw.eq(CommunityNotes::getAuditStatus, CommunityConstants.COMMUNITY_NOTE_AUDIT_SUCCESS);
        lqw.eq(CommunityNotes::getIsDel, Constants.COMMON_IS_FILED_ZERO);
        lqw.orderByDesc(CommunityNotes::getId);
        lqw.last(" limit " + num);
        return dao.selectList(lqw);
    }

    /**
     * 社区笔记点赞数量
     * @param noteId 笔记ID
     * @param operationType 操作类型：add-点赞，sub-取消
     */
    @Override
    public void operationLike(Integer noteId, String operationType) {
        LambdaUpdateWrapper<CommunityNotes> wrapper = Wrappers.lambdaUpdate();
        if (operationType.equals(Constants.OPERATION_TYPE_ADD)) {
            wrapper.setSql("like_num = like_num + 1");
        } else {
            wrapper.setSql("like_num = like_num - 1");
        }
        wrapper.eq(CommunityNotes::getId, noteId);
        update(wrapper);
    }

    /**
     * 操作社区笔记评论数量
     * @param noteId 笔记ID
     * @param num 评论数量
     * @param operationType 操作类型
     */
    @Override
    public void operationReplyNum(Integer noteId, Integer num, String operationType) {
        LambdaUpdateWrapper<CommunityNotes> wrapper = Wrappers.lambdaUpdate();
        if (operationType.equals(Constants.OPERATION_TYPE_ADD)) {
            wrapper.setSql(StrUtil.format("reply_num = reply_num + {}", num));
        } else {
            wrapper.setSql(StrUtil.format("reply_num = reply_num - {}", num));
        }
        wrapper.eq(CommunityNotes::getId, noteId);
        update(wrapper);
    }

    /**
     * 社区笔记发现推荐列表
     * @param noteId 笔记ID
     * @param request 分页参数
     */
    @Override
    public PageInfo<CommunityNotes> findDiscoverNoteRecommendList(Integer noteId, PageParamRequest request) {
        CommunityNotes note = getByIdException(noteId);
        Page<CommunityNotes> page = PageHelper.startPage(request.getPage(), request.getLimit());
        LambdaQueryWrapper<CommunityNotes> lqw = Wrappers.lambdaQuery();
        lqw.lt(CommunityNotes::getId, noteId);
        lqw.eq(CommunityNotes::getType, note.getType());
        lqw.eq(CommunityNotes::getAuditStatus, CommunityConstants.COMMUNITY_NOTE_AUDIT_SUCCESS);
        lqw.eq(CommunityNotes::getIsDel, Constants.COMMON_IS_FILED_ZERO);
        lqw.orderByDesc(CommunityNotes::getLikeNum, CommunityNotes::getId);
        List<CommunityNotes> list = dao.selectList(lqw);
        if (CollUtil.isNotEmpty(list)) {
            Collections.shuffle(list);
        }
        return CommonPage.copyPageInfo(page, list);
    }

    @Override
    public Map<Integer, CommunityNotes> getMapByIdList(List<Integer> noteIdList) {
        LambdaQueryWrapper<CommunityNotes> lqw = Wrappers.lambdaQuery();
        lqw.select(CommunityNotes::getId, CommunityNotes::getTitle, CommunityNotes::getCover, CommunityNotes::getUid,
                CommunityNotes::getType, CommunityNotes::getLikeNum);
        lqw.in(CommunityNotes::getId, noteIdList);
        List<CommunityNotes> list = dao.selectList(lqw);
        Map<Integer, CommunityNotes> map = new HashMap<>();
        list.forEach(e -> {
            map.put(e.getId(), e);
        });
        return map;
    }

    /**
     * 删除用户所有笔记
     * @param userId 用户ID
     */
    @Override
    public Boolean deleteByUid(Integer userId) {
        LambdaUpdateWrapper<CommunityNotes> wrapper = Wrappers.lambdaUpdate();
        wrapper.set(CommunityNotes::getIsDel, Constants.COMMON_IS_FILED_ONE);
        wrapper.eq(CommunityNotes::getUid, userId);
        wrapper.eq(CommunityNotes::getIsDel, Constants.COMMON_IS_FILED_ZERO);
        return update(wrapper);
    }

    /**
     * 校验笔记参数
     */
    private void validatorNoteParam(CommunityNoteSaveRequest request) {
        if (CommunityConstants.COMMUNITY_NOTE_TYPE_IMAGE_CONTENT.equals(request.getType())) {
            if (StrUtil.isBlank(request.getImage())) {
                throw new CrmebException(CommonResultCode.VALIDATE_FAILED.setMessage("请先上传图片"));
            }
            String[] split = request.getImage().split(",");
            if (split.length > 9) {
                throw new CrmebException(CommonResultCode.VALIDATE_FAILED.setMessage("最多只能上传9张图片"));
            }
            if (StrUtil.isNotBlank(request.getContent()) && request.getContent().length() > 600) {
                throw new CrmebException(CommonResultCode.VALIDATE_FAILED.setMessage("正文不能超过600个字符"));
            }

        }
        if (CommunityConstants.COMMUNITY_NOTE_TYPE_SHORT_VIDEO.equals(request.getType())) {
            if (StrUtil.isBlank(request.getVideo())) {
                throw new CrmebException(CommonResultCode.VALIDATE_FAILED.setMessage("请先上传视频"));
            }
            if (StrUtil.isNotBlank(request.getContent()) && request.getContent().length() > 200) {
                throw new CrmebException(CommonResultCode.VALIDATE_FAILED.setMessage("视频正文不能超过200个字符"));
            }

        }
        if (StrUtil.isNotBlank(request.getTopicIds())) {
            String[] topicSplit = request.getTopicIds().split(",");
            if (topicSplit.length > 5) {
                throw new CrmebException(CommonResultCode.VALIDATE_FAILED.setMessage("最多只能关联5个话题"));
            }
        }
        if (StrUtil.isNotBlank(request.getProIds())) {
            String[] proSplit = request.getProIds().split(",");
            if (proSplit.length > 5) {
                throw new CrmebException(CommonResultCode.VALIDATE_FAILED.setMessage("最多只能关联5个商品"));
            }
        }
    }
}

