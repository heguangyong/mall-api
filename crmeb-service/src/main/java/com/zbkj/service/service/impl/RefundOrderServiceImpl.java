package com.zbkj.service.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.PageInfo;
import com.zbkj.common.model.admin.SystemAdmin;
import com.zbkj.common.model.order.Order;
import com.zbkj.common.model.order.OrderDetail;
import com.zbkj.common.model.order.RefundOrder;
import com.zbkj.common.request.*;
import com.zbkj.common.response.*;
import com.zbkj.common.utils.SecurityUtil;
import com.zbkj.service.dao.RefundOrderDao;
import com.zbkj.service.service.RefundOrderManagerService;
import com.zbkj.service.service.RefundOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

/**
 * RefundOrderServiceImpl 接口实现
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
public class RefundOrderServiceImpl extends ServiceImpl<RefundOrderDao, RefundOrder> implements RefundOrderService {


    @Autowired
    private RefundOrderManagerService refundOrderManagerService;
    /**
     * 商户端退款订单分页列表
     *
     * @param request          查询参数
     * @return PageInfo
     */
    @Override
    public PageInfo<MerchantRefundOrderPageResponse> getMerchantAdminPage(RefundOrderSearchRequest request) {
        SystemAdmin systemAdmin = SecurityUtil.getLoginUserVo().getUser();
        return refundOrderManagerService.getMerchantAdminPage(request, systemAdmin);
    }

    /**
     * 获取商户端退款订单各状态数量
     *
     * @return RefundOrderCountItemResponse
     */
    @Override
    public RefundOrderCountItemResponse getMerchantOrderStatusNum(RefundOrderSearchRequest request) {
        SystemAdmin systemAdmin = SecurityUtil.getLoginUserVo().getUser();
        return refundOrderManagerService.getMerchantOrderStatusNum(request, systemAdmin);
    }

    /**
     * 备注退款单
     *
     * @param request 备注参数
     * @return Boolean
     */
    @Override
    public Boolean mark(RefundOrderRemarkRequest request) {
        SystemAdmin systemAdmin = SecurityUtil.getLoginUserVo().getUser();
        return refundOrderManagerService.mark(request, systemAdmin);
    }

    @Override
    public RefundOrder getInfoException(String refundOrderNo) {
       return refundOrderManagerService.getInfoException(refundOrderNo);
    }

    /**
     * 退款订单列表
     * @param request 搜索参数
     * @return List
     */
    @Override
    public PageInfo<RefundOrderResponse> getH5List(OrderAfterSalesSearchRequest request) {
       return refundOrderManagerService.getH5List(request);
    }

    /**
     * 退款订单详情（移动端）
     * @param refundOrderNo 退款订单号
     * @return RefundOrderInfoResponse
     */
    @Override
    public RefundOrderInfoResponse getRefundOrderDetailByRefundOrderNo(String refundOrderNo) {
        return refundOrderManagerService.getRefundOrderDetailByRefundOrderNo(refundOrderNo);
    }

    /**
     * 商户端退款单详情响应对象
     * @param refundOrderNo 退款单号
     * @return 退款单详情
     */
    @Override
    public RefundOrderAdminDetailResponse getMerchantDetail(String refundOrderNo) {
        SystemAdmin systemAdmin = SecurityUtil.getLoginUserVo().getUser();
        return refundOrderManagerService.getMerchantDetail(refundOrderNo, systemAdmin);
    }

    /**
     * 平台端退款订单分页列表
     * @param request 查询参数
     * @return PageInfo
     */
    @Override
    public PageInfo<PlatformRefundOrderPageResponse> getPlatformAdminPage(RefundOrderSearchRequest request) {
        return refundOrderManagerService.getPlatformAdminPage(request);
    }

    /**
     * 获取平台端退款订单各状态数量
     * @return RefundOrderCountItemResponse
     */
    @Override
    public RefundOrderCountItemResponse getPlatformOrderStatusNum(RefundOrderSearchRequest request) {
        return refundOrderManagerService.getPlatformOrderStatusNum(request);
    }

    /**
     * 平台备注退款单
     * @param request 备注参数
     * @return Boolean
     */
    @Override
    public Boolean platformMark(RefundOrderRemarkRequest request) {
        return refundOrderManagerService.platformMark(request);
    }

    /**fan
     * 平台端退款订单详情
     * @param refundOrderNo 退款单号
     * @return 退款单详情
     */
    @Override
    public RefundOrderAdminDetailResponse getPlatformDetail(String refundOrderNo) {
        return refundOrderManagerService.getPlatformDetail(refundOrderNo);
    }

    /**
     * 获取某一天的所有数据
     * @param merId 商户id，0为所有商户
     * @param date 日期：年-月-日
     * @return List
     */
    @Override
    public List<RefundOrder> findByDate(Integer merId, String date) {
        return refundOrderManagerService.findByDate(merId, date);
    }

    /**
     * 获取某一月的所有数据
     * @param merId 商户id，0为所有商户
     * @param month 日期：年-月
     * @return List
     */
    @Override
    public List<RefundOrder> findByMonth(Integer merId, String month) {
       return refundOrderManagerService.findByMonth(merId, month);
    }

    /**
     * 根据日期获取退款订单数量
     * @param date 日期
     * @return Integer
     */
    @Override
    public Integer getRefundOrderNumByDate(String date) {
        return refundOrderManagerService.getRefundOrderNumByDate(date);
    }

    /**
     * 根据日期获取退款订单金额
     * @param date 日期
     * @return Integer
     */
    @Override
    public BigDecimal getRefundOrderAmountByDate(String date) {
       return refundOrderManagerService.getRefundOrderAmountByDate(date);
    }

    /**
     * 获取退款中（申请）订单数量
     */
    @Override
    public Integer getRefundingCount(Integer userId) {
        return refundOrderManagerService.getRefundingCount(userId);
    }

    /**
     * 获取退款单详情
     * @param refundOrderNo 退款单号
     */
    @Override
    public RefundOrder getByRefundOrderNo(String refundOrderNo) {
        return refundOrderManagerService.getByRefundOrderNo(refundOrderNo);
    }

    /**
     * 待退款订单数量
     * @return Integer
     */
    @Override
    public Integer getAwaitAuditNum(Integer merId) {
        return refundOrderManagerService.getAwaitAuditNum(merId);
    }

    /**
     * 撤销退款单
     * @param refundOrderNo 退款单号
     */
    @Override
    public Boolean revoke(String refundOrderNo) {
        return refundOrderManagerService.revoke(refundOrderNo);
    }

    /**
     * 退款单审核
     * @param request 审核参数
     * @return 审核结果
     */
    @Override
    public Boolean audit(OrderRefundAuditRequest request) {
        SystemAdmin systemAdmin = SecurityUtil.getLoginUserVo().getUser();
        return refundOrderManagerService.audit(request, systemAdmin);
    }

    /**
     * 退款单收到退货
     * @param refundOrderNo 退款单号
     */
    @Override
    public Boolean receiving(String refundOrderNo) {
        SystemAdmin systemAdmin = SecurityUtil.getLoginUserVo().getUser();
        return refundOrderManagerService.receiving(refundOrderNo, systemAdmin);
    }

    /**
     * 平台强制退款
     * @param refundOrderNo 退款单号
     */
    @Override
    public Boolean compulsoryRefund(String refundOrderNo) {
        SystemAdmin systemAdmin = SecurityUtil.getLoginUserVo().getUser();
        return refundOrderManagerService.compulsoryRefund(refundOrderNo, systemAdmin);
    }

    /**
     * 退款单-商家拒绝收货退款
     * @param request 拒绝收货请求对象
     */
    @Override
    public Boolean receivingReject(RejectReceivingRequest request) {
        SystemAdmin systemAdmin = SecurityUtil.getLoginUserVo().getUser();
        return refundOrderManagerService.receivingReject(request, systemAdmin);
    }

    /**
     * 自动撤销退款
     */
    @Override
    public void autoRevoke() {
       refundOrderManagerService.autoRevoke();
    }

    /**
     * 查询支付宝退款定时任务
     */
    @Override
    public Boolean queryAliPayRefund(String refundOrderNo) {
        return refundOrderManagerService.queryAliPayRefund(refundOrderNo);
    }

    /**
     * 订单退款是否包含用户退款
     * @param orderNo 订单编号
     */
    @Override
    public Boolean isOrderContainUserRefund(String orderNo) {
       return refundOrderManagerService.isOrderContainUserRefund(orderNo);
    }

    /**
     * 商户直接退款
     * @param order 订单
     * @param orderDetailList 需要退款的订单详情列表
     */
    @Override
    public Boolean merchantDirectRefund(Order order, List<OrderDetail> orderDetailList) {
        return refundOrderManagerService.merchantDirectRefund(order, orderDetailList);
    }

    /**
     * 根据第三方单号查询退款单
     * @param outRefundNo 第三方单号
     */
    @Override
    public List<RefundOrder> findByOutRefundNo(String outRefundNo) {
       return refundOrderManagerService.findByOutRefundNo(outRefundNo);
    }

}

