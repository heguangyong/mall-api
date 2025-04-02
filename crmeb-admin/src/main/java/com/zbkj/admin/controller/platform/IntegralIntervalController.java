package com.zbkj.admin.controller.platform;

import com.zbkj.common.annotation.LogControllerAnnotation;
import com.zbkj.common.enums.MethodType;
import com.zbkj.common.model.system.GroupConfig;
import com.zbkj.common.page.CommonPage;
import com.zbkj.common.request.IntegralIntervalAddRequest;
import com.zbkj.common.request.IntegralIntervalPageSearchRequest;
import com.zbkj.common.request.IntegralProductAddRequest;
import com.zbkj.common.result.CommonResult;
import com.zbkj.service.service.GroupConfigService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 积分区间控制器
 *
 * @author Han
 * @version 1.0.0
 * @Date 2024/8/19
 */
@Slf4j
@RestController
@RequestMapping("api/admin/platform/integral/interval")
@Api(tags = "积分区间控制器")
public class IntegralIntervalController {

    @Autowired
    private GroupConfigService groupConfigService;

    @PreAuthorize("hasAuthority('platform:integral:interval:page')")
    @ApiOperation(value = "积分区间分页列表") //配合swagger使用
    @RequestMapping(value = "/page", method = RequestMethod.GET)
    public CommonResult<CommonPage<GroupConfig>> getList(@Validated IntegralIntervalPageSearchRequest request) {
        return CommonResult.success(CommonPage.restPage(groupConfigService.getIntegralIntervalPage(request)));
    }

    @PreAuthorize("hasAuthority('platform:integral:interval:save')")
    @ApiOperation(value = "新增积分区间")
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    public CommonResult<String> save(@RequestBody @Validated IntegralIntervalAddRequest request) {
        if (groupConfigService.saveIntegralInterval(request)) {
            return CommonResult.success();
        }
        return CommonResult.failed();
    }

    @PreAuthorize("hasAuthority('platform:integral:interval:update')")
    @ApiOperation(value = "修改积分区间")
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public CommonResult<String> update(@RequestBody @Validated IntegralIntervalAddRequest request) {
        if (groupConfigService.updateIntegralInterval(request)) {
            return CommonResult.success();
        }
        return CommonResult.failed();
    }

    @PreAuthorize("hasAuthority('platform:integral:interval:delete')")
    @ApiOperation(value = "删除积分区间")
    @RequestMapping(value = "/delete/{id}", method = RequestMethod.POST)
    public CommonResult<String> delete(@PathVariable Integer id) {
        if (groupConfigService.deleteIntegralInterval(id)) {
            return CommonResult.success();
        }
        return CommonResult.failed();
    }

    @PreAuthorize("hasAuthority('platform:integral:interval:update:status')")
    @ApiOperation(value = "修改积分区间状态")
    @RequestMapping(value = "/update/status/{id}", method = RequestMethod.POST)
    public CommonResult<String> updateStatus(@PathVariable Integer id) {
        if (groupConfigService.updateIntegralIntervalStatus(id)) {
            return CommonResult.success();
        }
        return CommonResult.failed();
    }
}
