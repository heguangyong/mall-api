package com.zbkj.front.controller.pc;

import com.zbkj.common.page.CommonPage;
import com.zbkj.common.request.PageParamRequest;
import com.zbkj.common.response.PcHomeRecommendedResponse;
import com.zbkj.common.response.PcShoppingConfigResponse;
import com.zbkj.common.response.ProductRecommendedResponse;
import com.zbkj.common.result.CommonResult;
import com.zbkj.common.vo.PcHomeBannerVo;
import com.zbkj.front.service.PcShoppingService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Pc首页控制器
 *
 * @author HZW
 * @version 1.0.0
 * @Date 2023/9/27
 */
@Slf4j
@RestController
@RequestMapping("api/front/pc/home")
@Api(tags = "Pc首页控制器")
public class PcHomeController {

    @Autowired
    private PcShoppingService pcShoppingService;

    @ApiOperation(value = "获取首页配置信息")
    @RequestMapping(value = "/get/config", method = RequestMethod.GET)
    public CommonResult<PcShoppingConfigResponse> getHomeConfig() {
        return CommonResult.success(pcShoppingService.getHomeConfig());
    }

    @ApiOperation(value = "获取首页banner")
    @RequestMapping(value = "/get/banner", method = RequestMethod.GET)
    public CommonResult<List<PcHomeBannerVo>> getHomeBanner() {
        return CommonResult.success(pcShoppingService.getHomeBanner());
    }

    @ApiOperation(value = "获取首页推荐板块")
    @RequestMapping(value = "/get/recommended", method = RequestMethod.GET)
    public CommonResult<List<PcHomeRecommendedResponse>> getHomeRecommended() {
        return CommonResult.success(pcShoppingService.getHomeRecommended());
    }

    @ApiOperation(value = "推荐板块商品分页列表")
    @RequestMapping(value = "/recommended/{id}/product/page", method = RequestMethod.GET)
    public CommonResult<CommonPage<ProductRecommendedResponse>> findRecommendedProductPage(@PathVariable Integer id, @ModelAttribute PageParamRequest pageRequest) {
        return CommonResult.success(CommonPage.restPage(pcShoppingService.findRecommendedProductPage(id, pageRequest)));
    }
}
