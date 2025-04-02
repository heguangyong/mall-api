package com.zbkj.admin.controller.merchant;

import com.zbkj.admin.service.PcShoppingService;
import com.zbkj.common.model.system.SystemConfig;
import com.zbkj.common.result.CommonResult;
import com.zbkj.common.vo.MerchantPcShoppingConfigVo;
import com.zbkj.service.service.SystemConfigService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;


/**
 * 配置表 前端控制器
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
@RequestMapping("api/admin/merchant/config")
@Api(tags = "商户端设置")
public class MerchantConfigController {

    @Autowired
    private SystemConfigService systemConfigService;
    @Autowired
    private PcShoppingService pcShoppingService;

//    @PreAuthorize("hasAuthority('merchant:config:getuniq')")
//    @ApiOperation(value = "表单配置根据key获取")
//    @RequestMapping(value = "/getuniq", method = RequestMethod.GET)
//    public CommonResult<Object> justGetUniq(@RequestParam String key) {
//        return CommonResult.success(systemConfigService.getValueByKey(key));
//    }
//
//    @PreAuthorize("hasAuthority('merchant:config:get')")
//    @ApiOperation(value = "根据key获取配置")
//    @RequestMapping(value = "/get", method = RequestMethod.GET)
//    public CommonResult<List<SystemConfig>> getByKey(@RequestParam String key) {
//        return CommonResult.success(systemConfigService.getListByKey(key));
//    }

    @PreAuthorize("hasAuthority('merchant:config:pc:shopping:get')")
    @ApiOperation(value = "获取商户PC商城设置")
    @RequestMapping(value = "/get/pc/shopping/config", method = RequestMethod.GET)
    public CommonResult<MerchantPcShoppingConfigVo> getPcShoppingConfig() {
        return CommonResult.success(pcShoppingService.getMerchantPcShoppingConfig());
    }


    @PreAuthorize("hasAuthority('merchant:config:pc:shopping:save')")
    @ApiOperation(value = "编辑商户PC商城设置")
    @RequestMapping(value = "/save/pc/shopping/config", method = RequestMethod.POST)
    public CommonResult<Object> updatePcShoppingConfig(@RequestBody @Validated MerchantPcShoppingConfigVo voRequest) {
        if (pcShoppingService.updateMerchantPcShoppingConfig(voRequest)) {
            return CommonResult.success().setMessage("编辑成功");
        }
        return CommonResult.failed().setMessage("编辑失败");
    }
}



