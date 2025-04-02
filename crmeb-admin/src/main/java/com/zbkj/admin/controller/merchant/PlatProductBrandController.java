package com.zbkj.admin.controller.merchant;

import com.zbkj.common.page.CommonPage;
import com.zbkj.common.request.BrandCategorySearchRequest;
import com.zbkj.common.request.PageParamRequest;
import com.zbkj.common.response.ProductBrandResponse;
import com.zbkj.common.result.CommonResult;
import com.zbkj.service.service.ProductBrandService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


/**
 * 平台端商品品牌控制器
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
@RequestMapping("api/admin/merchant/plat/product/brand")
@Api(tags = "商户端商品品牌控制器")
public class PlatProductBrandController {

    @Autowired
    private ProductBrandService productBrandService;

    @PreAuthorize("hasAuthority('merchant:plat:product:brand:list')")
    @ApiOperation(value = "品牌分页列表")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public CommonResult<CommonPage<ProductBrandResponse>> getList(@Validated BrandCategorySearchRequest request, @Validated PageParamRequest pageParamRequest) {
        return CommonResult.success(CommonPage.restPage(productBrandService.getPageListByCategory(request, pageParamRequest)));
    }

    @PreAuthorize("hasAuthority('merchant:plat:product:brand:cache:list')")
    @ApiOperation(value = "品牌缓存列表(全部)")
    @RequestMapping(value = "/cache/list", method = RequestMethod.GET)
    public CommonResult<List<ProductBrandResponse>> getCacheAllList() {
        return CommonResult.success(productBrandService.getCacheAllList());
    }
}



