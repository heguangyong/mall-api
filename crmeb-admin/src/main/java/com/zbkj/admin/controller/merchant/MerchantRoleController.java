package com.zbkj.admin.controller.merchant;

import com.zbkj.common.annotation.LogControllerAnnotation;
import com.zbkj.common.enums.MethodType;
import com.zbkj.common.model.admin.SystemRole;
import com.zbkj.common.page.CommonPage;
import com.zbkj.common.request.PageParamRequest;
import com.zbkj.common.request.SystemRoleRequest;
import com.zbkj.common.request.SystemRoleSearchRequest;
import com.zbkj.common.request.SystemRoleStatusRequest;
import com.zbkj.common.response.RoleInfoResponse;
import com.zbkj.common.result.CommonResult;
import com.zbkj.service.service.SystemRoleService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


/**
 * 商户端 管理员角色控制器
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
@RequestMapping("api/admin/merchant/role")
@Api(tags = "商户端 管理员角色控制器")
public class MerchantRoleController {

    @Autowired
    private SystemRoleService systemRoleService;

    @PreAuthorize("hasAuthority('merchant:admin:role:list')")
    @ApiOperation(value = "角色分页列表")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public CommonResult<CommonPage<SystemRole>> getList(@Validated SystemRoleSearchRequest request, @Validated PageParamRequest pageParamRequest) {
        CommonPage<SystemRole> systemRoleCommonPage = CommonPage.restPage(systemRoleService.getList(request, pageParamRequest));
        return CommonResult.success(systemRoleCommonPage);
    }

    @LogControllerAnnotation(intoDB = true, methodType = MethodType.ADD, description = "新增角色")
    @PreAuthorize("hasAuthority('merchant:admin:role:save')")
    @ApiOperation(value = "新增角色")
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    public CommonResult<String> save(@RequestBody @Validated SystemRoleRequest systemRoleRequest) {
        if (systemRoleService.add(systemRoleRequest)) {
            return CommonResult.success();
        }
        return CommonResult.failed();
    }

    @LogControllerAnnotation(intoDB = true, methodType = MethodType.DELETE, description = "删除角色")
    @PreAuthorize("hasAuthority('merchant:admin:role:delete')")
    @ApiOperation(value = "删除角色")
    @RequestMapping(value = "/delete/{id}", method = RequestMethod.POST)
    public CommonResult<String> delete(@PathVariable(value = "id") Integer id) {
        if (systemRoleService.delete(id)) {
            return CommonResult.success();
        }
        return CommonResult.failed();
    }

    @LogControllerAnnotation(intoDB = true, methodType = MethodType.UPDATE, description = "修改角色")
    @PreAuthorize("hasAuthority('merchant:admin:role:update')")
    @ApiOperation(value = "修改角色")
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public CommonResult<String> update(@RequestBody @Validated SystemRoleRequest systemRoleRequest) {
        if (systemRoleService.edit(systemRoleRequest)) {
            return CommonResult.success();
        }
        return CommonResult.failed();
    }

    @PreAuthorize("hasAuthority('merchant:admin:role:info')")
    @ApiOperation(value = "角色详情")
    @RequestMapping(value = "/info/{id}", method = RequestMethod.GET)
    public CommonResult<RoleInfoResponse> info(@PathVariable Integer id) {
        return CommonResult.success(systemRoleService.getInfo(id));
    }

    @LogControllerAnnotation(intoDB = true, methodType = MethodType.UPDATE, description = "修改角色状态")
    @PreAuthorize("hasAuthority('merchant:admin:role:update:status')")
    @ApiOperation(value = "修改角色状态")
    @RequestMapping(value = "/updateStatus", method = RequestMethod.POST)
    public CommonResult<Object> updateStatus(@Validated @RequestBody SystemRoleStatusRequest request) {
        if (systemRoleService.updateStatus(request)) {
            return CommonResult.success("修改成功");
        }
        return CommonResult.failed("修改失败");
    }
}



