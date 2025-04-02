package com.zbkj.service.service;

import com.alibaba.fastjson.JSONException;
import com.github.pagehelper.PageInfo;
import com.zbkj.common.request.*;
import com.zbkj.common.response.*;
import com.zbkj.common.vo.MyRecord;

import java.io.IOException;
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
public interface ProductManagerService {

    // ==================================================================================
    // ==================           商户端                   =============================
    // ==================================================================================

    /**
     * 获取产品列表Admin
     *
     * @param request          筛选参数
     * @return PageInfo
     */
    PageInfo<AdminProductListResponse> getAdminList(MerProductSearchRequest request);

    /**
     * 新增商品
     *
     * @param request 商品请求对象
     * @return Boolean
     */
    Boolean save(ProductAddRequest request);

    /**
     * 商品回收/删除
     *
     * @param request 删除参数
     * @return Boolean
     */
    Boolean deleteProduct(ProductDeleteRequest request);

    /**
     * 恢复已删除商品
     *
     * @param productId 商品id
     * @return 恢复结果
     */
    Boolean restoreProduct(Integer productId);

    /**
     * 更新商品信息
     *
     * @param productRequest 商品参数
     * @return 更新结果
     */
    Boolean update(ProductAddRequest productRequest);

    /**
     * 获取tabsHeader对应数量
     *
     * @return List
     */
    List<ProductTabsHeaderResponse> getTabsHeader(MerProductTabsHeaderRequest request);

    /**
     * 下架
     *
     * @param id 商品id
     */
    Boolean offShelf(Integer id);

    /**
     * 上架
     *
     * @param id 商品id
     * @return Boolean
     */
    Boolean putOnShelf(Integer id);

    /**
     * 快捷添加库存
     *
     * @param request 添加库存参数
     * @return Boolean
     */
    Boolean quickAddStock(ProductAddStockRequest request);

    /**
     * 商品免审编辑
     *
     * @param request 商品免审编辑参数
     * @return Boolean
     */
    Boolean reviewFreeEdit(ProductReviewFreeEditRequest request);

    /**
     * 商品提审
     *
     * @return Boolean
     */
    Boolean submitAudit(ProductSubmitAuditRequest request);


    // ==================================================================================
    // ==================           平台端                   =============================
    // ==================================================================================

    /**
     * 平台端商品分页列表
     *
     * @param request          查询参数
     * @return PageInfo
     */
    PageInfo<PlatformProductListResponse> getPlatformPageList(PlatProductSearchRequest request);

    /**
     * 根据id集合以及活动上限加载商品数据
     *
     * @param ids id集合
     * @return 平台商品列表
     */
    List<PlatformProductListResponse> getPlatformListForIdsByLimit(List<Integer> ids);

    /**
     * 商品审核
     *
     * @param request 审核参数
     * @return Boolean
     */
    Boolean audit(ProductAuditRequest request);

    /**
     * 平台端获取商品表头数量
     *
     * @return List
     */
    List<ProductTabsHeaderResponse> getPlatformTabsHeader(PlatProductTabsHeaderRequest request);

    /**
     * 平台端商品编辑
     *
     * @param request 商品编辑参数
     * @return Boolean
     */
    Boolean platUpdate(ProductPlatUpdateRequest request);


    // ==================================================================================
    // ==================           公用部分                 =============================
    // ==================================================================================

    /**
     * 商品详情（管理端）
     *
     * @param id 商品id
     * @return ProductInfoResponse
     */
    ProductInfoResponse getInfo(Integer id);

    // ==================================================================================
    // ==================           移动端                   =============================
    // ==================================================================================

    /**
     * 根据其他平台url导入产品信息
     *
     * @param url 待倒入平台的url
     * @param tag 待导入平台标识
     * @return 待导入的商品信息
     */
    ProductResponseForCopyProduct importProductFrom99Api(String url, int tag) throws IOException, JSONException;

    /**
     * 强制下加商品
     *
     * @param request 商品id参数
     * @return Boolean
     */
    Boolean forceDown(ProductForceDownRequest request);

    /**
     * 获取复制商品配置
     *
     * @return copyType 复制类型：1：一号通
     * copyNum 复制条数(一号通类型下有值)
     */
    MyRecord copyConfig();

    /**
     * 复制平台商品
     *
     * @param url 商品链接
     * @return MyRecord
     */
    ProductResponseForCopyProduct copyProduct(String url);

    /**
     * 商品搜索分页列表（活动）
     *
     * @param request     搜索参数
     * @return PageInfo
     */
    PageInfo<ProductActivityResponse> getActivitySearchPage(ProductActivitySearchRequest request);

    /**
     * 商品搜索分页列表（营销）
     *
     * @param request     搜索参数
     * @return PageInfo
     */
    PageInfo<ProductMarketingResponse> getMarketingSearchPage(PlatProductMarketingSearchRequest request);

    /**
     * 商品搜索分页列表（活动）商户端
     *
     * @param request     搜索参数
     * @return PageInfo
     */
    PageInfo<ProductActivityResponse> getActivitySearchPageByMerchant(ProductActivitySearchRequest request);

    /**
     * 商品搜索分页列表（营销）商户端
     *
     * @param request     搜索参数
     * @return PageInfo
     */
    PageInfo<ProductMarketingResponse> getMarketingSearchPageByMerchant(MerProductMarketingSearchRequest request);

    /**
     * 设置运费模板
     */
    Boolean setFreightTemplate(ProductFreightTemplateRequest request);

    /**
     * 设置佣金
     */
    Boolean setBrokerage(ProductSetBrokerageRequest request);

    /**
     * 添加回馈券
     */
    Boolean addFeedbackCoupons(ProductAddFeedbackCouponsRequest request);

    /**
     * 批量上架商品
     *
     * @param idList 商品ID列表
     */
    Boolean batchUp(List<Integer> idList);

    /**
     * 批量商品下架
     *
     * @param idList 商品ID列表
     */
    Boolean batchDown(List<Integer> idList);

    /**
     * 批量设置运费模板
     */
    Boolean batchSetFreightTemplate(BatchSetProductFreightTemplateRequest request);

    /**
     * 批量设置佣金
     */
    Boolean batchSetBrokerage(BatchSetProductBrokerageRequest request);

    /**
     * 批量添加回馈券
     */
    Boolean batchAddFeedbackCoupons(BatchAddProductFeedbackCouponsRequest request);

    /**
     * 商品批量加入回收站
     *
     * @param idList 商品ID列表
     */
    Boolean batchRecycle(List<Integer> idList);

    /**
     * 批量删除商品
     *
     * @param idList 商品ID列表
     */
    Boolean batchDelete(List<Integer> idList);

    /**
     * 批量恢复回收站商品
     *
     * @param idList 商品ID列表
     */
    Boolean batchRestore(List<Integer> idList);

    /**
     * 批量提审商品
     *
     * @param idList 商品ID列表
     */
    Boolean batchSubmitAudit(List<Integer> idList);

    /**
     * 平台端批量设置虚拟销量
     */
    Boolean platBatchSetVirtualSales(BatchSetVirtualSalesRequest request);

    /**
     * 平台端批量商品审核
     */
    Boolean batchAudit(BatchProductAuditRequest request);
}
