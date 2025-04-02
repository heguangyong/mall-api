package com.zbkj.front.controller.employee;

import com.zbkj.common.response.employee.EmployeeMerchantActiveResponse;
import com.zbkj.common.response.employee.FrontMerchantEmployeeResponse;
import com.zbkj.common.result.CommonResult;
import com.zbkj.service.service.MerchantEmployeeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @Auther: 大粽子
 * @Date: 2024/5/29 11:14
 * @Description: 描述对应的业务场景
 */
@Slf4j
@RestController
@RequestMapping("api/front/employee/merchant")
@Api(tags = "移动端商家管理 - 商户基础操作") //配合swagger使用
public class EmployeeMerchantController {

    @Autowired
    private MerchantEmployeeService merchantEmployeeService;

    @ApiOperation(value = "激活当前正在操作的商户")
    @RequestMapping(value = "/active/{id}", method = RequestMethod.GET)
    public CommonResult<EmployeeMerchantActiveResponse> activeMerchant(@PathVariable(value = "id") Integer id) {
        return CommonResult.success(merchantEmployeeService.activeMerchant(id));
    }

    @ApiOperation(value = "获取归属商户列表")
    @RequestMapping(value = "/belong/List", method = RequestMethod.GET)
    public CommonResult<List<FrontMerchantEmployeeResponse>> findBelongList() {
        return CommonResult.success(merchantEmployeeService.findBelongList());
    }
}
