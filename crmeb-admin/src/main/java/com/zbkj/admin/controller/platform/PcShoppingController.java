package com.zbkj.admin.controller.platform;

import com.zbkj.admin.service.PcShoppingService;
import com.zbkj.admin.vo.PcShoppingBaseConfigVo;
import com.zbkj.common.result.CommonResult;
import com.zbkj.common.vo.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * PC商城控制器
 * +----------------------------------------------------------------------
 * | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
 * +----------------------------------------------------------------------
 * | Copyright (c) 2016~2025 https://www.crmeb.com All rights reserved.
 * +----------------------------------------------------------------------
 * | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
 * +----------------------------------------------------------------------
 * | Author: CRMEB Team <admin@crmeb.com>
 * +----------------------------------------------------------------------
 */
@Slf4j
@RestController
@RequestMapping("api/admin/platform/pc/shopping")
@Api(tags = "平台端PC商城控制器")
public class PcShoppingController {

    @Autowired
    private PcShoppingService pcShoppingService;

    @PreAuthorize("hasAuthority('platform:pc:shopping:base:config:get')")
    @ApiOperation(value = "获取PC商城基础配置")
    @RequestMapping(value = "/base/config/get", method = RequestMethod.GET)
    public CommonResult<PcShoppingBaseConfigVo> getBaseConfig() {
        return CommonResult.success(pcShoppingService.getBaseConfig());
    }

    @PreAuthorize("hasAuthority('platform:pc:shopping:base:config:edit')")
    @ApiOperation(value = "编辑PC商城基础配置")
    @RequestMapping(value = "/base/config/edit", method = RequestMethod.POST)
    public CommonResult<Object> editBaseConfig(@RequestBody @Validated PcShoppingBaseConfigVo voRequest) {
        if (pcShoppingService.editBaseConfig(voRequest)) {
            return CommonResult.success().setMessage("编辑成功");
        }
        return CommonResult.failed().setMessage("编辑失败");
    }

    @PreAuthorize("hasAuthority('platform:pc:shopping:home:banner:get')")
    @ApiOperation(value = "获取PC商城首页banner")
    @RequestMapping(value = "/home/banner/get", method = RequestMethod.GET)
    public CommonResult<PcHomeBannerListVo> getHomeBanner() {
        PcHomeBannerListVo vo = new PcHomeBannerListVo();
        vo.setBannerList(pcShoppingService.getHomeBanner());
        return CommonResult.success(vo);
    }

    @PreAuthorize("hasAuthority('platform:pc:shopping:home:banner:save')")
    @ApiOperation(value = "保存PC商城首页banner")
    @RequestMapping(value = "/home/banner/save", method = RequestMethod.POST)
    public CommonResult<Object> saveHomeBanner(@RequestBody @Validated PcHomeBannerListVo voRequest) {
        if (pcShoppingService.saveHomeBanner(voRequest.getBannerList())) {
            return CommonResult.success().setMessage("保存成功");
        }
        return CommonResult.failed().setMessage("保存失败");
    }

    @PreAuthorize("hasAuthority('platform:pc:shopping:home:recommended:list')")
    @ApiOperation(value = "获取PC商城首页推荐")
    @RequestMapping(value = "/get/recommended/list", method = RequestMethod.GET)
    public CommonResult<List<PcHomeRecommendedVo>> findHomeRecommendedList() {
        return CommonResult.success(pcShoppingService.findHomeRecommendedList());
    }

    @PreAuthorize("hasAuthority('platform:pc:shopping:home:recommended:add')")
    @ApiOperation(value = "添加PC商城首页推荐板块")
    @RequestMapping(value = "/recommended/add", method = RequestMethod.POST)
    public CommonResult<Object> addHomeRecommended(@RequestBody @Validated PcHomeRecommendedVo voRequest) {
        if (pcShoppingService.addHomeRecommended(voRequest)) {
            return CommonResult.success().setMessage("添加成功");
        }
        return CommonResult.failed().setMessage("添加失败");
    }

