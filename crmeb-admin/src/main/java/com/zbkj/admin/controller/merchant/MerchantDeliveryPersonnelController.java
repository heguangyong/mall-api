package com.zbkj.admin.controller.merchant;

import com.zbkj.common.page.CommonPage;
import com.zbkj.common.request.MerchantDeliveryPersonnelSaveRequest;
import com.zbkj.common.request.MerchantDeliveryPersonnelSearchRequest;
import com.zbkj.common.response.MerchantDeliveryPersonnelPageResponse;
import com.zbkj.common.result.CommonResult;
import com.zbkj.service.service.MerchantDeliveryPersonnelService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


/**
 * 商户配送人员表 前端控制器
 */
@Slf4j
@RestController
@RequestMapping("api/admin/merchant/delivery/personnel")
@Api(tags = "商户配送人员控制器") //配合swagger使用
public class MerchantDeliveryPersonnelController {

    @Autowired
    private MerchantDeliveryPersonnelService merchantDeliveryPersonnelService;

    @PreAuthorize("hasAuthority('merchant:delivery:personnel:page')")
    @ApiOperation(value = "商户配送人员分页列表") //配合swagger使用
    @RequestMapping(value = "/page", method = RequestMethod.GET)
    public CommonResult<CommonPage<MerchantDeliveryPersonnelPageResponse>> findPage(@Validated MerchantDeliveryPersonnelSearchRequest request) {
        return CommonResult.success(CommonPage.restPage(merchantDeliveryPersonnelService.findPage(request)));
    }

    @PreAuthorize("hasAuthority('merchant:delivery:personnel:save')")
    @ApiOperation(value = "新增商户配送人员")
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    public CommonResult<String> save(@RequestBody @Validated MerchantDeliveryPersonnelSaveRequest request) {
        if (merchantDeliveryPersonnelService.add(request)) {
            return CommonResult.success("新增成功");
        }
        return CommonResult.failed("新增失败");
    }

    @PreAuthorize("hasAuthority('merchant:delivery:personnel:delete')")
    @ApiOperation(value = "删除商户配送人员")
    @RequestMapping(value = "/delete/{id}", method = RequestMethod.POST)
    public CommonResult<String> delete(@PathVariable(value = "id") Integer id) {
        if (merchantDeliveryPersonnelService.delete(id)) {
            return CommonResult.success("删除成功");
        }
        return CommonResult.failed("删除失败");
    }

    @PreAuthorize("hasAuthority('merchant:delivery:personnel:edit')")
    @ApiOperation(value = "编辑商户配送人员")
    @RequestMapping(value = "/edit", method = RequestMethod.POST)
    public CommonResult<String> edit(@RequestBody @Validated MerchantDeliveryPersonnelSaveRequest request) {
        if (merchantDeliveryPersonnelService.edit(request)) {
            return CommonResult.success("编辑成功");
        }
        return CommonResult.failed("编辑失败");
    }
}



