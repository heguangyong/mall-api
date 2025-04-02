package com.zbkj.admin.controller.platform;

import com.zbkj.common.model.system.SystemConfig;
import com.zbkj.common.request.SaveConfigRequest;
import com.zbkj.common.request.SystemConfigAdminRequest;
import com.zbkj.common.request.SystemFormCheckRequest;
import com.zbkj.common.result.CommonResult;
import com.zbkj.service.service.SystemConfigService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;


/**
 * 配置表 前端控制器
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
@RequestMapping("api/admin/platform/system/config")
@Api(tags = "平台端设置控制器")
public class SystemConfigController {

    @Autowired
    private SystemConfigService systemConfigService;

    /**
     * 查询配置表信息
     * @param formId Integer
     */
    @PreAuthorize("hasAuthority('platform:system:config:info')")
    @ApiOperation(value = "详情")
    @RequestMapping(value = "/info", method = RequestMethod.GET)
    public CommonResult<HashMap<String, String>> info(@RequestParam(value = "formId") Integer formId) {
        return CommonResult.success(systemConfigService.info(formId));
    }


    /**
     * 整体保存表单数据
     * @param systemFormCheckRequest SystemFormCheckRequest 新增参数
     */
    @PreAuthorize("hasAuthority('platform:system:config:save:form')")
    @ApiOperation(value = "整体保存表单数据")
    @RequestMapping(value = "/save/form", method = RequestMethod.POST)
    public CommonResult<String> saveFrom(@RequestBody @Validated SystemFormCheckRequest systemFormCheckRequest) {
        if (systemConfigService.saveForm(systemFormCheckRequest)) {
            return CommonResult.success();
        }
        return CommonResult.failed();
    }

    /**
     * 检测表单name是否存在
     * @param name name
     */
    @PreAuthorize("hasAuthority('platform:system:config:check')")
    @ApiOperation(value = "检测表单name是否存在")
    @RequestMapping(value = "/check", method = RequestMethod.GET)
    public CommonResult<Boolean> check(@RequestParam String name) {
        return CommonResult.success(systemConfigService.checkName(name));
    }

    /**
     * 更新配置信息
     */
    @PreAuthorize("hasAuthority('platform:system:config:update')")
    @ApiOperation(value = "更新配置信息")
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public CommonResult<List<SystemConfig>> getByKey(@RequestBody @Validated List<SystemConfigAdminRequest> requestList) {
        if (systemConfigService.updateByList(requestList)) {
            return CommonResult.success();
        }
        return CommonResult.failed();
    }

    @PreAuthorize("hasAuthority('platform:system:config:change:color:get')")
    @ApiOperation(value = "获取主题色")
    @RequestMapping(value = "/get/change/color", method = RequestMethod.GET)
    public CommonResult<SystemConfig> getChangeColor() {
        return CommonResult.success(systemConfigService.getChangeColor());
    }

    @PreAuthorize("hasAuthority('platform:system:config:change:color:save')")
    @ApiOperation(value = "保存主题色")
    @RequestMapping(value = "/save/change/color", method = RequestMethod.POST)
    public CommonResult<String> saveChangeColor(@RequestBody SaveConfigRequest request) {
        if (systemConfigService.saveChangeColor(request)) {
            return CommonResult.success("保存成功");
        }
        return CommonResult.failed("保存失败");
    }

    @PreAuthorize("hasAuthority('platform:system:config:seckill:style:get')")
    @ApiOperation(value = "获取秒杀样式")
    @RequestMapping(value = "/get/seckill/style", method = RequestMethod.GET)
    public CommonResult<SystemConfig> getSeckillStyle() {
        return CommonResult.success(systemConfigService.getSeckillStyle());
    }

    @PreAuthorize("hasAuthority('platform:system:config:seckill:style:save')")
    @ApiOperation(value = "保存秒杀样式")
    @RequestMapping(value = "/save/seckill/style", method = RequestMethod.POST)
    public CommonResult<String> saveSeckillStyle(@RequestBody SaveConfigRequest request) {
        if (systemConfigService.saveSeckillStyle(request)) {
            return CommonResult.success("保存成功");
        }
        return CommonResult.failed("保存失败");
    }
}



