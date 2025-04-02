package com.zbkj.admin.service;

import com.zbkj.admin.vo.PcShoppingBaseConfigVo;
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
     * 获取PC商城基础配置
     */
    PcShoppingBaseConfigVo getBaseConfig();

    /**
     * 编辑PC商城基础配置
     */
    Boolean editBaseConfig(PcShoppingBaseConfigVo voRequest);

    /**
     * 获取PC商城首页banner
     */
    List<PcHomeBannerVo> getHomeBanner();

    /**
     * 添加PC商城首页banner
     */
    Boolean saveHomeBanner(List<PcHomeBannerVo> bannerVoList);

    /**
     * 获取首页推荐列表
     */
    List<PcHomeRecommendedVo> findHomeRecommendedList();

    /**
     * 添加PC商城首页推荐板块
     */
    Boolean addHomeRecommended(PcHomeRecommendedVo voRequest);

    /**
     * 编辑PC商城首页推荐板块
     */
    Boolean editHomeRecommended(PcHomeRecommendedVo voRequest);

    /**
     * 删除PC商城首页推荐板块
     */
    Boolean deleteHomeRecommended(Integer id);

    /**
     * PC商城首页推荐板块开关
     */
    Boolean switchHomeRecommended(Integer id);

    /**
     * 获取PC商城经营理念配置
     */
    List<PcPhilosophyVo> getPhilosophy();

    /**
     * 保存PC商城经营理念配置
     */
    Boolean savePhilosophy(List<PcPhilosophyVo> philosophyVoList);

//    /**
//     * 获取PC商城底部授权配置
//     */
//    PcBottomAuthorizeVo getBottomAuthorize();
//
//    /**
//     * 保存PC商城底部授权配置
//     */
//    Boolean saveBottomAuthorize(PcBottomAuthorizeVo voRequest);

    /**
     * 获取PC商城快捷入口配置
     */
    List<PcQuickEntryVo> getQuickEntry();

    /**
     * 保存PC商城快捷入口配置
     */
    Boolean saveQuickEntry(List<PcQuickEntryVo> voListRequest);

    /**
     * 获取商户PC商城设置
     */
    MerchantPcShoppingConfigVo getMerchantPcShoppingConfig();

    /**
     * 编辑商户PC商城设置
     */
    Boolean updateMerchantPcShoppingConfig(MerchantPcShoppingConfigVo voRequest);

    /**
     * 获取PC商城底部二维码配置
     */
    List<PcBottomQrCodeVo> getBottomQrCode();

    /**
     * 保存PC商城底部二维码配置
     */
    Boolean saveBottomQrCode(List<PcBottomQrCodeVo> voListRequest);

    /**
     * 获取PC商城友情链接配置
     */
    List<PcFriendlyLinkVo> getFriendlyLinks();

    /**
     * 保存PC商城友情链接配置
     */
    Boolean saveFriendlyLinks(List<PcFriendlyLinkVo> voListRequest);

    /**
     * 获取PC商城首页广告
     */
    PcHomeAdvertisementVo getHomeAdvertisement();

    /**
     * 编辑PC商城首页广告
     */
    Boolean editHomeAdvertisement(PcHomeAdvertisementVo voRequest);

    /**
     * 获取PC商城首页导航配置
     */
    List<PcHomeNavigationVo> getHomeNavigation();

    /**
     * 保存PC商城首页导航配置
     */
    Boolean saveHomeNavigation(List<PcHomeNavigationVo> voListRequest);
}
