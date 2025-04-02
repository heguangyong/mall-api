package com.zbkj.admin.controller.merchant;

import com.zbkj.common.model.merchant.MerchantEmployee;
import com.zbkj.common.page.CommonPage;
import com.zbkj.common.request.merchant.manage.MerchantEmployeeRequest;
import com.zbkj.common.request.merchant.manage.MerchantEmployeeSearchRequest;

import com.zbkj.common.result.CommonResult;
import com.zbkj.service.service.MerchantEmployeeService;
import com.zbkj.service.service.SystemAttachmentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;



/**
 *  前端控制器
 */
@Slf4j
@RestController
@RequestMapping("api/admin/merchant/employee")
@Api(tags = "商家端 - 移动端管理员管理") //配合swagger使用

public class MerchantEmployeeController {

    @Autowired
    private MerchantEmployeeService merchantEmployeeService;


    /**
     * 分页显示
     * @param request 搜索条件
     * @author dazongzi
     * @since 2024-05-24
     */
    @ApiOperation(value = "分页列表") //配合swagger使用
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public CommonResult<CommonPage<MerchantEmployee>>  getList(@Validated MerchantEmployeeSearchRequest request) {
        CommonPage<MerchantEmployee> merchantEmployeeCommonPage = CommonPage.restPage(merchantEmployeeService.getList(request));
        return CommonResult.success(merchantEmployeeCommonPage);
    }

    /**
     * 新增
     * @param merchantEmployeeRequest 新增参数
     * @author dazongzi
     * @since 2024-05-24
     */
    @ApiOperation(value = "新增")
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    public CommonResult<String> save(@RequestBody @Validated MerchantEmployeeRequest merchantEmployeeRequest) {
        if(merchantEmployeeService.addMerchantEmployee(merchantEmployeeRequest)) {
            return CommonResult.success();
        } else {
            return CommonResult.failed();
        }
    }

    /**
     * 删除
     * @param id Integer
     * @author dazongzi
     * @since 2024-05-24
     */
    @ApiOperation(value = "删除")
    @RequestMapping(value = "/delete", method = RequestMethod.GET)
    public CommonResult<String> delete(@RequestParam(value = "id") Integer id) {
        if(merchantEmployeeService.deleteById(id)) {
            return CommonResult.success();
        } else {
            return CommonResult.failed();
        }
    }

    /**
     * 修改
     * @param merchantEmployeeRequest 修改参数
     * @author dazongzi
     * @since 2024-05-24
     */
    @ApiOperation(value = "修改")
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public CommonResult<String> update(@RequestBody @Validated MerchantEmployeeRequest merchantEmployeeRequest) {
        if(merchantEmployeeService.editMerchantEmployee(merchantEmployeeRequest)) {
            return CommonResult.success();
        } else {
            return CommonResult.failed();
        }
    }

    /**
     * 查询信息
     * @param id Integer
     * @author dazongzi
     * @since 2024-05-24
     */
    @ApiOperation(value = "详情")
    @RequestMapping(value = "/info", method = RequestMethod.GET)
    public CommonResult<MerchantEmployee> info(@RequestParam(value = "id") Integer id) {
        MerchantEmployee merchantEmployee = merchantEmployeeService.getById(id);
        return CommonResult.success(merchantEmployee);
    }
}



