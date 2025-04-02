package com.zbkj.service.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.URLUtil;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zbkj.common.config.CrmebConfig;
import com.zbkj.common.constants.*;
import com.zbkj.common.exception.CrmebException;
import com.zbkj.common.model.admin.SystemAdmin;
import com.zbkj.common.model.express.Express;
import com.zbkj.common.model.merchant.Merchant;
import com.zbkj.common.model.order.*;
import com.zbkj.common.model.system.SystemNotification;
import com.zbkj.common.model.user.User;
import com.zbkj.common.model.user.UserToken;
import com.zbkj.common.page.CommonPage;
import com.zbkj.common.request.*;
import com.zbkj.common.response.*;
import com.zbkj.common.response.onepass.ElectrResponse;
import com.zbkj.common.result.CommonResultCode;
import com.zbkj.common.result.OrderResultCode;
import com.zbkj.common.utils.CrmebDateUtil;
import com.zbkj.common.utils.CrmebUtil;
import com.zbkj.common.utils.RedisUtil;
import com.zbkj.common.utils.ValidateFormUtil;
import com.zbkj.common.vo.DateLimitUtilVo;
import com.zbkj.common.vo.LogisticsResultVo;
import com.zbkj.common.vo.MyRecord;
import com.zbkj.service.dao.OrderDao;
import com.zbkj.service.service.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * OrderServiceImpl 接口实现
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
public class OrderServiceImpl extends ServiceImpl<OrderDao, Order> implements OrderService {
    private static final Logger logger = LoggerFactory.getLogger(OrderServiceImpl.class);
    @Resource
    private OrderDao dao;

    @Autowired
    private MerchantOrderService merchantOrderService;
    @Autowired
    private UserService userService;
    @Autowired
    private OrderDetailService orderDetailService;
    @Autowired
    private SystemAdminService systemAdminService;
    @Autowired
    private TransactionTemplate transactionTemplate;
    @Autowired
    private OrderStatusService orderStatusService;
    @Autowired
    private ExpressService expressService;
    @Autowired
    private SystemConfigService systemConfigService;
    @Autowired
    private OnePassService onePassService;
    @Autowired
    private MerchantService merchantService;
    @Autowired
    private LogisticService logisticService;
    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private OrderInvoiceService orderInvoiceService;
    @Autowired
    private OrderInvoiceDetailService orderInvoiceDetailService;
    @Autowired
    private UserTokenService userTokenService;
    @Autowired
    private TemplateMessageService templateMessageService;
    @Autowired
    private SystemNotificationServiceImpl systemNotificationService;
    @Autowired
    private MerchantPrintService merchantPrintService;
    @Lazy
    @Autowired
    private AsyncService asyncService;
    @Autowired
    private RefundOrderService refundOrderService;
    @Autowired
    private CrmebConfig crmebConfig;

    /**
     * 根据订单编号获取订单
     *
     * @param orderNo 订单编号
     */
    @Override
    public Order getByOrderNo(String orderNo) {
        LambdaQueryWrapper<Order> lqw = Wrappers.lambdaQuery();
        lqw.eq(Order::getOrderNo, orderNo);
        lqw.last(" limit 1");
        Order order = dao.selectOne(lqw);
        if (ObjectUtil.isNull(order)) {
            throw new CrmebException(OrderResultCode.ORDER_NOT_EXIST);
        }
        return order;
    }

    @Override
    public Boolean updatePaid(String orderNo) {
        LambdaUpdateWrapper<Order> lqw = new LambdaUpdateWrapper<>();
        lqw.set(Order::getPaid, true);
        lqw.set(Order::getPayTime, DateUtil.date());
        lqw.set(Order::getStatus, OrderConstants.ORDER_STATUS_WAIT_SHIPPING);
        lqw.eq(Order::getOrderNo, orderNo);
        lqw.eq(Order::getPaid, false);
        return update(lqw);
    }

    /**
     * 获取订单
     *
     * @param outTradeNo 商户系统内部的订单号
     */
    @Override
    public Order getByOutTradeNo(String outTradeNo) {
        LambdaQueryWrapper<Order> lqw = Wrappers.lambdaQuery();
        lqw.eq(Order::getOutTradeNo, outTradeNo);
        lqw.last(" limit 1");
        Order order = dao.selectOne(lqw);
        if (ObjectUtil.isNull(order)) {
            throw new CrmebException(OrderResultCode.ORDER_NOT_EXIST);
        }
        return order;
    }

    /**
     * 获取用户订单列表V1.4
     *
     * @param userId  用户id
     * @param request 搜索参数
     * @return PageInfo
     */
    @Override
    public PageInfo<Order> getUserOrderList_v1_4(Integer userId, OrderFrontListRequest request) {
        Page<Order> page = PageHelper.startPage(request.getPage(), request.getLimit());
        if (StrUtil.isBlank(request.getKeywords())) {
            LambdaQueryWrapper<Order> lqw = Wrappers.lambdaQuery();
            lqw.eq(Order::getUid, userId);
            if (request.getStatus() >= 0) {
                if (request.getStatus() == 1) {
                    lqw.in(Order::getStatus, OrderConstants.ORDER_STATUS_WAIT_SHIPPING, OrderConstants.ORDER_STATUS_PART_SHIPPING);
                    lqw.in(Order::getGroupBuyRecordStatus, 99, 10);
                } else if(request.getStatus() == 3) {
                    lqw.eq(Order::getStatus, 3);
                    lqw.in(Order::getGroupBuyRecordStatus, 99, 10);
                } else {
                    lqw.eq(Order::getStatus, request.getStatus());
//                    lqw.eq(Order::getGroupBuyRecordStatus, 0);
                }
                lqw.lt(Order::getRefundStatus, OrderConstants.ORDER_REFUND_STATUS_REFUND);
            }
            lqw.eq(Order::getIsUserDel, false);
            lqw.eq(Order::getIsMerchantDel, false);
            lqw.ne(Order::getSecondType, OrderConstants.ORDER_SECOND_TYPE_VIDEO);
            lqw.eq(Order::getIsDel, false);
            if (ObjectUtil.isNotNull(request.getSecondType()) && request.getSecondType().equals(OrderConstants.ORDER_SECOND_TYPE_INTEGRAL)) {
                lqw.eq(Order::getSecondType, request.getSecondType());
            }
            lqw.orderByDesc(Order::getId);
            List<Order> orderList = dao.selectList(lqw);
            return CommonPage.copyPageInfo(page, orderList);
        }
        Map<String, Object> searchMap = new HashMap<>();
        String keywords = URLUtil.decode(request.getKeywords());
        searchMap.put("keywords", keywords);
        searchMap.put("status", request.getStatus());
        searchMap.put("userId", userId);
        if (ObjectUtil.isNotNull(request.getSecondType()) && request.getSecondType().equals(OrderConstants.ORDER_SECOND_TYPE_INTEGRAL)) {
            searchMap.put("secondType", request.getSecondType());
        }
        List<Order> orderList = dao.findFrontList(searchMap);
        return CommonPage.copyPageInfo(page, orderList);
    }

    /**
     * 取消订单
     *
     * @param orderNo 订单编号
     * @param isUser  是否用户取消
     * @return Boolean
     */
    @Override
    public Boolean cancel(String orderNo, Boolean isUser) {
        LambdaUpdateWrapper<Order> wrapper = Wrappers.lambdaUpdate();
        wrapper.set(Order::getCancelStatus, isUser ? OrderConstants.ORDER_CANCEL_STATUS_USER : OrderConstants.ORDER_CANCEL_STATUS_SYSTEM);
        wrapper.set(Order::getStatus, OrderConstants.ORDER_STATUS_CANCEL);
        wrapper.eq(Order::getOrderNo, orderNo);
        wrapper.eq(Order::getPaid, false);
        wrapper.eq(Order::getStatus, OrderConstants.ORDER_STATUS_WAIT_PAY);
        return update(wrapper);
    }

    /**
     * 商户端后台分页列表
     *
     * @param request 查询参数
     * @return PageInfo
     */
    @Override
    public PageInfo<MerchantOrderPageResponse> getMerchantAdminPage(OrderSearchRequest request, SystemAdmin systemAdmin) {
        Page<Order> startPage = PageHelper.startPage(request.getPage(), request.getLimit());
        Map<String, Object> map = CollUtil.newHashMap();
        map.put("status", request.getStatus());
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
        map.put("merId", systemAdmin.getMerId());
        if (StrUtil.isNotBlank(request.getOrderNo())) {
            map.put("orderNo", URLUtil.decode(request.getOrderNo()));
        }
        if (ObjectUtil.isNotNull(request.getType())) {
            map.put("type", request.getType());
        }
        List<MerchantOrderPageResponse> responseList = dao.getMerchantAdminPage(map);
        if (CollUtil.isEmpty(responseList)) {
            return CommonPage.copyPageInfo(startPage, CollUtil.newArrayList());
        }
        responseList.forEach(response -> {
            if (response.getRefundStatus() > 0) {
                response.setUserRefundSign(refundOrderService.isOrderContainUserRefund(response.getOrderNo()));
                if (response.getRefundStatus().equals(2)) {
                    response.setRefundNum(orderDetailService.getOrderRefundNum(response.getOrderNo()));
                }
            }
            List<OrderInfoFrontDataResponse> infoResponseList = CollUtil.newArrayList();
            List<OrderDetail> orderDetailList = orderDetailService.getByOrderNo(response.getOrderNo());
            orderDetailList.forEach(od -> {
                OrderInfoFrontDataResponse orderInfoResponse = new OrderInfoFrontDataResponse();
                BeanUtils.copyProperties(od, orderInfoResponse);
                infoResponseList.add(orderInfoResponse);
            });
            response.setInfoResponseList(infoResponseList);
        });
        return CommonPage.copyPageInfo(startPage, responseList);
    }

    /**
     * 获取商户端订单各状态数量
     */
    @Override
    public OrderCountItemResponse getMerchantOrderStatusNum(OrderTabsHeaderRequest request, SystemAdmin systemAdmin) {
        OrderCountItemResponse response = new OrderCountItemResponse();
        // 全部订单
        response.setAll(getCount(request, OrderConstants.MERCHANT_ORDER_STATUS_ALL, systemAdmin.getMerId()));
        // 未支付订单
        response.setUnPaid(getCount(request, OrderConstants.MERCHANT_ORDER_STATUS_UNPAID, systemAdmin.getMerId()));
        // 未发货订单
        response.setNotShipped(getCount(request, OrderConstants.MERCHANT_ORDER_STATUS_NOT_SHIPPED, systemAdmin.getMerId()));
        // 待收货订单
        response.setSpike(getCount(request, OrderConstants.MERCHANT_ORDER_STATUS_SPIKE, systemAdmin.getMerId()));
        // 已收货订单
        response.setReceiving(getCount(request, OrderConstants.MERCHANT_ORDER_STATUS_RECEIVING, systemAdmin.getMerId()));
        // 交易完成订单
        response.setComplete(getCount(request, OrderConstants.MERCHANT_ORDER_STATUS_COMPLETE, systemAdmin.getMerId()));
        // 已退款订单
        response.setRefunded(getCount(request, OrderConstants.MERCHANT_ORDER_STATUS_REFUNDED, systemAdmin.getMerId()));
        // 已删除订单
        response.setDeleted(getCount(request, OrderConstants.MERCHANT_ORDER_STATUS_DELETED, systemAdmin.getMerId()));
        // 待核销订单
        response.setVerification(getCount(request, OrderConstants.MERCHANT_ORDER_STATUS_AWAIT_VERIFICATION, systemAdmin.getMerId()));
        return response;
    }

