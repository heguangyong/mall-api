package com.zbkj.service.service.impl;

import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.URLUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.PageHelper;
import com.zbkj.common.model.system.SystemGroup;
import com.zbkj.common.request.PageParamRequest;
import com.zbkj.common.request.SystemGroupRequest;
import com.zbkj.common.request.SystemGroupSearchRequest;
import com.zbkj.service.dao.SystemGroupDao;
import com.zbkj.service.service.SystemGroupService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * SystemGroupServiceImpl 接口实现
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
public class SystemGroupServiceImpl extends ServiceImpl<SystemGroupDao, SystemGroup> implements SystemGroupService {

    @Resource
    private SystemGroupDao dao;

    /**
     * 列表
     *
     * @param request          请求参数
     * @param pageParamRequest 分页类参数
     * @return List<SystemGroup>
     */
    @Override
    public List<SystemGroup> getList(SystemGroupSearchRequest request, PageParamRequest pageParamRequest) {
        PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());

        //带 SystemGroup 类的多条件查询
        LambdaQueryWrapper<SystemGroup> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        if (StrUtil.isNotBlank(request.getKeywords())) {
            String keywords = URLUtil.decode(request.getKeywords());
            lambdaQueryWrapper.like(SystemGroup::getName, keywords);
        }
        lambdaQueryWrapper.orderByDesc(SystemGroup::getId);
        return dao.selectList(lambdaQueryWrapper);
    }

    /**
     * 新增组合数据
     *
     * @param systemGroupRequest 新增参数
     */
    @Override
    public Boolean add(SystemGroupRequest systemGroupRequest) {
        SystemGroup systemGroup = new SystemGroup();
        BeanUtils.copyProperties(systemGroupRequest, systemGroup);
        return save(systemGroup);
    }

    /**
     * 删除组合数据表
     *
     * @param id Integer
     */
    @Override
    public Boolean delete(Integer id) {
        return removeById(id);
    }

    /**
     * 修改组合数据表
     *
     * @param id                 integer id
     * @param systemGroupRequest 修改参数
     */
    @Override
    public Boolean edit(Integer id, SystemGroupRequest systemGroupRequest) {
        SystemGroup systemGroup = new SystemGroup();
        BeanUtils.copyProperties(systemGroupRequest, systemGroup);
        systemGroup.setId(id);
        return updateById(systemGroup);
    }

}

