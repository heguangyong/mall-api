package com.zbkj.admin.controller.platform;

import com.zbkj.common.model.wechat.live.WechatLiveRoom;
import com.zbkj.common.page.CommonPage;
import com.zbkj.common.request.wxmplive.goods.WechatMpLiveGoodsSortRequest;
import com.zbkj.common.request.wxmplive.goods.WechatLiveGoodsInRoomResponse;
import com.zbkj.common.request.wxmplive.room.WechatMpLiveRoomReviewRequest;
import com.zbkj.common.request.wxmplive.room.WechatMpLiveRoomSearchRequest;
import com.zbkj.common.request.wxmplive.room.WechatLiveRoomSharCode;
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
@RequestMapping("api/admin/platform/mp/live/room")
@Api(tags = "平台端 - 微信小程序 - 直播间管理")
@Validated
public class PlatformWechatLiveRoomController {

    @Autowired
    private WechatLiveRoomService weChatLiveRoomService;

    @PreAuthorize("hasAuthority('platform:mp:live:room:review')")
    @ApiOperation(value = "平台端 - 小程序直播 - 直播室审核")
    @RequestMapping(value = "/review", method = RequestMethod.POST)
    public CommonResult<Boolean> WechatMPLiveRoomReview(@RequestBody @Validated WechatMpLiveRoomReviewRequest reviewRequest) {
        return CommonResult.success(weChatLiveRoomService.reviewRoom(reviewRequest.getId(), reviewRequest.getReviewStatus(), reviewRequest.getReviewReason()));
    }

    @PreAuthorize("hasAuthority('platform:mp:live:room:goodssort')")
    @ApiOperation(value = "平台端 - 小程序直播 - 商品排序")
    @RequestMapping(value = "/goodsort", method = RequestMethod.POST)
    public CommonResult<Boolean> WechatMPLiveGoodsSort(@RequestBody WechatMpLiveGoodsSortRequest request) {
        return CommonResult.success(weChatLiveRoomService.goodsSort(request.getRoomId(), request.getGoods()));
    }


