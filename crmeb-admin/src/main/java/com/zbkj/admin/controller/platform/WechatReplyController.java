package com.zbkj.admin.controller.platform;

import com.zbkj.common.model.wechat.WechatReply;
import com.zbkj.common.page.CommonPage;
import com.zbkj.common.request.PageParamRequest;
import com.zbkj.common.request.WechatReplyRequest;
import com.zbkj.common.request.WechatReplySearchRequest;
import com.zbkj.common.result.CommonResult;
import com.zbkj.service.service.WechatReplyService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


/**
 * 微信关键字回复表 前端控制器
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
@RequestMapping("api/admin/platform/wechat/public/keywords/reply")
@Api(tags = "微信公众号关键字回复管理")
public class WechatReplyController {

    @Autowired
    private WechatReplyService wechatReplyService;

    @PreAuthorize("hasAuthority('platform:wechat:public:keywords:reply:list')")
    @ApiOperation(value = "微信关键字回复分页列表")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public CommonResult<CommonPage<WechatReply>> getList(@Validated WechatReplySearchRequest request,
                                                         @Validated PageParamRequest pageParamRequest) {
        return CommonResult.success(CommonPage.restPage(wechatReplyService.getList(request, pageParamRequest)));
    }

    @PreAuthorize("hasAuthority('platform:wechat:public:keywords:reply:save')")
    @ApiOperation(value = "新增微信关键字回复")
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    public CommonResult<String> save(@RequestBody @Validated WechatReplyRequest wechatReplyRequest) {
        if (wechatReplyService.create(wechatReplyRequest)) {
            return CommonResult.success();
        }
        return CommonResult.failed();
    }

    @PreAuthorize("hasAuthority('platform:wechat:public:keywords:reply:delete')")
    @ApiOperation(value = "删除微信关键字回复")
    @RequestMapping(value = "/delete/{id}", method = RequestMethod.GET)
    public CommonResult<String> delete(@PathVariable(value = "id") Integer id) {
        if (wechatReplyService.removeById(id)) {
            return CommonResult.success();
        }
        return CommonResult.failed();
    }

    @PreAuthorize("hasAuthority('platform:wechat:public:keywords:reply:update')")
    @ApiOperation(value = "修改微信关键字回复")
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public CommonResult<String> update(@RequestBody @Validated WechatReplyRequest wechatReplyRequest) {
        if (wechatReplyService.updateReply(wechatReplyRequest)) {
            return CommonResult.success();
        }
        return CommonResult.failed();
    }

    @PreAuthorize("hasAuthority('platform:wechat:public:keywords:reply:status')")
    @ApiOperation(value = "修改微信关键字回复状态")
    @RequestMapping(value = "/status/{id}", method = RequestMethod.POST)
    public CommonResult<String> update(@PathVariable(value = "id") Integer id) {
        if (wechatReplyService.updateStatus(id)) {
            return CommonResult.success();
        }
        return CommonResult.failed();
    }

    @PreAuthorize("hasAuthority('platform:wechat:public:keywords:reply:info')")
    @ApiOperation(value = "微信关键字回复详情")
    @RequestMapping(value = "/info/{id}", method = RequestMethod.GET)
    public CommonResult<WechatReply> info(@PathVariable(value = "id") Integer id) {
        return CommonResult.success(wechatReplyService.getInfo(id));
    }

    @PreAuthorize("hasAuthority('platform:wechat:public:keywords:reply:info:keywords')")
    @ApiOperation(value = "根据关键字查询微信关键字回复")
    @RequestMapping(value = "/info/keywords", method = RequestMethod.GET)
    public CommonResult<WechatReply> keywordsInfo(@RequestParam(value = "keywords") String keywords) {
        return CommonResult.success(wechatReplyService.keywordsInfo(keywords));
    }
}



