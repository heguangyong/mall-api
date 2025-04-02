package com.zbkj.service.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.URLUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zbkj.common.constants.Constants;
import com.zbkj.common.constants.ProductConstants;
import com.zbkj.common.enums.PayComponentEditStatusEnum;
import com.zbkj.common.enums.PayComponentPlatformStatusEnum;
import com.zbkj.common.enums.PayComponentStatusEnum;
import com.zbkj.common.exception.CrmebException;
import com.zbkj.common.model.admin.SystemAdmin;
import com.zbkj.common.model.merchant.Merchant;
import com.zbkj.common.model.product.Product;
import com.zbkj.common.model.product.ProductAttr;
import com.zbkj.common.model.product.ProductAttrValue;
import com.zbkj.common.model.user.User;
import com.zbkj.common.model.wechat.video.*;
import com.zbkj.common.page.CommonPage;
import com.zbkj.common.request.PageParamRequest;
import com.zbkj.common.request.ProductAttrValueAddRequest;
import com.zbkj.common.request.wxvedio.product.ComponentProductSearchRequest;
import com.zbkj.common.request.wxvedio.product.PayComponentProductAddRequest;
import com.zbkj.common.response.ProMerchantProductResponse;
import com.zbkj.common.response.ProductAttrValueResponse;
import com.zbkj.common.response.ProductDetailResponse;
import com.zbkj.common.response.ProductMerchantResponse;
import com.zbkj.common.response.wxvideo.PayComponentProductResponse;
import com.zbkj.common.utils.CrmebUtil;
import com.zbkj.common.utils.SecurityUtil;
import com.zbkj.common.vo.wxvedioshop.*;
import com.zbkj.service.dao.PayComponentProductDao;
import com.zbkj.service.service.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 *    微信 自定义交易组件 过甚商品操作
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
@Service
public class PayComponentProductServiceImpl extends ServiceImpl<PayComponentProductDao, PayComponentProduct> implements PayComponentProductService {
    private static final Logger logger = LoggerFactory.getLogger(PayComponentProductServiceImpl.class);
    @Resource
    private PayComponentProductDao dao;

    @Autowired
    private TransactionTemplate transactionTemplate;

    @Autowired
    private PayComponentDraftProductService draftProductService;
    @Autowired
    private WechatVideoSpuService wechatVideoSpuService;
    @Autowired
    private PayComponentProductInfoService infoService;
    @Autowired
    private PayComponentProductSkuService skuService;
    @Autowired
    private SystemAttachmentService systemAttachmentService;
    @Autowired
    private ProductAttrService productAttrService;
    @Autowired
    private ProductAttrValueService productAttrValueService;
    @Autowired
    private UserService userService;
    @Autowired
    private SystemConfigService systemConfigService;
    @Autowired
    private PayComponentCatService catService;
    @Autowired
    private MerchantService merchantService;
    @Autowired
    private UserMerchantCollectService userMerchantCollectService;
    @Autowired
    private ProductService productService;

    /**
     * 根据草稿id删除草稿及商品 只有未上架商品才可以删除 删除时需要调用微信自定义交易组件侧同步删除
     * @param proId 商品id
     * @return Boolean
     */
    @Override
    public Boolean delete(Integer proId) {
        PayComponentProduct product = getById(proId);
        if (ObjectUtil.isNull(product)) {
            throw new CrmebException("商品不存在");
        }
        if (product.getIsDel()) {
            throw new CrmebException("商品已删除");
        }
        ShopSpuCommonVo shopSpuCommonVo = new ShopSpuCommonVo();
        shopSpuCommonVo.setOutProductId(proId.toString());
        Boolean isDel = wechatVideoSpuService.shopSpuDel(shopSpuCommonVo);
        if (!isDel) {
            throw new CrmebException("自定义交易组件删除商品失败");
        }
        product.setIsDel(true);
        Boolean execute = transactionTemplate.execute(e -> {
            updateById(product);
            // 删除草稿箱
            draftProductService.deleteByProId(proId);
            return Boolean.TRUE;
        });
        return execute;
    }