    /**
     * 订单详情（PC）
     *
     * @param orderNo 订单编号
     * @return OrderAdminDetailResponse
     */
    @Override
    public OrderAdminDetailResponse adminDetail(String orderNo, SystemAdmin systemAdmin) {
        Order order = getByOrderNo(orderNo);
        if (order.getIsMerchantDel() || !order.getMerId().equals(systemAdmin.getMerId())) {
            throw new CrmebException(OrderResultCode.ORDER_NOT_EXIST);
        }
        OrderAdminDetailResponse orderAdminDetailResponse = new OrderAdminDetailResponse();
        MerchantOrder merchantOrder = merchantOrderService.getOneByOrderNo(orderNo);
        BeanUtils.copyProperties(merchantOrder, orderAdminDetailResponse);
        BeanUtils.copyProperties(order, orderAdminDetailResponse);
        List<OrderDetail> orderDetailList = orderDetailService.getByOrderNo(orderNo);
        List<OrderInfoFrontDataResponse> orderInfoList = orderDetailList.stream().map(e -> {
            OrderInfoFrontDataResponse dataResponse = new OrderInfoFrontDataResponse();
            BeanUtils.copyProperties(e, dataResponse);
            return dataResponse;
        }).collect(Collectors.toList());
        orderAdminDetailResponse.setOrderDetailList(orderInfoList);

        if (order.getRefundStatus() > 0) {
            orderAdminDetailResponse.setUserRefundSign(refundOrderService.isOrderContainUserRefund(order.getOrderNo()));
        }
        // 用户信息
        User user = userService.getById(order.getUid());
        orderAdminDetailResponse.setNickname(user.getNickname());
        orderAdminDetailResponse.setPhone(user.getPhone());
        orderAdminDetailResponse.setIsLogoff(user.getIsLogoff());
        orderAdminDetailResponse.setAvatar(user.getAvatar());
        orderAdminDetailResponse.setUserLevel(user.getLevel());
        orderAdminDetailResponse.setIsPaidMember(user.getIsPaidMember());
//        if (merchantOrder.getClerkId() > 0) {
//            SystemAdmin clerkAdmin = systemAdminService.getById(merchantOrder.getClerkId());
//            // 这里做一个溶于判断 当管理员中不存在的情况下 那么查询用户id 要么管理员核销，要么移动端商户管理员核销
//            if (ObjectUtil.isNull(clerkAdmin)) {
//                clerkAdmin = userService.getSystemAdminByMerchantEmployee();
//            }
//            orderAdminDetailResponse.setClerkName(clerkAdmin.getRealName());
//        }
        if (crmebConfig.getPhoneMaskSwitch()) {
            orderAdminDetailResponse.setPhone(CrmebUtil.maskMobile(orderAdminDetailResponse.getPhone()));
            if (StrUtil.isNotBlank(orderAdminDetailResponse.getUserPhone())) {
                orderAdminDetailResponse.setUserPhone(CrmebUtil.maskMobile(orderAdminDetailResponse.getUserPhone()));
            }
        }
        return orderAdminDetailResponse;
    }

    /**
     * 发货
     *
     * @param request 发货参数
     * @return Boolean
     */
    @Override
    public Boolean send(OrderSendRequest request, SystemAdmin systemAdmin) {
        validateOrderSend(request);
        Order order = getByOrderNo(request.getOrderNo());
        if (!order.getMerId().equals(systemAdmin.getMerId())) {
            throw new CrmebException(OrderResultCode.ORDER_NOT_EXIST);
        }
        if (order.getIsUserDel() || order.getIsMerchantDel()) {
            throw new CrmebException(OrderResultCode.ORDER_DELETE);
        }
        if (!order.getLevel().equals(OrderConstants.ORDER_LEVEL_MERCHANT)) {
            throw new CrmebException(OrderResultCode.ORDER_LEVEL_ABNORMAL);
        }
        if (!(order.getStatus().equals(OrderConstants.ORDER_STATUS_WAIT_SHIPPING)
                || order.getStatus().equals(OrderConstants.ORDER_STATUS_PART_SHIPPING))) {
            throw new CrmebException(OrderResultCode.ORDER_STATUS_ABNORMAL.setMessage("订单不处于待发货状态"));
        }
        if (order.getRefundStatus().equals(OrderConstants.ORDER_REFUND_STATUS_ALL)) {
            throw new CrmebException(OrderResultCode.ORDER_REFUND_ED);
        }
        if (order.getSecondType().equals(OrderConstants.ORDER_SECOND_TYPE_VIRTUALLY)
                && !request.getDeliveryType().equals(OrderConstants.ORDER_DELIVERY_TYPE_NO_NEED)) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "虚拟商品订单只支持无需发货方式发货");
        }
        MerchantOrder merchantOrder = merchantOrderService.getOneByOrderNo(request.getOrderNo());
        if (!merchantOrder.getShippingType().equals(OrderConstants.ORDER_SHIPPING_TYPE_EXPRESS)) {
            throw new CrmebException(OrderResultCode.ORDER_SHIPPING_TYPE_PICK_UP);
        }
        if (order.getRefundStatus().equals(OrderConstants.ORDER_REFUND_STATUS_APPLY)) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "请先处理售后，再进行发货操作");
        }

        if (!request.getIsSplit()) {// 全部发货
            if (request.getDeliveryType().equals(OrderConstants.ORDER_DELIVERY_TYPE_EXPRESS)) {
                return sendExpress(request, order, merchantOrder);
            }
            if (request.getDeliveryType().equals(OrderConstants.ORDER_DELIVERY_TYPE_MERCHANT)) {
                return sendMerchant(request.getDeliveryCarrier(), request.getCarrierPhone(), order, merchantOrder);
            }
            if (request.getDeliveryType().equals(OrderConstants.ORDER_DELIVERY_TYPE_NO_NEED)) {
                return sendNoNeed(StrUtil.isNotBlank(request.getDeliveryMark()) ? request.getDeliveryMark() : "", order, merchantOrder);
            }
        }
        if (CollUtil.isEmpty(request.getDetailList())) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "拆单发货详情不能为空");
        }
        List<SplitOrderSendDetailRequest> detailRequestList = request.getDetailList();
        List<Integer> detailIdList = detailRequestList.stream().map(SplitOrderSendDetailRequest::getOrderDetailId).distinct().collect(Collectors.toList());
        if (detailRequestList.size() != detailIdList.size()) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "有重复的发货单详情");
        }
        List<OrderDetail> orderDetailList = orderDetailService.getByOrderNo(request.getOrderNo());
        detailRequestList.forEach(detailRequest -> {
            if (detailRequest.getNum() < 1) {
                throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "订单详情发货数量不能小于1");
            }
            OrderDetail orderDetail = orderDetailList.stream().filter(e -> e.getId().equals(detailRequest.getOrderDetailId())).findAny().orElse(null);
            if (ObjectUtil.isNull(orderDetail)) {
                throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "订单详情ID不对应");
            }
            if (orderDetail.getDeliveryNum().equals(0)) {
                if ((orderDetail.getPayNum() - orderDetail.getRefundNum() - detailRequest.getNum()) < 0) {
                    throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "超出可发货数量,请重新选择数量");
                }
            } else {
                if (orderDetail.getPayNum() - orderDetail.getDeliveryNum() - detailRequest.getNum() < 0) {
                    throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "超出可发货数量,请重新选择数量");
                }
            }
        });
        // 拆单发货
        if (request.getDeliveryType().equals(OrderConstants.ORDER_DELIVERY_TYPE_EXPRESS)) {
            return splitSendExpress(request, order, merchantOrder, orderDetailList);
        }
        if (request.getDeliveryType().equals(OrderConstants.ORDER_DELIVERY_TYPE_MERCHANT)) {
            return splitSendMerchant(request, order, merchantOrder, orderDetailList);
        }
        if (request.getDeliveryType().equals(OrderConstants.ORDER_DELIVERY_TYPE_NO_NEED)) {
            return splitSendNoNeed(request, order, merchantOrder, orderDetailList);
        }
        return Boolean.TRUE;
    }

    /**
     * 无需发货发货
     * @param order 订单
     * @param merchantOrder 商户订单
     * @param orderDetailList 待发货订单详情
     */
    private Boolean splitSendNoNeed(OrderSendRequest request, Order order, MerchantOrder merchantOrder, List<OrderDetail> orderDetailList) {
        List<SplitOrderSendDetailRequest> detailRequestList = request.getDetailList();
        List<OrderInvoiceDetail> orderInvoiceDetailList = new ArrayList<>();
        List<OrderDetail> orderDetailUpdateList = new ArrayList<>();
        detailRequestList.forEach(detail -> {
            OrderDetail orderDetail = orderDetailList.stream().filter(e -> e.getId().equals(detail.getOrderDetailId())).findAny().orElse(null);
            orderDetail.setDeliveryNum(orderDetail.getDeliveryNum() + detail.getNum());
            OrderInvoiceDetail invoiceDetail = new OrderInvoiceDetail();
            BeanUtils.copyProperties(orderDetail, invoiceDetail);
            invoiceDetail.setNum(detail.getNum());
            invoiceDetail.setCreateTime(DateUtil.date());
            invoiceDetail.setUpdateTime(DateUtil.date());
            orderInvoiceDetailList.add(invoiceDetail);
            orderDetailUpdateList.add(orderDetail);
        });
        if (CollUtil.isEmpty(orderInvoiceDetailList)) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "订单没有需要发货的商品");
        }
        OrderInvoice invoice = new OrderInvoice();
        invoice.setMerId(order.getMerId());
        invoice.setOrderNo(order.getOrderNo());
        invoice.setUid(order.getUid());
        invoice.setDeliveryMark(request.getDeliveryMark());
        invoice.setTotalNum(orderInvoiceDetailList.stream().mapToInt(OrderInvoiceDetail::getNum).sum());
        invoice.setDeliveryType(OrderConstants.ORDER_DELIVERY_TYPE_NO_NEED);

        merchantOrder.setIsSplitDelivery(Boolean.TRUE);
        merchantOrder.setDeliveryType(OrderConstants.ORDER_DELIVERY_TYPE_NO_NEED);
        order.setStatus(OrderConstants.ORDER_STATUS_PART_SHIPPING);

        String message = StrUtil.format(OrderStatusConstants.ORDER_LOG_MESSAGE_SEND_NO_NEED, request.getDeliveryMark());
        Boolean execute = transactionTemplate.execute(i -> {
            updateById(order);
            merchantOrderService.updateById(merchantOrder);
            orderDetailService.updateBatchById(orderDetailUpdateList);
            orderInvoiceService.save(invoice);
            orderInvoiceDetailList.forEach(e -> e.setInvoiceId(invoice.getId()));
            orderInvoiceDetailService.saveBatch(orderInvoiceDetailList);
            //订单记录增加
            orderStatusService.createLog(order.getOrderNo(), OrderStatusConstants.ORDER_STATUS_EXPRESS, message);
            return Boolean.TRUE;
        });
        if (!execute) throw new CrmebException("无需发货拆单发货失败！");
        List<OrderDetail> detailList = orderDetailService.getByOrderNo(order.getOrderNo());
        long count = detailList.stream().filter(e -> e.getPayNum() > (e.getDeliveryNum() + e.getRefundNum())).count();
        if (count <= 0) {
            order.setStatus(OrderConstants.ORDER_STATUS_WAIT_RECEIPT);
            updateById(order);
            // 全部发货 + 小程序发货管理 TODO 缺少无需发货
//            if (order.getPayType().equals(PayConstants.PAY_TYPE_WE_CHAT) && order.getPayChannel().equals(PayConstants.PAY_CHANNEL_WECHAT_MINI)) {
//                String shippingSwitch = systemConfigService.getValueByKey(WeChatConstants.CONFIG_WECHAT_ROUTINE_SHIPPING_SWITCH);
//                if (StrUtil.isNotBlank(shippingSwitch) && shippingSwitch.equals("1")) {
//                    asyncService.wechatSendUploadShipping(order.getOrderNo());
//                }
//            }
        }
        // 发送消息通知 TODO 原消息通知为快递发货专用，字段不符
        return execute;
    }

    /**
     * 商家配送
     * @param request         发货参数
     * @param order 订单
     * @param merchantOrder 商户订单
     * @param orderDetailList 待发货订单详情
     */
    private Boolean splitSendMerchant(OrderSendRequest request, Order order, MerchantOrder merchantOrder, List<OrderDetail> orderDetailList) {
        List<SplitOrderSendDetailRequest> detailRequestList = request.getDetailList();
        List<OrderInvoiceDetail> orderInvoiceDetailList = new ArrayList<>();
        List<OrderDetail> orderDetailUpdateList = new ArrayList<>();
        detailRequestList.forEach(detail -> {
            OrderDetail orderDetail = orderDetailList.stream().filter(e -> e.getId().equals(detail.getOrderDetailId())).findAny().orElse(null);
            orderDetail.setDeliveryNum(orderDetail.getDeliveryNum() + detail.getNum());
            OrderInvoiceDetail invoiceDetail = new OrderInvoiceDetail();
            BeanUtils.copyProperties(orderDetail, invoiceDetail);
            invoiceDetail.setNum(detail.getNum());
            invoiceDetail.setCreateTime(DateUtil.date());
            invoiceDetail.setUpdateTime(DateUtil.date());
            orderInvoiceDetailList.add(invoiceDetail);
            orderDetailUpdateList.add(orderDetail);
        });
        if (CollUtil.isEmpty(orderInvoiceDetailList)) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "订单没有需要发货的商品");
        }

        OrderInvoice invoice = new OrderInvoice();
        invoice.setMerId(order.getMerId());
        invoice.setOrderNo(order.getOrderNo());
        invoice.setUid(order.getUid());
        invoice.setDeliveryCarrier(request.getDeliveryCarrier());
        invoice.setCarrierPhone(request.getCarrierPhone());
        invoice.setTotalNum(orderInvoiceDetailList.stream().mapToInt(OrderInvoiceDetail::getNum).sum());
        invoice.setDeliveryType(OrderConstants.ORDER_DELIVERY_TYPE_MERCHANT);

        merchantOrder.setIsSplitDelivery(Boolean.TRUE);
        merchantOrder.setDeliveryType(OrderConstants.ORDER_DELIVERY_TYPE_MERCHANT);
        order.setStatus(OrderConstants.ORDER_STATUS_PART_SHIPPING);

        String message = StrUtil.format(OrderStatusConstants.ORDER_LOG_MESSAGE_SEND_MERCHANT, request.getDeliveryCarrier(), request.getCarrierPhone());
        Boolean execute = transactionTemplate.execute(i -> {
            updateById(order);
            merchantOrderService.updateById(merchantOrder);
            orderDetailService.updateBatchById(orderDetailUpdateList);
            orderInvoiceService.save(invoice);
            orderInvoiceDetailList.forEach(e -> e.setInvoiceId(invoice.getId()));
            orderInvoiceDetailService.saveBatch(orderInvoiceDetailList);
            //订单记录增加
            orderStatusService.createLog(order.getOrderNo(), OrderStatusConstants.ORDER_STATUS_EXPRESS, message);
            return Boolean.TRUE;
        });
        if (!execute) throw new CrmebException("商家配送拆单发货失败！");
        List<OrderDetail> detailList = orderDetailService.getByOrderNo(order.getOrderNo());
        long count = detailList.stream().filter(e -> e.getPayNum() > (e.getDeliveryNum() + e.getRefundNum())).count();
        if (count <= 0) {
            order.setStatus(OrderConstants.ORDER_STATUS_WAIT_RECEIPT);
            updateById(order);
            // 全部发货 + 小程序发货管理 TODO 缺少商家配送
//            if (order.getPayType().equals(PayConstants.PAY_TYPE_WE_CHAT) && order.getPayChannel().equals(PayConstants.PAY_CHANNEL_WECHAT_MINI)) {
//                String shippingSwitch = systemConfigService.getValueByKey(WeChatConstants.CONFIG_WECHAT_ROUTINE_SHIPPING_SWITCH);
//                if (StrUtil.isNotBlank(shippingSwitch) && shippingSwitch.equals("1")) {
//                    asyncService.wechatSendUploadShipping(order.getOrderNo());
//                }
//            }
        }
        // 发送消息通知 TODO 原消息通知为快递发货专用，字段不符
        return execute;
    }

    /**
     * 无需发货发货
     * @param order 订单
     * @param merchantOrder 商户订单
     */
    private Boolean sendNoNeed(String deliveryMark, Order order, MerchantOrder merchantOrder) {
        List<OrderDetail> orderDetailList = orderDetailService.getByOrderNo(order.getOrderNo());
        List<OrderInvoiceDetail> orderInvoiceDetailList = new ArrayList<>();
        List<OrderDetail> orderDetailUpdateList = new ArrayList<>();
        orderDetailList.forEach(od -> {
            if (od.getPayNum() > od.getDeliveryNum()) {
                OrderInvoiceDetail invoiceDetail = new OrderInvoiceDetail();
                BeanUtils.copyProperties(od, invoiceDetail);
                invoiceDetail.setNum(od.getPayNum() - od.getDeliveryNum());
                invoiceDetail.setCreateTime(DateUtil.date());
                invoiceDetail.setUpdateTime(DateUtil.date());
                orderInvoiceDetailList.add(invoiceDetail);
                od.setDeliveryNum(od.getPayNum());
                orderDetailUpdateList.add(od);
            }
        });
        if (CollUtil.isEmpty(orderInvoiceDetailList)) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "订单没有需要发货的商品");
        }
        OrderInvoice invoice = new OrderInvoice();
        invoice.setMerId(order.getMerId());
        invoice.setOrderNo(order.getOrderNo());
        invoice.setUid(order.getUid());
        invoice.setDeliveryMark(deliveryMark);
        invoice.setDeliveryType(OrderConstants.ORDER_DELIVERY_TYPE_NO_NEED);
        invoice.setTotalNum(orderInvoiceDetailList.stream().mapToInt(OrderInvoiceDetail::getNum).sum());

        merchantOrder.setIsSplitDelivery(Boolean.FALSE);
        merchantOrder.setDeliveryType(OrderConstants.ORDER_DELIVERY_TYPE_NO_NEED);
        order.setStatus(OrderConstants.ORDER_STATUS_WAIT_RECEIPT);

        String message = StrUtil.format(OrderStatusConstants.ORDER_LOG_MESSAGE_SEND_NO_NEED, deliveryMark);
        Boolean execute = transactionTemplate.execute(i -> {
            updateById(order);
            merchantOrderService.updateById(merchantOrder);
            orderDetailService.updateBatchById(orderDetailUpdateList);
            orderInvoiceService.save(invoice);
            orderInvoiceDetailList.forEach(e -> e.setInvoiceId(invoice.getId()));
            orderInvoiceDetailService.saveBatch(orderInvoiceDetailList);
            //订单记录增加
            orderStatusService.createLog(order.getOrderNo(), OrderStatusConstants.ORDER_STATUS_EXPRESS, message);
            return Boolean.TRUE;
        });
        if (!execute) throw new CrmebException("无需发货发货失败！");
        // 发送消息通知 TODO 原消息通知为快递发货专用，字段不符
        // 全部发货 + 小程序发货管理 TODO 缺少无需发货
