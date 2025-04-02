package com.zbkj.front.controller;

import com.zbkj.common.request.IosBindingPhoneRequest;
import com.zbkj.common.result.CommonResult;
import com.zbkj.front.service.IosService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * IOS控制器
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
@RestController("IosController")
@RequestMapping("api/front/ios")
@Api(tags = "IOS控制器")
public class IosController {

    @Autowired
    private IosService iosService;

    @ApiOperation(value = "IOS绑定手机号（登录后绑定）")
    @RequestMapping(value = "/binding/phone", method = RequestMethod.POST)
    public CommonResult<Object> bindingPhone(@RequestBody @Validated IosBindingPhoneRequest request) {
        if (iosService.bindingPhone(request)) {
            return CommonResult.success("绑定成功");
        }
        return CommonResult.failed("绑定失败");
    }
}
