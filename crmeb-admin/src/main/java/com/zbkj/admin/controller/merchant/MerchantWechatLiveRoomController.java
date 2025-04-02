package com.zbkj.admin.controller.merchant;

import com.zbkj.common.model.wechat.live.WechatLiveAssistant;
import com.zbkj.common.model.wechat.live.WechatLiveRoom;
import com.zbkj.common.page.CommonPage;
import com.zbkj.common.request.wxmplive.assistant.WechatMpLiveAddAssistantToRoomRequest;
import com.zbkj.common.request.wxmplive.goods.WechatLiveGoodsInRoomResponse;
import com.zbkj.common.request.wxmplive.goods.WechatMpLiveAddGoodsToRoomRequest;
import com.zbkj.common.request.wxmplive.goods.WechatMpLiveGoodsOnSaleRequest;
import com.zbkj.common.request.wxmplive.goods.WechatMpLiveGoodsSortRequest;
import com.zbkj.common.request.wxmplive.room.WechatLiveRoomSharCode;
import com.zbkj.common.request.wxmplive.room.WechatMpLiveRoomInfoRequest;
import com.zbkj.common.request.wxmplive.room.WechatMpLiveRoomSearchRequest;
import com.zbkj.common.result.CommonResult;
import com.zbkj.service.service.WechatLiveRoomService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @Auther: 大粽子
 * @Date: 2023/3/9 10:54
 * @Description: 商户微信小程序直播控制器
 */
@Slf4j
@RestController
@RequestMapping("api/admin/merchant/mp/live/room")
@Api(tags = "商户端 - 微信小程序 - 直播间管理")
@Validated
public class MerchantWechatLiveRoomController {

    @Autowired
    private WechatLiveRoomService weChatLiveRoomService;

    @PreAuthorize("hasAuthority('merchant:mp:live:room:create')")
    @ApiOperation(value = "商户端 - 小程序直播 - 创建直播室")
    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public CommonResult<Boolean> WechatMPLiveCreateRoom(@RequestBody @Validated WechatMpLiveRoomInfoRequest wxMaLiveRoomInfo) {
        return CommonResult.success(weChatLiveRoomService.creteRoom(wxMaLiveRoomInfo));
    }

