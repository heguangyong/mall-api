package com.zbkj.service.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.domain.*;
import com.alipay.api.request.*;
import com.alipay.api.response.*;
import com.zbkj.common.constants.AlipayConfig;
import com.zbkj.common.constants.PayConstants;
import com.zbkj.common.constants.SysConfigConstants;
import com.zbkj.common.exception.CrmebException;
import com.zbkj.common.model.alipay.AliPayInfo;
import com.zbkj.common.result.PayResultCode;
import com.zbkj.common.vo.MyRecord;
import com.zbkj.service.service.AliPayInfoService;
import com.zbkj.service.service.AliPayService;
import com.zbkj.service.service.SystemConfigService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * 支付宝支付 Service impl
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
public class AliPayServiceImpl implements AliPayService {

    private static final Logger logger = LoggerFactory.getLogger(AliPayServiceImpl.class);

    @Autowired
    private SystemConfigService systemConfigService;
    @Autowired
    private AliPayInfoService aliPayInfoService;

    /**
     * 支付宝退款
     *
     * @param outTradeNo             支付宝交易号
     * @param refundOrderNo          退款单号
     * @param refundReasonWapExplain 退款说明
     * @param refundPrice            退款金额
     * @return Boolean
     */
    @Override
    public Boolean refund(String outTradeNo, String refundOrderNo, String refundReasonWapExplain, BigDecimal refundPrice) {

        //商户订单号和支付宝交易号不能同时为空。 trade_no、  out_trade_no如果同时存在优先取trade_no
        //商户订单号，和支付宝交易号二选一
        String out_trade_no = outTradeNo;
        //支付宝交易号，和商户订单号二选一
//        String trade_no = outTradeNo;
        //退款金额，不能大于订单总金额
        String refund_amount = refundPrice.toString();
        //退款的原因说明
        String refund_reason = refundReasonWapExplain;
        //标识一次退款请求，同一笔交易多次退款需要保证唯一，如需部分退款，则此参数必传。
        String out_request_no = refundOrderNo;
        /**********************/
        // SDK 公共请求类，包含公共请求参数，以及封装了签名与验签，开发者无需关注签名与验签
        MyRecord aliPayRecord = getAliPayRecord();
        String aliPayAppid = aliPayRecord.getStr(AlipayConfig.APPID);
        String aliPayPrivateKey = aliPayRecord.getStr(AlipayConfig.RSA_PRIVATE_KEY);
        String aliPayPublicKey = aliPayRecord.getStr(AlipayConfig.ALIPAY_PUBLIC_KEY);
        AlipayClient client = new DefaultAlipayClient(AlipayConfig.URL, aliPayAppid, aliPayPrivateKey, AlipayConfig.FORMAT, AlipayConfig.CHARSET, aliPayPublicKey, AlipayConfig.SIGNTYPE);
        AlipayTradeRefundRequest alipay_request = new AlipayTradeRefundRequest();

        AlipayTradeRefundModel model = new AlipayTradeRefundModel();
        model.setOutTradeNo(out_trade_no);
        model.setRefundAmount(refund_amount);
        model.setRefundReason(refund_reason);
        model.setOutRequestNo(out_request_no);
        alipay_request.setBizModel(model);
        String apiDomain = systemConfigService.getValueByKeyException(SysConfigConstants.CONFIG_KEY_API_URL);
        alipay_request.setNotifyUrl(apiDomain + PayConstants.ALI_PAY_NOTIFY_API_URI);

        AlipayTradeRefundResponse alipay_response = null;
        try {
            alipay_response = client.execute(alipay_request);
        } catch (AlipayApiException e) {
            logger.error("支付宝退款申请异常，alipay_response = " + alipay_response);
            logger.error(e.getMessage());
            throw new CrmebException("支付宝支付结果异常");
        }
        if ("Y".equals(alipay_response.getFundChange())) {
            return Boolean.TRUE;
        }
        return queryRefund(outTradeNo, refundOrderNo);
    }