//        if (order.getPayType().equals(PayConstants.PAY_TYPE_WE_CHAT) && order.getPayChannel().equals(PayConstants.PAY_CHANNEL_WECHAT_MINI)) {
//            String shippingSwitch = systemConfigService.getValueByKey(WeChatConstants.CONFIG_WECHAT_ROUTINE_SHIPPING_SWITCH);
//            if (StrUtil.isNotBlank(shippingSwitch) && shippingSwitch.equals("1")) {
//                asyncService.wechatSendUploadShipping(merchantOrder.getOrderNo());
//            }
//        }
        return execute;
    }

    /**
     * 商家配送
     * @param deliveryCarrier 配送人员
     * @param carrierPhone 配送人员手机号
     * @param order 订单
     * @param merchantOrder 商户订单
     */
    private Boolean sendMerchant(String deliveryCarrier, String carrierPhone, Order order, MerchantOrder merchantOrder) {
        List<OrderDetail> orderDetailList = orderDetailService.getByOrderNo(order.getOrderNo());
        List<OrderInvoiceDetail> orderInvoiceDetailList = new ArrayList<>();
        List<OrderDetail> orderDetailUpdateList = new ArrayList<>();
        orderDetailList.forEach(od -> {
            if (od.getPayNum() > od.getDeliveryNum()) {
                OrderInvoiceDetail invoiceDetail = new OrderInvoiceDetail();
                BeanUtils.copyProperties(od, invoiceDetail);
                invoiceDetail.setNum(od.getPayNum() - od.getDeliveryNum());
                invoiceDetail.setCreateTime(DateUtil.date());
                invoiceDetail.setUpdateTime(DateUtil.date());
                orderInvoiceDetailList.add(invoiceDetail);
                od.setDeliveryNum(od.getPayNum());
                orderDetailUpdateList.add(od);
            }
        });
        if (CollUtil.isEmpty(orderInvoiceDetailList)) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "订单没有需要发货的商品");
        }

        OrderInvoice invoice = new OrderInvoice();
        invoice.setMerId(order.getMerId());
        invoice.setOrderNo(order.getOrderNo());
        invoice.setUid(order.getUid());
        invoice.setDeliveryCarrier(deliveryCarrier);
        invoice.setDeliveryType(OrderConstants.ORDER_DELIVERY_TYPE_MERCHANT);
        invoice.setCarrierPhone(carrierPhone);
        invoice.setTotalNum(orderInvoiceDetailList.stream().mapToInt(OrderInvoiceDetail::getNum).sum());

        merchantOrder.setIsSplitDelivery(Boolean.FALSE);
        merchantOrder.setDeliveryType(OrderConstants.ORDER_DELIVERY_TYPE_MERCHANT);
        order.setStatus(OrderConstants.ORDER_STATUS_WAIT_RECEIPT);

        String message = StrUtil.format(OrderStatusConstants.ORDER_LOG_MESSAGE_SEND_MERCHANT, deliveryCarrier, carrierPhone);
        Boolean execute = transactionTemplate.execute(i -> {
            updateById(order);
            merchantOrderService.updateById(merchantOrder);
            orderDetailService.updateBatchById(orderDetailUpdateList);
            orderInvoiceService.save(invoice);
            orderInvoiceDetailList.forEach(e -> e.setInvoiceId(invoice.getId()));
            orderInvoiceDetailService.saveBatch(orderInvoiceDetailList);
            //订单记录增加
            orderStatusService.createLog(order.getOrderNo(), OrderStatusConstants.ORDER_STATUS_EXPRESS, message);
            return Boolean.TRUE;
        });
        if (!execute) throw new CrmebException("商家配送发货失败！");
        // 发送消息通知 TODO 原消息通知为快递发货专用，字段不符
        // 全部发货 + 小程序发货管理 TODO 缺少商家配送
