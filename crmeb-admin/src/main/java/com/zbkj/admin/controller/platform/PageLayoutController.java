package com.zbkj.admin.controller.platform;

import com.alibaba.fastjson.JSONObject;
import com.zbkj.common.response.PageLayoutBottomNavigationResponse;
import com.zbkj.common.response.PageLayoutIndexResponse;
import com.zbkj.common.result.CommonResult;
import com.zbkj.common.vo.SplashAdConfigVo;
import com.zbkj.service.service.PageLayoutService;
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
 * 页面设计 前端控制器
 *  +----------------------------------------------------------------------
 *  | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
 *  +----------------------------------------------------------------------
 *  | Copyright (c) 2016~2025 https://www.crmeb.com All rights reserved.
 *  +----------------------------------------------------------------------
 *  | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
 *  +----------------------------------------------------------------------
 *  | Author: CRMEB Team <admin@crmeb.com>
 *  +----------------------------------------------------------------------
 */
@Slf4j
@RestController
@RequestMapping("api/admin/platform/page/layout")
@Api(tags = "页面布局管理")
public class PageLayoutController {

    @Autowired
    private PageLayoutService pageLayoutService;

    @PreAuthorize("hasAuthority('platform:page:layout:index')")
    @ApiOperation(value = "页面首页")
    @RequestMapping(value = "/index", method = RequestMethod.GET)
    public CommonResult<PageLayoutIndexResponse> index() {
        return CommonResult.success(pageLayoutService.index());
    }

    @PreAuthorize("hasAuthority('platform:page:layout:bottom:navigation')")
    @ApiOperation(value = "页面底部导航")
    @RequestMapping(value = "/bottom/navigation/get", method = RequestMethod.GET)
    public CommonResult<PageLayoutBottomNavigationResponse> getBottomNavigation() {
        return CommonResult.success(pageLayoutService.getBottomNavigation());
    }

    @PreAuthorize("hasAuthority('platform:page:layout:index:banner:save')")
    @ApiOperation(value = "页面首页banner保存")
    @RequestMapping(value = "/index/banner/save", method = RequestMethod.POST)
    public CommonResult<Object> indexBannerSave(@RequestBody JSONObject jsonObject) {
        if (pageLayoutService.indexBannerSave(jsonObject)) {
            return CommonResult.success();
        }
        return CommonResult.failed();
    }

    @PreAuthorize("hasAuthority('platform:page:layout:index:menu:save')")
    @ApiOperation(value = "页面首页menu保存")
    @RequestMapping(value = "/index/menu/save", method = RequestMethod.POST)
    public CommonResult<Object> indexMenuSave(@RequestBody JSONObject jsonObject) {
        if (pageLayoutService.indexMenuSave(jsonObject)) {
            return CommonResult.success();
        }
        return CommonResult.failed();
    }

    @PreAuthorize("hasAuthority('platform:page:layout:index:banner:save')")
    @ApiOperation(value = "页面用户中心banner保存")
    @RequestMapping(value = "/user/banner/save", method = RequestMethod.POST)
    public CommonResult<Object> userBannerSave(@RequestBody JSONObject jsonObject) {
        if (pageLayoutService.userBannerSave(jsonObject)) {
            return CommonResult.success();
        }
        return CommonResult.failed();
    }

    @PreAuthorize("hasAuthority('platform:page:layout:user:menu:save')")
    @ApiOperation(value = "页面用户中心导航保存")
    @RequestMapping(value = "/user/menu/save", method = RequestMethod.POST)
    public CommonResult<Object> userMenuSave(@RequestBody JSONObject jsonObject) {
        if (pageLayoutService.userMenuSave(jsonObject)) {
            return CommonResult.success();
        }
        return CommonResult.failed();
    }

    @PreAuthorize("hasAuthority('platform:page:layout:bottom:navigation:save')")
    @ApiOperation(value = "底部导航保存")
    @RequestMapping(value = "/bottom/navigation/save", method = RequestMethod.POST)
    public CommonResult<Object> bottomNavigationSave(@RequestBody JSONObject jsonObject) {
        if (pageLayoutService.bottomNavigationSave(jsonObject)) {
            return CommonResult.success();
        }
        return CommonResult.failed();
    }

    @PreAuthorize("hasAuthority('platform:page:layout:splash:ad:get')")
    @ApiOperation(value = "获取开屏广告配置")
    @RequestMapping(value = "/splash/ad/get", method = RequestMethod.GET)
    public CommonResult<SplashAdConfigVo> getSplashAdConfig() {
        return CommonResult.success(pageLayoutService.getSplashAdConfig());
    }

    @PreAuthorize("hasAuthority('platform:page:layout:splash:ad:save')")
    @ApiOperation(value = "编辑开屏广告配置")
    @RequestMapping(value = "/splash/ad/save", method = RequestMethod.POST)
    public CommonResult<Object> splashAdConfigSave(@RequestBody @Validated SplashAdConfigVo configVo) {
        if (pageLayoutService.splashAdConfigSave(configVo)) {
            return CommonResult.success("编辑成功");
        }
        return CommonResult.failed("编辑失败");
    }
}
