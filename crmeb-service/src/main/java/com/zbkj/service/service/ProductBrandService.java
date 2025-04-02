package com.zbkj.service.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.github.pagehelper.PageInfo;
import com.zbkj.common.model.product.ProductBrand;
import com.zbkj.common.request.BrandCategorySearchRequest;
import com.zbkj.common.request.PageParamRequest;
import com.zbkj.common.request.ProductBrandRequest;
import com.zbkj.common.response.ProductBrandListResponse;
import com.zbkj.common.response.ProductBrandResponse;

import java.util.List;

/**
*  ProductBrandService 接口
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
public interface ProductBrandService extends IService<ProductBrand> {

    /**
     * 品牌分页列表
     * @param pageParamRequest 分页参数
     * @return PageInfo
     */
    PageInfo<ProductBrandListResponse> getAdminPage(PageParamRequest pageParamRequest);

    /**
     * 添加品牌
     * @param request 添加参数
     * @return Boolean
     */
    Boolean add(ProductBrandRequest request);

    /**
     * 删除品牌
     * @param id 品牌ID
     * @return Boolean
     */
    Boolean delete(Integer id);

    /**
     * 编辑品牌
     * @param request 修改参数
     * @return Boolean
     */
    Boolean edit(ProductBrandRequest request);

    /**
     * 修改品牌显示状态
     * @param id 品牌ID
     * @return Boolean
     */
    Boolean updateShowStatus(Integer id);

    /**
     * 品牌缓存列表(全部)
     * @return List
     */
    List<ProductBrandResponse> getCacheAllList();

    /**
     * 通过分类查询品牌分页列表
     * @param request 查询参数
     * @param pageParamRequest 分页参数
     * @return PageInfo
     */
    PageInfo<ProductBrandResponse> getPageListByCategory(BrandCategorySearchRequest request, PageParamRequest pageParamRequest);

    /**
     * 根据ID列表获取品牌列表
     *
     * @param brandIdList 品牌ID列表
     */
    List<ProductBrand> findByIdList(List<Integer> brandIdList);
}
