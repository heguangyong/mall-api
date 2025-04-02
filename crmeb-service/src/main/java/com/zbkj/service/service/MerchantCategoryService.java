package com.zbkj.service.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.github.pagehelper.PageInfo;
import com.zbkj.common.model.merchant.MerchantCategory;
import com.zbkj.common.request.merchant.MerchantCategoryRequest;
import com.zbkj.common.request.PageParamRequest;

import java.util.List;
import java.util.Map;

/**
 * MerchantCategoryService 接口
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
public interface MerchantCategoryService extends IService<MerchantCategory> {

    /**
     * 获取商户分类分页列表
     * @param pageParamRequest 分页参数
     * @return PageInfo
     */
    PageInfo<MerchantCategory> getAdminPage(PageParamRequest pageParamRequest);

    /**
     * 添加商户分类
     * @param request 请求参数
     * @return Boolean
     */
    Boolean add(MerchantCategoryRequest request);

    /**
     * 编辑商户分类
     * @param request 请求参数
     * @return Boolean
     */
    Boolean edit(MerchantCategoryRequest request);

    /**
     * 删除商户分类
     * @param id 商户分类ID
     * @return Boolean
     */
    Boolean delete(Integer id);

    /**
     * 全部商户分类列表
     * @return List
     */
    List<MerchantCategory> allList();

    /**
     * 全部商户分类Map
     * @return Map
     */
    Map<Integer, MerchantCategory> allMap();

    MerchantCategory getByIdException(Integer categoryId);
}