    @PreAuthorize("hasAuthority('platform:pc:shopping:home:recommended:edit')")
    @ApiOperation(value = "编辑PC商城首页推荐板块")
    @RequestMapping(value = "/recommended/edit", method = RequestMethod.POST)
    public CommonResult<Object> editHomeRecommended(@RequestBody @Validated PcHomeRecommendedVo voRequest) {
        if (pcShoppingService.editHomeRecommended(voRequest)) {
            return CommonResult.success().setMessage("编辑成功");
        }
        return CommonResult.failed().setMessage("编辑失败");
    }

    @PreAuthorize("hasAuthority('platform:pc:shopping:home:recommended:delete')")
    @ApiOperation(value = "删除PC商城首页推荐板块")
    @RequestMapping(value = "/recommended/delete/{id}", method = RequestMethod.POST)
    public CommonResult<Object> deleteHomeRecommended(@PathVariable(value = "id") Integer id) {
        if (pcShoppingService.deleteHomeRecommended(id)) {
            return CommonResult.success().setMessage("删除成功");
        }
        return CommonResult.failed().setMessage("删除失败");
    }

    @PreAuthorize("hasAuthority('platform:pc:shopping:home:recommended:switch')")
    @ApiOperation(value = "PC商城首页推荐板块开关")
    @RequestMapping(value = "/recommended/switch/{id}", method = RequestMethod.POST)
    public CommonResult<Object> switchHomeRecommended(@PathVariable(value = "id") Integer id) {
        if (pcShoppingService.switchHomeRecommended(id)) {
            return CommonResult.success().setMessage("变更开关状态成功");
        }
        return CommonResult.failed().setMessage("变更开关状态失败");
    }

    @PreAuthorize("hasAuthority('platform:pc:shopping:home:advertisement:get')")
    @ApiOperation(value = "获取PC商城首页广告")
    @RequestMapping(value = "/home/advertisement/get", method = RequestMethod.GET)
    public CommonResult<PcHomeAdvertisementVo> getHomeAdvertisement() {
        return CommonResult.success(pcShoppingService.getHomeAdvertisement());
    }

    @PreAuthorize("hasAuthority('platform:pc:shopping:home:advertisement:edit')")
    @ApiOperation(value = "编辑PC商城首页广告")
    @RequestMapping(value = "/home/advertisement/edit", method = RequestMethod.POST)
    public CommonResult<Object> editHomeAdvertisement(@RequestBody @Validated PcHomeAdvertisementVo voRequest) {
        if (pcShoppingService.editHomeAdvertisement(voRequest)) {
            return CommonResult.success().setMessage("编辑成功");
        }
        return CommonResult.failed().setMessage("编辑失败");
    }

    @PreAuthorize("hasAuthority('platform:pc:shopping:philosophy:get')")
    @ApiOperation(value = "获取PC商城经营理念配置")
    @RequestMapping(value = "/philosophy/get", method = RequestMethod.GET)
    public CommonResult<PcPhilosophyListVo> getPhilosophy() {
        PcPhilosophyListVo vo = new PcPhilosophyListVo();
        vo.setPhilosophyVoList(pcShoppingService.getPhilosophy());
        return CommonResult.success(vo);
    }

    @PreAuthorize("hasAuthority('platform:pc:shopping:philosophy:save')")
    @ApiOperation(value = "保存PC商城经营理念配置")
    @RequestMapping(value = "/philosophy/save", method = RequestMethod.POST)
    public CommonResult<Object> savePhilosophy(@RequestBody @Validated PcPhilosophyListVo voRequest) {
        if (pcShoppingService.savePhilosophy(voRequest.getPhilosophyVoList())) {
            return CommonResult.success().setMessage("保存成功");
        }
        return CommonResult.failed().setMessage("保存失败");
    }

//    @PreAuthorize("hasAuthority('platform:pc:shopping:bottom:authorize:get')")
//    @ApiOperation(value = "获取PC商城底部授权配置")
//    @RequestMapping(value = "/bottom/authorize/get", method = RequestMethod.GET)
//    public CommonResult<PcBottomAuthorizeVo> getBottomAuthorize() {
//        return CommonResult.success(pcShoppingService.getBottomAuthorize());
//    }

//    @PreAuthorize("hasAuthority('platform:pc:shopping:bottom:authorize:save')")
//    @ApiOperation(value = "保存PC商城底部授权配置")
//    @RequestMapping(value = "/bottom/authorize/save", method = RequestMethod.POST)
//    public CommonResult<Object> saveBottomAuthorize(@RequestBody @Validated PcBottomAuthorizeVo voRequest) {
//        if (pcShoppingService.saveBottomAuthorize(voRequest)) {
//            return CommonResult.success().setMessage("保存成功");
//        }
//        return CommonResult.failed().setMessage("保存失败");
//    }

