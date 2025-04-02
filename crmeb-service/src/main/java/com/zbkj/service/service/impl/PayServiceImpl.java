package com.zbkj.service.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.domain.AlipayTradeQueryModel;
import com.alipay.api.request.AlipayTradeQueryRequest;
import com.alipay.api.response.AlipayTradeQueryResponse;
import com.zbkj.common.config.CrmebConfig;
import com.zbkj.common.constants.*;
import com.zbkj.common.exception.CrmebException;
import com.zbkj.common.model.admin.SystemAdmin;
import com.zbkj.common.model.alipay.AliPayInfo;
import com.zbkj.common.model.bill.Bill;
import com.zbkj.common.model.bill.MerchantBill;
import com.zbkj.common.model.coupon.CouponUser;
import com.zbkj.common.model.member.PaidMemberOrder;
import com.zbkj.common.model.merchant.Merchant;
import com.zbkj.common.model.merchant.MerchantBalanceRecord;
import com.zbkj.common.model.order.*;
import com.zbkj.common.model.product.ProductCoupon;
import com.zbkj.common.model.system.SystemNotification;
import com.zbkj.common.model.user.*;
import com.zbkj.common.model.wechat.WechatPayInfo;
import com.zbkj.common.model.wechat.video.PayComponentProduct;
import com.zbkj.common.request.OrderPayRequest;
import com.zbkj.common.request.WechatPaymentQueryRequest;
import com.zbkj.common.response.CashierInfoResponse;
import com.zbkj.common.response.OrderPayResultResponse;
import com.zbkj.common.response.PayConfigResponse;
import com.zbkj.common.result.CommonResultCode;
import com.zbkj.common.result.MemberResultCode;
import com.zbkj.common.result.OrderResultCode;
import com.zbkj.common.result.UserResultCode;
import com.zbkj.common.utils.*;
import com.zbkj.common.vo.*;
import com.zbkj.common.vo.wxvedioshop.ShopOrderAddResultVo;
import com.zbkj.common.vo.wxvedioshop.order.*;
import com.zbkj.service.service.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLDecoder;
import java.util.*;
import java.util.stream.Collectors;

