package com.zbkj.admin.controller.merchant;

import com.zbkj.common.model.wechat.video.PayComponentShopBrand;
import com.zbkj.common.page.CommonPage;
import com.zbkj.common.request.PageParamRequest;
import com.zbkj.common.request.wxvedio.image.ShopUploadImgRequest;
import com.zbkj.common.result.CommonResult;
import com.zbkj.common.vo.wxvedioshop.WechatVideoUploadImageResponseVo;
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
@RequestMapping("api/admin/merchant/pay/component/shop")
@Api(tags = "02微信小程序 自定义交易组件—接入商品前必须接口") //配合swagger使用
public class PayComponentShopMerchantController {

    @Autowired
    private PayComponentShopService shopService;

    @Autowired
    private WechatVideoBeforeService wechatVideoBeforeService;

    @Autowired
    private WechatVideoSpuService wechatVideoSpuService;

    /***************************************** 接入之前必须调用 START ************************************************/

    @PreAuthorize("hasAuthority('merchant:pay:component:shop:cat:get')")
    @ApiOperation(value = "获取类目详情")
    @RequestMapping(value = "/cat/get", method = RequestMethod.POST)
    public CommonResult<List<ShopCatDetailVo>> shopCatGet() {
        return CommonResult.success(wechatVideoSpuService.getShopCat());
    }

    @PreAuthorize("hasAuthority('merchant:pay:component:shop:img:upload')")
    @ApiOperation(value = "上传图片 到微信自定义组件换链接")
    @RequestMapping(value = "/img/upload", method = RequestMethod.POST)
    public CommonResult<WechatVideoUploadImageResponseVo> shopImgUpload(@RequestBody ShopUploadImgRequest request) {
        return CommonResult.success(wechatVideoBeforeService.shopImgUpload(request));
    }

    /***************************************** 接入之前必须调用 END ************************************************/


    @PreAuthorize("hasAuthority('merchant:pay:component:shop:brand:list')")
    @ApiOperation(value = "品牌列表 分页")
    @RequestMapping(value = "/brand/list", method = RequestMethod.GET)
    public CommonResult<CommonPage<PayComponentShopBrand>> shopBrandList(@Validated PageParamRequest pageParamRequest, @RequestParam(value = "status", required = false) Integer status) {
        return CommonResult.success(CommonPage.restPage(shopService.brandList(pageParamRequest, status)));
    }

    @PreAuthorize("hasAuthority('merchant:pay:component:shop:brand:usable:list')")
    @ApiOperation(value = "品牌列表 不分页")
    @RequestMapping(value = "/brand/usable/list", method = RequestMethod.GET)
    public CommonResult<List<PayComponentShopBrand>> shopUsableBrandList() {
        return CommonResult.success(shopService.usableBrandList());
    }
}
