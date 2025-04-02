package com.zbkj.common.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * @ClassName UrlToBase64Request
 * @Description 图片地址转base64请求对象
 * @Author HZW
 * @Date 2023/4/14 15:32
 * @Version 1.0
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="UrlToBase64Request对象", description="图片地址转base64请求对象")
public class UrlToBase64Request implements Serializable {

    private static final long serialVersionUID = -3139529897632491713L;

    @ApiModelProperty(value = "图片Url地址")
    @NotBlank(message = "图片Url地址不能为空")
    private String url;

}
