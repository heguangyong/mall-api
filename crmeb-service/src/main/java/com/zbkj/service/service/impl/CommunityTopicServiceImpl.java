package com.zbkj.service.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.URLUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zbkj.common.constants.CommunityConstants;
import com.zbkj.common.constants.Constants;
import com.zbkj.common.exception.CrmebException;
import com.zbkj.common.model.community.CommunityNotes;
import com.zbkj.common.model.community.CommunityTopic;
import com.zbkj.common.page.CommonPage;
import com.zbkj.common.request.CommonSearchRequest;
import com.zbkj.common.request.CommunityTopicSaveRequest;
import com.zbkj.common.request.CommunityTopicSearchRequest;
import com.zbkj.common.result.CommonResultCode;
import com.zbkj.common.result.CommunityResultCode;
import com.zbkj.common.utils.CrmebUtil;
import com.zbkj.service.dao.community.CommunityTopicDao;
import com.zbkj.service.service.CommunityNotesService;
import com.zbkj.service.service.CommunityTopicService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * CommunityTopic 接口实现
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
public class CommunityTopicServiceImpl extends ServiceImpl<CommunityTopicDao, CommunityTopic> implements CommunityTopicService {

    @Resource
    private CommunityTopicDao dao;

    @Autowired
    private TransactionTemplate transactionTemplate;

    @Autowired
    private CommunityNotesService notesService;

    /**
     * 社区话题分页列表
     */
    @Override
    public PageInfo<CommunityTopic> findPageList(CommunityTopicSearchRequest request) {
        Page<CommunityTopic> page = PageHelper.startPage(request.getPage(), request.getLimit());
        LambdaQueryWrapper<CommunityTopic> lqw = Wrappers.lambdaQuery();
        lqw.select(CommunityTopic::getId, CommunityTopic::getName, CommunityTopic::getIsHot, CommunityTopic::getCreateTime, CommunityTopic::getCountUse);
        if (StrUtil.isNotBlank(request.getName())) {
            lqw.like(CommunityTopic::getName, URLUtil.decode(request.getName()));
        }
        if (ObjectUtil.isNotNull(request.getIsHot())) {
            lqw.eq(CommunityTopic::getIsHot, request.getIsHot());
        }
        lqw.eq(CommunityTopic::getIsDel, 0);
        lqw.orderByDesc(CommunityTopic::getIsHot, CommunityTopic::getId);
        List<CommunityTopic> list = dao.selectList(lqw);
        if (CollUtil.isNotEmpty(list)) {
            list.forEach(l -> {
                l.setCountUse(notesService.getCountByTopic(l.getId()));
            });
        }
        return CommonPage.copyPageInfo(page, list);
    }

    /**
     * 添加社区话题
     */
    @Override
    public void add(CommunityTopicSaveRequest request) {
        if (isExistName(request.getName(), 0)) {
            throw new CrmebException(CommunityResultCode.COMMUNITY_TOPIC_NAME_EXIST);
        }
        CommunityTopic topic = new CommunityTopic();
        BeanUtils.copyProperties(request, topic, "id");
        topic.setIsHot(0);
        boolean save = save(topic);
        if (!save) {
            throw new CrmebException(CommonResultCode.ERROR.setMessage("添加社区话题失败"));
        }
    }

    /**
     * 编辑社区话题
     */
    @Override
    public void edit(CommunityTopicSaveRequest request) {
        if (ObjectUtil.isNull(request.getId()) || request.getId() <= 0) {
            throw new CrmebException(CommunityResultCode.COMMUNITY_TOPIC_ID_NULL);
        }
        CommunityTopic communityTopic = getByIdException(request.getId());
        if (!communityTopic.getName().equals(request.getName()) && isExistName(request.getName(), request.getId())) {
            throw new CrmebException(CommunityResultCode.COMMUNITY_TOPIC_NAME_EXIST);
        }
        BeanUtils.copyProperties(request, communityTopic);
        boolean update = updateById(communityTopic);
        if (!update) {
            throw new CrmebException(CommonResultCode.ERROR.setMessage("编辑社区话题失败"));
        }
    }

    /**
     * 删除社区话题
     * @param id 话题ID
     */
    @Override
    public void deleteById(Integer id) {
        CommunityTopic communityTopic = getByIdException(id);
        communityTopic.setIsDel(1);

        List<CommunityNotes> communityNotes = notesService.findAllByTopic(id);
        communityNotes.forEach(note -> {
            note.setTopicIds(noteDeleteTopicId(note.getTopicIds(), id));
        });

        Boolean execute = transactionTemplate.execute(e -> {
            updateById(communityTopic);
            if (CollUtil.isNotEmpty(communityNotes)) {
                communityNotes.forEach(note -> {
                    notesService.updateTopicIds(note.getId(), note.getTopicIds());
                });
            }
            return Boolean.TRUE;
        });
        if (!execute) {
            throw new CrmebException(CommonResultCode.ERROR.setMessage("删除社区话题失败"));
        }
    }

    /**
     * 获取话题Map
     * @param topicIdList 话题ID列表
     * @return Map<id，CommunityTopic>
     */
    @Override
    public Map<Integer, CommunityTopic> getMapInIdList(List<Integer> topicIdList) {
        Map<Integer, CommunityTopic> map = CollUtil.newHashMap();
        if (CollUtil.isEmpty(topicIdList)) {
            return map;
        }
        List<CommunityTopic> list = findAllByIdList(topicIdList);
        list.forEach(e -> {
            map.put(e.getId(), e);
        });
        return map;
    }

