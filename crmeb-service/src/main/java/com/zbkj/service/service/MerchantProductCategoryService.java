package com.zbkj.service.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zbkj.common.model.merchant.MerchantProductCategory;
import com.zbkj.common.request.merchant.MerchantProductCategoryRequest;
import com.zbkj.common.vo.ProCategoryCacheVo;

import java.util.List;

/**
*  StoreProductCategoryService 接口
*  +----------------------------------------------------------------------
*  | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
*  +----------------------------------------------------------------------
*  | Copyright (c) 2016~2025 https://www.crmeb.com All rights reserved.
*  +----------------------------------------------------------------------
*  | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
*  +----------------------------------------------------------------------
*  | Author: CRMEB Team <admin@crmeb.com>
*  +----------------------------------------------------------------------
*/
public interface MerchantProductCategoryService extends IService<MerchantProductCategory> {

    /**
     * 获取分类列表
     * @return List
     */
    List<MerchantProductCategory> getAdminList();

    /**
     * 添加分类
     * @param request 分类参数
     * @return Boolean
     */
    Boolean add(MerchantProductCategoryRequest request);

    /**
     * 删除分类
     * @param id 分类id
     * @return Boolean
     */
    Boolean delete(Integer id);

    /**
     * 修改分类
     * @param request 修改参数
     * @return Boolean
     */
    Boolean edit(MerchantProductCategoryRequest request);

    /**
     * 修改分类显示状态
     * @param id 分类ID
     * @return Boolean
     */
    Boolean updateShowStatus(Integer id);

    /**
     * 获取分类缓存树
     * @return List<ProCategoryCacheVo>
     */
    List<ProCategoryCacheVo> getCacheTree();

    /**
     * 获取商户商品分类列表
     * @param merId 商户id
     * @return List
     */
    List<ProCategoryCacheVo> findListByMerId(Integer merId);
}
