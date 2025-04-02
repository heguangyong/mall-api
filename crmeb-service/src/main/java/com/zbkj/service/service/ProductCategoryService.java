package com.zbkj.service.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zbkj.common.model.product.ProductCategory;
import com.zbkj.common.request.ProductCategoryRequest;
import com.zbkj.common.vo.ProCategoryCacheVo;

import java.util.List;
import java.util.Map;

/**
*  ProductCategoryService 接口
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
public interface ProductCategoryService extends IService<ProductCategory> {

    /**
     * 获取分类列表
     */
    List<ProductCategory> getAdminList();

    /**
     * 添加商品分类
     * @param request 添加参数
     * @return Boolean
     */
    Boolean add(ProductCategoryRequest request);

    /**
     * 删除分类
     * @param id 分类ID
     * @return Boolean
     */
    Boolean delete(Integer id);

    /**
     * 修改分类
     * @param request 修改参数
     * @return Boolean
     */
    Boolean edit(ProductCategoryRequest request);

    /**
     * 修改分类显示状态
     * @param id 分类ID
     * @return Boolean
     */
    Boolean updateShowStatus(Integer id);

    /**
     * 获取分类缓存树(平台端)
     * @return List<ProCategoryCacheVo>
     */
    List<ProCategoryCacheVo> getCacheTree();

    /**
     * 商户端分类缓存树
     * @return List<ProCategoryCacheVo>
     */
    List<ProCategoryCacheVo> getMerchantCacheTree();

    /**
     * 根据菜单id获取所有下级对象
     * @param pid 菜单id
     * @param level 分类级别
     * @return List<ProductCategory>
     */
    List<ProductCategory> findAllChildListByPid(Integer pid, Integer level);

    /**
     * 获取一级分类数据
     */
    List<ProCategoryCacheVo> getFrontFirstCategory();

    /**
     * 获取首页第三级分类数据
     * @param firstId 第一级分类id
     */
    List<ProductCategory> getHomeThirdCategory(Integer firstId);

    /**
     * 获取第三级分类数据
     * @param firstId 第一级分类id
     * @param limit 查询数量，0全部
     */
    List<ProductCategory> getThirdCategoryByFirstId(Integer firstId, Integer limit);

    /**
     * 通过分类id列表获取分类map
     * @param cateIdList 分类id列表
     * @return Map
     */
    Map<Integer, ProductCategory> getMapByIdList(List<Integer> cateIdList);

    /**
     * 获取分类名称通过Ids
     * @param proCategoryIds 分类ID字符，逗号分隔
     * @return 分类名称字符，逗号分隔
     */
    String getNameStrByIds(String proCategoryIds);

    /**
     * 通过子ID获取所有父ID
     * @param childIdList 子分类ID
     */
    List<Integer> findParentIdByChildIds(List<Integer> childIdList);

    List<ProductCategory> findByIdList(List<Integer> idList);
}
