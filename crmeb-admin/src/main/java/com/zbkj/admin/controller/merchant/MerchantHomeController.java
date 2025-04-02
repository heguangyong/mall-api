package com.zbkj.admin.controller.merchant;

import com.zbkj.admin.service.AdminHomeService;
import com.zbkj.service.service.HomeService;
import com.zbkj.common.response.HomeOperatingMerDataResponse;
import com.zbkj.common.response.HomeRateResponse;
import com.zbkj.common.response.ProductRankingResponse;
import com.zbkj.common.result.CommonResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


/**
 * 商户端主页控制器
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
@RequestMapping("api/admin/merchant/statistics/home")
@Api(tags = "商户端主页控制器")
public class MerchantHomeController {

    @Autowired
    private AdminHomeService adminHomeService;

    @PreAuthorize("hasAuthority('merchant:statistics:home:index')")
    @ApiOperation(value = "首页数据")
    @RequestMapping(value = "/index", method = RequestMethod.GET)
    public CommonResult<HomeRateResponse> indexDate() {
        return CommonResult.success(adminHomeService.indexMerchantDate());
    }

    @PreAuthorize("hasAuthority('merchant:statistics:home:operating:data')")
    @ApiOperation(value = "经营数据")
    @RequestMapping(value = "/operating/data", method = RequestMethod.GET)
    public CommonResult<HomeOperatingMerDataResponse> operatingData() {
        return CommonResult.success(adminHomeService.operatingMerchantData());
    }

    @PreAuthorize("hasAuthority('merchant:statistics:home:product:pay:ranking')")
    @ApiOperation(value = "商品支付排行榜")
    @RequestMapping(value = "/product/pay/ranking", method = RequestMethod.GET)
    public CommonResult<List<ProductRankingResponse>> productPayRanking() {
        return CommonResult.success(adminHomeService.merchantProductPayRanking());
    }

    @PreAuthorize("hasAuthority('merchant:statistics:home:product:pageview:ranking')")
    @ApiOperation(value = "商品浏览量排行榜")
    @RequestMapping(value = "/product/pageview/ranking", method = RequestMethod.GET)
    public CommonResult<List<ProductRankingResponse>> productPageviewRanking() {
        return CommonResult.success(adminHomeService.merchantProductPageviewRanking());
    }
}