//        if (order.getPayType().equals(PayConstants.PAY_TYPE_WE_CHAT) && order.getPayChannel().equals(PayConstants.PAY_CHANNEL_WECHAT_MINI)) {
//            String shippingSwitch = systemConfigService.getValueByKey(WeChatConstants.CONFIG_WECHAT_ROUTINE_SHIPPING_SWITCH);
//            if (StrUtil.isNotBlank(shippingSwitch) && shippingSwitch.equals("1")) {
//                asyncService.wechatSendUploadShipping(merchantOrder.getOrderNo());
//            }
//        }
        return execute;
    }

    /**
     * 小票打印
     *
     * @param orderNo 订单编号
     * @return 打印结果
     */
    @Override
    public void printReceipt(String orderNo, SystemAdmin systemAdmin) {
        MerchantOrder merchantOrder = merchantOrderService.getOneByOrderNo(orderNo);
        if (!systemAdmin.getMerId().equals(merchantOrder.getMerId())) {
            throw new CrmebException(OrderResultCode.ORDER_NOT_EXIST);
        }
        Merchant merchant = merchantService.getByIdException(systemAdmin.getMerId());
        // 小票打印开关：0关闭，1=手动打印，2=自动打印，3=自动和手动
        if (merchant.getReceiptPrintingSwitch() == 0) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "小票打印功能未开启");
        }
        if (merchant.getReceiptPrintingSwitch() == 2) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "小票打印未开启手动打印");
        }
        merchantPrintService.printReceipt(merchantOrder);
    }

    /**
     * 商户删除订单
     *
     * @param orderNo 订单编号
     * @return Boolean
     */
    @Override
    public Boolean merchantDeleteByOrderNo(String orderNo, SystemAdmin systemAdmin) {
        Order order = getByOrderNoAndMerId(orderNo, systemAdmin.getMerId());
        if (!order.getIsUserDel()) {
            throw new CrmebException(OrderResultCode.ORDER_USER_NOT_DELETE);
        }
        order.setIsMerchantDel(true);
        return updateById(order);
    }

    /**
     * 商户备注订单
     *
     * @param request 备注参数
     * @return Boolean
     */
    @Override
    public Boolean merchantMark(OrderRemarkRequest request, SystemAdmin systemAdmin) {
        Order order = getByOrderNoAndMerId(request.getOrderNo(), systemAdmin.getMerId());
        MerchantOrder merchantOrder = merchantOrderService.getOneByOrderNo(order.getOrderNo());
        merchantOrder.setMerchantRemark(request.getRemark());
        return merchantOrderService.updateById(merchantOrder);
    }

    /**
     * 订单收货
     *
     * @param orderNo 订单号
     */
    @Override
    public Boolean takeDelivery(String orderNo) {
        LambdaUpdateWrapper<Order> wrapper = Wrappers.lambdaUpdate();
        wrapper.set(Order::getStatus, OrderConstants.ORDER_STATUS_TAKE_DELIVERY);
        wrapper.set(Order::getReceivingTime, DateUtil.date());
        wrapper.eq(Order::getOrderNo, orderNo);
        wrapper.eq(Order::getStatus, OrderConstants.ORDER_STATUS_WAIT_RECEIPT);
        return update(wrapper);
    }

    /**
     * 平台端后台分页列表
     *
     * @param request 查询参数
     * @return PageInfo
     */
    @Override
    public PageInfo<PlatformOrderPageResponse> getPlatformAdminPage(OrderSearchRequest request) {
        Page<Order> startPage = PageHelper.startPage(request.getPage(), request.getLimit());
        Map<String, Object> map = CollUtil.newHashMap();
        map.put("status", request.getStatus());
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
        if (StrUtil.isNotBlank(request.getOrderNo())) {
            map.put("orderNo", URLUtil.decode(request.getOrderNo()));
        }
        if (ObjectUtil.isNotNull(request.getType())) {
            map.put("type", request.getType());
        }
        List<PlatformOrderPageResponse> responseList = dao.getPlatformAdminPage(map);
        if (CollUtil.isEmpty(responseList)) {
            return CommonPage.copyPageInfo(startPage, CollUtil.newArrayList());
        }
        List<Integer> merIdList = responseList.stream().map(PlatformOrderPageResponse::getMerId).filter(merId -> merId > 0).distinct().collect(Collectors.toList());
        Map<Integer, Merchant> merchantMap;
        if (CollUtil.isNotEmpty(merIdList)) {
            merchantMap = merchantService.getMerIdMapByIdList(merIdList);
        } else {
            merchantMap = null;
        }
        responseList.forEach(response -> {
            if (response.getMerId() > 0) {
                response.setMerName(merchantMap.get(response.getMerId()).getName());
            }
            if (response.getRefundStatus().equals(2)) {
                response.setRefundNum(orderDetailService.getOrderRefundNum(response.getOrderNo()));
            }
        });
        return CommonPage.copyPageInfo(startPage, responseList);
    }

    private void getOrderRefundNum(String orderNo) {

    }

    /**
     * 获取平台端订单各状态数量
     */
    @Override
    public OrderCountItemResponse getPlatformOrderStatusNum(OrderTabsHeaderRequest request) {
        OrderCountItemResponse response = new OrderCountItemResponse();
        // 全部订单
        response.setAll(getPlatCount(request, OrderConstants.MERCHANT_ORDER_STATUS_ALL));
        // 未支付订单
        response.setUnPaid(getPlatCount(request, OrderConstants.MERCHANT_ORDER_STATUS_UNPAID));
        // 未发货订单
        response.setNotShipped(getPlatCount(request, OrderConstants.MERCHANT_ORDER_STATUS_NOT_SHIPPED));
        // 待收货订单
        response.setSpike(getPlatCount(request, OrderConstants.MERCHANT_ORDER_STATUS_SPIKE));
        // 交易完成订单
        response.setComplete(getPlatCount(request, OrderConstants.MERCHANT_ORDER_STATUS_COMPLETE));
        // 已退款订单
        response.setRefunded(getPlatCount(request, OrderConstants.MERCHANT_ORDER_STATUS_REFUNDED));
        // 已删除订单
        response.setDeleted(getPlatCount(request, OrderConstants.MERCHANT_ORDER_STATUS_DELETED));
        // 待核销订单
        response.setVerification(getPlatCount(request, OrderConstants.MERCHANT_ORDER_STATUS_AWAIT_VERIFICATION));
        // 已收货订单
        response.setReceiving(getPlatCount(request, OrderConstants.MERCHANT_ORDER_STATUS_RECEIVING));
        // 已取消订单
        response.setCancel(getPlatCount(request, OrderConstants.MERCHANT_ORDER_STATUS_CANCEL));

        return response;
    }

    /**
     * 订单详情（平台）
     *
     * @param orderNo 订单编号
     * @return PlatformOrderAdminDetailResponse
     */
    @Override
    public PlatformOrderAdminDetailResponse platformInfo(String orderNo) {
        Order order = getByOrderNo(orderNo);
        PlatformOrderAdminDetailResponse response = new PlatformOrderAdminDetailResponse();
        MerchantOrder merchantOrder = merchantOrderService.getOneByOrderNo(orderNo);
        BeanUtils.copyProperties(merchantOrder, response);
        BeanUtils.copyProperties(order, response);
        List<OrderDetail> orderDetailList = orderDetailService.getByOrderNo(orderNo);
        List<OrderInfoFrontDataResponse> orderInfoList = orderDetailList.stream().map(e -> {
            OrderInfoFrontDataResponse dataResponse = new OrderInfoFrontDataResponse();
            BeanUtils.copyProperties(e, dataResponse);
            return dataResponse;
        }).collect(Collectors.toList());
        response.setOrderDetailList(orderInfoList);

        // 用户信息
        User user = userService.getById(order.getUid());
        response.setNickname(user.getNickname());
        response.setPhone(user.getPhone());
        response.setIsLogoff(user.getIsLogoff());
//        if (merchantOrder.getClerkId() > 0) {
//            SystemAdmin clerkAdmin = systemAdminService.getById(merchantOrder.getClerkId());
//            response.setClerkName(clerkAdmin.getRealName());
//        }
        if (order.getMerId() > 0) {
            Merchant merchant = merchantService.getById(order.getMerId());
            response.setMerName(merchant.getName());
            response.setMerIsSelf(merchant.getIsSelf());
        }

        if (crmebConfig.getPhoneMaskSwitch()) {
            response.setPhone(CrmebUtil.maskMobile(response.getPhone()));
            if (StrUtil.isNotBlank(response.getUserPhone())) {
                response.setUserPhone(CrmebUtil.maskMobile(response.getUserPhone()));
            }
        }
        return response;
    }

    /**
     * 获取订单快递信息(商户端)
     *
     * @param invoiceId 发货单ID
     * @return LogisticsResultVo
     */
    @Override
    public LogisticsResultVo getLogisticsInfoByMerchant(Integer invoiceId, SystemAdmin systemAdmin) {
        OrderInvoice orderInvoice = orderInvoiceService.getById(invoiceId);
        if (ObjectUtil.isNull(orderInvoice)) {
            throw new CrmebException(OrderResultCode.ORDER_INVOICE_NOT_EXIST);
        }
        if (!orderInvoice.getDeliveryType().equals(OrderConstants.ORDER_DELIVERY_TYPE_EXPRESS)) {
            throw new CrmebException(OrderResultCode.ORDER_INVOICE_LOGISTICS_NOT_EXIST);
        }
        MerchantOrder merchantOrder = merchantOrderService.getOneByOrderNo(orderInvoice.getOrderNo());
        if (ObjectUtil.isNull(merchantOrder) || !systemAdmin.getMerId().equals(merchantOrder.getMerId())) {
            throw new CrmebException(OrderResultCode.ORDER_NOT_EXIST);
        }
        return logisticService.info(orderInvoice.getTrackingNumber(), null, Optional.ofNullable(orderInvoice.getExpressCode()).orElse(""), merchantOrder.getUserPhone());
    }

    /**
     * 获取订单快递信息
     *
     * @param invoiceId 发货单ID
     * @return LogisticsResultVo
     */
    @Override
    public LogisticsResultVo getLogisticsInfo(Integer invoiceId) {
        OrderInvoice orderInvoice = orderInvoiceService.getById(invoiceId);
        if (ObjectUtil.isNull(orderInvoice)) {
            throw new CrmebException(OrderResultCode.ORDER_INVOICE_NOT_EXIST);
        }
        if (!orderInvoice.getDeliveryType().equals(OrderConstants.ORDER_DELIVERY_TYPE_EXPRESS)) {
            throw new CrmebException(OrderResultCode.ORDER_INVOICE_LOGISTICS_NOT_EXIST);
        }
        MerchantOrder merchantOrder = merchantOrderService.getOneByOrderNo(orderInvoice.getOrderNo());
        return logisticService.info(orderInvoice.getTrackingNumber(), null, Optional.ofNullable(orderInvoice.getExpressCode()).orElse(""), merchantOrder.getUserPhone());
    }

    /**
     * 核销码核销订单
     *
     * @param verifyCode 核销码
     * @return 核销结果
     */
    @Override
    public Boolean verificationOrderByCode(String verifyCode, SystemAdmin systemAdmin) {
        MerchantOrder merchantOrder = merchantOrderService.getOneByVerifyCode(verifyCode);
//        List<MerchantOrder> merchantOrderListForPrint = CollUtil.newArrayList();
        if (ObjectUtil.isNull(merchantOrder)) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "请输入正确的核销码");
        }
        Order order = getByOrderNo(merchantOrder.getOrderNo());
        if (!systemAdmin.getMerId().equals(order.getMerId())) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "请输入正确的核销码");
        }
        if (!order.getStatus().equals(OrderConstants.ORDER_STATUS_AWAIT_VERIFICATION)) {
            throw new CrmebException(OrderResultCode.ORDER_VERIFICATION);
        }
        if (order.getRefundStatus().equals(OrderConstants.ORDER_REFUND_STATUS_APPLY)) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "请先处理售后，再进行核销操作");
        }
        if (order.getRefundStatus().equals(OrderConstants.ORDER_REFUND_STATUS_ALL)) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "订单已退款");
        }
        order.setStatus(OrderConstants.ORDER_STATUS_TAKE_DELIVERY);
        order.setReceivingTime(DateUtil.date());
