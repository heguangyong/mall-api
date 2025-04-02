package com.zbkj.service.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.zbkj.common.constants.*;
import com.zbkj.common.dto.*;
import com.zbkj.common.exception.CrmebException;
import com.zbkj.common.model.member.PaidMemberOrder;
import com.zbkj.common.model.order.MerchantOrder;
import com.zbkj.common.model.order.Order;
import com.zbkj.common.model.order.OrderDetail;
import com.zbkj.common.model.order.RechargeOrder;
import com.zbkj.common.model.user.UserToken;
import com.zbkj.common.model.wechat.WechatPayInfo;
import com.zbkj.common.response.OrderInvoiceResponse;
import com.zbkj.common.utils.CrmebDateUtil;
import com.zbkj.common.utils.CrmebUtil;
import com.zbkj.common.utils.RedisUtil;
import com.zbkj.common.utils.RestTemplateUtil;
import com.zbkj.service.service.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 微信订单发货管理服务实现类
 *
 * @author Han
 * @version 1.0.0
 * @Date 2023/11/27
 */
@Service
public class WechatOrderShippingServiceImpl implements WechatOrderShippingService {

    private static final Logger logger = LoggerFactory.getLogger(WechatOrderShippingServiceImpl.class);

    @Autowired
    private WechatService wechatService;
    @Autowired
    private WechatPayInfoService wechatPayInfoService;
    @Autowired
    private UserTokenService userTokenService;
    @Autowired
    private OrderInvoiceService orderInvoiceService;
    @Autowired
    private RestTemplateUtil restTemplateUtil;
    @Autowired
    private OrderService orderService;
    @Autowired
    private MerchantOrderService merchantOrderService;
    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private RechargeOrderService rechargeOrderService;
    @Autowired
    private SystemConfigService systemConfigService;
    @Autowired
    private OrderDetailService orderDetailService;
    @Autowired
    private PaidMemberOrderService paidMemberOrderService;

    /**
     * 录入发货信息
     */
    @Override
    public void uploadShippingInfo(String orderNo) {
        uploadShippingInfo(orderNo, "normal");
    }

    /**
     * 录入发货信息
     * @param type normal-正常订单，verify-核销订单，fictitious-虚拟订单
     */
    @Override
    public void uploadShippingInfo(String orderNo, String type) {
        Order order = orderService.getByOrderNo(orderNo);
        String accessToken = wechatService.getMiniAccessToken();

        if (order.getSecondType().equals(OrderConstants.ORDER_SECOND_TYPE_CLOUD)
                || order.getSecondType().equals(OrderConstants.ORDER_SECOND_TYPE_CDKEY)) {
            type = "fictitious";
        }
        int logisticsType = 0;
        int deliveryMode = 1;
        boolean isAllDelivered = true;
        switch (type) {
            case "fictitious":
                logisticsType = 3;
                break;
            case "verify":
                logisticsType = 4;
                break;
            case "normal":
                logisticsType = 1;
                deliveryMode = 2;
                isAllDelivered = false;
                break;
        }

        WechatOrderKeyDto wechatOrderKey = getWechatOrderKey(order.getOutTradeNo());
        WechatOrderPayerDto wechatOrderPayer = getWechatOrderPayer(order.getUid());
        List<WechatUploadShippingInfoDto> shippingList = getShippingList(orderNo, type);

        WechatUploadShippingDto wechatUploadShippingDto = new WechatUploadShippingDto();
        wechatUploadShippingDto.setOrderKey(wechatOrderKey);
        wechatUploadShippingDto.setLogisticsType(logisticsType);
        wechatUploadShippingDto.setDeliveryMode(deliveryMode);
        wechatUploadShippingDto.setIsAllDelivered(isAllDelivered);
        wechatUploadShippingDto.setUploadTime(CrmebDateUtil.dateToStr(new Date(), DateConstants.DATE_FORMAT_RFC_3339));
        wechatUploadShippingDto.setPayer(wechatOrderPayer);

        wechatUploadShippingDto.setShippingList(shippingList);

        if (type.equals("normal")) {
            // 判断订单是否全部发货
            if (orderService.isAllSendGoods(order.getPlatOrderNo())) {
                wechatUploadShippingDto.setIsAllDelivered(true);
            }
        }

        postUpload(accessToken, wechatUploadShippingDto);
    }

