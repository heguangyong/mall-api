package com.zbkj.admin.controller.platform;

import com.zbkj.common.result.CommonResult;
import com.zbkj.common.vo.OnePassApplicationInfoVo;
import com.zbkj.service.service.OnePassService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * 一号通控制器
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
@RequestMapping("api/admin/platform/one/pass")
@Api(tags = "一号通控制器")
public class OnePassController {

    @Autowired
    private OnePassService onePassService;

    @PreAuthorize("hasAuthority('platform:one:pass:app:save')")
    @ApiOperation(value = "一号通应用信息保存")
    @RequestMapping(value = "/app/info/save", method = RequestMethod.POST)
    public CommonResult<Object> saveApplication(@Validated @RequestBody OnePassApplicationInfoVo request) {
        if (onePassService.saveApplicationInfo(request)) {
            return CommonResult.success().setMessage("保存成功");
        }
        return CommonResult.failed();
    }

    @PreAuthorize("hasAuthority('platform:one:pass:app:get')")
    @ApiOperation(value = "一号通应用信息获取")
    @RequestMapping(value = "/app/info/get", method = RequestMethod.GET)
    public CommonResult<OnePassApplicationInfoVo> getOnePassApplication() {
        return CommonResult.success(onePassService.getApplicationInfo());
    }

}
