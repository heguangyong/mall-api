package com.zbkj.admin.controller.merchant;

import com.zbkj.common.model.page.PageDiy;
import com.zbkj.common.page.CommonPage;
import com.zbkj.common.request.CommonSearchRequest;
import com.zbkj.common.request.page.PageDiyEditNameRequest;
import com.zbkj.common.request.page.PageDiyRequest;
import com.zbkj.common.response.page.PageDiyResponse;
import com.zbkj.common.result.CommonResult;
import com.zbkj.service.service.PageDiyService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 商户DIY 控制器
 *
 * @author Han
 * @version 1.0.0
 * @Date 2024/10/28
 */
@Slf4j
@RestController
@RequestMapping("api/admin/merchant/page/diy")
@Api(tags = "商户装修控制器") //配合swagger使用
public class MerchantPageDiyController {

    @Autowired
    private PageDiyService pageDiyService;

    @PreAuthorize("hasAuthority('merchant:page:diy:page')")
    @ApiOperation(value = "商户装修模板分页列表") //配合swagger使用
    @RequestMapping(value = "/page", method = RequestMethod.GET)
    public CommonResult<CommonPage<PageDiy>> findPage(@Validated CommonSearchRequest request) {
        return CommonResult.success(CommonPage.restPage(pageDiyService.findMerchantPage(request)));
    }

    @PreAuthorize("hasAuthority('merchant:page:diy:set:default')")
    @ApiOperation(value = "设置商户装修默认首页")
    @RequestMapping(value = "/set/default/{id}", method = RequestMethod.POST)
    public CommonResult<String> setDefault(@PathVariable(value = "id") Integer id) {
        if (pageDiyService.setDiyDefault(id)) {
            return CommonResult.success("设置成功");
        }
        return CommonResult.failed("设置失败");
    }

    @PreAuthorize("hasAuthority('merchant:page:diy:get:default:id')")
    @ApiOperation(value = "获取商户装修默认首页ID")
    @RequestMapping(value = "/get/default/id", method = RequestMethod.GET)
    public CommonResult<Integer> getDefaultId() {
        return CommonResult.success(pageDiyService.getDefaultId());
    }

    @PreAuthorize("hasAuthority('merchant:page:diy:save')")
    @ApiOperation(value = "新增商户装修模板")
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    public CommonResult<PageDiy> save(@RequestBody @Validated PageDiyRequest request) {
        return CommonResult.success(pageDiyService.saveMerchantPageDiy(request));
    }

    @PreAuthorize("hasAuthority('merchant:page:diy:delete')")
    @ApiOperation(value = "删除商户装修模板")
    @RequestMapping(value = "/delete/{id}", method = RequestMethod.POST)
    public CommonResult<String> delete(@PathVariable(value = "id") Integer id) {
        if (pageDiyService.deletePageDiy(id)) {
            return CommonResult.success("删除成功");
        }
        return CommonResult.failed("删除失败");
    }

    @PreAuthorize("hasAuthority('merchant:page:diy:update')")
    @ApiOperation(value = "编辑商户装修模板")
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public CommonResult<String> update(@RequestBody @Validated PageDiyRequest request) {
        if (pageDiyService.editMerchantPageDiy(request)) {
            return CommonResult.success("编辑成功");
        }
        return CommonResult.failed("编辑失败");
    }

    @PreAuthorize("hasAuthority('merchant:page:diy:update:name')")
    @ApiOperation(value = "编辑商户装修模板名称")
    @RequestMapping(value = "/update/name", method = RequestMethod.POST)
    public CommonResult<String> update(@RequestBody @Validated PageDiyEditNameRequest pageDiyEditNameRequest) {
        if (pageDiyService.editPageDiyName(pageDiyEditNameRequest)) {
            return CommonResult.success("编辑成功");
        }
        return CommonResult.failed("编辑失败");
    }

    @PreAuthorize("hasAuthority('merchant:page:diy:info')")
    @ApiOperation(value = "详情")
    @RequestMapping(value = "/info/{id}", method = RequestMethod.GET)
    public CommonResult<PageDiyResponse> info(@PathVariable(value = "id") Integer id) {
        return CommonResult.success(pageDiyService.getMerchantPageDiyInfo(id));
    }
}
