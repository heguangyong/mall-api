package com.zbkj.front.controller.employee;

import com.zbkj.common.annotation.LogControllerAnnotation;
import com.zbkj.common.enums.MethodType;
import com.zbkj.common.page.CommonPage;
import com.zbkj.common.request.OrderRefundAuditRequest;
import com.zbkj.common.request.RefundOrderRemarkRequest;
import com.zbkj.common.request.RefundOrderSearchRequest;
import com.zbkj.common.request.RejectReceivingRequest;
import com.zbkj.common.response.MerchantRefundOrderPageResponse;
import com.zbkj.common.response.RefundOrderAdminDetailResponse;
import com.zbkj.common.response.RefundOrderCountItemResponse;
import com.zbkj.common.result.CommonResult;
import com.zbkj.service.service.RefundOrderEmployeeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 移动端商家管理 - 退款订单控制器
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
@RequestMapping("api/front/employee/refund/order")
@Api(tags = "移动端商家管理 - 退款订单控制器") //配合swagger使用
public class EmployeeRefundOrderController {

    @Autowired
    private RefundOrderEmployeeService refundOrderEmployeeService;

    @ApiOperation(value = "商户端退款订单分页列表") //配合swagger使用
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public CommonResult<CommonPage<MerchantRefundOrderPageResponse>> getList(@Validated RefundOrderSearchRequest request) {
        return CommonResult.success(CommonPage.restPage(refundOrderEmployeeService.getMerchantAdminPage(request)));
    }

    @ApiOperation(value = "商户端退款订单详情") //配合swagger使用
    @RequestMapping(value = "/detail/{refundOrderNo}", method = RequestMethod.GET)
    public CommonResult<RefundOrderAdminDetailResponse> getDetail(@PathVariable(value = "refundOrderNo") String refundOrderNo) {
        return CommonResult.success(refundOrderEmployeeService.getMerchantDetail(refundOrderNo));
    }

    @ApiOperation(value = "商户端获取退款订单各状态数量")
    @RequestMapping(value = "/status/num", method = RequestMethod.GET)
    public CommonResult<RefundOrderCountItemResponse> getOrderStatusNum(@Validated RefundOrderSearchRequest request) {
        return CommonResult.success(refundOrderEmployeeService.getMerchantOrderStatusNum(request));
    }

    @LogControllerAnnotation(intoDB = true, methodType = MethodType.UPDATE, description = "商户备注退款订单")
    @ApiOperation(value = "商户备注退款订单")
    @RequestMapping(value = "/mark", method = RequestMethod.POST)
    public CommonResult<String> mark(@RequestBody @Validated RefundOrderRemarkRequest request) {
        if (refundOrderEmployeeService.mark(request)) {
            return CommonResult.success();
        }
        return CommonResult.failed();
    }

    @LogControllerAnnotation(intoDB = true, methodType = MethodType.UPDATE, description = "退款单审核")
    @ApiOperation(value = "退款单审核")
    @RequestMapping(value = "/audit", method = RequestMethod.POST)
    public CommonResult<String> audit(@RequestBody @Validated OrderRefundAuditRequest request) {
        if (refundOrderEmployeeService.audit(request)) {
            return CommonResult.success();
        }
        return CommonResult.failed();
    }

    @LogControllerAnnotation(intoDB = true, methodType = MethodType.UPDATE, description = "退款单收到退货")
    @ApiOperation(value = "退款单收到退货")
    @RequestMapping(value = "/receiving/{refundOrderNo}", method = RequestMethod.POST)
    public CommonResult<String> receiving(@PathVariable(value = "refundOrderNo") String refundOrderNo) {
        if (refundOrderEmployeeService.receiving(refundOrderNo)) {
            return CommonResult.success();
        }
        return CommonResult.failed();
    }

    @LogControllerAnnotation(intoDB = true, methodType = MethodType.UPDATE, description = "退款单拒绝收货")
    @ApiOperation(value = "退款单拒绝收货")
    @RequestMapping(value = "/receiving/reject", method = RequestMethod.POST)
    public CommonResult<String> receivingReject(@RequestBody @Validated RejectReceivingRequest request) {
        if (refundOrderEmployeeService.receivingReject(request)) {
            return CommonResult.success();
        }
        return CommonResult.failed();
    }
}



