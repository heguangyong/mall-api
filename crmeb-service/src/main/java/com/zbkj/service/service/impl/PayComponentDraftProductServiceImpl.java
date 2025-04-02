package com.zbkj.service.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.URLUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zbkj.common.constants.Constants;
import com.zbkj.common.constants.DateConstants;
import com.zbkj.common.enums.PayComponentEditStatusEnum;
import com.zbkj.common.enums.PayComponentPlatformEditStatusEnum;
import com.zbkj.common.enums.PayComponentPlatformStatusEnum;
import com.zbkj.common.enums.PayComponentStatusEnum;
import com.zbkj.common.exception.CrmebException;
import com.zbkj.common.model.admin.SystemAdmin;
import com.zbkj.common.model.wechat.video.*;
import com.zbkj.common.page.CommonPage;
import com.zbkj.common.request.PageParamRequest;
import com.zbkj.common.request.ProductAttrValueAddRequest;
import com.zbkj.common.request.wxvedio.product.ComponentProductSearchRequest;
import com.zbkj.common.request.wxvedio.product.PayComponentDraftProductMerchantOperationReviewStatus;
import com.zbkj.common.request.wxvedio.product.PayComponentDraftProductPlatformOperationReviewStatus;
import com.zbkj.common.request.wxvedio.product.PayComponentProductAddRequest;
import com.zbkj.common.utils.CrmebDateUtil;
import com.zbkj.common.utils.CrmebUtil;
import com.zbkj.common.utils.SecurityUtil;
import com.zbkj.common.vo.wxvedioshop.*;
import com.zbkj.service.dao.PayComponentDraftProductDao;
import com.zbkj.service.service.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 *
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
public class PayComponentDraftProductServiceImpl extends ServiceImpl<PayComponentDraftProductDao, PayComponentDraftProduct> implements PayComponentDraftProductService {

    private static final Logger logger = LoggerFactory.getLogger(PayComponentDraftProductServiceImpl.class);

    @Resource
    private PayComponentDraftProductDao dao;

    @Autowired
    private TransactionTemplate transactionTemplate;
    @Autowired
    private PayComponentProductService productService;

    @Autowired
    private WechatVideoSpuService wechatVideoSpuService;
    @Autowired
    private PayComponentProductSkuService skuService;
    @Autowired
    private SystemAttachmentService systemAttachmentService;
    @Autowired
    private PayComponentCatService catService;
    @Autowired
    private MerchantService merchantService;
    @Autowired
    private WechatVideoBeforeService wechatVideoBeforeService;

