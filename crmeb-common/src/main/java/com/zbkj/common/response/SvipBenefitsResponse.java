package com.zbkj.common.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * svip权益响应对象
 *
 * @author Han
 * @version 1.0.0
 * @Date 2024/5/14
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "SvipBenefitsResponse", description = "svip权益响应对象")
public class SvipBenefitsResponse implements Serializable {

    private static final long serialVersionUID = -9017659056868439528L;

    @ApiModelProperty(value = "权益图标")
    private String imageUrl;

    @ApiModelProperty(value = "展示名称")
    private String value;

    @ApiModelProperty(value = "权益简介")
    private String message;

}
