package com.zbkj.admin.controller.platform;

import com.zbkj.service.service.HomeService;
import com.zbkj.common.response.HomeOperatingDataResponse;
import com.zbkj.common.response.PlatformHomeRateResponse;
import com.zbkj.common.response.UserChannelDataResponse;
import com.zbkj.common.result.CommonResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


/**
 * 平台端主页控制器
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
@RequestMapping("api/admin/platform/statistics/home")
@Api(tags = "平台端主页控制器")
public class HomeController {

    @Autowired
    private HomeService homeService;

    @PreAuthorize("hasAuthority('platform:statistics:home:index')")
    @ApiOperation(value = "首页数据")
    @RequestMapping(value = "/index", method = RequestMethod.GET)
    public CommonResult<PlatformHomeRateResponse> indexDate() {
        return CommonResult.success(homeService.indexPlatformDate());
    }

    @PreAuthorize("hasAuthority('platform:statistics:home:operating:data')")
    @ApiOperation(value = "经营数据")
    @RequestMapping(value = "/operating/data", method = RequestMethod.GET)
    public CommonResult<HomeOperatingDataResponse> operatingData() {
        return CommonResult.success(homeService.operatingPlatformData());
    }

    @PreAuthorize("hasAuthority('platform:statistics:home:user:channel')")
    @ApiOperation(value = "用户渠道数据")
    @RequestMapping(value = "/channel", method = RequestMethod.GET)
    public CommonResult<List<UserChannelDataResponse>> getChannelData() {
        return CommonResult.success(homeService.getUserChannelData());
    }

}



