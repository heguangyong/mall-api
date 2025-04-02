package com.zbkj.admin.controller.merchant;

import com.zbkj.common.model.wechat.live.WechatLiveGoods;
import com.zbkj.common.page.CommonPage;
import com.zbkj.common.request.PageParamRequest;
import com.zbkj.common.request.wxmplive.goods.WechatLiveGoodsAddRequest;
import com.zbkj.common.request.wxmplive.goods.WechatLiveGoodsEditRequest;
import com.zbkj.common.request.wxmplive.goods.WechatLiveGoodsSearchRequest;
import com.zbkj.common.result.CommonResult;
import com.zbkj.service.service.WechatLiveGoodsService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;


/**
 * 前端控制器
 */
@Slf4j
@RestController
@RequestMapping("api/admin/merchant/mp/live/goods")
@Api(tags = "商户端 - 微信小程序 - 直播 - 商品") //配合swagger使用
public class MerchantWechatLiveGoodsController {

    @Autowired
    private WechatLiveGoodsService wechatLiveGoodsService;

    /**
     * 分页显示
     *
     * @param request          搜索条件
     * @param pageParamRequest 分页参数
     * @author dazongzi
     * @since 2023-03-27
     */
    @PreAuthorize("hasAuthority('merchant:mp:live:goods:list')")
    @ApiOperation(value = "分页列表") //配合swagger使用
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public CommonResult<CommonPage<WechatLiveGoods>> getList(@Validated WechatLiveGoodsSearchRequest request, @Validated PageParamRequest pageParamRequest) {
        CommonPage<WechatLiveGoods> wechatLiveGoodsCommonPage = CommonPage.restPage(wechatLiveGoodsService.getList(request, pageParamRequest, Boolean.TRUE));
        return CommonResult.success(wechatLiveGoodsCommonPage);
    }

    /**
     * 新增
     *
     * @param wechatLiveGoodsRequest 新增参数
     * @author dazongzi
     * @since 2023-03-27
     */
    @PreAuthorize("hasAuthority('merchant:mp:live:goods:save')")
    @ApiOperation(value = "批量新增并提审")
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    public CommonResult<String> save(@RequestBody @Validated List<WechatLiveGoodsAddRequest> wechatLiveGoodsRequest) {
        if (wechatLiveGoodsService.addGoods(wechatLiveGoodsRequest)) {
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
    @PreAuthorize("hasAuthority('merchant:mp:live:goods:delete')")
    @ApiOperation(value = "删除")
    @RequestMapping(value = "/delete/{id}", method = RequestMethod.GET)
    public CommonResult<String> delete(@PathVariable(name = "id") Integer id) {
        if (wechatLiveGoodsService.deleteGoods(id)) {
            return CommonResult.success();
        }
        return CommonResult.failed();
    }

    /**
     * 修改
     *
     * @param wechatLiveGoodsRequest 修改参数
     * @author dazongzi
     * @since 2023-03-27
     */
    @PreAuthorize("hasAuthority('merchant:mp:live:goods:edit')")
    @ApiOperation(value = "修改")
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public CommonResult<String> update(@RequestBody @Validated WechatLiveGoodsEditRequest wechatLiveGoodsRequest) {
        if (wechatLiveGoodsService.updateGoods(wechatLiveGoodsRequest)) {
            return CommonResult.success();
        }
        return CommonResult.failed();
    }

    @PreAuthorize("hasAuthority('merchant:mp:live:goods:sort')")
    @ApiOperation(value = "修改排序")
    @RequestMapping(value = "/sort/{id}/{sort}", method = RequestMethod.GET)
    public CommonResult<String> sort(@PathVariable(name = "id") Integer id, @PathVariable(name = "sort") Integer sort) {
        if (wechatLiveGoodsService.updateSort(id, sort)) {
            return CommonResult.success();
        }
        return CommonResult.failed();
    }

    /**
     * 查询信息
     *
     * @author dazongzi
     * @since 2023-03-27
     */
    @PreAuthorize("hasAuthority('merchant:mp:live:goods:info')")
    @ApiOperation(value = "详情")
    @RequestMapping(value = "/info/{id}", method = RequestMethod.GET)
    public CommonResult<WechatLiveGoods> info(@PathVariable(value = "id") Integer id) {
        WechatLiveGoods wechatLiveGoods = wechatLiveGoodsService.getGoodInfo(id);
        return CommonResult.success(wechatLiveGoods);
    }

    @PreAuthorize("hasAuthority('merchant:mp:live:goods:reset')")
    @ApiOperation(value = "撤回申请")
    @RequestMapping(value = "/audit/reset/{id}", method = RequestMethod.GET)
    public CommonResult<Boolean> resetAudit(@PathVariable(value = "id") Integer id) {
        return CommonResult.success(wechatLiveGoodsService.merchantResetAudit(id));
    }

    @PreAuthorize("hasAuthority('merchant:mp:live:goods:audit')")
    @ApiOperation(value = "重新提交申请")
    @RequestMapping(value = "/audit/{id}", method = RequestMethod.GET)
    public CommonResult<String> auditGoods(@PathVariable(value = "id") Integer id) {
        return CommonResult.success(wechatLiveGoodsService.merchantAuditGoods(id));
    }

    // 用于测试
    @ApiOperation(value = "用于测试 获取直播商品审核状态")
    @RequestMapping(value = "/auditforwx", method = RequestMethod.GET)
    public CommonResult<String> auditWareHouse() {
        wechatLiveGoodsService.getGoodsWareHouse();
        return CommonResult.success();
    }
}



