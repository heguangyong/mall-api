package com.zbkj.common.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @ClassName BaseUploadRequest
 * @Description base64图片上传请求对象
 * @Author HZW
 * @Date 2023/5/8 16:26
 * @Version 1.0
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "BaseUploadRequest对象", description = "base64图片上传请求对象")
public class BaseUploadRequest implements Serializable {

    private static final long serialVersionUID = -7795148477030414962L;

    @ApiModelProperty(value = "base64地址", required = true)
    @NotBlank(message = "请填写base64地址")
    private String base64Url;

    @ApiModelProperty(value = "模块 用户user,商品product,微信wechat,news文章", required = true)
    @NotBlank(message = "model不能为空")
    private String model;

    @ApiModelProperty(value = "分类ID 0编辑器,1商品图片,2拼团图片,3砍价图片,4秒杀图片,5文章图片,6组合数据图,7前台用户,8微信系列", required = true)
    @NotNull(message = "请选择图片分类")
    private Integer pid;
}