    /** TODO 暂时保留
     * 更新商品,这里的更新商品其实是替换审核商品，撤回原来商品并替换为现有商品提交审核
     * @param addRequest 商品请求参数
     * @return Boolean
     */
    @Override
    public Boolean update(PayComponentProductAddRequest addRequest) {
        // 获取商品
        PayComponentProduct product = getById(addRequest.getId());
        if (ObjectUtil.isNull(product) || product.getIsDel()) {
            throw new CrmebException("商品不存在");
        }
        // 获取草稿商品
        PayComponentDraftProduct draftProduct = draftProductService.getByProId(product.getId());
        if (ObjectUtil.isNull(draftProduct) || draftProduct.getIsDel()) {
            throw new CrmebException("草稿商品不存在");
        }
        // 根据平台审核状态判断是否可编辑 这里的平台状态已经是在微信状态之上的 所以只用系统状态判断
        if (draftProduct.getStatus().equals(PayComponentEditStatusEnum.REVIEW_ING.getCode())) {
            // 先撤回之前的审核申请，然后再提交新的申请
            ShopSpuCommonVo shopSpuCommonVo = new ShopSpuCommonVo();
            shopSpuCommonVo.setOutProductId(product.getId().toString());
            Boolean delAudit = wechatVideoSpuService.shopSpuDelAudit(shopSpuCommonVo);
            if (!delAudit) {
                throw new CrmebException("商品审核中，撤回审核失败!");
            }
        }

        BeanUtils.copyProperties(addRequest, product);
        if (ObjectUtil.isNull(addRequest.getBrandId()) || addRequest.getBrandId() == 0) {
            product.setBrandId(2100000000);
        }

        PayComponentProductInfo productInfo = new PayComponentProductInfo();
        if (StrUtil.isNotBlank(addRequest.getDescInfo())) {
            productInfo.setDesc(addRequest.getDescInfo());
        }
        // 规格部分
        List<PayComponentProductSkuAttr> attrList = CollUtil.newArrayList();
        List<PayComponentProductSku> skuList = CollUtil.newArrayList();
        if (!addRequest.getSpecType()) { // 单规格
            PayComponentProductSkuAttr skuAttr = new PayComponentProductSkuAttr();
            skuAttr.setAttrKey("请选择规格");
            skuAttr.setAttrValue("默认");
            skuAttr.setIsDel(false);
            attrList.add(skuAttr);
            ProductAttrValueAddRequest attrValueRequest = addRequest.getAttrValue().get(0);
            PayComponentProductSku sku = new PayComponentProductSku();
            sku.setThumbImg(attrValueRequest.getImage());
            sku.setSalePrice(attrValueRequest.getPrice().multiply(new BigDecimal("100")).longValue());
            sku.setMarketPrice(attrValueRequest.getOtPrice().multiply(new BigDecimal("100")).longValue());
            sku.setStockNum(attrValueRequest.getStock());
            sku.setAttrList(attrList);
            sku.setIsDel(true);
            skuList.add(sku);
        } else { // 多规格
            List<ProductAttrValueAddRequest> valueRequestList = addRequest.getAttrValue();
            valueRequestList.forEach(attrValueRequest -> {
                PayComponentProductSku sku = new PayComponentProductSku();
                sku.setThumbImg(attrValueRequest.getImage());
                sku.setSalePrice(attrValueRequest.getPrice().multiply(new BigDecimal("100")).longValue());
                sku.setMarketPrice(attrValueRequest.getOtPrice().multiply(new BigDecimal("100")).longValue());
                sku.setStockNum(attrValueRequest.getStock());
                sku.setIsDel(true);
                String attrValueJson = attrValueRequest.getAttrValue();
                JSONObject jsonObject = JSONObject.parseObject(attrValueJson);
                attrList.clear();
                for (Map.Entry<String, Object> vo : jsonObject.entrySet()) {
                    PayComponentProductSkuAttr skuAttr = new PayComponentProductSkuAttr();
                    skuAttr.setAttrKey(vo.getKey());
                    skuAttr.setAttrValue(vo.getValue().toString());
                    skuAttr.setIsDel(false);
                    attrList.add(skuAttr);
                }
                sku.setAttrList(attrList);
                skuList.add(sku);
            });
        }
        for (PayComponentProductSku sku : skuList) {
            sku.setProductId(product.getId());
        }
        boolean skuSave = skuService.saveBatch(skuList);
        if (!skuSave) {
            throw new CrmebException("第一步，本地保存商品sku时错误");
        }
        // 第二步，组装商品，发给组件
        ShopSpuAddVo shopSpuAddVo = assembleShopSpuVo(product, skuList, productInfo);
        ShopSpuAddResponseVo spuUpdateResponseVo;
        try {
            spuUpdateResponseVo = wechatVideoSpuService.shopSpuUpdate(shopSpuAddVo);
        } catch (Exception e) {// 抛出异常，说明微信调用错误，删除之前保存的商品、sku等信息
            List<Integer> skuIds = skuList.stream().map(PayComponentProductSku::getId).collect(Collectors.toList());
            skuService.removeByIds(skuIds);
            throw new CrmebException(e.getMessage());
        }

        // 第三步，保存草稿商品，保存需要给前端展示的数据表
        // 轮播图
        product.setHeadImg(systemAttachmentService.clearPrefix(product.getHeadImg()));
        // 商品资质图
        if (StrUtil.isNotBlank(product.getQualificationPics())) {
            product.setQualificationPics(systemAttachmentService.clearPrefix(product.getQualificationPics()));
        }
        BeanUtils.copyProperties(product, draftProduct, "status", "id");
        draftProduct.setHeadImg(systemAttachmentService.clearPrefix(draftProduct.getHeadImg()));
        if (StrUtil.isNotBlank(draftProduct.getQualificationPics())) {
            draftProduct.setQualificationPics(systemAttachmentService.clearPrefix(draftProduct.getQualificationPics()));
        }
        draftProduct.setProductId(product.getId());
        draftProduct.setUpdateTime(spuUpdateResponseVo.getUpdateTime());
        draftProduct.setEditStatus(PayComponentEditStatusEnum.REVIEW_ING.getCode());
        // sku
        Map<String, String> skuMap = CollUtil.newHashMap();
        spuUpdateResponseVo.getSkus().forEach(e -> {
            skuMap.put(e.getOutSkuId(), e.getSkuId());
        });
        skuList.forEach(sku -> {
            sku.setComponentProductId(spuUpdateResponseVo.getProductId());
            sku.setSkuId(skuMap.get(sku.getId().toString()));
            sku.setIsDel(false);
        });

        List<PayComponentProductSkuAttr> attrsList = CollUtil.newArrayList();
        skuList.forEach(e -> {
            List<PayComponentProductSkuAttr> skuAttrs = e.getAttrList();
            skuAttrs.forEach(attr -> {
                attr.setSkuId(e.getId());
                attr.setComponentSkuId(e.getSkuId());
                attr.setIsDel(false);
            });
            attrsList.addAll(skuAttrs);
        });
        draftProduct.setSku(JSONArray.toJSONString(skuList));
        draftProduct.setAttr(JSONArray.toJSONString(addRequest.getAttr()));
        draftProduct.setAttrValue(JSONArray.toJSONString(addRequest.getAttrValue()));
        draftProduct.setIsDel(false);
        draftProduct.setAddTime(DateUtil.date());
        draftProduct.setSales(0);
        draftProduct.setSpecType(addRequest.getSpecType());
        draftProduct.setDescInfo(addRequest.getDescInfo());
        int stock = skuList.stream().mapToInt(PayComponentProductSku::getStockNum).sum();
        draftProduct.setStock(stock);

        boolean save = draftProductService.updateById(draftProduct);
        if (!save) {
            throw new CrmebException("第三步，本地保存草稿商品时错误");
        }
        return save;
    }

