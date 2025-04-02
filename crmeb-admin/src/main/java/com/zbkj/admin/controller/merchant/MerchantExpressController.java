package com.zbkj.admin.controller.merchant;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageInfo;
import com.zbkj.common.annotation.LogControllerAnnotation;
import com.zbkj.common.enums.MethodType;
import com.zbkj.common.model.express.Express;
import com.zbkj.common.model.express.MerchantExpress;
import com.zbkj.common.page.CommonPage;
import com.zbkj.common.request.ExpressUpdateRequest;
import com.zbkj.common.request.MerchantExpressSearchRequest;
import com.zbkj.common.request.MerchantRelateExpressRequest;
import com.zbkj.common.request.merchant.MerchantExpressRequest;
import com.zbkj.common.result.CommonResult;
import com.zbkj.service.service.ExpressService;
import com.zbkj.service.service.MerchantExpressService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;


/**
 * 商户端物流公司控制器
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
@RequestMapping("api/admin/merchant/express")
@Api(tags = "商户端物流公司控制器")
public class MerchantExpressController {

    @Autowired
    private ExpressService expressService;
    @Autowired
    private MerchantExpressService merchantExpressService;

    @PreAuthorize("hasAuthority('merchant:express:all')")
    @ApiOperation(value = "查询全部物流公司")
    @RequestMapping(value = "/all", method = RequestMethod.GET)
    @ApiImplicitParam(name = "type", value = "类型：normal-普通，elec-电子面单", required = true)
    public CommonResult<List<Express>> all(@RequestParam(value = "type") String type) {
        return CommonResult.success(expressService.findAll(type));
    }

    @PreAuthorize("hasAuthority('merchant:express:template')")
    @ApiOperation(value = "查询物流公司面单模板")
    @RequestMapping(value = "/template", method = RequestMethod.GET)
    @ApiImplicitParam(name = "com", value = "快递公司编号", required = true)
    public CommonResult<JSONObject> template(@RequestParam(value = "com") String com) {
        return CommonResult.success(expressService.template(com));
    }

    @PreAuthorize("hasAuthority('merchant:express:relate')")
    @LogControllerAnnotation(intoDB = true, methodType = MethodType.ADD, description = "商户关联物流公司")
    @ApiOperation(value = "商户关联物流公司")
    @RequestMapping(value = "/relate", method = RequestMethod.POST)
    public CommonResult<String> relate(@RequestBody @Validated MerchantRelateExpressRequest request) {
        if (merchantExpressService.relate(request.getExpressId())) {
            return CommonResult.success();
        }
        return CommonResult.failed();
    }

    @PreAuthorize("hasAuthority('merchant:express:update')")
    @ApiOperation(value = "配置物流月结账号 - 电子面单")
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public CommonResult<String> update(@RequestBody @Validated MerchantExpressRequest expressRequest) {
        MerchantExpress merchantExpress = new MerchantExpress();
        BeanUtils.copyProperties(expressRequest, merchantExpress);
        if (merchantExpressService.updateMerchantExpress(merchantExpress)) {
            return CommonResult.success();
        }
        return CommonResult.failed();
    }

    @PreAuthorize("hasAuthority('merchant:express:search:page')")
    @ApiOperation(value = "商户物流公司分页列表")
    @RequestMapping(value = "/search/page", method = RequestMethod.GET)
    public CommonResult<CommonPage<MerchantExpress>> searchPage(@Validated MerchantExpressSearchRequest request) {
        PageInfo<MerchantExpress> pageInfo = merchantExpressService.searchPage(request);
        return CommonResult.success(CommonPage.restPage(pageInfo));
    }

    @PreAuthorize("hasAuthority('merchant:express:open:switch')")
    @LogControllerAnnotation(intoDB = true, methodType = MethodType.ADD, description = "商户物流公司开关")
    @ApiOperation(value = "商户物流公司开关")
    @RequestMapping(value = "/open/switch/{id}", method = RequestMethod.POST)
    public CommonResult<String> openSwitch(@PathVariable(name = "id") Integer id) {
        if (merchantExpressService.openSwitch(id)) {
            return CommonResult.success();
        }
        return CommonResult.failed();
    }

    @PreAuthorize("hasAuthority('merchant:express:default:switch')")
    @LogControllerAnnotation(intoDB = true, methodType = MethodType.ADD, description = "商户物流公司默认开关")
    @ApiOperation(value = "商户物流公司默认开关")
    @RequestMapping(value = "/default/switch/{id}", method = RequestMethod.POST)
    public CommonResult<String> defaultSwitch(@PathVariable(name = "id") Integer id) {
        if (merchantExpressService.defaultSwitch(id)) {
            return CommonResult.success();
        }
        return CommonResult.failed();
    }

    @PreAuthorize("hasAuthority('merchant:express:delete')")
    @LogControllerAnnotation(intoDB = true, methodType = MethodType.ADD, description = "商户物流公司删除")
    @ApiOperation(value = "商户物流公司删除")
    @RequestMapping(value = "/delete/{id}", method = RequestMethod.POST)
    public CommonResult<String> delete(@PathVariable(name = "id") Integer id) {
        if (merchantExpressService.delete(id)) {
            return CommonResult.success();
        }
        return CommonResult.failed();
    }
}



