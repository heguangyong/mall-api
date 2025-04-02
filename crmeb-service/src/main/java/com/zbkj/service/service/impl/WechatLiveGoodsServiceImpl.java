package com.zbkj.service.service.impl;

import cn.binarywang.wx.miniapp.api.WxMaService;
import cn.binarywang.wx.miniapp.bean.live.WxMaLiveGoodInfo;
import cn.binarywang.wx.miniapp.bean.live.WxMaLiveResult;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.URLUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.PageHelper;
import com.zbkj.common.enums.WechatMPLiveGoodsReviewStatusEnum;
import com.zbkj.common.exception.CrmebException;
import com.zbkj.common.exception.weixin.CRMEBWxError;
import com.zbkj.common.model.admin.SystemAdmin;
import com.zbkj.common.model.merchant.Merchant;
import com.zbkj.common.model.wechat.live.WechatLiveGoods;
import com.zbkj.common.request.PageParamRequest;
import com.zbkj.common.request.wxmplive.goods.WechatLiveGoodsAddRequest;
import com.zbkj.common.request.wxmplive.goods.WechatLiveGoodsEditRequest;
import com.zbkj.common.request.wxmplive.goods.WechatLiveGoodsSearchRequest;
import com.zbkj.common.utils.SecurityUtil;
import com.zbkj.service.dao.wechat.live.WechatLiveGoodsDao;
import com.zbkj.service.service.MerchantService;
import com.zbkj.service.service.SystemAttachmentService;
import com.zbkj.service.service.WechatLiveGoodsService;
import me.chanjar.weixin.common.enums.WxType;
import me.chanjar.weixin.common.error.WxErrorException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Auther: 大粽子
 * @Date: 2023/3/21 15:04
 * @Description: 小程序直播商品管理
 */
@Service
public class WechatLiveGoodsServiceImpl extends ServiceImpl<WechatLiveGoodsDao, WechatLiveGoods> implements WechatLiveGoodsService {
    private static final Logger logger = LoggerFactory.getLogger(WechatLiveGoodsServiceImpl.class);

    @Resource
    private WechatLiveGoodsDao dao;

    @Autowired
    private WxMaService wxMaService;

    @Autowired
    private MerchantService merchantService;

    @Autowired
    private SystemAttachmentService systemAttachmentService;

    /**
     * 列表
     *
     * @param request          请求参数
     * @param pageParamRequest 分页类参数
     * @return List<WechatLiveGoods>
     * @author dazongzi
     * @since 2023-03-27
     */
    @Override
    public List<WechatLiveGoods> getList(WechatLiveGoodsSearchRequest request, PageParamRequest pageParamRequest, Boolean isMer) {
        PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());

        //带 WechatLiveGoods 类的多条件查询
        LambdaQueryWrapper<WechatLiveGoods> lambdaQueryWrapper = Wrappers.lambdaQuery();
        if (ObjectUtil.isNotEmpty(request.getKeywords())) {
            String decodeKeywords = URLUtil.decode(request.getKeywords());
            lambdaQueryWrapper.eq(WechatLiveGoods::getId, decodeKeywords)
                    .or().eq(WechatLiveGoods::getProductId, decodeKeywords)
                    .or().like(WechatLiveGoods::getName, decodeKeywords)
                    .or().like(WechatLiveGoods::getGoodsId, decodeKeywords)
                    .or().like(WechatLiveGoods::getAuditId, decodeKeywords)
                    .or().like(WechatLiveGoods::getMerName, decodeKeywords);
        }
        if (ObjectUtil.isNotEmpty(request.getReviewStatus())) {
            lambdaQueryWrapper.eq(WechatLiveGoods::getReviewStatus, request.getReviewStatus());
        }