    /**
     * 添加草稿商品，仅仅新增草稿商品 不会自动提审 提审需要调用提审状态API
     * @param addRequest 商品请求参数
     * @return Boolean
     */
    @Override
    public Boolean add(PayComponentProductAddRequest addRequest) {
        // 创建草稿商品之前确认是否有同名商品
        List<PayComponentDraftProduct> draftProductByOperation = getDraftProductByOperation(null, addRequest.getTitle());
        if(ObjectUtil.isNull(draftProductByOperation) || draftProductByOperation.size() > 0){
            throw new CrmebException("当前草稿商品的名称已存在");
        }

        SystemAdmin currentMerchant = SecurityUtil.getLoginUserVo().getUser();
        // 第一步，正式商品库创建商品，sku，先设置为删除状态 可以理解为对应线上商品的数据copy
        PayComponentProduct product = new PayComponentProduct();
        BeanUtils.copyProperties(addRequest, product);
        if (ObjectUtil.isNull(addRequest.getBrandId()) || addRequest.getBrandId() == 0) {
            // 品牌ID，商家需要申请品牌并通过获取品牌接口brand/get获取，如果是无品牌请填2100000000 https://developers.weixin.qq.com/miniprogram/dev/platform-capabilities/business-capabilities/ministore/minishopopencomponent/API/spu/up_spu.html#%E8%AF%B7%E6%B1%82%E5%8F%82%E6%95%B0%E8%AF%B4%E6%98%8E
            product.setBrandId(2100000000);
        }

        product.setHeadImg(addRequest.getHeadImg());
        if (StrUtil.isNotBlank(product.getQualificationPics()) && !product.getQualificationPics().equals("[]")) {
            product.setQualificationPics(systemAttachmentService.clearPrefix(product.getQualificationPics()));
        }
        product.setIsDel(true);// 以免待审核商品出现在过审商品列表

//        PayComponentProductInfo productInfo = new PayComponentProductInfo();
//        if (StrUtil.isNotBlank(addRequest.getDescInfo())) {
//            productInfo.setDesc(systemAttachmentService.clearPrefix(addRequest.getDescInfo()));
//        }
        // 规格部分
        List<PayComponentProductSku> skuList = CollUtil.newArrayList();
        if (!addRequest.getSpecType()) { // 单规格
            PayComponentProductSkuAttr skuAttr = new PayComponentProductSkuAttr();
            skuAttr.setAttrKey("请选择规格");
            skuAttr.setAttrValue("默认");
            skuAttr.setIsDel(false);
            List<PayComponentProductSkuAttr> attrList = CollUtil.newArrayList();
            attrList.add(skuAttr);
            ProductAttrValueAddRequest attrValueRequest = addRequest.getAttrValue().get(0);
            PayComponentProductSku sku = new PayComponentProductSku();
            sku.setThumbImg(attrValueRequest.getImage());
            sku.setSalePrice(attrValueRequest.getPrice().multiply(new BigDecimal("100")).longValue());
            sku.setMarketPrice(attrValueRequest.getOtPrice().multiply(new BigDecimal("100")).longValue());
            sku.setStockNum(attrValueRequest.getStock());
            sku.setAttrList(attrList);
            sku.setIsDel(true);
            sku.setSku("默认");
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

                List<String> skuAttrList = new ArrayList<>();
                String attrValueJson = attrValueRequest.getAttrValue();
                JSONObject jsonObject = JSONObject.parseObject(attrValueJson);
                List<PayComponentProductSkuAttr> attrList = CollUtil.newArrayList();
                for (Map.Entry<String, Object> vo : jsonObject.entrySet()){
                    skuAttrList.add(vo.getValue().toString());
                    PayComponentProductSkuAttr skuAttr = new PayComponentProductSkuAttr();
                    skuAttr.setAttrKey(vo.getKey());
                    skuAttr.setAttrValue(vo.getValue().toString());
                    skuAttr.setIsDel(false);
                    attrList.add(skuAttr);
                }
                sku.setSku(String.join(",", skuAttrList));
                sku.setAttrList(attrList);
                skuList.add(sku);
            });
        }
        Boolean execute = transactionTemplate.execute(e -> {
            product.setMerId(currentMerchant.getMerId());
            product.setPlatformEditStatus(PayComponentPlatformEditStatusEnum.INIT.getCode());
            productService.save(product);
            product.setPath(StrUtil.format("/pages/goods/goods_details/index?id={}&type={}", product.getId(), "video"));
            productService.updateById(product);
            for (PayComponentProductSku sku : skuList) {
                sku.setProductId(product.getId());
//                skuService.save(sku);
            }
            skuService.saveOrUpdateBatch(skuList);
            return Boolean.TRUE;
        });
        if (!execute) {
            throw new CrmebException("本地保存商品时错误");
        }
        PayComponentDraftProduct draftProduct = new PayComponentDraftProduct();
        BeanUtils.copyProperties(product, draftProduct);
        draftProduct.setId(null);
        draftProduct.setProductId(product.getId());
        draftProduct.setComponentProductId(null);
        draftProduct.setCreateTime(CrmebDateUtil.nowDate(DateConstants.DATE_FORMAT));
        draftProduct.setUpdateTime(CrmebDateUtil.nowDate(DateConstants.DATE_FORMAT));
        draftProduct.setStatus(PayComponentStatusEnum.STATUS_INIT.getCode());
        // 微信审核状态 未审核
        draftProduct.setEditStatus(PayComponentEditStatusEnum.BEFORE_REVIEW.getCode());
        // 系统自定义状态 未提审 也就是可以多次编译的草稿
        draftProduct.setPlatformStatus(PayComponentPlatformStatusEnum.INIT.getCode());
        draftProduct.setMerId(currentMerchant.getMerId());

        draftProduct.setSku(JSONArray.toJSONString(skuList));
        draftProduct.setAttr(JSONArray.toJSONString(addRequest.getAttr()));
        draftProduct.setAttrValue(JSONArray.toJSONString(addRequest.getAttrValue()));
        draftProduct.setIsDel(false);
        draftProduct.setAddTime(DateUtil.date());
        draftProduct.setSales(0);
        draftProduct.setSpecType(addRequest.getSpecType());
        draftProduct.setDescInfo(systemAttachmentService.clearPrefix(addRequest.getDescInfo()));
        int stock = skuList.stream().mapToInt(PayComponentProductSku::getStockNum).sum();
        draftProduct.setStock(stock);
        boolean save = save(draftProduct);
        if (!save) {
            throw new CrmebException("本地保存草稿商品时错误");
        }
        return save;
    }

    /**
     * 编辑草稿商品
     *
     * @param addRequest 编辑草稿参数
     * @return 编辑结果
     */
    @Override
    public Boolean edit(PayComponentProductAddRequest addRequest) {
        if(ObjectUtil.isEmpty(addRequest.getId())){
            throw new CrmebException("id不能为空");
        }
        // 第一步，正式商品库创建商品，sku，先设置为删除状态 可以理解为对应线上商品的数据copy
        PayComponentProduct product = productService.getById(addRequest.getProductId());
        BeanUtils.copyProperties(addRequest, product);
        if (ObjectUtil.isNull(addRequest.getBrandId()) || addRequest.getBrandId() == 0) {
            product.setBrandId(2100000000);
        }

        product.setHeadImg(addRequest.getHeadImg());
        if (StrUtil.isNotBlank(product.getQualificationPics()) && !product.getQualificationPics().equals("[]")) {
            product.setQualificationPics(systemAttachmentService.clearPrefix(product.getQualificationPics()));
        }
        product.setIsDel(true);// 以免待审核商品出现在过审商品列表

//        PayComponentProductInfo productInfo = new PayComponentProductInfo();
//        if (StrUtil.isNotBlank(addRequest.getDescInfo())) {
//            productInfo.setDesc(systemAttachmentService.clearPrefix(addRequest.getDescInfo()));
//        }
        // 规格部分
        List<PayComponentProductSku> skuList = CollUtil.newArrayList();
        if (!addRequest.getSpecType()) { // 单规格
            PayComponentProductSkuAttr skuAttr = new PayComponentProductSkuAttr();
            skuAttr.setAttrKey("请选择规格");
            skuAttr.setAttrValue("默认");
            skuAttr.setIsDel(false);
            List<PayComponentProductSkuAttr> attrList = CollUtil.newArrayList();
            attrList.add(skuAttr);
            ProductAttrValueAddRequest attrValueRequest = addRequest.getAttrValue().get(0);
            PayComponentProductSku sku = new PayComponentProductSku();
            sku.setThumbImg(attrValueRequest.getImage());
            sku.setSalePrice(attrValueRequest.getPrice().multiply(new BigDecimal("100")).longValue());
            sku.setMarketPrice(attrValueRequest.getOtPrice().multiply(new BigDecimal("100")).longValue());
            sku.setStockNum(attrValueRequest.getStock());
            sku.setAttrList(attrList);
            sku.setIsDel(true);
            sku.setSku("默认");
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

                List<String> skuAttrList = new ArrayList<>();
                String attrValueJson = attrValueRequest.getAttrValue();
                JSONObject jsonObject = JSONObject.parseObject(attrValueJson);
                List<PayComponentProductSkuAttr> attrList = CollUtil.newArrayList();
                for (Map.Entry<String, Object> vo : jsonObject.entrySet()){
                    skuAttrList.add(vo.getValue().toString());
                    PayComponentProductSkuAttr skuAttr = new PayComponentProductSkuAttr();
                    skuAttr.setAttrKey(vo.getKey());
                    skuAttr.setAttrValue(vo.getValue().toString());
                    skuAttr.setIsDel(false);
                    attrList.add(skuAttr);
                }
                sku.setSku(String.join(",", skuAttrList));
                sku.setAttrList(attrList);
                skuList.add(sku);
            });
        }
        Boolean execute = transactionTemplate.execute(e -> {
//            productService.save(product);
//            product.setPath(StrUtil.format("/pages/goods_details/index?id={}&type={}", product.getId(), "video"));
            productService.updateById(product);
            for (PayComponentProductSku sku : skuList) {
                sku.setProductId(product.getId());
//                skuService.save(sku);
            }
            skuService.saveOrUpdateBatch(skuList);
            return Boolean.TRUE;
        });
        if (!execute) {
            throw new CrmebException("本地保存商品时错误");
        }
        PayComponentDraftProduct draftProduct = new PayComponentDraftProduct();
        BeanUtils.copyProperties(product, draftProduct);
        draftProduct.setId(addRequest.getId());
        draftProduct.setProductId(product.getId());
        draftProduct.setComponentProductId(product.getComponentProductId());
        draftProduct.setCreateTime(CrmebDateUtil.nowDate(DateConstants.DATE_FORMAT));
        draftProduct.setUpdateTime(CrmebDateUtil.nowDate(DateConstants.DATE_FORMAT));
        draftProduct.setStatus(PayComponentStatusEnum.STATUS_INIT.getCode());
        // 微信审核状态 未审核
        draftProduct.setEditStatus(PayComponentEditStatusEnum.BEFORE_REVIEW.getCode());
        // 系统自定义状态 未提审 也就是可以多次编译的草稿
        draftProduct.setPlatformStatus(PayComponentPlatformEditStatusEnum.INIT.getCode());
        draftProduct.setSku(JSONArray.toJSONString(skuList));
        draftProduct.setAttr(JSONArray.toJSONString(addRequest.getAttr()));
        draftProduct.setAttrValue(JSONArray.toJSONString(addRequest.getAttrValue()));
        draftProduct.setIsDel(false);
        draftProduct.setAddTime(DateUtil.date());
        draftProduct.setSales(0);
        draftProduct.setSpecType(addRequest.getSpecType());
        draftProduct.setDescInfo(systemAttachmentService.clearPrefix(addRequest.getDescInfo()));
        int stock = skuList.stream().mapToInt(PayComponentProductSku::getStockNum).sum();
        draftProduct.setStock(stock);

        boolean draftUpdate = updateById(draftProduct);
        if (!draftUpdate) {
            throw new CrmebException("本地保存草稿商品时错误");
        }
        return draftUpdate;
    }

    /**
     * 根据商品id获取草稿商品
     * @param proId 商品id
     * @return PayComponentDraftProduct
     */
    @Override
    public PayComponentDraftProduct getByProId(Integer proId) {
        LambdaQueryWrapper<PayComponentDraftProduct> lqw = Wrappers.lambdaQuery();
        lqw.eq(PayComponentDraftProduct::getProductId, proId);
        return dao.selectOne(lqw);
    }

    /**
     * 通过商品id删除草稿 不可恢复
     * @param proId 商品id
     */
    @Override
    public Boolean deleteByProId(Integer proId) {
        LambdaUpdateWrapper<PayComponentDraftProduct> luw = Wrappers.lambdaUpdate();
        luw.set(PayComponentDraftProduct::getIsDel, true);
        luw.eq(PayComponentDraftProduct::getProductId, proId);
        return update(luw);
    }

    /**
     * 当前商户端获取 微信审核成功之前的数据列表
     * @param request 搜索参数
     * @param pageParamRequest 分页参数
     * @return PageInfo
     */
    @Override
    public PageInfo<PayComponentDraftProduct> getCurrentMerchantAdminListBeforeWeChatReview(ComponentProductSearchRequest request, PageParamRequest pageParamRequest) {
        SystemAdmin currentMerchantAdmin = SecurityUtil.getLoginUserVo().getUser();
        Page<PayComponentDraftProduct> page = PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());

        LambdaQueryWrapper<PayComponentDraftProduct> lqw = Wrappers.lambdaQuery();
        lqw.eq(PayComponentDraftProduct::getIsDel, false);
        if (ObjectUtil.isNotNull(request.getProId())) {
            lqw.eq(PayComponentDraftProduct::getProductId, request.getProId());
        }
        if (StrUtil.isNotBlank(request.getSearch())) {
            lqw.like(PayComponentDraftProduct::getTitle, URLUtil.decode(request.getSearch()));
        }
        lqw.eq(PayComponentDraftProduct::getIsDel, false);
        lqw.eq(PayComponentDraftProduct::getMerId, currentMerchantAdmin.getMerId());
        // 查询微信终审之前的所有状态
        lqw.lt(PayComponentDraftProduct::getEditStatus, PayComponentEditStatusEnum.REVIEW_SUCCESS.getCode());
        lqw.orderByDesc(PayComponentDraftProduct::getId);
        List<PayComponentDraftProduct> productList = dao.selectList(lqw);
        productList.forEach(e -> {
            PayComponentCat componentCat = catService.getByThirdCatId(e.getThirdCatId());
            if (ObjectUtil.isNull(componentCat)) {
                e.setThirdCatName("");
            } else {
                e.setThirdCatName(Optional.ofNullable(componentCat.getThirdCatName()).orElse(""));
            }
        });
        return CommonPage.copyPageInfo(page, productList);
    }

    /**
     * 平台端获取 商户提审 到 微信审核成功之前的数据列表
     * @param request 搜索参数
     * @param pageParamRequest 分页参数
     * @return PageInfo
     */
    @Override
    public PageInfo<PayComponentDraftProduct> getPlatformAdminListAfterMerchantReviewBeforeWeChatReview(ComponentProductSearchRequest request, PageParamRequest pageParamRequest) {
        Page<PayComponentDraftProduct> page = PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());

        LambdaQueryWrapper<PayComponentDraftProduct> lqw = Wrappers.lambdaQuery();
        lqw.eq(PayComponentDraftProduct::getIsDel, false);
        if (ObjectUtil.isNotNull(request.getProId())) {
            lqw.eq(PayComponentDraftProduct::getProductId, request.getProId());
        }
        if (StrUtil.isNotBlank(request.getSearch())) {
            lqw.like(PayComponentDraftProduct::getTitle, URLUtil.decode(request.getSearch()));
        }
        lqw.eq(PayComponentDraftProduct::getIsDel, false);
        // 平台审核状态提审的状态
        lqw.eq(PayComponentDraftProduct::getPlatformEditStatus, PayComponentPlatformEditStatusEnum.PLATFORM_REVIEW_ING.getCode());
        // 查询微信终审之前的状态
        lqw.lt(PayComponentDraftProduct::getEditStatus, PayComponentEditStatusEnum.REVIEW_SUCCESS.getCode());
        lqw.orderByDesc(PayComponentDraftProduct::getId);
        List<PayComponentDraftProduct> productList = dao.selectList(lqw);
        productList.forEach(e -> {
            PayComponentCat componentCat = catService.getByThirdCatId(e.getThirdCatId());
            if (ObjectUtil.isNull(componentCat)) {
                e.setThirdCatName("");
            } else {
                e.setThirdCatName(Optional.ofNullable(componentCat.getThirdCatName()).orElse(""));
            }
        });
        return CommonPage.copyPageInfo(page, productList);
    }

    /**
     * 商品详情
     * @param id 商品id
     * @return PayComponentDraftProduct
     */
    @Override
    public PayComponentDraftProduct getInfo(Integer id) {
        PayComponentDraftProduct draftProduct = getById(id);
        if (ObjectUtil.isNull(draftProduct) || draftProduct.getIsDel()) {
            throw new CrmebException("商品不存在");
        }
//        List<JSONObject> jsonAttrList = JSONArray.parseArray(draftProduct.getAttrValue(), JSONObject.class);
//        jsonAttrList.forEach(e -> {
//            String sku = e.getString("suk");
//            String[] skus = sku.split(",");
//            for (int i = 0; i < skus.length; i++) {
//                e.put("value" + i, skus[i]);
//            }
//        });
//        draftProduct.setAttrValue(JSONArray.toJSONString(jsonAttrList));

        // 查询对应类目
        PayComponentCat componentCat = catService.getByThirdCatId(draftProduct.getThirdCatId());
        draftProduct.setCatInfo(componentCat);
        return draftProduct;
    }

    /**
     * 商家角色 操作草稿商品状态
     *
     * @param reviewStatus 操作状态Request
     * @return 结果
     */
    @Override
    public Boolean OperationPlatformReviewStatusByMerchant(PayComponentDraftProductMerchantOperationReviewStatus reviewStatus) {
        // 参数有效性已经在request 中验证
        LambdaUpdateWrapper<PayComponentDraftProduct> draftProductLambdaUpdateWrapper = Wrappers.lambdaUpdate();
        draftProductLambdaUpdateWrapper.eq(PayComponentDraftProduct::getId, reviewStatus.getDraftProductId());
        draftProductLambdaUpdateWrapper.set(PayComponentDraftProduct::getPlatformEditStatus, reviewStatus.getPlatformEditStatus());
        return update(draftProductLambdaUpdateWrapper);
    }

    /**
     * 平台角色草稿商品操作状态
     *
     * @param reviewStatus 操作状态Request
     * @return 结果
     */
    @Override
    public Boolean OperationPlatformReviewStatusByPlatform(PayComponentDraftProductPlatformOperationReviewStatus reviewStatus) {
        // 判断平台初审时审核失败的参数验证
        if(reviewStatus.getPlatformEditStatus().equals(PayComponentPlatformEditStatusEnum.PLATFORM_REVIEW_FAILED.getCode())
                && ObjectUtil.isEmpty(reviewStatus.getPlatformStatusReason())){
            logger.error("平台审核视频号草稿商品拒绝时 必须填写拒绝原因{}", JSON.toJSONString(reviewStatus));
            throw new CrmebException("平台审核视频号草稿商品拒绝时 必须填写拒绝原因");
        }
        // 参数有效性已经在request 中验证 更新商品和草稿
        LambdaUpdateWrapper<PayComponentDraftProduct> draftProductLambdaUpdateWrapper = Wrappers.lambdaUpdate();
        draftProductLambdaUpdateWrapper.eq(PayComponentDraftProduct::getId, reviewStatus.getDraftProductId());
        draftProductLambdaUpdateWrapper.set(PayComponentDraftProduct::getPlatformEditStatus, reviewStatus.getPlatformEditStatus());
        if(ObjectUtil.isNotEmpty(reviewStatus.getPlatformStatusReason())){
            draftProductLambdaUpdateWrapper.set(PayComponentDraftProduct::getPlatformStatusReason, reviewStatus.getPlatformStatusReason());
        }

        // 平台审核拒绝直接返回
        if(reviewStatus.getPlatformEditStatus().equals(PayComponentPlatformEditStatusEnum.PLATFORM_REVIEW_FAILED.getCode())){
            LambdaUpdateWrapper<PayComponentProduct> productLambdaUpdateWrapper = Wrappers.lambdaUpdate();
            productLambdaUpdateWrapper.eq(PayComponentProduct::getId, reviewStatus.getDraftProductId());
            productLambdaUpdateWrapper.set(PayComponentProduct::getPlatformEditStatus, reviewStatus.getPlatformEditStatus());
            update(draftProductLambdaUpdateWrapper);
            productService.update(productLambdaUpdateWrapper);
            return true;
        }
        // 平台审核通过直接提交给微信审核
        PayComponentDraftProduct currentDraftProduct = getById(reviewStatus.getDraftProductId());
        PayComponentProduct currentPayComponentProduct = productService.getById(currentDraftProduct.getProductId());

        // 规格部分
        List<PayComponentProductSku> skuList = CollUtil.newArrayList();
        if (!currentDraftProduct.getSpecType()) { // 单规格
            PayComponentProductSkuAttr skuAttr = new PayComponentProductSkuAttr();
            skuAttr.setAttrKey("请选择规格");
            skuAttr.setAttrValue("默认");
            skuAttr.setIsDel(false);
            List<PayComponentProductSkuAttr> attrList = CollUtil.newArrayList();
            attrList.add(skuAttr);
            ProductAttrValueAddRequest attrValueRequest =
                    JSONObject.parseObject(JSONArray.parseArray(currentDraftProduct.getAttrValue()).get(0).toString(),ProductAttrValueAddRequest.class);
            PayComponentProductSku sku = new PayComponentProductSku();
            sku.setThumbImg(attrValueRequest.getImage());
            sku.setSalePrice(attrValueRequest.getPrice().multiply(new BigDecimal("100")).longValue());
            sku.setMarketPrice(attrValueRequest.getOtPrice().multiply(new BigDecimal("100")).longValue());
            sku.setStockNum(attrValueRequest.getStock());
            sku.setAttrList(attrList);
            sku.setIsDel(true);
            sku.setId(attrValueRequest.getId());
            sku.setSku("默认");
            skuList.add(sku);
        } else { // 多规格
            List<ProductAttrValueAddRequest> valueRequestList =
                    JSONArray.parseArray(currentDraftProduct.getAttrValue(), ProductAttrValueAddRequest.class);
            valueRequestList.forEach(attrValueRequest -> {
                PayComponentProductSku sku = new PayComponentProductSku();
                sku.setThumbImg(attrValueRequest.getImage());
                sku.setSalePrice(attrValueRequest.getPrice().multiply(new BigDecimal("100")).longValue());
                sku.setMarketPrice(attrValueRequest.getOtPrice().multiply(new BigDecimal("100")).longValue());
                sku.setStockNum(attrValueRequest.getStock());
                sku.setIsDel(true);
                sku.setId(attrValueRequest.getId());
                List<String> skuAttrList = new ArrayList<>();
                String attrValueJson = attrValueRequest.getAttrValue();
                JSONObject jsonObject = JSONObject.parseObject(attrValueJson);
                List<PayComponentProductSkuAttr> attrList = CollUtil.newArrayList();
                for (Map.Entry<String, Object> vo : jsonObject.entrySet()){
                    skuAttrList.add(vo.getValue().toString());
                    PayComponentProductSkuAttr skuAttr = new PayComponentProductSkuAttr();
                    skuAttr.setAttrKey(vo.getKey());
                    skuAttr.setAttrValue(vo.getValue().toString());
                    skuAttr.setIsDel(false);
                    attrList.add(skuAttr);
                }
                sku.setSku(String.join(",", skuAttrList));
                sku.setAttrList(attrList);
                skuList.add(sku);
            });
        }


//        if(update){
        PayComponentProductInfo productInfo = new PayComponentProductInfo();
        if (StrUtil.isNotBlank(currentDraftProduct.getDescInfo())) {
            productInfo.setDesc(systemAttachmentService.clearPrefix(currentDraftProduct.getDescInfo()));
        }
        ShopSpuAddVo shopSpuAddVo = assembleShopSpuVo(currentPayComponentProduct, skuList, productInfo);

        LambdaUpdateWrapper<PayComponentProduct> productLambdaUpdateWrapper = Wrappers.lambdaUpdate();
        productLambdaUpdateWrapper.eq(PayComponentProduct::getId, reviewStatus.getDraftProductId());
        productLambdaUpdateWrapper.set(PayComponentProduct::getPlatformEditStatus, reviewStatus.getPlatformEditStatus());
        ShopSpuAddResponseVo spuAddResponseVo = null;
        try {
            spuAddResponseVo = wechatVideoSpuService.shopSpuAdd(shopSpuAddVo);
            draftProductLambdaUpdateWrapper.set(PayComponentDraftProduct::getEditStatus, PayComponentEditStatusEnum.REVIEW_ING.getCode());
            update(draftProductLambdaUpdateWrapper);
            productService.update(productLambdaUpdateWrapper);
        } catch (Exception e) {// 抛出异常，说明微信调用错误，删除之前保存的商品、sku等信息
            draftProductLambdaUpdateWrapper.set(PayComponentDraftProduct::getPlatformStatusReason, e.getMessage());
            draftProductLambdaUpdateWrapper.set(PayComponentDraftProduct::getPlatformEditStatus, PayComponentPlatformEditStatusEnum.PLATFORM_REVIEW_FAILED.getCode());
            update(draftProductLambdaUpdateWrapper);
            productLambdaUpdateWrapper.set(PayComponentProduct::getPlatformEditStatus, PayComponentPlatformEditStatusEnum.PLATFORM_REVIEW_FAILED.getCode());
            productService.update(productLambdaUpdateWrapper);
        }
        return true;
    }

    /**
     * 根据固定属性查询草稿商品
     *
     * @param id   id
     * @param title 草稿商品名称
     * @return 查询结果
     */
    @Override
    public List<PayComponentDraftProduct> getDraftProductByOperation(Integer id, String title) {
        LambdaQueryWrapper<PayComponentDraftProduct> lambdaQueryWrapper = Wrappers.lambdaQuery();
        if(ObjectUtil.isNotEmpty(id)){
            lambdaQueryWrapper.eq(PayComponentDraftProduct::getId, id);
        }
        if(ObjectUtil.isNotEmpty(title)){
            lambdaQueryWrapper.eq(PayComponentDraftProduct::getTitle, title);
        }
        return dao.selectList(lambdaQueryWrapper);
    }

    /**
     * 组装spu添加对象
     * @return ShopSpuAddVo
     */
    private ShopSpuAddVo assembleShopSpuVo(PayComponentProduct product, List<PayComponentProductSku> skuList, PayComponentProductInfo productInfo) {
        ShopSpuAddVo shopSpuAddVo = new ShopSpuAddVo();
        shopSpuAddVo.setOutProductId(product.getId().toString());
        shopSpuAddVo.setTitle(product.getTitle());
        shopSpuAddVo.setPath(product.getPath());
        List<String> stringList = JSONArray.parseArray(product.getHeadImg(), String.class);
        shopSpuAddVo.setHeadImg(stringList);
        if (StrUtil.isNotBlank(product.getQualificationPics()) && !product.getQualificationPics().equals("[]")) {
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
}