    /**
     * 微信过审商品 商家操作上架
     * @param proId 商品id
     * @return Boolean
     */
    @Override
    public Boolean putonByMerchant(Integer proId) {
        PayComponentProduct product = getById(proId);
        if (ObjectUtil.isNull(product)) {
            throw new CrmebException("商品不存在");
        }
        if (product.getIsDel()) {
            throw new CrmebException("商品已删除");
        }
        if (!product.getStatus().equals(PayComponentStatusEnum.STATUS_PUTDOWN.getCode())) {
            throw new CrmebException("商品只有在自主下架状态，才能上架");
        }
        ShopSpuCommonVo shopSpuCommonVo = new ShopSpuCommonVo();
        shopSpuCommonVo.setOutProductId(proId.toString());
        shopSpuCommonVo.setNeedEditSpu(0);
        ShopSpuVo shopSpuVo = wechatVideoSpuService.shopSpuGet(shopSpuCommonVo);
        if (!shopSpuVo.getStatus().equals(PayComponentStatusEnum.STATUS_PUTDOWN.getCode())) {
            throw new CrmebException("组件中商品不属于自主下架状态");
        }
        Boolean isListing = wechatVideoSpuService.shopSpuPuton(shopSpuCommonVo);
        if (!isListing) {
            throw new CrmebException("组件中商品上架失败");
        }
        product.setStatus(PayComponentStatusEnum.STATUS_PUTON.getCode());
        product.setPlatformStatus(PayComponentPlatformStatusEnum.MERCHANT_PUTON.getCode());

        // 更新草稿状态
        LambdaUpdateWrapper<PayComponentDraftProduct> draftProductLambdaQueryWrapper = Wrappers.lambdaUpdate();
        draftProductLambdaQueryWrapper.eq(PayComponentDraftProduct::getProductId, product.getId());
        draftProductLambdaQueryWrapper.set(PayComponentDraftProduct::getPlatformStatus, PayComponentPlatformStatusEnum.MERCHANT_PUTON.getCode());
        draftProductService.update(draftProductLambdaQueryWrapper);

        return updateById(product);
    }

