package com.zbkj.front.controller;

import com.zbkj.common.model.product.ProductCategory;
import com.zbkj.common.result.CommonResult;
import com.zbkj.common.vo.ProCategoryCacheVo;
import com.zbkj.service.service.ProductCategoryService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 商品分类控制器
 *  +----------------------------------------------------------------------
 *  | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
 *  +----------------------------------------------------------------------
 *  | Copyright (c) 2016~2025 https://www.crmeb.com All rights reserved.
 *  +----------------------------------------------------------------------
 *  | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
 *  +----------------------------------------------------------------------
 *  | Author: CRMEB Team <admin@crmeb.com>
 *  +----------------------------------------------------------------------
 */
@Slf4j
@RestController
@RequestMapping("api/front/product/category")
@Api(tags = "商品分类控制器")
public class ProductCategoryController {

    @Autowired
    private ProductCategoryService productCategoryService;

    @ApiOperation(value = "获取第一级商品分类")
    @RequestMapping(value = "/get/first", method = RequestMethod.GET)
    public CommonResult<List<ProCategoryCacheVo>> getFirstCategory() {
        return CommonResult.success(productCategoryService.getFrontFirstCategory());
    }

    @ApiOperation(value = "获取首页第三级商品分类")
    @RequestMapping(value = "/get/third/{id}", method = RequestMethod.GET)
    public CommonResult<List<ProductCategory>> getHomeThirdCategory(@PathVariable(name = "id") Integer id) {
        return CommonResult.success(productCategoryService.getHomeThirdCategory(id));
    }

    @ApiOperation(value = "获取商品分类缓存树")
    @RequestMapping(value = "/get/tree", method = RequestMethod.GET)
    public CommonResult<List<ProCategoryCacheVo>> getCategory() {
        return CommonResult.success(productCategoryService.getMerchantCacheTree());
    }
}