//        merchantOrder.setClerkId(systemAdmin.getId());
//        merchantOrderListForPrint.add(merchantOrder);
        Boolean execute = transactionTemplate.execute(e -> {
            updateById(order);
            orderDetailService.takeDelivery(order.getOrderNo());
            merchantOrderService.updateById(merchantOrder);
            return Boolean.TRUE;
        });
        if (execute) {
            redisUtil.lPush(TaskConstants.ORDER_TASK_REDIS_KEY_AFTER_TAKE_BY_USER, order.getOrderNo());
            if (order.getPayType().equals(PayConstants.PAY_TYPE_WE_CHAT) && order.getPayChannel().equals(PayConstants.PAY_CHANNEL_WECHAT_MINI)) {
                String shippingSwitch = systemConfigService.getValueByKey(WeChatConstants.CONFIG_WECHAT_ROUTINE_SHIPPING_SWITCH);
                if (StrUtil.isNotBlank(shippingSwitch) && shippingSwitch.equals("1")) {
                    asyncService.verifyOrderWechatSendUploadShipping(merchantOrder.getOrderNo());
                }
            }
        }
        try {
            // 打印小票 op=1 为方法调用这里也就是支付后自动打印小票的场景
//            merchantPrintService.printReceipt(merchantOrderListForPrint, 3);
            // TODO 原业务逻辑模糊
            Merchant merchant = merchantService.getByIdException(merchantOrder.getMerId());
            // 小票打印开关：0关闭，1=手动打印，2=自动打印，3=自动和手动
            if (merchant.getReceiptPrintingSwitch() == 0) {
                throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "小票打印功能未开启");
            }
            if (merchant.getReceiptPrintingSwitch() < 2) {
                throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "小票打印未开启自动打印");
            }
            merchantPrintService.printReceipt(merchantOrder);
        } catch (Exception e) {
            logger.error(StrUtil.format("小票打印异常，Exception：{}", e.getMessage()), e);
        }
        return execute;
    }

    /**
     * 根据核销码获取
     *
     * @param verifyCode  授权码
     * @param systemAdmin 移动端商户管理员
     * @return 待审核订单详情
     */
    @Override
    public MerchantOrderPageResponse getVerificationOrderByCode(String verifyCode, SystemAdmin systemAdmin) {
        MerchantOrder merchantOrder = merchantOrderService.getByVerifyCodeForMerchant(verifyCode, systemAdmin.getMerId());
        if (ObjectUtil.isNull(merchantOrder)) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "请输入正确的核销码");
        }
        Order order = getByOrderNo(merchantOrder.getOrderNo());
        if (!systemAdmin.getMerId().equals(order.getMerId())) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "请输入正确的核销码");
        }
        if (!order.getStatus().equals(OrderConstants.ORDER_STATUS_AWAIT_VERIFICATION)) {
            throw new CrmebException(OrderResultCode.ORDER_VERIFICATION);
        }
        MerchantOrderPageResponse merchantOrderPageResponse = new MerchantOrderPageResponse();
        BeanUtils.copyProperties(merchantOrder, merchantOrderPageResponse);
        List<OrderInfoFrontDataResponse> infoResponseList = CollUtil.newArrayList();
        List<OrderDetail> orderDetailList = orderDetailService.getByOrderNo(merchantOrder.getOrderNo());
        orderDetailList.forEach(od -> {
            OrderInfoFrontDataResponse orderInfoResponse = new OrderInfoFrontDataResponse();
            BeanUtils.copyProperties(od, orderInfoResponse);
            infoResponseList.add(orderInfoResponse);
        });
        if (order.getRefundStatus() > 0) {
            merchantOrderPageResponse.setUserRefundSign(refundOrderService.isOrderContainUserRefund(order.getOrderNo()));
        }
        merchantOrderPageResponse.setInfoResponseList(infoResponseList);
        return merchantOrderPageResponse;
    }

    /**
     * 通过日期获取商品交易件数
     *
     * @param date 日期，yyyy-MM-dd格式
     * @return Integer
     */
    @Override
    public Integer getOrderProductNumByDate(String date) {
        QueryWrapper<Order> wrapper = new QueryWrapper<>();
        wrapper.select("IFNULL(sum(total_num), 0) as total_num");
        wrapper.eq("is_del", 0);
        wrapper.apply("date_format(create_time, '%Y-%m-%d') = {0}", date);
        Order order = dao.selectOne(wrapper);
        return order.getTotalNum();
    }

    /**
     * 通过日期获取商品交易成功件数
     *
     * @param date 日期，yyyy-MM-dd格式
     * @return Integer
     */
    @Override
    public Integer getOrderSuccessProductNumByDate(String date) {
        QueryWrapper<Order> wrapper = new QueryWrapper<>();
        wrapper.select("IFNULL(sum(total_num), 0) as total_num");
        wrapper.eq("paid", 1);
        wrapper.eq("is_del", 0);
        wrapper.apply("date_format(pay_time, '%Y-%m-%d') = {0}", date);
        Order order = dao.selectOne(wrapper);
        return order.getTotalNum();
    }

    /**
     * 通过日期获取订单数量
     *
     * @param date 日期，yyyy-MM-dd格式
     * @return Integer
     */
    @Override
    public Integer getOrderNumByDate(Integer merId, String date) {
        QueryWrapper<Order> wrapper = new QueryWrapper<>();
        wrapper.select("id");
        if (merId > 0) {
            wrapper.eq("mer_id", merId);
        }
        wrapper.eq("is_del", 0);
        wrapper.apply("date_format(create_time, '%Y-%m-%d') = {0}", date);
        return dao.selectCount(wrapper);
    }

    /**
     * 通过日期获取支付订单金额
     *
     * @param date 日期，yyyy-MM-dd格式
     * @return BigDecimal
     */
    @Override
    public BigDecimal getPayOrderAmountByDate(Integer merId, String date) {
        QueryWrapper<Order> wrapper = new QueryWrapper<>();
        wrapper.select("pay_price");
        wrapper.eq("paid", 1);
        if (merId > 0) {
            wrapper.eq("mer_id", merId);
        }
        wrapper.eq("is_del", false);
        wrapper.apply("date_format(create_time, '%Y-%m-%d') = {0}", date);
        List<Order> orderList = dao.selectList(wrapper);
        if (CollUtil.isEmpty(orderList)) {
            return BigDecimal.ZERO;
        }
        return orderList.stream().map(Order::getPayPrice).reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * 订单细节详情列表
     *
     * @param orderNo 订单号
     * @return 订单细节详情列表
     */
    @Override
    public List<OrderDetail> getDetailList(String orderNo, SystemAdmin systemAdmin) {
        getByOrderNoAndMerId(orderNo, systemAdmin.getMerId());
        return orderDetailService.getShipmentByOrderNo(orderNo);
    }

    /**
     * 获取订单发货单列表(商户端)
     *
     * @param orderNo 订单号
     * @return 发货单列表
     */
    @Override
    public List<OrderInvoiceResponse> getInvoiceListByMerchant(String orderNo, SystemAdmin systemAdmin) {
        getByOrderNoAndMerId(orderNo, systemAdmin.getMerId());
        return getInvoiceList(orderNo);
    }

    /**
     * 获取订单发货单列表
     *
     * @param orderNo 订单号
     * @return 发货单列表
     */
    @Override
    public List<OrderInvoiceResponse> getInvoiceList(String orderNo) {
        return orderInvoiceService.findByOrderNo(orderNo);
    }

    /**
     * 获取可以自动完成的订单
     *
     * @param autoCompleteDay 自动完成订单天数
     * @return 可以自动完成的订单列表
     */
    @Override
    public List<Order> findCanCompleteOrder(Integer autoCompleteDay) {
        DateTime autoCompleteDate = DateUtil.offsetDay(DateUtil.date(), -autoCompleteDay);
        LambdaQueryWrapper<Order> lqw = Wrappers.lambdaQuery();
        lqw.select(Order::getId, Order::getUid, Order::getOrderNo);
        lqw.eq(Order::getStatus, OrderConstants.ORDER_STATUS_TAKE_DELIVERY);
        lqw.eq(Order::getLevel, OrderConstants.ORDER_LEVEL_MERCHANT);
        lqw.le(Order::getReceivingTime, autoCompleteDate);
        return dao.selectList(lqw);
    }

    /**
     * 按订单号批量完成订单
     *
     * @param orderNoList 订单号列表
     * @return Boolean
     */
    @Override
    public Boolean batchCompleteByOrderNo(List<String> orderNoList) {
        LambdaUpdateWrapper<Order> wrapper = Wrappers.lambdaUpdate();
        wrapper.set(Order::getStatus, OrderConstants.ORDER_STATUS_COMPLETE);
        wrapper.in(Order::getOrderNo, orderNoList);
        return update(wrapper);
    }

    /**
     * 获取订单数量（订单状态， 用户id）
     *
     * @param status 订单状态（0：待支付，1：待发货,2：部分发货， 3：待核销，4：待收货,5：已收货,6：已完成，9：已取消）
     * @param userId 用户ID
     * @return 订单数量
     */
    @Override
    public Integer getCountByStatusAndUid(Integer status, Integer userId) {
        if (status < 0) {
            return 0;
        }
        LambdaQueryWrapper<Order> lqw = Wrappers.lambdaQuery();
        lqw.eq(Order::getUid, userId);
        lqw.eq(Order::getIsDel, false);
        if (OrderConstants.ORDER_STATUS_WAIT_SHIPPING.equals(status)) {
            lqw.in(Order::getStatus, OrderConstants.ORDER_STATUS_WAIT_SHIPPING, OrderConstants.ORDER_STATUS_PART_SHIPPING);
            lqw.in(Order::getGroupBuyRecordStatus, 99, 10);
        } else if (OrderConstants.ORDER_STATUS_AWAIT_VERIFICATION.equals(status)) {
            lqw.eq(Order::getStatus, OrderConstants.ORDER_STATUS_AWAIT_VERIFICATION);
            lqw.in(Order::getGroupBuyRecordStatus, 99, 10);
        } else  {
            lqw.eq(Order::getStatus, status);
        }
        lqw.eq(Order::getIsUserDel, false);
        lqw.eq(Order::getIsMerchantDel, false);
        lqw.ne(Order::getSecondType, OrderConstants.ORDER_SECOND_TYPE_VIDEO);
        lqw.eq(Order::getIsDel, false);
        lqw.ne(Order::getRefundStatus, OrderConstants.MERCHANT_REFUND_ORDER_STATUS_REFUND);
        return dao.selectCount(lqw);
    }

    /**
     * 通过日期获取支付订单数量
     *
     * @param date 日期，yyyy-MM-dd格式
     * @return Integer
     */
    @Override
    public Integer getPayOrderNumByDate(String date) {
        QueryWrapper<Order> wrapper = new QueryWrapper<>();
        wrapper.select("id");
        wrapper.eq("paid", 1);
        wrapper.eq("is_del", 0);
        wrapper.apply("date_format(pay_time, '%Y-%m-%d') = {0}", date);
        return dao.selectCount(wrapper);
    }

    /**
     * 获取推广订单总金额
     *
     * @param orderNoList 订单编号列表
     * @return BigDecimal
     */
    @Override
    public BigDecimal getSpreadOrderTotalPriceByOrderList(List<String> orderNoList) {
        LambdaQueryWrapper<Order> lqw = new LambdaQueryWrapper<>();
        lqw.select(Order::getPayPrice);
        lqw.in(Order::getOrderNo, orderNoList);
        List<Order> orderList = dao.selectList(lqw);
        return orderList.stream().map(Order::getPayPrice).reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * 订单拆单删除
     *
     * @param orderNo 订单号
     */
    @Override
    public Boolean paySplitDelete(String orderNo) {
        LambdaUpdateWrapper<Order> wrapper = Wrappers.lambdaUpdate();
        wrapper.set(Order::getIsDel, true);
        wrapper.eq(Order::getOrderNo, orderNo);
        wrapper.eq(Order::getIsDel, false);
        return update(wrapper);
    }

    /**
     * 通过原始单号获取订单列表
     *
     * @param orderNo 原始单号
     * @return 订单列表
     */
    @Override
    public List<Order> getByPlatOrderNo(String orderNo) {
        LambdaQueryWrapper<Order> lqw = Wrappers.lambdaQuery();
        lqw.eq(Order::getPlatOrderNo, orderNo);
        return dao.selectList(lqw);
    }

    /**
     * 判断用户是否存在待处理订单
     * 待发货、部分发货、待核销
     *
     * @param uid 用户id
     */
    @Override
    public Boolean isExistPendingOrderByUid(Integer uid) {
        LambdaQueryWrapper<Order> lqw = Wrappers.lambdaQuery();
        lqw.select(Order::getId);
        lqw.eq(Order::getUid, uid);
        lqw.eq(Order::getIsDel, 0);
        lqw.in(Order::getStatus, OrderConstants.ORDER_STATUS_WAIT_SHIPPING, OrderConstants.ORDER_STATUS_PART_SHIPPING, OrderConstants.ORDER_STATUS_AWAIT_VERIFICATION);
        lqw.ne(Order::getRefundStatus, OrderConstants.ORDER_REFUND_STATUS_REFUND);
        lqw.last(" limit 1");
        Order order = dao.selectOne(lqw);
        return ObjectUtil.isNotNull(order);
    }

    /**
     * 获取待收货订单
     *
     * @param sendTime 发货时间
     * @return List
     */
    @Override
    public List<Order> findAwaitTakeDeliveryOrderList(String sendTime) {
        LambdaQueryWrapper<Order> lqw = Wrappers.lambdaQuery();
        lqw.eq(Order::getStatus, OrderConstants.ORDER_STATUS_WAIT_RECEIPT);
        lqw.ne(Order::getRefundStatus, OrderConstants.ORDER_REFUND_STATUS_REFUND);
        lqw.le(Order::getUpdateTime, sendTime);
        lqw.eq(Order::getIsDel, false);
        return dao.selectList(lqw);
    }

    /**
     * 获取待发货订单数量
     *
     * @return Integer
     */
    @Override
    public Integer getNotShippingNum(Integer merId) {
        return getCount("", OrderConstants.MERCHANT_ORDER_STATUS_NOT_SHIPPED, merId);
    }

    /**
     * 获取待核销订单数量
     *
     * @return Integer
     */
    @Override
    public Integer getAwaitVerificationNum(Integer merId) {
        return getCount("", OrderConstants.MERCHANT_ORDER_STATUS_AWAIT_VERIFICATION, merId);
    }

    /**
     * 获取用户购买的商品数量
     *
     * @param uid         用户ID
     * @param proId       商品ID
     * @param marketingType 营销类型
     */
    @Override
    public Integer getProductNumCount(Integer uid, Integer proId, Integer marketingType) {
        return dao.getProductNumCount(uid, proId, marketingType);
    }

    /**
     * 获取某一天的所有数据
     *
     * @param merId 商户id，0为所有商户
     * @param date  日期：年-月-日
     * @return List
     */
    @Override
    public List<Order> findPayByDate(Integer merId, String date) {
        LambdaQueryWrapper<Order> lqw = Wrappers.lambdaQuery();
        if (merId > 0) {
            lqw.eq(Order::getMerId, merId);
        }
        lqw.eq(Order::getPaid, 1);
        lqw.ne(Order::getStatus, OrderConstants.ORDER_STATUS_WAIT_PAY);
        lqw.eq(Order::getIsDel, 0);
        lqw.apply("date_format(pay_time, '%Y-%m-%d') = {0}", date);
        return dao.selectList(lqw);
    }

    /**
     * 获取导出订单列表
     *
     * @param request 请求参数
     */
    @Override
    public List<Order> findExportList(OrderSearchRequest request) {
        LambdaQueryWrapper<Order> lqw = Wrappers.lambdaQuery();
        if (ObjectUtil.isNotNull(request.getMerId()) && request.getMerId() > 0) {
            lqw.eq(Order::getMerId, request.getMerId());
        }
        if (StrUtil.isNotBlank(request.getOrderNo())) {
            lqw.like(Order::getOrderNo, URLUtil.decode(request.getOrderNo()));
        }
        if (ObjectUtil.isNotNull(request.getType())) {
            lqw.eq(Order::getType, request.getType());
        }
        if (StrUtil.isNotEmpty(request.getDateLimit())) {
            getRequestTimeWhere(lqw, request.getDateLimit());
        }
        getMerchantStatusWhere(lqw, request.getStatus());
        lqw.orderByDesc(Order::getId);
        return dao.selectList(lqw);
    }

    /**
     * 订单是否全部发货（总订单）
     *
     * @param orderNo 商户订单号
     */
    @Override
    public Boolean isAllSendGoods(String orderNo) {
        List<Order> orderList = getByPlatOrderNo(orderNo);
        boolean isAll = true;
        for (Order order : orderList) {
            if (order.getStatus() < OrderConstants.ORDER_STATUS_AWAIT_VERIFICATION) {
                isAll = false;
                break;
            }
        }
        return isAll;
    }

    /**
     * 虚拟发货
     *
     * @param order 商户订单信息
     */
    @Override
    public Boolean virtualShipment(Order order) {
        List<OrderDetail> orderDetailList = orderDetailService.getByOrderNo(order.getOrderNo());
        MerchantOrder merchantOrder = merchantOrderService.getOneByOrderNo(order.getOrderNo());
        merchantOrder.setIsSplitDelivery(Boolean.FALSE);
        merchantOrder.setDeliveryType(OrderConstants.ORDER_DELIVERY_TYPE_FICTITIOUS);
        order.setStatus(OrderConstants.ORDER_STATUS_WAIT_RECEIPT);
        orderDetailList.forEach(od -> {
            od.setDeliveryNum(od.getPayNum());
        });
        Boolean execute = transactionTemplate.execute(i -> {
            updateById(order);
            merchantOrderService.updateById(merchantOrder);
            orderDetailService.updateBatchById(orderDetailList);
            //订单记录增加
            orderStatusService.createLog(order.getOrderNo(), OrderStatusConstants.ORDER_STATUS_DELIVERY_VI, OrderStatusConstants.ORDER_LOG_MESSAGE_DELIVERY_VI);

            // 订单虚拟收货
            takeDelivery(order.getOrderNo());
            orderDetailService.takeDelivery(order.getOrderNo());
            orderStatusService.createLog(order.getOrderNo(), OrderStatusConstants.ORDER_STATUS_USER_TAKE_DELIVERY, OrderStatusConstants.ORDER_LOG_USER_RECEIPT);

            return Boolean.TRUE;
        });

        if (!execute) {
            logger.error("虚拟发货失败,订单号={}", order.getOrderNo());
            return execute;
        }
        // 虚拟发货 + 小程序发货管理
        if (order.getPayType().equals(PayConstants.PAY_TYPE_WE_CHAT) && order.getPayChannel().equals(PayConstants.PAY_CHANNEL_WECHAT_MINI)) {
            String shippingSwitch = systemConfigService.getValueByKey(WeChatConstants.CONFIG_WECHAT_ROUTINE_SHIPPING_SWITCH);
            if (StrUtil.isNotBlank(shippingSwitch) && shippingSwitch.equals("1")) {
                asyncService.wechatSendUploadShipping(merchantOrder.getOrderNo());
            }
        }
        redisUtil.lPush(TaskConstants.ORDER_TASK_REDIS_KEY_AFTER_TAKE_BY_USER, order.getOrderNo());
        return execute;
    }

    /**
     * 商户直接退款
     */
    @Override
    public Boolean directRefund(MerchantOrderDirectRefundRequest request, SystemAdmin systemAdmin) {
        if (request.getReturnType().equals(2) && CollUtil.isEmpty(request.getDetailList())) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "请选择要退款的商品");
        }
        Order order = getByOrderNoAndMerId(request.getOrderNo(), systemAdmin.getMerId());
        if (order.getRefundStatus().equals(OrderConstants.ORDER_REFUND_STATUS_ALL)) {
            throw new CrmebException(OrderResultCode.ORDER_REFUND_ED);
        }
        // 判断订单是否发生用户退款流程
        if (isContainUserRefund(order)) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "发生用户申请退款的订单无法商家直接退款");
        }
        List<OrderDetail> orderDetailList = new ArrayList<>();
        if (request.getReturnType().equals(2)) {
            List<MerchantOrderDirectRefundDetailRequest> detailList = request.getDetailList();
            for (MerchantOrderDirectRefundDetailRequest detailRequest : detailList) {
                OrderDetail orderDetail = orderDetailService.getById(detailRequest.getOrderDetailId());
                if (ObjectUtil.isNull(orderDetail) || !orderDetail.getOrderNo().equals(order.getOrderNo())) {
                    throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "选择的退款商品订单详情不存在,订单详情ID=" + detailRequest.getOrderDetailId());
                }
                if (orderDetail.getApplyRefundNum() > 0) {
                    throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "选择的退款商品订单详情存在退款申请，无法操作直接退款,订单详情ID=" + detailRequest.getOrderDetailId());
                }
                int canNum = orderDetail.getPayNum() - orderDetail.getRefundNum();
                if (canNum < detailRequest.getNum()) {
                    throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "选择的退款数量大于可退款数量,订单详情ID=" + detailRequest.getOrderDetailId());
                }
                orderDetail.setApplyRefundNum(detailRequest.getNum());
                orderDetailList.add(orderDetail);
            }
        } else {
            orderDetailList = orderDetailService.getByOrderNo(order.getOrderNo());
            for (int i = 0; i < orderDetailList.size(); ) {
                OrderDetail orderDetail = orderDetailList.get(i);
                if (orderDetail.getApplyRefundNum() > 0) {
                    throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "选择的退款商品订单详情存在退款申请，无法操作直接退款,订单详情ID=" + orderDetail.getId());
                }
                if (orderDetail.getPayNum() <= orderDetail.getRefundNum()) {
                    orderDetailList.remove(i);
                    continue;
                }
                orderDetail.setApplyRefundNum(orderDetail.getPayNum() - orderDetail.getRefundNum());
                i++;
            }
        }
        return refundOrderService.merchantDirectRefund(order, orderDetailList);
    }

    /**
     * 修改发货单配送信息
     */
    @Override
    public Boolean updateInvoice(OrderInvoiceUpdateRequest request, SystemAdmin systemAdmin) {
        OrderInvoice invoice = orderInvoiceService.getById(request.getId());
        if (ObjectUtil.isNull(invoice) || !invoice.getMerId().equals(systemAdmin.getMerId())) {
            throw new CrmebException(OrderResultCode.ORDER_INVOICE_NOT_EXIST);
        }
        validateUpdateInvoice(invoice.getDeliveryType(), request);
        if (invoice.getDeliveryType().equals(OrderConstants.ORDER_DELIVERY_TYPE_EXPRESS)) {
            Express express = expressService.getByCode(request.getExpressCode());
            if (ObjectUtil.isNull(express)) {
                throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "未找到快递公司");
            }
            String trackingNumber = request.getExpressNumber();
            invoice.setExpressCode(express.getCode());
            invoice.setExpressName(express.getName());
            invoice.setTrackingNumber(trackingNumber);
        }
        if (invoice.getDeliveryType().equals(OrderConstants.ORDER_DELIVERY_TYPE_MERCHANT)) {
            invoice.setDeliveryCarrier(request.getDeliveryCarrier());
            invoice.setCarrierPhone(request.getCarrierPhone());
        }
        if (invoice.getDeliveryType().equals(OrderConstants.ORDER_DELIVERY_TYPE_NO_NEED)) {
            invoice.setDeliveryMark(StrUtil.isNotBlank(request.getDeliveryMark()) ? request.getDeliveryMark() : "");
        }
        return orderInvoiceService.updateById(invoice);
    }

    /**
     * 积分订单各状态数量
     */
    @Override
    public OrderCountItemResponse getIntegralOrderStatusNum(IntegralOrderTabsHeaderRequest request) {
        OrderCountItemResponse response = new OrderCountItemResponse();
        // 全部订单
        response.setAll(getIntegralOrderCount(request, OrderConstants.MERCHANT_ORDER_STATUS_ALL));
        // 未支付订单
        response.setUnPaid(getIntegralOrderCount(request, OrderConstants.MERCHANT_ORDER_STATUS_UNPAID));
        // 未发货订单
        response.setNotShipped(getIntegralOrderCount(request, OrderConstants.MERCHANT_ORDER_STATUS_NOT_SHIPPED));
        // 待收货订单
        response.setSpike(getIntegralOrderCount(request, OrderConstants.MERCHANT_ORDER_STATUS_SPIKE));
        // 已收货订单
        response.setReceiving(getIntegralOrderCount(request, OrderConstants.MERCHANT_ORDER_STATUS_RECEIVING));
        // 交易完成订单
        response.setComplete(getIntegralOrderCount(request, OrderConstants.MERCHANT_ORDER_STATUS_COMPLETE));
        // 已删除订单
        response.setDeleted(getIntegralOrderCount(request, OrderConstants.MERCHANT_ORDER_STATUS_DELETED));
        response.setCancel(getIntegralOrderCount(request, OrderConstants.MERCHANT_ORDER_STATUS_CANCEL));
        return response;
    }

    /**
     * 积分订单分页列表（平台端）
     */
    @Override
    public PageInfo<IntegralOrderPageResponse> findIntegralOrderPageByPlat(IntegralOrderSearchRequest request) {
        Page<Order> startPage = PageHelper.startPage(request.getPage(), request.getLimit());
        Map<String, Object> map = CollUtil.newHashMap();
        map.put("status", request.getStatus());
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
        if (StrUtil.isNotBlank(request.getOrderNo())) {
            map.put("orderNo", URLUtil.decode(request.getOrderNo()));
        }
        List<IntegralOrderPageResponse> responseList = dao.findIntegralOrderPageByPlat(map);
        if (CollUtil.isEmpty(responseList)) {
            return CommonPage.copyPageInfo(startPage, CollUtil.newArrayList());
        }
        return CommonPage.copyPageInfo(startPage, responseList);
    }

    /**
     * 积分订单备注
     */
    @Override
    public Boolean integralOrderMark(OrderRemarkRequest request) {
        Order order = getByOrderNoAndMerId(request.getOrderNo(), 0);
        MerchantOrder merchantOrder = merchantOrderService.getOneByOrderNo(order.getOrderNo());
        merchantOrder.setMerchantRemark(request.getRemark());
        return merchantOrderService.updateById(merchantOrder);
    }

    /**
     * 积分订单详情
     */
    @Override
    public OrderAdminDetailResponse adminIntegralOrderDetail(String orderNo) {
        Order order = getByOrderNoAndMerId(orderNo, 0);
        if (order.getIsDel()) {
            throw new CrmebException(OrderResultCode.ORDER_NOT_EXIST);
        }
        OrderAdminDetailResponse orderAdminDetailResponse = new OrderAdminDetailResponse();
        MerchantOrder merchantOrder = merchantOrderService.getOneByOrderNo(orderNo);
        BeanUtils.copyProperties(merchantOrder, orderAdminDetailResponse);
        BeanUtils.copyProperties(order, orderAdminDetailResponse);
        List<OrderDetail> orderDetailList = orderDetailService.getByOrderNo(orderNo);
        List<OrderInfoFrontDataResponse> orderInfoList = orderDetailList.stream().map(e -> {
            OrderInfoFrontDataResponse dataResponse = new OrderInfoFrontDataResponse();
            BeanUtils.copyProperties(e, dataResponse);
            return dataResponse;
        }).collect(Collectors.toList());
        orderAdminDetailResponse.setOrderDetailList(orderInfoList);

        if (order.getRefundStatus() > 0) {
            orderAdminDetailResponse.setUserRefundSign(refundOrderService.isOrderContainUserRefund(order.getOrderNo()));
        }
        // 用户信息
        User user = userService.getById(order.getUid());
        orderAdminDetailResponse.setNickname(user.getNickname());
        orderAdminDetailResponse.setPhone(user.getPhone());
        orderAdminDetailResponse.setIsLogoff(user.getIsLogoff());
        orderAdminDetailResponse.setAvatar(user.getAvatar());
        orderAdminDetailResponse.setUserLevel(user.getLevel());
        orderAdminDetailResponse.setIsPaidMember(user.getIsPaidMember());
        if (crmebConfig.getPhoneMaskSwitch() && StrUtil.isNotBlank(orderAdminDetailResponse.getPhone())) {
            orderAdminDetailResponse.setPhone(CrmebUtil.maskMobile(orderAdminDetailResponse.getPhone()));
            orderAdminDetailResponse.setUserPhone(CrmebUtil.maskMobile(orderAdminDetailResponse.getUserPhone()));
        }
        return orderAdminDetailResponse;
    }


    /**
     * 根据pt订单号更新商户订单号 拼团中使用
     * @param ptOrderNo 平台订单号
     * @return 执行结果
     */
    @Override
    public Order getMerchantOrderNoByPtOrderNoForGroupBuy(String ptOrderNo){
        // 根据拼团中的pt订单号获取商户订单号 之后 再更新拼团用户购买记录中的订单号为商户订单号
        Order order = getOne(Wrappers.lambdaQuery(Order.class).eq(Order::getPlatOrderNo, ptOrderNo));
        if(ObjectUtil.isNull(order)){
            logger.error("拼团订单更新订单号查询失败！: ptNo:${}", ptOrderNo);
        }

        return order;
    }

    private void validateUpdateInvoice(String deliveryType, OrderInvoiceUpdateRequest request) {
        if (deliveryType.equals(OrderConstants.ORDER_DELIVERY_TYPE_EXPRESS)) {
            if (StrUtil.isBlank(request.getExpressNumber())
                    && StrUtil.isNotBlank(request.getExpressRecordType())
                    && request.getExpressRecordType().equals(OrderConstants.ORDER_DELIVERY_TYPE_EXPRESS_E))
                throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "请填写快递单号");
            if (StrUtil.isBlank(request.getExpressCode()))
                throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "请选择快递公司");
        }
        if (deliveryType.equals(OrderConstants.ORDER_DELIVERY_TYPE_MERCHANT)) {
            if (StrUtil.isBlank(request.getDeliveryCarrier()))
                throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "请填写配送人员");
            if (StrUtil.isBlank(request.getCarrierPhone()))
                throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "请填写配送人员手机号");
        }
    }

    /**
     * 是否包含用户退款
     */
    private Boolean isContainUserRefund(Order order) {
        if (order.getRefundStatus().equals(0)) {
            return Boolean.FALSE;
        }
        return refundOrderService.isOrderContainUserRefund(order.getOrderNo());
    }

    /**
     * 根据订单编号获取订单
     *
     * @param orderNo 订单编号
     * @param merId   商户ID
     * @return Order
     * @Author 莫名
     * @Date 2022/10/8 11:40
     */
    private Order getByOrderNoAndMerId(String orderNo, Integer merId) {
        LambdaQueryWrapper<Order> lqw = Wrappers.lambdaQuery();
        lqw.eq(Order::getOrderNo, orderNo);
        lqw.eq(Order::getMerId, merId);
        lqw.last(" limit 1");
        Order order = dao.selectOne(lqw);
        if (ObjectUtil.isNull(order)) {
            throw new CrmebException(OrderResultCode.ORDER_NOT_EXIST);
        }
        return order;
    }

    /**
     * 拆单发货
     *
     * @param request         发货参数
     * @param order           订单
     * @param merchantOrder   订单商户信息
     * @param orderDetailList 订单详情列表鸟
     */
    private Boolean splitSendExpress(OrderSendRequest request, Order order, MerchantOrder merchantOrder, List<OrderDetail> orderDetailList) {
        Express express = expressService.getByCode(request.getExpressCode());
        if (ObjectUtil.isNull(express)) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "未找到快递公司");
        }
        // 筛选需要发货的订单详情
        List<SplitOrderSendDetailRequest> detailRequestList = request.getDetailList();
        List<OrderInvoiceDetail> orderInvoiceDetailList = new ArrayList<>();
        List<OrderDetail> orderDetailUpdateList = new ArrayList<>();
        detailRequestList.forEach(detail -> {
            OrderDetail orderDetail = orderDetailList.stream().filter(e -> e.getId().equals(detail.getOrderDetailId())).findAny().orElse(null);
            orderDetail.setDeliveryNum(orderDetail.getDeliveryNum() + detail.getNum());
            OrderInvoiceDetail invoiceDetail = new OrderInvoiceDetail();
            BeanUtils.copyProperties(orderDetail, invoiceDetail);
            invoiceDetail.setNum(detail.getNum());
            invoiceDetail.setCreateTime(DateUtil.date());
            invoiceDetail.setUpdateTime(DateUtil.date());
            orderInvoiceDetailList.add(invoiceDetail);
            orderDetailUpdateList.add(orderDetail);
        });
        // String trackingNumber = request.getExpressNumber();
        // 在这里处理电子面单
        ElectrResponse electrResponse = expressDump(request, merchantOrder, express);
        logger.info("电子面单返回结果：{}", JSONObject.toJSONString(electrResponse));
        // 生成发货单
        OrderInvoice invoice = new OrderInvoice();
        invoice.setMerId(order.getMerId());
        invoice.setOrderNo(order.getOrderNo());
        invoice.setUid(order.getUid());
        invoice.setExpressCode(electrResponse.getCode());
        invoice.setExpressName(electrResponse.getLabel());
        invoice.setTrackingNumber(electrResponse.getKuaidinum());
        invoice.setExpressRecordType(request.getExpressRecordType());
        invoice.setTotalNum(orderInvoiceDetailList.stream().mapToInt(OrderInvoiceDetail::getNum).sum());

        merchantOrder.setIsSplitDelivery(Boolean.TRUE);
        merchantOrder.setDeliveryType(OrderConstants.ORDER_DELIVERY_TYPE_EXPRESS);
        order.setStatus(OrderConstants.ORDER_STATUS_PART_SHIPPING);

        String message = OrderStatusConstants.ORDER_LOG_MESSAGE_EXPRESS.replace("{deliveryName}",
                express.getName()).replace("{deliveryCode}", electrResponse.getKuaidinum());
        Boolean execute = transactionTemplate.execute(i -> {
            updateById(order);
            merchantOrderService.updateById(merchantOrder);
            orderDetailService.updateBatchById(orderDetailUpdateList);
            orderInvoiceService.save(invoice);
            orderInvoiceDetailList.forEach(e -> e.setInvoiceId(invoice.getId()));
            orderInvoiceDetailService.saveBatch(orderInvoiceDetailList);
            //订单记录增加
            orderStatusService.createLog(order.getOrderNo(), OrderStatusConstants.ORDER_STATUS_EXPRESS_SPLIT_OLD, message);
            return Boolean.TRUE;
        });
        if (!execute) throw new CrmebException("快递拆单发货失败！");
        List<OrderDetail> detailList = orderDetailService.getByOrderNo(order.getOrderNo());
        long count = detailList.stream().filter(e -> e.getPayNum() > (e.getDeliveryNum() + e.getRefundNum())).count();
        if (count <= 0) {
            order.setStatus(OrderConstants.ORDER_STATUS_WAIT_RECEIPT);
            updateById(order);
            if (order.getPayType().equals(PayConstants.PAY_TYPE_WE_CHAT) && order.getPayChannel().equals(PayConstants.PAY_CHANNEL_WECHAT_MINI)) {
                String shippingSwitch = systemConfigService.getValueByKey(WeChatConstants.CONFIG_WECHAT_ROUTINE_SHIPPING_SWITCH);
                if (StrUtil.isNotBlank(shippingSwitch) && shippingSwitch.equals("1")) {
                    asyncService.wechatSendUploadShipping(order.getOrderNo());
                }
            }
        }

        SystemNotification payNotification = systemNotificationService.getByMark(NotifyConstants.DELIVER_GOODS_MARK);
        // 发送消息通知
        pushMessageOrder(order, payNotification, invoice);
        return execute;
    }

    /**
     * 快递发货
     *
     * @param request       发货参数
     * @param order         订单信息
     * @param merchantOrder 订单商户信息
     */
    private Boolean sendExpress(OrderSendRequest request, Order order, MerchantOrder merchantOrder) {
        //快递公司信息
        Express express = expressService.getByCode(request.getExpressCode());
        if (ObjectUtil.isNull(express)) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "未找到快递公司");
        }
        // 筛选需要发货的订单详情
        List<OrderDetail> orderDetailList = orderDetailService.getByOrderNo(order.getOrderNo());
        List<OrderInvoiceDetail> orderInvoiceDetailList = new ArrayList<>();
        List<OrderDetail> orderDetailUpdateList = new ArrayList<>();
        orderDetailList.forEach(od -> {
            if (od.getPayNum() > od.getDeliveryNum()) {
                OrderInvoiceDetail invoiceDetail = new OrderInvoiceDetail();
                BeanUtils.copyProperties(od, invoiceDetail);
                invoiceDetail.setNum(od.getPayNum() - od.getDeliveryNum());
                invoiceDetail.setCreateTime(DateUtil.date());
                invoiceDetail.setUpdateTime(DateUtil.date());
                orderInvoiceDetailList.add(invoiceDetail);
                od.setDeliveryNum(od.getPayNum());
                orderDetailUpdateList.add(od);
            }
        });
        if (CollUtil.isEmpty(orderInvoiceDetailList)) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "订单没有需要发货的商品");
        }
        // String trackingNumber = request.getExpressNumber();
        // 在这里处理电子面单
        ElectrResponse electrResponse = expressDump(request, merchantOrder, express);
        logger.info("电子面单返回结果：{}", JSONObject.toJSONString(electrResponse));

        // 生成发货单
        OrderInvoice invoice = new OrderInvoice();
        invoice.setMerId(order.getMerId());
        invoice.setOrderNo(order.getOrderNo());
        invoice.setUid(order.getUid());
        invoice.setExpressCode(electrResponse.getCode());
        invoice.setExpressName(electrResponse.getLabel());
        invoice.setTrackingNumber(electrResponse.getKuaidinum());
        invoice.setExpressRecordType(request.getExpressRecordType());
        invoice.setTotalNum(orderInvoiceDetailList.stream().mapToInt(OrderInvoiceDetail::getNum).sum());

        merchantOrder.setIsSplitDelivery(Boolean.FALSE);
        merchantOrder.setDeliveryType(OrderConstants.ORDER_DELIVERY_TYPE_EXPRESS);
        order.setStatus(OrderConstants.ORDER_STATUS_WAIT_RECEIPT);

        String message = OrderStatusConstants.ORDER_LOG_MESSAGE_EXPRESS.replace("{deliveryName}",
                express.getName()).replace("{deliveryCode}", electrResponse.getKuaidinum());

        Boolean execute = transactionTemplate.execute(i -> {
            updateById(order);
            merchantOrderService.updateById(merchantOrder);
            orderDetailService.updateBatchById(orderDetailUpdateList);
            orderInvoiceService.save(invoice);
            orderInvoiceDetailList.forEach(e -> e.setInvoiceId(invoice.getId()));
            orderInvoiceDetailService.saveBatch(orderInvoiceDetailList);
            //订单记录增加
            orderStatusService.createLog(request.getOrderNo(), OrderStatusConstants.ORDER_STATUS_EXPRESS, message);
            return Boolean.TRUE;
        });

        if (!execute) throw new CrmebException("快递发货失败！");
        // 发送消息通知
        SystemNotification payNotification = systemNotificationService.getByMark(NotifyConstants.DELIVER_GOODS_MARK);
        pushMessageOrder(order, payNotification, invoice);
        // 全部发货 + 小程序发货管理
        if (order.getPayType().equals(PayConstants.PAY_TYPE_WE_CHAT) && order.getPayChannel().equals(PayConstants.PAY_CHANNEL_WECHAT_MINI)) {
            String shippingSwitch = systemConfigService.getValueByKey(WeChatConstants.CONFIG_WECHAT_ROUTINE_SHIPPING_SWITCH);
            if (StrUtil.isNotBlank(shippingSwitch) && shippingSwitch.equals("1")) {
                asyncService.wechatSendUploadShipping(merchantOrder.getOrderNo());
            }
        }
        return execute;
    }

    /**
     * 电子面单
     * @param request
     * @param merchantOrder
     * @param express
     */
    private ElectrResponse expressDump(OrderSendRequest request, MerchantOrder merchantOrder, Express express) {
        ElectrResponse electrResponse = new ElectrResponse();
        // 发货记录类型，1快递发货、2电子面单
        if(request.getExpressRecordType().equals("1")){ // 根据发货类型判断是否电子面单
            electrResponse.setKuaidinum(request.getExpressNumber());
            electrResponse.setLabel(express.getName());
            electrResponse.setCode(express.getCode());
            return electrResponse;
        }
        MyRecord record = new MyRecord();
        record.set("com", express.getCode());// 快递公司编码
        record.set("to_name", merchantOrder.getRealName());// 收件人
        record.set("to_tel", merchantOrder.getUserPhone());// 收件人电话
        record.set("to_addr", merchantOrder.getUserAddress());// 收件人详细地址
        record.set("from_name", request.getToName());// 寄件人
        record.set("from_tel", request.getToTel());// 寄件人电话
        record.set("from_addr", request.getToAddr());// 寄件人详细地址
        record.set("temp_id", request.getExpressTempId());// 电子面单模板ID
        record.set("siid", "");// 云打印机编号
        record.set("print_type", "IMAGE");// 打印图片
        record.set("count", merchantOrder.getTotalNum());// 商品数量

        //获取购买商品名称
        List<OrderDetail> byOrderNo = orderDetailService.getByOrderNo(merchantOrder.getOrderNo());
        List<String> productNameList = new ArrayList<>();
        for (OrderDetail orderDetail : byOrderNo) {
            productNameList.add(orderDetail.getProductName());
        }

        record.set("cargo", String.join(",", productNameList));// 物品名称
        if (express.getPartnerId()) {
            record.set("partner_id", express.getAccount());// 电子面单月结账号(部分快递公司必选)
        }
        if (express.getPartnerKey()) {
            record.set("partner_key", express.getPassword());// 电子面单密码(部分快递公司必选)
        }
        if (express.getNet()) {
            record.set("net", express.getNetName());// 收件网点名称(部分快递公司必选)
        }

        try {
            MyRecord myRecord = onePassService.expressDump(record);
            logger.info("电子面单的返回数据:{}", JSONObject.toJSONString(myRecord));
            electrResponse.setKuaidinum(myRecord.getStr("kuaidinum"));
            electrResponse.setLabel(myRecord.getStr("label"));
            electrResponse.setCode(myRecord.getStr("code"));
        }catch (Exception e){
            logger.error("打印电子面单失败 - 面单的返回数据:{}", e.getMessage());
            throw new CrmebException("一号通-电子面单失败："+ e.getMessage());
        }

        return electrResponse;
    }

    /**
     * 校验快递发货参数
     *
     * @param request 发货参数
     */
    private void validateOrderSend(OrderSendRequest request) {
        if (request.getDeliveryType().equals(OrderConstants.ORDER_DELIVERY_TYPE_EXPRESS)) {
            if (StrUtil.isBlank(request.getExpressNumber())
                    && StrUtil.isNotBlank(request.getExpressRecordType())
                    && request.getExpressRecordType().equals(OrderConstants.ORDER_DELIVERY_TYPE_EXPRESS_E))
                throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "请填写快递单号");
            if (StrUtil.isBlank(request.getExpressCode()))
                throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "请选择快递公司");
        }
        if (request.getDeliveryType().equals(OrderConstants.ORDER_DELIVERY_TYPE_MERCHANT)) {
            if (StrUtil.isBlank(request.getDeliveryCarrier()))
                throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "请填写配送人员");
            if (StrUtil.isBlank(request.getCarrierPhone()))
                throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "请填写配送人员手机号");
        }
    }

    /**
     * 获取订单总数
     *
     * @param status String 状态
     * @return Integer
     */
    private Integer getCount(OrderTabsHeaderRequest request, String status, Integer merId) {
        Map<String, Object> map = CollUtil.newHashMap();
        map.put("status", status);
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
            map.put("merId", merId);
        } else if (ObjectUtil.isNotNull(request.getMerId()) && request.getMerId() > 0) {
            map.put("merId", request.getMerId());
        }
        if (StrUtil.isNotBlank(request.getOrderNo())) {
            map.put("orderNo", URLUtil.decode(request.getOrderNo()));
        }
        if (ObjectUtil.isNotNull(request.getType())) {
            map.put("type", request.getType());
        }
        return dao.getMerchantAdminPageCount(map);
    }

    /**
     * 获取订单总数(平台端)
     *
     * @param status String 状态
     * @return Integer
     */
    private Integer getPlatCount(OrderTabsHeaderRequest request, String status) {
        Map<String, Object> map = CollUtil.newHashMap();
        map.put("status", status);
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
        if (StrUtil.isNotBlank(request.getOrderNo())) {
            map.put("orderNo", URLUtil.decode(request.getOrderNo()));
        }
        if (ObjectUtil.isNotNull(request.getType())) {
            map.put("type", request.getType());
        }
        return dao.getPlatformAdminPageCount(map);
    }

    /**
     * 获取积分订单总数
     *
     * @param status String 状态
     * @return Integer
     */
    private Integer getIntegralOrderCount(IntegralOrderTabsHeaderRequest request, String status) {
        Map<String, Object> map = CollUtil.newHashMap();
        map.put("status", status);
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
        if (StrUtil.isNotBlank(request.getOrderNo())) {
            map.put("orderNo", URLUtil.decode(request.getOrderNo()));
        }

        return dao.getIntegralOrderCount(map);
    }

    /**
     * 获取订单总数
     *
     * @param dateLimit 时间端
     * @param status    String 状态
     * @return Integer
     */
    private Integer getCount(String dateLimit, String status, Integer merId) {
        //总数只计算时间
        LambdaQueryWrapper<Order> lqw = Wrappers.lambdaQuery();
        if (merId > 0) {
            lqw.eq(Order::getMerId, merId);
            lqw.eq(Order::getIsMerchantDel, false);
        }
        if (StrUtil.isNotBlank(dateLimit)) {
            getRequestTimeWhere(lqw, dateLimit);
        }
        getMerchantStatusWhere(lqw, status);
        return dao.selectCount(lqw);
    }

    /**
     * 根据订单状态获取where条件(商户端)
     *
     * @param lqw    LambdaQueryWrapper<Order> 表达式
     * @param status 订单状态（all 全部； 未支付 unPaid； 未发货 notShipped；待收货 spike；已完成 complete；已退款:refunded；已删除:deleted
     */
    private void getMerchantStatusWhere(LambdaQueryWrapper<Order> lqw, String status) {
        if (StrUtil.isBlank(status)) {
            return;
        }
        switch (status) {
            case OrderConstants.MERCHANT_ORDER_STATUS_ALL: //全部
                break;
            case OrderConstants.MERCHANT_ORDER_STATUS_UNPAID: //未支付
                lqw.eq(Order::getPaid, false);
                lqw.eq(Order::getStatus, OrderConstants.ORDER_STATUS_WAIT_PAY);
                lqw.eq(Order::getCancelStatus, OrderConstants.ORDER_CANCEL_STATUS_NORMAL);
                lqw.eq(Order::getIsUserDel, false);
                break;
            case OrderConstants.MERCHANT_ORDER_STATUS_NOT_SHIPPED: //未发货
                lqw.in(Order::getStatus, OrderConstants.ORDER_STATUS_WAIT_SHIPPING, OrderConstants.ORDER_STATUS_PART_SHIPPING);
                lqw.eq(Order::getCancelStatus, OrderConstants.ORDER_CANCEL_STATUS_NORMAL);
                lqw.ne(Order::getRefundStatus, OrderConstants.ORDER_REFUND_STATUS_REFUND);
                lqw.eq(Order::getIsUserDel, false);
                lqw.in(Order::getGroupBuyRecordStatus, 10, 99);
                break;
            case OrderConstants.MERCHANT_ORDER_STATUS_SPIKE: //待收货
                lqw.eq(Order::getStatus, OrderConstants.ORDER_STATUS_WAIT_RECEIPT);
                lqw.eq(Order::getCancelStatus, OrderConstants.ORDER_CANCEL_STATUS_NORMAL);
                lqw.ne(Order::getRefundStatus, OrderConstants.ORDER_REFUND_STATUS_REFUND);
                lqw.eq(Order::getIsUserDel, false);
                break;
            case OrderConstants.MERCHANT_ORDER_STATUS_RECEIVING: //已收货
                lqw.eq(Order::getStatus, OrderConstants.ORDER_STATUS_TAKE_DELIVERY);
                lqw.eq(Order::getCancelStatus, OrderConstants.ORDER_CANCEL_STATUS_NORMAL);
                lqw.ne(Order::getRefundStatus, OrderConstants.ORDER_REFUND_STATUS_REFUND);
                lqw.eq(Order::getIsUserDel, false);
                break;
            case OrderConstants.MERCHANT_ORDER_STATUS_COMPLETE: //交易完成
                lqw.eq(Order::getStatus, OrderConstants.ORDER_STATUS_COMPLETE);
                lqw.eq(Order::getCancelStatus, OrderConstants.ORDER_CANCEL_STATUS_NORMAL);
                lqw.ne(Order::getRefundStatus, OrderConstants.ORDER_REFUND_STATUS_REFUND);
                lqw.eq(Order::getIsUserDel, false);
                break;
            case OrderConstants.MERCHANT_ORDER_STATUS_AWAIT_VERIFICATION: //待核销
                lqw.eq(Order::getStatus, OrderConstants.ORDER_STATUS_AWAIT_VERIFICATION);
                lqw.eq(Order::getCancelStatus, OrderConstants.ORDER_CANCEL_STATUS_NORMAL);
                lqw.ne(Order::getRefundStatus, OrderConstants.ORDER_REFUND_STATUS_REFUND);
                lqw.eq(Order::getIsUserDel, false);
                lqw.in(Order::getGroupBuyRecordStatus, 10, 99);
                break;
            case OrderConstants.MERCHANT_ORDER_STATUS_REFUNDED: //已退款
                lqw.eq(Order::getPaid, true);
                lqw.eq(Order::getCancelStatus, OrderConstants.ORDER_CANCEL_STATUS_NORMAL);
                lqw.eq(Order::getRefundStatus, OrderConstants.ORDER_REFUND_STATUS_REFUND);
                lqw.eq(Order::getIsUserDel, false);
                break;
            case OrderConstants.MERCHANT_ORDER_STATUS_DELETED: //已删除
                lqw.eq(Order::getIsUserDel, true);
                break;
            case OrderConstants.MERCHANT_ORDER_STATUS_CANCEL: //已取消
                lqw.eq(Order::getPaid, false);
                lqw.eq(Order::getStatus, OrderConstants.ORDER_STATUS_CANCEL);
                lqw.in(Order::getCancelStatus, OrderConstants.ORDER_CANCEL_STATUS_SYSTEM, OrderConstants.ORDER_CANCEL_STATUS_USER);
                lqw.eq(Order::getIsUserDel, false);
                break;
        }
        lqw.eq(Order::getIsDel, false);
    }

    /**
     * 获取request的where条件
     *
     * @param lqw       LambdaQueryWrapper<Order> 表达式
     * @param dateLimit 时间区间参数
     */
    private void getRequestTimeWhere(LambdaQueryWrapper<Order> lqw, String dateLimit) {
        DateLimitUtilVo dateLimitUtilVo = CrmebDateUtil.getDateLimit(dateLimit);
        lqw.between(Order::getCreateTime, dateLimitUtilVo.getStartTime(), dateLimitUtilVo.getEndTime());
    }


    /**
     * 发送消息通知
     * 根据用户类型发送
     * 公众号模板消息
     * 小程序订阅消息
     */
    private void pushMessageOrder(Order order, SystemNotification notification, OrderInvoice invoice) {
        if (!order.getPayType().equals(PayConstants.PAY_TYPE_WE_CHAT)) {
            return;
        }
        if (order.getPayChannel().equals(PayConstants.PAY_CHANNEL_H5)) {
            return;
        }
        UserToken userToken;
        HashMap<String, String> temMap = new HashMap<>();
        // 公众号
        if (order.getPayChannel().equals(PayConstants.PAY_CHANNEL_WECHAT_PUBLIC) && notification.getIsWechat().equals(1)) {
            userToken = userTokenService.getTokenByUserId(order.getUid(), UserConstants.USER_TOKEN_TYPE_WECHAT);
            if (ObjectUtil.isNull(userToken)) {
                return;
            }
            /**
             * {{first.DATA}}
             * 订单编号：{{keyword1.DATA}}
             * 物流公司：{{keyword2.DATA}}
             * 物流单号：{{keyword3.DATA}}
             * {{remark.DATA}}
             */
            // 发送微信模板消息
            temMap.put(Constants.WE_CHAT_TEMP_KEY_FIRST, "订单发货提醒");
            temMap.put("keyword1", order.getOrderNo());
            temMap.put("keyword2", invoice.getExpressName());
            temMap.put("keyword3", invoice.getExpressCode());
            temMap.put(Constants.WE_CHAT_TEMP_KEY_END, "欢迎再次购买！");
            templateMessageService.pushTemplateMessage(notification.getWechatId(), temMap, userToken.getToken());
            return;
        }
        if (order.getPayChannel().equals(PayConstants.PAY_CHANNEL_WECHAT_MINI) && notification.getIsRoutine().equals(1)) {
            // 小程序发送订阅消息
            userToken = userTokenService.getTokenByUserId(order.getUid(), UserConstants.USER_TOKEN_TYPE_ROUTINE);
            if (ObjectUtil.isNull(userToken)) {
                return;
            }
            // 组装数据
            // 注释部分为丰享汇小程序
//        temMap.put("character_string1", storeOrder.getOrderId());
//        temMap.put("name3", storeOrder.getDeliveryName());
//        temMap.put("character_string4", storeOrder.getDeliveryId());
//        temMap.put("thing7", "您的订单已发货");
            // 放开部分为二码秦川小程序
            temMap.put("character_string7", order.getOrderNo());
            temMap.put("thing1", invoice.getExpressName());
            temMap.put("character_string2", invoice.getExpressCode());
            temMap.put("thing11", "您的订单已发货");
            templateMessageService.pushMiniTemplateMessage(notification.getRoutineId(), temMap, userToken.getToken());
        }
    }

}

