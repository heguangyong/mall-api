package com.zbkj.common.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 字符串转Base64请求对象
 * @Author HZW
 * @Date 2023/6/29 10:09
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "StrToBaseRequest对象", description = "字符串转Base64请求对象")
public class StrToBaseRequest implements Serializable {

    private static final long serialVersionUID = 5256783230192236618L;

    @ApiModelProperty(value = "文本")
    @NotEmpty(message = "文本不能为空")
    private String text;

    @ApiModelProperty(value = "宽")
    @NotNull(message = "宽不能为空")
    private Integer width;

    @ApiModelProperty(value = "高")
    @NotNull(message = "高不能为空")
    private Integer height;

}
