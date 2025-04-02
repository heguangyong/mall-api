package com.zbkj.front.controller;

import com.zbkj.common.response.page.PageDiyResponse;
import com.zbkj.common.result.CommonResult;
import com.zbkj.service.service.PageDiyService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * 商户装修控制器
 *
 * @author Han
 * @version 1.0.0
 * @Date 2024/11/1
 */
@Slf4j
@RestController
@RequestMapping("api/front/merchant/page/diy")
@Api(tags = "商户装修控制器") //配合swagger使用
public class MerchantPageDiyController {

    @Autowired
    private PageDiyService pageDiyService;

    @ApiOperation(value = "店铺装修详情")
    @RequestMapping(value = "/info/{merId}/{id}", method = RequestMethod.GET)
    public CommonResult<PageDiyResponse> info(@PathVariable(value = "merId") Integer merId, @PathVariable(value = "id", required = false) Integer id) {
        return CommonResult.success(pageDiyService.getMerchantPageDiyInfoByFront(merId, id));
    }

}
