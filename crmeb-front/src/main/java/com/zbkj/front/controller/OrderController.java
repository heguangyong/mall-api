package com.zbkj.front.controller;

import com.zbkj.common.page.CommonPage;
import com.zbkj.common.request.*;
import com.zbkj.common.response.*;
import com.zbkj.common.result.CommonResult;
import com.zbkj.common.vo.LogisticsResultVo;
import com.zbkj.front.service.FrontOrderService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
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
@RequestMapping("api/front/order")
@Api(tags = "订单")
public class OrderController {

    @Autowired
    private FrontOrderService orderService;

    @ApiOperation(value = "预下单")
    @RequestMapping(value = "/pre/order", method = RequestMethod.POST)
    public CommonResult<OrderNoResponse> preOrder(@RequestBody @Validated PreOrderRequest request) {
        return CommonResult.success(orderService.preOrder_V1_7(request));
    }

    @ApiOperation(value = "加载预下单")
    @RequestMapping(value = "load/pre/{preOrderNo}", method = RequestMethod.GET)
    public CommonResult<PreOrderResponse> loadPreOrder(@PathVariable String preOrderNo) {
        return CommonResult.success(orderService.loadPreOrder(preOrderNo));
    }

    @ApiOperation(value = "计算订单价格")
    @RequestMapping(value = "/computed/price", method = RequestMethod.POST)
    public CommonResult<ComputedOrderPriceResponse> computedPrice(@Validated @RequestBody OrderComputedPriceRequest request) {
        return CommonResult.success(orderService.computedOrderPrice(request));
    }

    @ApiOperation(value = "创建订单")
    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public CommonResult<OrderNoResponse> createOrder(@Validated @RequestBody CreateOrderRequest orderRequest) {
        return CommonResult.success(orderService.createOrder(orderRequest));
    }

    @ApiOperation(value = "订单列表")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public CommonResult<CommonPage<OrderFrontDataResponse>> orderList(@ModelAttribute @Validated OrderFrontListRequest request) {
        return CommonResult.success(CommonPage.restPage(orderService.list_v1_4(request)));
    }

    @ApiOperation(value = "订单详情")
    @RequestMapping(value = "/detail/{orderNo}", method = RequestMethod.GET)
    public CommonResult<OrderFrontDetailResponse> orderDetail(@PathVariable String orderNo) {
        return CommonResult.success(orderService.frontDetail(orderNo));
    }

    @ApiOperation(value = "订单取消")
    @RequestMapping(value = "/cancel/{orderNo}", method = RequestMethod.POST)
    public CommonResult<Boolean> cancel(@PathVariable(value = "orderNo") String orderNo) {
        if (orderService.cancel(orderNo)) {
            return CommonResult.success();
        }
        return CommonResult.failed();
    }

    @ApiOperation(value = "订单商品评论列表")
    @RequestMapping(value = "/reply/list", method = RequestMethod.GET)
    public CommonResult<CommonPage<InfoReplyResponse>> replyList(@ModelAttribute PageParamRequest pageRequest) {
        return CommonResult.success(CommonPage.restPage(orderService.replyList(pageRequest)));
    }

    @ApiOperation(value = "评价订单商品")
    @RequestMapping(value = "/reply/product", method = RequestMethod.POST)
    public CommonResult<Boolean> replyProduct(@RequestBody @Validated OrderProductReplyRequest request) {
        if (orderService.replyProduct(request)) {
            return CommonResult.success();
        }
        return CommonResult.failed();
    }

    @ApiOperation(value = "删除订单")
    @RequestMapping(value = "/delete/{orderNo}", method = RequestMethod.POST)
    public CommonResult<Boolean> delete(@PathVariable(value = "orderNo") String orderNo) {
        if (orderService.delete(orderNo)) {
            return CommonResult.success();
        }
        return CommonResult.failed();
    }

    @ApiOperation(value = "订单收货")
    @RequestMapping(value = "/take/delivery/{orderNo}", method = RequestMethod.POST)
    public CommonResult<String> take(@PathVariable(value = "orderNo") String orderNo) {
        if (orderService.takeDelivery(orderNo)) {
            return CommonResult.success("订单收货成功");
        }
        return CommonResult.failed("订单收货失败");
    }

    @ApiOperation(value = "获取订单发货单列表")
    @RequestMapping(value = "/{orderNo}/invoice/list", method = RequestMethod.GET)
    public CommonResult<OrderInvoiceFrontResponse> getInvoiceList(@PathVariable(value = "orderNo") String orderNo) {
        return CommonResult.success(orderService.getInvoiceList(orderNo));
    }

    @ApiOperation(value = "物流信息查询")
    @RequestMapping(value = "/logistics/{invoiceId}", method = RequestMethod.GET)
    public CommonResult<LogisticsResultVo> getLogisticsInfo(@PathVariable(value = "invoiceId") Integer invoiceId) {
        return CommonResult.success(orderService.getLogisticsInfo(invoiceId));
    }

    @ApiOperation(value = "获取订单状态图")
    @RequestMapping(value = "/status/image", method = RequestMethod.GET)
    public CommonResult<List<HashMap<String, Object>>> getOrderStatusImage() {
        return CommonResult.success(orderService.getOrderStatusImage());
    }
}