    /**
     * 获取支付宝配置
     */
    private MyRecord getAliPayRecord() {
        List<String> list = new ArrayList<>();
        list.add(AlipayConfig.APPID);
        list.add(AlipayConfig.RSA_PRIVATE_KEY);
        list.add(AlipayConfig.ALIPAY_PUBLIC_KEY);
        MyRecord myRecord = systemConfigService.getValuesByKeyList(list);
        if (StrUtil.isBlank(myRecord.getStr(AlipayConfig.APPID))) {
            throw new CrmebException(PayResultCode.ALI_PAY_NOT_CONFIG);
        }
        if (StrUtil.isBlank(myRecord.getStr(AlipayConfig.RSA_PRIVATE_KEY))) {
            throw new CrmebException(PayResultCode.ALI_PAY_NOT_CONFIG);
        }
        if (StrUtil.isBlank(myRecord.getStr(AlipayConfig.ALIPAY_PUBLIC_KEY))) {
            throw new CrmebException(PayResultCode.ALI_PAY_NOT_CONFIG);
        }
        return myRecord;
    }

    /**
     * 支付宝支付
     * @param orderNo 订单号
     * @param price 支付金额
     * @param orderType 订单类型：order - 商品订单，recharge - 充值订单,svip - 付费会员订单
     * @param payChannel 支付渠道：alipayApp - 支付宝app支付, alipay - 支付宝支付
     * @param timeExpire 绝对超时时间，格式为yyyy-MM-dd HH:mm:ss。
     * @return 支付宝调用结果
     */
    @Override
    public String pay(String orderNo, BigDecimal price, String orderType, String payChannel, String timeExpire) {
        if (payChannel.equals(PayConstants.PAY_CHANNEL_ALI_APP_PAY)) {// APP 支付
            return appPay(orderNo, price, orderType, timeExpire);
        }
        if (payChannel.equals(PayConstants.PAY_CHANNEL_ALI_PC_PAY)) {// APP 支付
            return pcPay(orderNo, price, orderType, timeExpire);
        }
        //商户订单号，商户网站订单系统中唯一订单号，必填
        String out_trade_no = orderNo;
        //付款金额，必填
        String total_amount = price.toString();
        //订单名称，必填
        String subject = systemConfigService.getValueByKeyException(SysConfigConstants.CONFIG_KEY_SITE_NAME);
        // 商品描述，可空
        // String body = "用户订购商品个数：1";
        // 该笔订单允许的最晚付款时间，逾期将关闭交易。取值范围：1m～15d。m-分钟，h-小时，d-天，1c-当天（1c-当天的情况下，无论交易何时创建，都在0点关闭）。 该参数数值不接受小数点， 如 1.5h，可转换为 90m。
        String timeout_express = "30m";

        //获得初始化的AlipayClient
        MyRecord aliPayRecord = getAliPayRecord();
        String aliPayAppid = aliPayRecord.getStr(AlipayConfig.APPID);
        String aliPayPrivateKey = aliPayRecord.getStr(AlipayConfig.RSA_PRIVATE_KEY);
        String aliPayPublicKey = aliPayRecord.getStr(AlipayConfig.ALIPAY_PUBLIC_KEY);
        AlipayClient alipayClient = new DefaultAlipayClient(AlipayConfig.URL, aliPayAppid, aliPayPrivateKey, AlipayConfig.FORMAT, AlipayConfig.CHARSET, aliPayPublicKey, AlipayConfig.SIGNTYPE);

        String returnUrl = "";
        String quitUrl = "";
        String encode = "type=";
        switch (orderType) {
            case "order":
                returnUrl = systemConfigService.getValueByKey(AlipayConfig.return_url);
                quitUrl = systemConfigService.getValueByKey(AlipayConfig.quit_url);
                encode = encode.concat(PayConstants.PAY_SERVICE_TYPE_ORDER);
                break;
            case "recharge":
                returnUrl = systemConfigService.getValueByKey(AlipayConfig.recharge_return_url);
                quitUrl = systemConfigService.getValueByKey(AlipayConfig.recharge_quit_url);
                encode = encode.concat(PayConstants.PAY_SERVICE_TYPE_RECHARGE);
                break;
            case "svip":
                returnUrl = systemConfigService.getValueByKey(AlipayConfig.svip_return_url);
                quitUrl = systemConfigService.getValueByKey(AlipayConfig.svip_quit_url);
                encode = encode.concat(PayConstants.PAY_SERVICE_TYPE_SVIP);
                break;
        }
        //设置请求参数
        AlipayTradeWapPayRequest alipayRequest = new AlipayTradeWapPayRequest();
        alipayRequest.setReturnUrl(returnUrl);
        String apiDomain = systemConfigService.getValueByKeyException(SysConfigConstants.CONFIG_KEY_API_URL);
        alipayRequest.setNotifyUrl(apiDomain + PayConstants.ALI_PAY_NOTIFY_API_URI);

        AlipayTradeWapPayModel model = new AlipayTradeWapPayModel();
        model.setOutTradeNo(out_trade_no);
        model.setSubject(subject);
        model.setTotalAmount(total_amount);
        model.setTimeoutExpress(timeout_express);
        model.setProductCode("QUICK_WAP_PAY");
        model.setQuitUrl(quitUrl);
        if (StrUtil.isNotBlank(timeExpire)) {
            model.setTimeExpire(timeExpire);
        }

        try {
            encode = URLEncoder.encode(encode, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            throw new CrmebException("支付宝参数UrlEncode异常");
        }
        model.setPassbackParams(encode);

        alipayRequest.setBizModel(model);
        //请求
        String result;
        AlipayTradeWapPayResponse response;
        try {
            response = alipayClient.pageExecute(alipayRequest);
            result = response.getBody();
        } catch (AlipayApiException e) {
            logger.error("支付宝订单生成失败," + e.getErrMsg());
            throw new CrmebException(e.getErrMsg());
        }

        // 保存支付宝订单信息
        AliPayInfo aliPayInfo = new AliPayInfo();
        aliPayInfo.setAppId(aliPayAppid);
        aliPayInfo.setTimestamp(DateUtil.now());
        aliPayInfo.setBody("");
        aliPayInfo.setSubject(subject);
        aliPayInfo.setOutTradeNo(out_trade_no);
        aliPayInfo.setTimeoutExpress(timeout_express);
        aliPayInfo.setTotalAmount(total_amount);
        aliPayInfo.setPassbackParams(model.getPassbackParams());
        aliPayInfo.setNotifyUrl(alipayRequest.getNotifyUrl());
        aliPayInfo.setCode(Optional.ofNullable(response.getCode()).orElse(""));
        aliPayInfo.setMsg(Optional.ofNullable(response.getMsg()).orElse(""));
        aliPayInfo.setSubCode(Optional.ofNullable(response.getSubCode()).orElse(""));
        aliPayInfo.setSubMsg(Optional.ofNullable(response.getSubMsg()).orElse(""));
        aliPayInfo.setTradeNo(Optional.ofNullable(response.getTradeNo()).orElse(""));
        aliPayInfo.setSellerId(Optional.ofNullable(response.getSellerId()).orElse(""));
        aliPayInfoService.save(aliPayInfo);
        return result;
    }

    /**
     * 支付宝App支付
     * @param orderNo 订单号
     * @param price 支付金额
     * @param orderType 订单类型：order - 商品订单，recharge - 充值订单
     * @param timeExpire 绝对超时时间，格式为yyyy-MM-dd HH:mm:ss。
     */
    private String appPay(String orderNo, BigDecimal price, String orderType, String timeExpire) {
        //商户订单号，商户网站订单系统中唯一订单号，必填
        String out_trade_no = orderNo;
        //付款金额，必填
        String total_amount = price.toString();
        //订单名称，必填
        String subject = systemConfigService.getValueByKeyException(SysConfigConstants.CONFIG_KEY_SITE_NAME);
        //商品描述，可空
//            String body = "用户订购商品个数：1";

        // 该笔订单允许的最晚付款时间，逾期将关闭交易。取值范围：1m～15d。m-分钟，h-小时，d-天，1c-当天（1c-当天的情况下，无论交易何时创建，都在0点关闭）。 该参数数值不接受小数点， 如 1.5h，可转换为 90m。
        String timeout_express = "30m";

        //获得初始化的AlipayClient
        MyRecord aliPayRecord = getAliPayRecord();
        String aliPayAppid = aliPayRecord.getStr(AlipayConfig.APPID);
        String aliPayPrivateKey = aliPayRecord.getStr(AlipayConfig.RSA_PRIVATE_KEY);
        String aliPayPublicKey = aliPayRecord.getStr(AlipayConfig.ALIPAY_PUBLIC_KEY);
        AlipayClient alipayClient = new DefaultAlipayClient(AlipayConfig.URL, aliPayAppid, aliPayPrivateKey, AlipayConfig.FORMAT, AlipayConfig.CHARSET, aliPayPublicKey, AlipayConfig.SIGNTYPE);
        //实例化具体API对应的request类,类名称和接口名称对应,当前调用接口名称：alipay.trade.app.pay
        AlipayTradeAppPayRequest request = new AlipayTradeAppPayRequest();
        //SDK已经封装掉了公共参数，这里只需要传入业务参数。以下方法为sdk的model入参方式(model和biz_content同时存在的情况下取biz_content)。
        AlipayTradeAppPayModel model = new AlipayTradeAppPayModel();
        model.setSubject(subject);
        model.setOutTradeNo(out_trade_no);
        model.setTimeoutExpress(timeout_express);
        model.setTotalAmount(total_amount);
        model.setProductCode("QUICK_MSECURITY_PAY");

        String encode = "type=";
        switch (orderType) {
            case "order":
                encode = encode.concat(PayConstants.PAY_SERVICE_TYPE_ORDER);
                break;
            case "recharge":
                encode = encode.concat(PayConstants.PAY_SERVICE_TYPE_RECHARGE);
                break;
            case "svip":
                encode = encode.concat(PayConstants.PAY_SERVICE_TYPE_SVIP);
                break;
        }
        try {
            encode = URLEncoder.encode(encode, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            throw new CrmebException("支付宝参数UrlEncode异常");
        }
        model.setPassbackParams(encode);

        if (StrUtil.isNotBlank(timeExpire)) {
            model.setTimeExpire(timeExpire);
        }

        request.setBizModel(model);
        String apiDomain = systemConfigService.getValueByKeyException(SysConfigConstants.CONFIG_KEY_API_URL);
        request.setNotifyUrl(apiDomain + PayConstants.ALI_PAY_NOTIFY_API_URI);

        //请求
        String result;
        AlipayTradeAppPayResponse response;
        try {
            //这里和普通的接口调用不同，使用的是sdkExecute
            response = alipayClient.sdkExecute(request);
            result = response.getBody();
        } catch (AlipayApiException e) {
            logger.error("生成支付宝app支付请求异常," + e.getErrMsg());
            throw new CrmebException(e.getErrMsg());
        }
        // 保存支付宝订单信息
        AliPayInfo aliPayInfo = new AliPayInfo();
        aliPayInfo.setAppId(aliPayAppid);
        aliPayInfo.setTimestamp(DateUtil.now());
        aliPayInfo.setBody("");
        aliPayInfo.setSubject(subject);
        aliPayInfo.setOutTradeNo(out_trade_no);
        aliPayInfo.setTimeoutExpress(timeout_express);
        aliPayInfo.setTotalAmount(total_amount);
        aliPayInfo.setPassbackParams(model.getPassbackParams());
        aliPayInfo.setNotifyUrl(request.getNotifyUrl());
        aliPayInfo.setCode(Optional.ofNullable(response.getCode()).orElse(""));
        aliPayInfo.setMsg(Optional.ofNullable(response.getMsg()).orElse(""));
        aliPayInfo.setSubCode(Optional.ofNullable(response.getSubCode()).orElse(""));
        aliPayInfo.setSubMsg(Optional.ofNullable(response.getSubMsg()).orElse(""));
        aliPayInfo.setTradeNo(Optional.ofNullable(response.getTradeNo()).orElse(""));
        aliPayInfo.setSellerId(Optional.ofNullable(response.getSellerId()).orElse(""));
        aliPayInfoService.save(aliPayInfo);
        return result;


    }

    /**
     * 支付宝PC支付
     * @param orderNo 订单号
     * @param price 支付金额
     * @param orderType 订单类型：order - 商品订单，recharge - 充值订单
     * @param timeExpire 绝对超时时间，格式为yyyy-MM-dd HH:mm:ss。
     */
    private String pcPay(String orderNo, BigDecimal price, String orderType, String timeExpire) {
        //商户订单号，商户网站订单系统中唯一订单号，必填
        String out_trade_no = orderNo;
        //付款金额，必填
        String total_amount = price.toString();
        //订单名称，必填
        String subject = systemConfigService.getValueByKeyException(SysConfigConstants.CONFIG_KEY_SITE_NAME);
        //商品描述，可空
        // String body = "用户订购商品个数：1";

        // 该笔订单允许的最晚付款时间，逾期将关闭交易。取值范围：1m～15d。m-分钟，h-小时，d-天，1c-当天（1c-当天的情况下，无论交易何时创建，都在0点关闭）。 该参数数值不接受小数点， 如 1.5h，可转换为 90m。
        String timeout_express = "30m";

        //获得初始化的AlipayClient
        MyRecord aliPayRecord = getAliPayRecord();
        String aliPayAppid = aliPayRecord.getStr(AlipayConfig.APPID);
        String aliPayPrivateKey = aliPayRecord.getStr(AlipayConfig.RSA_PRIVATE_KEY);
        String aliPayPublicKey = aliPayRecord.getStr(AlipayConfig.ALIPAY_PUBLIC_KEY);
        AlipayClient alipayClient = new DefaultAlipayClient(AlipayConfig.URL, aliPayAppid, aliPayPrivateKey, AlipayConfig.FORMAT, AlipayConfig.CHARSET, aliPayPublicKey, AlipayConfig.SIGNTYPE);

        String returnUrl = "";
        String encode = "type=";
        switch (orderType) {
            case "order":
                returnUrl = systemConfigService.getValueByKey(AlipayConfig.return_url);
                encode = encode.concat(PayConstants.PAY_SERVICE_TYPE_ORDER);
                break;
            case "recharge":
                returnUrl = systemConfigService.getValueByKey(AlipayConfig.recharge_return_url);
                encode = encode.concat(PayConstants.PAY_SERVICE_TYPE_RECHARGE);
                break;
            case "svip":
                returnUrl = systemConfigService.getValueByKey(AlipayConfig.svip_return_url);
                encode = encode.concat(PayConstants.PAY_SERVICE_TYPE_SVIP);
                break;
        }

        AlipayTradePrecreateRequest payRequest = new AlipayTradePrecreateRequest();
        payRequest.setReturnUrl(returnUrl);
        String apiDomain = systemConfigService.getValueByKeyException(SysConfigConstants.CONFIG_KEY_API_URL);
        payRequest.setNotifyUrl(apiDomain + PayConstants.ALI_PAY_NOTIFY_API_URI);

        AlipayTradePrecreateModel model = new AlipayTradePrecreateModel();
        model.setSubject(subject);
        model.setOutTradeNo(out_trade_no);
        model.setTimeoutExpress(timeout_express);
        model.setTotalAmount(total_amount);

        try {
            encode = URLEncoder.encode(encode, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            throw new CrmebException("支付宝参数UrlEncode异常");
        }
        model.setPassbackParams(encode);

        payRequest.setBizModel(model);

        //请求
        String result;
        AlipayTradePrecreateResponse response;
        try {
            //这里和普通的接口调用不同，使用的是sdkExecute
            response = alipayClient.execute(payRequest);
        } catch (Exception e) {
            logger.error("生成支付宝PC支付请求异常," + e);
            throw new CrmebException("生成支付宝PC支付请求异常");
        }
        if (!response.isSuccess()) {
            logger.error("生成支付宝PC支付请求失败");
            throw new CrmebException("生成支付宝PC支付请求失败");
        }
        result = response.getQrCode();
        // 保存支付宝订单信息
        AliPayInfo aliPayInfo = new AliPayInfo();
        aliPayInfo.setAppId(aliPayAppid);
        aliPayInfo.setTimestamp(DateUtil.now());
        aliPayInfo.setBody("");
        aliPayInfo.setSubject(subject);
        aliPayInfo.setOutTradeNo(out_trade_no);
        aliPayInfo.setTimeoutExpress(timeout_express);
        aliPayInfo.setTotalAmount(total_amount);
        aliPayInfo.setPassbackParams(model.getPassbackParams());
        aliPayInfo.setNotifyUrl(payRequest.getNotifyUrl());
        aliPayInfo.setCode(Optional.ofNullable(response.getCode()).orElse(""));
        aliPayInfo.setMsg(Optional.ofNullable(response.getMsg()).orElse(""));
        aliPayInfo.setSubCode(Optional.ofNullable(response.getSubCode()).orElse(""));
        aliPayInfo.setSubMsg(Optional.ofNullable(response.getSubMsg()).orElse(""));
        aliPayInfo.setTradeNo("");
        aliPayInfo.setSellerId("");
        aliPayInfoService.save(aliPayInfo);
        return result;
    }

    /**
     * 查询退款
     * @param refundOrderNo 订单编号
     */
    @Override
    public Boolean queryRefund(String outTradeNo, String refundOrderNo) {

        //商户订单号和支付宝交易号不能同时为空。 trade_no、  out_trade_no如果同时存在优先取trade_no
        //商户订单号，和支付宝交易号二选一
        String out_trade_no = outTradeNo;
        //支付宝交易号，和商户订单号二选一
        //        String trade_no = new String(request.getParameter("WIDtrade_no").getBytes("ISO-8859-1"),"UTF-8");
        //请求退款接口时，传入的退款请求号，如果在退款请求时未传入，则该值为创建交易时的外部交易号
        //        String out_request_no = new String(request.getParameter("WIDout_request_no").getBytes("ISO-8859-1"),"UTF-8");
        String out_request_no = refundOrderNo;
        /**********************/
        // SDK 公共请求类，包含公共请求参数，以及封装了签名与验签，开发者无需关注签名与验签
        MyRecord aliPayRecord = getAliPayRecord();
        String aliPayAppid = aliPayRecord.getStr(AlipayConfig.APPID);
        String aliPayPrivateKey = aliPayRecord.getStr(AlipayConfig.RSA_PRIVATE_KEY);
        String aliPayPublicKey = aliPayRecord.getStr(AlipayConfig.ALIPAY_PUBLIC_KEY);
        AlipayClient client = new DefaultAlipayClient(AlipayConfig.URL, aliPayAppid, aliPayPrivateKey, AlipayConfig.FORMAT, AlipayConfig.CHARSET, aliPayPublicKey, AlipayConfig.SIGNTYPE);

        AlipayTradeFastpayRefundQueryRequest alipay_request = new AlipayTradeFastpayRefundQueryRequest();

        AlipayTradeFastpayRefundQueryModel model=new AlipayTradeFastpayRefundQueryModel();
        model.setOutTradeNo(out_trade_no);
        model.setOutRequestNo(out_request_no);
        alipay_request.setBizModel(model);
        AlipayTradeFastpayRefundQueryResponse alipay_response= null;
        try {
            alipay_response = client.execute(alipay_request);
        } catch (AlipayApiException e) {
            logger.error("支付宝退款查询异常，alipay_response = " + alipay_response);
            throw new CrmebException("支付宝退款查询异常");
        }
        if (alipay_response.getRefundStatus().equals("REFUND_SUCCESS")) {
            return Boolean.TRUE;
        }
        if (alipay_response.getRefundStatus().equals("REFUND_PROCESSING")) {
            logger.error("支付宝退款查询：退款处理中, outTradeNo = {}", outTradeNo);
            return Boolean.FALSE;
        }
        if (alipay_response.getRefundStatus().equals("REFUND_FAIL")) {
            logger.error("支付宝退款查询：支付宝退款失败, outTradeNo = {}", outTradeNo);
            return Boolean.FALSE;
        }
        logger.error("支付宝退款查询：异常错误");
        return Boolean.FALSE;
    }
}
