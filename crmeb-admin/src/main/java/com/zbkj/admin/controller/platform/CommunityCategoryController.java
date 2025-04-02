package com.zbkj.admin.controller.platform;

import com.zbkj.common.model.community.CommunityCategory;
import com.zbkj.common.page.CommonPage;
import com.zbkj.common.request.CommunityCategorySaveRequest;
import com.zbkj.common.request.CommunityCategorySearchRequest;
import com.zbkj.common.result.CommonResult;
import com.zbkj.service.service.CommunityCategoryService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * @ClassName CommunityCategoryController
 * @Description 社区分类控制器
 * @Author HZW
 * @Date 2023/3/7 11:32
 * @Version 1.0
 */
@Slf4j
@RestController
@RequestMapping("api/admin/platform/community/category")
@Api(tags = "社区分类管理")
public class CommunityCategoryController {

    @Autowired
    private CommunityCategoryService communityCategoryService;

    @PreAuthorize("hasAuthority('platform:community:category:page:list')")
    @ApiOperation(value = "社区分类分页列表")
    @RequestMapping(value = "/page/list", method = RequestMethod.GET)
    public CommonPage<CommunityCategory> findPageList(@Validated CommunityCategorySearchRequest request) {
        return CommonPage.restPage(communityCategoryService.findPageList(request));
    }

    @PreAuthorize("hasAuthority('platform:community:category:add')")
    @ApiOperation(value = "添加社区分类")
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public CommonResult<Object> add(@RequestBody @Validated CommunityCategorySaveRequest request) {
        communityCategoryService.add(request);
        return CommonResult.success();
    }

    @PreAuthorize("hasAuthority('platform:community:category:update')")
    @ApiOperation(value = "编辑社区分类")
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public CommonResult<Object> update(@RequestBody @Validated CommunityCategorySaveRequest request) {
        communityCategoryService.edit(request);
        return CommonResult.success();
    }

    @PreAuthorize("hasAuthority('platform:community:category:delete')")
    @ApiOperation(value = "删除社区分类")
    @RequestMapping(value = "/delete/{id}", method = RequestMethod.POST)
    public CommonResult<Object> update(@PathVariable("id") Integer id) {
        communityCategoryService.deleteById(id);
        return CommonResult.success();
    }

    @PreAuthorize("hasAuthority('platform:community:category:show:switch')")
    @ApiOperation(value = "社区分类显示开关")
    @RequestMapping(value = "/show/{id}", method = RequestMethod.POST)
    public CommonResult<Object> showSwitch(@PathVariable("id") Integer id) {
        communityCategoryService.showSwitch(id);
        return CommonResult.success();
    }
}
