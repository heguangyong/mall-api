package com.zbkj.common.request.merchant;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class MerchantExpressRequest {
    @ApiModelProperty(value = "快递公司id")
    @NotNull(message = "快递公司id不能为空")
    private Integer id;

    @ApiModelProperty(value = "账号")
    private String account;

    @ApiModelProperty(value = "密码")
    private String password;

    @ApiModelProperty(value = "网点名称")
    private String netName;

    @ApiModelProperty(value = "是否显示")
    private Boolean isOpen;

}
