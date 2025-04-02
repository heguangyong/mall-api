package com.zbkj.admin.service;

import com.zbkj.common.response.HomeOperatingMerDataResponse;
import com.zbkj.common.response.HomeRateResponse;
import com.zbkj.common.response.ProductRankingResponse;

import java.util.List;

/**
 * 管理端 - 首页统计
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
public interface AdminHomeService {

    /**
     * 首页数据
     * @return HomeRateResponse
     */
    HomeRateResponse indexMerchantDate();

    /**
     * 经营数据
     * @return HomeOperatingMerDataResponse
     */
    HomeOperatingMerDataResponse operatingMerchantData();

    /**
     * 商户端商品支付排行榜
     */
    List<ProductRankingResponse> merchantProductPayRanking();

    /**
     * 商品浏览量排行榜
     */
    List<ProductRankingResponse> merchantProductPageviewRanking();
}
