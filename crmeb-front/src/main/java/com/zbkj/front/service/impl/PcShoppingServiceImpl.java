package com.zbkj.front.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONArray;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zbkj.common.constants.Constants;
import com.zbkj.common.constants.GroupConfigConstants;
import com.zbkj.common.constants.SysConfigConstants;
import com.zbkj.common.exception.CrmebException;
import com.zbkj.common.model.product.Product;
import com.zbkj.common.model.system.GroupConfig;
import com.zbkj.common.page.CommonPage;
import com.zbkj.common.request.PageParamRequest;
import com.zbkj.common.response.PcHomeRecommendedResponse;
import com.zbkj.common.response.PcShoppingConfigResponse;
import com.zbkj.common.response.ProductRecommendedResponse;
import com.zbkj.common.response.ProductTagsFrontResponse;
import com.zbkj.common.result.CommonResultCode;
import com.zbkj.common.vo.*;
import com.zbkj.front.service.PcShoppingService;
import com.zbkj.service.service.GroupConfigService;
import com.zbkj.service.service.ProductService;
import com.zbkj.service.service.ProductTagService;
import com.zbkj.service.service.SystemConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * PC商城service实现类
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
public class PcShoppingServiceImpl implements PcShoppingService {

    @Autowired
    private SystemConfigService systemConfigService;
    @Autowired
    private GroupConfigService groupConfigService;
    @Autowired
    private ProductService productService;
    @Autowired
    private ProductTagService productTagService;

    /**
     * 获取PC商城基础配置
     */
    @Override
    public PcShoppingConfigResponse getHomeConfig() {
        List<String> keyList = new ArrayList<>();
        keyList.add(SysConfigConstants.PC_SHOPPING_LEFT_TOP_LOGO);
        keyList.add(SysConfigConstants.PC_SHOPPING_GOOD_STORE_IMAGE);
        keyList.add(SysConfigConstants.PC_SHOPPING_GO_PHONE_QR_CODE_TYPE);
//        keyList.add(SysConfigConstants.PC_SHOPPING_AUTHORIZE_PHONE);
//        keyList.add(SysConfigConstants.PC_SHOPPING_AUTHORIZE_ADDRESS);
//        keyList.add(SysConfigConstants.PC_SHOPPING_AUTHORIZE_INFO);
//        keyList.add(SysConfigConstants.PC_SHOPPING_AUTHORIZE_FILING_NUM);
        MyRecord myRecord = systemConfigService.getValuesByKeyList(keyList);
        PcShoppingConfigResponse response = new PcShoppingConfigResponse();
        response.setLeftTopLogo(myRecord.getStr(SysConfigConstants.PC_SHOPPING_LEFT_TOP_LOGO));
        response.setGoodStoreImage(myRecord.getStr(SysConfigConstants.PC_SHOPPING_GOOD_STORE_IMAGE));
        response.setGoPhoneQrCodeType(myRecord.getStr(SysConfigConstants.PC_SHOPPING_GO_PHONE_QR_CODE_TYPE));
//        response.setPhone(myRecord.getStr(SysConfigConstants.PC_SHOPPING_AUTHORIZE_PHONE));
//        response.setAddress(myRecord.getStr(SysConfigConstants.PC_SHOPPING_AUTHORIZE_ADDRESS));
//        response.setAuthInfo(myRecord.getStr(SysConfigConstants.PC_SHOPPING_AUTHORIZE_INFO));
//        response.setFilingNum(myRecord.getStr(SysConfigConstants.PC_SHOPPING_AUTHORIZE_FILING_NUM));

        response.setPhilosophyList(getPhilosophyList());
        response.setFriendlyLinkList(getFriendlyLinkList());
        response.setQuickEntryList(getQuickEntryList());
        response.setQrCodeList(getBottomQrCodeList());
        response.setAdvertisement(getHomeAdvertisement());
        response.setHomeNavigationList(getHomeNavigationList());
        return response;
    }

    private List<PcHomeNavigationVo> getHomeNavigationList() {
        List<GroupConfig> configList = groupConfigService.findByTag(GroupConfigConstants.TAG_HOME_NAVIGATION, Constants.SORT_ASC, true);
        if (CollUtil.isEmpty(configList)) {
            return new ArrayList<>();
        }
        Iterator<GroupConfig> iterator = configList.iterator();
        List<PcHomeNavigationVo> voList = new ArrayList<>();
        while (iterator.hasNext()) {
            GroupConfig config = iterator.next();
            PcHomeNavigationVo vo = new PcHomeNavigationVo();
            vo.setId(config.getId());
            vo.setName(config.getName());
            vo.setLinkUrl(config.getLinkUrl());
            vo.setSort(config.getSort());
            voList.add(vo);
        }
        return voList;
    }

