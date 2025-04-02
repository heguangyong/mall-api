package com.zbkj.admin.controller.platform;

import com.zbkj.common.page.CommonPage;
import com.zbkj.common.request.CommonAuditRequest;
import com.zbkj.common.request.CommunityReplySearchRequest;
import com.zbkj.common.request.PageParamRequest;
import com.zbkj.common.response.CommunityCommentReplyResponse;
import com.zbkj.common.response.CommunityReplyPageDateResponse;
import com.zbkj.common.result.CommonResult;
import com.zbkj.service.service.CommunityReplyService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * @ClassName CommunityReplyController
 * @Description 社区评论控制器
 * @Author HZW
 * @Date 2023/3/7 11:32
 * @Version 1.0
 */
@Slf4j
@RestController
@RequestMapping("api/admin/platform/community/reply")
@Api(tags = "社区评论管理")
public class CommunityReplyController {

    @Autowired
    private CommunityReplyService replyService;

    @PreAuthorize("hasAuthority('platform:community:reply:page:list')")
    @ApiOperation(value = "社区评论分页列表")
    @RequestMapping(value = "/page/list", method = RequestMethod.GET)
    public CommonPage<CommunityReplyPageDateResponse> findPageList(@Validated CommunityReplySearchRequest request) {
        return CommonPage.restPage(replyService.findPageList(request));
    }

    @PreAuthorize("hasAuthority('platform:community:reply:audit')")
    @ApiOperation(value = "社区评论审核")
    @RequestMapping(value = "/audit", method = RequestMethod.POST)
    public CommonResult<Object> audit(@RequestBody @Validated CommonAuditRequest request) {
        replyService.audit(request);
        return CommonResult.success();
    }

    @PreAuthorize("hasAuthority('platform:community:reply:delete')")
    @ApiOperation(value = "社区评论删除")
    @RequestMapping(value = "/delete/{id}", method = RequestMethod.POST)
    public CommonResult<Object> delete(@PathVariable("id") Integer id) {
        replyService.delete(id);
        return CommonResult.success();
    }

    @PreAuthorize("hasAuthority('platform:community:reply:note:page:list')")
    @ApiOperation(value = "社区评论-文章分页列表")
    @RequestMapping(value = "/note/page/list/{nid}", method = RequestMethod.GET)
    public CommonPage<CommunityCommentReplyResponse> findNotePageList(@PathVariable("nid") Integer nid, @Validated PageParamRequest request) {
        return CommonPage.restPage(replyService.findNotePageList(nid, request));
    }
}