    /**
     * 微信过审商品 商家操作下架
     * @param proId 商品id
     * @return Boolean
     */
    @Override
    public Boolean putdownByMerchant(Integer proId) {
        PayComponentProduct product = getById(proId);
        if (ObjectUtil.isNull(product)) {
            throw new CrmebException("商品不存在");
        }
        if (product.getIsDel()) {
            throw new CrmebException("商品已删除");
        }
        if (!product.getStatus().equals(0) && !product.getStatus().equals(PayComponentStatusEnum.STATUS_PUTON.getCode())) {
            throw new CrmebException("商品只有在初始值/上架状态，才能下架");
        }
        ShopSpuCommonVo shopSpuCommonVo = new ShopSpuCommonVo();
        shopSpuCommonVo.setOutProductId(proId.toString());
        shopSpuCommonVo.setNeedEditSpu(0);
        ShopSpuVo shopSpuVo = wechatVideoSpuService.shopSpuGet(shopSpuCommonVo);
        if (!shopSpuVo.getStatus().equals(0) && !shopSpuVo.getStatus().equals(PayComponentStatusEnum.STATUS_PUTON.getCode())) {
            throw new CrmebException("组件中商品不属于自主下架状态");
        }
        Boolean isDelisting = wechatVideoSpuService.shopSpuPutdown(shopSpuCommonVo);
        if (!isDelisting) {
            throw new CrmebException("组件中商品下架失败");
        }
        product.setStatus(PayComponentStatusEnum.STATUS_PUTDOWN.getCode());
        product.setPlatformStatus(PayComponentPlatformStatusEnum.MERCHANT_PUTDOWN.getCode());

        // 更新草稿状态
        LambdaUpdateWrapper<PayComponentDraftProduct> draftProductLambdaQueryWrapper = Wrappers.lambdaUpdate();
        draftProductLambdaQueryWrapper.eq(PayComponentDraftProduct::getProductId, product.getId());
        draftProductLambdaQueryWrapper.set(PayComponentDraftProduct::getPlatformStatus, PayComponentPlatformStatusEnum.MERCHANT_PUTDOWN.getCode());
        draftProductService.update(draftProductLambdaQueryWrapper);
        return updateById(product);
    }