    /**
     * 获取首页banner
     */
    @Override
    public List<PcHomeBannerVo> getHomeBanner() {
        List<GroupConfig> configList = groupConfigService.findByTag(GroupConfigConstants.TAG_HOME_BANNER, Constants.SORT_ASC, true);
        if (CollUtil.isEmpty(configList)) {
            return new ArrayList<>();
        }
        Iterator<GroupConfig> iterator = configList.iterator();
        List<PcHomeBannerVo> voList = new ArrayList<>();
        while (iterator.hasNext()) {
            GroupConfig config = iterator.next();
            PcHomeBannerVo vo = new PcHomeBannerVo();
            vo.setId(config.getId());
            vo.setImageUrl(config.getImageUrl());
            vo.setName(config.getName());
            vo.setLinkUrl(config.getLinkUrl());
            vo.setSort(config.getSort());
            voList.add(vo);
        }
        return voList;
    }

    /**
     * 获取首页推荐板块
     */
    @Override
    public List<PcHomeRecommendedResponse> getHomeRecommended() {
        List<GroupConfig> configList = groupConfigService.findByTag(GroupConfigConstants.TAG_HOME_RECOMMEND, Constants.SORT_DESC, true);

        return configList.stream().map(config -> {
            PcHomeRecommendedResponse response = new PcHomeRecommendedResponse();
            response.setId(config.getId());
            response.setName(config.getName());
            response.setImageUrl(config.getImageUrl());
            response.setLinkUrl(config.getLinkUrl());
            response.setSort(config.getSort());

            List<Product> list = productService.findHomeRecommended(config.getMessage(), config.getValue(), config.getExpand(), true);
            List<ProductRecommendedResponse> recommendedResponseList = list.stream().map(p -> {
                ProductRecommendedResponse proResponse = new ProductRecommendedResponse();
                proResponse.setId(p.getId());
                proResponse.setImage(p.getImage());
                proResponse.setName(p.getName());
                proResponse.setPrice(p.getPrice());
                proResponse.setSales(p.getSales() + p.getFicti());
                proResponse.setIsPaidMember(p.getIsPaidMember());
                proResponse.setVipPrice(p.getVipPrice());
                // 设置商品标签
                ProductTagsFrontResponse productTagsFrontResponse = productTagService.setProductTagByProductTagsRules(p.getId(), p.getBrandId(), p.getMerId(), p.getCategoryId(), proResponse.getProductTags());
                proResponse.setProductTags(productTagsFrontResponse);
                return proResponse;
            }).collect(Collectors.toList());
            response.setProductList(recommendedResponseList);
            return response;
        }).collect(Collectors.toList());
    }

