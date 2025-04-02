package com.zbkj.admin.controller.platform;

import com.zbkj.common.model.wechat.WechatUrlLinkRecord;
import com.zbkj.common.page.CommonPage;
import com.zbkj.common.request.CommonSearchRequest;
import com.zbkj.common.request.WechatGenerateUrlLinkRequest;
import com.zbkj.common.result.CommonResult;
import com.zbkj.service.service.WechatUrlLinkRecordService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 微信url链接 前端控制器
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
@RequestMapping("api/admin/platform/wechat/url/link")
@Api(tags = "微信url链接控制器")
public class WechatUrlLinkController {

    @Autowired
    private WechatUrlLinkRecordService wechatUrlLinkRecordService;

    @PreAuthorize("hasAuthority('platform:wechat:mini:generate:url:link')")
    @ApiOperation(value = "生成微信小程序加密URLLink")
    @RequestMapping(value = "/mini/generate", method = RequestMethod.POST)
    public CommonResult<String> miniGenerateUrlLink(@RequestBody @Validated WechatGenerateUrlLinkRequest request) {
        return CommonResult.success(wechatUrlLinkRecordService.miniGenerateUrlLink(request));
    }

    @PreAuthorize("hasAuthority('platform:wechat:mini:url:link:page:list')")
    @ApiOperation(value = "微信小程序url链接分页列表")
    @RequestMapping(value = "/mini/list", method = RequestMethod.GET)
    public CommonResult<CommonPage<WechatUrlLinkRecord>> findRecordPageList(@ModelAttribute @Validated CommonSearchRequest request) {
        return CommonResult.success(CommonPage.restPage(wechatUrlLinkRecordService.findMiniRecordPageList(request)));
    }

    @PreAuthorize("hasAuthority('platform:wechat:mini:delete:url:link')")
    @ApiOperation(value = "删除微信小程序加密URLLink")
    @RequestMapping(value = "/mini/delete/{id}", method = RequestMethod.POST)
    public CommonResult<Object> miniDeleteUrlLink(@PathVariable(name = "id") Integer id) {
        if (wechatUrlLinkRecordService.deleteMiniUrlLinkRecord(id)) {
            return CommonResult.success().setMessage("删除成功");
        }
        return CommonResult.failed().setMessage("删除失败");
    }
}



