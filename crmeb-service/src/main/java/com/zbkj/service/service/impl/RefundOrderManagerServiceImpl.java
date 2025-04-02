package com.zbkj.service.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.URLUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zbkj.common.constants.*;
import com.zbkj.common.exception.CrmebException;
import com.zbkj.common.model.admin.SystemAdmin;
import com.zbkj.common.model.merchant.MerchantAddress;
import com.zbkj.common.model.order.*;
import com.zbkj.common.model.user.User;
import com.zbkj.common.model.user.UserBalanceRecord;
import com.zbkj.common.page.CommonPage;
import com.zbkj.common.request.*;
import com.zbkj.common.response.*;
import com.zbkj.common.result.CommonResultCode;
import com.zbkj.common.result.OrderResultCode;
import com.zbkj.common.utils.*;
import com.zbkj.common.vo.DateLimitUtilVo;
import com.zbkj.common.vo.RefundOrderDetailOrderInfoVo;
import com.zbkj.common.vo.WxRefundVo;
import com.zbkj.service.dao.RefundOrderDao;
import com.zbkj.service.service.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * RefundOrderServiceImpl 接口实现 最深度的退款订单 service
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
public class RefundOrderManagerServiceImpl extends ServiceImpl<RefundOrderDao, RefundOrder> implements RefundOrderManagerService {
    private static final Logger logger = LoggerFactory.getLogger(RefundOrderManagerServiceImpl.class);
    @Resource
    private RefundOrderDao dao;

    @Autowired
    private OrderService orderService;
    @Autowired
    private OrderDetailService orderDetailService;
    @Autowired
    private RefundOrderInfoService refundOrderInfoService;
    @Autowired
    private TransactionTemplate transactionTemplate;
    @Autowired
    private WechatService wechatService;
    @Autowired
    private SystemConfigService systemConfigService;
    @Autowired
    private AliPayService aliPayService;
    @Autowired
    private UserService userService;
    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private UserBalanceRecordService userBalanceRecordService;
    @Autowired
    private MerchantAddressService merchantAddressService;
    @Autowired
    private RefundOrderStatusService refundOrderStatusService;
    @Autowired
    private MerchantOrderService merchantOrderService;
    @Autowired
    private AsyncService asyncService;

