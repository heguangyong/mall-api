package com.zbkj.admin.controller.platform.groupbuy;

import com.zbkj.common.enums.GroupBuyGroupStatusEnum;
import com.zbkj.common.model.admin.SystemAdmin;
import com.zbkj.common.page.CommonPage;
import com.zbkj.common.request.PageParamRequest;
import com.zbkj.common.request.groupbuy.GroupBuyActivitySearchRequest;
import com.zbkj.common.request.groupbuy.PatGroupBuyActivitySearchRequest;
import com.zbkj.common.response.groupbuy.GroupBuyActivityListHeaderCount;
import com.zbkj.common.response.groupbuy.GroupBuyActivityResponse;
import com.zbkj.common.result.CommonResult;
import com.zbkj.common.utils.SecurityUtil;
import com.zbkj.service.service.groupbuy.GroupBuyActivityService;
import com.zbkj.service.service.groupbuy.GroupBuyRecordService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;


/**
 * 拼团活动表 前端控制器
 */
@Slf4j
@RestController
@RequestMapping("api/admin/platform/groupbuy/activity")
@Api(tags = "平台端 - 拼团活动表") //配合swagger使用

public class PlatGroupBuyActivityController {

    @Autowired
    private GroupBuyActivityService groupBuyActivityService;
    @Autowired
    private GroupBuyRecordService groupBuyRecordService;

    /**
     * 分页显示拼团活动表
     * @param request 搜索条件
     * @param pageParamRequest 分页参数
     * @author dazongzi
     * @since 2024-08-13
     */
    @ApiOperation(value = "拼团 分页列表") //配合swagger使用
    @PreAuthorize("hasAuthority('platform:groupbuy:activity:list')")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public CommonResult<CommonPage<GroupBuyActivityResponse>> getList(@Validated PatGroupBuyActivitySearchRequest request, @Validated PageParamRequest pageParamRequest) {
        CommonPage<GroupBuyActivityResponse> groupBuyActivityCommonPage = CommonPage.restPage(groupBuyActivityService.getList(request, pageParamRequest));
        return CommonResult.success(groupBuyActivityCommonPage);
    }

    @ApiOperation(value = "拼团头部 对应活动进程数量") //配合swagger使用
    @PreAuthorize("hasAuthority('platform:groupbuy:activity:list:count')")
    @RequestMapping(value = "/list/count", method = RequestMethod.GET)
    public CommonResult<List<GroupBuyActivityListHeaderCount>> getListStatusCount(GroupBuyActivitySearchRequest request) {
        SystemAdmin systemAdmin = SecurityUtil.getLoginUserVo().getUser();
        return CommonResult.success(groupBuyActivityService.getListHeaderCount(request, systemAdmin));
    }


    /**
     * 查询拼团活动表信息
     * @param id Integer
     * @author dazongzi
     * @since 2024-08-13
     */
    @ApiOperation(value = "详情")
    @PreAuthorize("hasAuthority('platform:groupbuy:activity:info')")
    @RequestMapping(value = "/info", method = RequestMethod.GET)
    public CommonResult<GroupBuyActivityResponse> info(@RequestParam(value = "id") Integer id) {
        GroupBuyActivityResponse groupBuyActivity = groupBuyActivityService.getGroupBuyActivity(id);
        return CommonResult.success(groupBuyActivity);
    }

    @ApiOperation(value = "活动审核 - 通过")
    @PreAuthorize("hasAuthority('platform:groupbuy:activity:review:pass')")
    @RequestMapping(value = "/review/pass", method = RequestMethod.GET)
    public CommonResult<String> reviewPass(@RequestParam(value = "id") Integer id) {
        if(groupBuyActivityService.groupBuyGroupStatusProgress(id, GroupBuyGroupStatusEnum.GROUP_BUY_ENUM_ACTIVITY_STATUS_PASS.getCode(), null)){
            return CommonResult.success();
        } else {
            return CommonResult.failed();
        }
    }

    @ApiOperation(value = "活动审核 - 拒绝")
    @PreAuthorize("hasAuthority('platform:groupbuy:activity:review:refuse')")
    @RequestMapping(value = "/review/refuse", method = RequestMethod.GET)
    public CommonResult<String> reviewRefuse(@RequestParam(value = "id") Integer id, @RequestParam(value = "reason") String reason) {
        if(groupBuyActivityService.groupBuyGroupStatusProgress(id, GroupBuyGroupStatusEnum.GROUP_BUY_ENUM_ACTIVITY_STATUS_REFUSE.getCode(), reason)){
            return CommonResult.success();
        } else {
            return CommonResult.failed();
        }
    }

    @ApiOperation(value = "强制关闭")
    @PreAuthorize("hasAuthority('platform:groupbuy:activity:review:close')")
    @RequestMapping(value = "/review/close", method = RequestMethod.GET)
    public CommonResult<String> reviewClose(@RequestParam(value = "id") Integer id,  @RequestParam(value = "reason", required = false) String reason) {
        if(groupBuyActivityService.groupBuyGroupStatusProgressClose(id, null)){
            return CommonResult.success();
        } else {
            return CommonResult.failed();
        }
    }

    @ApiOperation(value = "TEST TASK")
//    @PreAuthorize("hasAuthority('platform:groupbuy:activity:review:close')")
    @RequestMapping(value = "/task", method = RequestMethod.GET)
    public CommonResult<String> task() {
        groupBuyRecordService.rollBackIsBuyed();
        return CommonResult.success();

    }
}



