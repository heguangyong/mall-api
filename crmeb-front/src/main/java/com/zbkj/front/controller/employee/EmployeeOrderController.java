package com.zbkj.front.controller.employee;

import com.zbkj.common.annotation.LogControllerAnnotation;
import com.zbkj.common.enums.MethodType;
import com.zbkj.common.model.order.OrderDetail;
import com.zbkj.common.page.CommonPage;
import com.zbkj.common.request.*;
import com.zbkj.common.response.MerchantOrderPageResponse;
import com.zbkj.common.response.OrderAdminDetailResponse;
import com.zbkj.common.response.OrderCountItemResponse;
import com.zbkj.common.response.OrderInvoiceResponse;
import com.zbkj.common.result.CommonResult;
import com.zbkj.common.vo.LogisticsResultVo;
import com.zbkj.front.service.EmployeeOrderService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 移动端 商家管理
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
@Slf4j
@RestController
@RequestMapping("api/front/employee/order")
@Api(tags = "移动端商家管理 - 订单控制器") //配合swagger使用
public class EmployeeOrderController {

    @Autowired
    private EmployeeOrderService employeeOrderService;

    @ApiOperation(value = "商户端订单分页列表") //配合swagger使用
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public CommonResult<CommonPage<MerchantOrderPageResponse>> getList(@Validated OrderSearchRequest request) {
        return CommonResult.success(CommonPage.restPage(employeeOrderService.getMerchantAdminPage(request)));
    }

    @ApiOperation(value = "获取订单各状态数量")
    @RequestMapping(value = "/status/num", method = RequestMethod.GET)
    public CommonResult<OrderCountItemResponse> getOrderStatusNum(@Validated OrderTabsHeaderRequest request) {
        return CommonResult.success(employeeOrderService.getMerchantOrderStatusNum(request));
    }

    @LogControllerAnnotation(intoDB = true, methodType = MethodType.DELETE, description = "移动端商家管理 删除订单")
    @ApiOperation(value = "订单删除")
    @RequestMapping(value = "/delete/{orderNo}", method = RequestMethod.POST)
    public CommonResult<String> delete(@PathVariable(name = "orderNo") String orderNo) {
        if (employeeOrderService.merchantDeleteByOrderNo(orderNo)) {
            return CommonResult.success();
        }
        return CommonResult.failed();
    }

    @LogControllerAnnotation(intoDB = true, methodType = MethodType.UPDATE, description = "商户备注订单")
    @ApiOperation(value = "商户备注订单")
    @RequestMapping(value = "/mark", method = RequestMethod.POST)
    public CommonResult<String> mark(@RequestBody @Validated OrderRemarkRequest request) {
        if (employeeOrderService.merchantMark(request)) {
            return CommonResult.success();
        }
        return CommonResult.failed();
    }

    @ApiOperation(value = "订单详情")
    @RequestMapping(value = "/info/{orderNo}", method = RequestMethod.GET)
    public CommonResult<OrderAdminDetailResponse> info(@PathVariable(value = "orderNo") String orderNo) {
        return CommonResult.success(employeeOrderService.adminDetail(orderNo));
    }

    @ApiOperation(value = "订单细节详情列表（发货使用）")
    @RequestMapping(value = "/{orderNo}/detail/list", method = RequestMethod.GET)
    public CommonResult<List<OrderDetail>> getDetailList(@PathVariable(value = "orderNo") String orderNo) {
        return CommonResult.success(employeeOrderService.getDetailList(orderNo));
    }

    @LogControllerAnnotation(intoDB = true, methodType = MethodType.UPDATE, description = "订单发货")
    @ApiOperation(value = "订单发货")
    @RequestMapping(value = "/send", method = RequestMethod.POST)
    public CommonResult<Boolean> send(@RequestBody @Validated OrderSendRequest request) {
        if (employeeOrderService.send(request)) {
            return CommonResult.success();
        }
        return CommonResult.failed();
    }

    @LogControllerAnnotation(intoDB = true, methodType = MethodType.UPDATE, description = "小票打印")
    @ApiOperation(value = "小票打印")
    @RequestMapping(value = "/printreceipt/{orderno}", method = RequestMethod.GET)
    public CommonResult<Boolean> printReceipt(@PathVariable(value = "orderno") String orderno) {
        employeeOrderService.printReceipt(orderno);
        return CommonResult.success();

    }

    @ApiOperation(value = "获取订单发货单列表")
    @RequestMapping(value = "/{orderNo}/invoice/list", method = RequestMethod.GET)
    public CommonResult<List<OrderInvoiceResponse>> getInvoiceList(@PathVariable(value = "orderNo") String orderNo) {
        return CommonResult.success(employeeOrderService.getInvoiceListByMerchant(orderNo));
    }

    @ApiOperation(value = "订单物流详情")
    @RequestMapping(value = "/get/{invoiceId}/logistics/info", method = RequestMethod.GET)
    public CommonResult<LogisticsResultVo> getLogisticsInfo(@PathVariable(value = "invoiceId") Integer invoiceId) {
        return CommonResult.success(employeeOrderService.getLogisticsInfoByMerchant(invoiceId));
    }

    @LogControllerAnnotation(intoDB = true, methodType = MethodType.UPDATE, description = "移动端商家管理 - 核销码订单详情")
    @ApiOperation(value = "核销码核销订单详情")
    @RequestMapping(value = "/get/verification", method = RequestMethod.POST)
    public CommonResult<MerchantOrderPageResponse> getVerificationOrder(@RequestBody @Validated OrderVerificationRequest request) {
        return CommonResult.success(employeeOrderService.getVerificationOrderByCode(request.getVerifyCode()));
    }

    @LogControllerAnnotation(intoDB = true, methodType = MethodType.UPDATE, description = "移动端商家管理 - 核销码核销订单")
    @ApiOperation(value = "核销码核销订单")
    @RequestMapping(value = "/verification", method = RequestMethod.POST)
    public CommonResult<Object> verificationOrder(@RequestBody @Validated OrderVerificationRequest request) {
        return CommonResult.success(employeeOrderService.verificationOrderByCode(request.getVerifyCode()));
    }

    @LogControllerAnnotation(intoDB = true, methodType = MethodType.UPDATE, description = "移动端商家管理 - 商户直接退款")
    @ApiOperation(value = "商户直接退款")
    @RequestMapping(value = "/direct/refund", method = RequestMethod.POST)
    public CommonResult<Object> directRefund(@RequestBody @Validated MerchantOrderDirectRefundRequest request) {
        if (employeeOrderService.directRefund(request)) {
            return CommonResult.success().setMessage("直接退款成功");
        }
        return CommonResult.failed().setMessage("直接退款失败");
    }

    @LogControllerAnnotation(intoDB = true, methodType = MethodType.UPDATE, description = "修改发货单配送信息")
    @ApiOperation(value = "修改发货单配送信息")
    @RequestMapping(value = "/invoice/update", method = RequestMethod.POST)
    public CommonResult<Object> updateInvoice(@RequestBody @Validated OrderInvoiceUpdateRequest request) {
        if (employeeOrderService.updateInvoice(request)) {
            return CommonResult.success().setMessage("修改发货单配送信息成功");
        }
        return CommonResult.failed().setMessage("修改发货单配送信息失败");
    }
}



