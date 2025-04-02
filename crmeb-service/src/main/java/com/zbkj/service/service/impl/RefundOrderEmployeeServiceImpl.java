package com.zbkj.service.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.PageInfo;
import com.zbkj.common.enums.MerchantEmployee;
import com.zbkj.common.model.admin.SystemAdmin;
import com.zbkj.common.model.order.RefundOrder;
import com.zbkj.common.request.OrderRefundAuditRequest;
import com.zbkj.common.request.RefundOrderRemarkRequest;
import com.zbkj.common.request.RefundOrderSearchRequest;
import com.zbkj.common.request.RejectReceivingRequest;
import com.zbkj.common.response.MerchantRefundOrderPageResponse;
import com.zbkj.common.response.RefundOrderAdminDetailResponse;
import com.zbkj.common.response.RefundOrderCountItemResponse;
import com.zbkj.service.dao.RefundOrderDao;
import com.zbkj.service.service.MerchantEmployeeService;
import com.zbkj.service.service.RefundOrderEmployeeService;
import com.zbkj.service.service.RefundOrderManagerService;
import com.zbkj.service.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * RefundOrderServiceImpl 接口实现 针对移动端商家管理
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
public class RefundOrderEmployeeServiceImpl extends ServiceImpl<RefundOrderDao, RefundOrder> implements RefundOrderEmployeeService {


    @Autowired
    private RefundOrderManagerService refundOrderManagerService;

    @Autowired
    private UserService userService;

    @Autowired
    private MerchantEmployeeService merchantEmployeeService;
    /**
     * 商户端退款订单分页列表
     *
     * @param request          查询参数
     * @return PageInfo
     */
    @Override
    public PageInfo<MerchantRefundOrderPageResponse> getMerchantAdminPage(RefundOrderSearchRequest request) {
        merchantEmployeeService.checkShopMangerRoleByUserId(MerchantEmployee.ROLE_REFUND.getRoleValue());
        SystemAdmin systemAdmin = userService.getSystemAdminByMerchantEmployee();
        return refundOrderManagerService.getMerchantAdminPage(request, systemAdmin);
    }

    /**
     * 获取商户端退款订单各状态数量
     *
     * @return RefundOrderCountItemResponse
     */
    @Override
    public RefundOrderCountItemResponse getMerchantOrderStatusNum(RefundOrderSearchRequest request) {
        merchantEmployeeService.checkShopMangerRoleByUserId(MerchantEmployee.ROLE_REFUND.getRoleValue());
        SystemAdmin systemAdmin = userService.getSystemAdminByMerchantEmployee();
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
        merchantEmployeeService.checkShopMangerRoleByUserId(MerchantEmployee.ROLE_REFUND.getRoleValue());
        SystemAdmin systemAdmin = userService.getSystemAdminByMerchantEmployee();
        return refundOrderManagerService.mark(request, systemAdmin);
    }

    /**
     * 商户端退款单详情响应对象
     * @param refundOrderNo 退款单号
     * @return 退款单详情
     */
    @Override
    public RefundOrderAdminDetailResponse getMerchantDetail(String refundOrderNo) {
        merchantEmployeeService.checkShopMangerRoleByUserId(MerchantEmployee.ROLE_REFUND.getRoleValue());
        SystemAdmin systemAdmin = userService.getSystemAdminByMerchantEmployee();
        return refundOrderManagerService.getMerchantDetail(refundOrderNo, systemAdmin);
    }

    /**
     * 退款单审核
     * @param request 审核参数
     * @return 审核结果
     */
    @Override
    public Boolean audit(OrderRefundAuditRequest request) {
        merchantEmployeeService.checkShopMangerRoleByUserId(MerchantEmployee.ROLE_REFUND.getRoleValue());
        SystemAdmin systemAdmin = userService.getSystemAdminByMerchantEmployee();
        return refundOrderManagerService.audit(request, systemAdmin);
    }

    /**
     * 退款单收到退货
     * @param refundOrderNo 退款单号
     */
    @Override
    public Boolean receiving(String refundOrderNo) {
        merchantEmployeeService.checkShopMangerRoleByUserId(MerchantEmployee.ROLE_REFUND.getRoleValue());
        SystemAdmin systemAdmin = userService.getSystemAdminByMerchantEmployee();
        return refundOrderManagerService.receiving(refundOrderNo, systemAdmin);
    }

    /**
     * 退款单-商家拒绝收货退款
     * @param request 拒绝收货请求对象
     */
    @Override
    public Boolean receivingReject(RejectReceivingRequest request) {
        merchantEmployeeService.checkShopMangerRoleByUserId(MerchantEmployee.ROLE_REFUND.getRoleValue());
        SystemAdmin systemAdmin = userService.getSystemAdminByMerchantEmployee();
        return refundOrderManagerService.receivingReject(request, systemAdmin);
    }

}