    private void postUpload(String accessToken, WechatUploadShippingDto wechatUploadShippingDto) {
        String resultData = restTemplateUtil.postStringData(StrUtil.format(WeChatConstants.WECHAT_MINI_UPLOAD_SHIPPING_URL, accessToken), JSONObject.toJSONString(wechatUploadShippingDto));
        JSONObject resultJsonObject = JSONObject.parseObject(resultData);
        try {
            checkWechatResult(resultJsonObject);
        } catch (Exception e) {
            logger.error("微信小程序上传发货管理，失败", e);
        }
    }

    /**
     * @param type normal-正常订单，verify-核销订单，fictitious-虚拟订单
     * @return
     */
    private List<WechatUploadShippingInfoDto> getShippingList(String orderNo, String type) {
        List<WechatUploadShippingInfoDto> shippingInfoDtoList = new ArrayList<>();
        if (type.equals("fictitious") || type.equals("verify")) {
            WechatUploadShippingInfoDto shippingInfoDto = new WechatUploadShippingInfoDto();
            List<OrderDetail> orderDetailList = orderDetailService.getByOrderNo(orderNo);
            String collect = orderDetailList.stream().map(d -> StrUtil.format("{}*{}件", d.getProductName(), d.getPayNum())).collect(Collectors.joining(","));
            if (collect.length() > 120) {
                collect = collect.substring(0, 119);
            }
            shippingInfoDto.setItemDesc(collect);
            shippingInfoDtoList.add(shippingInfoDto);
        }
        if (type.equals("normal")) {
            List<OrderInvoiceResponse> invoiceResponseList = orderInvoiceService.findByOrderNo(orderNo);
            if (CollUtil.isEmpty(invoiceResponseList)) {
                throw new CrmebException("未找到发货单");
            }
            shippingInfoDtoList = invoiceResponseList.stream().map(e -> {
                WechatUploadShippingInfoDto shippingInfoDto = new WechatUploadShippingInfoDto();
                shippingInfoDto.setTrackingNo(e.getTrackingNumber());
                String expressCompany = getWechatDeliveryIdByOrderExpressName(e.getExpressName());
                shippingInfoDto.setExpressCompany(expressCompany);
                String collect = e.getDetailList().stream().map(d -> StrUtil.format("{}*{}件", d.getProductName(), d.getNum())).collect(Collectors.joining(","));
                if (collect.length() > 120) {
                    collect = collect.substring(0, 119);
                }
                shippingInfoDto.setItemDesc(collect);
                if (expressCompany.equals("SF")) {
                    MerchantOrder merchantOrder = merchantOrderService.getOneByOrderNo(orderNo);
                    WechatShippingContactDto contactDto = new WechatShippingContactDto();
                    contactDto.setReceiverContact(CrmebUtil.maskMobile(merchantOrder.getUserPhone()));
                    shippingInfoDto.setContact(contactDto);
                }
                return shippingInfoDto;
            }).collect(Collectors.toList());
        }

        return shippingInfoDtoList;
    }

    private WechatOrderPayerDto getWechatOrderPayer(Integer uid) {
        UserToken userToken = userTokenService.getTokenByUserId(uid, UserConstants.USER_TOKEN_TYPE_ROUTINE);
        if (ObjectUtil.isNull(userToken)) {
            throw new CrmebException("未找到用户对应的小程序openID");
        }
        WechatOrderPayerDto payerDto = new WechatOrderPayerDto();
        payerDto.setOpenid(userToken.getToken());
        return payerDto;
    }

    private WechatOrderKeyDto getWechatOrderKey(String outTradeNo) {
        WechatPayInfo wechatPayInfo = wechatPayInfoService.getByNo(outTradeNo);
        if (ObjectUtil.isNull(wechatPayInfo)) {
            throw new CrmebException("未找到对应微信订单");
        }
        WechatOrderKeyDto orderKeyDto = new WechatOrderKeyDto();
        orderKeyDto.setOrderNumberType(2);
        orderKeyDto.setMchid(wechatPayInfo.getMchId());
        orderKeyDto.setTransactionId(wechatPayInfo.getTransactionId());
        orderKeyDto.setOutTradeNo(outTradeNo);
        return orderKeyDto;
    }