/**
 * PayServiceImpl 接口实现
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
public class PayServiceImpl implements PayService {

    private static final Logger logger = LoggerFactory.getLogger(PayServiceImpl.class);

    @Autowired
    private SystemConfigService systemConfigService;
    @Autowired
    private OrderService orderService;
    @Autowired
    private UserService userService;
    @Autowired
    private TransactionTemplate transactionTemplate;
    @Autowired
    private UserTokenService userTokenService;
    @Autowired
    private WechatService wechatService;
    @Autowired
    private WechatPayInfoService wechatPayInfoService;
    @Autowired
    private AliPayService aliPayService;
    @Autowired
    private MerchantOrderService merchantOrderService;
    @Autowired
    private OrderStatusService orderStatusService;
    @Autowired
    private UserIntegralRecordService userIntegralRecordService;
    @Autowired
    private SystemNotificationService systemNotificationService;
    @Autowired
    private SmsService smsService;
    @Autowired
    private TemplateMessageService templateMessageService;
    @Autowired
    private OrderDetailService orderDetailService;
    @Autowired
    private ProductCouponService productCouponService;
    @Autowired
    private CouponUserService couponUserService;
    @Autowired
    private CouponService couponService;
    @Autowired
    private UserBrokerageRecordService userBrokerageRecordService;
    @Autowired
    private UserBalanceRecordService userBalanceRecordService;
    @Autowired
    private MerchantService merchantService;
    @Autowired
    private MerchantBillService merchantBillService;
    @Autowired
    private OrderProfitSharingService orderProfitSharingService;
    @Autowired
    private BillService billService;
    @Autowired
    private WechatVideoOrderService wechatVideoOrderService;
    @Autowired
    private AsyncService asyncService;
    @Autowired
    private PayComponentOrderService payComponentOrderService;
    @Autowired
    private PayComponentProductService payComponentProductService;
    @Autowired
    private AliPayInfoService aliPayInfoService;
    @Autowired
    private RechargeOrderService rechargeOrderService;
    @Autowired
    private MerchantBalanceRecordService merchantBalanceRecordService;
    @Autowired
    private MerchantPrintService merchantPrintService;
    @Autowired
    private CrmebConfig crmebConfig;
    @Autowired
    private SystemAdminService systemAdminService;
    @Autowired
    private PaidMemberOrderService paidMemberOrderService;
    @Autowired
    private RedisUtil redisUtil;

    /**
     * 获取支付配置
     */
    @Override
    public PayConfigResponse getPayConfig() {
        String payWxOpen = systemConfigService.getValueByKey(SysConfigConstants.CONFIG_PAY_WECHAT_OPEN);
        String yuePayStatus = systemConfigService.getValueByKey(SysConfigConstants.CONFIG_YUE_PAY_STATUS);
        String aliPayStatus = systemConfigService.getValueByKey(SysConfigConstants.CONFIG_ALI_PAY_STATUS);
        PayConfigResponse response = new PayConfigResponse();
        response.setYuePayStatus(Constants.CONFIG_FORM_SWITCH_OPEN.equals(yuePayStatus));
        response.setPayWechatOpen(Constants.CONFIG_FORM_SWITCH_OPEN.equals(payWxOpen));
        response.setAliPayStatus(Constants.CONFIG_FORM_SWITCH_OPEN.equals(aliPayStatus));
        if (Constants.CONFIG_FORM_SWITCH_OPEN.equals(yuePayStatus)) {
            User user = userService.getInfo();
            response.setUserBalance(user.getNowMoney());
        }
        return response;
    }

    /**
     * 订单支付
     *
     * @param orderPayRequest 订单支付参数
     * @return OrderPayResultResponse
     */
    @Override
    public OrderPayResultResponse payment(OrderPayRequest orderPayRequest) {
        Order order = orderService.getByOrderNo(orderPayRequest.getOrderNo());
        if (order.getCancelStatus() > OrderConstants.ORDER_CANCEL_STATUS_NORMAL) {
            throw new CrmebException(OrderResultCode.ORDER_CANCEL);
        }
        if (order.getPaid()) {
            throw new CrmebException(OrderResultCode.ORDER_PAID);
        }
        if (order.getStatus() > OrderConstants.ORDER_STATUS_WAIT_PAY) {
            throw new CrmebException(OrderResultCode.ORDER_STATUS_ABNORMAL);
        }
        User user = userService.getInfo();
        // 根据支付类型进行校验,更换支付类型
        order.setPayType(orderPayRequest.getPayType());
        order.setPayChannel(orderPayRequest.getPayChannel());
        // 获取过期时间 增加redis缓存的判断 2025-02-12
        DateTime cancelTime;
        if (redisUtil.exists(redisUtil.get(StrUtil.format(RedisConstants.ORDER_EXPIRE_TIME, order.getOrderNo())))) {
            String expireTime = redisUtil.get(StrUtil.format(RedisConstants.ORDER_EXPIRE_TIME, order.getOrderNo()));
            cancelTime = DateUtil.parseDateTime(expireTime);
        } else {
            cancelTime = DateUtil.offset(order.getCreateTime(), DateField.MINUTE, crmebConfig.getOrderCancelTime());
        }
        long between = DateUtil.between(cancelTime, DateUtil.date(), DateUnit.SECOND, false);
        if (between > 0) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "订单已过期");
        }

        // 余额支付
        if (orderPayRequest.getPayType().equals(PayConstants.PAY_TYPE_YUE)) {
            if (user.getNowMoney().compareTo(order.getPayPrice()) < 0) {
                throw new CrmebException(UserResultCode.USER_BALANCE_INSUFFICIENT);
            }
        }

        OrderPayResultResponse response = new OrderPayResultResponse();
        response.setOrderNo(order.getOrderNo());
        response.setPayType(order.getPayType());
        response.setPayChannel(order.getPayChannel());
        if (!order.getSecondType().equals(OrderConstants.ORDER_SECOND_TYPE_INTEGRAL)) {
            if (order.getPayPrice().compareTo(BigDecimal.ZERO) <= 0) {
                throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "支付金额不能低于等于0元");
            }
        }
        // 余额支付
        if (order.getPayType().equals(PayConstants.PAY_TYPE_YUE) || order.getPayPrice().compareTo(BigDecimal.ZERO) <= 0) {
            Boolean yueBoolean = yuePay(order, user);
            response.setStatus(yueBoolean);
            return response;
        }

        // 微信视频号下单 需要额外调用支付参数
        if (order.getPayChannel().equals(PayConstants.PAY_CHANNEL_WECHAT_MINI_VIDEO)) {
            WxPayJsResultVo vo = new WxPayJsResultVo();
            UserToken tokenByUser = userTokenService.getTokenByUserId(user.getId(), UserConstants.USER_TOKEN_TYPE_ROUTINE);
            List<OrderDetail> orderDetailList = orderDetailService.getByOrderNo(order.getOrderNo());
            //  视频号下单都是单品
            PayComponentProduct payComponentProduct = payComponentProductService.getById(orderDetailList.get(0).getProductId());
            //组装商品信息
            ShopOrderDetailAddVo shopOrderDetailAddVo = new ShopOrderDetailAddVo();
            List<ShopOrderProductInfoAddVo> shopOrderProductInfoAddVoList = new ArrayList<>();
            for (OrderDetail orderDetail : orderDetailList) {
                ShopOrderProductInfoAddVo shopOrderProductInfoAddVo = new ShopOrderProductInfoAddVo();
                shopOrderProductInfoAddVo.setOutProductId(orderDetail.getProductId().toString());
                shopOrderProductInfoAddVo.setOutSkuId(orderDetail.getAttrValueId().toString());
                shopOrderProductInfoAddVo.setProductCnt(orderDetail.getPayNum());
                shopOrderProductInfoAddVo.setSalePrice(orderDetail.getPrice().multiply(new BigDecimal("100")).longValue());
                shopOrderProductInfoAddVo.setSkuRealPrice(orderDetail.getPayPrice().multiply(new BigDecimal("100")).longValue());
                shopOrderProductInfoAddVo.setPath(payComponentProduct.getPath());
                shopOrderProductInfoAddVo.setTitle(payComponentProduct.getTitle());
                shopOrderProductInfoAddVo.setHeadImg(payComponentProduct.getHeadImg());
                shopOrderProductInfoAddVoList.add(shopOrderProductInfoAddVo);
            }
            shopOrderDetailAddVo.setProductInfos(shopOrderProductInfoAddVoList);

            // 组装支付方式
            ShopOrderPayInfoAddVo payInfoAddVo = new ShopOrderPayInfoAddVo();
            payInfoAddVo.setPayMethodType(0);
            payInfoAddVo.setPayMethod("微信支付"); // 视频号暂时只有微信支付
            shopOrderDetailAddVo.setPayInfo(payInfoAddVo);

            // 组装支付详细信息
            ShopOrderPriceInfoVo shopOrderPriceInfoVo = new ShopOrderPriceInfoVo();
            shopOrderPriceInfoVo.setOrderPrice(order.getPayPrice().multiply(new BigDecimal("100")).longValue());
            // 视频号商品暂时面部免运费
            shopOrderPriceInfoVo.setFreight(0L);
            shopOrderPriceInfoVo.setDiscountedPrice(0L);
            shopOrderPriceInfoVo.setAdditionalPrice(0L);
            shopOrderPriceInfoVo.setAdditional_remarks(null);
            shopOrderDetailAddVo.setPriceInfo(shopOrderPriceInfoVo);

            // 组装自定义交易组件主体数据
            ShopOrderAddVo shopOrderAddVo = new ShopOrderAddVo();
            shopOrderAddVo.setOrderDetail(shopOrderDetailAddVo);
            shopOrderAddVo.setFund_type(1);
            shopOrderAddVo.setTrace_id(null);
            shopOrderAddVo.setOutOrderId(order.getOrderNo());
            shopOrderAddVo.setOpenid(tokenByUser.getToken());
            shopOrderAddVo.setOutUserId(user.getId());
            shopOrderAddVo.setMerId(order.getMerId());

            // 订单路由地址
            shopOrderAddVo.setPath("/pages/users/order_details/index?orderNo=" + orderPayRequest.getOrderNo());

            // 组装订单信息 视频号都是单品 最多是多量
            List<MerchantOrder> merchantOrderList = merchantOrderService.getByOrderNo(orderPayRequest.getOrderNo());
            if (ObjectUtil.isNull(merchantOrderList) || merchantOrderList.size() == 0) {
                throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "未找到视频号商品订单");
            }
            MerchantOrder CurrentMerchantOrder = merchantOrderList.get(0);
            ShopOrderAddressInfoAddVo shopOrderAddressInfoAddVo = new ShopOrderAddressInfoAddVo();
            shopOrderAddressInfoAddVo.setReceiverName(CurrentMerchantOrder.getRealName());
            shopOrderAddressInfoAddVo.setDetailedAddress(CurrentMerchantOrder.getUserAddress());
            shopOrderAddressInfoAddVo.setTelNumber(CurrentMerchantOrder.getUserPhone());
            shopOrderAddVo.setAddressInfo(shopOrderAddressInfoAddVo);

            // 1: 正常快递, 2: 无需快递, 3: 线下配送, 4: 用户自提，视频号场景目前只支持 1，正常快递
            ShopOrderDeliveryDetailAddVo shopOrderDeliveryDetailAddVo = new ShopOrderDeliveryDetailAddVo();
            shopOrderDeliveryDetailAddVo.setDeliveryType(1);

            shopOrderAddVo.setDeliveryDetail(shopOrderDeliveryDetailAddVo);
            shopOrderAddVo.setExpire_time(WxPayUtil.getCurrentTimestamp().intValue() + 900);
            shopOrderAddVo.setCreateTime(CrmebDateUtil.nowDateTimeStr());
            ShopOrderAddResultVo shopOrderAddResultVo = payComponentOrderService.create(shopOrderAddVo);
            ShopOrderGetPaymentParamsRequestVo videoPaymentRequestVo = new ShopOrderGetPaymentParamsRequestVo(
                    shopOrderAddResultVo.getOutOrderId(), order.getOrderNo(), tokenByUser.getToken()
            );
            ShopOrderGetPaymentParamsRersponseVo shopOrderGetPaymentParamsRersponseVo = wechatVideoOrderService.shopOrderGetPaymentParams(videoPaymentRequestVo);
            response.setStatus(true);
            vo.setTimeStamp(WxPayUtil.getCurrentTimestamp() + "");
            vo.setNonceStr(shopOrderGetPaymentParamsRersponseVo.getNonceStr());
            vo.setPackages(shopOrderGetPaymentParamsRersponseVo.get_package());
            vo.setPaySign(shopOrderGetPaymentParamsRersponseVo.getPaySign());
            vo.setSignType(shopOrderGetPaymentParamsRersponseVo.getSignType());
            response.setJsConfig(vo);
            return response;
        }
        // 微信支付，调用微信预下单，返回拉起微信支付需要的信息
        if (order.getPayType().equals(PayConstants.PAY_TYPE_WE_CHAT)) {
            WxPayJsResultVo vo = wechatPayment(order);
            orderService.updateById(order);
            response.setStatus(true);
            response.setJsConfig(vo);
            return response;
        }
        if (order.getPayType().equals(PayConstants.PAY_TYPE_ALI_PAY)) {
            String result = aliPayment(order);
            order.setOutTradeNo(order.getOrderNo());
            orderService.updateById(order);
            response.setStatus(true);
            response.setAlipayRequest(result);
            return response;
        }
        response.setStatus(false);
        return response;
    }

    /**
     * 查询支付结果
     *
     * @param orderNo 订单编号
     */
    @Override
    public Boolean queryWechatPayResult(String orderNo) {
        Order order = orderService.getByOrderNo(orderNo);
        if (ObjectUtil.isNull(order)) {
            throw new CrmebException(OrderResultCode.ORDER_NOT_EXIST);
        }
        if (order.getCancelStatus() > OrderConstants.ORDER_CANCEL_STATUS_NORMAL) {
            throw new CrmebException(OrderResultCode.ORDER_CANCEL);
        }
        if (!order.getPayType().equals(PayConstants.PAY_TYPE_WE_CHAT)) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "不是微信支付类型订单");
        }
        if (order.getPaid()) {
            return Boolean.TRUE;
        }
        WechatPayInfo wechatPayInfo = wechatPayInfoService.getByNo(order.getOutTradeNo());
        if (ObjectUtil.isNull(wechatPayInfo)) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "未找到对应微信订单");
        }
        Map<String, String> payVo = getWechatQueryPayVo(order.getOutTradeNo(), order.getPayChannel());
        MyRecord myRecord = wechatService.payOrderQuery(payVo);
        if (myRecord.getInt("payStatus") < 1) {
            return Boolean.FALSE;
        }
        wechatPayInfo.setIsSubscribe(myRecord.getStr("is_subscribe"));
        wechatPayInfo.setTradeState(myRecord.getStr("trade_state"));
        wechatPayInfo.setBankType(myRecord.getStr("bank_type"));
        wechatPayInfo.setCashFee(myRecord.getInt("cash_fee"));
        wechatPayInfo.setCouponFee(myRecord.getInt("coupon_fee"));
        wechatPayInfo.setTransactionId(myRecord.getStr("transaction_id"));
        wechatPayInfo.setTimeEnd(myRecord.getStr("time_end"));
        wechatPayInfo.setTradeStateDesc(myRecord.getStr("trade_state_desc"));
        Boolean updatePaid = transactionTemplate.execute(e -> {
            orderService.updatePaid(orderNo);
            wechatPayInfoService.updateById(wechatPayInfo);
            return Boolean.TRUE;
        });
        if (!updatePaid) {
            throw new CrmebException("支付成功更新订单失败");
        }
        asyncService.orderPaySuccessSplit(order.getOrderNo());
        // 添加支付成功task
