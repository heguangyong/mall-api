package com.zbkj.service.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zbkj.common.exception.CrmebException;
import com.zbkj.common.model.user.UserTag;
import com.zbkj.common.page.CommonPage;
import com.zbkj.common.request.PageParamRequest;
import com.zbkj.common.request.UserTagRequest;
import com.zbkj.common.result.CommonResultCode;
import com.zbkj.common.result.UserResultCode;
import com.zbkj.service.dao.UserTagDao;
import com.zbkj.service.service.UserService;
import com.zbkj.service.service.UserTagService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * UserTagServiceImpl 接口实现
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
public class UserTagServiceImpl extends ServiceImpl<UserTagDao, UserTag> implements UserTagService {

    @Resource
    private UserTagDao dao;

    @Autowired
    private UserService userService;

    /**
     * 列表
     *
     * @param pageParamRequest 分页类参数
     * @return PageInfo<UserTag>
     */
    @Override
    public PageInfo<UserTag> getList(PageParamRequest pageParamRequest) {
        Page<Object> page = PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());
        LambdaQueryWrapper<UserTag> lqw = Wrappers.lambdaQuery();
        lqw.orderByDesc(UserTag::getId);
        List<UserTag> tagList = dao.selectList(lqw);
        return CommonPage.copyPageInfo(page, tagList);
    }

    /**
     * 新增用户标签
     *
     * @param userTagRequest 标签参数
     */
    @Override
    public Boolean create(UserTagRequest userTagRequest) {
        UserTag userTag = new UserTag();
        BeanUtils.copyProperties(userTagRequest, userTag);
        userTag.setId(null);
        return save(userTag);
    }

    /**
     * 删除用户标签
     *
     * @param id 标签id
     */
    @Override
    public Boolean delete(Integer id) {
        UserTag userTag = getById(id);
        if (ObjectUtil.isNull(userTag)) {
            return Boolean.TRUE;
        }
        // 判断是否有用户使用标签
        if (userService.isUsedTag(id)) {
            throw new CrmebException(UserResultCode.USER_TAG_USING);
        }
        return removeById(id);
    }

    /**
     * 修改用户标签
     *
     * @param userTagRequest 标签参数
     */
    @Override
    public Boolean updateTag(UserTagRequest userTagRequest) {
        if (ObjectUtil.isNull(userTagRequest.getId())) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "ID不能为空");
        }
        UserTag userTag = getById(userTagRequest.getId());
        userTag.setName(userTagRequest.getName());
        return updateById(userTag);
    }

    /**
     * 所有用户标签列表
     *
     * @return List
     */
    @Override
    public List<UserTag> getAllList() {
        LambdaQueryWrapper<UserTag> lqw = Wrappers.lambdaQuery();
        lqw.orderByDesc(UserTag::getId);
        return dao.selectList(lqw);
    }

}

