package com.zbkj.admin.controller.merchant.groupbuy;

import com.zbkj.common.model.admin.SystemAdmin;
import com.zbkj.common.page.CommonPage;
import com.zbkj.common.request.GroupBuyRecordPageNumRequest;
import com.zbkj.common.request.PageParamRequest;
import com.zbkj.common.request.groupbuy.GroupBuyRecordSearchRequest;
import com.zbkj.common.response.groupbuy.GroupBuyActivityRecordAdminListResponse;
import com.zbkj.common.response.groupbuy.GroupBuyActivityRecordListHeaderCount;
import com.zbkj.common.response.groupbuy.GroupBuyRecordDetailResponse;
import com.zbkj.common.result.CommonResult;
import com.zbkj.common.utils.SecurityUtil;
import com.zbkj.service.service.groupbuy.GroupBuyRecordService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;


/**
 * 拼团活动记录表 前端控制器
 */
@Slf4j
@RestController
@RequestMapping("api/admin/merchant/groupbuy/record")
@Api(tags = "商户端 - 拼团活动记录表") //配合swagger使用
public class MerchantGroupBuyRecordController {

    @Autowired
    private GroupBuyRecordService groupBuyRecordService;

    /**
     * 分页显示拼团活动记录表
     *
     * @param request          搜索条件
     * @param pageParamRequest 分页参数
     * @author dazongzi
     * @since 2024-08-13
     */
    @ApiOperation(value = "分页列表") //配合swagger使用
    @PreAuthorize("hasAuthority('merchant:groupbuy:record:list')")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public CommonResult<CommonPage<GroupBuyActivityRecordAdminListResponse>> getList(@Validated GroupBuyRecordSearchRequest request, @Validated PageParamRequest pageParamRequest) {
        CommonPage<GroupBuyActivityRecordAdminListResponse> groupBuyRecordCommonPage = CommonPage.restPage(groupBuyRecordService.getList(request, pageParamRequest));
        return CommonResult.success(groupBuyRecordCommonPage);
    }


    @ApiOperation(value = "拼团记录头部 对应活动进程数量") //配合swagger使用
    @PreAuthorize("hasAuthority('merchant:groupbuy:record:list:count')")
    @RequestMapping(value = "/list/count", method = RequestMethod.GET)
    public CommonResult<GroupBuyActivityRecordListHeaderCount> getListStatusCount(@Validated GroupBuyRecordPageNumRequest request) {
        SystemAdmin systemAdmin = SecurityUtil.getLoginUserVo().getUser();
        return CommonResult.success(groupBuyRecordService.getListHeaderCount(request, systemAdmin));
    }

    /**
     * 查询拼团活动记录表信息
     *
     * @param id Integer
     * @author dazongzi
     * @since 2024-08-13
     */
    @ApiOperation(value = "拼团开团记录详情")
    @PreAuthorize("hasAuthority('merchant:groupbuy:record:info')")
    @RequestMapping(value = "/info/{id}", method = RequestMethod.GET)
    public CommonResult<GroupBuyRecordDetailResponse> info(@PathVariable(value = "id") Integer id) {
        return CommonResult.success(groupBuyRecordService.getByAdminDetail(id));
    }
}



