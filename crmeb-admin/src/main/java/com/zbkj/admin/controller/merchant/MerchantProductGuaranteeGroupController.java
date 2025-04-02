package com.zbkj.admin.controller.merchant;

import com.zbkj.common.annotation.LogControllerAnnotation;
import com.zbkj.common.enums.MethodType;
import com.zbkj.common.request.ProductGuaranteeGroupAddRequest;
import com.zbkj.common.response.ProductGuaranteeGroupListResponse;
import com.zbkj.common.result.CommonResult;
import com.zbkj.service.service.ProductGuaranteeGroupService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 商户端商品保障服务组合控制器
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
@RequestMapping("api/admin/merchant/product/guarantee/group")
@Api(tags = "商户端商品保障服务组合控制器") //配合swagger使用
public class MerchantProductGuaranteeGroupController {

    @Autowired
    private ProductGuaranteeGroupService productGuaranteeGroupService;

    @ApiOperation(value = "保障服务组合列表")
    @PreAuthorize("hasAuthority('merchant:product:guarantee:group:list')")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public CommonResult<List<ProductGuaranteeGroupListResponse>> list() {
        return CommonResult.success(productGuaranteeGroupService.findList());
    }

    @LogControllerAnnotation(intoDB = true, methodType = MethodType.ADD, description = "新增保障服务组合")
    @ApiOperation(value = "新增保障服务组合")
    @PreAuthorize("hasAuthority('merchant:product:guarantee:group:add')")
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public CommonResult<String> add(@RequestBody @Validated ProductGuaranteeGroupAddRequest request) {
        if (productGuaranteeGroupService.add(request)) {
            return CommonResult.success();
        }
        return CommonResult.failed();
    }

    @LogControllerAnnotation(intoDB = true, methodType = MethodType.UPDATE, description = "编辑保障服务组合")
    @ApiOperation(value = "编辑保障服务组合")
    @PreAuthorize("hasAuthority('merchant:product:guarantee:group:edit')")
    @RequestMapping(value = "/edit", method = RequestMethod.POST)
    public CommonResult<String> edit(@RequestBody @Validated ProductGuaranteeGroupAddRequest request) {
        if (productGuaranteeGroupService.edit(request)) {
            return CommonResult.success();
        }
        return CommonResult.failed();
    }

    @LogControllerAnnotation(intoDB = true, methodType = MethodType.DELETE, description = "删除保障服务组合")
    @ApiOperation(value = "删除保障服务组合")
    @PreAuthorize("hasAuthority('merchant:product:guarantee:group:delete')")
    @RequestMapping(value = "/delete/{id}", method = RequestMethod.POST)
    public CommonResult<String> delete(@PathVariable(value = "id") Integer id) {
        if (productGuaranteeGroupService.delete(id)) {
            return CommonResult.success();
        }
        return CommonResult.failed();
    }

}
