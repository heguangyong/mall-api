package com.zbkj.front.service;

import com.github.pagehelper.PageInfo;
import com.zbkj.common.request.PageParamRequest;
import com.zbkj.common.response.PcHomeRecommendedResponse;
import com.zbkj.common.response.PcShoppingConfigResponse;
import com.zbkj.common.response.ProductRecommendedResponse;
import com.zbkj.common.vo.*;

import java.util.List;

/**
 * Pc商城service
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
public interface PcShoppingService {

    /**
     * 获取首页配置
     */
    PcShoppingConfigResponse getHomeConfig();

    /**
     * 获取首页banner
     */
    List<PcHomeBannerVo> getHomeBanner();

    /**
     * 获取首页推荐板块
     */
    List<PcHomeRecommendedResponse> getHomeRecommended();

    /**
     * 推荐板块商品分页列表
     *
     * @param recommendId 推荐板块ID
     * @param pageRequest 分页参数
     */
    PageInfo<ProductRecommendedResponse> findRecommendedProductPage(Integer recommendId, PageParamRequest pageRequest);
}
