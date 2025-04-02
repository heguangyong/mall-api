package com.zbkj.admin.controller.merchant;

import com.zbkj.common.annotation.LogControllerAnnotation;
import com.zbkj.common.enums.MethodType;
import com.zbkj.common.page.CommonPage;
import com.zbkj.common.request.CdkeyLibrarySaveRequest;
import com.zbkj.common.request.CdkeyLibrarySearchRequest;
import com.zbkj.common.response.CdkeyLibraryPageResponse;
import com.zbkj.common.response.CdkeyLibrarySimpleResponse;
import com.zbkj.common.result.CommonResult;
import com.zbkj.service.service.CdkeyLibraryService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;


/**
 * 商户卡密库控制器 前端控制器
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
@RequestMapping("api/admin/merchant/cdkey/library")
@Api(tags = "商户卡密库控制器")
public class CdkeyLibraryController {

    @Autowired
    private CdkeyLibraryService cdkeyLibraryService;

    @PreAuthorize("hasAuthority('merchant:cdkey:library:page:list')")
    @ApiOperation(value = "卡密库分页列表")
    @RequestMapping(value = "/page/list", method = RequestMethod.GET)
    public CommonResult<CommonPage<CdkeyLibraryPageResponse>> getList(@Validated CdkeyLibrarySearchRequest request) {
        return CommonResult.success(CommonPage.restPage(cdkeyLibraryService.findPageList(request)));
    }

    @PreAuthorize("hasAuthority('merchant:cdkey:library:add')")
    @LogControllerAnnotation(intoDB = true, methodType = MethodType.ADD, description = "新增卡密库")
    @ApiOperation(value = "新增卡密库")
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    public CommonResult<Object> save(@RequestBody CdkeyLibrarySaveRequest request) {
        if (cdkeyLibraryService.add(request)) {
            return CommonResult.success().setMessage("新增卡密库成功");
        }
        return CommonResult.failed().setMessage("新增卡密库失败");
    }

    @PreAuthorize("hasAuthority('merchant:cdkey:library:delete')")
    @LogControllerAnnotation(intoDB = true, methodType = MethodType.DELETE, description = "删除卡密库")
    @ApiOperation(value = "删除卡密库")
    @RequestMapping(value = "/delete/{id}", method = RequestMethod.POST)
    public CommonResult<Object> delete(@PathVariable(value = "id") Integer id) {
        if (cdkeyLibraryService.delete(id)) {
            return CommonResult.success().setMessage("删除卡密库成功");
        }
        return CommonResult.failed().setMessage("删除卡密库失败");
    }

    @PreAuthorize("hasAuthority('merchant:cdkey:library:update')")
    @LogControllerAnnotation(intoDB = true, methodType = MethodType.UPDATE, description = "修改卡密库")
    @ApiOperation(value = "修改卡密库")
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public CommonResult<Object> update(@RequestBody CdkeyLibrarySaveRequest request) {
        if (cdkeyLibraryService.updateLibrary(request)) {
            return CommonResult.success().setMessage("修改卡密库成功");
        }
        return CommonResult.failed().setMessage("修改卡密库失败");
    }

    @PreAuthorize("hasAuthority('merchant:cdkey:library:unrelated:list')")
    @LogControllerAnnotation(intoDB = true, methodType = MethodType.UPDATE, description = "未关联卡密库列表")
    @ApiOperation(value = "未关联卡密库列表")
    @RequestMapping(value = "/unrelated/list", method = RequestMethod.GET)
    public CommonResult<List<CdkeyLibrarySimpleResponse>> findUnrelatedList() {
        return CommonResult.success(cdkeyLibraryService.findUnrelatedList());
    }
}



