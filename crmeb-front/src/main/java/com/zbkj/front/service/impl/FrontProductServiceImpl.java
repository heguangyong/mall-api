package com.zbkj.front.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.github.pagehelper.PageInfo;
import com.zbkj.common.constants.GroupConfigConstants;
import com.zbkj.common.constants.ProductConstants;
import com.zbkj.common.constants.SysConfigConstants;
import com.zbkj.common.exception.CrmebException;
import com.zbkj.common.model.coupon.Coupon;
import com.zbkj.common.model.merchant.Merchant;
import com.zbkj.common.model.order.OrderDetail;
import com.zbkj.common.model.product.*;
import com.zbkj.common.model.record.BrowseRecord;
import com.zbkj.common.model.system.GroupConfig;
import com.zbkj.common.model.user.User;
import com.zbkj.common.page.CommonPage;
import com.zbkj.common.request.CouponProductSearchRequest;
import com.zbkj.common.request.PageParamRequest;
import com.zbkj.common.request.ProductFrontSearchRequest;
import com.zbkj.common.request.SystemCouponProductSearchRequest;
import com.zbkj.common.request.merchant.MerchantProductSearchRequest;
import com.zbkj.common.response.*;
import com.zbkj.common.result.CommonResultCode;
import com.zbkj.common.utils.CrmebUtil;
import com.zbkj.front.service.FrontProductService;
import com.zbkj.front.service.SeckillService;
import com.zbkj.service.service.*;
import com.zbkj.service.service.groupbuy.GroupBuyActivitySkuService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * IndexServiceImpl 接口实现
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
public class FrontProductServiceImpl implements FrontProductService {

    private static final Logger logger = LoggerFactory.getLogger(FrontProductServiceImpl.class);

    @Autowired
    private ProductService productService;
    @Autowired
    private ProductReplyService productReplyService;
    @Autowired
    private UserService userService;
    @Autowired
    private ProductRelationService productRelationService;
    @Autowired
    private ProductAttrValueService productAttrValueService;
    @Autowired
    private AsyncService asyncService;
    @Autowired
    private MerchantService merchantService;
    @Autowired
    private PayComponentProductService componentProductService;
    @Autowired
    private UserMerchantCollectService userMerchantCollectService;
    @Autowired
    private ProductGuaranteeService productGuaranteeService;
    @Autowired
    private SeckillService seckillService;
    @Autowired
    private ActivityStyleService activityStyleService;
    @Autowired
    private OrderDetailService orderDetailService;
    @Autowired
    private BrowseRecordService browseRecordService;
    @Autowired
    private CouponService couponService;
    @Autowired
    private ProductBrandService productBrandService;
    @Autowired
    private ProductCategoryService productCategoryService;
    @Autowired
    private GroupConfigService groupConfigService;
    @Autowired
    private ProductTagService productTagService;
    @Autowired
    private SystemConfigService systemConfigService;
    @Autowired
    private GroupBuyActivitySkuService groupBuyActivitySkuService;
    @Autowired
    private ProductAttributeService productAttributeService;
    @Autowired
    private ProductAttributeOptionService productAttributeOptionService;


    /**
     * 商品列表
     *
     * @return List<ProductFrontResponse>
     */
    @Override
    public PageInfo<ProductFrontResponse> getList(ProductFrontSearchRequest request, PageParamRequest pageRequest) {
        return productService.findH5List(request, pageRequest);
    }