//        redisUtil.lPush(TaskConstants.ORDER_TASK_PAY_SUCCESS_AFTER, orderNo);
        return Boolean.TRUE;
    }

    /**
     * 查询订单支付宝支付结果
     *
     * @param orderNo 订单编号
     */
    @Override
    public Boolean queryAliPayResult(String orderNo) {
        AliPayInfo aliPayInfo = aliPayInfoService.getByOutTradeNo(orderNo);
        if (ObjectUtil.isNull(aliPayInfo)) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "支付宝订单信息不存在");
        }
        String passbackParams = aliPayInfo.getPassbackParams();
        if (StrUtil.isBlank(passbackParams)) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "未知的支付宝订单类型");
        }
        String decode;
        try {
            decode = URLDecoder.decode(passbackParams, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            throw new CrmebException("ali pay query error : 订单支付类型解码失败==》" + orderNo);
        }

        String[] split = decode.split("=");
        String orderType = split[1];
        if (PayConstants.PAY_SERVICE_TYPE_RECHARGE.equals(orderType)) {// 充值订单
            RechargeOrder rechargeOrder = rechargeOrderService.getByOutTradeNo(orderNo);
            if (ObjectUtil.isNull(rechargeOrder)) {
                throw new CrmebException(StrUtil.format("ali pay query error : 充值订单后置处理，没有找到对应订单，支付服务方订单号：{}", orderNo));
            }
            if (rechargeOrder.getPaid()) {
                return Boolean.TRUE;
            }
            aliPayQuery(orderNo);
            // 支付成功处理
            Boolean rechargePayAfter = rechargeOrderService.paySuccessAfter(rechargeOrder);
            if (!rechargePayAfter) {
                throw new CrmebException(StrUtil.format("ali pay recharge pay after error : 数据保存失败==》" + orderNo));
            }
            return Boolean.TRUE;
        }
        if (PayConstants.PAY_SERVICE_TYPE_SVIP.equals(orderType)) {// 充值订单
            PaidMemberOrder paidMemberOrder = paidMemberOrderService.getByOutTradeNo(orderNo);
            if (ObjectUtil.isNull(paidMemberOrder)) {
                throw new CrmebException(StrUtil.format("ali pay query error : SVIP订单后置处理，没有找到对应订单，支付服务方订单号：{}", orderNo));
            }
            if (paidMemberOrder.getPaid()) {
                return Boolean.TRUE;
            }
            aliPayQuery(orderNo);
            // 支付成功处理
            // 支付成功处理
            Boolean svipPayAfter = paidMemberOrderService.paySuccessAfter(paidMemberOrder);
            if (!svipPayAfter) {
                logger.error("ali pay svip pay after error : 数据保存失败==》" + paidMemberOrder);
                throw new CrmebException(StrUtil.format("ali pay svip pay after error : 数据处理失败==》" + orderNo));
            }
            return Boolean.TRUE;
        }

        Order order = orderService.getByOrderNo(orderNo);
        if (ObjectUtil.isNull(order)) {
            throw new CrmebException(OrderResultCode.ORDER_NOT_EXIST);
        }
        if (order.getCancelStatus() > OrderConstants.ORDER_CANCEL_STATUS_NORMAL) {
            throw new CrmebException(OrderResultCode.ORDER_CANCEL);
        }
        if (!order.getPayType().equals(PayConstants.PAY_TYPE_ALI_PAY)) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "不是支付宝支付类型订单");
        }
        if (order.getPaid()) {
            return Boolean.TRUE;
        }
        aliPayQuery(orderNo);
        Boolean execute = transactionTemplate.execute(e -> {
            Boolean updatePaid = orderService.updatePaid(orderNo);
            if (!updatePaid) {
                logger.error("支付宝查询订单状态，商品订单更新支付状态失败，orderNo = {}", orderNo);
                e.setRollbackOnly();
                return Boolean.FALSE;
            }
            return Boolean.TRUE;
        });

        if (!execute) {
            throw new CrmebException("支付成功更新订单失败");
        }
        asyncService.orderPaySuccessSplit(order.getOrderNo());
        return Boolean.TRUE;
    }

    private AlipayTradeQueryResponse aliPayQuery(String orderNo) {
        //支付宝交易号
        // SDK 公共请求类，包含公共请求参数，以及封装了签名与验签，开发者无需关注签名与验签
        String aliPayAppid = systemConfigService.getValueByKey(AlipayConfig.APPID);
        String aliPayPrivateKey = systemConfigService.getValueByKey(AlipayConfig.RSA_PRIVATE_KEY);
        String aliPayPublicKey = systemConfigService.getValueByKey(AlipayConfig.ALIPAY_PUBLIC_KEY);
        String aliPayPublicKey2 = systemConfigService.getValueByKey(AlipayConfig.ALIPAY_PUBLIC_KEY_2);
        AlipayClient client = new DefaultAlipayClient(AlipayConfig.URL, aliPayAppid, aliPayPrivateKey, AlipayConfig.FORMAT, AlipayConfig.CHARSET, aliPayPublicKey2, AlipayConfig.SIGNTYPE);
        AlipayTradeQueryRequest alipay_request = new AlipayTradeQueryRequest();

        AlipayTradeQueryModel model = new AlipayTradeQueryModel();
        // 商户订单号，商户网站订单系统中唯一订单号，必填
        model.setOutTradeNo(orderNo);
        alipay_request.setBizModel(model);

        AlipayTradeQueryResponse alipay_response = null;
        try {
            alipay_response = client.execute(alipay_request);
        } catch (AlipayApiException e) {
            e.printStackTrace();
            logger.error("支付宝支付查询异常，" + e.getMessage());
            throw new CrmebException("支付宝支付查询异常");
        }
        if (ObjectUtil.isNull(alipay_response)) {
            logger.error("支付宝支付结果异常,查询结果为空");
            throw new CrmebException("支付宝支付结果异常,查询结果为空");
        }
        if (ObjectUtil.isNull(alipay_response.getTradeStatus()) || !alipay_response.getTradeStatus().equals("TRADE_SUCCESS")) {
            logger.error("支付宝支付结果异常，tradeStatus = " + alipay_response.getTradeStatus());
            throw new CrmebException("支付宝支付结果异常");
        }
        return alipay_response;
    }

    /**
     * 支付成功后置处理(临时)
     *
     * @param orderNo 订单编号
     */
    @Override
    public Boolean payAfterProcessingTemp(String orderNo) {
        Order platOrder = orderService.getByOrderNo(orderNo);
        if (ObjectUtil.isNull(platOrder)) {
            logger.error("OrderTaskServiceImpl.orderPaySuccessAfter | 订单不存在，orderNo: {}", orderNo);
            throw new CrmebException("订单不存在，orderNo: " + orderNo);
        }
        User user = userService.getById(platOrder.getUid());
        // 获取拆单后订单
        List<Order> orderList = orderService.getByPlatOrderNo(platOrder.getOrderNo());
        if (CollUtil.isEmpty(orderList)) {
            logger.error("OrderTaskServiceImpl.orderPaySuccessAfter | 商户订单信息不存在,orderNo: {}", orderNo);
            throw new CrmebException("商户订单信息不存在，orderNo: " + orderNo);
        }
        List<UserIntegralRecord> integralList = CollUtil.newArrayList();
        List<UserBrokerageRecord> brokerageRecordList = CollUtil.newArrayList();
        List<OrderProfitSharing> profitSharingList = CollUtil.newArrayList();
        List<MerchantBill> merchantBillList = CollUtil.newArrayList();
        List<Bill> billList = CollUtil.newArrayList();
        List<MerchantOrder> merchantOrderList = CollUtil.newArrayList();
        List<MerchantOrder> merchantOrderListForPrint = CollUtil.newArrayList();

        List<OrderDetail> orderDetailList = CollUtil.newArrayList();

        for (Order order : orderList) {
            // 拆单后，一个主订单只会对应一个商户订单
            MerchantOrder merchantOrder = merchantOrderService.getOneByOrderNo(order.getOrderNo());
            // 排除核销订单，核销订单在具体核销步骤再打印小票
            if (merchantOrder.getShippingType().equals(OrderConstants.ORDER_SHIPPING_TYPE_EXPRESS)) {
                merchantOrderListForPrint.add(merchantOrder);
            }
            if (order.getGainIntegral() > 0) {
                // 生成赠送积分记录
                UserIntegralRecord integralRecord = integralRecordGainInit(user.getId(), order.getOrderNo(), order.getGainIntegral());
                integralList.add(integralRecord);
            }
            List<OrderDetail> merOrderDetailList = orderDetailService.getByOrderNo(order.getOrderNo());
            // 佣金处理
            List<UserBrokerageRecord> broRecordList = assignCommission(merchantOrder, merOrderDetailList);
            if (CollUtil.isNotEmpty(broRecordList)) {
                brokerageRecordList.addAll(broRecordList);
                merchantOrderList.add(merchantOrder);
                orderDetailList.addAll(merOrderDetailList);
            }
            // 商户帐单流水、分账
            if (!order.getSecondType().equals(OrderConstants.ORDER_SECOND_TYPE_INTEGRAL)) {// 积分订单不参与账单
                OrderProfitSharing orderProfitSharing = initOrderProfitSharing(merchantOrder);
                MerchantBill merchantBill = initPayMerchantBill(merchantOrder, orderProfitSharing.getProfitSharingMerPrice());
                List<Bill> platBillList = initPlatformBill(order, merchantOrder, orderProfitSharing);
                profitSharingList.add(orderProfitSharing);
                merchantBillList.add(merchantBill);
                billList.addAll(platBillList);
            }
        }

        // 商户余额记录
        List<MerchantBalanceRecord> merchantBalanceRecordList = profitSharingList.stream().map(sharing -> {
            MerchantBalanceRecord merchantBalanceRecord = new MerchantBalanceRecord();
            merchantBalanceRecord.setMerId(sharing.getMerId());
            merchantBalanceRecord.setLinkNo(sharing.getOrderNo());
            merchantBalanceRecord.setLinkType("order");
            merchantBalanceRecord.setType(1);
            merchantBalanceRecord.setAmount(sharing.getProfitSharingMerPrice().add(sharing.getFreightFee()));
            merchantBalanceRecord.setTitle(StrUtil.format("订单支付，商户预计分账金额{}元", merchantBalanceRecord.getAmount()));
            merchantBalanceRecord.setBalance(BigDecimal.ZERO);
            merchantBalanceRecord.setStatus(1);
            return merchantBalanceRecord;
        }).collect(Collectors.toList());

        // 分销员逻辑
        if (!user.getIsPromoter()) {
            String funcStatus = systemConfigService.getValueByKey(SysConfigConstants.RETAIL_STORE_SWITCH);
            if (funcStatus.equals("1")) {
                String broQuota = systemConfigService.getValueByKey(SysConfigConstants.RETAIL_STORE_LINE);
                if (!broQuota.equals("-1") && platOrder.getPayPrice().compareTo(new BigDecimal(broQuota)) >= 0) {// -1 不成为分销员
                    user.setIsPromoter(true);
                }
            }
        } else {
            user.setIsPromoter(false);
        }
        Boolean execute = transactionTemplate.execute(e -> {
            // 订单、佣金
            if (CollUtil.isNotEmpty(brokerageRecordList)) {
                merchantOrderService.updateBatchById(merchantOrderList);
                orderDetailService.updateBatchById(orderDetailList);
                userBrokerageRecordService.saveBatch(brokerageRecordList);
            }
            // 订单日志
            orderList.forEach(o -> orderStatusService.createLog(o.getOrderNo(), OrderStatusConstants.ORDER_STATUS_PAY_SPLIT, StrUtil.format(OrderStatusConstants.ORDER_LOG_MESSAGE_PAY_SPLIT, platOrder.getOrderNo())));
            // 用户信息变更
            userService.paySuccessChange(user.getId(), user.getIsPromoter());
            // 积分记录
            if (CollUtil.isNotEmpty(integralList)) {
                userIntegralRecordService.saveBatch(integralList);
            }
            if (CollUtil.isNotEmpty(billList)) {
                billService.saveBatch(billList);
            }
            if (CollUtil.isNotEmpty(merchantBillList)) {
                merchantBillService.saveBatch(merchantBillList);
            }
            if (CollUtil.isNotEmpty(profitSharingList)) {
                orderProfitSharingService.saveBatch(profitSharingList);
            }
            if (CollUtil.isNotEmpty(merchantBalanceRecordList)) {
                merchantBalanceRecordService.saveBatch(merchantBalanceRecordList);
            }
            return Boolean.TRUE;
        });
        if (execute) {
            SystemNotification payNotification = systemNotificationService.getByMark(NotifyConstants.PAY_SUCCESS_MARK);
            // 发送短信
            if (StrUtil.isNotBlank(user.getPhone()) && payNotification.getIsSms().equals(1)) {
                try {
                    smsService.sendPaySuccess(user.getPhone(), platOrder.getOrderNo(), platOrder.getPayPrice());
                } catch (Exception e) {
                    logger.error("支付成功短信发送异常", e);
                }
            }
            if (payNotification.getIsWechat().equals(1) || payNotification.getIsRoutine().equals(1)) {
                //下发模板通知
                try {
                    pushMessageOrder(platOrder, user, payNotification);
                } catch (Exception e) {
                    logger.error("支付成功发送微信通知失败", e);
                }
            }

            // 购买成功后根据配置送优惠券
            autoSendCoupons(platOrder);
            List<String> orderNoList = orderList.stream().map(Order::getOrderNo).collect(Collectors.toList());
            asyncService.orderPayAfterFreezingOperation(orderNoList);

            SystemNotification merchantPayNotification = systemNotificationService.getByMark(NotifyConstants.MERCHANT_PAY_SUCCESS_REMINDER);
            if (merchantPayNotification.getIsSms().equals(1)) {
                try {
                    orderList.forEach(order -> {
                        List<SystemAdmin> smsAdminList = systemAdminService.findReceiveSmsAdminListByMerId(order.getMerId());
                        smsService.sendPaySuccessToMerchant(smsAdminList, order.getOrderNo());
                    });
                } catch (Exception e) {
                    logger.error("支付成功商户管理员短信发送异常", e);
                }
            }

            try {
                if (!platOrder.getSecondType().equals(OrderConstants.ORDER_SECOND_TYPE_INTEGRAL)) {
                    for (int i = 0; i < merchantOrderListForPrint.size(); ) {
                        MerchantOrder merchantOrder = merchantOrderListForPrint.get(i);
                        Merchant merchant = merchantService.getByIdException(merchantOrder.getMerId());
                        // 小票打印开关：0关闭，1=手动打印，2=自动打印，3=自动和手动
                        if (merchant.getReceiptPrintingSwitch() == 0) {
                            merchantOrderListForPrint.remove(i);
                            continue;
                        }
                        if (merchant.getReceiptPrintingSwitch() < 2) {
                            merchantOrderListForPrint.remove(i);
                            continue;
                        }
                        i++;
                    }
                    // 打印小票 op=1 为方法调用这里也就是支付后自动打印小票的场景
                    if (CollUtil.isNotEmpty(merchantOrderListForPrint)) {
                        merchantPrintService.batchPrintTicket(merchantOrderListForPrint);
                    }
                }
            } catch (Exception e) {
                logger.error(StrUtil.format("小票打印异常，Exception：{}", e.getMessage()), e);
            }

            orderList.forEach(order -> {
                if (!order.getType().equals(OrderConstants.ORDER_TYPE_PITUAN)) {
                    if (order.getSecondType().equals(OrderConstants.ORDER_SECOND_TYPE_CLOUD) || order.getSecondType().equals(OrderConstants.ORDER_SECOND_TYPE_CDKEY)) {
                        orderService.virtualShipment(order);
                    }
                }
            });
        }

        return execute;
    }

    /**
     * 获取收银台信息
     *
     * @param orderNo 订单号
     */
    @Override
    public CashierInfoResponse getCashierIno(String orderNo) {
        Order order = orderService.getByOrderNo(orderNo);
        if (order.getCancelStatus() > OrderConstants.ORDER_CANCEL_STATUS_NORMAL) {
            throw new CrmebException(OrderResultCode.ORDER_CANCEL);
        }
        if (order.getPaid()) {
            throw new CrmebException(OrderResultCode.ORDER_PAID);
        }
        if (order.getStatus() > OrderConstants.ORDER_STATUS_WAIT_PAY) {
            throw new CrmebException(OrderResultCode.ORDER_STATUS_ABNORMAL);
        }
        MerchantOrder merchantOrder = merchantOrderService.getOneByOrderNo(orderNo);
        CashierInfoResponse response = new CashierInfoResponse();
        response.setPayPrice(order.getPayPrice());
        response.setTotalNum(order.getTotalNum());
        response.setConsigneeName(merchantOrder.getRealName());
        response.setConsigneePhone(merchantOrder.getUserPhone());
        response.setConsigneeAddress(merchantOrder.getUserAddress());
        DateTime cancelDate = DateUtil.offsetMinute(order.getCreateTime(), crmebConfig.getOrderCancelTime());
        response.setCancelDateTime(cancelDate.getTime());
        return response;
    }

    /**
     * 查询微信支付结果
     */
    @Override
    public Boolean queryWechatPaymentResult(WechatPaymentQueryRequest request) {
        if (request.getOrderType().equals(PayConstants.PAY_SERVICE_TYPE_ORDER)) {
            return queryWechatPayResult(request.getOrderNo());
        }
        if (request.getOrderType().equals(PayConstants.PAY_SERVICE_TYPE_SVIP)) {
            return querySvipWechatPayResult(request.getOrderNo());
        }
        return queryRechargeWechatPayResult(request.getOrderNo());
    }

    private Boolean queryRechargeWechatPayResult(String orderNo) {
        RechargeOrder rechargeOrder = rechargeOrderService.getByOrderNo(orderNo);
        if (ObjectUtil.isNull(rechargeOrder)) {
            throw new CrmebException(OrderResultCode.ORDER_NOT_EXIST);
        }
        if (!rechargeOrder.getPayType().equals(PayConstants.PAY_TYPE_WE_CHAT)) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "不是微信支付类型订单");
        }
        if (rechargeOrder.getPaid()) {
            return Boolean.TRUE;
        }
        WechatPayInfo wechatPayInfo = wechatPayInfoService.getByNo(rechargeOrder.getOutTradeNo());
        if (ObjectUtil.isNull(wechatPayInfo)) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "未找到对应微信订单");
        }
        Map<String, String> payVo = getWechatQueryPayVo(rechargeOrder.getOutTradeNo(), rechargeOrder.getPayChannel());
        MyRecord myRecord = wechatService.payOrderQuery(payVo);
        if (myRecord.getInt("payStatus") < 1) {
            return Boolean.FALSE;
        }
        wechatPayInfo.setIsSubscribe(myRecord.getStr("is_subscribe"));
        wechatPayInfo.setTradeState(myRecord.getStr("trade_state"));
        wechatPayInfo.setBankType(myRecord.getStr("bank_type"));
        wechatPayInfo.setCashFee(myRecord.getInt("cash_fee"));
        wechatPayInfo.setCouponFee(myRecord.getInt("coupon_fee"));
        wechatPayInfo.setTransactionId(myRecord.getStr("transaction_id"));
        wechatPayInfo.setTimeEnd(myRecord.getStr("time_end"));
        wechatPayInfo.setTradeStateDesc(myRecord.getStr("trade_state_desc"));
        Boolean updatePaid = transactionTemplate.execute(e -> {
            Boolean update = rechargeOrderService.paySuccessAfter(rechargeOrder);
            if (!update) {
                logger.error("充值订单支付处理结果失败，orderNo = {}", rechargeOrder.getOrderNo());
                e.setRollbackOnly();
                return false;
            }
            wechatPayInfoService.updateById(wechatPayInfo);
            return Boolean.TRUE;
        });
        if (!updatePaid) {
            throw new CrmebException("充值支付成功更新订单失败");
        }
        return Boolean.TRUE;
    }

    /**
     * 查询svip订单微信支付结果
     */
    private Boolean querySvipWechatPayResult(String orderNo) {
        PaidMemberOrder order = paidMemberOrderService.getByOrderNo(orderNo);
        if (ObjectUtil.isNull(order)) {
            throw new CrmebException(MemberResultCode.PAID_MEMBER_ORDER_NOT_EXIST);
        }
        if (!order.getPayType().equals(PayConstants.PAY_TYPE_WE_CHAT)) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "不是微信支付类型订单");
        }
        if (order.getPaid()) {
            return Boolean.TRUE;
        }
        WechatPayInfo wechatPayInfo = wechatPayInfoService.getByNo(order.getOutTradeNo());
        if (ObjectUtil.isNull(wechatPayInfo)) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "未找到对应微信订单");
        }
        Map<String, String> payVo = getWechatQueryPayVo(order.getOutTradeNo(), order.getPayChannel());
        MyRecord myRecord = wechatService.payOrderQuery(payVo);
        if (myRecord.getInt("payStatus") < 1) {
            return Boolean.FALSE;
        }
        wechatPayInfo.setIsSubscribe(myRecord.getStr("is_subscribe"));
        wechatPayInfo.setTradeState(myRecord.getStr("trade_state"));
        wechatPayInfo.setBankType(myRecord.getStr("bank_type"));
        wechatPayInfo.setCashFee(myRecord.getInt("cash_fee"));
        wechatPayInfo.setCouponFee(myRecord.getInt("coupon_fee"));
        wechatPayInfo.setTransactionId(myRecord.getStr("transaction_id"));
        wechatPayInfo.setTimeEnd(myRecord.getStr("time_end"));
        wechatPayInfo.setTradeStateDesc(myRecord.getStr("trade_state_desc"));
        Boolean updatePaid = transactionTemplate.execute(e -> {
            Boolean update = paidMemberOrderService.paySuccessAfter(order);
            if (!update) {
                logger.error("svip订单支付处理结果失败，orderNo = {}", order.getOrderNo());
                e.setRollbackOnly();
                return false;
            }
            wechatPayInfoService.updateById(wechatPayInfo);
            return Boolean.TRUE;
        });
        if (!updatePaid) {
            throw new CrmebException("svip支付成功更新订单失败");
        }
        return Boolean.TRUE;
    }

    /**
     * 发送消息通知
     * 根据用户类型发送
     * 公众号模板消息
     * 小程序订阅消息
     */
    private void pushMessageOrder(Order order, User user, SystemNotification payNotification) {
        if (order.getPayChannel().equals(PayConstants.PAY_CHANNEL_H5)) {// H5
            return;
        }
        UserToken userToken;
        HashMap<String, String> temMap = new HashMap<>();
        if (!order.getPayType().equals(PayConstants.PAY_TYPE_WE_CHAT)) {
            return;
        }
        List<OrderDetail> orderDetailList = orderDetailService.getByOrderNo(order.getOrderNo());
        // 公众号模板消息
        if (order.getPayChannel().equals(PayConstants.PAY_CHANNEL_WECHAT_PUBLIC) && payNotification.getIsWechat().equals(1) && user.getIsWechatPublic()) {
            userToken = userTokenService.getTokenByUserId(user.getId(), UserConstants.USER_TOKEN_TYPE_WECHAT);
            if (ObjectUtil.isNull(userToken)) {
                return;
            }
            // 发送微信模板消息
            /**
             * {{first.DATA}}
             * 订单号：{{keyword1.DATA}}
             * 商品名称：{{keyword2.DATA}}
             * 支付金额：{{keyword3.DATA}}
             * 下单人：{{keyword4.DATA}}
             * 订单支付时间：{{keyword5.DATA}}
             * {{remark.DATA}}
             */
            temMap.put(Constants.WE_CHAT_TEMP_KEY_FIRST, "订单支付成功通知！");
            temMap.put("keyword1", order.getOrderNo());
            temMap.put("keyword2", orderDetailList.stream().map(OrderDetail::getProductName).collect(Collectors.joining(",")));
            temMap.put("keyword3", order.getPayPrice().toString());
            temMap.put("keyword4", user.getNickname());
            temMap.put("keyword5", order.getPayTime().toString());
            temMap.put(Constants.WE_CHAT_TEMP_KEY_END, "欢迎下次再来！");
            templateMessageService.pushTemplateMessage(payNotification.getWechatId(), temMap, userToken.getToken());
            return;
        }
        if (order.getPayChannel().equals(PayConstants.PAY_CHANNEL_WECHAT_MINI) && payNotification.getIsRoutine().equals(1)) {
            // 小程序发送订阅消息
            userToken = userTokenService.getTokenByUserId(user.getId(), UserConstants.USER_TOKEN_TYPE_ROUTINE);
            if (ObjectUtil.isNull(userToken)) {
                return;
            }
            // 组装数据
//            temMap.put("character_string1", storeOrder.getOrderId());
//            temMap.put("amount2", storeOrder.getPayPrice().toString() + "元");
//            temMap.put("thing7", "您的订单已支付成功");
            temMap.put("character_string3", order.getOrderNo());
            temMap.put("amount9", order.getPayPrice().toString() + "元");
            temMap.put("thing6", "您的订单已支付成功");
            templateMessageService.pushMiniTemplateMessage(payNotification.getRoutineId(), temMap, userToken.getToken());
        }
    }


    /**
     * 佣金处理
     *
     * @param merchantOrder   商户订单部分
     * @param orderDetailList 订单详情列表
     * @return 佣金记录列表
     */
    private List<UserBrokerageRecord> assignCommission(MerchantOrder merchantOrder, List<OrderDetail> orderDetailList) {
        // 秒杀订单不参与分佣
        if (merchantOrder.getType().equals(OrderConstants.ORDER_TYPE_SECKILL)) {
            return CollUtil.newArrayList();
        }
        // 积分订单不参与分佣
        if (merchantOrder.getSecondType().equals(OrderConstants.ORDER_SECOND_TYPE_INTEGRAL)) {
            return CollUtil.newArrayList();
        }
        // 积分订单不参与分佣
        if (merchantOrder.getType().equals(OrderConstants.ORDER_TYPE_PITUAN)){
            return CollUtil.newArrayList();
        }
        // 检测商城是否开启分销功能
        String isOpen = systemConfigService.getValueByKey(SysConfigConstants.RETAIL_STORE_SWITCH);
        if (StrUtil.isBlank(isOpen) || isOpen.equals("0")) {
            return CollUtil.newArrayList();
        }
        long count = orderDetailList.stream().filter(e -> e.getSubBrokerageType() > 0).count();
        if (count == 0L) {
            return CollUtil.newArrayList();
        }
        // 查找订单所属人信息
        User user = userService.getById(merchantOrder.getUid());
        // 当前用户不存在 没有上级 或者 当用用户上级时自己  直接返回
        if (ObjectUtil.isNull(user.getSpreadUid()) || user.getSpreadUid() < 1 || user.getSpreadUid().equals(merchantOrder.getUid())) {
            return CollUtil.newArrayList();
        }
        // 获取参与分佣的人（两级）
        List<MyRecord> spreadRecordList = getSpreadRecordList(user.getSpreadUid());
        if (CollUtil.isEmpty(spreadRecordList)) {
            return CollUtil.newArrayList();
        }
        // 获取佣金冻结期
        String freezingTime = systemConfigService.getValueByKey(SysConfigConstants.RETAIL_STORE_BROKERAGE_FREEZING_TIME);
        List<UserBrokerageRecord> brokerageRecordList = new ArrayList<>();
        // 计算两级佣金金额
        if (spreadRecordList.size() == 1) {
            BigDecimal firstBrokerage = BigDecimal.ZERO;
            for (OrderDetail orderDetail : orderDetailList) {
                if (orderDetail.getSubBrokerageType().equals(0)) {
                    continue;
                }
                BigDecimal detailPrice = (orderDetail.getIsSvip() && orderDetail.getIsPaidMemberProduct()) ? orderDetail.getVipPrice() : orderDetail.getPrice();
                BigDecimal brokerage = detailPrice.multiply(new BigDecimal(orderDetail.getPayNum().toString()))
                        .subtract(orderDetail.getMerCouponPrice())
                        .multiply(new BigDecimal(orderDetail.getBrokerage().toString()))
                        .divide(new BigDecimal("100"), 2, BigDecimal.ROUND_DOWN);
                orderDetail.setFirstBrokerageFee(brokerage);
                firstBrokerage = firstBrokerage.add(brokerage);
            }
            merchantOrder.setFirstBrokerage(firstBrokerage);
            UserBrokerageRecord brokerageRecord = new UserBrokerageRecord();
            brokerageRecord.setUid(spreadRecordList.get(0).getInt("spreadUid"));
            brokerageRecord.setSubUid(merchantOrder.getUid());
            brokerageRecord.setLinkNo(merchantOrder.getOrderNo());
            brokerageRecord.setLinkType(BrokerageRecordConstants.BROKERAGE_RECORD_LINK_TYPE_ORDER);
            brokerageRecord.setType(BrokerageRecordConstants.BROKERAGE_RECORD_TYPE_ADD);
            brokerageRecord.setTitle(BrokerageRecordConstants.BROKERAGE_RECORD_TITLE_ORDER);
            brokerageRecord.setPrice(firstBrokerage);
            brokerageRecord.setMark(StrUtil.format("获得推广佣金，分佣{}", firstBrokerage));
            brokerageRecord.setStatus(BrokerageRecordConstants.BROKERAGE_RECORD_STATUS_CREATE);
            brokerageRecord.setFrozenTime(Integer.valueOf(Optional.ofNullable(freezingTime).orElse("0")));
            brokerageRecord.setCreateTime(CrmebDateUtil.nowDateTime());
            brokerageRecord.setBrokerageLevel(spreadRecordList.get(0).getInt("index"));
            brokerageRecordList.add(brokerageRecord);
            return brokerageRecordList;
        }
        BigDecimal firstBrokerage = BigDecimal.ZERO;
        BigDecimal secondBrokerage = BigDecimal.ZERO;
        for (OrderDetail orderDetail : orderDetailList) {
            if (orderDetail.getSubBrokerageType().equals(0)) {
                continue;
            }
            BigDecimal detailPrice = (orderDetail.getIsSvip() && orderDetail.getIsPaidMemberProduct()) ? orderDetail.getVipPrice() : orderDetail.getPrice();
            BigDecimal brokerage = detailPrice.multiply(new BigDecimal(orderDetail.getPayNum().toString()))
                    .subtract(orderDetail.getMerCouponPrice())
                    .multiply(new BigDecimal(orderDetail.getBrokerage().toString()))
                    .divide(new BigDecimal("100"), 2, BigDecimal.ROUND_DOWN);
            BigDecimal brokerageTwo = detailPrice.multiply(new BigDecimal(orderDetail.getPayNum().toString()))
                    .subtract(orderDetail.getMerCouponPrice())
                    .multiply(new BigDecimal(orderDetail.getBrokerageTwo().toString()))
                    .divide(new BigDecimal("100"), 2, BigDecimal.ROUND_DOWN);

            orderDetail.setFirstBrokerageFee(brokerage);
            orderDetail.setSecondBrokerageFee(brokerageTwo);
            firstBrokerage = firstBrokerage.add(brokerage);
            secondBrokerage = secondBrokerage.add(brokerageTwo);
        }
        merchantOrder.setFirstBrokerage(firstBrokerage);
        merchantOrder.setSecondBrokerage(secondBrokerage);
        // 生成佣金记录
        brokerageRecordList = spreadRecordList.stream().map(record -> {
            UserBrokerageRecord brokerageRecord = new UserBrokerageRecord();
            brokerageRecord.setUid(record.getInt("spreadUid"));
            brokerageRecord.setSubUid(merchantOrder.getUid());
            brokerageRecord.setLinkNo(merchantOrder.getOrderNo());
            brokerageRecord.setLinkType(BrokerageRecordConstants.BROKERAGE_RECORD_LINK_TYPE_ORDER);
            brokerageRecord.setType(BrokerageRecordConstants.BROKERAGE_RECORD_TYPE_ADD);
            brokerageRecord.setTitle(BrokerageRecordConstants.BROKERAGE_RECORD_TITLE_ORDER);
            BigDecimal price = record.getInt("index") == 1 ? merchantOrder.getFirstBrokerage() : merchantOrder.getSecondBrokerage();
            brokerageRecord.setPrice(price);
            brokerageRecord.setMark(StrUtil.format("获得推广佣金，分佣{}", price));
            brokerageRecord.setStatus(BrokerageRecordConstants.BROKERAGE_RECORD_STATUS_CREATE);
            brokerageRecord.setFrozenTime(Integer.valueOf(Optional.ofNullable(freezingTime).orElse("0")));
            brokerageRecord.setCreateTime(CrmebDateUtil.nowDateTime());
            brokerageRecord.setBrokerageLevel(record.getInt("index"));
            return brokerageRecord;
        }).collect(Collectors.toList());
        return brokerageRecordList;
    }

    /**
     * 获取参与分佣人员（两级）
     *
     * @param spreadUid 一级分佣人Uid
     * @return List<MyRecord>
     */
    private List<MyRecord> getSpreadRecordList(Integer spreadUid) {
        List<MyRecord> recordList = CollUtil.newArrayList();

        // 第一级
        User spreadUser = userService.getById(spreadUid);
        if (ObjectUtil.isNull(spreadUser) || !spreadUser.getIsPromoter()) {
            return recordList;
        }

        MyRecord firstRecord = new MyRecord();
        firstRecord.set("index", 1);
        firstRecord.set("spreadUid", spreadUid);
        recordList.add(firstRecord);

        // 第二级
        User spreadSpreadUser = userService.getById(spreadUser.getSpreadUid());
        if (ObjectUtil.isNull(spreadSpreadUser) || !spreadSpreadUser.getIsPromoter()) {
            return recordList;
        }
        MyRecord secondRecord = new MyRecord();
        secondRecord.set("index", 2);
        secondRecord.set("spreadUid", spreadSpreadUser.getId());
        recordList.add(secondRecord);
        return recordList;
    }

    /**
     * 初始化订单分帐表
     *
     * @param merchantOrder 商户部分订单
     * @return 分账记录
     */
    private OrderProfitSharing initOrderProfitSharing(MerchantOrder merchantOrder) {
        // 获取商户信息
        Merchant merchant = merchantService.getByIdException(merchantOrder.getMerId());
        // 分账计算
        // 商户收入 = 订单应付 - 商户优惠 -平台手续费 - 佣金
        BigDecimal orderPrice = merchantOrder.getPayPrice().add(merchantOrder.getIntegralPrice()).add(merchantOrder.getPlatCouponPrice()).subtract(merchantOrder.getPayPostage());
        // 平台手续费
        BigDecimal platFee = orderPrice.multiply(new BigDecimal(merchant.getHandlingFee())).divide(new BigDecimal(100), 2, BigDecimal.ROUND_UP);
        // 商户收入金额
        BigDecimal merchantFee = orderPrice.subtract(platFee).subtract(merchantOrder.getFirstBrokerage()).subtract(merchantOrder.getSecondBrokerage());
        OrderProfitSharing orderProfitSharing = new OrderProfitSharing();
        orderProfitSharing.setOrderNo(merchantOrder.getOrderNo());
        orderProfitSharing.setMerId(merchantOrder.getMerId());
        orderProfitSharing.setOrderPrice(merchantOrder.getPayPrice());
        orderProfitSharing.setIntegralNum(merchantOrder.getUseIntegral());
        orderProfitSharing.setIntegralPrice(merchantOrder.getIntegralPrice());
        orderProfitSharing.setProfitSharingPlatPrice(platFee);
        orderProfitSharing.setProfitSharingMerPrice(merchantFee);
        orderProfitSharing.setFirstBrokerageFee(merchantOrder.getFirstBrokerage());
        orderProfitSharing.setSecondBrokerageFee(merchantOrder.getSecondBrokerage());
        orderProfitSharing.setPlatCouponPrice(merchantOrder.getPlatCouponPrice());
        orderProfitSharing.setFreightFee(merchantOrder.getPayPostage());
        return orderProfitSharing;
    }

    /**
     * 初始化订单支付商户账单表
     *
     * @param merchantOrder 商户订单部分
     * @param merchantFee   商户分账金额
     */
    private MerchantBill initPayMerchantBill(MerchantOrder merchantOrder, BigDecimal merchantFee) {
        MerchantBill merchantBill = new MerchantBill();
        merchantBill.setMerId(merchantOrder.getMerId());
        merchantBill.setType(BillConstants.BILL_TYPE_PAY_ORDER);
        merchantBill.setOrderNo(merchantOrder.getOrderNo());
        merchantBill.setUid(merchantOrder.getUid());
        merchantBill.setPm(BillConstants.BILL_PM_ADD);
        BigDecimal merFee = merchantOrder.getPayPostage().add(merchantFee);
        merchantBill.setAmount(merFee);
        merchantBill.setMark(StrUtil.format("订单{}支付{}元，商户收入{}元", merchantOrder.getOrderNo(), merchantOrder.getPayPrice(), merFee));
        return merchantBill;
    }

    /**
     * 初始化订单支付平台账单表
     *
     * @param order              订单
     * @param merchantOrder      商户订单部分
     * @param orderProfitSharing 分账数据
     * @return List
     */
    private List<Bill> initPlatformBill(Order order, MerchantOrder merchantOrder, OrderProfitSharing orderProfitSharing) {
        List<Bill> billList = CollUtil.newArrayList();

        Bill payBill = new Bill();
        payBill.setUid(order.getUid());
        payBill.setOrderNo(order.getOrderNo());
        payBill.setAmount(order.getPayPrice());
        if (order.getPayType().equals(PayConstants.PAY_TYPE_YUE)) {
            payBill.setPm(BillConstants.BILL_PM_SUB);
            payBill.setType(BillConstants.BILL_TYPE_YUE_PAY);
            payBill.setMark(StrUtil.format("余额支付成功，扣除用户余额{}元", order.getPayPrice()));
        } else {
            payBill.setPm(BillConstants.BILL_PM_ADD);
            payBill.setType(BillConstants.BILL_TYPE_PAY_ORDER);
            payBill.setMark(StrUtil.format("订单支付成功，支付金额{}元", order.getPayPrice()));
        }
        billList.add(payBill);

        Bill collectBill = new Bill();
        collectBill.setMerId(merchantOrder.getMerId());
        collectBill.setOrderNo(order.getOrderNo());
        collectBill.setAmount(orderProfitSharing.getProfitSharingMerPrice().add(merchantOrder.getPayPostage()));
        collectBill.setPm(BillConstants.BILL_PM_SUB);
        collectBill.setType(BillConstants.BILL_TYPE_MERCHANT_COLLECT);
        collectBill.setMark(StrUtil.format("订单支付成功，商户分账{}元", orderProfitSharing.getProfitSharingMerPrice().add(merchantOrder.getPayPostage())));
        billList.add(collectBill);

        Bill platBill = new Bill();
        platBill.setOrderNo(order.getOrderNo());
        platBill.setAmount(orderProfitSharing.getProfitSharingPlatPrice());
        platBill.setPm(BillConstants.BILL_PM_ADD);
        platBill.setType(BillConstants.BILL_TYPE_PAY_ORDER);
        platBill.setMark(StrUtil.format("订单支付成功，平台手续费{}元", orderProfitSharing.getProfitSharingPlatPrice()));
        billList.add(platBill);

        if (ObjectUtil.isNotNull(orderProfitSharing.getFirstBrokerageFee()) && orderProfitSharing.getFirstBrokerageFee().compareTo(BigDecimal.ZERO) > 0) {
            Bill firstBill = new Bill();
            firstBill.setOrderNo(order.getOrderNo());
            firstBill.setAmount(orderProfitSharing.getFirstBrokerageFee());
            firstBill.setPm(BillConstants.BILL_PM_SUB);
            firstBill.setType(BillConstants.BILL_TYPE_BROKERAGE);
            firstBill.setMark(StrUtil.format("订单支付成功，分配一级佣金{}元", orderProfitSharing.getFirstBrokerageFee()));
            billList.add(firstBill);
            if (orderProfitSharing.getSecondBrokerageFee().compareTo(BigDecimal.ZERO) > 0) {
                Bill secondBill = new Bill();
                secondBill.setOrderNo(order.getOrderNo());
                secondBill.setAmount(orderProfitSharing.getSecondBrokerageFee());
                secondBill.setPm(BillConstants.BILL_PM_SUB);
                secondBill.setType(BillConstants.BILL_TYPE_BROKERAGE);
                secondBill.setMark(StrUtil.format("订单支付成功，分配二级佣金{}元", orderProfitSharing.getSecondBrokerageFee()));
                billList.add(secondBill);
            }
        }

        if (orderProfitSharing.getIntegralNum() > 0) {
            Bill integralBill = new Bill();
            integralBill.setOrderNo(order.getOrderNo());
            integralBill.setAmount(order.getIntegralPrice());
            integralBill.setPm(BillConstants.BILL_PM_SUB);
            integralBill.setType(BillConstants.BILL_TYPE_PAY_ORDER);
            integralBill.setMark(StrUtil.format("订单支付成功，用户使用{}积分抵扣{}元，平台扣除", orderProfitSharing.getIntegralNum(), orderProfitSharing.getIntegralPrice()));
            billList.add(integralBill);
        }
        return billList;
    }

    /**
     * 初始化积分赠送记录
     */
    private UserIntegralRecord integralRecordGainInit(Integer uid, String orderNo, Integer gainIntegral) {
        UserIntegralRecord integralRecord = new UserIntegralRecord();
        integralRecord.setUid(uid);
        integralRecord.setLinkId(orderNo);
        integralRecord.setLinkType(IntegralRecordConstants.INTEGRAL_RECORD_LINK_TYPE_ORDER);
        integralRecord.setType(IntegralRecordConstants.INTEGRAL_RECORD_TYPE_ADD);
        integralRecord.setTitle(IntegralRecordConstants.INTEGRAL_RECORD_TITLE_ORDER);
        integralRecord.setIntegral(gainIntegral);
//        integralRecord.setBalance(balance);
        integralRecord.setMark(StrUtil.format("订单支付成功奖励{}积分", gainIntegral));
        integralRecord.setStatus(IntegralRecordConstants.INTEGRAL_RECORD_STATUS_CREATE);
        // 获取积分冻结期
        String freezeTime = systemConfigService.getValueByKey(SysConfigConstants.CONFIG_KEY_STORE_INTEGRAL_EXTRACT_TIME);
        integralRecord.setFrozenTime(Integer.valueOf(Optional.ofNullable(freezeTime).orElse("0")));
        integralRecord.setCreateTime(CrmebDateUtil.nowDateTime());
        return integralRecord;
    }

    private Map<String, String> getWechatQueryPayVo(String outTradeNo, String payChannel) {
        // 获取appid、mch_id
        // 微信签名key
        String appId = "";
        String mchId = "";
        String signKey = "";
        String tempPayChannel = payChannel;
        if (tempPayChannel.equals(PayConstants.PAY_CHANNEL_H5) || tempPayChannel.equals(PayConstants.PAY_CHANNEL_WECHAT_NATIVE)) {
            String source = systemConfigService.getValueByKey(SysConfigConstants.WECHAT_PAY_SOURCE_H5_PC);
            if (StrUtil.isNotBlank(source) && source.equals(PayConstants.WECHAT_PAY_SOURCE_MINI)) {
                tempPayChannel = PayConstants.PAY_CHANNEL_WECHAT_MINI;
            } else {
                tempPayChannel = PayConstants.PAY_CHANNEL_WECHAT_PUBLIC;
            }
        }
        switch (tempPayChannel) {
            case PayConstants.PAY_CHANNEL_WECHAT_PUBLIC:
                appId = systemConfigService.getValueByKeyException(WeChatConstants.WECHAT_PUBLIC_APPID);
                mchId = systemConfigService.getValueByKeyException(WeChatConstants.WECHAT_PAY_PUBLIC_MCHID);
                signKey = systemConfigService.getValueByKeyException(WeChatConstants.WECHAT_PAY_PUBLIC_KEY);
                break;
            case PayConstants.PAY_CHANNEL_WECHAT_MINI:
            case PayConstants.PAY_CHANNEL_WECHAT_MINI_VIDEO:
                appId = systemConfigService.getValueByKeyException(WeChatConstants.WECHAT_MINI_APPID);
                mchId = systemConfigService.getValueByKeyException(WeChatConstants.WECHAT_PAY_MINI_MCHID);
                signKey = systemConfigService.getValueByKeyException(WeChatConstants.WECHAT_PAY_MINI_KEY);
                break;
            case PayConstants.PAY_CHANNEL_WECHAT_APP_IOS:
            case PayConstants.PAY_CHANNEL_WECHAT_APP_ANDROID:
                appId = systemConfigService.getValueByKeyException(WeChatConstants.WECHAT_APP_APPID);
                mchId = systemConfigService.getValueByKeyException(WeChatConstants.WECHAT_PAY_APP_MCHID);
                signKey = systemConfigService.getValueByKeyException(WeChatConstants.WECHAT_PAY_APP_KEY);
                break;
        }

        // 生成查询订单对象
        Map<String, String> map = CollUtil.newHashMap();
        map.put("appid", appId);
        map.put("mch_id", mchId);
        map.put("out_trade_no", outTradeNo);
        map.put("nonce_str", WxPayUtil.getNonceStr());
        map.put("sign_type", PayConstants.WX_PAY_SIGN_TYPE_MD5);
        map.put("sign", WxPayUtil.getSign(map, signKey));
        return map;
    }

    /**
     * 支付宝支付
     *
     * @param order 订单
     * @return result
     */
    private String aliPayment(Order order) {
        DateTime cancelTime = DateUtil.offset(order.getCreateTime(), DateField.MINUTE, crmebConfig.getOrderCancelTime());
        String cancelStr = cancelTime.toString(DateConstants.DATE_FORMAT);
        return aliPayService.pay(order.getOrderNo(), order.getPayPrice(), "order", order.getPayChannel(), cancelStr);
    }

    /**
     * 微信支付
     *
     * @param order 订单
     * @return WxPayJsResultVo
     */
    private WxPayJsResultVo wechatPayment(Order order) {
        // 预下单
        Map<String, String> unifiedorder = unifiedorder(order);
        WxPayJsResultVo vo = new WxPayJsResultVo();
        vo.setAppId(unifiedorder.get("appId"));
        vo.setNonceStr(unifiedorder.get("nonceStr"));
        vo.setPackages(unifiedorder.get("package"));
        vo.setSignType(unifiedorder.get("signType"));
        vo.setTimeStamp(unifiedorder.get("timeStamp"));
        vo.setPaySign(unifiedorder.get("paySign"));
        if (order.getPayChannel().equals(PayConstants.PAY_CHANNEL_H5)) {
            vo.setMwebUrl(unifiedorder.get("mweb_url"));
        }
        if (order.getPayChannel().equals(PayConstants.PAY_CHANNEL_WECHAT_APP_IOS) ||
                order.getPayChannel().equals(PayConstants.PAY_CHANNEL_WECHAT_APP_ANDROID)) {// App
            vo.setPartnerid(unifiedorder.get("partnerid"));
        }
        if (order.getPayChannel().equals(PayConstants.PAY_CHANNEL_WECHAT_NATIVE)) {
            vo.setMwebUrl(unifiedorder.get("code_url"));
        }
        // 更新商户订单号
        order.setOutTradeNo(unifiedorder.get("outTradeNo"));
        return vo;
    }

    /**
     * 余额支付
     *
     * @param order 订单
     * @return Boolean Boolean
     */
    private Boolean yuePay(Order order, User user) {
        // 用户余额扣除
        Boolean execute = transactionTemplate.execute(e -> {
            Boolean update = Boolean.TRUE;
            // 订单修改
            order.setPaid(true);
            order.setPayTime(DateUtil.date());
            order.setStatus(OrderConstants.ORDER_STATUS_WAIT_SHIPPING);
            order.setPayType(PayConstants.PAY_TYPE_YUE);
            order.setPayChannel(PayConstants.PAY_CHANNEL_YUE);
            orderService.updateById(order);
            // 这里只扣除金额，账单记录在task中处理
            if (order.getPayPrice().compareTo(BigDecimal.ZERO) > 0) {
                update = userService.updateNowMoney(order.getUid(), order.getPayPrice(), Constants.OPERATION_TYPE_SUBTRACT);
                if (!update) {
                    logger.error("余额支付，扣除用户余额失败，orderNo = {}", order.getOrderNo());
                    e.setRollbackOnly();
                    return update;
                }
                // 用户余额记录
                UserBalanceRecord userBalanceRecord = new UserBalanceRecord();
                userBalanceRecord.setUid(user.getId());
                userBalanceRecord.setLinkId(order.getOrderNo());
                userBalanceRecord.setLinkType(BalanceRecordConstants.BALANCE_RECORD_LINK_TYPE_ORDER);
                userBalanceRecord.setType(BalanceRecordConstants.BALANCE_RECORD_TYPE_SUB);
                userBalanceRecord.setAmount(order.getPayPrice());
                userBalanceRecord.setBalance(user.getNowMoney().subtract(order.getPayPrice()));
                userBalanceRecord.setRemark(StrUtil.format(BalanceRecordConstants.BALANCE_RECORD_REMARK_ORDER, order.getPayPrice()));
                userBalanceRecordService.save(userBalanceRecord);
            }
            return update;
        });
        if (!execute) throw new CrmebException("余额支付订单失败");
        asyncService.orderPaySuccessSplit(order.getOrderNo());
        return true;
    }

    /**
     * 预下单
     *
     * @param order 订单
     * @return 预下单返回对象
     */
    private Map<String, String> unifiedorder(Order order) {
        // 获取用户openId
        // 根据订单支付类型来判断获取公众号openId还是小程序openId
        UserToken userToken = new UserToken();
        userToken.setToken("");
        if (order.getPayChannel().equals(PayConstants.PAY_CHANNEL_WECHAT_PUBLIC)) {// 公众号
            userToken = userTokenService.getTokenByUserId(order.getUid(), UserConstants.USER_TOKEN_TYPE_WECHAT);
        }
        if (order.getPayChannel().equals(PayConstants.PAY_CHANNEL_WECHAT_MINI)
                || order.getPayChannel().equals(PayConstants.PAY_CHANNEL_WECHAT_MINI_VIDEO)) {// 小程序
            userToken = userTokenService.getTokenByUserId(order.getUid(), UserConstants.USER_TOKEN_TYPE_ROUTINE);
        }

        // 获取appid、mch_id、微信签名key
        String appId = "";
        String mchId = "";
        String signKey = "";
        // 从config表中获取H5/PC支付使用的是公众号还是小程序的
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
                appId = systemConfigService.getValueByKeyException(WeChatConstants.WECHAT_PUBLIC_APPID);
                mchId = systemConfigService.getValueByKeyException(WeChatConstants.WECHAT_PAY_PUBLIC_MCHID);
                signKey = systemConfigService.getValueByKeyException(WeChatConstants.WECHAT_PAY_PUBLIC_KEY);
                break;
            case PayConstants.PAY_CHANNEL_WECHAT_MINI:
            case PayConstants.PAY_CHANNEL_WECHAT_MINI_VIDEO:
                appId = systemConfigService.getValueByKeyException(WeChatConstants.WECHAT_MINI_APPID);
                mchId = systemConfigService.getValueByKeyException(WeChatConstants.WECHAT_PAY_MINI_MCHID);
                signKey = systemConfigService.getValueByKeyException(WeChatConstants.WECHAT_PAY_MINI_KEY);
                break;
            case PayConstants.PAY_CHANNEL_WECHAT_APP_IOS:
            case PayConstants.PAY_CHANNEL_WECHAT_APP_ANDROID:
                appId = systemConfigService.getValueByKeyException(WeChatConstants.WECHAT_APP_APPID);
                mchId = systemConfigService.getValueByKeyException(WeChatConstants.WECHAT_PAY_APP_MCHID);
                signKey = systemConfigService.getValueByKeyException(WeChatConstants.WECHAT_PAY_APP_KEY);
                break;
        }
        // 获取微信预下单对象
        CreateOrderRequestVo unifiedorderVo = getUnifiedorderVo(order, userToken.getToken(), appId, mchId, signKey);

        // 预下单（统一下单）
        CreateOrderResponseVo responseVo = wechatService.payUnifiedorder(unifiedorderVo);
        // 组装前端预下单参数
        Map<String, String> map = new HashMap<>();
        map.put("appId", unifiedorderVo.getAppid());
        map.put("nonceStr", unifiedorderVo.getAppid());
        map.put("package", "prepay_id=".concat(responseVo.getPrepayId()));
        map.put("signType", unifiedorderVo.getSign_type());
        Long currentTimestamp = WxPayUtil.getCurrentTimestamp();
        map.put("timeStamp", Long.toString(currentTimestamp));
        String paySign = WxPayUtil.getSign(map, signKey);
        map.put("paySign", paySign);
        map.put("prepayId", responseVo.getPrepayId());
        map.put("prepayTime", CrmebDateUtil.nowDateTimeStr());
        map.put("outTradeNo", unifiedorderVo.getOut_trade_no());
        if (order.getPayChannel().equals(PayConstants.PAY_CHANNEL_H5)) {
            map.put("mweb_url", responseVo.getMWebUrl());
        }
        if (order.getPayChannel().equals(PayConstants.PAY_CHANNEL_WECHAT_APP_IOS) ||
                order.getPayChannel().equals(PayConstants.PAY_CHANNEL_WECHAT_APP_ANDROID)) {// App
            map.put("partnerid", mchId);
            map.put("package", responseVo.getPrepayId());
            Map<String, Object> appMap = new HashMap<>();
            appMap.put("appid", unifiedorderVo.getAppid());
            appMap.put("partnerid", mchId);
            appMap.put("prepayid", responseVo.getPrepayId());
            appMap.put("package", "Sign=WXPay");
            appMap.put("noncestr", unifiedorderVo.getAppid());
            appMap.put("timestamp", currentTimestamp);
            String sign = WxPayUtil.getSignObject(appMap, signKey);
            map.put("paySign", sign);
        }
        if (order.getPayChannel().equals(PayConstants.PAY_CHANNEL_WECHAT_NATIVE)) {
            map.put("code_url", responseVo.getCodeUrl());
        }
        return map;
    }

    /**
     * 获取微信预下单对象
     *
     * @return 微信预下单对象
     */
    private CreateOrderRequestVo getUnifiedorderVo(Order order, String openid, String appId, String mchId, String signKey) {
        // 获取域名
        String domain = systemConfigService.getValueByKeyException(SysConfigConstants.CONFIG_KEY_SITE_URL);
        String apiDomain = systemConfigService.getValueByKeyException(SysConfigConstants.CONFIG_KEY_API_URL);

        AttachVo attachVo = new AttachVo(PayConstants.PAY_SERVICE_TYPE_ORDER, order.getUid());

        CreateOrderRequestVo vo = new CreateOrderRequestVo();
        vo.setAppid(appId);
        vo.setMch_id(mchId);
        vo.setNonce_str(WxPayUtil.getNonceStr());
        vo.setSign_type(PayConstants.WX_PAY_SIGN_TYPE_MD5);
        String siteName = systemConfigService.getValueByKeyException(SysConfigConstants.CONFIG_KEY_SITE_NAME);
        // 因商品名称在微信侧超长更换为网站名称
        vo.setBody(siteName);
        vo.setAttach(JSONObject.toJSONString(attachVo));
        vo.setOut_trade_no(CrmebUtil.getOrderNo(OrderConstants.ORDER_PREFIX_WECHAT));
        // 订单中使用的是BigDecimal,这里要转为Integer类型
        vo.setTotal_fee(order.getPayPrice().multiply(BigDecimal.TEN).multiply(BigDecimal.TEN).intValue());
        vo.setSpbill_create_ip(RequestUtil.getClientIp());
        vo.setNotify_url(apiDomain + PayConstants.WX_PAY_NOTIFY_API_URI);
        switch (order.getPayChannel()) {
            case PayConstants.PAY_CHANNEL_H5:
                vo.setTrade_type(PayConstants.WX_PAY_TRADE_TYPE_H5);
                vo.setOpenid(null);
                break;
            case PayConstants.PAY_CHANNEL_WECHAT_APP_IOS:
            case PayConstants.PAY_CHANNEL_WECHAT_APP_ANDROID:
                vo.setTrade_type(PayConstants.WX_PAY_TRADE_TYPE_APP);
                vo.setOpenid(null);
                break;
            case PayConstants.PAY_CHANNEL_WECHAT_NATIVE:
                vo.setTrade_type(PayConstants.WX_PAY_TRADE_TYPE_NATIVE);
                vo.setProduct_id(order.getOrderNo());
                vo.setOpenid(null);
                break;
            default:
                vo.setTrade_type(PayConstants.WX_PAY_TRADE_TYPE_JS);
                vo.setOpenid(openid);
        }
        CreateOrderH5SceneInfoVo createOrderH5SceneInfoVo = new CreateOrderH5SceneInfoVo(
                new CreateOrderH5SceneInfoDetailVo(
                        domain,
                        systemConfigService.getValueByKeyException(SysConfigConstants.CONFIG_KEY_SITE_NAME)
                )
        );
        vo.setScene_info(JSONObject.toJSONString(createOrderH5SceneInfoVo));

        vo.setTime_start(DateUtil.date().toString(DateConstants.DATE_TIME_FORMAT_NUM));
        DateTime cancelTime = DateUtil.offset(order.getCreateTime(), DateField.MINUTE, crmebConfig.getOrderCancelTime());
        String cancelStr = cancelTime.toString(DateConstants.DATE_TIME_FORMAT_NUM);
        vo.setTime_expire(cancelStr);
        String sign = WxPayUtil.getSign(vo, signKey);
        vo.setSign(sign);
        return vo;
    }

    /**
     * 商品购买后根据配置送券
     */
    private void autoSendCoupons(Order order) {
        List<OrderDetail> orderDetailList = orderDetailService.getByOrderNo(order.getOrderNo());
        if (CollUtil.isEmpty(orderDetailList)) {
            return;
        }
        List<CouponUser> couponUserList = CollUtil.newArrayList();
        Map<Integer, Boolean> couponMap = CollUtil.newHashMap();
        List<Integer> proIdList = orderDetailList.stream().map(OrderDetail::getProductId).distinct().collect(Collectors.toList());
        for (Integer proId : proIdList) {
            List<ProductCoupon> couponsForGiveUser = productCouponService.getListByProductId(proId);
            for (int i = 0; i < couponsForGiveUser.size(); ) {
                ProductCoupon productCoupon = couponsForGiveUser.get(i);
                MyRecord record = couponUserService.paySuccessGiveAway(productCoupon.getCouponId(), order.getUid());
                if (record.getStr("status").equals("fail")) {
                    logger.error(StrUtil.format("支付成功领取优惠券失败，失败原因：{}", record.getStr("errMsg")));
                    couponsForGiveUser.remove(i);
                    continue;
                }
                CouponUser couponUser = record.get("couponUser");
                couponUserList.add(couponUser);
                couponMap.put(couponUser.getCouponId(), record.getBoolean("isLimited"));
                i++;
            }
        }

        Boolean execute = transactionTemplate.execute(e -> {
            if (CollUtil.isNotEmpty(couponUserList)) {
                couponUserService.saveBatch(couponUserList);
                couponUserList.forEach(i -> couponService.deduction(i.getCouponId(), 1, couponMap.get(i.getCouponId())));
            }
            return Boolean.TRUE;
        });
        if (!execute) {
            logger.error(StrUtil.format("支付成功领取优惠券，更新数据库失败，订单编号：{}", order.getOrderNo()));
        }
    }
}

