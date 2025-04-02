package com.zbkj.front.controller;

import com.zbkj.common.page.CommonPage;
import com.zbkj.common.request.*;
import com.zbkj.common.response.CommunityCommentReplyResponse;
import com.zbkj.common.response.CommunityNoteFrontDetailResponse;
import com.zbkj.common.response.CommunityNoteFrontFollowResponse;
import com.zbkj.common.response.CommunityNoteFrontPageResponse;
import com.zbkj.common.result.CommonResult;
import com.zbkj.front.service.CommunityFrontService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * @ClassName CommunityController
 * @Description 社区笔记控制器
 * @Author HZW
 * @Date 2023/3/13 9:19
 * @Version 1.0
 */
@Slf4j
@RestController
@RequestMapping("api/front/community/note")
@Api(tags = "社区笔记控制器")
public class CommunityNoteController {

    @Autowired
    private CommunityFrontService communityFrontService;

    @ApiOperation(value = "社区笔记发现列表")
    @RequestMapping(value = "/discover/list", method = RequestMethod.GET)
    public CommonPage<CommunityNoteFrontPageResponse> findDiscoverNoteList(@ModelAttribute @Validated CommunityNoteFrontDiscoverRequest request) {
        return CommonPage.restPage(communityFrontService.findDiscoverNoteList(request));
    }

    @ApiOperation(value = "社区笔记关注列表")
    @RequestMapping(value = "/follow/list", method = RequestMethod.GET)
    public CommonPage<CommunityNoteFrontFollowResponse> findFollowNoteList(@ModelAttribute @Validated PageParamRequest request) {
        return CommonPage.restPage(communityFrontService.findFollowNoteList(request));
    }

    @ApiOperation(value = "社区笔记作者列表")
    @RequestMapping(value = "/author/list/{authorId}", method = RequestMethod.GET)
    public CommonPage<CommunityNoteFrontPageResponse> findAuthorNoteList(@PathVariable("authorId") Integer authorId, PageParamRequest request) {
        return CommonPage.restPage(communityFrontService.findAuthorNoteList(authorId, request));
    }

    @ApiOperation(value = "社区用户笔记详情")
    @RequestMapping(value = "/user/detail/{noteId}", method = RequestMethod.GET)
    public CommunityNoteFrontDetailResponse getUserNoteDetail(@PathVariable("noteId") Integer noteId) {
        return communityFrontService.getUserNoteDetail(noteId);
    }

    @ApiOperation(value = "社区笔记评论列表")
    @RequestMapping(value = "/reply/list/{noteId}", method = RequestMethod.GET)
    public CommonPage<CommunityCommentReplyResponse> findNoteReplyPageList(@PathVariable("noteId") Integer noteId, PageParamRequest request) {
        return CommonPage.restPage(communityFrontService.findNoteReplyPageList(noteId, request));
    }

    @ApiOperation(value = "社区话题笔记列表")
    @RequestMapping(value = "/topic/list", method = RequestMethod.GET)
    public CommonPage<CommunityNoteFrontPageResponse> findTopicNoteList(@Validated CommunityNoteTopicSearchRequest request) {
        return CommonPage.restPage(communityFrontService.findTopicNoteList(request));
    }

    @ApiOperation(value = "社区笔记发现推荐列表")
    @RequestMapping(value = "/discover/list/recommend/{noteId}", method = RequestMethod.GET)
    public CommonPage<CommunityNoteFrontDetailResponse> findDiscoverNoteRecommendList(@PathVariable("noteId") Integer noteId, PageParamRequest request) {
        return CommonPage.restPage(communityFrontService.findDiscoverNoteRecommendList(noteId, request));
    }

    @ApiOperation(value = "创建社区笔记")
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public CommonResult<Object> createNote(@RequestBody @Validated CommunityNoteSaveRequest request) {
        communityFrontService.createNote(request);
        return CommonResult.success();
    }

    @ApiOperation(value = "编辑社区笔记")
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public CommonResult<Object> updateNote(@RequestBody @Validated CommunityNoteSaveRequest request) {
        communityFrontService.updateNote(request);
        return CommonResult.success();
    }

    @ApiOperation(value = "社区之我的笔记列表")
    @RequestMapping(value = "/my/list", method = RequestMethod.GET)
    public CommonPage<CommunityNoteFrontPageResponse> findMyNoteList(PageParamRequest request) {
        return CommonPage.restPage(communityFrontService.findMyNoteList(request));
    }

    @ApiOperation(value = "社区笔记评论开关")
    @RequestMapping(value = "/reply/switch/{noteId}", method = RequestMethod.POST)
    public CommonResult<Object> updateNoteReplySwitch(@PathVariable("noteId") Integer noteId) {
        communityFrontService.updateNoteReplySwitch(noteId);
        return CommonResult.success();
    }

    @ApiOperation(value = "社区笔记点赞/取消")
    @RequestMapping(value = "/like/{noteId}", method = RequestMethod.POST)
    public CommonResult<Object> likeNote(@PathVariable("noteId") Integer noteId) {
        communityFrontService.likeNote(noteId);
        return CommonResult.success();
    }

    @ApiOperation(value = "社区笔记评论点赞/取消")
    @RequestMapping(value = "/reply/like/{replyId}", method = RequestMethod.POST)
    public CommonResult<Object> likeReply(@PathVariable("replyId") Integer replyId) {
        communityFrontService.likeReply(replyId);
        return CommonResult.success();
    }

    @ApiOperation(value = "社区笔记添加评论")
    @RequestMapping(value = "/reply/add", method = RequestMethod.POST)
    public CommunityCommentReplyResponse replyAdd(@RequestBody @Validated CommunityReplyAddRequest request) {
        return communityFrontService.replyAdd(request);
    }

    @ApiOperation(value = "社区笔记删除")
    @RequestMapping(value = "/delete/{noteId}", method = RequestMethod.POST)
    public CommonResult<Object> deleteNote(@PathVariable("noteId") Integer noteId) {
        communityFrontService.deleteNote(noteId);
        return CommonResult.success();
    }

    @ApiOperation(value = "社区笔记评论删除")
    @RequestMapping(value = "/reply/delete/{replyId}", method = RequestMethod.POST)
    public CommonResult<Integer> deleteReply(@PathVariable("replyId") Integer replyId) {
        return CommonResult.success(communityFrontService.deleteReply(replyId));
    }

    @ApiOperation(value = "社区之我的点赞列表")
    @RequestMapping(value = "/my/like/list", method = RequestMethod.GET)
    public CommonPage<CommunityNoteFrontPageResponse> findMyLikeNoteList(PageParamRequest request) {
        return CommonPage.restPage(communityFrontService.findMyLikeNoteList(request));
    }

    @ApiOperation(value = "获取社区笔记评论平台开关设置")
    @RequestMapping(value = "/reply/platform/switch/config", method = RequestMethod.GET)
    public String getPlatformSwitchConfig() {
        return communityFrontService.getPlatformSwitchConfig();
    }
}
