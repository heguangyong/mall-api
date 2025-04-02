package com.zbkj.admin.controller.merchant;

import com.zbkj.common.model.product.ProductGuarantee;
import com.zbkj.common.result.CommonResult;
import com.zbkj.service.service.ProductGuaranteeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
@RequestMapping("api/admin/merchant/plat/product/guarantee")
@Api(tags = "商户端商品保障服务控制器")
public class PlatProductGuaranteeController {

    @Autowired
    private ProductGuaranteeService guaranteeService;

    @PreAuthorize("hasAuthority('merchant:plat:product:guarantee:list')")
    @ApiOperation(value = "保障服务列表")
    @ApiImplicitParam(name="type", value="类型，0-全部，1-有效", required = true)
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public CommonResult<List<ProductGuarantee>> getList(@RequestParam(name = "type", defaultValue = "0") Integer type) {
        return CommonResult.success(guaranteeService.getList(type));
    }
}



