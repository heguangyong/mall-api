package com.zbkj.service.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.github.pagehelper.PageInfo;
import com.zbkj.common.model.community.CommunityCategory;
import com.zbkj.common.request.CommunityCategorySaveRequest;
import com.zbkj.common.request.CommunityCategorySearchRequest;

import java.util.List;
import java.util.Map;

/**
* CommunityCategory 接口
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
public interface CommunityCategoryService extends IService<CommunityCategory> {

    /**
     * 社区分类分页列表
     * @param request 请求参数
     * @return 分页列表
     */
    PageInfo<CommunityCategory> findPageList(CommunityCategorySearchRequest request);

    /**
     * 保存社区分类
     * @param request 请求参数
     */
    void add(CommunityCategorySaveRequest request);

    /**
     * 编辑社区分类
     * @param request 请求参数
     */
    void edit(CommunityCategorySaveRequest request);

    /**
     * 删除社区分类
     * @param id 分类ID
     */
    void deleteById(Integer id);

    /**
     * 获取分类Map
     * @param cateIdList 分类ID列表
     * @return Map<id，name>
     */
    Map<Integer, String> getMapInIdList(List<Integer> cateIdList);

    /**
     * 社区分类显示开关
     * @param id 分类ID
     */
    void showSwitch(Integer id);

    /**
     * 根据显示状态获取全部分类列表
     */
    List<CommunityCategory> findListByShow(Integer isShow);
}