package com.zbkj.admin.controller.platform;

import com.zbkj.common.model.community.CommunityTopic;
import com.zbkj.common.page.CommonPage;
import com.zbkj.common.request.CommunityCategorySaveRequest;
import com.zbkj.common.request.CommunityTopicSaveRequest;
import com.zbkj.common.request.CommunityTopicSearchRequest;
import com.zbkj.common.result.CommonResult;
import com.zbkj.service.service.CommunityTopicService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * @ClassName CommunityTopicController
 * @Description 社区话题控制器
 * @Author HZW
 * @Date 2023/3/7 11:32
 * @Version 1.0
 */
@Slf4j
@RestController
@RequestMapping("api/admin/platform/community/topic")
@Api(tags = "社区话题管理")
public class CommunityTopicController {

    @Autowired
    private CommunityTopicService topicService;

    @PreAuthorize("hasAuthority('platform:community:topic:page:list')")
    @ApiOperation(value = "社区话题分页列表")
    @RequestMapping(value = "/page/list", method = RequestMethod.GET)
    public CommonPage<CommunityTopic> findPageList(@Validated CommunityTopicSearchRequest request) {
        return CommonPage.restPage(topicService.findPageList(request));
    }

    @PreAuthorize("hasAuthority('platform:community:topic:add')")
    @ApiOperation(value = "添加社区话题")
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public CommonResult<Object> add(@RequestBody @Validated CommunityTopicSaveRequest request) {
        topicService.add(request);
        return CommonResult.success();
    }

    @PreAuthorize("hasAuthority('platform:community:topic:update')")
    @ApiOperation(value = "编辑社区话题")
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public CommonResult<Object> update(@RequestBody @Validated CommunityTopicSaveRequest request) {
        topicService.edit(request);
        return CommonResult.success();
    }

    @PreAuthorize("hasAuthority('platform:community:topic:delete')")
    @ApiOperation(value = "删除社区话题")
    @RequestMapping(value = "/delete/{id}", method = RequestMethod.POST)
    public CommonResult<Object> update(@PathVariable("id") Integer id) {
        topicService.deleteById(id);
        return CommonResult.success();
    }

    @PreAuthorize("hasAuthority('platform:community:topic:recommend:switch')")
    @ApiOperation(value = "社区话题开启/关闭推荐")
    @RequestMapping(value = "/recommend/{id}", method = RequestMethod.POST)
    public CommonResult<Object> recommendSwitch(@PathVariable("id") Integer id) {
        topicService.recommendSwitch(id);
        return CommonResult.success();
    }
}
