package com.zbkj.service.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.zbkj.common.model.page.PageCategory;
import com.zbkj.common.request.PageParamRequest;
import com.zbkj.common.request.page.PageCategorySearchRequest;

import java.util.List;

/**
* @author dazongzi
* @description PageCategoryService 接口
* @date 2023-05-16
*/
public interface PageCategoryService extends IService<PageCategory> {

    List<PageCategory> getList(PageCategorySearchRequest request, PageParamRequest pageParamRequest);
}
