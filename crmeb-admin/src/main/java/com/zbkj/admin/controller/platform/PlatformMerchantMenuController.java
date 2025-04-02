package com.zbkj.admin.controller.platform;

import com.zbkj.common.annotation.LogControllerAnnotation;
import com.zbkj.common.enums.MethodType;
import com.zbkj.common.model.admin.SystemMenu;
import com.zbkj.common.request.SystemMenuRequest;
import com.zbkj.common.request.SystemMenuSearchRequest;
import com.zbkj.common.result.CommonResult;
import com.zbkj.service.service.SystemMenuService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;


/**
 * 平台端商户菜单控制器
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
@RequestMapping("api/admin/platform/merchant/menu")
@Api(tags = "平台端商户菜单控制器")
public class PlatformMerchantMenuController {

    @Autowired
    private SystemMenuService systemMenuService;

    @PreAuthorize("hasAuthority('platform:merchant:menu:list')")
    @ApiOperation(value = "菜单列表")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public CommonResult<List<SystemMenu>> getList(@Validated SystemMenuSearchRequest request) {
        return CommonResult.success(systemMenuService.getMerchantList(request));
    }

    @PreAuthorize("hasAuthority('platform:merchant:menu:add')")
    @LogControllerAnnotation(intoDB = true, methodType = MethodType.ADD, description = "新增商户菜单")
    @ApiOperation(value = "新增菜单")
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public CommonResult<String> add(@RequestBody @Validated SystemMenuRequest systemMenuRequest) {
        if (systemMenuService.addMerchantMenu(systemMenuRequest)) {
            return CommonResult.success();
        }
        return CommonResult.failed();
    }

    @PreAuthorize("hasAuthority('platform:merchant:menu:delete')")
    @LogControllerAnnotation(intoDB = true, methodType = MethodType.UPDATE, description = "删除商户菜单")
    @ApiOperation(value = "删除菜单")
    @RequestMapping(value = "/delete/{id}", method = RequestMethod.POST)
    public CommonResult<String> delete(@PathVariable(value = "id") Integer id) {
        if (systemMenuService.deleteMerchantMenu(id)) {
            return CommonResult.success();
        }
        return CommonResult.failed();
    }

    @PreAuthorize("hasAuthority('platform:merchant:menu:update')")
    @LogControllerAnnotation(intoDB = true, methodType = MethodType.UPDATE, description = "修改商户菜单")
    @ApiOperation(value = "修改菜单")
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public CommonResult<String> update(@RequestBody @Validated SystemMenuRequest systemMenuRequest) {
        if (systemMenuService.editMerchantMenu(systemMenuRequest)) {
            return CommonResult.success();
        }
        return CommonResult.failed();
    }

    @PreAuthorize("hasAuthority('platform:merchant:menu:info')")
    @ApiOperation(value = "菜单详情")
    @RequestMapping(value = "/info/{id}", method = RequestMethod.GET)
    public CommonResult<SystemMenu> info(@PathVariable(value = "id") Integer id) {
        return CommonResult.success(systemMenuService.getInfo(id));
   }

    @PreAuthorize("hasAuthority('platform:merchant:menu:show:status')")
    @ApiOperation(value = "修改菜单显示状态")
    @RequestMapping(value = "/update/show/{id}", method = RequestMethod.POST)
    public CommonResult<Object> updateShowStatus(@PathVariable(value = "id") Integer id) {
        if (systemMenuService.updateMerchantShowStatus(id)) {
            return CommonResult.success("修改成功");
        }
        return CommonResult.failed("修改失败");
    }
}



