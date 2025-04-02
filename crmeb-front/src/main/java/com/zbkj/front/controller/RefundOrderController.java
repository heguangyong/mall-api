package com.zbkj.front.controller;

import com.zbkj.common.model.order.OrderDetail;
import com.zbkj.common.page.CommonPage;
import com.zbkj.common.request.CommonSearchRequest;
import com.zbkj.common.request.OrderAfterSalesSearchRequest;
import com.zbkj.common.request.OrderRefundApplyRequest;
import com.zbkj.common.request.OrderRefundReturningGoodsRequest;
import com.zbkj.common.response.RefundOrderInfoResponse;
import com.zbkj.common.response.RefundOrderResponse;
import com.zbkj.common.result.CommonResult;
import com.zbkj.front.service.FrontOrderService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * H5端订单操作
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
@RequestMapping("api/front/refund")
@Api(tags = "售后订单控制器")
public class RefundOrderController {

    @Autowired
    private FrontOrderService orderService;

    @ApiOperation(value = "售后申请列表(可申请售后列表)")
    @RequestMapping(value = "/after/sale/apply/list", method = RequestMethod.GET)
    public CommonResult<CommonPage<OrderDetail>> afterSaleApplyList(@ModelAttribute CommonSearchRequest request) {
        return CommonResult.success(CommonPage.restPage(orderService.getAfterSaleApplyList(request)));
    }

    @ApiOperation(value = "订单退款申请")
    @RequestMapping(value = "/apply", method = RequestMethod.POST)
    public CommonResult<Boolean> refundApply(@RequestBody @Validated OrderRefundApplyRequest request) {
        if(orderService.refundApply(request)) {
            return CommonResult.success();
        }
        return CommonResult.failed();
    }

    @ApiOperation(value = "订单退款理由（平台提供）")
    @RequestMapping(value = "/reason", method = RequestMethod.GET)
    public CommonResult<List<String>> refundReason() {
        return CommonResult.success(orderService.getRefundReason());
    }

    @ApiOperation(value = "退款订单详情")
    @RequestMapping(value = "/detail/{refundOrderNo}", method = RequestMethod.GET)
    public CommonResult<RefundOrderInfoResponse> refundOrderDetail(@PathVariable String refundOrderNo) {
        return CommonResult.success(orderService.refundOrderDetail(refundOrderNo));
    }

    @ApiOperation(value = "退款订单列表")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public CommonResult<CommonPage<RefundOrderResponse>> refundOrderList(@ModelAttribute @Validated OrderAfterSalesSearchRequest request) {
        return CommonResult.success(CommonPage.restPage(orderService.getRefundOrderList(request)));
    }

    @ApiOperation(value = "退款单退回商品")
    @RequestMapping(value = "/returning/goods", method = RequestMethod.POST)
    public CommonResult<Boolean> returningGoods(@RequestBody @Validated OrderRefundReturningGoodsRequest request) {
        if(orderService.returningGoods(request)) {
            return CommonResult.success();
        }
        return CommonResult.failed();
    }

    @ApiOperation(value = "撤销退款单")
    @RequestMapping(value = "/revoke/{refundOrderNo}", method = RequestMethod.POST)
    public CommonResult<Boolean> revoke(@PathVariable String refundOrderNo) {
        if(orderService.revoke(refundOrderNo)) {
            return CommonResult.success();
        }
        return CommonResult.failed();
    }
}
