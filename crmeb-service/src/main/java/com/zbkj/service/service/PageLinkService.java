package com.zbkj.service.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.zbkj.common.model.page.PageLink;
import com.zbkj.common.request.PageParamRequest;
import com.zbkj.common.request.page.PageLinkSearchRequest;

import java.util.List;

/**
* @author dazongzi
* @description PageLinkService 接口
* @date 2023-05-16
*/
public interface PageLinkService extends IService<PageLink> {

    List<PageLink> getList(PageLinkSearchRequest request, PageParamRequest pageParamRequest);
}
