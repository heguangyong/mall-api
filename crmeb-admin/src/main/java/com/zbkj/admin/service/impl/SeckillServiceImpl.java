package com.zbkj.admin.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.github.pagehelper.PageInfo;
import com.zbkj.admin.service.SeckillService;
import com.zbkj.common.constants.DateConstants;
import com.zbkj.common.constants.ProductConstants;
import com.zbkj.common.exception.CrmebException;
import com.zbkj.common.model.admin.SystemAdmin;
import com.zbkj.common.model.merchant.Merchant;
import com.zbkj.common.model.product.*;
import com.zbkj.common.model.seckill.SeckillActivity;
import com.zbkj.common.model.seckill.SeckillActivityTime;
import com.zbkj.common.model.seckill.SeckillProduct;
import com.zbkj.common.model.seckill.SeckillTimeInterval;
import com.zbkj.common.page.CommonPage;
import com.zbkj.common.request.*;
import com.zbkj.common.response.SeckillActivityDetailResponse;
import com.zbkj.common.response.SeckillActivityPageResponse;
import com.zbkj.common.response.SeckillProductPageResponse;
import com.zbkj.common.response.SeckillTimeIntervalResponse;
import com.zbkj.common.result.CommonResultCode;
import com.zbkj.common.result.MarketingResultCode;
import com.zbkj.common.utils.CrmebUtil;
import com.zbkj.common.utils.SecurityUtil;
import com.zbkj.service.service.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 秒杀service实现类
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
public class SeckillServiceImpl implements SeckillService {

    private final Logger logger = LoggerFactory.getLogger(SeckillServiceImpl.class);

    @Autowired
    private SeckillActivityService seckillActivityService;
    @Autowired
    private SeckillProductService seckillProductService;
    @Autowired
    private SeckillTimeIntervalService seckillTimeIntervalService;
    @Autowired
    private SeckillActivityTimeService seckillActivityTimeService;
    @Autowired
    private TransactionTemplate transactionTemplate;
    @Autowired
    private ProductService productService;
    @Autowired
    private ProductAttrValueService productAttrValueService;
    @Autowired
    private ProductAttrService productAttrService;
    @Autowired
    private MerchantService merchantService;
    @Autowired
    private ProductCategoryService productCategoryService;
    @Autowired
    private ProductAttributeService productAttributeService;
    @Autowired
    private ProductAttributeOptionService productAttributeOptionService;

