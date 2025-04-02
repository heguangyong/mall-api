package com.zbkj.front.controller.employee;

import com.zbkj.common.page.CommonPage;
import com.zbkj.common.request.MerchantDeliveryPersonnelSearchRequest;
import com.zbkj.common.response.MerchantDeliveryPersonnelPageResponse;
import com.zbkj.common.result.CommonResult;
import com.zbkj.front.service.EmployeeMerchantDeliveryPersonnelService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * 移动端商家管理 - 配送人员控制器
 *
 * @author Han
 * @version 1.0.0
 * @Date 2024/11/12
 */
@Slf4j
@RestController
@RequestMapping("api/front/employee/merchant/delivery/personnel")
@Api(tags = "移动端商家管理 - 配送人员控制器") //配合swagger使用
public class EmployeeMerchantDeliveryPersonnelController {

    @Autowired
    private EmployeeMerchantDeliveryPersonnelService merchantDeliveryPersonnelService;

    @ApiOperation(value = "商户配送人员分页列表")
    @RequestMapping(value = "/page", method = RequestMethod.GET)
    public CommonResult<CommonPage<MerchantDeliveryPersonnelPageResponse>> findPage(@Validated MerchantDeliveryPersonnelSearchRequest request) {
        return CommonResult.success(CommonPage.restPage(merchantDeliveryPersonnelService.findPage(request)));
    }

}
