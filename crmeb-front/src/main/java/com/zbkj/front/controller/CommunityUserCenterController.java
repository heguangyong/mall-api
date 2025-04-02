package com.zbkj.front.controller;

import com.zbkj.common.page.CommonPage;
import com.zbkj.common.request.EditMySignatureRequest;
import com.zbkj.common.request.PageParamRequest;
import com.zbkj.common.response.CommunityUserHomePageResponse;
import com.zbkj.common.response.CommunityUserResponse;
import com.zbkj.front.service.CommunityFrontService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName CommunityController
 * @Description 社区用户中心控制器
 * @Author HZW
 * @Date 2023/3/13 9:19
 * @Version 1.0
 */
@Slf4j
@RestController
@RequestMapping("api/front/community/user")
@Api(tags = "社区用户中心控制器")
public class CommunityUserCenterController {

    @Autowired
    private CommunityFrontService communityFrontService;

    @ApiOperation(value = "我的主页")
    @RequestMapping(value = "/my/home/page", method = RequestMethod.GET)
    public CommunityUserHomePageResponse getMyHomePage() {
        return communityFrontService.getMyHomePage();
    }

    @ApiOperation(value = "我的关注列表")
    @RequestMapping(value = "/my/concerned/list", method = RequestMethod.GET)
    public CommonPage<CommunityUserResponse> findMyConcernedList(PageParamRequest request) {
        return CommonPage.restPage(communityFrontService.findMyConcernedList(request));
    }

    @ApiOperation(value = "我的粉丝列表")
    @RequestMapping(value = "/my/fans/list", method = RequestMethod.GET)
    public CommonPage<CommunityUserResponse> findMyFansList(PageParamRequest request) {
        return CommonPage.restPage(communityFrontService.findMyFansList(request));
    }

    @ApiOperation(value = "编辑个性签名")
    @RequestMapping(value = "/edit/signature", method = RequestMethod.POST)
    public String editMySignature(@RequestBody @Validated EditMySignatureRequest request) {
        communityFrontService.editMySignature(request);
        return "编辑个性签名成功";
    }
}
