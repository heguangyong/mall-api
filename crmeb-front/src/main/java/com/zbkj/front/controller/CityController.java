package com.zbkj.front.controller;


import com.zbkj.common.model.city.CityRegion;
import com.zbkj.common.request.CitySearchRequest;
import com.zbkj.common.response.CityResponse;
import com.zbkj.common.result.CommonResult;
import com.zbkj.common.vo.CityVo;
import com.zbkj.service.service.CityRegionService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 城市服务
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
@RequestMapping("api/front/city")
@Api(tags = "移动端城市控制器")
public class CityController {

    @Autowired
    private CityRegionService cityRegionService;

    @ApiOperation(value = "获取城市区域tree结构的列表")
    @RequestMapping(value = "/list/tree", method = RequestMethod.GET)
    public CommonResult<List<CityVo>> getListTree() {
        return CommonResult.success(cityRegionService.getRegionListTree());
    }

    @ApiOperation(value = "获取城市区域分级列表")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public CommonResult<List<CityResponse>> getList(@ModelAttribute @Validated CitySearchRequest request) {
        return CommonResult.success(cityRegionService.getCityRegionList(request));
    }

    @ApiOperation(value = "获取省级城市数据")
    @RequestMapping(value = "/province/list", method = RequestMethod.GET)
    public CommonResult<List<CityRegion>> findProvinceList() {
        return CommonResult.success(cityRegionService.findProvinceList());
    }

    @ApiOperation(value = "获取市级城市数据")
    @RequestMapping(value = "/city/list/{parentId}", method = RequestMethod.GET)
    public CommonResult<List<CityRegion>> findCityList(@PathVariable(value = "parentId") Integer parentId) {
        return CommonResult.success(cityRegionService.findCityList(parentId));
    }

    @ApiOperation(value = "获取区级城市数据")
    @RequestMapping(value = "/district/list/{parentId}", method = RequestMethod.GET)
    public CommonResult<List<CityRegion>> findDistrictList(@PathVariable(value = "parentId") Integer parentId) {
        return CommonResult.success(cityRegionService.findDistrictList(parentId));
    }

    @ApiOperation(value = "获取街道级城市数据")
    @RequestMapping(value = "/street/list/{parentId}", method = RequestMethod.GET)
    public CommonResult<List<CityRegion>> findStreetList(@PathVariable(value = "parentId") Integer parentId) {
        return CommonResult.success(cityRegionService.findStreetList(parentId));
    }
}



