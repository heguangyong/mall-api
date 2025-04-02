package com.zbkj.admin.controller.platform;

import com.zbkj.common.annotation.LogControllerAnnotation;
import com.zbkj.common.enums.MethodType;
import com.zbkj.common.request.PaidMemberBenefitsStatementRequest;
import com.zbkj.common.request.PaidMemberCardSaveRequest;
import com.zbkj.common.request.PaidMemberCardSearchRequest;
import com.zbkj.common.response.PaidMemberCardResponse;
import com.zbkj.common.result.CommonResult;
import com.zbkj.common.vo.PaidMemberBenefitsVo;
import com.zbkj.common.vo.PaidMemberConfigVo;
import com.zbkj.service.service.PaidMemberCardService;
import com.zbkj.service.service.PaidMemberService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 付费会员控制器
 *
 * @author Han
 * @version 1.0.0
 * @Date 2024/5/10
 */
@Slf4j
@RestController
@RequestMapping("api/admin/platform/paid/member")
@Api(tags = "付费会员控制器")
public class PaidMemberController {

    @Autowired
    private PaidMemberService paidMemberService;
    @Autowired
    private PaidMemberCardService paidMemberCardService;

    @PreAuthorize("hasAuthority('platform:paid:member:base:config:get')")
    @ApiOperation(value = "付费会员基础配置信息获取")
    @RequestMapping(value = "/base/config/get", method = RequestMethod.GET)
    public CommonResult<PaidMemberConfigVo> getBaseConfig() {
        return CommonResult.success(paidMemberService.getBaseConfig());
    }

    @PreAuthorize("hasAuthority('platform:paid:member:base:config:edit')")
    @ApiOperation(value = "编辑付费会员基础配置")
    @RequestMapping(value = "/base/config/edit", method = RequestMethod.POST)
    public CommonResult<Object> editBaseConfig(@RequestBody @Validated PaidMemberConfigVo voRequest) {
        if (paidMemberService.editBaseConfig(voRequest)) {
            return CommonResult.success().setMessage("编辑成功");
        }
        return CommonResult.failed().setMessage("编辑失败");
    }

    @PreAuthorize("hasAuthority('platform:paid:member:benefits:list')")
    @ApiOperation(value = "获取付费会员会员权益")
    @RequestMapping(value = "/benefits/list", method = RequestMethod.GET)
    public CommonResult<List<PaidMemberBenefitsVo>> getBenefitsList() {
        return CommonResult.success(paidMemberService.getBenefitsList());
    }

    @PreAuthorize("hasAuthority('platform:paid:member:benefits:edit')")
    @ApiOperation(value = "编辑付费会员会员权益")
    @RequestMapping(value = "/benefits/edit", method = RequestMethod.POST)
    public CommonResult<Object> editBenefits(@RequestBody @Validated PaidMemberBenefitsVo voRequest) {
        if (paidMemberService.editBenefits(voRequest)) {
            return CommonResult.success().setMessage("编辑成功");
        }
        return CommonResult.failed().setMessage("编辑失败");
    }

    @PreAuthorize("hasAuthority('platform:paid:member:benefits:switch')")
    @ApiOperation(value = "付费会员会员权益开关")
    @RequestMapping(value = "/benefits/switch/{id}", method = RequestMethod.POST)
    public CommonResult<Object> editBenefitsSwitch(@PathVariable(name = "id") Integer id) {
        if (paidMemberService.editBenefitsSwitch(id)) {
            return CommonResult.success().setMessage("开关更新成功");
        }
        return CommonResult.failed().setMessage("开关更新失败");
    }

    @PreAuthorize("hasAuthority('platform:paid:member:benefits:statement:edit')")
    @ApiOperation(value = "编辑付费会员会员权益说明")
    @RequestMapping(value = "/benefits/statement/edit", method = RequestMethod.POST)
    public CommonResult<Object> editBenefitsStatement(@RequestBody @Validated PaidMemberBenefitsStatementRequest request) {
        if (paidMemberService.editBenefitsStatement(request)) {
            return CommonResult.success().setMessage("编辑成功");
        }
        return CommonResult.failed().setMessage("编辑失败");
    }

    @PreAuthorize("hasAuthority('platform:paid:member:card:list')")
    @ApiOperation(value="付费会员卡列表")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public CommonResult<List<PaidMemberCardResponse>> getCardList(@Validated PaidMemberCardSearchRequest request) {
        return CommonResult.success(paidMemberCardService.findPlatList(request));
    }

    @LogControllerAnnotation(intoDB = true, methodType = MethodType.ADD, description = "添加付费会员卡")
    @PreAuthorize("hasAuthority('platform:paid:member:card:add')")
    @ApiOperation(value="添加付费会员卡")
    @RequestMapping(value = "/card/add", method = RequestMethod.POST)
    public CommonResult<String> addCard(@RequestBody @Validated PaidMemberCardSaveRequest request) {
        if (paidMemberCardService.add(request)) {
            return CommonResult.success("添加成功");
        }
        return CommonResult.failed("添加失败");
    }

    @PreAuthorize("hasAuthority('platform:paid:member:card:edit')")
    @ApiOperation(value = "编辑付费会员卡")
    @RequestMapping(value = "/card/edit", method = RequestMethod.POST)
    public CommonResult<Object> editCard(@RequestBody @Validated PaidMemberCardSaveRequest request) {
        if (paidMemberCardService.edit(request)) {
            return CommonResult.success().setMessage("编辑成功");
        }
        return CommonResult.failed().setMessage("编辑失败");
    }

    @PreAuthorize("hasAuthority('platform:paid:member:card:switch')")
    @ApiOperation(value = "付费会员卡开关")
    @RequestMapping(value = "/card/switch/{id}", method = RequestMethod.POST)
    public CommonResult<Object> editCardSwitch(@PathVariable(name = "id") Integer id) {
        if (paidMemberCardService.editSwitch(id)) {
            return CommonResult.success().setMessage("开关更新成功");
        }
        return CommonResult.failed().setMessage("开关更新失败");
    }

    @PreAuthorize("hasAuthority('platform:paid:member:card:delete')")
    @ApiOperation(value = "删除付费会员卡")
    @RequestMapping(value = "/card/delete/{id}", method = RequestMethod.POST)
    public CommonResult<Object> deleteCard(@PathVariable(name = "id") Integer id) {
        if (paidMemberCardService.deleteCard(id)) {
            return CommonResult.success().setMessage("删除成功");
        }
        return CommonResult.failed().setMessage("删除失败");
    }
}
