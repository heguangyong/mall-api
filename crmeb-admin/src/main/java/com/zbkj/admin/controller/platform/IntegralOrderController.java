package com.zbkj.admin.controller.platform;

import com.zbkj.common.annotation.LogControllerAnnotation;
import com.zbkj.common.enums.MethodType;
import com.zbkj.common.model.admin.SystemAdmin;
import com.zbkj.common.page.CommonPage;
import com.zbkj.common.request.*;
import com.zbkj.common.response.IntegralOrderPageResponse;
import com.zbkj.common.response.OrderAdminDetailResponse;
import com.zbkj.common.response.OrderCountItemResponse;
import com.zbkj.common.response.OrderInvoiceResponse;
import com.zbkj.common.result.CommonResult;
import com.zbkj.common.utils.SecurityUtil;
import com.zbkj.common.vo.LogisticsResultVo;
import com.zbkj.service.service.OrderService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 积分订单控制器
 *
 * @author Han
 * @version 1.0.0
 * @Date 2024/8/26
 */
@Slf4j
@RestController
@RequestMapping("api/admin/platform/integral/order")
@Api(tags = "积分订单控制器")
public class IntegralOrderController {

    @Autowired
    private OrderService orderService;

    @PreAuthorize("hasAuthority('platform:integral:order:page:list')")
    @ApiOperation(value = "积分订单分页列表") //配合swagger使用
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public CommonResult<CommonPage<IntegralOrderPageResponse>> getList(@Validated IntegralOrderSearchRequest request) {
        return CommonResult.success(CommonPage.restPage(orderService.findIntegralOrderPageByPlat(request)));
    }

    @PreAuthorize("hasAuthority('platform:integral:order:status:num')")
    @ApiOperation(value = "积分订单各状态数量")
    @RequestMapping(value = "/status/num", method = RequestMethod.GET)
    public CommonResult<OrderCountItemResponse> getIntegralOrderStatusNum(@Validated IntegralOrderTabsHeaderRequest request) {
        return CommonResult.success(orderService.getIntegralOrderStatusNum(request));
    }

    @PreAuthorize("hasAuthority('platform:integral:order:mark')")
    @ApiOperation(value = "积分订单备注")
    @RequestMapping(value = "/mark", method = RequestMethod.POST)
    public CommonResult<String> mark(@RequestBody @Validated OrderRemarkRequest request) {
        if (orderService.integralOrderMark(request)) {
            return CommonResult.success();
        }
        return CommonResult.failed();
    }

    @PreAuthorize("hasAuthority('platform:integral:order:detail')")
    @ApiOperation(value = "积分订单详情")
    @RequestMapping(value = "/detail/{orderNo}", method = RequestMethod.GET)
    public CommonResult<OrderAdminDetailResponse> detail(@PathVariable(value = "orderNo") String orderNo) {
        return CommonResult.success(orderService.adminIntegralOrderDetail(orderNo));
    }

    @PreAuthorize("hasAuthority('platform:integral:order:send')")
    @ApiOperation(value = "积分订单发货")
    @RequestMapping(value = "/send", method = RequestMethod.POST)
    public CommonResult<Boolean> send(@RequestBody @Validated OrderSendRequest request) {
        SystemAdmin systemAdmin = SecurityUtil.getLoginUserVo().getUser();
        if (orderService.send(request, systemAdmin)) {
            return CommonResult.success();
        }
        return CommonResult.failed();
    }

    @PreAuthorize("hasAuthority('platform:integral:order:invoice:list')")
    @ApiOperation(value = "获取订单发货单列表")
    @RequestMapping(value = "/{orderNo}/invoice/list", method = RequestMethod.GET)
    public CommonResult<List<OrderInvoiceResponse>> getInvoiceList(@PathVariable(value = "orderNo") String orderNo) {
        SystemAdmin systemAdmin = SecurityUtil.getLoginUserVo().getUser();
        return CommonResult.success(orderService.getInvoiceListByMerchant(orderNo, systemAdmin));
    }

    @PreAuthorize("hasAuthority('platform:integral:order:logistics:info')")
    @ApiOperation(value = "订单物流详情")
    @RequestMapping(value = "/get/{invoiceId}/logistics/info", method = RequestMethod.GET)
    public CommonResult<LogisticsResultVo> getLogisticsInfo(@PathVariable(value = "invoiceId") Integer invoiceId) {
        SystemAdmin systemAdmin = SecurityUtil.getLoginUserVo().getUser();
        return CommonResult.success(orderService.getLogisticsInfoByMerchant(invoiceId, systemAdmin));
    }

    @PreAuthorize("hasAuthority('platform:integral:order:invoice:update')")
    @ApiOperation(value = "修改发货单配送信息")
    @RequestMapping(value = "/invoice/update", method = RequestMethod.POST)
    public CommonResult<Object> updateInvoice(@RequestBody @Validated OrderInvoiceUpdateRequest request) {
        SystemAdmin systemAdmin = SecurityUtil.getLoginUserVo().getUser();
        if (orderService.updateInvoice(request, systemAdmin)) {
            return CommonResult.success().setMessage("修改发货单配送信息成功");
        }
        return CommonResult.failed().setMessage("修改发货单配送信息失败");
    }
}