    @PreAuthorize("hasAuthority('platform:pc:shopping:quick:entry:get')")
    @ApiOperation(value = "获取PC商城快捷入口配置")
    @RequestMapping(value = "/quick/entry/get", method = RequestMethod.GET)
    public CommonResult<List<PcQuickEntryVo>> getQuickEntry() {
        return CommonResult.success(pcShoppingService.getQuickEntry());
    }

    @PreAuthorize("hasAuthority('platform:pc:shopping:quick:entry:save')")
    @ApiOperation(value = "保存PC商城快捷入口配置")
    @RequestMapping(value = "/quick/entry/save", method = RequestMethod.POST)
    public CommonResult<Object> saveQuickEntry(@RequestBody @Validated List<PcQuickEntryVo> voListRequest) {
        if (pcShoppingService.saveQuickEntry(voListRequest)) {
            return CommonResult.success().setMessage("保存成功");
        }
        return CommonResult.failed().setMessage("保存失败");
    }

    @PreAuthorize("hasAuthority('platform:pc:shopping:bottom:qrcode:get')")
    @ApiOperation(value = "获取PC商城底部二维码配置")
    @RequestMapping(value = "/bottom/qrcode/get", method = RequestMethod.GET)
    public CommonResult<List<PcBottomQrCodeVo>> getBottomQrCode() {
        return CommonResult.success(pcShoppingService.getBottomQrCode());
    }

    @PreAuthorize("hasAuthority('platform:pc:shopping:bottom:qrcode:save')")
    @ApiOperation(value = "保存PC商城底部二维码配置")
    @RequestMapping(value = "/bottom/qrcode/save", method = RequestMethod.POST)
    public CommonResult<Object> saveBottomQrCode(@RequestBody @Validated List<PcBottomQrCodeVo> voListRequest) {
        if (pcShoppingService.saveBottomQrCode(voListRequest)) {
            return CommonResult.success().setMessage("保存成功");
        }
        return CommonResult.failed().setMessage("保存失败");
    }

    @PreAuthorize("hasAuthority('platform:pc:shopping:friendly:link:get')")
    @ApiOperation(value = "获取PC商城友情链接配置")
    @RequestMapping(value = "/friendly/link/get", method = RequestMethod.GET)
    public CommonResult<List<PcFriendlyLinkVo>> getFriendlyLinks() {
        return CommonResult.success(pcShoppingService.getFriendlyLinks());
    }

    @PreAuthorize("hasAuthority('platform:pc:shopping:friendly:link:save')")
    @ApiOperation(value = "保存PC商城友情链接配置")
    @RequestMapping(value = "/friendly/link/save", method = RequestMethod.POST)
    public CommonResult<Object> saveFriendlyLinks(@RequestBody @Validated List<PcFriendlyLinkVo> voListRequest) {
        if (pcShoppingService.saveFriendlyLinks(voListRequest)) {
            return CommonResult.success().setMessage("保存成功");
        }
        return CommonResult.failed().setMessage("保存失败");
    }

    @PreAuthorize("hasAuthority('platform:pc:shopping:home:navigation:get')")
    @ApiOperation(value = "获取PC商城首页导航配置")
    @RequestMapping(value = "/home/navigation/get", method = RequestMethod.GET)
    public CommonResult<List<PcHomeNavigationVo>> getHomeNavigation() {
        return CommonResult.success(pcShoppingService.getHomeNavigation());
    }

    @PreAuthorize("hasAuthority('platform:pc:shopping:home:navigation:save')")
    @ApiOperation(value = "保存PC商城首页导航配置")
    @RequestMapping(value = "/home/navigation/save", method = RequestMethod.POST)
    public CommonResult<Object> saveHomeNavigation(@RequestBody @Validated List<PcHomeNavigationVo> voListRequest) {
        if (pcShoppingService.saveHomeNavigation(voListRequest)) {
            return CommonResult.success().setMessage("保存成功");
        }
        return CommonResult.failed().setMessage("保存失败");
    }
}
