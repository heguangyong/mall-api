package com.zbkj.service.service.impl;


import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.zbkj.common.config.CrmebConfig;
import com.zbkj.common.constants.*;
import com.zbkj.common.exception.CrmebException;
import com.zbkj.common.model.bill.Bill;
import com.zbkj.common.model.bill.MerchantBill;
import com.zbkj.common.model.cdkey.CardSecret;
import com.zbkj.common.model.groupbuy.GroupBuyActivitySku;
import com.zbkj.common.model.groupbuy.GroupBuyUser;
import com.zbkj.common.model.merchant.Merchant;
import com.zbkj.common.model.merchant.MerchantBalanceRecord;
import com.zbkj.common.model.order.*;
import com.zbkj.common.model.product.Product;
import com.zbkj.common.model.product.ProductAttrValue;
import com.zbkj.common.model.seckill.SeckillProduct;
import com.zbkj.common.model.system.SystemNotification;
import com.zbkj.common.model.user.User;
import com.zbkj.common.model.user.UserBrokerageRecord;
import com.zbkj.common.model.user.UserIntegralRecord;
import com.zbkj.common.model.user.UserToken;
import com.zbkj.common.utils.CrmebDateUtil;
import com.zbkj.common.utils.CrmebUtil;
import com.zbkj.common.utils.RedisUtil;
import com.zbkj.common.vo.MyRecord;
import com.zbkj.service.service.*;
import com.zbkj.service.service.groupbuy.GroupBuyActivitySkuService;
import com.zbkj.service.service.groupbuy.GroupBuyUserService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