    @PreAuthorize("hasAuthority('merchant:mp:live:room:delete')")
    @ApiOperation(value = "商户端 - 小程序直播 - 删除直播室")
    @RequestMapping(value = "/delete/{id}", method = RequestMethod.GET)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "待删除的直播间id", dataType = "int")
    })
    public CommonResult<Object> WechatMPLiveDeleteRoom(@PathVariable Integer id) {
        if (weChatLiveRoomService.deleteRoom(id)) {
            return CommonResult.success();
        }
        return CommonResult.failed();
    }

    @PreAuthorize("hasAuthority('merchant:mp:live:room:edit')")
    @ApiOperation(value = "商户端 - 小程序直播 - 编辑直播室")
    @RequestMapping(value = "/edit", method = RequestMethod.POST)
    public CommonResult<Boolean> WechatMPLiveEditRoom(@RequestBody @Validated WechatMpLiveRoomInfoRequest roomInfo) {
        if (weChatLiveRoomService.editRoom(roomInfo)) {
            return CommonResult.success();
        }
        return CommonResult.failed();
    }

    @PreAuthorize("hasAuthority('merchant:mp:live:room:info')")
    @ApiOperation(value = "商户端 - 小程序直播 - 直播间详情")
    @RequestMapping(value = "/info/{id}", method = RequestMethod.GET)
    public CommonResult<WechatLiveRoom> WechatMPLiveRoomInfo(@PathVariable Integer id) {
        return CommonResult.success(weChatLiveRoomService.getRoomInfoById(id));
    }

    @PreAuthorize("hasAuthority('merchant:mp:live:room:list')")
    @ApiOperation(value = "商户端 - 小程序直播 - 直播室列表和回放")
    @RequestMapping(value = "/list", method = RequestMethod.POST)
    public CommonResult<CommonPage<WechatLiveRoom>> WechatMPLiveListRoom(@RequestBody WechatMpLiveRoomSearchRequest weChatMpLiveRoomSearchRequest) {
        return CommonResult.success(CommonPage.restPage(weChatLiveRoomService.getLiveList(weChatMpLiveRoomSearchRequest.getSearchRequest(),
                weChatMpLiveRoomSearchRequest.getPageParamRequest(), Boolean.TRUE)));
    }


    @PreAuthorize("hasAuthority('merchant:mp:live:room:addgoods')")
    @ApiOperation(value = "商户端 - 小程序直播 - 导入商品")
    @RequestMapping(value = "/addgoods", method = RequestMethod.POST)
    public CommonResult<Boolean> WechatMPLiveAddGoodsRoom(@RequestBody WechatMpLiveAddGoodsToRoomRequest weChatMpLiveAddGoodsRequest) {
        return CommonResult.success(weChatLiveRoomService.addGoodsToRoom(weChatMpLiveAddGoodsRequest.getId(),
                weChatMpLiveAddGoodsRequest.getIds()));
    }

    @PreAuthorize("hasAuthority('merchant:mp:live:room:goodslist')")
    @ApiOperation(value = "商户端 - 小程序直播 - 直播间商品列表")
    @RequestMapping(value = "/goodslist/{id}", method = RequestMethod.GET)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "直播间ID", dataType = "int")
    })
    public CommonResult<List<WechatLiveGoodsInRoomResponse>> WechatMPLiveRoomGoodsList(@PathVariable Integer id) {
        return CommonResult.success(weChatLiveRoomService.getRoomGoodsList(id));
    }


    @PreAuthorize("hasAuthority('merchant:mp:live:room:getsharecode')")
    @ApiOperation(value = "商户端 - 小程序直播 - 获取直播间分享二维码")
    @RequestMapping(value = "/getsharecode/{id}", method = RequestMethod.GET)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "待获取分享二维码的直播间id", dataType = "int")
    })
    public CommonResult<WechatLiveRoomSharCode> WechatMPLiveRoomGetShareCode(@PathVariable Integer id) {
        return CommonResult.success(weChatLiveRoomService.getShareQRCodeByRoomId(id, null));
    }

    @PreAuthorize("hasAuthority('merchant:mp:live:room:deletegoods')")
    @ApiOperation(value = "商户端 - 小程序直播 - 删除直播间商品")
    @RequestMapping(value = "/deletegoodsinroom/{id}/{goodsId}", method = RequestMethod.GET)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "直播间id", dataType = "int"),
            @ApiImplicitParam(name = "goodsId", value = "删除的商品id", dataType = "int")
    })
    public CommonResult<Boolean> WechatMPLiveDeleteGoodsInRoom(@PathVariable Integer id, @PathVariable Integer goodsId) {
        return CommonResult.success(weChatLiveRoomService.deleteGoodsInRoom(id, goodsId));
    }

    @PreAuthorize("hasAuthority('merchant:mp:live:room:goodsonsale')")
    @ApiOperation(value = "商户端 - 小程序直播 - 商品上下架")
    @RequestMapping(value = "/goodsonsale", method = RequestMethod.POST)
    public CommonResult<Boolean> WechatMPLiveGoodsOnSale(@RequestBody WechatMpLiveGoodsOnSaleRequest request) {
        return CommonResult.success(weChatLiveRoomService.goodsOnSale(request.getRoomId(), request.getGoodsId(), request.getOnSale()));
    }

    @PreAuthorize("hasAuthority('merchant:mp:live:room:goodssort')")
    @ApiOperation(value = "商户端 - 小程序直播 - 商品排序")
    @RequestMapping(value = "/goodsort", method = RequestMethod.POST)
    public CommonResult<Boolean> WechatMPLiveGoodsOnSale(@RequestBody WechatMpLiveGoodsSortRequest request) {
        return CommonResult.success(weChatLiveRoomService.goodsSort(request.getRoomId(), request.getGoods()));
    }

    @PreAuthorize("hasAuthority('merchant:mp:live:room:mangerass')")
    @ApiOperation(value = "商户端 - 小程序直播 - 管理直播间小助手")
    @RequestMapping(value = "/mangerass", method = RequestMethod.POST)
    public CommonResult<Boolean> WechatMPLiveAddAssistantToRoom(@RequestBody WechatMpLiveAddAssistantToRoomRequest request) {
        return CommonResult.success(weChatLiveRoomService.mangerAssistant(request.getId(), request.getAssid()));
    }

    @PreAuthorize("hasAuthority('merchant:mp:live:room:getass')")
    @ApiOperation(value = "商户端 - 小程序直播 - 查询直播间小助手")
    @RequestMapping(value = "/getass/{id}", method = RequestMethod.GET)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "本地直播间id", dataType = "int")
    })
    public CommonResult<List<WechatLiveAssistant>> WechatMPLiveModifyAssistant(@PathVariable Integer id) {
        return CommonResult.success(weChatLiveRoomService.getAssistantByRoom(id));
    }

    @PreAuthorize("hasAuthority('merchant:mp:live:room:updatecomment')")
    @ApiOperation(value = "商户端 - 小程序直播 - 禁言管理")
    @RequestMapping(value = "/updatecomment/{id}/{banComment}", method = RequestMethod.GET)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "直播间id", dataType = "int"),
            @ApiImplicitParam(name = "banComment", value = "1-禁言，0-取消禁言", dataType = "int", allowableValues = "0,1")
    })
    public CommonResult<Boolean> WechatMPLiveUpdateComment(@PathVariable Integer id, @PathVariable Integer banComment) {
        return CommonResult.success(weChatLiveRoomService.updateComment(id, banComment));
    }


    @PreAuthorize("hasAuthority('merchant:mp:live:room:isfeedspublic')")
    @ApiOperation(value = "商户端 - 小程序直播 - 官方收录管理")
    @RequestMapping(value = "/updatefeedpublic/{id}/{isFeedsPublic}", method = RequestMethod.GET)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "本地直播间id", dataType = "int"),
            @ApiImplicitParam(name = "isFeedsPublic", value = "是否开启官方收录 【1: 开启，0：关闭】", dataType = "int", allowableValues = "0,1")
    })
    public CommonResult<Boolean> WechatMPLiveUpdateFeedPublic(@PathVariable Integer id, @PathVariable Integer isFeedsPublic) {
        return CommonResult.success(weChatLiveRoomService.updateFeedPublic(id, isFeedsPublic));
    }

    @PreAuthorize("hasAuthority('merchant:mp:live:room:closekf')")
    @ApiOperation(value = "商户端 - 小程序直播 - 客服管理")
    @RequestMapping(value = "/updateclosekf/{id}/{closeKf}", method = RequestMethod.GET)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "直播间id", dataType = "int"),
            @ApiImplicitParam(name = "closeKf", value = "是否关闭客服 【0：开启，1：关闭】", dataType = "int", allowableValues = "0,1")
    })
    public CommonResult<Boolean> WechatMPLiveUpdateKF(@PathVariable Integer id, @PathVariable Integer closeKf) {
        return CommonResult.success(weChatLiveRoomService.updateKF(id, closeKf));
    }

    @PreAuthorize("hasAuthority('merchant:mp:live:room:updatereplay')")
    @ApiOperation(value = "商户端 - 小程序直播 - 开启回放")
    @RequestMapping(value = "/updatereplay/{id}/{closeReplay}", method = RequestMethod.GET)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "直播间id", dataType = "int"),
            @ApiImplicitParam(name = "closeReplay", value = "是否关闭回放 【0：开启，1：关闭】", dataType = "int", allowableValues = "0,1")
    })
    public CommonResult<Boolean> WechatMPLiveUpdateReplay(@PathVariable Integer id, @PathVariable Integer closeReplay) {
        return CommonResult.success(weChatLiveRoomService.updateReplay(id, closeReplay));
    }


    // 用于测试
    @ApiOperation(value = "用于测试 同步直播间状态")
    @RequestMapping(value = "/syncliveroom", method = RequestMethod.GET)
    public CommonResult<String> syncliveroom() {
        weChatLiveRoomService.syncLiveRoom();
        return CommonResult.success();
    }
}
