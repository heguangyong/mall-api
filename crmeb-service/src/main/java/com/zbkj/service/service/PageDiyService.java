package com.zbkj.service.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.github.pagehelper.PageInfo;
import com.zbkj.common.model.page.PageDiy;
import com.zbkj.common.request.CommonSearchRequest;
import com.zbkj.common.request.PageParamRequest;
import com.zbkj.common.request.page.PageDiyEditNameRequest;
import com.zbkj.common.request.page.PageDiyRequest;
import com.zbkj.common.response.page.PageDiyResponse;

import java.util.List;

/**
* @author dazongzi
* @description PageDiyService 接口
* @date 2023-05-16
*/
public interface PageDiyService extends IService<PageDiy> {

    /**
     * 列表
     * @author dazongzi
     * @since 2023-05-16
     * @return List<PageDiy>
     */
    List<PageDiy> getList(String name,PageParamRequest pageParamRequest);

    /**
     * 商户DIY列表
     */
    PageInfo<PageDiy> findMerchantPage(CommonSearchRequest request);

    /**
     * 保存diy
     * @param pageDiy
     */
    PageDiy savePageDiy(PageDiy pageDiy);

    /**
     * 更新diy
     * @param pageDiy
     */
    Boolean editPageDiy(PageDiy pageDiy);

    /**
     * 编辑diy名称
     * @param pageDiyEditNameRequest 待编辑名称对象
     */
    Boolean editPageDiyName(PageDiyEditNameRequest pageDiyEditNameRequest);

    /**
     * 设置DIY首页模版
     * @param diyId 被设置为首页的diy模版id
     * @return 设置结果
     */
    Boolean setDiyDefault(Integer diyId);

//    /**
//     * 获取DIY首页模版Id
//     * @param isLoadValue 是否加载value详情数据
//     * @return 首页模版ID
//     */
//    PageDiy getDiyPageHome(Boolean isLoadValue);

    /** 针对Front
     * 根据id加载diy模版配置
     * @param id 对应的模版id
     * 描述：
     *           id=0加载商城首页的，也就是setDiyPageHome后的数据，平台端会默认设置已经有首页的数据，也会预制
     *           id>1时加载对应的模版即可
     */
    PageDiyResponse getDiyPageByPageIdForFront(Integer id);

    /** 针对Admin
     * 根据id加载diy模版配置
     * @param id 对应的模版id
     * 描述：
     *           id=0加载商城首页的，也就是setDiyPageHome后的数据，平台端会默认设置已经有首页的数据，也会预制
     *           id>1时加载对应的模版即可
     */
    PageDiy getDiyPageByPageIdForAdmin(Integer id);

    /**
     * 获取DIY默认模板ID
     */
    Integer getDefaultId();

    /**
     * 保存商户diy模板
     */
    PageDiy saveMerchantPageDiy(PageDiyRequest request);

    /**
     * 编辑商户diy模板
     */
    Boolean editMerchantPageDiy(PageDiyRequest request);

    /**
     * 删除DIY模板
     */
    Boolean deletePageDiy(Integer id);

    /**
     * 获取商户端装修模板详情
     */
    PageDiyResponse getMerchantPageDiyInfo(Integer id);

    /**
     * 获取移动端商户装修默认模板
     */
    PageDiyResponse getMerchantPageDiyInfoByFront(Integer merId, Integer id);

    /**
     * 获取商户创建默认模板
     */
    PageDiy getMerchantDefDiy();
}
