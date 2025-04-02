package com.zbkj.front.controller;

import com.zbkj.common.model.community.CommunityCategory;
import com.zbkj.common.model.community.CommunityTopic;
import com.zbkj.common.page.CommonPage;
import com.zbkj.common.request.CommonSearchRequest;
import com.zbkj.common.request.PageParamRequest;
import com.zbkj.common.response.CommunityRecommendAuthorResponse;
import com.zbkj.common.response.CommunityTopicFrontCountResponse;
import com.zbkj.common.response.CommunityUserHomePageResponse;
import com.zbkj.common.result.CommonResult;
import com.zbkj.front.service.CommunityFrontService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @ClassName CommunityController
 * @Description 社区控制器
 * @Author HZW
 * @Date 2023/3/13 9:19
 * @Version 1.0
 */
@Slf4j
@RestController
@RequestMapping("api/front/community")
@Api(tags = "社区控制器")
public class CommunityController {

    @Autowired
    private CommunityFrontService communityFrontService;

    @ApiOperation(value = "社区分类列表")
    @RequestMapping(value = "/category/list", method = RequestMethod.GET)
    public List<CommunityCategory> getAllCategory() {
        return communityFrontService.getAllCategory();
    }

    @ApiOperation(value = "社区推荐作者列表")
    @RequestMapping(value = "/recommend/author/list", method = RequestMethod.GET)
    public CommonPage<CommunityRecommendAuthorResponse> recommendAuthorList(PageParamRequest request) {
        return CommonPage.restPage(communityFrontService.getRecommendAuthorList(request));
    }

    @ApiOperation(value = "社区用户主页")
    @RequestMapping(value = "/user/home/page/{id}", method = RequestMethod.GET)
    public CommunityUserHomePageResponse getUserHomePage(@PathVariable("id") Integer id) {
        return communityFrontService.getUserHomePage(id);
    }

    @ApiOperation(value = "社区话题统计数据")
    @RequestMapping(value = "/topic/count/{tid}", method = RequestMethod.GET)
    public CommunityTopicFrontCountResponse getTopicCount(@PathVariable("tid") Integer tid) {
        return communityFrontService.getTopicCount(tid);
    }

    @ApiOperation(value = "社区推荐话题列表")
    @RequestMapping(value = "/topic/recommend/list", method = RequestMethod.GET)
    public List<CommunityTopic> findRecommendTopicList() {
        return communityFrontService.findRecommendTopicList();
    }

    @ApiOperation(value = "社区话题搜索列表")
    @RequestMapping(value = "/topic/list", method = RequestMethod.GET)
    public CommonPage<CommunityTopic> findSearchTopicList(CommonSearchRequest request) {
        return CommonPage.restPage(communityFrontService.findSearchTopicList(request));
    }

    @ApiOperation(value = "社区关注/取关作者")
    @RequestMapping(value = "/concerned/author/{authorId}", method = RequestMethod.POST)
    public CommonResult<Object> concernedAuthor(@PathVariable("authorId") Integer authorId) {
        communityFrontService.concernedAuthor(authorId);
        return CommonResult.success();
    }
}
