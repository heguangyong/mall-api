package com.zbkj.service.service.impl;

import com.alibaba.fastjson.JSONException;
import com.github.pagehelper.PageInfo;
import com.zbkj.common.model.admin.SystemAdmin;
import com.zbkj.common.request.*;
import com.zbkj.common.response.*;
import com.zbkj.common.utils.SecurityUtil;
import com.zbkj.common.vo.MyRecord;
import com.zbkj.service.service.ProductManagerService;
import com.zbkj.service.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

/**
 * 订单管理 service 调用业务
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
@Service
public class ProductManagerServiceImpl implements ProductManagerService {

    @Autowired
    private ProductService productService;

    /**
     * 获取产品列表Admin
     *
     * @param request          筛选参数
     * @return PageInfo
     */
    @Override
    public PageInfo<AdminProductListResponse> getAdminList(MerProductSearchRequest request) {
        SystemAdmin admin = SecurityUtil.getLoginUserVo().getUser();
        return productService.getAdminList(request, admin);
    }

    /**
     * 新增产品
     *
     * @param request 新增产品request对象
     * @return 新增结果
     */
    @Override
    public Boolean save(ProductAddRequest request) {
        return productService.save(request);
    }


    /**
     * 更新商品信息
     *
     * @param productRequest 商品参数
     * @return 更新结果
     */
    @Override
    public Boolean update(ProductAddRequest productRequest) {
        SystemAdmin admin = SecurityUtil.getLoginUserVo().getUser();
        return productService.update(productRequest, admin);
    }

    /**
     * 商品详情（管理端）
     *
     * @param id 商品id
     * @return ProductInfoResponse
     */
    @Override
    public ProductInfoResponse getInfo(Integer id) {
        SystemAdmin admin = SecurityUtil.getLoginUserVo().getUser();
        return productService.getInfo(id, admin);
    }

    /**
     * 根据商品tabs获取对应类型的产品数量
     *
     * @return List
     */
    @Override
    public List<ProductTabsHeaderResponse> getTabsHeader(MerProductTabsHeaderRequest request) {
        SystemAdmin admin = SecurityUtil.getLoginUserVo().getUser();
        return productService.getTabsHeader(request, admin);
    }

    /**
     * 根据其他平台url导入产品信息
     *
     * @param url 待导入平台url
     * @param tag 1=淘宝，2=京东，3=苏宁，4=拼多多， 5=天猫
     * @return ProductRequest
     */
    @Override
    public ProductResponseForCopyProduct importProductFrom99Api(String url, int tag) throws JSONException, IOException {
        SystemAdmin admin = SecurityUtil.getLoginUserVo().getUser();
        return productService.importProductFrom99Api(url, tag, admin);
    }

    /**
     * 商品回收/删除
     *
     * @param request 删除参数
     * @return Boolean
     */
    @Override
    public Boolean deleteProduct(ProductDeleteRequest request) {
        SystemAdmin admin = SecurityUtil.getLoginUserVo().getUser();
        return productService.deleteProduct(request, admin);
    }

    /**
     * 恢复已删除的商品
     *
     * @param productId 商品id
     * @return 恢复结果
     */
    @Override
    public Boolean restoreProduct(Integer productId) {
        SystemAdmin admin = SecurityUtil.getLoginUserVo().getUser();
        return productService.restoreProduct(productId, admin);
    }

    /**
     * 下架
     *
     * @param id 商品id
     */
    @Override
    public Boolean offShelf(Integer id) {
        SystemAdmin admin = SecurityUtil.getLoginUserVo().getUser();
        return productService.offShelf(id, admin);
    }

    /**
     * 上架
     *
     * @param id 商品id
     * @return Boolean
     */
    @Override
    public Boolean putOnShelf(Integer id) {
        SystemAdmin admin = SecurityUtil.getLoginUserVo().getUser();
        return productService.putOnShelf(id, admin);
    }

    /**
     * 平台端商品分页列表
     *
     * @param request          查询参数
     * @return PageInfo
     */
    @Override
    public PageInfo<PlatformProductListResponse> getPlatformPageList(PlatProductSearchRequest request) {
        return productService.getPlatformPageList(request);
    }

    /**
     * 根据id集合以及活动上限加载商品数据
     *
     * @param ids id集合
     * @return 平台商品列表
     */
    @Override
    public List<PlatformProductListResponse> getPlatformListForIdsByLimit(List<Integer> ids) {
        return productService.getPlatformListForIdsByLimit(ids);
    }

    /**
     * 商品审核
     *
     * @param request 审核参数
     * @return Boolean
     */
    @Override
    public Boolean audit(ProductAuditRequest request) {
        return productService.audit(request);
    }

    /**
     * 强制下架商品
     *
     * @param request 商品id参数
     * @return Boolean
     */
    @Override
    public Boolean forceDown(ProductForceDownRequest request) {
        return productService.forceDown(request);
    }

    /**
     * 平台端获取商品表头数量
     *
     * @return List
     */
    @Override
    public List<ProductTabsHeaderResponse> getPlatformTabsHeader(PlatProductTabsHeaderRequest request) {
        return productService.getPlatformTabsHeader(request);
    }

    /**
     * 平台端商品编辑
     *
     * @param request 商品编辑参数
     * @return Boolean
     */
    @Override
    public Boolean platUpdate(ProductPlatUpdateRequest request) {
        return productService.platUpdate(request);
    }

    /**
     * 商品提审
     */
    @Override
    public Boolean submitAudit(ProductSubmitAuditRequest request) {
        SystemAdmin admin = SecurityUtil.getLoginUserVo().getUser();
        return productService.submitAudit(request, admin);
    }

    /**
     * 快捷添加库存
     *
     * @param request 添加库存参数
     * @return Boolean
     */
    @Override
    public Boolean quickAddStock(ProductAddStockRequest request) {
        SystemAdmin admin = SecurityUtil.getLoginUserVo().getUser();
        return productService.quickAddStock(request, admin);
    }

    /**
     * 商品免审编辑
     *
     * @param request 商品免审编辑参数
     * @return Boolean
     */
    @Override
    public Boolean reviewFreeEdit(ProductReviewFreeEditRequest request) {
        SystemAdmin admin = SecurityUtil.getLoginUserVo().getUser();
        return productService.reviewFreeEdit(request, admin);
    }

    /**
     * 获取复制商品配置
     *
     * @return copyType 复制类型：1：一号通
     * copyNum 复制条数(一号通类型下有值)
     */
    @Override
    public MyRecord copyConfig() {
        SystemAdmin admin = SecurityUtil.getLoginUserVo().getUser();
        return productService.copyConfig(admin);
    }

    /**
     * 复制平台商品
     *
     * @param url 商品链接
     * @return MyRecord
     */
    @Override
    public ProductResponseForCopyProduct copyProduct(String url) {
        SystemAdmin admin = SecurityUtil.getLoginUserVo().getUser();
        return productService.copyProduct(url, admin);
    }

    /**
     * 商品搜索分页列表（活动）
     *
     * @param request     搜索参数
     * @return PageInfo
     */
    @Override
    public PageInfo<ProductActivityResponse> getActivitySearchPage(ProductActivitySearchRequest request) {
        SystemAdmin admin = SecurityUtil.getLoginUserVo().getUser();
        return productService.getActivitySearchPage(request, admin);
    }

    /**
     * 商品搜索分页列表（营销）
     *
     * @param request     搜索参数
     * @return PageInfo
     */
    @Override
    public PageInfo<ProductMarketingResponse> getMarketingSearchPage(PlatProductMarketingSearchRequest request) {
        SystemAdmin admin = SecurityUtil.getLoginUserVo().getUser();
        return productService.getMarketingSearchPage(request, admin);
    }

    /**
     * 商品搜索分页列表（活动）商户端
     *
     * @param request     搜索参数
     * @return PageInfo
     */
    @Override
    public PageInfo<ProductActivityResponse> getActivitySearchPageByMerchant(ProductActivitySearchRequest request) {
        SystemAdmin admin = SecurityUtil.getLoginUserVo().getUser();
        return productService.getActivitySearchPageByMerchant(request, admin);
    }

    /**
     * 商品搜索分页列表（营销）商户端
     *
     * @param request     搜索参数
     * @return PageInfo
     */
    @Override
    public PageInfo<ProductMarketingResponse> getMarketingSearchPageByMerchant(MerProductMarketingSearchRequest request) {
        SystemAdmin admin = SecurityUtil.getLoginUserVo().getUser();
        return productService.getMarketingSearchPageByMerchant(request, admin);
    }

    /**
     * 设置运费模板
     */
    @Override
    public Boolean setFreightTemplate(ProductFreightTemplateRequest request) {
        SystemAdmin admin = SecurityUtil.getLoginUserVo().getUser();
        return productService.setFreightTemplate(request, admin);
    }

    /**
     * 设置佣金
     */
    @Override
    public Boolean setBrokerage(ProductSetBrokerageRequest request) {
        SystemAdmin admin = SecurityUtil.getLoginUserVo().getUser();
        return productService.setBrokerage(request, admin);
    }

    /**
     * 添加回馈券
     */
    @Override
    public Boolean addFeedbackCoupons(ProductAddFeedbackCouponsRequest request) {
        SystemAdmin admin = SecurityUtil.getLoginUserVo().getUser();
        return productService.addFeedbackCoupons(request, admin);
    }

    /**
     * 批量上架商品
     *
     * @param idList 商品ID列表
     */
    @Override
    public Boolean batchUp(List<Integer> idList) {
        SystemAdmin admin = SecurityUtil.getLoginUserVo().getUser();
        return productService.batchUp(idList, admin);
    }

    /**
     * 批量商品下架
     *
     * @param idList 商品ID列表
     */
    @Override
    public Boolean batchDown(List<Integer> idList) {
        SystemAdmin admin = SecurityUtil.getLoginUserVo().getUser();
        return productService.batchDown(idList, admin);
    }

    /**
     * 批量设置运费模板
     */
    @Override
    public Boolean batchSetFreightTemplate(BatchSetProductFreightTemplateRequest request) {
        SystemAdmin admin = SecurityUtil.getLoginUserVo().getUser();
        return productService.batchSetFreightTemplate(request, admin);
    }

    /**
     * 批量设置佣金
     */
    @Override
    public Boolean batchSetBrokerage(BatchSetProductBrokerageRequest request) {
        SystemAdmin admin = SecurityUtil.getLoginUserVo().getUser();
        return productService.batchSetBrokerage(request, admin);
    }

    /**
     * 批量添加回馈券
     */
    @Override
    public Boolean batchAddFeedbackCoupons(BatchAddProductFeedbackCouponsRequest request) {
        SystemAdmin admin = SecurityUtil.getLoginUserVo().getUser();
        return productService.batchAddFeedbackCoupons(request, admin);
    }

    /**
     * 商品批量加入回收站
     *
     * @param idList 商品ID列表
     */
    @Override
    public Boolean batchRecycle(List<Integer> idList) {
        SystemAdmin admin = SecurityUtil.getLoginUserVo().getUser();
        return productService.batchRecycle(idList, admin);
    }

    /**
     * 批量删除商品
     *
     * @param idList 商品ID列表
     */
    @Override
    public Boolean batchDelete(List<Integer> idList) {
        SystemAdmin admin = SecurityUtil.getLoginUserVo().getUser();
        return productService.batchDelete(idList, admin);
    }

    /**
     * 批量恢复回收站商品
     *
     * @param idList 商品ID列表
     */
    @Override
    public Boolean batchRestore(List<Integer> idList) {
        SystemAdmin admin = SecurityUtil.getLoginUserVo().getUser();
        return productService.batchRestore(idList, admin);
    }

    /**
     * 批量提审商品
     *
     * @param idList 商品ID列表
     */
    @Override
    public Boolean batchSubmitAudit(List<Integer> idList) {
        SystemAdmin admin = SecurityUtil.getLoginUserVo().getUser();
        return productService.batchSubmitAudit(idList, admin);
    }

    /**
     * 平台端批量设置虚拟销量
     */
    @Override
    public Boolean platBatchSetVirtualSales(BatchSetVirtualSalesRequest request) {
        return productService.platBatchSetVirtualSales(request);
    }

    /**
     * 平台端批量商品审核
     */
    @Override
    public Boolean batchAudit(BatchProductAuditRequest request) {
        return productService.batchAudit(request);
    }
}

