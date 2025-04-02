package com.zbkj.admin.controller.platform;

import com.baomidou.mybatisplus.core.metadata.PageList;
import com.github.pagehelper.PageInfo;
import com.zbkj.common.model.wechat.video.PayComponentCat;
import com.zbkj.common.page.CommonPage;
import com.zbkj.common.request.PageParamRequest;
import com.zbkj.common.request.wxvedio.cat.PayComponentCatPageListRequest;
import com.zbkj.common.result.CommonResult;
import com.zbkj.common.vo.wxvedioshop.cat_brand.CatItem;
import com.zbkj.service.service.PayComponentCatService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


/**
 * 组件类目表 前端控制器
 *  +----------------------------------------------------------------------
 *  | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
 *  +----------------------------------------------------------------------
 *  | Copyright (c) 2016~2025 https://www.crmeb.com All rights reserved.
 *  +----------------------------------------------------------------------
 *  | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
 *  +----------------------------------------------------------------------
 *  | Author: CRMEB Team <admin@crmeb.com>
 *  +----------------------------------------------------------------------
 */
@Slf4j
@RestController
@RequestMapping("api/admin/platform/pay/component/cat")
@Api(tags = "04小程序 自定义交易组件—类目") //配合swagger使用
public class PayComponentCatController {

    @Autowired
    private PayComponentCatService payComponentCatService;

    /**
     * 获取类目树形结构
     */
    @PreAuthorize("hasAuthority('platform:pay:component:cat:treelist')")
    @ApiOperation(value = "获取类目列表 树形结构")
    @RequestMapping(value = "/get/treelist", method = RequestMethod.GET)
    public CommonResult<List<CatItem>> getTreeList() {
        return CommonResult.success(payComponentCatService.getTreeList());
    }

    /**
     * 获取类目分页数据
     */
    @PreAuthorize("hasAuthority('platform:pay:component:cat:list')")
    @ApiOperation(value = "获取类目列表 分页 用于平台查看和审核类目")
    @RequestMapping(value = "/get/list", method = RequestMethod.GET)
    public CommonResult<CommonPage<PayComponentCat>> getList(@Validated PayComponentCatPageListRequest payComponentCat,
                                                             @Validated PageParamRequest pageParamRequest) {
        return CommonResult.success( CommonPage.restPage(payComponentCatService.getList(payComponentCat, pageParamRequest)));
    }


    /**
     * 获取类目(测试用，前端不调用)
     */
    @ApiOperation(value = "获取类目(测试用，前端不调用 系统初始化时)")
    @RequestMapping(value = "/get", method = RequestMethod.GET)
    public CommonResult<Object> sendUserCode() {
        payComponentCatService.autoUpdate();
        return CommonResult.success();
    }
}



