package com.zbkj.front.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.github.pagehelper.PageInfo;
import com.zbkj.common.constants.*;
import com.zbkj.common.exception.CrmebException;
import com.zbkj.common.model.cdkey.CardSecret;
import com.zbkj.common.model.merchant.Merchant;
import com.zbkj.common.model.order.MerchantOrder;
import com.zbkj.common.model.order.Order;
import com.zbkj.common.model.order.OrderDetail;
import com.zbkj.common.model.product.*;
import com.zbkj.common.model.seckill.SeckillActivity;
import com.zbkj.common.model.seckill.SeckillActivityTime;
import com.zbkj.common.model.seckill.SeckillProduct;
import com.zbkj.common.model.system.SystemConfig;
import com.zbkj.common.model.system.SystemForm;
import com.zbkj.common.model.user.User;
import com.zbkj.common.model.user.UserAddress;
import com.zbkj.common.page.CommonPage;
import com.zbkj.common.request.*;
import com.zbkj.common.response.*;
import com.zbkj.common.result.CommonResultCode;
import com.zbkj.common.utils.CrmebUtil;
import com.zbkj.common.utils.RedisUtil;
import com.zbkj.common.vo.MyRecord;
import com.zbkj.common.vo.PreMerchantOrderVo;
import com.zbkj.common.vo.PreOrderInfoDetailVo;
import com.zbkj.common.vo.PreOrderInfoVo;
import com.zbkj.front.service.FrontOrderService;
import com.zbkj.front.service.SeckillService;
import com.zbkj.service.service.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 秒杀服务实现类
 *
 * @Author 指缝de阳光
 * @Date 2022/12/26 14:36
 * @Version 1.0
 */
@Service
public class SeckillServiceImpl implements SeckillService {

    private final Logger logger = LoggerFactory.getLogger(SeckillServiceImpl.class);

    @Autowired
    private SeckillActivityService seckillActivityService;
    @Autowired
    private SeckillActivityTimeService seckillActivityTimeService;
    @Autowired
    private SeckillProductService seckillProductService;
    @Autowired
    private ProductGuaranteeService productGuaranteeService;
    @Autowired
    private ProductAttrValueService productAttrValueService;
    @Autowired
    private UserMerchantCollectService userMerchantCollectService;
    @Autowired
    private MerchantService merchantService;
    @Autowired
    private ProductService productService;
    @Autowired
    private UserAddressService userAddressService;
    @Autowired
    private TransactionTemplate transactionTemplate;
    @Autowired
    private OrderService orderService;
    @Autowired
    private MerchantOrderService merchantOrderService;
    @Autowired
    private OrderDetailService orderDetailService;
    @Autowired
    private OrderStatusService orderStatusService;
    @Autowired
    private UserService userService;
    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private FrontOrderService frontOrderService;
    @Autowired
    private ProductRelationService productRelationService;
    @Autowired
    private SystemFormService systemFormService;
    @Autowired
    private SystemAttachmentService systemAttachmentService;
    @Autowired
    private CardSecretService cardSecretService;
    @Autowired
    private CdkeyLibraryService cdkeyLibraryService;
    @Autowired
    private ProductAttributeService productAttributeService;
    @Autowired
    private ProductAttributeOptionService productAttributeOptionService;
    @Autowired
    private SystemConfigService systemConfigService;

    /**
     * 获取首页秒杀信息
     */
    @Override
    public List<SeckillProduct> getIndexInfo() {
        DateTime dateTime = DateUtil.date();
        String dateStr = dateTime.toString(DateConstants.DATE_FORMAT_NUM);
        String hmStr = dateTime.toString(DateConstants.DATE_FORMAT_TIME_HHMM);
        List<Integer> acvitityIdList = seckillActivityTimeService.findActivityByDateTime(Integer.valueOf(dateStr), Integer.valueOf(hmStr));
        if (CollUtil.isEmpty(acvitityIdList)) {
            return new ArrayList<>();
        }
        List<SeckillActivity> activityList = seckillActivityService.findByIdListAndOpen(acvitityIdList, true);
        if (CollUtil.isEmpty(activityList)) {
            return new ArrayList<>();
        }
        List<Integer> aidList = activityList.stream().map(SeckillActivity::getId).collect(Collectors.toList());
        List<SeckillProduct> seckillProductList = seckillProductService.getIndexList(aidList);
        if (CollUtil.isEmpty(seckillProductList)) {
            return new ArrayList<>();
        }
        seckillProductList.forEach(sp -> {
            Product product = productService.getById(sp.getProductId());
            sp.setName(product.getName());
            sp.setImage(product.getImage());
        });
        return seckillProductList;
    }

