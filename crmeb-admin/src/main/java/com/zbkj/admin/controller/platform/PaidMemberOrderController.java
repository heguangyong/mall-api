package com.zbkj.admin.controller.platform;

import com.zbkj.common.page.CommonPage;
import com.zbkj.common.request.PaidMemberOrderSearchRequest;
import com.zbkj.common.response.PaidMemberOrderInfoResponse;
import com.zbkj.common.response.PaidMemberOrderResponse;
import com.zbkj.common.result.CommonResult;
import com.zbkj.service.service.PaidMemberOrderService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * 付费会员控制器
 *
 * @author Han
 * @version 1.0.0
 * @Date 2024/5/10
 */
@Slf4j
@RestController
@RequestMapping("api/admin/platform/paid/member/order")
@Api(tags = "付费会员订单控制器")
public class PaidMemberOrderController {

    @Autowired
    private PaidMemberOrderService paidMemberOrderService;

    @PreAuthorize("hasAuthority('platform:paid:member:order:page:list')")
    @ApiOperation(value = "付费会员订单分页列表") //配合swagger使用
    @RequestMapping(value = "/page/list", method = RequestMethod.GET)
    public CommonResult<CommonPage<PaidMemberOrderResponse>> getPage(@Validated PaidMemberOrderSearchRequest request) {
        return CommonResult.success(CommonPage.restPage(paidMemberOrderService.findPlatformPage(request)));
    }

    @PreAuthorize("hasAuthority('platform:paid:member:order:info')")
    @ApiOperation(value = "付费会员订单详情") //配合swagger使用
    @RequestMapping(value = "/info/{orderNo}", method = RequestMethod.GET)
    public CommonResult<PaidMemberOrderInfoResponse> getInfo(@PathVariable(name = "orderNo") String orderNo) {
        return CommonResult.success(paidMemberOrderService.platformInfo(orderNo));
    }
}
