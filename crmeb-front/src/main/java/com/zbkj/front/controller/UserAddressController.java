package com.zbkj.front.controller;

import com.zbkj.common.model.user.UserAddress;
import com.zbkj.common.request.UserAddressRequest;
import com.zbkj.common.request.WechatAddressImportRequest;
import com.zbkj.common.result.CommonResult;
import com.zbkj.service.service.UserAddressService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;


/**
 * 用户地址 前端控制器
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
@RequestMapping("api/front/address")
@Api(tags = "用户 -- 地址")
public class UserAddressController {

    @Autowired
    private UserAddressService userAddressService;

    @ApiOperation(value = "用户地址分页列表")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public CommonResult<List<UserAddress>> getList() {
        return CommonResult.success(userAddressService.getAllList());
    }

    @ApiOperation(value = "用户地址添加")
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public CommonResult<Integer> add(@RequestBody @Validated UserAddressRequest request) {
        return CommonResult.success(userAddressService.create(request));
    }

    @ApiOperation(value = "用户地址编辑")
    @RequestMapping(value = "/edit", method = RequestMethod.POST)
    public CommonResult<UserAddress> edit(@RequestBody @Validated UserAddressRequest request) {
        if (userAddressService.edit(request)) {
            return CommonResult.success();
        }
        return CommonResult.failed();
    }

    @ApiOperation(value = "用户地址删除")
    @RequestMapping(value = "/delete/{id}", method = RequestMethod.POST)
    public CommonResult<String> delete(@PathVariable Integer id) {
        if (userAddressService.delete(id)) {
            return CommonResult.success();
        }
        return CommonResult.failed();
    }

    @ApiOperation(value = "地址详情")
    @RequestMapping(value = "/detail/{id}", method = RequestMethod.GET)
    public CommonResult<UserAddress> info(@PathVariable(value = "id") Integer id) {
        return CommonResult.success(userAddressService.getDetail(id));
    }

    @ApiOperation(value = "获取默认地址")
    @RequestMapping(value = "/get/default", method = RequestMethod.GET)
    public CommonResult<UserAddress> getDefault() {
        return CommonResult.success(userAddressService.getDefault());
    }

    @ApiOperation(value = "设置默认地址")
    @RequestMapping(value = "/set/default/{id}", method = RequestMethod.POST)
    public CommonResult<Object> setDefault(@PathVariable(value = "id") Integer id) {
        if (userAddressService.setDefault(id)) {
            return CommonResult.success();
        }
        return CommonResult.failed();
    }

    @ApiOperation(value = "获取微信地址信息")
    @RequestMapping(value = "/wechat/info", method = RequestMethod.POST)
    public CommonResult<UserAddress> wechatInfo(@RequestBody @Validated WechatAddressImportRequest request) {
        return CommonResult.success(userAddressService.getWechatInfo(request));
    }
}