    /**
     * 商户端退款订单分页列表
     *
     * @param request          查询参数
     * @return PageInfo
     */
    @Override
    public PageInfo<MerchantRefundOrderPageResponse> getMerchantAdminPage(RefundOrderSearchRequest request, SystemAdmin systemAdmin) {
        Page<RefundOrder> page = PageHelper.startPage(request.getPage(), request.getLimit());
        Map<String, Object> map = CollUtil.newHashMap();
        if (StrUtil.isNotBlank(request.getContent())) {
            ValidateFormUtil.validatorUserCommonSearch(request);
            String keywords = URLUtil.decode(request.getContent());
            switch (request.getSearchType()) {
                case UserConstants.USER_SEARCH_TYPE_ALL:
                    map.put("keywords", keywords);
                    break;
                case UserConstants.USER_SEARCH_TYPE_UID:
                    map.put("uid", Integer.valueOf(request.getContent()));
                    break;
                case UserConstants.USER_SEARCH_TYPE_NICKNAME:
                    map.put("nickname", keywords);
                    break;
                case UserConstants.USER_SEARCH_TYPE_PHONE:
                    map.put("phone", request.getContent());
                    break;
            }
        }
        //时间范围
        if (StrUtil.isNotBlank(request.getDateLimit())) {
            DateLimitUtilVo dateLimit = CrmebDateUtil.getDateLimit(request.getDateLimit());
            //判断时间
            int compareDateResult = CrmebDateUtil.compareDate(dateLimit.getEndTime(), dateLimit.getStartTime(), DateConstants.DATE_FORMAT);
            if (compareDateResult == -1) {
                throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "开始时间不能大于结束时间！");
            }
            if (StrUtil.isNotBlank(dateLimit.getStartTime())) {
                map.put("startTime", dateLimit.getStartTime());
                map.put("endTime", dateLimit.getEndTime());
            }
        }
        map.put("merId",  systemAdmin.getMerId());
        if (StrUtil.isNotBlank(request.getRefundOrderNo())) {
            map.put("refundOrderNo", request.getRefundOrderNo());
        }
        if (StrUtil.isNotBlank(request.getOrderNo())) {
            map.put("orderNo", request.getOrderNo());
        }
        if (StrUtil.isNotBlank(request.getTrackingNumber())) {
            map.put("trackingNumber", request.getTrackingNumber());
        }
        if (ObjectUtil.isNotNull(request.getRefundStatus()) && request.getRefundStatus() != 9) {
            map.put("refundStatus", request.getRefundStatus());
        }
        List<MerchantRefundOrderPageResponse> responseList = dao.getMerchantAdminPage(map);
        if (CollUtil.isEmpty(responseList)) {
            return CommonPage.copyPageInfo(page, CollUtil.newArrayList());
        }
        responseList.forEach(response -> {
            // TODO 移动端商户管理需要区分
            RefundOrderInfo refundOrderInfo = refundOrderInfoService.getByRefundOrderNo(response.getRefundOrderNo());
            response.setRefundOrderInfo(refundOrderInfo);
        });
        return CommonPage.copyPageInfo(page, responseList);
    }

    /**
     * 获取商户端退款订单各状态数量
     *
     * @return RefundOrderCountItemResponse
     */
    @Override
    public RefundOrderCountItemResponse getMerchantOrderStatusNum(RefundOrderSearchRequest request, SystemAdmin systemAdmin) {
        return getOrderStatusNum(request, systemAdmin.getMerId());
    }

    /**
     * 备注退款单
     *
     * @param request 备注参数
     * @return Boolean
     */
    @Override
    public Boolean mark(RefundOrderRemarkRequest request, SystemAdmin systemAdmin) {
        RefundOrder refundOrder = getInfoException(request.getRefundOrderNo());
        if (!refundOrder.getMerId().equals(systemAdmin.getMerId())) {
            throw new CrmebException(OrderResultCode.REFUND_ORDER_NOT_EXIST);
        }
        refundOrder.setMerRemark(request.getRemark());
        return updateById(refundOrder);
    }

    /**
     * 微信退款
     * @param order 订单
     * @param refundOrderNo 退款单号
     * @param refundPrice 退款金额
     * @param totalPrice 订单支付总金额
     */
    private void wxRefund(Order order, String refundOrderNo, BigDecimal refundPrice, BigDecimal totalPrice) {
        String appId = "";
        String mchId = "";
        String signKey = "";
        String path = "";
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
                path = systemConfigService.getValueByKeyException(WeChatConstants.WECHAT_PAY_PUBLIC_CERTIFICATE_PATH);
                break;
            case PayConstants.PAY_CHANNEL_WECHAT_MINI:
            case PayConstants.PAY_CHANNEL_WECHAT_MINI_VIDEO:
                appId = systemConfigService.getValueByKeyException(WeChatConstants.WECHAT_MINI_APPID);
                mchId = systemConfigService.getValueByKeyException(WeChatConstants.WECHAT_PAY_MINI_MCHID);
                signKey = systemConfigService.getValueByKeyException(WeChatConstants.WECHAT_PAY_MINI_KEY);
                path = systemConfigService.getValueByKeyException(WeChatConstants.WECHAT_PAY_MINI_CERTIFICATE_PATH);
                break;
            case PayConstants.PAY_CHANNEL_WECHAT_APP_IOS:
            case PayConstants.PAY_CHANNEL_WECHAT_APP_ANDROID:
                appId = systemConfigService.getValueByKeyException(WeChatConstants.WECHAT_APP_APPID);
                mchId = systemConfigService.getValueByKeyException(WeChatConstants.WECHAT_PAY_APP_MCHID);
                signKey = systemConfigService.getValueByKeyException(WeChatConstants.WECHAT_PAY_APP_KEY);
                path = systemConfigService.getValueByKeyException(WeChatConstants.WECHAT_PAY_APP_CERTIFICATE_PATH);
                break;
        }
        String apiDomain = systemConfigService.getValueByKeyException(SysConfigConstants.CONFIG_KEY_API_URL);

        //统一下单数据
        WxRefundVo wxRefundVo = new WxRefundVo();
        wxRefundVo.setAppid(appId);
        wxRefundVo.setMch_id(mchId);
        wxRefundVo.setNonce_str(WxPayUtil.getNonceStr());
        wxRefundVo.setOut_trade_no(order.getOutTradeNo());
        wxRefundVo.setOut_refund_no(refundOrderNo);
        wxRefundVo.setTotal_fee(totalPrice.multiply(BigDecimal.TEN).multiply(BigDecimal.TEN).intValue());
        wxRefundVo.setRefund_fee(refundPrice.multiply(BigDecimal.TEN).multiply(BigDecimal.TEN).intValue());
        wxRefundVo.setNotify_url(apiDomain + PayConstants.WX_PAY_REFUND_NOTIFY_API_URI);
        String sign = WxPayUtil.getSign(wxRefundVo, signKey);
        wxRefundVo.setSign(sign);
        wechatService.payRefund(wxRefundVo, path);
    }

    /**
     * 设置订单状态
     *
     * @param order       订单
     */
    private void settingOrderStatus(Order order) {
        List<OrderDetail> orderDetailList = orderDetailService.getByOrderNo(order.getOrderNo());
        if (order.getStatus().equals(OrderConstants.ORDER_STATUS_PART_SHIPPING)) {
            long counted = orderDetailList.stream().filter(e -> e.getPayNum() > (e.getDeliveryNum() + e.getRefundNum())).count();
            if (counted <= 0) {
                order.setStatus(OrderConstants.ORDER_STATUS_WAIT_RECEIPT);
                if (order.getPayType().equals(PayConstants.PAY_TYPE_WE_CHAT) && order.getPayChannel().equals(PayConstants.PAY_CHANNEL_WECHAT_MINI)) {
                    String shippingSwitch = systemConfigService.getValueByKey(WeChatConstants.CONFIG_WECHAT_ROUTINE_SHIPPING_SWITCH);
                    if (StrUtil.isNotBlank(shippingSwitch) && shippingSwitch.equals("1")) {
                        asyncService.wechatSendUploadShipping(order.getOrderNo());
                    }
                }
            }
        }

        long count = orderDetailList.stream().filter(e -> e.getRefundNum().equals(0)).count();
        if (count == orderDetailList.size()) {
            order.setRefundStatus(OrderConstants.ORDER_REFUND_STATUS_NORMAL);
            return;
        }
        OrderDetail orderDetail = orderDetailList.stream().filter(e -> e.getApplyRefundNum() > 0).findAny().orElse(null);
        if (ObjectUtil.isNotNull(orderDetail)) {
            order.setRefundStatus(OrderConstants.ORDER_REFUND_STATUS_APPLY);
            return;
        }
        long refundCount = orderDetailList.stream().filter(e -> e.getRefundNum().equals(e.getPayNum())).count();
        if (refundCount == orderDetailList.size()) {
            order.setRefundStatus(OrderConstants.ORDER_REFUND_STATUS_ALL);
            return;
        }
        order.setRefundStatus(OrderConstants.ORDER_REFUND_STATUS_PORTION);
    }

    @Override
    public RefundOrder getInfoException(String refundOrderNo) {
        LambdaQueryWrapper<RefundOrder> lqw = Wrappers.lambdaQuery();
        lqw.eq(RefundOrder::getRefundOrderNo, refundOrderNo);
        lqw.last(" limit 1");
        RefundOrder refundOrder = dao.selectOne(lqw);
        if (ObjectUtil.isNull(refundOrder)) {
            throw new CrmebException(OrderResultCode.REFUND_ORDER_NOT_EXIST);
        }
        return refundOrder;
    }

    /**
     * 退款订单列表
     * @param request 搜索参数
     * @return List
     */
    @Override
    public PageInfo<RefundOrderResponse> getH5List(OrderAfterSalesSearchRequest request) {
        Integer userId = userService.getUserId();
        Map<String, Object> map = CollUtil.newHashMap();
        map.put("userId", userId);
        if (request.getType().equals(0)) {
            map.put("refundStatusStr", "0,2,4,5");
        }
        if (StrUtil.isNotBlank(request.getKeywords())) {
            map.put("keywords", URLUtil.decode(request.getKeywords()));
        }
        Page<Object> page = PageHelper.startPage(request.getPage(), request.getLimit());
        List<RefundOrderResponse> list = dao.findSearchList(map);
        return CommonPage.copyPageInfo(page, list);
    }

    /**
     * 退款订单详情（移动端）
     * @param refundOrderNo 退款订单号
     * @return RefundOrderInfoResponse
     */
    @Override
    public RefundOrderInfoResponse getRefundOrderDetailByRefundOrderNo(String refundOrderNo) {
        RefundOrderInfoResponse response = dao.getRefundOrderDetailByRefundOrderNo(refundOrderNo);
        response.setStatusList(refundOrderStatusService.findListByRefundOrderNo(refundOrderNo));
        return response;
    }

    /**
     * 商户端退款单详情响应对象
     * @param refundOrderNo 退款单号
     * @return 退款单详情
     */
    @Override
    public RefundOrderAdminDetailResponse getMerchantDetail(String refundOrderNo, SystemAdmin systemAdmin) {

        RefundOrder refundOrder = getInfoException(refundOrderNo);
        if (!systemAdmin.getMerId().equals(refundOrder.getMerId())) {
            throw new CrmebException(OrderResultCode.REFUND_ORDER_NOT_EXIST);
        }
        RefundOrderInfo refundOrderInfo = refundOrderInfoService.getByRefundOrderNo(refundOrderNo);

        RefundOrderAdminDetailResponse response = new RefundOrderAdminDetailResponse();
        response.setRefundOrderNo(refundOrder.getRefundOrderNo());
        response.setAfterSalesType(refundOrder.getAfterSalesType());
        response.setRefundStatus(refundOrder.getRefundStatus());
        response.setRefundPrice(refundOrder.getRefundPrice());
        response.setPayPrice(refundOrderInfo.getPayPrice());
        response.setPromoterType(refundOrder.getPromoterType());

        response.setProductName(refundOrderInfo.getProductName());
        response.setImage(refundOrderInfo.getImage());
        response.setSku(refundOrderInfo.getSku());
        response.setPrice(refundOrderInfo.getPrice());
        response.setRefundTime(refundOrder.getRefundTime());
        response.setPayNum(refundOrderInfo.getPayNum());
        response.setRefundUseIntegral(refundOrder.getRefundUseIntegral());
        response.setRefundGainIntegral(refundOrder.getRefundGainIntegral());
        response.setRefundFirstBrokerageFee(refundOrder.getRefundFirstBrokerageFee());
        response.setRefundSecondBrokerageFee(refundOrder.getRefundSecondBrokerageFee());
        response.setRefundFreightFee(refundOrder.getRefundFreightFee());
        response.setApplyRefundNum(refundOrder.getTotalNum());

        response.setReturnGoodsType(refundOrder.getReturnGoodsType());
        response.setRefundReasonWap(refundOrder.getRefundReasonWap());
        response.setRefundReasonWapImg(refundOrder.getRefundReasonWapImg());
        response.setRefundReasonWapExplain(refundOrder.getRefundReasonWapExplain());
        response.setReceiver(refundOrder.getReceiver());
        response.setReceiverPhone(refundOrder.getReceiverPhone());
        response.setReceiverAddressDetail(refundOrder.getReceiverAddressDetail());
        response.setExpressName(refundOrder.getExpressName());
        response.setTrackingNumber(refundOrder.getTrackingNumber());
        response.setTelephone(refundOrder.getTelephone());
        response.setIsUserRevoke(refundOrder.getIsUserRevoke());
        response.setRefundReason(refundOrder.getRefundReason());
        response.setMerRemark(refundOrder.getMerRemark());

        response.setStatusList(refundOrderStatusService.findListByRefundOrderNo(refundOrderNo));

        RefundOrderDetailOrderInfoVo orderInfoVo = getRefundOrderDetailOrderInfo(refundOrder.getOrderNo());
        response.setOrderInfoVo(orderInfoVo);
        return response;
    }

    /**
     * 获取退款单详情订单信息部分
     *
     * @param orderNo 订单编号
     */
    private RefundOrderDetailOrderInfoVo getRefundOrderDetailOrderInfo(String orderNo) {
        Order order = orderService.getByOrderNo(orderNo);
        MerchantOrder merchantOrder = merchantOrderService.getOneByOrderNo(orderNo);
        RefundOrderDetailOrderInfoVo orderInfoVo = new RefundOrderDetailOrderInfoVo();
        orderInfoVo.setOrderNo(orderNo);
        orderInfoVo.setUid(order.getUid());
        User user = userService.getById(order.getUid());
        orderInfoVo.setNickname(user.getNickname());
        orderInfoVo.setPhone(user.getPhone());
        orderInfoVo.setStatus(order.getStatus());
        orderInfoVo.setTotalNum(order.getTotalNum());
        orderInfoVo.setPaid(order.getPaid());
        orderInfoVo.setPayType(order.getPayType());
        orderInfoVo.setPayTime(order.getPayTime());
        orderInfoVo.setCreateTime(order.getCreateTime());
        if (order.getStatus() <= OrderConstants.ORDER_STATUS_WAIT_SHIPPING || order.getStatus().equals(OrderConstants.ORDER_STATUS_AWAIT_VERIFICATION)) {// 未发货
            orderInfoVo.setDeliveryNum(0);
        } else {
            if (!merchantOrder.getIsSplitDelivery()) {
                orderInfoVo.setDeliveryNum(merchantOrder.getTotalNum());
            } else {
                List<OrderDetail> orderDetailList = orderDetailService.getShipmentByOrderNo(orderNo);
                int deliveryNum = orderDetailList.stream().mapToInt(OrderDetail::getDeliveryNum).sum();
                orderInfoVo.setDeliveryNum(deliveryNum);
            }
        }

        orderInfoVo.setProTotalPrice(order.getProTotalPrice());
        orderInfoVo.setPayPrice(order.getPayPrice());
        orderInfoVo.setMerCouponPrice(order.getMerCouponPrice());
        orderInfoVo.setPlatCouponPrice(order.getPlatCouponPrice());
        orderInfoVo.setIntegralPrice(order.getIntegralPrice());
        orderInfoVo.setPayPostage(order.getPayPostage());
        orderInfoVo.setUseIntegral(order.getUseIntegral());
        orderInfoVo.setGainIntegral(order.getGainIntegral());
        orderInfoVo.setUserRemark(merchantOrder.getUserRemark());
        orderInfoVo.setMerchantRemark(merchantOrder.getMerchantRemark());
        orderInfoVo.setShippingType(merchantOrder.getShippingType());
        orderInfoVo.setRealName(merchantOrder.getRealName());
        orderInfoVo.setUserPhone(merchantOrder.getUserPhone());
        orderInfoVo.setUserAddress(merchantOrder.getUserAddress());
        orderInfoVo.setRefundStatus(order.getRefundStatus());
        orderInfoVo.setType(order.getType());
        orderInfoVo.setSecondType(order.getSecondType());
        orderInfoVo.setSvipDiscountPrice(order.getSvipDiscountPrice());
        return orderInfoVo;
    }

    /**
     * 平台端退款订单分页列表
     * @param request 查询参数
     * @return PageInfo
     */
    @Override
    public PageInfo<PlatformRefundOrderPageResponse> getPlatformAdminPage(RefundOrderSearchRequest request) {
        Page<RefundOrder> page = PageHelper.startPage(request.getPage(), request.getLimit());
        Map<String, Object> map = CollUtil.newHashMap();
        if (StrUtil.isNotBlank(request.getContent())) {
            ValidateFormUtil.validatorUserCommonSearch(request);
            String keywords = URLUtil.decode(request.getContent());
            switch (request.getSearchType()) {
                case UserConstants.USER_SEARCH_TYPE_ALL:
                    map.put("keywords", keywords);
                    break;
                case UserConstants.USER_SEARCH_TYPE_UID:
                    map.put("uid", Integer.valueOf(request.getContent()));
                    break;
                case UserConstants.USER_SEARCH_TYPE_NICKNAME:
                    map.put("nickname", keywords);
                    break;
                case UserConstants.USER_SEARCH_TYPE_PHONE:
                    map.put("phone", request.getContent());
                    break;
            }
        }
        //时间范围
        if (StrUtil.isNotBlank(request.getDateLimit())) {
            DateLimitUtilVo dateLimit = CrmebDateUtil.getDateLimit(request.getDateLimit());
            //判断时间
            int compareDateResult = CrmebDateUtil.compareDate(dateLimit.getEndTime(), dateLimit.getStartTime(), DateConstants.DATE_FORMAT);
            if (compareDateResult == -1) {
                throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "开始时间不能大于结束时间！");
            }
            if (StrUtil.isNotBlank(dateLimit.getStartTime())) {
                map.put("startTime", dateLimit.getStartTime());
                map.put("endTime", dateLimit.getEndTime());
            }
        }
        if (ObjectUtil.isNotNull(request.getMerId()) && request.getMerId() > 0) {
            map.put("merId", request.getMerId());
        }
        if (StrUtil.isNotBlank(request.getRefundOrderNo())) {
            map.put("refundOrderNo", request.getRefundOrderNo());
        }
        if (StrUtil.isNotBlank(request.getOrderNo())) {
            map.put("orderNo", request.getOrderNo());
        }
        if (StrUtil.isNotBlank(request.getTrackingNumber())) {
            map.put("trackingNumber", request.getTrackingNumber());
        }
        if (ObjectUtil.isNotNull(request.getRefundStatus()) && request.getRefundStatus() != 9) {
            map.put("refundStatus", request.getRefundStatus());
        }
        List<PlatformRefundOrderPageResponse> responseList = dao.getPlatformAdminPage(map);
        return CommonPage.copyPageInfo(page, responseList);
    }

    /**
     * 获取平台端退款订单各状态数量
     * @return RefundOrderCountItemResponse
     */
    @Override
    public RefundOrderCountItemResponse getPlatformOrderStatusNum(RefundOrderSearchRequest request) {
        return getOrderStatusNum(request, ObjectUtil.isNull(request.getMerId()) ? 0 : request.getMerId());
    }

    /**
     * 平台备注退款单
     * @param request 备注参数
     * @return Boolean
     */
    @Override
    public Boolean platformMark(RefundOrderRemarkRequest request) {
        RefundOrder refundOrder = getInfoException(request.getRefundOrderNo());
        refundOrder.setPlatformRemark(request.getRemark());
        return updateById(refundOrder);
    }

    /**fan
     * 平台端退款订单详情
     * @param refundOrderNo 退款单号
     * @return 退款单详情
     */
    @Override
    public RefundOrderAdminDetailResponse getPlatformDetail(String refundOrderNo) {
        RefundOrder refundOrder = getInfoException(refundOrderNo);
        RefundOrderInfo refundOrderInfo = refundOrderInfoService.getByRefundOrderNo(refundOrderNo);

        RefundOrderAdminDetailResponse response = new RefundOrderAdminDetailResponse();
        response.setRefundOrderNo(refundOrder.getRefundOrderNo());
        response.setAfterSalesType(refundOrder.getAfterSalesType());
        response.setRefundStatus(refundOrder.getRefundStatus());
        response.setRefundPrice(refundOrder.getRefundPrice());
        response.setPayPrice(refundOrderInfo.getPayPrice());
        response.setPromoterType(refundOrder.getPromoterType());

        response.setProductName(refundOrderInfo.getProductName());
        response.setImage(refundOrderInfo.getImage());
        response.setSku(refundOrderInfo.getSku());
        response.setPrice(refundOrderInfo.getPrice());
        response.setRefundTime(refundOrder.getRefundTime());
        response.setPayNum(refundOrderInfo.getPayNum());
        response.setRefundUseIntegral(refundOrder.getRefundUseIntegral());
        response.setRefundGainIntegral(refundOrder.getRefundGainIntegral());
        response.setRefundFirstBrokerageFee(refundOrder.getRefundFirstBrokerageFee());
        response.setRefundSecondBrokerageFee(refundOrder.getRefundSecondBrokerageFee());
        response.setRefundFreightFee(refundOrder.getRefundFreightFee());
        response.setApplyRefundNum(refundOrder.getTotalNum());

        response.setReturnGoodsType(refundOrder.getReturnGoodsType());
        response.setRefundReasonWap(refundOrder.getRefundReasonWap());
        response.setRefundReasonWapImg(refundOrder.getRefundReasonWapImg());
        response.setRefundReasonWapExplain(refundOrder.getRefundReasonWapExplain());
        response.setReceiver(refundOrder.getReceiver());
        response.setReceiverPhone(refundOrder.getReceiverPhone());
        response.setReceiverAddressDetail(refundOrder.getReceiverAddressDetail());
        response.setExpressName(refundOrder.getExpressName());
        response.setTrackingNumber(refundOrder.getTrackingNumber());
        response.setTelephone(refundOrder.getTelephone());
        response.setIsUserRevoke(refundOrder.getIsUserRevoke());
        response.setRefundReason(refundOrder.getRefundReason());

        response.setMerRemark(refundOrder.getMerRemark());
        response.setPlatformRemark(refundOrder.getPlatformRemark());

        response.setStatusList(refundOrderStatusService.findListByRefundOrderNo(refundOrderNo));

        RefundOrderDetailOrderInfoVo orderInfoVo = getRefundOrderDetailOrderInfo(refundOrder.getOrderNo());
        response.setOrderInfoVo(orderInfoVo);
        return response;
    }

    /**
     * 获取某一天的所有数据
     * @param merId 商户id，0为所有商户
     * @param date 日期：年-月-日
     * @return List
     */
    @Override
    public List<RefundOrder> findByDate(Integer merId, String date) {
        LambdaQueryWrapper<RefundOrder> lqw = Wrappers.lambdaQuery();
        if (merId > 0) {
            lqw.eq(RefundOrder::getMerId, merId);
        }
        lqw.eq(RefundOrder::getRefundStatus, OrderConstants.MERCHANT_REFUND_ORDER_STATUS_REFUND);
        lqw.apply("date_format(refund_time, '%Y-%m-%d') = {0}", date);
        return dao.selectList(lqw);
    }

    /**
     * 获取某一月的所有数据
     * @param merId 商户id，0为所有商户
     * @param month 日期：年-月
     * @return List
     */
    @Override
    public List<RefundOrder> findByMonth(Integer merId, String month) {
        LambdaQueryWrapper<RefundOrder> lqw = Wrappers.lambdaQuery();
        if (merId > 0) {
            lqw.eq(RefundOrder::getMerId, merId);
        }
        lqw.eq(RefundOrder::getRefundStatus, OrderConstants.MERCHANT_REFUND_ORDER_STATUS_REFUND);
        lqw.apply("date_format(refund_time, '%Y-%m') = {0}", month);
        return dao.selectList(lqw);
    }

    /**
     * 根据日期获取退款订单数量
     * @param date 日期
     * @return Integer
     */
    @Override
    public Integer getRefundOrderNumByDate(String date) {
        LambdaQueryWrapper<RefundOrder> lqw = Wrappers.lambdaQuery();
        lqw.select(RefundOrder::getId);
        lqw.eq(RefundOrder::getRefundStatus, OrderConstants.MERCHANT_REFUND_ORDER_STATUS_REFUND);
        lqw.apply("date_format(update_time, '%Y-%m-%d') = {0}", date);
        return dao.selectCount(lqw);
    }

    /**
     * 根据日期获取退款订单金额
     * @param date 日期
     * @return Integer
     */
    @Override
    public BigDecimal getRefundOrderAmountByDate(String date) {
        LambdaQueryWrapper<RefundOrder> lqw = Wrappers.lambdaQuery();
        lqw.select(RefundOrder::getRefundPrice);
        lqw.eq(RefundOrder::getRefundStatus, OrderConstants.MERCHANT_REFUND_ORDER_STATUS_REFUND);
        lqw.apply("date_format(update_time, '%Y-%m-%d') = {0}", date);
        List<RefundOrder> orderList = dao.selectList(lqw);
        if (CollUtil.isEmpty(orderList)) {
            return BigDecimal.ZERO;
        }
        return orderList.stream().map(RefundOrder::getRefundPrice).reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * 获取退款中（申请）订单数量
     */
    @Override
    public Integer getRefundingCount(Integer userId) {
        LambdaQueryWrapper<RefundOrder> lqw = Wrappers.lambdaQuery();
        lqw.eq(RefundOrder::getUid, userId);
        List<Integer> statusList = new ArrayList<>();
        statusList.add(OrderConstants.MERCHANT_REFUND_ORDER_STATUS_APPLY);
        statusList.add(OrderConstants.MERCHANT_REFUND_ORDER_STATUS_REFUNDING);
        statusList.add(OrderConstants.MERCHANT_REFUND_ORDER_STATUS_RETURN_GOODS);
        statusList.add(OrderConstants.MERCHANT_REFUND_ORDER_STATUS_AWAIT_RECEIVING);
        lqw.in(RefundOrder::getRefundStatus, statusList);
        return dao.selectCount(lqw);
    }

    /**
     * 获取退款单详情
     * @param refundOrderNo 退款单号
     */
    @Override
    public RefundOrder getByRefundOrderNo(String refundOrderNo) {
        LambdaQueryWrapper<RefundOrder> lqw = Wrappers.lambdaQuery();
        lqw.eq(RefundOrder::getRefundOrderNo, refundOrderNo);
        lqw.last(" limit 1");
        return dao.selectOne(lqw);
    }

    /**
     * 待退款订单数量
     * @return Integer
     */
    @Override
    public Integer getAwaitAuditNum(Integer merId) {
        LambdaQueryWrapper<RefundOrder> lqw = Wrappers.lambdaQuery();
        lqw.select(RefundOrder::getId);
        lqw.eq(RefundOrder::getRefundStatus, OrderConstants.MERCHANT_REFUND_ORDER_STATUS_APPLY);
        if (merId > 0) {
            lqw.eq(RefundOrder::getMerId, merId);
        }
        return dao.selectCount(lqw);
    }

    /**
     * 撤销退款单
     * @param refundOrderNo 退款单号
     */
    @Override
    public Boolean revoke(String refundOrderNo) {
        RefundOrder refundOrder = getByRefundOrderNo(refundOrderNo);
        if (ObjectUtil.isNull(refundOrder)) {
            throw new CrmebException(OrderResultCode.REFUND_ORDER_NOT_EXIST);
        }
        if (!(refundOrder.getRefundStatus() == 0 || refundOrder.getRefundStatus() == 4 || refundOrder.getRefundStatus() == 5)) {
            throw new CrmebException(OrderResultCode.REFUND_ORDER_STATUS_ABNORMAL);
        }
        Order order = orderService.getByOrderNo(refundOrder.getOrderNo());
        if (ObjectUtil.isNull(order)) {
            throw new CrmebException(OrderResultCode.ORDER_NOT_EXIST.setMessage("退款单关联的订单不存在"));
        }
        refundOrder.setRefundStatus(OrderConstants.MERCHANT_REFUND_ORDER_STATUS_REVOKE);
        refundOrder.setIsUserRevoke(true);
        RefundOrderInfo refundOrderInfo = refundOrderInfoService.getByRefundOrderNo(refundOrder.getRefundOrderNo());
        OrderDetail orderDetail = orderDetailService.getById(refundOrderInfo.getOrderDetailId());
        orderDetail.setApplyRefundNum(orderDetail.getApplyRefundNum() - refundOrderInfo.getApplyRefundNum());
        Boolean execute = transactionTemplate.execute(e -> {
            updateById(refundOrder);
            orderDetailService.updateById(orderDetail);
            refundOrderStatusService.add(refundOrderNo, RefundOrderConstants.REFUND_ORDER_LOG_REVOKE, "用户撤销退款");
            return Boolean.TRUE;
        });
        if (execute) {
            // 设置订单退款状态
            settingOrderStatus(order);
            orderService.updateById(order);
        }
        return execute;
    }

    /**
     * 退款单审核
     * @param request 审核参数
     * @return 审核结果
     */
    @Override
    public Boolean audit(OrderRefundAuditRequest request, SystemAdmin systemAdmin) {
        validatedAuditRequest(request);

        RefundOrder refundOrder = getInfoException(request.getRefundOrderNo());
        if (!refundOrder.getRefundStatus().equals(OrderConstants.MERCHANT_REFUND_ORDER_STATUS_APPLY)) {
            throw new CrmebException(OrderResultCode.REFUND_ORDER_STATUS_ABNORMAL);
        }
        if (!refundOrder.getMerId().equals(systemAdmin.getMerId())) {
            throw new CrmebException(OrderResultCode.REFUND_ORDER_NOT_EXIST);
        }
        Order order = orderService.getByOrderNo(refundOrder.getOrderNo());
        if (ObjectUtil.isNull(order)) {
            throw new CrmebException(OrderResultCode.ORDER_NOT_EXIST.setMessage("退款单关联的订单不存在"));
        }
        if (request.getAuditType().equals(RefundOrderConstants.REFUND_ORDER_AUDIT_REFUSE)) {
            refundOrder.setRefundStatus(OrderConstants.MERCHANT_REFUND_ORDER_STATUS_REJECT);
            refundOrder.setRefundReason(request.getReason());
            RefundOrderInfo refundOrderInfo = refundOrderInfoService.getByRefundOrderNo(refundOrder.getRefundOrderNo());
            OrderDetail orderDetail = orderDetailService.getById(refundOrderInfo.getOrderDetailId());
            orderDetail.setApplyRefundNum(orderDetail.getApplyRefundNum() - refundOrderInfo.getApplyRefundNum());
            Boolean execute = transactionTemplate.execute(e -> {
                updateById(refundOrder);
                orderDetailService.updateById(orderDetail);
                refundOrderStatusService.add(refundOrder.getRefundOrderNo(), RefundOrderConstants.REFUND_ORDER_LOG_AUDIT, "售后单商家审核拒绝");
                return Boolean.TRUE;
            });
            if (execute) {
                // 设置订单退款状态
                settingOrderStatus(order);
                orderService.updateById(order);
            }
            return execute;
        }
        if (refundOrder.getAfterSalesType().equals(RefundOrderConstants.REFUND_ORDER_AFTER_SALES_TYPE_PRICE_PRODUCT)) {
            if (refundOrder.getReturnGoodsType().equals(1)) {
                if (ObjectUtil.isNull(request.getMerAddressId())) {
                    throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "请选择商家退货地址");
                }
                MerchantAddress merchantAddress = merchantAddressService.getByIdException(request.getMerAddressId());
                refundOrder.setReceiver(merchantAddress.getReceiverName());
                refundOrder.setReceiverPhone(merchantAddress.getReceiverPhone());
                refundOrder.setReceiverAddressDetail(merchantAddress.getDetail());
            }
            refundOrder.setRefundStatus(OrderConstants.MERCHANT_REFUND_ORDER_STATUS_RETURN_GOODS);

            return transactionTemplate.execute(e -> {
                updateById(refundOrder);
                refundOrderStatusService.add(refundOrder.getRefundOrderNo(), RefundOrderConstants.REFUND_ORDER_LOG_AUDIT, "售后单商家审核通过");
                return Boolean.TRUE;
            });
        }
        Boolean refundResult = refundPrice(refundOrder, order);
        if (refundResult) {
            refundOrderStatusService.add(refundOrder.getRefundOrderNo(), RefundOrderConstants.REFUND_ORDER_LOG_AUDIT, "售后单商家审核通过");
        }
        return refundResult;
    }

    /**
     * 退款单收到退货
     * @param refundOrderNo 退款单号
     */
    @Override
    public Boolean receiving(String refundOrderNo, SystemAdmin systemAdmin) {
        RefundOrder refundOrder = getInfoException(refundOrderNo);
        if (refundOrder.getRefundStatus().equals(OrderConstants.MERCHANT_REFUND_ORDER_STATUS_REJECT)
            || refundOrder.getRefundStatus().equals(OrderConstants.MERCHANT_REFUND_ORDER_STATUS_REFUNDING)
            || refundOrder.getRefundStatus().equals(OrderConstants.MERCHANT_REFUND_ORDER_STATUS_REFUND)
            || refundOrder.getRefundStatus().equals(OrderConstants.MERCHANT_REFUND_ORDER_STATUS_REVOKE)) {
            throw new CrmebException(OrderResultCode.REFUND_ORDER_STATUS_ABNORMAL);
        }
        if (!refundOrder.getMerId().equals(systemAdmin.getMerId())) {
            throw new CrmebException(OrderResultCode.REFUND_ORDER_NOT_EXIST);
        }

        Order order = orderService.getByOrderNo(refundOrder.getOrderNo());
        Boolean refundResult = refundPrice(refundOrder, order);
        if (refundResult) {
            refundOrderStatusService.add(refundOrder.getRefundOrderNo(), RefundOrderConstants.REFUND_ORDER_LOG_RECEIVING, "售后单商家确认收货");
        }
        return refundResult;
    }

    /**
     * 平台强制退款
     * @param refundOrderNo 退款单号
     */
    @Override
    public Boolean compulsoryRefund(String refundOrderNo, SystemAdmin systemAdmin) {
        RefundOrder refundOrder = getInfoException(refundOrderNo);
        if (!refundOrder.getRefundStatus().equals(OrderConstants.MERCHANT_REFUND_ORDER_STATUS_APPLY)
                && !refundOrder.getRefundStatus().equals(OrderConstants.MERCHANT_REFUND_ORDER_STATUS_AWAIT_RECEIVING)) {
            throw new CrmebException(OrderResultCode.REFUND_ORDER_STATUS_ABNORMAL);
        }
        Order order = orderService.getByOrderNo(refundOrder.getOrderNo());
        refundOrder.setIsCompulsoryRefund(true);
        refundOrder.setCompulsoryAdminId(systemAdmin.getId());
        Boolean refundResult = refundPrice(refundOrder, order);
        if (refundResult) {
            refundOrderStatusService.add(refundOrder.getRefundOrderNo(), RefundOrderConstants.REFUND_ORDER_LOG_COMPULSORY, "平台强制退款");
        }
        return refundResult;
    }

    /**
     * 退款单-商家拒绝收货退款
     * @param request 拒绝收货请求对象
     */
    @Override
    public Boolean receivingReject(RejectReceivingRequest request, SystemAdmin systemAdmin) {
        RefundOrder refundOrder = getInfoException(request.getRefundOrderNo());
        if (!refundOrder.getMerId().equals(systemAdmin.getMerId())) {
            throw new CrmebException(OrderResultCode.REFUND_ORDER_NOT_EXIST);
        }
        if (!refundOrder.getRefundStatus().equals(OrderConstants.MERCHANT_REFUND_ORDER_STATUS_AWAIT_RECEIVING)) {
            throw new CrmebException(OrderResultCode.REFUND_ORDER_STATUS_ABNORMAL);
        }
        refundOrder.setRefundStatus(OrderConstants.MERCHANT_REFUND_ORDER_STATUS_REJECT);
        refundOrder.setRefundReason(request.getReason());
        RefundOrderInfo refundOrderInfo = refundOrderInfoService.getByRefundOrderNo(refundOrder.getRefundOrderNo());
        Order order = orderService.getByOrderNo(refundOrder.getOrderNo());
        OrderDetail orderDetail = orderDetailService.getById(refundOrderInfo.getOrderDetailId());
        orderDetail.setApplyRefundNum(orderDetail.getApplyRefundNum() - refundOrderInfo.getApplyRefundNum());
        Boolean execute = transactionTemplate.execute(e -> {
            updateById(refundOrder);
            orderDetailService.updateById(orderDetail);
            refundOrderStatusService.add(refundOrder.getRefundOrderNo(), RefundOrderConstants.REFUND_ORDER_LOG_REJECTION_GOODS, "售后单商家拒绝收货");
            return Boolean.TRUE;
        });
        if (execute) {
            // 设置订单退款状态
            settingOrderStatus(order);
            orderService.updateById(order);
        }
        return execute;
    }

    /**
     * 自动撤销退款
     */
    @Override
    public void autoRevoke() {
        List<RefundOrder> refundOrderList = findCanAutoRevokeOrderList();
        if (CollUtil.isEmpty(refundOrderList)) {
            return;
        }
        for (RefundOrder refundOrder : refundOrderList) {
            Order order = orderService.getByOrderNo(refundOrder.getOrderNo());
            if (ObjectUtil.isNull(order)) {
                continue;
            }
            refundOrder.setRefundStatus(OrderConstants.MERCHANT_REFUND_ORDER_STATUS_REVOKE);
            refundOrder.setIsUserRevoke(false);
            RefundOrderInfo refundOrderInfo = refundOrderInfoService.getByRefundOrderNo(refundOrder.getRefundOrderNo());
            OrderDetail orderDetail = orderDetailService.getById(refundOrderInfo.getOrderDetailId());
            orderDetail.setApplyRefundNum(orderDetail.getApplyRefundNum() - refundOrderInfo.getApplyRefundNum());
            updateById(refundOrder);
            orderDetailService.updateById(orderDetail);
            refundOrderStatusService.add(refundOrder.getRefundOrderNo(), RefundOrderConstants.REFUND_ORDER_LOG_REVOKE, "系统自动撤销退款单");
            settingOrderStatus(order);
            orderService.updateById(order);
        }
    }

    /**
     * 查询支付宝退款定时任务
     */
    @Override
    public Boolean queryAliPayRefund(String refundOrderNo) {
        RefundOrder refundOrder = getByRefundOrderNo(refundOrderNo);
        if (ObjectUtil.isNull(refundOrder)) {
            logger.error("查询支付宝退款 : 订单信息不存在==》{}", refundOrderNo);
            return false;
        }
        if (refundOrder.getRefundStatus().equals(OrderConstants.MERCHANT_REFUND_ORDER_STATUS_REFUND)) {
            return true;
        }
        if (!refundOrder.getRefundStatus().equals(OrderConstants.MERCHANT_REFUND_ORDER_STATUS_REFUNDING)) {
            logger.error("查询支付宝退款 : 退款单状态异常==> {}", refundOrderNo);
            return false;
        }
        Order order = orderService.getByOrderNo(refundOrder.getOrderNo());
        Boolean queryResultBoolean = aliPayService.queryRefund(order.getPlatOrderNo(), refundOrder.getOutRefundNo());
        if (!queryResultBoolean) {
            logger.error("查询支付宝退款 : 支付宝查询失败==> {}", refundOrderNo);
            return false;
        }
        refundOrder.setRefundStatus(OrderConstants.MERCHANT_REFUND_ORDER_STATUS_REFUND);
        boolean update = updateById(refundOrder);
        if (!update) {
            logger.error("查询支付宝退款 : 支付宝退款订单更新失败==> {}", refundOrderNo);
            return false;
        }
        // 退款task
        return redisUtil.lPush(TaskConstants.ORDER_TASK_REDIS_KEY_AFTER_REFUND_BY_USER, refundOrderNo);
    }

    /**
     * 订单退款是否包含用户退款
     * @param orderNo 订单编号
     */
    @Override
    public Boolean isOrderContainUserRefund(String orderNo) {
        LambdaQueryWrapper<RefundOrder> lqw = Wrappers.lambdaQuery();
        lqw.eq(RefundOrder::getOrderNo, orderNo);
        lqw.notIn(RefundOrder::getRefundStatus, OrderConstants.MERCHANT_REFUND_ORDER_STATUS_REJECT, OrderConstants.MERCHANT_REFUND_ORDER_STATUS_REVOKE);
        lqw.eq(RefundOrder::getPromoterType, "user");
        Integer count = dao.selectCount(lqw);
        return count > 0;
    }

    /**
     * 商户直接退款
     * @param order 订单
     * @param orderDetailList 需要退款的订单详情列表
     */
    @Override
    public Boolean merchantDirectRefund(Order order, List<OrderDetail> orderDetailList) {
        // 生成退款单
        String outRefundNo = CrmebUtil.getOrderNo(OrderConstants.ORDER_PREFIX_REFUND);
        MerchantOrder merchantOrder = merchantOrderService.getOneByOrderNo(order.getOrderNo());
        List<RefundOrder> refundOrderList = new ArrayList<>();
        List<RefundOrderInfo> refundOrderInfoList = new ArrayList<>();
        BigDecimal totalRefundPrice = BigDecimal.ZERO;
        for (OrderDetail orderDetail : orderDetailList) {
            RefundOrder refundOrder = new RefundOrder();
            refundOrder.setRefundOrderNo(CrmebUtil.getOrderNo(OrderConstants.ORDER_PREFIX_REFUND));
            refundOrder.setOrderNo(order.getOrderNo());
            refundOrder.setMerId(order.getMerId());
            refundOrder.setUid(order.getUid());
            refundOrder.setRealName(merchantOrder.getRealName());
            refundOrder.setUserPhone(merchantOrder.getUserPhone());
            refundOrder.setUserAddress(merchantOrder.getUserAddress());
            refundOrder.setTotalNum(orderDetail.getApplyRefundNum());
            refundOrder.setRefundReasonWap("");
            refundOrder.setRefundReasonWapImg("");
            refundOrder.setRefundReasonWapExplain("");
            refundOrder.setRefundStatus(OrderConstants.MERCHANT_REFUND_ORDER_STATUS_APPLY);
            refundOrder.setAfterSalesType(1);
            refundOrder.setReturnGoodsType(0);
            refundOrder.setPromoterType("merchant");

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
            refundOrderInfo.setApplyRefundNum(orderDetail.getApplyRefundNum());

            BigDecimal refundPrice;
            if (orderDetail.getPayNum().equals(refundOrderInfo.getApplyRefundNum())) {
                // sku整退
                refundPrice = orderDetail.getPayPrice();
                orderDetail.setRefundPrice(refundPrice);
                refundOrderInfo.setRefundPrice(refundPrice);
                if (orderDetail.getUseIntegral() > 0) {
                    orderDetail.setRefundUseIntegral(orderDetail.getUseIntegral());
                    orderDetail.setRefundIntegralPrice(orderDetail.getIntegralPrice());
                    refundOrderInfo.setRefundUseIntegral(orderDetail.getRefundUseIntegral());
                    refundOrderInfo.setRefundIntegralPrice(orderDetail.getRefundIntegralPrice());
                }
                if (orderDetail.getGainIntegral() > 0) {
                    orderDetail.setRefundGainIntegral(orderDetail.getGainIntegral());
                    refundOrderInfo.setRefundGainIntegral(orderDetail.getRefundGainIntegral());
                }
                if (orderDetail.getFirstBrokerageFee().compareTo(BigDecimal.ZERO) > 0) {
                    orderDetail.setRefundFirstBrokerageFee(orderDetail.getFirstBrokerageFee());
                    refundOrderInfo.setRefundFirstBrokerageFee(orderDetail.getRefundFirstBrokerageFee());
                }
                if (orderDetail.getSecondBrokerageFee().compareTo(BigDecimal.ZERO) > 0) {
                    orderDetail.setRefundSecondBrokerageFee(orderDetail.getSecondBrokerageFee());
                    refundOrderInfo.setRefundSecondBrokerageFee(orderDetail.getRefundSecondBrokerageFee());
                }
                if (orderDetail.getPlatCouponPrice().compareTo(BigDecimal.ZERO) > 0) {
                    orderDetail.setRefundPlatCouponPrice(orderDetail.getPlatCouponPrice());
                    refundOrderInfo.setRefundPlatCouponPrice(orderDetail.getPlatCouponPrice());
                }
                if (orderDetail.getFreightFee().compareTo(BigDecimal.ZERO) > 0) {
                    refundOrderInfo.setRefundFreightFee(orderDetail.getFreightFee());
                    orderDetail.setRefundFreightFee(orderDetail.getFreightFee());
                }
                refundOrderInfo.setMerchantRefundPrice(refundPrice);
            } else if (orderDetail.getPayNum() == (orderDetail.getRefundNum() + refundOrderInfo.getApplyRefundNum())) { // sku分退
                refundPrice = orderDetail.getPayPrice().subtract(orderDetail.getRefundPrice());
                // sku最后一部分退款
                orderDetail.setRefundPrice(orderDetail.getPayPrice());
                refundOrderInfo.setRefundPrice(refundPrice);
                refundOrderInfo.setMerchantRefundPrice(refundPrice);
                if (orderDetail.getUseIntegral() > 0) {
                    refundOrderInfo.setRefundUseIntegral(orderDetail.getUseIntegral() - orderDetail.getRefundUseIntegral());
                    refundOrderInfo.setRefundIntegralPrice(orderDetail.getIntegralPrice().subtract(orderDetail.getRefundIntegralPrice()));
                    orderDetail.setRefundUseIntegral(orderDetail.getUseIntegral());
                    orderDetail.setRefundIntegralPrice(orderDetail.getIntegralPrice());
                }
                if (orderDetail.getGainIntegral() > 0) {
                    refundOrderInfo.setRefundGainIntegral(orderDetail.getGainIntegral() - orderDetail.getRefundGainIntegral());
                    orderDetail.setRefundGainIntegral(orderDetail.getGainIntegral());
                }
                if (orderDetail.getFirstBrokerageFee().compareTo(BigDecimal.ZERO) > 0) {
                    refundOrderInfo.setRefundFirstBrokerageFee(orderDetail.getFirstBrokerageFee().subtract(orderDetail.getRefundFirstBrokerageFee()));
                    orderDetail.setRefundFirstBrokerageFee(orderDetail.getFirstBrokerageFee());
                }
                if (orderDetail.getSecondBrokerageFee().compareTo(BigDecimal.ZERO) > 0) {
                    refundOrderInfo.setRefundSecondBrokerageFee(orderDetail.getSecondBrokerageFee().subtract(orderDetail.getRefundSecondBrokerageFee()));
                    orderDetail.setRefundSecondBrokerageFee(orderDetail.getSecondBrokerageFee());
                }
                if (orderDetail.getPlatCouponPrice().compareTo(BigDecimal.ZERO) > 0) {
                    refundOrderInfo.setRefundPlatCouponPrice(orderDetail.getPlatCouponPrice().subtract(orderDetail.getRefundPlatCouponPrice()));
                    orderDetail.setRefundPlatCouponPrice(orderDetail.getPlatCouponPrice());
                }
                if (orderDetail.getFreightFee().compareTo(BigDecimal.ZERO) > 0) {
                    refundOrderInfo.setRefundFreightFee(orderDetail.getFreightFee().subtract(orderDetail.getRefundFreightFee()));
                    orderDetail.setRefundFreightFee(orderDetail.getFreightFee());
                }
            } else {
                // sku非最后一部分退款
                BigDecimal ratio = new BigDecimal(refundOrderInfo.getApplyRefundNum()).divide(new BigDecimal(orderDetail.getPayNum()), 10, BigDecimal.ROUND_HALF_UP);
                refundPrice = orderDetail.getPayPrice().multiply(ratio).setScale(2, BigDecimal.ROUND_HALF_UP);
                orderDetail.setRefundPrice(orderDetail.getRefundPrice().add(refundPrice));
                refundOrderInfo.setRefundPrice(refundPrice);
                refundOrderInfo.setMerchantRefundPrice(refundPrice);

                if (orderDetail.getUseIntegral() > 0) {
                    refundOrderInfo.setRefundUseIntegral(new BigDecimal(orderDetail.getUseIntegral().toString()).multiply(ratio).setScale(0, BigDecimal.ROUND_HALF_UP).intValue());
                    refundOrderInfo.setRefundIntegralPrice(orderDetail.getIntegralPrice().multiply(ratio).setScale(2, BigDecimal.ROUND_HALF_UP));
                    orderDetail.setRefundUseIntegral(orderDetail.getRefundUseIntegral() + refundOrderInfo.getRefundUseIntegral());
                    orderDetail.setRefundIntegralPrice(orderDetail.getRefundIntegralPrice().add(refundOrderInfo.getRefundIntegralPrice()));
                }
                if (orderDetail.getGainIntegral() > 0) {
                    refundOrderInfo.setRefundGainIntegral(new BigDecimal(orderDetail.getGainIntegral().toString()).multiply(ratio).setScale(0, BigDecimal.ROUND_HALF_UP).intValue());
                    orderDetail.setRefundGainIntegral(orderDetail.getRefundGainIntegral() + refundOrderInfo.getRefundGainIntegral());
                }
                if (orderDetail.getFirstBrokerageFee().compareTo(BigDecimal.ZERO) > 0) {
                    refundOrderInfo.setRefundFirstBrokerageFee(orderDetail.getFirstBrokerageFee().multiply(ratio).setScale(2, BigDecimal.ROUND_HALF_UP));
                    orderDetail.setRefundFirstBrokerageFee(orderDetail.getRefundFirstBrokerageFee().add(refundOrderInfo.getRefundFirstBrokerageFee()));
                }
                if (orderDetail.getSecondBrokerageFee().compareTo(BigDecimal.ZERO) > 0) {
                    refundOrderInfo.setRefundSecondBrokerageFee(orderDetail.getSecondBrokerageFee().multiply(ratio).setScale(2, BigDecimal.ROUND_HALF_UP));
                    orderDetail.setRefundSecondBrokerageFee(orderDetail.getRefundSecondBrokerageFee().add(refundOrderInfo.getRefundSecondBrokerageFee()));
                }
                if (orderDetail.getPlatCouponPrice().compareTo(BigDecimal.ZERO) > 0) {
                    refundOrderInfo.setRefundPlatCouponPrice(orderDetail.getPlatCouponPrice().multiply(ratio).setScale(2, BigDecimal.ROUND_HALF_UP));
                    orderDetail.setRefundPlatCouponPrice(orderDetail.getRefundPlatCouponPrice().add(refundOrderInfo.getRefundPlatCouponPrice()));
                }
                if (orderDetail.getFreightFee().compareTo(BigDecimal.ZERO) > 0) {
                    refundOrderInfo.setRefundFreightFee(orderDetail.getFreightFee().multiply(ratio).setScale(2, BigDecimal.ROUND_HALF_UP));
                    orderDetail.setRefundFreightFee(orderDetail.getRefundFreightFee().add(refundOrderInfo.getRefundFreightFee()));
                }
            }

            refundOrder.setRefundPrice(refundPrice);
            refundOrder.setMerchantRefundPrice(refundOrderInfo.getMerchantRefundPrice());
            refundOrder.setRefundUseIntegral(refundOrderInfo.getRefundUseIntegral());
            refundOrder.setRefundIntegralPrice(refundOrderInfo.getRefundIntegralPrice());
            refundOrder.setRefundGainIntegral(refundOrderInfo.getRefundGainIntegral());
            refundOrder.setRefundFirstBrokerageFee(refundOrderInfo.getRefundFirstBrokerageFee());
            refundOrder.setRefundSecondBrokerageFee(refundOrderInfo.getRefundSecondBrokerageFee());
            refundOrder.setRefundPayType(order.getPayType());
            refundOrder.setRefundPlatCouponPrice(refundOrderInfo.getRefundPlatCouponPrice());
            refundOrder.setRefundFreightFee(refundOrderInfo.getRefundFreightFee());

            orderDetail.setApplyRefundNum(orderDetail.getApplyRefundNum() - refundOrderInfo.getApplyRefundNum());
            orderDetail.setRefundNum(orderDetail.getRefundNum() + refundOrderInfo.getApplyRefundNum());

            refundOrder.setOutRefundNo(outRefundNo);
            refundOrder.setRefundStatus(OrderConstants.MERCHANT_REFUND_ORDER_STATUS_REFUNDING);

            refundOrderList.add(refundOrder);
            refundOrderInfoList.add(refundOrderInfo);
            totalRefundPrice = totalRefundPrice.add(refundPrice);
        }

        //退款
        if (order.getPayType().equals(PayConstants.PAY_TYPE_WE_CHAT) && totalRefundPrice.compareTo(BigDecimal.ZERO) > 0) {
            try {
                Order platOrder = orderService.getByOrderNo(order.getPlatOrderNo());
                wxRefund(order, outRefundNo, totalRefundPrice, platOrder.getPayPrice());
            } catch (Exception e) {
                e.printStackTrace();
                throw new CrmebException("商家主动退款，微信申请退款失败！" + e.getMessage());
            }
        }
        Boolean aliPayIsRefund = false;
        if (order.getPayType().equals(PayConstants.PAY_TYPE_ALI_PAY) && totalRefundPrice.compareTo(BigDecimal.ZERO) > 0) {
            try {
                aliPayIsRefund = aliPayService.refund(order.getPlatOrderNo(), outRefundNo, "商家主动退款", totalRefundPrice);
            } catch (Exception e) {
                e.printStackTrace();
                throw new CrmebException("商家主动退款，支付宝申请退款失败！" + e.getMessage());
            }
        }

        User user = userService.getById(order.getUid());
        refundOrderList.forEach(refundOrder -> {
            refundOrder.setRefundTime(DateUtil.date());
        });
        Boolean finalAliPayIsRefund = aliPayIsRefund;
        BigDecimal finalTotalRefundPrice = totalRefundPrice;
        Boolean execute = transactionTemplate.execute(e -> {
            orderDetailService.updateBatchById(orderDetailList);

            if (order.getPayType().equals(PayConstants.PAY_TYPE_YUE)) {
                refundOrderList.forEach(refundOrder -> {
                    refundOrder.setRefundStatus(OrderConstants.MERCHANT_REFUND_ORDER_STATUS_REFUND);
                });
                for (RefundOrder refundOrder : refundOrderList) {
                    if (refundOrder.getRefundPrice().compareTo(BigDecimal.ZERO) > 0) {
                        // 更新用户金额
                        userService.updateNowMoney(order.getUid(), refundOrder.getRefundPrice(), Constants.OPERATION_TYPE_ADD);
                        // 用户余额记录
                        UserBalanceRecord userBalanceRecord = new UserBalanceRecord();
                        userBalanceRecord.setUid(user.getId());
                        userBalanceRecord.setLinkId(refundOrder.getRefundOrderNo());
                        userBalanceRecord.setLinkType(BalanceRecordConstants.BALANCE_RECORD_LINK_TYPE_ORDER);
                        userBalanceRecord.setType(BalanceRecordConstants.BALANCE_RECORD_TYPE_ADD);
                        userBalanceRecord.setAmount(refundOrder.getRefundPrice());
                        userBalanceRecord.setBalance(user.getNowMoney().add(refundOrder.getRefundPrice()));
                        userBalanceRecord.setRemark(StrUtil.format(BalanceRecordConstants.BALANCE_RECORD_REMARK_ORDER_REFUND, refundOrder.getRefundPrice()));
                        userBalanceRecordService.save(userBalanceRecord);
                    }
                }
            }
            if (order.getPayType().equals(PayConstants.PAY_TYPE_ALI_PAY) && finalTotalRefundPrice.compareTo(BigDecimal.ZERO) > 0 && finalAliPayIsRefund) {
                refundOrderList.forEach(refundOrder -> {
                    refundOrder.setRefundStatus(OrderConstants.MERCHANT_REFUND_ORDER_STATUS_REFUND);
                });
            }
            saveBatch(refundOrderList);
            refundOrderInfoService.saveBatch(refundOrderInfoList);
            return Boolean.TRUE;
        });
        if (execute) {
            settingOrderStatus(order);
            orderService.updateById(order);
            // 积分、佣金、优惠券等放入后置task中处理
            if (order.getPayType().equals(PayConstants.PAY_TYPE_YUE)) {
                refundOrderList.forEach(refundOrder -> {
                    redisUtil.lPush(TaskConstants.ORDER_TASK_REDIS_KEY_AFTER_REFUND_BY_USER, refundOrder.getRefundOrderNo());
                });
            }
            if (order.getPayType().equals(PayConstants.PAY_TYPE_ALI_PAY) && totalRefundPrice.compareTo(BigDecimal.ZERO) > 0) {
                if (aliPayIsRefund) {
                    refundOrderList.forEach(refundOrder -> {
                        redisUtil.lPush(TaskConstants.ORDER_TASK_REDIS_KEY_AFTER_REFUND_BY_USER, refundOrder.getRefundOrderNo());
                    });
                } else {
                    refundOrderList.forEach(refundOrder -> {
                        redisUtil.lPush(TaskConstants.TASK_REFUND_ORDER_ALIPAY_QUERY_KEY, refundOrder.getRefundOrderNo());
                    });
                }
            }
        }
        return execute;
    }

    /**
     * 根据第三方单号查询退款单
     * @param outRefundNo 第三方单号
     */
    @Override
    public List<RefundOrder> findByOutRefundNo(String outRefundNo) {
        LambdaQueryWrapper<RefundOrder> lqw = Wrappers.lambdaQuery();
        lqw.eq(RefundOrder::getOutRefundNo, outRefundNo);
        return dao.selectList(lqw);
    }

    private List<RefundOrder> findCanAutoRevokeOrderList() {
        DateTime date = DateUtil.date();
        DateTime dateTime = DateUtil.offsetDay(date, -7);
        return dao.findCanAutoRevokeOrderList(dateTime.toString());
    }

    /**
     * 退款
     * @param refundOrder 退款单
     */
    private Boolean refundPrice(RefundOrder refundOrder, Order order) {
        RefundOrderInfo refundOrderInfo = refundOrderInfoService.getByRefundOrderNo(refundOrder.getRefundOrderNo());
        OrderDetail orderDetail = orderDetailService.getById(refundOrderInfo.getOrderDetailId());
        BigDecimal refundPrice;
        if (orderDetail.getPayNum().equals(refundOrderInfo.getApplyRefundNum())) {
            // sku整退
            refundPrice = orderDetail.getPayPrice();
            orderDetail.setRefundPrice(refundPrice);
            refundOrderInfo.setRefundPrice(refundPrice);
            if (orderDetail.getUseIntegral() > 0) {
                orderDetail.setRefundUseIntegral(orderDetail.getUseIntegral());
                orderDetail.setRefundIntegralPrice(orderDetail.getIntegralPrice());
                refundOrderInfo.setRefundUseIntegral(orderDetail.getRefundUseIntegral());
                refundOrderInfo.setRefundIntegralPrice(orderDetail.getRefundIntegralPrice());
            }
            if (orderDetail.getGainIntegral() > 0) {
                orderDetail.setRefundGainIntegral(orderDetail.getGainIntegral());
                refundOrderInfo.setRefundGainIntegral(orderDetail.getRefundGainIntegral());
            }
            if (orderDetail.getFirstBrokerageFee().compareTo(BigDecimal.ZERO) > 0) {
                orderDetail.setRefundFirstBrokerageFee(orderDetail.getFirstBrokerageFee());
                refundOrderInfo.setRefundFirstBrokerageFee(orderDetail.getRefundFirstBrokerageFee());
            }
            if (orderDetail.getSecondBrokerageFee().compareTo(BigDecimal.ZERO) > 0) {
                orderDetail.setRefundSecondBrokerageFee(orderDetail.getSecondBrokerageFee());
                refundOrderInfo.setRefundSecondBrokerageFee(orderDetail.getRefundSecondBrokerageFee());
            }
            if (orderDetail.getPlatCouponPrice().compareTo(BigDecimal.ZERO) > 0) {
                orderDetail.setRefundPlatCouponPrice(orderDetail.getPlatCouponPrice());
                refundOrderInfo.setRefundPlatCouponPrice(orderDetail.getPlatCouponPrice());
            }
            refundOrderInfo.setMerchantRefundPrice(refundPrice);
            if (orderDetail.getFreightFee().compareTo(BigDecimal.ZERO) > 0) {
                refundOrderInfo.setRefundFreightFee(orderDetail.getFreightFee());
                orderDetail.setRefundFreightFee(orderDetail.getFreightFee());
            }
        } else if (orderDetail.getPayNum() == (orderDetail.getRefundNum() + refundOrderInfo.getApplyRefundNum())) { // sku分退
            refundPrice = orderDetail.getPayPrice().subtract(orderDetail.getRefundPrice());
            // sku最后一部分退款
            orderDetail.setRefundPrice(orderDetail.getPayPrice());
            refundOrderInfo.setRefundPrice(refundPrice);
            refundOrderInfo.setMerchantRefundPrice(refundPrice);
            if (orderDetail.getUseIntegral() > 0) {
                refundOrderInfo.setRefundUseIntegral(orderDetail.getUseIntegral() - orderDetail.getRefundUseIntegral());
                refundOrderInfo.setRefundIntegralPrice(orderDetail.getIntegralPrice().subtract(orderDetail.getRefundIntegralPrice()));
                orderDetail.setRefundUseIntegral(orderDetail.getUseIntegral());
                orderDetail.setRefundIntegralPrice(orderDetail.getIntegralPrice());
            }
            if (orderDetail.getGainIntegral() > 0) {
                refundOrderInfo.setRefundGainIntegral(orderDetail.getGainIntegral() - orderDetail.getRefundGainIntegral());
                orderDetail.setRefundGainIntegral(orderDetail.getGainIntegral());
            }
            if (orderDetail.getFirstBrokerageFee().compareTo(BigDecimal.ZERO) > 0) {
                refundOrderInfo.setRefundFirstBrokerageFee(orderDetail.getFirstBrokerageFee().subtract(orderDetail.getRefundFirstBrokerageFee()));
                orderDetail.setRefundFirstBrokerageFee(orderDetail.getFirstBrokerageFee());
            }
            if (orderDetail.getSecondBrokerageFee().compareTo(BigDecimal.ZERO) > 0) {
                refundOrderInfo.setRefundSecondBrokerageFee(orderDetail.getSecondBrokerageFee().subtract(orderDetail.getRefundSecondBrokerageFee()));
                orderDetail.setRefundSecondBrokerageFee(orderDetail.getSecondBrokerageFee());
            }
            if (orderDetail.getPlatCouponPrice().compareTo(BigDecimal.ZERO) > 0) {
                refundOrderInfo.setRefundPlatCouponPrice(orderDetail.getPlatCouponPrice().subtract(orderDetail.getRefundPlatCouponPrice()));
                orderDetail.setRefundPlatCouponPrice(orderDetail.getPlatCouponPrice());
            }
            if (orderDetail.getFreightFee().compareTo(BigDecimal.ZERO) > 0) {
                refundOrderInfo.setRefundFreightFee(orderDetail.getFreightFee().subtract(orderDetail.getRefundFreightFee()));
                orderDetail.setRefundFreightFee(orderDetail.getFreightFee());
            }
        } else {
            // sku非最后一部分退款
            BigDecimal ratio = new BigDecimal(refundOrderInfo.getApplyRefundNum()).divide(new BigDecimal(orderDetail.getPayNum()), 10, BigDecimal.ROUND_HALF_UP);
            refundPrice = orderDetail.getPayPrice().multiply(ratio).setScale(2, BigDecimal.ROUND_HALF_UP);
            orderDetail.setRefundPrice(orderDetail.getRefundPrice().add(refundPrice));
            refundOrderInfo.setRefundPrice(refundPrice);
            refundOrderInfo.setMerchantRefundPrice(refundPrice);

            if (orderDetail.getUseIntegral() > 0) {
                refundOrderInfo.setRefundUseIntegral(new BigDecimal(orderDetail.getUseIntegral().toString()).multiply(ratio).setScale(0, BigDecimal.ROUND_HALF_UP).intValue());
                refundOrderInfo.setRefundIntegralPrice(orderDetail.getIntegralPrice().multiply(ratio).setScale(2, BigDecimal.ROUND_HALF_UP));
                orderDetail.setRefundUseIntegral(orderDetail.getRefundUseIntegral() + refundOrderInfo.getRefundUseIntegral());
                orderDetail.setRefundIntegralPrice(orderDetail.getRefundIntegralPrice().add(refundOrderInfo.getRefundIntegralPrice()));
            }
            if (orderDetail.getGainIntegral() > 0) {
                refundOrderInfo.setRefundGainIntegral(new BigDecimal(orderDetail.getGainIntegral().toString()).multiply(ratio).setScale(0, BigDecimal.ROUND_HALF_UP).intValue());
                orderDetail.setRefundGainIntegral(orderDetail.getRefundGainIntegral() + refundOrderInfo.getRefundGainIntegral());
            }
            if (orderDetail.getFirstBrokerageFee().compareTo(BigDecimal.ZERO) > 0) {
                refundOrderInfo.setRefundFirstBrokerageFee(orderDetail.getFirstBrokerageFee().multiply(ratio).setScale(2, BigDecimal.ROUND_HALF_UP));
                orderDetail.setRefundFirstBrokerageFee(orderDetail.getRefundFirstBrokerageFee().add(refundOrderInfo.getRefundFirstBrokerageFee()));
            }
            if (orderDetail.getSecondBrokerageFee().compareTo(BigDecimal.ZERO) > 0) {
                refundOrderInfo.setRefundSecondBrokerageFee(orderDetail.getSecondBrokerageFee().multiply(ratio).setScale(2, BigDecimal.ROUND_HALF_UP));
                orderDetail.setRefundSecondBrokerageFee(orderDetail.getRefundSecondBrokerageFee().add(refundOrderInfo.getRefundSecondBrokerageFee()));
            }
            if (orderDetail.getPlatCouponPrice().compareTo(BigDecimal.ZERO) > 0) {
                refundOrderInfo.setRefundPlatCouponPrice(orderDetail.getPlatCouponPrice().multiply(ratio).setScale(2, BigDecimal.ROUND_HALF_UP));
                orderDetail.setRefundPlatCouponPrice(orderDetail.getRefundPlatCouponPrice().add(refundOrderInfo.getRefundPlatCouponPrice()));
            }
            if (orderDetail.getFreightFee().compareTo(BigDecimal.ZERO) > 0) {
                refundOrderInfo.setRefundFreightFee(orderDetail.getFreightFee().multiply(ratio).setScale(2, BigDecimal.ROUND_HALF_UP));
                orderDetail.setRefundFreightFee(orderDetail.getRefundFreightFee().add(refundOrderInfo.getRefundFreightFee()));
            }
        }

        refundOrder.setRefundPrice(refundPrice);
        refundOrder.setMerchantRefundPrice(refundOrderInfo.getMerchantRefundPrice());
        refundOrder.setRefundUseIntegral(refundOrderInfo.getRefundUseIntegral());
        refundOrder.setRefundIntegralPrice(refundOrderInfo.getRefundIntegralPrice());
        refundOrder.setRefundGainIntegral(refundOrderInfo.getRefundGainIntegral());
        refundOrder.setRefundFirstBrokerageFee(refundOrderInfo.getRefundFirstBrokerageFee());
        refundOrder.setRefundSecondBrokerageFee(refundOrderInfo.getRefundSecondBrokerageFee());
        refundOrder.setRefundPayType(order.getPayType());
        refundOrder.setRefundPlatCouponPrice(refundOrderInfo.getRefundPlatCouponPrice());
        refundOrder.setRefundFreightFee(refundOrderInfo.getRefundFreightFee());
        //退款
        if (order.getPayType().equals(PayConstants.PAY_TYPE_WE_CHAT) && refundPrice.compareTo(BigDecimal.ZERO) > 0) {
            try {
                Order platOrder = orderService.getByOrderNo(order.getPlatOrderNo());
                wxRefund(order, refundOrder.getOutRefundNo(), refundPrice, platOrder.getPayPrice());
            } catch (Exception e) {
                e.printStackTrace();
                throw new CrmebException("微信申请退款失败！" + e.getMessage());
            }
        }
        Boolean aliPayIsRefund = false;
        if (order.getPayType().equals(PayConstants.PAY_TYPE_ALI_PAY) && refundPrice.compareTo(BigDecimal.ZERO) > 0) {
            try {
                aliPayIsRefund = aliPayService.refund(order.getPlatOrderNo(), refundOrder.getOutRefundNo(), refundOrder.getRefundReasonWapExplain(), refundPrice);
            } catch (Exception e) {
                e.printStackTrace();
                throw new CrmebException("支付宝申请退款失败！" + e.getMessage());
            }
        }

        orderDetail.setApplyRefundNum(orderDetail.getApplyRefundNum() - refundOrderInfo.getApplyRefundNum());
        orderDetail.setRefundNum(orderDetail.getRefundNum() + refundOrderInfo.getApplyRefundNum());

        User user = userService.getById(order.getUid());
        refundOrder.setRefundTime(DateUtil.date());
        Boolean finalAliPayIsRefund = aliPayIsRefund;
        Boolean execute = transactionTemplate.execute(e -> {
            orderDetailService.updateById(orderDetail);
            refundOrderInfoService.updateById(refundOrderInfo);
            refundOrder.setRefundStatus(OrderConstants.MERCHANT_REFUND_ORDER_STATUS_REFUNDING);
            if (order.getPayType().equals(PayConstants.PAY_TYPE_YUE)) {
                refundOrder.setRefundStatus(OrderConstants.MERCHANT_REFUND_ORDER_STATUS_REFUND);
                if (refundOrder.getRefundPrice().compareTo(BigDecimal.ZERO) > 0) {
                    // 更新用户金额
                    userService.updateNowMoney(order.getUid(), refundOrder.getRefundPrice(), Constants.OPERATION_TYPE_ADD);
                    // 用户余额记录
                    UserBalanceRecord userBalanceRecord = new UserBalanceRecord();
                    userBalanceRecord.setUid(user.getId());
                    userBalanceRecord.setLinkId(refundOrder.getRefundOrderNo());
                    userBalanceRecord.setLinkType(BalanceRecordConstants.BALANCE_RECORD_LINK_TYPE_ORDER);
                    userBalanceRecord.setType(BalanceRecordConstants.BALANCE_RECORD_TYPE_ADD);
                    userBalanceRecord.setAmount(refundOrder.getRefundPrice());
                    userBalanceRecord.setBalance(user.getNowMoney().add(refundOrder.getRefundPrice()));
                    userBalanceRecord.setRemark(StrUtil.format(BalanceRecordConstants.BALANCE_RECORD_REMARK_ORDER_REFUND, refundOrder.getRefundPrice()));
                    userBalanceRecordService.save(userBalanceRecord);
                }
            }
            if (order.getPayType().equals(PayConstants.PAY_TYPE_ALI_PAY) && refundPrice.compareTo(BigDecimal.ZERO) > 0 && finalAliPayIsRefund) {
                refundOrder.setRefundStatus(OrderConstants.MERCHANT_REFUND_ORDER_STATUS_REFUND);
            }
            updateById(refundOrder);
            return Boolean.TRUE;
        });
        if (execute) {
            settingOrderStatus(order);
            orderService.updateById(order);
            // 积分、佣金、优惠券等放入后置task中处理
            if (order.getPayType().equals(PayConstants.PAY_TYPE_YUE)) {
                redisUtil.lPush(TaskConstants.ORDER_TASK_REDIS_KEY_AFTER_REFUND_BY_USER, refundOrder.getRefundOrderNo());
            }
            if (order.getPayType().equals(PayConstants.PAY_TYPE_ALI_PAY) && refundPrice.compareTo(BigDecimal.ZERO) > 0) {
                if (aliPayIsRefund) {
                    redisUtil.lPush(TaskConstants.ORDER_TASK_REDIS_KEY_AFTER_REFUND_BY_USER, refundOrder.getRefundOrderNo());
                } else {
                    redisUtil.lPush(TaskConstants.TASK_REFUND_ORDER_ALIPAY_QUERY_KEY, refundOrder.getRefundOrderNo());
                }
            }
        }
        return execute;
    }

    /**
     * 校验审核参数
     */
    private void validatedAuditRequest(OrderRefundAuditRequest request) {
        if (request.getAuditType().equals("refuse")) {
            if (StrUtil.isEmpty(request.getReason())) {
                throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "请填写拒绝退款原因");
            }
        }
    }

    /**
     * 获取退款订单各状态数量
     *
     * @param merId     商户id，平台为0
     * @return RefundOrderCountItemResponse
     */
    private RefundOrderCountItemResponse getOrderStatusNum(RefundOrderSearchRequest request, Integer merId) {
        RefundOrderCountItemResponse response = new RefundOrderCountItemResponse();
        // 全部订单
        response.setAll(getCount(request, 9, merId));
        // 待审核
        response.setAwait(getCount(request, OrderConstants.MERCHANT_REFUND_ORDER_STATUS_APPLY, merId));
        // 审核拒绝
        response.setReject(getCount(request, OrderConstants.MERCHANT_REFUND_ORDER_STATUS_REJECT, merId));
        // 退款中
        response.setRefunding(getCount(request, OrderConstants.MERCHANT_REFUND_ORDER_STATUS_REFUNDING, merId));
        // 已退款
        response.setRefunded(getCount(request, OrderConstants.MERCHANT_REFUND_ORDER_STATUS_REFUND, merId));
        response.setAwaitReturning(getCount(request, OrderConstants.MERCHANT_REFUND_ORDER_STATUS_RETURN_GOODS, merId));
        response.setAwaitReceiving(getCount(request, OrderConstants.MERCHANT_REFUND_ORDER_STATUS_AWAIT_RECEIVING, merId));
        response.setRevoke(getCount(request, OrderConstants.MERCHANT_REFUND_ORDER_STATUS_REVOKE, merId));
        return response;
    }

    /**
     * 获取订单总数
     *
     * @param dateLimit 时间端
     * @param status    String 状态
     * @return Integer
     */
    private Integer getCount(String dateLimit, Integer status, Integer merId) {
        //总数只计算时间
        QueryWrapper<RefundOrder> queryWrapper = new QueryWrapper<>();
        if (merId > 0) {
            queryWrapper.eq("mer_id", merId);
        }
        if (StrUtil.isNotBlank(dateLimit)) {
            getRequestTimeWhere(queryWrapper, dateLimit);
        }
        getStatusWhere(queryWrapper, status);
        return dao.selectCount(queryWrapper);
    }

    /**
     * 获取订单总数
     *
     * @param status    String 状态
     * @return Integer
     */
    private Integer getCount(RefundOrderSearchRequest request, Integer status, Integer merId) {
        Map<String, Object> map = CollUtil.newHashMap();
        if (StrUtil.isNotBlank(request.getContent())) {
            ValidateFormUtil.validatorUserCommonSearch(request);
            String keywords = URLUtil.decode(request.getContent());
            switch (request.getSearchType()) {
                case UserConstants.USER_SEARCH_TYPE_ALL:
                    map.put("keywords", keywords);
                    break;
                case UserConstants.USER_SEARCH_TYPE_UID:
                    map.put("uid", Integer.valueOf(request.getContent()));
                    break;
                case UserConstants.USER_SEARCH_TYPE_NICKNAME:
                    map.put("nickname", keywords);
                    break;
                case UserConstants.USER_SEARCH_TYPE_PHONE:
                    map.put("phone", request.getContent());
                    break;
            }
        }
        //时间范围
        if (StrUtil.isNotBlank(request.getDateLimit())) {
            DateLimitUtilVo dateLimit = CrmebDateUtil.getDateLimit(request.getDateLimit());
            //判断时间
            int compareDateResult = CrmebDateUtil.compareDate(dateLimit.getEndTime(), dateLimit.getStartTime(), DateConstants.DATE_FORMAT);
            if (compareDateResult == -1) {
                throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "开始时间不能大于结束时间！");
            }
            if (StrUtil.isNotBlank(dateLimit.getStartTime())) {
                map.put("startTime", dateLimit.getStartTime());
                map.put("endTime", dateLimit.getEndTime());
            }
        }
        if (merId > 0) {
            map.put("merId",  merId);
        }
        if (StrUtil.isNotBlank(request.getRefundOrderNo())) {
            map.put("refundOrderNo", request.getRefundOrderNo());
        }
        if (StrUtil.isNotBlank(request.getOrderNo())) {
            map.put("orderNo", request.getOrderNo());
        }
        if (StrUtil.isNotBlank(request.getTrackingNumber())) {
            map.put("trackingNumber", request.getTrackingNumber());
        }
        if (ObjectUtil.isNotNull(status) && status != 9) {
            map.put("refundStatus", status);
        }
        return dao.getMerchantAdminPageCount(map);
    }

    /**
     * 获取request的where条件
     *
     * @param queryWrapper QueryWrapper<StoreOrder> 表达式
     * @param dateLimit    时间区间参数
     */
    private void getRequestTimeWhere(QueryWrapper<RefundOrder> queryWrapper, String dateLimit) {
        DateLimitUtilVo dateLimitUtilVo = CrmebDateUtil.getDateLimit(dateLimit);
        queryWrapper.between("create_time", dateLimitUtilVo.getStartTime(), dateLimitUtilVo.getEndTime());
    }

    /**
     * 根据订单状态获取where条件
     *
     * @param queryWrapper QueryWrapper<StoreOrder> 表达式
     * @param status       Integer 类型 9-全部
     */
    private void getStatusWhere(QueryWrapper<RefundOrder> queryWrapper, Integer status) {
        if (ObjectUtil.isNull(status)) {
            return;
        }
        if (status == 9) {
            return;
        }
        queryWrapper.eq("refund_status", status);
    }
}

