package com.zbkj.admin.controller.platform;

import com.zbkj.common.model.user.UserTag;
import com.zbkj.common.page.CommonPage;
import com.zbkj.common.request.PageParamRequest;
import com.zbkj.common.request.UserTagRequest;
import com.zbkj.common.result.CommonResult;
import com.zbkj.service.service.UserTagService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;


/**
 * 平台端用户标签控制器
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
@RequestMapping("api/admin/platform/user/tag")
@Api(tags = "平台端用户标签控制器") //配合swagger使用
public class PlatformUserTagController {

    @Autowired
    private UserTagService userTagService;

    @PreAuthorize("hasAuthority('platform:user:tag:list')")
    @ApiOperation(value = "用户标签分页列表") //配合swagger使用
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public CommonResult<CommonPage<UserTag>> getList(@Validated PageParamRequest pageParamRequest) {
        CommonPage<UserTag> userTagCommonPage = CommonPage.restPage(userTagService.getList(pageParamRequest));
        return CommonResult.success(userTagCommonPage);
    }

    @PreAuthorize("hasAuthority('platform:user:tag:save')")
    @ApiOperation(value = "新增用户标签")
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    public CommonResult<String> save(@RequestBody @Validated UserTagRequest userTagRequest) {
        if (userTagService.create(userTagRequest)) {
            return CommonResult.success();
        }
        return CommonResult.failed();
    }

    @PreAuthorize("hasAuthority('platform:user:tag:delete')")
    @ApiOperation(value = "删除用户标签")
    @RequestMapping(value = "/delete/{id}", method = RequestMethod.POST)
    public CommonResult<String> delete(@PathVariable(value = "id") Integer id) {
        if (userTagService.delete(id)) {
            return CommonResult.success();
        }
        return CommonResult.failed();
    }

    @PreAuthorize("hasAuthority('platform:user:tag:update')")
    @ApiOperation(value = "修改用户标签")
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public CommonResult<String> update(@RequestBody @Validated UserTagRequest userTagRequest) {
        if (userTagService.updateTag(userTagRequest)) {
            return CommonResult.success();
        }
        return CommonResult.failed();
    }

    @PreAuthorize("hasAuthority('platform:user:tag:all:list')")
    @ApiOperation(value = "用户标签全部列表") //配合swagger使用
    @RequestMapping(value = "/all/list", method = RequestMethod.GET)
    public CommonResult<List<UserTag>> getAllList() {
        return CommonResult.success(userTagService.getAllList());
    }
}



