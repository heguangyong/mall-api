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
import com.zbkj.common.constants.Constants;
import com.zbkj.common.exception.CrmebException;
import com.zbkj.common.model.community.CommunityCategory;
import com.zbkj.common.page.CommonPage;
import com.zbkj.common.request.CommunityCategorySaveRequest;
import com.zbkj.common.request.CommunityCategorySearchRequest;
import com.zbkj.common.result.CommonResultCode;
import com.zbkj.common.result.CommunityResultCode;
import com.zbkj.service.dao.community.CommunityCategoryDao;
import com.zbkj.service.service.CommunityCategoryService;
import com.zbkj.service.service.CommunityNotesService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * CommunityCategory 接口实现
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
public class CommunityCategoryServiceImpl extends ServiceImpl<CommunityCategoryDao, CommunityCategory> implements CommunityCategoryService {

    @Resource
    private CommunityCategoryDao dao;

    @Autowired
    private CommunityNotesService notesService;

    /**
     * 社区分类分页列表
     *
     * @param request 请求参数
     * @return 分页列表
     */
    @Override
    public PageInfo<CommunityCategory> findPageList(CommunityCategorySearchRequest request) {
        Page<CommunityCategory> page = PageHelper.startPage(request.getPage(), request.getLimit());
        LambdaQueryWrapper<CommunityCategory> lqw = Wrappers.lambdaQuery();
        lqw.select(CommunityCategory::getId, CommunityCategory::getName, CommunityCategory::getSort,
                CommunityCategory::getIsShow, CommunityCategory::getCreateTime);
        if (StrUtil.isNotBlank(request.getName())) {
            lqw.like(CommunityCategory::getName, URLUtil.decode(request.getName()));
        }
        if (ObjectUtil.isNotNull(request.getIsShow())) {
            lqw.eq(CommunityCategory::getIsShow, request.getIsShow());
        }
        lqw.eq(CommunityCategory::getIsDel, Constants.COMMON_IS_FILED_ZERO);
        lqw.orderByDesc(CommunityCategory::getSort, CommunityCategory::getId);
        List<CommunityCategory> list = dao.selectList(lqw);
        return CommonPage.copyPageInfo(page, list);
    }

    /**
     * 保存社区分类
     *
     * @param request 请求参数
     */
    @Override
    public void add(CommunityCategorySaveRequest request) {
        if (isExistName(request.getName(), 0)) {
            throw new CrmebException(CommunityResultCode.COMMUNITY_CATEGORY_EXIST);
        }
        CommunityCategory category = new CommunityCategory();
        BeanUtils.copyProperties(request, category, "id");
        boolean save = save(category);
        if (!save) {
            throw new CrmebException("添加社区分类失败");
        }
    }

    /**
     * 编辑社区分类
     *
     * @param request 请求参数
     */
    @Override
    public void edit(CommunityCategorySaveRequest request) {
        if (ObjectUtil.isNull(request.getId())) {
            throw new CrmebException(CommunityResultCode.COMMUNITY_CATEGORY_ID_NULL);
        }
        CommunityCategory category = getByIdExpetion(request.getId());

        if (!category.getName().equals(request.getName()) && isExistName(request.getName(), request.getId())) {
            throw new CrmebException(CommunityResultCode.COMMUNITY_CATEGORY_EXIST);
        }
        BeanUtils.copyProperties(request, category);
        boolean update = updateById(category);
        if (!update) {
            throw new CrmebException(CommonResultCode.ERROR.setMessage("编辑社区分类失败"));
        }
    }

    /**
     * 删除社区分类
     * @param id 分类ID
     */
    @Override
    public void deleteById(Integer id) {
        CommunityCategory category = getByIdExpetion(id);

        if (notesService.isUseCategory(id)) {
            throw new CrmebException(CommunityResultCode.COMMUNITY_CATEGORY_USING);
        }
        category.setIsDel(Constants.COMMON_IS_FILED_ONE);
        boolean update = updateById(category);
        if (!update) {
            throw new CrmebException(CommonResultCode.ERROR.setMessage("删除社区分类失败"));
        }
    }

    /**
     * 获取分类Map
     * @param cateIdList 分类ID列表
     * @return Map<id，name>
     */
    @Override
    public Map<Integer, String> getMapInIdList(List<Integer> cateIdList) {
        LambdaQueryWrapper<CommunityCategory> lqw = Wrappers.lambdaQuery();
        lqw.select(CommunityCategory::getId, CommunityCategory::getName);
        lqw.in(CommunityCategory::getId, cateIdList);
        lqw.eq(CommunityCategory::getIsDel, Constants.COMMON_IS_FILED_ZERO);
        List<CommunityCategory> list = dao.selectList(lqw);
        Map<Integer, String> map = CollUtil.newHashMap();
        list.forEach(e -> {
            map.put(e.getId(), e.getName());
        });
        return map;
    }

    /**
     * 社区分类显示开关
     * @param id 分类ID
     */
    @Override
    public void showSwitch(Integer id) {
        CommunityCategory category = getByIdExpetion(id);
        category.setIsShow(category.getIsShow().equals(Constants.COMMON_IS_FILED_ONE) ? Constants.COMMON_IS_FILED_ZERO : Constants.COMMON_IS_FILED_ONE);
        boolean update = updateById(category);
        if (!update) {
            throw new CrmebException("编辑社区分类显示状态失败");
        }
    }

    /**
     * 根据显示状态获取全部分类列表
     */
    @Override
    public List<CommunityCategory> findListByShow(Integer isShow) {
        LambdaQueryWrapper<CommunityCategory> lqw = Wrappers.lambdaQuery();
        lqw.select(CommunityCategory::getId, CommunityCategory::getName);
        if (ObjectUtil.isNotNull(isShow)) {
            lqw.eq(CommunityCategory::getIsShow, isShow);
        }
        lqw.eq(CommunityCategory::getIsDel, Constants.COMMON_IS_FILED_ZERO);
        lqw.orderByDesc(CommunityCategory::getSort, CommunityCategory::getId);
        return dao.selectList(lqw);
    }

    private boolean isExistName(String name, Integer id) {
        LambdaQueryWrapper<CommunityCategory> lqw = Wrappers.lambdaQuery();
        lqw.select(CommunityCategory::getId);
        lqw.eq(CommunityCategory::getName, name);
        lqw.eq(CommunityCategory::getIsDel, Constants.COMMON_IS_FILED_ZERO);
        if (ObjectUtil.isNotNull(id) && id > 0) {
            lqw.ne(CommunityCategory::getId, id);
        }
        lqw.last("limit 1");
        CommunityCategory communityCategory = dao.selectOne(lqw);
        return ObjectUtil.isNotNull(communityCategory);
    }

    private CommunityCategory getByIdExpetion(Integer id) {
        CommunityCategory category = getById(id);
        if (ObjectUtil.isNull(category) || category.getIsDel().equals(Constants.COMMON_IS_FILED_ONE)) {
            throw new CrmebException(CommunityResultCode.COMMUNITY_CATEGORY_NOT_EXIST);
        }
        return category;
    }
}