        if (ObjectUtil.isNotEmpty(request.getMerchant_type())) {
            lambdaQueryWrapper.eq(WechatLiveGoods::getMerType, request.getMerchant_type());
        }
        if (isMer) {
            lambdaQueryWrapper.eq(WechatLiveGoods::getMerId, SecurityUtil.getLoginUserVo().getUser().getMerId());
        }
        lambdaQueryWrapper.orderByDesc(WechatLiveGoods::getSort).orderByDesc(WechatLiveGoods::getId);
        return dao.selectList(lambdaQueryWrapper);
    }

    /**
     * 小程序直播添加并提审商品 平台审核
     *
     * @param id           待提审商品id
     * @param reviewStatus 审核状态 0=商户创建平台待审核，1=平台审核通过，2=平台审核失败,3=微信审核成功，4=微信审核失败
     * @param reviewReason 审核意见
     * @return 提审结果
     */
    @Override
    public Boolean platReview(Integer id, Integer reviewStatus, String reviewReason) {
        WechatLiveGoods wechatLiveGoods = dao.selectById(id);
        try {
            if (ObjectUtil.isEmpty(wechatLiveGoods)) throw new CrmebException("待审核商品未找到");

            if (WechatMPLiveGoodsReviewStatusEnum.PLAT_REVIEW_ERROR.getCode().equals(reviewStatus) &&
                    ObjectUtil.isEmpty(reviewReason)) {
                throw new CrmebException("平台审核失败原因不能为空");
            }
            wechatLiveGoods.setReviewStatus(reviewStatus);
            wechatLiveGoods.setReviewReason(reviewReason);
            if (reviewStatus.equals(WechatMPLiveGoodsReviewStatusEnum.PLAT_REVIEW_ERROR.getCode())) {
                dao.updateById(wechatLiveGoods);
            } else {
                // 提交给微信审核
                WxMaLiveGoodInfo wxMaLiveGoodInfo = new WxMaLiveGoodInfo();
                BeanUtils.copyProperties(wechatLiveGoods, wxMaLiveGoodInfo);
                WxMaLiveResult wxMaLiveResult = wxMaService.getLiveGoodsService().addGoods(wxMaLiveGoodInfo);
                // 更新微信端结果
                if (ObjectUtil.isNotNull(wxMaLiveResult.getAuditId())) {
                    wechatLiveGoods.setGoodsId(wxMaLiveResult.getGoodsId());
                    wechatLiveGoods.setAuditId(wxMaLiveResult.getAuditId());
                    wechatLiveGoods.setReviewStatus(WechatMPLiveGoodsReviewStatusEnum.PLAT_REVIEW_SUCCESS.getCode());
                } else {
                    wechatLiveGoods.setReviewStatus(WechatMPLiveGoodsReviewStatusEnum.WECHAT_REVIEW_ERROR.getCode());
                    wechatLiveGoods.setReviewReason("微信审核不通过");
                    logger.error("微信审核直播商品失败原因:{}", JSON.toJSONString(wechatLiveGoods));
                }
                dao.updateById(wechatLiveGoods);
            }
            return true;
        } catch (WxErrorException e) {
            CRMEBWxError crmebWxError = CRMEBWxError.fromJson(e.getError().getJson(), WxType.Open);
            wechatLiveGoods.setReviewStatus(WechatMPLiveGoodsReviewStatusEnum.WECHAT_REVIEW_ERROR.getCode());
            wechatLiveGoods.setReviewReason(crmebWxError.getErrmsg());
            dao.updateById(wechatLiveGoods);
            throw new CrmebException("微信小程序直播 - 平台提审！" + crmebWxError.getErrmsg());
        }
    }

    /**
     * 根据微信审核id获取直播商品
     *
     * @param auditId 微信审核id
     * @return 商品结果
     */
    @Override
    public WechatLiveGoods getByAuditId(Integer auditId) {
        LambdaQueryWrapper<WechatLiveGoods> lambdaQueryWrapper = Wrappers.lambdaQuery();
        lambdaQueryWrapper.eq(WechatLiveGoods::getAuditId, auditId);
        return dao.selectOne(lambdaQueryWrapper);
    }

    /**
     * 根据微信直播商品id 获取直播商品集合
     *
     * @param goodsIds 直播商品id集合
     * @return 直播商品集合
     */
    @Override
    public List<WechatLiveGoods> getByGoodsId(List<Integer> goodsIds) {
        LambdaQueryWrapper<WechatLiveGoods> lambdaQueryWrapper = Wrappers.lambdaQuery();
        lambdaQueryWrapper.in(WechatLiveGoods::getGoodsId, goodsIds);
        return dao.selectList(lambdaQueryWrapper);
    }

    /**
     * 商户重新提交商品审核
     *
     * @param id 直播商品id
     * @return 提交结果
     */
    @Override
    public String merchantAuditGoods(Integer id) {
        try {
            WechatLiveGoods liveGoods = getGoodInfo(id);
            if (!liveGoods.getReviewStatus().equals(WechatMPLiveGoodsReviewStatusEnum.PLAT_REVIEW_SUCCESS.getCode())) {
                throw new CrmebException("直播商品并非微信审核中 不能执行重新提交审核操作");
            }
            // 提交微信直播商品审核
            String auditId = wxMaService.getLiveGoodsService().auditGoods(liveGoods.getGoodsId());
            if (ObjectUtil.isEmpty(auditId)) throw new CrmebException("重新提交商品审核失败");

            // 更新本地状态
            liveGoods.setAuditId(Long.valueOf(auditId));
            dao.updateById(liveGoods);
        } catch (WxErrorException e) {
            logger.error("微信小程序直播 - 重新提交商品失败 {}", e.getError());
            throw new CrmebException("重新提交商品审核失败" + CRMEBWxError.fromJson(e.getError().getJson(), WxType.Open));
        }
        return null;
    }

    /**
     * 商户撤回商品审核
     * 审核状态 0=商户创建平台待审核，1=平台审核通过，2=平台审核失败,3=微信审核成功，4=微信审核失败
     * 撤回审核是微信提审状态下的操作，需要reviewStatus在大于2的前提下调用 或者是auditId有值的时候可以调用
     *
     * @param id 商品id
     * @return 撤回结果
     */
    @Override
    public Boolean merchantResetAudit(Integer id) {
        try {
            WechatLiveGoods wechatLiveGoods = getGoodInfo(id);
            // 微信审核中才可以调用此接口
            if (!wechatLiveGoods.getReviewStatus().equals(WechatMPLiveGoodsReviewStatusEnum.PLAT_REVIEW_SUCCESS.getCode())) {
                throw new CrmebException("直播商品状态并非微信审核中 不能执行撤回操作");
            }
            // 调用微信撤回商品审核状态
            boolean auditResult = wxMaService.getLiveGoodsService().resetAudit(Integer.valueOf(wechatLiveGoods.getAuditId().toString()),
                    wechatLiveGoods.getGoodsId());
            if (!auditResult) throw new CrmebException("微信小程序直播商品 撤回审核失败");

            wechatLiveGoods.setReviewStatus(WechatMPLiveGoodsReviewStatusEnum.CREATED.getCode());
            return dao.updateById(wechatLiveGoods) > 0;
        } catch (WxErrorException e) {
            logger.error("微信小程序直播 - 撤回商品审核 {}", e.getError());
            throw new CrmebException("微信小程序直播 - 撤回商品审核！" + CRMEBWxError.fromJson(e.getError().getJson(), WxType.Open));
        }
    }

    /**
     * 根据审核状态获取直播商品的审核状态并更新
     */
    @Override
    public void getGoodsWareHouse() {
        try {
            SystemAdmin admin = SecurityUtil.getLoginUserVo().getUser();
            LambdaQueryWrapper<WechatLiveGoods> wrapperQuery = Wrappers.lambdaQuery();
            wrapperQuery.eq(WechatLiveGoods::getReviewStatus, WechatMPLiveGoodsReviewStatusEnum.PLAT_REVIEW_SUCCESS.getCode());
            wrapperQuery.eq(WechatLiveGoods::getMerId, admin.getMerId());
            List<WechatLiveGoods> wechatLiveGoodsList = dao.selectList(wrapperQuery);
            List<Integer> goodsIds = wechatLiveGoodsList.stream().map(WechatLiveGoods::getGoodsId).distinct().collect(Collectors.toList());
            // 获取微信审核状态
            WxMaLiveResult goodsWareHouse = wxMaService.getLiveGoodsService().getGoodsWareHouse(goodsIds);
            for (WechatLiveGoods wechatLiveGoods : wechatLiveGoodsList) {
                List<WxMaLiveResult.Goods> collect = goodsWareHouse.getGoods().stream().filter(goods -> goods.getGoodsId().equals(wechatLiveGoods.getGoodsId())).collect(Collectors.toList());
                WxMaLiveResult.Goods good = collect.get(0);
                wechatLiveGoods.setAuditStatus(good.getAuditStatus()); // 0：未审核，1：审核中，2:审核通过，3审核失败
                wechatLiveGoods.setThirdPartyTag(Integer.valueOf(good.getThirdPartyTag()));
                wechatLiveGoods.setThirdPartyAppid(good.getThirdPartyAppid());
                wechatLiveGoods.setReviewStatus(WechatMPLiveGoodsReviewStatusEnum.WECHAT_REVIEW_SUCCESS.getCode());
            }
            updateBatchById(wechatLiveGoodsList);
        } catch (WxErrorException e) {
            logger.error("微信小程序直播 - 获取商品的信息与审核状态 {}", e.getError());
            throw new CrmebException("微信小程序直播 - 获取商品的信息与审核状态！" + CRMEBWxError.fromJson(e.getError().getJson(), WxType.Open));
        }
    }

    /**
     * 新增直播商品
     *
     * @param requests 新增商品对象
     * @return 创建结果
     */
    @Override
    public Boolean addGoods(List<WechatLiveGoodsAddRequest> requests) {
        // 获取当前登录人信息和商户信息
        SystemAdmin currentUser = SecurityUtil.getLoginUserVo().getUser();
        Merchant currentMerchantInfo = merchantService.getByIdException(currentUser.getMerId());

        List<WechatLiveGoods> wechatLiveGoodsList = new ArrayList<>();
        for (WechatLiveGoodsAddRequest request : requests) {
            WechatLiveGoods wechatLiveGoods = new WechatLiveGoods();
            BeanUtils.copyProperties(request, wechatLiveGoods);
            // 冗余商家信息到直播信息表
            wechatLiveGoods.setReviewStatus(WechatMPLiveGoodsReviewStatusEnum.BEFORE_REVIEW.getCode());
            wechatLiveGoods.setMerName(currentMerchantInfo.getName());
            wechatLiveGoods.setMerType(currentMerchantInfo.getTypeId());
            wechatLiveGoods.setMerId(currentMerchantInfo.getId());
            wechatLiveGoods.setCoverImgUrlLocal(systemAttachmentService.clearPrefix(wechatLiveGoods.getCoverImgUrlLocal()));
            wechatLiveGoodsList.add(wechatLiveGoods);
        }
        return saveBatch(wechatLiveGoodsList);
    }

    /**
     * 更新商品
     *
     * @param goods 待更新商品
     * @return 更新结果
     */
    @Override
    public Boolean updateGoods(WechatLiveGoodsEditRequest goods) {
        try {
            WechatLiveGoods currentWechatLiveGoods = dao.selectById(goods.getId());
            if (ObjectUtil.isNull(currentWechatLiveGoods)) {
                throw new CrmebException("直播商品不存在");
            }
            SystemAdmin admin = SecurityUtil.getLoginUserVo().getUser();
            if (!admin.getMerId().equals(currentWechatLiveGoods.getMerId())) {
                throw new CrmebException("直播商品不存在");
            }
            WxMaLiveGoodInfo wxMaLiveGoodInfo = new WxMaLiveGoodInfo();
            BeanUtils.copyProperties(goods, wxMaLiveGoodInfo);

            // 如果微信已经审核 先更新微信端数据
            if (currentWechatLiveGoods.getReviewStatus().equals(WechatMPLiveGoodsReviewStatusEnum.WECHAT_REVIEW_SUCCESS.getCode())) {
                wxMaLiveGoodInfo.setGoodsId(currentWechatLiveGoods.getGoodsId());
                boolean updateResult = wxMaService.getLiveGoodsService().updateGoods(wxMaLiveGoodInfo);
                if (!updateResult) {
                    logger.error("编辑直播商品失败:{}", JSON.toJSONString(currentWechatLiveGoods));
                }
            }

            // 本地更新 重置审核状态
            WechatLiveGoods localWechatLiveGoods = new WechatLiveGoods();
            BeanUtils.copyProperties(goods, localWechatLiveGoods);
            localWechatLiveGoods.setReviewReason(null).setReviewStatus(WechatMPLiveGoodsReviewStatusEnum.BEFORE_REVIEW.getCode());
            localWechatLiveGoods.setCoverImgUrlLocal(systemAttachmentService.clearPrefix(localWechatLiveGoods.getCoverImgUrlLocal()));
            return dao.updateById(localWechatLiveGoods) > 0;
        } catch (WxErrorException e) {
            logger.error("微信小程序直播 - 更新商品 {}", e.getError());
            throw new CrmebException("微信小程序直播 - 更新商品！" + CRMEBWxError.fromJson(e.getError().getJson(), WxType.Open));
        }
    }

    /**
     * 更新排序状态
     *
     * @param id   直播商品id
     * @param sort 直播商品排序
     * @return 执行排序结果
     */
    @Override
    public Boolean updateSort(Integer id, Integer sort) {
        WechatLiveGoods liveGoods = getById(id);
        if (ObjectUtil.isNull(liveGoods)) {
            throw new CrmebException("直播商品不存在");
        }
        SystemAdmin admin = SecurityUtil.getLoginUserVo().getUser();
        if (admin.getMerId() > 0 && !admin.getMerId().equals(liveGoods.getMerId())) {
            throw new CrmebException("直播商品不存在");
        }
        WechatLiveGoods wechatLiveGoods = new WechatLiveGoods().setId(id).setSort(sort);
        return dao.updateById(wechatLiveGoods) > 0;
    }

    /**
     * 提示：状态都在对应接口已经和本地更新，如果还需要批量调用商品列表，最多也是多一个商品信息同步的功能，此接扣暂放
     * 获取商品列表
     *
     * @param pageParamRequest 分页参数 分页大小，默认30，不超过100
     * @param status           商品状态，0：未审核。1：审核中，2：审核通过，3：审核驳回
     * @return 分页查询结果
     */
    @Override
    public WxMaLiveResult getApprovedGoods(PageParamRequest pageParamRequest, Integer status) {
        try {
            return wxMaService.getLiveGoodsService().getApprovedGoods(pageParamRequest.getPage(), pageParamRequest.getLimit(), status);
        } catch (WxErrorException e) {
            logger.error("微信小程序直播 - 获取商品列表 {}", e.getError());
            throw new CrmebException("微信小程序直播 - 获取商品列表！" + CRMEBWxError.fromJson(e.getError().getJson(), WxType.Open));
        }
    }

    /**
     * 删除商品
     *
     * @param id 待删除商品id
     * @return 删除结果
     */
    @Override
    public Boolean deleteGoods(Integer id) {
        SystemAdmin admin = SecurityUtil.getLoginUserVo().getUser();
        try {
            WechatLiveGoods wechatLiveGoods = dao.selectById(id);
            if (ObjectUtil.isNull(wechatLiveGoods)) {
                throw new CrmebException("直播商品不存在");
            }
            if (admin.getMerId() > 0 && !admin.getMerId().equals(wechatLiveGoods.getMerId())) {
                throw new CrmebException("直播商品不存在");
            }
            // 未审核通过的只删除本地
            if (!wechatLiveGoods.getReviewStatus().equals(WechatMPLiveGoodsReviewStatusEnum.WECHAT_REVIEW_SUCCESS.getCode())) {
                return dao.deleteById(id) > 0;
            }
            boolean deleteResult = wxMaService.getLiveGoodsService().deleteGoods(wechatLiveGoods.getGoodsId());
            if (!deleteResult) throw new CrmebException("删除微信直播商品失败:");

            return dao.deleteById(id) > 0;
        } catch (WxErrorException e) {
            logger.error("微信小程序直播 - 删除商品 {}", e.getError());
            throw new CrmebException("微信小程序直播 - 删除商品！" + CRMEBWxError.fromJson(e.getError().getJson(), WxType.Open));
        }
    }

    @Override
    public WechatLiveGoods getGoodInfo(Integer id) {
        WechatLiveGoods wechatLiveGoods = getById(id);
        if (ObjectUtil.isNull(wechatLiveGoods)) {
            throw new CrmebException("直播商品不存在");
        }
        SystemAdmin admin = SecurityUtil.getLoginUserVo().getUser();
        if (admin.getMerId() > 0 && !admin.getMerId().equals(wechatLiveGoods.getMerId())) {
            throw new CrmebException("直播商品不存在");
        }
        return wechatLiveGoods;
    }
}