    @PreAuthorize("hasAuthority('platform:mp:live:room:delete')")
    @ApiOperation(value = "平台端 - 小程序直播 - 删除直播室")
    @RequestMapping(value = "/delete/{roomId}", method = RequestMethod.GET)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "roomId", value = "待删除的直播间id", dataType = "int")
    })
    public CommonResult<Object> WechatMPLiveDeleteRoom(@PathVariable Integer roomId) {
        Boolean deleteRoomResult = weChatLiveRoomService.deleteRoom(roomId);
        if(deleteRoomResult){
            return CommonResult.success();
        }else{
            return CommonResult.failed();
        }
    }



    @PreAuthorize("hasAuthority('platform:mp:live:room:list')")
    @ApiOperation(value = "平台端 - 小程序直播 - 直播室列表和回放")
    @RequestMapping(value = "/list", method = RequestMethod.POST)
    public CommonResult<CommonPage<WechatLiveRoom>> WechatMPLiveListRoom(@RequestBody WechatMpLiveRoomSearchRequest weChatMpLiveRoomSearchRequest) {
        return CommonResult.success( CommonPage.restPage(weChatLiveRoomService.getLiveList(weChatMpLiveRoomSearchRequest.getSearchRequest(),
                weChatMpLiveRoomSearchRequest.getPageParamRequest(), Boolean.FALSE)));
    }

    @PreAuthorize("hasAuthority('platform:mp:live:room:getsharecode')")
    @ApiOperation(value = "平台端 - 小程序直播 - 获取直播间分享二维码")
    @RequestMapping(value = "/getsharecode/{id}", method = RequestMethod.GET)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "待获取分享二维码的直播间id", dataType = "int")
    })
    public CommonResult<WechatLiveRoomSharCode> WechatMPLiveRoomGetShareCode(@PathVariable Integer id) {
        return CommonResult.success(weChatLiveRoomService.getShareQRCodeByRoomId(id, null));
    }

    @PreAuthorize("hasAuthority('platform:mp:live:room:deletegoods')")
    @ApiOperation(value = "平台端 - 小程序直播 - 删除直播间商品")
    @RequestMapping(value = "/deletegoodsinroom/{id}/{goodsId}", method = RequestMethod.GET)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "直播间id", dataType = "int"),
            @ApiImplicitParam(name = "goodsId", value = "删除的商品id",dataType = "int")
    })
    public CommonResult<Boolean> WechatMPLiveDeleteGoodsInRoom(@PathVariable Integer id,@PathVariable Integer goodsId) {
        return CommonResult.success(weChatLiveRoomService.deleteGoodsInRoom(id, goodsId));
    }

    @PreAuthorize("hasAuthority('platform:mp:live:room:goodslist')")
    @ApiOperation(value = "平台端 - 小程序直播 - 直播间商品列表")
    @RequestMapping(value = "/goodslist/{id}", method = RequestMethod.GET)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "直播间ID", dataType = "int")
    })
    public CommonResult<List<WechatLiveGoodsInRoomResponse>> WechatMPLiveRoomGoodsList(@PathVariable Integer id) {
        return CommonResult.success(weChatLiveRoomService.getRoomGoodsList(id));
    }


    @PreAuthorize("hasAuthority('platform:mp:live:room:updatecomment')")
    @ApiOperation(value = "平台端 - 小程序直播 - 禁言管理")
    @RequestMapping(value = "/updatecomment/{id}/{banComment}", method = RequestMethod.GET)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "直播间id", dataType = "int"),
            @ApiImplicitParam(name = "banComment", value = "1-禁言，0-取消禁言",dataType = "int", allowableValues = "0,1")
    })
    public CommonResult<Boolean> WechatMPLiveUpdateComment(@PathVariable Integer id, @PathVariable Integer banComment) {
        return CommonResult.success(weChatLiveRoomService.updateComment(id, banComment));
    }


    @PreAuthorize("hasAuthority('platform:mp:live:room:isfeedspubic')")
    @ApiOperation(value = "平台端 - 小程序直播 - 官方收录管理")
    @RequestMapping(value = "/updatefeedpublic/{id}/{isFeedsPublic}", method = RequestMethod.GET)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "直播间id", dataType = "int"),
            @ApiImplicitParam(name = "isFeedsPublic", value = "是否开启官方收录 【1: 开启，0：关闭】",dataType = "int", allowableValues = "0,1")
    })
    public CommonResult<Boolean> WechatMPLiveUpdateFeedPublic(@PathVariable Integer id, @PathVariable Integer isFeedsPublic) {
        return CommonResult.success(weChatLiveRoomService.updateFeedPublic(id, isFeedsPublic));
    }


    @PreAuthorize("hasAuthority('platform:mp:live:room:updatereplay')")
    @ApiOperation(value = "平台端 - 小程序直播 - 开启回放")
    @RequestMapping(value = "/updatereplay/{id}/{closeReplay}", method = RequestMethod.GET)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "直播间id", dataType = "int"),
            @ApiImplicitParam(name = "closeReplay", value = "是否关闭回放 【0：开启，1：关闭】",dataType = "int", allowableValues = "0,1")
    })
    public CommonResult<Boolean> WechatMPLiveUpdateReplay(@PathVariable Integer id, @PathVariable Integer closeReplay) {
        return CommonResult.success(weChatLiveRoomService.updateReplay(id, closeReplay));
    }


//    @PreAuthorize("hasAuthority('platform:mp:live:room:getvideo')")
//    @ApiOperation(value = "平台端 - 小程序直播 - 下载讲解视频")
//    @RequestMapping(value = "/getvideo/{id}/{goodsId}", method = RequestMethod.GET)
//    @ApiImplicitParams({
//            @ApiImplicitParam(name = "id", value = "直播间id", dataType = "int"),
//            @ApiImplicitParam(name = "goodsId", value = "商品id",dataType = "int", allowableValues = "0,1")
//    })
//    public CommonResult<String> WeChatMPLiveGetVideo(@PathVariable Integer id, @PathVariable Integer goodsId) {
//        return CommonResult.success(weChatLiveRoomService.getVideo(id, goodsId));
//    }

    @PreAuthorize("hasAuthority('platform:mp:live:room:showstore')")
    @ApiOperation(value = "平台端 - 小程序直播 - 显示在商城")
    @RequestMapping(value = "/showstore/{id}/{show}", method = RequestMethod.GET)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "直播间id", dataType = "int"),
            @ApiImplicitParam(name = "show", value = "0=关闭，1显示",dataType = "int", allowableValues = "0,1")
    })
    public CommonResult<Boolean> WechatMPLiveShowInStore(@PathVariable Integer id, @PathVariable Integer show) {
        return CommonResult.success(weChatLiveRoomService.showStore(id, show));
    }
}
