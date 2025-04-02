package com.zbkj.service.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.PageHelper;
import com.zbkj.common.model.page.PageCategory;
import com.zbkj.common.request.PageParamRequest;
import com.zbkj.common.request.page.PageCategorySearchRequest;
import com.zbkj.service.dao.page.PageCategoryDao;
import com.zbkj.service.service.PageCategoryService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
* @author dazongzi
* @description PageCategoryServiceImpl 接口实现
* @date 2023-05-16
*/
@Service
public class PageCategoryServiceImpl extends ServiceImpl<PageCategoryDao, PageCategory> implements PageCategoryService {

    @Resource
    private PageCategoryDao dao;


    /**
    * 列表
    * @param request 请求参数
    * @param pageParamRequest 分页类参数
    * @author dazongzi
    * @since 2023-05-16
    * @return List<PageCategory>
    */
    @Override
    public List<PageCategory> getList(PageCategorySearchRequest request, PageParamRequest pageParamRequest) {
        PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());

        //带 PageCategory 类的多条件查询
        LambdaQueryWrapper<PageCategory> lambdaQueryWrapper = Wrappers.lambdaQuery();
        if(ObjectUtil.isNotEmpty(request.getId())){
            lambdaQueryWrapper.eq(PageCategory::getId, request.getId());
        }
        if(ObjectUtil.isNotEmpty(request.getLevel())){
            lambdaQueryWrapper.eq(PageCategory::getLevel, request.getLevel());
        }
        if(ObjectUtil.isNotEmpty(request.getIsMer())){
            lambdaQueryWrapper.eq(PageCategory::getIsMer, request.getIsMer());
        }
        if(ObjectUtil.isNotEmpty(request.getName())){
            lambdaQueryWrapper.like(PageCategory::getName, request.getName());
        }
        if(ObjectUtil.isNotEmpty(request.getPid())){
            lambdaQueryWrapper.eq(PageCategory::getPid, request.getPid());
        }
        if(ObjectUtil.isNotEmpty(request.getType())){
            lambdaQueryWrapper.eq(PageCategory::getType, request.getType());
        }
        return dao.selectList(lambdaQueryWrapper);
    }

}

