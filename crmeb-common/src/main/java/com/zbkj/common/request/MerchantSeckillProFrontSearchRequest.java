package com.zbkj.common.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;

/**
 * 移动端商户秒杀商品搜索请求对象
 *
 * @author Han
 * @version 1.0.0
 * @Date 2024/11/14
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "MerchantSeckillProFrontSearchRequest", description = "移动端商户秒杀商品搜索请求对象")
public class MerchantSeckillProFrontSearchRequest extends PageParamRequest {

    @ApiModelProperty(value = "商户ID")
    @NotBlank(message = "请选择商户")
    private Integer merId;
}