    /**
     * 秒杀时段信息
     */
    @Override
    public List<SeckillFrontTimeResponse> activityTimeInfo() {
        DateTime dateTime = DateUtil.date();
        String dateStr = dateTime.toString(DateConstants.DATE_FORMAT_NUM);

        DateTime tomorrow = DateUtil.tomorrow();
        String tomorrowDateStr = tomorrow.toString(DateConstants.DATE_FORMAT_NUM);

        List<SeckillFrontTimeResponse> responsesList = CollUtil.newArrayList();
        List<SeckillActivityTime> todayTimeList = seckillActivityTimeService.findFrontByDate(Integer.valueOf(dateStr));
        if (CollUtil.isNotEmpty(todayTimeList)) {
            String todayStr = dateTime.toString(DateConstants.DATE_FORMAT_DATE);
            Integer hm = Integer.valueOf(dateTime.toString(DateConstants.DATE_FORMAT_TIME_HHMM));
            todayTimeList.forEach(time -> {
                SeckillFrontTimeResponse response = new SeckillFrontTimeResponse();
                response.setDate(todayStr);
                response.setStartTime(getTimeIntervalTimeStr(String.valueOf(time.getStartTime())));
                response.setEndTime(getTimeIntervalTimeStr(String.valueOf(time.getEndTime())));
                if (hm < time.getStartTime()) {
                    response.setStatus(2);
                }
                if (hm > time.getEndTime()) {
                    response.setStatus(0);
                }
                if (hm >= time.getStartTime() && hm <= time.getEndTime()) {
                    response.setStatus(1);
                }
                responsesList.add(response);
            });
        }

        List<SeckillActivityTime> tomTimeList = seckillActivityTimeService.findFrontByDate(Integer.valueOf(tomorrowDateStr));
        if (CollUtil.isNotEmpty(tomTimeList)) {
            String tomorrowStr = tomorrow.toString(DateConstants.DATE_FORMAT_DATE);
            todayTimeList.forEach(time -> {
                SeckillFrontTimeResponse response = new SeckillFrontTimeResponse();
                response.setDate(tomorrowStr);
                response.setStartTime(getTimeIntervalTimeStr(String.valueOf(time.getStartTime())));
                response.setEndTime(getTimeIntervalTimeStr(String.valueOf(time.getEndTime())));
                response.setStatus(3);
                responsesList.add(response);
            });
        }
        responsesList.forEach(this::setTimeStamp);
        return responsesList;
    }

    /**
     * 设置时间戳
     */
    private void setTimeStamp(SeckillFrontTimeResponse response) {
        String date = response.getDate();
        String start = date + " " + response.getStartTime() + ":00";
        String end = date + " " + response.getEndTime() + ":00";
        response.setStartTimeStamp(DateUtil.parse(start).getTime());
        response.setEndTimeStamp(DateUtil.parse(end).getTime());
    }

    /**
     * 秒杀商品列表
     *
     * @param request     搜索参数
     * @param pageRequest 分页参数
     * @return PageInfo
     */
    @Override
    public PageInfo<SeckillProductFrontResponse> getProductList(SeckillProductFrontSearchRequest request, PageParamRequest pageRequest) {
        String dateStr = DateUtil.parse(request.getDate()).toString(DateConstants.DATE_FORMAT_NUM);
        Integer startTime = getIntervalTimeInt(request.getStartTime());
        Integer endTime = getIntervalTimeInt(request.getEndTime());
        List<Integer> seckillIdList = seckillActivityTimeService.findActivityByDateAndTime(Integer.valueOf(dateStr), startTime, endTime);
        if (CollUtil.isEmpty(seckillIdList)) {
            return new PageInfo<>();
        }
        PageInfo<SeckillProduct> pageInfo = seckillProductService.getFrontPage(seckillIdList, 0, pageRequest.getPage(), pageRequest.getLimit());
        List<SeckillProduct> seckillProductList = pageInfo.getList();
        if (CollUtil.isEmpty(seckillProductList)) {
            return new PageInfo<>();
        }
        List<Integer> merIdList = seckillProductList.stream().map(SeckillProduct::getMerId).collect(Collectors.toList());
        Map<Integer, Merchant> merchantMap = merchantService.getMapByIdList(merIdList);
        List<SeckillProductFrontResponse> responseList = seckillProductList.stream().map(product -> {
            SeckillProductFrontResponse response = new SeckillProductFrontResponse();
            Product baseProduct = productService.getById(product.getProductId());
            BeanUtils.copyProperties(product, response);
            response.setImage(baseProduct.getImage());
            response.setName(baseProduct.getName());
            BigDecimal divide = new BigDecimal(String.valueOf((product.getQuotaShow() - product.getQuota()))).divide(new BigDecimal(product.getQuotaShow().toString()), 2, BigDecimal.ROUND_HALF_UP);
            int range = divide.multiply(new BigDecimal("100")).intValue();
            response.setPayRange(range + "%");
            response.setIsSelf(merchantMap.get(product.getMerId()).getIsSelf());
            return response;
        }).collect(Collectors.toList());
        return CommonPage.copyPageInfo(pageInfo, responseList);
    }

