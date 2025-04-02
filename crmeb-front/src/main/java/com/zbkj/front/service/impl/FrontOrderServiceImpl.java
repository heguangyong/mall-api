package com.zbkj.front.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageInfo;
import com.zbkj.common.config.CrmebConfig;
import com.zbkj.common.constants.*;
import com.zbkj.common.enums.GroupBuyRecordEnum;
import com.zbkj.common.exception.CrmebException;
import com.zbkj.common.model.cat.Cart;
import com.zbkj.common.model.cdkey.CardSecret;
import com.zbkj.common.model.coupon.Coupon;
import com.zbkj.common.model.coupon.CouponProduct;
import com.zbkj.common.model.coupon.CouponUser;
import com.zbkj.common.model.express.ShippingTemplates;
import com.zbkj.common.model.express.ShippingTemplatesFree;
import com.zbkj.common.model.express.ShippingTemplatesRegion;
import com.zbkj.common.model.groupbuy.GroupBuyActivitySku;
import com.zbkj.common.model.groupbuy.GroupBuyRecord;
import com.zbkj.common.model.groupbuy.GroupBuyUser;
import com.zbkj.common.model.merchant.Merchant;
import com.zbkj.common.model.order.*;
import com.zbkj.common.model.product.Product;
import com.zbkj.common.model.product.ProductAttrValue;
import com.zbkj.common.model.product.ProductCategory;
import com.zbkj.common.model.product.ProductReply;
import com.zbkj.common.model.seckill.SeckillProduct;
import com.zbkj.common.model.system.SystemForm;
import com.zbkj.common.model.user.User;
import com.zbkj.common.model.user.UserAddress;
import com.zbkj.common.model.user.UserIntegralRecord;
import com.zbkj.common.model.wechat.video.PayComponentProduct;
import com.zbkj.common.model.wechat.video.PayComponentProductSku;
import com.zbkj.common.page.CommonPage;
import com.zbkj.common.request.*;
import com.zbkj.common.response.*;
import com.zbkj.common.response.groupbuy.GroupBuyActivityRecordForFrontShareUse;
import com.zbkj.common.result.CommonResultCode;
import com.zbkj.common.result.OrderResultCode;
import com.zbkj.common.utils.CrmebDateUtil;
import com.zbkj.common.utils.CrmebUtil;
import com.zbkj.common.utils.RedisUtil;
import com.zbkj.common.vo.*;
import com.zbkj.front.service.FrontOrderService;
import com.zbkj.front.service.SeckillService;
import com.zbkj.service.service.*;
import com.zbkj.service.service.groupbuy.GroupBuyActivitySkuService;
import com.zbkj.service.service.groupbuy.GroupBuyRecordService;
import com.zbkj.service.service.groupbuy.GroupBuyUserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * H5端订单操作
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
public class FrontOrderServiceImpl implements FrontOrderService {

    private final Logger logger = LoggerFactory.getLogger(FrontOrderServiceImpl.class);

    @Autowired
    private UserService userService;
    @Autowired
    private CartService cartService;
    @Autowired
    private ProductService productService;
    @Autowired
    private ProductAttrValueService productAttrValueService;
    @Autowired
    private OrderService orderService;
    @Autowired
    private UserAddressService userAddressService;
    @Autowired
    private SystemConfigService systemConfigService;
    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private ShippingTemplatesService shippingTemplatesService;
    @Autowired
    private ShippingTemplatesFreeService shippingTemplatesFreeService;
    @Autowired
    private ShippingTemplatesRegionService shippingTemplatesRegionService;
    @Autowired
    private CouponUserService couponUserService;
    @Autowired
    private CouponProductService couponProductService;
    @Autowired
    private MerchantService merchantService;
    @Autowired
    private TransactionTemplate transactionTemplate;
    @Autowired
    private MerchantOrderService merchantOrderService;
    @Autowired
    private OrderDetailService orderDetailService;
    @Autowired
    private OrderStatusService orderStatusService;
    @Autowired
    private SystemAttachmentService systemAttachmentService;
    @Autowired
    private ProductReplyService productReplyService;
    @Autowired
    private RefundOrderService refundOrderService;
    @Autowired
    private RefundOrderInfoService refundOrderInfoService;
    @Autowired
    private PayComponentProductService payComponentProductService;
    @Autowired
    private PayComponentProductSkuService payComponentProductSkuService;
    @Autowired
    private UserIntegralRecordService userIntegralRecordService;
    @Autowired
    private SystemGroupDataService systemGroupDataService;
    @Autowired
    private SeckillService seckillService;
    @Autowired
    private SeckillProductService seckillProductService;
    @Autowired
    private CouponService couponService;
    @Autowired
    private ProductCategoryService productCategoryService;
    @Autowired
    private RefundOrderStatusService refundOrderStatusService;
    @Autowired
    private CrmebConfig crmebConfig;
    @Autowired
    private CdkeyLibraryService cdkeyLibraryService;
    @Autowired
    private CardSecretService cardSecretService;
    @Autowired
    private SystemFormService systemFormService;
    @Autowired
    private GroupBuyRecordService groupBuyRecordService;
    @Autowired
    private GroupBuyUserService groupBuyUserService;
    @Autowired
    private GroupBuyActivitySkuService groupBuyActivitySkuService;

    /**
     * 预下单V1.7
     * @param request 预下单请求参数
     * @return PreOrderResponse
     */
    @Override
    public OrderNoResponse preOrder_V1_7(PreOrderRequest request) {
        User user = userService.getInfo();
        // 校验预下单商品信息
        PreOrderInfoVo preOrderInfoVo = validatePreOrderRequest_V1_7(request, user);
        List<PreOrderInfoDetailVo> orderInfoList = new ArrayList<>();
        for (PreMerchantOrderVo merchantOrderVo : preOrderInfoVo.getMerchantOrderVoList()) {
            orderInfoList.addAll(merchantOrderVo.getOrderInfoList());
            BigDecimal merTotalPrice = merchantOrderVo.getOrderInfoList().stream()
                    .map(e -> e.getPrice().multiply(new BigDecimal(e.getPayNum())))
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            merchantOrderVo.setProTotalFee(merTotalPrice);
            merchantOrderVo.setSvipDiscountPrice(BigDecimal.ZERO);
            if (user.getIsPaidMember() && preOrderInfoVo.getType().equals(OrderConstants.ORDER_TYPE_BASE)) {
                BigDecimal merSvipTotalPrice = merchantOrderVo.getOrderInfoList().stream()
                        .map(e -> {
                            BigDecimal price;
                            if (e.getIsPaidMember()) {
                                price = e.getVipPrice().multiply(new BigDecimal(e.getPayNum()));
                            } else {
                                price = e.getPrice().multiply(new BigDecimal(e.getPayNum()));
                            }
                            return price;
                        })
                        .reduce(BigDecimal.ZERO, BigDecimal::add);
                merchantOrderVo.setSvipDiscountPrice(merTotalPrice.subtract(merSvipTotalPrice));
            }
            merchantOrderVo.setProTotalNum(merchantOrderVo.getOrderInfoList().stream().mapToInt(PreOrderInfoDetailVo::getPayNum).sum());
        }
        // 商品总计金额
        BigDecimal totalPrice = orderInfoList.stream().map(e -> e.getPrice().multiply(new BigDecimal(e.getPayNum()))).reduce(BigDecimal.ZERO, BigDecimal::add);
        preOrderInfoVo.setProTotalFee(totalPrice);
        int redeemIntegral = orderInfoList.stream().mapToInt(e -> e.getRedeemIntegral() * e.getPayNum()).sum();
        preOrderInfoVo.setRedeemIntegral(redeemIntegral);
        // 购买商品总数量
        int orderProNum = orderInfoList.stream().mapToInt(PreOrderInfoDetailVo::getPayNum).sum();
        preOrderInfoVo.setOrderProNum(orderProNum);

        // 运费计算
        if (preOrderInfoVo.getSecondType().equals(OrderConstants.ORDER_SECOND_TYPE_CLOUD)
                || preOrderInfoVo.getSecondType().equals(OrderConstants.ORDER_SECOND_TYPE_CDKEY)
                || preOrderInfoVo.getSecondType().equals(OrderConstants.ORDER_SECOND_TYPE_VIRTUALLY)) {
            preOrderInfoVo.setFreightFee(BigDecimal.ZERO);
            preOrderInfoVo.setAddressId(0);
        } else {
            // 获取默认地址
            UserAddress userAddress = userAddressService.getDefaultByUid(user.getId());
            if (ObjectUtil.isNotNull(userAddress)) {
                // 计算运费
                getFreightFee_V_1_8(preOrderInfoVo, userAddress, user.getIsPaidMember());
                preOrderInfoVo.setAddressId(userAddress.getId());
            } else {
                preOrderInfoVo.setFreightFee(BigDecimal.ZERO);
                preOrderInfoVo.setAddressId(0);
            }
        }
        // 实际支付金额
        preOrderInfoVo.setSvipDiscountPrice(BigDecimal.ZERO);
        if (user.getIsPaidMember() && preOrderInfoVo.getType().equals(OrderConstants.ORDER_TYPE_BASE)) {
            // svip价格
            BigDecimal svipTotalPrice = orderInfoList.stream()
                    .map(e -> {
                        BigDecimal price;
                        if (e.getIsPaidMember()) {
                            price = e.getVipPrice().multiply(new BigDecimal(e.getPayNum()));
                        } else {
                            price = e.getPrice().multiply(new BigDecimal(e.getPayNum()));
                        }
                        return price;
                    })
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            preOrderInfoVo.setSvipDiscountPrice(preOrderInfoVo.getProTotalFee().subtract(svipTotalPrice));
        }
        preOrderInfoVo.setPayFee(preOrderInfoVo.getProTotalFee().add(preOrderInfoVo.getFreightFee()).subtract(preOrderInfoVo.getSvipDiscountPrice()));
        preOrderInfoVo.setUserIntegral(user.getIntegral());
        preOrderInfoVo.setUserBalance(user.getNowMoney());
        preOrderInfoVo.setIntegralDeductionSwitch(false);
        preOrderInfoVo.setIsUseIntegral(false);

        if (preOrderInfoVo.getSecondType().equals(OrderConstants.ORDER_SECOND_TYPE_VIDEO)
                || preOrderInfoVo.getType().equals(OrderConstants.ORDER_TYPE_SECKILL)
                || preOrderInfoVo.getType().equals(OrderConstants.ORDER_TYPE_PITUAN)) {
            // 活动商品不使用优惠券
            return createPreOrderResponse(user.getId(), preOrderInfoVo);
        }
        if (!preOrderInfoVo.getSecondType().equals(OrderConstants.ORDER_SECOND_TYPE_INTEGRAL)) {
            // 积分订单不进行自动优惠券领取
            preOrderSetCouponPrice(preOrderInfoVo, orderInfoList, user);
        }
        BigDecimal proPayFee = preOrderInfoVo.getProTotalFee().subtract(preOrderInfoVo.getCouponFee()).subtract(preOrderInfoVo.getSvipDiscountPrice());
        preOrderInfoVo.setPayFee(proPayFee.add(preOrderInfoVo.getFreightFee()));
        String integralDeductionSwitch = systemConfigService.getValueByKey(SysConfigConstants.CONFIG_KEY_INTEGRAL_DEDUCTION_SWITCH);
        String integralDeductionStartMoney = systemConfigService.getValueByKey(SysConfigConstants.CONFIG_KEY_INTEGRAL_DEDUCTION_START_MONEY);
        if ("true".equals(integralDeductionSwitch) && proPayFee.compareTo(new BigDecimal(integralDeductionStartMoney)) >= 0) {
            preOrderInfoVo.setIntegralDeductionSwitch(true);
        }
        return createPreOrderResponse(user.getId(), preOrderInfoVo);
    }

    private OrderNoResponse createPreOrderResponse(Integer userId, PreOrderInfoVo preOrderInfoVo) {
        // 拼团下单参数验证
        if(preOrderInfoVo.getType().equals(OrderConstants.ORDER_TYPE_PITUAN)){
            List<PreMerchantOrderVo> merchantOrderVoList = preOrderInfoVo.getMerchantOrderVoList();
            for (PreMerchantOrderVo preMerchantOrderVo : merchantOrderVoList) {
                List<PreOrderInfoDetailVo> orderInfoList = preMerchantOrderVo.getOrderInfoList();
                for (PreOrderInfoDetailVo ov : orderInfoList) {
                    if(ObjectUtil.isEmpty(ov.getGroupBuyActivityId())) throw new CrmebException("拼团活动ID 不能为空");
                    // 判断该当前用户时候购买过！会在初次打开后，在分享界面没法后买状态，但流程已经到购买状态，所以需要判断
                    List<GroupBuyUser> groupBuyUsers = groupBuyUserService.getCurrentGroupBuyActivityRecordListByUserId(
                            userId, ov.getGroupBuyActivityId(), ov.getGroupBuyRecordId(), GroupBuyRecordEnum.GROUP_BUY_RECORD_ENUM_STATUS_INIT.getCode());
                    if(ov.getGroupBuyRecordId()>0 && !groupBuyUsers.isEmpty()) throw new CrmebException("当前拼团已经参与过");
                }
            }
        }
        String key = userId + CrmebDateUtil.getNowTime().toString() + CrmebUtil.getUuid();
        redisUtil.set(OrderConstants.PRE_ORDER_CACHE_PREFIX + key, JSONObject.toJSONString(preOrderInfoVo), OrderConstants.PRE_ORDER_CACHE_TIME, TimeUnit.MINUTES);
        OrderNoResponse response = new OrderNoResponse();
        response.setOrderNo(key);
        response.setOrderType(preOrderInfoVo.getType());
        return response;
    }

