package com.zbkj.service.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.parser.Feature;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zbkj.common.constants.ProductConstants;
import com.zbkj.common.constants.WeChatConstants;
import com.zbkj.common.enums.*;
import com.zbkj.common.exception.CrmebException;
import com.zbkj.common.model.product.ProductAttr;
import com.zbkj.common.model.product.ProductAttrValue;
import com.zbkj.common.model.wechat.WechatCallback;
import com.zbkj.common.model.wechat.video.*;
import com.zbkj.common.request.ProductAttrValueRequest;
import com.zbkj.common.vo.MyRecord;
import com.zbkj.common.vo.wxvedioshop.ShopSpuAuditVo;
import com.zbkj.common.vo.wxvedioshop.ShopSpuCommonVo;
import com.zbkj.common.vo.wxvedioshop.ShopSpuVo;
import com.zbkj.service.dao.WechatCallbackDao;
import com.zbkj.service.service.*;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.Resource;
import java.util.*;

/**
 * 微信小程序回调Service
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
public class WechatCallbackServiceImpl extends ServiceImpl<WechatCallbackDao, WechatCallback> implements WechatCallbackService {

    private static final Logger logger = LoggerFactory.getLogger(WechatCallbackServiceImpl.class);

    @Resource
    private WechatCallbackDao dao;

    @Autowired
    private PayComponentProductService componentProductService;
    @Autowired
    private PayComponentDraftProductService componentDraftProductService;
    @Autowired
    private WechatVideoSpuService wechatVideoSpuService;
    @Autowired
    private PayComponentProductAuditInfoService componentProductAuditInfoService;
    @Autowired
    private SystemAttachmentService systemAttachmentService;
    @Autowired
    private TransactionTemplate transactionTemplate;
    @Autowired
    private PayComponentProductInfoService componentProductInfoService;
    @Autowired
    private PayComponentProductSkuService componentProductSkuService;
    @Autowired
    private PayComponentProductSkuAttrService componentProductSkuAttrService;
    @Autowired
    private ProductAttrService productAttrService;
    @Autowired
    private ProductAttrValueService productAttrValueService;
    @Autowired
    private PayComponentShopBrandService componentShopBrandService;

    @Autowired
    private PayComponentOrderService componentOrderService;

    @Autowired
    private PayComponentCatService payComponentCatService;

    @Autowired
    private OrderService orderService;

    /**
     * 微信回调
     * @param request request
     * @return String
     */
    @Override
    public String callback(String request) {
        JSONObject jsonObject = JSONObject.parseObject(request);
        WechatCallback wechatCallback = new WechatCallback();
        wechatCallback.setToUserName(jsonObject.getString("ToUserName"));
        wechatCallback.setFromUserName(jsonObject.getString("FromUserName"));
        wechatCallback.setCreateTime(jsonObject.getLong("CreateTime"));
        wechatCallback.setMsgType(jsonObject.getString("MsgType"));
        wechatCallback.setEvent(jsonObject.getString("Event"));
        wechatCallback.setAddTime(DateUtil.date());
        wechatCallback.setContent(request);
        save(wechatCallback);
        switch (wechatCallback.getEvent()) {
                // 账户接入回调
            case WeChatConstants.WECHAT_CALLBACK_EVENT_OPEN_PRODUCT_ACCOUNT_REGISTER:

                break;
                // 场景审核接入回调
            case WeChatConstants.WECHAT_CALLBACK_EVENT_OPEN_PRODUCT_SCENE_GROUP_AUDIT:

                break;
                // 类目审核回调
            case WeChatConstants.WECHAT_CALLBACK_EVENT_OPEN_PRODUCT_CATEGORY_AUDIT:
                payComponentCatService.getAuditResultOrAuditCallBack(jsonObject.getJSONObject("QualificationAuditResult"), null);
                break;
                // 系统下架商品通知
            case WeChatConstants.WECHAT_CALLBACK_EVENT_OPEN_PRODUCT_SPU_STATUS_UPDATE:
                payComponentsSyncRiskPutDownByWeiChat(jsonObject);
                break;
                // 视频号支付回调
            case WeChatConstants.WECHAT_CALLBACK_EVENT_OPEN_PRODUCT_ORDER_PAY:
                payComponentsSyncPaidStatus(jsonObject);
                break;
                // 分销员绑定回调
            case WeChatConstants.WECHAT_CALLBACK_EVENT_MINIPROGRAM_SHARER_BIND_STATUS_CHANGE:
                break;
                // 用户领券回调
            case WeChatConstants.WECHAT_CALLBACK_EVENT_OPEN_PRODUCT_RECEIVE_COUPON:
                break;
                // 商品审核
            case WeChatConstants.WECAHT_CALLBACK_EVENT_SPU_AUDIT:
                spuAuditEvent(jsonObject);
                break;
                // 品牌审核
            case WeChatConstants.WECAHT_CALLBACK_EVENT_BRAND_AUDIT:
                brandAuditEvent(jsonObject);
                break;
            default:
                logger.error("微信小程序回调方式未知 {}", JSONObject.parseObject(request));
                break;
        }
        return "success";
    }

    /**
     * 自定义交易组件 通过回调 同步支付结果
     *
     * @param jsonObject 回调内容
     */
    @Override
    public void payComponentsSyncPaidStatus(JSONObject jsonObject) {
        String justCallbackContent = jsonObject.getString("order_info");
        if(ObjectUtil.isNull(jsonObject.getString("order_info"))) throw new CrmebException("同步支付回调时 未找到对应订单 jsonObject:"+JSON.toJSONString(jsonObject));
        // 解析支付结果
        PayComponentCallBackPayResult payResult =
                JSONObject.parseObject(justCallbackContent, PayComponentCallBackPayResult.class);
        PayComponentOrder componentOrder = componentOrderService.getByOrderNo(payResult.getOut_order_id());

        // 设置支付时间和支付单号
        componentOrder.setTimeEnd(payResult.getPay_time());
        componentOrder.setTransactionId(payResult.getTransaction_id());
        componentOrder.setStatus(ShopPaymentEnum.ORDER_DELIVERY.getCode()); // 20 = 待发货
        boolean comOrderUpdated = componentOrderService.updateById(componentOrder);
        if(!comOrderUpdated) {
            throw new CrmebException("自定义交易组件 支付回调 更新支付状态失败:"+JSON.toJSONString(componentOrder));
        }
        orderService.updatePaid(componentOrder.getOrderNo());
    }

    /**
     * 自定义交易组件 微信强制或风控下架
     *
     * @param jsonObject 仅仅传递call的内容
     */
    @Override
    public void payComponentsSyncRiskPutDownByWeiChat(JSONObject jsonObject) {
        String product_id = jsonObject.getString("product_id");
        // 更新过审商品状态
        PayComponentProduct byComponentProductId = componentProductService.getByComponentProductId(Integer.valueOf(product_id));
        byComponentProductId.setStatus(PayComponentStatusEnum.STATUS_PUTDOWN_RISK.getCode());
        byComponentProductId.setPlatformEditStatus(PayComponentPlatformEditStatusEnum.INIT.getCode());
        componentProductService.updateById(byComponentProductId);

        // 更新草稿商品状态
        PayComponentDraftProduct forUpdateDraftProduct = new PayComponentDraftProduct();
        forUpdateDraftProduct.setProductId(byComponentProductId.getId());
        forUpdateDraftProduct.setStatus(PayComponentStatusEnum.STATUS_PUTDOWN_RISK.getCode());
        // 重置平台审核状态
        forUpdateDraftProduct.setPlatformEditStatus(PayComponentPlatformEditStatusEnum.INIT.getCode());
        forUpdateDraftProduct.setReject_reason(jsonObject.getString("reason"));
        componentDraftProductService.updateById(forUpdateDraftProduct);

    }


    /**
     * 品牌审核时间
     * @param jsonObject jsonObject
     */
    private void brandAuditEvent(JSONObject jsonObject) {
        String auditId = jsonObject.getString("audit_id");
        // 查找品牌记录
        PayComponentShopBrand shopBrand = componentShopBrandService.getByAuditId(auditId);
        if (ObjectUtil.isNull(shopBrand)) {
            logger.error(StrUtil.format("品牌审核回调未找到品牌id，审核id:{}", auditId));
            return ;
        }
        Integer status = jsonObject.getInteger("status");
        if (status.equals(1)) { // 审核通过
            shopBrand.setBrandId(jsonObject.getInteger("brand_id"));
        }
        if (status.equals(9)) {// 审核拒绝
            shopBrand.setRejectReason(jsonObject.getString("reject_reason"));
            if (ObjectUtil.isNotNull(jsonObject.getInteger("brand_id"))) {
                shopBrand.setBrandId(jsonObject.getInteger("brand_id"));
            }
        }
        shopBrand.setStatus(status);
        boolean update = componentShopBrandService.updateById(shopBrand);
        if (!update) {
            logger.error(StrUtil.format("品牌审核回调保存数据出错，审核id:{}", auditId));
        }
    }

    /**
     * 商品审核事件
     * 1.先判断是否审核通过
     * 2.如果审核未通过，修改草稿商品表状态、记录失败原因
     * 3.如果审核通过，修改商品表，修改草稿商品表状态，修改sku表，记录审核表
     * platform_edit_status 平台审核状态:1-未审核，2-平台审核中，3-平台审核失败，4-平台审核成功
     * @param jsonObject jsonObject
     */
    private void spuAuditEvent(JSONObject jsonObject) {
        String outProductId = jsonObject.getString("out_product_id");
        // 审核未通过
        if (jsonObject.getInteger("status").equals(PayComponentEditStatusEnum.REVIEW_FAILED.getCode())) {
            // 查找对应的草稿商品
            PayComponentDraftProduct draftProduct = componentDraftProductService.getByProId(Integer.valueOf(outProductId));
            if (ObjectUtil.isNull(draftProduct) || draftProduct.getIsDel()) {
                logger.error(StrUtil.format("商品审核未通过，未找到草稿商品，商品id:{}，失败原因:{}", outProductId, jsonObject.getString("reject_reason")));
                return ;
            }
            ShopSpuCommonVo shopSpuCommonVo = new ShopSpuCommonVo();
            shopSpuCommonVo.setOutProductId(outProductId);
            shopSpuCommonVo.setNeedEditSpu(1); // 0=草稿 1=正式/过审
            ShopSpuVo shopSpuVo = wechatVideoSpuService.shopSpuGet(shopSpuCommonVo);
            draftProduct.setEditStatus(shopSpuVo.getEditStatus());
            draftProduct.setStatus(shopSpuVo.getStatus());
            draftProduct.setUpdateTime(shopSpuVo.getUpdateTime());
            // 写入平台自己的状态 微信侧审核失败 判断是否风控强制下架
            if(shopSpuVo.getStatus().equals(PayComponentStatusEnum.STATUS_PUTDOWN_RISK.getCode())){
                draftProduct.setPlatformStatus(PayComponentPlatformStatusEnum.PLATFORM_PUTDOWN.getCode());
            }
            draftProduct.setPlatformStatusReason(jsonObject.getString("reject_reason"));
            componentDraftProductService.updateById(draftProduct);

            // 保存审核数据
            ShopSpuAuditVo shopSpuAuditVo = shopSpuVo.getAuditInfo();
            PayComponentProductAuditInfo auditInfo = componentProductAuditInfoService.getByProductIdAndAuditId(Integer.valueOf(outProductId), shopSpuAuditVo.getAuditId());
            if (ObjectUtil.isNull(auditInfo)) {
                auditInfo = new PayComponentProductAuditInfo();
                auditInfo.setProductId(Integer.valueOf(outProductId));
                auditInfo.setComponentProductId(draftProduct.getComponentProductId());
                auditInfo.setAuditId(shopSpuAuditVo.getAuditId());
                auditInfo.setRejectReason(Optional.ofNullable(shopSpuAuditVo.getRejectReason()).orElse(""));
                auditInfo.setAuditTime(shopSpuAuditVo.getAuditTime());
                auditInfo.setSubmitTime(shopSpuAuditVo.getSubmitTime());
                componentProductAuditInfoService.save(auditInfo);
            }
            return ;
        }
        // 审核通过
        // 查找对应的草稿商品
        PayComponentDraftProduct draftProduct = componentDraftProductService.getByProId(Integer.valueOf(outProductId));
        if (ObjectUtil.isNull(draftProduct) || draftProduct.getIsDel()) {
            logger.error(StrUtil.format("商品审核回调未找到草稿商品，商品id:{}", outProductId));
            return ;
        }
        //查找对应的商品
        PayComponentProduct product = componentProductService.getById(Integer.valueOf(outProductId));
        if (ObjectUtil.isNull(product)) {
            logger.error(StrUtil.format("商品审核回调未找到商品，商品id:{}", outProductId));
            return ;
        }
        // 判断是商品新增还是商品修改
        if (product.getIsDel() && ObjectUtil.isNull(product.getComponentProductId())) {
            // 商品新增
            // 获取组件线上商品数据
            ShopSpuCommonVo shopSpuCommonVo = new ShopSpuCommonVo();
            shopSpuCommonVo.setOutProductId(outProductId);
            shopSpuCommonVo.setNeedEditSpu(0);
            ShopSpuVo shopSpuVo = wechatVideoSpuService.shopSpuGet(shopSpuCommonVo);

            draftProduct.setStatus(shopSpuVo.getStatus());
            draftProduct.setEditStatus(shopSpuVo.getEditStatus());
            draftProduct.setUpdateTime(shopSpuVo.getUpdateTime());

            product.setComponentProductId(draftProduct.getComponentProductId());
            product.setStatus(shopSpuVo.getStatus());
            product.setEditStatus(shopSpuVo.getEditStatus());
            product.setCreateTime(draftProduct.getCreateTime());
            product.setUpdateTime(draftProduct.getUpdateTime());
            product.setStock(draftProduct.getStock());
            product.setIsDel(false);

            PayComponentProductInfo productInfo = new PayComponentProductInfo();
            if (StrUtil.isNotBlank(draftProduct.getDescInfo())) {
                productInfo.setProductId(product.getId());
                productInfo.setComponentProductId(product.getComponentProductId());
                productInfo.setDesc(draftProduct.getDescInfo());
                productInfo.setIsDel(false);
            }

            List<PayComponentProductSku> skuList = JSONArray.parseArray(draftProduct.getSku(), PayComponentProductSku.class);
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

            List<ProductAttr> attrList = JSONArray.parseArray(draftProduct.getAttr(), ProductAttr.class);
            List<ProductAttrValueRequest> attrValueList = JSONArray.parseArray(draftProduct.getAttrValue(), ProductAttrValueRequest.class);
            // 组装历史表数据，原有attr、attrValue表保存
            MyRecord tableData = assembleHistoryTableData(product, attrList, attrValueList);
            List<ProductAttr> productAttrList = tableData.get("productAttrList");
            List<ProductAttrValue> productAttrValueList = tableData.get("productAttrValueList");

            // 保存审核数据
            ShopSpuAuditVo shopSpuAuditVo = shopSpuVo.getAuditInfo();
            PayComponentProductAuditInfo auditInfo = componentProductAuditInfoService.getByProductIdAndAuditId(Integer.valueOf(outProductId), shopSpuAuditVo.getAuditId());
            if (ObjectUtil.isNull(auditInfo)) {
                auditInfo = new PayComponentProductAuditInfo();
                auditInfo.setProductId(Integer.valueOf(outProductId));
                auditInfo.setComponentProductId(draftProduct.getComponentProductId());
                auditInfo.setAuditId(shopSpuAuditVo.getAuditId());
                auditInfo.setRejectReason(Optional.ofNullable(shopSpuAuditVo.getRejectReason()).orElse(""));
                auditInfo.setAuditTime(shopSpuAuditVo.getAuditTime());
                auditInfo.setSubmitTime(shopSpuAuditVo.getSubmitTime());
            }
            PayComponentProductAuditInfo finalAuditInfo = auditInfo;
            Boolean execute = transactionTemplate.execute(e -> {
                componentDraftProductService.updateById(draftProduct);
                componentProductService.updateById(product);
                if (StrUtil.isNotBlank(draftProduct.getDescInfo())) {
                    componentProductInfoService.save(productInfo);
                }

                // 历史表部分
                productAttrService.saveBatch(productAttrList);
                productAttrValueService.saveBatch(productAttrValueList);

                skuList.forEach(s -> {
                    for (ProductAttrValue value : productAttrValueList) {
                        if (value.getSku().equals(s.getSku())) {
                            s.setAttrValueId(value.getId());
                            break;
                        }
                    }
                    s.setIsDel(false);
                });
                componentProductSkuService.updateBatchById(skuList);
                componentProductSkuAttrService.saveBatch(attrsList);

                if (ObjectUtil.isNull(finalAuditInfo.getId())) {
                    componentProductAuditInfoService.save(finalAuditInfo);
                }
                return Boolean.TRUE;
            });
            if (!execute) {
                logger.error(StrUtil.format("商品审核回调新增商品保存数据出错，商品id:{}", outProductId));
            }

            return ;
        }
        if (product.getIsDel()) {
            logger.error(StrUtil.format("商品审核回调正式商品不存在，商品id:{}", outProductId));
            return ;
        }
        // 更新商品
        // 获取组件线上商品数据
        ShopSpuCommonVo shopSpuCommonVo = new ShopSpuCommonVo();
        shopSpuCommonVo.setOutProductId(outProductId);
        shopSpuCommonVo.setNeedEditSpu(0);
        ShopSpuVo shopSpuVo = wechatVideoSpuService.shopSpuGet(shopSpuCommonVo);

        draftProduct.setStatus(shopSpuVo.getStatus());
        draftProduct.setEditStatus(shopSpuVo.getEditStatus());
        draftProduct.setUpdateTime(shopSpuVo.getUpdateTime());
        // 同步草稿箱数据
        BeanUtils.copyProperties(draftProduct, product, "id", "sales", "isDel");
        product.setHeadImg(systemAttachmentService.prefixImage(product.getHeadImg()));
        if (StrUtil.isNotBlank(product.getQualificationPics())) {
            product.setQualificationPics(systemAttachmentService.prefixImage(product.getQualificationPics()));
        }

        PayComponentProductInfo productInfo = new PayComponentProductInfo();
        if (StrUtil.isNotBlank(draftProduct.getDescInfo())) {
            productInfo.setProductId(product.getId());
            productInfo.setComponentProductId(product.getComponentProductId());
            productInfo.setDesc(draftProduct.getDescInfo());
            productInfo.setIsDel(false);
        }
        // 处理sku部分
        List<PayComponentProductSku> skuList = JSONArray.parseArray(draftProduct.getSku(), PayComponentProductSku.class);
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

        List<ProductAttr> attrList = JSONArray.parseArray(draftProduct.getAttr(), ProductAttr.class);
        List<ProductAttrValueRequest> attrValueList = JSONArray.parseArray(draftProduct.getAttrValue(), ProductAttrValueRequest.class);
        // 组装历史表数据，原有attr、attrValue表保存
        MyRecord tableData = assembleHistoryTableData(product, attrList, attrValueList);
        List<ProductAttr> productAttrList = tableData.get("productAttrList");
        List<ProductAttrValue> productAttrValueList = tableData.get("productAttrValueList");

        // 保存审核数据
        ShopSpuAuditVo shopSpuAuditVo = shopSpuVo.getAuditInfo();
        PayComponentProductAuditInfo auditInfo = componentProductAuditInfoService.getByProductIdAndAuditId(Integer.valueOf(outProductId), shopSpuAuditVo.getAuditId());
        if (ObjectUtil.isNull(auditInfo)) {
            auditInfo = new PayComponentProductAuditInfo();
            auditInfo.setProductId(Integer.valueOf(outProductId));
            auditInfo.setComponentProductId(draftProduct.getComponentProductId());
            auditInfo.setAuditId(shopSpuAuditVo.getAuditId());
            auditInfo.setRejectReason(Optional.ofNullable(shopSpuAuditVo.getRejectReason()).orElse(""));
            auditInfo.setAuditTime(shopSpuAuditVo.getAuditTime());
            auditInfo.setSubmitTime(shopSpuAuditVo.getSubmitTime());
        }
        PayComponentProductAuditInfo finalAuditInfo = auditInfo;
        Boolean execute = transactionTemplate.execute(e -> {
            componentDraftProductService.updateById(draftProduct);
            componentProductService.updateById(product);
            // 删除历史info数据
            componentProductInfoService.deleteByProId(product.getId());
            if (StrUtil.isNotBlank(draftProduct.getDescInfo())) {
                componentProductInfoService.save(productInfo);
            }

            // 历史表部分
            productAttrService.removeByProductId(product.getId(), ProductConstants.PRODUCT_TYPE_COMPONENT);
            productAttrService.saveBatch(productAttrList);
            productAttrValueService.removeByProductId(product.getId(), ProductConstants.PRODUCT_TYPE_COMPONENT);
            productAttrValueService.saveBatch(productAttrValueList);

            // 先删除现有数据
            componentProductSkuService.deleteByProId(product.getId());
            skuList.forEach(s -> {
                for (ProductAttrValue value : productAttrValueList) {
                    if (value.getSku().equals(s.getSku())) {
                        s.setAttrValueId(value.getId());
                        break;
                    }
                }
            });
            componentProductSkuService.updateBatchById(skuList);

            componentProductSkuAttrService.saveBatch(attrsList);

            if (ObjectUtil.isNull(finalAuditInfo.getId())) {
                componentProductAuditInfoService.save(finalAuditInfo);
            }
            return Boolean.TRUE;
        });
        if (!execute) {
            logger.error(StrUtil.format("商品审核回调新增商品保存数据出错，商品id:{}", outProductId));
        }

    }

    /**
     * 组装历史表数据，原有attr、attrValue表保存
     * @param product 组件商品
     * @param attrList 规格数组
     * @param attrValueList 规格属性数组
     * @return MyRecord
     */
    private MyRecord assembleHistoryTableData(PayComponentProduct product, List<ProductAttr> attrList, List<ProductAttrValueRequest> attrValueList) {
        List<ProductAttr> productAttrList = CollUtil.newArrayList();
        List<ProductAttrValue> productAttrValueList = CollUtil.newArrayList();
        // 原有attr、attrValue表保存
        if (product.getSpecType() ) { // 多属性
            if (CollUtil.isNotEmpty(attrList)) {
                attrList.forEach(e->{
                    e.setProductId(product.getId());
                    e.setAttrValues(StringUtils.strip(e.getAttrValues().replace("\"",""),"[]"));
                    e.setType(ProductConstants.PRODUCT_TYPE_COMPONENT);
                });
                productAttrList.addAll(attrList);
            }
            if (CollUtil.isNotEmpty(attrValueList)) {
                // 批量设置attrValues对象的商品id
                for (ProductAttrValueRequest attrValuesRequest : attrValueList) {
                    attrValuesRequest.setProductId(product.getId());
                    ProductAttrValue productAttrValue = new ProductAttrValue();
                    BeanUtils.copyProperties(attrValuesRequest,productAttrValue);
                    //设置sku字段
                    if(StrUtil.isBlank(attrValuesRequest.getAttrValue())) {
                        break;
                    }
                    productAttrValue.setSku(getSku(attrValuesRequest.getAttrValue()));
                    productAttrValue.setImage(systemAttachmentService.clearPrefix(productAttrValue.getImage()));
                    productAttrValue.setAttrValue(JSON.toJSONString(attrValuesRequest.getAttrValue()));
                    productAttrValue.setType(ProductConstants.PRODUCT_TYPE_COMPONENT);
                    productAttrValueList.add(productAttrValue);
                }
            }
        } else { // 单属性
            ProductAttr singleAttr = new ProductAttr();
            singleAttr.setProductId(product.getId()).setAttrName("规格").setAttrValues("默认").setType(ProductConstants.PRODUCT_TYPE_COMPONENT);
            productAttrList.add(singleAttr);

            ProductAttrValue singleAttrValue = new ProductAttrValue();
            // 一级、二级返佣
            ProductAttrValueRequest attrValueRequest = attrValueList.get(0);
            BeanUtils.copyProperties(attrValueRequest, singleAttrValue);
            if (ObjectUtil.isNull(singleAttrValue.getBrokerage())) {
                singleAttrValue.setBrokerage(0);
            }
            if (ObjectUtil.isNull(singleAttrValue.getBrokerageTwo())) {
                singleAttrValue.setBrokerageTwo(0);
            }
            singleAttrValue.setProductId(product.getId());
            singleAttrValue.setSku("默认");
            singleAttrValue.setType(ProductConstants.PRODUCT_TYPE_COMPONENT);
            singleAttrValue.setImage(systemAttachmentService.clearPrefix(singleAttrValue.getImage()));
            productAttrValueList.add(singleAttrValue);
        }
        MyRecord record = new MyRecord();
        record.set("productAttrList", productAttrList);
        record.set("productAttrValueList", productAttrValueList);
        return record;
    }

    /**
     * 商品sku
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
//        List<String> strings = jsonObject.values().stream().map(o -> (String) o).collect(Collectors.toList());
        return String.join(",", strings);
    }
}

