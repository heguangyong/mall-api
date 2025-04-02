package com.zbkj.common.response.employee;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "EmployeeMerchantActiveResponse", description = "移动端商户管理的 商户权限和电子面单开关")
public class EmployeeMerchantActiveResponse {

    @ApiModelProperty(value = "移动端商家管理 token")
    private String token;

    @ApiModelProperty(value = "电子面单开关：0关闭，1=开启")
    private Integer electrPrintingSwitch;
}