    /**
     * 批量录入充值订单发货
     */
    @Override
    public void batchUploadRechargeOrderShipping() {
        String shippingSwitch = systemConfigService.getValueByKey(WeChatConstants.CONFIG_WECHAT_ROUTINE_SHIPPING_SWITCH);
        if (StrUtil.isBlank(shippingSwitch) || shippingSwitch.equals("0")) {
            return;
        }
        List<RechargeOrder> rechargeOrderList = rechargeOrderService.findAwaitUploadWechatList();
        if (CollUtil.isEmpty(rechargeOrderList)) {
            return;
        }
        String accessToken = wechatService.getMiniAccessToken();
        for (RechargeOrder rechargeOrder : rechargeOrderList) {
            WechatOrderKeyDto wechatOrderKey;
            WechatOrderPayerDto wechatOrderPayer;
            try {
                wechatOrderKey = getWechatOrderKey(rechargeOrder.getOutTradeNo());
                wechatOrderPayer = getWechatOrderPayer(rechargeOrder.getUid());
            } catch (Exception e) {
                logger.error("充值订单微信小程序发货，获取对应微信订单信息错误，充值单号={}", rechargeOrder.getOrderNo());
                logger.error("充值订单微信小程序发货，获取对应微信订单信息错误，异常信息", e);
                continue;
            }
            List<WechatUploadShippingInfoDto> shippingList = getShippingList(rechargeOrder.getPrice(), rechargeOrder.getGivePrice(), "recharge");

            WechatUploadShippingDto wechatUploadShippingDto = new WechatUploadShippingDto();
            wechatUploadShippingDto.setOrderKey(wechatOrderKey);
            wechatUploadShippingDto.setLogisticsType(3);
            wechatUploadShippingDto.setDeliveryMode(1);
            wechatUploadShippingDto.setIsAllDelivered(true);
            wechatUploadShippingDto.setUploadTime(CrmebDateUtil.dateToStr(new Date(), DateConstants.DATE_FORMAT_RFC_3339));
            wechatUploadShippingDto.setPayer(wechatOrderPayer);
            wechatUploadShippingDto.setShippingList(shippingList);

            postUpload(accessToken, wechatUploadShippingDto);
        }
    }

    /**
     *
     * @param type recharge-充值订单，svip-svip订单
     */
    private List<WechatUploadShippingInfoDto> getShippingList(BigDecimal price, BigDecimal givePrice, String type) {
        List<WechatUploadShippingInfoDto> shippingInfoDtoList = new ArrayList<>();
        WechatUploadShippingInfoDto shippingInfoDto = new WechatUploadShippingInfoDto();
        if (type.equals("recharge")) {
            shippingInfoDto.setItemDesc(StrUtil.format("用户充值{}余额,赠送{}", price, givePrice));
            shippingInfoDtoList.add(shippingInfoDto);
        }
        if (type.equals("svip")) {
            shippingInfoDto.setItemDesc(StrUtil.format("用户花费{}购买SVIP会员,赠送{}余额", price, givePrice));
            shippingInfoDtoList.add(shippingInfoDto);
        }
        return shippingInfoDtoList;
    }

    /**
     * 批量录入SVIP订单发货
     */
    @Override
    public void batchUploadSvipOrderShipping() {
        String shippingSwitch = systemConfigService.getValueByKey(WeChatConstants.CONFIG_WECHAT_ROUTINE_SHIPPING_SWITCH);
        if (StrUtil.isBlank(shippingSwitch) || shippingSwitch.equals("0")) {
            return;
        }
        List<PaidMemberOrder> orderList = paidMemberOrderService.findAwaitUploadWechatList();
        if (CollUtil.isEmpty(orderList)) {
            return;
        }

        String accessToken = wechatService.getMiniAccessToken();
        for (PaidMemberOrder order : orderList) {
            WechatOrderKeyDto wechatOrderKey;
            WechatOrderPayerDto wechatOrderPayer;
            try {
                wechatOrderKey = getWechatOrderKey(order.getOutTradeNo());
                wechatOrderPayer = getWechatOrderPayer(order.getUid());
            } catch (Exception e) {
                logger.error("SVIP订单微信小程序发货，获取对应微信订单信息错误，充值单号={}", order.getOrderNo());
                logger.error("SVIP订单微信小程序发货，获取对应微信订单信息错误，异常信息", e);
                continue;
            }
            List<WechatUploadShippingInfoDto> shippingList = getShippingList(order.getPrice(), order.getGiftBalance(), "svip");

            WechatUploadShippingDto wechatUploadShippingDto = new WechatUploadShippingDto();
            wechatUploadShippingDto.setOrderKey(wechatOrderKey);
            wechatUploadShippingDto.setLogisticsType(3);
            wechatUploadShippingDto.setDeliveryMode(1);
            wechatUploadShippingDto.setIsAllDelivered(true);
            wechatUploadShippingDto.setUploadTime(CrmebDateUtil.dateToStr(new Date(), DateConstants.DATE_FORMAT_RFC_3339));
            wechatUploadShippingDto.setPayer(wechatOrderPayer);
            wechatUploadShippingDto.setShippingList(shippingList);

            postUpload(accessToken, wechatUploadShippingDto);
        }
    }

