package com.zbkj.service.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.github.pagehelper.PageInfo;
import com.zbkj.common.model.admin.SystemAdmin;
import com.zbkj.common.model.order.Order;
import com.zbkj.common.model.order.OrderDetail;
import com.zbkj.common.model.order.RefundOrder;
import com.zbkj.common.request.*;
import com.zbkj.common.response.*;

import java.math.BigDecimal;
import java.util.List;

/**
*  RefundOrderService 接口
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
public interface RefundOrderService extends IService<RefundOrder> {

    /**
     * 商户端退款订单分页列表
     * @param request 查询参数
     * @return PageInfo
     */
    PageInfo<MerchantRefundOrderPageResponse> getMerchantAdminPage(RefundOrderSearchRequest request);

    /**
     * 获取商户端退款订单各状态数量
     * @return RefundOrderCountItemResponse
     */
    RefundOrderCountItemResponse getMerchantOrderStatusNum(RefundOrderSearchRequest request);

    /**
     * 备注退款单
     * @param request 备注参数
     * @return Boolean
     */
    Boolean mark(RefundOrderRemarkRequest request);

    RefundOrder getInfoException(String refundOrderNo);

    /**
     * 移动端退款订单列表
     * @param request 搜索参数
     * @return List
     */
    PageInfo<RefundOrderResponse> getH5List(OrderAfterSalesSearchRequest request);

    /**
     * 退款订单详情（移动端）
     * @param refundOrderNo 退款订单号
     * @return RefundOrderInfoResponse
     */
    RefundOrderInfoResponse getRefundOrderDetailByRefundOrderNo(String refundOrderNo);

    /**
     * 商户端退款单详情响应对象
     * @param refundOrderNo 退款单号
     * @return 退款单详情
     */
    RefundOrderAdminDetailResponse getMerchantDetail(String refundOrderNo);

    /**
     * 平台端退款订单分页列表
     * @param request 查询参数
     * @return PageInfo
     */
    PageInfo<PlatformRefundOrderPageResponse> getPlatformAdminPage(RefundOrderSearchRequest request);

    /**
     * 获取平台端退款订单各状态数量
     * @param dateLimit 时间参数
     * @return RefundOrderCountItemResponse
     */
    RefundOrderCountItemResponse getPlatformOrderStatusNum(RefundOrderSearchRequest request);

    /**
     * 平台备注退款单
     * @param request 备注参数
     * @return Boolean
     */
    Boolean platformMark(RefundOrderRemarkRequest request);

    /**
     * 平台端退款订单详情
     * @param refundOrderNo 退款单号
     * @return 退款单详情
     */
    RefundOrderAdminDetailResponse getPlatformDetail(String refundOrderNo);

    /**
     * 获取某一天的所有数据
     * @param merId 商户id，0为所有商户
     * @param date 日期：年-月-日
     * @return List
     */
    List<RefundOrder> findByDate(Integer merId, String date);

    /**
     * 获取某一月的所有数据
     * @param merId 商户id，0为所有商户
     * @param month 日期：年-月
     * @return List
     */
    List<RefundOrder> findByMonth(Integer merId, String month);

    /**
     * 根据日期获取退款订单数量
     * @param date 日期
     * @return Integer
     */
    Integer getRefundOrderNumByDate(String date);

    /**
     * 根据日期获取退款订单金额
     * @param date 日期
     * @return Integer
     */
    BigDecimal getRefundOrderAmountByDate(String date);

    /**
     * 获取退款中（申请）订单数量
     */
    Integer getRefundingCount(Integer userId);

    /**
     * 获取退款单详情
     * @param refundOrderNo 退款单号
     */
    RefundOrder getByRefundOrderNo(String refundOrderNo);

    /**
     * 待退款订单数量
     * @return Integer
     */
    Integer getAwaitAuditNum(Integer merId);

    /**
     * 撤销退款单
     * @param refundOrderNo 退款单号
     */
    Boolean revoke(String refundOrderNo);

    /**
     * 退款单审核
     * @param request 审核参数
     * @return 审核结果
     */
    Boolean audit(OrderRefundAuditRequest request);

    /**
     * 退款单收到退货
     * @param refundOrderNo 退款单号
     */
    Boolean receiving(String refundOrderNo);

    /**
     * 平台强制退款
     * @param refundOrderNo 退款单号
     */
    Boolean compulsoryRefund(String refundOrderNo);

    /**
     * 退款单-商家拒绝收货退款
     * @param request 拒绝收货请求对象
     */
    Boolean receivingReject(RejectReceivingRequest request);

    /**
     * 自动撤销退款
     */
    void autoRevoke();

    /**
     * 查询支付宝退款定时任务
     */
    Boolean queryAliPayRefund(String refundOrderNo);

    /**
     * 订单退款是否包含用户退款
     * @param orderNo 订单编号
     */
    Boolean isOrderContainUserRefund(String orderNo);

    /**
     * 商户直接退款
     * @param order 订单
     * @param orderDetailList 需要退款的订单详情列表
     */
    Boolean merchantDirectRefund(Order order, List<OrderDetail> orderDetailList);

    /**
     * 根据第三方单号查询退款单
     * @param outRefundNo 第三方单号
     */
    List<RefundOrder> findByOutRefundNo(String outRefundNo);
}
