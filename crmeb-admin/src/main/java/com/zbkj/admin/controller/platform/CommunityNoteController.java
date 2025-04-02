package com.zbkj.admin.controller.platform;

import com.zbkj.common.page.CommonPage;
import com.zbkj.common.request.*;
import com.zbkj.common.response.CommunityNoteDetailResponse;
import com.zbkj.common.response.CommunityNotePageDateResponse;
import com.zbkj.common.result.CommonResult;
import com.zbkj.service.service.CommunityNotesService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * @ClassName CommunityNoteController
 * @Description 社区笔记控制器
 * @Author HZW
 * @Date 2023/3/7 11:32
 * @Version 1.0
 */
@Slf4j
@RestController
@RequestMapping("api/admin/platform/community/note")
@Api(tags = "社区笔记管理")
public class CommunityNoteController {

    @Autowired
    private CommunityNotesService notesService;

    @PreAuthorize("hasAuthority('platform:community:note:page:list')")
    @ApiOperation(value = "社区笔记分页列表")
    @RequestMapping(value = "/page/list", method = RequestMethod.GET)
    public CommonPage<CommunityNotePageDateResponse> findPageList(@Validated CommunityNoteSearchRequest request) {
        return CommonPage.restPage(notesService.findPageList(request));
    }

    @PreAuthorize("hasAuthority('platform:community:note:detail')")
    @ApiOperation(value = "社区笔记详情")
    @RequestMapping(value = "/detail/{id}", method = RequestMethod.GET)
    public CommunityNoteDetailResponse detail(@PathVariable("id") Integer id) {
        return notesService.detail(id);
    }

    @PreAuthorize("hasAuthority('platform:community:note:audit')")
    @ApiOperation(value = "社区笔记审核")
    @RequestMapping(value = "/audit", method = RequestMethod.POST)
    public CommonResult<Object> audit(@RequestBody @Validated CommonAuditRequest request) {
        notesService.audit(request);
        return CommonResult.success();
    }

    @PreAuthorize("hasAuthority('platform:community:note:forced:down')")
    @ApiOperation(value = "社区笔记强制下架")
    @RequestMapping(value = "/forced/down/{id}", method = RequestMethod.POST)
    public CommonResult<Object> forcedDown(@RequestBody @Validated CommonForcedDownRequest request) {
        notesService.forcedDown(request);
        return CommonResult.success();
    }

    @PreAuthorize("hasAuthority('platform:community:note:delete')")
    @ApiOperation(value = "社区笔记删除")
    @RequestMapping(value = "/delete/{id}", method = RequestMethod.POST)
    public CommonResult<Object> delete(@PathVariable("id") Integer id) {
        notesService.delete(id);
        return CommonResult.success();
    }

    @PreAuthorize("hasAuthority('platform:community:note:category:batch:update')")
    @ApiOperation(value = "社区笔记分类批量修改")
    @RequestMapping(value = "/category/batch/update", method = RequestMethod.POST)
    public CommonResult<Object> categoryBatchUpdate(@RequestBody @Validated CommunityNoteCategoryBatchUpdateRequest request) {
        notesService.categoryBatchUpdate(request);
        return CommonResult.success();
    }

    @PreAuthorize("hasAuthority('platform:community:note:star:update')")
    @ApiOperation(value = "社区笔记推荐星级编辑")
    @RequestMapping(value = "/star/update", method = RequestMethod.POST)
    public CommonResult<Object> updateStar(@RequestBody @Validated CommonStarUpdateRequest request) {
        notesService.updateStar(request);
        return CommonResult.success();
    }

    @PreAuthorize("hasAuthority('platform:community:note:repley:force:off:switch')")
    @ApiOperation(value = "社区笔记评论强制关闭开关")
    @RequestMapping(value = "/reply/force/off/{id}", method = RequestMethod.POST)
    public CommonResult<Object> replyForceOffSwitch(@PathVariable("id") Integer id) {
        notesService.replyForceOffSwitch(id);
        return CommonResult.success();
    }
}
