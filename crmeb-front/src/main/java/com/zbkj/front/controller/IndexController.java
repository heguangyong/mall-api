package com.zbkj.front.controller;


import cn.hutool.core.util.ObjectUtil;
import com.zbkj.common.exception.CrmebException;
import com.zbkj.common.model.seckill.SeckillProduct;
import com.zbkj.common.model.system.SystemConfig;
import com.zbkj.common.page.CommonPage;
import com.zbkj.common.request.PageParamRequest;
import com.zbkj.common.response.*;
import com.zbkj.common.result.CommonResult;
import com.zbkj.common.vo.SplashAdConfigVo;
import com.zbkj.front.service.IndexService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;

/**
 * 首页控制器
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
@RequestMapping("api/front/index")
@Api(tags = "首页控制器")
public class IndexController {

    @Autowired
    private IndexService indexService;

    @ApiOperation(value = "首页数据")
    @RequestMapping(value = "/info", method = RequestMethod.GET)
    public CommonResult<IndexInfoResponse> getIndexInfo() {
        return CommonResult.success(indexService.getIndexInfo());
    }

    @ApiOperation(value = "首页商品列表")
    @RequestMapping(value = "/product/list", method = RequestMethod.GET)
    @ApiImplicitParam(name="cid", value="一级商品分类id，全部传0", required = true)
    public CommonResult<CommonPage<ProductCommonResponse>> getProductList(@RequestParam(value = "cid") Integer cid,
                                                                          PageParamRequest pageParamRequest) {
        return CommonResult.success(CommonPage.restPage(indexService.findIndexProductList(cid, pageParamRequest)));
    }

    @ApiOperation(value = "首页店铺列表-根据数量加载")
    @RequestMapping(value = "/merchant/list/{recomdnum}", method = RequestMethod.GET)
    public CommonResult<CommonPage<IndexMerchantResponse>> getMerchantListByRecomdNum(@PathVariable(name = "recomdnum", required = false) Integer recomdnum) {
        return CommonResult.success(CommonPage.restPage(indexService.findIndexMerchantListByRecomdNum(recomdnum)));
    }

    @ApiOperation(value = "首页店铺列表-根据id集合加载")
    @RequestMapping(value = "/merchant/listbyids/{ids}", method = RequestMethod.GET)
    public CommonResult<List<IndexMerchantResponse>> getMerchantListByIds(@PathVariable(name = "ids", required = false) String ids) {
        return CommonResult.success(indexService.findIndexMerchantListByIds(ids));
    }

    @ApiOperation(value = "热门搜索")
    @RequestMapping(value = "/search/keyword", method = RequestMethod.GET)
    public CommonResult<List<HashMap<String, Object>>> hotKeywords() {
        return CommonResult.success(indexService.hotKeywords());
    }

    @ApiOperation(value = "颜色配置")
    @RequestMapping(value = "/color/config", method = RequestMethod.GET)
    public CommonResult<SystemConfig> getColorConfig() {
        return CommonResult.success(indexService.getColorConfig());
    }

    @ApiOperation(value = "全局本地图片域名")
    @RequestMapping(value = "/image/domain", method = RequestMethod.GET)
    public CommonResult<String> getImageDomain() {
        return CommonResult.success(indexService.getImageDomain());
    }

    @ApiOperation(value = "版权图片")
    @RequestMapping(value = "/copyright/company/image", method = RequestMethod.GET)
    public CommonResult<Object> getCopyrightCompanyImage() {
        return CommonResult.success(indexService.getCopyrightCompanyImage());
    }

    @ApiOperation(value = "首页秒杀信息")
    @RequestMapping(value = "/seckill/info", method = RequestMethod.GET)
    public CommonResult<List<SeckillProduct>> getIndexSeckillInfo() {
        return CommonResult.success(indexService.getIndexSeckillInfo());
    }

    @ApiOperation(value = "首页优惠券")
    @RequestMapping(value = "/coupon/info/{limit}", method = RequestMethod.GET)
    public CommonResult<List<CouponFrontResponse>> getIndexCouponInfo(@PathVariable(value = "limit") Integer limit) {
        if(ObjectUtil.isNull(limit) || limit <=0) throw new CrmebException("limit参数不合法");
        if(limit >= 8) throw new CrmebException("首页 组件 优惠券上限不得超过 8 条");
        return CommonResult.success(indexService.getIndexCouponInfo(limit));
    }

    @ApiOperation(value = "获取系统时间")
    @RequestMapping(value = "/get/system/time", method = RequestMethod.GET)
    public CommonResult<Long> getSystemTime() {
        return CommonResult.success(System.currentTimeMillis());
    }

    @ApiOperation(value = "获取底部导航信息")
    @RequestMapping(value = "/get/bottom/navigation", method = RequestMethod.GET)
    public CommonResult<PageLayoutBottomNavigationResponse> getBottomNavigation() {
        return CommonResult.success(indexService.getBottomNavigationInfo());
    }

    @ApiOperation(value = "获取版本信息")
    @RequestMapping(value = "/index/get/version", method = RequestMethod.GET)
    public CommonResult<AppVersionResponse> getVersion() {
        return CommonResult.success(indexService.getVersion());
    }

    @ApiOperation(value = "获取版权信息")
    @RequestMapping(value = "/copyright/info", method = RequestMethod.GET)
    public CommonResult<CopyrightConfigInfoResponse> getCopyrightInfo() {
        return CommonResult.success(indexService.getCopyrightInfo());
    }

    @ApiOperation(value = "获取移动端域名")
    @RequestMapping(value = "/get/domain", method = RequestMethod.GET)
    public CommonResult<String> getFrontDomain() {
        return CommonResult.success(indexService.getFrontDomain());
    }

    @ApiOperation(value = "获取平台客服")
    @RequestMapping(value = "/get/customer/service", method = RequestMethod.GET)
    public CommonResult<CustomerServiceResponse> getPlatCustomerService() {
        return CommonResult.success(indexService.getPlatCustomerService());
    }

    @ApiOperation(value = "全局配置信息")
    @RequestMapping(value = "/global/config/info", method = RequestMethod.GET)
    public CommonResult<FrontGlobalConfigResponse> getGlobalConfigInfo() {
        return CommonResult.success(indexService.getGlobalConfigInfo());
    }

    @ApiOperation(value = "获取开屏广告信息")
    @RequestMapping(value = "/splash/ad/info", method = RequestMethod.GET)
    public CommonResult<SplashAdConfigVo> getSplashAdInfo() {
        return CommonResult.success(indexService.getSplashAdInfo());
    }
}



