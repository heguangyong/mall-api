package com.zbkj.admin.controller.platform;

import com.github.pagehelper.PageInfo;
import com.zbkj.common.annotation.LogControllerAnnotation;
import com.zbkj.common.enums.MethodType;
import com.zbkj.common.model.merchant.MerchantType;
import com.zbkj.common.page.CommonPage;
import com.zbkj.common.request.merchant.MerchantTypeRequest;
import com.zbkj.common.request.PageParamRequest;
import com.zbkj.common.result.CommonResult;
import com.zbkj.service.service.MerchantTypeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 商户类型控制器
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
@RequestMapping("api/admin/platform/merchant/type")
@Api(tags = "平台端商户类型控制器")
public class MerchantTypeController {

    @Autowired
    private MerchantTypeService typeService;

    @PreAuthorize("hasAuthority('platform:merchant:type:list')")
    @ApiOperation(value="商户类型分页列表")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public CommonResult<CommonPage<MerchantType>> getList(@Validated PageParamRequest pageParamRequest) {
        PageInfo<MerchantType> pageInfo = typeService.getAdminPage(pageParamRequest);
        return CommonResult.success(CommonPage.restPage(pageInfo));
    }

    @LogControllerAnnotation(intoDB = true, methodType = MethodType.ADD, description = "添加商户类型")
    @PreAuthorize("hasAuthority('platform:merchant:type:add')")
    @ApiOperation(value="添加商户类型")
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public CommonResult<Object> add(@RequestBody @Validated MerchantTypeRequest request) {
        if (typeService.add(request)) {
            return CommonResult.success("添加商户类型成功");
        }
        return CommonResult.failed("添加商户类型失败");
    }

    @LogControllerAnnotation(intoDB = true, methodType = MethodType.UPDATE, description = "编辑类型类型")
    @PreAuthorize("hasAuthority('platform:merchant:type:update')")
    @ApiOperation(value="编辑类型类型")
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public CommonResult<Object> update(@RequestBody @Validated MerchantTypeRequest request) {
        if (typeService.edit(request)) {
            return CommonResult.success("编辑商户类型成功");
        }
        return CommonResult.failed("编辑商户类型失败");
    }

    @LogControllerAnnotation(intoDB = true, methodType = MethodType.DELETE, description = "删除商户类型")
    @PreAuthorize("hasAuthority('platform:merchant:type:delete')")
    @ApiOperation(value="删除商户类型")
    @RequestMapping(value = "/delete/{id}", method = RequestMethod.POST)
    public CommonResult<Object> delete(@PathVariable("id") Integer id) {
        if (typeService.delete(id)) {
            return CommonResult.success("删除商户类型成功");
        }
        return CommonResult.failed("删除商户类型失败");
    }

    @PreAuthorize("hasAuthority('platform:merchant:type:all')")
    @ApiOperation(value="获取全部商户类型列表")
    @RequestMapping(value = "/all/list", method = RequestMethod.GET)
    public CommonResult<List<MerchantType>> allList() {
        return CommonResult.success(typeService.allList());
    }
}