    /**
     * 获取秒杀商品详情
     *
     * @param id 秒杀商品ID
     * @return 秒杀商品详情
     */
    @Override
    public ProductDetailResponse getProductDetail(Integer id) {
        ProductDetailResponse productDetailResponse = new ProductDetailResponse();
        SeckillProduct seckillProduct = seckillProductService.getFrontDetail(id);
        List<ProductAttribute> attributeList = productAttributeService.findListByProductId(seckillProduct.getProductId());
        attributeList.forEach(attr -> {
            List<ProductAttributeOption> optionList = productAttributeOptionService.findListByAttrId(attr.getId());
            attr.setOptionList(optionList);
        });

        List<ProductAttrValue> productAttrValueList = productAttrValueService.getListByProductIdAndType(seckillProduct.getId(), seckillProduct.getType(), ProductConstants.PRODUCT_MARKETING_TYPE_SECKILL, true);

        SeckillActivity seckillActivity = seckillActivityService.getById(seckillProduct.getActivityId());

        // 获取秒杀关联的基础商品
        Product baseProduct = productService.getById(seckillProduct.getProductId());

        // 包装返回信息
        Product product = new Product();
        BeanUtils.copyProperties(baseProduct, product);
        product.setId(seckillProduct.getId());
        product.setPrice(seckillProduct.getSeckillPrice());
        product.setOtPrice(seckillProduct.getPrice());
        product.setStock(seckillProduct.getQuota());
        product.setSales(seckillProduct.getQuotaShow() - seckillProduct.getQuota());
        product.setContent(seckillProduct.getContent());
        product.setVipPrice(BigDecimal.ZERO);
        product.setIsPaidMember(false);
        productDetailResponse.setProductInfo(product);
        if (StrUtil.isNotBlank(baseProduct.getGuaranteeIds())) {
            List<ProductGuarantee> productGuaranteeList = productGuaranteeService.findByIdList(CrmebUtil.stringToArray(baseProduct.getGuaranteeIds()));
            productDetailResponse.setGuaranteeList(productGuaranteeList);
        }
        productDetailResponse.setProductAttr(attributeList);

        LinkedHashMap<String, ProductAttrValueResponse> skuMap = new LinkedHashMap<>();
        for (ProductAttrValue productAttrValue : productAttrValueList) {
            ProductAttrValueResponse atr = new ProductAttrValueResponse();
            ProductAttrValue baseAttrValue = productAttrValueService.getByIdAndProductIdAndType(productAttrValue.getMasterId(), seckillProduct.getProductId(), productAttrValue.getType(), ProductConstants.PRODUCT_MARKETING_TYPE_BASE);
            BeanUtils.copyProperties(baseAttrValue, atr);
            atr.setId(productAttrValue.getId());
            atr.setPrice(productAttrValue.getPrice());
            atr.setCost(productAttrValue.getCost());
            atr.setOtPrice(productAttrValue.getOtPrice());
            atr.setStock(productAttrValue.getQuota());
            if (baseAttrValue.getStock() <= 0) {
                atr.setStock(0);
            }
            atr.setSales(productAttrValue.getQuotaShow() - productAttrValue.getQuota());
            atr.setVipPrice(BigDecimal.ZERO);
            skuMap.put(atr.getSku(), atr);
        }
        productDetailResponse.setProductValue(skuMap);
        productDetailResponse.setOneQuota(seckillActivity.getOneQuota());

        // 秒杀时段
        List<SeckillActivityTime> timeList = seckillActivityTimeService.findByActivityId(seckillProduct.getActivityId());
        DateTime dateTime = DateUtil.date();
        String dateStr = dateTime.toString(DateConstants.DATE_FORMAT_NUM);
        SeckillActivityTime seckillActivityTime = timeList.get(0);
        if (seckillActivityTime.getStartDate() <= Integer.parseInt(dateStr) && Integer.parseInt(dateStr) <= seckillActivityTime.getEndDate()) {
            int timeInt = Integer.parseInt(dateTime.toString(DateConstants.DATE_FORMAT_TIME_HHMM));
            SeckillActivityTime activityTime = timeList.stream().filter(e -> e.getStartTime() <= timeInt && timeInt <= e.getEndTime()).findFirst().orElse(null);
            if (ObjectUtil.isNotNull(activityTime)) {// 当前商品正在秒杀中
                String startTimeStr = getTimeIntervalTimeStr(String.valueOf(activityTime.getStartTime()));
                String endTimeStr = getTimeIntervalTimeStr(String.valueOf(activityTime.getEndTime()));
                productDetailResponse.setStartTimeStamp(DateUtil.parse(dateTime.toString(DateConstants.DATE_FORMAT_DATE) + " " + startTimeStr + ":00").getTime());
                productDetailResponse.setEndTimeStamp(DateUtil.parse(dateTime.toString(DateConstants.DATE_FORMAT_DATE) + " " + endTimeStr + ":00").getTime());
            } else {// 当前商品未开始秒杀
                List<SeckillActivityTime> waitTimeList = timeList.stream().filter(e -> e.getStartTime() >= timeInt).collect(Collectors.toList());
                if (CollUtil.isNotEmpty(waitTimeList)) {
                    SeckillActivityTime waitTime = waitTimeList.stream().min(Comparator.comparing(SeckillActivityTime::getStartTime)).get();
                    if (ObjectUtil.isNotNull(waitTime)) {
                        String startTimeStr = getTimeIntervalTimeStr(String.valueOf(waitTime.getStartTime()));
                        String endTimeStr = getTimeIntervalTimeStr(String.valueOf(waitTime.getEndTime()));
                        productDetailResponse.setStartTimeStamp(DateUtil.parse(dateTime.toString(DateConstants.DATE_FORMAT_DATE) + " " + startTimeStr + ":00").getTime());
                        productDetailResponse.setEndTimeStamp(DateUtil.parse(dateTime.toString(DateConstants.DATE_FORMAT_DATE) + " " + endTimeStr + ":00").getTime());
                    }
                }
            }
        }
        // 获取商户信息
        Merchant merchant = merchantService.getById(productDetailResponse.getProductInfo().getMerId());
        ProductMerchantResponse merchantResponse = new ProductMerchantResponse();
        BeanUtils.copyProperties(merchant, merchantResponse);
        merchantResponse.setCollectNum(userMerchantCollectService.getCountByMerId(merchant.getId()));
        // 获取商户推荐商品
        List<ProMerchantProductResponse> merchantProductResponseList = productService.getRecommendedProductsByMerId(merchant.getId(), 4);
        merchantResponse.setProList(merchantProductResponseList);
        // 获取用户
        Integer userId = userService.getUserId();
        productDetailResponse.setUserCollect(false);
        // 用户收藏
        if (userId > 0) {
            merchantResponse.setIsCollect(userMerchantCollectService.isCollect(userId, merchant.getId()));
            // 查询用户是否收藏收藏
            productDetailResponse.setUserCollect(productRelationService.existCollectByUser(userId, seckillProduct.getProductId()));
        }
        productDetailResponse.setMerchantInfo(merchantResponse);
        productDetailResponse.setMasterProductId(seckillProduct.getProductId());

        return productDetailResponse;
    }