    /**
     * 获取商品详情V1.7
     *
     * @param id            商品id
     * @param type          基础类型：0=普通商品,1-积分商品,2-虚拟商品,4=视频号,5-云盘商品,6-卡密商品
     * @param marketingType 营销类型：0=基础商品,1=秒杀,2=拼团
     * @param groupActivityId 拼团活动id
     * @return 商品详情信息
     */
    @Override
    public ProductDetailResponse getDetail_V1_7(Integer id, Integer type, Integer marketingType, Integer groupActivityId, Integer groupRecordId) {

        if (type < 0 || type > 6) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "商品类型未知");
        }
        if (marketingType < 0 || marketingType > 2) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "营销类型未知");
        }
        if (type == 4 && marketingType == 0) {
            return componentProductService.getH5Detail(id);
        }
        if (marketingType == 1) {
            return seckillService.getProductDetail(id);
        }
        if (marketingType == 2) {
            return groupBuyActivitySkuService.getGroupBuyDetail(id, groupActivityId, groupRecordId);
        }
        return getBaseDetail(id);
    }

    /**
     * 获取基础商品详情
     * @param id 商品ID
     */
    private ProductDetailResponse getBaseDetail(Integer id) {
        // 查询普通商品
        ProductDetailResponse productDetailResponse = new ProductDetailResponse();
        Product product = productService.getH5Detail(id);
        productDetailResponse.setProductInfo(product);
        if (StrUtil.isNotBlank(product.getGuaranteeIds())) {
            productDetailResponse.setGuaranteeList(productGuaranteeService.findByIdList(CrmebUtil.stringToArray(product.getGuaranteeIds())));
        }
        // 获取商品规格
        List<ProductAttribute> attributeList = productAttributeService.findListByProductId(product.getId());
        attributeList.forEach(attr -> {
            List<ProductAttributeOption> optionList = productAttributeOptionService.findListByAttrId(attr.getId());
            attr.setOptionList(optionList);
        });
        // 根据制式设置attr属性
        productDetailResponse.setProductAttr(attributeList);

        // 根据制式设置sku属性
        LinkedHashMap<String, ProductAttrValueResponse> skuMap = new LinkedHashMap<>();
        List<ProductAttrValue> productAttrValueList = productAttrValueService.getListByProductIdAndType(product.getId(), product.getType(), ProductConstants.PRODUCT_MARKETING_TYPE_BASE, true);
        // sku默认逻辑补充
        long defaultCount = productAttrValueList.stream().filter(e -> e.getIsDefault().equals(true)).count();
        if (defaultCount < 1) {
            productAttrValueList.get(0).setIsDefault(true);
        }

        for (ProductAttrValue productAttrValue : productAttrValueList) {
            ProductAttrValueResponse atr = new ProductAttrValueResponse();
            BeanUtils.copyProperties(productAttrValue, atr);
            skuMap.put(atr.getSku(), atr);
        }
        productDetailResponse.setProductValue(skuMap);

        Integer userId = userService.getUserId();

        if (!product.getType().equals(ProductConstants.PRODUCT_TYPE_INTEGRAL)) {
            // 获取商户信息
            Merchant merchant = merchantService.getById(product.getMerId());
            ProductMerchantResponse merchantResponse = new ProductMerchantResponse();
            BeanUtils.copyProperties(merchant, merchantResponse);
            merchantResponse.setCollectNum(userMerchantCollectService.getCountByMerId(merchant.getId()));
            // 获取商户推荐商品
            List<ProMerchantProductResponse> merchantProductResponseList = productService.getRecommendedProductsByMerId(merchant.getId(), 6);
            merchantResponse.setProList(merchantProductResponseList);

            // 获取用户
            productDetailResponse.setUserCollect(false);
            if (userId > 0) {
                merchantResponse.setIsCollect(userMerchantCollectService.isCollect(userId, merchant.getId()));
                // 查询用户是否收藏收藏
                productDetailResponse.setUserCollect(productRelationService.existCollectByUser(userId, product.getId()));
            }
            productDetailResponse.setMerchantInfo(merchantResponse);
            // 添加活动背景
            productDetailResponse.getProductInfo().setActivityStyle(activityStyleService.makeActivityBackgroundStyle(productDetailResponse.getProductInfo()));
        }

        // 异步调用进行数据统计
        asyncService.productDetailStatistics(product.getId(), userId);
        // 获取优惠券（商户）数据2条
        productDetailResponse.setCouponList(couponService.findProductDetailLimit(product.getId(), 4));

        return productDetailResponse;
    }

    /**
     * 商品评论列表
     *
     * @param proId            商品编号
     * @param type             评价等级|0=全部,1=好评,2=中评,3=差评
     * @param pageParamRequest 分页参数
     * @return List<ProductReplyResponse>
     */
    @Override
    public PageInfo<ProductReplyResponse> getReplyList(Integer proId, Integer type, PageParamRequest pageParamRequest) {
        return productReplyService.getH5List(proId, type, pageParamRequest);
    }

    /**
     * 产品评价数量和好评度
     *
     * @return StoreProductReplayCountResponse
     */
    @Override
    public ProductReplayCountResponse getReplyCount(Integer id) {
        return productReplyService.getH5Count(id);
    }

    /**
     * 商品列表转为首页商品格式
     *
     * @param productList 商品列表
     */
    private List<ProductCommonResponse> productToIndexProduct(List<Product> productList) {
        List<ProductCommonResponse> productResponseArrayList = new ArrayList<>();
        for (Product product : productList) {
            ProductCommonResponse productResponse = new ProductCommonResponse();
            BeanUtils.copyProperties(product, productResponse);
            // 评论总数
            Integer sumCount = productReplyService.getCountByScore(product.getId(), ProductConstants.PRODUCT_REPLY_TYPE_ALL);
            // 好评总数
            Integer goodCount = productReplyService.getCountByScore(product.getId(), ProductConstants.PRODUCT_REPLY_TYPE_GOOD);
            String replyChance = "0";
            if (sumCount > 0 && goodCount > 0) {
                replyChance = String.format("%.2f", ((goodCount.doubleValue() / sumCount.doubleValue())));
            }
            productResponse.setReplyNum(sumCount);
            productResponse.setPositiveRatio(replyChance);
            productResponse.setSales(product.getSales() + product.getFicti());
            // 设置商品标签
            ProductTagsFrontResponse productTagsFrontResponse = productTagService.setProductTagByProductTagsRules(product.getId(), product.getBrandId(), product.getMerId(), product.getCategoryId(), productResponse.getProductTags());
            productResponse.setProductTags(productTagsFrontResponse);
            productResponseArrayList.add(productResponse);
        }
        return productResponseArrayList;
    }

    /**
     * 商品详情评论
     *
     * @param id 商品id
     * @return ProductDetailReplyResponse
     * 评论只有一条，图文
     * 评价总数
     * 好评率
     */
    @Override
    public ProductDetailReplyResponse getProductReply(Integer id) {
        return productReplyService.getH5ProductReply(id);
    }

    /**
     * 商户商品列表
     *
     * @param request          搜索参数
     * @param pageParamRequest 分页参数
     * @return List
     */
    @Override
    public PageInfo<ProductCommonResponse> getMerchantProList(MerchantProductSearchRequest request, PageParamRequest pageParamRequest) {
        PageInfo<Product> pageInfo = productService.findMerchantProH5List(request, pageParamRequest);
        List<Product> productList = pageInfo.getList();
        if (CollUtil.isEmpty(productList)) {
            return CommonPage.copyPageInfo(pageInfo, CollUtil.newArrayList());
        }
        productList = activityStyleService.makeActivityBorderStyle(productList);
        List<ProductCommonResponse> responseList = productToIndexProduct(productList);
        return CommonPage.copyPageInfo(pageInfo, responseList);
    }

    /**
     * 优惠券商品列表
     *
     * @param request 搜索参数
     * @return PageInfo
     */
    @Override
    public PageInfo<ProductFrontResponse> getCouponProList(CouponProductSearchRequest request) {
        PageInfo<Product> pageInfo = productService.getCouponProList(request);
        List<Product> productList = pageInfo.getList();
        if (CollUtil.isEmpty(productList)) {
            return CommonPage.copyPageInfo(pageInfo, CollUtil.newArrayList());
        }
        List<Integer> merIdList = productList.stream().map(Product::getMerId).distinct().collect(Collectors.toList());
        Map<Integer, Merchant> merchantMap = merchantService.getMapByIdList(merIdList);
        List<ProductFrontResponse> frontResponseList = productList.stream().map(product -> {
            ProductFrontResponse response = new ProductFrontResponse();
            BeanUtils.copyProperties(product, response);
            Merchant merchant = merchantMap.get(product.getMerId());
            response.setMerName(merchant.getName());
            response.setMerCategoryId(merchant.getCategoryId());
            response.setMerTypeId(merchant.getTypeId());
            response.setSales(product.getSales() + product.getFicti());
            return response;
        }).collect(Collectors.toList());
        return CommonPage.copyPageInfo(pageInfo, frontResponseList);
    }

    /**
     * 获取已购商品列表
     *
     * @param pageParamRequest 分页参数
     */
    @Override
    public PageInfo<ProductSimpleResponse> findPurchasedList(PageParamRequest pageParamRequest) {
        Integer userId = userService.getUserIdException();
        PageInfo<OrderDetail> pageInfo = orderDetailService.findPurchasedList(userId, pageParamRequest);
        List<OrderDetail> detailList = pageInfo.getList();
        if (CollUtil.isEmpty(detailList)) {
            return CommonPage.copyPageInfo(pageInfo, new ArrayList<>());
        }
        List<Integer> proIdList = detailList.stream().map(OrderDetail::getProductId).distinct().collect(Collectors.toList());
        Map<Integer, Product> productMap = productService.getMapByIdList(proIdList);
        List<ProductSimpleResponse> responseList = detailList.stream().map(detail -> {
            Product product = productMap.get(detail.getProductId());
            ProductSimpleResponse response = new ProductSimpleResponse();
            response.setProductId(product.getId());
            response.setName(product.getName());
            response.setImage(product.getImage());
            response.setPrice(product.getPrice());
            return response;
        }).collect(Collectors.toList());
        return CommonPage.copyPageInfo(pageInfo, responseList);
    }

    /**
     * 足迹商品列表
     *
     * @param pageParamRequest 分页参数
     */
    @Override
    public PageInfo<ProductSimpleResponse> findBrowseList(PageParamRequest pageParamRequest) {
        Integer userId = userService.getUserIdException();
        PageInfo<BrowseRecord> pageInfo = browseRecordService.findPageByUserId(userId, pageParamRequest);
        List<BrowseRecord> browseRecordList = pageInfo.getList();
        if (CollUtil.isEmpty(browseRecordList)) {
            return CommonPage.copyPageInfo(pageInfo, new ArrayList<>());
        }
        List<Integer> proIdList = browseRecordList.stream().map(BrowseRecord::getProductId).distinct().collect(Collectors.toList());
        Map<Integer, Product> productMap = productService.getMapByIdList(proIdList);
        List<ProductSimpleResponse> responseList = browseRecordList.stream().map(detail -> {
            Product product = productMap.get(detail.getProductId());
            ProductSimpleResponse response = new ProductSimpleResponse();
            response.setProductId(product.getId());
            response.setName(product.getName());
            response.setImage(product.getImage());
            response.setPrice(product.getPrice());
            response.setStock(product.getStock());
            return response;
        }).collect(Collectors.toList());
        return CommonPage.copyPageInfo(pageInfo, responseList);
    }

    /**
     * 系统优惠券商品列表
     *
     * @param request 搜索参数
     */
    @Override
    public PageInfo<ProductFrontResponse> findCouponProductList(SystemCouponProductSearchRequest request) {
        Coupon coupon = couponService.getById(request.getCouponId());
        if (ObjectUtil.isNull(coupon) || coupon.getIsDel()) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "优惠券不存在");
        }
        if (!coupon.getStatus()) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "请重新选择优惠券");
        }
        PageInfo<Product> pageInfo = productService.findCouponProductList(coupon.getId(), coupon.getCategory(), coupon.getLinkedData(), request);
        List<Product> productList = pageInfo.getList();
        if (CollUtil.isEmpty(productList)) {
            return CommonPage.copyPageInfo(pageInfo, CollUtil.newArrayList());
        }
        List<Integer> merIdList = productList.stream().map(Product::getMerId).distinct().collect(Collectors.toList());
        Map<Integer, Merchant> merchantMap = merchantService.getMapByIdList(merIdList);
        List<ProductFrontResponse> frontResponseList = productList.stream().map(product -> {
            ProductFrontResponse response = new ProductFrontResponse();
            BeanUtils.copyProperties(product, response);
            Merchant merchant = merchantMap.get(product.getMerId());
            response.setMerName(merchant.getName());
            response.setMerCategoryId(merchant.getCategoryId());
            response.setMerTypeId(merchant.getTypeId());
            response.setReplyNum(productReplyService.getCountByScore(product.getId(), ProductConstants.PRODUCT_REPLY_TYPE_ALL));
            response.setSales(product.getSales() + product.getFicti());
            return response;
        }).collect(Collectors.toList());
        return CommonPage.copyPageInfo(pageInfo, frontResponseList);
    }

    /**
     * 推荐商品分页列表
     *
     * @param pageRequest 分页参数
     */
    @Override
    public PageInfo<RecommendProductResponse> findRecommendPage(PageParamRequest pageRequest) {
        return productService.findRecommendPage(pageRequest);
    }

    /**
     * 商品列表搜索前置接口
     */
    @Override
    public ProductSearchBeforeResponse getListBefore(String keyword) {
        if (StrUtil.isBlank(keyword)) {
            return null;
        }
        List<Integer> productBrandIdList = productService.findProductBrandIdByKeyword(keyword);
        if (CollUtil.isEmpty(productBrandIdList)) {
            return null;
        }
        List<ProductBrand> brandList = productBrandService.findByIdList(productBrandIdList);
        List<Integer> productCategoryIdList = productService.findProductCategoryIdByKeyword(keyword);
        List<ProductCategory> categoryList = productCategoryService.findByIdList(productCategoryIdList);

        ProductSearchBeforeResponse response = new ProductSearchBeforeResponse();
        response.setBrandList(brandList);
        response.setCategoryList(categoryList);
        response.setProductTagList(productTagService.getStatusOnProductTagList());
        return response;
    }

    /**
     * PC店铺推荐商品
     *
     * @param merId 商户ID
     */
    @Override
    public List<PcMerchantRecommendProductResponse> getRecommendProductByMerId(Integer merId) {
        GroupConfig recommendProductConfig = groupConfigService.getOneByTagAndMerId(GroupConfigConstants.TAG_MERCHANT_PC_RECOMMEND_PRODUCT, merId);
        List<PcMerchantRecommendProductResponse> responseList = new ArrayList<>();
        if (ObjectUtil.isNull(recommendProductConfig) || StrUtil.isBlank(recommendProductConfig.getValue())) {
            return responseList;
        }
        List<Integer> proIdList = CrmebUtil.stringToArray(recommendProductConfig.getValue());
        List<Product> productList = productService.findByIds(proIdList, "front");
        if (CollUtil.isEmpty(productList)) {
            return responseList;
        }
        Map<Integer, Product> productMap = productList.stream().collect(Collectors.toMap(Product::getId, Product -> Product));
        for (int i = 0; i < proIdList.size(); i++) {
            Integer pid = proIdList.get(i);
            if (!productMap.containsKey(pid)) {
                continue;
            }
            Product product = productMap.get(pid);
            PcMerchantRecommendProductResponse response = new PcMerchantRecommendProductResponse();
            response.setId(product.getId());
            response.setName(product.getName());
            response.setImage(product.getImage());
            response.setPrice(product.getPrice());
            response.setSales(product.getSales() + product.getFicti());
            response.setIsPaidMember(product.getIsPaidMember());
            response.setVipPrice(product.getVipPrice());
            responseList.add(response);
        }
        return responseList;
    }


    /**
     * 根据商品id集合 加载对应商品
     *
     * @param proIdList id集合
     * @return id集合对应的商品列表
     */
    @Override
    public List<ProductFrontResponse> getProductByIds(List<Integer> proIdList) {
        List<Product> byIdsAndLabel = productService.findByIds(proIdList, "front");
        List<ProductFrontResponse> productFrontResponses = byIdsAndLabel.stream().map(productItem -> {
            ProductFrontResponse response = new ProductFrontResponse();
            BeanUtils.copyProperties(productItem, response);
            return response;
        }).collect(Collectors.toList());
        productFrontResponses.forEach(e -> {
            // 评论总数
            Integer sumCount = productReplyService.getCountByScore(e.getId(), ProductConstants.PRODUCT_REPLY_TYPE_ALL);
            // 好评总数
            Integer goodCount = productReplyService.getCountByScore(e.getId(), ProductConstants.PRODUCT_REPLY_TYPE_GOOD);
            // 设置商品标签
            ProductTagsFrontResponse productTagsFrontResponse = productTagService.setProductTagByProductTagsRules(e.getId(), e.getBrandId(), e.getMerId(), e.getCategoryId(), e.getProductTags());
            e.setProductTags(productTagsFrontResponse);

            String replyChance = "0";
            if (sumCount > 0 && goodCount > 0) {
                replyChance = String.format("%.2f", ((goodCount.doubleValue() / sumCount.doubleValue())));
            }
            e.setReplyNum(sumCount);
            e.setPositiveRatio(replyChance);
            e.setSales(e.getSales() + e.getFicti());
        });

        // 查询活动边框配置信息, 并赋值给商品response 重复添加的商品数据会根据数据添加持续覆盖后的为准
        List<Product> products = new ArrayList<>();
        productFrontResponses.forEach(response -> {
            Product product = new Product();
            BeanUtils.copyProperties(response, product);
            products.add(product);
        });
        List<Product> makeProductList = activityStyleService.makeActivityBorderStyle(products);

        makeProductList.forEach(p -> {
            productFrontResponses.stream().map(resProduct -> {
                if (p.getId().equals(resProduct.getId())) {
                    resProduct.setActivityStyle(p.getActivityStyle());
                }
                return resProduct;
            }).collect(Collectors.toList());
        });
        return productFrontResponses;
    }

    /**
     * 会员商品分页列表
     */
    @Override
    public PageInfo<RecommendProductResponse> findMemberPage(PageParamRequest pageParamRequest) {
        Integer userId = userService.getUserId();
        if (userId <= 0) {
            return new PageInfo<>();
        }
        String proSwitch = systemConfigService.getValueByKey(SysConfigConstants.CONFIG_PAID_MEMBER_PRODUCT_SWITCH);
        if (!proSwitch.equals("1")) {
            return new PageInfo<>();
        }
        User user = userService.getById(userId);
        if (!user.getIsPaidMember()) {
            String paidMemberPriceDisplay = systemConfigService.getValueByKey(SysConfigConstants.CONFIG_PAID_MEMBER_PRICE_DISPLAY);
            if (StrUtil.isBlank(paidMemberPriceDisplay) || !paidMemberPriceDisplay.equals("all")) {
                return new PageInfo<>();
            }
        }
        return productService.findMemberPage(pageParamRequest);
    }
}

