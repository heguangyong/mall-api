package com.zbkj.admin.controller.platform;

import com.zbkj.admin.service.CommunityService;
import com.zbkj.common.result.CommonResult;
import com.zbkj.common.vo.CommunityConfigVo;
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
 * @ClassName CommunityCategoryController
 * @Description 社区控制器
 * @Author HZW
 * @Date 2023/3/7 11:32
 * @Version 1.0
 */
@Slf4j
@RestController
@RequestMapping("api/admin/platform/community/category")
@Api(tags = "社区管理")
public class CommunityController {

    @Autowired
    private CommunityService communityService;

    @PreAuthorize("hasAuthority('platform:community:get:config')")
    @ApiOperation(value = "获取社区配置")
    @RequestMapping(value = "/get/config", method = RequestMethod.GET)
    public CommunityConfigVo getConfig() {
        return communityService.getConfig();
    }

    @PreAuthorize("hasAuthority('platform:community:update:config')")
    @ApiOperation(value = "更新社区配置")
    @RequestMapping(value = "/update/config", method = RequestMethod.POST)
    public CommonResult<Object> updateConfig(@RequestBody @Validated CommunityConfigVo request) {
        communityService.updateConfig(request);
        return CommonResult.success();
    }

}
