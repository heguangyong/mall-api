package com.zbkj.service.service;

import com.github.pagehelper.PageInfo;
import com.zbkj.common.request.*;
import com.zbkj.common.response.AdminProductListResponse;
import com.zbkj.common.response.ProductInfoResponse;
import com.zbkj.common.response.ProductTabsHeaderResponse;

import java.util.List;

/**
 * ProductService 接口
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
public interface ProductEmployeeService {

    // ==================================================================================
    // ==================           商户端                   =============================
    // ==================================================================================

    /**
     * 获取产品列表Admin
     * @param request 筛选参数
     * @return PageInfo
     */
    PageInfo<AdminProductListResponse> getAdminList(MerProductSearchRequest request);

    /**
     * 商品回收/删除
     * @param request 删除参数
     * @return Boolean
     */
    Boolean deleteProduct(ProductDeleteRequest request);

    /**
     * 恢复已删除商品
     * @param productId 商品id
     * @return 恢复结果
     */
    Boolean restoreProduct(Integer productId);

    /**
     * 获取tabsHeader对应数量
     * @return List
     */
    List<ProductTabsHeaderResponse> getTabsHeader(MerProductTabsHeaderRequest request);

    /**
     * 下架
     * @param id 商品id
     */
    Boolean offShelf(Integer id);

    /**
     * 上架
     * @param id 商品id
     * @return Boolean
     */
    Boolean putOnShelf(Integer id);

    /**
     * 快捷添加库存
     * @param request 添加库存参数
     * @return Boolean
     */
    Boolean quickAddStock(ProductAddStockRequest request);

    /**
     * 商品免审编辑
     * @param request 商品免审编辑参数
     * @return Boolean
     */
    Boolean reviewFreeEdit(ProductReviewFreeEditRequest request);


    /**
     * 商品无状态判断编辑 谨慎调用
     * @param request 待编辑商品对象
     * @return 编辑结果
     */
    Boolean merchantEmployeeAnyTimeEdit(ProductAnyTimeEditRequest request);

    /**
     * 商品提审
     * @return Boolean
     */
    Boolean submitAudit(ProductSubmitAuditRequest request);

    /**
     * 商品详情（管理端）
     * @param id 商品id
     * @return ProductInfoResponse
     */
    ProductInfoResponse getInfo(Integer id);

    /**
     * 批量上架商品
     * @param idList 商品ID列表
     */
    Boolean batchUp(List<Integer> idList);

    /**
     * 批量商品下架
     * @param idList 商品ID列表
     */
    Boolean batchDown(List<Integer> idList);

    /**
     * 商品批量加入回收站
     * @param idList 商品ID列表
     */
    Boolean batchRecycle(List<Integer> idList);

    /**
     * 批量删除商品
     * @param idList 商品ID列表
     */
    Boolean batchDelete(List<Integer> idList);

    /**
     * 批量恢复回收站商品
     * @param idList 商品ID列表
     */
    Boolean batchRestore(List<Integer> idList);

    /**
     * 批量提审商品
     * @param idList 商品ID列表
     */
    Boolean batchSubmitAudit(List<Integer> idList);
}
