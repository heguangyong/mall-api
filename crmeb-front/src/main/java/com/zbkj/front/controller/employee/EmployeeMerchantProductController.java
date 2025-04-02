package com.zbkj.front.controller.employee;

import com.zbkj.common.annotation.LogControllerAnnotation;
import com.zbkj.common.enums.MethodType;
import com.zbkj.common.page.CommonPage;
import com.zbkj.common.request.*;
import com.zbkj.common.response.AdminProductListResponse;
import com.zbkj.common.response.ProductInfoResponse;
import com.zbkj.common.response.ProductTabsHeaderResponse;
import com.zbkj.common.result.CommonResult;
import com.zbkj.service.service.ProductEmployeeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;


/**
 * 移动端商家管理 - 商品控制器
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
@RequestMapping("api/front/employee/product")
@Api(tags = "移动端商家管理 - 商品控制器") //配合swagger使用
public class EmployeeMerchantProductController {

    @Autowired
    private ProductEmployeeService productService;

    @ApiOperation(value = "商品分页列表") //配合swagger使用
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public CommonResult<CommonPage<AdminProductListResponse>> getList(@Validated MerProductSearchRequest request) {
        return CommonResult.success(CommonPage.restPage(productService.getAdminList(request)));
    }


    @LogControllerAnnotation(intoDB = true, methodType = MethodType.DELETE, description = "删除商品")
    @ApiOperation(value = "删除商品")
    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    public CommonResult<String> delete(@RequestBody @Validated ProductDeleteRequest request) {
        if (productService.deleteProduct(request)) {
            return CommonResult.success();
        }
        return CommonResult.failed();
    }

    @LogControllerAnnotation(intoDB = true, methodType = MethodType.UPDATE, description = "恢复回收站商品")
    @ApiOperation(value = "恢复回收站商品")
    @RequestMapping(value = "/restore/{id}", method = RequestMethod.POST)
    public CommonResult<String> restore(@PathVariable Integer id) {
        if (productService.restoreProduct(id)) {
            return CommonResult.success();
        }
        return CommonResult.failed();
    }

    @ApiOperation(value = "商品详情")
    @RequestMapping(value = "/info/{id}", method = RequestMethod.GET)
    public CommonResult<ProductInfoResponse> info(@PathVariable Integer id) {
        return CommonResult.success(productService.getInfo(id));
   }

   @ApiOperation(value = "商品表头数量")
   @RequestMapping(value = "/tabs/headers", method = RequestMethod.GET)
   public CommonResult<List<ProductTabsHeaderResponse>> getTabsHeader(@Validated MerProductTabsHeaderRequest request) {
        return CommonResult.success(productService.getTabsHeader(request));
   }

    @LogControllerAnnotation(intoDB = true, methodType = MethodType.UPDATE, description = "商品提审")
    @ApiOperation(value = "商品提审")
    @RequestMapping(value = "/submit/audit/{id}", method = RequestMethod.POST)
    public CommonResult<String> submitAudit(@PathVariable Integer id) {
        ProductSubmitAuditRequest request = new ProductSubmitAuditRequest();
        request.setId(id);
        if (productService.submitAudit(request)) {
            return CommonResult.success();
        }
        return CommonResult.failed();
    }

    @LogControllerAnnotation(intoDB = true, methodType = MethodType.UPDATE, description = "上架商品")
    @ApiOperation(value = "商品上架")
    @RequestMapping(value = "/up/{id}", method = RequestMethod.POST)
    public CommonResult<String> up(@PathVariable Integer id) {
        if (productService.putOnShelf(id)) {
            return CommonResult.success();
        }
        return CommonResult.failed();
    }

    @LogControllerAnnotation(intoDB = true, methodType = MethodType.UPDATE, description = "下架商品")
    @ApiOperation(value = "商品下架")
    @RequestMapping(value = "/down/{id}", method = RequestMethod.POST)
    public CommonResult<String> down(@PathVariable Integer id) {
        if (productService.offShelf(id)) {
            return CommonResult.success();
        }
        return CommonResult.failed();
    }

    @LogControllerAnnotation(intoDB = true, methodType = MethodType.UPDATE, description = "快捷添加库存")
    @ApiOperation(value = "快捷添加库存")
    @RequestMapping(value = "/quick/stock/add", method = RequestMethod.POST)
    public CommonResult<String> quickAddStock(@RequestBody @Validated ProductAddStockRequest request) {
        if (productService.quickAddStock(request)) {
            return CommonResult.success();
        }
        return CommonResult.failed();
    }

    @LogControllerAnnotation(intoDB = true, methodType = MethodType.UPDATE, description = "商品免审编辑")
    @ApiOperation(value = "商品免审编辑")
    @RequestMapping(value = "/review/free/edit", method = RequestMethod.POST)
    public CommonResult<String> reviewFreeEdit(@RequestBody @Validated ProductReviewFreeEditRequest request) {
        if (productService.reviewFreeEdit(request)) {
            return CommonResult.success();
        }
        return CommonResult.failed();
    }


    @LogControllerAnnotation(intoDB = true, methodType = MethodType.UPDATE, description = "商品无状态编辑 - 风险操作")
    @ApiOperation(value = "商品无状态编辑")
    @RequestMapping(value = "/anytime/edit", method = RequestMethod.POST)
    public CommonResult<String> anyTimeEdit(@RequestBody @Validated ProductAnyTimeEditRequest request) {
        if (productService.merchantEmployeeAnyTimeEdit(request)) {
            return CommonResult.success();
        }
        return CommonResult.failed();
    }


    @LogControllerAnnotation(intoDB = true, methodType = MethodType.UPDATE, description = "批量上架商品")
    @ApiOperation(value = "批量上架商品")
    @RequestMapping(value = "/batch/up", method = RequestMethod.POST)
    public CommonResult<String> batchUp(@RequestBody @Valid CommonBatchRequest request) {
        if (productService.batchUp(request.getIdList())) {
            return CommonResult.success();
        }
        return CommonResult.failed();
    }

    @LogControllerAnnotation(intoDB = true, methodType = MethodType.UPDATE, description = "批量下架商品")
    @ApiOperation(value = "批量商品下架")
    @RequestMapping(value = "/batch/down", method = RequestMethod.POST)
    public CommonResult<String> batchDown(@RequestBody @Valid CommonBatchRequest request) {
        if (productService.batchDown(request.getIdList())) {
            return CommonResult.success();
        }
        return CommonResult.failed();
    }

    @LogControllerAnnotation(intoDB = true, methodType = MethodType.DELETE, description = "批量加入回收站")
    @ApiOperation(value = "批量加入回收站")
    @RequestMapping(value = "/batch/recycle", method = RequestMethod.POST)
    public CommonResult<String> batchRecycle(@RequestBody @Validated CommonBatchRequest request) {
        if (productService.batchRecycle(request.getIdList())) {
            return CommonResult.success();
        }
        return CommonResult.failed();
    }

    @LogControllerAnnotation(intoDB = true, methodType = MethodType.DELETE, description = "批量删除商品")
    @ApiOperation(value = "批量删除商品")
    @RequestMapping(value = "/batch/delete", method = RequestMethod.POST)
    public CommonResult<String> batchDelete(@RequestBody @Validated CommonBatchRequest request) {
        if (productService.batchDelete(request.getIdList())) {
            return CommonResult.success();
        }
        return CommonResult.failed();
    }

    @LogControllerAnnotation(intoDB = true, methodType = MethodType.UPDATE, description = "批量恢复回收站商品")
    @ApiOperation(value = "批量恢复回收站商品")
    @RequestMapping(value = "/batch/restore", method = RequestMethod.POST)
    public CommonResult<String> batchRestore(@RequestBody @Validated CommonBatchRequest request) {
        if (productService.batchRestore(request.getIdList())) {
            return CommonResult.success();
        }
        return CommonResult.failed();
    }

    @LogControllerAnnotation(intoDB = true, methodType = MethodType.UPDATE, description = "批量提审商品")
    @ApiOperation(value = "批量提审商品")
    @RequestMapping(value = "/batch/submit/audit", method = RequestMethod.POST)
    public CommonResult<String> batchSubmitAudit(@RequestBody @Validated CommonBatchRequest request) {
        if (productService.batchSubmitAudit(request.getIdList())) {
            return CommonResult.success();
        }
        return CommonResult.failed();
    }
}



