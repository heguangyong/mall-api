package com.zbkj.service.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.github.pagehelper.PageInfo;
import com.zbkj.common.model.order.RefundOrder;
import com.zbkj.common.request.OrderRefundAuditRequest;
import com.zbkj.common.request.RefundOrderRemarkRequest;
import com.zbkj.common.request.RefundOrderSearchRequest;
import com.zbkj.common.request.RejectReceivingRequest;
import com.zbkj.common.response.MerchantRefundOrderPageResponse;
import com.zbkj.common.response.RefundOrderAdminDetailResponse;
import com.zbkj.common.response.RefundOrderCountItemResponse;

/**
*  RefundOrderService 接口 针对移动端商家管理
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
public interface RefundOrderEmployeeService extends IService<RefundOrder> {

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

    /**
     * 商户端退款单详情响应对象
     * @param refundOrderNo 退款单号
     * @return 退款单详情
     */
    RefundOrderAdminDetailResponse getMerchantDetail(String refundOrderNo);

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
     * 退款单-商家拒绝收货退款
     * @param request 拒绝收货请求对象
     */
    Boolean receivingReject(RejectReceivingRequest request);
}
