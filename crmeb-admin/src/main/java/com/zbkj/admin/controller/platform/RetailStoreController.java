package com.zbkj.admin.controller.platform;

import com.zbkj.common.model.user.User;
import com.zbkj.common.page.CommonPage;
import com.zbkj.common.request.PageParamRequest;
import com.zbkj.common.request.RetailStorePeopleSearchRequest;
import com.zbkj.common.request.RetailStoreSubUserSearchRequest;
import com.zbkj.common.request.UserUpdateSpreadRequest;
import com.zbkj.common.response.PromotionOrderResponse;
import com.zbkj.common.response.SpreadUserResponse;
import com.zbkj.common.result.CommonResult;
import com.zbkj.common.vo.RetailStoreConfigVo;
import com.zbkj.service.service.RetailStoreService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 分销控制器
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
@RequestMapping("api/admin/platform/retail/store")
@Api(tags = "分销控制器")
public class RetailStoreController {

    @Autowired
    private RetailStoreService retailStoreService;

    @PreAuthorize("hasAuthority('platform:retail:store:config:get')")
    @ApiOperation(value = "分销配置信息获取")
    @RequestMapping(value = "/config/get", method = RequestMethod.GET)
    public CommonResult<RetailStoreConfigVo> getRetailStoreConfig() {
        return CommonResult.success(retailStoreService.getRetailStoreConfig());
    }

    @PreAuthorize("hasAuthority('platform:retail:store:config:save')")
    @ApiOperation(value = "分销配置信息保存")
    @RequestMapping(value = "/config/save", method = RequestMethod.POST)
    public CommonResult<Object> saveRetailStoreConfig(@RequestBody @Validated RetailStoreConfigVo retailShopRequest) {
        return CommonResult.success(retailStoreService.saveRetailStoreConfig(retailShopRequest));
    }

    @PreAuthorize("hasAuthority('platform:retail:store:update:user:spread')")
    @ApiOperation(value = "修改用户上级推广人")
    @RequestMapping(value = "/update/user/spread", method = RequestMethod.POST)
    public CommonResult<String> updateUserSpread(@Validated @RequestBody UserUpdateSpreadRequest request) {
        if (retailStoreService.updateUserSpread(request)) {
            return CommonResult.success("修改成功");
        }
        return CommonResult.failed("修改失败");
    }

    @PreAuthorize("hasAuthority('platform:retail:store:clean:user:spread')")
    @ApiOperation(value = "清除用户上级推广人")
    @RequestMapping(value = "/clean/spread/{id}", method = RequestMethod.GET)
    public CommonResult<Object> clearUserSpread(@PathVariable Integer id) {
        return CommonResult.success(retailStoreService.clearUserSpread(id));
    }

    @PreAuthorize("hasAuthority('platform:retail:store:people:list')")
    @ApiOperation(value = "分销员列表")
    @RequestMapping(value = "/people/list", method = RequestMethod.GET)
    public CommonResult<CommonPage<SpreadUserResponse>> getRetailStorePeoplePage(@ModelAttribute RetailStorePeopleSearchRequest request) {
        return CommonResult.success(CommonPage.restPage(retailStoreService.getRetailStorePeoplePage(request)));
    }

    @PreAuthorize("hasAuthority('platform:retail:store:sub:user:list')")
    @ApiOperation(value = "根据条件获取下级推广用户列表")
    @RequestMapping(value = "/sub/user/list", method = RequestMethod.GET)
    public CommonResult<CommonPage<User>> getRetailStoreSubUserList(@ModelAttribute @Validated RetailStoreSubUserSearchRequest request,
                                                                    @ModelAttribute PageParamRequest pageParamRequest) {
        return CommonResult.success(CommonPage.restPage(retailStoreService.getRetailStoreSubUserList(request, pageParamRequest)));
    }

    @PreAuthorize("hasAuthority('platform:retail:store:promotion:order:list')")
    @ApiOperation(value = "根据条件获取推广订单列表")
    @RequestMapping(value = "/promotion/order/list", method = RequestMethod.GET)
    public CommonResult<CommonPage<PromotionOrderResponse>> getPromotionOrderList(@ModelAttribute @Validated RetailStoreSubUserSearchRequest request,
                                                                                  @ModelAttribute PageParamRequest pageParamRequest) {
        return CommonResult.success(CommonPage.restPage(retailStoreService.getPromotionOrderList(request, pageParamRequest)));
    }
}
