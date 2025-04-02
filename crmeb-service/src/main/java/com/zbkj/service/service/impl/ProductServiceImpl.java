package com.zbkj.service.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.URLUtil;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.parser.Feature;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zbkj.common.config.CrmebConfig;
import com.zbkj.common.constants.*;
import com.zbkj.common.exception.CrmebException;
import com.zbkj.common.model.admin.SystemAdmin;
import com.zbkj.common.model.cdkey.CdkeyLibrary;
import com.zbkj.common.model.coupon.Coupon;
import com.zbkj.common.model.coupon.CouponProduct;
import com.zbkj.common.model.coupon.CouponUser;
import com.zbkj.common.model.express.ShippingTemplates;
import com.zbkj.common.model.merchant.Merchant;
import com.zbkj.common.model.merchant.MerchantInfo;
import com.zbkj.common.model.product.*;
import com.zbkj.common.model.seckill.SeckillActivity;
import com.zbkj.common.model.system.SystemForm;
import com.zbkj.common.page.CommonPage;
import com.zbkj.common.request.*;
import com.zbkj.common.request.merchant.MerchantProductSearchRequest;
import com.zbkj.common.response.*;
import com.zbkj.common.response.productTag.ProductTagsForSearchResponse;
import com.zbkj.common.result.*;
import com.zbkj.common.utils.CrmebDateUtil;
import com.zbkj.common.utils.CrmebUtil;
import com.zbkj.common.utils.SecurityUtil;
import com.zbkj.common.vo.DateLimitUtilVo;
import com.zbkj.common.vo.MyRecord;
import com.zbkj.common.vo.OnePassUserInfoVo;
import com.zbkj.common.vo.SimpleProductVo;
import com.zbkj.service.dao.ProductDao;
import com.zbkj.service.service.*;
import com.zbkj.service.service.groupbuy.GroupBuyActivitySkuService;
import com.zbkj.service.util.ProductUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 订单原始业务
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
public class ProductServiceImpl extends ServiceImpl<ProductDao, Product>
        implements ProductService {

    private final Logger LOGGER = LoggerFactory.getLogger(ProductServiceImpl.class);

    @Resource
    private ProductDao dao;

    @Autowired
    private ProductAttrService attrService;
    @Autowired
    private ProductAttrValueService productAttrValueService;
    @Autowired
    private ProductDescriptionService productDescriptionService;
    @Autowired
    private SystemAttachmentService systemAttachmentService;
    @Autowired
    private CouponProductService couponProductService;
    @Autowired
    private ProductCouponService productCouponService;
    @Autowired
    private CouponUserService couponUserService;
    @Autowired
    private TransactionTemplate transactionTemplate;
    @Autowired
    private MerchantService merchantService;
    @Autowired
    private ProductCategoryService productCategoryService;
    @Autowired
    private MerchantInfoService merchantInfoService;
    @Autowired
    private UserService userService;
    @Autowired
    private CrmebConfig crmebConfig;
    @Autowired
    private CouponService couponService;
    @Autowired
    private ProductUtils productUtils;
    @Autowired
    private SystemConfigService systemConfigService;
    @Autowired
    private OnePassService onePassService;
    @Autowired
    private ProductRelationService productRelationService;
    @Autowired
    private CartService cartService;
    @Autowired
    private ProductReplyService productReplyService;
    @Autowired
    private SeckillActivityService seckillActivityService;
    @Autowired
    private ActivityStyleService activityStyleService;
    @Autowired
    private ProductTagService productTagService;
    @Autowired
    private CdkeyLibraryService cdkeyLibraryService;
    @Autowired
    private ShippingTemplatesService shippingTemplatesService;
    @Autowired
    private ProductGuaranteeService productGuaranteeService;
    @Autowired
    private SystemFormService systemFormService;
    @Autowired
    private SeckillProductService seckillProductService;
    @Autowired
    private GroupBuyActivitySkuService groupBuyActivitySkuService;
    @Autowired
    private ProductAttributeService productAttributeService;
    @Autowired
    private ProductAttributeOptionService productAttributeOptionService;

    /**
     * 获取产品列表Admin
     *
     * @param request 筛选参数
     * @return PageInfo
     */
    @Override
    public PageInfo<AdminProductListResponse> getAdminList(MerProductSearchRequest request, SystemAdmin admin) {
        //带 Product 类的多条件查询
        LambdaQueryWrapper<Product> lqw = new LambdaQueryWrapper<>();
        lqw.eq(Product::getMerId, admin.getMerId());
        setAdminListWrapperByType(lqw, request.getType(), admin.getMerId());
        //关键字搜索
        if (StrUtil.isNotBlank(request.getKeywords())) {
            String keywords = URLUtil.decode(request.getKeywords());
            lqw.and(i -> i.like(Product::getName, keywords)
                    .or().apply(" find_in_set({0}, keyword)", keywords));
        }
        lqw.apply(StrUtil.isNotBlank(request.getCateId()), "FIND_IN_SET({0}, cate_id)", request.getCateId());
        if (ObjectUtil.isNotNull(request.getCategoryId())) {
            lqw.eq(Product::getCategoryId, request.getCategoryId());
        }
        if (ObjectUtil.isNotNull(request.getProductType())) {
            lqw.eq(Product::getType, request.getProductType());
        }
        if (ObjectUtil.isNotNull(request.getIsPaidMember())) {
            lqw.eq(Product::getIsPaidMember, request.getIsPaidMember());
        }
        lqw.orderByDesc(Product::getSort).orderByDesc(Product::getId);
        Page<Product> productPage = PageHelper.startPage(request.getPage(), request.getLimit());
        List<Product> products = dao.selectList(lqw);
        if (CollUtil.isEmpty(products)) {
            return CommonPage.copyPageInfo(productPage, CollUtil.newArrayList());
        }
        List<AdminProductListResponse> productResponses = new ArrayList<>();
        for (Product product : products) {
            AdminProductListResponse productResponse = new AdminProductListResponse();
            BeanUtils.copyProperties(product, productResponse);
            // 收藏数
            productResponse.setCollectCount(productRelationService.getCollectCountByProductId(product.getId()));
            productResponses.add(productResponse);
        }
        // 多条sql查询处理分页正确
        return CommonPage.copyPageInfo(productPage, productResponses);
    }

    /**
     * 根据id集合获取商品简单信息
     *
     * @param productIds id集合
     * @return 商品信息
     */
    @Override
    public List<SimpleProductVo> getSimpleListInIds(List<Integer> productIds) {
        LambdaQueryWrapper<Product> lqw = new LambdaQueryWrapper<>();
        lqw.select(Product::getId, Product::getName, Product::getImage, Product::getPrice, Product::getStock);
        lqw.in(Product::getId, productIds);
        lqw.eq(Product::getIsDel, false);
        List<Product> selectList = dao.selectList(lqw);
        return selectList.stream().map(e -> {
            SimpleProductVo vo = new SimpleProductVo();
            BeanUtils.copyProperties(e, vo);
            return vo;
        }).collect(Collectors.toList());
    }

    /**
     * 新增产品
     *
     * @param request 新增产品request对象
     * @return 新增结果
     */
    @Override
    public Boolean save(ProductAddRequest request) {
        // 商品信息校验
        saveProductValidator(request.getType(), request.getSpecType(), request.getAttrValueList(),
                request.getIsSub(), request.getCategoryId(), request.getIsPaidMember());

        SystemAdmin admin = SecurityUtil.getLoginUserVo().getUser();
        if (ObjectUtil.isNotNull(request.getSystemFormId()) && request.getSystemFormId() > 0) {
            if (!systemFormService.isExist(request.getSystemFormId(), admin.getMerId())) {
                throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "系统表单不存在");
            }
        }
        Map<Integer, CdkeyLibrary> cdkeyMap = new HashMap<>();
        if (request.getType().equals(ProductConstants.PRODUCT_TYPE_CDKEY)) {
            List<Integer> cdkIdList = request.getAttrValueList().stream().map(ProductAttrValueAddRequest::getCdkeyId).distinct().collect(Collectors.toList());
            List<CdkeyLibrary> cdkeyLibraryList = cdkeyLibraryService.findByIdList(cdkIdList);
            for (CdkeyLibrary cdkeyLibrary : cdkeyLibraryList) {
                if (!admin.getMerId().equals(cdkeyLibrary.getMerId())) {
                    throw new CrmebException(ProductResultCode.PRODUCT_CDKEY_LIBRARY_NOT_EXIST);
                }
                if (cdkeyLibrary.getProductId() > 0) {
                    throw new CrmebException(ProductResultCode.PRODUCT_CDKEY_LIBRARY_INTERACTION);
                }
                cdkeyMap.put(cdkeyLibrary.getId(), cdkeyLibrary);
            }
        }
        if (StrUtil.isBlank(request.getKeyword())) {
            request.setKeyword("");
        } else if (request.getKeyword().length() > 32) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "关键字长度不能超过32个字符");
        }

        Merchant merchant = merchantService.getByIdException(admin.getMerId());

        Product product = new Product();
        BeanUtils.copyProperties(request, product);
        product.setId(null);
        product.setMerId(admin.getMerId());
        product.setMarketingType(ProductConstants.PRODUCT_MARKETING_TYPE_BASE);
        if (product.getType().equals(ProductConstants.PRODUCT_TYPE_CLOUD)
                || product.getType().equals(ProductConstants.PRODUCT_TYPE_CDKEY)
                || product.getType().equals(ProductConstants.PRODUCT_TYPE_VIRTUALLY)) {

            product.setDeliveryMethod("1");
            product.setTempId(0);
            // 非虚拟商品退款开关处理
            if (!product.getType().equals(ProductConstants.PRODUCT_TYPE_VIRTUALLY)) {
                product.setRefundSwitch(true);
            }
        }

        String cdnUrl = systemAttachmentService.getCdnUrl();
        //主图
        product.setImage(systemAttachmentService.clearPrefix(product.getImage(), cdnUrl));
        //轮播图
        product.setSliderImage(systemAttachmentService.clearPrefix(product.getSliderImage(), cdnUrl));
        // 展示图
        if (StrUtil.isNotBlank(product.getFlatPattern())) {
            product.setFlatPattern(systemAttachmentService.clearPrefix(product.getFlatPattern(), cdnUrl));
        }

        List<ProductAttrValueAddRequest> attrValueAddRequestList = request.getAttrValueList();
        //计算价格
        ProductAttrValueAddRequest minAttrValue = attrValueAddRequestList.stream().min(Comparator.comparing(ProductAttrValueAddRequest::getPrice)).get();
        product.setPrice(minAttrValue.getPrice());
        product.setOtPrice(minAttrValue.getOtPrice());
        product.setCost(minAttrValue.getCost());
        product.setAuditStatus(ProductConstants.AUDIT_STATUS_EXEMPTION);
        product.setIsAudit(false);
        product.setIsShow(false);
        if (product.getIsPaidMember()) {
            product.setVipPrice(minAttrValue.getVipPrice());
        }

        List<ProductAttrAddRequest> addRequestList = request.getAttrList();
        List<ProductAttribute> attrList = new ArrayList<>();
        Map<String, List<ProductAttributeOption>> optionMap = new HashMap<>();

        addRequestList.forEach(attrRequest -> {
            ProductAttribute attr = new ProductAttribute();
            attr.setAttributeName(attrRequest.getAttributeName());
            attr.setIsShowImage(attrRequest.getIsShowImage());
            attr.setSort(ObjectUtil.isNotNull(attrRequest.getSort()) ? attrRequest.getSort() : 0);
            List<ProductAttrOptionAddRequest> optionRequestList = attrRequest.getOptionList();
            List<ProductAttributeOption> attrOptionList = optionRequestList.stream().map(optionRequest -> {
                ProductAttributeOption option = new ProductAttributeOption();
                option.setOptionName(optionRequest.getOptionName());
                option.setSort(ObjectUtil.isNotNull(optionRequest.getSort()) ? optionRequest.getSort() : 0);
                option.setImage(StrUtil.isNotBlank(optionRequest.getImage()) ? systemAttachmentService.clearPrefix(optionRequest.getImage(), cdnUrl) : "");
                return option;
            }).collect(Collectors.toList());
            attrList.add(attr);
            optionMap.put(attr.getAttributeName(), attrOptionList);
        });

        List<ProductAttrValue> attrValueList = attrValueAddRequestList.stream().map(e -> {
            ProductAttrValue attrValue = new ProductAttrValue();
            BeanUtils.copyProperties(e, attrValue);
            attrValue.setId(null);
            attrValue.setSku(getSku(e.getAttrValue()));
            attrValue.setQuota(0);
            attrValue.setQuotaShow(0);
            attrValue.setType(product.getType());
            attrValue.setMarketingType(product.getMarketingType());
            attrValue.setImage(systemAttachmentService.clearPrefix(e.getImage(), cdnUrl));
            if (product.getType().equals(ProductConstants.PRODUCT_TYPE_CDKEY)) {
                CdkeyLibrary cdkeyLibrary = cdkeyMap.get(e.getCdkeyId());
                attrValue.setStock(cdkeyLibrary.getTotalNum() - cdkeyLibrary.getUsedNum());
            }
            attrValue.setVipPrice(product.getIsPaidMember() ? e.getVipPrice() : BigDecimal.ZERO);
            return attrValue;
        }).collect(Collectors.toList());

        product.setStock(attrValueList.stream().mapToInt(ProductAttrValue::getStock).sum());

        // 处理富文本
        ProductDescription spd = new ProductDescription();
        spd.setDescription(StrUtil.isNotBlank(request.getContent()) ? systemAttachmentService.clearPrefix(request.getContent(), cdnUrl) : "");
        spd.setType(product.getType());
        spd.setMarketingType(product.getMarketingType());

        if (merchant.getProductSwitch() && request.getIsAutoSubmitAudit()) {
            product.setIsAutoUp(request.getIsAutoUp());
        } else {
            product.setIsAutoUp(request.getIsAutoUp());
        }

        Boolean execute = transactionTemplate.execute(e -> {
            if (merchant.getProductSwitch()) {// 开启商品审核
                product.setAuditStatus(ProductConstants.AUDIT_STATUS_WAIT);
            }
            save(product);

            attrList.forEach(attr -> attr.setProductId(product.getId()));
            productAttributeService.saveBatch(attrList);
            attrList.forEach(attr -> {
                List<ProductAttributeOption> optionList = optionMap.get(attr.getAttributeName());
                optionList.forEach(option -> {
                    option.setProductId(attr.getProductId());
                    option.setAttributeId(attr.getId());
                });
                productAttributeOptionService.saveBatch(optionList);
            });

            attrValueList.forEach(value -> value.setProductId(product.getId()));
            productAttrValueService.saveBatch(attrValueList, 100);

            spd.setProductId(product.getId());
            productDescriptionService.deleteByProductId(product.getId(), product.getType(), product.getMarketingType());
            productDescriptionService.save(spd);

            if (product.getType().equals(ProductConstants.PRODUCT_TYPE_CDKEY)) {
                List<CdkeyLibrary> cdkeyLibraryList = attrValueList.stream().map(attrValue -> {
                    CdkeyLibrary cdkeyLibrary = new CdkeyLibrary();
                    cdkeyLibrary.setId(attrValue.getCdkeyId());
                    cdkeyLibrary.setProductId(product.getId());
                    cdkeyLibrary.setProductAttrValueId(attrValue.getId());
                    return cdkeyLibrary;
                }).collect(Collectors.toList());
                cdkeyLibraryService.updateBatchById(cdkeyLibraryList);
            }

            if (CollUtil.isNotEmpty(request.getCouponIds())) {
                List<ProductCoupon> couponList = new ArrayList<>();
                for (Integer couponId : request.getCouponIds()) {
                    ProductCoupon spc = new ProductCoupon();
                    spc.setProductId(product.getId());
                    spc.setCouponId(couponId);
                    spc.setAddTime(CrmebDateUtil.getNowTime());
                    couponList.add(spc);
                }
                productCouponService.saveBatch(couponList);
            }
            return Boolean.TRUE;
        });

        if (execute) {
            // 自动提审
            if (merchant.getProductSwitch() && request.getIsAutoSubmitAudit()) {
                LambdaUpdateWrapper<Product> wrapper = Wrappers.lambdaUpdate();
                wrapper.set(Product::getIsAudit, true);
                wrapper.set(Product::getIsShow, false);
                wrapper.eq(Product::getId, product.getId());
                return update(wrapper);
            }
            // 自动上架
            if (!merchant.getProductSwitch() && request.getIsAutoUp()) {
                if (!merchant.getIsSwitch()) {
                    LOGGER.error("商户未开启无法自动上架，merId = {}, proId = {}", merchant.getId(), product.getId());
                    return true;
                }
                LambdaUpdateWrapper<Product> wrapper = Wrappers.lambdaUpdate();
                wrapper.set(Product::getIsShow, true);
                wrapper.eq(Product::getId, product.getId());
                return update(wrapper);
            }
        }
        return execute;
    }

    /**
     * 保存商品校验器
     *
     * @param type          商品类型
     * @param specType      商品规格 false单 ture多
     * @param attrValueList sku列表
     * @param isSub         是否单独分佣
     * @param categoryId    系统商品分类ID
     * @param isPaidMember  是否付费会员商品
     */
    private void saveProductValidator(Integer type, Boolean specType, List<ProductAttrValueAddRequest> attrValueList,
                                      Boolean isSub, Integer categoryId, Boolean isPaidMember) {
        validateProductBaseType(type);
        // 多规格需要校验规格参数
        if (!specType) {
            if (attrValueList.size() > 1) {
                throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "单规格商品属性值不能大于1");
            }
        }
        if (isSub) {
            attrValueList.forEach(av -> {
                int brokerageRatio = av.getBrokerage() + av.getBrokerageTwo();
                if (brokerageRatio > crmebConfig.getRetailStoreBrokerageRatio()) {
                    throw new CrmebException(CommonResultCode.VALIDATE_FAILED, StrUtil.format("一二级返佣比例之和范围为 0~{}", crmebConfig.getRetailStoreBrokerageRatio()));
                }
            });
        }
        ProductCategory productCategory = productCategoryService.getById(categoryId);
        if (ObjectUtil.isNull(productCategory) || productCategory.getIsDel()) {
            throw new CrmebException(ProductResultCode.PRODUCT_PLAT_CATEGORY_NOT_EXIST);
        }
        if (productCategory.getLevel() < 3) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "必须选择商品平台第三级分类");
        }

        validateProductAttrValue(type, attrValueList);
        // 付费会员价校验
        if (isPaidMember) {
            attrValueList.forEach(value -> {
                if (ObjectUtil.isNull(value.getVipPrice()) || value.getVipPrice().compareTo(BigDecimal.ZERO) <= 0 || value.getVipPrice().compareTo(value.getPrice()) >= 0) {
                    throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "请正确设置商品规格vip价格");
                }
            });
        }

    }

    /**
     * 校验商品基础类型
     *
     * @param type 商品类型
     */
    private void validateProductBaseType(Integer type) {
        List<Integer> typeList = CollUtil.newArrayList();
        typeList.add(ProductConstants.PRODUCT_TYPE_NORMAL);
        typeList.add(ProductConstants.PRODUCT_TYPE_INTEGRAL);
        typeList.add(ProductConstants.PRODUCT_TYPE_VIRTUALLY);
        typeList.add(ProductConstants.PRODUCT_TYPE_COMPONENT);
        typeList.add(ProductConstants.PRODUCT_TYPE_CLOUD);
        typeList.add(ProductConstants.PRODUCT_TYPE_CDKEY);
        if (!typeList.contains(type)) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "未知的商品类型");
        }
    }

    private void validateProductAttrValue(Integer type, List<ProductAttrValueAddRequest> attrValueList) {
        if (ProductConstants.PRODUCT_TYPE_CLOUD.equals(type)) {
            for (ProductAttrValueAddRequest attrValue : attrValueList) {
                if (StrUtil.isBlank(attrValue.getExpand())) {
                    throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "请填写云盘商品规格属性对应的链接");
                }
            }
        }
        if (ProductConstants.PRODUCT_TYPE_CDKEY.equals(type)) {
            for (ProductAttrValueAddRequest attrValue : attrValueList) {
                if (ObjectUtil.isNull(attrValue.getCdkeyId()) || attrValue.getCdkeyId() <= 0) {
                    throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "请选择卡密商品规格属性对应的卡密库");
                }
            }
            List<Integer> cdkIdList = attrValueList.stream().map(ProductAttrValueAddRequest::getCdkeyId).distinct().collect(Collectors.toList());
            if (cdkIdList.size() != attrValueList.size()) {
                throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "卡密商品规格属性中有重复的卡密库");
            }
        }
    }

    /**
     * 商品sku
     *
     * @param attrValue json字符串
     * @return sku
     */
    private String getSku(String attrValue) {
        LinkedHashMap<String, String> linkedHashMap = JSONObject.parseObject(attrValue, LinkedHashMap.class, Feature.OrderedField);
        Iterator<Map.Entry<String, String>> iterator = linkedHashMap.entrySet().iterator();
        List<String> strings = CollUtil.newArrayList();
        while (iterator.hasNext()) {
            Map.Entry<String, String> next = iterator.next();
            strings.add(next.getValue());
        }
        return String.join(",", strings);
    }

    /**
     * 更新商品信息
     *
     * @param productRequest 商品参数
     * @return 更新结果
     */
    @Override
    public Boolean update(ProductAddRequest productRequest, SystemAdmin admin) {
        if (ObjectUtil.isNull(productRequest.getId())) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "商品ID不能为空");
        }
        saveProductValidator(productRequest.getType(), productRequest.getSpecType(), productRequest.getAttrValueList(),
                productRequest.getIsSub(), productRequest.getCategoryId(), productRequest.getIsPaidMember());

        Product tempProduct = getById(productRequest.getId());
        if (ObjectUtil.isNull(tempProduct) || !admin.getMerId().equals(tempProduct.getMerId())) {
            throw new CrmebException(ProductResultCode.PRODUCT_NOT_EXIST);
        }
        if (tempProduct.getIsRecycle() || tempProduct.getIsDel()) {
            throw new CrmebException(ProductResultCode.PRODUCT_DELETE);
        }
        if (tempProduct.getIsShow()) {
            throw new CrmebException(ProductResultCode.PRODUCT_IS_SHOW);
        }
        if (tempProduct.getIsAudit()) {
            throw new CrmebException(ProductResultCode.PRODUCT_AUDIT_ING);
        }
        if (!tempProduct.getType().equals(productRequest.getType())) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "商品类型不可编辑");
        }
        if (productRequest.getSystemFormId() > 0) {
            if (!systemFormService.isExist(productRequest.getSystemFormId(), admin.getMerId())) {
                throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "系统表单不存在");
            }
        }

        Map<Integer, CdkeyLibrary> cdkeyMap = new HashMap<>();
        if (tempProduct.getType().equals(ProductConstants.PRODUCT_TYPE_CDKEY)) {
            List<Integer> cdkIdList = productRequest.getAttrValueList().stream().map(ProductAttrValueAddRequest::getCdkeyId).distinct().collect(Collectors.toList());
            List<CdkeyLibrary> cdkeyLibraryList = cdkeyLibraryService.findByIdList(cdkIdList);
            for (CdkeyLibrary cdkeyLibrary : cdkeyLibraryList) {
                if (!admin.getMerId().equals(cdkeyLibrary.getMerId())) {
                    throw new CrmebException(ProductResultCode.PRODUCT_CDKEY_LIBRARY_NOT_EXIST);
                }
                if (cdkeyLibrary.getProductId() > 0 && !cdkeyLibrary.getProductId().equals(tempProduct.getId())) {
                    throw new CrmebException(ProductResultCode.PRODUCT_CDKEY_LIBRARY_INTERACTION);
                }
                cdkeyMap.put(cdkeyLibrary.getId(), cdkeyLibrary);
            }
        }
        if (StrUtil.isBlank(productRequest.getKeyword())) {
            productRequest.setKeyword("");
        } else if (productRequest.getKeyword().length() > 255) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "关键字长度不能超过255个字符");
        }

        Product product = new Product();
        BeanUtils.copyProperties(productRequest, product);
        product.setAuditStatus(tempProduct.getAuditStatus());
        product.setType(tempProduct.getType());
        product.setMarketingType(tempProduct.getMarketingType());
        Merchant merchant = merchantService.getByIdException(tempProduct.getMerId());

        String cdnUrl = systemAttachmentService.getCdnUrl();
        //主图
        product.setImage(systemAttachmentService.clearPrefix(product.getImage(), cdnUrl));
        //轮播图
        product.setSliderImage(systemAttachmentService.clearPrefix(product.getSliderImage(), cdnUrl));

        List<ProductAttrValueAddRequest> attrValueAddRequestList = productRequest.getAttrValueList();
        //计算价格
        ProductAttrValueAddRequest minAttrValue = attrValueAddRequestList.stream().min(Comparator.comparing(ProductAttrValueAddRequest::getPrice)).get();
        product.setPrice(minAttrValue.getPrice());
        product.setOtPrice(minAttrValue.getOtPrice());
        product.setCost(minAttrValue.getCost());
        if (product.getIsPaidMember()) {
            product.setVipPrice(minAttrValue.getVipPrice());
        }

        // attr部分
        List<ProductAttrAddRequest> addRequestList = productRequest.getAttrList();
        List<ProductAttribute> attrList = new ArrayList<>();
        Map<String, List<ProductAttributeOption>> optionMap = new HashMap<>();

        addRequestList.forEach(attrRequest -> {
            ProductAttribute attr = new ProductAttribute();
            attr.setProductId(product.getId());
            attr.setAttributeName(attrRequest.getAttributeName());
            attr.setIsShowImage(attrRequest.getIsShowImage());
            attr.setSort(ObjectUtil.isNotNull(attrRequest.getSort()) ? attrRequest.getSort() : 0);
            List<ProductAttrOptionAddRequest> optionRequestList = attrRequest.getOptionList();
            List<ProductAttributeOption> attrOptionList = optionRequestList.stream().map(optionRequest -> {
                ProductAttributeOption option = new ProductAttributeOption();
                option.setProductId(product.getId());
                option.setOptionName(optionRequest.getOptionName());
                option.setSort(ObjectUtil.isNotNull(optionRequest.getSort()) ? optionRequest.getSort() : 0);
                option.setImage(StrUtil.isNotBlank(optionRequest.getImage()) ? systemAttachmentService.clearPrefix(optionRequest.getImage(), cdnUrl) : "");
                return option;
            }).collect(Collectors.toList());
            attrList.add(attr);
            optionMap.put(attr.getAttributeName(), attrOptionList);
        });

        // attrValue部分
        List<ProductAttrValue> attrValueAddList = CollUtil.newArrayList();
        List<ProductAttrValue> attrValueUpdateList = CollUtil.newArrayList();
        attrValueAddRequestList.forEach(e -> {
            ProductAttrValue attrValue = new ProductAttrValue();
            BeanUtils.copyProperties(e, attrValue);
            attrValue.setSku(getSku(e.getAttrValue()));
            attrValue.setImage(systemAttachmentService.clearPrefix(e.getImage(), cdnUrl));
            attrValue.setVersion(0);
            attrValue.setType(product.getType());
            attrValue.setMarketingType(product.getMarketingType());
            attrValue.setVipPrice(product.getIsPaidMember() ? e.getVipPrice() : BigDecimal.ZERO);
            if (product.getType().equals(ProductConstants.PRODUCT_TYPE_CDKEY)) {
                CdkeyLibrary cdkeyLibrary = cdkeyMap.get(e.getCdkeyId());
                attrValue.setStock(cdkeyLibrary.getTotalNum() - cdkeyLibrary.getUsedNum());
                e.setStock(cdkeyLibrary.getTotalNum() - cdkeyLibrary.getUsedNum());
            }
            if (ObjectUtil.isNull(attrValue.getId()) || attrValue.getId().equals(0)) {
                attrValue.setId(null);
                attrValue.setProductId(product.getId());
                attrValue.setQuota(0);
                attrValue.setQuotaShow(0);
                attrValueAddList.add(attrValue);
            } else {
                attrValue.setProductId(product.getId());
                attrValue.setIsDel(false);
                attrValueUpdateList.add(attrValue);
            }
        });
        // 获取保留的sku列表
        List<Integer> tempSkuIdList = CollUtil.newArrayList();
        List<ProductAttrValue> tempAttrValueList = productAttrValueService.getListByProductIdAndType(tempProduct.getId(), tempProduct.getType(), tempProduct.getMarketingType(), false);
        if (CollUtil.isNotEmpty(attrValueUpdateList)) {
            List<Integer> updateSkuIdList = attrValueUpdateList.stream().map(ProductAttrValue::getId).collect(Collectors.toList());
            for (ProductAttrValue tempAttrValue : tempAttrValueList) {
                if (!updateSkuIdList.contains(tempAttrValue.getId())) {
                    tempSkuIdList.add(tempAttrValue.getId());
                }
            }
        } else {
            tempSkuIdList = tempAttrValueList.stream().map(ProductAttrValue::getId).collect(Collectors.toList());
        }

        product.setStock(attrValueAddRequestList.stream().mapToInt(ProductAttrValueAddRequest::getStock).sum());

        // 处理富文本
        ProductDescription spd = new ProductDescription();
        spd.setDescription(StrUtil.isNotBlank(productRequest.getContent()) ? systemAttachmentService.clearPrefix(productRequest.getContent(), cdnUrl) : "");
        spd.setType(product.getType());
        spd.setMarketingType(product.getMarketingType());
        spd.setProductId(product.getId());

        if (product.getType().equals(ProductConstants.PRODUCT_TYPE_CLOUD)
                || product.getType().equals(ProductConstants.PRODUCT_TYPE_CDKEY)
                || product.getType().equals(ProductConstants.PRODUCT_TYPE_VIRTUALLY)) {

            product.setDeliveryMethod("1");
            product.setTempId(0);
            // 非虚拟商品退款开关处理
            if (!product.getType().equals(ProductConstants.PRODUCT_TYPE_VIRTUALLY)) {
                product.setRefundSwitch(true);
            }
        }

        if (merchant.getProductSwitch() && productRequest.getIsAutoSubmitAudit()) {
            product.setIsAutoUp(productRequest.getIsAutoUp());
        } else {
            product.setIsAutoUp(productRequest.getIsAutoUp());
        }

        List<Integer> finalSkuIdList = tempSkuIdList;
        Boolean execute = transactionTemplate.execute(e -> {
            if (!merchant.getProductSwitch() && product.getAuditStatus().equals(ProductConstants.AUDIT_STATUS_EXEMPTION)) {
                product.setAuditStatus(ProductConstants.AUDIT_STATUS_EXEMPTION);
            } else {
                product.setAuditStatus(ProductConstants.AUDIT_STATUS_WAIT);
            }

            product.setIsAudit(false);
            dao.updateById(product);

            // 先删除原用attr+value
            productAttributeService.deleteByProductUpdate(product.getId());
            productAttributeOptionService.deleteByProductUpdate(product.getId());
            productAttrValueService.deleteByProductIdAndType(product.getId(), product.getType(), product.getMarketingType());
            cdkeyLibraryService.clearAssociationProduct(product.getId());

            productAttributeService.saveBatch(attrList);
            attrList.forEach(attr -> {
                List<ProductAttributeOption> optionList = optionMap.get(attr.getAttributeName());
                optionList.forEach(option -> {
                    option.setAttributeId(attr.getId());
                });
                productAttributeOptionService.saveBatch(optionList);
            });

            if (CollUtil.isNotEmpty(attrValueAddList)) {
                productAttrValueService.saveBatch(attrValueAddList);
                if (product.getType().equals(ProductConstants.PRODUCT_TYPE_CDKEY)) {
                    List<CdkeyLibrary> cdkeyLibraryList = attrValueAddList.stream().map(attrValue -> {
                        CdkeyLibrary cdkeyLibrary = new CdkeyLibrary();
                        cdkeyLibrary.setId(attrValue.getCdkeyId());
                        cdkeyLibrary.setProductId(product.getId());
                        cdkeyLibrary.setProductAttrValueId(attrValue.getId());
                        return cdkeyLibrary;
                    }).collect(Collectors.toList());
                    cdkeyLibraryService.updateBatchById(cdkeyLibraryList);
                }
            }
            if (CollUtil.isNotEmpty(attrValueUpdateList)) {
                productAttrValueService.saveOrUpdateBatch(attrValueUpdateList);
                if (product.getType().equals(ProductConstants.PRODUCT_TYPE_CDKEY)) {
                    List<CdkeyLibrary> cdkeyLibraryList = attrValueUpdateList.stream().map(attrValue -> {
                        CdkeyLibrary cdkeyLibrary = new CdkeyLibrary();
                        cdkeyLibrary.setId(attrValue.getCdkeyId());
                        cdkeyLibrary.setProductId(product.getId());
                        cdkeyLibrary.setProductAttrValueId(attrValue.getId());
                        return cdkeyLibrary;
                    }).collect(Collectors.toList());
                    cdkeyLibraryService.updateBatchById(cdkeyLibraryList);
                }
            }

            productDescriptionService.deleteByProductId(product.getId(), product.getType(), product.getMarketingType());
            productDescriptionService.save(spd);

            if (CollUtil.isNotEmpty(productRequest.getCouponIds())) {
                productCouponService.deleteByProductId(product.getId());
                List<ProductCoupon> couponList = new ArrayList<>();
                for (Integer couponId : productRequest.getCouponIds()) {
                    ProductCoupon spc = new ProductCoupon();
                    spc.setProductId(product.getId());
                    spc.setCouponId(couponId);
                    spc.setAddTime(CrmebDateUtil.getNowTime());
                    couponList.add(spc);
                }
                productCouponService.saveBatch(couponList);
            } else {
                productCouponService.deleteByProductId(product.getId());
            }
            if (tempProduct.getSystemFormId().equals(0) && productRequest.getSystemFormId() > 0) {
                cartService.productDelete(product.getId());
            }

            // 删除秒杀活动对应被删除的sku 和 拼团的sku
            if (CollUtil.isNotEmpty(finalSkuIdList)) {
                productAttrValueService.deleteByMasterIdListAndMarktingType(finalSkuIdList, ProductConstants.PRODUCT_MARKETING_TYPE_SECKILL);
                groupBuyActivitySkuService.deleteSkusIdsForProductTurnOff(finalSkuIdList);
            }
            if (ProductConstants.AUDIT_STATUS_WAIT.equals(product.getAuditStatus())) {
                // 待审核商品同步进行秒杀商品下架
                seckillProductService.downByProductId(product.getId());
            }
            return Boolean.TRUE;
        });

        if (execute) {
            if (merchant.getProductSwitch() && productRequest.getIsAutoSubmitAudit()
                    && product.getAuditStatus().equals(ProductConstants.AUDIT_STATUS_WAIT)) {
                LambdaUpdateWrapper<Product> wrapper = Wrappers.lambdaUpdate();
                wrapper.set(Product::getIsAudit, true);
                wrapper.set(Product::getIsShow, false);
                wrapper.eq(Product::getId, product.getId());
                return update(wrapper);
            }
            if (!merchant.getProductSwitch() && productRequest.getIsAutoUp()
                    && product.getAuditStatus().equals(ProductConstants.AUDIT_STATUS_EXEMPTION)) {
                if (!merchant.getIsSwitch()) {
                    LOGGER.error("商户未开启无法自动上架，merId = {}, proId = {}", merchant.getId(), product.getId());
                    return true;
                }
                List<ProductAttrValue> skuList = productAttrValueService.getListByProductIdAndType(product.getId(), product.getType(), product.getMarketingType(), false);
                List<Integer> skuIdList = skuList.stream().map(ProductAttrValue::getId).collect(Collectors.toList());

                LambdaUpdateWrapper<Product> wrapper = Wrappers.lambdaUpdate();
                wrapper.set(Product::getIsShow, true);
                wrapper.eq(Product::getId, product.getId());
                return transactionTemplate.execute(e -> {
                    update(wrapper);
                    if (CollUtil.isNotEmpty(skuIdList)) {
                        cartService.productStatusNoEnable(skuIdList);
                    }
                    return Boolean.TRUE;
                });
            }
        }
        return execute;
    }

    /**
     * 商品详情（管理端）
     *
     * @param id 商品id
     * @return ProductInfoResponse
     */
    @Override
    public ProductInfoResponse getInfo(Integer id, SystemAdmin admin) {
        Product product = dao.selectById(id);
        if (ObjectUtil.isNull(product)) {
            throw new CrmebException(ProductResultCode.PRODUCT_NOT_EXIST);
        }
        if (admin.getMerId() > 0 && !admin.getMerId().equals(product.getMerId())) {
            throw new CrmebException(ProductResultCode.PRODUCT_NOT_EXIST);
        }

        ProductInfoResponse productInfoResponse = new ProductInfoResponse();
        BeanUtils.copyProperties(product, productInfoResponse);

        List<ProductAttribute> attrList = productAttributeService.findListByProductId(product.getId());
        attrList.forEach(attr -> {
            List<ProductAttributeOption> optionList = productAttributeOptionService.findListByAttrId(attr.getId());
            attr.setOptionList(optionList);
        });
        productInfoResponse.setAttrList(attrList);

        List<ProductAttrValue> attrValueList = productAttrValueService.getListByProductIdAndType(product.getId(), product.getType(), product.getMarketingType(), false);
        List<AttrValueResponse> valueResponseList = attrValueList.stream().map(e -> {
            AttrValueResponse valueResponse = new AttrValueResponse();
            BeanUtils.copyProperties(e, valueResponse);
            if (e.getType().equals(ProductConstants.PRODUCT_TYPE_CDKEY)) {
                CdkeyLibrary cdkeyLibrary = cdkeyLibraryService.getByIdException(e.getCdkeyId());
                valueResponse.setCdkeyLibraryName(cdkeyLibrary.getName());
            }
            return valueResponse;
        }).collect(Collectors.toList());
        productInfoResponse.setAttrValueList(valueResponseList);

        ProductDescription sd = productDescriptionService.getByProductIdAndType(product.getId(), product.getType(), product.getMarketingType());
        productInfoResponse.setContent(sd.getDescription());

        // 获取已关联的优惠券
        List<ProductCoupon> productCouponList = productCouponService.getListByProductId(product.getId());
        if (CollUtil.isNotEmpty(productCouponList)) {
            List<Integer> ids = productCouponList.stream().map(ProductCoupon::getCouponId).collect(Collectors.toList());
            if (admin.getMerId() > 0) {
                productInfoResponse.setCouponIds(ids);
            } else {
                productInfoResponse.setCouponList(couponService.findSimpleListByIdList(ids));
            }
        }

        // 收藏量
        productInfoResponse.setCollectNum(productRelationService.getCollectCountByProductId(product.getId()));

        // 保障服务
        if (StrUtil.isNotBlank(product.getGuaranteeIds())) {
            List<ProductGuarantee> guaranteeList = productGuaranteeService.findByIdList(CrmebUtil.stringToArray(product.getGuaranteeIds()));
            productInfoResponse.setGuaranteeList(guaranteeList);
        }

        // 系统表单
        if (product.getSystemFormId() > 0) {
            SystemForm systemForm = systemFormService.getById(product.getSystemFormId());
            productInfoResponse.setSystemFormValue(systemForm.getFormValue());
        }
        return productInfoResponse;
    }

    /**
     * 根据商品tabs获取对应类型的产品数量
     *
     * @return List
     */
    @Override
    public List<ProductTabsHeaderResponse> getTabsHeader(MerProductTabsHeaderRequest request, SystemAdmin systemAdmin) {

        List<ProductTabsHeaderResponse> headers = new ArrayList<>();
        ProductTabsHeaderResponse header1 = new ProductTabsHeaderResponse(0, 1);
        ProductTabsHeaderResponse header2 = new ProductTabsHeaderResponse(0, 2);
        ProductTabsHeaderResponse header3 = new ProductTabsHeaderResponse(0, 3);
        ProductTabsHeaderResponse header4 = new ProductTabsHeaderResponse(0, 4);
        ProductTabsHeaderResponse header5 = new ProductTabsHeaderResponse(0, 5);
        ProductTabsHeaderResponse header6 = new ProductTabsHeaderResponse(0, 6);
        ProductTabsHeaderResponse header7 = new ProductTabsHeaderResponse(0, 7);
        ProductTabsHeaderResponse header8 = new ProductTabsHeaderResponse(0, 8);
        headers.add(header1);
        headers.add(header2);
        headers.add(header3);
        headers.add(header4);
        headers.add(header5);
        headers.add(header6);
        headers.add(header7);
        headers.add(header8);

        LambdaQueryWrapper<Product> lqw = new LambdaQueryWrapper<>();
        for (ProductTabsHeaderResponse h : headers) {
            lqw.clear();
            lqw.eq(Product::getMerId, systemAdmin.getMerId());
            setAdminListWrapperByType(lqw, h.getType(), systemAdmin.getMerId());
            if (StrUtil.isNotBlank(request.getKeywords())) {
                String keywords = URLUtil.decode(request.getKeywords());
                lqw.and(i -> i.like(Product::getName, keywords)
                        .or().apply(" find_in_set({0}, keyword)", keywords));
            }
            if (StrUtil.isNotBlank(request.getCateId())) {
                Integer cateId = Integer.valueOf(request.getCateId());
                lqw.apply("FIND_IN_SET ({0}, cate_id)", cateId);
            }
            if (ObjectUtil.isNotNull(request.getCategoryId())) {
                lqw.eq(Product::getCategoryId, request.getCategoryId());
            }
            if (ObjectUtil.isNotNull(request.getIsPaidMember())) {
                lqw.eq(Product::getIsPaidMember, request.getIsPaidMember());
            }
            if (ObjectUtil.isNotNull(request.getProductType())) {
                lqw.eq(Product::getType, request.getProductType());
            }
            h.setCount(dao.selectCount(lqw));
        }
        return headers;
    }

    /**
     * 商户端商品列表table类型查询条件
     *
     * @param type  商品列表table类型
     * @param merId 商户ID
     */
    private void setAdminListWrapperByType(LambdaQueryWrapper<Product> lqw, Integer type, Integer merId) {
        switch (type) {
            case 1:
                //出售中（已上架）
                lqw.eq(Product::getIsShow, true);
                lqw.eq(Product::getIsRecycle, false);
                lqw.eq(Product::getIsDel, false);
                lqw.in(Product::getAuditStatus, ProductConstants.AUDIT_STATUS_SUCCESS, ProductConstants.AUDIT_STATUS_EXEMPTION);
                break;
            case 2:
                //仓库中（未上架）
                lqw.eq(Product::getIsShow, false);
                lqw.eq(Product::getIsRecycle, false);
                lqw.eq(Product::getIsDel, false);
                lqw.eq(Product::getIsAudit, false);
                lqw.in(Product::getAuditStatus, ProductConstants.AUDIT_STATUS_EXEMPTION, ProductConstants.AUDIT_STATUS_SUCCESS);
                break;
            case 3:
                //已售罄
                lqw.le(Product::getStock, 0);
                lqw.eq(Product::getIsRecycle, false);
                lqw.eq(Product::getIsDel, false);
                lqw.in(Product::getAuditStatus, ProductConstants.AUDIT_STATUS_SUCCESS, ProductConstants.AUDIT_STATUS_EXEMPTION);
                break;
            case 4:
                //警戒库存
                MerchantInfo merchantInfo = merchantInfoService.getByMerId(merId);
                lqw.le(Product::getStock, ObjectUtil.isNotNull(merchantInfo) ? merchantInfo.getAlertStock() : 0);
                lqw.eq(Product::getIsRecycle, false);
                lqw.eq(Product::getIsDel, false);
                lqw.in(Product::getAuditStatus, ProductConstants.AUDIT_STATUS_SUCCESS, ProductConstants.AUDIT_STATUS_EXEMPTION);
                break;
            case 5:
                //回收站
                lqw.eq(Product::getIsRecycle, true);
                lqw.eq(Product::getIsDel, false);
                break;
            case 6:
                //待审核
                lqw.eq(Product::getAuditStatus, ProductConstants.AUDIT_STATUS_WAIT);
                lqw.eq(Product::getIsAudit, true);
                lqw.eq(Product::getIsRecycle, false);
                lqw.eq(Product::getIsDel, false);
                break;
            case 7:
                //审核失败
                lqw.eq(Product::getAuditStatus, ProductConstants.AUDIT_STATUS_FAIL);
                lqw.eq(Product::getIsAudit, false);
                lqw.eq(Product::getIsRecycle, false);
                lqw.eq(Product::getIsDel, false);
                break;
            case 8:
                //待提审
                lqw.eq(Product::getIsShow, false);
                lqw.eq(Product::getIsRecycle, false);
                lqw.eq(Product::getIsDel, false);
                lqw.eq(Product::getIsAudit, false);
                lqw.eq(Product::getAuditStatus, ProductConstants.AUDIT_STATUS_WAIT);
                break;
            default:
                break;
        }
    }

    /**
     * 根据其他平台url导入产品信息
     *
     * @param url 待导入平台url
     * @param tag 1=淘宝，2=京东，3=苏宁，4=拼多多， 5=天猫
     * @return ProductRequest
     */
    @Override
    public ProductResponseForCopyProduct importProductFrom99Api(String url, int tag, SystemAdmin admin) throws JSONException {
        Merchant merchant = merchantService.getByIdException(admin.getMerId());
        if (merchant.getCopyProductNum() <= 0) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "商户复制商品数量不足");
        }

        ProductResponseForCopyProduct copyProduct = null;
        try {
            switch (tag) {
                case 1:
                    copyProduct = productUtils.getTaobaoProductInfo99Api(url, tag);
                    break;
                case 2:
                    copyProduct = productUtils.getJDProductInfo99Api(url, tag);
                    break;
                case 3:
                    copyProduct = productUtils.getSuningProductInfo99Api(url, tag);
                    break;
                case 4:
                    copyProduct = productUtils.getPddProductInfo99Api(url, tag);
                    break;
                case 5:
                    copyProduct = productUtils.getTmallProductInfo99Api(url, tag);
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new CrmebException("确认URL和平台是否正确，以及平台费用是否足额" + e.getMessage());
        }
        Boolean sub = merchantService.subCopyProductNum(merchant.getId());
        if (!sub) {
            LOGGER.error("扣除商户复制条数异常：商户ID = {}", merchant.getId());
        }
        return copyProduct;
    }

    /**
     * 商品回收/删除
     *
     * @param request 删除参数
     * @return Boolean
     */
    @Override
    public Boolean deleteProduct(ProductDeleteRequest request, SystemAdmin admin) {
        Product product = getById(request.getId());
        if (ObjectUtil.isNull(product) || !admin.getMerId().equals(product.getMerId())) {
            throw new CrmebException(ProductResultCode.PRODUCT_NOT_EXIST);
        }
        if (ProductConstants.PRODUCT_DELETE_TYPE_RECYCLE.equals(request.getType()) && product.getIsRecycle()) {
            throw new CrmebException(ProductResultCode.PRODUCT_RECYCLE);
        }

        LambdaUpdateWrapper<Product> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(Product::getId, product.getId());
        if (ProductConstants.PRODUCT_DELETE_TYPE_DELETE.equals(request.getType())) {
            wrapper.set(Product::getIsDel, true);
        } else {
            wrapper.set(Product::getIsRecycle, true);
        }
        return transactionTemplate.execute(e -> {
            update(wrapper);
            if (request.getType().equals("recycle")) {
                cartService.productStatusNotEnable(request.getId());
            } else {
                cartService.productDelete(request.getId());
                if (product.getType().equals(ProductConstants.PRODUCT_TYPE_CDKEY)) {
                    cdkeyLibraryService.clearAssociationProduct(product.getId());
                }
            }
            if (ProductConstants.PRODUCT_DELETE_TYPE_DELETE.equals(request.getType())) {
                // 同步删除秒杀活动中的次级商品
                seckillProductService.deleteByProductId(product.getId());
            }
            return Boolean.TRUE;
        });
    }

    /**
     * 恢复已删除的商品
     *
     * @param productId 商品id
     * @return 恢复结果
     */
    @Override
    public Boolean restoreProduct(Integer productId, SystemAdmin admin) {
        LambdaUpdateWrapper<Product> wrapper = new LambdaUpdateWrapper<>();
        wrapper.set(Product::getIsRecycle, false);
        wrapper.set(Product::getIsShow, false);
        wrapper.eq(Product::getId, productId);
        wrapper.eq(Product::getMerId, admin.getMerId());
        return update(wrapper);
    }

    /**
     * 添加/扣减库存
     *
     * @param id   商品id
     * @param num  数量
     * @param type 类型：add—添加，sub—扣减
     */
    @Override
    public Boolean operationStock(Integer id, Integer num, String type) {
        UpdateWrapper<Product> updateWrapper = new UpdateWrapper<>();
        if (type.equals(Constants.OPERATION_TYPE_QUICK_ADD)) {
            updateWrapper.setSql(StrUtil.format("stock = stock + {}", num));
        }
        if (type.equals(Constants.OPERATION_TYPE_ADD)) {
            updateWrapper.setSql(StrUtil.format("stock = stock + {}", num));
            updateWrapper.setSql(StrUtil.format("sales = sales - {}", num));
        }
        if (type.equals(Constants.OPERATION_TYPE_SUBTRACT)) {
            updateWrapper.setSql(StrUtil.format("stock = stock - {}", num));
            updateWrapper.setSql(StrUtil.format("sales = sales + {}", num));
            // 扣减时加乐观锁保证库存不为负
            updateWrapper.last(StrUtil.format(" and (stock - {} >= 0)", num));
        }
        if (type.equals(Constants.OPERATION_TYPE_DELETE)) {
            updateWrapper.setSql(StrUtil.format("stock = stock - {}", num));
            // 扣减时加乐观锁保证库存不为负
            updateWrapper.last(StrUtil.format(" and (stock - {} >= 0)", num));
        }
        updateWrapper.eq("id", id);
        boolean update = update(updateWrapper);
        if (!update) {
            throw new CrmebException("更新普通商品库存失败,商品id = " + id);
        }
        return update;
    }

    /**
     * 下架
     *
     * @param id 商品id
     */
    @Override
    public Boolean offShelf(Integer id, SystemAdmin admin) {
        Product product = getById(id);
        if (ObjectUtil.isNull(product) || !admin.getMerId().equals(product.getMerId())) {
            throw new CrmebException(ProductResultCode.PRODUCT_NOT_EXIST);
        }
        if (!product.getIsShow()) {
            return true;
        }

        product.setIsShow(false);

        return transactionTemplate.execute(e -> {
            dao.updateById(product);
            cartService.productStatusNotEnable(id);
            // 商品下架时，清除用户收藏
            productRelationService.deleteByProId(product.getId());
            return Boolean.TRUE;
        });
    }

    /**
     * 上架
     *
     * @param id 商品id
     * @return Boolean
     */
    @Override
    public Boolean putOnShelf(Integer id, SystemAdmin admin) {
        Product product = getById(id);
        if (ObjectUtil.isNull(product) || !admin.getMerId().equals(product.getMerId())) {
            throw new CrmebException(ProductResultCode.PRODUCT_NOT_EXIST);
        }
        if (product.getIsShow()) {
            return true;
        }
        if (product.getIsAudit()) {
            throw new CrmebException(ProductResultCode.PRODUCT_AUDIT_ING);
        }
        if (!product.getAuditStatus().equals(ProductConstants.AUDIT_STATUS_EXEMPTION) && !product.getAuditStatus().equals(ProductConstants.AUDIT_STATUS_SUCCESS)) {
            throw new CrmebException(ProductResultCode.PRODUCT_AUDIT_STATUS_EXCEPTION);
        }
        Merchant merchant = merchantService.getById(admin.getMerId());
        if (!merchant.getIsSwitch()) {
            throw new CrmebException(MerchantResultCode.MERCHANT_SWITCH_CLOSE);
        }
        product.setIsShow(true);
        // 获取商品skuid
        List<ProductAttrValue> skuList = productAttrValueService.getListByProductIdAndType(id, product.getType(), product.getMarketingType(), false);
        List<Integer> skuIdList = skuList.stream().map(ProductAttrValue::getId).collect(Collectors.toList());
        return transactionTemplate.execute(e -> {
            dao.updateById(product);
            if (CollUtil.isNotEmpty(skuIdList)) {
                cartService.productStatusNoEnable(skuIdList);
            }
            return Boolean.TRUE;
        });
    }

    /**
     * 首页商品列表
     *
     * @param pageParamRequest 分页参数
     * @param cid              一级商品分类id，全部传0
     * @return CommonPage
     */
    @Override
    public PageInfo<Product> getIndexProduct(Integer cid, PageParamRequest pageParamRequest) {
        LambdaQueryWrapper<Product> lqw = Wrappers.lambdaQuery();
        lqw.select(Product::getId, Product::getMerId, Product::getImage, Product::getName, Product::getUnitName,
                Product::getPrice, Product::getOtPrice, Product::getSales, Product::getFicti, Product::getCategoryId,
                Product::getBrandId, Product::getIsPaidMember, Product::getVipPrice, Product::getStock);
        getForSaleWhere(lqw);
        lqw.gt(Product::getStock, 0);
        if (cid > 0) {
            List<ProductCategory> categoryList = productCategoryService.getThirdCategoryByFirstId(cid, 0);
            if (CollUtil.isEmpty(categoryList)) {
                return new PageInfo<>();
            }
            List<Integer> cidList = categoryList.stream().map(ProductCategory::getId).collect(Collectors.toList());
            lqw.in(Product::getCategoryId, cidList);
        }
        lqw.in(Product::getType, ProductConstants.PRODUCT_TYPE_NORMAL, ProductConstants.PRODUCT_TYPE_VIRTUALLY,
                ProductConstants.PRODUCT_TYPE_CLOUD, ProductConstants.PRODUCT_TYPE_CDKEY);
        lqw.eq(Product::getMarketingType, ProductConstants.PRODUCT_MARKETING_TYPE_BASE);
        lqw.orderByDesc(Product::getRank);
        lqw.orderByDesc(Product::getId);
        Page<Product> page = PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());
        List<Product> productList = dao.selectList(lqw);
        // 查询活动边框配置信息, 并赋值给商品response 重复添加的商品数据会根据数据添加持续覆盖后的为准
        productList = activityStyleService.makeActivityBorderStyle(productList);
        return CommonPage.copyPageInfo(page, productList);
    }


    /**
     * 获取出售中商品的Where条件
     */
    private void getForSaleWhere(LambdaQueryWrapper<Product> lqw) {
        lqw.eq(Product::getIsDel, false);
        lqw.eq(Product::getIsRecycle, false);
        lqw.eq(Product::getIsShow, true);
        lqw.in(Product::getAuditStatus, ProductConstants.AUDIT_STATUS_SUCCESS, ProductConstants.AUDIT_STATUS_EXEMPTION);
        lqw.eq(Product::getMarketingType, ProductConstants.PRODUCT_MARKETING_TYPE_BASE);
    }

    /**
     * 获取商品移动端列表
     *
     * @param request     筛选参数
     * @param pageRequest 分页参数
     * @return PageInfo
     */
    @Override
    public PageInfo<ProductFrontResponse> findH5List(ProductFrontSearchRequest request, PageParamRequest pageRequest) {
        Map<String, Object> map = new HashMap<>();
        StringJoiner brandIds = new StringJoiner(",");
        StringJoiner merIds = new StringJoiner(",");
        StringJoiner categoryIds = new StringJoiner(",");
        if (ObjectUtil.isNotNull(request.getCid()) && !request.getCid().isEmpty()) {
            categoryIds.add(request.getCid());
        }
        if (StrUtil.isNotBlank(request.getKeyword())) {
            String keyword = URLUtil.decode(request.getKeyword());
            map.put("keywords", keyword);
        }
        if (ObjectUtil.isNotNull(request.getMaxPrice())) {
            map.put("maxPrice", request.getMaxPrice());
        }
        if (ObjectUtil.isNotNull(request.getMinPrice())) {
            map.put("minPrice", request.getMinPrice());
        }
        if (ObjectUtil.isNotNull(request.getBrandId()) && !request.getBrandId().isEmpty()) {
            brandIds.add(request.getBrandId());
        }
        if (ObjectUtil.isNotNull(request.getMerId()) && !request.getMerId().isEmpty()) {
            merIds.add(request.getMerId());
        }
        if (ObjectUtil.isNotNull(request.getTagId())) {
            ProductTagsForSearchResponse tagSearchConfig = productTagService.getProductIdListByProductTagId(request.getTagId());
            if (CollUtil.isNotEmpty(tagSearchConfig.getProductIds())) {
                map.put("id", tagSearchConfig.getProductIds().stream().map(Objects::toString).collect(Collectors.joining(",")));
            }
            if (ObjectUtil.isNotNull(tagSearchConfig.getBrandId())) {
                brandIds.add(tagSearchConfig.getBrandId().stream().map(Objects::toString).collect(Collectors.joining(",")));
            }
            if (ObjectUtil.isNotNull(tagSearchConfig.getMerId())) {
                merIds.add(tagSearchConfig.getMerId().stream().map(Objects::toString).collect(Collectors.joining(",")));
            }
            if (ObjectUtil.isNotNull(tagSearchConfig.getCategoryId())) {
                categoryIds.add(tagSearchConfig.getCategoryId().stream().map(Objects::toString).collect(Collectors.joining(",")));
            }
        }

        if (StrUtil.isNotEmpty(brandIds.toString())) {
            map.put("brandId", brandIds.toString());
        }
        if (StrUtil.isNotEmpty(merIds.toString())) {
            map.put("merId", merIds.toString());
        }
        if (StrUtil.isNotEmpty(categoryIds.toString())) {
            map.put("categoryId", categoryIds.toString());
        }

        // 排序部分
        if (StrUtil.isNotBlank(request.getSalesOrder())) {
            if (request.getSalesOrder().equals(Constants.SORT_DESC)) {
                map.put("lastStr", " order by (p.sales + p.ficti) desc, p.rank desc, p.sort desc, p.id desc");
            } else {
                map.put("lastStr", " order by (p.sales + p.ficti) asc, p.rank desc, p.sort desc, p.id desc");
            }
        } else if (StrUtil.isNotBlank(request.getPriceOrder())) {
            if (request.getPriceOrder().equals(Constants.SORT_DESC)) {
                map.put("lastStr", " order by p.price desc, p.rank desc, p.sort desc, p.id desc");
            } else {
                map.put("lastStr", " order by p.price asc, p.rank desc, p.sort desc, p.id desc");
            }
        } else {
            map.put("lastStr", " order by p.rank desc, p.sort desc, p.id desc");
        }
        Page<Product> page = PageHelper.startPage(pageRequest.getPage(), pageRequest.getLimit());
        List<ProductFrontResponse> responseList = dao.findH5List(map);
        if (CollUtil.isEmpty(responseList)) {
            return CommonPage.copyPageInfo(page, responseList);
        }
        responseList.forEach(e -> {
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
        responseList.forEach(response -> {
            Product product = new Product();
            BeanUtils.copyProperties(response, product);
            products.add(product);
        });
        List<Product> makeProductList = activityStyleService.makeActivityBorderStyle(products);

        makeProductList.forEach(p -> {
            responseList.stream().map(resProduct -> {
                if (p.getId().equals(resProduct.getId())) {
                    resProduct.setActivityStyle(p.getActivityStyle());
                }
                return resProduct;
            }).collect(Collectors.toList());
        });
        return CommonPage.copyPageInfo(page, responseList);
    }

    /**
     * 获取移动端商品详情
     *
     * @param id 商品id
     * @return Product
     */
    @Override
    public Product getH5Detail(Integer id) {
        LambdaQueryWrapper<Product> lqw = Wrappers.lambdaQuery();
        lqw.select(Product::getId, Product::getMerId, Product::getImage, Product::getName, Product::getSliderImage,
                Product::getOtPrice, Product::getStock, Product::getSales, Product::getPrice, Product::getIntro, Product::getCost,
                Product::getFicti, Product::getBrowse, Product::getUnitName, Product::getGuaranteeIds, Product::getBrandId,
                Product::getCategoryId, Product::getType, Product::getIsPaidMember, Product::getVipPrice, Product::getMarketingType,
                Product::getSystemFormId, Product::getDeliveryMethod, Product::getRedeemIntegral, Product::getExchangeNum);
        lqw.eq(Product::getId, id);
        getForSaleWhere(lqw);
        Product product = dao.selectOne(lqw);
        if (ObjectUtil.isNull(product)) {
            throw new CrmebException(ProductResultCode.PRODUCT_NOT_EXIST.setMessage(StrUtil.format("没有找到ID： {} 的商品", id)));
        }
        ProductDescription sd = productDescriptionService.getByProductIdAndType(product.getId(), product.getType(), product.getMarketingType());
        if (ObjectUtil.isNotNull(sd)) {
            product.setContent(StrUtil.isBlank(sd.getDescription()) ? "" : sd.getDescription());
        }
        return product;
    }

    /**
     * 获取购物车商品信息
     *
     * @param productId 商品编号
     * @return Product
     */
    @Override
    public Product getCartByProId(Integer productId) {
        LambdaQueryWrapper<Product> lqw = Wrappers.lambdaQuery();
        lqw.select(Product::getId, Product::getImage, Product::getName, Product::getType, Product::getIsPaidMember, Product::getDeliveryMethod);
        lqw.eq(Product::getId, productId);
        return dao.selectOne(lqw);
    }

    /**
     * 根据日期获取新增商品数量
     *
     * @param date 日期，yyyy-MM-dd格式
     * @return Integer
     */
    @Override
    public Integer getNewProductByDate(String date) {
        LambdaQueryWrapper<Product> lqw = Wrappers.lambdaQuery();
        lqw.select(Product::getId);
        lqw.eq(Product::getIsDel, 0);
        lqw.apply("date_format(create_time, '%Y-%m-%d') = {0}", date);
        return dao.selectCount(lqw);
    }

    /**
     * 获取所有未删除的商品
     *
     * @return List<Product>
     */
    @Override
    public List<Product> findAllProductByNotDelete() {
        LambdaQueryWrapper<Product> lqw = Wrappers.lambdaQuery();
        lqw.select(Product::getId, Product::getMerId);
        lqw.eq(Product::getIsDel, 0);
        return dao.selectList(lqw);
    }

    /**
     * 模糊搜索商品名称
     *
     * @param productName 商品名称
     * @param merId       商户Id
     * @return List
     */
    @Override
    public List<Product> likeProductName(String productName, Integer merId) {
        LambdaQueryWrapper<Product> lqw = Wrappers.lambdaQuery();
        lqw.select(Product::getId);
        lqw.like(Product::getName, productName);
        lqw.eq(Product::getIsDel, 0);
        if (!merId.equals(0)) {
            lqw.eq(Product::getMerId, merId);
        }
        return dao.selectList(lqw);
    }

    /**
     * 销售中（上架）商品数量
     *
     * @return Integer
     */
    @Override
    public Integer getOnSaleNum(Integer merId) {
        LambdaQueryWrapper<Product> lqw = Wrappers.lambdaQuery();
        if (merId > 0) {
            lqw.eq(Product::getMerId, merId);
        }
        getForSaleWhere(lqw);
        return dao.selectCount(lqw);
    }

    /**
     * 强制下架商户所有商品
     *
     * @param merchantId 商户ID
     * @return Boolean
     */
    @Override
    public Boolean forcedRemovalAll(Integer merchantId) {
        LambdaUpdateWrapper<Product> wrapper = Wrappers.lambdaUpdate();
        wrapper.set(Product::getIsShow, false);
        wrapper.set(Product::getAuditStatus, ProductConstants.AUDIT_STATUS_WAIT);
        wrapper.eq(Product::getMerId, merchantId);
        wrapper.eq(Product::getIsDel, false);
        wrapper.ne(Product::getAuditStatus, ProductConstants.AUDIT_STATUS_FAIL);
        boolean update = update(wrapper);
        if (!update) {
            return update;
        }
        LambdaQueryWrapper<Product> query = Wrappers.lambdaQuery();
        query.select(Product::getId);
        query.eq(Product::getMerId, merchantId);
        query.eq(Product::getIsDel, false);
        List<Product> productList = dao.selectList(query);
        productList.forEach(product -> {
            // 更新购物车数据
            cartService.productStatusNotEnable(product.getId());
            // 商品强制下架时，清除用户收藏
            productRelationService.deleteByProId(product.getId());
        });
        return true;
    }

    /**
     * 平台端商品分页列表
     *
     * @param request 查询参数
     * @return PageInfo
     */
    @Override
    public PageInfo<PlatformProductListResponse> getPlatformPageList(PlatProductSearchRequest request) {
        HashMap<String, Object> map = CollUtil.newHashMap();
        map.put("type", request.getType());
        if (ObjectUtil.isNotNull(request.getCategoryId())) {
            ProductCategory category = productCategoryService.getById(request.getCategoryId());
            if (category.getLevel().equals(3)) {
                map.put("categoryIds", request.getCategoryId());
            } else {
                List<ProductCategory> categoryList = productCategoryService.findAllChildListByPid(category.getId(), category.getLevel());
                List<String> cateIdList = categoryList.stream().filter(e -> e.getLevel().equals(3)).map(e -> e.getId().toString()).collect(Collectors.toList());
                String categoryIds = String.join(",", cateIdList);
                map.put("categoryIds", categoryIds);
            }
        }
        if (ObjectUtil.isNotNull(request.getMerId())) {
            map.put("merId", request.getMerId());
        }
        if (ObjectUtil.isNotNull(request.getIsSelf())) {
            map.put("self", request.getIsSelf());
        }
        if (StrUtil.isNotBlank(request.getKeywords())) {
            String keywords = URLUtil.decode(request.getKeywords());
            map.put("keywords", keywords);
        }
        if (ObjectUtil.isNotNull(request.getIsPaidMember())) {
            map.put("isPaidMember", request.getIsPaidMember() ? 1 : 0);
        }
        if (ObjectUtil.isNotNull(request.getProductType())) {
            map.put("productType", request.getProductType());
        }
        Page<Product> page = PageHelper.startPage(request.getPage(), request.getLimit());
        List<PlatformProductListResponse> proList = dao.getPlatformPageList(map);
        return CommonPage.copyPageInfo(page, proList);
    }


    /**
     * 根据id集合查询对应商品列表
     *
     * @param ids 商品id集合 逗号分割
     * @return 商品列表
     */
    private List<PlatformProductListResponse> getPlatformListForIds(List<Integer> ids) {
        LambdaQueryWrapper<Product> lambdaQueryWrapper = Wrappers.lambdaQuery();
        lambdaQueryWrapper.eq(Product::getIsDel, Boolean.FALSE);
//        lambdaQueryWrapper.eq(Product::getIsShow, Boolean.TRUE);
        lambdaQueryWrapper.in(Product::getId, ids);
        lambdaQueryWrapper.eq(Product::getMarketingType, ProductConstants.PRODUCT_MARKETING_TYPE_BASE);
        List<Product> products = dao.selectList(lambdaQueryWrapper);
        List<PlatformProductListResponse> platformProductListResponses = productListToPlatFromProductListResponse(products);
        return platformProductListResponses;
    }

    /**
     * 根据id集合以及活动上限加载商品数据
     *
     * @param ids id集合
     * @return 平台商品列表
     */
    @Override
    public List<PlatformProductListResponse> getPlatformListForIdsByLimit(List<Integer> ids) {
        if (crmebConfig.getSelectProductLimit() < ids.size()) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "指定商品上限：" + crmebConfig.getSelectProductLimit());
        }
        return getPlatformListForIds(ids);
    }

    /**
     * 商品审核
     *
     * @param request 审核参数
     * @return Boolean
     */
    @Override
    public Boolean audit(ProductAuditRequest request) {
        if (request.getAuditStatus().equals("fail") && StrUtil.isEmpty(request.getReason())) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "审核拒绝请填写拒绝原因");
        }
        Product product = getByIdException(request.getId());
        if (!product.getAuditStatus().equals(ProductConstants.AUDIT_STATUS_WAIT)) {
            throw new CrmebException(ProductResultCode.PRODUCT_AUDIT_STATUS_EXCEPTION);
        }
        if (!product.getIsAudit()) {
            throw new CrmebException(ProductResultCode.PRODUCT_AUDIT_STATUS_EXCEPTION);
        }
        if (request.getAuditStatus().equals("fail")) {
            product.setAuditStatus(ProductConstants.AUDIT_STATUS_FAIL);
            product.setReason(request.getReason());
            product.setIsAudit(false);
            product.setIsShow(false);
            return updateById(product);
        }
        // 审核成功
        product.setAuditStatus(ProductConstants.AUDIT_STATUS_SUCCESS);
        // 免审店铺商品回归免审状态
        Merchant merchant = merchantService.getByIdException(product.getMerId());
        if (!merchant.getProductSwitch()) {
            product.setAuditStatus(ProductConstants.AUDIT_STATUS_EXEMPTION);
        }
        product.setIsAudit(false);

        return transactionTemplate.execute(e -> {
            if (merchant.getIsSwitch() && product.getIsAutoUp()) {
                product.setIsShow(true);
                List<ProductAttrValue> skuList = productAttrValueService.getListByProductIdAndType(product.getId(), product.getType(), product.getMarketingType(), false);
                List<Integer> skuIdList = skuList.stream().map(ProductAttrValue::getId).collect(Collectors.toList());
                if (CollUtil.isNotEmpty(skuIdList)) {
                    cartService.productStatusNoEnable(skuIdList);
                }
                updateById(product);
            } else {
                product.setIsShow(false);
                updateById(product);
            }
            return Boolean.TRUE;
        });
    }

    /**
     * 强制下架商品
     *
     * @param request 商品id参数
     * @return Boolean
     */
    @Override
    public Boolean forceDown(ProductForceDownRequest request) {
        String ids = request.getIds();
        List<Integer> idList = Stream.of(ids.split(",")).map(Integer::valueOf).collect(Collectors.toList());
        LambdaUpdateWrapper<Product> wrapper = Wrappers.lambdaUpdate();
        wrapper.set(Product::getIsShow, false);
        wrapper.set(Product::getIsAudit, false);
        wrapper.set(Product::getAuditStatus, ProductConstants.AUDIT_STATUS_WAIT);
        wrapper.in(Product::getId, idList);
        return transactionTemplate.execute(e -> {
            boolean update = update(wrapper);
            if (update) {
                idList.forEach(id -> {
                    // 修改购物车状态
                    cartService.productStatusNotEnable(id);
                    // 商品强制下架时，清除用户收藏
                    productRelationService.deleteByProId(id);
                });
                // 下架基础商品对应的秒杀商品
                seckillProductService.downByProductIdList(idList);
            }
            return Boolean.TRUE;
        });
    }

    /**
     * 是否有商品使用对应的商户商品分类
     *
     * @param id 商户商品分类id
     * @return Boolean
     */
    @Override
    public Boolean isExistStoreCategory(Integer id) {
        LambdaQueryWrapper<Product> lqw = Wrappers.lambdaQuery();
        lqw.select(Product::getId);
        lqw.eq(Product::getIsDel, false);
        lqw.apply(" find_in_set({0}, cate_id)", id);
        lqw.last(" limit 1");
        Product Product = dao.selectOne(lqw);
        return ObjectUtil.isNotNull(Product);
    }

    /**
     * 商品增加浏览量
     *
     * @param proId 商品id
     * @return Boolean
     */
    @Override
    public Boolean addBrowse(Integer proId) {
        LambdaUpdateWrapper<Product> wrapper = Wrappers.lambdaUpdate();
        wrapper.setSql("browse = browse + 1");
        wrapper.eq(Product::getId, proId);
        return update(wrapper);
    }

    /**
     * 获取商户推荐商品
     *
     * @param merId 商户id
     * @param num   查询商品数量
     * @return List
     */
    @Override
    public List<ProMerchantProductResponse> getRecommendedProductsByMerId(Integer merId, Integer num) {
        LambdaQueryWrapper<Product> lqw = Wrappers.lambdaQuery();
        lqw.select(Product::getId, Product::getMerId, Product::getImage, Product::getName,
                Product::getPrice, Product::getSales, Product::getFicti, Product::getStock);
        lqw.eq(Product::getMerId, merId);
        getForSaleWhere(lqw);
        lqw.orderByDesc(Product::getSort);
        lqw.last("limit " + num);
        List<Product> productList = dao.selectList(lqw);
        if (CollUtil.isEmpty(productList)) {
            return CollUtil.newArrayList();
        }
        return productList.stream().map(product -> {
            ProMerchantProductResponse response = new ProMerchantProductResponse();
            BeanUtils.copyProperties(product, response);
            return response;
        }).collect(Collectors.toList());
    }

    /**
     * 商户商品列表
     *
     * @param request          搜索参数
     * @param pageParamRequest 分页参数
     * @return List
     */
    @Override
    public PageInfo<Product> findMerchantProH5List(MerchantProductSearchRequest request, PageParamRequest pageParamRequest) {
        LambdaQueryWrapper<Product> lqw = Wrappers.lambdaQuery();
        // id、名称、图片、价格、销量
        lqw.select(Product::getId, Product::getName, Product::getImage, Product::getPrice, Product::getOtPrice,
                Product::getSales, Product::getFicti, Product::getUnitName, Product::getStock, Product::getMerId,
                Product::getCategoryId, Product::getBrandId, Product::getVipPrice, Product::getIsPaidMember);

        getForSaleWhere(lqw);
        lqw.eq(Product::getMerId, request.getMerId());
        if (StrUtil.isNotBlank(request.getKeyword())) {
            String keyword = URLUtil.decode(request.getKeyword());
            lqw.and(i -> i.like(Product::getName, keyword)
                    .or().like(Product::getKeyword, keyword));
        }
        if (StrUtil.isNotBlank(request.getCids())) {
            String cateIdSql = CrmebUtil.getFindInSetSql("cate_id", request.getCids());
            lqw.apply(cateIdSql);

        }
        if (ObjectUtil.isNotNull(request.getMaxPrice())) {
            lqw.le(Product::getPrice, request.getMaxPrice());
        }
        if (ObjectUtil.isNotNull(request.getMinPrice())) {
            lqw.ge(Product::getPrice, request.getMinPrice());
        }
        // 排序部分
        if (StrUtil.isNotBlank(request.getSalesOrder())) {
            if (request.getSalesOrder().equals(Constants.SORT_DESC)) {
                lqw.last(" order by (sales + ficti) desc, sort desc, id desc");
            } else {
                lqw.last(" order by (sales + ficti) asc, sort desc, id desc");
            }
        } else {
            if (StrUtil.isNotBlank(request.getPriceOrder())) {
                if (request.getPriceOrder().equals(Constants.SORT_DESC)) {
                    lqw.last(" order by price desc, sort desc, id desc");
                } else {
                    lqw.last(" order by price asc, sort desc, id desc");
                }
            } else {
                lqw.orderByDesc(Product::getSort);
                lqw.orderByDesc(Product::getId);
            }

        }
        Page<Product> page = PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());
        List<Product> productList = dao.selectList(lqw);
        return CommonPage.copyPageInfo(page, productList);
    }

    /**
     * 判断商品是否使用品牌
     *
     * @param brandId 品牌id
     * @return Boolean
     */
    @Override
    public Boolean isUseBrand(Integer brandId) {
        LambdaQueryWrapper<Product> lqw = Wrappers.lambdaQuery();
        lqw.select(Product::getId);
        lqw.eq(Product::getIsDel, false);
        lqw.eq(Product::getBrandId, brandId);
        lqw.last("limit 1");
        Product Product = dao.selectOne(lqw);
        return ObjectUtil.isNotNull(Product);
    }

    /**
     * 判断商品是否使用平台分类
     *
     * @param categoryId 平台分类id
     * @return Boolean
     */
    @Override
    public Boolean isUsePlatformCategory(Integer categoryId) {
        LambdaQueryWrapper<Product> lqw = Wrappers.lambdaQuery();
        lqw.select(Product::getId);
        lqw.eq(Product::getIsDel, false);
        lqw.eq(Product::getCategoryId, categoryId);
        lqw.last("limit 1");
        Product Product = dao.selectOne(lqw);
        return ObjectUtil.isNotNull(Product);
    }

    /**
     * 查询使用服务保障的商品列表
     *
     * @param gid 服务保障id
     * @return List
     */
    @Override
    public List<Product> findUseGuarantee(Integer gid) {
        LambdaQueryWrapper<Product> lqw = Wrappers.lambdaQuery();
        lqw.select(Product::getId, Product::getMerId);
        lqw.eq(Product::getIsDel, false);
        lqw.apply(" find_in_set({0}, guarantee_ids)", gid);
        return dao.selectList(lqw);
    }

    /**
     * 判断商品是否使用服务保障
     *
     * @param gid 服务保障id
     * @return Boolean
     */
    @Override
    public Boolean isUseGuarantee(Integer gid) {
        LambdaQueryWrapper<Product> lqw = Wrappers.lambdaQuery();
        lqw.select(Product::getId);
        lqw.eq(Product::getIsDel, false);
        lqw.apply(" find_in_set({0}, guarantee_ids)", gid);
        lqw.last("limit 1");
        Product Product = dao.selectOne(lqw);
        return ObjectUtil.isNotNull(Product);
    }

    /**
     * 获取待审核商品数量
     */
    @Override
    public Integer getAwaitAuditNum(Integer merId) {
        LambdaQueryWrapper<Product> lqw = Wrappers.lambdaQuery();
        lqw.eq(Product::getAuditStatus, ProductConstants.AUDIT_STATUS_WAIT);
        lqw.eq(Product::getIsAudit, true);
        lqw.eq(Product::getIsRecycle, false);
        lqw.eq(Product::getIsDel, false);
        setAdminListWrapperByType(lqw, 6, merId);
        if (merId > 0) {
            lqw.eq(Product::getMerId, merId);
        }
        return dao.selectCount(lqw);
    }

    /**
     * 下架商品商品
     *
     * @param merId 商户id
     */
    @Override
    public Boolean downByMerId(Integer merId) {
        LambdaUpdateWrapper<Product> wrapper = Wrappers.lambdaUpdate();
        wrapper.set(Product::getIsShow, false);
        wrapper.eq(Product::getMerId, merId);
        wrapper.eq(Product::getIsShow, true);
        return update(wrapper);
    }

    /**
     * 优惠券商品列表
     *
     * @param request 搜索参数
     */
    @Override
    public PageInfo<Product> getCouponProList(CouponProductSearchRequest request) {
        Integer userId = userService.getUserIdException();
        CouponUser couponUser = couponUserService.getById(request.getUserCouponId());
        if (ObjectUtil.isNull(couponUser) || !couponUser.getUid().equals(userId)
                || couponUser.getStatus() > CouponConstants.STORE_COUPON_USER_STATUS_USABLE) {
            throw new CrmebException(CouponResultCode.COUPON_NOT_EXIST.setMessage("优惠券不存在或不是未使用状态"));
        }
        List<Integer> pidList = null;
        if (couponUser.getCategory().equals(CouponConstants.COUPON_CATEGORY_PRODUCT)) {
            List<CouponProduct> cpList = couponProductService.findByCid(couponUser.getCouponId());
            if (CollUtil.isEmpty(cpList)) {
                throw new CrmebException(CouponResultCode.COUPON_PRODUCT_NOT_EXIST);
            }
            pidList = cpList.stream().map(CouponProduct::getPid).collect(Collectors.toList());
        }
        Coupon coupon = couponService.getById(couponUser.getCouponId());
        if (ObjectUtil.isNull(coupon)) {
            throw new CrmebException(CouponResultCode.COUPON_NOT_EXIST);
        }
        LambdaQueryWrapper<Product> lqw = Wrappers.lambdaQuery();
        // id、名称、图片、价格、销量
        lqw.select(Product::getId, Product::getName, Product::getImage, Product::getPrice, Product::getOtPrice,
                Product::getSales, Product::getFicti, Product::getUnitName, Product::getStock, Product::getMerId,
                Product::getVipPrice, Product::getIsPaidMember);
        getForSaleWhere(lqw);
        lqw.ne(Product::getType, ProductConstants.PRODUCT_TYPE_INTEGRAL);
        lqw.eq(Product::getMarketingType, ProductConstants.PRODUCT_MARKETING_TYPE_BASE);
        if (StrUtil.isNotBlank(request.getKeyword())) {
            String decode = URLUtil.decode(request.getKeyword());
            lqw.and(i -> i.like(Product::getName, decode)
                    .or().like(Product::getKeyword, decode));
        }
        if (couponUser.getCategory().equals(CouponConstants.COUPON_CATEGORY_MERCHANT)) {
            lqw.eq(Product::getMerId, couponUser.getMerId());
        }
        if (couponUser.getCategory().equals(CouponConstants.COUPON_CATEGORY_PRODUCT)) {
            if (CollUtil.isNotEmpty(pidList)) {
                lqw.in(Product::getId, pidList);
            }
        }
        if (couponUser.getCategory().equals(CouponConstants.COUPON_CATEGORY_PRODUCT_CATEGORY)) {
            ProductCategory productCategory = productCategoryService.getById(Integer.valueOf(coupon.getLinkedData()));
            List<Integer> pcIdList = new ArrayList<>();
            if (productCategory.getLevel().equals(3)) {
                pcIdList.add(productCategory.getId());
            } else {
                List<ProductCategory> productCategoryList = new ArrayList<>();
                if (productCategory.getLevel().equals(2)) {
                    productCategoryList = productCategoryService.findAllChildListByPid(productCategory.getId(), productCategory.getLevel());
                }
                if (productCategory.getLevel().equals(1)) {
                    productCategoryList = productCategoryService.getThirdCategoryByFirstId(productCategory.getId(), 0);
                }
                List<Integer> collect = productCategoryList.stream().map(ProductCategory::getId).collect(Collectors.toList());
                pcIdList.addAll(collect);
            }
            lqw.in(Product::getCategoryId, pcIdList);
        }
        if (couponUser.getCategory().equals(CouponConstants.COUPON_CATEGORY_BRAND)) {
            lqw.eq(Product::getBrandId, Integer.valueOf(coupon.getLinkedData()));
        }
        if (couponUser.getCategory().equals(CouponConstants.COUPON_CATEGORY_JOINT_MERCHANT)) {
            lqw.in(Product::getMerId, coupon.getLinkedData());
        }
        lqw.orderByDesc(Product::getSort);
        lqw.orderByDesc(Product::getId);
        Page<Product> page = PageHelper.startPage(request.getPage(), request.getLimit());
        List<Product> productList = dao.selectList(lqw);
        return CommonPage.copyPageInfo(page, productList);
    }

    /**
     * 平台端获取商品表头数量
     *
     * @return List
     */
    @Override
    public List<ProductTabsHeaderResponse> getPlatformTabsHeader(PlatProductTabsHeaderRequest request) {
        List<ProductTabsHeaderResponse> headers = new ArrayList<>();
        ProductTabsHeaderResponse header1 = new ProductTabsHeaderResponse(0, 1);
        ProductTabsHeaderResponse header2 = new ProductTabsHeaderResponse(0, 2);
        ProductTabsHeaderResponse header6 = new ProductTabsHeaderResponse(0, 6);
        ProductTabsHeaderResponse header7 = new ProductTabsHeaderResponse(0, 7);
        headers.add(header1);
        headers.add(header2);
        headers.add(header6);
        headers.add(header7);

        HashMap<String, Object> map = CollUtil.newHashMap();
        if (ObjectUtil.isNotNull(request.getCategoryId())) {
            ProductCategory category = productCategoryService.getById(request.getCategoryId());
            if (category.getLevel().equals(3)) {
                map.put("categoryIds", request.getCategoryId());
            } else {
                List<ProductCategory> categoryList = productCategoryService.findAllChildListByPid(category.getId(), category.getLevel());
                List<String> cateIdList = categoryList.stream().filter(e -> e.getLevel().equals(3)).map(e -> e.getId().toString()).collect(Collectors.toList());
                String categoryIds = String.join(",", cateIdList);
                map.put("categoryIds", categoryIds);
            }
        }
        if (ObjectUtil.isNotNull(request.getMerId())) {
            map.put("merId", request.getMerId());
        }
        if (ObjectUtil.isNotNull(request.getIsSelf())) {
            map.put("self", request.getIsSelf());
        }
        if (StrUtil.isNotBlank(request.getKeywords())) {
            String keywords = URLUtil.decode(request.getKeywords());
            map.put("keywords", keywords);
        }
        if (ObjectUtil.isNotNull(request.getIsPaidMember())) {
            map.put("isPaidMember", request.getIsPaidMember() ? 1 : 0);
        }
        if (ObjectUtil.isNotNull(request.getProductType())) {
            map.put("productType", request.getProductType());
        }
        for (ProductTabsHeaderResponse h : headers) {
            map.put("type", h.getType());
            h.setCount(dao.getPlatformPageCount(map));
        }
        return headers;
    }

    /**
     * 平台端商品编辑
     *
     * @param request 商品编辑参数
     * @return Boolean
     */
    @Override
    public Boolean platUpdate(ProductPlatUpdateRequest request) {
        Product product = getByIdException(request.getId());
        if (product.getFicti().equals(request.getFicti()) && product.getRank().equals(request.getRank())) {
            return Boolean.TRUE;
        }
        LambdaUpdateWrapper<Product> wrapper = Wrappers.lambdaUpdate();
        wrapper.set(Product::getFicti, request.getFicti());
        wrapper.set(Product::getRank, request.getRank());
        wrapper.eq(Product::getId, request.getId());
        return update(wrapper);
    }

    /**
     * 是否有商品使用运费模板
     *
     * @return Boolean
     */
    @Override
    public Boolean isUseShippingTemplateId(Integer templateId) {
        LambdaQueryWrapper<Product> lqw = Wrappers.lambdaQuery();
        lqw.eq(Product::getTempId, templateId);
        lqw.eq(Product::getIsDel, false);
        return dao.selectCount(lqw) > 0;
    }

    /**
     * 商品提审
     */
    @Override
    public Boolean submitAudit(ProductSubmitAuditRequest request, SystemAdmin admin) {
        Product product = getByIdException(request.getId());
        if (!admin.getMerId().equals(product.getMerId())) {
            throw new CrmebException(ProductResultCode.PRODUCT_NOT_EXIST);
        }
        if (product.getIsAudit()) {
            throw new CrmebException(ProductResultCode.PRODUCT_AUDIT_ING);
        }
        if (!product.getAuditStatus().equals(ProductConstants.AUDIT_STATUS_WAIT)) {
            throw new CrmebException(ProductResultCode.PRODUCT_AUDIT_STATUS_EXCEPTION);
        }
        if (product.getIsRecycle()) {
            throw new CrmebException(ProductResultCode.PRODUCT_RECYCLE);
        }
        LambdaUpdateWrapper<Product> wrapper = Wrappers.lambdaUpdate();
        wrapper.set(Product::getIsAudit, true);
        wrapper.set(Product::getIsShow, false);
        wrapper.set(Product::getIsAutoUp, request.getIsAutoUp());
        wrapper.eq(Product::getId, request.getId());
        return update(wrapper);
    }

    /**
     * 快捷添加库存
     *
     * @param request 添加库存参数
     * @return Boolean
     */
    @Override
    public Boolean quickAddStock(ProductAddStockRequest request, SystemAdmin admin) {
        Product product = getByIdException(request.getId());
        if (!admin.getMerId().equals(product.getMerId())) {
            throw new CrmebException(ProductResultCode.PRODUCT_NOT_EXIST);
        }
        if (product.getIsAudit()) {
            throw new CrmebException(ProductResultCode.PRODUCT_AUDIT_ING);
        }
        if (product.getType().equals(ProductConstants.PRODUCT_TYPE_CDKEY)) {
            throw new CrmebException(ProductResultCode.PRODUCT_CDKEY_NOT_QUICK_ADD);
        }
        List<ProductAttrValueAddStockRequest> valueStockList = request.getAttrValueList();
        List<Integer> attrIdList = valueStockList.stream().map(ProductAttrValueAddStockRequest::getId).distinct().collect(Collectors.toList());
        if (attrIdList.size() != valueStockList.size()) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "有重复的商品规格属性ID");
        }
        List<ProductAttrValue> valueList = productAttrValueService.getByProductIdAndAttrIdList(request.getId(), attrIdList, product.getType(), product.getMarketingType());
        if (CollUtil.isEmpty(valueList) || valueList.size() != attrIdList.size()) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "商品规格属性ID数组数据异常，请刷新后再试");
        }
        for (ProductAttrValueAddStockRequest value : valueStockList) {
            for (ProductAttrValue attrValue : valueList) {
                if (attrValue.getId().equals(value.getId())) {
                    value.setVersion(attrValue.getVersion());
                    break;
                }
            }
        }
        int totalStock = valueStockList.stream().mapToInt(ProductAttrValueAddStockRequest::getStock).sum();
        return transactionTemplate.execute(e -> {
            operationStock(product.getId(), totalStock, Constants.OPERATION_TYPE_QUICK_ADD);
            valueStockList.forEach(valueStock -> {
                productAttrValueService.operationStock(valueStock.getId(), valueStock.getStock(),
                        Constants.OPERATION_TYPE_QUICK_ADD, product.getType(), product.getMarketingType(),
                        valueStock.getVersion());
            });
            return Boolean.TRUE;
        });
    }

    /**
     * 商品免审编辑
     *
     * @param request 商品免审编辑参数
     * @return Boolean
     */
    @Override
    public Boolean reviewFreeEdit(ProductReviewFreeEditRequest request, SystemAdmin admin) {
        Product product = getByIdException(request.getId());
        if (!admin.getMerId().equals(product.getMerId())) {
            throw new CrmebException(ProductResultCode.PRODUCT_NOT_EXIST);
        }
        if (product.getIsShow()) {
            throw new CrmebException(ProductResultCode.PRODUCT_IS_SHOW);
        }
        if (product.getIsAudit()) {
            throw new CrmebException(ProductResultCode.PRODUCT_AUDIT_ING);
        }

        List<ProductAttrValueReviewFreeEditRequest> attrValueRequestList = request.getAttrValue();
        List<ProductAttrValue> attrValueList = productAttrValueService.getListByProductIdAndType(product.getId(), product.getType(), product.getMarketingType(), false);
        if (CollUtil.isEmpty(attrValueList) || attrValueList.size() != attrValueRequestList.size()) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "商品规格属性数量不一致");
        }
        if (product.getType().equals(ProductConstants.PRODUCT_TYPE_CDKEY)) {
            List<Integer> cdkIdList = attrValueRequestList.stream().map(ProductAttrValueReviewFreeEditRequest::getCdkeyId).distinct().collect(Collectors.toList());
            if (cdkIdList.size() != attrValueRequestList.size()) {
                throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "卡密商品规格属性中有重复的卡密库");
            }
        }

        List<Integer> cdkeyIdUpdateList = new ArrayList<>();
        List<CdkeyLibrary> cdkeyBingingList = new ArrayList<>();
        attrValueList.forEach(attrValue -> {
            for (ProductAttrValueReviewFreeEditRequest attrValueRequest : attrValueRequestList) {
                if (attrValueRequest.getId().equals(attrValue.getId())) {
                    attrValue.setPrice(attrValueRequest.getPrice());
                    attrValue.setVersion(0);
                    if (product.getType().equals(ProductConstants.PRODUCT_TYPE_CLOUD)) {
                        attrValue.setExpand(attrValueRequest.getExpand());
                    }
                    if (product.getType().equals(ProductConstants.PRODUCT_TYPE_CDKEY)) {
                        if (!attrValue.getCdkeyId().equals(attrValueRequest.getCdkeyId())) {
                            cdkeyIdUpdateList.add(attrValue.getCdkeyId());
                            CdkeyLibrary cdkeyLibrary = cdkeyLibraryService.getByIdException(attrValueRequest.getCdkeyId());
                            if (!admin.getMerId().equals(cdkeyLibrary.getMerId())) {
                                throw new CrmebException(ProductResultCode.PRODUCT_CDKEY_LIBRARY_NOT_EXIST);
                            }
                            if (cdkeyLibrary.getProductId() > 0 && cdkeyLibrary.getProductId().equals(product.getId())) {
                                throw new CrmebException(ProductResultCode.PRODUCT_CDKEY_LIBRARY_INTERACTION);
                            }
                            attrValue.setStock(cdkeyLibrary.getTotalNum() - cdkeyLibrary.getUsedNum());
                            attrValue.setCdkeyId(attrValueRequest.getCdkeyId());
                            CdkeyLibrary newCdkeyLibrary = new CdkeyLibrary();
                            newCdkeyLibrary.setId(attrValueRequest.getCdkeyId());
                            newCdkeyLibrary.setProductId(product.getId());
                            newCdkeyLibrary.setProductAttrValueId(attrValue.getId());
                            cdkeyBingingList.add(newCdkeyLibrary);
                        }
                    } else {
                        attrValue.setStock(attrValueRequest.getStock());
                    }
                    break;
                }
            }
        });

        ProductAttrValue minAttrValue = attrValueList.stream().min(Comparator.comparing(ProductAttrValue::getPrice)).get();
        Product tempProduct = new Product();
        tempProduct.setId(product.getId());
        tempProduct.setPrice(minAttrValue.getPrice());
        tempProduct.setStock(attrValueList.stream().mapToInt(ProductAttrValue::getStock).sum());
        if (!product.getCateId().equals(request.getCateId())) {
            tempProduct.setCateId(request.getCateId());
        }
        return transactionTemplate.execute(e -> {
            boolean update = updateById(tempProduct);
            if (!update) {
                LOGGER.error("免审编辑商品失败，商品id = {}", tempProduct.getId());
                e.setRollbackOnly();
                return Boolean.FALSE;
            }
            update = productAttrValueService.updateBatchById(attrValueList, 100);
            if (!update) {
                LOGGER.error("免审编辑商品规格属性失败，商品id = {}", tempProduct.getId());
                e.setRollbackOnly();
                return Boolean.FALSE;
            }
            if (CollUtil.isNotEmpty(cdkeyIdUpdateList)) {
                cdkeyLibraryService.clearAssociationByIds(cdkeyIdUpdateList);
            }
            if (CollUtil.isNotEmpty(cdkeyBingingList)) {
                cdkeyLibraryService.updateBatchById(cdkeyBingingList);
            }
            return Boolean.TRUE;
        });
    }

    /**
     * 商品任何状态都可以编辑数量和价格的接口 谨慎使用
     *
     * @param request 编辑对象
     * @param admin   编辑人
     * @return 编辑状态
     */
    @Override
    public Boolean merchantEmployeeAnyTimeEdit(ProductAnyTimeEditRequest request, SystemAdmin admin) {
        Product product = getByIdException(request.getId());
        if (!admin.getMerId().equals(product.getMerId())) {
            throw new CrmebException(ProductResultCode.PRODUCT_NOT_EXIST);
        }

        List<ProductAttrValueAnyTimeEditRequest> attrValueRequestList = request.getAttrValue();
        List<ProductAttrValue> attrValueList = productAttrValueService.getListByProductIdAndType(product.getId(), product.getType(), product.getMarketingType(), false);
        if (CollUtil.isEmpty(attrValueList) || attrValueList.size() != attrValueRequestList.size()) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "商品规格属性数量不一致");
        }
        List<Integer> cdkeyIdUpdateList = new ArrayList<>();
        List<CdkeyLibrary> cdkeyBingingList = new ArrayList<>();
        attrValueList.forEach(attrValue -> {
            for (ProductAttrValueAnyTimeEditRequest attrValueRequest : attrValueRequestList) {
                if (attrValueRequest.getId().equals(attrValue.getId())) {
                    attrValue.setPrice(attrValueRequest.getPrice());
                    attrValue.setOtPrice(attrValueRequest.getOtPrice());
                    attrValue.setCost(attrValueRequest.getCost());
                    attrValue.setVersion(0);
                    if (product.getIsPaidMember() && ObjectUtil.isNotNull(attrValueRequest.getVipPrice())) {
                        attrValue.setVipPrice(attrValueRequest.getVipPrice());
                    }
                    if (product.getType().equals(ProductConstants.PRODUCT_TYPE_CLOUD)) {
                        attrValue.setExpand(attrValueRequest.getExpand());
                    }
                    if (product.getType().equals(ProductConstants.PRODUCT_TYPE_CDKEY)) {
                        if (!attrValue.getCdkeyId().equals(attrValueRequest.getCdkeyId())) {
                            cdkeyIdUpdateList.add(attrValue.getCdkeyId());
                            CdkeyLibrary cdkeyLibrary = cdkeyLibraryService.getByIdException(attrValueRequest.getCdkeyId());
                            if (!admin.getMerId().equals(cdkeyLibrary.getMerId())) {
                                throw new CrmebException(ProductResultCode.PRODUCT_CDKEY_LIBRARY_NOT_EXIST);
                            }
                            if (cdkeyLibrary.getProductId() > 0 && cdkeyLibrary.getProductId().equals(product.getId())) {
                                throw new CrmebException(ProductResultCode.PRODUCT_CDKEY_LIBRARY_INTERACTION);
                            }
                            attrValue.setStock(cdkeyLibrary.getTotalNum() - cdkeyLibrary.getUsedNum());
                            attrValue.setCdkeyId(attrValueRequest.getCdkeyId());
                            CdkeyLibrary newCdkeyLibrary = new CdkeyLibrary();
                            newCdkeyLibrary.setId(attrValueRequest.getCdkeyId());
                            newCdkeyLibrary.setProductId(product.getId());
                            newCdkeyLibrary.setProductAttrValueId(attrValue.getId());
                            cdkeyBingingList.add(newCdkeyLibrary);
                        }
                    } else {
                        attrValue.setStock(attrValueRequest.getStock());
                    }
                    break;
                }
            }
        });

        ProductAttrValue minAttrValue = attrValueList.stream().min(Comparator.comparing(ProductAttrValue::getPrice)).get();
        Product tempProduct = new Product();
        tempProduct.setId(product.getId());
        tempProduct.setPrice(minAttrValue.getPrice());
        tempProduct.setStock(attrValueList.stream().mapToInt(ProductAttrValue::getStock).sum());
        if (!product.getCateId().equals(request.getCateId())) {
            tempProduct.setCateId(request.getCateId());
        }
        if (product.getIsPaidMember()) {
            tempProduct.setVipPrice(minAttrValue.getVipPrice());
        }
        return transactionTemplate.execute(e -> {
            boolean update = updateById(tempProduct);
            if (!update) {
                LOGGER.error("无状态限制商品编辑失败，商品id = {}", tempProduct.getId());
                e.setRollbackOnly();
                return Boolean.FALSE;
            }
            update = productAttrValueService.updateBatchById(attrValueList, 100);
            if (!update) {
                LOGGER.error("无状态限制商品编辑失败，商品id = {}", tempProduct.getId());
                e.setRollbackOnly();
                return Boolean.FALSE;
            }
            if (CollUtil.isNotEmpty(cdkeyIdUpdateList)) {
                cdkeyLibraryService.clearAssociationByIds(cdkeyIdUpdateList);
            }
            if (CollUtil.isNotEmpty(cdkeyBingingList)) {
                cdkeyLibraryService.updateBatchById(cdkeyBingingList);
            }
            return Boolean.TRUE;
        });
    }

    /**
     * 获取复制商品配置
     *
     * @return copyType 复制类型：1：一号通
     * copyNum 复制条数(一号通类型下有值)
     */
    @Override
    public MyRecord copyConfig(SystemAdmin admin) {
        String copyType = systemConfigService.getValueByKey(SysConfigConstants.CONFIG_PRODUCT_COPY_TYPE);
        if (StrUtil.isBlank(copyType)) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "请先进行采集商品配置");
        }
        int copyNum = 0;
        if (copyType.equals("1")) {// 一号通
            if (admin.getMerId() > 0) {
                Merchant merchant = merchantService.getById(admin.getMerId());
                copyNum = merchant.getCopyProductNum();
            } else {
                OnePassUserInfoVo info = onePassService.info();
                copyNum = Optional.ofNullable(info.getCopy().getNum()).orElse(0);
            }
        }
        MyRecord record = new MyRecord();
        record.set("copyType", copyType);
        record.set("copyNum", copyNum);
        return record;
    }

    /**
     * 复制平台商品
     *
     * @param url 商品链接
     * @return MyRecord
     */
    @Override
    public ProductResponseForCopyProduct copyProduct(String url, SystemAdmin currentMerchantAdmin) {
        // 校验当前商户的copy余量
        Merchant currentMerchant = merchantService.getByIdException(currentMerchantAdmin.getMerId());
        if (currentMerchant.getCopyProductNum() <= 0) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "当前商户采集次数不足，请联系平台客服");
        }
        ProductResponseForCopyProduct productResponseForCopyProduct;
        try {
            JSONObject jsonObject = onePassService.copyGoods(url);
            productResponseForCopyProduct = ProductUtils.onePassCopyTransition(jsonObject);
        } catch (Exception e) {
            throw new CrmebException("一号通采集商品异常：" + e.getMessage());
        }
        Boolean sub = merchantService.subCopyProductNum(currentMerchant.getId());
        if (!sub) {
            LOGGER.error("扣除商户复制条数异常：商户ID = {}", currentMerchant.getId());
        }
        return productResponseForCopyProduct;
    }

    /**
     * 获取商品Map
     *
     * @param proIdList 商品id列表
     * @return Map
     */
    @Override
    public Map<Integer, Product> getMapByIdList(List<Integer> proIdList) {
        Map<Integer, Product> productMap = CollUtil.newHashMap();
        if (CollUtil.isEmpty(proIdList)) {
            return productMap;
        }
        LambdaQueryWrapper<Product> lqw = Wrappers.lambdaQuery();
        lqw.select(Product::getId, Product::getName, Product::getPrice, Product::getImage, Product::getIsShow, Product::getIsRecycle, Product::getIsDel);
        lqw.in(Product::getId, proIdList);
        List<Product> productList = dao.selectList(lqw);
        productList.forEach(e -> {
            productMap.put(e.getId(), e);
        });
        return productMap;
    }

    /**
     * 商品搜索分页列表（活动）
     *
     * @param request     搜索参数
     * @return PageInfo
     */
    @Override
    public PageInfo<ProductActivityResponse> getActivitySearchPage(ProductActivitySearchRequest request, SystemAdmin admin) {
        Page<Product> page = PageHelper.startPage(request.getPage(), request.getLimit());
        Map<String, Object> map = new HashMap<>();
        if (StrUtil.isNotBlank(request.getName())) {
            map.put("name", URLUtil.decode(request.getName()));
        }
        if (ObjectUtil.isNotNull(request.getCategoryId())) {
            map.put("categoryId", request.getCategoryId());
        }
        if (ObjectUtil.isNotNull(request.getIsShow())) {
            map.put("isShow", request.getIsShow() ? 1 : 0);
        }
        if (ObjectUtil.isNotNull(request.getMerStars()) && request.getMerStars() > 0) {
            map.put("merStars", request.getMerStars());
        }
        if (admin.getMerId() > 0) {
            request.setMerIds(admin.getMerId().toString());
        }
        if (StrUtil.isNotBlank(request.getMerIds())) {
            map.put("merIds", request.getMerIds());
        }
        if (ObjectUtil.isNotNull(request.getBrandId())) {
            map.put("brandId", request.getBrandId());
        }
        // 排序部分
        if (StrUtil.isNotBlank(request.getSalesOrder())) {
            if (request.getSalesOrder().equals(Constants.SORT_DESC)) {
                map.put("lastStr", " order by (p.sales + p.ficti) desc, p.rank desc, p.sort desc, p.id desc");
            } else {
                map.put("lastStr", " order by (p.sales + p.ficti) asc, p.rank desc, p.sort desc, p.id desc");
            }
        } else if (StrUtil.isNotBlank(request.getPriceOrder())) {
            if (request.getPriceOrder().equals(Constants.SORT_DESC)) {
                map.put("lastStr", " order by p.price desc, p.rank desc, p.sort desc, p.id desc");
            } else {
                map.put("lastStr", " order by p.price asc, p.rank desc, p.sort desc, p.id desc");
            }
        } else {
            map.put("lastStr", " order by p.rank desc, p.sort desc, p.id desc");
        }
        List<ProductActivityResponse> responseList = dao.getActivitySearchPage(map);
        responseList.forEach(response -> {
            List<ProductAttrValue> attrValueList = productAttrValueService.getListByProductIdAndType(response.getId(), response.getType(), ProductConstants.PRODUCT_MARKETING_TYPE_BASE, false);
            response.setAttrValue(attrValueList);
        });
        return CommonPage.copyPageInfo(page, responseList);
    }

    /**
     * 商品搜索分页列表（营销）
     *
     * @param request 搜索参数
     * @return PageInfo
     */
    @Override
    public PageInfo<ProductMarketingResponse> getMarketingSearchPage(PlatProductMarketingSearchRequest request, SystemAdmin admin) {
        Page<Product> page = PageHelper.startPage(request.getPage(), request.getLimit());
        Map<String, Object> map = new HashMap<>();
        if (StrUtil.isNotBlank(request.getName())) {
            map.put("name", URLUtil.decode(request.getName()));
        }
        if (ObjectUtil.isNotNull(request.getCategoryId())) {
            map.put("categoryId", request.getCategoryId());
        }
        if (ObjectUtil.isNotNull(request.getIsShow())) {
            map.put("isShow", request.getIsShow() ? 1 : 0);
        }
        if (StrUtil.isNotBlank(request.getMerIds())) {
            map.put("merIds", request.getMerIds());
        }
        List<ProductMarketingResponse> responseList = dao.getMarketingSearchPage(map);
        responseList.forEach(response -> {
            List<ProductAttrValue> attrValueList = productAttrValueService.getListByProductIdAndType(response.getId(),
                    response.getType(), ProductConstants.PRODUCT_MARKETING_TYPE_BASE, false);
            response.setAttrValue(attrValueList);
        });
        return CommonPage.copyPageInfo(page, responseList);
    }

    /**
     * 商品搜索分页列表（活动）商户端
     *
     * @param request 搜索参数
     * @return PageInfo
     */
    @Override
    public PageInfo<ProductActivityResponse> getActivitySearchPageByMerchant(ProductActivitySearchRequest request, SystemAdmin admin) {
        Page<Product> page = PageHelper.startPage(request.getPage(), request.getLimit());
        Map<String, Object> map = new HashMap<>();
        if (StrUtil.isNotBlank(request.getName())) {
            map.put("name", URLUtil.decode(request.getName()));
        }
        if (ObjectUtil.isNotNull(request.getCategoryId())) {
            map.put("categoryId", request.getCategoryId());
        }
        if (StrUtil.isNotBlank(request.getCateIds())) {
            String cateIdSql = CrmebUtil.getFindInSetSql("p.cate_id", request.getCateIds());
            map.put("cateIdSql", cateIdSql);
        }
        if (ObjectUtil.isNotNull(request.getIsShow())) {
            map.put("isShow", request.getIsShow() ? 1 : 0);
        }
        map.put("merId", admin.getMerId());
        if (ObjectUtil.isNotNull(request.getProductId())) {
            map.put("productId", request.getProductId());
        }
        // 排序部分
        if (StrUtil.isNotBlank(request.getSalesOrder())) {
            if (request.getSalesOrder().equals(Constants.SORT_DESC)) {
                map.put("lastStr", " order by (p.sales + p.ficti) desc, p.sort desc, p.id desc");
            } else {
                map.put("lastStr", " order by (p.sales + p.ficti) asc, p.sort desc, p.id desc");
            }
        } else if (StrUtil.isNotBlank(request.getPriceOrder())) {
            if (request.getPriceOrder().equals(Constants.SORT_DESC)) {
                map.put("lastStr", " order by p.price desc, p.sort desc, p.id desc");
            } else {
                map.put("lastStr", " order by p.price asc, p.sort desc, p.id desc");
            }
        } else {
            map.put("lastStr", " order by p.sort desc, p.id desc");
        }

        List<ProductActivityResponse> responseList = dao.getActivitySearchPageByMerchant(map);
        responseList.forEach(response -> {
            List<ProductAttrValue> attrValueList = productAttrValueService.getListByProductIdAndType(response.getId(), response.getType(), ProductConstants.PRODUCT_MARKETING_TYPE_BASE, false);
            response.setAttrValue(attrValueList);
        });
        return CommonPage.copyPageInfo(page, responseList);
    }

    /**
     * 商品搜索分页列表（营销）商户端
     *
     * @param request 搜索参数
     * @return PageInfo
     */
    @Override
    public PageInfo<ProductMarketingResponse> getMarketingSearchPageByMerchant(MerProductMarketingSearchRequest request, SystemAdmin admin) {
        Map<String, Object> map = new HashMap<>();
        if (request.getMarketingType().equals(ProductConstants.PRODUCT_MARKETING_TYPE_SECKILL)) {
            if (ObjectUtil.isNull(request.getActivityId())) {
                throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "请选择秒杀活动");
            }
            SeckillActivity activity = seckillActivityService.getById(request.getActivityId());
            if (ObjectUtil.isNull(activity) || activity.getIsDel()) {
                throw new CrmebException(MarketingResultCode.SECKILL_ACTIVITY_NOT_EXIST);
            }
            if (activity.getStatus().equals(2)) {
                throw new CrmebException(MarketingResultCode.SECKILL_ACTIVITY_END);
            }
            Merchant merchant = merchantService.getByIdException(admin.getMerId());
            if (activity.getMerStars() > merchant.getStarLevel()) {
                throw new CrmebException(MarketingResultCode.SECKILL_ACTIVITY_MERCHANT_LEVEL_INSUFFICIENT);
            }
            if (!activity.getProCategory().equals("0")) {
                map.put("proCateIds", activity.getProCategory());
            }
        }

        if (StrUtil.isNotBlank(request.getName())) {
            map.put("name", URLUtil.decode(request.getName()));
        }
        if (ObjectUtil.isNotNull(request.getCategoryId())) {
            map.put("categoryId", request.getCategoryId());
        }
        if (ObjectUtil.isNotNull(request.getCateId())) {
            map.put("cateId", request.getCateId());
        }
        if (ObjectUtil.isNotNull(request.getIsShow())) {
            map.put("isShow", request.getIsShow() ? 1 : 0);
        }
        if (ObjectUtil.isNotNull(request.getProductId()) && request.getProductId() > 0) {
            map.put("productId", request.getProductId());
        }
        map.put("merId", admin.getMerId());
        Page<Product> page = PageHelper.startPage(request.getPage(), request.getLimit());
        List<ProductMarketingResponse> responseList = dao.getMarketingSearchPageByMerchant(map);
        responseList.forEach(response -> {
            List<ProductAttrValue> attrValueList = productAttrValueService.getListByProductIdAndType(response.getId(),
                    response.getType(), ProductConstants.PRODUCT_MARKETING_TYPE_BASE, false);
            response.setAttrValue(attrValueList);
        });
        return CommonPage.copyPageInfo(page, responseList);
    }

    private Product getByIdException(Integer id) {
        Product product = getById(id);
        if (ObjectUtil.isNull(product) || product.getIsDel()) {
            throw new CrmebException(ProductResultCode.PRODUCT_NOT_EXIST);
        }
        return product;
    }

    /**
     * 把商品列表转换为 平台商品商品列表格式
     *
     * @param productList 商品列表
     * @return 平台商品列表格式
     */
    private List<PlatformProductListResponse> productListToPlatFromProductListResponse(List<Product> productList) {
        List<PlatformProductListResponse> platformProductListResponses = new ArrayList<>();
        for (Product product : productList) {
            PlatformProductListResponse r = new PlatformProductListResponse();
            BeanUtils.copyProperties(product, r);
            platformProductListResponses.add(r);
        }
        return platformProductListResponses;
    }

    /**
     * 领券中心优惠券商品列表
     *
     * @param couponCategory 优惠券类型：1-商家券, 2-商品券, 3-通用券，4-品类券，5-品牌券，6-跨店券
     * @param pidList        商品ID列表
     * @param linkedData     优惠券关联参数
     * @param pcIdList       商品分类ID列表（3级）
     */
    @Override
    public List<SimpleProductVo> findCouponListLimit3(Integer couponCategory, List<Integer> pidList, String linkedData, List<Integer> pcIdList) {
        LambdaQueryWrapper<Product> lqw = new LambdaQueryWrapper<>();
        lqw.select(Product::getId, Product::getName, Product::getImage, Product::getPrice, Product::getStock);
        switch (couponCategory) {
            case 2:
                lqw.in(Product::getId, pidList);
                break;
            case 4:
                lqw.in(Product::getCategoryId, pcIdList);
                break;
            case 5:
                lqw.eq(Product::getBrandId, Integer.valueOf(linkedData));
                break;
            case 6:
                lqw.in(Product::getMerId, CrmebUtil.stringToArray(linkedData));
                break;
        }
        getForSaleWhere(lqw);
        lqw.orderByDesc(Product::getSort, Product::getId);
        lqw.last(" limit 3");
        List<Product> productList = dao.selectList(lqw);
        if (CollUtil.isEmpty(productList)) {
            return new ArrayList<>();
        }
        return productList.stream().map(e -> {
            SimpleProductVo vo = new SimpleProductVo();
            BeanUtils.copyProperties(e, vo);
            return vo;
        }).collect(Collectors.toList());
    }

    /**
     * 系统优惠券商品列表
     *
     * @param couponId         优惠券ID
     * @param couponCategory   优惠券分类
     * @param couponLinkedDate 优惠券关联参数
     * @param pageParamRequest 分页参数
     */
    @Override
    public PageInfo<Product> findCouponProductList(Integer couponId, Integer couponCategory, String couponLinkedDate, SystemCouponProductSearchRequest pageParamRequest) {
        LambdaQueryWrapper<Product> lqw = new LambdaQueryWrapper<>();
        switch (couponCategory) {
            case 2:
                List<CouponProduct> couponProductList = couponProductService.findByCid(couponId);
                List<Integer> pidList = couponProductList.stream().map(CouponProduct::getPid).collect(Collectors.toList());
                lqw.in(Product::getId, pidList);
                break;
            case 4:
                ProductCategory productCategory = productCategoryService.getById(Integer.valueOf(couponLinkedDate));
                List<Integer> pcIdList = new ArrayList<>();
                if (productCategory.getLevel().equals(3)) {
                    pcIdList.add(productCategory.getId());
                } else {
                    List<ProductCategory> productCategoryList = new ArrayList<>();
                    if (productCategory.getLevel().equals(2)) {
                        productCategoryList = productCategoryService.findAllChildListByPid(productCategory.getId(), productCategory.getLevel());
                    }
                    if (productCategory.getLevel().equals(1)) {
                        productCategoryList = productCategoryService.getThirdCategoryByFirstId(productCategory.getId(), 0);
                    }
                    List<Integer> collect = productCategoryList.stream().map(ProductCategory::getId).collect(Collectors.toList());
                    pcIdList.addAll(collect);
                }
                lqw.in(Product::getCategoryId, pcIdList);
                break;
            case 5:
                lqw.eq(Product::getBrandId, Integer.valueOf(couponLinkedDate));
                break;
            case 6:
                lqw.in(Product::getMerId, CrmebUtil.stringToArray(couponLinkedDate));
                break;
        }
        getForSaleWhere(lqw);
        lqw.ne(Product::getType, ProductConstants.PRODUCT_TYPE_INTEGRAL);
        lqw.eq(Product::getMarketingType, ProductConstants.PRODUCT_MARKETING_TYPE_BASE);
        if (StrUtil.isNotBlank(pageParamRequest.getKeyword())) {
            String decode = URLUtil.decode(pageParamRequest.getKeyword());
            lqw.like(Product::getName, decode);
        }
        lqw.orderByDesc(Product::getSort, Product::getId);
        Page<Product> page = PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());
        List<Product> productList = dao.selectList(lqw);
        return CommonPage.copyPageInfo(page, productList);
    }

    /**
     * 通过ID获取商品列表
     *
     * @param proIdsList 商品ID列表
     */
    @Override
    public List<Product> findByIds(List<Integer> proIdsList) {
        return findByIdsAndLabel(proIdsList, "admin");
    }

    /**
     * 通过ID获取商品列表
     *
     * @param proIdsList 商品ID列表
     * @param label      admin-管理端，front-移动端
     */
    @Override
    public List<Product> findByIds(List<Integer> proIdsList, String label) {
        return findByIdsAndLabel(proIdsList, label);
    }

    /**
     * 通过ID获取商品列表
     *
     * @param proIdsList 商品ID列表
     * @param label      admin-管理端，front-移动端
     */
    private List<Product> findByIdsAndLabel(List<Integer> proIdsList, String label) {
        LambdaQueryWrapper<Product> lqw = Wrappers.lambdaQuery();
        lqw.in(Product::getId, proIdsList);
        if (label.equals("front")) {
            getForSaleWhere(lqw);
        }
        //拼接Sql
        StringBuilder builder = new StringBuilder();
        builder.append("order by field(id,");
        int length = proIdsList.size();
        for(int i= 0; i<length; i++) {
            if (i == 0) {
                builder.append(proIdsList.get(i));
            } else {
                builder.append(",")
                        .append(proIdsList.get(i));
            }
            if (i == length - 1) {
                builder.append(")");
            }
        }
        lqw.last(builder.toString());
        return dao.selectList(lqw);
    }

    /**
     * 获取首页推荐商品
     *
     * @param message 商品关联标识
     * @param value   分类ID、商户ID、品牌ID
     * @param expand  商品ID字符串
     * @param isHome  是否首页
     */
    @Override
    public List<Product> findHomeRecommended(String message, String value, String expand, boolean isHome) {
        LambdaQueryWrapper<Product> lqw = Wrappers.lambdaQuery();
        lqw.select(Product::getId, Product::getImage, Product::getName, Product::getSales, Product::getPrice,
                Product::getFicti, Product::getBrandId, Product::getMerId, Product::getCategoryId, Product::getIsPaidMember,
                Product::getVipPrice);
        getForSaleWhere(lqw);
        switch (message) {
            case "product":
                List<Integer> proIdList = CrmebUtil.stringToArray(expand);
                lqw.in(Product::getId, proIdList);
                break;
            case "category":
                lqw.eq(Product::getCategoryId, value);
                break;
            case "brand":
                lqw.eq(Product::getBrandId, value);
                break;
            case "merchant":
//                lqw.eq(Product::getMerId, value);
                List<Integer> merIdList = CrmebUtil.stringToArray(expand);
                lqw.in(Product::getMerId, merIdList);
                break;
        }
        if (isHome) {
            lqw.last(" order by sales + ficti desc limit 8");
        } else {
            lqw.last(" order by sales + ficti desc");
        }
        return dao.selectList(lqw);
    }

    /**
     * 推荐商品分页列表
     *
     * @param pageRequest 分页参数
     */
    @Override
    public PageInfo<RecommendProductResponse> findRecommendPage(PageParamRequest pageRequest) {
        LambdaQueryWrapper<Product> lqw = Wrappers.lambdaQuery();
        lqw.select(Product::getId, Product::getMerId, Product::getImage, Product::getName, Product::getUnitName,
                Product::getPrice, Product::getSales, Product::getFicti, Product::getCategoryId, Product::getBrandId,
                Product::getIsPaidMember, Product::getVipPrice);
        getForSaleWhere(lqw);
        lqw.orderByDesc(Product::getRank, Product::getSales, Product::getFicti, Product::getId);
        Page<Product> page = PageHelper.startPage(pageRequest.getPage(), pageRequest.getLimit());
        List<Product> productList = dao.selectList(lqw);
        if (CollUtil.isEmpty(productList)) {
            return CommonPage.copyPageInfo(page, CollUtil.newArrayList());
        }
        productList = activityStyleService.makeActivityBorderStyle(productList);
        List<RecommendProductResponse> responseList = productList.stream().map(p -> {
            RecommendProductResponse response = new RecommendProductResponse();
            BeanUtils.copyProperties(p, response);
            response.setSales(p.getSales() + p.getFicti());
            // 设置商品标签
            ProductTagsFrontResponse productTagsFrontResponse = productTagService.setProductTagByProductTagsRules(p.getId(), p.getBrandId(), p.getMerId(), p.getCategoryId(), response.getProductTags());
            response.setProductTags(productTagsFrontResponse);
            return response;
        }).collect(Collectors.toList());
        return CommonPage.copyPageInfo(page, responseList);
    }

    /**
     * 会员商品分页列表
     */
    @Override
    public PageInfo<RecommendProductResponse> findMemberPage(PageParamRequest pageParamRequest) {
        LambdaQueryWrapper<Product> lqw = Wrappers.lambdaQuery();
        lqw.select(Product::getId, Product::getMerId, Product::getImage, Product::getName, Product::getUnitName,
                Product::getPrice, Product::getSales, Product::getFicti, Product::getCategoryId, Product::getBrandId,
                Product::getIsPaidMember, Product::getVipPrice, Product::getStock);
        getForSaleWhere(lqw);
        lqw.eq(Product::getIsPaidMember, 1);
        lqw.orderByDesc(Product::getId);
        Page<Product> page = PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());
        List<Product> productList = dao.selectList(lqw);
        if (CollUtil.isEmpty(productList)) {
            return CommonPage.copyPageInfo(page, CollUtil.newArrayList());
        }
        productList = activityStyleService.makeActivityBorderStyle(productList);
        List<RecommendProductResponse> responseList = productList.stream().map(p -> {
            RecommendProductResponse response = new RecommendProductResponse();
            BeanUtils.copyProperties(p, response);
            response.setSales(p.getSales() + p.getFicti());
            // 设置商品标签
            ProductTagsFrontResponse productTagsFrontResponse = productTagService.setProductTagByProductTagsRules(p.getId(), p.getBrandId(), p.getMerId(), p.getCategoryId(), response.getProductTags());
            response.setProductTags(productTagsFrontResponse);
            return response;
        }).collect(Collectors.toList());
        return CommonPage.copyPageInfo(page, responseList);
    }

    /**
     * 校验商品是否可用（移动端可用）
     *
     * @param proId 商品ID
     */
    @Override
    public Boolean validatedCanUseById(Integer proId) {
        Product product = getById(proId);
        if (ObjectUtil.isNull(product)) return false;
        if (product.getIsDel()) return false;
        if (product.getIsRecycle()) return false;
        if (!product.getIsShow()) return false;
        if (!product.getAuditStatus().equals(ProductConstants.AUDIT_STATUS_SUCCESS)
                && !product.getAuditStatus().equals(ProductConstants.AUDIT_STATUS_EXEMPTION)) {
            return false;
        }
        return true;
    }

    /**
     * 根据关键字获取商品所有的品牌ID
     *
     * @param keyword 关键字
     */
    @Override
    public List<Integer> findProductBrandIdByKeyword(String keyword) {
        return dao.findProductBrandIdByKeyword(URLUtil.decode(keyword));
    }

    /**
     * 根据关键字获取商品所有的分类ID
     *
     * @param keyword 关键字
     */
    @Override
    public List<Integer> findProductCategoryIdByKeyword(String keyword) {
        return dao.findProductCategoryIdByKeyword(URLUtil.decode(keyword));
    }

    /**
     * 设置运费模板
     */
    @Override
    public Boolean setFreightTemplate(ProductFreightTemplateRequest request, SystemAdmin admin) {
        Product product = getByIdException(request.getId());
        if (!admin.getMerId().equals(product.getMerId())) {
            throw new CrmebException(ProductResultCode.PRODUCT_NOT_EXIST);
        }
        if (product.getIsRecycle()) {
            throw new CrmebException(ProductResultCode.PRODUCT_DELETE);
        }
        if (product.getIsShow()) {
            throw new CrmebException(ProductResultCode.PRODUCT_IS_SHOW);
        }
        if (product.getIsAudit()) {
            throw new CrmebException(ProductResultCode.PRODUCT_AUDIT_ING);
        }
        if (product.getAuditStatus().equals(ProductConstants.AUDIT_STATUS_WAIT) || product.getAuditStatus().equals(ProductConstants.AUDIT_STATUS_FAIL)) {
            throw new CrmebException(ProductResultCode.PRODUCT_AUDIT_STATUS_EXCEPTION.setMessage("只有仓库中商品才能设置运费"));
        }
        if (!product.getType().equals(ProductConstants.PRODUCT_TYPE_NORMAL)) {
            // 云盘、卡密商品直接返回成功，不修改运费模板
            return Boolean.TRUE;
        }
        ShippingTemplates shippingTemplate = shippingTemplatesService.getById(request.getTemplateId());
        if (ObjectUtil.isNull(shippingTemplate) || !admin.getMerId().equals(shippingTemplate.getMerId())) {
            throw new CrmebException(ProductResultCode.SHIPPING_TEMPLATES_NOT_EXIST);
        }
        LambdaUpdateWrapper<Product> wrapper = Wrappers.lambdaUpdate();
        wrapper.set(Product::getTempId, shippingTemplate.getId());
        wrapper.eq(Product::getId, product.getId());
        return update(wrapper);
    }

    /**
     * 设置佣金
     */
    @Override
    public Boolean setBrokerage(ProductSetBrokerageRequest request, SystemAdmin admin) {
        Product product = getByIdException(request.getId());
        if (!admin.getMerId().equals(product.getMerId())) {
            throw new CrmebException(ProductResultCode.PRODUCT_NOT_EXIST);
        }
        if (product.getIsRecycle()) {
            throw new CrmebException(ProductResultCode.PRODUCT_DELETE);
        }
        if (product.getIsShow()) {
            throw new CrmebException(ProductResultCode.PRODUCT_IS_SHOW);
        }
        if (product.getIsAudit()) {
            throw new CrmebException(ProductResultCode.PRODUCT_AUDIT_ING);
        }
        if (product.getAuditStatus().equals(ProductConstants.AUDIT_STATUS_WAIT) || product.getAuditStatus().equals(ProductConstants.AUDIT_STATUS_FAIL)) {
            throw new CrmebException(ProductResultCode.PRODUCT_AUDIT_STATUS_EXCEPTION.setMessage("只有仓库中商品才能设置佣金"));
        }
        int brokerageRatio = request.getBrokerage() + request.getBrokerageTwo();
        if (brokerageRatio > crmebConfig.getRetailStoreBrokerageRatio()) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, StrUtil.format("一二级返佣比例之和范围为 0~{}", crmebConfig.getRetailStoreBrokerageRatio()));
        }
        return transactionTemplate.execute(e -> {
            if (!product.getIsSub()) {
                LambdaUpdateWrapper<Product> wrapper = Wrappers.lambdaUpdate();
                wrapper.set(Product::getIsSub, 1);
                wrapper.eq(Product::getId, product.getId());
                update(wrapper);
            }
            productAttrValueService.updateBrokerageByProductId(product.getId(), product.getType(), product.getMarketingType(), request.getBrokerage(), request.getBrokerageTwo());
            return Boolean.TRUE;
        });
    }

    /**
     * 添加回馈券
     */
    @Override
    public Boolean addFeedbackCoupons(ProductAddFeedbackCouponsRequest request, SystemAdmin admin) {
        Product product = getByIdException(request.getId());
        if (!admin.getMerId().equals(product.getMerId())) {
            throw new CrmebException(ProductResultCode.PRODUCT_NOT_EXIST);
        }
        if (product.getIsRecycle()) {
            throw new CrmebException(ProductResultCode.PRODUCT_DELETE);
        }
        if (product.getIsShow()) {
            throw new CrmebException(ProductResultCode.PRODUCT_IS_SHOW);
        }
        if (product.getIsAudit()) {
            throw new CrmebException(ProductResultCode.PRODUCT_AUDIT_ING);
        }
        if (product.getAuditStatus().equals(ProductConstants.AUDIT_STATUS_WAIT) || product.getAuditStatus().equals(ProductConstants.AUDIT_STATUS_FAIL)) {
            throw new CrmebException(ProductResultCode.PRODUCT_AUDIT_STATUS_EXCEPTION.setMsgParams("只有仓库中商品才能设置添加回馈券"));
        }
        List<Coupon> couponList = couponService.findByIds(request.getCouponIds());
        for (Coupon coupon : couponList) {
            if (!coupon.getMerId().equals(admin.getMerId())) {
                throw new CrmebException(CouponResultCode.COUPON_NOT_EXIST);
            }
        }
        List<ProductCoupon> productCouponList = productCouponService.getListByProductId(product.getId());
        if (CollUtil.isNotEmpty(productCouponList)) {
            List<Integer> pcList = productCouponList.stream().map(ProductCoupon::getCouponId).collect(Collectors.toList());
            for (int i = 0; i < couponList.size(); ) {
                Coupon coupon = couponList.get(i);
                if (pcList.contains(coupon.getId())) {
                    couponList.remove(i);
                    continue;
                }
                i++;
            }
        }
        if (CollUtil.isEmpty(couponList)) {
            return Boolean.TRUE;
        }
        List<ProductCoupon> addPCList = new ArrayList<>();
        for (Integer couponId : request.getCouponIds()) {
            ProductCoupon spc = new ProductCoupon();
            spc.setProductId(product.getId());
            spc.setCouponId(couponId);
            spc.setAddTime(CrmebDateUtil.getNowTime());
            addPCList.add(spc);
        }
        return productCouponService.saveBatch(addPCList);
    }

    /**
     * 批量上架商品
     *
     * @param idList 商品ID列表
     */
    @Override
    public Boolean batchUp(List<Integer> idList, SystemAdmin admin) {
        List<Product> productList = findByIds(idList);
        if (CollUtil.isEmpty(productList) || productList.size() != idList.size()) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "商品数量不符或商品未找到");
        }
        for (int i = 0; i < productList.size(); ) {
            Product product = productList.get(i);
            if (!admin.getMerId().equals(product.getMerId())) {
                throw new CrmebException(ProductResultCode.PRODUCT_NOT_EXIST);
            }
            if (product.getIsAudit()) {
                throw new CrmebException(ProductResultCode.PRODUCT_AUDIT_ING);
            }
            if (!product.getAuditStatus().equals(ProductConstants.AUDIT_STATUS_EXEMPTION) && !product.getAuditStatus().equals(ProductConstants.AUDIT_STATUS_SUCCESS)) {
                throw new CrmebException(ProductResultCode.PRODUCT_AUDIT_STATUS_EXCEPTION);
            }
            if (product.getIsShow()) {
                productList.remove(i);
                continue;
            }
            i++;
        }
        if (CollUtil.isEmpty(productList)) {
            return Boolean.TRUE;
        }
        List<Integer> proIdList = productList.stream().map(Product::getId).collect(Collectors.toList());
        Merchant merchant = merchantService.getById(admin.getMerId());
        if (!merchant.getIsSwitch()) {
            throw new CrmebException(MerchantResultCode.MERCHANT_SWITCH_CLOSE);
        }
        return transactionTemplate.execute(e -> {
            Boolean update = productBatchUpOrDown(proIdList, "up");
            if (!update) {
                LOGGER.error("商品批量上架更新状态失败，idList = {}", idList);
                e.setRollbackOnly();
                return Boolean.FALSE;
            }
            productList.forEach(product -> {
                List<ProductAttrValue> skuList = productAttrValueService.getListByProductIdAndType(product.getId(), product.getType(), product.getMarketingType(), false);
                List<Integer> skuIdList = skuList.stream().map(ProductAttrValue::getId).collect(Collectors.toList());
                cartService.productStatusNoEnable(skuIdList);
            });
            return Boolean.TRUE;
        });
    }

    /**
     * 批量商品下架
     *
     * @param idList 商品ID列表
     */
    @Override
    public Boolean batchDown(List<Integer> idList, SystemAdmin admin) {
        List<Product> productList = findByIds(idList);
        if (CollUtil.isEmpty(productList) || productList.size() != idList.size()) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "商品数量不符或商品未找到");
        }
        for (int i = 0; i < productList.size(); ) {
            Product product = productList.get(i);
            if (!admin.getMerId().equals(product.getMerId())) {
                throw new CrmebException(ProductResultCode.PRODUCT_NOT_EXIST);
            }
            if (!product.getIsShow()) {
                productList.remove(i);
                continue;
            }
            i++;
        }
        if (CollUtil.isEmpty(productList)) {
            return Boolean.TRUE;
        }
        List<Integer> proIdList = productList.stream().map(Product::getId).collect(Collectors.toList());
        return transactionTemplate.execute(e -> {
            Boolean update = productBatchUpOrDown(proIdList, "down");
            if (!update) {
                LOGGER.error("商品批量下架更新状态失败，idList = {}", idList);
                e.setRollbackOnly();
                return Boolean.FALSE;
            }
            productList.forEach(product -> {
                cartService.productStatusNotEnable(product.getId());
                // 商品下架时，清除用户收藏
                productRelationService.deleteByProId(product.getId());
            });
            return Boolean.TRUE;
        });
    }

    /**
     * 批量设置运费模板
     */
    @Override
    public Boolean batchSetFreightTemplate(BatchSetProductFreightTemplateRequest request, SystemAdmin admin) {
        List<Product> productList = findByIds(request.getIdList());
        if (CollUtil.isEmpty(productList) || productList.size() != request.getIdList().size()) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "商品数量不符或商品未找到");
        }
        for (int i = 0; i < productList.size(); ) {
            Product product = productList.get(i);
            if (!admin.getMerId().equals(product.getMerId())) {
                throw new CrmebException(ProductResultCode.PRODUCT_NOT_EXIST);
            }
            if (product.getIsRecycle()) {
                throw new CrmebException(ProductResultCode.PRODUCT_DELETE);
            }
            if (product.getIsShow()) {
                throw new CrmebException(ProductResultCode.PRODUCT_IS_SHOW);
            }
            if (product.getIsAudit()) {
                throw new CrmebException(ProductResultCode.PRODUCT_AUDIT_ING);
            }
            if (product.getAuditStatus().equals(ProductConstants.AUDIT_STATUS_WAIT) || product.getAuditStatus().equals(ProductConstants.AUDIT_STATUS_FAIL)) {
                throw new CrmebException(ProductResultCode.PRODUCT_AUDIT_STATUS_EXCEPTION.setMessage("只有仓库中商品才能设置运费"));
            }
            if (!product.getType().equals(ProductConstants.PRODUCT_TYPE_NORMAL)) {
                // 云盘、卡密商品直接返回成功，不修改运费模板
                productList.remove(i);
                continue;
            }
            i++;
        }
        if (CollUtil.isEmpty(productList)) {
            return Boolean.TRUE;
        }
        ShippingTemplates shippingTemplate = shippingTemplatesService.getById(request.getTemplateId());
        if (ObjectUtil.isNull(shippingTemplate) || !admin.getMerId().equals(shippingTemplate.getMerId())) {
            throw new CrmebException(ProductResultCode.SHIPPING_TEMPLATES_NOT_EXIST);
        }
        List<Integer> proIdList = productList.stream().map(Product::getId).collect(Collectors.toList());
        LambdaUpdateWrapper<Product> wrapper = Wrappers.lambdaUpdate();
        wrapper.set(Product::getTempId, shippingTemplate.getId());
        wrapper.in(Product::getId, proIdList);
        return update(wrapper);
    }

    /**
     * 批量设置佣金
     */
    @Override
    public Boolean batchSetBrokerage(BatchSetProductBrokerageRequest request, SystemAdmin admin) {
        List<Product> productList = findByIds(request.getIdList());
        if (CollUtil.isEmpty(productList) || productList.size() != request.getIdList().size()) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "商品数量不符或商品未找到");
        }
        for (Product product : productList) {
            if (!admin.getMerId().equals(product.getMerId())) {
                throw new CrmebException(ProductResultCode.PRODUCT_NOT_EXIST);
            }
            if (product.getIsRecycle()) {
                throw new CrmebException(ProductResultCode.PRODUCT_DELETE);
            }
            if (product.getIsShow()) {
                throw new CrmebException(ProductResultCode.PRODUCT_IS_SHOW);
            }
            if (product.getIsAudit()) {
                throw new CrmebException(ProductResultCode.PRODUCT_AUDIT_ING);
            }
            if (product.getAuditStatus().equals(ProductConstants.AUDIT_STATUS_WAIT) || product.getAuditStatus().equals(ProductConstants.AUDIT_STATUS_FAIL)) {
                throw new CrmebException(ProductResultCode.PRODUCT_AUDIT_STATUS_EXCEPTION.setMessage("只有仓库中商品才能设置佣金"));
            }
        }
        int brokerageRatio = request.getBrokerage() + request.getBrokerageTwo();
        if (brokerageRatio > crmebConfig.getRetailStoreBrokerageRatio()) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, StrUtil.format("一二级返佣比例之和范围为 0~{}", crmebConfig.getRetailStoreBrokerageRatio()));
        }
        return transactionTemplate.execute(e -> {
            productList.forEach(product -> {
                if (!product.getIsSub()) {
                    LambdaUpdateWrapper<Product> wrapper = Wrappers.lambdaUpdate();
                    wrapper.set(Product::getIsSub, 1);
                    wrapper.eq(Product::getId, product.getId());
                    update(wrapper);
                }
                productAttrValueService.updateBrokerageByProductId(product.getId(), product.getType(),
                        product.getMarketingType(), request.getBrokerage(), request.getBrokerageTwo());
            });
            return Boolean.TRUE;
        });
    }

    /**
     * 批量添加回馈券
     */
    @Override
    public Boolean batchAddFeedbackCoupons(BatchAddProductFeedbackCouponsRequest request, SystemAdmin admin) {
        List<Product> productList = findByIds(request.getIdList());
        if (CollUtil.isEmpty(productList) || productList.size() != request.getIdList().size()) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "商品数量不符或商品未找到");
        }
        for (Product product : productList) {
            if (!admin.getMerId().equals(product.getMerId())) {
                throw new CrmebException(ProductResultCode.PRODUCT_NOT_EXIST);
            }
            if (product.getIsRecycle()) {
                throw new CrmebException(ProductResultCode.PRODUCT_DELETE);
            }
            if (product.getIsShow()) {
                throw new CrmebException(ProductResultCode.PRODUCT_IS_SHOW);
            }
            if (product.getIsAudit()) {
                throw new CrmebException(ProductResultCode.PRODUCT_AUDIT_ING);
            }
            if (product.getAuditStatus().equals(ProductConstants.AUDIT_STATUS_WAIT) || product.getAuditStatus().equals(ProductConstants.AUDIT_STATUS_FAIL)) {
                throw new CrmebException(ProductResultCode.PRODUCT_AUDIT_STATUS_EXCEPTION.setMessage("只有仓库中商品才能设置添加回馈券"));
            }
        }
        List<Coupon> couponList = couponService.findByIds(request.getCouponIds());
        for (Coupon coupon : couponList) {
            if (!coupon.getMerId().equals(admin.getMerId())) {
                throw new CrmebException(CouponResultCode.COUPON_NOT_EXIST);
            }
        }
        return transactionTemplate.execute(e -> {
            for (Product product : productList) {
                List<ProductCoupon> productCouponList = productCouponService.getListByProductId(product.getId());
                if (CollUtil.isNotEmpty(productCouponList)) {
                    List<Integer> pcList = productCouponList.stream().map(ProductCoupon::getCouponId).collect(Collectors.toList());
                    for (int i = 0; i < couponList.size(); ) {
                        Coupon coupon = couponList.get(i);
                        if (pcList.contains(coupon.getId())) {
                            couponList.remove(i);
                            continue;
                        }
                        i++;
                    }
                }
                if (CollUtil.isEmpty(couponList)) {
                    continue;
                }
                List<ProductCoupon> addPCList = new ArrayList<>();
                for (Integer couponId : request.getCouponIds()) {
                    ProductCoupon spc = new ProductCoupon();
                    spc.setProductId(product.getId());
                    spc.setCouponId(couponId);
                    spc.setAddTime(CrmebDateUtil.getNowTime());
                    addPCList.add(spc);
                }
                productCouponService.saveBatch(addPCList);
            }
            return Boolean.TRUE;
        });
    }

    /**
     * 商品批量加入回收站
     *
     * @param idList 商品ID列表
     */
    @Override
    public Boolean batchRecycle(List<Integer> idList, SystemAdmin admin) {
        List<Product> productList = findByIds(idList);
        if (CollUtil.isEmpty(productList) || productList.size() != idList.size()) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "商品数量不符或商品未找到");
        }
        for (Product product : productList) {
            if (!admin.getMerId().equals(product.getMerId())) {
                throw new CrmebException(ProductResultCode.PRODUCT_NOT_EXIST);
            }
            if (product.getIsRecycle()) {
                throw new CrmebException(ProductResultCode.PRODUCT_RECYCLE);
            }
        }
        LambdaUpdateWrapper<Product> wrapper = new LambdaUpdateWrapper<>();
        wrapper.set(Product::getIsRecycle, 1);
        wrapper.in(Product::getId, idList);
        return transactionTemplate.execute(e -> {
            update(wrapper);
            idList.forEach(id -> {
                cartService.productStatusNotEnable(id);
            });
            return Boolean.TRUE;
        });
    }

    /**
     * 批量删除商品
     *
     * @param idList 商品ID列表
     */
    @Override
    public Boolean batchDelete(List<Integer> idList, SystemAdmin admin) {
        List<Product> productList = findByIds(idList);
        if (CollUtil.isEmpty(productList) || productList.size() != idList.size()) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "商品数量不符或商品未找到");
        }
        for (Product product : productList) {
            if (!admin.getMerId().equals(product.getMerId())) {
                throw new CrmebException(ProductResultCode.PRODUCT_NOT_EXIST);
            }
//            if (!product.getIsRecycle()) {
//                throw new CrmebException("只有回收站中的商品才能删除");
//            }
        }
        LambdaUpdateWrapper<Product> wrapper = new LambdaUpdateWrapper<>();
        wrapper.set(Product::getIsDel, 1);
        wrapper.in(Product::getId, idList);
        return transactionTemplate.execute(e -> {
            update(wrapper);
            productList.forEach(product -> {
                cartService.productDelete(product.getId());
                if (product.getType().equals(ProductConstants.PRODUCT_TYPE_CDKEY)) {
                    cdkeyLibraryService.clearAssociationProduct(product.getId());
                }
            });
            return Boolean.TRUE;
        });
    }

    /**
     * 批量恢复回收站商品
     *
     * @param idList 商品ID列表
     */
    @Override
    public Boolean batchRestore(List<Integer> idList, SystemAdmin admin) {
        LambdaUpdateWrapper<Product> wrapper = new LambdaUpdateWrapper<>();
        wrapper.set(Product::getIsRecycle, 0);
        wrapper.set(Product::getIsShow, 0);
        wrapper.in(Product::getId, idList);
        wrapper.eq(Product::getMerId, admin.getMerId());
        return update(wrapper);
    }

    /**
     * 批量提审商品
     *
     * @param idList 商品ID列表
     */
    @Override
    public Boolean batchSubmitAudit(List<Integer> idList, SystemAdmin admin) {
        List<Product> productList = findByIds(idList);
        if (CollUtil.isEmpty(productList) || productList.size() != idList.size()) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "商品数量不符或商品未找到");
        }
        for (Product product : productList) {
            if (!admin.getMerId().equals(product.getMerId())) {
                throw new CrmebException(ProductResultCode.PRODUCT_NOT_EXIST);
            }
            if (product.getIsAudit()) {
                throw new CrmebException(ProductResultCode.PRODUCT_AUDIT_ING);
            }
            if (!product.getAuditStatus().equals(ProductConstants.AUDIT_STATUS_WAIT)) {
                throw new CrmebException(ProductResultCode.PRODUCT_AUDIT_STATUS_EXCEPTION);
            }
            if (product.getIsRecycle()) {
                throw new CrmebException(ProductResultCode.PRODUCT_RECYCLE);
            }
        }
        LambdaUpdateWrapper<Product> wrapper = Wrappers.lambdaUpdate();
        wrapper.set(Product::getIsAudit, 1);
        wrapper.set(Product::getIsShow, 0);
        wrapper.in(Product::getId, idList);
        return update(wrapper);
    }

    /**
     * 平台端批量设置虚拟销量
     */
    @Override
    public Boolean platBatchSetVirtualSales(BatchSetVirtualSalesRequest request) {
        UpdateWrapper<Product> wrapper = Wrappers.update();
        wrapper.setSql(StrUtil.format("ficti = ficti + {}", request.getFicti()));
        wrapper.in("id", request.getIdList());
        return update(wrapper);
    }

    /**
     * 平台端批量商品审核
     */
    @Override
    public Boolean batchAudit(BatchProductAuditRequest request) {
        if (request.getAuditStatus().equals("fail") && StrUtil.isEmpty(request.getReason())) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "审核拒绝请填写拒绝原因");
        }
        List<Product> productList = findByIds(request.getIdList());
        for (Product product : productList) {
            if (!product.getAuditStatus().equals(ProductConstants.AUDIT_STATUS_WAIT)) {
                throw new CrmebException(ProductResultCode.PRODUCT_AUDIT_STATUS_EXCEPTION);
            }
            if (!product.getIsAudit()) {
                throw new CrmebException(ProductResultCode.PRODUCT_AUDIT_STATUS_EXCEPTION);
            }
        }
        if (request.getAuditStatus().equals("fail")) {
            LambdaUpdateWrapper<Product> wrapper = Wrappers.lambdaUpdate();
            wrapper.set(Product::getAuditStatus, ProductConstants.AUDIT_STATUS_FAIL);
            wrapper.set(Product::getReason, request.getReason());
            wrapper.set(Product::getIsAudit, 0);
            wrapper.set(Product::getIsShow, 0);
            wrapper.in(Product::getId, request.getIdList());
            return update(wrapper);
        }
        List<Integer> merIdList = productList.stream().map(Product::getMerId).distinct().collect(Collectors.toList());
        Map<Integer, Merchant> merchantMap = merchantService.getMapByIdList(merIdList);
        productList.forEach(product -> {
            product.setAuditStatus(ProductConstants.AUDIT_STATUS_SUCCESS);
            Merchant merchant = merchantMap.get(product.getMerId());
            if (!merchant.getProductSwitch()) {
                product.setAuditStatus(ProductConstants.AUDIT_STATUS_EXEMPTION);
            }
            product.setIsAudit(false);
            product.setIsShow(false);
        });
        return updateBatchById(productList);
    }

    /**
     * 清除商品系统表单
     *
     * @param systemFormId 系统表单ID
     */
    @Override
    public Boolean clearSystemFormByFormId(Integer systemFormId) {
        LambdaUpdateWrapper<Product> wrapper = Wrappers.lambdaUpdate();
        wrapper.set(Product::getSystemFormId, 0);
        wrapper.eq(Product::getSystemFormId, systemFormId);
        wrapper.eq(Product::getIsDel, 0);
        return update(wrapper);
    }

    private Boolean productBatchUpOrDown(List<Integer> idList, String operation) {
        LambdaUpdateWrapper<Product> wrapper = Wrappers.lambdaUpdate();
        if (operation.equals("up")) {
            wrapper.set(Product::getIsShow, 1);
        } else {
            wrapper.set(Product::getIsShow, 0);
        }
        wrapper.in(Product::getId, idList);
        return update(wrapper);
    }

    /**
     * 新增积分商品
     */
    @Override
    public Boolean saveIntegralProduct(IntegralProductAddRequest request) {
        if (!request.getSpecType()) {
            if (request.getAttrValueList().size() > 1) {
                throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "单规格商品属性值不能大于1");
            }
        }
        if (StrUtil.isBlank(request.getKeyword())) {
            request.setKeyword("");
        } else if (request.getKeyword().length() > 32) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "关键字长度不能超过32个字符");
        }

        Product product = new Product();
        BeanUtils.copyProperties(request, product);
        product.setId(null);
        product.setMerId(0);
        product.setMarketingType(ProductConstants.PRODUCT_MARKETING_TYPE_BASE);
        product.setType(ProductConstants.PRODUCT_TYPE_INTEGRAL);
        product.setDeliveryMethod("1");
        product.setTempId(0);
        product.setRefundSwitch(false);
        String cdnUrl = systemAttachmentService.getCdnUrl();
        //主图
        product.setImage(systemAttachmentService.clearPrefix(product.getImage(), cdnUrl));
        //轮播图
        product.setSliderImage(systemAttachmentService.clearPrefix(product.getSliderImage(), cdnUrl));

        List<IntegralProductAttrValueAddRequest> attrValueAddRequestList = request.getAttrValueList();
        //计算价格
        IntegralProductAttrValueAddRequest minAttrValue;
        IntegralProductAttrValueAddRequest tempAttrValue = attrValueAddRequestList.stream().min(Comparator.comparing(IntegralProductAttrValueAddRequest::getRedeemIntegral)).get();
        List<IntegralProductAttrValueAddRequest> minAttrValueList = attrValueAddRequestList.stream().filter(e -> e.getRedeemIntegral().equals(tempAttrValue.getRedeemIntegral())).collect(Collectors.toList());
        if (minAttrValueList.size() > 1) {
            minAttrValue = minAttrValueList.stream().min(Comparator.comparing(IntegralProductAttrValueAddRequest::getPrice)).get();
        } else {
            minAttrValue = tempAttrValue;
        }

        product.setRedeemIntegral(minAttrValue.getRedeemIntegral());
        product.setPrice(minAttrValue.getPrice());
        product.setOtPrice(BigDecimal.ZERO);
        product.setCost(minAttrValue.getCost());
        product.setAuditStatus(ProductConstants.AUDIT_STATUS_EXEMPTION);
        product.setIsAudit(false);

        List<ProductAttrAddRequest> addRequestList = request.getAttrList();
        List<ProductAttribute> attrList = new ArrayList<>();
        Map<String, List<ProductAttributeOption>> optionMap = new HashMap<>();

        addRequestList.forEach(attrRequest -> {
            ProductAttribute attr = new ProductAttribute();
            attr.setAttributeName(attrRequest.getAttributeName());
            attr.setIsShowImage(attrRequest.getIsShowImage());
            attr.setSort(ObjectUtil.isNotNull(attrRequest.getSort()) ? attrRequest.getSort() : 0);
            List<ProductAttrOptionAddRequest> optionRequestList = attrRequest.getOptionList();
            List<ProductAttributeOption> attrOptionList = optionRequestList.stream().map(optionRequest -> {
                ProductAttributeOption option = new ProductAttributeOption();
                option.setOptionName(optionRequest.getOptionName());
                option.setSort(ObjectUtil.isNotNull(optionRequest.getSort()) ? optionRequest.getSort() : 0);
                option.setImage(StrUtil.isNotBlank(optionRequest.getImage()) ? systemAttachmentService.clearPrefix(optionRequest.getImage(), cdnUrl) : "");
                return option;
            }).collect(Collectors.toList());
            attrList.add(attr);
            optionMap.put(attr.getAttributeName(), attrOptionList);
        });

        List<ProductAttrValue> attrValueList = attrValueAddRequestList.stream().map(e -> {
            ProductAttrValue attrValue = new ProductAttrValue();
            BeanUtils.copyProperties(e, attrValue);
            attrValue.setId(null);
            attrValue.setSku(getSku(e.getAttrValue()));
            attrValue.setQuota(0);
            attrValue.setQuotaShow(0);
            attrValue.setType(product.getType());
            attrValue.setMarketingType(product.getMarketingType());
            attrValue.setImage(systemAttachmentService.clearPrefix(e.getImage(), cdnUrl));
            attrValue.setVipPrice(BigDecimal.ZERO);
            return attrValue;
        }).collect(Collectors.toList());

        product.setStock(attrValueList.stream().mapToInt(ProductAttrValue::getStock).sum());

        // 处理富文本
        ProductDescription spd = new ProductDescription();
        spd.setDescription(StrUtil.isNotBlank(request.getContent()) ? systemAttachmentService.clearPrefix(request.getContent(), cdnUrl) : "");
        spd.setType(product.getType());
        spd.setMarketingType(product.getMarketingType());

        return transactionTemplate.execute(e -> {
            boolean save = save(product);
            if (!save) {
                e.setRollbackOnly();
                LOGGER.error("添加积分商品失败，request = {}", request);
                return Boolean.FALSE;
            }

            attrList.forEach(attr -> attr.setProductId(product.getId()));
            productAttributeService.saveBatch(attrList);
            attrList.forEach(attr -> {
                List<ProductAttributeOption> optionList = optionMap.get(attr.getAttributeName());
                optionList.forEach(option -> {
                    option.setProductId(attr.getProductId());
                    option.setAttributeId(attr.getId());
                });
                productAttributeOptionService.saveBatch(optionList);
            });
            attrValueList.forEach(value -> value.setProductId(product.getId()));
            productAttrValueService.saveBatch(attrValueList, 100);

            spd.setProductId(product.getId());
            productDescriptionService.deleteByProductId(product.getId(), product.getType(), product.getMarketingType());
            productDescriptionService.save(spd);

            return Boolean.TRUE;
        });
    }

    /**
     * 修改积分商品
     */
    @Override
    public Boolean updateIntegralProduct(IntegralProductAddRequest request) {
        if (ObjectUtil.isNull(request.getId())) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "商品ID不能为空");
        }
        if (!request.getSpecType()) {
            if (request.getAttrValueList().size() > 1) {
                throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "单规格商品属性值不能大于1");
            }
        }
        if (StrUtil.isBlank(request.getKeyword())) {
            request.setKeyword("");
        } else if (request.getKeyword().length() > 32) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "关键字长度不能超过32个字符");
        }

        Product tempProduct = getById(request.getId());
        if (ObjectUtil.isNull(tempProduct) || !tempProduct.getMerId().equals(0)) {
            throw new CrmebException(ProductResultCode.PRODUCT_NOT_EXIST);
        }
        if (tempProduct.getIsRecycle() || tempProduct.getIsDel()) {
            throw new CrmebException(ProductResultCode.PRODUCT_DELETE);
        }

        Product product = new Product();
        BeanUtils.copyProperties(request, product);
        product.setType(tempProduct.getType());
        product.setMarketingType(tempProduct.getMarketingType());

        String cdnUrl = systemAttachmentService.getCdnUrl();
        //主图
        product.setImage(systemAttachmentService.clearPrefix(product.getImage(), cdnUrl));
        //轮播图
        product.setSliderImage(systemAttachmentService.clearPrefix(product.getSliderImage(), cdnUrl));

        List<IntegralProductAttrValueAddRequest> attrValueAddRequestList = request.getAttrValueList();
        //计算价格
        IntegralProductAttrValueAddRequest minAttrValue;
        IntegralProductAttrValueAddRequest tempAttrValue = attrValueAddRequestList.stream().min(Comparator.comparing(IntegralProductAttrValueAddRequest::getRedeemIntegral)).get();
        List<IntegralProductAttrValueAddRequest> minAttrValueList = attrValueAddRequestList.stream().filter(e -> e.getRedeemIntegral().equals(tempAttrValue.getRedeemIntegral())).collect(Collectors.toList());
        if (minAttrValueList.size() > 1) {
            minAttrValue = minAttrValueList.stream().min(Comparator.comparing(IntegralProductAttrValueAddRequest::getPrice)).get();
        } else {
            minAttrValue = tempAttrValue;
        }
        product.setRedeemIntegral(minAttrValue.getRedeemIntegral());
        product.setPrice(minAttrValue.getPrice());
        product.setCost(minAttrValue.getCost());

        // attr部分
        List<ProductAttrAddRequest> addRequestList = request.getAttrList();
        List<ProductAttribute> attrList = new ArrayList<>();
        Map<String, List<ProductAttributeOption>> optionMap = new HashMap<>();

        addRequestList.forEach(attrRequest -> {
            ProductAttribute attr = new ProductAttribute();
            attr.setProductId(product.getId());
            attr.setAttributeName(attrRequest.getAttributeName());
            attr.setIsShowImage(attrRequest.getIsShowImage());
            attr.setSort(ObjectUtil.isNotNull(attrRequest.getSort()) ? attrRequest.getSort() : 0);
            List<ProductAttrOptionAddRequest> optionRequestList = attrRequest.getOptionList();
            List<ProductAttributeOption> attrOptionList = optionRequestList.stream().map(optionRequest -> {
                ProductAttributeOption option = new ProductAttributeOption();
                option.setProductId(product.getId());
                option.setOptionName(optionRequest.getOptionName());
                option.setSort(ObjectUtil.isNotNull(optionRequest.getSort()) ? optionRequest.getSort() : 0);
                option.setImage(StrUtil.isNotBlank(optionRequest.getImage()) ? systemAttachmentService.clearPrefix(optionRequest.getImage(), cdnUrl) : "");
                return option;
            }).collect(Collectors.toList());
            attrList.add(attr);
            optionMap.put(attr.getAttributeName(), attrOptionList);
        });

        // attrValue部分
        List<ProductAttrValue> attrValueAddList = CollUtil.newArrayList();
        List<ProductAttrValue> attrValueUpdateList = CollUtil.newArrayList();
        attrValueAddRequestList.forEach(e -> {
            ProductAttrValue attrValue = new ProductAttrValue();
            BeanUtils.copyProperties(e, attrValue);
            attrValue.setSku(getSku(e.getAttrValue()));
            attrValue.setImage(systemAttachmentService.clearPrefix(e.getImage(), cdnUrl));
            attrValue.setVersion(0);
            attrValue.setType(product.getType());
            attrValue.setMarketingType(product.getMarketingType());
            attrValue.setVipPrice(BigDecimal.ZERO);
            if (ObjectUtil.isNull(attrValue.getId()) || attrValue.getId().equals(0)) {
                attrValue.setId(null);
                attrValue.setProductId(product.getId());
                attrValue.setQuota(0);
                attrValue.setQuotaShow(0);
                attrValueAddList.add(attrValue);
            } else {
                attrValue.setProductId(product.getId());
                attrValue.setIsDel(false);
                attrValueUpdateList.add(attrValue);
            }
        });

        product.setStock(attrValueAddRequestList.stream().mapToInt(IntegralProductAttrValueAddRequest::getStock).sum());

        // 处理富文本
        ProductDescription spd = new ProductDescription();
        spd.setDescription(StrUtil.isNotBlank(request.getContent()) ? systemAttachmentService.clearPrefix(request.getContent(), cdnUrl) : "");
        spd.setType(product.getType());
        spd.setMarketingType(product.getMarketingType());
        spd.setProductId(product.getId());

        return transactionTemplate.execute(e -> {
            dao.updateById(product);

            // 先删除原用attr+value
            productAttributeService.deleteByProductUpdate(product.getId());
            productAttributeOptionService.deleteByProductUpdate(product.getId());
            productAttrValueService.deleteByProductIdAndType(product.getId(), product.getType(), product.getMarketingType());

            productAttributeService.saveBatch(attrList);
            attrList.forEach(attr -> {
                List<ProductAttributeOption> optionList = optionMap.get(attr.getAttributeName());
                optionList.forEach(option -> {
                    option.setAttributeId(attr.getId());
                });
                productAttributeOptionService.saveBatch(optionList);
            });

            if (CollUtil.isNotEmpty(attrValueAddList)) {
                productAttrValueService.saveBatch(attrValueAddList);
            }
            if (CollUtil.isNotEmpty(attrValueUpdateList)) {
                productAttrValueService.saveOrUpdateBatch(attrValueUpdateList);
            }

            productDescriptionService.deleteByProductId(product.getId(), product.getType(), product.getMarketingType());
            productDescriptionService.save(spd);
            return Boolean.TRUE;
        });
    }

    /**
     * 积分商品详情
     */
    @Override
    public IntegralProductDetailResponse getIntegralProductDetail(Integer id) {
        Product product = dao.selectById(id);
        if (ObjectUtil.isNull(product) || product.getMerId() > 0) {
            throw new CrmebException(ProductResultCode.PRODUCT_NOT_EXIST);
        }

        IntegralProductDetailResponse response = new IntegralProductDetailResponse();
        BeanUtils.copyProperties(product, response);

        List<ProductAttribute> attrList = productAttributeService.findListByProductId(product.getId());
        attrList.forEach(attr -> {
            List<ProductAttributeOption> optionList = productAttributeOptionService.findListByAttrId(attr.getId());
            attr.setOptionList(optionList);
        });
        response.setAttrList(attrList);

        List<ProductAttrValue> attrValueList = productAttrValueService.getListByProductIdAndType(product.getId(), product.getType(), product.getMarketingType(), false);
        List<AttrValueResponse> valueResponseList = attrValueList.stream().map(e -> {
            AttrValueResponse valueResponse = new AttrValueResponse();
            BeanUtils.copyProperties(e, valueResponse);
            return valueResponse;
        }).collect(Collectors.toList());
        response.setAttrValueList(valueResponseList);

        ProductDescription sd = productDescriptionService.getByProductIdAndType(product.getId(), product.getType(), product.getMarketingType());
        response.setContent(sd.getDescription());

        return response;
    }

    /**
     * 删除积分商品
     */
    @Override
    public Boolean deleteIntegralProduct(Integer id) {
        Product tempProduct = getById(id);
        if (ObjectUtil.isNull(tempProduct) || !tempProduct.getMerId().equals(0)) {
            throw new CrmebException(ProductResultCode.PRODUCT_NOT_EXIST);
        }
        if (tempProduct.getIsDel()) {
            throw new CrmebException(ProductResultCode.PRODUCT_DELETE);
        }
        tempProduct.setIsDel(true);
        return updateById(tempProduct);
    }

    /**
     * 获取积分商品表头数量
     */
    @Override
    public MyRecord getIntegralProductTabsHeader(IntegralProductTabsHeaderRequest request) {
        String keywords = "";
        if (StrUtil.isNotBlank(request.getKeywords())) {
            keywords = URLUtil.decode(request.getKeywords());
        }
        DateLimitUtilVo dateLimit = null;
        if (StrUtil.isNotBlank(request.getDateLimit())) {
            dateLimit = CrmebDateUtil.getDateLimit(request.getDateLimit());
            //判断时间
            int compareDateResult = CrmebDateUtil.compareDate(dateLimit.getEndTime(), dateLimit.getStartTime(), DateConstants.DATE_FORMAT);
            if (compareDateResult == -1) {
                throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "开始时间不能大于结束时间！");
            }
        }
        MyRecord record = new MyRecord();
        record.set("upNum", getIntegralProductShowNum(true, keywords, dateLimit));
        record.set("downNum", getIntegralProductShowNum(false, keywords, dateLimit));
        return record;
    }

    /**
     * 积分商品分页列表（平台）
     */
    @Override
    public PageInfo<IntegralProductPageResponse> getIntegralProductPageByPlat(IntegralProductPageSearchRequest request) {
        LambdaQueryWrapper<Product> lqw = new LambdaQueryWrapper<>();
        lqw.eq(Product::getMerId, 0);
        lqw.eq(Product::getIsShow, request.getIsShow());
        lqw.eq(Product::getIsDel, 0);
        lqw.eq(Product::getType, ProductConstants.PRODUCT_TYPE_INTEGRAL);
        //关键字搜索
        if (StrUtil.isNotBlank(request.getKeywords())) {
            String keywords = URLUtil.decode(request.getKeywords());
            lqw.and(i -> i.like(Product::getName, keywords)
                    .or().apply(" find_in_set({0}, keyword)", keywords));
        }
        if (StrUtil.isNotBlank(request.getDateLimit())) {
            DateLimitUtilVo dateLimit = CrmebDateUtil.getDateLimit(request.getDateLimit());
            //判断时间
            int compareDateResult = CrmebDateUtil.compareDate(dateLimit.getEndTime(), dateLimit.getStartTime(), DateConstants.DATE_FORMAT);
            if (compareDateResult == -1) {
                throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "开始时间不能大于结束时间！");
            }
            lqw.between(Product::getCreateTime, dateLimit.getStartTime(), dateLimit.getEndTime());
        }
        lqw.orderByDesc(Product::getSort).orderByDesc(Product::getId);
        Page<Product> productPage = PageHelper.startPage(request.getPage(), request.getLimit());
        List<Product> productList = dao.selectList(lqw);
        if (CollUtil.isEmpty(productList)) {
            return CommonPage.copyPageInfo(productPage, CollUtil.newArrayList());
        }
        List<IntegralProductPageResponse> productResponses = new ArrayList<>();
        for (Product product : productList) {
            IntegralProductPageResponse productResponse = new IntegralProductPageResponse();
            BeanUtils.copyProperties(product, productResponse);
            // 收藏数
            productResponses.add(productResponse);
        }
        // 多条sql查询处理分页正确
        return CommonPage.copyPageInfo(productPage, productResponses);
    }

    private int getIntegralProductShowNum(Boolean isShow, String keywords, DateLimitUtilVo dateLimit) {
        LambdaQueryWrapper<Product> lqw = new LambdaQueryWrapper<>();
        lqw.eq(Product::getMerId, 0);
        lqw.eq(Product::getIsShow, isShow);
        lqw.eq(Product::getType, ProductConstants.PRODUCT_TYPE_INTEGRAL);
        if (StrUtil.isNotBlank(keywords)) {
            lqw.and(i -> i.like(Product::getName, keywords)
                    .or().apply(" find_in_set({0}, keyword)", keywords));
        }
        if (ObjectUtil.isNotNull(dateLimit)) {
            lqw.between(Product::getCreateTime, dateLimit.getStartTime(), dateLimit.getEndTime());
        }
        lqw.eq(Product::getIsDel, false);
        return dao.selectCount(lqw);
    }


    /**
     * 积分商品热门推荐分页列表
     */
    @Override
    public PageInfo<IntegralProductFrontResponse> findIntegralProductHotPage(PageParamRequest request) {
        LambdaQueryWrapper<Product> lqw = new LambdaQueryWrapper<>();
        lqw.eq(Product::getMerId, 0);
        lqw.eq(Product::getIsShow, 1);
        lqw.eq(Product::getIsDel, 0);
        lqw.eq(Product::getIsHot, 1);
        lqw.eq(Product::getType, ProductConstants.PRODUCT_TYPE_INTEGRAL);
        lqw.orderByDesc(Product::getSort).orderByDesc(Product::getId);
        Page<Product> page = PageHelper.startPage(request.getPage(), request.getLimit());
        List<Product> productList = dao.selectList(lqw);
        if (CollUtil.isEmpty(productList)) {
            return CommonPage.copyPageInfo(page, new ArrayList<>());
        }
        List<IntegralProductFrontResponse> responseList = productList.stream().map(product -> {
            IntegralProductFrontResponse response = new IntegralProductFrontResponse();
            BeanUtils.copyProperties(product, response);
            return response;
        }).collect(Collectors.toList());
        return CommonPage.copyPageInfo(page, responseList);
    }

    /**
     * 积分商品分页列表(积分区间)
     */
    @Override
    public PageInfo<IntegralProductFrontResponse> findIntegralIntervalProductPage(int startIntegral, int endIntegral, int page, int limit) {
        LambdaQueryWrapper<Product> lqw = new LambdaQueryWrapper<>();
        lqw.eq(Product::getMerId, 0);
        lqw.eq(Product::getIsShow, 1);
        lqw.eq(Product::getIsDel, 0);
        lqw.eq(Product::getType, ProductConstants.PRODUCT_TYPE_INTEGRAL);
        if (startIntegral >= 0 && endIntegral > 0) {
            lqw.ge(Product::getRedeemIntegral, startIntegral);
            lqw.le(Product::getRedeemIntegral, endIntegral);
        }
        lqw.orderByDesc(Product::getSort).orderByDesc(Product::getId);
        Page<Product> productPage = PageHelper.startPage(page, limit);
        List<Product> productList = dao.selectList(lqw);
        if (CollUtil.isEmpty(productList)) {
            return CommonPage.copyPageInfo(productPage, new ArrayList<>());
        }
        List<IntegralProductFrontResponse> responseList = productList.stream().map(product -> {
            IntegralProductFrontResponse response = new IntegralProductFrontResponse();
            BeanUtils.copyProperties(product, response);
            return response;
        }).collect(Collectors.toList());
        return CommonPage.copyPageInfo(productPage, responseList);
    }

    /**
     * 变更积分商品上下架
     */
    @Override
    public Boolean updateShowIntegralProduct(Integer id) {
        Product product = getByIdException(id);
        if (product.getMerId() > 0 || !product.getType().equals(ProductConstants.PRODUCT_TYPE_INTEGRAL)) {
            throw new CrmebException(ProductResultCode.PRODUCT_NOT_EXIST);
        }
        Product tempProduct = new Product();
        tempProduct.setId(product.getId());
        tempProduct.setIsShow(!product.getIsShow());
        return updateById(tempProduct);
    }
}

