package com.zbkj.admin.controller.merchant;

import com.zbkj.common.annotation.LogControllerAnnotation;
import com.zbkj.common.enums.MethodType;
import com.zbkj.common.model.system.SystemForm;
import com.zbkj.common.page.CommonPage;
import com.zbkj.common.request.PageParamRequest;
import com.zbkj.common.request.SystemFormAddRequest;
import com.zbkj.common.result.CommonResult;
import com.zbkj.service.service.SystemFormService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 商户端系统表单控制器
 *
 * @author Han
 * @version 1.0.0
 * @Date 2024/8/5
 */
@Slf4j
@RestController
@RequestMapping("api/admin/merchant/system/form")
@Api(tags = "系统表单控制器")
public class MerchantSystemFormController {

    @Autowired
    private SystemFormService service;

    @PreAuthorize("hasAuthority('merchant:system:form:page')")
    @ApiOperation(value = "系统表单分页列表")
    @RequestMapping(value = "/page", method = RequestMethod.GET)
    public CommonResult<CommonPage<SystemForm>> getMerchantPage(@Validated PageParamRequest pageRequest) {
        return CommonResult.success(CommonPage.restPage(service.getMerchantPage(pageRequest)));
    }

    @PreAuthorize("hasAuthority('merchant:system:form:detail')")
    @ApiOperation(value = "系统表单详情")
    @RequestMapping(value = "/detail/{id}", method = RequestMethod.GET)
    public CommonResult<SystemForm> activityDetail(@PathVariable("id") Integer id) {
        return CommonResult.success(service.getDetail(id));
    }

    @LogControllerAnnotation(intoDB = true, methodType = MethodType.ADD, description = "添加系统表单")
    @PreAuthorize("hasAuthority('merchant:system:form:add')")
    @ApiOperation(value = "添加系统表单")
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public CommonResult<Map<String, Object>> addForm(@RequestBody @Validated SystemFormAddRequest request) {
        return CommonResult.success(service.addForm(request));
    }

    @LogControllerAnnotation(intoDB = true, methodType = MethodType.UPDATE, description = "修改系统表单")
    @PreAuthorize("hasAuthority('merchant:system:form:update')")
    @ApiOperation(value = "修改系统表单")
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public CommonResult<String> updateForm(@RequestBody @Validated SystemFormAddRequest request) {
        if (service.updateForm(request)) {
            return CommonResult.success();
        }
        return CommonResult.failed();
    }

    @LogControllerAnnotation(intoDB = true, methodType = MethodType.DELETE, description = "删除系统表单")
    @PreAuthorize("hasAuthority('merchant:system:form:delete')")
    @ApiOperation(value = "删除系统表单")
    @RequestMapping(value = "/delete/{id}", method = RequestMethod.POST)
    public CommonResult<String> deleteForm(@PathVariable(name = "id") Integer id) {
        if (service.deleteForm(id)) {
            return CommonResult.success();
        }
        return CommonResult.failed();
    }
}
