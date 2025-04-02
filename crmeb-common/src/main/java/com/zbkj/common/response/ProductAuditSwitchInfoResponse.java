package com.zbkj.common.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * 商品审核开关信息响应对象
 *
 * @author Han
 * @version 1.0.0
 * @Date 2024/5/30
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "ProductAuditSwitchInfoResponse", description = "商品审核开关信息响应对象")
public class ProductAuditSwitchInfoResponse implements Serializable {

    private static final long serialVersionUID = 8675085394595378142L;

    @ApiModelProperty(value = "商品开关:0-关闭，1-开启")
    private Boolean productSwitch;

    @ApiModelProperty(value = "商户开关:0-关闭，1-开启")
    private Boolean isSwitch;
}
