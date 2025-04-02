package com.zbkj.service.service;

import com.zbkj.common.model.admin.SystemAdmin;
import com.zbkj.common.response.*;
import com.zbkj.common.vo.MyRecord;

import java.util.List;
import java.util.Map;

/**
 * 首页统计
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
public interface HomeService {

    /**
     * 首页数据
     * @return HomeRateResponse
     */
    HomeRateResponse indexMerchantDate(SystemAdmin systemAdmin);

    /**
     * 经营数据
     * @return HomeOperatingMerDataResponse
     */
    HomeOperatingMerDataResponse operatingMerchantData(SystemAdmin systemAdmin);

    /**
     * 平台端首页数据
     * @return PlatformHomeRateResponse
     */
    PlatformHomeRateResponse indexPlatformDate();

    /**
     * 平台端首页经营数据
     */
    HomeOperatingDataResponse operatingPlatformData();

    /**
     * 平台端首页获取用户渠道数据
     */
    List<UserChannelDataResponse> getUserChannelData();

    /**
     * 商户端商品支付排行榜
     */
    List<ProductRankingResponse> merchantProductPayRanking(SystemAdmin systemAdmin);

    /**
     * 商品浏览量排行榜
     */
    List<ProductRankingResponse> merchantProductPageviewRanking(SystemAdmin systemAdmin);
}
