package com.zbkj.service.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.github.pagehelper.PageInfo;
import com.zbkj.common.model.product.ProductReply;
import com.zbkj.common.request.PageParamRequest;
import com.zbkj.common.request.ProductReplyCommentRequest;
import com.zbkj.common.request.ProductReplySearchRequest;
import com.zbkj.common.request.ProductReplyVirtualRequest;
import com.zbkj.common.response.ProductDetailReplyResponse;
import com.zbkj.common.response.ProductReplayCountResponse;
import com.zbkj.common.response.ProductReplyResponse;

/**
 * StoreProductReplyService 接口
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
public interface ProductReplyService extends IService<ProductReply> {

    /**
     * 平台端商品评论列表
     * @param request 请求参数
     * @return PageInfo
     */
    PageInfo<ProductReplyResponse> getAdminPage(ProductReplySearchRequest request);

    /**
     * 商户端商品评论分页列表
     * @param request 请求参数
     * @return PageInfo
     */
    PageInfo<ProductReplyResponse> getMerchantAdminPage(ProductReplySearchRequest request);

    /**
     * 添加虚拟评论
     * @param request 评论参数
     * @return 评论结果
     */
    Boolean virtualCreate(ProductReplyVirtualRequest request);

    /**
     * H5商品评论统计
     * @param productId 商品编号
     * @return MyRecord
     */
    ProductReplayCountResponse getH5Count(Integer productId);

    /**
     * H5商品详情评论信息
     * @param proId 商品编号
     * @return ProductDetailReplyResponse
     */
    ProductDetailReplyResponse getH5ProductReply(Integer proId);

    /**
     * 移动端商品评论列表
     *
     * @param proId            商品编号
     * @param type             评价等级|0=全部,1=好评,2=中评,3=差评
     * @param pageParamRequest 分页参数
     * @return PageInfo<ProductReplyResponse>
     */
    PageInfo<ProductReplyResponse> getH5List(Integer proId, Integer type, PageParamRequest pageParamRequest);

    /**
     * 删除评论
     * @param id 评论id
     * @return Boolean
     */
    Boolean delete(Integer id);

    /**
     * 商品评论回复
     * @param request 回复参数
     */
    Boolean comment(ProductReplyCommentRequest request);

    /**
     * 获取统计数据（好评、中评、差评）
     */
    Integer getCountByScore(Integer productId, String type);
}
