package com.zbkj.admin.controller.merchant;

import com.alibaba.fastjson.JSONObject;
import com.zbkj.common.request.wxmplive.media.WechatMediaUploadLocalRequest;
import com.zbkj.common.result.CommonResult;
import com.zbkj.service.service.WechatMediaService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.bean.result.WxMediaUploadResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;

/**
 * @Auther: 大粽子
 * @Date: 2023/3/9 10:54
 * @Description: 商户微信 素材
 */
@Slf4j
@RestController
@RequestMapping("api/admin/merchant/mp/media")
@Api(tags = "商户端 - 微信 - 素材")
@Validated
public class MerchantWechatMediaController {

    @Autowired
    private WechatMediaService weChatMediaService;

    @PreAuthorize("hasAuthority('merchant:mp:media:upload')")
    @ApiOperation(value = "商户端 - 微信 - 素材上传")
    @RequestMapping(value = "/upload", method = RequestMethod.POST)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "type", value = "分别有图片（image）、语音（voice）、视频（video）和缩略图（thumb）",dataType = "String")
    })
    public CommonResult<WxMediaUploadResult> WechatMediaUpload(MultipartFile multipart, @RequestParam(value = "type", defaultValue = "分别有图片（image）、语音（voice）、视频（video）和缩略图（thumb）") String type)
            throws Exception {
        WxMediaUploadResult wxMediaUploadResult = weChatMediaService.uploadMedia(type, multipart);
        return CommonResult.success(wxMediaUploadResult);
    }

    @PreAuthorize("hasAuthority('merchant:mp:media:uploadlocal')")
    @ApiOperation(value = "商户端 - 微信 - 素材上传本地")
    @RequestMapping(value = "/uploadlocal", method = RequestMethod.POST)
    public CommonResult<WxMediaUploadResult> WechatMediaUploadLocal(@RequestBody WechatMediaUploadLocalRequest request)
            throws Exception {
        WxMediaUploadResult wxMediaUploadResult = weChatMediaService.uploadMediaByLocal(request.getType(), request.getImagePath());
        return CommonResult.success(wxMediaUploadResult);
    }

    @PreAuthorize("hasAuthority('merchant:mp:media:get')")
    @ApiOperation(value = "商户端 - 微信 - 素材获取")
    @RequestMapping(value = "/get", method = RequestMethod.GET)
    public CommonResult<File> WechatMediaGet(@RequestParam(value = "mediaId") String mediaId)
            throws Exception {
        File fileByMedia = weChatMediaService.getFileByMediaId(mediaId);
        return CommonResult.success(fileByMedia);
    }



    @PreAuthorize("hasAuthority('merchant:mp:media:preserveupload')")
    @ApiOperation(value = "商户端 - 微信 - 上传永久素材")
    @RequestMapping(value = "/preserveupload", method = RequestMethod.GET)
    public CommonResult<JSONObject> WechatMediaPreserveUpload(@RequestParam String url) {
//        String result = weChatMediaService.preserveUploadImgNotMediaId(multipart);
        JSONObject result = weChatMediaService.preserveUploadImgHasMediaId(url);
        return CommonResult.success(result);
    }


}