    /**
     * 新增秒杀时段
     */
    @Override
    public Boolean createTimeInterval(SeckillTimeIntervalRequest request) {
        SeckillTimeInterval timeInterval = new SeckillTimeInterval();
        BeanUtils.copyProperties(request, timeInterval);
        setTimeIntervalRequest(request, timeInterval);
        timeInterval.setId(null);
        if (seckillTimeIntervalService.checkTimeUnique(timeInterval.getStartTime(), timeInterval.getEndTime(), 0)) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "当前时间段的秒杀配置已存在");
        }
        return seckillTimeIntervalService.save(timeInterval);
    }

    /**
     * 编辑秒杀时段
     */
    @Override
    public Boolean updateTimeInterval(SeckillTimeIntervalRequest request) {
        if (ObjectUtil.isNull(request.getId())) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "秒杀时段ID不能为空");
        }
        SeckillTimeInterval interval = seckillTimeIntervalService.getById(request.getId());
        if (ObjectUtil.isNull(interval) || interval.getIsDel()) {
            throw new CrmebException(MarketingResultCode.SECKILL_TIME_INTERVAL_NOT_EXIST);
        }
        BeanUtils.copyProperties(request, interval);
        setTimeIntervalRequest(request, interval);
        if (seckillTimeIntervalService.checkTimeUnique(interval.getStartTime(), interval.getEndTime(), interval.getId())) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "当前时间段的秒杀配置已存在");
        }
        return transactionTemplate.execute(e -> {
            seckillTimeIntervalService.updateById(interval);
            // 判断是否有活动使用此时段
            if (seckillActivityTimeService.isExistTimeInterval(interval.getId())) {
                seckillActivityTimeService.updateTimeByIntervalId(interval.getId(), interval.getStartTime(), interval.getEndTime());
            }
            return Boolean.TRUE;
        });
    }

    /**
     * 删除秒杀时段
     * @param id 秒杀时段ID
     */
    @Override
    public Boolean deleteTimeInterval(Integer id) {
        SeckillTimeInterval interval = seckillTimeIntervalService.getById(id);
        if (ObjectUtil.isNull(interval) || interval.getIsDel()) {
            throw new CrmebException(MarketingResultCode.SECKILL_TIME_INTERVAL_NOT_EXIST);
        }
        // 判断是否有活动使用此时段
        if (seckillActivityTimeService.isExistTimeInterval(id)) {
            throw new CrmebException(MarketingResultCode.SECKILL_TIME_INTERVAL_USED);
        }
        return seckillTimeIntervalService.deleteById(id);
    }

    /**
     * 秒杀时段列表
     * @param request 搜索参数
     */
    @Override
    public List<SeckillTimeIntervalResponse> getTimeIntervalList(SeckillTimeIntervalSearchRequest request) {
        List<SeckillTimeInterval> intervalList = seckillTimeIntervalService.getAdminList(request);
        List<SeckillTimeIntervalResponse> responseList = CollUtil.newArrayList();
        if (CollUtil.isNotEmpty(intervalList)) {
            intervalList.forEach(e -> {
                SeckillTimeIntervalResponse response = new SeckillTimeIntervalResponse();
                BeanUtils.copyProperties(e, response);
                response.setStartTime(getTimeIntervalTime(e.getStartTime().toString()));
                response.setEndTime(getTimeIntervalTime(e.getEndTime().toString()));
                responseList.add(response);
            });
        }
        return responseList;
    }

    /**
     * 秒杀时段开关
     * @param id 秒杀时段ID
     */
    @Override
    public Boolean switchTimeInterval(Integer id) {
        SeckillTimeInterval interval = seckillTimeIntervalService.getById(id);
        if (ObjectUtil.isNull(interval)) {
            throw new CrmebException(MarketingResultCode.SECKILL_TIME_INTERVAL_NOT_EXIST);
        }
        interval.setStatus(interval.getStatus().equals(1) ? 0 : 1);
        return seckillTimeIntervalService.updateById(interval);
    }

    /**
     * 新增秒杀活动
     */
    @Override
    public Boolean createActivity(SeckillActivitySaveRequest request) {
        validateSeckillDate(request);
        SeckillActivity activity = new SeckillActivity();
        BeanUtils.copyProperties(request, activity);
        activity.setId(null);

        List<Integer> timeIntervalList = CrmebUtil.stringToArray(request.getTimeIntervals());
        List<SeckillActivityTime> activityTimeList = timeIntervalList.stream().map(ti -> {
            SeckillTimeInterval timeInterval = seckillTimeIntervalService.getById(ti);
            if (ObjectUtil.isNull(timeInterval) || timeInterval.getStatus().equals(0)) {
                throw new CrmebException(MarketingResultCode.SECKILL_TIME_INTERVAL_NOT_EXIST, "秒杀时段不存在或未开启");
            }
            SeckillActivityTime activityTime = new SeckillActivityTime();
            activityTime.setTimeIntervalId(timeInterval.getId());
            activityTime.setStartDate(CrmebUtil.dateStrToInteger(activity.getStartDate()));
            activityTime.setEndDate(CrmebUtil.dateStrToInteger(activity.getEndDate()));
            activityTime.setStartTime(timeInterval.getStartTime());
            activityTime.setEndTime(timeInterval.getEndTime());
            return activityTime;
        }).collect(Collectors.toList());

        List<SeckillProduct> productList = CollUtil.newArrayList();
        List<ProductAttrValue> attrValueList = CollUtil.newArrayList();

        if (CollUtil.isNotEmpty(request.getProductList())) {
            request.getProductList().forEach(productRequest -> {
                Product product = productService.getById(productRequest.getId());
                if (ObjectUtil.isNull(product) || !product.getMarketingType().equals(ProductConstants.PRODUCT_MARKETING_TYPE_BASE)) {
                    throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "没有对应的商品信息，商品ID = " + productRequest.getId());
                }
                if (product.getIsAudit()) {
                    throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "商品处于审核流程中，商品ID = " + productRequest.getId());
                }
                if (!product.getAuditStatus().equals(ProductConstants.AUDIT_STATUS_EXEMPTION) && !product.getAuditStatus().equals(ProductConstants.AUDIT_STATUS_SUCCESS)) {
                    throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "商品处于审核流程中，商品ID = " + productRequest.getId());
                }
                List<ProductAttrValue> productAttrValueList = productAttrValueService.getListByProductIdAndType(product.getId(), product.getType(), ProductConstants.PRODUCT_MARKETING_TYPE_BASE, false);
                if (CollUtil.isEmpty(productAttrValueList)) {
                    throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "没有对应的商品sku信息，商品ID = " + productRequest.getId());
                }

                List<SeckillActivityProductAttrValueRequest> attrValueRequestList = productRequest.getAttrValue();
                BigDecimal activityPrice = attrValueRequestList.stream().map(SeckillActivityProductAttrValueRequest::getActivityPrice).min(BigDecimal::compareTo).orElse(BigDecimal.ZERO);
                if (activityPrice.compareTo(BigDecimal.ZERO) < 1) {
                    throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "秒杀价格不能小于等于0");
                }
                int quota = attrValueRequestList.stream().mapToInt(SeckillActivityProductAttrValueRequest::getQuota).sum();
                if (quota < 1) {
                    throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "秒杀商品的限量必须大于0,商品名称:" + product.getName());
                }
                SeckillProduct seckillProduct = new SeckillProduct();
                BeanUtils.copyProperties(product, seckillProduct);
                seckillProduct.setSeckillPrice(activityPrice);
                seckillProduct.setQuota(quota);
                seckillProduct.setQuotaShow(quota);
                seckillProduct.setSales(0);
                seckillProduct.setSort(productRequest.getSort());
                seckillProduct.setAuditStatus(2);
                seckillProduct.setId(product.getId());
                seckillProduct.setProductId(product.getId());

                productAttrValueList.forEach(v -> {
                    SeckillActivityProductAttrValueRequest attrValueRequest = attrValueRequestList.stream().filter(e -> e.getAttrValueId().equals(v.getId())).findFirst().orElse(null);
                    if (ObjectUtil.isNull(attrValueRequest)) {
                        throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "添加秒杀商品未找到对应SKU，秒杀商品名称:" + product.getName() + ",SKU = " + v.getSku());
                    }
                    if (attrValueRequest.getActivityPrice().compareTo(BigDecimal.ZERO) < 1) {
                        throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "秒杀价格不能小于等于0");
                    }
                    if (attrValueRequest.getQuota() > v.getStock()) {
                        throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "秒杀商品的SKU的限量不能大于库存,商品名称：" + product.getName());
                    }
                    v.setQuota(attrValueRequest.getQuota());
                    v.setQuotaShow(attrValueRequest.getQuota());
                    v.setOtPrice(v.getPrice());
                    v.setPrice(attrValueRequest.getActivityPrice());
                    v.setMasterId(v.getId());
                    v.setStock(attrValueRequest.getQuota());
                    v.setSales(0);
                });
                productList.add(seckillProduct);
                attrValueList.addAll(productAttrValueList);
            });
        }

        Boolean execute = transactionTemplate.execute(e -> {
            seckillActivityService.save(activity);
            if (CollUtil.isNotEmpty(activityTimeList)) {
                activityTimeList.forEach(t -> t.setSeckillId(activity.getId()));
            }
            seckillActivityTimeService.saveBatch(activityTimeList);
            // 秒杀商品添加
            if (CollUtil.isNotEmpty(productList)) {
                productList.forEach(product -> {
                    Integer pid = product.getId();
                    product.setId(null);
                    product.setActivityId(activity.getId());
                    seckillProductService.save(product);

                    List<ProductAttrValue> attrValues = attrValueList.stream().filter(av -> av.getProductId().equals(pid)).collect(Collectors.toList());
                    attrValues.forEach(av -> {
                        av.setId(null);
                        av.setVersion(0);
                        av.setProductId(product.getId());
                        av.setMarketingType(ProductConstants.PRODUCT_MARKETING_TYPE_SECKILL);
                    });
                    productAttrValueService.saveBatch(attrValues);
                });
            }

            return Boolean.TRUE;
        });
        return execute;
    }

    /**
     * 秒杀活动分页列表
     */
    @Override
    public PageInfo<SeckillActivityPageResponse> activityPage(SeckillActivitySearchRequest request) {
        SystemAdmin admin = SecurityUtil.getLoginUserVo().getUser();
        PageInfo<SeckillActivity> activityPage = seckillActivityService.getActivityPage(request, admin.getMerId() > 0);
        List<SeckillActivity> activityList = activityPage.getList();
        if (CollUtil.isEmpty(activityList)) {
            return CommonPage.copyPageInfo(activityPage, new ArrayList<>());
        }
        DateTime dateTime = DateUtil.date();
        String dateStr = dateTime.toString(DateConstants.DATE_FORMAT_NUM);
        String hmStr = dateTime.toString(DateConstants.DATE_FORMAT_TIME_HHMM);
        List<SeckillActivityPageResponse> responseList = activityList.stream().map(activity -> {
            SeckillActivityPageResponse response = new SeckillActivityPageResponse();
            BeanUtils.copyProperties(activity, response);
            response.setProductNum(seckillProductService.getCountByActivityId(activity.getId()));
            List<SeckillActivityTime> timeList = seckillActivityTimeService.findByActivityId(activity.getId());
            List<String> timeStrList = CollUtil.newArrayList();
            timeList.forEach(time -> {
                Integer startTime = time.getStartTime();
                Integer endTime = time.getEndTime();
                String timeToStr = activityTimeToStr(startTime, endTime);
                timeStrList.add(timeToStr);
            });
            response.setTimeList(timeStrList);
            if (activity.getStatus() < 2) {
                Integer activityStatus = seckillActivityTimeService.getActivityStatus(activity.getId(), Integer.valueOf(dateStr), Integer.valueOf(hmStr));
                if (activityStatus.equals(1) && activity.getIsOpen().equals(0)) {
                    activityStatus = 0;
                }
                if (!activityStatus.equals(activity.getStatus())) {
                    activity.setStatus(activityStatus);
                    response.setStatus(activityStatus);
                    seckillActivityService.updateById(activity);
                }
            }
            if (!activity.getProCategory().equals("0")) {
                response.setProductCategoryNames(productCategoryService.getNameStrByIds(activity.getProCategory()));
            }
            return response;
        }).collect(Collectors.toList());
        return CommonPage.copyPageInfo(activityPage, responseList);
    }

    /**
     * 秒杀活动详情
     */
    @Override
    public SeckillActivityDetailResponse activityDetail(Integer id) {
        SeckillActivity seckillActivity = seckillActivityService.getById(id);
        if (ObjectUtil.isNull(seckillActivity) || seckillActivity.getIsDel()) {
            throw new CrmebException(MarketingResultCode.SECKILL_ACTIVITY_NOT_EXIST);
        }
        List<SeckillActivityTime> timeList = seckillActivityTimeService.findByActivityId(seckillActivity.getId());
        String timeIntervals = timeList.stream().map(e -> String.valueOf(e.getTimeIntervalId())).collect(Collectors.joining(","));
        List<String> timeStrList = CollUtil.newArrayList();
        timeList.forEach(time -> {
            Integer startTime = time.getStartTime();
            Integer endTime = time.getEndTime();
            String timeToStr = activityTimeToStr(startTime, endTime);
            timeStrList.add(timeToStr);
        });

        List<SeckillProduct> productList = seckillProductService.findByActivityId(seckillActivity.getId());
        SeckillActivityDetailResponse response = new SeckillActivityDetailResponse();
        BeanUtils.copyProperties(seckillActivity, response);
        response.setTimeIntervals(timeIntervals);
        response.setTimeList(timeStrList);
        if (CollUtil.isNotEmpty(productList)) {
            List<Integer> merIdList = productList.stream().map(SeckillProduct::getMerId).collect(Collectors.toList());
            Map<Integer, Merchant> merchantMap = merchantService.getMapByIdList(merIdList);
            List<Integer> cateIdList = productList.stream().map(SeckillProduct::getCategoryId).collect(Collectors.toList());
            Map<Integer, ProductCategory> categoryMap = productCategoryService.getMapByIdList(cateIdList);
            productList.forEach(p -> {
                List<ProductAttrValue> attrValueList = productAttrValueService.getListByProductIdAndType(p.getId(), p.getType(), ProductConstants.PRODUCT_MARKETING_TYPE_SECKILL, false);
                setShowStatus(attrValueList, p.getProductId(), p.getType());
                p.setAttrValue(attrValueList);
                p.setMerName(merchantMap.get(p.getMerId()).getName());
                p.setCategoryName(categoryMap.get(p.getCategoryId()).getName());
            });
        }
        response.setProductList(productList);
        return response;
    }

    /**
     * 设置秒杀商品sku的显示状态
     * @param seckillAttrValueList  秒杀商品SKU列表
     * @param masterProductId 主商品ID
     */
    private void setShowStatus(List<ProductAttrValue> seckillAttrValueList, Integer masterProductId, Integer type) {
        List<ProductAttrValue> attrValueList = productAttrValueService.getListByProductIdAndType(masterProductId, type, ProductConstants.PRODUCT_MARKETING_TYPE_BASE, false);
        for (ProductAttrValue attrValue : attrValueList) {
            for (ProductAttrValue seckillAttrValue : seckillAttrValueList) {
                if (seckillAttrValue.getMasterId().equals(attrValue.getId())) {
                    seckillAttrValue.setIsShow(attrValue.getIsShow());
                }
            }
        }
    }

    /**
     * 编辑秒杀活动
     */
    @Override
    public Boolean updateActivity(SeckillActivitySaveRequest request) {
        if (ObjectUtil.isNull(request.getId())) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "秒杀活动ID不能为空");
        }
        SeckillActivity seckillActivity = seckillActivityService.getById(request.getId());
        if (ObjectUtil.isNull(seckillActivity) || seckillActivity.getIsDel()) {
            throw new CrmebException(MarketingResultCode.SECKILL_ACTIVITY_NOT_EXIST);
        }
        if (seckillActivity.getStatus().equals(2)) {
            throw new CrmebException(MarketingResultCode.SECKILL_ACTIVITY_STATUS_ABNORMAL, "秒杀活动已结束，无法编辑");
        }
        if (seckillActivity.getIsOpen().equals(1)) {
            DateTime dateTime = DateUtil.date();
            String dateStr = dateTime.toString(DateConstants.DATE_FORMAT_NUM);
            String hmStr = dateTime.toString(DateConstants.DATE_FORMAT_TIME_HHMM);
            Integer activityStatus = seckillActivityTimeService.getActivityStatus(seckillActivity.getId(), Integer.valueOf(dateStr), Integer.valueOf(hmStr));
            if (activityStatus.equals(1)) {
                throw new CrmebException(MarketingResultCode.SECKILL_ACTIVITY_STATUS_ABNORMAL, "秒杀活动正在进行中，无法编辑");
            }
        }

        BeanUtils.copyProperties(request, seckillActivity);

        List<Integer> timeIntervalList = CrmebUtil.stringToArray(request.getTimeIntervals());
        List<SeckillActivityTime> activityTimeList = timeIntervalList.stream().map(ti -> {
            SeckillTimeInterval timeInterval = seckillTimeIntervalService.getById(ti);
            if (ObjectUtil.isNull(timeInterval) || timeInterval.getStatus().equals(0)) {
                throw new CrmebException(MarketingResultCode.SECKILL_TIME_INTERVAL_NOT_EXIST);
            }
            SeckillActivityTime activityTime = new SeckillActivityTime();
            activityTime.setSeckillId(seckillActivity.getId());
            activityTime.setTimeIntervalId(timeInterval.getId());
            activityTime.setStartDate(CrmebUtil.dateStrToInteger(seckillActivity.getStartDate()));
            activityTime.setEndDate(CrmebUtil.dateStrToInteger(seckillActivity.getEndDate()));
            activityTime.setStartTime(timeInterval.getStartTime());
            activityTime.setEndTime(timeInterval.getEndTime());
            return activityTime;
        }).collect(Collectors.toList());

        Boolean execute = transactionTemplate.execute(e -> {
            seckillActivityService.updateById(seckillActivity);
            seckillActivityTimeService.deleteByActivityId(seckillActivity.getId());
            seckillActivityTimeService.saveBatch(activityTimeList);
            return Boolean.TRUE;
        });
        return execute;
    }

    /**
     * 删除秒杀活动
     * @param id 活动id
     */
    @Override
    public Boolean deleteActivity(Integer id) {
        SeckillActivity seckillActivity = seckillActivityService.getById(id);
        if (ObjectUtil.isNull(seckillActivity) || seckillActivity.getIsDel()) {
            throw new CrmebException(MarketingResultCode.SECKILL_ACTIVITY_NOT_EXIST);
        }
        if (seckillActivity.getStatus() < 2 && seckillActivity.getIsOpen().equals(1)) {
            DateTime dateTime = DateUtil.date();
            String dateStr = dateTime.toString(DateConstants.DATE_FORMAT_NUM);
            String hmStr = dateTime.toString(DateConstants.DATE_FORMAT_TIME_HHMM);
            Integer activityStatus = seckillActivityTimeService.getActivityStatus(seckillActivity.getId(), Integer.valueOf(dateStr), Integer.valueOf(hmStr));
            if (activityStatus.equals(1)) {
                throw new CrmebException(MarketingResultCode.SECKILL_ACTIVITY_STATUS_ABNORMAL, "秒杀活动正在进行中，无法删除");
            }
        }
        seckillActivity.setIsDel(true);
        List<SeckillProduct> seckillProductList = seckillProductService.findByActivityId(id);
        Boolean execute = transactionTemplate.execute(e -> {
            seckillActivityService.updateById(seckillActivity);
            seckillActivityTimeService.deleteByActivityId(seckillActivity.getId());
            seckillProductList.forEach(p -> {
                p.setIsDel(true);
            });
            seckillProductService.updateBatchById(seckillProductList);
            return Boolean.TRUE;
        });
        return execute;
    }

    /**
     * 秒杀活动开关
     * @param id 活动id
     */
    @Override
    public Boolean switchActivity(Integer id) {
        SeckillActivity seckillActivity = seckillActivityService.getById(id);
        if (ObjectUtil.isNull(seckillActivity) || seckillActivity.getIsDel()) {
            throw new CrmebException(MarketingResultCode.SECKILL_ACTIVITY_NOT_EXIST);
        }
        if (seckillActivity.getStatus().equals(2)) {
            throw new CrmebException(MarketingResultCode.SECKILL_ACTIVITY_STATUS_ABNORMAL, "秒杀活动已结束");
        }
        if (seckillActivity.getIsOpen().equals(0)) {
            Integer proNum = seckillProductService.getCountByActivityId(seckillActivity.getId());
            if (proNum <= 0) {
                throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "秒杀活动必须有商品才可开启");
            }
        }
        seckillActivity.setIsOpen(seckillActivity.getIsOpen().equals(1) ? 0 : 1);
        return seckillActivityService.updateById(seckillActivity);
    }

    /**
     * 获取秒杀商品分页列表
     * @param request 搜索参数
     */
    @Override
    public PageInfo<SeckillProductPageResponse> getSeckillProductPage(SeckillProductSearchRequest request) {
        SystemAdmin admin = SecurityUtil.getLoginUserVo().getUser();
        if (admin.getMerId() > 0) {
            request.setMerIds(admin.getMerId().toString());
        }
        PageInfo<SeckillProductPageResponse> seckillProductPage = seckillProductService.getSeckillProductPage(request);
        List<SeckillProductPageResponse> responseList = seckillProductPage.getList();
        if (CollUtil.isEmpty(responseList)) {
            return seckillProductPage;
        }
        List<Integer> activityIdList = responseList.stream().map(SeckillProductPageResponse::getActivityId).collect(Collectors.toList());
        Map<Integer, List<String>> timeMap = getTimeMapByActivityIdList(activityIdList);
        responseList.forEach(e -> e.setTimeList(timeMap.get(e.getActivityId())));
        return seckillProductPage;
    }

    /**
     * 获取秒杀活动时间Map
     * @param activityIdList 活动ID数组
     * @return 秒杀活动时间Map
     */
    private Map<Integer, List<String>> getTimeMapByActivityIdList(List<Integer> activityIdList) {
        Map<Integer, List<String>> timeMap = new HashMap<>();
        activityIdList.forEach(aid -> {
            List<SeckillActivityTime> timeList = seckillActivityTimeService.findByActivityId(aid);
            List<String> timeStrList = CollUtil.newArrayList();
            timeList.forEach(time -> {
                Integer startTime = time.getStartTime();
                Integer endTime = time.getEndTime();
                String timeToStr = activityTimeToStr(startTime, endTime);
                timeStrList.add(timeToStr);
            });
            timeMap.put(aid, timeStrList);
        });
        return timeMap;
    }

    /**
     * 秒杀商品设置活动价
     */
    @Override
    public Boolean setProductPrice(SeckillProductPriceRequest request) {
        List<SeckillProductPriceProRequest> productRequestList = request.getProductList();
        List<SeckillProduct> productList = CollUtil.newArrayList();
        List<ProductAttrValue> attrValueList = CollUtil.newArrayList();
        SystemAdmin admin = SecurityUtil.getLoginUserVo().getUser();
        productRequestList.forEach(pro -> {
            SeckillProduct seckillProduct = seckillProductService.getById(pro.getId());
            if (ObjectUtil.isNull(seckillProduct) || seckillProduct.getIsDel()) {
                throw new CrmebException(MarketingResultCode.SECKILL_ACTIVITY_PRODUCT_NOT_EXIST);
            }
            if (admin.getMerId() > 0 && !admin.getMerId().equals(seckillProduct.getMerId())) {
                throw new CrmebException(MarketingResultCode.SECKILL_ACTIVITY_PRODUCT_NOT_EXIST);
            }
            List<SeckillProductPriceAttrValueRequest> valueRequestList = pro.getAttrValue();
            BigDecimal price = valueRequestList.stream().map(SeckillProductPriceAttrValueRequest::getActivityPrice).min(BigDecimal::compareTo).orElse(BigDecimal.ZERO);
            seckillProduct.setSeckillPrice(price);
            productList.add(seckillProduct);
            valueRequestList.forEach(e -> {
                ProductAttrValue attrValue = new ProductAttrValue();
                attrValue.setId(e.getId());
                attrValue.setPrice(e.getActivityPrice());
                attrValueList.add(attrValue);
            });
        });
        Boolean execute = transactionTemplate.execute(e -> {
            seckillProductService.updateBatchById(productList);
            productAttrValueService.updateBatchById(attrValueList);
            return Boolean.TRUE;
        });
        return execute;
    }

    /**
     * 秒杀商品强制下架
     */
    @Override
    public Boolean forceDownProduct(SeckillProductBatchRequest request) {
        return seckillProductService.forceDown(request.getIds());
    }

    /**
     * 秒杀商品删除
     */
    @Override
    public Boolean deleteProduct(SeckillProductBatchRequest request) {
        CrmebUtil.stringToArray(request.getIds());
        Boolean execute = transactionTemplate.execute(e -> {
            seckillProductService.delete(request.getIds());
            return Boolean.TRUE;
        });
        return execute;
    }

    /**
     * 秒杀商品上架
     */
    @Override
    public Boolean upProduct(SeckillProductBatchRequest request) {
        SystemAdmin admin = SecurityUtil.getLoginUserVo().getUser();
        return seckillProductService.up(request.getIds(), admin.getMerId());
    }

    /**
     * 秒杀商品下架
     */
    @Override
    public Boolean downProduct(SeckillProductBatchRequest request) {
        SystemAdmin admin = SecurityUtil.getLoginUserVo().getUser();
        return seckillProductService.down(request.getIds(), admin.getMerId());
    }

    /**
     * 商户端删除秒杀商品
     */
    @Override
    public Boolean merchantDeleteProduct(SeckillProductBatchRequest request) {
        List<Integer> proIdList = CrmebUtil.stringToArray(request.getIds());
        SystemAdmin admin = SecurityUtil.getLoginUserVo().getUser();
        proIdList.forEach(pid -> {
            SeckillProduct seckillProduct = seckillProductService.getById(pid);
            if (ObjectUtil.isNull(seckillProduct) || seckillProduct.getIsDel()) {
                throw new CrmebException(CommonResultCode.VALIDATE_FAILED, StrUtil.format("编号{}的商品不存在", pid));
            }
            if (!seckillProduct.getMerId().equals(admin.getMerId())) {
                throw new CrmebException(MarketingResultCode.SECKILL_ACTIVITY_PRODUCT_NOT_EXIST);
            }
            if (!seckillProduct.getAuditStatus().equals(3)) {
                throw new CrmebException(MarketingResultCode.SECKILL_ACTIVITY_STATUS_ABNORMAL, "只能删除审核失败的商品");
            }
        });

        Boolean execute = transactionTemplate.execute(e -> {
            seckillProductService.delete(request.getIds());
            productAttrValueService.batchDeleteByProductIdAndMarketingType(proIdList, ProductConstants.PRODUCT_MARKETING_TYPE_SECKILL);
            return Boolean.TRUE;
        });
        return execute;
    }

    /**
     * 秒杀商品撤回审核
     * @param id 秒杀商品ID
     */
    @Override
    public Boolean withdrawProductAudit(Integer id) {
        SeckillProduct seckillProduct = seckillProductService.getById(id);
        if (ObjectUtil.isNull(seckillProduct) || seckillProduct.getIsDel()) {
            throw new CrmebException(MarketingResultCode.SECKILL_ACTIVITY_PRODUCT_NOT_EXIST);
        }
        SystemAdmin admin = SecurityUtil.getLoginUserVo().getUser();
        if (!admin.getMerId().equals(seckillProduct.getMerId())) {
            throw new CrmebException(MarketingResultCode.SECKILL_ACTIVITY_PRODUCT_NOT_EXIST);
        }
        if (!seckillProduct.getAuditStatus().equals(1)) {
            throw new CrmebException(MarketingResultCode.SECKILL_ACTIVITY_STATUS_ABNORMAL, "秒杀商品审核状态异常，请刷新后再试");
        }
        seckillProduct.setIsDel(false);
        seckillProduct.setAuditStatus(3);
        seckillProduct.setReason("商户撤销审核");
        return seckillProductService.updateById(seckillProduct);
    }

    /**
     * 商户秒杀商品添加
     */
    @Override
    public Boolean merchantAddProduct(SeckillProductAddRequest request) {
        SystemAdmin admin = SecurityUtil.getLoginUserVo().getUser();
        SeckillActivity seckillActivity = seckillActivityService.getById(request.getId());
        if (ObjectUtil.isNull(seckillActivity) || seckillActivity.getIsDel()) {
            throw new CrmebException(MarketingResultCode.SECKILL_ACTIVITY_NOT_EXIST);
        }
        if (seckillActivity.getStatus().equals(2)) {
            throw new CrmebException(MarketingResultCode.SECKILL_ACTIVITY_STATUS_ABNORMAL, "秒杀活动已结束");
        }
        Merchant merchant = merchantService.getByIdException(admin.getMerId());
        if (seckillActivity.getMerStars() > merchant.getStarLevel()) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, StrUtil.format("参加此活动最低需{}星商户", seckillActivity.getMerStars()));
        }
        return commonAddProduct(seckillActivity.getId(), request.getProductList());
    }

    /**
     * 平台秒杀商品添加
     */
    @Override
    public Boolean platAddProduct(SeckillProductAddRequest request) {
        SeckillActivity seckillActivity = seckillActivityService.getById(request.getId());
        if (ObjectUtil.isNull(seckillActivity) || seckillActivity.getIsDel()) {
            throw new CrmebException(MarketingResultCode.SECKILL_ACTIVITY_NOT_EXIST);
        }
        return commonAddProduct(seckillActivity.getId(), request.getProductList());
    }

    /**
     * 秒杀商品审核
     */
    @Override
    public Boolean auditProduct(ProductAuditRequest request) {
        if (request.getAuditStatus().equals("fail") && StrUtil.isEmpty(request.getReason())) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "审核拒绝请填写拒绝原因");
        }
        SeckillProduct seckillProduct = seckillProductService.getById(request.getId());
        if (ObjectUtil.isNull(seckillProduct) || seckillProduct.getIsDel()) {
            throw new CrmebException(MarketingResultCode.SECKILL_ACTIVITY_PRODUCT_NOT_EXIST);
        }
        if (!seckillProduct.getAuditStatus().equals(1)) {
            throw new CrmebException(MarketingResultCode.SECKILL_ACTIVITY_PRODUCT_STATUS_ABNORMAL, "秒杀商品审核状态异常，请刷新后再试");
        }
        if (request.getAuditStatus().equals("fail")) {
            seckillProduct.setAuditStatus(3);
            seckillProduct.setReason(request.getReason());
        } else {
            seckillProduct.setAuditStatus(2);
            seckillProduct.setReason("");
        }
        return seckillProductService.updateById(seckillProduct);
    }

    /**
     * 添加秒杀商品
     * @param activityId 秒杀活动ID
     * @param productRequestList 商品列表
     */
    private Boolean commonAddProduct(Integer activityId, List<SeckillActivityProductRequest> productRequestList) {
        SystemAdmin admin = SecurityUtil.getLoginUserVo().getUser();
        List<SeckillProduct> productList = CollUtil.newArrayList();
        List<ProductAttrValue> attrValueList = CollUtil.newArrayList();

        productRequestList.forEach(productRequest -> {
            Product product = productService.getById(productRequest.getId());
            if (ObjectUtil.isNull(product) || !product.getMarketingType().equals(ProductConstants.PRODUCT_MARKETING_TYPE_BASE)) {
                throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "没有对应的商品信息，商品ID = " + productRequest.getId());
            }
            if (admin.getMerId() > 0 && !admin.getMerId().equals(product.getMerId())) {
                throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "没有对应的商品信息，商品ID = " + productRequest.getId());
            }
            List<ProductAttrValue> productAttrValueList = productAttrValueService.getListByProductIdAndType(product.getId(), product.getType(), ProductConstants.PRODUCT_MARKETING_TYPE_BASE, false);
            if (CollUtil.isEmpty(productAttrValueList)) {
                throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "没有对应的商品sku信息，商品ID = " + productRequest.getId());
            }

            List<SeckillActivityProductAttrValueRequest> attrValueRequestList = productRequest.getAttrValue();
            BigDecimal activityPrice = attrValueRequestList.stream().map(SeckillActivityProductAttrValueRequest::getActivityPrice).min(BigDecimal::compareTo).orElse(BigDecimal.ZERO);
            if (activityPrice.compareTo(BigDecimal.ZERO) < 1) {
                throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "秒杀价格不能小于等于0");
            }
            int quota = attrValueRequestList.stream().mapToInt(SeckillActivityProductAttrValueRequest::getQuota).sum();
            if (quota < 1) {
                throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "添加秒杀商品限量必须大于0，秒杀商品名称:" + product.getName());
            }
            SeckillProduct seckillProduct = new SeckillProduct();
            BeanUtils.copyProperties(product, seckillProduct);
            seckillProduct.setSeckillPrice(activityPrice);
            seckillProduct.setQuota(quota);
            seckillProduct.setQuotaShow(quota);
            seckillProduct.setSales(0);
            seckillProduct.setSort(productRequest.getSort());
            seckillProduct.setAuditStatus(admin.getMerId() > 0 ? 1 : 2);
            seckillProduct.setId(product.getId());
            seckillProduct.setActivityId(activityId);
            seckillProduct.setProductId(product.getId());

            productAttrValueList.forEach(v -> {
                SeckillActivityProductAttrValueRequest attrValueRequest = attrValueRequestList.stream().filter(e -> e.getAttrValueId().equals(v.getId())).findFirst().orElse(null);
                if (ObjectUtil.isNull(attrValueRequest)) {
                    throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "添加秒杀商品未找到对应SKU，秒杀商品名称:" + product.getName() + ",SKU = " + v.getSku());
                }
                if (attrValueRequest.getActivityPrice().compareTo(BigDecimal.ZERO) < 1) {
                    throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "秒杀价格不能小于等于0");
                }
                if (attrValueRequest.getQuota() > v.getStock()) {
                    throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "添加秒杀商品SKU限量不能超过库存，秒杀商品名称:" + product.getName());
                }
                v.setQuota(attrValueRequest.getQuota());
                v.setQuotaShow(attrValueRequest.getQuota());
                v.setOtPrice(v.getPrice());
                v.setPrice(attrValueRequest.getActivityPrice());
                v.setMasterId(v.getId());
                v.setStock(attrValueRequest.getQuota());
                v.setSales(0);
            });
            productList.add(seckillProduct);
            attrValueList.addAll(productAttrValueList);
        });
        if (CollUtil.isEmpty(productList)) {
            return Boolean.TRUE;
        }
        return transactionTemplate.execute(e -> {
            try {
                productList.forEach(product -> {
                    Integer pid = product.getId();
                    product.setId(null);
                    boolean result = seckillProductService.save(product);
                    if (!result) {
                        throw new CrmebException("创建秒杀商品失败");
                    }
                    List<ProductAttrValue> attrValues = attrValueList.stream().filter(av -> av.getProductId().equals(pid)).collect(Collectors.toList());
                    attrValues.forEach(av -> {
                        av.setId(null);
                        av.setVersion(0);
                        av.setProductId(product.getId());
                        av.setMarketingType(ProductConstants.PRODUCT_MARKETING_TYPE_SECKILL);
                    });
                    result = productAttrValueService.saveBatch(attrValues);
                    if (!result) {
                        throw new CrmebException("生成秒杀商品sku失败");
                    }

                });
            } catch (Exception exception) {
                e.setRollbackOnly();
                logger.error("添加秒杀商品失败");
                return Boolean.FALSE;
            }
            return Boolean.TRUE;
        });
    }

    /**
     * 活动时间转字符串展示
     * @param startTime 开始时间
     * @param endTime 结束时间
     */
    private String activityTimeToStr(Integer startTime, Integer endTime) {
        String start = "";
        String end = "";
        if (startTime.equals(0)) {
            start = "00:00";
        }
        if (0 < startTime && startTime < 1000) {
            String startTimeStr = startTime.toString();
            start = "0" + startTimeStr.substring(0,1) + ":" + startTimeStr.substring(1);
        }
        if (endTime < 1000) {
            String endTimeStr = endTime.toString();
            end = "0" + endTimeStr.substring(0,1) + ":" + endTimeStr.substring(1);
        }
        if (startTime >= 1000) {
            String startTimeStr = startTime.toString();
            start = startTimeStr.substring(0,2) + ":" + startTimeStr.substring(2);
        }
        if (endTime >= 1000) {
            String endTimeStr = endTime.toString();
            end = endTimeStr.substring(0,2) + ":" + endTimeStr.substring(2);
        }
        return start + " - " + end;
    }

    /**
     * 秒杀日期校验
     */
    private void validateSeckillDate(SeckillActivitySaveRequest request) {
        DateTime start = DateUtil.parse(request.getStartDate(), DateConstants.DATE_FORMAT_DATE);
        DateTime end = DateUtil.parse(request.getEndDate(), DateConstants.DATE_FORMAT_DATE);
        if (start.getTime() > end.getTime()) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "秒杀开始日期不能大于结束日期");
        }
        request.setStartDate(start.toString(DateConstants.DATE_FORMAT_DATE));
        request.setEndDate(end.toString(DateConstants.DATE_FORMAT_DATE));
    }

    /**
     * 获取时段时间
     * @param timeStr 时间字符
     */
    private String getTimeIntervalTime(String timeStr) {
        String time;
        switch (timeStr.length()) {
            case 1:
                time = "000" + timeStr;
                break;
            case 2:
                time = "00" + timeStr;
                break;
            case 3:
                time = "0" + timeStr;
                break;
            default:
                time = timeStr;
        }
//        String time = timeStr.length() == 3 ? "0" + timeStr : timeStr;
//        if (timeStr.length() == 1) {
//            time = "0000";
//        }
        return time.substring(0,2) + ":" + time.substring(2);
    }

    /**
     * 时间段赋值
     */
    private void setTimeIntervalRequest(SeckillTimeIntervalRequest request, SeckillTimeInterval timeInterval) {
        if (request.getStartTime().length() != 5 || request.getEndTime().length() != 5) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "时间参数不正确 例如:01:00,02:00");
        }
        if (!request.getStartTime().contains(":") || !request.getEndTime().contains(":")) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "时间参数不正确 例如:01:00,02:00");
        }

        String[] splitStart = request.getStartTime().split(":");
        for (String s : splitStart) {
            if (StrUtil.isBlank(s) || s.trim().length() != 2) {
                throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "时间参数不正确 例如:01:00,02:00");
            }
        }
        String start = splitStart[0].trim().concat(splitStart[1].trim());
        Integer startTime = Integer.valueOf(start);

        String[] splitEnd = request.getEndTime().split(":");
        for (String s : splitEnd) {
            if (StrUtil.isBlank(s) || s.trim().length() != 2) {
                throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "时间参数不正确 例如:01:00,02:00");
            }
        }
        String end = splitEnd[0].trim().concat(splitEnd[1].trim());
        Integer endTime = Integer.valueOf(end);

        if (startTime >= endTime) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "开始时间必须小于结束时间");
        }
        timeInterval.setStartTime(startTime);
        timeInterval.setEndTime(endTime);
    }


}