    /**
     * 推荐板块商品分页列表
     *
     * @param recommendId 推荐板块ID
     * @param pageRequest 分页参数
     */
    @Override
    public PageInfo<ProductRecommendedResponse> findRecommendedProductPage(Integer recommendId, PageParamRequest pageRequest) {
        GroupConfig groupConfig = groupConfigService.getByIdException(recommendId);
        if (!groupConfig.getTag().equals(GroupConfigConstants.TAG_HOME_RECOMMEND) || !groupConfig.getStatus()) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "推荐数据不存在");
        }
        Page<Product> page = PageHelper.startPage(pageRequest.getPage(), pageRequest.getLimit());
        List<Product> list = productService.findHomeRecommended(groupConfig.getMessage(), groupConfig.getValue(), groupConfig.getExpand(), false);
        if (CollUtil.isEmpty(list)) {
            return CommonPage.copyPageInfo(page, new ArrayList<>());
        }
        List<ProductRecommendedResponse> recommendedResponseList = list.stream().map(p -> {
            ProductRecommendedResponse proResponse = new ProductRecommendedResponse();
            proResponse.setId(p.getId());
            proResponse.setImage(p.getImage());
            proResponse.setName(p.getName());
            proResponse.setPrice(p.getPrice());
            proResponse.setSales(p.getSales() + p.getFicti());
            proResponse.setIsPaidMember(p.getIsPaidMember());
            proResponse.setVipPrice(p.getVipPrice());
            // 设置商品标签
            ProductTagsFrontResponse productTagsFrontResponse = productTagService.setProductTagByProductTagsRules(p.getId(), p.getBrandId(), p.getMerId(), p.getCategoryId(), proResponse.getProductTags());
            proResponse.setProductTags(productTagsFrontResponse);
            return proResponse;
        }).collect(Collectors.toList());
        return CommonPage.copyPageInfo(page, recommendedResponseList);
    }

    private PcHomeAdvertisementVo getHomeAdvertisement() {
        GroupConfig config = groupConfigService.getOneByTagAndMerId(GroupConfigConstants.TAG_HOME_ADVERTISEMENT, 0);
        PcHomeAdvertisementVo advertisementVo = new PcHomeAdvertisementVo();
        if (ObjectUtil.isNotNull(config) && config.getStatus()) {
            advertisementVo.setId(config.getId());
            advertisementVo.setImageUrl(config.getImageUrl());
            if (StrUtil.isNotBlank(config.getLinkUrl())) {
                advertisementVo.setLinkUrl(config.getLinkUrl());
            }
        }
        return advertisementVo;
    }

    private List<PcPhilosophyVo> getPhilosophyList() {
        List<GroupConfig> configList = groupConfigService.findByTag(GroupConfigConstants.TAG_PHILOSOPHY, Constants.SORT_ASC, true);
        if (CollUtil.isEmpty(configList)) {
            return new ArrayList<>();
        }
        Iterator<GroupConfig> iterator = configList.iterator();
        List<PcPhilosophyVo> voList = new ArrayList<>();
        while (iterator.hasNext()) {
            GroupConfig config = iterator.next();
            PcPhilosophyVo vo = new PcPhilosophyVo();
            vo.setId(config.getId());
            vo.setImageUrl(config.getImageUrl());
            vo.setName(config.getName());
            vo.setSort(config.getSort());
            voList.add(vo);
        }
        return voList;
    }

    private List<PcFriendlyLinkVo>  getFriendlyLinkList() {
        List<GroupConfig> configList = groupConfigService.findByTag(GroupConfigConstants.TAG_FRIENDLY_LINKS, Constants.SORT_ASC, true);
        if (CollUtil.isEmpty(configList)) {
            return new ArrayList<>();
        }
        Iterator<GroupConfig> iterator = configList.iterator();
        List<PcFriendlyLinkVo> voList = new ArrayList<>();
        while (iterator.hasNext()) {
            GroupConfig config = iterator.next();
            PcFriendlyLinkVo vo = new PcFriendlyLinkVo();
            vo.setId(config.getId());
            vo.setName(config.getName());
            vo.setLinkUrl(config.getLinkUrl());
            vo.setSort(config.getSort());
            voList.add(vo);
        }
        return voList;
    }

    private List<PcQuickEntryVo> getQuickEntryList() {
        List<GroupConfig> configList = groupConfigService.findByTag(GroupConfigConstants.TAG_QUICK_ENTRY, Constants.SORT_ASC, true);
        if (CollUtil.isEmpty(configList)) {
            return new ArrayList<>();
        }
        return configList.stream().map(confing -> {
            PcQuickEntryVo quickEntryVo = new PcQuickEntryVo();
            quickEntryVo.setId(confing.getId());
            quickEntryVo.setName(confing.getName());
            quickEntryVo.setSort(confing.getSort());
            if (StrUtil.isNotBlank(confing.getExpand())) {
                List<PcQuickEntryLinksVo> linksVoList = JSONArray.parseArray(confing.getExpand(), PcQuickEntryLinksVo.class);
                quickEntryVo.setLinkList(linksVoList);
            }
            return quickEntryVo;
        }).collect(Collectors.toList());
    }

    private List<PcBottomQrCodeVo> getBottomQrCodeList() {
        List<GroupConfig> codeConfigList = groupConfigService.findByTag(GroupConfigConstants.TAG_BOTTOM_QR_CODE, Constants.SORT_ASC, null);
        if (CollUtil.isEmpty(codeConfigList)) {
            return CollUtil.newArrayList();
        }
        return codeConfigList.stream().map(codeConfig -> {
            PcBottomQrCodeVo codeVo = new PcBottomQrCodeVo();
            codeVo.setId(codeConfig.getId());
            codeVo.setImageUrl(codeConfig.getImageUrl());
            codeVo.setSort(codeConfig.getSort());
            codeVo.setName(codeConfig.getName());
            return codeVo;
        }).collect(Collectors.toList());
    }
}
