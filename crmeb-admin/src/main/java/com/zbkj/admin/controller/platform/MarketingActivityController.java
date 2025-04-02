package com.zbkj.admin.controller.platform;

import com.zbkj.admin.service.MarketingActivityService;
import com.zbkj.common.annotation.LogControllerAnnotation;
import com.zbkj.common.enums.MethodType;
import com.zbkj.common.request.BirthdayPresentRequest;
import com.zbkj.common.request.NewPeoplePresentRequest;
import com.zbkj.common.response.BirthdayPresentResponse;
import com.zbkj.common.response.NewPeoplePresentResponse;
import com.zbkj.common.result.CommonResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * 营销活动控制器
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
@RequestMapping("api/admin/platform/marketing/activity")
@Api(tags = "营销活动控制器")
public class MarketingActivityController {

    @Autowired
    private MarketingActivityService marketingActivityService;

    @PreAuthorize("hasAuthority('platform:marketing:activity:birthday:present:config')")
    @ApiOperation(value="获取生日有礼配置")
    @RequestMapping(value = "/birthday/present/config", method = RequestMethod.GET)
    public BirthdayPresentResponse getBirthdayPresentConfig() {
        return marketingActivityService.getBirthdayPresentConfig();
    }

    @LogControllerAnnotation(intoDB = true, methodType = MethodType.UPDATE, description = "编辑生日有礼配置")
    @PreAuthorize("hasAuthority('platform:marketing:activity:birthday:present:edit')")
    @ApiOperation(value="编辑生日有礼配置")
    @RequestMapping(value = "/birthday/present/edit", method = RequestMethod.POST)
    public CommonResult<String> editBirthdayPresent(@RequestBody @Validated BirthdayPresentRequest request) {
        if (marketingActivityService.editBirthdayPresent(request)) {
            return CommonResult.success("编辑生日有礼配置成功");
        }
        return CommonResult.failed("编辑生日有礼配置失败");
    }

    @PreAuthorize("hasAuthority('platform:marketing:activity:new:people:present:config')")
    @ApiOperation(value="获取新人礼配置")
    @RequestMapping(value = "/new/people/present/config", method = RequestMethod.GET)
    public NewPeoplePresentResponse getNewPeopleConfig() {
        return marketingActivityService.getNewPeopleConfig();
    }

    @LogControllerAnnotation(intoDB = true, methodType = MethodType.UPDATE, description = "编辑生日有礼配置")
    @PreAuthorize("hasAuthority('platform:marketing:activity:new:people:present:edit')")
    @ApiOperation(value="编辑新人礼配置")
    @RequestMapping(value = "/new/people/present/edit", method = RequestMethod.POST)
    public CommonResult<String> editNewPeopleConfig(@RequestBody @Validated NewPeoplePresentRequest request) {
        if (marketingActivityService.editNewPeopleConfig(request)) {
            return CommonResult.success("编辑新人礼配置成功");
        }
        return CommonResult.failed("编辑新人礼配置失败");
    }

}
