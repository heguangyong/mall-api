package com.zbkj.admin.controller.merchant.groupbuy;

import com.zbkj.common.enums.GroupBuyGroupStatusEnum;
import com.zbkj.common.model.admin.SystemAdmin;
import com.zbkj.common.page.CommonPage;
import com.zbkj.common.request.PageParamRequest;
import com.zbkj.common.request.groupbuy.GroupBuyActivityRequest;
import com.zbkj.common.request.groupbuy.GroupBuyActivitySearchRequest;
import com.zbkj.common.request.groupbuy.GroupBuyActivityStatusOnOrOffRequest;
import com.zbkj.common.request.groupbuy.PatGroupBuyActivitySearchRequest;
import com.zbkj.common.response.groupbuy.GroupBuyActivityListHeaderCount;
import com.zbkj.common.response.groupbuy.GroupBuyActivityResponse;
import com.zbkj.common.result.CommonResult;
import com.zbkj.common.utils.SecurityUtil;
import com.zbkj.service.service.groupbuy.GroupBuyActivityService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
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
@RequestMapping("api/admin/merchant/groupbuy/activity")
@Api(tags = "商户端 - 拼团活动表") //配合swagger使用

public class MerchantGroupBuyActivityController {

    @Autowired
    private GroupBuyActivityService groupBuyActivityService;

    /**
     * 分页显示拼团活动表
     * @param request 搜索条件
     * @param pageParamRequest 分页参数
     * @author dazongzi
     * @since 2024-08-13
     */
    @ApiOperation(value = "分页列表") //配合swagger使用
    @PreAuthorize("hasAuthority('merchant:groupbuy:activity:list')")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public CommonResult<CommonPage<GroupBuyActivityResponse>> getList(@Validated GroupBuyActivitySearchRequest request, @Validated PageParamRequest pageParamRequest) {
        PatGroupBuyActivitySearchRequest requestPat = new PatGroupBuyActivitySearchRequest();
        BeanUtils.copyProperties(request, requestPat);
        CommonPage<GroupBuyActivityResponse> groupBuyActivityCommonPage = CommonPage.restPage(groupBuyActivityService.getList(requestPat, pageParamRequest));
        return CommonResult.success(groupBuyActivityCommonPage);
    }

    @ApiOperation(value = "拼团头部 对应活动进程数量") //配合swagger使用
    @PreAuthorize("hasAuthority('merchant:groupbuy:activity:list:count')")
    @RequestMapping(value = "/list/count", method = RequestMethod.GET)
    public CommonResult<List<GroupBuyActivityListHeaderCount>> getListStatusCount(GroupBuyActivitySearchRequest request) {
        SystemAdmin systemAdmin = SecurityUtil.getLoginUserVo().getUser();
        return CommonResult.success(groupBuyActivityService.getListHeaderCount(request, systemAdmin));
    }

    /**
     * 新增拼团活动表
     * @param groupBuyActivityRequest 新增参数
     * @author dazongzi
     * @since 2024-08-13
     */
    @ApiOperation(value = "新增")
    @PreAuthorize("hasAuthority('merchant:groupbuy:activity:add')")
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    public CommonResult<String> save(@RequestBody @Validated GroupBuyActivityRequest groupBuyActivityRequest) {
        if(groupBuyActivityService.addGroupBuyActivity(groupBuyActivityRequest)) {
            return CommonResult.success();
        } else {
            return CommonResult.failed();
        }
    }

    /**
     * 删除拼团活动表
     * @param id Integer
     * @author dazongzi
     * @since 2024-08-13
     */
    @ApiOperation(value = "删除")
    @PreAuthorize("hasAuthority('merchant:groupbuy:activity:delete')")
    @RequestMapping(value = "/delete", method = RequestMethod.GET)
    public CommonResult<String> delete(@RequestParam(value = "id") Integer id) {
        if(groupBuyActivityService.deleteGroupBuyActivity(id)) {
            return CommonResult.success();
        } else {
            return CommonResult.failed();
        }
    }

    /**
     * 修改拼团活动表
     * @param groupBuyActivityRequest 修改参数
     * @author dazongzi
     * @since 2024-08-13
     */
    @ApiOperation(value = "修改")
    @PreAuthorize("hasAuthority('merchant:groupbuy:activity:update')")
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public CommonResult<String> update(@RequestBody @Validated GroupBuyActivityRequest groupBuyActivityRequest) {
        if(groupBuyActivityService.updateGroupBuyActivity(groupBuyActivityRequest)) {
            return CommonResult.success();
        } else {
            return CommonResult.failed();
        }
    }

    /**
     * 查询拼团活动表信息
     * @param id Integer
     * @author dazongzi
     * @since 2024-08-13
     */
    @ApiOperation(value = "详情")
    @PreAuthorize("hasAuthority('merchant:groupbuy:activity:info')")
    @RequestMapping(value = "/info", method = RequestMethod.GET)
    public CommonResult<GroupBuyActivityResponse> info(@RequestParam(value = "id") Integer id) {
        GroupBuyActivityResponse groupBuyActivity = groupBuyActivityService.getGroupBuyActivity(id);
        return CommonResult.success(groupBuyActivity);
    }


    @ApiOperation(value = "活动状态 修改")
    @PreAuthorize("hasAuthority('merchant:groupbuy:activity:change:status')")
    @RequestMapping(value = "/status", method = RequestMethod.POST)
    public CommonResult<String> status(@RequestBody @Validated GroupBuyActivityStatusOnOrOffRequest request) {
        if(groupBuyActivityService.groupBuyActivityStatusOnOrOff(request.getId(), request.getStatus())){
            return CommonResult.success();
        } else {
            return CommonResult.failed();
        }
    }

    @ApiOperation(value = "撤回待审核")
    @PreAuthorize("hasAuthority('merchant:groupbuy:activity:rollback')")
    @RequestMapping(value = "/activity/rollback", method = RequestMethod.GET)
    public CommonResult<String> activityStatusRollback(@RequestParam(value = "id") Integer id) {
        if(groupBuyActivityService.groupBuyGroupStatusProgress(id, GroupBuyGroupStatusEnum.GROUP_BUY_ENUM_ACTIVITY_STATUS_CANCEL.getCode(), null)){
            return CommonResult.success();
        } else {
            return CommonResult.failed();
        }
    }
}



