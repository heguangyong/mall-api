package com.zbkj.admin.controller.merchant;

import com.zbkj.common.annotation.LogControllerAnnotation;
import com.zbkj.common.enums.MethodType;
import com.zbkj.common.model.admin.SystemAdmin;
import com.zbkj.common.page.CommonPage;
import com.zbkj.common.request.*;
import com.zbkj.common.response.SystemAdminResponse;
import com.zbkj.common.result.CommonResult;
import com.zbkj.service.service.SystemAdminService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;


/**
 * 后台管理员表 前端控制器
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
@RequestMapping("api/admin/merchant/admin")
@Api(tags = "商户端管理员服务")
public class MerchantAdminController {

    @Autowired
    private SystemAdminService systemAdminService;

    @PreAuthorize("hasAuthority('merchant:admin:list')")
    @ApiOperation(value = "管理员分页列表")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<CommonPage<SystemAdminResponse>> getList(@Validated SystemAdminRequest systemAdminRequest, PageParamRequest pageParamRequest) {
        CommonPage<SystemAdminResponse> systemAdminCommonPage = CommonPage.restPage(systemAdminService.getList(systemAdminRequest, pageParamRequest));
        return CommonResult.success(systemAdminCommonPage);
    }

    @PreAuthorize("hasAuthority('merchant:admin:save')")
    @LogControllerAnnotation(intoDB = true, methodType = MethodType.ADD, description = "新增后台管理员")
    @ApiOperation(value = "新增后台管理员")
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    public CommonResult<String> save(@RequestBody SystemAdminAddRequest systemAdminAddRequest) {
        if (systemAdminService.saveAdmin(systemAdminAddRequest)) {
            return CommonResult.success("添加管理员成功");
        }
        return CommonResult.failed("添加管理员失败");
    }

    @PreAuthorize("hasAuthority('merchant:admin:delete')")
    @LogControllerAnnotation(intoDB = true, methodType = MethodType.DELETE, description = "删除管理员")
    @ApiOperation(value = "删除管理员")
    @RequestMapping(value = "/delete", method = RequestMethod.GET)
    public CommonResult<String> delete(@RequestParam(value = "id") Integer id) {
        if (systemAdminService.removeAdmin(id)) {
            return CommonResult.success();
        }
        return CommonResult.failed();
    }

    @PreAuthorize("hasAuthority('merchant:admin:update')")
    @LogControllerAnnotation(intoDB = true, methodType = MethodType.UPDATE, description = "修改管理员")
    @ApiOperation(value = "修改管理员")
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public CommonResult<String> update(@RequestBody SystemAdminUpdateRequest systemAdminRequest) {
        if (systemAdminService.updateAdmin(systemAdminRequest)) {
            return CommonResult.success();
        }
        return CommonResult.failed();
    }

    @PreAuthorize("hasAuthority('merchant:admin:info')")
    @ApiOperation(value = "后台管理员详情")
    @RequestMapping(value = "/info", method = RequestMethod.GET)
    public CommonResult<SystemAdmin> info(@RequestParam(value = "id") @Valid Integer id) {
        return CommonResult.success(systemAdminService.getDetail(id));
    }

    @PreAuthorize("hasAuthority('merchant:admin:update:status')")
    @LogControllerAnnotation(intoDB = true, methodType = MethodType.UPDATE, description = "修改后台管理员状态")
    @ApiOperation(value = "修改后台管理员状态")
    @RequestMapping(value = "/updateStatus", method = RequestMethod.GET)
    public CommonResult<String> updateStatus(@RequestParam(value = "id") @Valid Integer id, @RequestParam(value = "status") @Valid Boolean status) {
        if (systemAdminService.updateStatus(id, status)) {
            return CommonResult.success("修改成功");
        }
        return CommonResult.failed("修改失败");
    }

    @LogControllerAnnotation(intoDB = true, methodType = MethodType.UPDATE, description = "修改后台管理员密码")
    @PreAuthorize("hasAuthority('merchant:admin:update:password')")
    @ApiOperation(value = "修改后台管理员密码")
    @RequestMapping(value = "/update/password", method = RequestMethod.POST)
    public CommonResult<Object> updatePassword(@RequestBody @Validated SystemAdminUpdatePwdRequest request) {
        if (systemAdminService.updatePassword(request)) {
            return CommonResult.success("修改成功");
        }
        return CommonResult.failed("修改失败");
    }

    @LogControllerAnnotation(intoDB = true, methodType = MethodType.UPDATE, description = "商户后台管理员短信开关")
    @PreAuthorize("hasAuthority('merchant:admin:update:receive:sms')")
    @ApiOperation(value = "商户后台管理员短信开关")
    @RequestMapping(value = "/update/receive/sms/{id}", method = RequestMethod.POST)
    public CommonResult<Object> updateReceiveSms(@PathVariable(name = "id") Integer id) {
        if (systemAdminService.updateReceiveSms(id)) {
            return CommonResult.success("修改成功");
        }
        return CommonResult.failed("修改失败");
    }
}



