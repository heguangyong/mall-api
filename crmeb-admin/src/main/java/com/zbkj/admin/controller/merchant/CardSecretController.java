package com.zbkj.admin.controller.merchant;

import com.zbkj.common.annotation.LogControllerAnnotation;
import com.zbkj.common.enums.MethodType;
import com.zbkj.common.model.cdkey.CardSecret;
import com.zbkj.common.page.CommonPage;
import com.zbkj.common.request.*;
import com.zbkj.common.result.CommonResult;
import com.zbkj.service.service.CardSecretService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;


/**
 * 商户卡密控制器 前端控制器
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
@RequestMapping("api/admin/merchant/card/secret")
@Api(tags = "商户卡密控制器")
public class CardSecretController {

    @Autowired
    private CardSecretService cardSecretService;

    @PreAuthorize("hasAuthority('merchant:card:secret:page:list')")
    @ApiOperation(value = "卡密分页列表")
    @RequestMapping(value = "/page/list", method = RequestMethod.GET)
    public CommonResult<CommonPage<CardSecret>> getList(@Validated CardSecretSearchRequest request) {
        return CommonResult.success(CommonPage.restPage(cardSecretService.findPageList(request)));
    }

    @PreAuthorize("hasAuthority('merchant:card:secret:add')")
    @LogControllerAnnotation(intoDB = true, methodType = MethodType.ADD, description = "新增卡密")
    @ApiOperation(value = "新增卡密")
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    public CommonResult<Object> save(@RequestBody @Validated CardSecretAddRequest request) {
        if (cardSecretService.add(request)) {
            return CommonResult.success().setMessage("新增卡密成功");
        }
        return CommonResult.failed().setMessage("新增卡密失败");
    }

    @PreAuthorize("hasAuthority('merchant:card:secret:delete')")
    @LogControllerAnnotation(intoDB = true, methodType = MethodType.DELETE, description = "删除卡密")
    @ApiOperation(value = "删除卡密")
    @RequestMapping(value = "/delete/{id}", method = RequestMethod.POST)
    public CommonResult<Object> delete(@PathVariable(value = "id") Integer id) {
        if (cardSecretService.delete(id)) {
            return CommonResult.success().setMessage("删除卡密成功");
        }
        return CommonResult.failed().setMessage("删除卡密失败");
    }

    @PreAuthorize("hasAuthority('merchant:cdkey:library:update')")
    @LogControllerAnnotation(intoDB = true, methodType = MethodType.UPDATE, description = "修改卡密")
    @ApiOperation(value = "修改卡密")
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public CommonResult<Object> update(@RequestBody @Validated CardSecretSaveRequest request) {
        if (cardSecretService.edit(request)) {
            return CommonResult.success().setMessage("修改卡密成功");
        }
        return CommonResult.failed().setMessage("修改卡密失败");
    }

    @PreAuthorize("hasAuthority('merchant:cdkey:library:import:excel')")
    @LogControllerAnnotation(intoDB = true, methodType = MethodType.UPDATE, description = "导入卡密")
    @ApiOperation(value = "导入卡密")
    @RequestMapping(value = "/import/excel", method = RequestMethod.POST)
    public CommonResult<Object> addImportExcel(MultipartFile file, @RequestParam(value = "libraryId") Integer libraryId) throws IOException {
        if (cardSecretService.addImportExcel(file, libraryId)) {
            return CommonResult.success().setMessage("导入卡密成功");
        }
        return CommonResult.failed().setMessage("导入卡密失败");
    }

    @PreAuthorize("hasAuthority('merchant:card:secret:batch:delete')")
    @LogControllerAnnotation(intoDB = true, methodType = MethodType.DELETE, description = "批量删除卡密")
    @ApiOperation(value = "批量删除卡密")
    @RequestMapping(value = "/batch/delete", method = RequestMethod.POST)
    public CommonResult<Object> batchDelete(@RequestBody @Validated BatchOperationCommonRequest request) {
        if (cardSecretService.batchDelete(request)) {
            return CommonResult.success().setMessage("批量删除卡密成功");
        }
        return CommonResult.failed().setMessage("批量删除卡密失败");
    }

}



