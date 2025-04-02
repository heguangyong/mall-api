package com.zbkj.admin.controller.platform;

import com.zbkj.common.annotation.LogControllerAnnotation;
import com.zbkj.common.enums.MethodType;
import com.zbkj.common.request.CityRegionEditRequest;
import com.zbkj.common.request.CityRegionRequest;
import com.zbkj.common.result.CommonResult;
import com.zbkj.common.vo.CityVo;
import com.zbkj.service.service.CityRegionService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


/**
 * 城市区域控制器
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
@RequestMapping("api/admin/platform/city/region")
@Api(tags = "城市管理")
public class CityRegionController {

    @Autowired
    private CityRegionService cityRegionService;

    @LogControllerAnnotation(intoDB = true, methodType = MethodType.ADD, description = "城市区域添加")
    @PreAuthorize("hasAuthority('platform:city:region:add')")
    @ApiOperation(value = "城市区域添加")
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public CommonResult<String> add(@Validated CityRegionRequest request) {
        if (cityRegionService.add(request)) {
            return CommonResult.success();
        }
        return CommonResult.failed();
    }

    @LogControllerAnnotation(intoDB = true, methodType = MethodType.UPDATE, description = "城市区域编辑")
    @PreAuthorize("hasAuthority('platform:city:region:edit')")
    @ApiOperation(value = "城市区域编辑")
    @RequestMapping(value = "/edit", method = RequestMethod.POST)
    public CommonResult<String> edit(@Validated CityRegionEditRequest request) {
        if (cityRegionService.edit(request)) {
            return CommonResult.success();
        }
        return CommonResult.failed();
    }

    @LogControllerAnnotation(intoDB = true, methodType = MethodType.DELETE, description = "城市区域删除")
    @PreAuthorize("hasAuthority('platform:city:region:delete')")
    @ApiOperation(value = "城市区域删除")
    @RequestMapping(value = "/delete/{regionId}", method = RequestMethod.POST)
    public CommonResult<String> delete(@PathVariable(value = "regionId") Integer regionId) {
        if (cityRegionService.delete(regionId)) {
            return CommonResult.success();
        }
        return CommonResult.failed();
    }

    @PreAuthorize("hasAuthority('platform:city:region:list:tree')")
    @ApiOperation(value = "获取城市区域tree结构的列表")
    @RequestMapping(value = "/list/tree", method = RequestMethod.GET)
    public CommonResult<List<CityVo>> getListTree() {
        return CommonResult.success(cityRegionService.getRegionListTree());
    }
}



