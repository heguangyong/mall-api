package com.zbkj.admin.controller.merchant;

import com.zbkj.common.annotation.LogControllerAnnotation;
import com.zbkj.common.enums.MethodType;
import com.zbkj.common.model.product.ProductRule;
import com.zbkj.common.page.CommonPage;
import com.zbkj.common.request.PageParamRequest;
import com.zbkj.common.request.ProductRuleRequest;
import com.zbkj.common.request.ProductRuleSearchRequest;
import com.zbkj.common.result.CommonResult;
import com.zbkj.service.service.ProductRuleService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


/**
 * 商品规则值(规格)控制器
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
@RequestMapping("api/admin/merchant/product/rule")
@Api(tags = "商户端商品规则值(规格)控制器") //配合swagger使用
public class MerchantProductRuleController {

    @Autowired
    private ProductRuleService productRuleService;

    @PreAuthorize("hasAuthority('merchant:product:rule:page:list')")
    @ApiOperation(value = "商户端商品规则值(规格)分页列表") //配合swagger使用
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public CommonResult<CommonPage<ProductRule>> getList(@Validated ProductRuleSearchRequest request,
                                                         @Validated PageParamRequest pageParamRequest) {
        return CommonResult.success(CommonPage.restPage(productRuleService.getList(request, pageParamRequest)));
    }

    @PreAuthorize("hasAuthority('merchant:product:rule:save')")
    @LogControllerAnnotation(intoDB = true, methodType = MethodType.ADD, description = "新增商品规格")
    @ApiOperation(value = "新增商品规格")
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    public CommonResult<String> save(@RequestBody @Validated ProductRuleRequest ProductRuleRequest) {
        if (productRuleService.save(ProductRuleRequest)) {
            return CommonResult.success();
        } else {
            return CommonResult.failed();
        }
    }

    @PreAuthorize("hasAuthority('merchant:product:rule:delete')")
    @LogControllerAnnotation(intoDB = true, methodType = MethodType.DELETE, description = "删除商品规格")
    @ApiOperation(value = "删除商品规格")
    @RequestMapping(value = "/delete/{id}", method = RequestMethod.POST)
    public CommonResult<String> delete(@PathVariable Integer id) {
        if (productRuleService.deleteById(id)) {
            return CommonResult.success();
        } else {
            return CommonResult.failed();
        }
    }

    @PreAuthorize("hasAuthority('merchant:product:rule:update')")
    @LogControllerAnnotation(intoDB = true, methodType = MethodType.UPDATE, description = "修改商品规格")
    @ApiOperation(value = "修改商品规格")
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public CommonResult<String> update(@RequestBody @Validated ProductRuleRequest ProductRuleRequest) {
        if (productRuleService.updateRule(ProductRuleRequest)) {
            return CommonResult.success();
        } else {
            return CommonResult.failed();
        }
    }

    @PreAuthorize("hasAuthority('merchant:product:rule:info')")
    @ApiOperation(value = "商品规格详情")
    @RequestMapping(value = "/info/{id}", method = RequestMethod.GET)
    public CommonResult<ProductRule> info(@PathVariable Integer id) {
        return CommonResult.success(productRuleService.getRuleInfo(id));
   }
}