    private void preOrderSetCouponPrice(PreOrderInfoVo preOrderInfoVo, List<PreOrderInfoDetailVo> orderInfoList, User user) {
        // 自动领券计算
        BigDecimal couponPrice = BigDecimal.ZERO;
        List<Integer> proIdsList = orderInfoList.stream().map(PreOrderInfoDetailVo::getProductId).distinct().collect(Collectors.toList());
        List<Integer> merIdList = new ArrayList<>();
        for (PreMerchantOrderVo merchantOrderVo : preOrderInfoVo.getMerchantOrderVoList()) {
            Integer merId = merchantOrderVo.getMerId();
            merIdList.add(merId);
            List<Integer> proIdList = merchantOrderVo.getOrderInfoList().stream().map(PreOrderInfoDetailVo::getProductId).collect(Collectors.toList());
            BigDecimal merPrice = merchantOrderVo.getProTotalFee().subtract(merchantOrderVo.getSvipDiscountPrice());
            List<Coupon> merCouponList = couponService.findManyByMerIdAndMoney(merId, proIdList, merPrice);
            for (int i = 0; i < merCouponList.size(); ) {
                Coupon coupon = merCouponList.get(i);
                if (coupon.getCategory().equals(CouponConstants.COUPON_CATEGORY_MERCHANT)) {
                    i++;
                    continue;
                }
                List<Integer> cpIdList = CrmebUtil.stringToArray(coupon.getLinkedData());
                List<PreOrderInfoDetailVo> detailVoList = merchantOrderVo.getOrderInfoList().stream().filter(f -> cpIdList.contains(f.getProductId())).collect(Collectors.toList());
                BigDecimal proPrice = detailVoList.stream()
                        .map(e -> {
                            BigDecimal price;
                            if (e.getIsPaidMember() && user.getIsPaidMember()) {
                                price = e.getVipPrice().multiply(new BigDecimal(e.getPayNum()));
                            } else {
                                price = e.getPrice().multiply(new BigDecimal(e.getPayNum()));
                            }
                            return price;
                        })
                        .reduce(BigDecimal.ZERO, BigDecimal::add);
                if (proPrice.compareTo(new BigDecimal(coupon.getMinPrice().toString())) < 0) {
                    merCouponList.remove(i);
                    continue;
                }
                if (proPrice.compareTo(new BigDecimal(coupon.getMoney().toString())) <= 0) {
                    merCouponList.remove(i);
                    continue;
                }
                i++;
            }
            // 查询适用的用户优惠券
            List<CouponUser> merCouponUserList = couponUserService.findManyByUidAndMerIdAndMoneyAndProList(user.getId(), merchantOrderVo.getMerId(), proIdList, merPrice);
            for (int i = 0; i < merCouponUserList.size(); ) {
                CouponUser couponUser = merCouponUserList.get(i);
                if (couponUser.getCategory().equals(CouponConstants.COUPON_CATEGORY_MERCHANT)) {
                    i++;
                    continue;
                }
                Coupon coupon = couponService.getById(couponUser.getCouponId());
                List<Integer> cpIdList = CrmebUtil.stringToArray(coupon.getLinkedData());
                List<PreOrderInfoDetailVo> detailVoList = merchantOrderVo.getOrderInfoList().stream().filter(f -> cpIdList.contains(f.getProductId())).collect(Collectors.toList());
                BigDecimal proPrice = detailVoList.stream()
                        .map(e -> {
                            BigDecimal price;
                            if (e.getIsPaidMember() && user.getIsPaidMember()) {
                                price = e.getVipPrice().multiply(new BigDecimal(e.getPayNum()));
                            } else {
                                price = e.getPrice().multiply(new BigDecimal(e.getPayNum()));
                            }
                            return price;
                        })
                        .reduce(BigDecimal.ZERO, BigDecimal::add);
                if (proPrice.compareTo(new BigDecimal(couponUser.getMinPrice().toString())) < 0) {
                    merCouponUserList.remove(i);
                    continue;
                }
                if (proPrice.compareTo(new BigDecimal(coupon.getMoney().toString())) <= 0) {
                    merCouponUserList.remove(i);
                    continue;
                }
                i++;
            }

            List<Integer> cidList = merCouponUserList.stream().map(CouponUser::getCouponId).collect(Collectors.toList());
            for (int i = 0; i < merCouponList.size(); ) {
                if (cidList.contains(merCouponList.get(i).getId())) {
                    merCouponList.remove(i);
                    continue;
                }
                if (!couponUserService.userIsCanReceiveCoupon(merCouponList.get(i), user.getId())) {
                    merCouponList.remove(i);
                    continue;
                }
                i++;
            }

            if (CollUtil.isEmpty(merCouponList) && CollUtil.isEmpty(merCouponUserList)) {
                continue;
            }
            if (CollUtil.isEmpty(merCouponList)) {
                CouponUser couponUser = merCouponUserList.get(0);
                merchantOrderVo.setUserCouponId(couponUser.getId());
                merchantOrderVo.setCouponFee(new BigDecimal(couponUser.getMoney().toString()));
                merchantOrderVo.setMerCouponFee(new BigDecimal(couponUser.getMoney().toString()));
                setMerProCouponPrice(merchantOrderVo, null, couponUser, user.getIsPaidMember());
                couponPrice = couponPrice.add(merchantOrderVo.getMerCouponFee());
                continue;
            }
            if (CollUtil.isEmpty(merCouponUserList)) {
                Coupon coupon = merCouponList.get(0);
                Integer couponUserId = couponUserService.autoReceiveCoupon(coupon, user.getId());
                merchantOrderVo.setUserCouponId(couponUserId);
                merchantOrderVo.setCouponFee(new BigDecimal(coupon.getMoney().toString()));
                merchantOrderVo.setMerCouponFee(new BigDecimal(coupon.getMoney().toString()));
                setMerProCouponPrice(merchantOrderVo, coupon, null, user.getIsPaidMember());
                couponPrice = couponPrice.add(merchantOrderVo.getMerCouponFee());
                continue;
            }

            Coupon coupon = merCouponList.get(0);
            CouponUser couponUser = merCouponUserList.get(0);
            if (couponUser.getMoney() >= coupon.getMoney()) {
                // 使用用户优惠券
                merchantOrderVo.setUserCouponId(merCouponUserList.get(0).getId());
                merchantOrderVo.setCouponFee(new BigDecimal(couponUser.getMoney().toString()));
                merchantOrderVo.setMerCouponFee(new BigDecimal(couponUser.getMoney().toString()));
                setMerProCouponPrice(merchantOrderVo, null, couponUser, user.getIsPaidMember());
            } else {
                // 领取优惠券下单
                Integer couponUserId = couponUserService.autoReceiveCoupon(merCouponList.get(0), user.getId());
                merchantOrderVo.setUserCouponId(couponUserId);
                merchantOrderVo.setCouponFee(new BigDecimal(coupon.getMoney().toString()));
                merchantOrderVo.setMerCouponFee(new BigDecimal(coupon.getMoney().toString()));
                setMerProCouponPrice(merchantOrderVo, coupon, null, user.getIsPaidMember());
            }
            couponPrice = couponPrice.add(merchantOrderVo.getMerCouponFee());
        }

        preOrderInfoVo.setMerCouponFee(couponPrice);
        // 自动领平台券计算
        // 查所有适用的平台优惠券
        BigDecimal remainPrice = preOrderInfoVo.getProTotalFee().subtract(preOrderInfoVo.getSvipDiscountPrice()).subtract(preOrderInfoVo.getMerCouponFee());
        List<Product> productList = productService.findByIds(proIdsList);
        List<Integer> proCategoryIdList = productList.stream().map(Product::getCategoryId).collect(Collectors.toList());
        List<Integer> secondParentIdList = productCategoryService.findParentIdByChildIds(proCategoryIdList);
        List<Integer> firstParentIdList = productCategoryService.findParentIdByChildIds(secondParentIdList);
        proCategoryIdList.addAll(secondParentIdList);
        proCategoryIdList.addAll(firstParentIdList);
        List<Integer> brandIdList = productList.stream().map(Product::getBrandId).collect(Collectors.toList());
        List<Coupon> platCouponList = couponService.findManyPlatByMerIdAndMoney(proIdsList, proCategoryIdList, merIdList, brandIdList, preOrderInfoVo.getProTotalFee().subtract(preOrderInfoVo.getSvipDiscountPrice()));
        for (int i = 0; i < platCouponList.size(); ) {
            Coupon coupon = platCouponList.get(i);
            if (coupon.getCategory().equals(CouponConstants.COUPON_CATEGORY_UNIVERSAL)) {
                if (remainPrice.compareTo(new BigDecimal(coupon.getMoney().toString())) <= 0) {
                    platCouponList.remove(i);
                    continue;
                }
            }
            if (coupon.getCategory().equals(CouponConstants.COUPON_CATEGORY_PRODUCT)) {
                List<Integer> cpIdList = CrmebUtil.stringToArray(coupon.getLinkedData());
                BigDecimal proPrice = orderInfoList.stream().filter(f -> cpIdList.contains(f.getProductId()))
                        .map(e -> {
                            BigDecimal price;
                            if (e.getIsPaidMember() && user.getIsPaidMember()) {
                                price = e.getVipPrice().multiply(new BigDecimal(e.getPayNum()));
                            } else {
                                price = e.getPrice().multiply(new BigDecimal(e.getPayNum()));
                            }
                            return price;
                        })
                        .reduce(BigDecimal.ZERO, BigDecimal::add);
                BigDecimal proSubPrice = orderInfoList.stream().filter(f -> cpIdList.contains(f.getProductId()))
                        .map(e -> {
                            BigDecimal price;
                            if (e.getIsPaidMember() && user.getIsPaidMember()) {
                                price = e.getVipPrice().multiply(new BigDecimal(e.getPayNum()));
                            } else {
                                price = e.getPrice().multiply(new BigDecimal(e.getPayNum()));
                            }
                            return price.subtract(e.getMerCouponPrice());
                        })
                        .reduce(BigDecimal.ZERO, BigDecimal::add);
                if (proPrice.compareTo(new BigDecimal(coupon.getMinPrice().toString())) < 0) {
                    platCouponList.remove(i);
                    continue;
                }
                if (proSubPrice.compareTo(new BigDecimal(coupon.getMoney().toString())) <= 0) {
                    platCouponList.remove(i);
                    continue;
                }
            }
            if (coupon.getCategory().equals(CouponConstants.COUPON_CATEGORY_PRODUCT_CATEGORY)) {
                List<Integer> cidList = new ArrayList<>();
                Integer categoryId = Integer.valueOf(coupon.getLinkedData());
                ProductCategory category = productCategoryService.getById(categoryId);
                if (category.getLevel().equals(3)) {
                    cidList.add(categoryId);
                } else {
                    List<ProductCategory> productCategoryList = productCategoryService.findAllChildListByPid(category.getId(), category.getLevel());
                    if (category.getLevel().equals(1)) {
                        productCategoryList = productCategoryList.stream().filter(f -> f.getLevel().equals(3)).collect(Collectors.toList());
                    }
                    cidList.addAll(productCategoryList.stream().map(ProductCategory::getId).collect(Collectors.toList()));
                }
                List<Integer> pIdList = productList.stream().filter(f -> cidList.contains(f.getCategoryId())).map(Product::getId).collect(Collectors.toList());
                BigDecimal proPrice = orderInfoList.stream().filter(f -> pIdList.contains(f.getProductId()))
                        .map(e -> {
                            BigDecimal price;
                            if (e.getIsPaidMember() && user.getIsPaidMember()) {
                                price = e.getVipPrice().multiply(new BigDecimal(e.getPayNum()));
                            } else {
                                price = e.getPrice().multiply(new BigDecimal(e.getPayNum()));
                            }
                            return price;
                        })
                        .reduce(BigDecimal.ZERO, BigDecimal::add);
                BigDecimal proSubPrice = orderInfoList.stream().filter(f -> pIdList.contains(f.getProductId()))
                        .map(e -> {
                            BigDecimal price;
                            if (e.getIsPaidMember() && user.getIsPaidMember()) {
                                price = e.getVipPrice().multiply(new BigDecimal(e.getPayNum()));
                            } else {
                                price = e.getPrice().multiply(new BigDecimal(e.getPayNum()));
                            }
                            return price.subtract(e.getMerCouponPrice());
                        })
                        .reduce(BigDecimal.ZERO, BigDecimal::add);
                if (proPrice.compareTo(new BigDecimal(coupon.getMinPrice().toString())) < 0) {
                    platCouponList.remove(i);
                    continue;
                }
                if (proSubPrice.compareTo(new BigDecimal(coupon.getMoney().toString())) <= 0) {
                    platCouponList.remove(i);
                    continue;
                }
            }
            if (coupon.getCategory().equals(CouponConstants.COUPON_CATEGORY_BRAND)) {
                Integer brandId = Integer.valueOf(coupon.getLinkedData());
                List<Integer> pIdList = productList.stream().filter(f -> brandId.equals(f.getBrandId())).map(Product::getId).collect(Collectors.toList());
                BigDecimal proPrice = orderInfoList.stream().filter(f -> pIdList.contains(f.getProductId()))
                        .map(e -> {
                            BigDecimal price;
                            if (e.getIsPaidMember() && user.getIsPaidMember()) {
                                price = e.getVipPrice().multiply(new BigDecimal(e.getPayNum()));
                            } else {
                                price = e.getPrice().multiply(new BigDecimal(e.getPayNum()));
                            }
                            return price;
                        })
                        .reduce(BigDecimal.ZERO, BigDecimal::add);
                BigDecimal proSubPrice = orderInfoList.stream().filter(f -> pIdList.contains(f.getProductId()))
                        .map(e -> {
                            BigDecimal price;
                            if (e.getIsPaidMember() && user.getIsPaidMember()) {
                                price = e.getVipPrice().multiply(new BigDecimal(e.getPayNum()));
                            } else {
                                price = e.getPrice().multiply(new BigDecimal(e.getPayNum()));
                            }
                            return price.subtract(e.getMerCouponPrice());
                        })
                        .reduce(BigDecimal.ZERO, BigDecimal::add);
                if (proPrice.compareTo(new BigDecimal(coupon.getMinPrice().toString())) < 0) {
                    platCouponList.remove(i);
                    continue;
                }
                if (proSubPrice.compareTo(new BigDecimal(coupon.getMoney().toString())) <= 0) {
                    platCouponList.remove(i);
                    continue;
                }
            }
            if (coupon.getCategory().equals(CouponConstants.COUPON_CATEGORY_JOINT_MERCHANT)) {
                List<Integer> mpIdList = CrmebUtil.stringToArray(coupon.getLinkedData());
                List<Integer> pIdList = productList.stream().filter(f -> mpIdList.contains(f.getMerId())).map(Product::getId).collect(Collectors.toList());
                BigDecimal proPrice = orderInfoList.stream().filter(f -> pIdList.contains(f.getProductId()))
                        .map(e -> {
                            BigDecimal price;
                            if (e.getIsPaidMember() && user.getIsPaidMember()) {
                                price = e.getVipPrice().multiply(new BigDecimal(e.getPayNum()));
                            } else {
                                price = e.getPrice().multiply(new BigDecimal(e.getPayNum()));
                            }
                            return price;
                        })
                        .reduce(BigDecimal.ZERO, BigDecimal::add);
                BigDecimal proSubPrice = orderInfoList.stream().filter(f -> pIdList.contains(f.getProductId()))
                        .map(e -> {
                            BigDecimal price;
                            if (e.getIsPaidMember() && user.getIsPaidMember()) {
                                price = e.getVipPrice().multiply(new BigDecimal(e.getPayNum()));
                            } else {
                                price = e.getPrice().multiply(new BigDecimal(e.getPayNum()));
                            }
                            return price.subtract(e.getMerCouponPrice());
                        })
                        .reduce(BigDecimal.ZERO, BigDecimal::add);
                if (proPrice.compareTo(new BigDecimal(coupon.getMinPrice().toString())) < 0) {
                    platCouponList.remove(i);
                    continue;
                }
                if (proSubPrice.compareTo(new BigDecimal(coupon.getMoney().toString())) <= 0) {
                    platCouponList.remove(i);
                    continue;
                }
            }
            i++;
        }
        // 查询适用的用户平台优惠券
        List<CouponUser> platCouponUserList = couponUserService.findManyPlatByUidAndMerIdAndMoneyAndProList(user.getId(), proIdsList, proCategoryIdList, merIdList, brandIdList, preOrderInfoVo.getProTotalFee().subtract(preOrderInfoVo.getSvipDiscountPrice()));
        for (int i = 0; i < platCouponUserList.size(); ) {
            CouponUser couponUser = platCouponUserList.get(i);
            if (couponUser.getCategory().equals(CouponConstants.COUPON_CATEGORY_UNIVERSAL)) {
                if (remainPrice.compareTo(new BigDecimal(couponUser.getMoney().toString())) <= 0) {
                    platCouponUserList.remove(i);
                    continue;
                }
            }
            Coupon coupon = couponService.getById(couponUser.getCouponId());
            if (couponUser.getCategory().equals(CouponConstants.COUPON_CATEGORY_PRODUCT)) {
                List<Integer> cpIdList = CrmebUtil.stringToArray(coupon.getLinkedData());
                BigDecimal proPrice = orderInfoList.stream().filter(f -> cpIdList.contains(f.getProductId()))
                        .map(e -> {
                            BigDecimal price;
                            if (e.getIsPaidMember() && user.getIsPaidMember()) {
                                price = e.getVipPrice().multiply(new BigDecimal(e.getPayNum()));
                            } else {
                                price = e.getPrice().multiply(new BigDecimal(e.getPayNum()));
                            }
                            return price;
                        })
                        .reduce(BigDecimal.ZERO, BigDecimal::add);
                BigDecimal proSubPrice = orderInfoList.stream().filter(f -> cpIdList.contains(f.getProductId()))
                        .map(e -> {
                            BigDecimal price;
                            if (e.getIsPaidMember() && user.getIsPaidMember()) {
                                price = e.getVipPrice().multiply(new BigDecimal(e.getPayNum()));
                            } else {
                                price = e.getPrice().multiply(new BigDecimal(e.getPayNum()));
                            }
                            return price.subtract(e.getMerCouponPrice());
                        })
                        .reduce(BigDecimal.ZERO, BigDecimal::add);
                if (proPrice.compareTo(new BigDecimal(couponUser.getMinPrice().toString())) < 0) {
                    platCouponUserList.remove(i);
                    continue;
                }
                if (proSubPrice.compareTo(new BigDecimal(coupon.getMoney().toString())) <= 0) {
                    platCouponUserList.remove(i);
                    continue;
                }
            }
            if (couponUser.getCategory().equals(CouponConstants.COUPON_CATEGORY_PRODUCT_CATEGORY)) {
                List<Integer> cidList = new ArrayList<>();
                Integer categoryId = Integer.valueOf(coupon.getLinkedData());
                ProductCategory category = productCategoryService.getById(categoryId);
                if (category.getLevel().equals(3)) {
                    cidList.add(categoryId);
                } else {
                    List<ProductCategory> productCategoryList = productCategoryService.findAllChildListByPid(category.getId(), category.getLevel());
                    if (category.getLevel().equals(1)) {
                        productCategoryList = productCategoryList.stream().filter(f -> f.getLevel().equals(3)).collect(Collectors.toList());
                    }
                    cidList.addAll(productCategoryList.stream().map(ProductCategory::getId).collect(Collectors.toList()));
                }
                List<Integer> pIdList = productList.stream().filter(f -> cidList.contains(f.getCategoryId())).map(Product::getId).collect(Collectors.toList());
                BigDecimal proPrice = orderInfoList.stream().filter(f -> pIdList.contains(f.getProductId()))
                        .map(e -> {
                            BigDecimal price;
                            if (e.getIsPaidMember() && user.getIsPaidMember()) {
                                price = e.getVipPrice().multiply(new BigDecimal(e.getPayNum()));
                            } else {
                                price = e.getPrice().multiply(new BigDecimal(e.getPayNum()));
                            }
                            return price;
                        })
                        .reduce(BigDecimal.ZERO, BigDecimal::add);
                BigDecimal proSubPrice = orderInfoList.stream().filter(f -> pIdList.contains(f.getProductId()))
                        .map(e -> {
                            BigDecimal price;
                            if (e.getIsPaidMember() && user.getIsPaidMember()) {
                                price = e.getVipPrice().multiply(new BigDecimal(e.getPayNum()));
                            } else {
                                price = e.getPrice().multiply(new BigDecimal(e.getPayNum()));
                            }
                            return price.subtract(e.getMerCouponPrice());
                        })
                        .reduce(BigDecimal.ZERO, BigDecimal::add);
                if (proPrice.compareTo(new BigDecimal(couponUser.getMinPrice().toString())) < 0) {
                    platCouponUserList.remove(i);
                    continue;
                }
                if (proSubPrice.compareTo(new BigDecimal(coupon.getMoney().toString())) <= 0) {
                    platCouponUserList.remove(i);
                    continue;
                }
            }
            if (couponUser.getCategory().equals(CouponConstants.COUPON_CATEGORY_BRAND)) {
                Integer brandId = Integer.valueOf(coupon.getLinkedData());
                List<Integer> pIdList = productList.stream().filter(f -> brandId.equals(f.getBrandId())).map(Product::getId).collect(Collectors.toList());
                BigDecimal proPrice = orderInfoList.stream().filter(f -> pIdList.contains(f.getProductId()))
                        .map(e -> {
                            BigDecimal price;
                            if (e.getIsPaidMember() && user.getIsPaidMember()) {
                                price = e.getVipPrice().multiply(new BigDecimal(e.getPayNum()));
                            } else {
                                price = e.getPrice().multiply(new BigDecimal(e.getPayNum()));
                            }
                            return price;
                        })
                        .reduce(BigDecimal.ZERO, BigDecimal::add);
                BigDecimal proSubPrice = orderInfoList.stream().filter(f -> pIdList.contains(f.getProductId()))
                        .map(e -> {
                            BigDecimal price;
                            if (e.getIsPaidMember() && user.getIsPaidMember()) {
                                price = e.getVipPrice().multiply(new BigDecimal(e.getPayNum()));
                            } else {
                                price = e.getPrice().multiply(new BigDecimal(e.getPayNum()));
                            }
                            return price.subtract(e.getMerCouponPrice());
                        })
                        .reduce(BigDecimal.ZERO, BigDecimal::add);
                if (proPrice.compareTo(new BigDecimal(couponUser.getMinPrice().toString())) < 0) {
                    platCouponUserList.remove(i);
                    continue;
                }
                if (proSubPrice.compareTo(new BigDecimal(coupon.getMoney().toString())) <= 0) {
                    platCouponUserList.remove(i);
                    continue;
                }
            }
            if (couponUser.getCategory().equals(CouponConstants.COUPON_CATEGORY_JOINT_MERCHANT)) {
                List<Integer> mpIdList = CrmebUtil.stringToArray(coupon.getLinkedData());
                List<Integer> pIdList = productList.stream().filter(f -> mpIdList.contains(f.getMerId())).map(Product::getId).collect(Collectors.toList());
                BigDecimal proPrice = orderInfoList.stream().filter(f -> pIdList.contains(f.getProductId()))
                        .map(e -> {
                            BigDecimal price;
                            if (e.getIsPaidMember() && user.getIsPaidMember()) {
                                price = e.getVipPrice().multiply(new BigDecimal(e.getPayNum()));
                            } else {
                                price = e.getPrice().multiply(new BigDecimal(e.getPayNum()));
                            }
                            return price;
                        })
                        .reduce(BigDecimal.ZERO, BigDecimal::add);
                BigDecimal proSubPrice = orderInfoList.stream().filter(f -> pIdList.contains(f.getProductId()))
                        .map(e -> {
                            BigDecimal price;
                            if (e.getIsPaidMember() && user.getIsPaidMember()) {
                                price = e.getVipPrice().multiply(new BigDecimal(e.getPayNum()));
                            } else {
                                price = e.getPrice().multiply(new BigDecimal(e.getPayNum()));
                            }
                            return price.subtract(e.getMerCouponPrice());
                        })
                        .reduce(BigDecimal.ZERO, BigDecimal::add);
                if (proPrice.compareTo(new BigDecimal(couponUser.getMinPrice().toString())) < 0) {
                    platCouponUserList.remove(i);
                    continue;
                }
                if (proSubPrice.compareTo(new BigDecimal(coupon.getMoney().toString())) <= 0) {
                    platCouponUserList.remove(i);
                    continue;
                }
            }
            i++;
        }

        List<Integer> platCidList = platCouponUserList.stream().map(CouponUser::getCouponId).collect(Collectors.toList());
        for (int i = 0; i < platCouponList.size(); ) {
            if (platCidList.contains(platCouponList.get(i).getId())) {
                platCouponList.remove(i);
                continue;
            }
            if (!couponUserService.userIsCanReceiveCoupon(platCouponList.get(i), user.getId())) {
                platCouponList.remove(i);
                continue;
            }
            i++;
        }

        if (CollUtil.isNotEmpty(platCouponUserList) && CollUtil.isEmpty(platCouponList)) {
            // 使用用户优惠券
            preOrderInfoVo.setPlatCouponFee(new BigDecimal(platCouponUserList.get(0).getMoney().toString()));
            preOrderInfoVo.setPlatUserCouponId(platCouponUserList.get(0).getId());
        }
        if (CollUtil.isEmpty(platCouponUserList) && CollUtil.isNotEmpty(platCouponList)) {
            // 领取优惠券下单
            preOrderInfoVo.setPlatCouponFee(new BigDecimal(platCouponList.get(0).getMoney().toString()));
            Integer couponUserId = couponUserService.autoReceiveCoupon(platCouponList.get(0), user.getId());
            preOrderInfoVo.setPlatUserCouponId(couponUserId);
        }
        if (CollUtil.isNotEmpty(platCouponUserList) && CollUtil.isNotEmpty(platCouponList)) {
            Long platCouponMoney = platCouponList.get(0).getMoney();
            Long platCouponUserMoney = platCouponUserList.get(0).getMoney();
            if (platCouponUserMoney >= platCouponMoney) {
                // 使用用户优惠券
                preOrderInfoVo.setPlatCouponFee(new BigDecimal(platCouponUserList.get(0).getMoney().toString()));
                preOrderInfoVo.setPlatUserCouponId(platCouponUserList.get(0).getId());
            } else {
                // 领取优惠券下单
                preOrderInfoVo.setPlatCouponFee(new BigDecimal(platCouponList.get(0).getMoney().toString()));
                Integer couponUserId = couponUserService.autoReceiveCoupon(platCouponList.get(0), user.getId());
                preOrderInfoVo.setPlatUserCouponId(couponUserId);
            }
        }
        preOrderInfoVo.setCouponFee(preOrderInfoVo.getMerCouponFee().add(preOrderInfoVo.getPlatCouponFee()));
    }

    /**
     * 设置订单商品详情商户优惠价格
     */
    private void setMerProCouponPrice(PreMerchantOrderVo merchantOrderVo, Coupon coupon, CouponUser couponUser, Boolean userIsPaidMember) {
        if (ObjectUtil.isNull(coupon)) {
            coupon = couponService.getById(couponUser.getCouponId());
        }
        List<PreOrderInfoDetailVo> proDtoList = new ArrayList<>();
        if (coupon.getCategory().equals(CouponConstants.COUPON_CATEGORY_MERCHANT)) {
            proDtoList = merchantOrderVo.getOrderInfoList();
        }
        if (coupon.getCategory().equals(CouponConstants.COUPON_CATEGORY_PRODUCT)) {
            List<Integer> proIdList = CrmebUtil.stringToArray(coupon.getLinkedData());
            proDtoList = merchantOrderVo.getOrderInfoList().stream().filter(d -> proIdList.contains(d.getProductId())).collect(Collectors.toList());
        }
        BigDecimal proTotalPrice = proDtoList.stream()
                .map(e -> {
                    BigDecimal price;
                    if (e.getIsPaidMember() && userIsPaidMember) {
                        price = e.getVipPrice().multiply(new BigDecimal(e.getPayNum()));
                    } else {
                        price = e.getPrice().multiply(new BigDecimal(e.getPayNum()));
                    }
                    return price;
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal couponPrice = merchantOrderVo.getCouponFee();
        for (int i = 0; i < proDtoList.size(); i++) {
            PreOrderInfoDetailVo d = proDtoList.get(i);
            if (proDtoList.size() == (i + 1)) {
                d.setMerCouponPrice(couponPrice);
                break;
            }
            BigDecimal proPrice;
            if (d.getIsPaidMember() && userIsPaidMember) {
                proPrice = d.getVipPrice().multiply(new BigDecimal(d.getPayNum().toString()));
            } else {
                proPrice = d.getPrice().multiply(new BigDecimal(d.getPayNum().toString()));
            }
            BigDecimal ratio = proPrice.divide(proTotalPrice, 10, RoundingMode.HALF_UP);
            BigDecimal detailCouponFee = couponPrice.multiply(ratio).setScale(2, RoundingMode.HALF_UP);
            couponPrice = couponPrice.subtract(detailCouponFee);
            d.setMerCouponPrice(detailCouponFee);
        }
    }

    /**
     * 加载预下单信息
     *
     * @param preOrderNo 预下单号
     * @return 预下单信息
     */
    @Override
    public PreOrderResponse loadPreOrder(String preOrderNo) {
        Integer userId = userService.getUserIdException();
        User user = userService.getById(userId);
        // 通过缓存获取预下单对象
        String key = OrderConstants.PRE_ORDER_CACHE_PREFIX + preOrderNo;
        boolean exists = redisUtil.exists(key);
        if (!exists) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "预下单订单不存在");
        }
        String orderVoString = redisUtil.get(key);
        PreOrderInfoVo orderInfoVo = JSONObject.parseObject(orderVoString, PreOrderInfoVo.class);
        PreOrderResponse preOrderResponse = new PreOrderResponse();
        BeanUtils.copyProperties(orderInfoVo, preOrderResponse);

        List<PreOrderMerchantInfoResponse> infoResponseList = new ArrayList<>();
        List<PreMerchantOrderVo> merchantOrderVoList = orderInfoVo.getMerchantOrderVoList();
        if (orderInfoVo.getType().equals(OrderConstants.ORDER_TYPE_SECKILL) || orderInfoVo.getSecondType().equals(OrderConstants.ORDER_SECOND_TYPE_VIDEO)
                || orderInfoVo.getSecondType().equals(OrderConstants.ORDER_SECOND_TYPE_INTEGRAL)) {
            for (PreMerchantOrderVo merchantOrderVo : merchantOrderVoList) {
                PreOrderMerchantInfoResponse infoResponse = new PreOrderMerchantInfoResponse();
                BeanUtils.copyProperties(merchantOrderVo, infoResponse);
                infoResponseList.add(infoResponse);
            }
            preOrderResponse.setMerchantInfoList(infoResponseList);
            return preOrderResponse;
        }
        List<Integer> merIdList = new ArrayList<>();
        List<Integer> proIdList = new ArrayList<>();
        List<Product> productList = new ArrayList<>();
        List<PreOrderInfoDetailVo> orderDetailInfoList = new ArrayList<>();
        for (PreMerchantOrderVo merchantOrderVo : merchantOrderVoList) {
            merIdList.add(merchantOrderVo.getMerId());
            PreOrderMerchantInfoResponse infoResponse = new PreOrderMerchantInfoResponse();
            BeanUtils.copyProperties(merchantOrderVo, infoResponse);
            merchantOrderVo.getOrderInfoList().forEach(info -> {
                if (!proIdList.contains(info.getProductId())) {
                    Product product = productService.getById(info.getProductId());
                    productList.add(product);
                    proIdList.add(product.getId());
                }
            });
            orderDetailInfoList.addAll(merchantOrderVo.getOrderInfoList());
            // 查询适用的用户优惠券
            List<Integer> merProIdList = merchantOrderVo.getOrderInfoList().stream().map(PreOrderInfoDetailVo::getProductId).distinct().collect(Collectors.toList());
            BigDecimal merPrice = merchantOrderVo.getProTotalFee().subtract(merchantOrderVo.getSvipDiscountPrice());
            BigDecimal merRemainingAmount = merPrice.subtract(orderInfoVo.getPlatCouponFee());
            List<CouponUser> merCouponUserList = couponUserService.findManyByUidAndMerIdAndMoneyAndProList(user.getId(), merchantOrderVo.getMerId(), merProIdList, merPrice);
            for (int i = 0; i < merCouponUserList.size(); ) {
                CouponUser couponUser = merCouponUserList.get(i);
                if (merchantOrderVo.getUserCouponId() > 0 && couponUser.getId().equals(merchantOrderVo.getUserCouponId())) {
                    couponUser.setIsChecked(true);
                    couponUser.setIsChoose(true);
                    i++;
                    continue;
                }
                if (couponUser.getCategory().equals(CouponConstants.COUPON_CATEGORY_MERCHANT)) {
                    if (merRemainingAmount.compareTo(new BigDecimal(couponUser.getMoney().toString())) > 0) {
                        couponUser.setIsChoose(true);
                    }
                    i++;
                    continue;
                }
                Coupon coupon = couponService.getById(couponUser.getCouponId());
                List<Integer> cpIdList = CrmebUtil.stringToArray(coupon.getLinkedData());
                List<PreOrderInfoDetailVo> detailVoList = merchantOrderVo.getOrderInfoList().stream().filter(f -> cpIdList.contains(f.getProductId())).collect(Collectors.toList());
                BigDecimal proPrice = detailVoList.stream()
                        .map(e -> {
                            BigDecimal price;
                            if (e.getIsPaidMember() && user.getIsPaidMember()) {
                                price = e.getVipPrice().multiply(new BigDecimal(e.getPayNum()));
                            } else {
                                price = e.getPrice().multiply(new BigDecimal(e.getPayNum()));
                            }
                            return price;
                        }).reduce(BigDecimal.ZERO, BigDecimal::add);
                BigDecimal proRemainingAmount = detailVoList.stream()
                        .map(e -> {
                            BigDecimal price;
                            if (e.getIsPaidMember() && user.getIsPaidMember()) {
                                price = e.getVipPrice().multiply(new BigDecimal(e.getPayNum()));
                            } else {
                                price = e.getPrice().multiply(new BigDecimal(e.getPayNum()));
                            }
                            return price.subtract(e.getPlatCouponPrice());
                        }).reduce(BigDecimal.ZERO, BigDecimal::add);
                if (proPrice.compareTo(new BigDecimal(couponUser.getMinPrice().toString())) < 0) {
                    merCouponUserList.remove(i);
                    continue;
                }
                if (proPrice.compareTo(new BigDecimal(couponUser.getMoney().toString())) <= 0) {
                    merCouponUserList.remove(i);
                    continue;
                }
                if (proRemainingAmount.compareTo(new BigDecimal(couponUser.getMinPrice().toString())) >= 0) {
                    if (proRemainingAmount.compareTo(new BigDecimal(couponUser.getMoney().toString())) > 0) {
                        couponUser.setIsChoose(true);
                    }
                }
                i++;
            }
            infoResponse.setMerCouponUserList(merCouponUserList);
            infoResponseList.add(infoResponse);
        }
        preOrderResponse.setMerchantInfoList(infoResponseList);

        // 获取平台可用优惠券列表
        BigDecimal proTotalFee = orderInfoVo.getProTotalFee().subtract(orderInfoVo.getSvipDiscountPrice());
        List<Integer> proCategoryIdList = productList.stream().map(Product::getCategoryId).collect(Collectors.toList());
        List<Integer> secondParentIdList = productCategoryService.findParentIdByChildIds(proCategoryIdList);
        List<Integer> firstParentIdList = productCategoryService.findParentIdByChildIds(secondParentIdList);
        proCategoryIdList.addAll(secondParentIdList);
        proCategoryIdList.addAll(firstParentIdList);
        List<Integer> brandIdList = productList.stream().map(Product::getBrandId).collect(Collectors.toList());
        List<CouponUser> platCouponUserList = couponUserService.findManyPlatByUidAndMerIdAndMoneyAndProList(user.getId(), proIdList, proCategoryIdList, merIdList, brandIdList, proTotalFee);

        BigDecimal remainingAmount = orderInfoVo.getProTotalFee().subtract(orderInfoVo.getSvipDiscountPrice()).subtract(orderInfoVo.getMerCouponFee());
        for (CouponUser couponUser : platCouponUserList) {
            if (orderInfoVo.getPlatUserCouponId() > 0 && couponUser.getId().equals(orderInfoVo.getPlatUserCouponId())) {
                couponUser.setIsChecked(true);
                couponUser.setIsChoose(true);
            }
            // 判断是否可用
            if (remainingAmount.compareTo(new BigDecimal(couponUser.getMoney().toString())) <= 0) {
                continue;
            }
            if (couponUser.getCategory().equals(CouponConstants.COUPON_CATEGORY_UNIVERSAL)) {
                if (orderInfoVo.getProTotalFee().compareTo(new BigDecimal(couponUser.getMinPrice().toString())) >= 0
                        && remainingAmount.compareTo(new BigDecimal(couponUser.getMoney().toString())) > 0) {
                    couponUser.setIsChoose(true);
                }
                continue;
            }
            Coupon coupon = couponService.getById(couponUser.getCouponId());
            if (couponUser.getCategory().equals(CouponConstants.COUPON_CATEGORY_PRODUCT)) {
                List<Integer> cpIdList = CrmebUtil.stringToArray(coupon.getLinkedData());
                BigDecimal proPrice = orderDetailInfoList.stream().filter(f -> cpIdList.contains(f.getProductId()))
                        .map(e -> {
                            BigDecimal price;
                            if (e.getIsPaidMember() && user.getIsPaidMember()) {
                                price = e.getVipPrice().multiply(new BigDecimal(e.getPayNum()));
                            } else {
                                price = e.getPrice().multiply(new BigDecimal(e.getPayNum()));
                            }
                            return price;
                        }).reduce(BigDecimal.ZERO, BigDecimal::add);
                BigDecimal proSubPrice = orderDetailInfoList.stream().filter(f -> cpIdList.contains(f.getProductId()))
                        .map(e -> {
                            BigDecimal price;
                            if (e.getIsPaidMember() && user.getIsPaidMember()) {
                                price = e.getVipPrice().multiply(new BigDecimal(e.getPayNum()));
                            } else {
                                price = e.getPrice().multiply(new BigDecimal(e.getPayNum()));
                            }
                            return price.subtract(e.getMerCouponPrice());
                        }).reduce(BigDecimal.ZERO, BigDecimal::add);
                if (proPrice.compareTo(new BigDecimal(couponUser.getMinPrice().toString())) >= 0
                        && proSubPrice.compareTo(new BigDecimal(couponUser.getMoney().toString())) > 0) {
                    couponUser.setIsChoose(true);
                }
                continue;
            }
            if (couponUser.getCategory().equals(CouponConstants.COUPON_CATEGORY_PRODUCT_CATEGORY)) {
                List<Integer> cidList = new ArrayList<>();
                Integer categoryId = Integer.valueOf(coupon.getLinkedData());
                ProductCategory category = productCategoryService.getById(categoryId);
                if (category.getLevel().equals(3)) {
                    cidList.add(categoryId);
                } else {
                    List<ProductCategory> productCategoryList = productCategoryService.findAllChildListByPid(category.getId(), category.getLevel());
                    if (category.getLevel().equals(1)) {
                        productCategoryList = productCategoryList.stream().filter(f -> f.getLevel().equals(3)).collect(Collectors.toList());
                    }
                    cidList.addAll(productCategoryList.stream().map(ProductCategory::getId).collect(Collectors.toList()));
                }
                List<Integer> pIdList = productList.stream().filter(f -> cidList.contains(f.getCategoryId())).map(Product::getId).collect(Collectors.toList());
                BigDecimal proPrice = orderDetailInfoList.stream().filter(f -> pIdList.contains(f.getProductId()))
                        .map(e -> {
                            BigDecimal price;
                            if (e.getIsPaidMember() && user.getIsPaidMember()) {
                                price = e.getVipPrice().multiply(new BigDecimal(e.getPayNum()));
                            } else {
                                price = e.getPrice().multiply(new BigDecimal(e.getPayNum()));
                            }
                            return price;
                        }).reduce(BigDecimal.ZERO, BigDecimal::add);
                BigDecimal proSubPrice = orderDetailInfoList.stream().filter(f -> pIdList.contains(f.getProductId()))
                        .map(e -> {
                            BigDecimal price;
                            if (e.getIsPaidMember() && user.getIsPaidMember()) {
                                price = e.getVipPrice().multiply(new BigDecimal(e.getPayNum()));
                            } else {
                                price = e.getPrice().multiply(new BigDecimal(e.getPayNum()));
                            }
                            return price.subtract(e.getMerCouponPrice());
                        }).reduce(BigDecimal.ZERO, BigDecimal::add);
                if (proPrice.compareTo(new BigDecimal(couponUser.getMinPrice().toString())) >= 0
                        && proSubPrice.compareTo(new BigDecimal(couponUser.getMoney().toString())) > 0) {
                    couponUser.setIsChoose(true);
                }
                continue;
            }
            if (couponUser.getCategory().equals(CouponConstants.COUPON_CATEGORY_BRAND)) {
                Integer brandId = Integer.valueOf(coupon.getLinkedData());
                List<Integer> pIdList = productList.stream().filter(f -> brandId.equals(f.getBrandId())).map(Product::getId).collect(Collectors.toList());
                BigDecimal proPrice = orderDetailInfoList.stream().filter(f -> pIdList.contains(f.getProductId()))
                        .map(e -> {
                            BigDecimal price;
                            if (e.getIsPaidMember() && user.getIsPaidMember()) {
                                price = e.getVipPrice().multiply(new BigDecimal(e.getPayNum()));
                            } else {
                                price = e.getPrice().multiply(new BigDecimal(e.getPayNum()));
                            }
                            return price;
                        }).reduce(BigDecimal.ZERO, BigDecimal::add);
                BigDecimal proSubPrice = orderDetailInfoList.stream().filter(f -> pIdList.contains(f.getProductId()))
                        .map(e -> {
                            BigDecimal price;
                            if (e.getIsPaidMember() && user.getIsPaidMember()) {
                                price = e.getVipPrice().multiply(new BigDecimal(e.getPayNum()));
                            } else {
                                price = e.getPrice().multiply(new BigDecimal(e.getPayNum()));
                            }
                            return price.subtract(e.getMerCouponPrice());
                        }).reduce(BigDecimal.ZERO, BigDecimal::add);
                if (proPrice.compareTo(new BigDecimal(couponUser.getMinPrice().toString())) >= 0
                        && proSubPrice.compareTo(new BigDecimal(couponUser.getMoney().toString())) > 0) {
                    couponUser.setIsChoose(true);
                }
                continue;
            }
            if (couponUser.getCategory().equals(CouponConstants.COUPON_CATEGORY_JOINT_MERCHANT)) {
                List<Integer> mpIdList = CrmebUtil.stringToArray(coupon.getLinkedData());
                List<Integer> pIdList = productList.stream().filter(f -> mpIdList.contains(f.getMerId())).map(Product::getId).collect(Collectors.toList());
                BigDecimal proPrice = orderDetailInfoList.stream().filter(f -> pIdList.contains(f.getProductId()))
                        .map(e -> {
                            BigDecimal price;
                            if (e.getIsPaidMember() && user.getIsPaidMember()) {
                                price = e.getVipPrice().multiply(new BigDecimal(e.getPayNum()));
                            } else {
                                price = e.getPrice().multiply(new BigDecimal(e.getPayNum()));
                            }
                            return price;
                        }).reduce(BigDecimal.ZERO, BigDecimal::add);
                BigDecimal proSubPrice = orderDetailInfoList.stream().filter(f -> pIdList.contains(f.getProductId()))
                        .map(e -> {
                            BigDecimal price;
                            if (e.getIsPaidMember() && user.getIsPaidMember()) {
                                price = e.getVipPrice().multiply(new BigDecimal(e.getPayNum()));
                            } else {
                                price = e.getPrice().multiply(new BigDecimal(e.getPayNum()));
                            }
                            return price.subtract(e.getMerCouponPrice());
                        }).reduce(BigDecimal.ZERO, BigDecimal::add);
                if (proPrice.compareTo(new BigDecimal(couponUser.getMinPrice().toString())) >= 0
                        && proSubPrice.compareTo(new BigDecimal(couponUser.getMoney().toString())) > 0) {
                    couponUser.setIsChoose(true);
                }
            }
        }

        preOrderResponse.setPlatCouponUserList(platCouponUserList);
        return preOrderResponse;
    }

