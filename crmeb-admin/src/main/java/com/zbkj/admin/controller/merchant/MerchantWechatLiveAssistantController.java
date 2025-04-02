package com.zbkj.admin.controller.merchant;


import com.zbkj.common.model.wechat.live.WechatLiveAssistant;
import com.zbkj.common.page.CommonPage;
import com.zbkj.common.request.PageParamRequest;
import com.zbkj.common.request.wxmplive.assistant.WechatLiveAssistantAddRequest;
import com.zbkj.common.result.CommonResult;
import com.zbkj.service.service.WechatLiveAssistantService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


/**
 * 前端控制器
 */
@Slf4j
@RestController
@RequestMapping("api/admin/merchant/mp/live/assistant")
@Api(tags = "商户端 - 微信小程序 - 直播 - 小助手") //配合swagger使用
public class MerchantWechatLiveAssistantController {

    @Autowired
    private WechatLiveAssistantService wechatLiveAssistantService;

    /**
     * 分页显示
     *
     * @param keywords         搜索条件
     * @param pageParamRequest 分页参数
     * @author dazongzi
     * @since 2023-03-27
     */
    @PreAuthorize("hasAuthority('merchant:mp:live:assistant:list')")
    @ApiOperation(value = "分页列表") //配合swagger使用
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "keywords", value = "微信号和微信昵称", dataType = "String")
    })
    public CommonResult<CommonPage<WechatLiveAssistant>> getList(@RequestParam String keywords, @Validated PageParamRequest pageParamRequest) {
        CommonPage<WechatLiveAssistant> wechatLiveAssistantCommonPage = CommonPage.restPage(wechatLiveAssistantService.getList(keywords, pageParamRequest));
        return CommonResult.success(wechatLiveAssistantCommonPage);
    }

    /**
     * 新增
     *
     * @param wechatLiveAssistant 新增参数
     * @author dazongzi
     * @since 2023-03-27
     */
    @PreAuthorize("hasAuthority('merchant:mp:live:assistant:save')")
    @ApiOperation(value = "新增")
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    public CommonResult<String> save(@RequestBody @Validated WechatLiveAssistantAddRequest wechatLiveAssistant) {
        WechatLiveAssistant assistant = new WechatLiveAssistant();
        BeanUtils.copyProperties(wechatLiveAssistant, assistant);
        if (wechatLiveAssistantService.saveUnique(assistant)) {
            return CommonResult.success();
        }
        return CommonResult.failed();
    }

    /**
     * 删除
     *
     * @param id Integer
     * @author dazongzi
     * @since 2023-03-27
     */
    @PreAuthorize("hasAuthority('merchant:mp:live:assistant:delete')")
    @ApiOperation(value = "删除")
    @RequestMapping(value = "/delete/{id}", method = RequestMethod.GET)
    public CommonResult<String> delete(@PathVariable(value = "id") Integer id) {
        if (wechatLiveAssistantService.deleteById(id)) {
            return CommonResult.success();
        }
        return CommonResult.failed();
    }

    /**
     * 修改
     *
     * @param wechatLiveAssistant 修改参数
     * @author dazongzi
     * @since 2023-03-27
     */
    @PreAuthorize("hasAuthority('merchant:mp:live:assistant:edit')")
    @ApiOperation(value = "修改")
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public CommonResult<String> update(@RequestBody @Validated WechatLiveAssistantAddRequest wechatLiveAssistant) {
        if (wechatLiveAssistantService.edit(wechatLiveAssistant)) {
            return CommonResult.success();
        }
        return CommonResult.failed();
    }

    /**
     * 查询信息
     *
     * @param id Integer
     * @author dazongzi
     * @since 2023-03-27
     */
    @PreAuthorize("hasAuthority('merchant:mp:live:assistant:info')")
    @ApiOperation(value = "详情")
    @RequestMapping(value = "/info/{id}", method = RequestMethod.GET)
    public CommonResult<WechatLiveAssistant> info(@PathVariable(value = "id") Integer id) {
        return CommonResult.success(wechatLiveAssistantService.getAssistantInfo(id));
    }
}



