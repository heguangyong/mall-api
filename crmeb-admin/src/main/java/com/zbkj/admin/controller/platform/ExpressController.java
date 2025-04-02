package com.zbkj.admin.controller.platform;

import com.alibaba.fastjson.JSONObject;
import com.zbkj.common.model.express.Express;
import com.zbkj.common.page.CommonPage;
import com.zbkj.common.request.ExpressSearchRequest;
import com.zbkj.common.request.ExpressUpdateRequest;
import com.zbkj.common.request.ExpressUpdateShowRequest;
import com.zbkj.common.request.PageParamRequest;
import com.zbkj.common.result.CommonResult;
import com.zbkj.service.service.ExpressService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;


/**
 * 平台端物流公司控制器
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
@Slf4j
@RestController
@RequestMapping("api/admin/platform/express")
@Api(tags = "平台端物流公司控制器")
public class ExpressController {

    @Autowired
    private ExpressService expressService;

    @PreAuthorize("hasAuthority('platform:express:list')")
    @ApiOperation(value = "分页显示快递公司列表")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    @ApiImplicitParam(name="keywords", value="搜索关键字")
    public CommonResult<CommonPage<Express>> getList(@Validated ExpressSearchRequest request,
                                                     @ModelAttribute PageParamRequest pageParamRequest) {
        return CommonResult.success(CommonPage.restPage(expressService.getList(request, pageParamRequest)));
    }

    @PreAuthorize("hasAuthority('platform:express:update')")
    @ApiOperation(value = "编辑快递公司")
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public CommonResult<String> update(@RequestBody @Validated ExpressUpdateRequest expressRequest) {
        if (expressService.updateExpress(expressRequest)) {
            return CommonResult.success();
        }
        return CommonResult.failed();
    }

    @PreAuthorize("hasAuthority('platform:express:update:show')")
    @ApiOperation(value = "修改快递公司显示状态")
    @RequestMapping(value = "/update/show", method = RequestMethod.POST)
    public CommonResult<String> update(@RequestBody @Validated ExpressUpdateShowRequest expressRequest) {
        if (expressService.updateExpressShow(expressRequest)) {
            return CommonResult.success();
        }
        return CommonResult.failed();
    }

    @PreAuthorize("hasAuthority('platform:express:sync')")
    @ApiOperation(value = "同步物流公司")
    @RequestMapping(value = "/sync/express", method = RequestMethod.POST)
    public CommonResult<String> syncExpress() {
        if (expressService.syncExpress()) {
            CommonResult<String> success = CommonResult.success();
            success.setMessage("同步物流公司成功");
            return success;
        }
        return CommonResult.failed("同步物流公司失败");
    }


    @PreAuthorize("hasAuthority('platform:express:info')")
    @ApiOperation(value = "快递公司详情")
    @RequestMapping(value = "/info", method = RequestMethod.GET)
    @ApiImplicitParam(name="id", value="快递公司ID", required = true)
    public CommonResult<Express> info(@RequestParam(value = "id") Integer id) {
        return CommonResult.success(expressService.getInfo(id));
   }

}