    /**
     * 平台操作 下架商家商品
     * 先检查状态 再执行操作
     * @param proId 商品id
     * @return Boolean
     */
    @Override
    public Boolean putdownByPlatForm(Integer proId) {
        PayComponentProduct product = getById(proId);
        if (ObjectUtil.isNull(product)) {
            throw new CrmebException("商品不存在");
        }
        if (product.getIsDel()) {
            throw new CrmebException("商品已删除");
        }
        if (!product.getStatus().equals(PayComponentStatusEnum.STATUS_PUTON.getCode())) {
            throw new CrmebException("当前商品非上架状态");
        }
        ShopSpuCommonVo shopSpuCommonVo = new ShopSpuCommonVo();
        shopSpuCommonVo.setOutProductId(proId.toString());
        shopSpuCommonVo.setNeedEditSpu(0);
        ShopSpuVo shopSpuVo = wechatVideoSpuService.shopSpuGet(shopSpuCommonVo);
        if (!shopSpuVo.getStatus().equals(0) && !shopSpuVo.getStatus().equals(PayComponentStatusEnum.STATUS_PUTON.getCode())) {
            throw new CrmebException("组件中商品不属于自主下架状态");
        }
        Boolean isDelisting = wechatVideoSpuService.shopSpuPutdown(shopSpuCommonVo);
        if (!isDelisting) {
            throw new CrmebException("组件中商品下架失败");
        }
        product.setStatus(PayComponentStatusEnum.STATUS_PUTDOWN.getCode());
        product.setPlatformStatus(PayComponentPlatformStatusEnum.PLATFORM_PUTDOWN.getCode());
        // 更新草稿状态
        LambdaUpdateWrapper<PayComponentDraftProduct> draftProductLambdaQueryWrapper = Wrappers.lambdaUpdate();
        draftProductLambdaQueryWrapper.eq(PayComponentDraftProduct::getProductId, product.getId());
        draftProductLambdaQueryWrapper.set(PayComponentDraftProduct::getPlatformStatus, PayComponentPlatformStatusEnum.PLATFORM_PUTDOWN.getCode());
        draftProductService.update(draftProductLambdaQueryWrapper);
        return updateById(product);
    }

