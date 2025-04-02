package com.zbkj.admin.controller.platform;

import com.zbkj.admin.service.AdminLoginService;
import com.zbkj.common.annotation.LogControllerAnnotation;
import com.zbkj.common.enums.MethodType;
import com.zbkj.common.page.CommonPage;
import com.zbkj.common.request.PageParamRequest;
import com.zbkj.common.request.merchant.*;
import com.zbkj.common.response.*;
import com.zbkj.common.result.CommonResult;
import com.zbkj.service.service.MerchantService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 商户控制器
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
@RequestMapping("api/admin/platform/merchant")
@Api(tags = "平台端商户控制器")
public class MerchantController {

    @Autowired
    private MerchantService merchantService;
    @Autowired
    private AdminLoginService adminLoginService;

    @PreAuthorize("hasAuthority('platform:merchant:page:list')")
    @ApiOperation(value="商户分页列表")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public CommonResult<CommonPage<MerchantPageResponse>> getPageList(@Validated MerchantSearchRequest searchRequest,
                                                                      @Validated PageParamRequest pageParamRequest) {
        return CommonResult.success(CommonPage.restPage(merchantService.getAdminPage(searchRequest, pageParamRequest)));
    }

    @PreAuthorize("hasAuthority('platform:merchant:list:header:num')")
    @ApiOperation(value="商户分页列表表头数量")
    @RequestMapping(value = "/list/header/num", method = RequestMethod.GET)
    public CommonResult<MerchantHeaderNumResponse> getListHeaderNum(@Validated MerchantSearchRequest searchRequest) {
        return CommonResult.success(merchantService.getListHeaderNum(searchRequest));
    }

    @LogControllerAnnotation(intoDB = true, methodType = MethodType.ADD, description = "添加商户")
    @PreAuthorize("hasAuthority('platform:merchant:add')")
    @ApiOperation(value="添加商户")
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public CommonResult<String> add(@RequestBody @Validated MerchantAddRequest request) {
        if (merchantService.add(request)) {
            return CommonResult.success("添加商户成功");
        }
        return CommonResult.failed("添加商户失败");
    }

    @LogControllerAnnotation(intoDB = true, methodType = MethodType.UPDATE, description = "编辑商户")
    @PreAuthorize("hasAuthority('platform:merchant:update')")
    @ApiOperation(value="编辑商户")
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public CommonResult<String> update(@RequestBody @Validated MerchantUpdateRequest request) {
        if (merchantService.edit(request)) {
            return CommonResult.success("编辑商户成功");
        }
        return CommonResult.failed("编辑商户失败");
    }

    @LogControllerAnnotation(intoDB = true, methodType = MethodType.UPDATE, description = "修改商户手机号")
    @PreAuthorize("hasAuthority('platform:merchant:update:phone')")
    @ApiOperation(value="修改商户手机号")
    @RequestMapping(value = "/update/phone", method = RequestMethod.POST)
    public CommonResult<String> updatePhone(@RequestBody @Validated MerchantUpdatePhoneRequest request) {
        if (merchantService.updatePhone(request)) {
            return CommonResult.success("修改商户手机号成功");
        }
        return CommonResult.failed("修改商户手机号失败");
    }

    @LogControllerAnnotation(intoDB = true, methodType = MethodType.UPDATE, description = "重置商户密码")
    @PreAuthorize("hasAuthority('platform:merchant:reset:password')")
    @ApiOperation(value="重置商户密码")
    @RequestMapping(value = "/reset/password/{id}", method = RequestMethod.POST)
    public CommonResult<String> resetPassword(@PathVariable(value = "id") Integer id) {
        if (merchantService.resetPassword(id)) {
            return CommonResult.success("重置商户密码成功");
        }
        return CommonResult.failed("重置商户密码失败");
    }

    @LogControllerAnnotation(intoDB = true, methodType = MethodType.UPDATE, description = "修改复制商品数量")
    @PreAuthorize("hasAuthority('platform:merchant:copy:prodcut:num')")
    @ApiOperation(value="修改复制商品数量")
    @RequestMapping(value = "/update/copy/product/num", method = RequestMethod.POST)
    public CommonResult<String> updateCopyProductNum(@RequestBody @Validated MerchantUpdateProductNumRequest request) {
        if (merchantService.updateCopyProductNum(request)) {
            return CommonResult.success("修改复制商品数量成功");
        }
        return CommonResult.failed("修改复制商品数量失败");
    }

    @PreAuthorize("hasAuthority('platform:merchant:detail')")
    @ApiOperation(value="商户详情")
    @RequestMapping(value = "/detail/{id}", method = RequestMethod.GET)
    public CommonResult<MerchantPlatformDetailResponse> getDetail(@PathVariable("id") Integer id) {
        return CommonResult.success(merchantService.getPlatformDetail(id));
    }

    @LogControllerAnnotation(intoDB = true, methodType = MethodType.UPDATE, description = "推荐开关")
    @PreAuthorize("hasAuthority('platform:merchant:recommend:switch')")
    @ApiOperation(value="推荐开关")
    @RequestMapping(value = "/recommend/switch/{id}", method = RequestMethod.POST)
    public CommonResult<String> recommendSwitch(@PathVariable("id") Integer id) {
        if (merchantService.recommendSwitch(id)) {
            return CommonResult.success("切换商户推荐开关成功");
        }
        return CommonResult.failed("切换商户推荐开关失败");
    }

    @LogControllerAnnotation(intoDB = true, methodType = MethodType.UPDATE, description = "关闭商户")
    @PreAuthorize("hasAuthority('platform:merchant:close')")
    @ApiOperation(value="关闭商户")
    @RequestMapping(value = "/close/{id}", method = RequestMethod.POST)
    public CommonResult<String> close(@PathVariable("id") Integer id) {
        if (merchantService.close(id)) {
            return CommonResult.success("关闭商户成功");
        }
        return CommonResult.failed("关闭商户失败");
    }

    @LogControllerAnnotation(intoDB = true, methodType = MethodType.UPDATE, description = "开启商户")
    @PreAuthorize("hasAuthority('platform:merchant:open')")
    @ApiOperation(value="开启商户")
    @RequestMapping(value = "/open/{id}", method = RequestMethod.POST)
    public CommonResult<String> open(@PathVariable("id") Integer id) {
        if (merchantService.open(id)) {
            return CommonResult.success("开启商户成功");
        }
        return CommonResult.failed("开启商户失败");
    }

    @PreAuthorize("hasAuthority('platform:merchant:use:category:list')")
    @ApiOperation(value="可用分类商户列表")
    @RequestMapping(value = "/use/category/list", method = RequestMethod.GET)
    public CommonResult<List<CategoryMerchantResponse>> getUseCategoryList() {
        return CommonResult.success(merchantService.getUseCategoryList());
    }

    /**
     * FIXME: 风险说明: 此接口的操作存在很大的安全性隐患，强烈不建议用户使用，并将其注释掉
     */
    @LogControllerAnnotation(intoDB = true, methodType = MethodType.UPDATE, description = "越权登录商户")
    @PreAuthorize("hasAuthority('platform:merchant:ultra:vires:login')")
    @ApiOperation(value="越权登录商户")
    @RequestMapping(value = "/ultra/vires/login/{id}", method = RequestMethod.POST)
    public CommonResult<SystemLoginResponse> ultraViresLogin(@PathVariable("id") Integer id) {
        SystemLoginResponse response = adminLoginService.ultraViresLoginMerchantByPlat(id);
        return CommonResult.success(response);
    }
}
