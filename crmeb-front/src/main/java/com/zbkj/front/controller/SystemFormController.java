package com.zbkj.front.controller;

import com.zbkj.common.model.system.SystemForm;
import com.zbkj.common.result.CommonResult;
import com.zbkj.service.service.SystemFormService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * 系统表单控制器
 *
 * @author Han
 * @version 1.0.0
 * @Date 2024/8/26
 */
@Slf4j
@RestController
@RequestMapping("api/front/system/form")
@Api(tags = "系统表单控制器")
public class SystemFormController {

    @Autowired
    private SystemFormService systemFormService;

    @ApiOperation(value = "系统表单详情")
    @RequestMapping(value = "/detail/{id}", method = RequestMethod.GET)
    public CommonResult<SystemForm> getDetail(@PathVariable("id") Integer id) {
        return CommonResult.success(systemFormService.getFrontDetail(id));
    }

}