    /** 视频号商品暂无保障服务
     * 获取H5商品详情（为兼容原格式，组装原来的数据格式）
     * @param id 商品id
     * @return ProductDetailResponse
     */
    @Override
    public ProductDetailResponse getH5Detail(Integer id) {
        PayComponentProduct product = getById(id);
        if (ObjectUtil.isNull(product) || product.getIsDel()) {
            throw new CrmebException("商品不存在");
        }
        if (!product.getStatus().equals(5)) {
            throw new CrmebException("商品未上架");
        }
        ProductDetailResponse productDetailResponse = new ProductDetailResponse();
        Product storeProduct = new Product();
        BeanUtils.copyProperties(product, storeProduct);
        List<String> imageList = JSONArray.parseArray(product.getHeadImg(), String.class);
        storeProduct.setImage(imageList.get(0));
        storeProduct.setSliderImage(product.getHeadImg());
        storeProduct.setName(product.getTitle());
        List<PayComponentProductSku> skuList = skuService.getListByProId(product.getId());
        Long price = skuList.stream().map(PayComponentProductSku::getSalePrice).distinct().min(Long::compare).get();
        storeProduct.setPrice(new BigDecimal(price).divide(new BigDecimal("100"), 2, RoundingMode.DOWN));
        Long otPrice = skuList.stream().map(PayComponentProductSku::getMarketPrice).distinct().min(Long::compare).get();
        storeProduct.setOtPrice(new BigDecimal(otPrice).divide(new BigDecimal("100"), 2, RoundingMode.DOWN));
        PayComponentProductInfo productInfo = infoService.getByProId(id);
        if (ObjectUtil.isNotNull(productInfo)) {
            storeProduct.setContent(productInfo.getDesc());
        }
        productDetailResponse.setProductInfo(storeProduct);


        // 获取商品规格
        ProductAttr spaPram = new ProductAttr();
        spaPram.setProductId(product.getId()).setType(ProductConstants.PRODUCT_TYPE_COMPONENT);
        List<ProductAttr> attrList = productAttrService.getListByProductIdAndType(product.getId(), ProductConstants.PRODUCT_TYPE_COMPONENT);
//        productDetailResponse.setProductAttr(attrList);

        // 根据制式设置sku属性
        HashMap<String, ProductAttrValueResponse> skuMap = CollUtil.newHashMap();
        ProductAttrValue spavPram = new ProductAttrValue();
        spavPram.setProductId(id).setType(ProductConstants.PRODUCT_TYPE_COMPONENT);
//        List<ProductAttrValue> storeProductAttrValues = productAttrValueService.getListByProductIdAndType(id, ProductConstants.PRODUCT_TYPE_COMPONENT);
//        for (ProductAttrValue storeProductAttrValue : storeProductAttrValues) {
//            ProductAttrValueResponse atr = new ProductAttrValueResponse();
//            BeanUtils.copyProperties(storeProductAttrValue, atr);
//            skuMap.put(atr.getSku(), atr);
//        }
        productDetailResponse.setProductValue(skuMap);

        // 获取商户信息
        Merchant merchant = merchantService.getById(product.getMerId());
        ProductMerchantResponse merchantResponse = new ProductMerchantResponse();
        BeanUtils.copyProperties(merchant, merchantResponse);
        merchantResponse.setCollectNum(userMerchantCollectService.getCountByMerId(merchant.getId()));
        // 获取商户推荐商品
        List<ProMerchantProductResponse> merchantProductResponseList = productService.getRecommendedProductsByMerId(merchant.getId(), 4);
        merchantResponse.setProList(merchantProductResponseList);
        productDetailResponse.setMerchantInfo(merchantResponse);

        // 用户收藏、分销返佣
        User user = userService.getInfo();
        if (ObjectUtil.isNotNull(user)) {
            // 查询用户是否收藏收藏
            user = userService.getInfo();
//            // 判断是否开启分销 TODO 分拥待确认
//            String brokerageFuncStatus = systemConfigService.getValueByKey(SysConfigConstants.CONFIG_KEY_BROKERAGE_FUNC_STATUS);
//            if (brokerageFuncStatus.equals(Constants.COMMON_SWITCH_OPEN)) {// 分销开启
//                storeProduct.setIsSub(true);
//                productDetailResponse.setPriceName(getPacketPriceRange(storeProduct.getIsSub(), storeProductAttrValues, user.getIsPromoter()));
//            }
        } else {
            productDetailResponse.setUserCollect(false);
        }

        return productDetailResponse;
    }

