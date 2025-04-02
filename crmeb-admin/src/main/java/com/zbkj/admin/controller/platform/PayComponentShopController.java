package com.zbkj.admin.controller.platform;

import com.zbkj.common.model.wechat.video.PayComponentShopBrand;
import com.zbkj.common.page.CommonPage;
import com.zbkj.common.request.PageParamRequest;
import com.zbkj.common.request.wxvedio.image.ShopUploadImgRequest;
import com.zbkj.common.result.CommonResult;
import com.zbkj.common.vo.wxvedioshop.WechatVideoUploadImageResponseVo;
import com.zbkj.common.vo.wxvedioshop.audit.*;
import com.zbkj.common.vo.wxvedioshop.cat_brand.ShopCatDetailVo;
import com.zbkj.service.service.PayComponentShopService;
import com.zbkj.service.service.WechatVideoBeforeService;
import com.zbkj.service.service.WechatVideoSpuService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 *  自定义交易组件—商家及接入前
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
@RestController
@RequestMapping("api/admin/platform/pay/component/shop")
@Api(tags = "02微信小程序 自定义交易组件—接入商品前必须接口") //配合swagger使用
public class PayComponentShopController {

    @Autowired
    private PayComponentShopService shopService;

    @Autowired
    private WechatVideoBeforeService wechatVideoBeforeService;

    @Autowired
    private WechatVideoSpuService wechatVideoSpuService;

    /***************************************** 接入之前必须调用 START ************************************************/

    @PreAuthorize("hasAuthority('platform:pay:component:shop:cat:get')")
    @ApiOperation(value = "获取类目详情")
    @RequestMapping(value = "/cat/get", method = RequestMethod.POST)
    public CommonResult<List<ShopCatDetailVo>> shopCatGet() {
        return CommonResult.success(wechatVideoSpuService.getShopCat());
    }

    @PreAuthorize("hasAuthority('platform:pay:component:shop:img:upload')")
    @ApiOperation(value = "上传图片 到微信自定义组件换链接")
    @RequestMapping(value = "/img/upload", method = RequestMethod.POST)
    public CommonResult<WechatVideoUploadImageResponseVo> shopImgUpload(@RequestBody ShopUploadImgRequest request) {
        return CommonResult.success(wechatVideoBeforeService.shopImgUpload(request));
    }

    @PreAuthorize("hasAuthority('platform:pay:component:shop:brand:audit')")
    @ApiOperation(value = "上传品牌信息")
    @RequestMapping(value = "/brand/audit", method = RequestMethod.POST)
    public CommonResult<String> shopAuditBrand(@RequestBody @Validated ShopAuditBrandRequestVo request) {
        return CommonResult.success(shopService.auditBrand(request));
    }

    @PreAuthorize("hasAuthority('platform:pay:component:shop:category:audit')")
    @ApiOperation(value = "上传类目资质")
    @RequestMapping(value = "/category/audit", method = RequestMethod.POST)
    public CommonResult<ShopAuditCategoryResponseVo> shopAuditCategory(@RequestBody @Validated ShopAuditCategoryRequestVo request) {
        return CommonResult.success(wechatVideoBeforeService.shopAuditCategory(request));
    }


    @PreAuthorize("hasAuthority('platform:pay:component:shop:audit:result')")
    @ApiOperation(value = "查询审核结果")
    @RequestMapping(value = "/audit/result", method = RequestMethod.POST)
    public CommonResult<ShopAuditResultResponseVo> shopAuditResult(@RequestBody @Validated ShopAuditResultCategoryAndBrandRequestVo request) {
        return CommonResult.success(wechatVideoBeforeService.shopAuditResult(request));
    }


    @PreAuthorize("hasAuthority('platform:pay:component:certificate')")
    @ApiOperation(value = "获取小程序提交过往的入驻资质信息")
    @RequestMapping(value = "/audit/certificate", method = RequestMethod.POST)
    public CommonResult<ShopAuditGetMiniAppCertificateRequestVo> shopAuditCertificate(@RequestBody @Validated ShopAuditGetMiniAppCertificateRequestVo request) {
        return CommonResult.success(wechatVideoBeforeService.shopAuditGetMinCertificate(request));
    }

    /***************************************** 接入之前必须调用 END ************************************************/


    @PreAuthorize("hasAuthority('platform:pay:component:shop:brand:list')")
    @ApiOperation(value = "品牌列表 分页")
    @RequestMapping(value = "/brand/list", method = RequestMethod.GET)
    public CommonResult<CommonPage<PayComponentShopBrand>> shopBrandList(@Validated PageParamRequest pageParamRequest, @RequestParam(value = "status", required = false) Integer status) {
        return CommonResult.success(CommonPage.restPage(shopService.brandList(pageParamRequest, status)));
    }

    @PreAuthorize("hasAuthority('platform:pay:component:shop:brand:usable:list')")
    @ApiOperation(value = "品牌列表 不分页")
    @RequestMapping(value = "/brand/usable/list", method = RequestMethod.GET)
    public CommonResult<List<PayComponentShopBrand>> shopUsableBrandList() {
        return CommonResult.success(shopService.usableBrandList());
    }
}
