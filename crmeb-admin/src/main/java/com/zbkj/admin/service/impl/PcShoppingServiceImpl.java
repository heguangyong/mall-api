package com.zbkj.admin.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONArray;
import com.zbkj.admin.service.PcShoppingService;
import com.zbkj.admin.vo.PcShoppingBaseConfigVo;
import com.zbkj.common.constants.Constants;
import com.zbkj.common.constants.GroupConfigConstants;
import com.zbkj.common.constants.SysConfigConstants;
import com.zbkj.common.exception.CrmebException;
import com.zbkj.common.model.admin.SystemAdmin;
import com.zbkj.common.model.merchant.Merchant;
import com.zbkj.common.model.product.Product;
import com.zbkj.common.model.system.GroupConfig;
import com.zbkj.common.result.CommonResultCode;
import com.zbkj.common.result.ProductResultCode;
import com.zbkj.common.utils.CrmebUtil;
import com.zbkj.common.utils.SecurityUtil;
import com.zbkj.common.vo.*;
import com.zbkj.service.service.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
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
    private TransactionTemplate transactionTemplate;
    @Autowired
    private GroupConfigService groupConfigService;
    @Autowired
    private SystemAttachmentService systemAttachmentService;
    @Autowired
    private ProductService productService;
    @Autowired
    private MerchantService merchantService;

    /**
     * 获取PC商城基础配置
     */
    @Override
    public PcShoppingBaseConfigVo getBaseConfig() {
        List<String> keyList = new ArrayList<>();
        keyList.add(SysConfigConstants.PC_SHOPPING_LEFT_TOP_LOGO);
        keyList.add(SysConfigConstants.PC_SHOPPING_GOOD_STORE_IMAGE);
        keyList.add(SysConfigConstants.PC_SHOPPING_GO_PHONE_QR_CODE_TYPE);
        keyList.add(SysConfigConstants.MERCHANT_APPLY_SWITCH);
        MyRecord myRecord = systemConfigService.getValuesByKeyList(keyList);
        PcShoppingBaseConfigVo vo = new PcShoppingBaseConfigVo();
        vo.setLeftTopLogo(myRecord.getStr(SysConfigConstants.PC_SHOPPING_LEFT_TOP_LOGO));
        vo.setGoodStoreImage(myRecord.getStr(SysConfigConstants.PC_SHOPPING_GOOD_STORE_IMAGE));
        vo.setGoPhoneQrCodeType(myRecord.getStr(SysConfigConstants.PC_SHOPPING_GO_PHONE_QR_CODE_TYPE));
        vo.setMerchantApplySwitch(myRecord.getStr(SysConfigConstants.MERCHANT_APPLY_SWITCH));
        return vo;
    }

    /**
     * 编辑PC商城基础配置
     */
    @Override
    public Boolean editBaseConfig(PcShoppingBaseConfigVo voRequest) {
        return transactionTemplate.execute(e -> {
            systemConfigService.updateOrSaveValueByName(SysConfigConstants.PC_SHOPPING_LEFT_TOP_LOGO, voRequest.getLeftTopLogo());
            systemConfigService.updateOrSaveValueByName(SysConfigConstants.PC_SHOPPING_GOOD_STORE_IMAGE, voRequest.getGoodStoreImage());
            systemConfigService.updateOrSaveValueByName(SysConfigConstants.PC_SHOPPING_GO_PHONE_QR_CODE_TYPE, voRequest.getGoPhoneQrCodeType());
            systemConfigService.updateOrSaveValueByName(SysConfigConstants.MERCHANT_APPLY_SWITCH, voRequest.getMerchantApplySwitch());
            return Boolean.TRUE;
        });
    }

    /**
     * 获取PC商城首页banner
     */
    @Override
    public List<PcHomeBannerVo> getHomeBanner() {
        List<GroupConfig> configList = groupConfigService.findByTag(GroupConfigConstants.TAG_HOME_BANNER, Constants.SORT_ASC, null);
        if (CollUtil.isEmpty(configList)) {
            return new ArrayList<>();
        }
        Iterator<GroupConfig> iterator = configList.iterator();
        List<PcHomeBannerVo> voList = new ArrayList<>();
        while (iterator.hasNext()) {
            GroupConfig config = iterator.next();
            PcHomeBannerVo vo = new PcHomeBannerVo();
            BeanUtils.copyProperties(config, vo);
            voList.add(vo);
        }
        return voList;
    }

    /**
     * 添加PC商城首页banner
     */
    @Override
    public Boolean saveHomeBanner(List<PcHomeBannerVo> bannerVoList) {
        if (CollUtil.isEmpty(bannerVoList) || bannerVoList.size() > 10) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "banner数量为1~10");
        }
        List<GroupConfig> configList = bannerVoList.stream().map(vo -> {
            GroupConfig groupConfig = new GroupConfig();
            groupConfig.setTag(GroupConfigConstants.TAG_HOME_BANNER);
            groupConfig.setImageUrl(vo.getImageUrl());
            groupConfig.setName(vo.getName());
            groupConfig.setLinkUrl(vo.getLinkUrl());
            groupConfig.setStatus(vo.getStatus());
            groupConfig.setSort(vo.getSort());
            return groupConfig;
        }).collect(Collectors.toList());
        return groupConfigService.saveList(configList);
    }

    /**
     * 获取首页推荐列表
     */
    @Override
    public List<PcHomeRecommendedVo> findHomeRecommendedList() {
        List<GroupConfig> configList = groupConfigService.findByTag(GroupConfigConstants.TAG_HOME_RECOMMEND, Constants.SORT_DESC, null);
        if (CollUtil.isEmpty(configList)) {
            return new ArrayList<>();
        }
        List<PcHomeRecommendedVo> voList = configList.stream().map(config -> {
            PcHomeRecommendedVo vo = new PcHomeRecommendedVo();
            vo.setId(config.getId());
            vo.setName(config.getName());
            vo.setImageUrl(config.getImageUrl());
            vo.setLinkUrl(config.getLinkUrl());
            vo.setSort(config.getSort());
            vo.setStatus(config.getStatus());
            vo.setProductAssociationType(config.getMessage());
            String data;
            if (config.getMessage().equals("product") || config.getMessage().equals("merchant")) {
                data = config.getExpand();
            } else {
                data = config.getValue();
            }
            vo.setData(data);
            return vo;
        }).collect(Collectors.toList());
        return voList;
    }

    /**
     * 添加PC商城首页推荐板块
     */
    @Override
    public Boolean addHomeRecommended(PcHomeRecommendedVo voRequest) {
        Integer count = groupConfigService.getCountByTag(GroupConfigConstants.TAG_HOME_RECOMMEND);
        if (count >= 10) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "PC首页推荐板块数量已达上限");
        }
        if (groupConfigService.isExistName(voRequest.getName(), GroupConfigConstants.TAG_HOME_RECOMMEND)) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "PC首页推荐板块名称不能重复");
        }
        GroupConfig groupConfig = new GroupConfig();
        groupConfig.setTag(GroupConfigConstants.TAG_HOME_RECOMMEND);
        groupConfig.setName(voRequest.getName());
        groupConfig.setImageUrl(systemAttachmentService.clearPrefix(voRequest.getImageUrl()));
        if (StrUtil.isNotBlank(voRequest.getLinkUrl())) {
            groupConfig.setLinkUrl(voRequest.getLinkUrl());
        }
        groupConfig.setStatus(voRequest.getStatus());
        groupConfig.setSort(voRequest.getSort());
        groupConfig.setMessage(voRequest.getProductAssociationType());
        switch (voRequest.getProductAssociationType()) {
            case "category":
            case "brand":
                groupConfig.setValue(voRequest.getData());
                break;
            case "merchant":
                groupConfig.setExpand(getMerchantProductAssociationDataStr(voRequest.getData()));
                break;
            case "product":
                groupConfig.setExpand(getProductAssociationDataStr(voRequest.getData()));
                break;
        }
        return groupConfigService.save(groupConfig);
    }

    /**
     * 编辑PC商城首页推荐板块
     */
    @Override
    public Boolean editHomeRecommended(PcHomeRecommendedVo voRequest) {
        if (ObjectUtil.isNull(voRequest.getId())) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "请选择首页推荐板块");
        }
        GroupConfig groupConfig = groupConfigService.getByIdException(voRequest.getId());
        if (!groupConfig.getTag().equals(GroupConfigConstants.TAG_HOME_RECOMMEND)) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "推荐板块数据不存在");
        }
        if (!groupConfig.getName().equals(voRequest.getName())) {
            if (groupConfigService.isExistName(voRequest.getName(), GroupConfigConstants.TAG_HOME_RECOMMEND)) {
                throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "PC首页推荐板块名称不能重复");
            }
            groupConfig.setName(voRequest.getName());
        }
        groupConfig.setImageUrl(systemAttachmentService.clearPrefix(voRequest.getImageUrl()));
        if (StrUtil.isNotBlank(voRequest.getLinkUrl())) {
            groupConfig.setLinkUrl(voRequest.getLinkUrl());
        }
        groupConfig.setStatus(voRequest.getStatus());
        groupConfig.setSort(voRequest.getSort());
        groupConfig.setMessage(voRequest.getProductAssociationType());
        switch (voRequest.getProductAssociationType()) {
            case "category":
            case "brand":
                groupConfig.setValue(voRequest.getData());
                break;
            case "merchant":
                groupConfig.setExpand(getMerchantProductAssociationDataStr(voRequest.getData()));
                break;
            case "product":
                groupConfig.setExpand(getProductAssociationDataStr(voRequest.getData()));
                break;
        }
        return groupConfigService.updateById(groupConfig);
    }

    /**
     * 删除PC商城首页推荐板块
     */
    @Override
    public Boolean deleteHomeRecommended(Integer id) {
        GroupConfig groupConfig = groupConfigService.getByIdException(id);
        if (!groupConfig.getTag().equals(GroupConfigConstants.TAG_HOME_RECOMMEND)) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "推荐板块数据不存在");
        }
        groupConfig.setIsDel(true);
        return groupConfigService.updateById(groupConfig);
    }

    /**
     * PC商城首页推荐板块开关
     */
    @Override
    public Boolean switchHomeRecommended(Integer id) {
        GroupConfig groupConfig = groupConfigService.getByIdException(id);
        if (!groupConfig.getTag().equals(GroupConfigConstants.TAG_HOME_RECOMMEND)) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "推荐板块数据不存在");
        }
        groupConfig.setStatus(!groupConfig.getStatus());
        return groupConfigService.updateById(groupConfig);
    }

    /**
     * 获取PC商城经营理念配置
     */
    @Override
    public List<PcPhilosophyVo> getPhilosophy() {
        List<GroupConfig> configList = groupConfigService.findByTag(GroupConfigConstants.TAG_PHILOSOPHY, Constants.SORT_ASC, null);
        if (CollUtil.isEmpty(configList)) {
            return new ArrayList<>();
        }
        Iterator<GroupConfig> iterator = configList.iterator();
        List<PcPhilosophyVo> voList = new ArrayList<>();
        while (iterator.hasNext()) {
            GroupConfig config = iterator.next();
            PcPhilosophyVo vo = new PcPhilosophyVo();
            BeanUtils.copyProperties(config, vo);
            voList.add(vo);
        }
        return voList;
    }

    /**
     * 保存PC商城经营理念配置
     */
    @Override
    public Boolean savePhilosophy(List<PcPhilosophyVo> philosophyVoList) {
        if (CollUtil.isEmpty(philosophyVoList) || philosophyVoList.size() != 4) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "经营理念数量为4");
        }
        List<GroupConfig> configList = philosophyVoList.stream().map(vo -> {
            GroupConfig groupConfig = new GroupConfig();
            groupConfig.setTag(GroupConfigConstants.TAG_PHILOSOPHY);
            groupConfig.setImageUrl(vo.getImageUrl());
            groupConfig.setName(vo.getName());
            groupConfig.setSort(vo.getSort());
            groupConfig.setStatus(true);
            return groupConfig;
        }).collect(Collectors.toList());
        return groupConfigService.saveList(configList);
    }

