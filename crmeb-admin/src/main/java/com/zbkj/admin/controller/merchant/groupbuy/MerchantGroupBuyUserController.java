package com.zbkj.admin.controller.merchant.groupbuy;

import com.zbkj.common.model.groupbuy.GroupBuyUser;
import com.zbkj.common.page.CommonPage;
import com.zbkj.common.request.PageParamRequest;
import com.zbkj.common.request.groupbuy.GroupBuyUserSearchRequest;
import com.zbkj.common.result.CommonResult;
import com.zbkj.service.service.groupbuy.GroupBuyUserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


/**
 * 拼团参与成员表 前端控制器
 */
@Slf4j
@RestController
@RequestMapping("api/admin/merchant/groupbuy/user")
@Api(tags = "商户端 - 拼团参与成员表") //配合swagger使用
public class MerchantGroupBuyUserController {

    @Autowired
    private GroupBuyUserService groupBuyUserService;

    /**
     * 分页显示拼团参与成员表
     * @param request 搜索条件
     * @param pageParamRequest 分页参数
     * @author dazongzi
     * @since 2024-08-13
     */
    @ApiOperation(value = "分页列表") //配合swagger使用
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public CommonResult<CommonPage<GroupBuyUser>>  getList(@Validated GroupBuyUserSearchRequest request, @Validated PageParamRequest pageParamRequest) {
        CommonPage<GroupBuyUser> groupBuyUserCommonPage = CommonPage.restPage(groupBuyUserService.getList(request, pageParamRequest));
        return CommonResult.success(groupBuyUserCommonPage);
    }


    /**
     * 查询拼团参与成员表信息
     * @param id Integer
     * @author dazongzi
     * @since 2024-08-13
     */
    @ApiOperation(value = "详情")
    @RequestMapping(value = "/info", method = RequestMethod.GET)
    public CommonResult<GroupBuyUser> info(@RequestParam(value = "id") Integer id) {
        GroupBuyUser groupBuyUser = groupBuyUserService.getById(id);
        return CommonResult.success(groupBuyUser);
    }
}



