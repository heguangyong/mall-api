package com.zbkj.admin.controller.platform;

import com.zbkj.common.annotation.LogControllerAnnotation;
import com.zbkj.common.enums.MethodType;
import com.zbkj.common.request.ProductGuaranteeRequest;
import com.zbkj.common.response.ProductGuaranteeResponse;
import com.zbkj.common.result.CommonResult;
import com.zbkj.service.service.ProductGuaranteeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;


/**
 * 平台端商品保障服务控制器
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
@RequestMapping("api/admin/platform/product/guarantee")
@Api(tags = "平台端商品保障服务控制器")
public class ProductGuaranteeController {

    @Autowired
    private ProductGuaranteeService guaranteeService;

    @PreAuthorize("hasAuthority('platform:product:guarantee:list')")
    @ApiOperation(value = "保障服务列表")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public CommonResult<List<ProductGuaranteeResponse>> getList() {
        return CommonResult.success(guaranteeService.getAdminList());
    }

    @LogControllerAnnotation(intoDB = true, methodType = MethodType.ADD, description = "新增保障服务")
    @PreAuthorize("hasAuthority('platform:product:guarantee:add')")
    @ApiOperation(value = "新增保障服务")
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public CommonResult<String> add(@RequestBody @Validated ProductGuaranteeRequest request) {
        if (guaranteeService.add(request)) {
            return CommonResult.success();
        }
        return CommonResult.failed();
    }

    @LogControllerAnnotation(intoDB = true, methodType = MethodType.DELETE, description = "删除保障服务")
    @PreAuthorize("hasAuthority('platform:product:guarantee:delete')")
    @ApiOperation(value = "删除保障服务")
    @RequestMapping(value = "/delete/{id}", method = RequestMethod.POST)
    public CommonResult<String> delete(@PathVariable(value = "id") Integer id) {
        if (guaranteeService.delete(id)) {
            return CommonResult.success();
        }
        return CommonResult.failed();
    }

    @LogControllerAnnotation(intoDB = true, methodType = MethodType.UPDATE, description = "修改保障服务")
    @PreAuthorize("hasAuthority('platform:product:guarantee:update')")
    @ApiOperation(value = "修改保障服务")
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public CommonResult<String> update(@RequestBody @Validated ProductGuaranteeRequest request) {
        if (guaranteeService.edit(request)) {
            return CommonResult.success();
        }
        return CommonResult.failed();
    }

    @LogControllerAnnotation(intoDB = true, methodType = MethodType.UPDATE, description = "修改保障服务显示状态")
    @PreAuthorize("hasAuthority('platform:product:guarantee:show:status')")
    @ApiOperation(value = "修改保障服务显示状态")
    @RequestMapping(value = "/update/show/{id}", method = RequestMethod.POST)
    public CommonResult<Object> updateShowStatus(@PathVariable(value = "id") Integer id) {
        if (guaranteeService.updateShowStatus(id)) {
            return CommonResult.success("修改成功");
        }
        return CommonResult.failed("修改失败");
    }
}



