package com.zbkj.front.service.impl;

import com.github.pagehelper.PageInfo;
import com.zbkj.common.enums.MerchantEmployee;
import com.zbkj.common.model.admin.SystemAdmin;
import com.zbkj.common.model.order.OrderDetail;
import com.zbkj.common.request.*;
import com.zbkj.common.response.MerchantOrderPageResponse;
import com.zbkj.common.response.OrderAdminDetailResponse;
import com.zbkj.common.response.OrderCountItemResponse;
import com.zbkj.common.response.OrderInvoiceResponse;
import com.zbkj.common.utils.SecurityUtil;
import com.zbkj.common.vo.LogisticsResultVo;
import com.zbkj.front.service.EmployeeOrderService;
import com.zbkj.service.service.MerchantEmployeeService;
import com.zbkj.service.service.OrderService;
import com.zbkj.service.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

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
public class EmployeeOrderServiceImpl implements EmployeeOrderService {

    @Autowired
    private OrderService orderService;

    @Autowired
    private UserService userService;

    @Autowired
    private MerchantEmployeeService merchantEmployeeService;

    /**
     * 商户端后台分页列表
     *
     * @param request          查询参数
     * @return PageInfo
     */
    @Override
    public PageInfo<MerchantOrderPageResponse> getMerchantAdminPage(OrderSearchRequest request) {
        merchantEmployeeService.checkShopMangerRoleByUserId(MerchantEmployee.ROLE_ORDER.getRoleValue());
        SystemAdmin systemAdmin = userService.getSystemAdminByMerchantEmployee();
        return orderService.getMerchantAdminPage(request, systemAdmin);
    }

    /**
     * 获取商户端订单各状态数量
     */
    @Override
    public OrderCountItemResponse getMerchantOrderStatusNum(OrderTabsHeaderRequest request) {
        merchantEmployeeService.checkShopMangerRoleByUserId(MerchantEmployee.ROLE_ORDER.getRoleValue());
        SystemAdmin systemAdmin = userService.getSystemAdminByMerchantEmployee();
        return orderService.getMerchantOrderStatusNum(request, systemAdmin);
    }

    /**
     * 订单详情（PC）
     *
     * @param orderNo 订单编号
     * @return OrderAdminDetailResponse
     */
    @Override
    public OrderAdminDetailResponse adminDetail(String orderNo) {
        merchantEmployeeService.checkShopMangerRoleByUserId(MerchantEmployee.ROLE_ORDER.getRoleValue());
        SystemAdmin systemAdmin = userService.getSystemAdminByMerchantEmployee();
        return orderService.adminDetail(orderNo, systemAdmin);
    }

    /**
     * 发货
     *
     * @param request 发货参数
     * @return Boolean
     */
    @Override
    public Boolean send(OrderSendRequest request) {
        merchantEmployeeService.checkShopMangerRoleByUserId(MerchantEmployee.ROLE_ORDER.getRoleValue());
        SystemAdmin systemAdmin = userService.getSystemAdminByMerchantEmployee();
        // 拆单发货
        return orderService.send(request, systemAdmin);
    }

    /**
     * 小票打印
     *
     * @param orderNo 订单编号
     * @return 打印结果
     */
    @Override
    public void printReceipt(String orderNo) {
        merchantEmployeeService.checkShopMangerRoleByUserId(MerchantEmployee.ROLE_ORDER.getRoleValue());
        SystemAdmin systemAdmin = userService.getSystemAdminByMerchantEmployee();
        orderService.printReceipt(orderNo, systemAdmin);
    }

    /**
     * 商户删除订单
     *
     * @param orderNo 订单编号
     * @return Boolean
     */
    @Override
    public Boolean merchantDeleteByOrderNo(String orderNo) {
        merchantEmployeeService.checkShopMangerRoleByUserId(MerchantEmployee.ROLE_ORDER.getRoleValue());
        SystemAdmin systemAdmin = userService.getSystemAdminByMerchantEmployee();
        return orderService.merchantDeleteByOrderNo(orderNo,systemAdmin);
    }

