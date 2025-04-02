package com.zbkj.admin.controller.platform;

import com.zbkj.common.model.order.RechargeOrder;
import com.zbkj.common.page.CommonPage;
import com.zbkj.common.request.RechargeOrderSearchRequest;
import com.zbkj.common.result.CommonResult;
import com.zbkj.service.service.RechargeOrderService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;


/**
 * 充值订单控制器
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
@RequestMapping("api/admin/platform/recharge/order")
@Api(tags = "充值订单控制器")
public class RechargeOrderController {

    @Autowired
    private RechargeOrderService rechargeOrderService;

    @PreAuthorize("hasAuthority('platform:recharge:order:list')")
    @ApiOperation(value = "充值订单分页列表")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public CommonResult<CommonPage<RechargeOrder>> getList(@Validated RechargeOrderSearchRequest request) {
        return CommonResult.success(CommonPage.restPage(rechargeOrderService.getAdminPage(request)));
    }

}