/**
 * StoreOrderServiceImpl 接口实现
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
public class OrderTaskServiceImpl implements OrderTaskService {
    //日志
    private static final Logger logger = LoggerFactory.getLogger(OrderTaskServiceImpl.class);

    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private OrderService orderService;
    @Autowired
    private OrderStatusService orderStatusService;
    @Autowired
    private MerchantOrderService merchantOrderService;
    @Autowired
    private OrderDetailService orderDetailService;
    @Autowired
    private UserService userService;
    @Autowired
    private TransactionTemplate transactionTemplate;
    @Autowired
    private SystemConfigService systemConfigService;
    @Autowired
    private PayService payService;
    @Autowired
    private RefundOrderService refundOrderService;
    @Autowired
    private RefundOrderInfoService refundOrderInfoService;
    @Autowired
    private UserIntegralRecordService userIntegralRecordService;
    @Autowired
    private UserBrokerageRecordService userBrokerageRecordService;
    @Autowired
    private MerchantService merchantService;
    @Autowired
    private ProductService productService;
    @Autowired
    private ProductAttrValueService productAttrValueService;
    @Autowired
    private OrderProfitSharingService orderProfitSharingService;
    @Autowired
    private MerchantBillService merchantBillService;
    @Autowired
    private BillService billService;
    @Autowired
    private CouponUserService couponUserService;
    @Autowired
    private SystemNotificationService systemNotificationService;
    @Autowired
    private UserTokenService userTokenService;
    @Autowired
    private TemplateMessageService templateMessageService;
    @Autowired
    private SeckillProductService seckillProductService;
    @Autowired
    private MerchantBalanceRecordService merchantBalanceRecordService;
    @Autowired
    private AsyncService asyncService;
    @Autowired
    private CrmebConfig crmebConfig;
    @Autowired
    private RefundOrderStatusService refundOrderStatusService;
    @Autowired
    private CardSecretService cardSecretService;
    @Autowired
    private CdkeyLibraryService cdkeyLibraryService;
    @Autowired
    private GroupBuyActivitySkuService groupBuyActivitySkuService;
    @Autowired
    private GroupBuyUserService groupBuyUserService;


    /**
     * 用户取消订单
     */
    @Override
    public void cancelByUser() {
        String redisKey = TaskConstants.ORDER_TASK_REDIS_KEY_AFTER_CANCEL_BY_USER;
        Long size = redisUtil.getListSize(redisKey);
        logger.info("OrderTaskServiceImpl.cancelByUser | size:" + size);
        if (size < 1) {
            return;
        }
        for (int i = 0; i < size; i++) {
            //如果10秒钟拿不到一个数据，那么退出循环
            Object data = redisUtil.getRightPop(redisKey, 10L);
            if (ObjectUtil.isNull(data)) {
                continue;
            }
            try {
                boolean result = userCancelOrder(String.valueOf(data));
                if (!result) {
                    redisUtil.lPush(redisKey, data);
                }
            } catch (Exception e) {
                redisUtil.lPush(redisKey, data);
            }
        }
    }

    /**
     * 用户取消订单
     *
     * @param orderNo 订单号
     */
    private Boolean userCancelOrder(String orderNo) {
        Order order = orderService.getByOrderNo(orderNo);
        if (ObjectUtil.isNull(order)) {
            logger.error("用户取消支付订单，订单不存在，订单号为:{}", orderNo);
            return Boolean.TRUE;
        }
        if (order.getPaid()) {
            return Boolean.TRUE;
        }
        if (!order.getStatus().equals(OrderConstants.ORDER_STATUS_CANCEL)) {
            logger.error("用户取消支付订单，订单状态该异常，订单号为:{}", orderNo);
            return Boolean.TRUE;
        }
        if (order.getCancelStatus().equals(OrderConstants.ORDER_CANCEL_STATUS_NORMAL)) {
            logger.error("用户取消支付订单，订单状态取消异常，订单号为:{}", orderNo);
            return Boolean.FALSE;
        }
        if (order.getCancelStatus().equals(OrderConstants.ORDER_CANCEL_STATUS_SYSTEM)) {
            return Boolean.TRUE;
        }
        return commonOrderCancel(order, "user");
    }

    /**
     * 用户积分记录——订单取消
     *
     * @param uid         用户ID
     * @param useIntegral 使用的积分
     * @param integral    用户当前积分
     * @param orderNo     订单号
     * @return 用户积分记录
     */
    private UserIntegralRecord initOrderCancelIntegralRecord(Integer uid, Integer useIntegral, Integer integral, String orderNo) {
        UserIntegralRecord integralRecord = new UserIntegralRecord();
        integralRecord.setUid(uid);
        integralRecord.setLinkId(orderNo);
        integralRecord.setLinkType(IntegralRecordConstants.INTEGRAL_RECORD_LINK_TYPE_ORDER);
        integralRecord.setType(IntegralRecordConstants.INTEGRAL_RECORD_TYPE_ADD);
        integralRecord.setTitle(StrUtil.format("订单取消，退回金额抵扣积分：{}", useIntegral));
        integralRecord.setIntegral(useIntegral);
        integralRecord.setBalance(integral + useIntegral);
        integralRecord.setStatus(IntegralRecordConstants.INTEGRAL_RECORD_STATUS_COMPLETE);
        return integralRecord;
    }

    /**
     * 用户积分记录——订单取消（积分订单）
     *
     * @param uid         用户ID
     * @param useIntegral 使用的积分
     * @param integral    用户当前积分
     * @param orderNo     订单号
     * @return 用户积分记录
     */
    private UserIntegralRecord initIntegralOrderCancelIntegralRecord(Integer uid, Integer useIntegral, Integer integral, String orderNo) {
        UserIntegralRecord integralRecord = new UserIntegralRecord();
        integralRecord.setUid(uid);
        integralRecord.setLinkId(orderNo);
        integralRecord.setLinkType(IntegralRecordConstants.INTEGRAL_RECORD_LINK_TYPE_ORDER);
        integralRecord.setType(IntegralRecordConstants.INTEGRAL_RECORD_TYPE_ADD);
        integralRecord.setTitle(StrUtil.format("积分订单取消，退回兑换积分：{}", useIntegral));
        integralRecord.setIntegral(useIntegral);
        integralRecord.setBalance(integral + useIntegral);
        integralRecord.setStatus(IntegralRecordConstants.INTEGRAL_RECORD_STATUS_COMPLETE);
        return integralRecord;
    }

    /**
     * 订单退款
     */
    @Override
    public void orderRefund() {
        String redisKey = TaskConstants.ORDER_TASK_REDIS_KEY_AFTER_REFUND_BY_USER;
        Long size = redisUtil.getListSize(redisKey);
        logger.info("OrderTaskServiceImpl.orderRefund | size:" + size);
        if (size < 1) {
            return;
        }
        for (int i = 0; i < size; i++) {
            //如果10秒钟拿不到一个数据，那么退出循环
            Object orderNoData = redisUtil.getRightPop(redisKey, 10L);
            if (ObjectUtil.isNull(orderNoData)) {
                continue;
            }
            try {
                Boolean result = refundAfterProcessing(orderNoData.toString());
                if (!result) {
                    redisUtil.lPush(redisKey, orderNoData);
                }
            } catch (Exception e) {
                logger.error("订单退款错误：" + e.getMessage());
                redisUtil.lPush(redisKey, orderNoData);
            }
        }
    }

    /**
     * 订单退款后置处理
     *
     * @param refundOrderNo 退款订单号
     */
    private Boolean refundAfterProcessing(String refundOrderNo) {
        RefundOrder refundOrder = refundOrderService.getInfoException(refundOrderNo);
        if (!refundOrder.getRefundStatus().equals(OrderConstants.MERCHANT_REFUND_ORDER_STATUS_REFUND)) {
            throw new CrmebException("退款单状态异常");
        }
        Order order = orderService.getByOrderNo(refundOrder.getOrderNo());
        String orderNo = order.getOrderNo();
        RefundOrderInfo refundOrderInfo = refundOrderInfoService.getByRefundOrderNo(refundOrderNo);
        List<UserIntegralRecord> integralRecordAddList = CollUtil.newArrayList();
        List<UserIntegralRecord> integralRecordUpdateList = CollUtil.newArrayList();
        // 退使用积分
        if (refundOrder.getRefundUseIntegral() > 0) {
            UserIntegralRecord userIntegralRecord = refundUseIntegralRecordInit(refundOrder, refundOrderInfo);
            integralRecordAddList.add(userIntegralRecord);
        }
        // 退赠送积分
        if (refundOrder.getRefundGainIntegral() > 0) {
            // 获取此订单支付时的商户订单积分记录
            UserIntegralRecord gainIntegralRecord = userIntegralRecordService.getByOrderNoAndType(orderNo, IntegralRecordConstants.INTEGRAL_RECORD_TYPE_ADD);
            if (gainIntegralRecord.getStatus() < IntegralRecordConstants.INTEGRAL_RECORD_STATUS_COMPLETE) {
                if (gainIntegralRecord.getIntegral().equals(refundOrder.getRefundGainIntegral())) {
                    gainIntegralRecord.setStatus(IntegralRecordConstants.INTEGRAL_RECORD_STATUS_INVALIDATION);
                    gainIntegralRecord.setUpdateTime(DateUtil.date());
                } else {
                    gainIntegralRecord.setIntegral(gainIntegralRecord.getIntegral() - refundOrder.getRefundGainIntegral());
                    gainIntegralRecord.setUpdateTime(DateUtil.date());
                }
                integralRecordUpdateList.add(gainIntegralRecord);
            }
        }
        // 分佣返还
        List<UserBrokerageRecord> brokerageRecordList = CollUtil.newArrayList();
        // 获取对应分佣记录
        List<UserBrokerageRecord> userBrokerageRecordList = userBrokerageRecordService.getByOrderNo(orderNo);
        if (CollUtil.isNotEmpty(userBrokerageRecordList) && userBrokerageRecordList.get(0).getStatus() < BrokerageRecordConstants.BROKERAGE_RECORD_STATUS_COMPLETE) {
            userBrokerageRecordList.forEach(r -> {
                if (r.getBrokerageLevel().equals(1)) {
                    if (r.getPrice().compareTo(refundOrderInfo.getRefundFirstBrokerageFee()) == 0) {
                        r.setStatus(BrokerageRecordConstants.BROKERAGE_RECORD_STATUS_INVALIDATION);
                    } else {
                        r.setPrice(r.getPrice().subtract(refundOrderInfo.getRefundFirstBrokerageFee()));
                    }
                }
                if (r.getBrokerageLevel().equals(2)) {
                    if (r.getPrice().compareTo(refundOrderInfo.getRefundSecondBrokerageFee()) == 0) {
                        r.setStatus(BrokerageRecordConstants.BROKERAGE_RECORD_STATUS_INVALIDATION);
                    } else {
                        r.setPrice(r.getPrice().subtract(refundOrderInfo.getRefundSecondBrokerageFee()));
                    }
                }
                brokerageRecordList.add(r);
            });
        }
        OrderProfitSharing orderProfitSharing = orderProfitSharingService.getByOrderNo(orderNo);
        BigDecimal merRefundPayPrice; // 商户退款金额
        BigDecimal platRefundPayPrice; // 平台退款金额

        if (orderProfitSharing.getOrderPrice().compareTo(refundOrder.getRefundPrice()) == 0) {
            merRefundPayPrice = orderProfitSharing.getProfitSharingMerPrice();
            platRefundPayPrice = orderProfitSharing.getProfitSharingPlatPrice();
        } else if ((orderProfitSharing.getOrderPrice().subtract(orderProfitSharing.getProfitSharingRefund())).compareTo(refundOrder.getRefundPrice()) == 0) {
            merRefundPayPrice = orderProfitSharing.getProfitSharingMerPrice().subtract(orderProfitSharing.getRefundProfitSharingMerPrice());
            platRefundPayPrice = orderProfitSharing.getProfitSharingPlatPrice().subtract(orderProfitSharing.getRefundProfitSharingPlatPrice());
        } else {
            BigDecimal payReRatio = refundOrder.getRefundPrice().divide(orderProfitSharing.getOrderPrice(), 10, RoundingMode.HALF_UP);
            merRefundPayPrice = orderProfitSharing.getProfitSharingMerPrice().multiply(payReRatio).setScale(2, RoundingMode.HALF_UP);
            if ((merRefundPayPrice.add(orderProfitSharing.getRefundProfitSharingMerPrice())).compareTo(orderProfitSharing.getProfitSharingMerPrice()) > 0) {
                merRefundPayPrice = orderProfitSharing.getProfitSharingMerPrice().subtract(orderProfitSharing.getRefundProfitSharingMerPrice());
            }
            platRefundPayPrice = orderProfitSharing.getProfitSharingPlatPrice().multiply(payReRatio).setScale(2, RoundingMode.HALF_UP);
            if ((platRefundPayPrice.add(orderProfitSharing.getRefundProfitSharingPlatPrice())).compareTo(orderProfitSharing.getProfitSharingPlatPrice()) > 0) {
                platRefundPayPrice = orderProfitSharing.getProfitSharingPlatPrice().subtract(orderProfitSharing.getRefundProfitSharingPlatPrice());
            }
        }
        orderProfitSharing.setRefundProfitSharingMerPrice(orderProfitSharing.getRefundProfitSharingMerPrice().add(merRefundPayPrice));
        orderProfitSharing.setRefundProfitSharingPlatPrice(orderProfitSharing.getRefundProfitSharingPlatPrice().add(platRefundPayPrice));
        if (refundOrder.getRefundPlatCouponPrice().compareTo(BigDecimal.ZERO) > 0) {
            orderProfitSharing.setRefundPlatCouponPrice(orderProfitSharing.getRefundPlatCouponPrice().add(refundOrder.getRefundPlatCouponPrice()));
        }
        if (refundOrder.getRefundFirstBrokerageFee().compareTo(BigDecimal.ZERO) > 0) {
            orderProfitSharing.setRefundFirstBrokerageFee(orderProfitSharing.getRefundFirstBrokerageFee().add(refundOrder.getRefundFirstBrokerageFee()));
        }
        if (refundOrder.getRefundSecondBrokerageFee().compareTo(BigDecimal.ZERO) > 0) {
            orderProfitSharing.setRefundSecondBrokerageFee(orderProfitSharing.getRefundSecondBrokerageFee().add(refundOrder.getRefundSecondBrokerageFee()));
        }

        // 退使用积分金额
        if (refundOrderInfo.getRefundUseIntegral() > 0) {
            orderProfitSharing.setRefundUseIntegral(orderProfitSharing.getRefundUseIntegral() + refundOrderInfo.getRefundUseIntegral());
            orderProfitSharing.setRefundIntegralPrice(orderProfitSharing.getRefundIntegralPrice().add(refundOrderInfo.getRefundIntegralPrice()));
        }

        refundOrder.setMerchantRefundPrice(merRefundPayPrice);
        refundOrder.setPlatformRefundPrice(platRefundPayPrice);
        if (CollUtil.isEmpty(brokerageRecordList)) {
            refundOrder.setIsReplace(true);
        }
        MerchantBill merchantBill = initMerchantBillRefund(refundOrder);

        // 平台分账返还
        Bill platBill = new Bill();
        platBill.setOrderNo(refundOrder.getRefundOrderNo());
        platBill.setMerId(refundOrder.getMerId());
        platBill.setPm(BillConstants.BILL_PM_SUB);
        platBill.setAmount(platRefundPayPrice);
        platBill.setType(BillConstants.BILL_TYPE_REFUND_ORDER);
        platBill.setMark(StrUtil.format("订单退款，平台返还分账金额{}元", platRefundPayPrice));

        orderProfitSharing.setProfitSharingRefund(orderProfitSharing.getProfitSharingRefund().add(refundOrder.getRefundPrice()));
        orderProfitSharing.setRefundFreightFee(orderProfitSharing.getRefundFreightFee().add(refundOrder.getRefundFreightFee()));

        merRefundPayPrice = merRefundPayPrice.add(refundOrder.getRefundFreightFee());
        boolean isMerchantRecordUpdate;
        MerchantBalanceRecord merchantBalanceRecord = merchantBalanceRecordService.getByLinkNo(refundOrder.getOrderNo());
        if (merchantBalanceRecord.getStatus().equals(1) || merchantBalanceRecord.getStatus().equals(2)) {
            BigDecimal subtract = merchantBalanceRecord.getAmount().subtract(merRefundPayPrice);
            if (subtract.compareTo(BigDecimal.ZERO) <= 0) {
                merchantBalanceRecord.setAmount(BigDecimal.ZERO);
                merchantBalanceRecord.setStatus(4);
            } else {
                merchantBalanceRecord.setAmount(merchantBalanceRecord.getAmount().subtract(merRefundPayPrice));
            }
            isMerchantRecordUpdate = true;
        } else {
            isMerchantRecordUpdate = false;
        }

        // 退优惠券
        Integer platCouponId = 0;
        Integer merCouponId = 0;
        OrderDetail orderDetail = orderDetailService.getById(refundOrderInfo.getOrderDetailId());
        if (orderDetail.getMerCouponPrice().compareTo(BigDecimal.ZERO) > 0 && orderDetail.getPayNum().equals(orderDetail.getRefundNum())) {
            merCouponId = getRefundMerCouponId(refundOrder.getOrderNo());
        }
        if (orderDetail.getPlatCouponPrice().compareTo(BigDecimal.ZERO) > 0 && orderDetail.getPayNum().equals(orderDetail.getRefundNum())) {
            platCouponId = getRefundPlatCouponId(order.getPlatOrderNo());
        }
        List<Integer> couponIdList = new ArrayList<>();
        if (merCouponId > 0) {
            couponIdList.add(merCouponId);
        }
        if (platCouponId > 0) {
            couponIdList.add(platCouponId);
        }
        logger.error(StrUtil.format("售后单后置任务中，商户优惠券ID={}，平台优惠券ID={}", merCouponId, platCouponId));
        return transactionTemplate.execute(e -> {
            // 回滚库存
            refundRollbackStock(refundOrderInfo, order.getType(), order.getSecondType());
            // 扣商户资金
            if (isMerchantRecordUpdate) {
                merchantBalanceRecordService.updateById(merchantBalanceRecord);
            } else {
                merchantService.operationBalance(refundOrder.getMerId(), refundOrder.getMerchantRefundPrice().add(refundOrder.getRefundFreightFee()), Constants.OPERATION_TYPE_SUBTRACT);
            }
            merchantBillService.save(merchantBill);
            // 更新退款单
            refundOrderService.updateById(refundOrder);

            if (CollUtil.isNotEmpty(integralRecordAddList)) {
                userIntegralRecordService.saveBatch(integralRecordAddList);
            }
            if (CollUtil.isNotEmpty(integralRecordUpdateList)) {
                userIntegralRecordService.updateBatchById(integralRecordUpdateList);
            }
            if (CollUtil.isNotEmpty(brokerageRecordList)) {
                userBrokerageRecordService.updateBatchById(brokerageRecordList);
            } else {
                // 平台代扣记录
                BigDecimal platReplacePrice = refundOrderInfo.getRefundFirstBrokerageFee().add(refundOrderInfo.getRefundSecondBrokerageFee());
                Bill bill = new Bill();
                bill.setOrderNo(refundOrder.getRefundOrderNo());
                bill.setMerId(refundOrder.getMerId());
                bill.setPm(BillConstants.BILL_PM_SUB);
                bill.setAmount(platReplacePrice);
                bill.setType(BillConstants.BILL_TYPE_REFUND_ORDER);
                bill.setMark(StrUtil.format("订单退款，平台代扣佣金应退金额{}元", platReplacePrice));
                billService.save(bill);
            }
            if (refundOrderInfo.getRefundUseIntegral() > 0) {
                userService.updateIntegral(refundOrder.getUid(), refundOrderInfo.getRefundUseIntegral(), Constants.OPERATION_TYPE_ADD);
                Bill bill = new Bill();
                bill.setOrderNo(refundOrder.getRefundOrderNo());
                bill.setMerId(refundOrder.getMerId());
                bill.setPm(BillConstants.BILL_PM_ADD);
                bill.setAmount(refundOrderInfo.getRefundIntegralPrice());
                bill.setType(BillConstants.BILL_TYPE_REFUND_ORDER);
                bill.setMark(StrUtil.format("订单退款，平台代扣积分抵扣金额返还{}元", refundOrderInfo.getRefundIntegralPrice()));
                billService.save(bill);
            }
            billService.save(platBill);

            orderProfitSharingService.updateById(orderProfitSharing);

            if (CollUtil.isNotEmpty(couponIdList)) {
                couponUserService.rollbackByIds(couponIdList);
            }
            refundOrderStatusService.add(refundOrder.getRefundOrderNo(), RefundOrderConstants.REFUND_ORDER_LOG_REFUND, "售后单退款成功");
            return Boolean.TRUE;
        });
    }

    /**
     * 获取退款平台优惠券ID
     *
     * @param platOrderNo 主订单号/平台订单号
     * @return 0-平台优惠券不满足退款，>0 - 平台优惠券ID
     */
    private Integer getRefundPlatCouponId(String platOrderNo) {
        List<Order> orderList = orderService.getByPlatOrderNo(platOrderNo);
        List<OrderDetail> orderDetailList = new ArrayList<>();
        orderList.forEach(o -> {
            orderDetailList.addAll(orderDetailService.getByOrderNo(o.getOrderNo()));
        });
        boolean isRefundCoupon = true;
        for (OrderDetail od : orderDetailList) {
            if (od.getPlatCouponPrice().compareTo(BigDecimal.ZERO) <= 0) {
                continue;
            }
            if (!od.getPayNum().equals(od.getRefundNum())) {
                isRefundCoupon = false;
                break;
            }
        }
        return isRefundCoupon ? orderList.get(0).getPlatCouponId() : 0;
    }

    /**
     * 获取退款商户优惠券ID
     *
     * @param orderNo 订单号
     * @return 0-商户优惠券不满足退款，>0 - 商户优惠券ID
     */
    private Integer getRefundMerCouponId(String orderNo) {
        List<OrderDetail> orderDetailList = orderDetailService.getByOrderNo(orderNo);
        boolean isRefund = true;
        for (OrderDetail orderDetail : orderDetailList) {
            if (orderDetail.getMerCouponPrice().compareTo(BigDecimal.ZERO) <= 0) {
                continue;
            }
            if (orderDetail.getPayNum() > orderDetail.getRefundNum()) {
                isRefund = false;
                break;
            }
        }
        if (!isRefund) {
            return 0;
        }
        MerchantOrder merchantOrder = merchantOrderService.getOneByOrderNo(orderNo);
        return merchantOrder.getCouponId();
    }

    /**
     * 初始化商户帐单-退款
     *
     * @param refundOrder 退款单
     */
    private MerchantBill initMerchantBillRefund(RefundOrder refundOrder) {
        MerchantBill merchantBill = new MerchantBill();
        merchantBill.setMerId(refundOrder.getMerId());
        merchantBill.setType(BillConstants.BILL_TYPE_REFUND_ORDER);
        merchantBill.setOrderNo(refundOrder.getRefundOrderNo());
        merchantBill.setUid(refundOrder.getUid());
        merchantBill.setPm(BillConstants.BILL_PM_SUB);
        merchantBill.setAmount(refundOrder.getMerchantRefundPrice().add(refundOrder.getRefundFreightFee()));
        merchantBill.setMark(StrUtil.format("订单{}退款{}元，商户支出{}元,退运费{}元", refundOrder.getOrderNo(), refundOrder.getRefundPrice(), refundOrder.getMerchantRefundPrice(), refundOrder.getRefundFreightFee()));
        return merchantBill;
    }

    /**
     * 退款回滚库存
     *
     * @param refundOrderInfo 退款单详情
     * @param orderType       订单类型
     * @param orderSecondType 订单二级类型
     */
    private void refundRollbackStock(RefundOrderInfo refundOrderInfo, Integer orderType, Integer orderSecondType) {
        if (orderType.equals(OrderConstants.ORDER_TYPE_SECKILL)) {
            SeckillProduct seckillProduct = seckillProductService.getById(refundOrderInfo.getProductId());
            seckillProductService.operationStock(seckillProduct.getId(), refundOrderInfo.getApplyRefundNum(), Constants.OPERATION_TYPE_ADD);
            ProductAttrValue seckillAttrValue = productAttrValueService.getById(refundOrderInfo.getAttrValueId());
            productAttrValueService.operationStock(seckillAttrValue.getId(), refundOrderInfo.getApplyRefundNum(),
                    Constants.OPERATION_TYPE_ADD, seckillAttrValue.getType(), seckillAttrValue.getMarketingType(), seckillAttrValue.getVersion());
            Product product = productService.getById(seckillProduct.getProductId());
            productService.operationStock(product.getId(), refundOrderInfo.getApplyRefundNum(), Constants.OPERATION_TYPE_ADD);
            ProductAttrValue productAttrValue = productAttrValueService.getById(seckillAttrValue.getMasterId());
            productAttrValueService.operationStock(productAttrValue.getId(), refundOrderInfo.getApplyRefundNum(),
                    Constants.OPERATION_TYPE_ADD, productAttrValue.getType(), productAttrValue.getMarketingType(),
                    productAttrValue.getVersion());
            return;
        }
        if (orderType.equals(OrderConstants.ORDER_TYPE_PITUAN)) {
            RefundOrder byRefundOrderNo = refundOrderService.getByRefundOrderNo(refundOrderInfo.getRefundOrderNo());
            Order order = orderService.getByOrderNo(byRefundOrderNo.getOrderNo());
            GroupBuyUser currentBuyUser = groupBuyUserService.getByOrderNo(order.getOrderNo());
            GroupBuyActivitySku groupBuyActivitySku = groupBuyActivitySkuService.getByAttrId(currentBuyUser.getGroupActivityId(),refundOrderInfo.getAttrValueId());
            groupBuyActivitySkuService.rollBackSKUStock(order,groupBuyActivitySku.getGroupActivityId(), refundOrderInfo.getAttrValueId(), refundOrderInfo.getApplyRefundNum());
        }
        if (orderSecondType.equals(OrderConstants.ORDER_SECOND_TYPE_CDKEY)) {
            OrderDetail orderDetail = orderDetailService.getById(refundOrderInfo.getOrderDetailId());
            List<Integer> csIdList = CrmebUtil.stringToArray(orderDetail.getCardSecretIds());
            List<CardSecret> cardSecretList = new ArrayList<>();
            for (int i = 0; i < refundOrderInfo.getApplyRefundNum(); ) {
                if (CollUtil.isEmpty(csIdList)) {
                    break;
                }
                Integer cdId = csIdList.get(i);
                CardSecret cardSecret = cardSecretService.getById(cdId);
                cardSecret.setIsUse(false);
                cardSecretList.add(cardSecret);
                csIdList.remove(i);
            }
            if (CollUtil.isNotEmpty(csIdList)) {
                orderDetail.setCardSecretIds(StrUtil.join(",", csIdList));
                orderDetailService.updateById(orderDetail);
            }
            Integer libraryId = cardSecretList.get(0).getLibraryId();
            cardSecretService.updateBatchById(cardSecretList);
            cdkeyLibraryService.operationUseNum(libraryId, cardSecretList.size(), Constants.OPERATION_TYPE_SUBTRACT);
            productService.operationStock(refundOrderInfo.getProductId(), refundOrderInfo.getApplyRefundNum(), Constants.OPERATION_TYPE_ADD);
        } else {
            productService.operationStock(refundOrderInfo.getProductId(), refundOrderInfo.getApplyRefundNum(), Constants.OPERATION_TYPE_ADD);
        }
        ProductAttrValue attrValue = productAttrValueService.getById(refundOrderInfo.getAttrValueId());
        productAttrValueService.operationStock(attrValue.getId(), refundOrderInfo.getApplyRefundNum(),
                Constants.OPERATION_TYPE_ADD, attrValue.getType(), attrValue.getMarketingType(), attrValue.getVersion());
    }

    /**
     * 初始化退积分抵扣部分积分记录
     *
     * @param refundOrder     退款单
     * @param refundOrderInfo 退款单详情
     */
    private UserIntegralRecord refundUseIntegralRecordInit(RefundOrder refundOrder, RefundOrderInfo refundOrderInfo) {
        UserIntegralRecord record = new UserIntegralRecord();
        record.setUid(refundOrder.getUid());
        record.setLinkId(refundOrder.getRefundOrderNo());
        record.setLinkType(IntegralRecordConstants.INTEGRAL_RECORD_LINK_TYPE_ORDER_REFUND);
        record.setType(IntegralRecordConstants.INTEGRAL_RECORD_TYPE_ADD);
        record.setTitle(IntegralRecordConstants.INTEGRAL_RECORD_TITLE_REFUND);
        record.setIntegral(refundOrderInfo.getRefundUseIntegral());
        record.setMark(StrUtil.format("订单退款，返还支付使用的{}积分", record.getIntegral()));
        record.setStatus(IntegralRecordConstants.INTEGRAL_RECORD_STATUS_COMPLETE);
        return record;
    }

    /**
     * 订单支付成功后置处理
     */
    @Override
    public void orderPaySuccessAfter() {
        String redisKey = TaskConstants.ORDER_TASK_PAY_SUCCESS_AFTER;
        Long size = redisUtil.getListSize(redisKey);
        logger.info("OrderTaskServiceImpl.orderPaySuccessAfter | size:" + size);
        if (size < 1) {
            return;
        }
        for (int i = 0; i < size; i++) {
            //如果10秒钟拿不到一个数据，那么退出循环
            Object data = redisUtil.getRightPop(redisKey, 10L);
            if (ObjectUtil.isNull(data)) {
                continue;
            }
            try {
                Boolean result = payService.payAfterProcessingTemp(String.valueOf(data));
                if (!result) {
                    logger.error("订单支付成功后置处理失败，订单号：{}", String.valueOf(data));
                    redisUtil.lPush(redisKey, data);
                }
            } catch (Exception e) {
                logger.error("order pay task error exception : {}", e.getMessage());
                redisUtil.lPush(redisKey, data);
            }
        }
    }

    /**
     * 自动取消未支付订单
     */
    @Override
    public void autoCancel() {
        String redisKey = TaskConstants.ORDER_TASK_REDIS_KEY_AUTO_CANCEL_KEY;
        Long size = redisUtil.getListSize(redisKey);
        logger.info("OrderTaskServiceImpl.autoCancel | size:" + size);
        if (size < 1) {
            return;
        }
        for (int i = 0; i < size; i++) {
            //如果10秒钟拿不到一个数据，那么退出循环
            Object data = redisUtil.getRightPop(redisKey, 10L);
            if (ObjectUtil.isNull(data)) {
                continue;
            }
            try {
                boolean result = orderAutoCancel(String.valueOf(data));
                if (!result) {
                    redisUtil.lPush(redisKey, data);
                }
            } catch (Exception e) {
                e.printStackTrace();
                redisUtil.lPush(redisKey, data);
            }
        }
    }

    /**
     * 支付订单自动取消
     *
     * @param orderNo 订单编号
     */
    private Boolean orderAutoCancel(String orderNo) {
        Order order = orderService.getByOrderNo(orderNo);
        if (ObjectUtil.isNull(order)) {
            logger.error("自动取消支付订单，订单不存在，订单号为:{}", orderNo);
            return Boolean.TRUE;
        }
        if (order.getPaid()) {
            return Boolean.TRUE;
        }
        if (order.getStatus().equals(OrderConstants.ORDER_STATUS_CANCEL)) {
            return Boolean.TRUE;
        }
        if (!order.getCancelStatus().equals(OrderConstants.ORDER_CANCEL_STATUS_NORMAL)) {
            return Boolean.TRUE;
        }
        // 获取过期时间
        DateTime cancelTime;
        boolean isRedis = true;
        if (redisUtil.exists(redisUtil.get(StrUtil.format(RedisConstants.ORDER_EXPIRE_TIME, order.getOrderNo())))) {
            String expireTime = redisUtil.get(StrUtil.format(RedisConstants.ORDER_EXPIRE_TIME, order.getOrderNo()));
            cancelTime = DateUtil.parseDateTime(expireTime);
        } else {
            cancelTime = DateUtil.offset(order.getCreateTime(), DateField.MINUTE, crmebConfig.getOrderCancelTime());
            isRedis = false;
        }
        long between = DateUtil.between(cancelTime, DateUtil.date(), DateUnit.SECOND, false);
        if (between < 0) {// 未到过期时间继续循环
            // redis不存在过期时间的待支付订单，存入过期时间缓存
            if (!isRedis) {
                DateTime expirationTime = DateUtil.offset(order.getCreateTime(), DateField.MINUTE, crmebConfig.getOrderCancelTime());
                long expireTime = DateUtil.between(DateUtil.date(), expirationTime, DateUnit.SECOND, true);
                redisUtil.set(StrUtil.format(RedisConstants.ORDER_EXPIRE_TIME, order.getOrderNo()), expirationTime.toString(), expireTime);
            }
            return Boolean.FALSE;
        }

        return commonOrderCancel(order, "auto");
    }

    /**
     * 订单取消公共操作
     *
     * @param cancelMethod 取消方法：user-用户取消,auto—系统自动取消
     */
    private Boolean commonOrderCancel(Order order, String cancelMethod) {
        User user = userService.getById(order.getUid());
        List<MerchantOrder> merchantOrderList = merchantOrderService.getByOrderNo(order.getOrderNo());
        List<Integer> couponIdList = new ArrayList<>();
        merchantOrderList.forEach(merchantOrder -> {
            if (merchantOrder.getCouponId() > 0) {
                couponIdList.add(merchantOrder.getCouponId());
            }
        });
        if (order.getPlatCouponId() > 0) {
            couponIdList.add(order.getPlatCouponId());
        }
        return transactionTemplate.execute(e -> {
            if (cancelMethod.equals("auto")) {
                Boolean cancel = orderService.cancel(order.getOrderNo(), false);
                if (!cancel) {
                    logger.error("自动取消订单失败，订单号：{}", order.getOrderNo());
                    e.setRollbackOnly();
                    return Boolean.FALSE;
                }
                orderStatusService.createLog(order.getOrderNo(), "cancel_order", "到期未支付系统自动取消");
            } else {
                //写订单日志
                orderStatusService.createLog(order.getOrderNo(), "cancel_order", "用户取消订单");
            }
            // 退优惠券
            if (CollUtil.isNotEmpty(couponIdList)) {
                couponUserService.rollbackByIds(couponIdList);
            }
            // 退积分
            if (order.getUseIntegral() > 0) {
                userService.updateIntegral(order.getUid(), order.getUseIntegral(), Constants.OPERATION_TYPE_ADD);
                UserIntegralRecord userIntegralRecord = initOrderCancelIntegralRecord(user.getId(), order.getUseIntegral(), user.getIntegral(), order.getOrderNo());
                userIntegralRecordService.save(userIntegralRecord);
            }
            // 积分订单退还积分
            if (order.getRedeemIntegral() > 0) {
                userService.updateIntegral(order.getUid(), order.getRedeemIntegral(), Constants.OPERATION_TYPE_ADD);
                UserIntegralRecord userIntegralRecord = initIntegralOrderCancelIntegralRecord(user.getId(), order.getUseIntegral(), user.getIntegral(), order.getOrderNo());
                userIntegralRecordService.save(userIntegralRecord);
            }
            Boolean rollbackStock = rollbackStock(order);
            if (!rollbackStock) {
                e.setRollbackOnly();
                logger.error("订单回滚库存失败,订单号:{}", order.getOrderNo());
                return Boolean.FALSE;
            }
            return Boolean.TRUE;
        });
    }

    /**
     * 订单回滚库存
     *
     * @param order 订单
     * @return 回滚结果
     */
    private Boolean rollbackStock(Order order) {
        // 查找出商品详情
        List<OrderDetail> orderDetailList = orderDetailService.getByOrderNo(order.getOrderNo());
        if (CollUtil.isEmpty(orderDetailList)) {
            logger.error("订单回滚库存未找到商品详情,订单号:{}", order.getOrderNo());
            return false;
        }

        Boolean result = false;
        if (order.getType().equals(OrderConstants.ORDER_TYPE_SECKILL)) {
            OrderDetail seckillOrderDetail = orderDetailList.get(0);
            // 秒杀订单处理
            SeckillProduct seckillProduct = seckillProductService.getById(seckillOrderDetail.getProductId());
            result = seckillProductService.operationStock(seckillProduct.getId(), seckillOrderDetail.getPayNum(), Constants.OPERATION_TYPE_ADD);
            if (!result) {
                logger.error("订单回滚库存——秒杀商品处理失败,订单号:{}", order.getOrderNo());
                return result;
            }
            ProductAttrValue seckillAttrValue = productAttrValueService.getById(seckillOrderDetail.getAttrValueId());
            // 秒杀sku处理
            result = productAttrValueService.operationStock(seckillAttrValue.getId(), seckillOrderDetail.getPayNum(),
                    Constants.OPERATION_TYPE_ADD, seckillAttrValue.getType(), seckillAttrValue.getMarketingType(),
                    seckillAttrValue.getVersion());
            if (!result) {
                logger.error("订单回滚库存——秒杀商品sku处理失败,订单号:{}", order.getOrderNo());
                return result;
            }
            // 基础商品回滚
            Product product = productService.getById(seckillProduct.getProductId());
            result = productService.operationStock(product.getId(), seckillOrderDetail.getPayNum(), Constants.OPERATION_TYPE_ADD);
            if (!result) {
                logger.error("订单回滚库存——秒杀订单基础商品处理失败,订单号:{}", order.getOrderNo());
                return result;
            }
            // 基础商品sku回滚
            ProductAttrValue productAttrValue = productAttrValueService.getById(seckillAttrValue.getMasterId());
            result = productAttrValueService.operationStock(productAttrValue.getId(), seckillOrderDetail.getPayNum(),
                    Constants.OPERATION_TYPE_ADD, productAttrValue.getType(), productAttrValue.getMarketingType(),
                    productAttrValue.getVersion());
            if (!result) {
                logger.error("订单回滚库存——秒杀订单基础商品sku处理失败,订单号:{}", order.getOrderNo());
                return result;
            }
            // 卡密商品回滚库存
            if (productAttrValue.getType().equals(ProductConstants.PRODUCT_TYPE_CDKEY)) {
                String cardSecretIds = seckillOrderDetail.getCardSecretIds();
                result = cardSecretService.cancelConsume(CrmebUtil.stringToArray(cardSecretIds));
                if (!result) {
                    logger.error("订单回滚库存——秒杀订单卡密处理失败,订单号:{}", order.getOrderNo());
                    return result;
                }
            }
            return Boolean.TRUE;
        }

        for (OrderDetail orderDetail : orderDetailList) {
            if (order.getType().equals(OrderConstants.ORDER_TYPE_PITUAN)) {
//                order.getGroupBuyRecordStatus()
                // 拼团订单返回库存处理
                GroupBuyActivitySku groupBuyActivitySku = groupBuyActivitySkuService.getById(orderDetail.getProductId());
                if (ObjectUtil.isNotNull(groupBuyActivitySku)) {
                    groupBuyActivitySkuService.rollBackSKUStock(order,groupBuyActivitySku.getGroupActivityId(),
                            groupBuyActivitySku.getId(), orderDetail.getPayNum());
                }

            }
            // 回滚商品库存
            Product product = productService.getById(orderDetail.getProductId());
            if (ObjectUtil.isNotNull(product)) {
                result = productService.operationStock(product.getId(), orderDetail.getPayNum(), Constants.OPERATION_TYPE_ADD);
                if (!result) {
                    logger.error("订单回滚库存——商品处理失败,订单号:{}", order.getOrderNo());
                    break;
                }
            }
            ProductAttrValue productAttrValue = productAttrValueService.getById(orderDetail.getAttrValueId());
            if (ObjectUtil.isNotNull(productAttrValue)) {
                result = productAttrValueService.operationStock(productAttrValue.getId(), orderDetail.getPayNum(),
                        Constants.OPERATION_TYPE_ADD, productAttrValue.getType(), productAttrValue.getMarketingType(),
                        productAttrValue.getVersion());
                if (!result) {
                    logger.error("订单回滚库存——商品sku处理失败,订单号:{}", order.getOrderNo());
                    break;
                }
            }
            if (order.getSecondType().equals(OrderConstants.ORDER_SECOND_TYPE_CDKEY)) {
                String cardSecretIds = orderDetail.getCardSecretIds();
                result = cardSecretService.cancelConsume(CrmebUtil.stringToArray(cardSecretIds));
                if (!result) {
                    logger.error("订单回滚库存——商品卡密处理失败,订单号:{}", order.getOrderNo());
                    break;
                }
            }
        }
        return result;
    }

    /**
     * 订单收货
     */
    @Override
    public void orderReceiving() {
        String redisKey = TaskConstants.ORDER_TASK_REDIS_KEY_AFTER_TAKE_BY_USER;
        Long size = redisUtil.getListSize(redisKey);
        logger.info("OrderTaskServiceImpl.orderReceiving | size:" + size);
        if (size < 1) {
            return;
        }
        for (int i = 0; i < size; i++) {
            //如果10秒钟拿不到一个数据，那么退出循环
            Object data = redisUtil.getRightPop(redisKey, 10L);
            if (ObjectUtil.isNull(data)) {
                continue;
            }
            try {
                Boolean result = takeDeliveryAfter(String.valueOf(data));
                if (!result) {
                    redisUtil.lPush(redisKey, data);
                }
            } catch (Exception e) {
                redisUtil.lPush(redisKey, data);
            }
        }
    }

    /**
     * 收货后续处理
     *
     * @param orderNo 订单号
     */
    private Boolean takeDeliveryAfter(String orderNo) {
        Order order = orderService.getByOrderNo(orderNo);
        if (ObjectUtil.isNull(order)) {
            logger.error("订单收货task处理，未找到订单，orderNo={}", orderNo);
            return Boolean.FALSE;
        }
        User user = userService.getById(order.getUid());

        List<String> nodeKeyList = new ArrayList<>();
        nodeKeyList.add(SysConfigConstants.MERCHANT_SHARE_NODE);
        nodeKeyList.add(SysConfigConstants.RETAIL_STORE_BROKERAGE_SHARE_NODE);
        nodeKeyList.add(SysConfigConstants.CONFIG_KEY_STORE_INTEGRAL_FREEZE_NODE);
        MyRecord nodeRecord = systemConfigService.getValuesByKeyList(nodeKeyList);

        Boolean execute = transactionTemplate.execute(e -> {
            String merchantShareNode = nodeRecord.getStr(SysConfigConstants.MERCHANT_SHARE_NODE);
            if (StrUtil.isNotBlank(merchantShareNode) && "receipt".equals(merchantShareNode)) {
                String merchantShareNodeFreezeDayStr = systemConfigService.getValueByKey(SysConfigConstants.MERCHANT_SHARE_FREEZE_TIME);
                if (StrUtil.isNotBlank(merchantShareNodeFreezeDayStr)) {
                    orderMerchantShareFreezingOperation(orderNo, Integer.parseInt(merchantShareNodeFreezeDayStr));
                }
            }
            String retailStoreBrokerageShareNode = nodeRecord.getStr(SysConfigConstants.RETAIL_STORE_BROKERAGE_SHARE_NODE);
            if (StrUtil.isNotBlank(retailStoreBrokerageShareNode) && "receipt".equals(retailStoreBrokerageShareNode)) {
                String retailStoreBrokerageShareFreezeDayStr = systemConfigService.getValueByKey(SysConfigConstants.RETAIL_STORE_BROKERAGE_FREEZING_TIME);
                if (StrUtil.isNotBlank(retailStoreBrokerageShareFreezeDayStr)) {
                    orderBrokerageShareFreezingOperation(orderNo, Integer.parseInt(retailStoreBrokerageShareFreezeDayStr));
                }
            }
            String integralFreezeNode = nodeRecord.getStr(SysConfigConstants.CONFIG_KEY_STORE_INTEGRAL_FREEZE_NODE);
            if (StrUtil.isNotBlank(integralFreezeNode) && "receipt".equals(integralFreezeNode)) {
                String integralDayStr = systemConfigService.getValueByKey(SysConfigConstants.CONFIG_KEY_STORE_INTEGRAL_EXTRACT_TIME);
                if (StrUtil.isNotBlank(integralDayStr)) {
                    orderIntegralFreezingOperation(orderNo, Integer.parseInt(integralDayStr));
                }
            }
            return Boolean.TRUE;
        });
        if (execute) {
            // 发送消息 确认收获通知
            pushMessageOrder(order, user);
        }
        return execute;
    }

    /**
     * 订单自动完成
     */
    @Override
    public void autoComplete() {
        Integer autoCompleteDay = Integer.parseInt(systemConfigService.getValueByKey(SysConfigConstants.CONFIG_ORDER_AUTO_COMPLETE_DAY));

        List<Order> orderList = orderService.findCanCompleteOrder(autoCompleteDay);
        if (CollUtil.isEmpty(orderList)) {
            logger.info("OrderTaskServiceImpl.autoComplete | size:0");
            return;
        }
        List<String> orderNoList = orderList.stream().map(Order::getOrderNo).collect(Collectors.toList());
        Boolean execute = transactionTemplate.execute(e -> {
            orderService.batchCompleteByOrderNo(orderNoList);
            orderNoList.forEach(orderNo -> {
                orderStatusService.createLog(orderNo, OrderStatusConstants.ORDER_STATUS_COMPLETE, "订单已完成");
            });
            return Boolean.TRUE;
        });
        if (execute) {
            logger.error("订单自动完成：更新数据库失败，orderNoList = {}", JSON.toJSONString(orderNoList));
        }
        asyncService.orderCompleteAfterFreezingOperation(orderNoList);
    }

    /**
     * 订单积分冻结处理
     *
     * @param orderNo   订单编号
     * @param freezeDay 积分冻结天数
     */
    private void orderIntegralFreezingOperation(String orderNo, Integer freezeDay) {
        Order order = orderService.getByOrderNo(orderNo);
        if (freezeDay > 0) {
            UserIntegralRecord record = userIntegralRecordService.getByOrderNoAndType(orderNo, IntegralRecordConstants.INTEGRAL_RECORD_TYPE_ADD);
            if (!record.getStatus().equals(IntegralRecordConstants.INTEGRAL_RECORD_STATUS_CREATE)) {
                return;
            }
            // 佣金进入冻结期
            record.setStatus(IntegralRecordConstants.INTEGRAL_RECORD_STATUS_FROZEN);
            // 计算解冻时间
            record.setFrozenTime(freezeDay);
            DateTime dateTime = DateUtil.offsetDay(new Date(), freezeDay);
            long thawTime = dateTime.getTime();
            record.setThawTime(thawTime);
            record.setUpdateTime(DateUtil.date());
            boolean batch = userIntegralRecordService.updateById(record);
            if (!batch) {
                logger.error("订单积分冻结处理失败，订单号={}", order.getOrderNo());
            }
            return;
        }
        User user = userService.getById(order.getUid());
        if (ObjectUtil.isNull(user)) {
            logger.error("订单积分冻结处理失败，未找到对应的用户信息，订单编号={}", order.getOrderNo());
            return;
        }
        Integer balance = user.getIntegral();

        UserIntegralRecord record = userIntegralRecordService.getByOrderNoAndType(orderNo, IntegralRecordConstants.INTEGRAL_RECORD_TYPE_ADD);
        if (record.getStatus().equals(IntegralRecordConstants.INTEGRAL_RECORD_STATUS_COMPLETE)) {
            return;
        }
        // 佣金完结期
        record.setStatus(IntegralRecordConstants.INTEGRAL_RECORD_STATUS_COMPLETE);
        // 计算解冻时间
        long thawTime = DateUtil.current(false);
        record.setFrozenTime(freezeDay);
        record.setThawTime(thawTime);
        // 计算积分余额
        balance = balance + record.getIntegral();
        record.setBalance(balance);
        record.setUpdateTime(DateUtil.date());
        Boolean execute = transactionTemplate.execute(e -> {
            userIntegralRecordService.updateById(record);
            userService.updateIntegral(user.getId(), record.getIntegral(), Constants.OPERATION_TYPE_ADD);
            return Boolean.TRUE;
        });
        if (!execute) {
            logger.error("订单积分冻结处理：直接解冻失败，订单编号={}", order.getOrderNo());
        }
    }

    /**
     * 订单佣金分账冻结处理
     *
     * @param orderNo   订单编号
     * @param freezeDay 积分冻结天数
     */
    private void orderBrokerageShareFreezingOperation(String orderNo, Integer freezeDay) {
        Order order = orderService.getByOrderNo(orderNo);
        List<UserBrokerageRecord> recordList = new ArrayList<>();
        if (freezeDay > 0) {
            List<UserBrokerageRecord> brokerageRecordList = userBrokerageRecordService.findListByLinkNoAndLinkType(orderNo, BrokerageRecordConstants.BROKERAGE_RECORD_LINK_TYPE_ORDER);
            if (CollUtil.isEmpty(brokerageRecordList)) {
                return;
            }
            for (UserBrokerageRecord record : brokerageRecordList) {
                if (!record.getStatus().equals(BrokerageRecordConstants.BROKERAGE_RECORD_STATUS_CREATE)) {
                    continue;
                }
                // 佣金进入冻结期
                record.setStatus(BrokerageRecordConstants.BROKERAGE_RECORD_STATUS_FROZEN);
                // 计算解冻时间
                record.setFrozenTime(freezeDay);
                DateTime dateTime = DateUtil.offsetDay(new Date(), record.getFrozenTime());
                long thawTime = dateTime.getTime();
                record.setThawTime(thawTime);
                record.setUpdateTime(DateUtil.date());
                recordList.add(record);
            }
            if (CollUtil.isEmpty(recordList)) {
                return;
            }
            userBrokerageRecordService.updateBatchById(recordList);
            return;
        }

        List<UserBrokerageRecord> brokerageRecordList = userBrokerageRecordService.findListByLinkNoAndLinkType(orderNo, BrokerageRecordConstants.BROKERAGE_RECORD_LINK_TYPE_ORDER);
        if (CollUtil.isEmpty(brokerageRecordList)) {
            return;
        }
        for (UserBrokerageRecord record : brokerageRecordList) {
            if (!record.getStatus().equals(BrokerageRecordConstants.BROKERAGE_RECORD_STATUS_CREATE)) {
                continue;
            }
            User user = userService.getById(record.getUid());
            if (ObjectUtil.isNull(user)) {
                continue;
            }
            record.setStatus(BrokerageRecordConstants.BROKERAGE_RECORD_STATUS_COMPLETE);
            // 计算佣金余额
            BigDecimal balance = user.getBrokeragePrice().add(record.getPrice());
            record.setBalance(balance);
            record.setUpdateTime(DateUtil.date());

            recordList.add(record);
        }
        if (CollUtil.isEmpty(recordList)) {
            return;
        }
        Boolean execute = transactionTemplate.execute(e -> {
            userBrokerageRecordService.updateBatchById(recordList);
            recordList.forEach(record -> {
                userService.updateBrokerage(record.getUid(), record.getPrice(), Constants.OPERATION_TYPE_ADD);
            });
            return Boolean.TRUE;
        });
        if (!execute) {
            logger.error(StrUtil.format("佣金解冻处理—数据库出错，订单编号 = {}", order.getOrderNo()));
        }

    }

    /**
     * 订单商户分账冻结处理
     *
     * @param orderNo   订单编号
     * @param freezeDay 积分冻结天数
     */
    private void orderMerchantShareFreezingOperation(String orderNo, Integer freezeDay) {
        Order order = orderService.getByOrderNo(orderNo);
        MerchantBalanceRecord record = merchantBalanceRecordService.getByLinkNo(orderNo);
        if (ObjectUtil.isNull(record)) {
            return;
        }
        if (!record.getStatus().equals(1)) {
            return;
        }
        if (freezeDay > 0) {
            record.setStatus(2);
            record.setFrozenTime(freezeDay);
            DateTime dateTime = DateUtil.offsetDay(new Date(), record.getFrozenTime());
            long thawTime = dateTime.getTime();
            record.setThawTime(thawTime);
            record.setUpdateTime(DateUtil.date());
            merchantBalanceRecordService.updateById(record);
            return;
        }

        record.setStatus(3);
        Merchant merchant = merchantService.getById(record.getMerId());
        record.setBalance(merchant.getBalance().add(record.getAmount()));
        Boolean execute = transactionTemplate.execute(e -> {
            merchantBalanceRecordService.updateById(record);
            merchantService.operationBalance(record.getMerId(), record.getAmount(), Constants.OPERATION_TYPE_ADD);
            return Boolean.TRUE;
        });
        if (!execute) {
            logger.error(StrUtil.format("商户余额解冻处理—数据库出错，订单编号 = {}", order.getOrderNo()));
        }
    }

    /**
     * 发送消息通知
     * 根据用户类型发送
     * 公众号模板消息
     * 小程序订阅消息
     */
    private void pushMessageOrder(Order order, User user) {

        SystemNotification notification = systemNotificationService.getByMark(NotifyConstants.RECEIPT_GOODS_MARK);
        if (order.getPayChannel().equals(PayConstants.PAY_CHANNEL_H5)) {
            return;
        }
        if (!order.getPayType().equals(PayConstants.PAY_TYPE_WE_CHAT)) {
            return;
        }
        UserToken userToken;
        HashMap<String, String> temMap = new HashMap<>();
        // 公众号

        if (order.getPayChannel().equals(PayConstants.PAY_CHANNEL_WECHAT_PUBLIC) && notification.getIsWechat().equals(1)) {
            userToken = userTokenService.getTokenByUserId(user.getId(), UserConstants.USER_TOKEN_TYPE_WECHAT);
            if (ObjectUtil.isNull(userToken)) {
                return;
            }
            /**
             * {{first.DATA}}
             * 订单编号：{{keyword1.DATA}}
             * 订单状态：{{keyword2.DATA}}
             * 收货时间：{{keyword3.DATA}}
             * 商品详情：{{keyword4.DATA}}
             * {{remark.DATA}}
             */
            // 发送微信模板消息
            temMap.put(Constants.WE_CHAT_TEMP_KEY_FIRST, "您购买的商品已确认收货！");
            temMap.put("keyword1", order.getOrderNo());
            temMap.put("keyword2", "已收货");
            temMap.put("keyword3", CrmebDateUtil.nowDateTimeStr());
            temMap.put("keyword4", "详情请进入订单查看");
            temMap.put(Constants.WE_CHAT_TEMP_KEY_END, "感谢你的使用。");
            templateMessageService.pushTemplateMessage(notification.getWechatId(), temMap, userToken.getToken());
        } else if (notification.getIsRoutine().equals(1)) {
            // 小程序发送订阅消息
            userToken = userTokenService.getTokenByUserId(user.getId(), UserConstants.USER_TOKEN_TYPE_ROUTINE);
            if (ObjectUtil.isNull(userToken)) {
                return;
            }
            List<OrderDetail> orderInvoiceDetailList = orderDetailService.getByOrderNo(order.getOrderNo());
            List<String> productNameList = orderInvoiceDetailList.stream().map(OrderDetail::getProductName).collect(Collectors.toList());
            // 获取商品名称
            String storeNameAndCarNumString = String.join(",", productNameList);

            // 组装数据
            if (StrUtil.isBlank(storeNameAndCarNumString)) {
                return;
            }
            if (storeNameAndCarNumString.length() > 20) {
                storeNameAndCarNumString = storeNameAndCarNumString.substring(0, 15) + "***";
            }
//        temMap.put("character_string6", storeOrder.getOrderId());
//        temMap.put("phrase4", "已收货");
//        temMap.put("time7", DateUtil.nowDateTimeStr());
//        temMap.put("thing1", storeNameAndCarNumString);
//        temMap.put("thing5", "您购买的商品已确认收货！");
            temMap.put("character_string6", order.getOrderNo());
            temMap.put("date5", CrmebDateUtil.nowDateTimeStr());
            temMap.put("thing2", storeNameAndCarNumString);
            templateMessageService.pushMiniTemplateMessage(notification.getRoutineId(), temMap, userToken.getToken());
        }
    }

    /**
     * 订单自动收货
     */
    @Override
    public void autoTakeDelivery() {
        int day = 14;
        String autoDay = systemConfigService.getValueByKey(SysConfigConstants.CONFIG_ORDER_AUTO_TAKE_DELIVERY_DAY);
        if (StrUtil.isNotBlank(autoDay) && Integer.parseInt(autoDay) >= 1) {
            day = Integer.parseInt(autoDay);
        }
        DateTime nowDate = DateUtil.date();
        DateTime dateTime = DateUtil.offsetDay(nowDate, -day);
        List<Order> orderList = orderService.findAwaitTakeDeliveryOrderList(dateTime.toString());
        if (CollUtil.isEmpty(orderList)) {
            return;
        }
        orderList = orderList.stream().filter(order -> !order.getType().equals(1)).collect(Collectors.toList());
        if (CollUtil.isEmpty(orderList)) {
            return;
        }
        List<String> orderNoList = orderList.stream().map(Order::getOrderNo).collect(Collectors.toList());
        Boolean execute = transactionTemplate.execute(e -> {
            orderNoList.forEach(orderNo -> {
                orderService.takeDelivery(orderNo);
                orderDetailService.takeDelivery(orderNo);
                orderStatusService.createLog(orderNo, OrderStatusConstants.ORDER_STATUS_USER_TAKE_DELIVERY, OrderStatusConstants.ORDER_LOG_SYSTEM_AUTO_RECEIPT);
            });
            return Boolean.TRUE;
        });
        if (!execute) {
            logger.error("自动收货操作数据数失败：订单号:{}", StringUtils.join(orderNoList, ","));
            return;
        }
        //后续操作放入redis
        orderNoList.forEach(orderNo -> {
            redisUtil.lPush(TaskConstants.ORDER_TASK_REDIS_KEY_AFTER_TAKE_BY_USER, orderNo);
        });
    }

    /**
     * 查询退款订单支付宝退款
     */
    @Override
    public void queryRefundOrderAliPayRefund() {
        String redisKey = TaskConstants.TASK_REFUND_ORDER_ALIPAY_QUERY_KEY;
        Long size = redisUtil.getListSize(redisKey);
        logger.info("OrderTaskServiceImpl.queryRefundOrderAliPayRefund | size:" + size);
        if (size < 1) {
            return;
        }
        for (int i = 0; i < size; i++) {
            //如果10秒钟拿不到一个数据，那么退出循环
            Object data = redisUtil.getRightPop(redisKey, 10L);
            if (ObjectUtil.isNull(data)) {
                continue;
            }
            try {
                boolean result = refundOrderService.queryAliPayRefund(String.valueOf(data));
                if (!result) {
                    redisUtil.lPush(redisKey, data);
                }
            } catch (Exception e) {
                e.printStackTrace();
                redisUtil.lPush(redisKey, data);
            }
        }
    }
}
