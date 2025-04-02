package com.zbkj.service.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.github.pagehelper.PageInfo;
import com.zbkj.common.model.wechat.video.PayComponentDraftProduct;
import com.zbkj.common.request.PageParamRequest;
import com.zbkj.common.request.wxvedio.product.ComponentProductSearchRequest;
import com.zbkj.common.request.wxvedio.product.PayComponentDraftProductMerchantOperationReviewStatus;
import com.zbkj.common.request.wxvedio.product.PayComponentDraftProductPlatformOperationReviewStatus;
import com.zbkj.common.request.wxvedio.product.PayComponentProductAddRequest;

import java.util.List;

/**
 *
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
public interface PayComponentDraftProductService extends IService<PayComponentDraftProduct> {

    /**
     * 添加草稿商品
     * @param addRequest 新增商品请求参数
     * @return Boolean
     */
    Boolean add(PayComponentProductAddRequest addRequest);

    /**
     * 编辑草稿商品
     * @param addRequest 编辑草稿参数
     * @return 编辑结果
     */
    Boolean edit(PayComponentProductAddRequest addRequest);

    /**
     * 根据Product商品id获取草稿商品
     * @param proId 商品id
     * @return PayComponentDraftProduct
     */
    PayComponentDraftProduct getByProId(Integer proId);

    /**
     * 通过商品id删除草稿
     * @param proId 商品id
     */
    Boolean deleteByProId(Integer proId);

    /**
     * 商户端 微信审核之前草稿 微信审核成功之前的数据都在这个列表
     * @param request 搜索参数
     * @param pageParamRequest 分页参数
     * @return PageInfo
     */
    PageInfo<PayComponentDraftProduct> getCurrentMerchantAdminListBeforeWeChatReview(ComponentProductSearchRequest request, PageParamRequest pageParamRequest);

    /**
     * 平台端获取 商户提审 到 微信审核成功之前的数据列表
     * @param request 搜索参数
     * @param pageParamRequest 分页参数
     * @return PageInfo
     */
    PageInfo<PayComponentDraftProduct> getPlatformAdminListAfterMerchantReviewBeforeWeChatReview(ComponentProductSearchRequest request, PageParamRequest pageParamRequest);

    /**
     * 商品详情
     * @param id 商品id
     * @return
     */
    PayComponentDraftProduct getInfo(Integer id);

    /**
     * 商家角色草稿商品操作状态
     * @param reviewStatus 操作状态Request
     * @return 结果
     */
    Boolean OperationPlatformReviewStatusByMerchant(PayComponentDraftProductMerchantOperationReviewStatus reviewStatus);

    /**
     * 平台角色草稿商品操作状态
     * @param reviewStatus 操作状态Request
     * @return 结果
     */
    Boolean OperationPlatformReviewStatusByPlatform(PayComponentDraftProductPlatformOperationReviewStatus reviewStatus);

    /**
     * 根据固定属性查询草稿商品
     * @param id id
     * @param title 草稿商品名称
     * @return 查询结果
     */
    List<PayComponentDraftProduct> getDraftProductByOperation(Integer id, String title);
}
