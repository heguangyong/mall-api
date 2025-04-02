package com.zbkj.admin.controller.merchant;

import com.zbkj.common.model.page.PageLink;
import com.zbkj.common.page.CommonPage;
import com.zbkj.common.request.PageParamRequest;
import com.zbkj.common.request.page.PageLinkRequest;
import com.zbkj.common.request.page.PageLinkSearchRequest;
import com.zbkj.common.result.CommonResult;
import com.zbkj.service.service.PageLinkService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 商户装修页面链接控制器
 *
 * @author Han
 * @version 1.0.0
 * @Date 2024/10/28
 */
@Slf4j
@RestController
@RequestMapping("api/admin/merchant/page/link")
@Api(tags = "商户装修页面链接控制器") //配合swagger使用
public class MerchantPageLinkController {

    @Autowired
    private PageLinkService pageLinkService;

    @PreAuthorize("hasAuthority('platform:pagelink:list')")
    @ApiOperation(value = "页面链接分页列表") //配合swagger使用
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public CommonResult<CommonPage<PageLink>> getList(@Validated PageLinkSearchRequest request, @Validated PageParamRequest pageParamRequest){
        CommonPage<PageLink> pageLinkCommonPage = CommonPage.restPage(pageLinkService.getList(request, pageParamRequest));
        return CommonResult.success(pageLinkCommonPage);
    }

    @PreAuthorize("hasAuthority('platform:pagelink:save')")
    @ApiOperation(value = "新增页面链接")
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    public CommonResult<String> save(@RequestBody @Validated PageLinkRequest pageLinkRequest){
        PageLink pageLink = new PageLink();
        BeanUtils.copyProperties(pageLinkRequest, pageLink);

        if(pageLinkService.save(pageLink)){
            return CommonResult.success();
        }else{
            return CommonResult.failed();
        }
    }

    @PreAuthorize("hasAuthority('platform:pagelink:delete')")
    @ApiOperation(value = "删除页面链接")
    @RequestMapping(value = "/delete", method = RequestMethod.GET)
    public CommonResult<String> delete(@RequestParam(value = "id") Integer id){
        if(pageLinkService.removeById(id)){
            return CommonResult.success();
        }else{
            return CommonResult.failed();
        }
    }

    @PreAuthorize("hasAuthority('platform:pagelink:update')")
    @ApiOperation(value = "修改页面链接")
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public CommonResult<String> update(@RequestBody @Validated PageLinkRequest pageLinkRequest){
        PageLink pageLink = new PageLink();
        BeanUtils.copyProperties(pageLinkRequest, pageLink);

        if(pageLinkService.updateById(pageLink)){
            return CommonResult.success();
        }else{
            return CommonResult.failed();
        }
    }

    @PreAuthorize("hasAuthority('platform:pagelink:info')")
    @ApiOperation(value = "查询页面链接信息")
    @RequestMapping(value = "/info", method = RequestMethod.GET)
    public CommonResult<PageLink> info(@RequestParam(value = "id") Integer id){
        PageLink pageLink = pageLinkService.getById(id);
        return CommonResult.success(pageLink);
    }
}