    /**
     * 计算订单价格
     *
     * @param request 计算订单价格请求对象
     * @return ComputedOrderPriceResponse
     */
    @Override
    public ComputedOrderPriceResponse computedOrderPrice(OrderComputedPriceRequest request) {
        // 通过缓存获取预下单对象
        String key = OrderConstants.PRE_ORDER_CACHE_PREFIX + request.getPreOrderNo();
        boolean exists = redisUtil.exists(key);
        if (!exists) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "预下单订单不存在");
        }
        List<OrderMerchantRequest> orderMerchantRequestList = request.getOrderMerchantRequestList();
        if (orderMerchantRequestList.stream().anyMatch(e -> e.getShippingType().equals(OrderConstants.ORDER_SHIPPING_TYPE_PICK_UP))) {
            orderMerchantRequestList.forEach(m -> {
                if (m.getShippingType().equals(OrderConstants.ORDER_SHIPPING_TYPE_PICK_UP)) {
                    Merchant merchant = merchantService.getByIdException(m.getMerId());
                    if (!merchant.getIsTakeTheir()) {
                        throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "请先联系商户管理员开启门店自提");
                    }
                }
            });
        }

        String orderVoString = redisUtil.get(key).toString();
        PreOrderInfoVo orderInfoVo = JSONObject.parseObject(orderVoString, PreOrderInfoVo.class);
        User user = userService.getInfo();

        return computedPrice(request, orderInfoVo, user);
    }

    /**
     * 创建订单
     *
     * @param orderRequest 创建订单请求参数
     * @return OrderNoResponse 订单编号
     */
    @Override
    public OrderNoResponse createOrder(CreateOrderRequest orderRequest) {
        User user = userService.getInfo();
        // 通过缓存获取预下单对象
        String key = OrderConstants.PRE_ORDER_CACHE_PREFIX + orderRequest.getPreOrderNo();
        boolean exists = redisUtil.exists(key);
        if (!exists) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "预下单订单不存在");
        }
        String orderVoString = redisUtil.get(key).toString();
        PreOrderInfoVo orderInfoVo = JSONObject.parseObject(orderVoString, PreOrderInfoVo.class);
        if (orderInfoVo.getType().equals(OrderConstants.ORDER_TYPE_SECKILL)) {
            return seckillService.createOrder(orderRequest, orderInfoVo, user);
        }
        // 拼团商品单独处理
        if (orderInfoVo.getType().equals(OrderConstants.ORDER_TYPE_PITUAN)) {
            return groupBuyRecordService.createOrder(orderRequest, orderInfoVo, user, key);
        }

        UserAddress userAddress = null;
        List<OrderMerchantRequest> orderMerchantRequestList = orderRequest.getOrderMerchantRequestList();
        if (orderInfoVo.getSecondType().equals(OrderConstants.ORDER_SECOND_TYPE_CLOUD)
                || orderInfoVo.getSecondType().equals(OrderConstants.ORDER_SECOND_TYPE_CDKEY)
                || orderInfoVo.getSecondType().equals(OrderConstants.ORDER_SECOND_TYPE_VIRTUALLY)) {
            orderRequest.setAddressId(0);
        } else {
            if (orderMerchantRequestList.stream().anyMatch(e -> e.getShippingType().equals(OrderConstants.ORDER_SHIPPING_TYPE_EXPRESS))) {
                if (ObjectUtil.isNull(orderRequest.getAddressId())) {
                    throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "请选择收货地址");
                }
                userAddress = userAddressService.getById(orderRequest.getAddressId());
                if (ObjectUtil.isNull(userAddress) || userAddress.getIsDel()) {
                    throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "收货地址有误");
                }
            }
            if (orderMerchantRequestList.stream().anyMatch(e -> e.getShippingType().equals(OrderConstants.ORDER_SHIPPING_TYPE_PICK_UP))) {
                orderMerchantRequestList.forEach(m -> {
                    if (m.getShippingType().equals(OrderConstants.ORDER_SHIPPING_TYPE_PICK_UP)) {
                        Merchant merchant = merchantService.getByIdException(m.getMerId());
                        if (!merchant.getIsTakeTheir()) {
                            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, StrUtil.format("{}没有配置店铺地址，请联系客服", merchant.getName()));
                        }
                    }
                });
            }
        }

        // 校验商品库存
        List<MyRecord> skuRecordList = validateProductStock(orderInfoVo);

        orderInfoVo.getMerchantOrderVoList().forEach(e -> {
            orderMerchantRequestList.forEach(o -> {
                if (o.getMerId().equals(e.getMerId())) {
                    e.setShippingType(o.getShippingType());
                    e.setUserCouponId(o.getUserCouponId());
                    e.setCouponFee(BigDecimal.ZERO);
                    e.setMerCouponFee(BigDecimal.ZERO);
                }
            });
        });
        // 计算订单各种价格
        getFreightFee_V_1_8(orderInfoVo, userAddress, user.getIsPaidMember());
        orderInfoVo.setPlatUserCouponId(orderRequest.getPlatUserCouponId());
        orderInfoVo.setPlatCouponFee(BigDecimal.ZERO);
        orderInfoVo.setCouponFee(BigDecimal.ZERO);
        orderInfoVo.setMerCouponFee(BigDecimal.ZERO);
        getCouponFee_V1_3(orderInfoVo, user.getId(), user.getIsPaidMember());
        if (orderRequest.getIsUseIntegral() && user.getIntegral() > 0) {// 使用积分
            integralDeductionComputed(orderInfoVo, user.getIntegral(), user.getIsPaidMember());
        }
        List<PreMerchantOrderVo> merchantOrderVoList = orderInfoVo.getMerchantOrderVoList();

        // 平台订单
        Order order = new Order();
        String orderNo = CrmebUtil.getOrderNo(OrderConstants.ORDER_PREFIX_PLATFORM);
        order.setOrderNo(orderNo);
        order.setMerId(0);
        order.setUid(user.getId());
        order.setTotalNum(orderInfoVo.getOrderProNum());
        order.setProTotalPrice(orderInfoVo.getProTotalFee());
        order.setTotalPostage(orderInfoVo.getFreightFee());
        order.setSvipDiscountPrice(orderInfoVo.getSvipDiscountPrice());
        order.setTotalPrice(order.getProTotalPrice().add(order.getTotalPostage()));
        order.setCouponPrice(orderInfoVo.getCouponFee());
        order.setUseIntegral(merchantOrderVoList.stream().mapToInt(PreMerchantOrderVo::getUseIntegral).sum());
        order.setIntegralPrice(merchantOrderVoList.stream().map(PreMerchantOrderVo::getIntegralPrice).reduce(BigDecimal.ZERO, BigDecimal::add));
        order.setPayPrice(order.getTotalPrice().subtract(order.getSvipDiscountPrice()).subtract(order.getCouponPrice()).subtract(order.getIntegralPrice()));
        order.setPayPostage(order.getTotalPostage());
        order.setPaid(false);
        order.setCancelStatus(OrderConstants.ORDER_CANCEL_STATUS_NORMAL);
        order.setLevel(OrderConstants.ORDER_LEVEL_PLATFORM);
        order.setType(orderInfoVo.getType());// 默认普通订单
        order.setSecondType(orderInfoVo.getSecondType());
        order.setMerCouponPrice(orderInfoVo.getMerCouponFee());
        order.setPlatCouponPrice(orderInfoVo.getPlatCouponFee());
        order.setPlatCouponId(orderInfoVo.getPlatUserCouponId());
        order.setIsSvip(user.getIsPaidMember());
        order.setSystemFormId(orderInfoVo.getSystemFormId());
        order.setOrderExtend(StrUtil.isNotBlank(orderRequest.getOrderExtend()) ? systemAttachmentService.clearPrefix(orderRequest.getOrderExtend(), systemAttachmentService.getCdnUrl()) : "");
        order.setRedeemIntegral(orderInfoVo.getRedeemIntegral());

        // 商户订单
        List<Integer> couponIdList = CollUtil.newArrayList();
        if (orderRequest.getPlatUserCouponId() > 0) {
            couponIdList.add(orderRequest.getPlatUserCouponId());
        }
        List<MerchantOrder> merchantOrderList = new ArrayList<>();
        List<OrderDetail> orderDetailList = new ArrayList<>();
        for (PreMerchantOrderVo merchantOrderVo : merchantOrderVoList) {
            MerchantOrder merchantOrder = new MerchantOrder();
            merchantOrder.setOrderNo(order.getOrderNo());
            merchantOrder.setMerId(merchantOrderVo.getMerId());
            merchantOrder.setUid(user.getId());
            for (OrderMerchantRequest om : orderMerchantRequestList) {
                if (om.getMerId().equals(merchantOrderVo.getMerId())) {
                    if (StrUtil.isNotBlank(om.getRemark())) {
                        merchantOrder.setUserRemark(om.getRemark());
                    }
                    merchantOrder.setShippingType(om.getShippingType());
                    break;
                }
            }
            if (order.getSecondType().equals(OrderConstants.ORDER_SECOND_TYPE_CLOUD)
                    || order.getSecondType().equals(OrderConstants.ORDER_SECOND_TYPE_CDKEY)
                    || order.getSecondType().equals(OrderConstants.ORDER_SECOND_TYPE_VIRTUALLY)) {
                merchantOrder.setRealName(user.getNickname());
                merchantOrder.setUserPhone(user.getPhone());
                merchantOrder.setUserAddress("");
            } else if (merchantOrder.getShippingType().equals(OrderConstants.ORDER_SHIPPING_TYPE_PICK_UP)) {
                merchantOrder.setUserAddress(merchantOrderVo.getMerName());
                merchantOrder.setVerifyCode(String.valueOf(CrmebUtil.randomCount(1111111111, 999999999)));
            } else {
                merchantOrder.setRealName(userAddress.getRealName());
                merchantOrder.setUserPhone(userAddress.getPhone());
                String userAddressStr = userAddress.getProvince() + userAddress.getCity() + userAddress.getDistrict() + userAddress.getStreet() + userAddress.getDetail();
                merchantOrder.setUserAddress(userAddressStr);
            }
            merchantOrder.setTotalNum(merchantOrderVo.getProTotalNum());
            merchantOrder.setProTotalPrice(merchantOrderVo.getProTotalFee());
            merchantOrder.setTotalPostage(merchantOrderVo.getFreightFee());
            merchantOrder.setTotalPrice(merchantOrder.getProTotalPrice().add(merchantOrder.getTotalPostage()));
            merchantOrder.setPayPostage(merchantOrder.getTotalPostage());
            merchantOrder.setUseIntegral(merchantOrderVo.getUseIntegral());
            merchantOrder.setIntegralPrice(merchantOrderVo.getIntegralPrice());
            merchantOrder.setCouponId(merchantOrderVo.getUserCouponId());
            if (merchantOrder.getCouponId() > 0) {
                couponIdList.add(merchantOrder.getCouponId());
            }
            merchantOrder.setSvipDiscountPrice(merchantOrderVo.getSvipDiscountPrice());
            List<PreOrderInfoDetailVo> detailVoList = merchantOrderVo.getOrderInfoList();
            BigDecimal merCouponPrice = detailVoList.stream().map(PreOrderInfoDetailVo::getMerCouponPrice).reduce(BigDecimal.ZERO, BigDecimal::add);
            merchantOrder.setMerCouponPrice(merCouponPrice);
            BigDecimal platCouponPrice = detailVoList.stream().map(PreOrderInfoDetailVo::getPlatCouponPrice).reduce(BigDecimal.ZERO, BigDecimal::add);
            merchantOrder.setPlatCouponPrice(platCouponPrice);
            merchantOrder.setCouponPrice(merchantOrder.getMerCouponPrice().add(merchantOrder.getPlatCouponPrice()));
            merchantOrder.setPayPrice(merchantOrder.getTotalPrice().subtract(merchantOrder.getSvipDiscountPrice()).subtract(merchantOrder.getCouponPrice()).subtract(merchantOrder.getIntegralPrice()));
            merchantOrder.setGainIntegral(0);
            merchantOrder.setType(order.getType());
            merchantOrder.setSecondType(order.getSecondType());
            merchantOrder.setIsSvip(order.getIsSvip());

            merchantOrderList.add(merchantOrder);

            for (PreOrderInfoDetailVo detailVo : detailVoList) {
                OrderDetail orderDetail = new OrderDetail();
                orderDetail.setOrderNo(order.getOrderNo());
                orderDetail.setMerId(merchantOrder.getMerId());
                orderDetail.setUid(user.getId());
                orderDetail.setProductId(detailVo.getProductId());
                orderDetail.setProductName(detailVo.getProductName());
                orderDetail.setImage(detailVo.getImage());
                orderDetail.setAttrValueId(detailVo.getAttrValueId());
                orderDetail.setSku(detailVo.getSku());
                orderDetail.setPrice(detailVo.getPrice());
                orderDetail.setPayNum(detailVo.getPayNum());
                orderDetail.setWeight(detailVo.getWeight());
                orderDetail.setVolume(detailVo.getVolume());
                orderDetail.setProductType(detailVo.getProductType());
                orderDetail.setProductMarketingType(detailVo.getProductMarketingType());
                orderDetail.setSubBrokerageType(detailVo.getSubBrokerageType());
                orderDetail.setBrokerage(detailVo.getBrokerage());
                orderDetail.setBrokerageTwo(detailVo.getBrokerageTwo());
                orderDetail.setFreightFee(detailVo.getFreightFee());

                orderDetail.setUseIntegral(detailVo.getUseIntegral());
                orderDetail.setIntegralPrice(detailVo.getIntegralPrice());
                orderDetail.setPayPrice(BigDecimal.ZERO);

                orderDetail.setIsSvip(order.getIsSvip());
                orderDetail.setIsPaidMemberProduct(detailVo.getIsPaidMember());
                orderDetail.setVipPrice(detailVo.getVipPrice());

                orderDetail.setMerCouponPrice(detailVo.getMerCouponPrice());
                orderDetail.setPlatCouponPrice(detailVo.getPlatCouponPrice());
                orderDetail.setCouponPrice(detailVo.getMerCouponPrice().add(detailVo.getPlatCouponPrice()));
                BigDecimal detailPayPrice;
                if (orderDetail.getIsSvip() && orderDetail.getIsPaidMemberProduct()) {
                    detailPayPrice = orderDetail.getVipPrice().multiply(new BigDecimal(orderDetail.getPayNum().toString())).add(orderDetail.getFreightFee()).subtract(orderDetail.getCouponPrice()).subtract(orderDetail.getIntegralPrice());
                } else {
                    detailPayPrice = orderDetail.getPrice().multiply(new BigDecimal(orderDetail.getPayNum().toString())).add(orderDetail.getFreightFee()).subtract(orderDetail.getCouponPrice()).subtract(orderDetail.getIntegralPrice());
                }

                if (detailPayPrice.compareTo(BigDecimal.ZERO) < 0) {
                    throw new CrmebException("订单详情价格计算错误，详情价格不能小于0");
                }
                orderDetail.setPayPrice(detailPayPrice);
                orderDetail.setProRefundSwitch(detailVo.getProRefundSwitch());
                if (order.getSecondType().equals(OrderConstants.ORDER_SECOND_TYPE_INTEGRAL)) {
                    orderDetail.setRedeemIntegral(detailVo.getRedeemIntegral());
                }
                orderDetailList.add(orderDetail);
            }
        }

        order.setCreateTime(DateUtil.date());
        Boolean execute = transactionTemplate.execute(e -> {
            Boolean result = false;
            if (order.getSecondType().equals(OrderConstants.ORDER_SECOND_TYPE_CLOUD)) { // 云盘订单
                MyRecord skuRecord = skuRecordList.get(0);
                // 扣减库存
                // 云盘商品口库存
                result = productService.operationStock(skuRecord.getInt("productId"), skuRecord.getInt("num"), Constants.OPERATION_TYPE_SUBTRACT);
                if (!result) {
                    e.setRollbackOnly();
                    logger.error("生成订单扣减商品库存失败,预下单号：{},商品ID：{}", orderRequest.getPreOrderNo(), skuRecord.getInt("productId"));
                    return result;
                }
                // 云盘商品规格扣库存
                result = productAttrValueService.operationStock(skuRecord.getInt("attrValueId"), skuRecord.getInt("num"), Constants.OPERATION_TYPE_SUBTRACT, skuRecord.getInt("type"), skuRecord.getInt("attrValueVersion"));
                if (!result) {
                    e.setRollbackOnly();
                    logger.error("生成订单扣减商品sku库存失败,预下单号：{},商品skuID：{}", orderRequest.getPreOrderNo(), skuRecord.getInt("attrValueId"));
                    return result;
                }
                orderDetailList.forEach(detail -> {
                    detail.setExpand(skuRecord.getStr("expand"));
                });
            }
            if (order.getSecondType().equals(OrderConstants.ORDER_SECOND_TYPE_CDKEY)) { // 卡密订单
                MyRecord skuRecord = skuRecordList.get(0);
                // 扣减库存
                // 卡密商品口库存
                result = productService.operationStock(skuRecord.getInt("productId"), skuRecord.getInt("num"), Constants.OPERATION_TYPE_SUBTRACT);
                if (!result) {
                    e.setRollbackOnly();
                    logger.error("生成订单扣减商品库存失败,预下单号：{},商品ID：{}", orderRequest.getPreOrderNo(), skuRecord.getInt("productId"));
                    return result;
                }
                // 卡密商品规格扣库存
                result = productAttrValueService.operationStock(skuRecord.getInt("attrValueId"), skuRecord.getInt("num"), Constants.OPERATION_TYPE_SUBTRACT, skuRecord.getInt("type"), skuRecord.getInt("attrValueVersion"));
                if (!result) {
                    e.setRollbackOnly();
                    logger.error("生成订单扣减商品sku库存失败,预下单号：{},商品skuID：{}", orderRequest.getPreOrderNo(), skuRecord.getInt("attrValueId"));
                    return result;
                }
                Integer cdkeyId = skuRecord.getInt("cdkeyId");
                List<Integer> csIdList = new ArrayList<>();
                for (int i = 0; i < skuRecord.getInt("num"); i++) {
                    CardSecret cardSecret = cardSecretService.consume(cdkeyId);
                    if (ObjectUtil.isNull(cardSecret)) {
                        e.setRollbackOnly();
                        logger.error("生成订单消费卡密失败,预下单号：{},商品skuID：{}", orderRequest.getPreOrderNo(), skuRecord.getInt("attrValueId"));
                        return Boolean.FALSE;
                    }
                    csIdList.add(cardSecret.getId());
                }
                String cardSecretIds = StrUtil.join(",", csIdList);
                orderDetailList.forEach(detail -> {
                    detail.setCardSecretIds(cardSecretIds);
                });
                cdkeyLibraryService.operationUseNum(cdkeyId, csIdList.size(), Constants.OPERATION_TYPE_ADD);
            }
            if (order.getSecondType().equals(OrderConstants.ORDER_SECOND_TYPE_VIDEO)) {// 视频号订单
                MyRecord skuRecord = skuRecordList.get(0);
                // 商品规格表扣库存
                productAttrValueService.operationStock(skuRecord.getInt("attrValueId"), skuRecord.getInt("num"), Constants.OPERATION_TYPE_SUBTRACT, ProductConstants.PRODUCT_TYPE_COMPONENT, skuRecord.getInt("attrValueVersion"));
                // 视频商品规格表扣库存
                payComponentProductSkuService.operationStock(skuRecord.getInt("skuId"), skuRecord.getInt("num"), Constants.OPERATION_TYPE_SUBTRACT, skuRecord.getInt("skuVersion"));
                // 视频号商品扣库存
                payComponentProductService.operationStock(skuRecord.getInt("productId"), skuRecord.getInt("num"), Constants.OPERATION_TYPE_SUBTRACT);
            }
            if (order.getSecondType().equals(OrderConstants.ORDER_SECOND_TYPE_NORMAL)
                    || order.getSecondType().equals(OrderConstants.ORDER_SECOND_TYPE_INTEGRAL)
                    || order.getSecondType().equals(OrderConstants.ORDER_SECOND_TYPE_VIRTUALLY)) { // 普通商品
                // 扣减库存
                for (MyRecord skuRecord : skuRecordList) {
                    // 普通商品口库存
                    result = productService.operationStock(skuRecord.getInt("productId"), skuRecord.getInt("num"), Constants.OPERATION_TYPE_SUBTRACT);
                    if (!result) {
                        e.setRollbackOnly();
                        logger.error("生成订单扣减商品库存失败,预下单号：{},商品ID：{}", orderRequest.getPreOrderNo(), skuRecord.getInt("productId"));
                        return result;
                    }
                    // 普通商品规格扣库存
                    result = productAttrValueService.operationStock(skuRecord.getInt("attrValueId"), skuRecord.getInt("num"), Constants.OPERATION_TYPE_SUBTRACT, skuRecord.getInt("type"), skuRecord.getInt("attrValueVersion"));
                    if (!result) {
                        e.setRollbackOnly();
                        logger.error("生成订单扣减商品sku库存失败,预下单号：{},商品skuID：{}", orderRequest.getPreOrderNo(), skuRecord.getInt("attrValueId"));
                        return result;
                    }
                }
            }

            orderService.save(order);
            merchantOrderService.saveBatch(merchantOrderList);
            orderDetailService.saveBatch(orderDetailList);
            // 扣除用户积分
            if (order.getUseIntegral() > 0) {
                result = userService.updateIntegral(user.getId(), order.getUseIntegral(), Constants.OPERATION_TYPE_SUBTRACT);
                if (!result) {
                    e.setRollbackOnly();
                    logger.error("生成订单扣除用户积分失败,预下单号：{}", orderRequest.getPreOrderNo());
                    return result;
                }
                UserIntegralRecord userIntegralRecord = initOrderUseIntegral(user.getId(), order.getUseIntegral(), user.getIntegral(), order.getOrderNo());
                userIntegralRecordService.save(userIntegralRecord);
            }

            if (CollUtil.isNotEmpty(couponIdList)) {
                couponUserService.useBatch(couponIdList);
            }
            // 生成订单日志
            orderStatusService.createLog(order.getOrderNo(), OrderStatusConstants.ORDER_STATUS_CREATE, OrderStatusConstants.ORDER_LOG_MESSAGE_CREATE);
            // 清除购物车数据
            if (CollUtil.isNotEmpty(orderInfoVo.getCartIdList())) {
                cartService.deleteCartByIds(orderInfoVo.getCartIdList());
            }
            // 积分订单预扣除积分
            if (order.getRedeemIntegral() > 0) {
                result = userService.updateIntegral(user.getId(), order.getRedeemIntegral(), Constants.OPERATION_TYPE_SUBTRACT);
                if (!result) {
                    e.setRollbackOnly();
                    logger.error("生成积分订单预扣除用户积分失败,预下单号：{}", orderRequest.getPreOrderNo());
                    return result;
                }
                UserIntegralRecord userIntegralRecord = initIntegralOrderUseIntegral(user.getId(), order.getRedeemIntegral(), user.getIntegral(), order.getOrderNo());
                userIntegralRecordService.save(userIntegralRecord);
            }

            return Boolean.TRUE;
        });
        if (!execute) {
            throw new CrmebException("订单生成失败");
        }

        // 删除缓存订单
        if (redisUtil.exists(key)) {
            redisUtil.delete(key);
        }

        // 加入自动未支付自动取消队列
        redisUtil.lPush(TaskConstants.ORDER_TASK_REDIS_KEY_AUTO_CANCEL_KEY, order.getOrderNo());
        // 设置自动过期时间 2025-02-11
        Integer orderCancelTimeMinute = crmebConfig.getOrderCancelTime();
        DateTime expirationTime = DateUtil.offset(order.getCreateTime(), DateField.MINUTE, crmebConfig.getOrderCancelTime());
        Long expireTime =  (long) orderCancelTimeMinute * 60;
        redisUtil.set(StrUtil.format(RedisConstants.ORDER_EXPIRE_TIME, order.getOrderNo()), expirationTime.toString(), expireTime);

        OrderNoResponse response = new OrderNoResponse();
        response.setOrderNo(order.getOrderNo());
        response.setPayPrice(order.getPayPrice());
        return response;
    }

    /**
     * 用户积分记录——订单抵扣
     *
     * @param uid         用户ID
     * @param useIntegral 使用的积分
     * @param integral    用户当前积分
     * @param orderNo     订单号
     * @return 用户积分记录
     */
    private UserIntegralRecord initOrderUseIntegral(Integer uid, Integer useIntegral, Integer integral, String orderNo) {
        UserIntegralRecord integralRecord = new UserIntegralRecord();
        integralRecord.setUid(uid);
        integralRecord.setLinkId(orderNo);
        integralRecord.setLinkType(IntegralRecordConstants.INTEGRAL_RECORD_LINK_TYPE_ORDER);
        integralRecord.setType(IntegralRecordConstants.INTEGRAL_RECORD_TYPE_SUB);
        integralRecord.setTitle(StrUtil.format("订单使用{}积分进行金额抵扣", useIntegral));
        integralRecord.setIntegral(useIntegral);
        integralRecord.setBalance(integral - useIntegral);
        integralRecord.setStatus(IntegralRecordConstants.INTEGRAL_RECORD_STATUS_COMPLETE);
        return integralRecord;
    }

    /**
     * 用户积分记录——积分订单抵扣
     *
     * @param uid         用户ID
     * @param useIntegral 使用的积分
     * @param integral    用户当前积分
     * @param orderNo     订单号
     * @return 用户积分记录
     */
    private UserIntegralRecord initIntegralOrderUseIntegral(Integer uid, Integer useIntegral, Integer integral, String orderNo) {
        UserIntegralRecord integralRecord = new UserIntegralRecord();
        integralRecord.setUid(uid);
        integralRecord.setLinkId(orderNo);
        integralRecord.setLinkType(IntegralRecordConstants.INTEGRAL_RECORD_LINK_TYPE_ORDER);
        integralRecord.setType(IntegralRecordConstants.INTEGRAL_RECORD_TYPE_SUB);
        integralRecord.setTitle(StrUtil.format("积分订单使用{}积分进行兑换", useIntegral));
        integralRecord.setIntegral(useIntegral);
        integralRecord.setBalance(integral - useIntegral);
        integralRecord.setStatus(IntegralRecordConstants.INTEGRAL_RECORD_STATUS_COMPLETE);
        return integralRecord;
    }

    /**
     * 积分抵扣计算
     *
     * @param orderInfoVo  订单vo
     * @param userIntegral 用户积分
     */
    private void integralDeductionComputed(PreOrderInfoVo orderInfoVo, Integer userIntegral, Boolean userIsPaidMember) {
        BigDecimal payPrice = orderInfoVo.getProTotalFee().subtract(orderInfoVo.getSvipDiscountPrice()).subtract(orderInfoVo.getCouponFee());
        String integralDeductionSwitch = systemConfigService.getValueByKeyException(SysConfigConstants.CONFIG_KEY_INTEGRAL_DEDUCTION_SWITCH);
        if (integralDeductionSwitch.equals("false")) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "积分抵扣未开启，请重新下单");
        }
        String integralDeductionStartMoney = systemConfigService.getValueByKeyException(SysConfigConstants.CONFIG_KEY_INTEGRAL_DEDUCTION_START_MONEY);
        if (Integer.parseInt(integralDeductionStartMoney) <= 0 || payPrice.compareTo(new BigDecimal(integralDeductionStartMoney)) < 0) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "支付金额不满足积分抵扣起始金额，请重新下单");
        }
        // 查询积分使用比例
        String integralRatio = systemConfigService.getValueByKeyException(SysConfigConstants.CONFIG_KEY_INTEGRAL_DEDUCTION_RATIO);
        // 可抵扣金额
        BigDecimal canDeductionPrice = payPrice.multiply(new BigDecimal(integralRatio)).divide(new BigDecimal("100"), 2, RoundingMode.DOWN);
        // 积分转换金额
        String integralDeductionMoney = systemConfigService.getValueByKeyException(SysConfigConstants.CONFIG_KEY_INTEGRAL_DEDUCTION_MONEY);
        BigDecimal deductionPrice = new BigDecimal(userIntegral.toString()).multiply(new BigDecimal(integralDeductionMoney));
        // 积分兑换金额小于实际支付可抵扣金额
        int useIntegral;
        BigDecimal integralDeductionPrice;
        if (deductionPrice.compareTo(canDeductionPrice) <= 0) {
            useIntegral = userIntegral;
            integralDeductionPrice = deductionPrice;
        } else {
            useIntegral = canDeductionPrice.divide(new BigDecimal(integralDeductionMoney), 0, RoundingMode.UP).intValue();
            integralDeductionPrice = canDeductionPrice;
        }
        List<PreMerchantOrderVo> merchantOrderVoList = orderInfoVo.getMerchantOrderVoList();
        Integer tempUseIntegral = useIntegral;
        BigDecimal tempIntegralDeductionPrice = integralDeductionPrice;
        List<PreMerchantOrderVo> voList = merchantOrderVoList.stream()
                .sorted(Comparator.comparing(o -> (o.getProTotalFee().subtract(o.getSvipDiscountPrice()).subtract(o.getCouponFee()))))
                .collect(Collectors.toList());
        BigDecimal paySubPrice = orderInfoVo.getProTotalFee().subtract(orderInfoVo.getMerCouponFee());
        for (int i = 0; i < voList.size(); i++) {
            PreMerchantOrderVo merchantOrderVo = voList.get(i);
            BigDecimal merPayPrice = merchantOrderVo.getProTotalFee().subtract(merchantOrderVo.getSvipDiscountPrice()).subtract(merchantOrderVo.getMerCouponFee());
            if (merchantOrderVoList.size() == (i + 1)) {
                merchantOrderVo.setUseIntegral(tempUseIntegral);
                merchantOrderVo.setIntegralPrice(tempIntegralDeductionPrice);
                int merUseIntegral = tempUseIntegral;
                BigDecimal merIntegralDeductionPrice = tempIntegralDeductionPrice;
                List<PreOrderInfoDetailVo> orderInfoList = merchantOrderVo.getOrderInfoList();
                orderInfoList = orderInfoList.stream()
                        .sorted(Comparator.comparing(o -> {
                            BigDecimal oPrice;
                            if (o.getIsPaidMember() && userIsPaidMember) {
                                oPrice = o.getVipPrice().multiply(new BigDecimal(o.getPayNum())).subtract(o.getMerCouponPrice());
                            } else {
                                oPrice = o.getPrice().multiply(new BigDecimal(o.getPayNum())).subtract(o.getMerCouponPrice());
                            }
                            return oPrice;
                        })).collect(Collectors.toList());
                if (orderInfoList.size() == 1) {
                    orderInfoList.get(0).setUseIntegral(merUseIntegral);
                    orderInfoList.get(0).setIntegralPrice(merIntegralDeductionPrice);
                } else {
                    for (int j = 0; j < orderInfoList.size(); j++) {
                        PreOrderInfoDetailVo detailVo = orderInfoList.get(j);
                        if (orderInfoList.size() == (j + 1)) {
                            detailVo.setUseIntegral(merUseIntegral);
                            detailVo.setIntegralPrice(merIntegralDeductionPrice);
                            break;
                        }
                        BigDecimal detailPayPrice;
                        if (detailVo.getIsPaidMember() && userIsPaidMember) {
                            detailPayPrice = detailVo.getVipPrice().multiply(new BigDecimal(detailVo.getPayNum())).subtract(detailVo.getMerCouponPrice());
                        } else {
                            detailPayPrice = detailVo.getPrice().multiply(new BigDecimal(detailVo.getPayNum())).subtract(detailVo.getMerCouponPrice());
                        }
                        BigDecimal ratio = detailPayPrice.divide(merPayPrice, 10, RoundingMode.HALF_UP);
                        int detailUseIntegral = new BigDecimal(Integer.toString(merUseIntegral)).multiply(ratio).setScale(0, RoundingMode.DOWN).intValue();
                        BigDecimal detailIntegralPrice = new BigDecimal(detailUseIntegral).multiply(new BigDecimal(integralDeductionMoney));
                        merUseIntegral = merUseIntegral - detailUseIntegral;
                        merIntegralDeductionPrice = merIntegralDeductionPrice.subtract(detailIntegralPrice);
                        detailVo.setUseIntegral(detailUseIntegral);
                        detailVo.setIntegralPrice(detailIntegralPrice);
                    }
                }
                break;
            }
            BigDecimal payRatio = merPayPrice.divide(paySubPrice, 10, RoundingMode.HALF_UP);
            Integer merUseIntegral = new BigDecimal(tempUseIntegral.toString()).multiply(payRatio).setScale(0, RoundingMode.DOWN).intValue();
            if (merUseIntegral.equals(0)) {
                continue;
            }
            BigDecimal merIntegralDeductionPrice = new BigDecimal(merUseIntegral).multiply(new BigDecimal(integralDeductionMoney));
            merchantOrderVo.setUseIntegral(merUseIntegral > tempUseIntegral ? tempUseIntegral : merUseIntegral);
            merchantOrderVo.setIntegralPrice(merIntegralDeductionPrice.compareTo(tempIntegralDeductionPrice) > 0 ? tempIntegralDeductionPrice : merIntegralDeductionPrice);
            if (merIntegralDeductionPrice.compareTo(merPayPrice) > 0) {
                merchantOrderVo.setIntegralPrice(merPayPrice);
            }
            List<PreOrderInfoDetailVo> orderInfoList = merchantOrderVo.getOrderInfoList();
            orderInfoList = orderInfoList.stream()
                    .sorted(Comparator.comparing(o -> {
                        BigDecimal oPrice;
                        if (o.getIsPaidMember() && userIsPaidMember) {
                            oPrice = o.getVipPrice().multiply(new BigDecimal(o.getPayNum())).subtract(o.getMerCouponPrice());
                        } else {
                            oPrice = o.getPrice().multiply(new BigDecimal(o.getPayNum())).subtract(o.getMerCouponPrice());
                        }
                        return oPrice;
                    })).collect(Collectors.toList());
            if (orderInfoList.size() == 1) {
                orderInfoList.get(0).setUseIntegral(merUseIntegral);
                orderInfoList.get(0).setIntegralPrice(merIntegralDeductionPrice);
            } else {
                for (int j = 0; j < orderInfoList.size(); j++) {
                    PreOrderInfoDetailVo detailVo = orderInfoList.get(j);
                    if (orderInfoList.size() == (j + 1)) {
                        detailVo.setUseIntegral(merUseIntegral);
                        detailVo.setIntegralPrice(merIntegralDeductionPrice);
                        break;
                    }
                    BigDecimal detailPayPrice;
                    if (detailVo.getIsPaidMember() && userIsPaidMember) {
                        detailPayPrice = detailVo.getVipPrice().multiply(new BigDecimal(detailVo.getPayNum())).subtract(detailVo.getMerCouponPrice());
                    } else {
                        detailPayPrice = detailVo.getPrice().multiply(new BigDecimal(detailVo.getPayNum())).subtract(detailVo.getMerCouponPrice());
                    }
                    BigDecimal ratio = detailPayPrice.divide(merPayPrice, 10, RoundingMode.HALF_UP);
                    int detailUseIntegral = new BigDecimal(merUseIntegral.toString()).multiply(ratio).setScale(0, RoundingMode.DOWN).intValue();
                    BigDecimal detailIntegralPrice = new BigDecimal(detailUseIntegral).multiply(new BigDecimal(integralDeductionMoney));
                    merUseIntegral = merUseIntegral - detailUseIntegral;
                    merIntegralDeductionPrice = merIntegralDeductionPrice.subtract(detailIntegralPrice);
                    detailVo.setUseIntegral(detailUseIntegral);
                    detailVo.setIntegralPrice(detailIntegralPrice);
                }
            }
            tempUseIntegral = tempUseIntegral - merchantOrderVo.getUseIntegral();
            tempIntegralDeductionPrice = tempIntegralDeductionPrice.subtract(merchantOrderVo.getIntegralPrice());
        }
        orderInfoVo.setMerchantOrderVoList(voList);
    }

    /**
     * 退款单退回商品
     */
    @Override
    public Boolean returningGoods(OrderRefundReturningGoodsRequest request) {
        RefundOrder refundOrder = refundOrderService.getByRefundOrderNo(request.getRefundOrderNo());
        if (ObjectUtil.isNull(refundOrder)) {
            throw new CrmebException(OrderResultCode.REFUND_ORDER_NOT_EXIST);
        }
        if (refundOrder.getRefundStatus() != 4) {
            throw new CrmebException(OrderResultCode.REFUND_ORDER_STATUS_ABNORMAL);
        }
        if (refundOrder.getReturnGoodsType() == 1) {
            if (StrUtil.isBlank(request.getExpressName())) {
                throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "请选择快递公司");
            }
            if (StrUtil.isBlank(request.getTrackingNumber())) {
                throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "请填写运单号");
            }
            refundOrder.setExpressName(request.getExpressName());
            refundOrder.setTrackingNumber(request.getTrackingNumber());
        }
        refundOrder.setTelephone(request.getTelephone());
        refundOrder.setRefundStatus(5);

        return transactionTemplate.execute(e -> {
            refundOrderService.updateById(refundOrder);
            refundOrderStatusService.add(refundOrder.getRefundOrderNo(), RefundOrderConstants.REFUND_ORDER_LOG_RETURNING_GOODS, "售后单用户退回商品");
            return Boolean.TRUE;
        });
    }

    /**
     * 撤销退款单
     * @param refundOrderNo 退款单号
     */
    @Override
    public Boolean revoke(String refundOrderNo) {
        return refundOrderService.revoke(refundOrderNo);
    }

    /**
     * 订单列表(v1.4.0)
     * @param request 搜索参数
     * @return PageInfo
     */
    @Override
    public PageInfo<OrderFrontDataResponse> list_v1_4(OrderFrontListRequest request) {
        Integer userId = userService.getUserIdException();

        PageInfo<Order> pageInfo = orderService.getUserOrderList_v1_4(userId, request);
        List<Order> orderList = pageInfo.getList();
        if (CollUtil.isEmpty(orderList)) {
            return CommonPage.copyPageInfo(pageInfo, CollUtil.newArrayList());
        }
        List<Integer> merIdList = orderList.stream().map(Order::getMerId).filter(i -> i > 0).distinct().collect(Collectors.toList());
        Map<Integer, Merchant> merchantMap = null;
        if (CollUtil.isNotEmpty(merIdList)) {
            merchantMap = merchantService.getMerIdMapByIdList(merIdList);
        }
        List<OrderFrontDataResponse> responseList = CollUtil.newArrayList();
        DateTime cancelTime;
        for (Order order : orderList) {
            OrderFrontDataResponse infoResponse = new OrderFrontDataResponse();
            BeanUtils.copyProperties(order, infoResponse);
            // 订单详情对象列表
            List<OrderDetail> orderDetailList = orderDetailService.getByOrderNo(order.getOrderNo());
            List<OrderInfoFrontDataResponse> infoResponseList = CollUtil.newArrayList();
            orderDetailList.forEach(e -> {
                OrderInfoFrontDataResponse orderInfoResponse = new OrderInfoFrontDataResponse();
                BeanUtils.copyProperties(e, orderInfoResponse);
                infoResponseList.add(orderInfoResponse);
            });
            infoResponse.setOrderInfoList(infoResponseList);
            if (order.getMerId() > 0) {
                infoResponse.setMerName(merchantMap.get(order.getMerId()).getName());
            }
            if (!order.getPaid()) {
                cancelTime = DateUtil.offset(order.getCreateTime(), DateField.MINUTE, crmebConfig.getOrderCancelTime());
                infoResponse.setCancelTime(cancelTime.getTime());
                // 返回订单过期时间 2025-02-12
                if (redisUtil.exists(StrUtil.format(RedisConstants.ORDER_EXPIRE_TIME, order.getOrderNo()))) {
                    String expireTime = redisUtil.get(StrUtil.format(RedisConstants.ORDER_EXPIRE_TIME, order.getOrderNo()));
                    infoResponse.setExpirationTime(expireTime);
                }
            }
            // 在次添加拼团订单信息
            if(order.getType().equals(OrderConstants.ORDER_TYPE_PITUAN)){
                GroupBuyUser currentGroupBuyUser = groupBuyUserService.getByOrderNo(order.getOrderNo());
                if(ObjectUtil.isNotNull(currentGroupBuyUser) && currentGroupBuyUser.getId() > 0){
                    GroupBuyRecord currentGroupBuyRecord = groupBuyRecordService.getById(currentGroupBuyUser.getGroupRecordId());
                    GroupBuyActivitySku currentSku = null;
                    GroupBuyActivityRecordForFrontShareUse groupBuyActivityRecord = new GroupBuyActivityRecordForFrontShareUse();
                    if(ObjectUtil.isNotNull(currentGroupBuyRecord)){
                        currentSku = groupBuyActivitySkuService.getByAttrId(currentGroupBuyUser.getGroupActivityId(), currentGroupBuyRecord.getSkuid());
                        groupBuyActivityRecord.setBuyingCountNum(currentGroupBuyRecord.getBuyingCountNum());
                        groupBuyActivityRecord.setYetBuyingNum(currentGroupBuyRecord.getYetBuyingNum());
                    }
                    if(ObjectUtil.isNull(currentSku)){ //这里有可能在购买后，管理端删除基础商品的sku后，关联操作拼团sku后导致这里查询不到，拼团价为null的情况下 前端不予生成拼团码
                        groupBuyActivityRecord.setActivePrice(null);
                    }else{
                        groupBuyActivityRecord.setActivePrice(currentSku.getActivePrice());
                    }
                    groupBuyActivityRecord.setGroupActivityId(currentGroupBuyUser.getGroupActivityId());
                    groupBuyActivityRecord.setProductId(currentGroupBuyUser.getProductGroupId());
                    groupBuyActivityRecord.setGroupRecordId(currentGroupBuyUser.getGroupRecordId());
                    groupBuyActivityRecord.setGroupLeaderNickname(currentGroupBuyUser.getGroupNickname());
                    groupBuyActivityRecord.setGroupLeaderAvatar(currentGroupBuyUser.getGroupAvatar());
                    groupBuyActivityRecord.setGroupLeaderUid(currentGroupBuyUser.getGroupUid());
                    infoResponse.setGroupBuyActivityRecord(groupBuyActivityRecord);
                }
            }
            responseList.add(infoResponse);
        }

        return CommonPage.copyPageInfo(pageInfo, responseList);
    }

    /**
     * 移动端订单详情
     *
     * @param orderNo 订单编号
     * @return OrderFrontDetailResponse
     */
    @Override
    public OrderFrontDetailResponse frontDetail(String orderNo) {
        User currentUser = userService.getInfo();
        Order order = orderService.getByOrderNo(orderNo);
        if (order.getIsUserDel() || order.getIsMerchantDel() || !order.getUid().equals(currentUser.getId())) {
            throw new CrmebException(OrderResultCode.ORDER_NOT_EXIST);
        }
        OrderFrontDetailResponse response = new OrderFrontDetailResponse();
        BeanUtils.copyProperties(order, response);
        List<MerchantOrder> merchantOrderList = merchantOrderService.getByOrderNo(orderNo);
        List<OrderDetail> orderDetailList = orderDetailService.getByOrderNo(orderNo);
        for (OrderDetail orderDetail : orderDetailList) { // set 拼团活动号
            response.setGroupBuyActivityId(orderDetail.getGroupBuyActivityId());
        }
        Map<Integer, List<OrderDetail>> orderDetailMap = orderDetailList.stream().collect(Collectors.groupingBy(OrderDetail::getMerId));

        List<MerchantOrderFrontDetailResponse> merDetailResponseList = CollUtil.newArrayList();
        for (MerchantOrder merchantOrder : merchantOrderList) {
            MerchantOrderFrontDetailResponse merDetailResponse = new MerchantOrderFrontDetailResponse();
            BeanUtils.copyProperties(merchantOrder, merDetailResponse);
            if (!merchantOrder.getSecondType().equals(OrderConstants.ORDER_SECOND_TYPE_INTEGRAL)) {
                Merchant merchant = merchantService.getById(merchantOrder.getMerId());
                merDetailResponse.setMerName(merchant.getName());
                if (merchantOrder.getShippingType().equals(OrderConstants.ORDER_SHIPPING_TYPE_PICK_UP)) {
                    merDetailResponse.setMerPhone(merchant.getPhone());
                    merDetailResponse.setMerProvince(merchant.getProvince());
                    merDetailResponse.setMerCity(merchant.getCity());
                    merDetailResponse.setMerDistrict(merchant.getDistrict());
                    merDetailResponse.setMerAddressDetail(merchant.getAddressDetail());
                    merDetailResponse.setMerLatitude(merchant.getLatitude());
                    merDetailResponse.setMerLongitude(merchant.getLongitude());
                }
                merDetailResponse.setIsSelf(merchant.getIsSelf());
            }
            List<OrderDetail> detailList = orderDetailMap.get(merchantOrder.getMerId());
            List<OrderInfoFrontDataResponse> dataResponseList = detailList.stream().map(d -> {
                OrderInfoFrontDataResponse dataResponse = new OrderInfoFrontDataResponse();
                BeanUtils.copyProperties(d, dataResponse);
                if (order.getSecondType().equals(OrderConstants.ORDER_SECOND_TYPE_CLOUD) || order.getSecondType().equals(OrderConstants.ORDER_SECOND_TYPE_CDKEY)) {
                    if (order.getStatus() <= 3 || order.getStatus() == 9) {
                        dataResponse.setExpand("");
                        dataResponse.setCardSecretIds("");
                    } else if (order.getSecondType().equals(OrderConstants.ORDER_SECOND_TYPE_CDKEY)) {
                            String cardSecretIds = d.getCardSecretIds();
                            List<Integer> cardSecretIdList = CrmebUtil.stringToArray(cardSecretIds);
                            List<CardSecret> cardSecretList = cardSecretService.findByIds(cardSecretIdList);
                            dataResponse.setCardSecretList(cardSecretList);
                    }
                }
                return dataResponse;
            }).collect(Collectors.toList());
            merDetailResponse.setOrderInfoList(dataResponseList);
            merDetailResponseList.add(merDetailResponse);
        }
        response.setMerchantOrderList(merDetailResponseList);
        if (!order.getPaid()) {
            DateTime cancelTime = DateUtil.offset(order.getCreateTime(), DateField.MINUTE, crmebConfig.getOrderCancelTime());
            response.setCancelTime(cancelTime.getTime());
            // 返回订单过期时间 2025-02-12
            if (redisUtil.exists(StrUtil.format(RedisConstants.ORDER_EXPIRE_TIME, order.getOrderNo()))) {
                String expireTime = redisUtil.get(StrUtil.format(RedisConstants.ORDER_EXPIRE_TIME, order.getOrderNo()));
                response.setExpirationTime(expireTime);
            }
        }
        return response;
    }

    /**
     * 订单商品评论列表
     *
     * @param pageRequest 分页参数
     * @return PageInfo
     */
    @Override
    public PageInfo<InfoReplyResponse> replyList(PageParamRequest pageRequest) {
        Integer userId = userService.getUserIdException();
        PageInfo<OrderDetail> pageInfo = orderDetailService.getReplyList(userId, false, pageRequest);
        List<OrderDetail> orderDetailList = pageInfo.getList();
        if (CollUtil.isEmpty(orderDetailList)) {
            return CommonPage.copyPageInfo(pageInfo, CollUtil.newArrayList());
        }
        List<Integer> merIdList = orderDetailList.stream().map(OrderDetail::getMerId).distinct().collect(Collectors.toList());
        Map<Integer, Merchant> merchantMap = merchantService.getMerIdMapByIdList(merIdList);
        List<InfoReplyResponse> responseList = orderDetailList.stream().map(info -> {
            InfoReplyResponse replyResponse = new InfoReplyResponse();
            BeanUtils.copyProperties(info, replyResponse);
            replyResponse.setMerName(merchantMap.get(info.getMerId()).getName());
            replyResponse.setMerIsSelf(merchantMap.get(info.getMerId()).getIsSelf());
            return replyResponse;
        }).collect(Collectors.toList());
        return CommonPage.copyPageInfo(pageInfo, responseList);
    }

    /**
     * 评价订单商品
     *
     * @param request 评价参数
     */
    @Override
    public Boolean replyProduct(OrderProductReplyRequest request) {
        Order order = orderService.getByOrderNo(request.getOrderNo());
        if (!(order.getStatus().equals(OrderConstants.ORDER_STATUS_COMPLETE) || order.getStatus().equals(OrderConstants.ORDER_STATUS_TAKE_DELIVERY))) {
            throw new CrmebException(OrderResultCode.ORDER_STATUS_ABNORMAL, "订单状态异常，无法评价");
        }
        OrderDetail orderDetail = orderDetailService.getById(request.getOrderDetailId());
        if (ObjectUtil.isNull(orderDetail) || !order.getOrderNo().equals(orderDetail.getOrderNo())) {
            throw new CrmebException(OrderResultCode.ORDER_DETAIL_NOT_EXIST);
        }
        if (!orderDetail.getIsReceipt()) {
            throw new CrmebException(OrderResultCode.ORDER_DETAIL_RECEIPT, "请收货后再评论");
        }
        if (orderDetail.getIsReply()) {
            throw new CrmebException(OrderResultCode.ORDER_DETAIL_REPLY);
        }
        orderDetail.setIsReply(true);
        User user = userService.getInfo();
        ProductReply productReply = new ProductReply();
        BeanUtils.copyProperties(request, productReply);
        productReply.setMerId(orderDetail.getMerId());
        productReply.setProductId(orderDetail.getProductId());
        if (order.getType().equals(OrderConstants.ORDER_TYPE_SECKILL)) {
            SeckillProduct seckillProduct = seckillProductService.getById(orderDetail.getProductId());
            productReply.setProductId(seckillProduct.getProductId());
        }
        productReply.setAttrValueId(orderDetail.getAttrValueId());
        productReply.setSku(orderDetail.getSku());
        productReply.setUid(user.getId());
        productReply.setNickname(user.getNickname());
        String cdnUrl = systemAttachmentService.getCdnUrl();
        productReply.setAvatar(systemAttachmentService.clearPrefix(user.getAvatar(), cdnUrl));
        if (CollUtil.isNotEmpty(request.getPics())) {
            List<String> pics = request.getPics().stream().map(e -> systemAttachmentService.clearPrefix(e, cdnUrl)).collect(Collectors.toList());
            productReply.setPics(String.join(",", pics));
        }
        Boolean execute = transactionTemplate.execute(e -> {
            orderDetailService.updateById(orderDetail);
            productReplyService.save(productReply);
            return Boolean.TRUE;
        });
        if (!execute) {
            throw new CrmebException("评价订单商品失败");
        }
        return execute;
    }

    /**
     * 取消订单
     *
     * @param orderNo 订单编号
     */
    @Override
    public Boolean cancel(String orderNo) {
        Integer uid = userService.getUserIdException();
        Order order = orderService.getByOrderNo(orderNo);
        if (!order.getUid().equals(uid)) {
            throw new CrmebException(OrderResultCode.ORDER_NOT_EXIST);
        }
        if (order.getPaid()) {
            throw new CrmebException(OrderResultCode.ORDER_PAID);
        }
        if (order.getStatus().equals(OrderConstants.ORDER_STATUS_CANCEL)) {
            throw new CrmebException(OrderResultCode.ORDER_CANCEL);
        }
        Boolean cancel = orderService.cancel(orderNo, true);
        if (cancel) {
            redisUtil.lPush(TaskConstants.ORDER_TASK_REDIS_KEY_AFTER_CANCEL_BY_USER, order.getOrderNo());
        }
        // 处理拼团的取消订单逻辑
        if(order.getType().equals(OrderConstants.ORDER_TYPE_PITUAN)){
            List<OrderDetail> ods = orderDetailService.getByOrderNo(order.getOrderNo());
            for (OrderDetail od : ods) {
                GroupBuyActivitySku groupBuyActivitySku = groupBuyActivitySkuService.getByAttrId(od.getGroupBuyActivityId(),od.getAttrValueId());
                groupBuyActivitySkuService.rollBackSKUStock(order,groupBuyActivitySku.getGroupActivityId(), od.getAttrValueId(), od.getPayNum());
            }

        }
        return cancel;
    }

    /**
     * 订单收货
     *
     * @param orderNo 订单号
     * @return Boolean
     */
    @Override
    public Boolean takeDelivery(String orderNo) {
        Integer userId = userService.getUserIdException();
        Order order = orderService.getByOrderNo(orderNo);
        if (!order.getUid().equals(userId)) {
            throw new CrmebException(OrderResultCode.ORDER_NOT_EXIST);
        }
        if (!order.getCancelStatus().equals(OrderConstants.ORDER_CANCEL_STATUS_NORMAL)) {
            throw new CrmebException(OrderResultCode.ORDER_CANCEL);
        }
        if (order.getRefundStatus().equals(OrderConstants.ORDER_REFUND_STATUS_ALL)) {
            throw new CrmebException(OrderResultCode.ORDER_STATUS_ABNORMAL, "已退款订单无法收货");
        }
        if (!order.getStatus().equals(OrderConstants.ORDER_STATUS_WAIT_RECEIPT)) {
            throw new CrmebException(OrderResultCode.ORDER_STATUS_ABNORMAL);
        }
        if (order.getRefundStatus().equals(OrderConstants.ORDER_REFUND_STATUS_APPLY)) {
            throw new CrmebException(OrderResultCode.ORDER_STATUS_ABNORMAL, "请先撤销售后，再进行收货操作");
        }
        Boolean execute = transactionTemplate.execute(e -> {
            orderService.takeDelivery(orderNo);
            orderDetailService.takeDelivery(orderNo);
            orderStatusService.createLog(order.getOrderNo(), OrderStatusConstants.ORDER_STATUS_USER_TAKE_DELIVERY, OrderStatusConstants.ORDER_LOG_USER_RECEIPT);
            return Boolean.TRUE;
        });
        if (execute) {
            //后续操作放入redis
            redisUtil.lPush(TaskConstants.ORDER_TASK_REDIS_KEY_AFTER_TAKE_BY_USER, orderNo);
        }
        return execute;
    }

    /**
     * 删除订单
     *
     * @param orderNo 订单号
     * @return Boolean
     */
    @Override
    public Boolean delete(String orderNo) {
        Integer userId = userService.getUserIdException();
        Order order = orderService.getByOrderNo(orderNo);
        if (!order.getUid().equals(userId)) {
            throw new CrmebException(OrderResultCode.ORDER_NOT_EXIST);
        }
        if (!(order.getStatus().equals(OrderConstants.ORDER_STATUS_COMPLETE) || order.getStatus().equals(OrderConstants.ORDER_STATUS_CANCEL))) {
            throw new CrmebException(OrderResultCode.ORDER_STATUS_ABNORMAL, "未完成订单无法删除");
        }
        if (order.getIsUserDel()) {
            throw new CrmebException(OrderResultCode.ORDER_DELETE);
        }
        order.setIsUserDel(true);
        return transactionTemplate.execute(e -> {
            orderService.updateById(order);
            orderStatusService.createLog(orderNo, OrderStatusConstants.ORDER_STATUS_USER_DELETE, OrderStatusConstants.ORDER_LOG_USER_DELETE);
            return Boolean.TRUE;
        });
    }

    /**
     * 售后申请列表(可申请售后列表)
     *
     * @param request 搜索参数
     */
    @Override
    public PageInfo<OrderDetail> getAfterSaleApplyList(CommonSearchRequest request) {
        Integer uid = userService.getUserIdException();
        PageInfo<OrderDetail> pageInfo = orderDetailService.findAfterSaleApplyList(uid, request);
        List<OrderDetail> orderDetailList = pageInfo.getList();
        if (CollUtil.isEmpty(orderDetailList)) {
            return pageInfo;
        }
        List<Integer> merIdList = orderDetailList.stream().map(OrderDetail::getMerId).distinct().collect(Collectors.toList());
        Map<Integer, Merchant> merchantMap = merchantService.getMapByIdList(merIdList);
        orderDetailList.forEach(o -> {
            o.setMerName(merchantMap.get(o.getMerId()).getName());
        });
        return pageInfo;
    }

    /**
     * 查询退款理由
     *
     * @return 退款理由集合
     */
    @Override
    public List<String> getRefundReason() {
        String reasonString = systemConfigService.getValueByKey(SysConfigConstants.CONFIG_KEY_STOR_REASON);
        reasonString = CrmebUtil.UnicodeToCN(reasonString);
        reasonString = reasonString.replace("rn", "n");
        return Arrays.asList(reasonString.split("\\n"));
    }

    /**
     * 订单退款申请
     *
     * @param request 申请参数
     * @return Boolean
     */
    @Override
    public Boolean refundApply(OrderRefundApplyRequest request) {
        Integer uid = userService.getUserIdException();
        Order order = orderService.getByOrderNo(request.getOrderNo());
        if (!order.getUid().equals(uid) || order.getIsUserDel()) {
            throw new CrmebException(OrderResultCode.ORDER_NOT_EXIST);
        }
        if (order.getCancelStatus() > OrderConstants.ORDER_CANCEL_STATUS_NORMAL) {
            throw new CrmebException(OrderResultCode.ORDER_CANCEL);
        }
        if (!order.getPaid()) {
            throw new CrmebException(OrderResultCode.ORDER_NOT_PAID);
        }
        if (order.getRefundStatus().equals(OrderConstants.ORDER_REFUND_STATUS_ALL)) {
            throw new CrmebException(OrderResultCode.ORDER_REFUND_ED);
        }
        if (order.getStatus().equals(OrderConstants.ORDER_STATUS_COMPLETE)) {
            throw new CrmebException(OrderResultCode.ORDER_STATUS_ABNORMAL, "已完成订单无法申请退款");
        }
        if (!order.getGroupBuyRecordStatus().equals(99)) {
            // 拼团判断
            if (!order.getGroupBuyRecordStatus().equals(10)) {
                throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "拼团状态异常，无法申请退款");
            }
        }
        OrderDetail orderDetail = orderDetailService.getById(request.getOrderDetailId());
        if (ObjectUtil.isNull(orderDetail) || !orderDetail.getOrderNo().equals(order.getOrderNo())) {
            throw new CrmebException(OrderResultCode.ORDER_DETAIL_NOT_EXIST);
        }
        int canApplyNum = orderDetail.getPayNum() - orderDetail.getApplyRefundNum() - orderDetail.getRefundNum();
        if (canApplyNum < request.getNum()) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, StrUtil.format("剩余可退款数量为{}", canApplyNum));
        }
        MerchantOrder merchantOrder = merchantOrderService.getOneByOrderNo(order.getOrderNo());

        RefundOrder refundOrder = new RefundOrder();
        refundOrder.setRefundOrderNo(CrmebUtil.getOrderNo(OrderConstants.ORDER_PREFIX_REFUND));
        refundOrder.setOrderNo(order.getOrderNo());
        refundOrder.setMerId(order.getMerId());
        refundOrder.setUid(order.getUid());
        refundOrder.setRealName(merchantOrder.getRealName());
        refundOrder.setUserPhone(merchantOrder.getUserPhone());
        refundOrder.setUserAddress(merchantOrder.getUserAddress());
        refundOrder.setRefundPrice(order.getPayPrice());
        refundOrder.setTotalNum(request.getNum());
        refundOrder.setRefundReasonWap(request.getText());
        refundOrder.setRefundReasonWapImg(systemAttachmentService.clearPrefix(request.getReasonImage()));
        refundOrder.setRefundReasonWapExplain(request.getExplain());
        refundOrder.setRefundStatus(OrderConstants.MERCHANT_REFUND_ORDER_STATUS_APPLY);
        refundOrder.setRefundPlatCouponPrice(merchantOrder.getPlatCouponPrice());
        refundOrder.setAfterSalesType(request.getAfterSalesType());
        refundOrder.setReturnGoodsType(request.getReturnGoodsType());

        RefundOrderInfo refundOrderInfo = new RefundOrderInfo();
        refundOrderInfo.setRefundOrderNo(refundOrder.getRefundOrderNo());
        refundOrderInfo.setMerId(orderDetail.getMerId());
        refundOrderInfo.setOrderDetailId(orderDetail.getId());
        refundOrderInfo.setProductId(orderDetail.getProductId());
        refundOrderInfo.setProductName(orderDetail.getProductName());
        refundOrderInfo.setImage(orderDetail.getImage());
        refundOrderInfo.setAttrValueId(orderDetail.getAttrValueId());
        refundOrderInfo.setSku(orderDetail.getSku());
        refundOrderInfo.setPrice(orderDetail.getPrice());
        refundOrderInfo.setPayNum(orderDetail.getPayNum());
        refundOrderInfo.setProductType(orderDetail.getProductType());
        refundOrderInfo.setPayPrice(orderDetail.getPayPrice());
        refundOrderInfo.setApplyRefundNum(request.getNum());
        // 临时性计算退款金额、积分
        if (request.getNum().equals(orderDetail.getPayNum())) {
            refundOrderInfo.setRefundPrice(orderDetail.getPayPrice());
            refundOrderInfo.setRefundUseIntegral(orderDetail.getUseIntegral());
            refundOrderInfo.setRefundGainIntegral(orderDetail.getGainIntegral());
            refundOrderInfo.setRefundFreightFee(orderDetail.getFreightFee());
        } else {
            refundOrderInfo.setRefundUseIntegral(0);
            refundOrderInfo.setRefundGainIntegral(0);
            BigDecimal ratio = new BigDecimal(request.getNum().toString()).divide(new BigDecimal(orderDetail.getPayNum().toString()), 10, RoundingMode.HALF_UP);
            refundOrderInfo.setRefundPrice(orderDetail.getPayPrice().multiply(ratio).setScale(2, RoundingMode.HALF_UP));
            if (orderDetail.getUseIntegral() > 0) {
                refundOrderInfo.setRefundUseIntegral(new BigDecimal(orderDetail.getUseIntegral().toString()).multiply(ratio).setScale(0, RoundingMode.HALF_UP).intValue());
            }
            if (orderDetail.getGainIntegral() > 0) {
                refundOrderInfo.setRefundGainIntegral(new BigDecimal(orderDetail.getGainIntegral().toString()).multiply(ratio).setScale(0, RoundingMode.HALF_UP).intValue());
            }
            if (orderDetail.getPlatCouponPrice().compareTo(BigDecimal.ZERO) > 0) {
                refundOrderInfo.setRefundPlatCouponPrice(orderDetail.getPlatCouponPrice().multiply(ratio).setScale(2, RoundingMode.HALF_UP));
            }
            refundOrderInfo.setRefundFreightFee(orderDetail.getFreightFee().multiply(ratio).setScale(2, RoundingMode.HALF_UP));
        }

        refundOrder.setRefundPrice(refundOrderInfo.getRefundPrice());
        refundOrder.setRefundUseIntegral(refundOrderInfo.getRefundUseIntegral());
        refundOrder.setRefundGainIntegral(refundOrderInfo.getRefundGainIntegral());
        refundOrder.setRefundPlatCouponPrice(refundOrderInfo.getRefundPlatCouponPrice());
        refundOrder.setRefundFreightFee(refundOrderInfo.getRefundFreightFee());

        order.setRefundStatus(OrderConstants.ORDER_REFUND_STATUS_APPLY);
        orderDetail.setApplyRefundNum(orderDetail.getApplyRefundNum() + request.getNum());

        String outRefundNo = CrmebUtil.getOrderNo(OrderConstants.ORDER_PREFIX_REFUND);
        refundOrder.setOutRefundNo(outRefundNo);
        Boolean execute = transactionTemplate.execute(e -> {
            orderService.updateById(order);
            orderDetailService.updateById(orderDetail);
            refundOrderService.save(refundOrder);
            refundOrderInfoService.save(refundOrderInfo);
            refundOrderStatusService.add(refundOrder.getRefundOrderNo(), RefundOrderConstants.REFUND_ORDER_LOG_APPLY, "用户发起退款单申请");
            return Boolean.TRUE;
        });
        if (Boolean.FALSE.equals(execute)) throw new CrmebException("申请退款失败");
        return execute;
    }

    /**
     * 退款订单列表
     *
     * @param request 搜索参数
     * @return PageInfo
     */
    @Override
    public PageInfo<RefundOrderResponse> getRefundOrderList(OrderAfterSalesSearchRequest request) {
        return refundOrderService.getH5List(request);
    }

    /**
     * 退款订单详情
     *
     * @param refundOrderNo 退款订单号
     * @return RefundOrderInfoResponse
     */
    @Override
    public RefundOrderInfoResponse refundOrderDetail(String refundOrderNo) {
        return refundOrderService.getRefundOrderDetailByRefundOrderNo(refundOrderNo);
    }

    /**
     * 订单物流详情
     */
    @Override
    public LogisticsResultVo getLogisticsInfo(Integer invoiceId) {
        return orderService.getLogisticsInfo(invoiceId);
    }

    /**
     * 获取发货单列表
     *
     * @param orderNo 订单号
     * @return 发货单列表
     */
    @Override
    public OrderInvoiceFrontResponse getInvoiceList(String orderNo) {
        Order order = orderService.getByOrderNo(orderNo);
        List<OrderInvoiceResponse> invoiceList = orderService.getInvoiceList(orderNo);
        OrderInvoiceFrontResponse response = new OrderInvoiceFrontResponse();
        response.setInvoiceList(invoiceList);
        if (CollUtil.isEmpty(invoiceList)) {
            response.setNum(1);
            response.setDeliveryNum(0);
            return response;
        }
        response.setNum(order.getStatus().equals(OrderConstants.ORDER_STATUS_PART_SHIPPING) ? invoiceList.size() + 1 : invoiceList.size());
        response.setDeliveryNum(invoiceList.size());
        return response;
    }

    /**
     * 获取个人中心订单数量
     */
    @Override
    public OrderCenterNumResponse userCenterNum() {
        Integer userId = userService.getUserIdException();
        OrderCenterNumResponse response = new OrderCenterNumResponse();
        response.setAwaitPayCount(orderService.getCountByStatusAndUid(OrderConstants.ORDER_STATUS_WAIT_PAY, userId));
        response.setAwaitShippedCount(orderService.getCountByStatusAndUid(OrderConstants.ORDER_STATUS_WAIT_SHIPPING, userId));
        response.setReceiptCount(orderService.getCountByStatusAndUid(OrderConstants.ORDER_STATUS_WAIT_RECEIPT, userId));
        response.setVerificationCount(orderService.getCountByStatusAndUid(OrderConstants.ORDER_STATUS_AWAIT_VERIFICATION, userId));
        response.setAwaitReplyCount(orderDetailService.getAwaitReplyCount(userId));
        response.setRefundCount(refundOrderService.getRefundingCount(userId));
        return response;
    }

    /**
     * 获取订单状态图
     */
    @Override
    public List<HashMap<String, Object>> getOrderStatusImage() {
        List<HashMap<String, Object>> mapList = systemGroupDataService.getListMapByGid(GroupDataConstants.GROUP_DATA_ID_ORDER_STATUS_IMAGE);
        return mapList;
    }

    private List<MyRecord> validateProductStock(PreOrderInfoVo orderInfoVo) {
        List<MyRecord> recordList = CollUtil.newArrayList();
        if (orderInfoVo.getSecondType().equals(OrderConstants.ORDER_SECOND_TYPE_VIDEO)) {// 视频号订单
            // 查询商品信息 视频号都是单品下单
            List<PreOrderInfoDetailVo> detailVos = orderInfoVo.getMerchantOrderVoList().get(0).getOrderInfoList();
            PayComponentProduct product = payComponentProductService.getById(detailVos.get(0).getProductId());
            if (ObjectUtil.isNull(product)) {
                throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "商品信息不存在，请刷新后重新选择");
            }
            if (product.getIsDel()) {
                throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "商品已删除，请刷新后重新选择");
            }
            if (!product.getStatus().equals(5)) {
                throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "商品已下架，请刷新后重新选择");
            }
            if (product.getStock().equals(0) || product.getStock() < detailVos.get(0).getPayNum()) {
                throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "商品库存不足，请刷新后重新选择");
            }
            // 查询商品规格属性值信息
            ProductAttrValue attrValue = productAttrValueService.getByIdAndProductIdAndType(detailVos.get(0).getAttrValueId(), detailVos.get(0).getProductId(), ProductConstants.PRODUCT_TYPE_COMPONENT);
            if (ObjectUtil.isNull(attrValue)) {
                throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "商品规格信息不存在，请刷新后重新选择");
            }
            if (attrValue.getStock().equals(0) || attrValue.getStock() < detailVos.get(0).getPayNum()) {
                throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "商品规格库存不足，请刷新后重新选择");
            }
            // 查询视频商品规格属性值
            PayComponentProductSku productSku = payComponentProductSkuService.getByProIdAndAttrValueId(product.getId(), attrValue.getId());
            if (ObjectUtil.isNull(productSku)) {
                throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "商品sku信息不存在，请刷新后重新选择");
            }
            if (productSku.getStockNum().equals(0) || productSku.getStockNum() < detailVos.get(0).getPayNum()) {
                throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "商品sku库存不足，请刷新后重新选择");
            }

            MyRecord record = new MyRecord();
            record.set("productId", product.getId());
            record.set("attrValueId", detailVos.get(0).getAttrValueId());
            record.set("attrValueVersion", attrValue.getVersion());
            record.set("num", detailVos.get(0).getPayNum());
            record.set("skuId", productSku.getId());
            record.set("skuVersion", productSku.getVersion());
            recordList.add(record);
            return recordList;
        }
        if (orderInfoVo.getSecondType().equals(OrderConstants.ORDER_SECOND_TYPE_INTEGRAL)) {
            List<PreOrderInfoDetailVo> orderInfoList = orderInfoVo.getMerchantOrderVoList().get(0).getOrderInfoList();
            orderInfoList.forEach(info -> {
                // 查询商品信息
                Product product = productService.getById(info.getProductId());
                if (ObjectUtil.isNull(product) || product.getIsDel()) {
                    throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "购买的商品信息不存在");
                }
                if (!product.getIsShow()) {
                    throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "购买的商品已下架");
                }
                if (product.getStock().equals(0) || info.getPayNum() > product.getStock()) {
                    throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "购买的商品库存不足");
                }
                // 查询商品规格属性值信息
                ProductAttrValue attrValue = productAttrValueService.getByIdAndProductIdAndType(
                        info.getAttrValueId(), info.getProductId(), product.getType(), product.getMarketingType());
                if (ObjectUtil.isNull(attrValue)) {
                    throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "购买的商品规格信息不存在");
                }
                if (!attrValue.getIsShow()) {
                    throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "购买的商品规格信息无效");
                }
                if (attrValue.getStock() < info.getPayNum()) {
                    throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "购买的商品库存不足");
                }
                User user = userService.getInfo();
                if (user.getIntegral() < (attrValue.getRedeemIntegral() * info.getPayNum())) {
                    throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "用户积分不足");
                }
                if (product.getExchangeNum() > 0) {
                    Integer orderProductNum = orderService.getProductNumCount(user.getId(), product.getId(), product.getMarketingType());
                    if (product.getExchangeNum() < (info.getPayNum() + orderProductNum)) {
                        throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "积分商品购买已达限量");
                    }
                }

                MyRecord record = new MyRecord();
                record.set("productId", info.getProductId());
                record.set("num", info.getPayNum());
                record.set("attrValueId", info.getAttrValueId());
                record.set("attrValueVersion", attrValue.getVersion());
                record.set("type", attrValue.getType());
                record.set("marketingType", attrValue.getMarketingType());
                if (product.getType().equals(ProductConstants.PRODUCT_TYPE_CLOUD)) {
                    record.set("expand", attrValue.getExpand());
                }
                if (product.getType().equals(ProductConstants.PRODUCT_TYPE_CDKEY)) {
                    record.set("cdkeyId", attrValue.getCdkeyId());
                }
                record.set("isPaidMember", product.getIsPaidMember());
                record.set("vipPrice", attrValue.getVipPrice());
                recordList.add(record);
            });
            return recordList;
        }
        // 普通商品
        List<PreMerchantOrderVo> merchantOrderVoList = orderInfoVo.getMerchantOrderVoList();
        merchantOrderVoList.forEach(merchantOrderVo -> {
            Merchant merchant = merchantService.getByIdException(merchantOrderVo.getMerId());
            if (!merchant.getIsSwitch()) {
                throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "商户已关闭，请重新下单");
            }
            merchantOrderVo.getOrderInfoList().forEach(info -> {
                // 查询商品信息
                Product product = productService.getById(info.getProductId());
                if (ObjectUtil.isNull(product) || product.getIsDel()) {
                    throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "购买的商品信息不存在");
                }
                if (!product.getIsShow()) {
                    throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "购买的商品已下架");
                }
                if (product.getStock().equals(0) || info.getPayNum() > product.getStock()) {
                    throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "购买的商品库存不足");
                }
                // 查询商品规格属性值信息
                ProductAttrValue attrValue = productAttrValueService.getByIdAndProductIdAndType(
                        info.getAttrValueId(), info.getProductId(), product.getType(), product.getMarketingType());
                if (ObjectUtil.isNull(attrValue)) {
                    throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "购买的商品规格信息不存在");
                }
                if (!attrValue.getIsShow()) {
                    throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "购买的商品规格信息无效");
                }
                if (attrValue.getStock() < info.getPayNum()) {
                    throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "购买的商品库存不足");
                }

                MyRecord record = new MyRecord();
                record.set("productId", info.getProductId());
                record.set("num", info.getPayNum());
                record.set("attrValueId", info.getAttrValueId());
                record.set("attrValueVersion", attrValue.getVersion());
                record.set("type", attrValue.getType());
                record.set("marketingType", attrValue.getMarketingType());
                if (product.getType().equals(ProductConstants.PRODUCT_TYPE_CLOUD)) {
                    record.set("expand", attrValue.getExpand());
                }
                if (product.getType().equals(ProductConstants.PRODUCT_TYPE_CDKEY)) {
                    record.set("cdkeyId", attrValue.getCdkeyId());
                }
                record.set("isPaidMember", product.getIsPaidMember());
                record.set("vipPrice", attrValue.getVipPrice());
                recordList.add(record);
            });
        });
        return recordList;
    }

    /**
     * 计算订单运费
     */
    @Override
    public void getFreightFee_V_1_8(PreOrderInfoVo orderInfoVo, UserAddress userAddress, Boolean userIsPaidMember) {
        BigDecimal freightFee = BigDecimal.ZERO;

        List<PreMerchantOrderVo> merchantOrderVoList = orderInfoVo.getMerchantOrderVoList();
        for (PreMerchantOrderVo merchantOrderVo : merchantOrderVoList) {
            if (orderInfoVo.getSecondType().equals(OrderConstants.ORDER_SECOND_TYPE_CLOUD)
                    || orderInfoVo.getSecondType().equals(OrderConstants.ORDER_SECOND_TYPE_CDKEY)
                    || orderInfoVo.getSecondType().equals(OrderConstants.ORDER_SECOND_TYPE_VIRTUALLY)) {
                orderInfoVo.setAddressId(0);
                merchantOrderVo.setFreightFee(BigDecimal.ZERO);
                merchantOrderVo.getOrderInfoList().forEach(info -> info.setFreightFee(BigDecimal.ZERO));
                continue;
            }
            BigDecimal storePostage = BigDecimal.ZERO;
            if (merchantOrderVo.getShippingType().equals(OrderConstants.ORDER_SHIPPING_TYPE_PICK_UP)) {
                merchantOrderVo.setFreightFee(storePostage);
                merchantOrderVo.getOrderInfoList().forEach(info -> info.setFreightFee(BigDecimal.ZERO));
                continue;
            }
            if (orderInfoVo.getSecondType().equals(OrderConstants.ORDER_SECOND_TYPE_INTEGRAL)) {
                // 积分订单默认全国包邮
                merchantOrderVo.setFreightFee(storePostage);
                merchantOrderVo.getOrderInfoList().forEach(info -> info.setFreightFee(BigDecimal.ZERO));
                continue;
            }
            if (ObjectUtil.isNull(userAddress) || userAddress.getCityId() <= 0) {
                merchantOrderVo.setFreightFee(storePostage);
                merchantOrderVo.getOrderInfoList().forEach(info -> info.setFreightFee(BigDecimal.ZERO));
                continue;
            }
            // 按模板构建MAP
            Map<Integer, MyRecord> tempMap = CollUtil.newHashMap();
            // 运费根据商品计算
//            Map<Integer, MyRecord> proMap = CollUtil.newHashMap();
            merchantOrderVo.getOrderInfoList().forEach(e -> {
                Integer tempId = e.getTempId();
                BigDecimal proPrice;
                if (e.getIsPaidMember() && userIsPaidMember) {
                    proPrice = e.getVipPrice().multiply(BigDecimal.valueOf(e.getPayNum()));
                } else {
                    proPrice = e.getPrice().multiply(BigDecimal.valueOf(e.getPayNum()));
                }
                if (tempMap.containsKey(tempId)) {
                    MyRecord record = tempMap.get(tempId);
                    record.set("totalPrice", record.getBigDecimal("totalPrice").add(proPrice));
                    record.set("totalNum", record.getInt("totalNum") + e.getPayNum());
                    BigDecimal weight = e.getWeight().multiply(BigDecimal.valueOf(e.getPayNum()));
                    record.set("weight", record.getBigDecimal("weight").add(weight));
                    BigDecimal volume = e.getVolume().multiply(BigDecimal.valueOf(e.getPayNum()));
                    record.set("volume", record.getBigDecimal("volume").add(volume));
                } else {
                    MyRecord record = new MyRecord();
                    record.set("totalPrice", proPrice);
                    record.set("totalNum", e.getPayNum());
                    record.set("tempId", tempId);
                    BigDecimal weight = e.getWeight().multiply(BigDecimal.valueOf(e.getPayNum()));
                    record.set("weight", weight);
                    BigDecimal volume = e.getVolume().multiply(BigDecimal.valueOf(e.getPayNum()));
                    record.set("volume", volume);
                    tempMap.put(tempId, record);
                }
            });

            // 指定包邮（单品运费模板）> 指定区域配送（单品运费模板）
            int districtId = userAddress.getDistrictId();
            for (Map.Entry<Integer, MyRecord> m : tempMap.entrySet()) {
                MyRecord record = m.getValue();
                Integer tempId = record.getInt("tempId");
                ShippingTemplates shippingTemplate = shippingTemplatesService.getById(tempId);
                if (ObjectUtil.isNull(shippingTemplate) || shippingTemplate.getAppoint().equals(ShippingTemplatesConstants.APPOINT_TYPE_ALL)) {
                    merchantOrderVo.getOrderInfoList().stream()
                            .filter(e -> e.getTempId().equals(m.getKey()))
                            .forEach(info -> info.setFreightFee(BigDecimal.ZERO));
                    continue;
                }
                if (shippingTemplate.getAppoint().equals(ShippingTemplatesConstants.APPOINT_TYPE_DEFINED)) {
                    ShippingTemplatesFree shippingTemplatesFree = shippingTemplatesFreeService.getByTempIdAndCityId(tempId, districtId);
                    if (ObjectUtil.isNotNull(shippingTemplatesFree)) {
                        BigDecimal totalPrice = record.getBigDecimal("totalPrice");
                        if (totalPrice.compareTo(shippingTemplatesFree.getPrice()) >= 0) {
                            merchantOrderVo.getOrderInfoList().stream()
                                    .filter(e -> e.getTempId().equals(m.getKey()))
                                    .forEach(info -> info.setFreightFee(BigDecimal.ZERO));
                            continue;
                        }
                        if (shippingTemplate.getType().equals(ShippingTemplatesConstants.CHARGE_MODE_TYPE_NUMBER)) {
                            if (BigDecimal.valueOf(record.getInt("totalNum")).compareTo(shippingTemplatesFree.getNumber()) >= 0) {
                                merchantOrderVo.getOrderInfoList().stream()
                                        .filter(e -> e.getTempId().equals(m.getKey()))
                                        .forEach(info -> info.setFreightFee(BigDecimal.ZERO));
                                continue;
                            }
                        }
                        BigDecimal surplus = shippingTemplate.getType().equals(ShippingTemplatesConstants.CHARGE_MODE_TYPE_WEIGHT)
                                ? record.getBigDecimal("weight") : record.getBigDecimal("volume");
                        if (surplus.compareTo(shippingTemplatesFree.getNumber()) >= 0) {
                            merchantOrderVo.getOrderInfoList().stream()
                                    .filter(e -> e.getTempId().equals(m.getKey()))
                                    .forEach(info -> info.setFreightFee(BigDecimal.ZERO));
                            continue;
                        }
                    }
                }
                ShippingTemplatesRegion shippingTemplatesRegion = shippingTemplatesRegionService.getByTempIdAndCityId(tempId, districtId);
                if (ObjectUtil.isNull(shippingTemplatesRegion)) {
                    shippingTemplatesRegion = shippingTemplatesRegionService.getByTempIdAndCityId(tempId, 0);
                }
                if (shippingTemplate.getAppoint().equals(ShippingTemplatesConstants.APPOINT_TYPE_PART)) {
                    if (ObjectUtil.isNull(shippingTemplatesRegion)) {
                        merchantOrderVo.getOrderInfoList().stream()
                                .filter(e -> e.getTempId().equals(m.getKey()))
                                .forEach(info -> info.setFreightFee(BigDecimal.ZERO));
                        continue;
                    }
                }
                BigDecimal postageFee = BigDecimal.ZERO;
                // 判断计费方式：件数、重量、体积
                switch (shippingTemplate.getType()) {
                    case ShippingTemplatesConstants.CHARGE_MODE_TYPE_NUMBER: // 件数
                        // 判断件数是否超过首件
                        Integer num = record.getInt("totalNum");
                        if (num <= shippingTemplatesRegion.getFirst().intValue()) {
                            storePostage = storePostage.add(shippingTemplatesRegion.getFirstPrice());
                            postageFee = shippingTemplatesRegion.getFirstPrice();
                        } else {// 超过首件的需要计算续件
                            int renewalNum = num - shippingTemplatesRegion.getFirst().intValue();
                            // 剩余件数/续件 = 需要计算的续件费用的次数
                            // 更新运费计算公式 2024-4-15
                            BigDecimal renewalPrice = shippingTemplatesRegion.getRenewalPrice().multiply(new BigDecimal(String.valueOf(renewalNum)).divide(shippingTemplatesRegion.getRenewal(), 0, RoundingMode.UP));
                            storePostage = storePostage.add(shippingTemplatesRegion.getFirstPrice()).add(renewalPrice);
                            postageFee = shippingTemplatesRegion.getFirstPrice().add(renewalPrice);
                        }
                        List<PreOrderInfoDetailVo> detailVoList = merchantOrderVo.getOrderInfoList().stream().filter(e -> e.getTempId().equals(tempId)).collect(Collectors.toList());
                        if (detailVoList.size() == 1) {
                            detailVoList.get(0).setFreightFee(postageFee);
                        } else {
                            Integer tempNum = num;
                            for (int i = 0; i < detailVoList.size(); i++) {
                                PreOrderInfoDetailVo detail = detailVoList.get(i);
                                if (detailVoList.size() == (i + 1)) {
                                    detail.setFreightFee(postageFee);
                                    break;
                                }
                                // 更新运费计算公式 2024-4-15
                                BigDecimal multiply = postageFee.multiply(new BigDecimal(detail.getPayNum().toString())).divide(new BigDecimal(tempNum.toString()), 2, RoundingMode.UP);
                                if (postageFee.compareTo(multiply) < 0) {
                                    multiply = postageFee;
                                }
                                detail.setFreightFee(multiply);
                                postageFee = postageFee.subtract(multiply);
                                tempNum = tempNum - detail.getPayNum();
                            }
                        }
                        break;
                    case ShippingTemplatesConstants.CHARGE_MODE_TYPE_WEIGHT: // 重量
                    case ShippingTemplatesConstants.CHARGE_MODE_TYPE_VOLIME: // 体积
                        BigDecimal surplus = shippingTemplate.getType().equals(ShippingTemplatesConstants.CHARGE_MODE_TYPE_WEIGHT) ? record.getBigDecimal("weight") : record.getBigDecimal("volume");
                        if (surplus.compareTo(shippingTemplatesRegion.getFirst()) <= 0) {
                            storePostage = storePostage.add(shippingTemplatesRegion.getFirstPrice());
                            postageFee = shippingTemplatesRegion.getFirstPrice();
                        } else {// 超过首件的需要计算续件
                            BigDecimal renewalNum = surplus.subtract(shippingTemplatesRegion.getFirst());
                            // 剩余件数/续件 = 需要计算的续件费用的次数
                            // 更新运费计算公式 2024-4-15
                            BigDecimal renewalPrice = shippingTemplatesRegion.getRenewalPrice().multiply(renewalNum.divide(shippingTemplatesRegion.getRenewal(), 0, RoundingMode.UP));
                            storePostage = storePostage.add(shippingTemplatesRegion.getFirstPrice()).add(renewalPrice);
                            postageFee = shippingTemplatesRegion.getFirstPrice().add(renewalPrice);
                        }
                        List<PreOrderInfoDetailVo> infoDetailVoList = merchantOrderVo.getOrderInfoList().stream().filter(e -> e.getTempId().equals(tempId)).collect(Collectors.toList());
                        if (infoDetailVoList.size() == 1) {
                            infoDetailVoList.get(0).setFreightFee(postageFee);
                        } else {
                            BigDecimal tempSurplus = surplus;
                            for (int i = 0; i < infoDetailVoList.size(); i++) {
                                PreOrderInfoDetailVo detail = infoDetailVoList.get(i);
                                if (infoDetailVoList.size() == (i + 1)) {
                                    detail.setFreightFee(postageFee);
                                    break;
                                }
                                BigDecimal wv = shippingTemplate.getType().equals(ShippingTemplatesConstants.CHARGE_MODE_TYPE_WEIGHT) ? detail.getWeight() : detail.getVolume();
                                // 更新运费计算公式 2024-4-15
                                BigDecimal multiply = postageFee.multiply(wv.multiply(new BigDecimal(detail.getPayNum().toString()))).divide(tempSurplus, 2, RoundingMode.HALF_UP);
                                if (postageFee.compareTo(multiply) < 0) {
                                    multiply = postageFee;
                                }
                                detail.setFreightFee(multiply);
                                postageFee = postageFee.subtract(multiply);
                                tempSurplus = tempSurplus.subtract(wv.multiply(new BigDecimal(detail.getPayNum().toString())));
                            }
                        }
                        break;
                }
            }
            merchantOrderVo.setFreightFee(storePostage);
            freightFee = freightFee.add(storePostage);
        }
        orderInfoVo.setFreightFee(freightFee);
    }

    /**
     * 校验预下单商品信息
     *
     * @param request 预下单请求参数
     * @return OrderInfoVo
     */
    private PreOrderInfoVo validatePreOrderRequest_V1_7(PreOrderRequest request, User user) {
        PreOrderInfoVo preOrderInfoVo = new PreOrderInfoVo();
        List<PreMerchantOrderVo> merchantOrderVoList = CollUtil.newArrayList();

        switch (request.getPreOrderType()) {
            case OrderConstants.PLACE_ORDER_TYPE_CART:
                merchantOrderVoList = validatePreOrderShopping_V1_7(request, user);
                List<Integer> cartIdList = request.getOrderDetails().stream().map(PreOrderDetailRequest::getShoppingCartId).distinct().collect(Collectors.toList());
                preOrderInfoVo.setCartIdList(cartIdList);
                preOrderInfoVo.setType(OrderConstants.ORDER_TYPE_BASE);
                preOrderInfoVo.setSecondType(OrderConstants.ORDER_SECOND_TYPE_NORMAL);
                break;
            case OrderConstants.PLACE_ORDER_TYPE_BUY_NOW:
                // 立即购买只会有一条详情
                PreOrderDetailRequest preOrderDetailRequest = request.getOrderDetails().get(0);
                PreMerchantOrderVo preMerchantOrderVo = validatePreOrderBase(preOrderDetailRequest);
                preOrderInfoVo.setType(OrderConstants.ORDER_TYPE_BASE);
                preOrderInfoVo.setSecondType(preMerchantOrderVo.getSecondType());
                preOrderInfoVo.setSystemFormId(preMerchantOrderVo.getSystemFormId());
                preOrderInfoVo.setSystemFormValue(preMerchantOrderVo.getSystemFormValue());
                preMerchantOrderVo.setSystemFormId(0);
                preMerchantOrderVo.setSystemFormValue("");
                merchantOrderVoList.add(preMerchantOrderVo);
                break;
            case OrderConstants.PLACE_ORDER_TYPE_VIDEO:
                // 视频号暂时只能购买一个商品
                merchantOrderVoList.add(validatePreOrderVideo(request.getOrderDetails().get(0)));
                preOrderInfoVo.setType(OrderConstants.ORDER_TYPE_BASE);
                preOrderInfoVo.setSecondType(OrderConstants.ORDER_SECOND_TYPE_VIDEO);
                break;
            case OrderConstants.PLACE_ORDER_TYPE_SECKILL:
                // 秒杀只支持单商品支付
                PreMerchantOrderVo preMerchantSeckillOrderVo = seckillService.validatePreOrderSeckill(request.getOrderDetails().get(0));
                preOrderInfoVo.setType(OrderConstants.ORDER_TYPE_SECKILL);
                preOrderInfoVo.setSecondType(preMerchantSeckillOrderVo.getSecondType());
                preOrderInfoVo.setSystemFormId(preMerchantSeckillOrderVo.getSystemFormId());
                preOrderInfoVo.setSystemFormValue(preMerchantSeckillOrderVo.getSystemFormValue());
                preMerchantSeckillOrderVo.setSystemFormId(0);
                preMerchantSeckillOrderVo.setSystemFormValue("");
                merchantOrderVoList.add(preMerchantSeckillOrderVo);
                break;
            case OrderConstants.PLACE_ORDER_TYPE_GROUP:
                preOrderInfoVo.setType(OrderConstants.ORDER_TYPE_PITUAN);
                PreOrderDetailRequest preOrderDetailRequestP = request.getOrderDetails().get(0);

                PreMerchantOrderVo preMerchantOrderVoP = groupBuyRecordService.validatePreOrderGroupBuy(preOrderDetailRequestP);

                for (PreOrderInfoDetailVo preOrderInfoDetailVo : preMerchantOrderVoP.getOrderInfoList()) {
                    preOrderInfoDetailVo.setGroupBuyActivityId(preOrderDetailRequestP.getGroupBuyActivityId());
                    preOrderInfoDetailVo.setGroupBuyRecordId(preOrderDetailRequestP.getGroupBuyRecordId());
                }
//                preOrderInfoVo.setType(OrderConstants.ORDER_TYPE_BASE);
                preOrderInfoVo.setSecondType(preMerchantOrderVoP.getSecondType());
                preOrderInfoVo.setSystemFormId(preMerchantOrderVoP.getSystemFormId());
                preOrderInfoVo.setSystemFormValue(preMerchantOrderVoP.getSystemFormValue());
                preMerchantOrderVoP.setSystemFormId(0);
                preMerchantOrderVoP.setSystemFormValue("");
                merchantOrderVoList.add(preMerchantOrderVoP);
                break;
        }
        preOrderInfoVo.setMerchantOrderVoList(merchantOrderVoList);
        return preOrderInfoVo;
    }

    /**
     * 基础商品校验
     */
    private PreMerchantOrderVo validatePreOrderBase(PreOrderDetailRequest detailRequest) {
        if (ObjectUtil.isNull(detailRequest.getProductId())) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "商品编号不能为空");
        }
        if (ObjectUtil.isNull(detailRequest.getAttrValueId())) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "商品规格属性值不能为空");
        }
        if (ObjectUtil.isNull(detailRequest.getProductNum()) || detailRequest.getProductNum() <= 0) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "购买数量必须大于0");
        }
        Product product = productService.getById(detailRequest.getProductId());
        if (ObjectUtil.isNull(product) || product.getIsDel()) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "商品信息不存在，请刷新后重新选择");
        }
        if (!product.getIsShow()) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "商品已下架，请刷新后重新选择");
        }
        if (product.getStock() < detailRequest.getProductNum()) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "商品库存不足，请刷新后重新选择");
        }
        // 查询商品规格属性值信息
        ProductAttrValue attrValue = productAttrValueService.getByIdAndProductIdAndType(detailRequest.getAttrValueId(),
                product.getId(), product.getType(), ProductConstants.PRODUCT_MARKETING_TYPE_BASE);
        if (ObjectUtil.isNull(attrValue)) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "商品规格信息不存在，请刷新后重新选择");
        }
        if (attrValue.getStock() < detailRequest.getProductNum()) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "商品规格库存不足，请刷新后重新选择");
        }
        if (!attrValue.getIsShow()) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "商品规格信息无效，请刷新后重新选择");
        }
        Merchant merchant = new Merchant();

        if (product.getType().equals(ProductConstants.PRODUCT_TYPE_INTEGRAL)) {
            User user = userService.getInfo();
            if (user.getIntegral() < (attrValue.getRedeemIntegral() * detailRequest.getProductNum())) {
                throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "积分不足，无法购买此商品");
            }
        } else {
            merchant = merchantService.getByIdException(product.getMerId());
            if (!merchant.getIsSwitch()) {
                throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "商户已关闭，请重新选择商品");
            }
            if (!merchant.getIsTakeTheir() && product.getDeliveryMethod().equals("2")) {
                throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "此商品配送方式设置错误，请联系客服");
            }
        }
        // 校验积分商品限购
        if (product.getType().equals(ProductConstants.PRODUCT_TYPE_INTEGRAL) && product.getExchangeNum() > 0) {
            Integer userId = userService.getUserId();
            Integer orderProductNum = orderService.getProductNumCount(userId, product.getId(), product.getMarketingType());
            if (product.getExchangeNum() < (detailRequest.getProductNum() + orderProductNum)) {
                throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "积分商品购买已达限量");
            }
        }

        // 构造PreMerchantOrderVo对象
        PreMerchantOrderVo merchantOrderVo = new PreMerchantOrderVo();
        if (product.getType().equals(ProductConstants.PRODUCT_TYPE_INTEGRAL)) {
            merchantOrderVo.setMerId(0);
            merchantOrderVo.setMerName("");
            merchantOrderVo.setTakeTheirSwitch(false);
            merchantOrderVo.setIsSelf(false);
        } else {
            merchantOrderVo.setMerId(merchant.getId());
            merchantOrderVo.setMerName(merchant.getName());
            merchantOrderVo.setTakeTheirSwitch(merchant.getIsTakeTheir());
            merchantOrderVo.setIsSelf(merchant.getIsSelf());
        }
        merchantOrderVo.setType(OrderConstants.ORDER_TYPE_BASE);
        merchantOrderVo.setSecondType(product.getType());
        merchantOrderVo.setFreightFee(BigDecimal.ZERO);
        merchantOrderVo.setCouponFee(BigDecimal.ZERO);
        merchantOrderVo.setUserCouponId(0);
        merchantOrderVo.setDeliveryMethodMer(product.getDeliveryMethod());
        PreOrderInfoDetailVo detailVo = new PreOrderInfoDetailVo();
        detailVo.setProductId(product.getId());
        detailVo.setProductName(product.getName());
        detailVo.setAttrValueId(attrValue.getId());
        detailVo.setSku(attrValue.getSku());
        detailVo.setPrice(attrValue.getPrice());
        detailVo.setPayPrice(attrValue.getPrice());
        detailVo.setPayNum(detailRequest.getProductNum());
        detailVo.setImage(StrUtil.isNotBlank(attrValue.getImage()) ? attrValue.getImage() : product.getImage());
        detailVo.setVolume(attrValue.getVolume());
        detailVo.setWeight(attrValue.getWeight());
        detailVo.setTempId(product.getTempId());
        detailVo.setSubBrokerageType(product.getIsSub() ? 1 : 2);
        detailVo.setBrokerage(attrValue.getBrokerage());
        detailVo.setBrokerageTwo(attrValue.getBrokerageTwo());
        detailVo.setIsPaidMember(product.getIsPaidMember());
        if (product.getIsPaidMember()) {
            detailVo.setVipPrice(attrValue.getVipPrice());
        }
        if (detailVo.getSubBrokerageType() == 2) {
            String firstRatio = systemConfigService.getValueByKey(SysConfigConstants.RETAIL_STORE_BROKERAGE_FIRST_RATIO);
            String secondRatio = systemConfigService.getValueByKey(SysConfigConstants.RETAIL_STORE_BROKERAGE_SECOND_RATIO);
            detailVo.setBrokerage(StrUtil.isNotBlank(firstRatio) ? Integer.parseInt(firstRatio) : 0);
            detailVo.setBrokerageTwo(StrUtil.isNotBlank(secondRatio) ? Integer.parseInt(secondRatio) : 0);
        }
        detailVo.setProductType(product.getType());
        detailVo.setProductMarketingType(product.getMarketingType());
        detailVo.setDeliveryMethod(product.getDeliveryMethod());
        detailVo.setProRefundSwitch(product.getRefundSwitch());
        List<PreOrderInfoDetailVo> infoList = CollUtil.newArrayList();
        infoList.add(detailVo);
        if (product.getSystemFormId() > 0) {
            merchantOrderVo.setSystemFormId(product.getSystemFormId());
            SystemForm systemForm = systemFormService.getById(product.getSystemFormId());
            merchantOrderVo.setSystemFormValue(systemForm.getFormValue());
        }
        if (product.getType().equals(5) || product.getType().equals(6)) {
            merchantOrderVo.setShippingType(3);
        } else {
            if (product.getDeliveryMethod().equals("2")) {
                merchantOrderVo.setShippingType(2);
            } else {
                merchantOrderVo.setShippingType(1);
            }
        }

        if (product.getType().equals(ProductConstants.PRODUCT_TYPE_INTEGRAL)) {
            detailVo.setRedeemIntegral(attrValue.getRedeemIntegral());
        }
        merchantOrderVo.setOrderInfoList(infoList);
        return merchantOrderVo;
    }

    /**
     * 购物车预下单校验
     *
     * @param request 请求参数
     * @param user    用户
     * @return List<PreMerchantOrderVo>
     */
    private List<PreMerchantOrderVo> validatePreOrderShopping_V1_7(PreOrderRequest request, User user) {
        List<PreMerchantOrderVo> merchantOrderVoList = CollUtil.newArrayList();
        request.getOrderDetails().forEach(e -> {
            if (ObjectUtil.isNull(e.getShoppingCartId())) {
                throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "购物车编号不能为空");
            }
            Cart cart = cartService.getByIdAndUid(e.getShoppingCartId(), user.getId());
            if (ObjectUtil.isNull(cart)) {
                throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "未找到对应的购物车信息");
            }
            e.setProductId(cart.getProductId());
            e.setAttrValueId(cart.getProductAttrUnique());
            e.setProductNum(cart.getCartNum());
            PreMerchantOrderVo merchantOrderVo = validatePreOrderBase(e);
            if (merchantOrderVoList.stream().anyMatch(m -> m.getMerId().equals(merchantOrderVo.getMerId()))) {
                for (PreMerchantOrderVo orderVo : merchantOrderVoList) {
                    if (orderVo.getMerId().equals(merchantOrderVo.getMerId())) {
                        orderVo.getOrderInfoList().addAll(merchantOrderVo.getOrderInfoList());
                        break;
                    }
                }
            } else {
                merchantOrderVoList.add(merchantOrderVo);
            }
        });
        for (PreMerchantOrderVo merchantOrderVo : merchantOrderVoList) {
            List<PreOrderInfoDetailVo> orderInfoList = merchantOrderVo.getOrderInfoList();
            boolean onlyMerchant = false;
            boolean onlyVerification = false;
            for (PreOrderInfoDetailVo info : orderInfoList) {
                if (info.getDeliveryMethod().equals("1")) {
                    onlyMerchant = true;
                }
                if (info.getDeliveryMethod().equals("2")) {
                    onlyVerification = true;
                }
            }
            if (onlyMerchant && onlyVerification) {
                throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "部分商品配送方式不一致，请单独下单");
            }
            if (onlyVerification) {
                merchantOrderVo.setShippingType(OrderConstants.ORDER_SHIPPING_TYPE_PICK_UP);
                Merchant merchant = merchantService.getByIdException(merchantOrderVo.getMerId());
                if (!merchant.getIsTakeTheir()) {
                    throw new CrmebException(CommonResultCode.VALIDATE_FAILED, StrUtil.format("{}没有配置店铺地址，请联系客服", merchant.getName()));
                }
            }
            if (onlyMerchant) {
                merchantOrderVo.setDeliveryMethodMer("1");
                merchantOrderVo.setShippingType(1);
            } else if (onlyVerification) {
                merchantOrderVo.setDeliveryMethodMer("2");
                merchantOrderVo.setShippingType(2);
            } else {
                merchantOrderVo.setDeliveryMethodMer("1,2");
                merchantOrderVo.setShippingType(1);
            }
        }
        return merchantOrderVoList;
    }

    private ComputedOrderPriceResponse computedPrice(OrderComputedPriceRequest request, PreOrderInfoVo orderInfoVo, User user) {
        String integralDeductionSwitch = systemConfigService.getValueByKey(SysConfigConstants.CONFIG_KEY_INTEGRAL_DEDUCTION_SWITCH);
        if (request.getIsUseIntegral()) {
            if (integralDeductionSwitch.equals("false")) {
                throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "积分抵扣未开启，请重新下单");
            }
        }

        // 计算各种价格
        ComputedOrderPriceResponse priceResponse = new ComputedOrderPriceResponse();
        List<OrderMerchantRequest> orderMerchantRequestList = request.getOrderMerchantRequestList();
        // 计算运费
        UserAddress userAddress = userAddressService.getById(request.getAddressId());
        orderInfoVo.getMerchantOrderVoList().forEach(e -> {
            orderMerchantRequestList.forEach(o -> {
                if (o.getMerId().equals(e.getMerId())) {
                    e.setShippingType(o.getShippingType());
                    e.setUserCouponId(o.getUserCouponId());
                    e.setCouponFee(BigDecimal.ZERO);
                    e.setMerCouponFee(BigDecimal.ZERO);
                }
            });
        });
        getFreightFee_V_1_8(orderInfoVo, userAddress, user.getIsPaidMember());
        priceResponse.setFreightFee(orderInfoVo.getFreightFee());
        priceResponse.setSvipDiscountPrice(orderInfoVo.getSvipDiscountPrice());
        // 优惠券计算
        orderInfoVo.setCouponFee(BigDecimal.ZERO);
        orderInfoVo.setPlatUserCouponId(request.getPlatUserCouponId());
        orderInfoVo.setPlatCouponFee(BigDecimal.ZERO);
        orderInfoVo.setMerCouponFee(BigDecimal.ZERO);
        priceResponse.setCouponFee(BigDecimal.ZERO);
        if (orderInfoVo.getType().equals(OrderConstants.ORDER_TYPE_SECKILL) || orderInfoVo.getSecondType().equals(OrderConstants.ORDER_SECOND_TYPE_VIDEO)
                || orderInfoVo.getSecondType().equals(OrderConstants.ORDER_SECOND_TYPE_INTEGRAL)) {
            priceResponse.setMerOrderResponseList(computedPriceGetMerOrderList(orderInfoVo, false));
        } else {
            getCouponFee_V1_3(orderInfoVo, user.getId(), user.getIsPaidMember());
            priceResponse.setCouponFee(orderInfoVo.getCouponFee());
            priceResponse.setPlatCouponFee(orderInfoVo.getPlatCouponFee());
            priceResponse.setMerCouponFee(orderInfoVo.getMerCouponFee());
            priceResponse.setMerOrderResponseList(computedPriceGetMerOrderList(orderInfoVo, user.getIsPaidMember()));
            priceResponse.setPlatCouponUserList(computedPriceGetPlatOrderList(orderInfoVo, user.getIsPaidMember()));
        }
        // 积分部分
        BigDecimal payPrice = orderInfoVo.getProTotalFee().subtract(orderInfoVo.getSvipDiscountPrice()).subtract(priceResponse.getCouponFee());
        priceResponse.setIsUseIntegral(request.getIsUseIntegral());
        priceResponse.setProTotalFee(orderInfoVo.getProTotalFee());

        if (orderInfoVo.getType().equals(OrderConstants.ORDER_TYPE_SECKILL) || orderInfoVo.getSecondType().equals(OrderConstants.ORDER_SECOND_TYPE_VIDEO)) {
            priceResponse.setDeductionPrice(BigDecimal.ZERO);
            priceResponse.setSurplusIntegral(user.getIntegral());
            priceResponse.setPayFee(payPrice.add(priceResponse.getFreightFee()));
            priceResponse.setUsedIntegral(0);
            priceResponse.setIntegralDeductionSwitch(false);
            priceResponse.setIsUseIntegral(false);
            return priceResponse;
        }

        if (!request.getIsUseIntegral() || user.getIntegral() <= 0) {// 不使用积分
            priceResponse.setDeductionPrice(BigDecimal.ZERO);
            priceResponse.setSurplusIntegral(user.getIntegral());
            priceResponse.setPayFee(payPrice.add(priceResponse.getFreightFee()));
            priceResponse.setUsedIntegral(0);
            priceResponse.setIntegralDeductionSwitch(false);
            priceResponse.setIsUseIntegral(false);
            String integralDeductionStartMoney = systemConfigService.getValueByKey(SysConfigConstants.CONFIG_KEY_INTEGRAL_DEDUCTION_START_MONEY);
            if ("true".equals(integralDeductionSwitch) && payPrice.compareTo(new BigDecimal(integralDeductionStartMoney)) >= 0) {
                priceResponse.setIntegralDeductionSwitch(true);
            }
            return priceResponse;
        }
        // 使用积分
        if (request.getIsUseIntegral() && user.getIntegral() > 0) {
            String integralDeductionStartMoney = systemConfigService.getValueByKey(SysConfigConstants.CONFIG_KEY_INTEGRAL_DEDUCTION_START_MONEY);
            if (Integer.parseInt(integralDeductionStartMoney) <= 0 || payPrice.compareTo(new BigDecimal(integralDeductionStartMoney)) < 0) {
                priceResponse.setDeductionPrice(BigDecimal.ZERO);
                priceResponse.setSurplusIntegral(user.getIntegral());
                priceResponse.setPayFee(payPrice.add(priceResponse.getFreightFee()));
                priceResponse.setUsedIntegral(0);
                priceResponse.setIntegralDeductionSwitch(false);
                priceResponse.setIsUseIntegral(false);
                return priceResponse;
            }

            // 查询积分使用比例
            String integralRatio = systemConfigService.getValueByKey(SysConfigConstants.CONFIG_KEY_INTEGRAL_DEDUCTION_RATIO);
            // 可抵扣金额
            BigDecimal canDeductionPrice = payPrice.multiply(new BigDecimal(integralRatio)).divide(new BigDecimal("100"), 2, BigDecimal.ROUND_DOWN);
            // 积分转换金额
            String integralDeductionMoney = systemConfigService.getValueByKey(SysConfigConstants.CONFIG_KEY_INTEGRAL_DEDUCTION_MONEY);
            BigDecimal deductionPrice = new BigDecimal(user.getIntegral()).multiply(new BigDecimal(integralDeductionMoney));
            // 积分兑换金额小于实际支付可抵扣金额
            if (deductionPrice.compareTo(canDeductionPrice) <= 0) {
//                payPrice = payPrice.subtract(deductionPrice);
                priceResponse.setSurplusIntegral(0);
                priceResponse.setUsedIntegral(user.getIntegral());
            } else {
                deductionPrice = canDeductionPrice;
                if (canDeductionPrice.compareTo(BigDecimal.ZERO) > 0) {
                    int usedIntegral = canDeductionPrice.divide(new BigDecimal(integralDeductionMoney), 0, BigDecimal.ROUND_UP).intValue();
                    priceResponse.setSurplusIntegral(user.getIntegral() - usedIntegral);
                    priceResponse.setUsedIntegral(usedIntegral);
                }
            }
            payPrice = payPrice.subtract(deductionPrice);
            priceResponse.setPayFee(payPrice.add(priceResponse.getFreightFee()));
            priceResponse.setDeductionPrice(deductionPrice);
            priceResponse.setIsUseIntegral(true);
            priceResponse.setIntegralDeductionSwitch(true);
        }
        return priceResponse;
    }

    private List<CouponUser> computedPriceGetPlatOrderList(PreOrderInfoVo orderInfoVo, Boolean userIsPaidMember) {
        if (orderInfoVo.getType().equals(OrderConstants.ORDER_TYPE_SECKILL) || orderInfoVo.getSecondType().equals(OrderConstants.ORDER_SECOND_TYPE_VIDEO)) {
            return new ArrayList<>();
        }
        Integer userId = userService.getUserId();
        // 获取平台可用优惠券列表
        List<Integer> merIdList = new ArrayList<>();
        List<Integer> proIdList = new ArrayList<>();
        List<Product> productList = new ArrayList<>();
        List<PreOrderInfoDetailVo> orderDetailInfoList = new ArrayList<>();
        for (PreMerchantOrderVo merchantOrderVo : orderInfoVo.getMerchantOrderVoList()) {
            merIdList.add(merchantOrderVo.getMerId());
            merchantOrderVo.getOrderInfoList().forEach(info -> {
                if (!proIdList.contains(info.getProductId())) {
                    Product product = productService.getById(info.getProductId());
                    productList.add(product);
                    proIdList.add(product.getId());
                }
            });
            orderDetailInfoList.addAll(merchantOrderVo.getOrderInfoList());
        }
        BigDecimal proTotalFee = orderInfoVo.getProTotalFee().subtract(orderInfoVo.getSvipDiscountPrice());
        List<Integer> proCategoryIdList = productList.stream().map(Product::getCategoryId).collect(Collectors.toList());
        List<Integer> secondParentIdList = productCategoryService.findParentIdByChildIds(proCategoryIdList);
        List<Integer> firstParentIdList = productCategoryService.findParentIdByChildIds(secondParentIdList);
        proCategoryIdList.addAll(secondParentIdList);
        proCategoryIdList.addAll(firstParentIdList);
        List<Integer> brandIdList = productList.stream().map(Product::getBrandId).collect(Collectors.toList());
        List<CouponUser> platCouponUserList = couponUserService.findManyPlatByUidAndMerIdAndMoneyAndProList(userId, proIdList, proCategoryIdList, merIdList, brandIdList, proTotalFee);

        BigDecimal remainingAmount = proTotalFee.subtract(orderInfoVo.getMerCouponFee());
        for (CouponUser couponUser : platCouponUserList) {
            if (orderInfoVo.getPlatUserCouponId() > 0 && couponUser.getId().equals(orderInfoVo.getPlatUserCouponId())) {
                couponUser.setIsChecked(true);
                couponUser.setIsChoose(true);
            }
            // 判断是否可用
            if (remainingAmount.compareTo(new BigDecimal(couponUser.getMoney().toString())) <= 0) {
                continue;
            }
            if (couponUser.getCategory().equals(CouponConstants.COUPON_CATEGORY_UNIVERSAL)) {
                if (proTotalFee.compareTo(new BigDecimal(couponUser.getMinPrice().toString())) >= 0
                        && remainingAmount.compareTo(new BigDecimal(couponUser.getMoney().toString())) > 0) {
                    couponUser.setIsChoose(true);
                }
                continue;
            }
            Coupon coupon = couponService.getById(couponUser.getCouponId());
            if (couponUser.getCategory().equals(CouponConstants.COUPON_CATEGORY_PRODUCT)) {
                List<Integer> cpIdList = CrmebUtil.stringToArray(coupon.getLinkedData());
                BigDecimal proPrice = orderDetailInfoList.stream().filter(f -> cpIdList.contains(f.getProductId()))
                        .map(e -> {
                            BigDecimal price;
                            if (e.getIsPaidMember() && userIsPaidMember) {
                                price = e.getVipPrice().multiply(new BigDecimal(e.getPayNum()));
                            } else {
                                price = e.getPrice().multiply(new BigDecimal(e.getPayNum()));
                            }
                            return price;
                        }).reduce(BigDecimal.ZERO, BigDecimal::add);
                BigDecimal proSubPrice = orderDetailInfoList.stream().filter(f -> cpIdList.contains(f.getProductId()))
                        .map(e -> {
                            BigDecimal price;
                            if (e.getIsPaidMember() && userIsPaidMember) {
                                price = e.getVipPrice().multiply(new BigDecimal(e.getPayNum()));
                            } else {
                                price = e.getPrice().multiply(new BigDecimal(e.getPayNum()));
                            }
                            return price.subtract(e.getMerCouponPrice());
                        }).reduce(BigDecimal.ZERO, BigDecimal::add);
                if (proPrice.compareTo(new BigDecimal(couponUser.getMinPrice().toString())) >= 0
                        && proSubPrice.compareTo(new BigDecimal(couponUser.getMoney().toString())) > 0) {
                    couponUser.setIsChoose(true);
                }
                continue;
            }
            if (couponUser.getCategory().equals(CouponConstants.COUPON_CATEGORY_PRODUCT_CATEGORY)) {
                List<Integer> cidList = new ArrayList<>();
                Integer categoryId = Integer.valueOf(coupon.getLinkedData());
                ProductCategory category = productCategoryService.getById(categoryId);
                if (category.getLevel().equals(3)) {
                    cidList.add(categoryId);
                } else {
                    List<ProductCategory> productCategoryList = productCategoryService.findAllChildListByPid(category.getId(), category.getLevel());
                    if (category.getLevel().equals(1)) {
                        productCategoryList = productCategoryList.stream().filter(f -> f.getLevel().equals(3)).collect(Collectors.toList());
                    }
                    cidList.addAll(productCategoryList.stream().map(ProductCategory::getId).collect(Collectors.toList()));
                }
                List<Integer> pIdList = productList.stream().filter(f -> cidList.contains(f.getCategoryId())).map(Product::getId).collect(Collectors.toList());
                BigDecimal proPrice = orderDetailInfoList.stream().filter(f -> pIdList.contains(f.getProductId()))
                        .map(e -> {
                            BigDecimal price;
                            if (e.getIsPaidMember() && userIsPaidMember) {
                                price = e.getVipPrice().multiply(new BigDecimal(e.getPayNum()));
                            } else {
                                price = e.getPrice().multiply(new BigDecimal(e.getPayNum()));
                            }
                            return price;
                        }).reduce(BigDecimal.ZERO, BigDecimal::add);
                BigDecimal proSubPrice = orderDetailInfoList.stream().filter(f -> pIdList.contains(f.getProductId()))
                        .map(e -> {
                            BigDecimal price;
                            if (e.getIsPaidMember() && userIsPaidMember) {
                                price = e.getVipPrice().multiply(new BigDecimal(e.getPayNum()));
                            } else {
                                price = e.getPrice().multiply(new BigDecimal(e.getPayNum()));
                            }
                            return price.subtract(e.getMerCouponPrice());
                        }).reduce(BigDecimal.ZERO, BigDecimal::add);
                if (proPrice.compareTo(new BigDecimal(couponUser.getMinPrice().toString())) >= 0
                        && proSubPrice.compareTo(new BigDecimal(couponUser.getMoney().toString())) > 0) {
                    couponUser.setIsChoose(true);
                }
                continue;
            }
            if (couponUser.getCategory().equals(CouponConstants.COUPON_CATEGORY_BRAND)) {
                Integer brandId = Integer.valueOf(coupon.getLinkedData());
                List<Integer> pIdList = productList.stream().filter(f -> brandId.equals(f.getBrandId())).map(Product::getId).collect(Collectors.toList());
                BigDecimal proPrice = orderDetailInfoList.stream().filter(f -> pIdList.contains(f.getProductId()))
                        .map(e -> {
                            BigDecimal price;
                            if (e.getIsPaidMember() && userIsPaidMember) {
                                price = e.getVipPrice().multiply(new BigDecimal(e.getPayNum()));
                            } else {
                                price = e.getPrice().multiply(new BigDecimal(e.getPayNum()));
                            }
                            return price;
                        }).reduce(BigDecimal.ZERO, BigDecimal::add);
                BigDecimal proSubPrice = orderDetailInfoList.stream().filter(f -> pIdList.contains(f.getProductId()))
                        .map(e -> {
                            BigDecimal price;
                            if (e.getIsPaidMember() && userIsPaidMember) {
                                price = e.getVipPrice().multiply(new BigDecimal(e.getPayNum()));
                            } else {
                                price = e.getPrice().multiply(new BigDecimal(e.getPayNum()));
                            }
                            return price.subtract(e.getMerCouponPrice());
                        }).reduce(BigDecimal.ZERO, BigDecimal::add);
                if (proPrice.compareTo(new BigDecimal(couponUser.getMinPrice().toString())) >= 0
                        && proSubPrice.compareTo(new BigDecimal(couponUser.getMoney().toString())) > 0) {
                    couponUser.setIsChoose(true);
                }
                continue;
            }
            if (couponUser.getCategory().equals(CouponConstants.COUPON_CATEGORY_JOINT_MERCHANT)) {
                List<Integer> mpIdList = CrmebUtil.stringToArray(coupon.getLinkedData());
                List<Integer> pIdList = productList.stream().filter(f -> mpIdList.contains(f.getMerId())).map(Product::getId).collect(Collectors.toList());
                BigDecimal proPrice = orderDetailInfoList.stream().filter(f -> pIdList.contains(f.getProductId()))
                        .map(e -> {
                            BigDecimal price;
                            if (e.getIsPaidMember() && userIsPaidMember) {
                                price = e.getVipPrice().multiply(new BigDecimal(e.getPayNum()));
                            } else {
                                price = e.getPrice().multiply(new BigDecimal(e.getPayNum()));
                            }
                            return price;
                        }).reduce(BigDecimal.ZERO, BigDecimal::add);
                BigDecimal proSubPrice = orderDetailInfoList.stream().filter(f -> pIdList.contains(f.getProductId()))
                        .map(e -> {
                            BigDecimal price;
                            if (e.getIsPaidMember() && userIsPaidMember) {
                                price = e.getVipPrice().multiply(new BigDecimal(e.getPayNum()));
                            } else {
                                price = e.getPrice().multiply(new BigDecimal(e.getPayNum()));
                            }
                            return price.subtract(e.getMerCouponPrice());
                        }).reduce(BigDecimal.ZERO, BigDecimal::add);
                if (proPrice.compareTo(new BigDecimal(couponUser.getMinPrice().toString())) >= 0
                        && proSubPrice.compareTo(new BigDecimal(couponUser.getMoney().toString())) > 0) {
                    couponUser.setIsChoose(true);
                }
            }
        }
        return platCouponUserList;
    }

    private List<ComputedMerchantOrderResponse> computedPriceGetMerOrderList(PreOrderInfoVo orderInfoVo, Boolean userIsPaidMember) {
        Integer userId = userService.getUserId();
        List<ComputedMerchantOrderResponse> merOrderResponseList = orderInfoVo.getMerchantOrderVoList().stream().map(vo -> {
            ComputedMerchantOrderResponse merOrderResponse = new ComputedMerchantOrderResponse();
            merOrderResponse.setMerId(vo.getMerId());
            merOrderResponse.setFreightFee(vo.getFreightFee());
            if (orderInfoVo.getType().equals(OrderConstants.ORDER_TYPE_SECKILL) || orderInfoVo.getSecondType().equals(OrderConstants.ORDER_SECOND_TYPE_VIDEO)
                    || orderInfoVo.getSecondType().equals(OrderConstants.ORDER_SECOND_TYPE_INTEGRAL)) {
                merOrderResponse.setUserCouponId(0);
                merOrderResponse.setCouponFee(BigDecimal.ZERO);
                return merOrderResponse;
            }
            merOrderResponse.setUserCouponId(vo.getUserCouponId());
            merOrderResponse.setCouponFee(vo.getMerCouponFee());
            merOrderResponse.setSvipDiscountPrice(vo.getSvipDiscountPrice());
            // 查询适用的用户优惠券
            List<Integer> merProIdList = vo.getOrderInfoList().stream().map(PreOrderInfoDetailVo::getProductId).distinct().collect(Collectors.toList());
            BigDecimal merPrice = vo.getProTotalFee().subtract(vo.getSvipDiscountPrice());
            BigDecimal merRemainingAmount = merPrice.subtract(orderInfoVo.getPlatCouponFee());
            List<CouponUser> merCouponUserList = couponUserService.findManyByUidAndMerIdAndMoneyAndProList(userId, vo.getMerId(), merProIdList, merPrice);
            for (int i = 0; i < merCouponUserList.size(); ) {
                CouponUser couponUser = merCouponUserList.get(i);
                if (vo.getUserCouponId() > 0 && couponUser.getId().equals(vo.getUserCouponId())) {
                    couponUser.setIsChecked(true);
                    couponUser.setIsChoose(true);
                    i++;
                    continue;
                }
                if (couponUser.getCategory().equals(CouponConstants.COUPON_CATEGORY_MERCHANT)) {
                    if (merRemainingAmount.compareTo(new BigDecimal(couponUser.getMoney().toString())) > 0) {
                        couponUser.setIsChoose(true);
                    }
                    i++;
                    continue;
                }
                Coupon coupon = couponService.getById(couponUser.getCouponId());
                List<Integer> cpIdList = CrmebUtil.stringToArray(coupon.getLinkedData());
                List<PreOrderInfoDetailVo> detailVoList = vo.getOrderInfoList().stream()
                        .filter(f -> cpIdList.contains(f.getProductId())).collect(Collectors.toList());
                BigDecimal proPrice = detailVoList.stream()
                        .map(e -> {
                            BigDecimal price;
                            if (e.getIsPaidMember() && userIsPaidMember) {
                                price = e.getVipPrice().multiply(new BigDecimal(e.getPayNum()));
                            } else {
                                price = e.getPrice().multiply(new BigDecimal(e.getPayNum()));
                            }
                            return price;
                        }).reduce(BigDecimal.ZERO, BigDecimal::add);
                BigDecimal proRemainingAmount = detailVoList.stream()
                        .map(e -> {
                            BigDecimal price;
                            if (e.getIsPaidMember() && userIsPaidMember) {
                                price = e.getVipPrice().multiply(new BigDecimal(e.getPayNum()));
                            } else {
                                price = e.getPrice().multiply(new BigDecimal(e.getPayNum()));
                            }
                            return price.subtract(e.getPlatCouponPrice());
                        }).reduce(BigDecimal.ZERO, BigDecimal::add);
                if (proPrice.compareTo(new BigDecimal(couponUser.getMinPrice().toString())) < 0) {
                    merCouponUserList.remove(i);
                    continue;
                }
                if (proPrice.compareTo(new BigDecimal(couponUser.getMoney().toString())) <= 0) {
                    merCouponUserList.remove(i);
                    continue;
                }
                if (proPrice.compareTo(new BigDecimal(couponUser.getMinPrice().toString())) >= 0) {
                    if (proRemainingAmount.compareTo(new BigDecimal(couponUser.getMoney().toString())) > 0) {
                        couponUser.setIsChoose(true);
                    }
                }
                i++;
            }
            merOrderResponse.setMerCouponUserList(merCouponUserList);
            return merOrderResponse;
        }).collect(Collectors.toList());
        return merOrderResponseList;
    }

    /**
     * 视频号下单校验
     * 暂时只支持都买一种商品
     *
     * @param detailRequest 请求参数
     * @return 预下单结果
     */
    private PreMerchantOrderVo validatePreOrderVideo(PreOrderDetailRequest detailRequest) {
        if (ObjectUtil.isNull(detailRequest.getProductId())) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "商品编号不能为空");
        }
        if (ObjectUtil.isNull(detailRequest.getAttrValueId())) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "商品规格属性值不能为空");
        }
        if (ObjectUtil.isNull(detailRequest.getProductNum()) || detailRequest.getProductNum() <= 0) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "购买数量必须大于0");
        }
        // 查询商品信息
        PayComponentProduct product = payComponentProductService.getById(detailRequest.getProductId());
        if (ObjectUtil.isNull(product)) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "商品信息不存在，请刷新后重新选择");
        }
        if (product.getIsDel()) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "商品已删除，请刷新后重新选择");
        }
        if (!product.getStatus().equals(5)) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "商品已下架，请刷新后重新选择");
        }
        if (product.getStock() < detailRequest.getProductNum()) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "商品库存不足，请刷新后重新选择");
        }
        // 查询商品规格属性值信息
        ProductAttrValue attrValue = productAttrValueService.getByIdAndProductIdAndType(detailRequest.getAttrValueId(), detailRequest.getProductId(), ProductConstants.PRODUCT_TYPE_COMPONENT);
        if (ObjectUtil.isNull(attrValue)) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "商品规格信息不存在，请刷新后重新选择");
        }
        if (attrValue.getStock() < detailRequest.getProductNum()) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "商品规格库存不足，请刷新后重新选择");
        }
        Merchant merchant = merchantService.getByIdException(product.getMerId());
        if (!merchant.getIsSwitch()) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "商户已关闭，请重新选择商品");
        }
        PreMerchantOrderVo merchantOrderVo = new PreMerchantOrderVo();
        merchantOrderVo.setMerId(merchant.getId());
        merchantOrderVo.setMerName(merchant.getName());
        merchantOrderVo.setFreightFee(BigDecimal.ZERO);
        merchantOrderVo.setCouponFee(BigDecimal.ZERO);
        merchantOrderVo.setUserCouponId(0);
        merchantOrderVo.setTakeTheirSwitch(merchant.getIsTakeTheir());
        merchantOrderVo.setIsSelf(merchant.getIsSelf());
        PreOrderInfoDetailVo detailVo = new PreOrderInfoDetailVo();
        detailVo.setProductId(product.getId());
        detailVo.setProductName(product.getTitle());
        detailVo.setAttrValueId(attrValue.getId());
        detailVo.setSku(attrValue.getSku());
        detailVo.setPrice(attrValue.getPrice());
        detailVo.setPayPrice(attrValue.getPrice());
        detailVo.setPayNum(detailRequest.getProductNum());
        detailVo.setImage(StrUtil.isNotBlank(attrValue.getImage()) ? attrValue.getImage() : product.getHeadImg());
        detailVo.setVolume(attrValue.getVolume());
        detailVo.setWeight(attrValue.getWeight());
        detailVo.setTempId(product.getTempId());
        detailVo.setSubBrokerageType(product.getIsSub() ? 1 : 2);
        detailVo.setBrokerage(attrValue.getBrokerage());
        detailVo.setBrokerageTwo(attrValue.getBrokerageTwo());
