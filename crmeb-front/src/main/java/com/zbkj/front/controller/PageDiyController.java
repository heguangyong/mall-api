package com.zbkj.front.controller;


import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSON;
import com.zbkj.common.exception.CrmebException;
import com.zbkj.common.model.page.PageDiy;
import com.zbkj.common.page.CommonPage;
import com.zbkj.common.request.page.PageDiyRequest;
import com.zbkj.common.response.page.PageDiyResponse;
import com.zbkj.common.result.CommonResult;
import com.zbkj.service.service.PageDiyService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


/**
 * DIY数据表 前端控制器
 */
@Slf4j
@RestController
@RequestMapping("api/front/pagediy")
@Api(tags = "DIY 控制器") //配合swagger使用

public class PageDiyController {

    @Autowired
    private PageDiyService pageDiyService;
    /**
     * 查询DIY数据表信息
     * @param id Integer
     * @author dazongzi
     * @since 2023-05-16
     */
    @ApiOperation(value = "详情")
    @RequestMapping(value = "/info/{id}", method = RequestMethod.GET)
    public CommonResult<PageDiyResponse> info(@PathVariable(value = "id") Integer id){
        PageDiyResponse response = pageDiyService.getDiyPageByPageIdForFront(id);
        if(ObjectUtil.isNull(response)) throw new CrmebException("未找到对应模版信息");
        return CommonResult.success(response);
   }
}



