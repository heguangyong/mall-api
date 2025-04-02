package com.zbkj.common.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 积分商品分页列表搜索对象
 *
 * @author Han
 * @version 1.0.0
 * @Date 2024/7/13
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "IntegralProductPageSearchRequest", description = "积分商品分页列表搜索对象")
public class IntegralProductPageSearchRequest extends PageParamRequest implements Serializable {

    private static final long serialVersionUID = -4572464885363357010L;

    @ApiModelProperty(value = "状态（0：未上架，1：上架）", required = true)
    @NotNull(message = "请选择上架状态")
    private Boolean isShow;

    @ApiModelProperty(value = "关键字搜索，支持(商品名称, 关键字)")
    private String keywords;

    @ApiModelProperty(value = "创建时间区间")
    private String dateLimit;
}
