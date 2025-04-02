package com.zbkj.admin.controller.platform;

import com.zbkj.common.request.wxvedio.forregister.ShopRegisterApplySceneRequest;
import com.zbkj.common.request.wxvedio.forregister.ShopRegisterFinishAccessInfoRequest;
import com.zbkj.common.result.CommonResult;
import com.zbkj.common.vo.wxvedioshop.BaseResultResponseVo;
import com.zbkj.common.vo.wxvedioshop.register.RegisterCheckResponseVo;
import com.zbkj.service.service.PayComponentShopService;
import com.zbkj.service.service.WechatVideoShopService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 *  自定义交易组件—商家接入
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
@RequestMapping("api/admin/platform/pay/component/register")
@Api(tags = "01微信小程序 自定义交易组件—申请接入") //配合swagger使用
public class PayComponentRegisterController {

    @Autowired
    private PayComponentShopService shopService;

    @Autowired
    private WechatVideoShopService wechatVideoShopService;

    /**
     * 微信小程序 自定义交易组件 接入申请
     * @return 申请结果
     */
    @PreAuthorize("hasAuthority('platform:pay:component:shop:register')")
    @ApiOperation(value = "自定义交易组件 接入申请")
    @RequestMapping(value = "/apply", method = RequestMethod.GET)
    public CommonResult<BaseResultResponseVo> shopRegisterApply() {
        return CommonResult.success(wechatVideoShopService.shopRegisterApply());
    }

    /**
     * 微信小程序 自定义交易组件 接入状态
     * 开通自定义交易组件时用此接口检查
     * @return 接入状态
     */
    @PreAuthorize("hasAuthority('platform:pay:component:shop:register:check')")
    @ApiOperation(value = "自定义交易组件 接入状态")
    @RequestMapping(value = "/register/check", method = RequestMethod.GET)
    public CommonResult<RegisterCheckResponseVo> shopRegisterCheck() {
        return CommonResult.success(wechatVideoShopService.shopRegisterCheck());
    }

    /**
     * 微信小程序 自定义交易组件 完成接入任务
     * @param srfai 6:完成 spu 接口，7:完成订单接口 / 19:完成二级商户号订单，8:完成物流接口，9:完成售后接口 / 20:完成二级商户号售后，10:测试完成，11:发版完成
     * @return 接入状态
     */
    @PreAuthorize("hasAuthority('platform:pay:component:shop:register:finish')")
    @ApiOperation(value = "自定义交易组件 完成接入任务")
    @RequestMapping(value = "/register/finishaccessinfo", method = RequestMethod.POST)
    public CommonResult<BaseResultResponseVo> shopRegisterFinishAccessInfo(ShopRegisterFinishAccessInfoRequest srfai) {
        return CommonResult.success(wechatVideoShopService.shopRegisterFinishAccessInfo(srfai));
    }

    /**
     * 微信小程序 自定义交易组件 场景接入申请
     * @param shopRegisterApplySceneRequest
     * @return 申请状态
     */
    @PreAuthorize("hasAuthority('platform:pay:component:shop:register:scene')")
    @ApiOperation(value = "自定义交易组件 场景接入申请")
    @RequestMapping(value = "/register/scene", method = RequestMethod.POST)
    public CommonResult<BaseResultResponseVo> shopRegisterFinishAccessInfo(ShopRegisterApplySceneRequest shopRegisterApplySceneRequest) {
        return CommonResult.success(wechatVideoShopService.shopRegisterApplyScene(shopRegisterApplySceneRequest));
    }

}
