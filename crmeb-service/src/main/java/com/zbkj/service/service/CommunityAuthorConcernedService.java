package com.zbkj.service.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.github.pagehelper.PageInfo;
import com.zbkj.common.model.community.CommunityAuthorConcerned;
import com.zbkj.common.request.PageParamRequest;

import java.util.List;

/**
* CommunityAuthorConcerned 接口
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
public interface CommunityAuthorConcernedService extends IService<CommunityAuthorConcerned> {

    /**
     * 获取关注的作者ID列表
     * @param userId 用户ID
     */
    List<Integer> findAuthorIdList(Integer userId);

    /**
     * 是否关注作者
     * @param authorId 作者ID
     * @param userId 用户ID
     */
    Boolean isConcernedByUid(Integer authorId, Integer userId);

    /**
     * 获取作者被关注数（粉丝数）
     * @param authorId 作者ID
     */
    Integer getCountByAuthorId(Integer authorId);

    /**
     * 获取用户关注数
     * @param userId 用户ID
     */
    Integer getCountByUserId(Integer userId);

    /**
     * 获取关注关系列表
     * @param authorId 作者ID
     * @param userId 用户ID
     * @param request 分页参数
     * @return PageInfo
     */
    PageInfo<CommunityAuthorConcerned> findPage(Integer authorId, Integer userId, PageParamRequest request);

    /**
     * 社区关注/取关作者
     * @param authorId 作者ID
     * @param userId 用户ID
     */
    void concernedAuthor(Integer authorId, Integer userId);
}