    /**
     * 秒杀预下单校验
     *
     * @param detailRequest 商品参数
     * @return PreMerchantOrderVo
     */
    @Override
    public PreMerchantOrderVo validatePreOrderSeckill(PreOrderDetailRequest detailRequest) {
        if (ObjectUtil.isNull(detailRequest.getProductId()) || detailRequest.getProductId() <= 0) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "请选择秒杀商品");
        }
        if (ObjectUtil.isNull(detailRequest.getAttrValueId())) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "商品规格属性值不能为空");
        }
        if (ObjectUtil.isNull(detailRequest.getProductNum()) || detailRequest.getProductNum() <= 0) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "购买数量必须大于0");
        }
        // 查询商品信息
        SeckillProduct seckillProduct = seckillProductService.getById(detailRequest.getProductId());
        if (ObjectUtil.isNull(seckillProduct) || seckillProduct.getIsDel()) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "商品信息不存在，请刷新后重新选择");
        }
        if (!seckillProduct.getIsShow()) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "商品已下架，请刷新后重新选择");
        }
        if (seckillProduct.getQuota() < detailRequest.getProductNum()) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "商品库存不足，请刷新后重新选择");
        }
        Product product = productService.getById(seckillProduct.getProductId());
        if (ObjectUtil.isNull(product) || product.getIsDel()) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "商品信息不存在，请刷新后重新选择");
        }
        if (product.getStock() < detailRequest.getProductNum()) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "商品库存不足，请刷新后重新选择");
        }
        // 查询秒杀活动
        SeckillActivity seckillActivity = seckillActivityService.getById(seckillProduct.getActivityId());
        if (ObjectUtil.isNull(seckillActivity) || seckillActivity.getIsDel() || seckillActivity.getIsOpen().equals(0)) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "秒杀活动不存在");
        }
        if (seckillActivity.getStatus().equals(2)) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "秒杀活动已结束");
        }
        Boolean inProgressNow = seckillActivityTimeService.isInProgressNow(seckillActivity.getId());
        if (!inProgressNow) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "秒杀活动已结束");
        }
        if (seckillActivity.getOneQuota() > 0 && seckillActivity.getOneQuota() < detailRequest.getProductNum()) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, StrUtil.format("此商品单次秒杀最多可购买{}件", seckillActivity.getOneQuota()));
        }
        Integer uid = userService.getUserId();
        if (seckillActivity.getAllQuota() > 0) {
            Integer productNumCount = orderService.getProductNumCount(uid, seckillProduct.getId(), ProductConstants.PRODUCT_MARKETING_TYPE_SECKILL);
            if ((seckillActivity.getAllQuota() - productNumCount) < detailRequest.getProductNum()) {
                throw new CrmebException(CommonResultCode.VALIDATE_FAILED, StrUtil.format("此商品整场秒杀活动还可买{}件", seckillActivity.getAllQuota() - productNumCount));
            }
        }
        // 查询商品规格属性值信息
        List<ProductAttrValue> productAttrValueList = productAttrValueService.getListByProductIdAndType(seckillProduct.getId(), seckillProduct.getType(), ProductConstants.PRODUCT_MARKETING_TYPE_SECKILL, false);
        ProductAttrValue attrValue = productAttrValueList.stream().filter(e -> e.getId().equals(detailRequest.getAttrValueId())).findFirst().orElse(null);
        if (ObjectUtil.isNull(attrValue)) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "商品规格信息不存在，请刷新后重新选择");
        }
        if (attrValue.getQuota() < detailRequest.getProductNum()) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "商品规格库存不足，请刷新后重新选择");
        }
        ProductAttrValue baseAttrValue = productAttrValueService.getByIdAndProductIdAndType(attrValue.getMasterId(), product.getId(), product.getType(), product.getMarketingType());
        if (ObjectUtil.isNull(baseAttrValue)) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "商品规格信息不存在，请刷新后重新选择");
        }
        if (!baseAttrValue.getIsShow()) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "商品规格信息无效，请刷新后重新选择");
        }
        if (baseAttrValue.getStock() < detailRequest.getProductNum()) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "商品规格库存不足，请刷新后重新选择");
        }
        Merchant merchant = merchantService.getByIdException(seckillProduct.getMerId());
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
        merchantOrderVo.setType(OrderConstants.ORDER_TYPE_SECKILL);
        merchantOrderVo.setSecondType(seckillProduct.getType());
        merchantOrderVo.setDeliveryMethodMer(product.getDeliveryMethod());
        PreOrderInfoDetailVo detailVo = new PreOrderInfoDetailVo();
        detailVo.setProductId(seckillProduct.getId());
        detailVo.setProductName(product.getName());
        detailVo.setAttrValueId(attrValue.getId());
        detailVo.setSku(baseAttrValue.getSku());
        detailVo.setPrice(attrValue.getPrice());
        detailVo.setPayPrice(attrValue.getPrice());
        detailVo.setPayNum(detailRequest.getProductNum());
        detailVo.setImage(StrUtil.isNotBlank(baseAttrValue.getImage()) ? baseAttrValue.getImage() : product.getImage());
        detailVo.setVolume(baseAttrValue.getVolume());
        detailVo.setWeight(baseAttrValue.getWeight());
        detailVo.setTempId(product.getTempId());
        detailVo.setSubBrokerageType(0);// 秒杀不参与分佣
        detailVo.setBrokerage(0);
        detailVo.setBrokerageTwo(0);
        detailVo.setProductType(product.getType());
        detailVo.setProductMarketingType(ProductConstants.PRODUCT_MARKETING_TYPE_SECKILL);
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
        merchantOrderVo.setOrderInfoList(infoList);
        return merchantOrderVo;
    }

    /**
     * 秒杀创建订单库存校验
     *
     * @param orderInfoVo 预下单信息
     * @return
     */
    @Override
    public MyRecord validateCreateOrderProductStock(PreOrderInfoVo orderInfoVo) {
        PreMerchantOrderVo merchantOrderVo = orderInfoVo.getMerchantOrderVoList().get(0);
        Merchant merchant = merchantService.getByIdException(merchantOrderVo.getMerId());
        if (!merchant.getIsSwitch()) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "商户已关闭，请重新下单");
        }
        PreOrderInfoDetailVo info = merchantOrderVo.getOrderInfoList().get(0);
        // 查询商品信息
        SeckillProduct seckillProduct = seckillProductService.getById(info.getProductId());
        if (ObjectUtil.isNull(seckillProduct) || seckillProduct.getIsDel()) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "购买的商品信息不存在");
        }
        if (!seckillProduct.getIsShow()) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "购买的商品已下架");
        }
        if (seckillProduct.getQuota().equals(0) || info.getPayNum() > seckillProduct.getQuota()) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "购买的商品库存不足");
        }
        Product baseProduct = productService.getById(seckillProduct.getProductId());
        if (ObjectUtil.isNull(baseProduct) || baseProduct.getIsDel()) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "购买的商品信息不存在");
        }
        if (baseProduct.getStock().equals(0) || info.getPayNum() > baseProduct.getStock()) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "购买的商品库存不足");
        }
        // 查询秒杀活动
        SeckillActivity seckillActivity = seckillActivityService.getById(seckillProduct.getActivityId());
        if (ObjectUtil.isNull(seckillActivity) || seckillActivity.getIsDel() || seckillActivity.getIsOpen().equals(0)) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "秒杀活动不存在");
        }
        if (seckillActivity.getStatus().equals(2)) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "秒杀活动已结束");
        }
        Boolean inProgressNow = seckillActivityTimeService.isInProgressNow(seckillActivity.getId());
        if (!inProgressNow) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "秒杀活动已结束");
        }
        if (seckillActivity.getOneQuota() > 0 && seckillActivity.getOneQuota() < info.getPayNum()) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, StrUtil.format("此商品单次秒杀最多可购买{}件", seckillActivity.getOneQuota()));
        }
        Integer uid = userService.getUserId();
        if (seckillActivity.getAllQuota() > 0) {
            Integer productNumCount = orderService.getProductNumCount(uid, seckillProduct.getId(), ProductConstants.PRODUCT_MARKETING_TYPE_SECKILL);
            if ((seckillActivity.getAllQuota() - productNumCount) < info.getPayNum()) {
                throw new CrmebException(CommonResultCode.VALIDATE_FAILED, StrUtil.format("此商品整场秒杀活动还可买{}件", seckillActivity.getAllQuota() - productNumCount));
            }
        }
        // 查询商品规格属性值信息
        ProductAttrValue attrValue = productAttrValueService.getByIdAndProductIdAndType(info.getAttrValueId(), info.getProductId(), seckillProduct.getType(), ProductConstants.PRODUCT_MARKETING_TYPE_SECKILL);
        if (ObjectUtil.isNull(attrValue)) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "购买的商品规格信息不存在");
        }
        if (attrValue.getQuota() < info.getPayNum()) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "购买的商品库存不足");
        }
        ProductAttrValue baseAttrValue = productAttrValueService.getByIdAndProductIdAndType(
                attrValue.getMasterId(), baseProduct.getId(), baseProduct.getType(), ProductConstants.PRODUCT_MARKETING_TYPE_BASE);
        if (ObjectUtil.isNull(baseAttrValue)) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "购买的商品规格信息不存在");
        }
        if (!baseAttrValue.getIsShow()) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "购买的商品规格信息无效");
        }
        if (baseAttrValue.getStock() < info.getPayNum()) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "购买的商品库存不足");
        }

        MyRecord record = new MyRecord();
        record.set("productId", info.getProductId());
        record.set("num", info.getPayNum());
        record.set("attrValueId", attrValue.getId());
        record.set("attrValueVersion", attrValue.getVersion());
        record.set("baseProductId", baseProduct.getId());
        record.set("baseAttrValueId", baseAttrValue.getId());
        record.set("baseAttrValueVersion", baseAttrValue.getVersion());
        record.set("type", attrValue.getType());
        record.set("marketingType", attrValue.getMarketingType());
        if (baseProduct.getType().equals(ProductConstants.PRODUCT_TYPE_CLOUD)) {
            record.set("expand", baseAttrValue.getExpand());
        }
        if (baseProduct.getType().equals(ProductConstants.PRODUCT_TYPE_CDKEY)) {
            record.set("cdkeyId", baseAttrValue.getCdkeyId());
        }
        return record;
    }

    /**
     * 创建秒杀订单
     *
     * @param orderRequest 下单请求对象
     * @param orderInfoVo  预下单缓存对象
     * @param user         用户信息
     */
    @Override
    public OrderNoResponse createOrder(CreateOrderRequest orderRequest, PreOrderInfoVo orderInfoVo, User user) {
        UserAddress userAddress = null;
        OrderMerchantRequest orderMerchantRequest = orderRequest.getOrderMerchantRequestList().get(0);
        if (orderInfoVo.getSecondType().equals(OrderConstants.ORDER_SECOND_TYPE_CLOUD)
                || orderInfoVo.getSecondType().equals(OrderConstants.ORDER_SECOND_TYPE_CDKEY)
                || orderInfoVo.getSecondType().equals(OrderConstants.ORDER_SECOND_TYPE_VIRTUALLY)) {
            orderRequest.setAddressId(0);
        } else {
            if (orderMerchantRequest.getShippingType().equals(OrderConstants.ORDER_SHIPPING_TYPE_EXPRESS)) {
                if (ObjectUtil.isNull(orderRequest.getAddressId())) {
                    throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "请选择收货地址");
                }
                userAddress = userAddressService.getById(orderRequest.getAddressId());
                if (ObjectUtil.isNull(userAddress) || userAddress.getIsDel()) {
                    throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "收货地址有误");
                }
            }
            if (orderMerchantRequest.getShippingType().equals(OrderConstants.ORDER_SHIPPING_TYPE_PICK_UP)) {
                Merchant merchant = merchantService.getByIdException(orderMerchantRequest.getMerId());
                if (!merchant.getIsTakeTheir()) {
                    throw new CrmebException(CommonResultCode.VALIDATE_FAILED, StrUtil.format("{}没有配置店铺地址，请联系客服", merchant.getName()));
                }
            }
        }

        PreMerchantOrderVo merchantOrderVo = orderInfoVo.getMerchantOrderVoList().get(0);
        merchantOrderVo.setShippingType(orderMerchantRequest.getShippingType());
        merchantOrderVo.setUserCouponId(orderMerchantRequest.getUserCouponId());
        merchantOrderVo.setCouponFee(BigDecimal.ZERO);
        merchantOrderVo.setMerCouponFee(BigDecimal.ZERO);
        merchantOrderVo.setUseIntegral(0);
        merchantOrderVo.setIntegralPrice(BigDecimal.ZERO);
        PreOrderInfoDetailVo detailVo = merchantOrderVo.getOrderInfoList().get(0);

        // 校验商品库存
        MyRecord skuRecord = validateCreateOrderProductStock(orderInfoVo);

        // 计算订单各种价格
        frontOrderService.getFreightFee_V_1_8(orderInfoVo, userAddress, false);
        orderInfoVo.setCouponFee(BigDecimal.ZERO);

        // 平台订单
        Order order = new Order();
        String orderNo = CrmebUtil.getOrderNo(OrderConstants.ORDER_PREFIX_PLATFORM);
        order.setOrderNo(orderNo);
        order.setMerId(0);
        order.setUid(user.getId());
        order.setTotalNum(orderInfoVo.getOrderProNum());
        order.setProTotalPrice(orderInfoVo.getProTotalFee());
        order.setTotalPostage(orderInfoVo.getFreightFee());
        order.setTotalPrice(order.getProTotalPrice().add(order.getTotalPostage()));
        order.setCouponPrice(orderInfoVo.getCouponFee());
        order.setUseIntegral(merchantOrderVo.getUseIntegral());
        order.setIntegralPrice(merchantOrderVo.getIntegralPrice());
        order.setPayPrice(order.getProTotalPrice().add(order.getTotalPostage()).subtract(order.getCouponPrice()).subtract(order.getIntegralPrice()));
        order.setPayPostage(order.getTotalPostage());
        order.setPaid(false);
        order.setCancelStatus(OrderConstants.ORDER_CANCEL_STATUS_NORMAL);
        order.setLevel(OrderConstants.ORDER_LEVEL_PLATFORM);
        order.setType(orderInfoVo.getType());
        order.setSecondType(orderInfoVo.getSecondType());
        order.setSystemFormId(orderInfoVo.getSystemFormId());
        order.setOrderExtend(StrUtil.isNotBlank(orderRequest.getOrderExtend()) ? systemAttachmentService.clearPrefix(orderRequest.getOrderExtend(), systemAttachmentService.getCdnUrl()) : "");

        // 商户订单
        // 秒杀只会有一个商户订单
        MerchantOrder merchantOrder = new MerchantOrder();
        merchantOrder.setOrderNo(order.getOrderNo());
        merchantOrder.setMerId(merchantOrderVo.getMerId());
        merchantOrder.setUid(user.getId());
        if (StrUtil.isNotBlank(orderMerchantRequest.getRemark())) {
            merchantOrder.setUserRemark(orderMerchantRequest.getRemark());
        }
        merchantOrder.setShippingType(orderMerchantRequest.getShippingType());
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
        merchantOrder.setCouponPrice(merchantOrderVo.getCouponFee());
        merchantOrder.setPayPrice(merchantOrder.getTotalPrice().subtract(merchantOrder.getCouponPrice()).subtract(merchantOrder.getIntegralPrice()));
        merchantOrder.setGainIntegral(0);
        merchantOrder.setType(order.getType());
        merchantOrder.setSecondType(order.getSecondType());


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
        orderDetail.setCouponPrice(detailVo.getCouponPrice());
        orderDetail.setUseIntegral(detailVo.getUseIntegral());
        orderDetail.setIntegralPrice(detailVo.getIntegralPrice());
        orderDetail.setPayPrice(BigDecimal.ZERO);
        BigDecimal detailPayPrice = orderDetail.getPrice().multiply(new BigDecimal(orderDetail.getPayNum().toString())).add(orderDetail.getFreightFee()).subtract(orderDetail.getCouponPrice()).subtract(orderDetail.getIntegralPrice());
        if (detailPayPrice.compareTo(BigDecimal.ZERO) >= 0) {
            orderDetail.setPayPrice(detailPayPrice);
        }
        orderDetail.setProRefundSwitch(detailVo.getProRefundSwitch());

        Boolean execute = transactionTemplate.execute(e -> {
            Boolean result = false;
            result = seckillProductService.operationStock(skuRecord.getInt("productId"), skuRecord.getInt("num"), Constants.OPERATION_TYPE_SUBTRACT);
            if (!result) {
                e.setRollbackOnly();
                logger.error("生成秒杀订单扣减秒杀商品库存失败,预下单号：{},商品ID：{}", orderRequest.getPreOrderNo(), skuRecord.getInt("productId"));
                return result;
            }
            result = productAttrValueService.operationStock(skuRecord.getInt("attrValueId"), skuRecord.getInt("num"), Constants.OPERATION_TYPE_SUBTRACT, skuRecord.getInt("type"), ProductConstants.PRODUCT_MARKETING_TYPE_SECKILL, skuRecord.getInt("attrValueVersion"));
            if (!result) {
                e.setRollbackOnly();
                logger.error("生成秒杀订单扣减基础商品sku库存失败,预下单号：{},商品skuID：{}", orderRequest.getPreOrderNo(), skuRecord.getInt("attrValueId"));
                return result;
            }
            // 普通商品口库存
            result = productService.operationStock(skuRecord.getInt("baseProductId"), skuRecord.getInt("num"), Constants.OPERATION_TYPE_SUBTRACT);
            if (!result) {
                e.setRollbackOnly();
                logger.error("生成秒杀订单扣减基础商品库存失败,预下单号：{},商品ID：{}", orderRequest.getPreOrderNo(), skuRecord.getInt("productId"));
                return result;
            }
            // 普通商品规格扣库存
            result = productAttrValueService.operationStock(skuRecord.getInt("baseAttrValueId"), skuRecord.getInt("num"), Constants.OPERATION_TYPE_SUBTRACT, skuRecord.getInt("type"), skuRecord.getInt("baseAttrValueVersion"));
            if (!result) {
                e.setRollbackOnly();
                logger.error("生成秒杀订单扣减基础商品sku库存失败,预下单号：{},商品skuID：{}", orderRequest.getPreOrderNo(), skuRecord.getInt("attrValueId"));
                return result;
            }
            if (order.getSecondType().equals(OrderConstants.ORDER_SECOND_TYPE_CLOUD)) { // 云盘订单
                orderDetail.setExpand(skuRecord.getStr("expand"));
            }
            if (order.getSecondType().equals(OrderConstants.ORDER_SECOND_TYPE_CDKEY)) { // 卡密订单
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
                orderDetail.setCardSecretIds(cardSecretIds);
                cdkeyLibraryService.operationUseNum(cdkeyId, csIdList.size(), Constants.OPERATION_TYPE_ADD);
            }

            orderService.save(order);
            merchantOrderService.save(merchantOrder);
            orderDetailService.save(orderDetail);
            // 生成订单日志
            orderStatusService.createLog(order.getOrderNo(), OrderStatusConstants.ORDER_STATUS_CREATE, OrderStatusConstants.ORDER_LOG_MESSAGE_CREATE);
            return Boolean.TRUE;
        });

        if (!execute) {
            throw new CrmebException("秒杀订单生成失败");
        }

        String key = OrderConstants.PRE_ORDER_CACHE_PREFIX + orderRequest.getPreOrderNo();
        // 删除缓存订单
        if (redisUtil.exists(key)) {
            redisUtil.delete(key);
        }

        // 加入自动未支付自动取消队列
        redisUtil.lPush(TaskConstants.ORDER_TASK_REDIS_KEY_AUTO_CANCEL_KEY, order.getOrderNo());

        OrderNoResponse response = new OrderNoResponse();
        response.setOrderNo(order.getOrderNo());
        response.setPayPrice(order.getPayPrice());
        return response;
    }

    /**
     * 商户秒杀商品列表
     */
    @Override
    public PageInfo<SeckillProductFrontResponse> findMerchantProductPage(MerchantSeckillProFrontSearchRequest request) {
        DateTime dateTime = DateUtil.date();
        String dateStr = dateTime.toString(DateConstants.DATE_FORMAT_NUM);
        String hmStr = dateTime.toString(DateConstants.DATE_FORMAT_TIME_HHMM);
        List<Integer> acvitityIdList = seckillActivityTimeService.findActivityByDateTime(Integer.valueOf(dateStr), Integer.valueOf(hmStr));
        if (CollUtil.isEmpty(acvitityIdList)) {
            return new PageInfo<>();
        }
        List<SeckillActivity> activityList = seckillActivityService.findByIdListAndOpen(acvitityIdList, true);
        if (CollUtil.isEmpty(activityList)) {
            return new PageInfo<>();
        }
        List<Integer> aidList = activityList.stream().map(SeckillActivity::getId).collect(Collectors.toList());
        PageInfo<SeckillProduct> pageInfo = seckillProductService.getFrontPage(aidList, request.getMerId(), request.getPage(), request.getLimit());
        List<SeckillProduct> seckillProductList = pageInfo.getList();
        if (CollUtil.isEmpty(seckillProductList)) {
            return new PageInfo<>();
        }
        Merchant merchant = merchantService.getByIdException(request.getMerId());
        List<SeckillProductFrontResponse> responseList = seckillProductList.stream().map(product -> {
            SeckillProductFrontResponse response = new SeckillProductFrontResponse();
            Product baseProduct = productService.getById(product.getProductId());
            BeanUtils.copyProperties(product, response);
            response.setImage(baseProduct.getImage());
            response.setName(baseProduct.getName());
            BigDecimal divide = new BigDecimal(String.valueOf((product.getQuotaShow() - product.getQuota()))).divide(new BigDecimal(product.getQuotaShow().toString()), 2, BigDecimal.ROUND_HALF_UP);
            int range = divide.multiply(new BigDecimal("100")).intValue();
            response.setPayRange(range + "%");
            response.setIsSelf(merchant.getIsSelf());
            return response;
        }).collect(Collectors.toList());
        return CommonPage.copyPageInfo(pageInfo, responseList);
    }

    /**
     * 获取秒杀样式
     */
    @Override
    public String getSeckillStyle() {
        SystemConfig systemConfig = systemConfigService.getSeckillStyle();
        return systemConfig.getValue();
    }

    /**
     * 获取时段时间
     */
    private Integer getIntervalTimeInt(String timeStr) {
        timeStr = timeStr.replaceAll("%3A", ":");
        if (!timeStr.contains(":")) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "时间参数不正确 例如:01:00,02:00");
        }
        String[] splitStart = timeStr.split(":");
        String start = splitStart[0].trim().concat(splitStart[1].trim());
        Integer startTime = Integer.valueOf(start);
        return startTime;
    }

    /**
     * 获取时段时间
     *
     * @param timeStr 时间字符
     */
    private String getTimeIntervalTimeStr(String timeStr) {
        String time = timeStr.length() == 3 ? "0" + timeStr : timeStr;
        if (timeStr.length() == 1) {
            time = "0000";
        }
        return time.substring(0, 2) + ":" + time.substring(2);
    }

}
