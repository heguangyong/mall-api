package com.zbkj.admin.controller.merchant;

import com.zbkj.common.page.CommonPage;
import com.zbkj.common.request.ProductReplyCommentRequest;
import com.zbkj.common.request.ProductReplySearchRequest;
import com.zbkj.common.request.ProductReplyVirtualRequest;
import com.zbkj.common.response.ProductReplyResponse;
import com.zbkj.common.result.CommonResult;
import com.zbkj.service.service.ProductReplyService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


/**
 * 商户端商品评论控制器
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
@RequestMapping("api/admin/merchant/product/reply")
@Api(tags = "商户端商品评论控制器") //配合swagger使用
public class MerchantProductReplyController {

    @Autowired
    private ProductReplyService storeProductReplyService;

    @PreAuthorize("hasAuthority('merchant:product:reply:page:list')")
    @ApiOperation(value = "商户端商品评论分页列表") //配合swagger使用
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public CommonResult<CommonPage<ProductReplyResponse>> getList(@Validated ProductReplySearchRequest request) {
        return CommonResult.success(CommonPage.restPage(storeProductReplyService.getMerchantAdminPage(request)));
    }

    @PreAuthorize("hasAuthority('merchant:product:reply:virtual')")
    @ApiOperation(value = "虚拟评论")
    @RequestMapping(value = "/virtual", method = RequestMethod.POST)
    public CommonResult<String> virtual(@RequestBody @Validated ProductReplyVirtualRequest request) {
        if (storeProductReplyService.virtualCreate(request)) {
            return CommonResult.success();
        }
        return CommonResult.failed();
    }

    @PreAuthorize("hasAuthority('merchant:product:reply:delete')")
    @ApiOperation(value = "删除评论")
    @RequestMapping(value = "/delete/{id}", method = RequestMethod.POST)
    public CommonResult<String> delete(@PathVariable Integer id) {
        if (storeProductReplyService.delete(id)) {
            return CommonResult.success();
        }
        return CommonResult.failed();
    }

    @PreAuthorize("hasAuthority('merchant:product:reply:comment')")
   @ApiOperation(value = "回复评论")
   @RequestMapping(value = "/comment", method = RequestMethod.POST)
   public CommonResult<String> comment(@RequestBody ProductReplyCommentRequest request) {
       if (storeProductReplyService.comment(request)) {
           return CommonResult.success();
       }
       return CommonResult.failed();
   }
}