//        PreOrderInfoDetailVo detailVo = new PreOrderInfoDetailVo();
//        detailVo.setProductId(product.getId());
//        detailVo.setProductName(product.getTitle());
//        detailVo.setAttrValueId(attrValue.getId());
//        detailVo.setSku(attrValue.getSku());
//        detailVo.setPrice(attrValue.getPrice());
//        detailVo.setPayNum(detailRequest.getProductNum());
//        detailVo.setImage(attrValue.getImage());
//        detailVo.setVolume(attrValue.getVolume());
//        detailVo.setWeight(attrValue.getWeight());
//        detailVo.setTempId(product.getTempId());
////        detailVo.setIsSub(product.getIsSub());
//        detailVo.setProductType(ProductConstants.PRODUCT_TYPE_COMPONENT);
        List<PreOrderInfoDetailVo> infoList = CollUtil.newArrayList();
        infoList.add(detailVo);
        merchantOrderVo.setOrderInfoList(infoList);
        return merchantOrderVo;
    }

    /**
     * 获取优惠金额
     */
    private void getCouponFee_V1_3(PreOrderInfoVo orderInfoVo, Integer uid, Boolean userIsPaidMember) {
        if (orderInfoVo.getType().equals(OrderConstants.ORDER_TYPE_SECKILL)) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "营销活动商品无法使用优惠券");
        }
        if (orderInfoVo.getSecondType().equals(OrderConstants.ORDER_SECOND_TYPE_VIDEO)) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "视频号商品无法使用优惠券");
        }
        orderInfoVo.getMerchantOrderVoList().forEach(merchantOrder -> {
            merchantOrder.setCouponFee(BigDecimal.ZERO);
            merchantOrder.setMerCouponFee(BigDecimal.ZERO);
            merchantOrder.getOrderInfoList().forEach(info -> {
                info.setMerCouponPrice(BigDecimal.ZERO);
                info.setPlatCouponPrice(BigDecimal.ZERO);
            });
        });
        List<PreMerchantOrderVo> merchantOrderVoList = orderInfoVo.getMerchantOrderVoList();
        long count = merchantOrderVoList.stream().filter(e -> e.getUserCouponId() > 0).count();
        if (count <= 0 && orderInfoVo.getPlatUserCouponId() <= 0) {
            orderInfoVo.setCouponFee(BigDecimal.ZERO);
            return;
        }
        BigDecimal couponFee = BigDecimal.ZERO;
        Date date = CrmebDateUtil.nowDateTime();
        List<Integer> proIdList = new ArrayList<>();
        for (PreMerchantOrderVo merchantOrderVo : merchantOrderVoList) {
            List<Integer> productIdList = merchantOrderVo.getOrderInfoList().stream().map(PreOrderInfoDetailVo::getProductId).collect(Collectors.toList());
            proIdList.addAll(productIdList);
            if (merchantOrderVo.getUserCouponId() <= 0) {
                merchantOrderVo.setCouponFee(BigDecimal.ZERO);
                merchantOrderVo.setMerCouponFee(BigDecimal.ZERO);
                continue;
            }
            BigDecimal merchantCouponFee = BigDecimal.ZERO;
            // 判断优惠券是否可以使用
            CouponUser couponUser = couponUserService.getById(merchantOrderVo.getUserCouponId());
            if (ObjectUtil.isNull(couponUser) || !couponUser.getUid().equals(uid)) {
                throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "优惠券领取记录不存在！");
            }
            if (!couponUser.getMerId().equals(merchantOrderVo.getMerId())) {
                throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "商家无此优惠券");
            }
            if (CouponConstants.STORE_COUPON_USER_STATUS_USED.equals(couponUser.getStatus())) {
                throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "此优惠券已使用！");
            }
            if (CouponConstants.STORE_COUPON_USER_STATUS_LAPSED.equals(couponUser.getStatus())) {
                throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "此优惠券已失效！");
            }
            //判断是否在使用时间内
            if (couponUser.getStartTime().compareTo(date) > 0) {
                throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "此优惠券还未到达使用时间范围之内！");
            }
            if (date.compareTo(couponUser.getEndTime()) > 0) {
                throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "此优惠券已经失效了");
            }
            BigDecimal subProTotalFee = merchantOrderVo.getProTotalFee().subtract(merchantOrderVo.getSvipDiscountPrice());
            if (new BigDecimal(couponUser.getMinPrice().toString()).compareTo(subProTotalFee) > 0) {
//                throw new CrmebException("总金额小于优惠券最小使用金额");
                throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "请合理搭配优惠组合，当前优惠券不满足使用条件");
            }

            //检测优惠券信息
            if (couponUser.getCategory().equals(CouponConstants.COUPON_CATEGORY_PRODUCT)) {
                // 商品券
                //设置优惠券所提供的集合
                List<CouponProduct> couponProductList = couponProductService.findByCid(couponUser.getCouponId());
                List<Integer> primaryKeyIdList = couponProductList.stream().map(CouponProduct::getPid).collect(Collectors.toList());
                //取两个集合的交集，如果是false则证明没有相同的值
                //oldList.retainAll(newList)返回值代表oldList是否保持原样，如果old和new完全相同，那old保持原样并返回false。
                //交集：listA.retainAll(listB) ——listA内容变为listA和listB都存在的对象；listB不变
                primaryKeyIdList.retainAll(productIdList);
                if (CollUtil.isEmpty(primaryKeyIdList)) {
                    throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "此券为商品券，请在购买相关产品后使用！");
                }
                List<PreOrderInfoDetailVo> infoDetailVoList = merchantOrderVo.getOrderInfoList().stream().filter(info -> primaryKeyIdList.contains(info.getProductId())).collect(Collectors.toList());
                if (CollUtil.isEmpty(infoDetailVoList)) {
                    throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "此券为商品券，请在购买相关产品后使用！");
                }
                BigDecimal proTotalPrice = infoDetailVoList.stream()
                        .map(e -> {
                            BigDecimal price;
                            if (e.getIsPaidMember() && userIsPaidMember) {
                                price = e.getVipPrice().multiply(new BigDecimal(e.getPayNum()));
                            } else {
                                price = e.getPrice().multiply(new BigDecimal(e.getPayNum()));
                            }
                            return price;
                        }).reduce(BigDecimal.ZERO, BigDecimal::add);
                if (new BigDecimal(couponUser.getMinPrice().toString()).compareTo(proTotalPrice) > 0) {
//                    throw new CrmebException("总金额小于优惠券最低使用金额");
                    throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "请合理搭配优惠组合，当前优惠券不满足使用条件");
                }
                if (proTotalPrice.compareTo(new BigDecimal(couponUser.getMoney().toString())) <= 0) {
                    throw new CrmebException(CommonResultCode.VALIDATE_FAILED, StrUtil.format("{}优惠券不适用，请重新选择优惠券", couponUser.getName()));
                }
                merchantCouponFee = merchantCouponFee.add(new BigDecimal(couponUser.getMoney().toString()));
                BigDecimal couponPrice = merchantCouponFee;
                if (infoDetailVoList.size() == 1) {
                    infoDetailVoList.get(0).setMerCouponPrice(couponPrice);
                } else {
                    for (int i = 0; i < infoDetailVoList.size(); i++) {
                        PreOrderInfoDetailVo detailVo = infoDetailVoList.get(i);
                        if (infoDetailVoList.size() == (i + 1)) {
                            detailVo.setMerCouponPrice(couponPrice);
                            break;
                        }
                        BigDecimal detailPrice;
                        if (detailVo.getIsPaidMember() && userIsPaidMember) {
                            detailPrice = detailVo.getVipPrice().multiply(new BigDecimal(detailVo.getPayNum()));
                        } else {
                            detailPrice = detailVo.getPrice().multiply(new BigDecimal(detailVo.getPayNum()));
                        }
                        BigDecimal detailCouponFee = couponPrice.multiply(detailPrice).divide(proTotalPrice, 2, RoundingMode.HALF_UP);
                        if (detailCouponFee.compareTo(detailPrice) > 0) {
                            detailCouponFee = detailPrice;
                        }
                        couponPrice = couponPrice.subtract(detailCouponFee);
                        proTotalPrice = proTotalPrice.subtract(detailPrice);
                        detailVo.setMerCouponPrice(detailCouponFee);
                    }
                }
            }
            if (couponUser.getCategory().equals(CouponConstants.COUPON_CATEGORY_MERCHANT)) {
                // 商家券
                List<PreOrderInfoDetailVo> infoDetailVoList = merchantOrderVo.getOrderInfoList();
                BigDecimal proTotalPrice = infoDetailVoList.stream()
                        .map(e -> {
                            BigDecimal price;
                            if (e.getIsPaidMember() && userIsPaidMember) {
                                price = e.getVipPrice().multiply(new BigDecimal(e.getPayNum()));
                            } else {
                                price = e.getPrice().multiply(new BigDecimal(e.getPayNum()));
                            }
                            return price;
                        }).reduce(BigDecimal.ZERO, BigDecimal::add);
                if (new BigDecimal(couponUser.getMinPrice().toString()).compareTo(proTotalPrice) > 0) {
//                    throw new CrmebException("总金额小于优惠券最低使用金额");
                    throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "请合理搭配优惠组合，当前优惠券不满足使用条件");
                }
                if (proTotalPrice.compareTo(new BigDecimal(couponUser.getMoney().toString())) <= 0) {
                    throw new CrmebException(CommonResultCode.VALIDATE_FAILED, StrUtil.format("{}优惠券不适用，请重新选择优惠券", couponUser.getName()));
                }
                merchantCouponFee = merchantCouponFee.add(new BigDecimal(couponUser.getMoney().toString()));
                BigDecimal couponPrice = merchantCouponFee;
                if (infoDetailVoList.size() == 1) {
                    infoDetailVoList.get(0).setMerCouponPrice(couponPrice);
                } else {
                    for (int i = 0; i < infoDetailVoList.size(); i++) {
                        PreOrderInfoDetailVo detailVo = infoDetailVoList.get(i);
                        if (infoDetailVoList.size() == (i + 1)) {
                            detailVo.setMerCouponPrice(couponPrice);
                            break;
                        }
                        BigDecimal detailPrice;
                        if (detailVo.getIsPaidMember() && userIsPaidMember) {
                            detailPrice = detailVo.getVipPrice().multiply(new BigDecimal(detailVo.getPayNum()));
                        } else {
                            detailPrice = detailVo.getPrice().multiply(new BigDecimal(detailVo.getPayNum()));
                        }
                        BigDecimal detailCouponFee = couponPrice.multiply(detailPrice).divide(proTotalPrice, 2, RoundingMode.HALF_UP);
                        if (detailCouponFee.compareTo(detailPrice) > 0) {
                            detailCouponFee = detailPrice;
                        }
                        couponPrice = couponPrice.subtract(detailCouponFee);
                        proTotalPrice = proTotalPrice.subtract(detailPrice);
                        detailVo.setMerCouponPrice(detailCouponFee);
                    }
                }
            }
            couponFee = couponFee.add(merchantCouponFee);
            merchantOrderVo.setCouponFee(merchantCouponFee);
            merchantOrderVo.setMerCouponFee(merchantCouponFee);
        }
        orderInfoVo.setMerCouponFee(couponFee);
        // 计算平台优惠券
        if (orderInfoVo.getPlatUserCouponId() > 0) {

            CouponUser couponUser = couponUserService.getById(orderInfoVo.getPlatUserCouponId());
            if (ObjectUtil.isNull(couponUser) || !couponUser.getUid().equals(uid)) {
                throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "优惠券领取记录不存在！");
            }
            if (!couponUser.getMerId().equals(0)) {
                throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "平台无此优惠券");
            }
            if (CouponConstants.STORE_COUPON_USER_STATUS_USED.equals(couponUser.getStatus())) {
                throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "此优惠券已使用！");
            }
            if (CouponConstants.STORE_COUPON_USER_STATUS_LAPSED.equals(couponUser.getStatus())) {
                throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "此优惠券已失效！");
            }
            //判断是否在使用时间内
            if (couponUser.getStartTime().compareTo(date) > 0) {
                throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "此优惠券还未到达使用时间范围之内！");
            }
            if (date.compareTo(couponUser.getEndTime()) > 0) {
                throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "此优惠券已经失效了");
            }

            Coupon coupon = couponService.getById(couponUser.getCouponId());
            List<PreOrderInfoDetailVo> orderInfoList = new ArrayList<>();
            merchantOrderVoList.forEach(m -> {
                orderInfoList.addAll(m.getOrderInfoList());
            });
            BigDecimal platCouponPrice = new BigDecimal(couponUser.getMoney().toString());
            if (couponUser.getCategory().equals(CouponConstants.COUPON_CATEGORY_UNIVERSAL)) {
                BigDecimal subProTotalFee = orderInfoVo.getProTotalFee().subtract(orderInfoVo.getSvipDiscountPrice());
                BigDecimal remainingAmount = subProTotalFee.subtract(orderInfoVo.getMerCouponFee());
                if (subProTotalFee.compareTo(new BigDecimal(couponUser.getMinPrice().toString())) < 0) {
//                    throw new CrmebException("总金额小于优惠券最小使用金额");
                    throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "请合理搭配优惠组合，当前优惠券不满足使用条件");
                }
                if (remainingAmount.compareTo(new BigDecimal(couponUser.getMoney().toString())) <= 0) {
                    throw new CrmebException(CommonResultCode.VALIDATE_FAILED, StrUtil.format("{}优惠券不适用，请重新选择优惠券", couponUser.getName()));
                }
                BigDecimal proTotalPrice = orderInfoList.stream()
                        .map(e -> {
                            BigDecimal price;
                            if (e.getIsPaidMember() && userIsPaidMember) {
                                price = e.getVipPrice().multiply(new BigDecimal(e.getPayNum()));
                            } else {
                                price = e.getPrice().multiply(new BigDecimal(e.getPayNum()));
                            }
                            return price;
                        }).reduce(BigDecimal.ZERO, BigDecimal::add);
                for (int i = 0; i < orderInfoList.size(); i++) {
                    PreOrderInfoDetailVo detailVo = orderInfoList.get(i);
                    if (orderInfoList.size() == (i + 1)) {
                        detailVo.setPlatCouponPrice(platCouponPrice);
                        detailVo.setCouponPrice(detailVo.getMerCouponPrice().add(detailVo.getPlatCouponPrice()));
                        break;
                    }
                    BigDecimal detailPrice;
                    if (detailVo.getIsPaidMember() && userIsPaidMember) {
                        detailPrice = detailVo.getVipPrice().multiply(new BigDecimal(detailVo.getPayNum()));
                    } else {
                        detailPrice = detailVo.getPrice().multiply(new BigDecimal(detailVo.getPayNum()));
                    }
                    BigDecimal detailCouponFee = platCouponPrice.multiply(detailPrice).divide(proTotalPrice, 2, RoundingMode.HALF_UP);
                    if (detailCouponFee.compareTo(detailPrice) > 0) {
                        detailCouponFee = detailPrice;
                    }
                    platCouponPrice = platCouponPrice.subtract(detailCouponFee);
                    proTotalPrice = proTotalPrice.subtract(detailPrice);
                    detailVo.setPlatCouponPrice(detailCouponFee);
                    detailVo.setCouponPrice(detailVo.getMerCouponPrice().add(detailVo.getPlatCouponPrice()));
                }
            }
            if (couponUser.getCategory().equals(CouponConstants.COUPON_CATEGORY_PRODUCT)) {
                List<Integer> cpIdList = CrmebUtil.stringToArray(coupon.getLinkedData());
                // 平台优惠券计算金额 = （商品金额 - 商户优惠金额） * 购买数量
                List<PreOrderInfoDetailVo> infoDetailList = orderInfoList.stream().filter(f -> cpIdList.contains(f.getProductId())).collect(Collectors.toList());
                BigDecimal proTotalPrice = infoDetailList.stream()
                        .map(e -> {
                            BigDecimal price;
                            if (e.getIsPaidMember() && userIsPaidMember) {
                                price = e.getVipPrice().multiply(new BigDecimal(e.getPayNum()));
                            } else {
                                price = e.getPrice().multiply(new BigDecimal(e.getPayNum()));
                            }
                            return price;
                        }).reduce(BigDecimal.ZERO, BigDecimal::add);
                BigDecimal proPrice = infoDetailList.stream()
                        .map(e -> {
                            BigDecimal price;
                            if (e.getIsPaidMember() && userIsPaidMember) {
                                price = e.getVipPrice().multiply(new BigDecimal(e.getPayNum()));
                            } else {
                                price = e.getPrice().multiply(new BigDecimal(e.getPayNum()));
                            }
                            return price.subtract(e.getMerCouponPrice());
                        }).reduce(BigDecimal.ZERO, BigDecimal::add);
                if (proTotalPrice.compareTo(new BigDecimal(couponUser.getMinPrice().toString())) < 0) {
//                    throw new CrmebException("总金额小于优惠券最小使用金额");
                    throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "请合理搭配优惠组合，当前优惠券不满足使用条件");
                }
                if (proPrice.compareTo(new BigDecimal(couponUser.getMoney().toString())) <= 0) {
                    throw new CrmebException(CommonResultCode.VALIDATE_FAILED, StrUtil.format("{}优惠券不适用，请重新选择优惠券", couponUser.getName()));
                }
                for (int i = 0; i < infoDetailList.size(); i++) {
                    PreOrderInfoDetailVo detailVo = infoDetailList.get(i);
                    if (orderInfoList.size() == (i + 1)) {
                        detailVo.setPlatCouponPrice(platCouponPrice);
                        detailVo.setCouponPrice(detailVo.getMerCouponPrice().add(detailVo.getPlatCouponPrice()));
                        break;
                    }
                    BigDecimal detailPrice;
                    if (detailVo.getIsPaidMember() && userIsPaidMember) {
                        detailPrice = detailVo.getVipPrice().multiply(new BigDecimal(detailVo.getPayNum()));
                    } else {
                        detailPrice = detailVo.getPrice().multiply(new BigDecimal(detailVo.getPayNum()));
                    }
                    BigDecimal detailCouponFee = platCouponPrice.multiply(detailPrice).divide(proTotalPrice, 2, RoundingMode.HALF_UP);
                    if (detailCouponFee.compareTo(detailPrice) > 0) {
                        detailCouponFee = detailPrice;
                    }
                    platCouponPrice = platCouponPrice.subtract(detailCouponFee);
                    proTotalPrice = proTotalPrice.subtract(detailPrice);
                    detailVo.setPlatCouponPrice(detailCouponFee);
                    detailVo.setCouponPrice(detailVo.getMerCouponPrice().add(detailVo.getPlatCouponPrice()));
                }
            }
            proIdList = proIdList.stream().distinct().collect(Collectors.toList());
            if (couponUser.getCategory().equals(CouponConstants.COUPON_CATEGORY_PRODUCT_CATEGORY)) {
                List<Integer> cidList = new ArrayList<>();
                Integer categoryId = Integer.valueOf(coupon.getLinkedData());
                ProductCategory category = productCategoryService.getById(categoryId);
                if (category.getLevel().equals(3)) {
                    cidList.add(categoryId);
                } else {
                    List<ProductCategory> productCategoryList = productCategoryService.findAllChildListByPid(category.getId(), category.getLevel());
                    if (category.getLevel().equals(1)) {
                        productCategoryList = productCategoryList.stream().filter(f -> f.getLevel().equals(3)).collect(Collectors.toList());
                    }
                    cidList.addAll(productCategoryList.stream().map(ProductCategory::getId).collect(Collectors.toList()));
                }
                List<Product> productList = productService.findByIds(proIdList);
                List<Integer> pIdList = productList.stream().filter(f -> cidList.contains(f.getCategoryId())).map(Product::getId).collect(Collectors.toList());
                List<PreOrderInfoDetailVo> infoDetailList = orderInfoList.stream().filter(f -> pIdList.contains(f.getProductId())).collect(Collectors.toList());
                BigDecimal proTotalPrice = infoDetailList.stream()
                        .map(e -> {
                            BigDecimal price;
                            if (e.getIsPaidMember() && userIsPaidMember) {
                                price = e.getVipPrice().multiply(new BigDecimal(e.getPayNum()));
                            } else {
                                price = e.getPrice().multiply(new BigDecimal(e.getPayNum()));
                            }
                            return price;
                        }).reduce(BigDecimal.ZERO, BigDecimal::add);
                BigDecimal proPrice = infoDetailList.stream()
                        .map(e -> {
                            BigDecimal price;
                            if (e.getIsPaidMember() && userIsPaidMember) {
                                price = e.getVipPrice().multiply(new BigDecimal(e.getPayNum()));
                            } else {
                                price = e.getPrice().multiply(new BigDecimal(e.getPayNum()));
                            }
                            return price.subtract(e.getMerCouponPrice());
                        }).reduce(BigDecimal.ZERO, BigDecimal::add);
                if (proTotalPrice.compareTo(new BigDecimal(couponUser.getMinPrice().toString())) < 0) {
//                    throw new CrmebException("总金额小于优惠券最小使用金额");
                    throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "请合理搭配优惠组合，当前优惠券不满足使用条件");
                }
                if (proPrice.compareTo(new BigDecimal(couponUser.getMoney().toString())) <= 0) {
                    throw new CrmebException(CommonResultCode.VALIDATE_FAILED, StrUtil.format("{}优惠券不适用，请重新选择优惠券", couponUser.getName()));
                }
                for (int i = 0; i < infoDetailList.size(); i++) {
                    PreOrderInfoDetailVo detailVo = infoDetailList.get(i);
                    if (orderInfoList.size() == (i + 1)) {
                        detailVo.setPlatCouponPrice(platCouponPrice);
                        detailVo.setCouponPrice(detailVo.getMerCouponPrice().add(detailVo.getPlatCouponPrice()));
                        break;
                    }
                    BigDecimal detailPrice;
                    if (detailVo.getIsPaidMember() && userIsPaidMember) {
                        detailPrice = detailVo.getVipPrice().multiply(new BigDecimal(detailVo.getPayNum()));
                    } else {
                        detailPrice = detailVo.getPrice().multiply(new BigDecimal(detailVo.getPayNum()));
                    }
                    BigDecimal detailCouponFee = platCouponPrice.multiply(detailPrice).divide(proTotalPrice, 2, RoundingMode.HALF_UP);
                    if (detailCouponFee.compareTo(detailPrice) > 0) {
                        detailCouponFee = detailPrice;
                    }
                    platCouponPrice = platCouponPrice.subtract(detailCouponFee);
                    proTotalPrice = proTotalPrice.subtract(detailPrice);
                    detailVo.setPlatCouponPrice(detailCouponFee);
                    detailVo.setCouponPrice(detailVo.getMerCouponPrice().add(detailVo.getPlatCouponPrice()));
                }
            }
            if (couponUser.getCategory().equals(CouponConstants.COUPON_CATEGORY_BRAND)) {
                Integer brandId = Integer.valueOf(coupon.getLinkedData());
                List<Product> productList = productService.findByIds(proIdList);
                List<Integer> pIdList = productList.stream().filter(f -> brandId.equals(f.getBrandId())).map(Product::getId).collect(Collectors.toList());
                List<PreOrderInfoDetailVo> infoDetailList = orderInfoList.stream().filter(f -> pIdList.contains(f.getProductId())).collect(Collectors.toList());
                BigDecimal proTotalPrice = infoDetailList.stream()
                        .map(e -> {
                            BigDecimal price;
                            if (e.getIsPaidMember() && userIsPaidMember) {
                                price = e.getVipPrice().multiply(new BigDecimal(e.getPayNum()));
                            } else {
                                price = e.getPrice().multiply(new BigDecimal(e.getPayNum()));
                            }
                            return price;
                        }).reduce(BigDecimal.ZERO, BigDecimal::add);
                BigDecimal proPrice = infoDetailList.stream()
                        .map(e -> {
                            BigDecimal price;
                            if (e.getIsPaidMember() && userIsPaidMember) {
                                price = e.getVipPrice().multiply(new BigDecimal(e.getPayNum()));
                            } else {
                                price = e.getPrice().multiply(new BigDecimal(e.getPayNum()));
                            }
                            return price.subtract(e.getMerCouponPrice());
                        }).reduce(BigDecimal.ZERO, BigDecimal::add);
                if (proTotalPrice.compareTo(new BigDecimal(couponUser.getMinPrice().toString())) < 0) {
//                    throw new CrmebException("总金额小于优惠券最小使用金额");
                    throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "请合理搭配优惠组合，当前优惠券不满足使用条件");
                }
                if (proPrice.compareTo(new BigDecimal(couponUser.getMoney().toString())) <= 0) {
                    throw new CrmebException(CommonResultCode.VALIDATE_FAILED, StrUtil.format("{}优惠券不适用，请重新选择优惠券", couponUser.getName()));
                }
                for (int i = 0; i < infoDetailList.size(); i++) {
                    PreOrderInfoDetailVo detailVo = infoDetailList.get(i);
                    if (orderInfoList.size() == (i + 1)) {
                        detailVo.setPlatCouponPrice(platCouponPrice);
                        detailVo.setCouponPrice(detailVo.getMerCouponPrice().add(detailVo.getPlatCouponPrice()));
                        break;
                    }
                    BigDecimal detailPrice;
                    if (detailVo.getIsPaidMember() && userIsPaidMember) {
                        detailPrice = detailVo.getVipPrice().multiply(new BigDecimal(detailVo.getPayNum()));
                    } else {
                        detailPrice = detailVo.getPrice().multiply(new BigDecimal(detailVo.getPayNum()));
                    }
                    BigDecimal detailCouponFee = platCouponPrice.multiply(detailPrice).divide(proTotalPrice, 2, RoundingMode.HALF_UP);
                    if (detailCouponFee.compareTo(detailPrice) > 0) {
                        detailCouponFee = detailPrice;
                    }
                    platCouponPrice = platCouponPrice.subtract(detailCouponFee);
                    proTotalPrice = proTotalPrice.subtract(detailPrice);
                    detailVo.setPlatCouponPrice(detailCouponFee);
                    detailVo.setCouponPrice(detailVo.getMerCouponPrice().add(detailVo.getPlatCouponPrice()));
                }
            }
            if (couponUser.getCategory().equals(CouponConstants.COUPON_CATEGORY_JOINT_MERCHANT)) {
                List<Product> productList = productService.findByIds(proIdList);
                List<Integer> mpIdList = CrmebUtil.stringToArray(coupon.getLinkedData());
                List<Integer> pIdList = productList.stream().filter(f -> mpIdList.contains(f.getMerId())).map(Product::getId).collect(Collectors.toList());
                List<PreOrderInfoDetailVo> infoDetailList = orderInfoList.stream().filter(f -> pIdList.contains(f.getProductId())).collect(Collectors.toList());
                BigDecimal proTotalPrice = infoDetailList.stream()
                        .map(e -> {
                            BigDecimal price;
                            if (e.getIsPaidMember() && userIsPaidMember) {
                                price = e.getVipPrice().multiply(new BigDecimal(e.getPayNum()));
                            } else {
                                price = e.getPrice().multiply(new BigDecimal(e.getPayNum()));
                            }
                            return price;
                        }).reduce(BigDecimal.ZERO, BigDecimal::add);
                BigDecimal proPrice = infoDetailList.stream()
                        .map(e -> {
                            BigDecimal price;
                            if (e.getIsPaidMember() && userIsPaidMember) {
                                price = e.getVipPrice().multiply(new BigDecimal(e.getPayNum()));
                            } else {
                                price = e.getPrice().multiply(new BigDecimal(e.getPayNum()));
                            }
                            return price.subtract(e.getMerCouponPrice());
                        }).reduce(BigDecimal.ZERO, BigDecimal::add);
                if (proTotalPrice.compareTo(new BigDecimal(couponUser.getMinPrice().toString())) < 0) {
//                    throw new CrmebException("总金额小于优惠券最小使用金额");
                    throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "请合理搭配优惠组合，当前优惠券不满足使用条件");
                }
                if (proPrice.compareTo(new BigDecimal(couponUser.getMoney().toString())) <= 0) {
                    throw new CrmebException(CommonResultCode.VALIDATE_FAILED, StrUtil.format("{}优惠券不适用，请重新选择优惠券", couponUser.getName()));
                }
                for (int i = 0; i < infoDetailList.size(); i++) {
                    PreOrderInfoDetailVo detailVo = infoDetailList.get(i);
                    if (orderInfoList.size() == (i + 1)) {
                        detailVo.setPlatCouponPrice(platCouponPrice);
                        detailVo.setCouponPrice(detailVo.getMerCouponPrice().add(detailVo.getPlatCouponPrice()));
                        break;
                    }
                    BigDecimal detailPrice;
                    if (detailVo.getIsPaidMember() && userIsPaidMember) {
                        detailPrice = detailVo.getVipPrice().multiply(new BigDecimal(detailVo.getPayNum()));
                    } else {
                        detailPrice = detailVo.getPrice().multiply(new BigDecimal(detailVo.getPayNum()));
                    }
                    BigDecimal detailCouponFee = platCouponPrice.multiply(detailPrice).divide(proTotalPrice, 2, RoundingMode.HALF_UP);
                    if (detailCouponFee.compareTo(detailPrice) > 0) {
                        detailCouponFee = detailPrice;
                    }
                    platCouponPrice = platCouponPrice.subtract(detailCouponFee);
                    proTotalPrice = proTotalPrice.subtract(detailPrice);
                    detailVo.setPlatCouponPrice(detailCouponFee);
                    detailVo.setCouponPrice(detailVo.getMerCouponPrice().add(detailVo.getPlatCouponPrice()));
                }
            }
            BigDecimal platCouponFee = new BigDecimal(couponUser.getMoney().toString());
            orderInfoVo.setPlatCouponFee(platCouponFee);
        }
        orderInfoVo.setCouponFee(orderInfoVo.getPlatCouponFee().add(orderInfoVo.getMerCouponFee()));
    }
}
