package com.zbkj.common.request.wxmplive.media;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;

/**
 * @Auther: 大粽子
 * @Date: 2023/3/30 19:01
 * @Description: 微信素材上传本地素材对象
 */
@Data
public class WechatMediaUploadLocalRequest {

    @ApiModelProperty(value = "暂时仅使用image ｜ type 是 媒体文件类型，分别有图片（image）、语音（voice）、视频（video）和缩略图（thumb）", required = true)
    @NotEmpty(message = "type 不能为空")
    private String type;

    @ApiModelProperty(value = "系统图片路径", required = true)
    @NotEmpty(message = "系统图片路径 不能为空")
    private String imagePath;
}