    /**
     * 获取运力公司名称通过订单发货公司
     *
     * @param expressName 订单快递公司名称
     * @return 运力公司编码
     */
    private String getWechatDeliveryIdByOrderExpressName(String expressName) {
        if (!redisUtil.exists(RedisConstants.WECHAT_MINI_DELIVERY_KEY)) {
            wechatGetDeliveryList();
        } else {
            Long hashSize = redisUtil.getHashSize(RedisConstants.WECHAT_MINI_DELIVERY_KEY);
            if (hashSize > 0) {
                wechatGetDeliveryList();
            }
        }
        if (!redisUtil.hHasKey(RedisConstants.WECHAT_MINI_DELIVERY_KEY, expressName)) {
            // 未从redis中找到对应物流公司，返回固定值,韵达物流
            return "YD";
        }
        Object object = redisUtil.hget(RedisConstants.WECHAT_MINI_DELIVERY_KEY, expressName);
        return object.toString();
    }

    /**
     * 检查调用微信结果
     * @param resultJsonObject 微信返回结果
     */
    private void checkWechatResult(JSONObject resultJsonObject) {
        if (ObjectUtil.isNull(resultJsonObject)) {
            throw new CrmebException("微信小程序接口异常，无返回结果");
        }
        if (!resultJsonObject.containsKey("errcode")) {
            logger.error("微信小程序接口失败，无errcode对象, errmsg = {}", resultJsonObject.getString("errmsg"));
            throw new CrmebException(StrUtil.format("微信小程序接口失败，无errcode对象, errmsg = {}", resultJsonObject.getString("errmsg")));
        }
        if (!resultJsonObject.getInteger("errcode").equals(0)) {
            logger.error("微信小程序接口失败，errcode = {}, errmsg = {}", resultJsonObject.getInteger("errcode"), resultJsonObject.getString("errmsg"));
            throw new CrmebException(StrUtil.format("微信小程序接口失败，errcode = {}, errmsg = {}", resultJsonObject.getInteger("errcode"), resultJsonObject.getString("errmsg")));
        }
    }

    /**
     * 获取微信运力ID列表
     * 例：
     * {
     *     "errcode": 0,
     *     "delivery_list": [
     *       {
     *           "delivery_id": "(AU)",
     *           "delivery_name": "Interparcel"
     *       },
     *       {
     *           "delivery_id": "BDT",
     *           "delivery_name": "八达通"
     *       },
     *       {
     *           "delivery_id": "YD",
     *           "delivery_name": "韵达速递"
     *       },
     *       ...
     *     ],
     *     "count": 1379
     * }
     */
    private void wechatGetDeliveryList() {
        String accessToken = wechatService.getMiniAccessToken();
//        JSONObject jsonObject = restTemplateUtil.post(StrUtil.format(WeChatConstants.WECHAT_MINI_GET_DELIVERY_LIST_URL, accessToken));
        String result = HttpUtil.post(StrUtil.format(WeChatConstants.WECHAT_MINI_GET_DELIVERY_LIST_URL, accessToken), String.valueOf(new HashMap<String, Object>()));
        JSONObject jsonObject = JSONObject.parseObject(result);
        checkWechatResult(jsonObject);
        JSONArray deliveryListArray = jsonObject.getJSONArray("delivery_list");
        for (int i = 0; i < deliveryListArray.size(); i++) {
            JSONObject deliveryJsonObject = deliveryListArray.getJSONObject(i);
            redisUtil.hset(RedisConstants.WECHAT_MINI_DELIVERY_KEY, deliveryJsonObject.getString("delivery_name"), deliveryJsonObject.getString("delivery_id"));
        }
    }
}
