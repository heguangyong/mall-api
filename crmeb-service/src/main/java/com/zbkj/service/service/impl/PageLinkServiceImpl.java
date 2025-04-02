package com.zbkj.service.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.PageHelper;
import com.zbkj.common.model.page.PageLink;
import com.zbkj.common.request.PageParamRequest;
import com.zbkj.common.request.page.PageLinkSearchRequest;
import com.zbkj.service.dao.page.PageLinkDao;
import com.zbkj.service.service.PageLinkService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
* @author dazongzi
* @description PageLinkServiceImpl 接口实现
* @date 2023-05-16
*/
@Service
public class PageLinkServiceImpl extends ServiceImpl<PageLinkDao, PageLink> implements PageLinkService {

    @Resource
    private PageLinkDao dao;


    /**
    * 列表
    * @param request 请求参数
    * @param pageParamRequest 分页类参数
    * @author dazongzi
    * @since 2023-05-16
    * @return List<PageLink>
    */
    @Override
    public List<PageLink> getList(PageLinkSearchRequest request, PageParamRequest pageParamRequest) {
        PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());

        //带 PageLink 类的多条件查询
        LambdaQueryWrapper<PageLink> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        PageLink model = new PageLink();
        BeanUtils.copyProperties(request, model);
        lambdaQueryWrapper.setEntity(model);
        return dao.selectList(lambdaQueryWrapper);
    }

}

