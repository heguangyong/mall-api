package com.zbkj.admin.controller.platform;


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
 * 页面链接 前端控制器
 */
@Slf4j
@RestController
@RequestMapping("api/admin/platform/pagelink")
@Api(tags = "DIY 页面链接 控制器") //配合swagger使用

public class PageLinkController {

    @Autowired
    private PageLinkService pageLinkService;

    /**
     * 分页显示页面链接
     * @param request 搜索条件
     * @param pageParamRequest 分页参数
     * @author dazongzi
     * @since 2023-05-16
     */
    @PreAuthorize("hasAuthority('platform:pagelink:list')")
    @ApiOperation(value = "分页列表") //配合swagger使用
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public CommonResult<CommonPage<PageLink>>  getList(@Validated PageLinkSearchRequest request, @Validated PageParamRequest pageParamRequest){
        CommonPage<PageLink> pageLinkCommonPage = CommonPage.restPage(pageLinkService.getList(request, pageParamRequest));
        return CommonResult.success(pageLinkCommonPage);
    }

    /**
     * 新增页面链接
     * @param pageLinkRequest 新增参数
     * @author dazongzi
     * @since 2023-05-16
     */
    @PreAuthorize("hasAuthority('platform:pagelink:save')")
    @ApiOperation(value = "新增")
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

    /**
     * 删除页面链接
     * @param id Integer
     * @author dazongzi
     * @since 2023-05-16
     */
    @PreAuthorize("hasAuthority('platform:pagelink:delete')")
    @ApiOperation(value = "删除")
    @RequestMapping(value = "/delete", method = RequestMethod.GET)
    public CommonResult<String> delete(@RequestParam(value = "id") Integer id){
        if(pageLinkService.removeById(id)){
            return CommonResult.success();
        }else{
            return CommonResult.failed();
        }
    }

    /**
     * 修改页面链接
     * @param pageLinkRequest 修改参数
     * @author dazongzi
     * @since 2023-05-16
     */
    @PreAuthorize("hasAuthority('platform:pagelink:update')")
    @ApiOperation(value = "修改")
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

    /**
     * 查询页面链接信息
     * @param id Integer
     * @author dazongzi
     * @since 2023-05-16
     */
    @PreAuthorize("hasAuthority('platform:pagelink:info')")
    @ApiOperation(value = "详情")
    @RequestMapping(value = "/info", method = RequestMethod.GET)
    public CommonResult<PageLink> info(@RequestParam(value = "id") Integer id){
        PageLink pageLink = pageLinkService.getById(id);
        return CommonResult.success(pageLink);
   }
}



