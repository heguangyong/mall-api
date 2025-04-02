package com.zbkj.front.controller.employee;

import com.zbkj.common.model.admin.SystemAdmin;
import com.zbkj.common.model.merchant.MerchantElect;
import com.zbkj.common.result.CommonResult;
import com.zbkj.service.service.MerchantElectService;
import com.zbkj.service.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;


/**
 * 商户电子面单配置表 前端控制器
 */
@Slf4j
@RestController
@RequestMapping("api/front/employee/merchant/elect")
@Api(tags = "移动端商家管理 - 电子面单主信息配置") //配合swagger使用

public class EmployeeMerchantElectController {

    @Autowired
    private MerchantElectService merchantElectService;
    @Autowired
    private UserService userService;

    /**
     * 查询商户电子面单配置表信息
     * @author dazongzi
     * @since 2025-02-20
     */
    @ApiOperation(value = "详情")
    @RequestMapping(value = "/info", method = RequestMethod.GET)
    public CommonResult<MerchantElect> info() {
        SystemAdmin systemAdmin = userService.getSystemAdminByMerchantEmployee();
        MerchantElect merchantElect = merchantElectService.getMerchantElect(systemAdmin);
        return CommonResult.success(merchantElect);
    }
}



