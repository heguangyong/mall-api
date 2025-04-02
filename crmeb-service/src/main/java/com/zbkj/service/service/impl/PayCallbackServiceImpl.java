package com.zbkj.service.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alipay.api.AlipayApiException;
import com.alipay.api.internal.util.AlipaySignature;
import com.zbkj.common.constants.*;
import com.zbkj.common.exception.CrmebException;
import com.zbkj.common.model.alipay.AliPayCallback;
import com.zbkj.common.model.member.PaidMemberOrder;
import com.zbkj.common.model.order.Order;
import com.zbkj.common.model.order.RechargeOrder;
import com.zbkj.common.model.order.RefundOrder;
import com.zbkj.common.model.user.User;
import com.zbkj.common.model.wechat.WechatPayInfo;
import com.zbkj.common.utils.CrmebDateUtil;
import com.zbkj.common.utils.CrmebUtil;
import com.zbkj.common.utils.RedisUtil;
import com.zbkj.common.utils.WxPayUtil;
import com.zbkj.common.vo.AttachVo;
import com.zbkj.common.vo.MyRecord;
import com.zbkj.common.vo.WechatPayCallbackVo;
import com.zbkj.service.service.*;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.security.Security;
import java.util.*;


/**
 * 订单支付回调 CallbackService 实现类
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
public class PayCallbackServiceImpl implements PayCallbackService {

    private static final Logger logger = LoggerFactory.getLogger(PayCallbackServiceImpl.class);

    @Autowired
    private OrderService orderService;
    @Autowired
    private UserService userService;
    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private SystemConfigService systemConfigService;
    @Autowired
    private TransactionTemplate transactionTemplate;
    @Autowired
    private WechatPayInfoService wechatPayInfoService;
    @Autowired
    private RechargeOrderService rechargeOrderService;
    @Autowired
    private AsyncService asyncService;
    @Autowired
    private RefundOrderService refundOrderService;
    @Autowired
    private AliPayCallbackService aliPayCallbackService;
    @Autowired
    private PaidMemberOrderService paidMemberOrderService;

    /**
     * 微信支付回调
     */
    @Override
    public String wechatPayCallback(String xmlInfo) {
        StringBuffer sb = new StringBuffer();
        sb.append("<xml>");
        if (StrUtil.isBlank(xmlInfo)) {
            sb.append("<return_code><![CDATA[FAIL]]></return_code>");
            sb.append("<return_msg><![CDATA[xmlInfo is blank]]></return_msg>");
            sb.append("</xml>");
            logger.error("wechat callback error : " + sb);
            return sb.toString();
        }

        try {
            HashMap<String, Object> map = WxPayUtil.processResponseXml(xmlInfo);
            // 通信是否成功
            String returnCode = (String) map.get("return_code");
            if (!returnCode.equals(Constants.SUCCESS)) {
                sb.append("<return_code><![CDATA[SUCCESS]]></return_code>");
                sb.append("<return_msg><![CDATA[OK]]></return_msg>");
                sb.append("</xml>");
                logger.error("wechat callback error : wx pay return code is fail returnMsg : " + map.get("return_msg"));
                return sb.toString();
            }
            // 交易是否成功
            String resultCode = (String) map.get("result_code");
            if (!resultCode.equals(Constants.SUCCESS)) {
                sb.append("<return_code><![CDATA[SUCCESS]]></return_code>");
                sb.append("<return_msg><![CDATA[OK]]></return_msg>");
                sb.append("</xml>");
                logger.error("wechat callback error : wx pay result code is fail");
                return sb.toString();
            }

            //解析xml
            WechatPayCallbackVo callbackVo = CrmebUtil.mapToObj(map, WechatPayCallbackVo.class);
            AttachVo attachVo = JSONObject.toJavaObject(JSONObject.parseObject(callbackVo.getAttach()), AttachVo.class);

            //判断openid
            User user = userService.getById(attachVo.getUserId());
            if (ObjectUtil.isNull(user)) {
                //用户信息错误
                throw new CrmebException("用户信息错误！");
            }

            //根据类型判断是订单或者充值
            if (!PayConstants.PAY_SERVICE_TYPE_ORDER.equals(attachVo.getType()) && !PayConstants.PAY_SERVICE_TYPE_RECHARGE.equals(attachVo.getType())
                    && !PayConstants.PAY_SERVICE_TYPE_SVIP.equals(attachVo.getType())) {
                logger.error("wechat pay err : 未知的支付类型==》" + callbackVo.getOutTradeNo());
                throw new CrmebException("未知的支付类型！");
            }
            // 订单
            if (PayConstants.PAY_SERVICE_TYPE_ORDER.equals(attachVo.getType())) {
                Order order = orderService.getByOutTradeNo(callbackVo.getOutTradeNo());
                if (ObjectUtil.isNull(order) || !order.getUid().equals(attachVo.getUserId())) {
                    logger.error("wechat pay error : 订单信息不存在==》" + callbackVo.getOutTradeNo());
                    throw new CrmebException("wechat pay error : 订单信息不存在==》" + callbackVo.getOutTradeNo());
                }
                if (order.getPaid()) {
                    sb.append("<return_code><![CDATA[SUCCESS]]></return_code>");
                    sb.append("<return_msg><![CDATA[OK]]></return_msg>");
                    sb.append("</xml>");
                    return sb.toString();
                }

                String signKey = "";
                String payChannel = order.getPayChannel();
                if (payChannel.equals(PayConstants.PAY_CHANNEL_H5) || payChannel.equals(PayConstants.PAY_CHANNEL_WECHAT_NATIVE)) {
                    String source = systemConfigService.getValueByKey(SysConfigConstants.WECHAT_PAY_SOURCE_H5_PC);
                    if (StrUtil.isNotBlank(source) && source.equals(PayConstants.WECHAT_PAY_SOURCE_MINI)) {
                        payChannel = PayConstants.PAY_CHANNEL_WECHAT_MINI;
                    } else {
                        payChannel = PayConstants.PAY_CHANNEL_WECHAT_PUBLIC;
                    }
                }
                switch (payChannel) {
                    case PayConstants.PAY_CHANNEL_WECHAT_PUBLIC:
                        signKey = systemConfigService.getValueByKeyException(WeChatConstants.WECHAT_PAY_PUBLIC_KEY);
                        break;
                    case PayConstants.PAY_CHANNEL_WECHAT_MINI:
                    case PayConstants.PAY_CHANNEL_WECHAT_MINI_VIDEO:
                        signKey = systemConfigService.getValueByKeyException(WeChatConstants.WECHAT_PAY_MINI_KEY);
                        break;
                    case PayConstants.PAY_CHANNEL_WECHAT_APP_IOS:
                    case PayConstants.PAY_CHANNEL_WECHAT_APP_ANDROID:
                        signKey = systemConfigService.getValueByKeyException(WeChatConstants.WECHAT_PAY_APP_KEY);
                        break;
                }
                Map<String, String> stringMap = WxPayUtil.xmlToMap(xmlInfo);
                String sign = WxPayUtil.getSign(stringMap, signKey);
                if (!sign.equals(stringMap.get(PayConstants.FIELD_SIGN))) {
                    logger.error("wechat pay error : 微信订单回调验签失败 ==> {}", xmlInfo);
                    throw new CrmebException(StrUtil.format("wechat pay error : 微信订单回调验签失败 ==> {}", xmlInfo));
                }

                WechatPayInfo wechatPayInfo = wechatPayInfoService.getByNo(order.getOutTradeNo());
                if (ObjectUtil.isNull(wechatPayInfo)) {
                    logger.error("wechat pay error : 微信订单信息不存在==》" + callbackVo.getOutTradeNo());
                    throw new CrmebException("wechat pay error : 微信订单信息不存在==》" + callbackVo.getOutTradeNo());
                }
                wechatPayInfo.setIsSubscribe(callbackVo.getIsSubscribe());
                wechatPayInfo.setBankType(callbackVo.getBankType());
                wechatPayInfo.setCashFee(callbackVo.getCashFee());
                wechatPayInfo.setCouponFee(callbackVo.getCouponFee());
                wechatPayInfo.setTransactionId(callbackVo.getTransactionId());
                wechatPayInfo.setTimeEnd(callbackVo.getTimeEnd());

                // 添加支付成功redis队列
                Boolean execute = transactionTemplate.execute(e -> {
                    order.setPaid(true);
                    order.setPayTime(CrmebDateUtil.nowDateTime());
                    order.setStatus(OrderConstants.ORDER_STATUS_WAIT_SHIPPING);
                    orderService.updateById(order);
                    wechatPayInfoService.updateById(wechatPayInfo);
                    return Boolean.TRUE;
                });
                if (!execute) {
                    logger.error("wechat pay error : 订单更新失败==》" + callbackVo.getOutTradeNo());
                    sb.append("<return_code><![CDATA[SUCCESS]]></return_code>");
                    sb.append("<return_msg><![CDATA[OK]]></return_msg>");
                    sb.append("</xml>");
                    return sb.toString();
                }
                asyncService.orderPaySuccessSplit(order.getOrderNo());
//                redisUtil.lPush(TaskConstants.ORDER_TASK_PAY_SUCCESS_AFTER, order.getOrderNo());
            }
            // 充值订单处理
            if (PayConstants.PAY_SERVICE_TYPE_RECHARGE.equals(attachVo.getType())) {
                RechargeOrder rechargeOrder = rechargeOrderService.getByOutTradeNo(callbackVo.getOutTradeNo());
                if (ObjectUtil.isNull(rechargeOrder)) {
                    logger.error("充值订单后置处理，没有找到对应订单，支付服务方订单号：{}", callbackVo.getOutTradeNo());
                    throw new CrmebException("没有找到充值订单信息");
                }
                if (rechargeOrder.getPaid()) {
                    sb.append("<return_code><![CDATA[SUCCESS]]></return_code>");
                    sb.append("<return_msg><![CDATA[OK]]></return_msg>");
                    sb.append("</xml>");
                    return sb.toString();
                }

                String signKey = "";
                String payChannel = rechargeOrder.getPayChannel();
                if (payChannel.equals(PayConstants.PAY_CHANNEL_H5) || payChannel.equals(PayConstants.PAY_CHANNEL_WECHAT_NATIVE)) {
                    String source = systemConfigService.getValueByKey(SysConfigConstants.WECHAT_PAY_SOURCE_H5_PC);
                    if (StrUtil.isNotBlank(source) && source.equals(PayConstants.WECHAT_PAY_SOURCE_MINI)) {
                        payChannel = PayConstants.PAY_CHANNEL_WECHAT_MINI;
                    } else {
                        payChannel = PayConstants.PAY_CHANNEL_WECHAT_PUBLIC;
                    }
                }
                switch (payChannel) {
                    case PayConstants.PAY_CHANNEL_WECHAT_PUBLIC:
                        signKey = systemConfigService.getValueByKeyException(WeChatConstants.WECHAT_PAY_PUBLIC_KEY);
                        break;
                    case PayConstants.PAY_CHANNEL_WECHAT_MINI:
                    case PayConstants.PAY_CHANNEL_WECHAT_MINI_VIDEO:
                    case PayConstants.PAY_CHANNEL_H5:// H5使用公众号的信息
                    case PayConstants.PAY_CHANNEL_WECHAT_NATIVE:// H5使用公众号的信息
                        signKey = systemConfigService.getValueByKeyException(WeChatConstants.WECHAT_PAY_MINI_KEY);
                        break;
                    case PayConstants.PAY_CHANNEL_WECHAT_APP_IOS:
                    case PayConstants.PAY_CHANNEL_WECHAT_APP_ANDROID:
                        signKey = systemConfigService.getValueByKeyException(WeChatConstants.WECHAT_PAY_APP_KEY);
                        break;
                }
                Map<String, String> stringMap = WxPayUtil.xmlToMap(xmlInfo);
                String sign = WxPayUtil.getSign(stringMap, signKey);
                if (!sign.equals(stringMap.get(PayConstants.FIELD_SIGN))) {
                    logger.error("wechat pay error : 微信充值订单回调验签失败 ==> {}", xmlInfo);
                    throw new CrmebException(StrUtil.format("wechat pay error : 微信充值订单回调验签失败 ==> {}", xmlInfo));
                }

                WechatPayInfo wechatPayInfo = wechatPayInfoService.getByNo(rechargeOrder.getOutTradeNo());
                if (ObjectUtil.isNull(wechatPayInfo)) {
                    logger.error("wechat pay error : 微信充值订单信息不存在==》" + callbackVo.getOutTradeNo());
                    throw new CrmebException("wechat pay error : 微信充值订单信息不存在==》" + callbackVo.getOutTradeNo());
                }
                wechatPayInfo.setIsSubscribe(callbackVo.getIsSubscribe());
                wechatPayInfo.setBankType(callbackVo.getBankType());
                wechatPayInfo.setCashFee(callbackVo.getCashFee());
                wechatPayInfo.setCouponFee(callbackVo.getCouponFee());
                wechatPayInfo.setTransactionId(callbackVo.getTransactionId());
                wechatPayInfo.setTimeEnd(callbackVo.getTimeEnd());
                wechatPayInfoService.updateById(wechatPayInfo);
                // 支付成功处理
                Boolean rechargePayAfter = rechargeOrderService.paySuccessAfter(rechargeOrder);
                if (!rechargePayAfter) {
                    logger.error("wechat recharge pay after error : 数据保存失败==》" + callbackVo.getOutTradeNo());
                    throw new CrmebException("wechat recharge pay after error : 数据保存失败==》" + callbackVo.getOutTradeNo());
                }
            }
            // svip订单
            if (PayConstants.PAY_SERVICE_TYPE_SVIP.equals(attachVo.getType())) {
                PaidMemberOrder svipOrder = paidMemberOrderService.getByOutTradeNo(callbackVo.getOutTradeNo());
                if (ObjectUtil.isNull(svipOrder)) {
                    logger.error("SVIP订单后置处理，没有找到对应订单，支付服务方订单号：{}", callbackVo.getOutTradeNo());
                    throw new CrmebException("没有找到充值订单信息");
                }
                if (svipOrder.getPaid()) {
                    sb.append("<return_code><![CDATA[SUCCESS]]></return_code>");
                    sb.append("<return_msg><![CDATA[OK]]></return_msg>");
                    sb.append("</xml>");
                    return sb.toString();
                }
                String signKey = "";
                String payChannel = svipOrder.getPayChannel();
                if (payChannel.equals(PayConstants.PAY_CHANNEL_H5) || payChannel.equals(PayConstants.PAY_CHANNEL_WECHAT_NATIVE)) {
                    String source = systemConfigService.getValueByKey(SysConfigConstants.WECHAT_PAY_SOURCE_H5_PC);
                    if (StrUtil.isNotBlank(source) && source.equals(PayConstants.WECHAT_PAY_SOURCE_MINI)) {
                        payChannel = PayConstants.PAY_CHANNEL_WECHAT_MINI;
                    } else {
                        payChannel = PayConstants.PAY_CHANNEL_WECHAT_PUBLIC;
                    }
                }
                switch (payChannel) {
                    case PayConstants.PAY_CHANNEL_WECHAT_PUBLIC:
                        signKey = systemConfigService.getValueByKeyException(WeChatConstants.WECHAT_PAY_PUBLIC_KEY);
                        break;
                    case PayConstants.PAY_CHANNEL_WECHAT_MINI:
                    case PayConstants.PAY_CHANNEL_WECHAT_MINI_VIDEO:
                    case PayConstants.PAY_CHANNEL_H5:// H5使用公众号的信息
                    case PayConstants.PAY_CHANNEL_WECHAT_NATIVE:// H5使用公众号的信息
                        signKey = systemConfigService.getValueByKeyException(WeChatConstants.WECHAT_PAY_MINI_KEY);
                        break;
                    case PayConstants.PAY_CHANNEL_WECHAT_APP_IOS:
                    case PayConstants.PAY_CHANNEL_WECHAT_APP_ANDROID:
                        signKey = systemConfigService.getValueByKeyException(WeChatConstants.WECHAT_PAY_APP_KEY);
                        break;
                }
                Map<String, String> stringMap = WxPayUtil.xmlToMap(xmlInfo);
                String sign = WxPayUtil.getSign(stringMap, signKey);
                if (!sign.equals(stringMap.get(PayConstants.FIELD_SIGN))) {
                    logger.error("wechat pay error : 微信SVIP订单回调验签失败 ==> {}", xmlInfo);
                    throw new CrmebException(StrUtil.format("wechat pay error : 微信SVIP订单回调验签失败 ==> {}", xmlInfo));
                }

                WechatPayInfo wechatPayInfo = wechatPayInfoService.getByNo(svipOrder.getOutTradeNo());
                if (ObjectUtil.isNull(wechatPayInfo)) {
                    logger.error("wechat pay error : 微信充值订单信息不存在==》" + callbackVo.getOutTradeNo());
                    throw new CrmebException("wechat pay error : 微信充值订单信息不存在==》" + callbackVo.getOutTradeNo());
                }
                wechatPayInfo.setIsSubscribe(callbackVo.getIsSubscribe());
                wechatPayInfo.setBankType(callbackVo.getBankType());
                wechatPayInfo.setCashFee(callbackVo.getCashFee());
                wechatPayInfo.setCouponFee(callbackVo.getCouponFee());
                wechatPayInfo.setTransactionId(callbackVo.getTransactionId());
                wechatPayInfo.setTimeEnd(callbackVo.getTimeEnd());
                wechatPayInfoService.updateById(wechatPayInfo);

                // 支付成功处理
                Boolean svipPayAfter = paidMemberOrderService.paySuccessAfter(svipOrder);
                if (!svipPayAfter) {
                    logger.error("wechat recharge pay after error : 数据保存失败==》" + callbackVo.getOutTradeNo());
                    throw new CrmebException("wechat recharge pay after error : 数据保存失败==》" + callbackVo.getOutTradeNo());
                }
            }
            sb.append("<return_code><![CDATA[SUCCESS]]></return_code>");
            sb.append("<return_msg><![CDATA[OK]]></return_msg>");
        } catch (Exception e) {
            sb.append("<return_code><![CDATA[FAIL]]></return_code>");
            sb.append("<return_msg><![CDATA[").append(e.getMessage()).append("]]></return_msg>");
            logger.error("wechat pay error : 业务异常==》" + e.getMessage());
        }
        sb.append("</xml>");
        return sb.toString();
    }

    /**
     * 支付宝支付回调
     */
    @Override
    public String aliPayCallback(HttpServletRequest request) {
        Map<String, String> params = convertRequestParamsToMap(request); // 将异步通知中收到的待验证所有参数都存放到map中
        String paramsJson = JSON.toJSONString(params);
        // 保存支付宝回调信息
        saveAliPayCallbackInfo(params);
        try {
            //商户订单号
            String outTradeNo = params.get("out_trade_no");
            String outRequestNo = params.get("out_request_no");
            // 判断是否是退款订单
            String refundFee = params.get("refund_fee");
            if (StrUtil.isNotBlank(refundFee)) {// 订单退款
                return "success";
            }

            // 判断订单类型
            String passbackParams = params.get("passback_params");
            if (StrUtil.isNotBlank(passbackParams)) {
                String decode;
                try {
                    decode = URLDecoder.decode(passbackParams, "utf-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                    logger.error("ali pay error : 订单支付类型解码失败==》" + outTradeNo);
                    return "fail";
                }
                String[] split = decode.split("=");
                String orderType = split[1];
                if (PayConstants.PAY_SERVICE_TYPE_RECHARGE.equals(orderType)) {// 充值订单
                    RechargeOrder rechargeOrder = rechargeOrderService.getByOutTradeNo(outTradeNo);
                    if (ObjectUtil.isNull(rechargeOrder)) {
                        logger.error("ali pay error : 充值订单后置处理，没有找到对应订单，支付服务方订单号：{}", outTradeNo);
                        return "fail";
                    }
                    if (rechargeOrder.getPaid()) {
                        return "success";
                    }
                    // 支付成功处理
                    Boolean rechargePayAfter = rechargeOrderService.paySuccessAfter(rechargeOrder);
                    if (!rechargePayAfter) {
                        logger.error("ali pay recharge pay after error : 数据保存失败==》" + outTradeNo);
                        return "fail";
                    }
                    return "success";
                }
                if (PayConstants.PAY_SERVICE_TYPE_SVIP.equals(orderType)) {// 充值订单
                    PaidMemberOrder svipOrder = paidMemberOrderService.getByOutTradeNo(outTradeNo);
                    if (ObjectUtil.isNull(svipOrder)) {
                        logger.error("ali pay error : SVIP订单后置处理，没有找到对应订单，支付服务方订单号：{}", outTradeNo);
                        return "fail";
                    }
                    if (svipOrder.getPaid()) {
                        return "success";
                    }
                    // 支付成功处理
                    Boolean svipPayAfter = paidMemberOrderService.paySuccessAfter(svipOrder);
                    if (!svipPayAfter) {
                        logger.error("ali pay svip pay after error : 数据保存失败==》" + outTradeNo);
                        return "fail";
                    }
                    return "success";
                }
            }

            // 找到原订单
            Order order = orderService.getByOrderNo(outTradeNo);
            if (ObjectUtil.isNull(order)) {
                logger.error("ali pay error : 订单信息不存在==》" + outTradeNo);
                return "fail";
            }
            if (order.getPaid()) {
                logger.error("ali pay error : 订单已处理==》" + outTradeNo);
                return "success";
            }
            //判断openid
            User user = userService.getById(order.getUid());
            if (ObjectUtil.isNull(user)) {
                //用户信息错误
                logger.error("支付宝回调用户信息错误，paramsJson = " + paramsJson);
                return "fail";
            }

            //支付宝交易号
            String tradeNo = params.get("trade_no");
            //交易状态
            String tradeStatus = params.get("trade_status");

            // 调用SDK验证签名
//            String aliPayPublicKey2 = systemConfigService.getValueByKey(AlipayConfig.ALIPAY_PUBLIC_KEY_2);
            String publicKey = systemConfigService.getValueByKey(AlipayConfig.ALIPAY_PUBLIC_KEY);
            boolean signVerified = AlipaySignature.rsaCheckV1(params, publicKey, AlipayConfig.CHARSET, "RSA2");
            if (signVerified) {//验证成功
                if (tradeStatus.equals("TRADE_FINISHED") || tradeStatus.equals("TRADE_SUCCESS")) {//交易成功
                    // 添加支付成功redis队列
                    Boolean execute = transactionTemplate.execute(e -> {
                        order.setPaid(true);
                        order.setPayTime(CrmebDateUtil.nowDateTime());
                        order.setStatus(OrderConstants.ORDER_STATUS_WAIT_SHIPPING);
                        orderService.updateById(order);
                        return Boolean.TRUE;
                    });
                    if (!execute) {
                        logger.error("ali pay error : 订单更新失败==》" + outTradeNo);
                        return "fail";
                    }
                    asyncService.orderPaySuccessSplit(order.getOrderNo());
                }
                return "success";
            } else {
                logger.error("支付宝回调签名认证失败，signVerified=false, paramsJson:{}", paramsJson);
                return "fail";
            }
        } catch (AlipayApiException e) {
            logger.error("支付宝回调签名认证失败,paramsJson:{},errorMsg:{}", paramsJson, e.getMessage());
            return "fail";
        }
    }

    /**
     * 保存支付宝回调信息
     *
     * @param params
     */
    private void saveAliPayCallbackInfo(Map<String, String> params) {
        AliPayCallback aliPayCallback = new AliPayCallback();
        aliPayCallback.setNotifyType(params.get("notify_type"));
        aliPayCallback.setNotifyId(params.get("notify_id"));
        aliPayCallback.setAppId(params.get("app_id"));
        aliPayCallback.setCharset(params.get("charset"));
        aliPayCallback.setVersion(params.get("version"));
        aliPayCallback.setSignType(params.get("sign_type"));
        aliPayCallback.setSign(params.get("sign"));
        aliPayCallback.setTradeNo(params.get("trade_no"));
        aliPayCallback.setOutTradeNo(params.get("out_trade_no"));
        aliPayCallback.setTradeStatus(params.get("trade_status"));
        aliPayCallback.setTotalAmount(new BigDecimal(params.get("total_amount")));
        aliPayCallback.setReceiptAmount(ObjectUtil.isNotNull(params.get("receipt_amount")) ? new BigDecimal(params.get("receipt_amount")) : null);
        aliPayCallback.setRefundFee(ObjectUtil.isNotNull(params.get("refund_fee")) ? new BigDecimal(params.get("refund_fee")) : null);
        aliPayCallback.setSubject(params.get("subject"));
        aliPayCallback.setBody(Optional.ofNullable(params.get("body")).orElse(""));
        aliPayCallback.setPassbackParams(params.get("passback_params"));
        aliPayCallback.setNotifyTime(DateUtil.parse(params.get("notify_time")));
        aliPayCallback.setAddTime(DateUtil.date());
        aliPayCallbackService.save(aliPayCallback);
    }

    /**
     * 将request中的参数转换成Map
     *
     * @param request
     * @return
     */
    private Map<String, String> convertRequestParamsToMap(HttpServletRequest request) {
        Map<String, String> retMap = new HashMap<String, String>();
        Map requestParams = request.getParameterMap();
        for (Iterator iter = requestParams.keySet().iterator(); iter.hasNext(); ) {
            String name = (String) iter.next();
            String[] values = (String[]) requestParams.get(name);
            String valueStr = "";
            for (int i = 0; i < values.length; i++) {
                valueStr = (i == values.length - 1) ? valueStr + values[i]
                        : valueStr + values[i] + ",";
            }
            //乱码解决，这段代码在出现乱码时使用。如果mysign和sign不相等也可以使用这段代码转化
            //valueStr = new String(valueStr.getBytes("ISO-8859-1"), "gbk");
            retMap.put(name, valueStr);
        }
        return retMap;
    }

    /**
     * 微信退款回调
     *
     * @param xmlInfo 微信回调json
     * @return MyRecord
     */
    @Override
    public String weChatRefund(String xmlInfo) {
        MyRecord notifyRecord = new MyRecord();
        MyRecord refundRecord = refundNotify(xmlInfo, notifyRecord);
        if (refundRecord.getStr("status").equals("fail")) {
            logger.error("微信退款回调失败==>" + refundRecord.getColumns() + ", rawData==>" + xmlInfo + ", data==>" + notifyRecord);
            return refundRecord.getStr("returnXml");
        }

        if (!refundRecord.getBoolean("isRefund")) {
            logger.error("微信退款回调失败==>" + refundRecord.getColumns() + ", rawData==>" + xmlInfo + ", data==>" + notifyRecord);
            return refundRecord.getStr("returnXml");
        }
        String outRefundNo = notifyRecord.getStr("out_refund_no");
        List<RefundOrder> refundOrderList = refundOrderService.findByOutRefundNo(outRefundNo);
        if (CollUtil.isEmpty(refundOrderList)) {
            logger.error("微信退款订单查询失败==>" + refundRecord.getColumns() + ", rawData==>" + xmlInfo + ", data==>" + notifyRecord);
            return refundRecord.getStr("returnXml");
        }
        RefundOrder refundOrder = refundOrderList.get(0);
        if (refundOrder.getRefundStatus().equals(OrderConstants.MERCHANT_REFUND_ORDER_STATUS_REFUND)) {
            logger.error("微信退款订单已确认成功==>" + refundRecord.getColumns() + ", rawData==>" + xmlInfo + ", data==>" + notifyRecord);
            return refundRecord.getStr("returnXml");
        }
        refundOrderList.forEach(r -> {
            r.setRefundStatus(OrderConstants.MERCHANT_REFUND_ORDER_STATUS_REFUND);
        });
        boolean update = refundOrderService.updateBatchById(refundOrderList);
        if (update) {
            // 退款task
            refundOrderList.forEach(ro -> {
                redisUtil.lPush(TaskConstants.ORDER_TASK_REDIS_KEY_AFTER_REFUND_BY_USER, ro.getRefundOrderNo());
            });
        } else {
            logger.error("微信退款订单更新失败==>" + refundRecord.getColumns() + ", rawData==>" + xmlInfo + ", data==>" + notifyRecord);
        }
        return refundRecord.getStr("returnXml");
    }

    /**
     * 支付订单回调通知
     *
     * @return MyRecord
     */
    private MyRecord refundNotify(String xmlInfo, MyRecord notifyRecord) {
        MyRecord refundRecord = new MyRecord();
        refundRecord.set("status", "fail");
        StringBuilder sb = new StringBuilder();
        sb.append("<xml>");
        if (StrUtil.isBlank(xmlInfo)) {
            sb.append("<return_code><![CDATA[FAIL]]></return_code>");
            sb.append("<return_msg><![CDATA[xmlInfo is blank]]></return_msg>");
            sb.append("</xml>");
            logger.error("wechat refund callback error : " + sb);
            return refundRecord.set("returnXml", sb.toString()).set("errMsg", "xmlInfo is blank");
        }

        Map<String, String> respMap;
        try {
            respMap = WxPayUtil.xmlToMap(xmlInfo);
        } catch (Exception e) {
            sb.append("<return_code><![CDATA[FAIL]]></return_code>");
            sb.append("<return_msg><![CDATA[").append(e.getMessage()).append("]]></return_msg>");
            sb.append("</xml>");
            logger.error("wechat refund callback error : " + e.getMessage());
            return refundRecord.set("returnXml", sb.toString()).set("errMsg", e.getMessage());
        }

        notifyRecord.setColums(_strMap2ObjMap(respMap));
        // 这里的可以应该根据小程序还是公众号区分
        String return_code = respMap.get("return_code");
        if (return_code.equals(Constants.SUCCESS)) {
            String appid = respMap.get("appid");
            String signKey = getSignKey(appid);
            // 解码加密信息
            String reqInfo = respMap.get("req_info");
            System.out.println("encodeReqInfo==>" + reqInfo);
            try {
                String decodeInfo = decryptToStr(reqInfo, signKey);
                Map<String, String> infoMap = WxPayUtil.xmlToMap(decodeInfo);
                notifyRecord.setColums(_strMap2ObjMap(infoMap));

                String refund_status = infoMap.get("refund_status");
                refundRecord.set("isRefund", refund_status.equals(Constants.SUCCESS));
            } catch (Exception e) {
                refundRecord.set("isRefund", false);
                logger.error("微信退款回调异常，e==》" + e.getMessage());
            }
        } else {
            notifyRecord.set("return_msg", respMap.get("return_msg"));
            refundRecord.set("isRefund", false);
        }
        sb.append("<return_code><![CDATA[SUCCESS]]></return_code>");
        sb.append("<return_msg><![CDATA[OK]]></return_msg>");
        sb.append("</xml>");
        return refundRecord.set("returnXml", sb.toString()).set("status", "ok");
    }

    private String getSignKey(String appid) {
        String publicAppid = systemConfigService.getValueByKey(WeChatConstants.WECHAT_PUBLIC_APPID);
        String miniAppid = systemConfigService.getValueByKey(WeChatConstants.WECHAT_MINI_APPID);
        String appAppid = systemConfigService.getValueByKey(WeChatConstants.WECHAT_APP_APPID);
        String signKey = "";
        if (StrUtil.isBlank(publicAppid) && StrUtil.isBlank(miniAppid) && StrUtil.isBlank(appAppid)) {
            throw new CrmebException("pay_weixin_appid或pay_routine_appid不能都为空");
        }
        if (StrUtil.isNotBlank(publicAppid) && appid.equals(publicAppid)) {
            signKey = systemConfigService.getValueByKeyException(WeChatConstants.WECHAT_PAY_PUBLIC_KEY);
        }
        if (StrUtil.isNotBlank(miniAppid) && appid.equals(miniAppid)) {
            signKey = systemConfigService.getValueByKeyException(WeChatConstants.WECHAT_PAY_MINI_KEY);
        }
        if (StrUtil.isNotBlank(appAppid) && appid.equals(appAppid)) {
            signKey = systemConfigService.getValueByKeyException(WeChatConstants.WECHAT_PAY_APP_KEY);
        }
        return signKey;
    }

    /**
     * java自带的是PKCS5Padding填充，不支持PKCS7Padding填充。
     * 通过BouncyCastle组件来让java里面支持PKCS7Padding填充
     * 在加解密之前加上：Security.addProvider(new BouncyCastleProvider())，
     * 并给Cipher.getInstance方法传入参数来指定Java使用这个库里的加/解密算法。
     */
    public static String decryptToStr(String reqInfo, String signKey) throws Exception {
        Security.addProvider(new BouncyCastleProvider());
//        byte[] decodeReqInfo = Base64.decode(reqInfo);
        byte[] decodeReqInfo = base64DecodeJustForWxPay(reqInfo).getBytes(StandardCharsets.ISO_8859_1);
        SecretKeySpec key = new SecretKeySpec(SecureUtil.md5(signKey).toLowerCase().getBytes(), "AES");
        Cipher cipher;
        cipher = Cipher.getInstance("AES/ECB/PKCS7Padding");
        cipher.init(Cipher.DECRYPT_MODE, key);
        return new String(cipher.doFinal(decodeReqInfo), StandardCharsets.UTF_8);
    }

    private static final List<String> list = new ArrayList<>();

    static {
        list.add("total_fee");
        list.add("cash_fee");
        list.add("coupon_fee");
        list.add("coupon_count");
        list.add("refund_fee");
        list.add("settlement_refund_fee");
        list.add("settlement_total_fee");
        list.add("cash_refund_fee");
        list.add("coupon_refund_fee");
        list.add("coupon_refund_count");
    }

    private Map<String, Object> _strMap2ObjMap(Map<String, String> params) {
        Map<String, Object> map = new HashMap<>();
        for (Map.Entry<String, String> entry : params.entrySet()) {
            if (list.contains(entry.getKey())) {
                try {
                    map.put(entry.getKey(), Integer.parseInt(entry.getValue()));
                } catch (NumberFormatException e) {
                    map.put(entry.getKey(), 0);
                    logger.error("字段格式错误，key==》" + entry.getKey() + ", value==》" + entry.getValue());
                }
                continue;
            }

            map.put(entry.getKey(), entry.getValue());
        }
        return map;
    }

    /**
     * 仅仅为微信解析密文使用
     *
     * @param source 待解析密文
     * @return 结果
     */
    public static String base64DecodeJustForWxPay(final String source) {
        String result = "";
        final Base64.Decoder decoder = Base64.getDecoder();
        result = new String(decoder.decode(source), StandardCharsets.ISO_8859_1);
        return result;
    }
}