    /**
     * 当前商户获取微信过审 商品列表
     * @param request 搜索参数
     * @param pageParamRequest 分页参数
     * @return PageInfo
     */
    @Override
    public PageInfo<PayComponentProduct> getMerchantProductListByAfterWechatReview(ComponentProductSearchRequest request, PageParamRequest pageParamRequest) {
        SystemAdmin currentMerchantAdmin = SecurityUtil.getLoginUserVo().getUser();
        Page<PayComponentProduct> page = PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());
        LambdaQueryWrapper<PayComponentProduct> lqw = Wrappers.lambdaQuery();
        lqw.eq(PayComponentProduct::getIsDel, false);
        if (ObjectUtil.isNotNull(request.getProId())) {
            lqw.eq(PayComponentProduct::getId, request.getProId());
        }
        if (StrUtil.isNotBlank(request.getSearch())) {
            lqw.like(PayComponentProduct::getTitle, request.getSearch());
        }
        lqw.eq(PayComponentProduct::getIsDel, false);
        // 查询商户提审后 微信侧过审商品信息
        lqw.eq(PayComponentProduct::getEditStatus, PayComponentEditStatusEnum.REVIEW_SUCCESS.getCode());
        lqw.eq(PayComponentProduct::getMerId, currentMerchantAdmin.getMerId());
        lqw.orderByDesc(PayComponentProduct::getId);
        List<PayComponentProduct> productList = dao.selectList(lqw);
        productList.forEach(e -> {
            PayComponentCat componentCat = catService.getByThirdCatId(e.getThirdCatId());
            e.setThirdCatName(Optional.ofNullable(componentCat.getThirdCatName()).orElse(""));
        });
        return CommonPage.copyPageInfo(page, productList);
    }

    /**
     * 平台获取该平台下 微信过审商品列表
     *
     * @param request          搜索参数
     * @param pageParamRequest 分页参数
     * @retur 查询结果
     */
    @Override
    public PageInfo<PayComponentProduct> getPlatformProductListByAfterWechatReview(ComponentProductSearchRequest request, PageParamRequest pageParamRequest) {
        Page<PayComponentProduct> page = PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());
        LambdaQueryWrapper<PayComponentProduct> lqw = Wrappers.lambdaQuery();
        lqw.eq(PayComponentProduct::getIsDel, false);
        if (ObjectUtil.isNotNull(request.getProId())) {
            lqw.eq(PayComponentProduct::getId, request.getProId());
        }
        if (StrUtil.isNotBlank(request.getSearch())) {
            lqw.like(PayComponentProduct::getTitle, URLUtil.decode(request.getSearch()));
        }
        // 查询商户提审后 微信侧过审商品信息
        lqw.eq(PayComponentProduct::getEditStatus, PayComponentEditStatusEnum.REVIEW_SUCCESS.getCode());
        lqw.orderByDesc(PayComponentProduct::getId);
        List<PayComponentProduct> productList = dao.selectList(lqw);
        productList.forEach(e -> {
            PayComponentCat componentCat = catService.getByThirdCatId(e.getThirdCatId());
            e.setThirdCatName(Optional.ofNullable(componentCat.getThirdCatName()).orElse(""));
        });
        return CommonPage.copyPageInfo(page, productList);
    }

    /**
     * 添加/扣减库存
     * @param productId 商品id
     * @param num 数量
     * @param operationType 类型：add—添加，sub—扣减
     * @return Boolean
     */
    @Override
    public Boolean operationStock(Integer productId, Integer num, String operationType) {
        UpdateWrapper<PayComponentProduct> updateWrapper = new UpdateWrapper<>();
        if (operationType.equals(Constants.OPERATION_TYPE_ADD)) {
            updateWrapper.setSql(StrUtil.format("stock = stock + {}", num));
            updateWrapper.setSql(StrUtil.format("sales = sales - {}", num));
        }
        if (operationType.equals(Constants.OPERATION_TYPE_SUBTRACT)) {
            updateWrapper.setSql(StrUtil.format("stock = stock - {}", num));
            updateWrapper.setSql(StrUtil.format("sales = sales + {}", num));
            // 扣减时加乐观锁保证库存不为负
            updateWrapper.last(StrUtil.format("and (stock - {} >= 0)", num));
        }
        updateWrapper.eq("id", productId);
        boolean update = update(updateWrapper);
        if (!update) {
            throw new CrmebException("更新组件商品库存失败，商品Id = " + productId);
        }
        return update;
    }

    /**
     * 获取商品详情
     * @param id 商品id
     * @return PayComponentProductResponse
     */
    @Override
    public PayComponentProductResponse getInfo(Integer id) {
        PayComponentProduct product = getById(id);
        if (ObjectUtil.isNull(product) || product.getIsDel()) {
            throw new CrmebException("商品不存在");
        }
        // 获取商品sku
        List<PayComponentProductSku> skuList = skuService.getListByProId(id);

        PayComponentProductResponse response = new PayComponentProductResponse();
        BeanUtils.copyProperties(product, response);
        response.setSkuList(skuList);
        return response;
    }

    /**
     * 组装spu添加对象
     * @return ShopSpuAddVo
     */
    private ShopSpuAddVo assembleShopSpuVo(PayComponentProduct product, List<PayComponentProductSku> skuList, PayComponentProductInfo productInfo) {
        ShopSpuAddVo shopSpuAddVo = new ShopSpuAddVo();
        shopSpuAddVo.setOutProductId(product.getId().toString());
        shopSpuAddVo.setTitle(product.getTitle());
        shopSpuAddVo.setPath(StrUtil.format("/pages/goods/goods_details/index?id={}", product.getId()));
        shopSpuAddVo.setHeadImg(CrmebUtil.stringToArrayStr(product.getHeadImg()));
        if (StrUtil.isNotBlank(product.getQualificationPics())) {
            shopSpuAddVo.setQualificationPics(CrmebUtil.stringToArrayStr(product.getQualificationPics()));
        }
        if (StrUtil.isNotBlank(productInfo.getDesc())) {
            ShopSpuInfoVo shopSpuInfoVo = new ShopSpuInfoVo();
            shopSpuInfoVo.setDesc(productInfo.getDesc());
            shopSpuAddVo.setDescInfo(shopSpuInfoVo);
        }
        shopSpuAddVo.setThirdCatId(product.getThirdCatId());
        shopSpuAddVo.setBrandId(product.getBrandId());
        List<ShopSpuSkuVo> skuVoList = skuList.stream().map(sku -> {
            ShopSpuSkuVo spuSkuVo = new ShopSpuSkuVo();
            BeanUtils.copyProperties(sku, spuSkuVo);
            spuSkuVo.setOutProductId(product.getId().toString());
            spuSkuVo.setOutSkuId(sku.getId().toString());

            List<ShopSpuSkuAttrVo> attrVoList = sku.getAttrList().stream().map(attr -> {
                ShopSpuSkuAttrVo shopSpuSkuAttrVo = new ShopSpuSkuAttrVo();
                BeanUtils.copyProperties(attr, shopSpuSkuAttrVo);
                return shopSpuSkuAttrVo;
            }).collect(Collectors.toList());
            spuSkuVo.setSkuAttrs(attrVoList);
            return spuSkuVo;
        }).collect(Collectors.toList());
        shopSpuAddVo.setSkus(skuVoList);
        return shopSpuAddVo;
    }

    /**
     * 根据Product商品id获取草稿商品
     *
     * @param proId 商品id
     * @return PayComponentDraftProduct
     */
    @Override
    public PayComponentProduct getByComponentProductId(Integer proId) {
        LambdaQueryWrapper<PayComponentProduct> lambdaQueryWrapper = Wrappers.lambdaQuery();
        lambdaQueryWrapper.eq(PayComponentProduct::getComponentProductId, proId);
        return getOne(lambdaQueryWrapper);
    }
}

