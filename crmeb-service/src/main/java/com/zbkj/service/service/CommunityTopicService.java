package com.zbkj.service.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.github.pagehelper.PageInfo;
import com.zbkj.common.model.community.CommunityTopic;
import com.zbkj.common.request.CommonSearchRequest;
import com.zbkj.common.request.CommunityTopicSaveRequest;
import com.zbkj.common.request.CommunityTopicSearchRequest;

import java.util.List;
import java.util.Map;

/**
* CommunityTopic 接口
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
public interface CommunityTopicService extends IService<CommunityTopic> {

    /**
     * 社区话题分页列表
     */
    PageInfo<CommunityTopic> findPageList(CommunityTopicSearchRequest request);

    /**
     * 添加社区话题
     */
    void add(CommunityTopicSaveRequest request);

    /**
     * 编辑社区话题
     */
    void edit(CommunityTopicSaveRequest request);

    /**
     * 删除社区话题
     * @param id 话题ID
     */
    void deleteById(Integer id);

    /**
     * 获取话题Map
     * @param topicIdList 话题ID列表
     * @return Map<id，CommunityTopic>
     */
    Map<Integer, CommunityTopic> getMapInIdList(List<Integer> topicIdList);

    /**
     * 获取话题Map
     * @param topicIdList 话题ID列表
     * @return Map<id，name>
     */
    Map<Integer, String> getNameMapInIdList(List<Integer> topicIdList);

    /**
     * 获取话题List
     * @param topicIdList 话题ID列表
     * @return List
     */
    List<CommunityTopic> findAllByIdList(List<Integer> topicIdList);

    /**
     * 社区话题开启/关闭推荐
     */
    void recommendSwitch(Integer id);

    /**
     * 社区推荐话题列表
     */
    List<CommunityTopic> findRecommendTopicList();

    /**
     * 社区话题搜索列表
     */
    PageInfo<CommunityTopic> findSearchTopicList(CommonSearchRequest request);
}