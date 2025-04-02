package com.zbkj.front.controller.employee;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageInfo;
import com.zbkj.common.annotation.LogControllerAnnotation;
import com.zbkj.common.enums.MethodType;
import com.zbkj.common.model.express.Express;
import com.zbkj.common.model.express.MerchantExpress;
import com.zbkj.common.page.CommonPage;
import com.zbkj.common.request.MerchantExpressSearchRequest;
import com.zbkj.common.request.MerchantRelateExpressRequest;
import com.zbkj.common.result.CommonResult;
import com.zbkj.service.service.EmployeeMerchantExpressService;
import com.zbkj.service.service.ExpressService;
import com.zbkj.service.service.MerchantExpressService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
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
@RequestMapping("api/front/employee/express")
@Api(tags = "移动端商家管理 - 商户端物流公司控制器")
public class EmployeeMerchantExpressController {

    @Autowired
    private EmployeeMerchantExpressService expressService;
    @ApiOperation(value = "商户物流公司分页列表")
    @RequestMapping(value = "/search/page", method = RequestMethod.GET)
    public CommonResult<CommonPage<MerchantExpress>> searchPage(@Validated MerchantExpressSearchRequest request) {
        PageInfo<MerchantExpress> pageInfo = expressService.searchPage(request);
        return CommonResult.success(CommonPage.restPage(pageInfo));
    }

    @ApiOperation(value = "查询物流公司面单模板")
    @RequestMapping(value = "/template", method = RequestMethod.GET)
    @ApiImplicitParam(name = "com", value = "快递公司编号", required = true)
    public CommonResult<JSONObject> template(@RequestParam(value = "com") String com) {
        return CommonResult.success(expressService.template(com));
    }
}