    /**
     * 获取话题Map
     * @param topicIdList 话题ID列表
     * @return Map<id，name>
     */
    @Override
    public Map<Integer, String> getNameMapInIdList(List<Integer> topicIdList) {
        Map<Integer, String> map = CollUtil.newHashMap();
        if (CollUtil.isEmpty(topicIdList)) {
            return map;
        }
        List<CommunityTopic> list = findAllByIdList(topicIdList);
        list.forEach(e -> {
            map.put(e.getId(), e.getName());
        });
        return map;
    }

    /**
     * 获取话题List
     * @param topicIdList 话题ID列表
     * @return List
     */
    @Override
    public List<CommunityTopic> findAllByIdList(List<Integer> topicIdList) {
        LambdaQueryWrapper<CommunityTopic> lqw = Wrappers.lambdaQuery();
        lqw.select(CommunityTopic::getId, CommunityTopic::getName);
        lqw.eq(CommunityTopic::getIsDel, 0);
        lqw.in(CommunityTopic::getId, topicIdList);
        return dao.selectList(lqw);
    }

    /**
     * 社区话题开启/关闭推荐
     */
    @Override
    public void recommendSwitch(Integer id) {
        CommunityTopic topic = getByIdException(id);
        if (topic.getIsHot().equals(0) && getRecommendCount() >= CommunityConstants.COMMUNITY_TOPIC_RECOMMEND_NUM) {
            throw new CrmebException(CommunityResultCode.COMMUNITY_TOPIC_RECOMMEND_NUM);
        }
        topic.setIsHot(topic.getIsHot().equals(1) ? 0 : 1);
        boolean update = updateById(topic);
        if (!update) {
            throw new CrmebException(CommonResultCode.ERROR.setMessage("社区话题开启/关闭推荐失败"));
        }
    }

    /**
     * 社区推荐话题列表
     */
    @Override
    public List<CommunityTopic> findRecommendTopicList() {
        LambdaQueryWrapper<CommunityTopic> lqw = Wrappers.lambdaQuery();
        lqw.select(CommunityTopic::getId, CommunityTopic::getName);
        lqw.eq(CommunityTopic::getIsHot, Constants.COMMON_IS_FILED_ONE);
        lqw.eq(CommunityTopic::getIsDel, Constants.COMMON_IS_FILED_ZERO);
        lqw.orderByDesc(CommunityTopic::getIsHot, CommunityTopic::getId);
        return dao.selectList(lqw);
    }

    /**
     * 社区话题搜索列表
     */
    @Override
    public PageInfo<CommunityTopic> findSearchTopicList(CommonSearchRequest request) {
        Page<CommonSearchRequest> page = PageHelper.startPage(request.getPage(), request.getLimit());
        LambdaQueryWrapper<CommunityTopic> lqw = Wrappers.lambdaQuery();
        lqw.select(CommunityTopic::getId, CommunityTopic::getName);
        if (StrUtil.isNotBlank(request.getKeywords())) {
            lqw.likeRight(CommunityTopic::getName, URLUtil.decode(request.getKeywords()));
        }
        lqw.eq(CommunityTopic::getIsDel, Constants.COMMON_IS_FILED_ZERO);
        lqw.orderByDesc(CommunityTopic::getId);
        List<CommunityTopic> list = dao.selectList(lqw);
        return CommonPage.copyPageInfo(page, list);
    }

    /**
     * 获取话题推荐数量
     * @return
     */
    private Integer getRecommendCount() {
        LambdaQueryWrapper<CommunityTopic> lqw = Wrappers.lambdaQuery();
        lqw.eq(CommunityTopic::getIsDel, 0);
        lqw.eq(CommunityTopic::getIsHot, 1);
        return dao.selectCount(lqw);
    }

    private String noteDeleteTopicId(String topicIds, Integer delId) {
        List<Integer> tidList = CrmebUtil.stringToArray(topicIds);
        for (int i = 0; i < tidList.size(); ) {
            if (tidList.get(i).equals(delId)) {
                tidList.remove(i);
                break;
            }
            i++;
        }
        if (CollUtil.isEmpty(tidList)) {
            return "";
        }
        if (tidList.size() == 1) {
            return tidList.get(0).toString();
        }
        return tidList.stream().map(String::valueOf).collect(Collectors.joining(","));
    }

    private CommunityTopic getByIdException(Integer id) {
        CommunityTopic topic = getById(id);
        if (ObjectUtil.isNull(topic) || topic.getIsDel().equals(1)) {
            throw new CrmebException(CommunityResultCode.COMMUNITY_TOPIC_NOT_EXIST);
        }
        return topic;
    }

    private boolean isExistName(String name, Integer id) {
        LambdaQueryWrapper<CommunityTopic> lqw = Wrappers.lambdaQuery();
        lqw.select(CommunityTopic::getId);
        lqw.eq(CommunityTopic::getName, name);
        lqw.eq(CommunityTopic::getIsDel, 0);
        if (ObjectUtil.isNotNull(id) && id > 0) {
            lqw.ne(CommunityTopic::getId, id);
        }
        lqw.last(" limit 1");
        CommunityTopic communityTopic = dao.selectOne(lqw);
        return ObjectUtil.isNotNull(communityTopic);
    }
}

