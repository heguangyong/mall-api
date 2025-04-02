package com.zbkj.common.response.onepass;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "ElectrResponse", description = "电子面单 Response对象")
public class ElectrResponse {

    @ApiModelProperty(value = "快递单号")
    private String kuaidinum;

    @ApiModelProperty(value = "快递名称")
    private String label;

    @ApiModelProperty(value = "快递CODE")
    private String code;

}