    /**
     * 商户备注订单
     *
     * @param request 备注参数
     * @return Boolean
     */
    @Override
    public Boolean merchantMark(OrderRemarkRequest request) {
        merchantEmployeeService.checkShopMangerRoleByUserId(MerchantEmployee.ROLE_ORDER.getRoleValue());
        SystemAdmin systemAdmin = userService.getSystemAdminByMerchantEmployee();
        return orderService.merchantMark(request, systemAdmin);
    }

    /**
     * 获取订单快递信息(商户端)
     *
     * @param invoiceId 发货单ID
     * @return LogisticsResultVo
     */
    @Override
    public LogisticsResultVo getLogisticsInfoByMerchant(Integer invoiceId) {
        merchantEmployeeService.checkShopMangerRoleByUserId(MerchantEmployee.ROLE_ORDER.getRoleValue());
        SystemAdmin systemAdmin = userService.getSystemAdminByMerchantEmployee();
        return orderService.getLogisticsInfoByMerchant(invoiceId,systemAdmin);
    }

    /**
     * 核销码核销订单
     *
     * @param verifyCode 核销码
     * @return 核销结果
     */
    @Override
    public Boolean verificationOrderByCode(String verifyCode) {
        merchantEmployeeService.checkShopMangerRoleByUserId(MerchantEmployee.ROLE_ORDER.getRoleValue());
        SystemAdmin systemAdmin = userService.getSystemAdminByMerchantEmployee();
        return orderService.verificationOrderByCode(verifyCode, systemAdmin);
    }

    /**
     * 根据核销码获取
     *
     * @param verifyCode 授权码
     * @return 待审核订单详情
     */
    @Override
    public MerchantOrderPageResponse getVerificationOrderByCode(String verifyCode) {
        merchantEmployeeService.checkShopMangerRoleByUserId(MerchantEmployee.ROLE_ORDER.getRoleValue());
        SystemAdmin systemAdmin = userService.getSystemAdminByMerchantEmployee();
        return orderService.getVerificationOrderByCode(verifyCode, systemAdmin);
    }

    /**
     * 订单细节详情列表
     *
     * @param orderNo 订单号
     * @return 订单细节详情列表
     */
    @Override
    public List<OrderDetail> getDetailList(String orderNo) {
        merchantEmployeeService.checkShopMangerRoleByUserId(MerchantEmployee.ROLE_ORDER.getRoleValue());
        SystemAdmin systemAdmin = userService.getSystemAdminByMerchantEmployee();
        return orderService.getDetailList(orderNo, systemAdmin);
    }

    /**
     * 获取订单发货单列表(商户端)
     *
     * @param orderNo 订单号
     * @return 发货单列表
     */
    @Override
    public List<OrderInvoiceResponse> getInvoiceListByMerchant(String orderNo) {
        merchantEmployeeService.checkShopMangerRoleByUserId(MerchantEmployee.ROLE_ORDER.getRoleValue());
        SystemAdmin systemAdmin = userService.getSystemAdminByMerchantEmployee();
        return orderService.getInvoiceListByMerchant(orderNo, systemAdmin);
    }

    /**
     * 商户直接退款
     */
    @Override
    public Boolean directRefund(MerchantOrderDirectRefundRequest request) {
        merchantEmployeeService.checkShopMangerRoleByUserId(MerchantEmployee.ROLE_ORDER.getRoleValue());
        SystemAdmin systemAdmin = userService.getSystemAdminByMerchantEmployee();
        return orderService.directRefund(request,systemAdmin);
    }

    /**
     * 修改发货单配送信息
     */
    @Override
    public Boolean updateInvoice(OrderInvoiceUpdateRequest request) {
        merchantEmployeeService.checkShopMangerRoleByUserId(MerchantEmployee.ROLE_ORDER.getRoleValue());
        SystemAdmin systemAdmin = userService.getSystemAdminByMerchantEmployee();
        return orderService.updateInvoice(request, systemAdmin);
    }


}

