package com.zbkj.admin.controller.platform;

import com.zbkj.admin.service.SeckillService;
import com.zbkj.common.annotation.LogControllerAnnotation;
import com.zbkj.common.enums.MethodType;
import com.zbkj.common.page.CommonPage;
import com.zbkj.common.request.*;
import com.zbkj.common.response.SeckillActivityDetailResponse;
import com.zbkj.common.response.SeckillActivityPageResponse;
import com.zbkj.common.response.SeckillProductPageResponse;
import com.zbkj.common.response.SeckillTimeIntervalResponse;
import com.zbkj.common.result.CommonResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;


/**
 * 秒杀控制器
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
@RequestMapping("api/admin/platform/seckill")
@Api(tags = "秒杀管理")
public class SeckillController {

    @Autowired
    private SeckillService seckillService;

    @LogControllerAnnotation(intoDB = true, methodType = MethodType.ADD, description = "新增秒杀时段")
    @PreAuthorize("hasAuthority('platform:seckill:time:interval:add')")
    @ApiOperation(value = "新增秒杀时段")
    @RequestMapping(value = "/time/interval/add", method = RequestMethod.POST)
    public CommonResult<String> createTimeInterval(@RequestBody @Validated SeckillTimeIntervalRequest request) {
        if (seckillService.createTimeInterval(request)) {
            return CommonResult.success();
        }
        return CommonResult.failed();
    }

    @LogControllerAnnotation(intoDB = true, methodType = MethodType.UPDATE, description = "编辑秒杀时段")
    @PreAuthorize("hasAuthority('platform:seckill:time:interval:update')")
    @ApiOperation(value = "编辑秒杀时段")
    @RequestMapping(value = "/time/interval/update", method = RequestMethod.POST)
    public CommonResult<String> updateTimeInterval(@RequestBody @Validated SeckillTimeIntervalRequest request) {
        if (seckillService.updateTimeInterval(request)) {
            return CommonResult.success();
        }
        return CommonResult.failed();
    }

    @LogControllerAnnotation(intoDB = true, methodType = MethodType.DELETE, description = "删除秒杀时段")
    @PreAuthorize("hasAuthority('platform:seckill:time:interval:delete')")
    @ApiOperation(value = "删除秒杀时段")
    @RequestMapping(value = "/time/interval/delete/{id}", method = RequestMethod.POST)
    public CommonResult<String> deleteTimeInterval(@PathVariable("id") Integer id) {
        if (seckillService.deleteTimeInterval(id)) {
            return CommonResult.success();
        }
        return CommonResult.failed();
    }

    @PreAuthorize("hasAuthority('platform:seckill:time:interval:list')")
    @ApiOperation(value = "秒杀时段列表")
    @RequestMapping(value = "/time/interval/list", method = RequestMethod.GET)
    public CommonResult<List<SeckillTimeIntervalResponse>> getTimeIntervalList(@Validated SeckillTimeIntervalSearchRequest request) {
        return CommonResult.success(seckillService.getTimeIntervalList(request));
    }

    @LogControllerAnnotation(intoDB = true, methodType = MethodType.UPDATE, description = "秒杀时段开关")
    @PreAuthorize("hasAuthority('platform:seckill:time:interval:switch')")
    @ApiOperation(value = "秒杀时段开关")
    @RequestMapping(value = "/time/interval/switch/{id}", method = RequestMethod.POST)
    public CommonResult<String> switchTimeInterval(@PathVariable("id") Integer id) {
        if (seckillService.switchTimeInterval(id)) {
            return CommonResult.success();
        }
        return CommonResult.failed();
    }

    @LogControllerAnnotation(intoDB = true, methodType = MethodType.ADD, description = "新增秒杀活动")
    @PreAuthorize("hasAuthority('platform:seckill:activity:add')")
    @ApiOperation(value = "新增秒杀活动")
    @RequestMapping(value = "/activity/add", method = RequestMethod.POST)
    public CommonResult<String> createActivity(@RequestBody @Validated SeckillActivitySaveRequest request) {
        if (seckillService.createActivity(request)) {
            return CommonResult.success();
        }
        return CommonResult.failed();
    }

    @PreAuthorize("hasAuthority('platform:seckill:activity:page')")
    @ApiOperation(value = "秒杀活动分页列表")
    @RequestMapping(value = "/activity/page", method = RequestMethod.GET)
    public CommonResult<CommonPage<SeckillActivityPageResponse>> activityPage(@Validated SeckillActivitySearchRequest request) {
        return CommonResult.success(CommonPage.restPage(seckillService.activityPage(request)));
    }

    @PreAuthorize("hasAuthority('platform:seckill:activity:detail')")
    @ApiOperation(value = "秒杀活动详情")
    @RequestMapping(value = "/activity/detail/{id}", method = RequestMethod.GET)
    public CommonResult<SeckillActivityDetailResponse> activityDetail(@PathVariable("id") Integer id) {
        return CommonResult.success(seckillService.activityDetail(id));
    }

    @LogControllerAnnotation(intoDB = true, methodType = MethodType.UPDATE, description = "编辑秒杀活动")
    @PreAuthorize("hasAuthority('platform:seckill:activity:update')")
    @ApiOperation(value = "编辑秒杀活动")
    @RequestMapping(value = "/activity/update", method = RequestMethod.POST)
    public CommonResult<String> updateActivity(@RequestBody @Validated SeckillActivitySaveRequest request) {
        if (seckillService.updateActivity(request)) {
            return CommonResult.success();
        }
        return CommonResult.failed();
    }

    @LogControllerAnnotation(intoDB = true, methodType = MethodType.DELETE, description = "删除秒杀活动")
    @PreAuthorize("hasAuthority('platform:seckill:activity:delete')")
    @ApiOperation(value = "删除秒杀活动")
    @RequestMapping(value = "/activity/delete/{id}", method = RequestMethod.POST)
    public CommonResult<String> deleteActivity(@PathVariable("id") Integer id) {
        if (seckillService.deleteActivity(id)) {
            return CommonResult.success();
        }
        return CommonResult.failed();
    }

    @LogControllerAnnotation(intoDB = true, methodType = MethodType.UPDATE, description = "秒杀活动开关")
    @PreAuthorize("hasAuthority('platform:seckill:activity:switch')")
    @ApiOperation(value = "秒杀活动开关")
    @RequestMapping(value = "/activity/switch/{id}", method = RequestMethod.POST)
    public CommonResult<String> switchActivity(@PathVariable("id") Integer id) {
        if (seckillService.switchActivity(id)) {
            return CommonResult.success();
        }
        return CommonResult.failed();
    }

    @PreAuthorize("hasAuthority('platform:seckill:product:list')")
    @ApiOperation(value = "秒杀商品列表")
    @RequestMapping(value = "/product/page", method = RequestMethod.GET)
    public CommonResult<CommonPage<SeckillProductPageResponse>> getSeckillProductPage(@Validated SeckillProductSearchRequest request) {
        return CommonResult.success(CommonPage.restPage(seckillService.getSeckillProductPage(request)));
    }

    @LogControllerAnnotation(intoDB = true, methodType = MethodType.UPDATE, description = "秒杀商品设置活动价")
    @PreAuthorize("hasAuthority('platform:seckill:product:price')")
    @ApiOperation(value = "秒杀商品设置活动价")
    @RequestMapping(value = "/product/set/price", method = RequestMethod.POST)
    public CommonResult<String> setProductPrice(@RequestBody @Validated SeckillProductPriceRequest request) {
        if (seckillService.setProductPrice(request)) {
            return CommonResult.success();
        }
        return CommonResult.failed();
    }

    @LogControllerAnnotation(intoDB = true, methodType = MethodType.UPDATE, description = "秒杀商品下架")
    @PreAuthorize("hasAuthority('platform:seckill:product:down')")
    @ApiOperation(value = "秒杀商品下架")
    @RequestMapping(value = "/product/down", method = RequestMethod.POST)
    public CommonResult<String> downProduct(@RequestBody @Validated SeckillProductBatchRequest request) {
        if (seckillService.forceDownProduct(request)) {
            return CommonResult.success();
        }
        return CommonResult.failed();
    }

    @LogControllerAnnotation(intoDB = true, methodType = MethodType.DELETE, description = "秒杀商品删除")
    @PreAuthorize("hasAuthority('platform:seckill:product:delete')")
    @ApiOperation(value = "秒杀商品删除")
    @RequestMapping(value = "/product/delete", method = RequestMethod.POST)
    public CommonResult<String> deleteProduct(@RequestBody @Validated SeckillProductBatchRequest request) {
        if (seckillService.deleteProduct(request)) {
            return CommonResult.success();
        }
        return CommonResult.failed();
    }

    @LogControllerAnnotation(intoDB = true, methodType = MethodType.ADD, description = "平台秒杀商品添加")
    @PreAuthorize("hasAuthority('platform:seckill:product:add')")
    @ApiOperation(value = "秒杀商品添加")
    @RequestMapping(value = "/product/add", method = RequestMethod.POST)
    public CommonResult<String> addProduct(@RequestBody @Validated SeckillProductAddRequest request) {
        if (seckillService.platAddProduct(request)) {
            return CommonResult.success();
        }
        return CommonResult.failed();
    }

    @LogControllerAnnotation(intoDB = true, methodType = MethodType.UPDATE, description = "秒杀商品审核")
    @PreAuthorize("hasAuthority('platform:seckill:product:audit')")
    @ApiOperation(value = "秒杀商品审核")
    @RequestMapping(value = "/product/audit", method = RequestMethod.POST)
    public CommonResult<String> auditProduct(@RequestBody @Validated ProductAuditRequest request) {
        if (seckillService.auditProduct(request)) {
            return CommonResult.success();
        }
        return CommonResult.failed();
    }
}



