package com.zbkj.admin.controller.platform;

import com.zbkj.common.page.CommonPage;
import com.zbkj.common.request.PageParamRequest;
import com.zbkj.common.request.ProductReplySearchRequest;
import com.zbkj.common.response.ProductReplyResponse;
import com.zbkj.common.result.CommonResult;
import com.zbkj.service.service.ProductReplyService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;


/**
 * 平台端商品评论控制器
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
@RequestMapping("api/admin/platform/product/reply")
@Api(tags = "平台端商品评论控制器") //配合swagger使用
public class ProductReplyController {

    @Autowired
    private ProductReplyService productReplyService;

    @PreAuthorize("hasAuthority('platform:product:reply:list')")
    @ApiOperation(value = "商品评论分页列表") //配合swagger使用
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public CommonResult<CommonPage<ProductReplyResponse>> getList(@Validated ProductReplySearchRequest request) {
        return CommonResult.success(CommonPage.restPage(productReplyService.getAdminPage(request)));
    }

    @PreAuthorize("hasAuthority('platform:product:reply:delete')")
    @ApiOperation(value = "删除评论")
    @RequestMapping(value = "/delete/{id}", method = RequestMethod.GET)
    public CommonResult<String> delete(@PathVariable Integer id) {
        if (productReplyService.delete(id)) {
            return CommonResult.success();
        }
        return CommonResult.failed();
    }
}