//    /**
//     * 获取PC商城底部授权配置
//     */
//    @Override
//    public PcBottomAuthorizeVo getBottomAuthorize() {
//        List<String> keyList = new ArrayList<>();
//        keyList.add(SysConfigConstants.PC_SHOPPING_AUTHORIZE_PHONE);
//        keyList.add(SysConfigConstants.PC_SHOPPING_AUTHORIZE_ADDRESS);
//        keyList.add(SysConfigConstants.PC_SHOPPING_AUTHORIZE_INFO);
//        keyList.add(SysConfigConstants.PC_SHOPPING_AUTHORIZE_FILING_NUM);
//        MyRecord myRecord = systemConfigService.getValuesByKeyList(keyList);
//        PcBottomAuthorizeVo vo = new PcBottomAuthorizeVo();
//        vo.setPhone(myRecord.getStr(SysConfigConstants.PC_SHOPPING_AUTHORIZE_PHONE));
//        vo.setAddress(myRecord.getStr(SysConfigConstants.PC_SHOPPING_AUTHORIZE_ADDRESS));
//        vo.setAuthInfo(myRecord.getStr(SysConfigConstants.PC_SHOPPING_AUTHORIZE_INFO));
//        vo.setFilingNum(myRecord.getStr(SysConfigConstants.PC_SHOPPING_AUTHORIZE_FILING_NUM));
//        return vo;
//    }
//
//    /**
//     * 保存PC商城底部授权配置
//     */
//    @Override
//    public Boolean saveBottomAuthorize(PcBottomAuthorizeVo voRequest) {
//        return transactionTemplate.execute(e -> {
//            String iphone = StrUtil.isNotBlank(voRequest.getPhone()) ? voRequest.getPhone() : "";
//            systemConfigService.updateOrSaveValueByName(SysConfigConstants.PC_SHOPPING_AUTHORIZE_PHONE, iphone);
//            String address = StrUtil.isNotBlank(voRequest.getAddress()) ? voRequest.getAddress() : "";
//            systemConfigService.updateOrSaveValueByName(SysConfigConstants.PC_SHOPPING_AUTHORIZE_ADDRESS, address);
//            systemConfigService.updateOrSaveValueByName(SysConfigConstants.PC_SHOPPING_AUTHORIZE_INFO, voRequest.getAuthInfo());
//            systemConfigService.updateOrSaveValueByName(SysConfigConstants.PC_SHOPPING_AUTHORIZE_FILING_NUM, voRequest.getFilingNum());
//            return Boolean.TRUE;
//        });
//    }

    /**
     * 获取PC商城快捷入口配置
     */
    @Override
    public List<PcQuickEntryVo> getQuickEntry() {
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

    /**
     * 保存PC商城快捷入口配置
     */
    @Override
    public Boolean saveQuickEntry(List<PcQuickEntryVo> voListRequest) {
        if (CollUtil.isEmpty(voListRequest)) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "配置数据不能为空");
        }
        List<GroupConfig> configList = voListRequest.stream().map(voRequest -> {
            GroupConfig config = new GroupConfig();
            config.setTag(GroupConfigConstants.TAG_QUICK_ENTRY);
            config.setName(voRequest.getName());
            config.setSort(voRequest.getSort());
            if (CollUtil.isNotEmpty(voRequest.getLinkList())) {
                config.setExpand(JSONArray.toJSONString(voRequest.getLinkList()));
            }
            config.setStatus(true);
            return config;
        }).collect(Collectors.toList());
        return groupConfigService.saveList(configList);
    }

    /**
     * 获取商户PC商城设置
     */
    @Override
    public MerchantPcShoppingConfigVo getMerchantPcShoppingConfig() {
        SystemAdmin admin = SecurityUtil.getLoginUserVo().getUser();
        Merchant merchant = merchantService.getByIdException(admin.getMerId());

        MerchantPcShoppingConfigVo configVo = new MerchantPcShoppingConfigVo();
        configVo.setPcLogo(merchant.getPcLogo());
        configVo.setPcGoodStoreCoverImage(merchant.getPcGoodStoreCoverImage());

        List<GroupConfig> bannerConfigList = groupConfigService.findByTag(GroupConfigConstants.TAG_MERCHANT_PC_BANNER, merchant.getId(), Constants.SORT_ASC, null);
        if (CollUtil.isNotEmpty(bannerConfigList)) {
            List<MerchantPcBannerVo> bannerVoList = bannerConfigList.stream().map(banner -> {
                MerchantPcBannerVo bannerVo = new MerchantPcBannerVo();
                bannerVo.setId(banner.getId());
                bannerVo.setImageUrl(banner.getImageUrl());
                bannerVo.setLinkUrl(banner.getLinkUrl());
                bannerVo.setSort(banner.getSort());
                bannerVo.setStatus(banner.getStatus());
                return bannerVo;
            }).collect(Collectors.toList());
            configVo.setBannerList(bannerVoList);
        }

        GroupConfig recommendProductConfig = groupConfigService.getOneByTagAndMerId(GroupConfigConstants.TAG_MERCHANT_PC_RECOMMEND_PRODUCT, merchant.getId());
        if (ObjectUtil.isNotNull(recommendProductConfig)) {
            configVo.setRecommendProductStr(recommendProductConfig.getValue());
            List<Integer> proIdList = CrmebUtil.stringToArray(recommendProductConfig.getValue());
            Map<Integer, Product> productMap = productService.getMapByIdList(proIdList);
            List<ChooseProductVo> productVoList = proIdList.stream().map(proId -> {
                Product product = productMap.get(proId);
                ChooseProductVo productVo = new ChooseProductVo();
                BeanUtils.copyProperties(product, productVo);
                return productVo;
            }).collect(Collectors.toList());
            configVo.setRecommendProductList(productVoList);
        }
        return configVo;
    }

    /**
     * 编辑商户PC商城设置
     * @param voRequest PC设置请求对象
     */
    @Override
    public Boolean updateMerchantPcShoppingConfig(MerchantPcShoppingConfigVo voRequest) {
        SystemAdmin admin = SecurityUtil.getLoginUserVo().getUser();
        Merchant merchant = merchantService.getByIdException(admin.getMerId());
        merchant.setPcLogo(systemAttachmentService.clearPrefix(voRequest.getPcLogo()));
        merchant.setPcGoodStoreCoverImage(systemAttachmentService.clearPrefix(voRequest.getPcGoodStoreCoverImage()));

        if (StrUtil.isNotBlank(voRequest.getRecommendProductStr())) {
            List<Integer> proIdList = CrmebUtil.stringToArray(voRequest.getRecommendProductStr());
            for (int i = 0; i < proIdList.size(); ) {
                if (!productService.validatedCanUseById(proIdList.get(i))) {
                    proIdList.remove(i);
                    continue;
                }
                i++;
            }
            if (CollUtil.isEmpty(proIdList)) {
                voRequest.setRecommendProductStr("");
            } else {
                voRequest.setRecommendProductStr(StrUtil.join(",", proIdList));
            }
        }

        Boolean execute = transactionTemplate.execute(e -> {
            merchantService.updateById(merchant);
            if (CollUtil.isNotEmpty(voRequest.getBannerList())) {
                saveMerchantPcBanner(voRequest.getBannerList(), merchant.getId());
            }

            if (StrUtil.isNotBlank(voRequest.getRecommendProductStr())) {
                GroupConfig groupConfig = new GroupConfig();
                groupConfig.setTag(GroupConfigConstants.TAG_MERCHANT_PC_RECOMMEND_PRODUCT);
                groupConfig.setMerId(merchant.getId());
                groupConfig.setValue(voRequest.getRecommendProductStr());
                groupConfigService.saveOne(groupConfig);
            } else {
                groupConfigService.deleteByTagAndMerId(GroupConfigConstants.TAG_MERCHANT_PC_RECOMMEND_PRODUCT, merchant.getId());
            }
            return Boolean.TRUE;
        });

        return execute;
    }

    /**
     * 获取PC商城底部二维码配置
     */
    @Override
    public List<PcBottomQrCodeVo> getBottomQrCode() {
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

    /**
     * 保存PC商城底部二维码配置
     */
    @Override
    public Boolean saveBottomQrCode(List<PcBottomQrCodeVo> voListRequest) {
        if (CollUtil.isEmpty(voListRequest)) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "配置数据不能为空");
        }
        List<GroupConfig> configList = voListRequest.stream().map(voRequest -> {
            GroupConfig config = new GroupConfig();
            config.setTag(GroupConfigConstants.TAG_BOTTOM_QR_CODE);
            config.setName(voRequest.getName());
            config.setImageUrl(voRequest.getImageUrl());
            config.setSort(voRequest.getSort());
            config.setStatus(true);
            return config;
        }).collect(Collectors.toList());
        return groupConfigService.saveList(configList);
    }

    /**
     * 添加PC商城首页banner
     */
    private Boolean saveMerchantPcBanner(List<MerchantPcBannerVo> bannerVoList, Integer merId) {
        if (CollUtil.isEmpty(bannerVoList) || bannerVoList.size() > 10) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "banner数量为1~10");
        }
        List<GroupConfig> configList = bannerVoList.stream().map(vo -> {
            GroupConfig groupConfig = new GroupConfig();
            groupConfig.setTag(GroupConfigConstants.TAG_MERCHANT_PC_BANNER);
            groupConfig.setMerId(merId);
            groupConfig.setImageUrl(vo.getImageUrl());
            groupConfig.setLinkUrl(vo.getLinkUrl());
            groupConfig.setStatus(vo.getStatus());
            groupConfig.setSort(vo.getSort());
            return groupConfig;
        }).collect(Collectors.toList());
        return groupConfigService.saveList(configList);
    }

    /**
     * 获取PC商城友情链接配置
     */
    @Override
    public List<PcFriendlyLinkVo> getFriendlyLinks() {
        List<GroupConfig> configList = groupConfigService.findByTag(GroupConfigConstants.TAG_FRIENDLY_LINKS, Constants.SORT_ASC, null);
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

    /**
     * 保存PC商城友情链接配置
     */
    @Override
    public Boolean saveFriendlyLinks(List<PcFriendlyLinkVo> voListRequest) {
        if (CollUtil.isEmpty(voListRequest)) {
            groupConfigService.deleteByTag(GroupConfigConstants.TAG_FRIENDLY_LINKS, 0);
            return Boolean.TRUE;
        }
        List<GroupConfig> configList = voListRequest.stream().map(vo -> {
            GroupConfig groupConfig = new GroupConfig();
            groupConfig.setTag(GroupConfigConstants.TAG_FRIENDLY_LINKS);
            groupConfig.setMerId(0);
            groupConfig.setName(vo.getName());
            groupConfig.setLinkUrl(vo.getLinkUrl());
            groupConfig.setSort(vo.getSort());
            groupConfig.setStatus(true);
            return groupConfig;
        }).collect(Collectors.toList());
        return groupConfigService.saveList(configList);
    }

    /**
     * 获取PC商城首页广告
     */
    @Override
    public PcHomeAdvertisementVo getHomeAdvertisement() {
        GroupConfig config = groupConfigService.getOneByTagAndMerId(GroupConfigConstants.TAG_HOME_ADVERTISEMENT, 0);
        PcHomeAdvertisementVo advertisementVo = new PcHomeAdvertisementVo();
        if (ObjectUtil.isNotNull(config)) {
            advertisementVo.setId(config.getId());
            advertisementVo.setImageUrl(config.getImageUrl());
            if (StrUtil.isNotBlank(config.getLinkUrl())) {
                advertisementVo.setLinkUrl(config.getLinkUrl());
            }
            advertisementVo.setStatus(config.getStatus());
        }
        return advertisementVo;
    }

    /**
     * 编辑PC商城首页广告
     */
    @Override
    public Boolean editHomeAdvertisement(PcHomeAdvertisementVo voRequest) {
        if (StrUtil.isBlank(voRequest.getImageUrl())) {
            groupConfigService.deleteByTagAndMerId(GroupConfigConstants.TAG_HOME_ADVERTISEMENT, 0);
        }
        GroupConfig groupConfig = new GroupConfig();
        groupConfig.setTag(GroupConfigConstants.TAG_HOME_ADVERTISEMENT);
        groupConfig.setMerId(0);
        groupConfig.setImageUrl(voRequest.getImageUrl());
        groupConfig.setLinkUrl(voRequest.getLinkUrl());
        groupConfig.setStatus(voRequest.getStatus());
        return groupConfigService.saveOne(groupConfig);
    }

    /**
     * 获取PC商城首页导航配置
     */
    @Override
    public List<PcHomeNavigationVo> getHomeNavigation() {
        List<GroupConfig> configList = groupConfigService.findByTag(GroupConfigConstants.TAG_HOME_NAVIGATION, Constants.SORT_ASC, null);
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
            vo.setStatus(config.getStatus());
            voList.add(vo);
        }
        return voList;
    }

    /**
     * 保存PC商城首页导航配置
     */
    @Override
    public Boolean saveHomeNavigation(List<PcHomeNavigationVo> voListRequest) {
        if (CollUtil.isEmpty(voListRequest)) {
            groupConfigService.deleteByTag(GroupConfigConstants.TAG_HOME_NAVIGATION, 0);
            return Boolean.TRUE;
        }
//        if (voListRequest.size() > 10) {
//            throw new CrmebException("自定义首页导航最多10个");
//        }
//        long counted = voListRequest.stream().filter(PcHomeNavigationVo::getStatus).count();
//        if (counted > 6) {
//            throw new CrmebException("自定义首页导航最多开启6个");
//        }
        PcHomeNavigationVo pcHomeNavigationVo = voListRequest.stream().filter(e -> e.getName().length() > 6).findFirst().orElse(null);
        if (ObjectUtil.isNotNull(pcHomeNavigationVo)) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "自定义首页导航名称最长为6个字符");
        }
        List<GroupConfig> configList = voListRequest.stream().map(vo -> {
            GroupConfig groupConfig = new GroupConfig();
            groupConfig.setTag(GroupConfigConstants.TAG_HOME_NAVIGATION);
            groupConfig.setMerId(0);
            groupConfig.setName(vo.getName());
            groupConfig.setLinkUrl(vo.getLinkUrl());
            groupConfig.setSort(vo.getSort());
            groupConfig.setStatus(vo.getStatus());
            return groupConfig;
        }).collect(Collectors.toList());
        return groupConfigService.saveList(configList);
    }

    /**
     * 获取检查后的商品关联数据
     * @param data 商品数据
     */
    private String getProductAssociationDataStr(String data) {
        List<Integer> proIdList = CrmebUtil.stringToArray(data);
        List<Product> productList = productService.findByIds(proIdList);
        if (CollUtil.isEmpty(productList)) {
            throw new CrmebException(ProductResultCode.PRODUCT_NOT_EXIST, "商品关联数据异常，请选择正确的商品");
        }
        for (int i = 0; i < productList.size(); ) {
            Product product = productList.get(i);
            if (product.getIsDel() || product.getIsRecycle() || !product.getIsShow()) {
                productList.remove(i);
                continue;
            }
            i++;
        }
        if (CollUtil.isEmpty(productList)) {
            throw new CrmebException(ProductResultCode.PRODUCT_NOT_EXIST, "商品关联数据异常，请选择正确的商品");
        }
        return productList.stream().map(e -> e.getId().toString()).collect(Collectors.joining(","));
    }

    private String getMerchantProductAssociationDataStr(String data) {
        List<Integer> merIdList = CrmebUtil.stringToArray(data);
        // 可以加入商户校验：数量、状态
        return merIdList.stream().map(Object::toString).collect(Collectors.joining(","));
    }
}
