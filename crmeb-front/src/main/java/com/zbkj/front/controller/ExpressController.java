package com.zbkj.front.controller;

import com.zbkj.common.model.express.Express;
import com.zbkj.common.result.CommonResult;
import com.zbkj.service.service.ExpressService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 物流公司控制器
 *
 * @author Hzw
 * @version 1.0.0
 * @Date 2023/12/9
 */
@Slf4j
@RestController
@RequestMapping("api/front/express")
@Api(tags = "物流公司控制器")
public class ExpressController {

    @Autowired
    private ExpressService expressService;

    @ApiOperation(value = "获取物流公司列表")
    @RequestMapping(value = "/all", method = RequestMethod.GET)
    public CommonResult<List<Express>> getAllList() {
        return CommonResult.success(expressService.findAll("normal"));
    }

}
