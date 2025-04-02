package com.zbkj.admin.controller.merchant;

import com.zbkj.common.result.CommonResult;
import com.zbkj.common.vo.CityVo;
import com.zbkj.service.service.CityRegionService;
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
 * 城市表 前端控制器
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
@RequestMapping("api/admin/merchant/city/region")
@Api(tags = "商户端城市区域管理")
public class MerchantCityRegionController {

    @Autowired
    private CityRegionService cityRegionService;

    @PreAuthorize("hasAuthority('merchant:city:list:tree')")
    @ApiOperation(value = "获取城市tree结构的列表")
    @RequestMapping(value = "/city/tree", method = RequestMethod.GET)
    public CommonResult<List<CityVo>> getCityListTree() {
        return CommonResult.success(cityRegionService.getCityListTree());
    }

    @PreAuthorize("hasAuthority('merchant:city:region:list:tree')")
    @ApiOperation(value = "获取城市区域tree结构的列表")
    @RequestMapping(value = "/list/tree", method = RequestMethod.GET)
    public CommonResult<List<CityVo>> getRegionListTree() {
        return CommonResult.success(cityRegionService.getRegionListTree());
    }
}



